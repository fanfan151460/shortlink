import request from './request'

/** 新建分组 POST /group —— 后端限制最多 10 个（count > 10 才抛异常） */
export function addGroup(name) {
  return request.post('/group', { name })
}

/** 查询未删除的分组 GET /group —— 后端按 sortOrder ASC, updateTime DESC 排序 */
export function listGroup() {
  return request.get('/group')
}

/** 查询全部（含已删除）分组 GET /group/deleted */
export function listAllGroup() {
  return request.get('/group/deleted')
}

/** 重命名分组 PUT /group */
export function updateGroup(gid, name) {
  return request.put('/group', { gid, name })
}

/** 删除分组 DELETE /group —— 空分组物理删除，非空分组内的短链进回收站 */
export function deleteGroup(gid) {
  return request.delete('/group', { params: { gid } })
}

/**
 * 分组排序 POST /group/sort
 *
 * 后端按传入数组整体重排，字段名是 groupId（不是 gid）。
 * 因为读接口不返回 sortOrder，前端只能把全部分组按新顺序从 0 重新编号后整体提交。
 */
export function sortGroup(orders) {
  return request.post('/group/sort', orders)
}
