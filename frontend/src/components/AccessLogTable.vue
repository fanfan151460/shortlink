<script setup>
import { onMounted, ref, watch } from 'vue'

import { getAccessRecords } from '@/api/stats'

const props = defineProps({
  gid: { type: String, required: true },
  fullShortUrl: { type: String, required: true },
  pageSize: { type: Number, default: 10 }
})

defineExpose({ reload })

const rows = ref([])
const loading = ref(false)
const current = ref(1)
const hasMore = ref(false)
const dateRange = ref([])

watch(
  () => props.fullShortUrl,
  () => {
    current.value = 1
    dateRange.value = []
    load()
  }
)

onMounted(load)

async function load() {
  if (!props.gid || !props.fullShortUrl) return
  loading.value = true
  try {
    const body = {
      fullShortUrl: props.fullShortUrl,
      gid: props.gid,
      current: current.value,
      size: props.pageSize
    }
    if (dateRange.value && dateRange.value.length === 2) {
      body.startDate = dateRange.value[0]
      body.endDate = dateRange.value[1]
    }
    const list = await getAccessRecords(body)
    rows.value = list || []
    // 接口没有返回 total，只能靠"这一页拿满了"推断还有下一页
    hasMore.value = rows.value.length >= props.pageSize
  } catch {
    rows.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

function reload() {
  current.value = 1
  load()
}

function onFilter() {
  current.value = 1
  load()
}

function onReset() {
  dateRange.value = []
  current.value = 1
  load()
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
  <div class="log-wrap">
    <div class="log-toolbar">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        value-format="YYYY-MM-DD"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        size="small"
      />
      <el-button type="primary" size="small" @click="onFilter">筛选</el-button>
      <el-button size="small" @click="onReset">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" size="small" border>
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column prop="browser" label="浏览器" width="110" />
      <el-table-column prop="os" label="操作系统" width="110" />
      <el-table-column prop="device" label="设备" width="100" />
      <el-table-column prop="network" label="网络" width="90" />
      <el-table-column prop="locale" label="地区" min-width="100" />
      <template #empty>暂无访问记录</template>
    </el-table>

    <div class="log-pager">
      <el-button size="small" :disabled="current <= 1" @click="go(-1)">上一页</el-button>
      <span class="muted">第 {{ current }} 页</span>
      <el-button size="small" :disabled="!hasMore" @click="go(1)">下一页</el-button>
    </div>
  </div>
</template>

<style scoped>
.log-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.log-pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding-top: 12px;
}
</style>
