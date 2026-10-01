<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { checkLogin, hasUsername, login, register } from '@/api/user'
import { getToken, getUsername, saveSession } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const tab = ref('login')
const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', password: '', realName: '', phone: '', mail: '' })

const loading = ref(false)
const registering = ref(false)
const nameHint = ref({ text: '', type: '' })

let checkTimer = null

onMounted(async () => {
  // 登录态失效的提示由 request.js 的 401 拦截器统一给，这里不再重复弹一条
  // 带着旧 token 直接打开 /login 时，先跟服务端确认一下还有效没有。
  // check-login 在网关上，前端这里不能只信 localStorage。
  const token = getToken()
  const username = getUsername()
  if (!token || !username) return
  try {
    const valid = await checkLogin(username, token)
    if (valid === true) router.replace('/links')
  } catch {
    /* 无效就当没登录，停下来让用户重新输 */
  }
})

async function onLogin() {
  if (!loginForm.username || !loginForm.password) {
    return ElMessage.warning('请输入用户名和密码')
  }
  loading.value = true
  try {
    const data = await login({ username: loginForm.username, password: loginForm.password })
    saveSession(data.token, loginForm.username)
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/links')
  } catch {
    /* 拦截器已经提示过了（业务错误是 HTTP 200 + code != "0"） */
  } finally {
    loading.value = false
  }
}

function onUsernameInput() {
  clearTimeout(checkTimer)
  const name = regForm.username.trim()
  if (!name) {
    nameHint.value = { text: '', type: '' }
    return
  }
  if (name.length < 3) {
    nameHint.value = { text: '用户名至少 3 位', type: 'err' }
    return
  }
  // 每次按键都打接口太浪费，停 400ms 再查
  checkTimer = setTimeout(async () => {
    try {
      const available = await hasUsername(name)
      nameHint.value = available
        ? { text: '用户名可用', type: 'ok' }
        : { text: '用户名已被占用', type: 'err' }
    } catch {
      nameHint.value = { text: '检测失败', type: 'err' }
    }
  }, 400)
}

async function onRegister() {
  if (regForm.password.length < 6) return ElMessage.warning('密码至少 6 位')
  registering.value = true
  try {
    await register({ ...regForm })
    ElMessage.success('注册成功，请登录')
    loginForm.username = regForm.username
    loginForm.password = ''
    regForm.username = ''
    regForm.password = ''
    regForm.realName = ''
    regForm.phone = ''
    regForm.mail = ''
    nameHint.value = { text: '', type: '' }
    tab.value = 'login'
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    registering.value = false
  }
}
</script>

<template>
  <div class="auth-wrap">
    <el-card class="auth-card" shadow="always">
      <div class="brand">
        <span class="brand-mark">
          <svg
            viewBox="0 0 24 24"
            width="24"
            height="24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.1"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
            <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
          </svg>
        </span>
        <h1 class="title">SaaS短链系统</h1>
        <p class="subtitle">短链生成 · 访问统计 · 营销活动</p>
      </div>

      <el-tabs v-model="tab" stretch>
        <el-tab-pane label="登录" name="login">
          <el-form label-position="top" @submit.prevent="onLogin">
            <el-form-item label="用户名">
              <el-input
                v-model="loginForm.username"
                placeholder="输入用户名"
                @keyup.enter="onLogin"
              />
            </el-form-item>
            <el-form-item label="密码">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="输入密码"
                show-password
                @keyup.enter="onLogin"
              />
            </el-form-item>
            <el-button type="primary" class="submit" :loading="loading" @click="onLogin">
              登录
            </el-button>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form label-position="top" @submit.prevent="onRegister">
            <el-form-item label="用户名">
              <el-input
                v-model="regForm.username"
                placeholder="输入用户名"
                autocomplete="off"
                @input="onUsernameInput"
              />
              <div v-if="nameHint.text" class="hint" :class="nameHint.type">
                {{ nameHint.text }}
              </div>
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="regForm.password" type="password" placeholder="至少 6 位" />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="regForm.realName" placeholder="可选" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="regForm.phone" placeholder="可选" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="regForm.mail" placeholder="可选" />
            </el-form-item>
            <el-button
              type="primary"
              class="submit"
              :loading="registering"
              @click="onRegister"
            >
              注册
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
/* 登录页是"门面"，比内页多铺一层更明显的渐变，卡片浮在上面 */
.auth-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: radial-gradient(900px 500px at 15% 0%, #e0e7ff 0%, transparent 55%),
    radial-gradient(800px 500px at 90% 100%, #cffafe 0%, transparent 55%), #f6f8fc;
}

.auth-card {
  width: 400px;
}

.brand {
  text-align: center;
  margin-bottom: 6px;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 14px;
  color: #fff;
  background: linear-gradient(135deg, var(--brand) 0%, #7c6ef0 100%);
  box-shadow: 0 6px 18px rgba(79, 70, 229, 0.32);
}

.title {
  margin: 12px 0 4px;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.3px;
  color: var(--ink-900);
}

.subtitle {
  margin: 0 0 8px;
  font-size: 12.5px;
  color: var(--ink-400);
}

.submit {
  width: 100%;
}

.hint {
  margin-top: 4px;
  font-size: 12px;
}

.hint.ok {
  color: #22c55e;
}

.hint.err {
  color: #ef4444;
}
</style>
