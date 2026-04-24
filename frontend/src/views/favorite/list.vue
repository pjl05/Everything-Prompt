<template>
  <div class="favorite-list-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">✨</span>
          <span class="logo-text text-gradient">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link">模板广场</a>
          <a href="/vip/templates" class="nav-link">高级模板</a>
          <a href="/word/list" class="nav-link">私有词库</a>
        </div>
        <div class="nav-auth">
          <el-dropdown v-if="userStore.isLoggedIn">
            <span class="user-avatar"><el-avatar :src="userStore.userInfo?.avatar || '/default-avatar.png'" /></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/favorite/list')" class="active">我的收藏</el-dropdown-item>
                <el-dropdown-item @click="userStore.logout()">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <div class="container main-content">
      <div class="page-header">
        <h1>我的<span class="text-gradient">收藏</span></h1>
        <p>管理你收藏的模板</p>
      </div>

      <div v-if="favorites.length === 0" class="empty-state glass-card">
        <div class="empty-icon">❤️</div>
        <h2>暂无收藏</h2>
        <p>去模板广场逛逛，发现喜欢的模板吧</p>
        <button class="btn-gradient" @click="$router.push('/templates')">浏览模板</button>
      </div>

      <div v-else class="template-grid">
        <div v-for="template in favorites" :key="template.id" class="template-card glass-card">
          <div class="template-category">{{ template.categoryName }}</div>
          <h3>{{ template.title }}</h3>
          <p class="template-desc">{{ template.description }}</p>
          <div class="template-footer">
            <span class="usage-count">使用 {{ template.usageCount }} 次</span>
            <div class="card-actions">
              <el-button size="small" type="primary" @click="useTemplate(template)">使用</el-button>
              <el-button size="small" type="danger" @click="removeFavorite(template.id)">取消</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { favoriteApi } from '../../api/favorite'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const favorites = ref([])

const useTemplate = (template) => {
  navigator.clipboard.writeText(template.content)
  ElMessage.success('模板内容已复制到剪贴板')
}

const removeFavorite = async (id) => {
  try {
    await favoriteApi.removeFavorite(id)
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (e) {
    ElMessage.error('取消失败')
  }
}

const fetchFavorites = async () => {
  try {
    favorites.value = await favoriteApi.getMyFavorites()
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  if (userStore.isLoggedIn) await userStore.fetchUserInfo()
  await fetchFavorites()
})
</script>

<style scoped>
.main-content { padding-top: 100px; padding-bottom: 60px; }
.page-header { margin-bottom: 32px; }
.page-header h1 { font-size: 36px; margin-bottom: 8px; }
.empty-state { text-align: center; padding: 80px 40px; }
.empty-icon { font-size: 64px; margin-bottom: 24px; }
.empty-state h2 { font-size: 24px; margin-bottom: 8px; }
.empty-state p { color: var(--text-secondary); margin-bottom: 24px; }
.template-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 24px; }
.template-card { padding: 24px; }
.template-category { font-size: 12px; color: var(--color-primary); font-weight: 600; margin-bottom: 8px; }
.template-card h3 { margin-bottom: 8px; font-size: 18px; }
.template-desc { color: var(--text-secondary); font-size: 14px; margin-bottom: 16px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.template-footer { display: flex; justify-content: space-between; align-items: center; }
.usage-count { font-size: 12px; color: var(--text-muted); }
.card-actions { display: flex; gap: 8px; }
</style>
