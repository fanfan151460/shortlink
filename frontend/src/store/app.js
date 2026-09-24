import { reactive } from 'vue'

import { listAllGroup, listGroup } from '@/api/group'

// 全局共享的少量状态。只放"多个页面都要读"的东西，其它一律留在组件里。
// 用不着 pinia：这里就是一个 reactive 对象。
export const appStore = reactive({
  groups: [],
  deletedGroups: [],
  currentGid: ''
})

/** 拉取未删除分组。当前选中项被删掉时自动退回第一个。 */
export async function loadGroups() {
  appStore.groups = (await listGroup()) || []
  if (!appStore.groups.some((g) => g.gid === appStore.currentGid)) {
    appStore.currentGid = appStore.groups[0] ? appStore.groups[0].gid : ''
  }
  return appStore.groups
}

/** 拉取已删除分组（回收站视图下才显示） */
export async function loadDeletedGroups() {
  appStore.deletedGroups = (await listAllGroup()) || []
  return appStore.deletedGroups
}

export function groupName(gid) {
  const hit =
    appStore.groups.find((g) => g.gid === gid) ||
    appStore.deletedGroups.find((g) => g.gid === gid)
  return hit ? hit.name : ''
}
