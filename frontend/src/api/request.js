import axios from 'axios'
import { ElMessage } from 'element-plus'

import { clearSession, getToken, getUsername } from '@/utils/auth'

// 开发默认走 vite 的 server.proxy（浏览器端零跨域）；
// VITE_API_MODE=direct 时直连网关，可以看到真实的 OPTIONS 预检请求。
// 生产构建两者都不选，走相对路径 /api/...，由 nginx 反向代理转发到网关。
const GATEWAY = 'http://8.149.237.177:8083'
const BASE_URL =
  import.meta.env.VITE_API_MODE === 'direct'
    ? `${GATEWAY}/api/short-link/admin/v1`
    : '/api/short-link/admin/v1'

// 401 处理由 main.js 注入，避免 request -> router -> views -> request 的循环依赖
let onUnauthorized = null
export function setUnauthorizedHandler(fn) {
  onUnauthorized = fn
}

/**
 * 网关白名单：这些请求不带 username / token。
 *
 * 必须区分 HTTP 方法。网关的判断是
 *   path == "/api/short-link/admin/v1/user" && method == POST   → 注册，放行
 * 所以 POST /user 不带鉴权头，而 PUT /user（改个人信息）必须带。
 * 只按路径做白名单会把 PUT /user 也漏掉，直接 401。
 */
function isWhitelisted(config) {
  const url = (config.url || '').replace(/\/+$/, '')
  const method = (config.method || 'get').toLowerCase()
  if (method === 'post' && url === '/user/login') return true
  if (method === 'post' && url === '/user') return true
  if (method === 'get' && url === '/user/has-username') return true
  if (method === 'get' && url === '/test') return true
  return false
}

const request = axios.create({ baseURL: BASE_URL, timeout: 20000 })

request.interceptors.request.use((config) => {
  config.headers['Content-Type'] = 'application/json'
  if (!isWhitelisted(config)) {
    const token = getToken()
    const username = getUsername()
    if (token) config.headers.token = token
    if (username) config.headers.username = username
  }
  return config
})

// 防止并发请求同时 401 时弹出一堆提示
let handling401 = false

request.interceptors.response.use(
  // HTTP 2xx：业务错误藏在这里。
  // GlobalExceptionHandler 把业务异常也返回成 HTTP 200，靠 body 的 code 区分，
  // 而 Result.SUCCESS_CODE 是字符串 "0"（不是数字 0）。
  (response) => {
    const result = response.data
    // 自检页要看到完整的 Result（code / message / requestId），用 raw 跳过解包
    if (response.config.raw === true) return result

    if (result && result.code === '0') return result.data

    const msg = (result && result.message) || '请求失败'
    if (!response.config.silent) ElMessage.error(msg)
    return Promise.reject(new Error(msg))
  },

  // HTTP 非 2xx。
  // 网关鉴权失败返回 401 + 空 body（response.setComplete()），不是 Result 对象，
  // 所以这里绝对不能读 response.data.code，否则 TypeError 会盖掉真正的 401 语义。
  (error) => {
    const status = error.response && error.response.status
    // 自检页会成批地打接口，失败是它预期内的结果，不要弹一堆提示、也不要被踢走
    const silent = Boolean(error.config && error.config.silent)

    if (status === 401) {
      if (silent) return Promise.reject(error)
      clearSession()
      if (!handling401) {
        handling401 = true
        // 两种原因都会走到这：token 闲置超过 30 分钟，或账号在别处登录把当前 token
        // 挤掉了（登录时只保留最近 3 个 token，第 4 次登录会清空之前的记录）
        ElMessage.error('登录已过期，或账号在别处登录，请重新登录')
        if (onUnauthorized) onUnauthorized()
        setTimeout(() => {
          handling401 = false
        }, 1000)
      }
      return Promise.reject(error)
    }

    if (!silent) {
      let msg
      if (error.code === 'ECONNABORTED') msg = '请求超时'
      else if (status) msg = `服务返回 ${status}`
      else msg = '网络异常，检查后端是否启动'
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request
export { BASE_URL }
