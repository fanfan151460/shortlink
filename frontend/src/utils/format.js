/** LocalDateTime 字符串 → "YYYY-MM-DD" */
export function fmtDate(value) {
  if (!value) return '-'
  const s = String(value)
  return s.split('T')[0] || s.split(' ')[0] || s
}

/** LocalDateTime 字符串 → "YYYY-MM-DD HH:mm" */
export function fmtDateTime(value) {
  if (!value) return '-'
  return String(value).substring(0, 16).replace('T', ' ')
}

/** 取站点根 + /favicon.ico；非法 URL 返回空串，img 的 onerror 会兜住 */
export function faviconUrl(url) {
  try {
    return new URL(url).origin + '/favicon.ico'
  } catch {
    return ''
  }
}

/** YYYY-MM-DD，n 天前（含今天共 n 天） */
export function daysAgo(n) {
  const d = new Date()
  d.setDate(d.getDate() - n + 1)
  return toISODate(d)
}

export function today() {
  return toISODate(new Date())
}

/**
 * 链接是否已过期：只有自定义有效期（validDateType=1）且到期日早于今天才算，当日仍有效。
 * 后端给的是 "YYYY-MM-DD"，字符串比大小就是日期比大小，不用建 Date。
 */
export function isExpired(link) {
  if (!link || link.validDateType !== 1 || !link.validDate) return false
  return String(link.validDate).slice(0, 10) < today()
}

function toISODate(d) {
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}
