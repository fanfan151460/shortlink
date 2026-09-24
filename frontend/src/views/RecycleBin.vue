<script setup>
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { deleteRecycleBin, pageRecycle, recoverRecycleBin } from '@/api/recycle'
import { appStore, groupName, loadDeletedGroups, loadGroups } from '@/store/app'
import { faviconUrl, fmtDateTime } from '@/utils/format'

const PAGE_SIZE = 10

const rows = ref([])
const loading = ref(false)
const current = ref(1)
const hasMore = ref(false)

onMounted(loadDeletedGroups)

watch(
  () => [appStore.currentGid, appStore.deletedGroups.length],
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
        <h2>回收站 · {{ groupName(appStore.currentGid) || '已删除分组' }}</h2>
        <el-button size="small" @click="refreshBoth">刷新</el-button>
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

        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="onRecover(row)">恢复</el-button>
            <el-button link type="danger" size="small" @click="onDelete(row)">彻底删除</el-button>
          </template>
        </el-table-column>

        <template #empty>回收站为空</template>
      </el-table>

      <div class="pager">
        <el-button size="small" :disabled="current <= 1" @click="go(-1)">上一页</el-button>
        <span class="muted">第 {{ current }} 页</span>
        <el-button size="small" :disabled="!hasMore" @click="go(1)">下一页</el-button>
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
