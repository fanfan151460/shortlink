import request from './request'

/** 抓取网页标题 GET /title */
export function getTitle(url) {
  return request.get('/title', { params: { url } })
}

/** 创建短链接 POST /create —— 后端带 @NoDuplicateSubmit，相同参数 3 秒内重复提交会被拒 */
export function createLink(data) {
  return request.post('/create', data)
}

/**
 * 分页查询短链接 GET /page
 *
 * orderFlag 只认 totalPv / totalUv / totalUip 三个值，传其它值不会报错，
 * 而是静默退化成 ORDER BY create_time DESC，所以别自己拼别的字符串。
 *
 * activityId 传值时只返回该活动下的渠道短链；不传则只返回"普通短链"
 * （后端是二分逻辑：activity_id IS NULL，不是在全部结果里筛掉活动短链）。
 */
export function pageLink({ gid, current = 1, size = 10, orderFlag, activityId }) {
  return request.get('/page', { params: { gid, current, size, orderFlag, activityId } })
}

/** 更新短链接 PUT /update */
export function updateLink(data) {
  return request.put('/update', data)
}

/**
 * 删除短链接（移入回收站） DELETE /remove
 *
 * 相比 POST /recycle-bin/save，这个还会顺手删掉 Redis 里的短链缓存，
 * 所以列表页的删除按钮用它——否则删完短链还能被缓存命中跳转。
 */
export function removeLink(fullShortUrl, gid) {
  return request.delete('/remove', { data: { fullShortUrl, gid } })
}

/**
 * 批量停用短链接 POST /batch-disable
 *
 * 入参两种二选一（同时传以 activityId 为准），两种都必须带 gid（分片键，不带会广播 16 张分片表）：
 *   { gid, fullShortUrls: [...] }  列表页勾选/单条操作
 *   { gid, activityId }            活动页"全部停用"——渠道列表是分页的，前端只有当前页，传列表会漏
 *
 * 返回值是实际改动的条数。全部已是目标状态时返回 0 且不报错（幂等空操作）。
 * 范围为空时：按 fullShortUrls 传会报"没有可操作的短链接"（选中的行已被删/串了 token）；
 * 按 activityId 传则返回 0（活动可能还没建渠道）。
 */
export function batchDisableLink(data) {
  return request.post('/batch-disable', data)
}

/** 批量启用短链接 POST /batch-enable，入参同上 */
export function batchEnableLink(data) {
  return request.post('/batch-enable', data)
}
