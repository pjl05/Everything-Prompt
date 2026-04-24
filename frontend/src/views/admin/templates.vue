<template>
  <div class="template-admin">
    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="搜索模板..." style="width: 200px;" clearable @keyup.enter="search" />
      <el-select v-model="status" placeholder="状态" clearable style="width: 120px;">
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button type="primary" class="btn-gradient" @click="search">搜索</el-button>
      <el-button type="primary" @click="showAddDialog = true">新增模板</el-button>
    </div>

    <el-table :data="templates" stripe class="glass-card">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="isPremium" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isPremium ? 'warning' : 'success'" size="small">{{ row.isPremium ? '付费' : '免费' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="usageCount" label="使用次数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editTemplate(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteTemplate(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="fetchTemplates" style="margin-top: 24px; justify-content: center;" />

    <el-dialog v-model="showAddDialog" :title="editingTemplate ? '编辑模板' : '新增模板'" width="600px">
      <el-form :model="templateForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="templateForm.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="templateForm.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="templateForm.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="templateForm.categoryId" style="width: 100%;">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签"><el-input v-model="templateForm.tags" placeholder="多个标签用逗号分隔" /></el-form-item>
        <el-form-item label="是否付费"><el-switch v-model="templateForm.isPremium" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../../api/admin'
import { templateApi } from '../../api/template'
import { ElMessage, ElMessageBox } from 'element-plus'

const templates = ref([])
const categories = ref([])
const keyword = ref('')
const status = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showAddDialog = ref(false)
const editingTemplate = ref(null)

const templateForm = ref({ title: '', description: '', content: '', categoryId: null, tags: '', isPremium: 0 })

const fetchTemplates = async () => {
  try {
    templates.value = await adminApi.getTemplates({ keyword: keyword.value, status: status.value })
  } catch (e) { console.error(e) }
}

const search = () => { page.value = 1; fetchTemplates() }

const editTemplate = (row) => {
  editingTemplate.value = row
  templateForm.value = { ...row }
  showAddDialog.value = true
}

const saveTemplate = async () => {
  try {
    if (editingTemplate.value) {
      await adminApi.updateTemplate(editingTemplate.value.id, templateForm.value)
      ElMessage.success('更新成功')
    } else {
      await adminApi.createTemplate(templateForm.value)
      ElMessage.success('创建成功')
    }
    showAddDialog.value = false
    editingTemplate.value = null
    fetchTemplates()
  } catch (e) { ElMessage.error('保存失败') }
}

const toggleStatus = async (row) => {
  try {
    await adminApi.updateTemplateStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (e) { ElMessage.error('更新失败') }
}

const deleteTemplate = async (id) => {
  await ElMessageBox.confirm('确认删除该模板?', '警告', { type: 'warning' })
  try {
    await adminApi.deleteTemplate(id)
    ElMessage.success('删除成功')
    fetchTemplates()
  } catch (e) { ElMessage.error('删除失败') }
}

onMounted(async () => {
  categories.value = await templateApi.getCategories()
  fetchTemplates()
})
</script>

<style scoped>
.page-toolbar { display: flex; gap: 16px; margin-bottom: 24px; }
</style>
