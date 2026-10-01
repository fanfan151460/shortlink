<script setup>
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight, Delete, Refresh, RefreshLeft } from '@element-plus/icons-vue'

import { deleteRecycleBin, pageRecycle, recoverRecycleBin } from '@/api/recycle'
import { appStore, groupName, loadDeletedGroups, loadGroups } from '@/store/app'
import { faviconUrl, fmtDateTime } from '@/utils/format'

const PAGE_SIZE = 10

const rows = ref([])
const loading = ref(false)
const current = ref(1)
const hasMore = ref(false)

onMounted(loadDeletedGroups)

// 只盯 currentGid。曾经把 deletedGroups.length 也放进来，但它的唯一生产者就是
// 本页 onMounted 里的 loadDeletedGroups() 自己 —— 赋完新数组立刻触发这个 watch，
// 于是进一次回收站发两遍分页请求（回收站为空时就是两条一模一样的报错）。
// 恢复/删除后本来就会显式 load()，不需要靠 deletedGroups 变化来驱动。
watch(
  () => appStore.currentGid,
  () => {
    current.value = 1
    load()
  },
  { immediate: true }
)

async function load() {
  const gid = appStore.currentGid
  if (!gid) {
    rows.value = []
    hasMore.value = false
    return
  }
  loading.value = true
  try {
    const list = await pageRecycle({ gid, current: current.value, size: PAGE_SIZE })
    rows.value = list || []
    hasMore.value = rows.value.length >= PAGE_SIZE
  } catch {
    rows.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

async function onRecover(row) {
  try {
    await ElMessageBox.confirm('确定恢复该短链接？', '恢复', {
      type: 'info',
      confirmButtonText: '恢复',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  try {
    await recoverRecycleBin(row.fullShortUrl, row.gid || appStore.currentGid)
    ElMessage.success('已恢复')
    await refreshBoth()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm('确定永久删除？此操作不可恢复。', '永久删除', {
      type: 'error',
      confirmButtonText: '永久删除',
      cancelButtonText: '取消',
      confirmButtonClass: 'el-button--danger'
    })
  } catch {
    return
  }
  try {
    await deleteRecycleBin(row.fullShortUrl, row.gid || appStore.currentGid)
    ElMessage.success('已永久删除')
    await load()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

/** 恢复后短链回到分组里，分组列表（含已删除分组）要一起刷新 */
async function refreshBoth() {
  await Promise.all([load(), loadDeletedGroups(), loadGroups()])
}

function go(delta) {
  const next = current.value + delta
  if (next < 1) return
  if (delta > 0 && !hasMore.value) return
  current.value = next
  load()
}
</script>

<template>
  <div class="page">
    <div v-if="!appStore.currentGid" class="empty">请从左侧选择一个分组</div>

    <template v-else>
      <div class="page-header">
        <div class="page-title">
          <span class="title-badge"><el-icon :size="17"><Delete /></el-icon></span>
          <div>
            <h2>回收站 · {{ groupName(appStore.currentGid) || '已删除分组' }}</h2>
            <p class="page-sub">这里的短链接仍保留数据，可恢复回原分组，也可永久删除</p>
          </div>
        </div>
        <el-tooltip content="刷新" placement="top" :show-after="400">
          <el-button size="small" :icon="Refresh" @click="refreshBoth" />
        </el-tooltip>
      </div>

      <el-table v-loading="loading" :data="rows" border>
        <el-table-column label="短链接" min-width="200">
          <template #default="{ row }">
            <img
              v-if="row.favicon || faviconUrl(row.originUrl)"
              class="favicon"
              :src="row.favicon || faviconUrl(row.originUrl)"
              alt=""
              @error="(e) => (e.target.style.display = 'none')"
            />
            <span class="mono">{{ row.fullShortUrl }}</span>
          </template>
        </el-table-column>

        <el-table-column label="原始链接" min-width="240">
          <template #default="{ row }">
            <span class="ellipsis" :title="row.originUrl">{{ row.originUrl || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="描述" min-width="140">
          <template #default="{ row }">{{ row.description || '-' }}</template>
        </el-table-column>

        <el-table-column label="删除时间" width="150">
          <template #default="{ row }">{{ fmtDateTime(row.updateTime) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-tooltip content="恢复" placement="top" :show-after="400">
              <el-button
                link
                type="primary"
                size="small"
                :icon="RefreshLeft"
                @click="onRecover(row)"
              />
            </el-tooltip>
            <el-tooltip content="彻底删除" placement="top" :show-after="400">
              <el-button link type="danger" size="small" :icon="Delete" @click="onDelete(row)" />
            </el-tooltip>
          </template>
        </el-table-column>

        <template #empty>回收站为空</template>
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
  </div>
</template>

<style scoped>
.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 0;
}
</style>
