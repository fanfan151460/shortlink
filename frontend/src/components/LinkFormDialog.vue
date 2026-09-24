<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

import { createLink, getTitle, updateLink } from '@/api/link'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  /** 'create' | 'edit' */
  mode: { type: String, default: 'create' },
  gid: { type: String, default: '' },
  /** 编辑时传入当前行，字段取自 ShortLinkRespDTO */
  link: { type: Object, default: null }
})
const emit = defineEmits(['update:modelValue', 'saved'])

const form = reactive({
  originUrl: '',
  validDateType: 0,
  validDate: '',
  description: ''
})
const submitting = ref(false)
const fetchingTitle = ref(false)

const visible = ref(props.modelValue)
watch(
  () => props.modelValue,
  (v) => {
    visible.value = v
    if (v) reset()
  }
)
watch(visible, (v) => emit('update:modelValue', v))

function reset() {
  const isEdit = props.mode === 'edit' && props.link
  form.originUrl = isEdit ? props.link.originUrl || '' : ''
  form.description = isEdit ? props.link.description || '' : ''
  // 编辑接口要么传 validDateType=0（永久），要么传一个具体时间；
  // 不回填旧的有效期，避免把"还有 3 天到期"编辑成"立刻过期"
  form.validDateType = 0
  form.validDate = ''
}

async function onFetchTitle() {
  if (!form.originUrl) return ElMessage.warning('请先输入原始链接')
  fetchingTitle.value = true
  try {
    const title = await getTitle(form.originUrl)
    if (title) {
      form.description = title
      ElMessage.success('标题已获取')
    } else {
      ElMessage.info('未获取到标题')
    }
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    fetchingTitle.value = false
  }
}

async function onSubmit() {
  if (!form.originUrl) return ElMessage.warning('请输入原始链接')
  if (form.validDateType === 1 && !form.validDate) {
    return ElMessage.warning('请选择有效期')
  }

  const body = {
    originUrl: form.originUrl,
    gid: props.gid,
    validDateType: form.validDateType,
    description: form.description
  }
  // 永久有效时不要把空的 validDate 发过去，null 会被后端的有效期校验拦下
  if (form.validDateType === 1) body.validDate = form.validDate
  if (props.mode === 'edit') body.fullShortUrl = props.link.fullShortUrl

  submitting.value = true
  try {
    if (props.mode === 'edit') {
      await updateLink(body)
      ElMessage.success('修改成功')
    } else {
      await createLink(body)
      ElMessage.success('创建成功')
    }
    visible.value = false
    emit('saved')
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="mode === 'edit' ? '编辑短链接' : '创建短链接'"
    width="520px"
  >
    <el-form label-position="top">
      <el-form-item label="原始链接">
        <div class="url-row">
          <el-input v-model="form.originUrl" placeholder="https://..." />
          <el-button :loading="fetchingTitle" @click="onFetchTitle">获取标题</el-button>
        </div>
      </el-form-item>

      <el-form-item label="有效期类型">
        <el-radio-group v-model="form.validDateType">
          <el-radio :value="0">永久有效</el-radio>
          <el-radio :value="1">自定义日期</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="form.validDateType === 1" label="有效期">
        <el-input v-model="form.validDate" type="datetime-local" />
      </el-form-item>

      <el-form-item label="描述">
        <el-input v-model="form.description" placeholder="可选描述" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">确认</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.url-row {
  display: flex;
  gap: 8px;
  width: 100%;
}
</style>
