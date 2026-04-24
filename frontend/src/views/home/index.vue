<template>
  <div class="home">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">✨</span>
          <span class="logo-text text-gradient">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link">模板广场</a>
          <a href="/generator" class="nav-link">Prompt生成器</a>
        </div>
        <div class="nav-auth">
          <el-button v-if="!userStore.isLoggedIn" link @click="$router.push('/login')">登录</el-button>
          <el-button v-if="!userStore.isLoggedIn" type="primary" @click="$router.push('/register')">注册</el-button>
          <el-dropdown v-else>
            <span class="user-avatar">
              <el-avatar :src="userStore.userInfo?.avatar || '/default-avatar.png'" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="userStore.logout()">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <section class="hero">
      <div class="hero-bg"></div>
      <div class="hero-content container">
        <h1 class="hero-title">
          释放 <span class="text-gradient">AI</span> 的全部潜力
        </h1>
        <p class="hero-subtitle">
          专业提示词一键生成 · 行业模板库 · 智能优化 · 个人词库
        </p>
        <div class="hero-actions">
          <button class="btn-gradient btn-lg" @click="$router.push('/generator')">
            立即开始 →
          </button>
          <button class="btn-outline" @click="$router.push('/templates')">
            浏览模板
          </button>
        </div>
      </div>
    </section>

    <section class="categories container">
      <h2 class="section-title">探索分类</h2>
      <div class="category-grid">
        <div v-for="cat in categories" :key="cat.id" class="category-card glass-card" @click="$router.push(`/templates?category=${cat.id}`)">
          <div class="category-icon">{{ cat.icon }}</div>
          <h3>{{ cat.name }}</h3>
          <p>{{ cat.description }}</p>
        </div>
      </div>
    </section>

    <section class="features">
      <div class="container">
        <h2 class="section-title">为什么选择我们</h2>
        <div class="feature-grid">
          <div class="feature-card glass-card">
            <div class="feature-icon">🎯</div>
            <h3>精准生成</h3>
            <p>基于场景、角色、要求的结构化输入，生成专业的AI提示词</p>
          </div>
          <div class="feature-card glass-card">
            <div class="feature-icon">📚</div>
            <h3>丰富模板</h3>
            <p>涵盖编程、办公、写作、绘画等20+行业的专业模板库</p>
          </div>
          <div class="feature-card glass-card">
            <div class="feature-icon">🤖</div>
            <h3>AI优化</h3>
            <p>智能优化你的原始提示词，让AI理解更准确</p>
          </div>
          <div class="feature-card glass-card">
            <div class="feature-icon">💾</div>
            <h3>个人词库</h3>
            <p>创建和管理你的专属提示词，随时复用</p>
          </div>
        </div>
      </div>
    </section>

    <footer class="footer">
      <p>© 2024 Everything-Prompt. 让AI提问更简单.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { templateApi } from '../../api/template'

const userStore = useUserStore()
const categories = ref([])

onMounted(async () => {
  if (userStore.isLoggedIn) {
    await userStore.fetchUserInfo()
  }
  try {
    categories.value = await templateApi.getCategories()
  } catch (e) {
    console.error('Failed to load categories:', e)
  }
})
</script>

<style scoped>
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
  display: flex;
  align-items: center;
  gap: 8px;
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
  transition: color 0.3s;
}
.nav-link:hover {
  color: var(--color-primary);
}
.hero {
  position: relative;
  padding: 160px 0 100px;
  overflow: hidden;
}
.hero-bg {
  position: absolute;
  inset: 0;
  background: var(--gradient-hero);
  opacity: 0.05;
  filter: blur(100px);
}
.hero-content {
  text-align: center;
  position: relative;
}
.hero-title {
  font-size: clamp(40px, 6vw, 72px);
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 24px;
}
.hero-subtitle {
  font-size: 20px;
  color: var(--text-secondary);
  margin-bottom: 40px;
}
.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}
.btn-lg {
  padding: 16px 32px;
  font-size: 16px;
}
.btn-outline {
  background: transparent;
  border: 2px solid var(--color-primary);
  color: var(--color-primary);
  padding: 14px 30px;
  border-radius: var(--radius-md);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-outline:hover {
  background: var(--color-primary);
  color: white;
}
.categories {
  padding: 80px 0;
}
.section-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 48px;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}
.category-card {
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
}
.category-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow);
}
.category-icon {
  font-size: 48px;
  margin-bottom: 16px;
}
.category-card h3 {
  margin-bottom: 8px;
}
.category-card p {
  color: var(--text-muted);
  font-size: 14px;
}
.features {
  padding: 80px 0;
  background: linear-gradient(180deg, transparent, rgba(102, 126, 234, 0.03));
}
.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}
.feature-card {
  padding: 32px;
  text-align: center;
}
.feature-icon {
  font-size: 48px;
  margin-bottom: 16px;
}
.footer {
  text-align: center;
  padding: 40px 0;
  color: var(--text-muted);
}
</style>
