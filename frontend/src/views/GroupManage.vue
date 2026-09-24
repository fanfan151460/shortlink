<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { addGroup, deleteGroup, sortGroup, updateGroup } from '@/api/group'
import { appStore, loadGroups } from '@/store/app'

const router = useRouter()

const newName = ref('')
const editingGid = ref('')
const editingName = ref('')
const busy = ref(false)

onMounted(loadGroups)

async function reload() {
  try {
    await loadGroups()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

async function onCreate() {
  const name = newName.value.trim()
  if (!name) return ElMessage.warning('请输入分组名称')
  busy.value = true
  try {
    await addGroup(name)
    newName.value = ''
    ElMessage.success('分组创建成功')
    await reload()
  } catch {
    /* 拦截器已经提示过了 */
  } finally {
    busy.value = false
  }
}

function startRename(row) {
  editingGid.value = row.gid
  editingName.value = row.name
}

function cancelRename() {
  editingGid.value = ''
  editingName.value = ''
}

async function submitRename(row) {
  const name = editingName.value.trim()
  if (!name) return ElMessage.warning('分组名称不能为空')
  if (name === row.name) return cancelRename()
  try {
    await updateGroup(row.gid, name)
    ElMessage.success('已重命名')
    cancelRename()
    await reload()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定删除分组「${row.name}」？空分组会被彻底删除，非空分组内的短链接将移入回收站。`,
      '删除分组',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  try {
    await deleteGroup(row.gid)
    ElMessage.success('已删除')
    if (appStore.currentGid === row.gid) appStore.currentGid = ''
    await reload()
  } catch {
    /* 拦截器已经提示过了 */
  }
}

/**
 * 上移 / 下移。
 *
 * GET /group 只返回 gid / name / delFlag，不返回 sortOrder，所以前端没法做
 * "只改这两个的序号" 的增量更新，只能把整个列表按新顺序从 0 重新编号后整体提交。
 * 另外注意接口字段名是 groupId，不是别处的 gid。
 */
async function move(index, delta) {
  const target = index + delta
  const list = appStore.groups
  if (target < 0 || target >= list.length) return
  const reordered = list.slice()
  ;[reordered[index], reordered[target]] = [reordered[target], reordered[index]]

  // 先本地生效，避免等接口回来才看到位移
  appStore.groups = reordered
  try {
    const orders = reordered.map((g, i) => ({ groupId: g.gid, sortOrder: i }))
    appStore.groups = (await sortGroup(orders)) || reordered
  } catch {
    await reload()
  }
}

function openLinks(gid) {
  appStore.currentGid = gid
  router.push({ name: 'links' })
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h2>分组管理</h2>
      <div class="toolbar">
        <el-input
          v-model="newName"
          placeholder="新建分组名称"
          maxlength="20"
          size="small"
          style="width: 180px"
          @keyup.enter="onCreate"
        />
        <el-button type="primary" size="small" :loading="busy" @click="onCreate">新建</el-button>
      </div>
    </div>

    <el-table :data="appStore.groups" border>
      <el-table-column label="排序" width="120" align="center">
        <template #default="{ $index }">
          <el-button
            link
            size="small"
            :disabled="$index === 0"
            @click="move($index, -1)"
          >
            上移
          </el-button>
          <el-button
            link
            size="small"
            :disabled="$index === appStore.groups.length - 1"
            @click="move($index, 1)"
          >
            下移
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="分组名称" min-width="220">
        <template #default="{ row }">
          <el-input
            v-if="editingGid === row.gid"
            v-model="editingName"
            size="small"
            style="width: 200px"
            @keyup.enter="submitRename(row)"
          />
          <span v-else>{{ row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column label="gid" width="140">
        <template #default="{ row }">
          <span class="mono muted">{{ row.gid }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="260" align="center">
        <template #default="{ row }">
          <template v-if="editingGid === row.gid">
            <el-button link type="primary" size="small" @click="submitRename(row)">保存</el-button>
            <el-button link size="small" @click="cancelRename">取消</el-button>
          </template>
          <template v-else>
            <el-button link type="primary" size="small" @click="openLinks(row.gid)">
              查看短链接
            </el-button>
            <el-button link type="primary" size="small" @click="startRename(row)">重命名</el-button>
            <el-button link type="danger" size="small" @click="onDelete(row)">删除</el-button>
          </template>
        </template>
      </el-table-column>

      <template #empty>暂无分组</template>
    </el-table>

    <p class="muted" style="margin-top: 12px">
      排序接口接收的是全量有序列表，所以这里每次上移/下移都会把全部序号按 0 起重新提交一遍。
    </p>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
