<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { addActivity, addActivityLinks, pageActivity, removeActivity, updateActivity } from '@/api/activity'
import { batchDisableLink, batchEnableLink, pageLink, removeLink } from '@/api/link'
import { appStore, groupName, loadGroups } from '@/store/app'
import { faviconUrl, fmtDate, fmtDateTime } from '@/utils/format'

const router = useRouter()

const PAGE_SIZE = 10

/** 常用渠道只是候选项，输入框允许自由创建任意渠道名 */
const CHANNEL_PRESETS = ['weixin', 'weibo', 'douyin', 'xiaohongshu', 'qq', 'sms', 'email']

const activities = ref([])
const loading = ref(false)
const current = ref(1)
const hasMore = ref(false)

const filters = reactive({
  gid: '',
  status: '',
  activityName: ''
})

// ---------- 新建活动 ----------

const createVisible = ref(false)
const creating = ref(false)
const createForm = reactive({
  activityName: '',
  originUrl: '',
  gid: '',
  status: 0,
  validDateType: 0,
  validDate: ''
})

// ---------- 编辑 / 删除活动 ----------

const editVisible = ref(false)
const savingEdit = ref(false)
const editForm = reactive({
  id: null,
  activityName: '',
  originUrl: '',
  status: 0,
  validDateType: 0,
  validDate: ''
})

function openEdit(row) {
  editForm.id = row.id
  editForm.activityName = row.activityName || ''
  editForm.originUrl = row.originUrl || ''
  editForm.status = row.status === 1 ? 1 : 0
  editForm.validDateType = row.validDateType === 1 ? 1 : 0
  editForm.validDate = row.validDate || ''
  editVisible.value = true
}

async function submitEdit() {
  if (!editForm.activityName.trim()) return ElMessage.warning('请输入活动名称')
  if (!editForm.originUrl.trim()) return ElMessage.warning('请输入活动目标链接')
  if (editForm.validDateType === 1 && !editForm.validDate) {
    return ElMessage.warning('请选择活动有效期')
  }

  const body = {
    id: editForm.id,
    activityName: editForm.activityName.trim(),
    originUrl: editForm.originUrl.trim(),
    status: editForm.status,
    validDateType: editForm.validDateType
  }
  // 永久有效时不要把空的 validDate 发过去，null 会被后端的有效期校验拦下
  if (editForm.validDateType === 1) body.validDate = editForm.validDate

  savingEdit.value = true
  try {
    await updateActivity(body)
    ElMessage.success('修改成功')
    editVisible.value = false
    await load()
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    savingEdit.value = false
  }
}

async function onRemove(row) {
  try {
    await ElMessageBox.confirm(
      `确定删除活动「${row.activityName}」？该活动下的渠道短链会一并停用（访问返回 404），但不会被删除，会回落到"短链接"列表，需要时可以逐条再启用。`,
      '删除活动',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  try {
    await removeActivity(row.id)
    ElMessage.success('已删除')
    // 删掉的是当前页最后一条时，往前退一页，避免停在空页上
    if (activities.value.length === 1 && current.value > 1) current.value -= 1
    await load()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

/**
 * 状态列开关：开 = 进行中(0)，关 = 已结束(1)。
 * 用 :model-value 而不是 v-model —— el-switch 只有 model-value 绑定时是受控的，
 * 视觉完全跟着 row.status 走，用户点了不会先跳过去再弹框；等他确认、后端改成功、列表刷新后开关才动。
 * 取消或失败都要重新 load()，否则开关停在旧位置会显得"点了没反应"。
 */
async function onToggleActivityStatus(row, nextOn) {
  const nextStatus = nextOn ? 0 : 1
  try {
    await ElMessageBox.confirm(
      nextStatus === 1
        ? `确定结束活动「${row.activityName}」？该活动下的渠道短链会一并停用（访问返回 404），改回进行中则重新启用；链接本身不会被删除。`
        : `确定把活动「${row.activityName}」改回进行中？该活动下的渠道短链会一并重新启用。`,
      nextStatus === 1 ? '结束活动' : '恢复活动',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  try {
    await updateActivity({ id: row.id, status: nextStatus })
    ElMessage.success(nextStatus === 1 ? '活动已结束' : '活动已恢复')
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    await load()
  }
}

// ---------- 渠道短链 ----------

const activeActivity = ref(null)
// 展开行按活动 id 各存一份，多行同时展开时各自的分页互不干扰
const channelState = reactive({})
/**
 * 必须先赋值、再读回来。`channelState[id] ??= {...}` 这个赋值表达式的值是右侧那个**原始对象**，
 * 不是 reactive 代理（Proxy 的 set 拦截器改不了表达式自身的值），拿它去改 loading/rows 都不会触发更新，
 * 表现就是展开后一直转圈、再点一次"刷新"才出数据（那时 key 已存在，读到的是代理）。
 */
function stateOf(id) {
  if (!channelState[id]) {
    channelState[id] = { rows: [], current: 1, hasMore: false, loading: false }
  }
  return channelState[id]
}

const channelCreateVisible = ref(false)
const channelCreating = ref(false)
const channelForm = reactive({
  channels: [],
  // 三态：'' = 跟随活动（默认）、0 = 永久有效、1 = 自定义日期。
  // 用 '' 而不是 null 当哨兵：Element Plus 靠严格相等判选中，:value="null" 容易"点了没反应"。
  validDateType: '',
  validDate: '',
  description: ''
})
/** 批量创建后逐个回显结果，方便一次性复制/核对 */
const createdLinks = ref([])

onMounted(async () => {
  try {
    await loadGroups()
  } catch {
    /* 拦截器已经提示过了 */
  }
  load()
})

async function load() {
  loading.value = true
  try {
    const list = await pageActivity({
      current: current.value,
      size: PAGE_SIZE,
      gid: filters.gid || undefined,
      status: filters.status === '' ? undefined : filters.status,
      activityName: filters.activityName.trim() || undefined
    })
    activities.value = list || []
    hasMore.value = activities.value.length >= PAGE_SIZE
  } catch {
    activities.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

function search() {
  current.value = 1
  load()
}

function resetFilters() {
  filters.gid = ''
  filters.status = ''
  filters.activityName = ''
  search()
}

function go(delta) {
  const next = current.value + delta
  if (next < 1) return
  if (delta > 0 && !hasMore.value) return
  current.value = next
  load()
}

function openCreate() {
  createForm.activityName = ''
  createForm.originUrl = ''
  createForm.gid = appStore.currentGid || (appStore.groups[0] ? appStore.groups[0].gid : '')
  createForm.status = 0
  createForm.validDateType = 0
  createForm.validDate = ''
  createVisible.value = true
}

async function submitCreate() {
  if (!createForm.activityName.trim()) return ElMessage.warning('请输入活动名称')
  if (!createForm.originUrl.trim()) return ElMessage.warning('请输入活动目标链接')
  if (!createForm.gid) return ElMessage.warning('请选择所属分组')
  if (createForm.validDateType === 1 && !createForm.validDate) {
    return ElMessage.warning('请选择活动有效期')
  }

  const body = {
    activityName: createForm.activityName.trim(),
    originUrl: createForm.originUrl.trim(),
    gid: createForm.gid,
    status: createForm.status,
    validDateType: createForm.validDateType
  }
  // 永久有效时不要把空的 validDate 发过去，null 会被后端的有效期校验拦下
  if (createForm.validDateType === 1) body.validDate = createForm.validDate

  creating.value = true
  try {
    await addActivity(body)
    ElMessage.success('活动创建成功')
    createVisible.value = false
    current.value = 1
    await load()
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    creating.value = false
  }
}

// ---------- 渠道短链（活动行内展开） ----------

/** 展开时才拉数据，收起不发请求 */
function onExpandChange(row, expandedRows) {
  if (expandedRows.some((r) => r.id === row.id)) loadChannels(row)
}

/** 列表页直接"新建渠道"：不用先展开，弹窗自己带 activityId */
function openChannelCreate(row) {
  activeActivity.value = row
  channelForm.channels = []
  // 默认"跟随活动"：活动有效期是唯一默认值，用户改了才按改的走
  channelForm.validDateType = ''
  channelForm.validDate = ''
  channelForm.description = ''
  createdLinks.value = []
  channelCreateVisible.value = true
}

async function loadChannels(row) {
  const st = stateOf(row.id)
  st.loading = true
  try {
    // 走的是同一个分页接口，靠 activityId 只取该活动下的渠道短链
    const list = await pageLink({
      gid: row.gid,
      activityId: row.id,
      current: st.current,
      size: PAGE_SIZE
    })
    st.rows = list || []
    st.hasMore = st.rows.length >= PAGE_SIZE
  } catch {
    st.rows = []
    st.hasMore = false
  } finally {
    st.loading = false
  }
}

function channelsGo(row, delta) {
  const st = stateOf(row.id)
  const next = st.current + delta
  if (next < 1) return
  if (delta > 0 && !st.hasMore) return
  st.current = next
  loadChannels(row)
}

async function onChannelRemove(row, act) {
  try {
    await ElMessageBox.confirm(`确定删除渠道短链 ${row.fullShortUrl}？它会被移入回收站。`, '删除', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  try {
    await removeLink(row.fullShortUrl, row.gid || act.gid)
    ElMessage.success('已移入回收站')
    await loadChannels(act)
  } catch {
    /* 拦截器已经提示过了 */
  }
}

async function submitChannelCreate() {
  const channels = channelForm.channels.map((c) => String(c).trim()).filter(Boolean)
  if (!channels.length) return ElMessage.warning('请至少填写一个渠道')
  if (channelForm.validDateType === 1 && !channelForm.validDate) {
    return ElMessage.warning('请选择有效期')
  }

  const act = activeActivity.value
  const body = {
    activityId: act.id,
    channels,
    description: channelForm.description
  }
  // '' = 跟随活动：整个字段都不传，后端按"继承活动有效期"处理。
  // 判断必须用 ===，'' == 0 为 true，用 == 会把"跟随活动"错当成"永久有效"。
  if (channelForm.validDateType !== '') body.validDateType = channelForm.validDateType
  // 永久有效时不要把空的 validDate 发过去，null 会被后端的有效期校验拦下
  if (channelForm.validDateType === 1) body.validDate = channelForm.validDate

  channelCreating.value = true
  try {
    createdLinks.value = (await addActivityLinks(body)) || []
    ElMessage.success(`已创建 ${createdLinks.value.length} 条渠道短链`)
    stateOf(act.id).current = 1
    await loadChannels(act)
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    channelCreating.value = false
  }
}

/**
 * 启停只切 enableStatus（0 已启用 / 1 未启用），不动删除标识——回收站那条链路完全独立。
 * 后端会在 UPDATE 之后删对应方向的缓存，所以停用/启用都是立刻生效的。
 */
async function onChannelToggleStatus(c, act) {
  const disabling = c.enableStatus !== 1
  try {
    await ElMessageBox.confirm(
      disabling
        ? `确定停用 ${c.fullShortUrl}？停用后访问会返回 404，链接不会被删除。`
        : `确定启用 ${c.fullShortUrl}？`,
      disabling ? '停用' : '启用',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  try {
    const body = { gid: c.gid || act.gid, fullShortUrls: [c.fullShortUrl] }
    await (disabling ? batchDisableLink(body) : batchEnableLink(body))
    ElMessage.success(disabling ? '已停用' : '已启用')
    await loadChannels(act)
  } catch {
    /* 拦截器已经提示过了 */
  }
}

/** 整活动的渠道一起启停：传 activityId 而不是列表——渠道列表是分页的，前端只有当前页 */
async function onToggleAllChannels(row, disabling) {
  try {
    await ElMessageBox.confirm(
      disabling
        ? `确定停用「${row.activityName}」下的全部渠道短链？停用后访问返回 404，链接不会被删除。`
        : `确定启用「${row.activityName}」下的全部渠道短链？`,
      disabling ? '全部停用' : '全部启用',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  try {
    const body = { gid: row.gid, activityId: row.id }
    const affected = await (disabling ? batchDisableLink(body) : batchEnableLink(body))
    ElMessage.success(`已${disabling ? '停用' : '启用'} ${affected} 条`)
    await loadChannels(row)
  } catch {
    /* 拦截器已经提示过了 */
  }
}

function openStats(row, act) {
  router.push({
    name: 'stats',
    query: { gid: row.gid || act.gid, fullShortUrl: row.fullShortUrl }
  })
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2>营销活动</h2>
      <div class="toolbar">
        <el-select v-model="filters.gid" placeholder="全部分组" clearable size="small" style="width: 150px">
          <el-option v-for="g in appStore.groups" :key="g.gid" :label="g.name" :value="g.gid" />
        </el-select>
        <el-select v-model="filters.status" placeholder="全部状态" clearable size="small" style="width: 120px">
          <el-option label="进行中" :value="0" />
          <el-option label="已结束" :value="1" />
        </el-select>
        <el-input
          v-model="filters.activityName"
          placeholder="活动名称"
          clearable
          size="small"
          style="width: 160px"
          @keyup.enter="search"
        />
        <el-button size="small" @click="search">查询</el-button>
        <el-button size="small" @click="resetFilters">重置</el-button>
        <el-button type="primary" size="small" @click="openCreate">+ 新建活动</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="activities"
      border
      row-key="id"
      @expand-change="onExpandChange"
    >
      <el-table-column type="expand" label="渠道短链" width="90">
        <template #default="{ row }">
          <div class="expand-panel">
            <div class="toolbar" style="margin-bottom: 12px">
              <el-button size="small" @click="loadChannels(row)">刷新</el-button>
              <el-button type="primary" size="small" @click="openChannelCreate(row)">
                + 批量建渠道
              </el-button>
              <el-button size="small" @click="onToggleAllChannels(row, true)">全部停用</el-button>
              <el-button size="small" @click="onToggleAllChannels(row, false)">全部启用</el-button>
            </div>

            <el-table v-loading="stateOf(row.id).loading" :data="stateOf(row.id).rows" border>
              <el-table-column label="渠道" width="120">
                <template #default="{ row: c }">
                  <el-tag size="small">{{ c.channel || '-' }}</el-tag>
                </template>
              </el-table-column>

              <el-table-column label="短链接" min-width="220">
                <template #default="{ row: c }">
                  <a :href="c.fullShortUrl" target="_blank" rel="noreferrer" class="mono">
                    {{ c.fullShortUrl }}
                  </a>
                </template>
              </el-table-column>

              <el-table-column label="历史 PV / UV / IP" width="170" align="center">
                <template #default="{ row: c }">
                  <span class="mono">
                    {{ c.totalPv || 0 }} / {{ c.totalUv || 0 }} / {{ c.totalUip || 0 }}
                  </span>
                </template>
              </el-table-column>

              <!-- enableStatus 语义以 Java 为准：0 = 已启用，1 = 未启用 -->
              <el-table-column label="状态" width="90" align="center">
                <template #default="{ row: c }">
                  <el-tag :type="c.enableStatus === 1 ? 'info' : 'success'" size="small">
                    {{ c.enableStatus === 1 ? '已停用' : '已启用' }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column label="操作" width="200" align="center">
                <template #default="{ row: c }">
                  <el-button link type="primary" size="small" @click="openStats(c, row)">
                    统计
                  </el-button>
                  <el-button
                    link
                    :type="c.enableStatus === 1 ? 'success' : 'warning'"
                    size="small"
                    @click="onChannelToggleStatus(c, row)"
                  >
                    {{ c.enableStatus === 1 ? '启用' : '停用' }}
                  </el-button>
                  <el-button link type="danger" size="small" @click="onChannelRemove(c, row)">
                    删除
                  </el-button>
                </template>
              </el-table-column>

              <template #empty>该活动下还没有渠道短链</template>
            </el-table>

            <!-- 用箭头而不是"上一页/下一页"文字，免得和外层活动列表的分页看起来重复 -->
            <div class="pager pager-arrows">
              <el-button
                size="small"
                :disabled="stateOf(row.id).current <= 1"
                @click="channelsGo(row, -1)"
              >
                ←
              </el-button>
              <span class="muted">第 {{ stateOf(row.id).current }} 页</span>
              <el-button
                size="small"
                :disabled="!stateOf(row.id).hasMore"
                @click="channelsGo(row, 1)"
              >
                →
              </el-button>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="活动名称" min-width="160">
        <template #default="{ row }">{{ row.activityName }}</template>
      </el-table-column>

      <el-table-column label="目标链接" min-width="240">
        <template #default="{ row }">
          <img
            v-if="faviconUrl(row.originUrl)"
            class="favicon"
            :src="faviconUrl(row.originUrl)"
            alt=""
            @error="(e) => (e.target.style.display = 'none')"
          />
          <span class="ellipsis" :title="row.originUrl">{{ row.originUrl }}</span>
        </template>
      </el-table-column>

      <el-table-column label="所属分组" width="140">
        <template #default="{ row }">{{ groupName(row.gid) || row.gid }}</template>
      </el-table-column>

      <!-- 开 = 进行中(0)，关 = 已结束(1)。受控写法，见 onToggleActivityStatus -->
      <el-table-column label="状态" width="110" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status !== 1"
            :width="64"
            inline-prompt
            active-text="进行中"
            inactive-text="已结束"
            @change="(v) => onToggleActivityStatus(row, v)"
          />
        </template>
      </el-table-column>

      <el-table-column label="有效期" width="120" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.validDateType === 1" type="warning" size="small">
            {{ fmtDate(row.validDate) }}
          </el-tag>
          <el-tag v-else type="info" size="small">永久有效</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="150">
        <template #default="{ row }">{{ fmtDateTime(row.createTime) }}</template>
      </el-table-column>

      <!-- 建渠道的入口只在展开面板里（行内展开的「+ 批量建渠道」），这里不再重复一个 -->
      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="onRemove(row)">删除</el-button>
        </template>
      </el-table-column>

      <template #empty>暂无活动</template>
    </el-table>

    <div class="pager">
      <el-button size="small" :disabled="current <= 1" @click="go(-1)">上一页</el-button>
      <span class="muted">第 {{ current }} 页</span>
      <el-button size="small" :disabled="!hasMore" @click="go(1)">下一页</el-button>
    </div>

    <!-- 新建活动 -->
    <el-dialog v-model="createVisible" title="新建营销活动" width="520px">
      <el-form label-position="top">
        <el-form-item label="活动名称">
          <el-input v-model="createForm.activityName" placeholder="例如：双十一大促" maxlength="50" />
        </el-form-item>
        <el-form-item label="目标链接">
          <el-input v-model="createForm.originUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="所属分组">
          <el-select v-model="createForm.gid" style="width: 100%">
            <el-option v-for="g in appStore.groups" :key="g.gid" :label="g.name" :value="g.gid" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="createForm.status">
            <el-radio :value="0">进行中</el-radio>
            <el-radio :value="1">已结束</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="有效期">
          <el-radio-group v-model="createForm.validDateType">
            <el-radio :value="0">永久有效</el-radio>
            <el-radio :value="1">自定义日期</el-radio>
          </el-radio-group>
          <div class="form-hint">
            这是之后批量建渠道时的默认有效期，新建的渠道短链会继承它（已有的链接不会跟着变）。
          </div>
        </el-form-item>
        <el-form-item v-if="createForm.validDateType === 1" label="到期日期">
          <el-input v-model="createForm.validDate" type="date" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="submitCreate">确认</el-button>
      </template>
    </el-dialog>

    <!-- 编辑活动 -->
    <el-dialog v-model="editVisible" title="编辑营销活动" width="520px">
      <el-form label-position="top">
        <el-form-item label="活动名称">
          <el-input v-model="editForm.activityName" maxlength="50" />
        </el-form-item>
        <el-form-item label="目标链接">
          <el-input v-model="editForm.originUrl" placeholder="https://..." />
          <div class="form-hint">
            已生成的渠道短链不会跟着改：它们在创建时就复制走了当时的目标链接，这里只影响之后新建的渠道短链。
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editForm.status">
            <el-radio :value="0">进行中</el-radio>
            <el-radio :value="1">已结束</el-radio>
          </el-radio-group>
          <div class="form-hint">
            改成「已结束」会把该活动下的渠道短链一并停用（访问返回 404），改回「进行中」则重新启用；链接本身不会被删除。
          </div>
        </el-form-item>
        <el-form-item label="有效期">
          <el-radio-group v-model="editForm.validDateType">
            <el-radio :value="0">永久有效</el-radio>
            <el-radio :value="1">自定义日期</el-radio>
          </el-radio-group>
          <div class="form-hint">
            改这里只影响之后新建的渠道短链：已有渠道短链在创建时就复制走了当时的活动有效期。
          </div>
        </el-form-item>
        <el-form-item v-if="editForm.validDateType === 1" label="到期日期">
          <el-input v-model="editForm.validDate" type="date" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingEdit" @click="submitEdit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 批量建渠道 -->
    <el-dialog
      v-model="channelCreateVisible"
      :title="`批量建渠道 · ${activeActivity ? activeActivity.activityName : ''}`"
      width="560px"
    >
      <el-form label-position="top">
        <el-form-item label="渠道">
          <el-select
            v-model="channelForm.channels"
            multiple
            filterable
            allow-create
            default-first-option
            :reserve-keyword="false"
            placeholder="选择或直接输入渠道名后回车"
            style="width: 100%"
          >
            <el-option v-for="c in CHANNEL_PRESETS" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="有效期">
          <!-- 三态：''（跟随活动，默认） / 0 永久 / 1 自定义。用 '' 当哨兵，别用 null。 -->
          <el-radio-group v-model="channelForm.validDateType">
            <el-radio :value="''">跟随活动</el-radio>
            <el-radio :value="0">永久有效</el-radio>
            <el-radio :value="1">自定义日期</el-radio>
          </el-radio-group>
          <div v-if="channelForm.validDateType === ''" class="form-hint">
            跟随活动：{{
              activeActivity && activeActivity.validDateType === 1
                ? `有效期至 ${fmtDate(activeActivity.validDate)}`
                : '永久有效'
            }}
          </div>
        </el-form-item>
        <el-form-item v-if="channelForm.validDateType === 1" label="到期日期">
          <el-input v-model="channelForm.validDate" type="date" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="channelForm.description" placeholder="可选描述，会写到每条渠道短链上" />
        </el-form-item>
      </el-form>

      <div v-if="createdLinks.length" class="result">
        <div class="section-label">本次创建结果</div>
        <div v-for="l in createdLinks" :key="l.fullShortUrl" class="result-row">
          <el-tag size="small">{{ l.channel || '-' }}</el-tag>
          <span class="mono">{{ l.fullShortUrl }}</span>
        </div>
      </div>

      <template #footer>
        <el-button @click="channelCreateVisible = false">关闭</el-button>
        <el-button type="primary" :loading="channelCreating" @click="submitChannelCreate">
          创建
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 0;
}

.pager-arrows .el-button {
  padding: 5px 10px;
  font-size: 14px;
  line-height: 1;
}

.section-label {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 6px;
}

.result {
  border-top: 1px dashed #e2e8f0;
  padding-top: 12px;
}

.result-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 3px 0;
}

.form-hint {
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.5;
  margin-top: 4px;
}

.expand-panel {
  padding: 12px 16px 4px 48px;
  background: #f8fafc;
}
</style>
