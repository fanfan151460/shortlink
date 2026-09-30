import request from './request'

/**
 * 一次性拿到 5 个维度 + 访问趋势 POST /stats/dashboard
 *
 * 返回 { locale, os, browser, device, network, access }，
 * 每个维度是 [{ name, value }]，access 是 [{ date, pv, uv, uip }]。
 */
export function getDashboard(data) {
  return request.post('/stats/dashboard', data)
}

/** 地区分布 POST /stats/locale —— name 是省份全称，如"广东省""北京市" */
export function getLocaleStats(data) {
  return request.post('/stats/locale', data)
}

/** 操作系统分布 POST /stats/os */
export function getOsStats(data) {
  return request.post('/stats/os', data)
}

/** 浏览器分布 POST /stats/browser */
export function getBrowserStats(data) {
  return request.post('/stats/browser', data)
}

/** 设备类型分布 POST /stats/device */
export function getDeviceStats(data) {
  return request.post('/stats/device', data)
}

/** 网络类型分布 POST /stats/network */
export function getNetworkStats(data) {
  return request.post('/stats/network', data)
}

/** 访问趋势（PV/UV/UIP 按天） POST /stats/access */
export function getAccessStats(data) {
  return request.post('/stats/access', data)
}

/** 访问明细记录 POST /stats/access-record */
export function getAccessRecords(data) {
  return request.post('/stats/access-record', data)
}
