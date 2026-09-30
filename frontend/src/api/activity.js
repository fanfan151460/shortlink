import request from './request'

/**
 * 创建营销活动 POST /activity
 *
 * 一个活动 = 一个目标链接 + N 个渠道短链；渠道短链通过 addActivityLinks 批量生成。
 * 后端会校验 gid 归属和 originUrl 域名黑名单。
 */
export function addActivity(data) {
  return request.post('/activity', data)
}

/**
 * 更新活动 PUT /activity
 *
 * 可改：名称、目标链接、状态（gid 不可改——它既是分片键又是权限维度）。
 * 注意改目标链接只影响之后新建的渠道短链：已有渠道短链在创建时就复制走了当时的目标链接。
 * 三个字段都传空会被后端拒绝（"没有需要更新的字段"）。
 */
export function updateActivity(data) {
  return request.put('/activity', data)
}

/**
 * 删除活动 DELETE /activity?id=
 *
 * 逻辑删除，**不会**连带删除渠道短链：那些链接仍能正常跳转（可能已经印在物料上了），
 * 并且会回落到"短链接"列表里继续可管理。
 */
export function removeActivity(id) {
  return request.delete('/activity', { params: { id } })
}

/**
 * 分页查询活动 GET /activity/page
 *
 * 后端固定按当前登录用户过滤，三个筛选条件（gid / status / activityName）都是可选的。
 * 注意接口不返回总数，只能靠"这一页取满了"推断还有下一页。
 */
export function pageActivity({ current = 1, size = 10, gid, status, activityName } = {}) {
  return request.get('/activity/page', { params: { current, size, gid, status, activityName } })
}

/**
 * 批量创建渠道短链 POST /activity/links
 *
 * 后端带 @NoDuplicateSubmit，相同参数 3 秒内重复提交会被拒。
 * 渠道名会被后端 trim / 去空 / 去重，且同一活动下渠道名唯一（uk_activity_channel）。
 */
export function addActivityLinks(data) {
  return request.post('/activity/links', data)
}
