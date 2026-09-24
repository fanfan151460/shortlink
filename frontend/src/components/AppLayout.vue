<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { addGroup, deleteGroup } from '@/api/group'
import { logout as logoutApi } from '@/api/user'
import { appStore, loadDeletedGroups, loadGroups } from '@/store/app'
import { clearSession, getToken, getUsername } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const newGroupName = ref('')
const creating = ref(false)

// 后端 saveGroup 是 `if (count > 10) throw`，即已有 11 个时再建第 12 个才报错。
// 前端按服务端真实行为把上限设在 11，而不是错误提示里写的 10。
const GROUP_LIMIT = 11

const inRecycle = computed(() => route.name === 'recycle')
const atGroupLimit = computed(() => appStore.groups.length >= GROUP_LIMIT)

const titles = {
  links: '短链接管理',
  groups: '分组管理',
  stats: '数据统计',
  recycle: '回收站',
  user: '个人中心',
  'self-check': '接口自检'
}

const navs = [
  { name: 'links', label: '短链接' },
  { name: 'groups', label: '分组管理' },
  { name: 'stats', label: '数据统计' },
  { name: 'recycle', label: '回收站' },
  { name: 'user', label: '个人中心' }
]

onMounted(async () => {
  await reloadGroups()
  if (route.name === 'recycle') await loadDeletedGroups()
})

async function reloadGroups() {
  try {
    await loadGroups()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

async function selectGroup(gid) {
  appStore.currentGid = gid
  if (route.name !== 'links' && route.name !== 'recycle') {
    router.push({ name: 'links' })
  }
}

async function onCreateGroup() {
  const name = newGroupName.value.trim()
  if (!name) return ElMessage.warning('请输入分组名称')
  creating.value = true
  try {
    await addGroup(name)
    newGroupName.value = ''
    ElMessage.success('分组创建成功')
    await reloadGroups()
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    creating.value = false
  }
}

async function onDeleteGroup(gid) {
  try {
    await ElMessageBox.confirm(
      '确定删除该分组？空分组会被彻底删除，非空分组内的短链接将移入回收站。',
      '删除分组',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  try {
    await deleteGroup(gid)
    ElMessage.success('已删除')
    if (appStore.currentGid === gid) appStore.currentGid = ''
    await reloadGroups()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

async function onLogout() {
  const username = getUsername()
  const token = getToken()
  try {
    await logoutApi(username, token)
  } catch {
    // 服务端注销失败（比如 token 已过期）也照样清本地，不能把用户卡在登录态里
  }
  clearSession()
  router.replace({ name: 'login' })
}
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="brand">SaaS短链系统</div>

      <div class="sidebar-body">
        <div class="section-label">我的分组</div>
        <div v-if="!appStore.groups.length" class="sidebar-hint">暂无分组</div>
        <div
          v-for="g in appStore.groups"
          :key="g.gid"
          class="group-item"
          :class="{ active: g.gid === appStore.currentGid }"
          @click="selectGroup(g.gid)"
        >
          <span class="name">{{ g.name }}</span>
          <span class="del" title="删除分组" @click.stop="onDeleteGroup(g.gid)">&times;</span>
        </div>

        <template v-if="inRecycle && appStore.deletedGroups.length">
          <div class="section-label">已删除分组</div>
          <div
            v-for="g in appStore.deletedGroups"
            :key="g.gid"
            class="group-item deleted"
            :class="{ active: g.gid === appStore.currentGid }"
            @click="selectGroup(g.gid)"
          >
            <span class="name">{{ g.name }}</span>
          </div>
        </template>
      </div>

      <div class="sidebar-footer">
        <el-input
          v-model="newGroupName"
          placeholder="新建分组"
          maxlength="20"
          size="small"
          :disabled="atGroupLimit"
          @keyup.enter="onCreateGroup"
        />
        <el-button
          type="primary"
          size="small"
          :loading="creating"
          :disabled="atGroupLimit"
          @click="onCreateGroup"
        >
          新建
        </el-button>
      </div>
      <div v-if="atGroupLimit" class="limit-tip">分组数量已达服务端上限</div>
    </aside>

    <div class="main">
      <header class="topbar">
        <div class="breadcrumb">{{ titles[route.name] || '' }}</div>
        <nav class="nav">
          <router-link
            v-for="n in navs"
            :key="n.name"
            :to="{ name: n.name }"
            class="nav-link"
            :class="{ active: route.name === n.name }"
          >
            {{ n.label }}
          </router-link>
        </nav>
        <div class="actions">
          <span class="muted">{{ getUsername() }}</span>
          <el-button link type="danger" size="small" @click="onLogout">退出</el-button>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 250px;
  flex-shrink: 0;
  background: #fff;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.brand {
  padding: 18px 20px;
  font-size: 16px;
  font-weight: 700;
  color: #4f46e5;
  border-bottom: 1px solid #e2e8f0;
}

.sidebar-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.section-label {
  padding: 8px 20px 4px;
  font-size: 11px;
  letter-spacing: 0.5px;
  color: #94a3b8;
}

.sidebar-hint {
  padding: 12px 20px;
  font-size: 13px;
  color: #94a3b8;
}

.group-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 9px 20px;
  font-size: 14px;
  cursor: pointer;
  border-left: 3px solid transparent;
}

.group-item:hover {
  background: #f8fafc;
}

.group-item.active {
  background: #eef2ff;
  border-left-color: #4f46e5;
  color: #4f46e5;
  font-weight: 500;
}

.group-item.deleted,
.group-item.deleted.active {
  color: #94a3b8;
}

.group-item .name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.group-item .del {
  visibility: hidden;
  color: #cbd5e1;
  font-size: 16px;
  line-height: 1;
  padding: 0 4px;
}

.group-item:hover .del {
  visibility: visible;
}

.group-item .del:hover {
  color: #ef4444;
}

.sidebar-footer {
  display: flex;
  gap: 6px;
  padding: 12px 16px;
  border-top: 1px solid #e2e8f0;
}

.limit-tip {
  padding: 0 16px 10px;
  font-size: 12px;
  color: #f59e0b;
}

.main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.topbar {
  height: 56px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
}

.breadcrumb {
  font-size: 14px;
  font-weight: 500;
}

.nav {
  flex: 1;
  display: flex;
  gap: 4px;
}

.nav-link {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #64748b;
  text-decoration: none;
}

.nav-link:hover {
  background: #f1f5f9;
}

.nav-link.active {
  background: #4f46e5;
  color: #fff;
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.content {
  flex: 1;
  overflow-y: auto;
}
</style>
