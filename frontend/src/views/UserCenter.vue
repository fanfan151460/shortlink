<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'

import { getUser, updateUser } from '@/api/user'
import { getUsername } from '@/utils/auth'

const loading = ref(false)
const saving = ref(false)
const original = ref({})

const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  mail: ''
})

onMounted(load)

async function load() {
  form.username = getUsername()
  if (!form.username) return
  loading.value = true
  try {
    const user = (await getUser(form.username)) || {}
    original.value = {
      realName: user.realName ?? '',
      phone: user.phone ?? '',
      mail: user.mail ?? ''
    }
    form.realName = original.value.realName
    form.phone = original.value.phone
    form.mail = original.value.mail
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    loading.value = false
  }
}

async function onSave() {
  // PUT /user 只做非空字段更新（MyBatis-Plus 的 update 只拼非 null 字段），
  // 所以这里可以安全地"只发改动过的字段"：
  //  · 密码留空 = 不改密码（接口不返回原密码，前端的空值不能当成"清空密码"发上去）
  //  · 其它字段改了才发，清空成空字符串也会被写进去
  const body = { username: form.username }
  if (form.password) body.password = form.password
  if (form.realName !== original.value.realName) body.realName = form.realName
  if (form.phone !== original.value.phone) body.phone = form.phone
  if (form.mail !== original.value.mail) body.mail = form.mail

  if (Object.keys(body).length === 1) {
    return ElMessage.info('没有需要保存的修改')
  }

  saving.value = true
  try {
    await updateUser(body)
    ElMessage.success('已保存')
    form.password = ''
    await load()
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    saving.value = false
  }
}

function onReset() {
  form.password = ''
  form.realName = original.value.realName
  form.phone = original.value.phone
  form.mail = original.value.mail
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <div class="page-title">
        <span class="title-badge"><el-icon :size="17"><User /></el-icon></span>
        <div>
          <h2>个人中心</h2>
          <p class="page-sub">账号资料与密码，留空的字段不会被修改</p>
        </div>
      </div>
    </div>

    <el-card v-loading="loading" shadow="never" class="card">
      <div class="profile">
        <span class="profile-avatar">{{ (form.username || '?').charAt(0).toUpperCase() }}</span>
        <div class="profile-meta">
          <div class="profile-name">{{ form.realName || form.username || '-' }}</div>
          <div class="profile-sub">登录名：{{ form.username || '-' }}</div>
        </div>
      </div>

      <el-form label-width="90px" style="max-width: 460px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="留空表示不修改"
          />
        </el-form-item>

        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" placeholder="可选" />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="可选" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.mail" placeholder="可选" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.card {
  max-width: 560px;
}

/* 表单上方一条简短的账号概览，让这张卡片不只是一排输入框 */
.profile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 16px;
  margin-bottom: 18px;
  border-bottom: 1px solid var(--line-soft);
}

.profile-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  flex-shrink: 0;
  border-radius: 50%;
  font-size: 19px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, var(--brand) 0%, #7c6ef0 100%);
  box-shadow: 0 3px 10px rgba(79, 70, 229, 0.28);
}

.profile-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--ink-900);
}

.profile-sub {
  margin-top: 2px;
  font-size: 12.5px;
  color: var(--ink-400);
}
</style>
