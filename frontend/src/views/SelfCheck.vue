<script setup>
/**
 * 接口自检页（验收工具，不是业务功能）
 *
 * 目的：一次把 admin 的全部 32 个接口打一遍，把 HTTP 状态码、耗时、返回码摊开，
 * 给出一句"通过 N/32"。后端每次改动跑一遍就是最便宜的回归。
 *
 * 分成三类，是因为副作用差别很大：
 *   A 只读（17）—— 不动任何数据，随时可跑
 *   B 会话（2）  —— 登录会挤占 token 名额、登出会直接退出，只能手动单点
 *   C 账号（2）  —— 注册会真的建一个用户，只能手动单点
 *   D 数据（11） —— 会在"自检临时分组"下建链接再清掉，跑完自动回收
 *
 * 所有请求都带 raw: true，拿到的是完整的 Result（含 code），而不是解包后的 data，
 * 否则业务错误和成功在前端就区分不出来了。silent: true 让它别弹一堆 toast。
 */
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import request from '@/api/request'
import { appStore, loadGroups } from '@/store/app'
import { getToken, getUsername } from '@/utils/auth'
import { daysAgo, today } from '@/utils/format'

const username = ref(getUsername())
const busy = ref(false)
const running = ref('')
const summary = ref({ pass: 0, total: 0 })

// 每个用例的执行结果，key 是 path+method
const results = reactive({})
function keyOf(ep) {
  return `${ep.method} ${ep.path}`
}

async function call(method, url, { params, data } = {}) {
  const started = performance.now()
  try {
    const result = await request({ method, url, params, data, raw: true, silent: true })
    const code = result && result.code
    return {
      ok: code === '0',
      status: 200,
      ms: Math.round(performance.now() - started),
      summary: code === '0' ? brief(result.data) : `code=${code} ${result.message || ''}`
    }
  } catch (e) {
    const status = e.response && e.response.status
    return {
      ok: false,
      status: status || 'ERR',
      ms: Math.round(performance.now() - started),
      // 网关鉴权失败是 401 + 空 body，这里明确写出来，别让人以为是解析问题
      summary: status === 401 ? '401（网关鉴权失败，body 为空）' : e.message
    }
  }
}

function brief(data) {
  if (data === undefined || data === null) return '(无 data)'
  let text
  if (Array.isArray(data)) text = `Array(${data.length}) ${JSON.stringify(data[0] ?? null)}`
  else if (typeof data === 'object') text = JSON.stringify(data)
  else text = String(data)
  return text.length > 140 ? text.slice(0, 140) + '…' : text
}

const statsBody = () => ({
  gid: appStore.groups[0] ? appStore.groups[0].gid : '',
  fullShortUrl: '',
  startDate: daysAgo(30),
  endDate: today()
})

// ---------------------------------------------------------------- A 只读 17 个
const readOnly = [
  { method: 'GET', path: '/test', desc: '服务存活探针（网关白名单）', run: () => call('get', '/test') },
  {
    method: 'GET',
    path: '/user/{username}',
    desc: '查询用户信息',
    run: () => call('get', `/user/${encodeURIComponent(username.value)}`)
  },
  {
    method: 'GET',
    path: '/user/has-username',
    desc: '用户名是否可用（返回 false 表示已占用）',
    run: () => call('get', '/user/has-username', { params: { username: username.value } })
  },
  {
    method: 'GET',
    path: '/user/check-login',
    desc: '校验登录态',
    run: () => call('get', '/user/check-login', { params: { username: username.value, token: getToken() } })
  },
  { method: 'GET', path: '/group', desc: '分组列表', run: () => call('get', '/group') },
  { method: 'GET', path: '/group/deleted', desc: '已删除分组列表', run: () => call('get', '/group/deleted') },
  {
    method: 'GET',
    path: '/page',
    desc: '短链接分页（orderFlag 三选一，其它值静默退化成按创建时间）',
    run: () =>
      call('get', '/page', {
        params: {
          gid: appStore.groups[0] ? appStore.groups[0].gid : '',
          current: 1,
          size: 10,
          orderFlag: 'totalPv'
        }
      })
  },
  {
    method: 'GET',
    path: '/recycle-bin/page',
    desc: '回收站分页',
    run: () =>
      call('get', '/recycle-bin/page', {
        params: { gid: appStore.groups[0] ? appStore.groups[0].gid : '', current: 1, size: 10 }
      })
  },
  {
    method: 'GET',
    path: '/title',
    desc: '抓取目标网页标题（会真的去访问外网）',
    run: () => call('get', '/title', { params: { url: 'https://www.baidu.com' } })
  },
  {
    method: 'POST',
    path: '/stats/dashboard',
    desc: '5 个维度 + 访问趋势的聚合接口',
    run: () => call('post', '/stats/dashboard', { data: statsBody() })
  },
  {
    method: 'POST',
    path: '/stats/locale',
    desc: '地区分布（省份全称）',
    run: () => call('post', '/stats/locale', { data: statsBody() })
  },
  { method: 'POST', path: '/stats/os', desc: '操作系统分布', run: () => call('post', '/stats/os', { data: statsBody() }) },
  { method: 'POST', path: '/stats/browser', desc: '浏览器分布', run: () => call('post', '/stats/browser', { data: statsBody() }) },
  { method: 'POST', path: '/stats/device', desc: '设备类型分布', run: () => call('post', '/stats/device', { data: statsBody() }) },
  { method: 'POST', path: '/stats/network', desc: '网络类型分布', run: () => call('post', '/stats/network', { data: statsBody() }) },
  { method: 'POST', path: '/stats/access', desc: '访问趋势（按天 PV/UV/UIP）', run: () => call('post', '/stats/access', { data: statsBody() }) },
  {
    method: 'POST',
    path: '/stats/access-record',
    desc: '访问明细记录',
    run: () =>
      call('post', '/stats/access-record', {
        data: { gid: appStore.groups[0] ? appStore.groups[0].gid : '', fullShortUrl: '', current: 1, size: 10 }
      })
  }
]

// ---------------------------------------------------------------- B 会话 2 个
const session = [
  {
    method: 'POST',
    path: '/user/login',
    desc: '登录。注意：最多保留 3 个 token，第 4 次登录会清掉之前的',
    manual: true,
    run: () => call('post', '/user/login', { data: { username: username.value, password: '' } })
  },
  {
    method: 'DELETE',
    path: '/user/logout',
    desc: '登出。手动点完这个会真的退出登录',
    manual: true,
    run: () => call('delete', '/user/logout', { params: { username: username.value, token: getToken() } })
  }
]

// ---------------------------------------------------------------- C 账号 2 个
const account = [
  {
    method: 'POST',
    path: '/user',
    desc: '注册。手动点会在库里留下一个真实的用户记录',
    manual: true,
    run: () => {
      const name = `selfcheck_${Date.now().toString(36).slice(-6)}`
      return call('post', '/user', { data: { username: name, password: '123456' } })
    }
  },
  {
    method: 'PUT',
    path: '/user',
    desc: '改个人信息。这里只发 username，服务端按非空字段更新，等于空跑一次真实请求',
    manual: true,
    run: () => call('put', '/user', { data: { username: username.value } })
  }
]

// ---------------------------------------------------------------- D 数据 11 个
// 顺序有依赖，共享 ctx：先建临时分组，再在里面建链接，最后清干净
const ctx = { gid: '', fullShortUrl: '' }

const writeFlow = [
  {
    method: 'POST',
    path: '/group',
    desc: '新建一个"自检临时分组"',
    run: async () => {
      const name = `自检-${Date.now().toString(36).slice(-6)}`
      ctx.name = name
      const res = await call('post', '/group', { data: { name } })
      if (res.ok) {
        // 新建接口不返回 gid，只能回查一次列表
        const list = await request.get('/group', { silent: true })
        const hit = (list || []).find((g) => g.name === name)
        ctx.gid = hit ? hit.gid : ''
      }
      return res
    }
  },
  {
    method: 'PUT',
    path: '/group',
    desc: '把临时分组改名',
    run: () => call('put', '/group', { data: { gid: ctx.gid, name: `${ctx.name}-已改名` } })
  },
  {
    method: 'POST',
    path: '/group/sort',
    desc: '整体重排。读接口不返回 sortOrder，只能全量按 0 起重新编号',
    run: async () => {
      const list = await request.get('/group', { silent: true })
      const orders = (list || []).map((g, i) => ({ groupId: g.gid, sortOrder: i }))
      return call('post', '/group/sort', { data: orders })
    }
  },
  {
    method: 'POST',
    path: '/create',
    desc: '建一条短链接（后端有 3 秒幂等窗口，重复跑要间隔开）',
    run: async () => {
      const res = await call('post', '/create', {
        data: {
          originUrl: 'https://www.baidu.com',
          gid: ctx.gid,
          validDateType: 0,
          description: '自检临时短链'
        }
      })
      if (res.ok) {
        const list = await request.get('/page', {
          params: { gid: ctx.gid, current: 1, size: 10 },
          silent: true
        })
        ctx.fullShortUrl = list && list[0] ? list[0].fullShortUrl : ''
      }
      return res
    }
  },
  {
    method: 'PUT',
    path: '/update',
    desc: '改描述',
    run: () =>
      call('put', '/update', {
        data: {
          fullShortUrl: ctx.fullShortUrl,
          gid: ctx.gid,
          originUrl: 'https://www.baidu.com',
          validDateType: 0,
          description: '自检-已改描述'
        }
      })
  },
  {
    method: 'POST',
    path: '/recycle-bin/save',
    desc: '移入回收站（这条路径不过期 Redis 缓存）',
    run: () => call('post', '/recycle-bin/save', { data: { fullShortUrl: ctx.fullShortUrl, gid: ctx.gid } })
  },
  {
    method: 'POST',
    path: '/recycle-bin/recover',
    desc: '从回收站恢复',
    run: () => call('post', '/recycle-bin/recover', { data: { fullShortUrl: ctx.fullShortUrl, gid: ctx.gid } })
  },
  {
    method: 'DELETE',
    path: '/remove',
    desc: '移入回收站（这条会顺手删 Redis 缓存）',
    run: () => call('delete', '/remove', { data: { fullShortUrl: ctx.fullShortUrl, gid: ctx.gid } })
  },
  {
    method: 'GET',
    path: '/recycle-bin/save-all/{gid}',
    desc: '整组移入回收站，返回是否有短链被移动',
    run: () => call('get', `/recycle-bin/save-all/${encodeURIComponent(ctx.gid)}`)
  },
  {
    method: 'DELETE',
    path: '/recycle-bin/delete',
    desc: '永久删除',
    run: () => call('delete', '/recycle-bin/delete', { data: { fullShortUrl: ctx.fullShortUrl, gid: ctx.gid } })
  },
  {
    method: 'DELETE',
    path: '/group',
    desc: '删掉临时分组，收尾',
    run: async () => {
      const res = await call('delete', '/group', { params: { gid: ctx.gid } })
      ctx.gid = ''
      ctx.fullShortUrl = ''
      await loadGroups()
      return res
    }
  }
]

const groups = [
  { title: 'A · 只读接口', hint: '不动任何数据，随时可以跑', list: readOnly },
  { title: 'B · 会话接口', hint: '会挤占 token 名额或直接退出登录，只能手动单点', list: session },
  { title: 'C · 账号接口', hint: '会在库里留下真实记录，只能手动单点', list: account },
  { title: 'D · 数据接口', hint: '在临时分组里造数据，跑完自动清理', list: writeFlow }
]

const totalCount = readOnly.length + session.length + account.length + writeFlow.length

async function runOne(ep) {
  running.value = keyOf(ep)
  try {
    results[keyOf(ep)] = await ep.run()
  } finally {
    running.value = ''
    recount()
  }
}

async function runList(list) {
  for (const ep of list) {
    await runOne(ep)
  }
}

async function runReadOnly() {
  busy.value = true
  try {
    await runList(readOnly)
  } finally {
    busy.value = false
  }
}

async function runAll() {
  busy.value = true
  try {
    await runList(readOnly)
    await runList(writeFlow)
  } finally {
    busy.value = false
    ElMessage.success('已跑完只读 + 数据接口，会话/账号接口请手动单点')
  }
}

async function runDataOnly() {
  busy.value = true
  try {
    await runList(writeFlow)
  } finally {
    busy.value = false
  }
}

function recount() {
  summary.value = { pass: 0, total: totalCount }
  for (const g of groups) {
    for (const ep of g.list) {
      const r = results[keyOf(ep)]
      if (r && r.ok) summary.value.pass += 1
    }
  }
}

function resetAll() {
  Object.keys(results).forEach((k) => delete results[k])
  recount()
}

const allPass = computed(() => summary.value.pass === totalCount)

recount()
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2>接口自检</h2>
      <div class="actions">
        <span class="score" :class="{ ok: allPass }">
          通过 {{ summary.pass }} / {{ summary.total }}
        </span>
        <el-button size="small" :disabled="busy" @click="resetAll">清空</el-button>
        <el-button size="small" :disabled="busy" @click="runReadOnly">跑只读（17）</el-button>
        <el-button size="small" :disabled="busy" @click="runDataOnly">只跑数据写入（11）</el-button>
        <el-button type="primary" size="small" :disabled="busy" @click="runAll">
          跑只读 + 数据（28）
        </el-button>
      </div>
    </div>

    <el-alert type="warning" :closable="false" show-icon class="tip">
      <p>D 组会以当前登录用户（{{ username }}）的身份建一个"自检临时分组"，在里面建链接、走一遍回收站流程，最后把分组删掉。</p>
      <p>B / C 两组不会自动跑：登录会挤占 token 名额，登出会把你踢出去，注册会真的往库里写一个用户。</p>
    </el-alert>

    <el-card v-for="g in groups" :key="g.title" shadow="never" class="block">
      <template #header>
        <span>{{ g.title }}（{{ g.list.length }}）</span>
        <span class="muted" style="margin-left: 8px">{{ g.hint }}</span>
      </template>

      <el-table :data="g.list" size="small" border>
        <el-table-column label="方法" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="row.method === 'GET' ? 'info' : 'warning'" disable-transitions>
              {{ row.method }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="路径" min-width="210">
          <template #default="{ row }">
            <span class="mono">{{ row.path }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="desc" label="说明" min-width="260" />

        <el-table-column label="HTTP" width="80" align="center">
          <template #default="{ row }">
            <span v-if="results[keyOf(row)]">{{ results[keyOf(row)].status }}</span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>

        <el-table-column label="耗时" width="80" align="center">
          <template #default="{ row }">
            <span v-if="results[keyOf(row)]">{{ results[keyOf(row)].ms }}ms</span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>

        <el-table-column label="结果" min-width="300">
          <template #default="{ row }">
            <template v-if="results[keyOf(row)]">
              <el-tag
                size="small"
                :type="results[keyOf(row)].ok ? 'success' : 'danger'"
                disable-transitions
              >
                {{ results[keyOf(row)].ok ? '通过' : '失败' }}
              </el-tag>
              <span class="mono resp">{{ results[keyOf(row)].summary }}</span>
            </template>
            <span v-else class="muted">未执行</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="90" align="center">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              :loading="running === keyOf(row)"
              :disabled="busy"
              @click="runOne(row)"
            >
              单跑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.score {
  font-size: 14px;
  font-weight: 600;
  color: #ef4444;
}

.score.ok {
  color: #22c55e;
}

.tip {
  margin-bottom: 16px;
}

.tip p {
  margin: 4px 0;
  line-height: 1.6;
}

.block {
  margin-bottom: 16px;
}

.resp {
  margin-left: 8px;
  font-size: 12px;
  color: #64748b;
  word-break: break-all;
}
</style>
