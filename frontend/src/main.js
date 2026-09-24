import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import { setUnauthorizedHandler } from './api/request'
import './styles/main.css'

const app = createApp(App)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

// 401 时统一踢回登录页。在 main.js 里注入，避免 request.js 直接依赖 router
// （router → views → api → request → router 会成环）
setUnauthorizedHandler(() => {
  if (router.currentRoute.value.name !== 'login') {
    router.replace({ name: 'login', query: { reason: 'expired' } })
  }
})

app.mount('#app')
