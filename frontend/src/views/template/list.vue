<template>
  <div class="template-list-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-text text-gradient" @click="$router.push('/')">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link" style="color: var(--color-primary);">模板广场</a>
          <a href="/generator" class="nav-link">Prompt生成器</a>
        </div>
        <div class="nav-auth">
          <el-button v-if="!userStore.isLoggedIn" link @click="$router.push('/login')">登录</el-button>
          <el-button v-if="!userStore.isLoggedIn" type="primary" @click="$router.push('/register')">注册</el-button>
        </div>
      </div>
    </nav>

    <div class="container main-content">
      <div class="page-header">
        <h1>模板广场</h1>
        <p>发现最优质的AI提示词模板</p>
      </div>

      <div class="filter-bar glass-card">
        <el-select v-model="selectedCategory" placeholder="全部分类" clearable @change="fetchTemplates">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索模板..." :prefix-icon="Search" clearable @keyup.enter="fetchTemplates" />
        <button class="btn-gradient" @click="fetchTemplates">搜索</button>
      </div>

      <div class="template-grid" v-if="templates.length > 0">
        <div v-for="template in templates" :key="template.id" class="template-card glass-card" @click="$router.push(`/templates/${template.id}`)">
          <div class="template-header">
            <span class="template-category">{{ getCategoryName(template.categoryId) }}</span>
            <el-tag v-if="template.isPremium" type="warning" size="small">VIP</el-tag>
          </div>
          <h3>{{ template.title }}</h3>
          <p class="template-desc">{{ template.description }}</p>
          <div class="template-footer">
            <span class="usage-count">使用 {{ template.usageCount || 0 }} 次</span>
            <el-rate v-model="template.rating" disabled size="small" />
          </div>
        </div>
      </div>

      <div v-else class="empty-state glass-card">
        <p>暂无模板</p>
      </div>

      <div class="pagination" v-if="total > size">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="fetchTemplates" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { templateApi } from '../../api/template'
import { useUserStore } from '../../stores/user'
import { Search } from '@element-plus/icons-vue'

const userStore = useUserStore()
const templates = ref([])
const categories = ref([])
const selectedCategory = ref(null)
const keyword = ref('')
const page = ref(1)
const size = ref(12)
const total = ref(0)

const getCategoryName = (categoryId) => {
  const cat = categories.value.find(c => c.id === categoryId)
  return cat ? cat.name : ''
}

const fetchTemplates = async () => {
  try {
    const result = await templateApi.getTemplates({
      categoryId: selectedCategory.value,
      keyword: keyword.value,
      page: page.value,
      size: size.value
    })
    templates.value = result.records
    total.value = result.total
  } catch (e) {
    console.error('Failed to load templates:', e)
  }
}

onMounted(async () => {
  try {
    categories.value = await templateApi.getCategories()
  } catch (e) {
    console.error('Failed to load categories:', e)
  }
  await fetchTemplates()
})
</script>

<style scoped>
.main-content {
  padding-top: 100px;
  padding-bottom: 60px;
}
.page-header {
  margin-bottom: 32px;
}
.page-header h1 {
  font-size: 36px;
  margin-bottom: 8px;
}
.filter-bar {
  display: flex;
  gap: 16px;
  padding: 20px;
  margin-bottom: 32px;
}
.filter-bar .el-select, .filter-bar .el-input {
  width: 200px;
}
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}
.template-card {
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
}
.template-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow);
}
.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.template-category {
  font-size: 12px;
  color: var(--color-primary);
  font-weight: 600;
}
.template-card h3 {
  margin-bottom: 8px;
  font-size: 18px;
}
.template-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.template-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.usage-count {
  font-size: 12px;
  color: var(--text-muted);
}
.empty-state {
  padding: 60px;
  text-align: center;
  color: var(--text-muted);
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 16px 0;
}
.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo {
  font-size: 20px;
  font-weight: 700;
}
.nav-links {
  display: flex;
  gap: 32px;
}
.nav-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 500;
}
</style>
