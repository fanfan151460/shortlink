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
 */
export function pageLink({ gid, current = 1, size = 10, orderFlag }) {
  return request.get('/page', { params: { gid, current, size, orderFlag } })
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
