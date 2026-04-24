<template>
  <div class="category-admin">
    <div class="page-toolbar">
      <el-button type="primary" @click="showAddDialog = true">新增分类</el-button>
    </div>

    <el-table :data="categories" stripe class="glass-card">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="icon" label="图标" width="80">
        <template #default="{ row }"><span style="font-size: 20px;">{{ row.icon }}</span></template>
      </el-table-column>
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column prop="isFree" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isFree ? 'success' : 'warning'" size="small">{{ row.isFree ? '免费' : '付费' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editCategory(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteCategory(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAddDialog" :title="editingCategory ? '编辑分类' : '新增分类'" width="500px">
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="categoryForm.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="categoryForm.code" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="categoryForm.icon" placeholder="如: 💻" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="categoryForm.description" type="textarea" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="categoryForm.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="是否免费"><el-switch v-model="categoryForm.isFree" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../../api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([])
const showAddDialog = ref(false)
const editingCategory = ref(null)

const categoryForm = ref({ name: '', code: '', icon: '', description: '', sortOrder: 0, isFree: 1 })

const fetchCategories = async () => {
  try { categories.value = await adminApi.getCategories() } catch (e) { console.error(e) }
}

const editCategory = (row) => {
  editingCategory.value = row
  categoryForm.value = { ...row }
  showAddDialog.value = true
}

const saveCategory = async () => {
  try {
    if (editingCategory.value) {
      await adminApi.updateCategory(editingCategory.value.id, categoryForm.value)
      ElMessage.success('更新成功')
    } else {
      await adminApi.createCategory(categoryForm.value)
      ElMessage.success('创建成功')
    }
    showAddDialog.value = false
    editingCategory.value = null
    fetchCategories()
  } catch (e) { ElMessage.error('保存失败') }
}

const toggleStatus = async (row) => {
  try {
    await adminApi.updateCategory(row.id, { ...row, status: row.status })
    ElMessage.success('状态更新成功')
  } catch (e) { ElMessage.error('更新失败') }
}

const deleteCategory = async (id) => {
  await ElMessageBox.confirm('确认删除该分类?', '警告', { type: 'warning' })
  try {
    await adminApi.deleteCategory(id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (e) { ElMessage.error('删除失败') }
}

onMounted(fetchCategories)
</script>

<style scoped>
.page-toolbar { display: flex; gap: 16px; margin-bottom: 24px; }
</style>
