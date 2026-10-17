<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  ArrowRight,
  Delete,
  Document,
  Edit,
  Link,
  Plus,
  QuestionFilled,
  Refresh,
  TrendCharts
} from '@element-plus/icons-vue'

import { batchDisableLink, batchEnableLink, pageLink, removeLink } from '@/api/link'
import { countRecycleBinAll, saveRecycleBinAll } from '@/api/recycle'
import AccessLogTable from '@/components/AccessLogTable.vue'
import LinkFormDialog from '@/components/LinkFormDialog.vue'
import { appStore, groupName } from '@/store/app'
import { faviconUrl, fmtDate, isExpired } from '@/utils/format'

const router = useRouter()

const PAGE_SIZE = 10

const rows = ref([])
const loading = ref(false)
const current = ref(1)
const hasMore = ref(false)

// 后端只认这三个值，传别的不会报错，而是静默退化成按创建时间倒序。
// 空串表示"不传 orderFlag"，同样落到默认排序。
const orderFlag = ref('')
const orderOptions = [
  { label: '按创建时间', value: '' },
  { label: '按总 PV', value: 'totalPv' },
  { label: '按总 UV', value: 'totalUv' },
  { label: '按总 IP', value: 'totalUip' }
]

// 表头「短链接」列上的开关：打开后把活动名下的渠道短链也列出来。
// 默认关 —— 活动短链归活动页管，混在普通列表里会让人以为能在这儿编辑。
const showActivity = ref(false)

const dialogVisible = ref(false)
const dialogMode = ref('create')
const editingLink = ref(null)

const logDrawer = ref(false)
const logTarget = ref({ gid: '', fullShortUrl: '' })

const currentLink = computed(() => editingLink.value)

watch(
  () => appStore.currentGid,
  () => {
    current.value = 1
    load()
  },
  { immediate: true }
)

watch(orderFlag, () => {
  current.value = 1
  load()
})

// 切换可见范围等于换了一份结果集，页码必须回到第 1 页，否则会停在一个空页上
watch(showActivity, () => {
  current.value = 1
  load()
})

async function load() {
  const gid = appStore.currentGid
  if (!gid) {
    rows.value = []
    hasMore.value = false
    return
  }
  loading.value = true
  try {
    const list = await pageLink({
      gid,
      current: current.value,
      size: PAGE_SIZE,
      orderFlag: orderFlag.value || undefined,
      includeActivity: showActivity.value || undefined
    })
    rows.value = list || []
    // 接口不返回总数，只能按"这一页取满了"推断还能往后翻
    hasMore.value = rows.value.length >= PAGE_SIZE
  } catch {
    rows.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

function openCreate() {
  dialogMode.value = 'create'
  editingLink.value = null
  dialogVisible.value = true
}

function openEdit(row) {
  dialogMode.value = 'edit'
  editingLink.value = row
  dialogVisible.value = true
}

function openLogs(row) {
  logTarget.value = { gid: row.gid || appStore.currentGid, fullShortUrl: row.fullShortUrl }
  logDrawer.value = true
}

function openStats(row) {
  router.push({
    name: 'stats',
    query: { gid: row.gid || appStore.currentGid, fullShortUrl: row.fullShortUrl }
  })
}

async function onRemove(row) {
  try {
    await ElMessageBox.confirm('确定删除该短链接？它会被移入回收站。', '删除', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  try {
    await removeLink(row.fullShortUrl, row.gid || appStore.currentGid)
    ElMessage.success('已移入回收站')
    load()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

function go(delta) {
  const next = current.value + delta
  if (next < 1) return
  if (delta > 0 && !hasMore.value) return
  current.value = next
  load()
}

/**
 * 启停只切 enableStatus（0 已启用 / 1 未启用），不动删除标识——回收站那条链路完全独立。
 * 后端会在 UPDATE 之后删对应方向的缓存，所以停用/启用都是立刻生效的。
 *
 * nextEnabled 来自状态列开关的 change 事件（true = 用户要启用）。
 */
async function onToggleStatus(row, nextEnabled) {
  const disabling = !nextEnabled
  try {
    await ElMessageBox.confirm(
      disabling
        ? `确定停用 ${row.fullShortUrl}？停用后访问会返回 404，链接不会被删除。`
        : `确定启用 ${row.fullShortUrl}？`,
      disabling ? '停用' : '启用',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  try {
    const body = { gid: row.gid || appStore.currentGid, fullShortUrls: [row.fullShortUrl] }
    await (disabling ? batchDisableLink(body) : batchEnableLink(body))
    ElMessage.success(disabling ? '已停用' : '已启用')
    load()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

async function onEmptyGroup() {
  const gid = appStore.currentGid
  // 先问一下会扫走多少条。整组移入是无差别全扫，活动名下的渠道短链也会被带走，
  // 但列表默认不显示它们 —— 不报数就会出现"界面上 7 条、实际移走 11 条"。
  let total = null
  try {
    total = await countRecycleBinAll(gid)
  } catch {
    /* 数不出来就退回老文案，别因此把整组操作堵死 */
  }
  const message = total
    ? `确定把该分组下的全部短链接移入回收站？分组本身会保留。本次会移走 ${total} 条未删除的短链接，含活动名下的渠道短链。`
    : '确定把该分组下的全部短链接移入回收站？分组本身会保留。'
  try {
    await ElMessageBox.confirm(message, '整组移入回收站', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  try {
    const affected = await saveRecycleBinAll(gid)
    ElMessage.success(affected ? '已整组移入回收站' : '该分组没有可移动的短链接')
    load()
  } catch {
    /* 拦截器已经提示过了 */
  }
}
</script>

<template>
  <div class="page">
    <div v-if="!appStore.currentGid" class="empty">请从左侧选择一个分组</div>

    <template v-else>
      <div class="page-header">
        <div class="page-title">
          <span class="title-badge"><el-icon :size="17"><Link /></el-icon></span>
          <div>
            <h2>{{ groupName(appStore.currentGid) || '短链接管理' }}</h2>
            <p class="page-sub">该分组下的全部短链接，可编辑、停用、查看访问记录</p>
          </div>
        </div>
        <div class="toolbar">
          <el-select v-model="orderFlag" size="small" style="width: 140px">
            <el-option
              v-for="o in orderOptions"
              :key="o.value"
              :label="o.label"
              :value="o.value"
            />
          </el-select>
          <el-tooltip content="刷新" placement="top" :show-after="400">
            <el-button size="small" :icon="Refresh" @click="load" />
          </el-tooltip>
          <!-- 破坏性动作保留文字：图标不足以表达"整组"这种量级 -->
          <el-button size="small" @click="onEmptyGroup">整组移入回收站</el-button>
          <el-button type="primary" size="small" :icon="Plus" @click="openCreate">创建短链接</el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="rows" border>
        <el-table-column min-width="250">
          <!--
            可见范围放在表头：列表默认只有普通短链，活动名下的渠道短链要显式打开。
            判据是 activity_id 本身，和活动是否过期/被删无关，所以开关文案不提这些。
          -->
          <template #header>
            <div class="col-head">
              <span>短链接</span>
              <el-tooltip
                content="打开后把活动名下的渠道短链也列出来"
                placement="top"
                :show-after="300"
              >
                <span class="act-toggle">
                  <el-switch v-model="showActivity" size="small" />
                  <span class="act-toggle-text">含活动</span>
                </span>
              </el-tooltip>
            </div>
          </template>
          <template #default="{ row }">
            <div class="link-cell">
              <img
                v-if="faviconUrl(row.originUrl)"
                class="favicon"
                :src="faviconUrl(row.originUrl)"
                alt=""
                @error="(e) => (e.target.style.display = 'none')"
              />
              <a :href="row.fullShortUrl" target="_blank" rel="noreferrer" class="mono">
                {{ row.fullShortUrl }}
              </a>
              <!-- 活动渠道行：标出它属于哪个活动、哪个渠道，否则和普通短链看不出区别 -->
              <span v-if="row.activityId" class="act-tag" :title="row.activityName">
                {{ row.activityName || '活动' }} · {{ row.channel || '渠道' }}
              </span>
              <!-- 有效期不单开一列，跟在链接后面当个小标签，免得表格再多一格 -->
              <span
                class="expire"
                :class="{ forever: row.validDateType !== 1, past: isExpired(row) }"
              >
                {{ row.validDateType === 1 ? fmtDate(row.validDate) + ' 到期' : '永久' }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="原始链接" min-width="180">
          <template #default="{ row }">
            <span class="ellipsis" :title="row.originUrl">{{ row.originUrl }}</span>
          </template>
        </el-table-column>

        <el-table-column label="描述" min-width="140">
          <template #default="{ row }">{{ row.description || '-' }}</template>
        </el-table-column>

        <!--
          列表只展示累计统计，不展示"今日"列。后端是给值的：分页 SQL 里确实没有
          today_pv（那三列在 t_link_stats_today，分片表不和 t_link 同表），但
          pageShortLink 随后单独查了一次补进 DTO。这里不渲染只是列表的取舍。
        -->
        <el-table-column width="200" align="center">
          <template #header>
            <span>历史 PV / UV / IP</span>
            <el-tooltip placement="top" :show-after="200">
              <template #content>
                PV：访问量，每打开一次算一次<br />
                UV：独立访客，同一个人当天只算一次<br />
                IP：独立 IP 数，同一个 IP 当天只算一次
              </template>
              <el-icon class="help-icon"><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <template #default="{ row }">
            <span class="mono">
              {{ row.totalPv || 0 }} / {{ row.totalUv || 0 }} / {{ row.totalUip || 0 }}
            </span>
          </template>
        </el-table-column>

        <!--
          enableStatus 语义以 Java 为准：0 = 已启用，1 = 未启用
          （sql/03_link.sql 的 DDL 注释写反了，别抄）。
          受控写法：只绑 model-value，不 mutate row，等接口成功、列表刷新后开关才动，
          不会"先滑过去再弹框"。改回旧值要靠重新 load()。

          过期判定放在前端：后端只看 enable_status，到期了这列仍显示"已启用"。
          链接本身已经打不开，再显示启用就是骗人，所以过期一律按禁用显示、且不给点
          （点了也没用，后端允许启用但访问依旧 404）。
        -->
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tooltip
              :content="
                isExpired(row)
                  ? '已过期，无法启用'
                  : row.enableStatus === 1
                    ? '点击启用'
                    : '点击停用'
              "
              placement="top"
              :show-after="400"
            >
              <el-switch
                class="switch-enable"
                :model-value="row.enableStatus !== 1 && !isExpired(row)"
                :disabled="isExpired(row)"
                @change="(v) => onToggleStatus(row, v)"
              />
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="170" align="center">
          <template #default="{ row }">
            <el-tooltip
              :content="row.activityId ? '活动渠道短链请在活动页编辑' : '编辑'"
              placement="top"
              :show-after="400"
            >
              <el-button
                link
                type="primary"
                size="small"
                :icon="Edit"
                :disabled="!!row.activityId"
                @click="openEdit(row)"
              />
            </el-tooltip>
            <el-tooltip content="数据统计" placement="top" :show-after="400">
              <el-button
                link
                type="primary"
                size="small"
                :icon="TrendCharts"
                @click="openStats(row)"
              />
            </el-tooltip>
            <el-tooltip content="访问记录" placement="top" :show-after="400">
              <el-button link type="primary" size="small" :icon="Document" @click="openLogs(row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top" :show-after="400">
              <el-button link type="danger" size="small" :icon="Delete" @click="onRemove(row)" />
            </el-tooltip>
          </template>
        </el-table-column>

        <template #empty>此分组暂无短链接</template>
      </el-table>

      <div class="pager">
        <el-tooltip content="上一页" placement="top" :show-after="400">
          <el-button size="small" :icon="ArrowLeft" :disabled="current <= 1" @click="go(-1)" />
        </el-tooltip>
        <span class="muted">{{ current }}</span>
        <el-tooltip content="下一页" placement="top" :show-after="400">
          <el-button size="small" :icon="ArrowRight" :disabled="!hasMore" @click="go(1)" />
        </el-tooltip>
      </div>
    </template>

    <LinkFormDialog
      v-model="dialogVisible"
      :mode="dialogMode"
      :gid="appStore.currentGid"
      :link="currentLink"
      @saved="load"
    />

    <el-drawer v-model="logDrawer" title="访问记录" size="720px">
      <div class="muted" style="margin-bottom: 12px">
        短链接：<code class="mono">{{ logTarget.fullShortUrl }}</code>
      </div>
      <AccessLogTable
        v-if="logDrawer"
        :gid="logTarget.gid"
        :fullShortUrl="logTarget.fullShortUrl"
      />
    </el-drawer>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 表头里的可见范围开关：和列名同排，字号压小不抢标题 */
.col-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.act-toggle {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 400;
}

.act-toggle-text {
  font-size: 12px;
  color: var(--ink-400);
}

/* 链接和有效期同一行：链接可以截断，有效期标签不被压缩 */
.link-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.link-cell .mono {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 活动渠道标记：活动名可能很长，截断掉，完整值走 title */
.act-tag {
  flex-shrink: 0;
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 6px;
  border-radius: 999px;
  font-size: 11px;
  line-height: 17px;
  background: #ede9fe;
  color: #6d28d9;
}

.expire {
  flex-shrink: 0;
  padding: 0 6px;
  border-radius: 999px;
  font-size: 11px;
  line-height: 17px;
  background: #fef3c7;
  color: #b45309;
}

.expire.forever {
  background: #f1f5f9;
  color: var(--ink-400);
}

/* 已过期：和状态列的"禁用"呼应，一眼看出开关为什么是关的 */
.expire.past {
  background: #fee2e2;
  color: #b91c1c;
}

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 0;
}
</style>
