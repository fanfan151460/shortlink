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

function toISODate(d) {
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}
