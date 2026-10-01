<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  ArrowUp,
  Close,
  Delete,
  Edit,
  Finished,
  Link,
  Promotion,
  TrendCharts,
  User
} from '@element-plus/icons-vue'

import { addGroup, deleteGroup, sortGroup, updateGroup } from '@/api/group'
import { logout as logoutApi } from '@/api/user'
import { appStore, loadDeletedGroups, loadGroups } from '@/store/app'
import { clearSession, getToken, getUsername } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const newGroupName = ref('')
const creating = ref(false)

// 侧栏里就地重命名：editingGid 非空时那一行换成输入框
const editingGid = ref('')
const editingName = ref('')

// 后端 saveGroup 是 `if (count > 10) throw`，即已有 11 个时再建第 12 个才报错。
// 前端按服务端真实行为把上限设在 11，而不是错误提示里写的 10。
const GROUP_LIMIT = 11

const inRecycle = computed(() => route.name === 'recycle')
const atGroupLimit = computed(() => appStore.groups.length >= GROUP_LIMIT)

const titles = {
  links: '短链接管理',
  activity: '营销活动',
  stats: '数据统计',
  recycle: '回收站',
  user: '个人中心',
  'self-check': '接口自检'
}

// 分组管理不再是独立页面，相关的增删改排序都收在左侧「我的分组」里
const navs = [
  { name: 'links', label: '短链接', icon: Link },
  { name: 'activity', label: '营销活动', icon: Promotion },
  { name: 'stats', label: '数据统计', icon: TrendCharts },
  { name: 'recycle', label: '回收站', icon: Delete },
  { name: 'user', label: '个人中心', icon: User }
]

const pageIcons = {
  links: Link,
  activity: Promotion,
  stats: TrendCharts,
  recycle: Delete,
  user: User,
  'self-check': Finished
}

const crumbIcon = computed(() => pageIcons[route.name] || Link)
const avatarLetter = computed(() => (getUsername() || '?').charAt(0).toUpperCase())

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
  // 点别的分组时顺手收掉正在编辑的输入框，免得它一直挂在那儿
  if (editingGid.value) cancelRename()
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

function startRename(g) {
  editingGid.value = g.gid
  editingName.value = g.name
}

function cancelRename() {
  editingGid.value = ''
  editingName.value = ''
}

async function submitRename(g) {
  const name = editingName.value.trim()
  if (!name) return ElMessage.warning('分组名称不能为空')
  if (name === g.name) return cancelRename()
  try {
    await updateGroup(g.gid, name)
    ElMessage.success('已重命名')
    cancelRename()
    await reloadGroups()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

/**
 * 上移 / 下移。
 *
 * GET /group 只返回 gid / name / delFlag，不返回 sortOrder，所以没法做"只改这两个的序号"
 * 的增量更新，只能把整个列表按新顺序从 0 重新编号后整体提交。接口字段名是 groupId 不是 gid。
 */
async function move(index, delta) {
  const target = index + delta
  const list = appStore.groups
  if (target < 0 || target >= list.length) return
  const reordered = list.slice()
  ;[reordered[index], reordered[target]] = [reordered[target], reordered[index]]

  // 先本地生效，避免等接口回来才看到位移
  appStore.groups = reordered
  try {
    const orders = reordered.map((g, i) => ({ groupId: g.gid, sortOrder: i }))
    appStore.groups = (await sortGroup(orders)) || reordered
  } catch {
    await reloadGroups()
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
      <!-- 内联 SVG 而不是图片：不占额外请求、不落图片文件、能跟着 currentColor 变色 -->
      <div class="brand">
        <span class="logo">
          <svg
            viewBox="0 0 24 24"
            width="17"
            height="17"
            fill="none"
            stroke="currentColor"
            stroke-width="2.1"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
            <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
          </svg>
        </span>
        <span class="brand-text">SaaS短链系统</span>
      </div>

      <div class="sidebar-body">
        <div class="section-label">我的分组</div>
        <div v-if="!appStore.groups.length" class="sidebar-hint">暂无分组</div>
        <!-- 分组管理收在这一行里：悬停出 ↑ ↓ 改名 ×，不再单开一个页面 -->
        <div
          v-for="(g, index) in appStore.groups"
          :key="g.gid"
          class="group-item"
          :class="{ active: g.gid === appStore.currentGid }"
          @click="selectGroup(g.gid)"
        >
          <el-input
            v-if="editingGid === g.gid"
            v-model="editingName"
            class="rename-input"
            size="small"
            maxlength="20"
            autofocus
            @click.stop
            @keyup.enter="submitRename(g)"
            @keyup.esc="cancelRename"
          />
          <template v-else>
            <span class="name">{{ g.name }}</span>
            <span class="ops" @click.stop>
              <span
                class="op"
                :class="{ disabled: index === 0 }"
                title="上移"
                @click="move(index, -1)"
              >
                <el-icon :size="13"><ArrowUp /></el-icon>
              </span>
              <span
                class="op"
                :class="{ disabled: index === appStore.groups.length - 1 }"
                title="下移"
                @click="move(index, 1)"
              >
                <el-icon :size="13"><ArrowDown /></el-icon>
              </span>
              <span class="op" title="重命名" @click="startRename(g)">
                <el-icon :size="13"><Edit /></el-icon>
              </span>
              <span class="op del" title="删除分组" @click="onDeleteGroup(g.gid)">
                <el-icon :size="13"><Close /></el-icon>
              </span>
            </span>
          </template>
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
        <div class="crumb">
          <el-icon class="crumb-icon" :size="15"><component :is="crumbIcon" /></el-icon>
          <span class="crumb-text">{{ titles[route.name] || '' }}</span>
        </div>
        <nav class="nav">
          <router-link
            v-for="n in navs"
            :key="n.name"
            :to="{ name: n.name }"
            class="nav-link"
            :class="{ active: route.name === n.name }"
          >
            <el-icon :size="15"><component :is="n.icon" /></el-icon>
            <span>{{ n.label }}</span>
          </router-link>
        </nav>
        <div class="actions">
          <span class="user-chip">
            <span class="avatar">{{ avatarLetter }}</span>
            <span class="uname">{{ getUsername() }}</span>
          </span>
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
  border-right: 1px solid var(--line);
  display: flex;
  flex-direction: column;
}

/* 品牌区用渐变方块 + 文字，和内页的 .title-badge 是同一套视觉语言 */
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 20px;
  border-bottom: 1px solid var(--line);
}

.brand .logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  border-radius: 9px;
  color: #fff;
  background: linear-gradient(135deg, var(--brand) 0%, #7c6ef0 100%);
  box-shadow: 0 2px 8px rgba(79, 70, 229, 0.28);
}

.brand-text {
  font-size: 15.5px;
  font-weight: 700;
  letter-spacing: -0.2px;
  color: var(--ink-900);
}

.sidebar-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.section-label {
  padding: 10px 20px 4px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.8px;
  color: var(--ink-400);
}

.sidebar-hint {
  padding: 12px 20px;
  font-size: 13px;
  color: var(--ink-400);
}

.group-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 1px 0;
  padding: 9px 20px;
  font-size: 14px;
  color: var(--ink-700);
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: background 0.15s, color 0.15s;
}

.group-item:hover {
  background: #f8fafc;
}

.group-item.active {
  background: linear-gradient(90deg, var(--brand-50) 0%, rgba(238, 242, 255, 0.35) 100%);
  border-left-color: var(--brand);
  color: var(--brand);
  font-weight: 500;
}

.group-item.deleted,
.group-item.deleted.active {
  color: var(--ink-400);
  background: none;
  border-left-color: transparent;
  font-weight: 400;
}

.group-item .name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 用 visibility 而不是 display 切换：分组名不会因为悬停导致的宽度变化而左右跳 */
.group-item .ops {
  visibility: hidden;
  display: flex;
  align-items: center;
  gap: 1px;
  flex-shrink: 0;
}

.group-item:hover .ops {
  visibility: visible;
}

.group-item .op {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--ink-400);
  padding: 3px;
  border-radius: 4px;
  cursor: pointer;
}

.group-item .op:hover {
  background: var(--line);
  color: var(--brand);
}

.group-item .op.disabled,
.group-item .op.disabled:hover {
  color: var(--line);
  background: transparent;
  cursor: default;
}

.group-item .del:hover {
  color: #ef4444;
}

.rename-input {
  flex: 1;
  min-width: 0;
}

.sidebar-footer {
  display: flex;
  gap: 6px;
  padding: 12px 16px;
  border-top: 1px solid var(--line);
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
  height: 58px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid var(--line);
  box-shadow: var(--shadow-xs);
}

/* 当前页标题：图标 + 文字，图标跟着主题色走 */
.crumb {
  display: flex;
  align-items: center;
  gap: 7px;
  flex-shrink: 0;
}

.crumb .crumb-icon {
  color: var(--brand);
}

.crumb-text {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: -0.2px;
  color: var(--ink-900);
  white-space: nowrap;
}

.nav {
  flex: 1;
  display: flex;
  gap: 4px;
  min-width: 0;
  overflow-x: auto;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 12px;
  border-radius: 8px;
  font-size: 13px;
  color: var(--ink-500);
  text-decoration: none;
  white-space: nowrap;
  transition: background 0.15s, color 0.15s;
}

.nav-link:hover {
  background: var(--brand-50);
  color: var(--brand);
}

.nav-link.active {
  background: linear-gradient(135deg, var(--brand) 0%, #6d63ee 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(79, 70, 229, 0.24);
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

/* 用户名做成胶囊：首字母圆形头像 + 名字，比裸文字更像"登录了" */
.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 3px 10px 3px 3px;
  border-radius: 999px;
  background: #f1f5f9;
}

.user-chip .avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, var(--brand) 0%, #7c6ef0 100%);
}

.user-chip .uname {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: var(--ink-700);
}

.content {
  flex: 1;
  overflow-y: auto;
}
</style>
