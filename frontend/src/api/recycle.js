import request from './request'

/**
 * 单条移入回收站 POST /recycle-bin/save
 *
 * 和 DELETE /remove 作用几乎重复，区别是这里**不删除 Redis 里的短链缓存**，
 * 所以列表页的"删除"按钮走的是 DELETE /remove。这个接口留给自检页做完整性覆盖。
 */
export function saveRecycleBin(fullShortUrl, gid) {
  return request.post('/recycle-bin/save', { fullShortUrl, gid })
}

/** 整个分组移入回收站 GET /recycle-bin/save-all/{gid} —— 返回是否有短链被移入 */
export function saveRecycleBinAll(gid) {
  return request.get(`/recycle-bin/save-all/${encodeURIComponent(gid)}`)
}

/** 回收站分页 GET /recycle-bin/page */
export function pageRecycle({ gid, current = 1, size = 10 }) {
  return request.get('/recycle-bin/page', { params: { gid, current, size } })
}

/** 从回收站恢复 POST /recycle-bin/recover */
export function recoverRecycleBin(fullShortUrl, gid) {
  return request.post('/recycle-bin/recover', { fullShortUrl, gid })
}

/** 永久删除 DELETE /recycle-bin/delete */
export function deleteRecycleBin(fullShortUrl, gid) {
  return request.delete('/recycle-bin/delete', { data: { fullShortUrl, gid } })
}
