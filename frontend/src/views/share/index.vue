<template>
  <div class="share-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-text text-gradient" @click="$router.push('/')">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link">模板广场</a>
          <a href="/ai/chat" class="nav-link">AI对话</a>
          <a href="/share" class="nav-link active">案例墙</a>
        </div>
      </div>
    </nav>

    <div class="main-content">
      <div class="page-header">
        <h1>案例墙</h1>
        <p>看看其他用户是如何使用提示词的</p>
      </div>

      <div class="share-grid">
        <div v-for="item in shares" :key="item.id" class="share-card glass-card">
          <div class="card-header">
            <h3>{{ item.promptTitle }}</h3>
            <button class="btn-like" :class="{ liked: item.hasLiked }" @click="toggleLike(item)">
              ❤️ {{ item.likeCount }}
            </button>
          </div>
          <p class="description">{{ item.description }}</p>
          <div class="prompt-preview">
            <pre>{{ item.promptContent }}</pre>
          </div>
          <div class="ai-result" v-if="item.aiResult">
            <h4>AI 回复</h4>
            <pre>{{ item.aiResult }}</pre>
          </div>
          <div class="card-footer">
            <span class="usage-count">使用 {{ item.usageCount }} 次</span>
            <span class="date">{{ formatDate(item.createTime) }}</span>
          </div>
        </div>
      </div>

      <div v-if="!loading && shares.length === 0" class="empty-state">
        <p>还没有分享，快来成为第一个分享者！</p>
        <button class="btn-gradient" @click="$router.push('/templates')">去挑选模板</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { shareApi } from '../../api/share'
import { ElMessage } from 'element-plus'

const shares = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

const loadShares = async () => {
  if (loading.value || !hasMore.value) return
  loading.value = true
  try {
    const res = await shareApi.getShareList(null, page.value, 10)
    const newShares = res.data.records || []
    if (newShares.length < 10) {
      hasMore.value = false
    }
    shares.value.push(...newShares)
    page.value++
  } catch (e) {
    ElMessage.error('加载分享失败')
  } finally {
    loading.value = false
  }
}

const toggleLike = async (item) => {
  try {
    const res = await shareApi.toggleLike(item.id)
    item.hasLiked = res.data.liked
    item.likeCount += res.data.liked ? 1 : -1
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

onMounted(() => {
  loadShares()
})
</script>

<style scoped>
.share-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 24px 60px;
}
.page-header {
  text-align: center;
  margin-bottom: 48px;
}
.page-header h1 {
  font-size: 36px;
  margin-bottom: 12px;
}
.page-header p {
  color: var(--text-secondary);
}
.share-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}
.share-card {
  padding: 24px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}
.card-header h3 {
  font-size: 18px;
  flex: 1;
}
.btn-like {
  background: rgba(255,255,255,0.1);
  border: none;
  padding: 6px 12px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}
.btn-like.liked {
  background: #ff6b6b;
  color: white;
}
.description {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
}
.prompt-preview {
  background: rgba(255,255,255,0.05);
  padding: 12px;
  border-radius: var(--radius-sm);
  margin-bottom: 12px;
}
.prompt-preview pre {
  white-space: pre-wrap;
  font-size: 12px;
  max-height: 100px;
  overflow: hidden;
}
.ai-result {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1), rgba(139, 92, 246, 0.05));
  padding: 12px;
  border-radius: var(--radius-sm);
  margin-bottom: 12px;
}
.ai-result h4 {
  font-size: 12px;
  color: var(--color-primary);
  margin-bottom: 8px;
}
.ai-result pre {
  white-space: pre-wrap;
  font-size: 12px;
  max-height: 80px;
  overflow: hidden;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-secondary);
}
.empty-state {
  text-align: center;
  padding: 60px;
  color: var(--text-secondary);
}
.empty-state p {
  margin-bottom: 20px;
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
.nav-link.active {
  color: var(--color-primary);
}
</style>
