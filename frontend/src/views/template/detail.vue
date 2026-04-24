<template>
  <div class="template-detail-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-text text-gradient" @click="$router.push('/')">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link">模板广场</a>
          <a href="/generator" class="nav-link">Prompt生成器</a>
        </div>
      </div>
    </nav>

    <div class="container main-content" v-if="template">
      <div class="detail-card glass-card">
        <div class="detail-header">
          <span class="category-tag">模板</span>
          <el-tag v-if="template.isPremium" type="warning">VIP</el-tag>
        </div>
        <h1>{{ template.title }}</h1>
        <p class="description">{{ template.description }}</p>

        <div class="template-content">
          <pre>{{ template.content }}</pre>
        </div>

        <div class="detail-actions">
          <button class="btn-gradient" @click="copyContent">
            {{ copied ? '已复制!' : '一键复制' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { templateApi } from '../../api/template'
import { ElMessage } from 'element-plus'

const route = useRoute()
const template = ref(null)
const copied = ref(false)

const copyContent = async () => {
  try {
    await navigator.clipboard.writeText(template.value.content)
    copied.value = true
    ElMessage.success('已复制到剪贴板')
    setTimeout(() => { copied.value = false }, 2000)
  } catch {
    ElMessage.error('复制失败')
  }
}

onMounted(async () => {
  try {
    template.value = await templateApi.getTemplateById(route.params.id)
  } catch (e) {
    console.error('Failed to load template:', e)
  }
})
</script>

<style scoped>
.main-content {
  padding-top: 100px;
  padding-bottom: 60px;
}
.detail-card {
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
}
.detail-header {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.category-tag {
  font-size: 12px;
  color: var(--color-primary);
  font-weight: 600;
}
.detail-card h1 {
  font-size: 32px;
  margin-bottom: 12px;
}
.description {
  color: var(--text-secondary);
  margin-bottom: 32px;
}
.template-content {
  background: #f8f9fa;
  padding: 24px;
  border-radius: var(--radius-md);
  margin-bottom: 32px;
}
.template-content pre {
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 14px;
  line-height: 1.6;
}
.detail-actions {
  display: flex;
  gap: 16px;
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
