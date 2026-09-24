<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { pageLink, removeLink } from '@/api/link'
import { saveRecycleBinAll } from '@/api/recycle'
import AccessLogTable from '@/components/AccessLogTable.vue'
import LinkFormDialog from '@/components/LinkFormDialog.vue'
import { appStore, groupName } from '@/store/app'
import { faviconUrl } from '@/utils/format'

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
      orderFlag: orderFlag.value || undefined
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

async function onEmptyGroup() {
  const gid = appStore.currentGid
  try {
    await ElMessageBox.confirm(
      '确定把该分组下的全部短链接移入回收站？分组本身会保留。',
      '整组移入回收站',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
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
        <h2>{{ groupName(appStore.currentGid) || '短链接管理' }}</h2>
        <div class="toolbar">
          <el-select v-model="orderFlag" size="small" style="width: 140px">
            <el-option
              v-for="o in orderOptions"
              :key="o.value"
              :label="o.label"
              :value="o.value"
            />
          </el-select>
          <el-button size="small" @click="load">刷新</el-button>
          <el-button size="small" @click="onEmptyGroup">整组移入回收站</el-button>
          <el-button type="primary" size="small" @click="openCreate">+ 创建短链接</el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="rows" border>
        <el-table-column label="短链接" min-width="200">
          <template #default="{ row }">
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
          </template>
        </el-table-column>

        <el-table-column label="原始链接" min-width="240">
          <template #default="{ row }">
            <span class="ellipsis" :title="row.originUrl">{{ row.originUrl }}</span>
          </template>
        </el-table-column>

        <el-table-column label="描述" min-width="140">
          <template #default="{ row }">{{ row.description || '-' }}</template>
        </el-table-column>

        <!--
          只有累计统计，没有"今日"列：ShortLinkRespDTO 里虽然有 todayPv / todayUv /
          todayIpCount 三个字段，但分页 SQL 根本没 select 它们，值恒为 null。
        -->
        <el-table-column label="历史 PV / UV / IP" width="170" align="center">
          <template #default="{ row }">
            <span class="mono">
              {{ row.totalPv || 0 }} / {{ row.totalUv || 0 }} / {{ row.totalUip || 0 }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="260" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="openStats(row)">统计</el-button>
            <el-button link type="primary" size="small" @click="openLogs(row)">访问记录</el-button>
            <el-button link type="danger" size="small" @click="onRemove(row)">删除</el-button>
          </template>
        </el-table-column>

        <template #empty>此分组暂无短链接</template>
      </el-table>

      <div class="pager">
        <el-button size="small" :disabled="current <= 1" @click="go(-1)">上一页</el-button>
        <span class="muted">第 {{ current }} 页</span>
        <el-button size="small" :disabled="!hasMore" @click="go(1)">下一页</el-button>
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

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 0;
}
</style>
