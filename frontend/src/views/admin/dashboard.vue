<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667EEA, #764BA2);">
          <User />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.userStats?.total_users || 0 }}</span>
          <span class="stat-label">总用户数</span>
        </div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #F6AD55, #ED8936);">
          <Star />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.userStats?.vip_users || 0 }}</span>
          <span class="stat-label">VIP会员</span>
        </div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #48BB78, #38A169);">
          <View />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.visitStats?.total_visits || 0 }}</span>
          <span class="stat-label">模板总数</span>
        </div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #F56565, #E53E3E);">
          <ChatDotRound />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.aiStats?.total_calls || 0 }}</span>
          <span class="stat-label">AI调用次数</span>
        </div>
      </div>
    </div>

    <div class="chart-section">
      <div class="chart-card glass-card">
        <h3>热门模板 TOP10</h3>
        <div class="top-templates">
          <div v-for="(t, i) in stats.topTemplates" :key="i" class="top-item">
            <span class="rank" :class="{ gold: i === 0, silver: i === 1, bronze: i === 2 }">{{ i + 1 }}</span>
            <span class="name">{{ t.title }}</span>
            <span class="count">{{ t.count }}次</span>
          </div>
        </div>
      </div>
      <div class="chart-card glass-card">
        <h3>今日数据</h3>
        <div class="today-stats">
          <div class="today-item">
            <span class="today-label">新增用户</span>
            <span class="today-value">{{ stats.userStats?.today_new_users || 0 }}</span>
          </div>
          <div class="today-item">
            <span class="today-label">AI调用</span>
            <span class="today-value">{{ stats.aiStats?.today_calls || 0 }}</span>
          </div>
          <div class="today-item">
            <span class="today-label">总Token消耗</span>
            <span class="today-value">{{ stats.aiStats?.total_tokens || 0 }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../../api/admin'
import { User, Star, View, ChatDotRound } from '@element-plus/icons-vue'

const stats = ref({})

onMounted(async () => {
  try {
    stats.value = await adminApi.getDashboard()
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  color: var(--text-muted);
  font-size: 14px;
}

.chart-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.chart-card {
  padding: 24px;
}

.chart-card h3 {
  margin-bottom: 20px;
  font-size: 16px;
}

.top-templates {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.rank {
  width: 24px;
  height: 24px;
  background: var(--color-primary);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

.rank.gold { background: linear-gradient(135deg, #FFD700, #FFA500); }
.rank.silver { background: linear-gradient(135deg, #C0C0C0, #A0A0A0); }
.rank.bronze { background: linear-gradient(135deg, #CD7F32, #B8860B); }

.name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.count {
  color: var(--text-muted);
  font-size: 14px;
}

.today-stats {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.today-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border-radius: 12px;
}

.today-label {
  color: var(--text-secondary);
}

.today-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
}

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .chart-section { grid-template-columns: 1fr; }
}
</style>
