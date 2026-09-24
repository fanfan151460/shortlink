import { createRouter, createWebHistory } from 'vue-router'

import AppLayout from '@/components/AppLayout.vue'
import { hasSession } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: AppLayout,
    children: [
      { path: '', redirect: '/links' },
      { path: 'links', name: 'links', component: () => import('@/views/LinkList.vue') },
      { path: 'groups', name: 'groups', component: () => import('@/views/GroupManage.vue') },
      { path: 'stats', name: 'stats', component: () => import('@/views/Stats.vue') },
      { path: 'recycle', name: 'recycle', component: () => import('@/views/RecycleBin.vue') },
      { path: 'user', name: 'user', component: () => import('@/views/UserCenter.vue') },
      // 验收工具，不属于业务功能，所以不放进侧边栏导航
      { path: 'self-check', name: 'self-check', component: () => import('@/views/SelfCheck.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/links' }
]

const router = createRouter({
  // history 模式：生产环境需要 nginx 配 try_files 兜底，否则刷新深链接会 404
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.public) return true
  // 只判断本地有没有 token，不做网络校验：token 可能已经失效（闲置 30 分钟或在别处登录），
  // 那种情况交给第一个真实请求的 401 拦截器去处理，避免每次路由跳转都多打一次接口
  if (!hasSession()) return { name: 'login', query: { redirect: to.fullPath } }
  return true
})

export default router
