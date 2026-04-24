<template>
  <div class="vip-templates-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">✨</span>
          <span class="logo-text text-gradient">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link">模板广场</a>
          <a href="/vip/templates" class="nav-link active">高级模板</a>
          <a href="/word/list" class="nav-link">私有词库</a>
        </div>
        <div class="nav-auth">
          <el-dropdown v-if="userStore.isLoggedIn">
            <span class="user-avatar">
              <el-avatar :src="userStore.userInfo?.avatar || '/default-avatar.png'" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/favorite/list')">我的收藏</el-dropdown-item>
                <el-dropdown-item @click="userStore.logout()">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <div class="container main-content">
      <div class="page-header">
        <div class="header-content">
          <h1>高级<span class="text-gradient">模板</span></h1>
          <p>解锁专属VIP模板，释放AI全部潜力</p>
        </div>
        <div class="vip-badge-large">
          <span class="crown">👑</span>
          <span>VIP专属</span>
        </div>
      </div>

      <div v-if="!userStore.isVip" class="vip-promo glass-card">
        <div class="promo-content">
          <h2>开通VIP，解锁全部高级模板</h2>
          <p>海量高级模板，助您工作事半功倍</p>
          <button class="btn-gradient btn-lg" @click="showUpgradeDialog">立即开通</button>
        </div>
        <div class="promo-features">
          <div class="feature-item"><span class="check">✓</span> 全部高级模板</div>
          <div class="feature-item"><span class="check">✓</span> 无限AI优化</div>
          <div class="feature-item"><span class="check">✓</span> 私有词库</div>
          <div class="feature-item"><span class="check">✓</span> 批量生成</div>
        </div>
      </div>

      <div v-else class="template-grid">
        <div v-for="template in templates" :key="template.id" class="template-card glass-card" @click="viewDetail(template.id)">
          <div class="vip-badge">VIP专属</div>
          <div class="template-category">{{ template.categoryName }}</div>
          <h3>{{ template.title }}</h3>
          <p class="template-desc">{{ template.description }}</p>
          <div class="template-footer">
            <span class="usage-count">使用 {{ template.usageCount }} 次</span>
            <el-rate v-model="template.rating" disabled size="small" />
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showUpgradeDialog" title="开通VIP会员" width="400px">
      <div class="price-options">
        <div class="price-card" :class="{ selected: selectedDays === 30 }" @click="selectedDays = 30">
          <div class="price">¥30</div>
          <div class="period">月卡</div>
        </div>
        <div class="price-card" :class="{ selected: selectedDays === 90 }" @click="selectedDays = 90">
          <div class="price">¥80</div>
          <div class="period">季卡</div>
          <div class="tag">省10元</div>
        </div>
        <div class="price-card" :class="{ selected: selectedDays === 365 }" @click="selectedDays = 365">
          <div class="price">¥280</div>
          <div class="period">年卡</div>
          <div class="tag">省40元</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showUpgradeDialog = false">取消</el-button>
        <el-button type="primary" class="btn-gradient" @click="handleUpgrade">确认开通</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { vipApi } from '../../api/vip'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const templates = ref([])
const showUpgradeDialog = ref(false)
const selectedDays = ref(30)

const viewDetail = (id) => router.push(`/templates/${id}`)

const handleUpgrade = async () => {
  try {
    await vipApi.upgrade(selectedDays.value)
    ElMessage.success('VIP开通成功')
    showUpgradeDialog.value = false
    userStore.fetchUserInfo()
  } catch (e) {
    ElMessage.error('开通失败')
  }
}

onMounted(async () => {
  if (userStore.isLoggedIn) await userStore.fetchUserInfo()
})
</script>

<style scoped>
.main-content { padding-top: 100px; padding-bottom: 60px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
.header-content h1 { font-size: 36px; margin-bottom: 8px; }
.vip-badge-large { display: flex; align-items: center; gap: 8px; background: linear-gradient(135deg, #F6AD55, #ED8936); color: white; padding: 12px 24px; border-radius: 24px; font-weight: 600; }
.crown { font-size: 24px; }
.vip-promo { display: flex; justify-content: space-between; align-items: center; padding: 40px; margin-bottom: 32px; background: linear-gradient(135deg, rgba(246, 173, 85, 0.1), rgba(237, 137, 54, 0.1)); border: 1px solid rgba(246, 173, 85, 0.3); }
.promo-content h2 { font-size: 24px; margin-bottom: 8px; }
.promo-content p { color: var(--text-secondary); margin-bottom: 20px; }
.promo-features { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.feature-item { display: flex; align-items: center; gap: 8px; }
.check { color: #48BB78; font-weight: bold; }
.template-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 24px; }
.template-card { position: relative; padding: 24px; cursor: pointer; transition: all 0.3s; }
.template-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-glow); }
.vip-badge { position: absolute; top: 12px; right: 12px; background: linear-gradient(135deg, #F6AD55, #ED8936); color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600; }
.template-category { font-size: 12px; color: var(--color-primary); font-weight: 600; margin-bottom: 8px; }
.template-card h3 { margin-bottom: 8px; font-size: 18px; }
.template-desc { color: var(--text-secondary); font-size: 14px; margin-bottom: 16px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.template-footer { display: flex; justify-content: space-between; align-items: center; }
.usage-count { font-size: 12px; color: var(--text-muted); }
.price-options { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.price-card { position: relative; padding: 20px; border: 2px solid var(--glass-border); border-radius: var(--radius-lg); text-align: center; cursor: pointer; transition: all 0.3s; }
.price-card.selected { border-color: var(--color-primary); background: rgba(102, 126, 234, 0.1); }
.price-card .tag { position: absolute; top: -8px; right: -8px; background: #ED8936; color: white; font-size: 10px; padding: 2px 8px; border-radius: 8px; }
.price { font-size: 24px; font-weight: 700; color: var(--color-primary); }
.period { font-size: 14px; color: var(--text-secondary); }
.btn-lg { padding: 16px 32px; font-size: 16px; }
</style>
