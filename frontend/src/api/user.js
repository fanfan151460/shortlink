import request from './request'

/** 查询用户信息 GET /user/{username} */
export function getUser(username) {
  return request.get(`/user/${encodeURIComponent(username)}`)
}

/** 健康检查（网关白名单内） GET /test */
export function test() {
  return request.get('/test')
}

/** 用户名是否可用 GET /user/has-username —— 返回 true 表示可用 */
export function hasUsername(username) {
  return request.get('/user/has-username', { params: { username } })
}

/** 注册 POST /user —— 网关白名单内，不带鉴权头 */
export function register(data) {
  return request.post('/user', data)
}

/** 修改个人信息 PUT /user —— 注意必须带鉴权头，白名单只放行 POST */
export function updateUser(data) {
  return request.put('/user', data)
}

/** 登录 POST /user/login —— 返回 { token } */
export function login(data) {
  return request.post('/user/login', data)
}

/** 校验登录态 GET /user/check-login */
export function checkLogin(username, token) {
  return request.get('/user/check-login', { params: { username, token } })
}

/** 退出登录 DELETE /user/logout */
export function logout(username, token) {
  return request.delete('/user/logout', { params: { username, token } })
}
