<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'

import { pageLink } from '@/api/link'
import {
  getAccessStats,
  getBrowserStats,
  getDashboard,
  getDeviceStats,
  getLocaleStats,
  getNetworkStats,
  getOsStats
} from '@/api/stats'
import AccessLogTable from '@/components/AccessLogTable.vue'
import { appStore, loadGroups } from '@/store/app'
import { daysAgo, today } from '@/utils/format'

const route = useRoute()

// 中国地图 GeoJSON 约 570KB。用动态 import 让它单独成一个 chunk：
// 首页和其它页面完全不用下载它，只有真的打开统计页才拉这一次（之后走缓存）。
let chinaNameSet = new Set()
let geoPromise = null
const geoLoaded = ref(false)

function ensureChinaMap() {
  if (!geoPromise) {
    geoPromise = import('@/assets/china.json').then((m) => {
      echarts.registerMap('china', m.default)
      chinaNameSet = new Set(m.default.features.map((f) => f.properties.name))
      geoLoaded.value = true
      return m.default
    })
  }
  return geoPromise
}

const gid = ref('')
const fullShortUrl = ref('')
const linkOptions = ref([])
const dateRange = ref([daysAgo(7), today()])
const splitMode = ref(false)
const loading = ref(false)

/** { locale, os, browser, device, network, access } */
const data = ref({})

const dims = [
  { key: 'os', title: '操作系统' },
  { key: 'browser', title: '浏览器' },
  { key: 'device', title: '设备类型' },
  { key: 'network', title: '网络类型' }
]

const CHART_COLORS = [
  '#4f46e5', '#f59e0b', '#22c55e', '#ef4444', '#06b6d4',
  '#8b5cf6', '#ec4899', '#14b8a6', '#f97316', '#6366f1'
]

// DOM 元素用普通对象存就行，不需要响应式（响应式反而会让 echarts 实例被代理）
const pieEls = {}
const mapEl = ref(null)
const trendEl = ref(null)
const charts = []

onMounted(async () => {
  window.addEventListener('resize', onResize)
  await loadGroups()
  gid.value = route.query.gid || appStore.currentGid || ''
  fullShortUrl.value = route.query.fullShortUrl || ''
  if (gid.value) await loadLinkOptions()
  if (fullShortUrl.value) load()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  disposeAll()
})

watch(gid, async (v) => {
  appStore.currentGid = v
  fullShortUrl.value = ''
  data.value = {}
  disposeAll()
  if (v) await loadLinkOptions()
})

watch([fullShortUrl, dateRange], () => {
  if (fullShortUrl.value) load()
})

async function loadLinkOptions() {
  try {
    linkOptions.value = (await pageLink({ gid: gid.value, current: 1, size: 50 })) || []
  } catch {
    linkOptions.value = []
  }
}

function preset(days) {
  dateRange.value = [daysAgo(days), today()]
}

async function load() {
  const body = {
    gid: gid.value,
    fullShortUrl: fullShortUrl.value,
    startDate: dateRange.value[0],
    endDate: dateRange.value[1]
  }
  loading.value = true
  disposeAll()
  try {
    if (splitMode.value) {
      // 6 个独立接口并发扇出 —— 用来和 dashboard 的 1 次聚合做对比
      const [locale, os, browser, device, network, access] = await Promise.all([
        getLocaleStats(body),
        getOsStats(body),
        getBrowserStats(body),
        getDeviceStats(body),
        getNetworkStats(body),
        getAccessStats(body)
      ])
      data.value = { locale, os, browser, device, network, access }
    } else {
      data.value = (await getDashboard(body)) || {}
    }
    // 地图数据按需加载，第一次打开统计页会多等一下这个 chunk
    if (items('locale').length) await ensureChinaMap()
    await nextTick()
    renderCharts()
  } catch {
    data.value = {}
  } finally {
    loading.value = false
  }
}

function disposeAll() {
  while (charts.length) {
    const c = charts.pop()
    if (c) c.dispose()
  }
}

function items(key) {
  return (data.value && data.value[key]) || []
}

function renderCharts() {
  renderMap()
  dims.forEach((d) => renderPie(d.key))
  renderTrend()
}

function renderMap() {
  const list = items('locale')
  if (!mapEl.value || !list.length) return
  const chart = echarts.init(mapEl.value)
  charts.push(chart)

  // 高德返回的是省全称（"广东省"），和 GeoJSON 里的 name 能对上；
  // 对不上的（比如定位失败时写的"未知"）不往地图里塞，在下面单独列出来，
  // 免得数据被静默丢掉还看不出来。
  const mapped = list.filter((i) => chinaNameSet.has(i.name))
  const max = mapped.reduce((m, i) => Math.max(m, Number(i.value) || 0), 0)

  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (p) => `${p.name}<br/>访问量：${Number.isFinite(p.value) ? p.value : 0}`
    },
    visualMap: {
      min: 0,
      max: max || 1,
      left: 12,
      bottom: 12,
      text: ['高', '低'],
      calculable: true,
      inRange: { color: ['#e0e7ff', '#818cf8', '#4f46e5', '#3730a3'] }
    },
    series: [
      {
        type: 'map',
        map: 'china',
        roam: false,
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 11 },
          itemStyle: { areaColor: '#f59e0b' }
        },
        data: mapped.map((i) => ({ name: i.name, value: Number(i.value) || 0 }))
      }
    ]
  })
}

function renderPie(key) {
  const el = pieEls[key]
  const list = items(key)
  if (!el || !list.length) return
  const chart = echarts.init(el)
  charts.push(chart)
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 0,
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { fontSize: 11 }
    },
    series: [
      {
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['36%', '50%'],
        itemStyle: { borderWidth: 0 },
        label: { show: false },
        color: CHART_COLORS,
        data: list.map((i) => ({ name: i.name || '未知', value: Number(i.value) || 0 }))
      }
    ]
  })
}

function renderTrend() {
  const list = items('access')
  if (!trendEl.value || !list.length) return
  const chart = echarts.init(trendEl.value)
  charts.push(chart)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0, itemWidth: 12, textStyle: { fontSize: 11 } },
    grid: { left: 44, right: 20, top: 40, bottom: 30 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: list.map((i) => i.date || ''),
      axisLabel: { fontSize: 10 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      trendLine('PV', list.map((i) => i.pv || 0), '#4f46e5'),
      trendLine('UV', list.map((i) => i.uv || 0), '#f59e0b'),
      trendLine('UIP', list.map((i) => i.uip || 0), '#22c55e')
    ]
  })
}

function trendLine(name, values, color) {
  return {
    name,
    type: 'line',
    smooth: true,
    symbolSize: 5,
    data: values,
    itemStyle: { color },
    lineStyle: { color }
  }
}

function unmatchedLocale() {
  return items('locale').filter((i) => !chinaNameSet.has(i.name))
}

function onResize() {
  charts.forEach((c) => c.resize())
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2>数据统计</h2>
    </div>

    <el-card shadow="never" class="filters">
      <div class="filter-row">
        <span class="label">分组</span>
        <el-select v-model="gid" placeholder="选择分组" size="small" style="width: 170px">
          <el-option v-for="g in appStore.groups" :key="g.gid" :label="g.name" :value="g.gid" />
        </el-select>

        <span class="label">短链接</span>
        <el-select
          v-model="fullShortUrl"
          placeholder="选择短链接"
          size="small"
          style="width: 250px"
          :disabled="!gid"
        >
          <el-option
            v-for="l in linkOptions"
            :key="l.fullShortUrl"
            :label="l.fullShortUrl"
            :value="l.fullShortUrl"
          />
        </el-select>
      </div>

      <div class="filter-row">
        <span class="label">时间</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          size="small"
          start-placeholder="开始"
          end-placeholder="结束"
        />
        <el-button size="small" @click="preset(7)">近 7 天</el-button>
        <el-button size="small" @click="preset(30)">近 30 天</el-button>
        <el-button size="small" @click="preset(90)">近 90 天</el-button>
      </div>

      <div class="filter-row">
        <!--
          同一个页面两种取数方式：默认走 /stats/dashboard 一次拿全，
          勾上则改成 6 个独立接口并发扇出。开关打开后能在浏览器 Network 里
          直接看到"1 次聚合请求"和"6 次并发请求"的差别。
        -->
        <el-checkbox v-model="splitMode" size="small">分开请求（6 个接口并发）</el-checkbox>
        <span class="muted">默认用 /stats/dashboard 聚合接口，1 次请求拿全 5 个维度 + 访问趋势</span>
      </div>
    </el-card>

    <div v-if="!gid || !fullShortUrl" class="empty">请先选择分组和短链接</div>

    <template v-else>
      <el-card v-loading="loading" shadow="never" class="block">
        <template #header>地区分布</template>
        <div v-if="!items('locale').length" class="empty">暂无数据</div>
        <!-- 地图 chunk 还没加载完时先别渲染，否则"未匹配省份"会把全部数据都列一遍 -->
        <div v-else-if="!geoLoaded" class="empty">地图加载中…</div>
        <template v-else>
          <div ref="mapEl" class="map"></div>
          <div v-if="unmatchedLocale().length" class="muted" style="margin-top: 8px">
            未匹配到省份（未计入地图）：
            <span v-for="i in unmatchedLocale()" :key="i.name" class="unmatched">
              {{ i.name }}（{{ i.value }}）
            </span>
          </div>
        </template>
      </el-card>

      <div class="grid">
        <el-card v-for="d in dims" :key="d.key" shadow="never" class="block">
          <template #header>{{ d.title }}</template>
          <div v-if="!items(d.key).length" class="empty">暂无数据</div>
          <div v-else :ref="(el) => (pieEls[d.key] = el)" class="pie"></div>
        </el-card>
      </div>

      <el-card shadow="never" class="block">
        <template #header>访问趋势（PV / UV / UIP）</template>
        <div v-if="!items('access').length" class="empty">暂无数据</div>
        <div v-else ref="trendEl" class="trend"></div>
      </el-card>

      <el-card shadow="never" class="block">
        <template #header>访问明细</template>
        <AccessLogTable :gid="gid" :fullShortUrl="fullShortUrl" />
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.filters {
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-row + .filter-row {
  margin-top: 12px;
}

.filter-row .label {
  font-size: 13px;
  color: #64748b;
}

.block {
  margin-bottom: 16px;
}

.map {
  height: 460px;
  width: 100%;
}

.pie {
  height: 220px;
  width: 100%;
}

.trend {
  height: 280px;
  width: 100%;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.unmatched {
  margin-right: 8px;
}
</style>
