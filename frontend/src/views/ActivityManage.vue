<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { addActivity, addActivityLinks, pageActivity, removeActivity, updateActivity } from '@/api/activity'
import { pageLink } from '@/api/link'
import { appStore, groupName, loadGroups } from '@/store/app'
import { faviconUrl, fmtDateTime } from '@/utils/format'

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
  status: 0
})

// ---------- 编辑 / 删除活动 ----------

const editVisible = ref(false)
const savingEdit = ref(false)
const editForm = reactive({
  id: null,
  activityName: '',
  originUrl: '',
  status: 0
})

function openEdit(row) {
  editForm.id = row.id
  editForm.activityName = row.activityName || ''
  editForm.originUrl = row.originUrl || ''
  editForm.status = row.status === 1 ? 1 : 0
  editVisible.value = true
}

async function submitEdit() {
  if (!editForm.activityName.trim()) return ElMessage.warning('请输入活动名称')
  if (!editForm.originUrl.trim()) return ElMessage.warning('请输入活动目标链接')

  savingEdit.value = true
  try {
    await updateActivity({
      id: editForm.id,
      activityName: editForm.activityName.trim(),
      originUrl: editForm.originUrl.trim(),
      status: editForm.status
    })
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
      `确定删除活动「${row.activityName}」？该活动下的渠道短链不会被删除——它们仍然可以正常跳转，也会继续出现在"短链接"列表里。`,
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

// ---------- 渠道短链 ----------

const drawerVisible = ref(false)
const activeActivity = ref(null)
const channelRows = ref([])
const channelLoading = ref(false)
const channelCurrent = ref(1)
const channelHasMore = ref(false)

const channelCreateVisible = ref(false)
const channelCreating = ref(false)
const channelForm = reactive({
  channels: [],
  validDateType: 0,
  validDate: '',
  description: ''
})
/** 批量创建后逐个回显结果，方便一次性复制/核对 */
const createdLinks = ref([])

const drawerTitle = computed(() =>
  activeActivity.value ? `渠道短链 · ${activeActivity.value.activityName}` : '渠道短链'
)

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
  createVisible.value = true
}

async function submitCreate() {
  if (!createForm.activityName.trim()) return ElMessage.warning('请输入活动名称')
  if (!createForm.originUrl.trim()) return ElMessage.warning('请输入活动目标链接')
  if (!createForm.gid) return ElMessage.warning('请选择所属分组')

  creating.value = true
  try {
    await addActivity({
      activityName: createForm.activityName.trim(),
      originUrl: createForm.originUrl.trim(),
      gid: createForm.gid,
      status: createForm.status
    })
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

// ---------- 渠道短链抽屉 ----------

async function openChannels(row) {
  activeActivity.value = row
  channelCurrent.value = 1
  createdLinks.value = []
  drawerVisible.value = true
  await loadChannels()
}

/** 列表页直接"新建渠道"：先开抽屉把上下文铺好，再弹批量创建框 */
async function openChannelsAndCreate(row) {
  await openChannels(row)
  openChannelCreate()
}

async function loadChannels() {
  const act = activeActivity.value
  if (!act) return
  channelLoading.value = true
  try {
    // 走的是同一个分页接口，靠 activityId 只取该活动下的渠道短链
    const list = await pageLink({
      gid: act.gid,
      activityId: act.id,
      current: channelCurrent.value,
      size: PAGE_SIZE
    })
    channelRows.value = list || []
    channelHasMore.value = channelRows.value.length >= PAGE_SIZE
  } catch {
    channelRows.value = []
    channelHasMore.value = false
  } finally {
    channelLoading.value = false
  }
}

function channelsGo(delta) {
  const next = channelCurrent.value + delta
  if (next < 1) return
  if (delta > 0 && !channelHasMore.value) return
  channelCurrent.value = next
  loadChannels()
}

function openChannelCreate() {
  channelForm.channels = []
  channelForm.validDateType = 0
  channelForm.validDate = ''
  channelForm.description = ''
  createdLinks.value = []
  channelCreateVisible.value = true
}

async function submitChannelCreate() {
  const channels = channelForm.channels.map((c) => String(c).trim()).filter(Boolean)
  if (!channels.length) return ElMessage.warning('请至少填写一个渠道')
  if (channelForm.validDateType === 1 && !channelForm.validDate) {
    return ElMessage.warning('请选择有效期')
  }

  const body = {
    activityId: activeActivity.value.id,
    channels,
    validDateType: channelForm.validDateType,
    description: channelForm.description
  }
  // 永久有效时不要把空的 validDate 发过去，null 会被后端的有效期校验拦下
  if (channelForm.validDateType === 1) body.validDate = channelForm.validDate

  channelCreating.value = true
  try {
    createdLinks.value = (await addActivityLinks(body)) || []
    ElMessage.success(`已创建 ${createdLinks.value.length} 条渠道短链`)
    channelCurrent.value = 1
    await loadChannels()
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    channelCreating.value = false
  }
}

function openStats(row) {
  router.push({
    name: 'stats',
    query: { gid: row.gid || activeActivity.value.gid, fullShortUrl: row.fullShortUrl }
  })
}

function statusText(status) {
  return status === 1 ? '已结束' : '进行中'
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

    <el-table v-loading="loading" :data="activities" border>
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

      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'info' : 'success'" size="small">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="150">
        <template #default="{ row }">{{ fmtDateTime(row.createTime) }}</template>
      </el-table-column>

      <el-table-column label="操作" width="320" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openChannels(row)">渠道短链</el-button>
          <el-button link type="primary" size="small" @click="openChannelsAndCreate(row)">
            新建渠道
          </el-button>
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
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingEdit" @click="submitEdit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 渠道短链 -->
    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="860px">
      <div class="toolbar" style="margin-bottom: 12px">
        <el-button size="small" @click="loadChannels">刷新</el-button>
        <el-button type="primary" size="small" @click="openChannelCreate">+ 批量建渠道</el-button>
      </div>

      <el-table v-loading="channelLoading" :data="channelRows" border>
        <el-table-column label="渠道" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.channel || '-' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="短链接" min-width="220">
          <template #default="{ row }">
            <a :href="row.fullShortUrl" target="_blank" rel="noreferrer" class="mono">
              {{ row.fullShortUrl }}
            </a>
          </template>
        </el-table-column>

        <el-table-column label="历史 PV / UV / IP" width="170" align="center">
          <template #default="{ row }">
            <span class="mono">
              {{ row.totalPv || 0 }} / {{ row.totalUv || 0 }} / {{ row.totalUip || 0 }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openStats(row)">统计</el-button>
          </template>
        </el-table-column>

        <template #empty>该活动下还没有渠道短链</template>
      </el-table>

      <div class="pager">
        <el-button size="small" :disabled="channelCurrent <= 1" @click="channelsGo(-1)">上一页</el-button>
        <span class="muted">第 {{ channelCurrent }} 页</span>
        <el-button size="small" :disabled="!channelHasMore" @click="channelsGo(1)">下一页</el-button>
      </div>
    </el-drawer>

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
        <el-form-item label="有效期类型">
          <el-radio-group v-model="channelForm.validDateType">
            <el-radio :value="0">永久有效</el-radio>
            <el-radio :value="1">自定义日期</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="channelForm.validDateType === 1" label="有效期">
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
</style>
