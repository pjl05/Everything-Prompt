<template>
  <div class="user-admin">
    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="搜索用户..." style="width: 200px;" clearable @keyup.enter="search" />
      <el-button type="primary" class="btn-gradient" @click="search">搜索</el-button>
    </div>

    <el-table :data="users" stripe class="glass-card">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'VIP' ? 'warning' : row.role === 'ADMIN' ? 'danger' : 'info'">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalAiCalls" label="AI调用" width="100" />
      <el-table-column prop="vipExpireTime" label="VIP到期" width="120">
        <template #default="{ row }">{{ row.vipExpireTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="grantVip(row)">授权VIP</el-button>
          <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="fetchUsers" style="margin-top: 24px; justify-content: center;" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../../api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

const fetchUsers = async () => {
  try {
    const result = await adminApi.getUsers({ keyword: keyword.value, page: page.value, size: size.value })
    users.value = result.records || result
    total.value = result.total || 0
  } catch (e) { console.error(e) }
}

const search = () => { page.value = 1; fetchUsers() }

const grantVip = async (user) => {
  await ElMessageBox.prompt('请输入VIP天数', `授权VIP - ${user.username}`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入数字'
  }).then(async ({ value }) => {
    try {
      await adminApi.grantVip(user.id, parseInt(value))
      ElMessage.success('授权成功')
      fetchUsers()
    } catch (e) { ElMessage.error('授权失败') }
  })
}

const toggleStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    await adminApi.updateUserStatus(user.id, newStatus)
    user.status = newStatus
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  } catch (e) { ElMessage.error('操作失败') }
}

onMounted(fetchUsers)
</script>

<style scoped>
.page-toolbar { display: flex; gap: 16px; margin-bottom: 24px; }
</style>
