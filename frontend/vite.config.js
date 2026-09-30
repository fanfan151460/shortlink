import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

// 网关地址。开发时通过 dev server 代理转发，生产时由 nginx 反向代理转发，
// 两者都保持 /api 前缀不变——网关的 Path 断言是 /api/short-link/admin/**。
const GATEWAY = 'http://8.149.237.177:8083'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  // VITE_API_MODE=direct 时前端直连网关，用于观察真实的跨域预检（OPTIONS）。
  // 默认走代理，浏览器端零跨域。
  const direct = env.VITE_API_MODE === 'direct'

  return {
    plugins: [vue()],
    resolve: {
      alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) }
    },
    server: {
      port: 5173,
      // direct 模式下不需要代理，但保留也无妨：请求已经是绝对地址，不会命中 /api
      proxy: direct
        ? {}
        : {
            '/api': {
              target: GATEWAY,
              changeOrigin: true
              // 不写 rewrite：网关的 Path 断言带 /api 前缀，剥掉就会 404
            }
          }
    },
    build: {
      outDir: 'dist',
      rollupOptions: {
        output: {
          // echarts 和 element-plus 都很大而且是"谁都用得到"的公共依赖，
          // 单独切出来浏览器才能长期缓存，改业务代码不会让它们重新下载
          manualChunks: {
            echarts: ['echarts'],
            element: ['element-plus'],
            vendor: ['vue', 'vue-router', 'axios']
          }
        }
      }
    }
  }
})
