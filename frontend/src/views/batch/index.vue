<template>
  <div class="batch-page">
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
        <h1>批量<span class="text-gradient">生成</span></h1>
        <p>一次生成多个版本的提示词</p>
      </div>

      <div class="batch-layout">
        <div class="batch-form glass-card">
          <h3>生成配置</h3>
          <el-form label-position="top">
            <el-form-item label="需求描述">
              <el-input v-model="form.requirement" type="textarea" :rows="6" placeholder="描述你希望AI完成的任务..." />
            </el-form-item>
            <el-form-item label="生成数量">
              <div class="count-slider">
                <el-slider v-model="form.count" :min="3" :max="10" show-stops :marks="marks" />
                <span class="count-value">{{ form.count }} 个版本</span>
              </div>
            </el-form-item>
            <el-button type="primary" class="btn-gradient btn-block" :loading="loading" @click="generate">
              开始生成
            </el-button>
          </el-form>
        </div>

        <div class="batch-result glass-card">
          <div class="result-header">
            <h3>生成结果</h3>
            <div v-if="results.length > 0" class="result-actions">
              <el-button size="small" @click="exportResults('txt')">导出TXT</el-button>
              <el-button size="small" @click="exportResults('md')">导出MD</el-button>
            </div>
          </div>
          <div class="result-content">
            <div v-if="results.length === 0 && !loading" class="result-placeholder">
              <div class="placeholder-icon">🎯</div>
              <p>填写左侧表单，点击"开始生成"按钮</p>
              <p class="tip">AI将为你生成 {{ form.count }} 个不同版本的提示词</p>
            </div>
            <div v-else-if="loading" class="result-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <p>AI正在生成中，请稍候...</p>
            </div>
            <div v-else class="result-list">
              <div v-for="(result, index) in results" :key="index" class="result-item">
                <div class="result-header">
                  <span class="result-badge">版本 {{ index + 1 }}</span>
                  <el-button size="small" @click="copyResult(result)">复制</el-button>
                </div>
                <pre class="result-text">{{ result }}</pre>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '../../stores/user'
import { aiApi } from '../../api/ai'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

const userStore = useUserStore()
const form = ref({ requirement: '', count: 5 })
const loading = ref(false)
const results = ref([])
const marks = { 3: '3', 5: '5', 8: '8', 10: '10' }

const generate = async () => {
  if (!form.value.requirement.trim()) {
    ElMessage.warning('请输入需求描述')
    return
  }
  loading.value = true
  try {
    const response = await aiApi.batchGenerate(form.value.requirement, form.value.count)
    results.value = response.split('\n').filter(line => line.trim())
    ElMessage.success('生成成功')
  } catch (e) {
    ElMessage.error('生成失败')
  } finally {
    loading.value = false
  }
}

const copyResult = async (content) => {
  try {
    await navigator.clipboard.writeText(content)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}

const exportResults = (format) => {
  const content = results.value.map((r, i) => `版本 ${i + 1}:\n${r}`).join('\n\n')
  const mimeType = format === 'md' ? 'text/markdown' : 'text/plain'
  const filename = `batch-prompts-${Date.now()}.${format}`
  const blob = new Blob([format === 'md' ? '# 批量生成的提示词\n\n' + content : content], { type: mimeType })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}
</script>

<style scoped>
.main-content { padding-top: 100px; padding-bottom: 60px; }
.page-header { margin-bottom: 32px; }
.page-header h1 { font-size: 36px; margin-bottom: 8px; }
.batch-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 32px; }
.batch-form, .batch-result { padding: 32px; }
.batch-form h3, .batch-result h3 { margin-bottom: 24px; font-size: 18px; }
.count-slider { display: flex; align-items: center; gap: 16px; width: 100%; }
.count-value { min-width: 80px; text-align: center; font-weight: 600; color: var(--color-primary); }
.btn-block { width: 100%; margin-top: 16px; }
.result-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.result-actions { display: flex; gap: 8px; }
.result-content { min-height: 300px; }
.result-placeholder, .result-loading { height: 300px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--text-muted); }
.placeholder-icon { font-size: 48px; margin-bottom: 16px; }
.tip { font-size: 12px; margin-top: 8px; }
.result-list { display: flex; flex-direction: column; gap: 20px; }
.result-item { background: #f8f9fa; border-radius: var(--radius-md); padding: 16px; }
.result-badge { background: var(--color-primary); color: white; padding: 2px 12px; border-radius: 8px; font-size: 12px; font-weight: 600; }
.result-text { margin-top: 12px; white-space: pre-wrap; word-break: break-all; font-size: 14px; line-height: 1.6; }
@media (max-width: 768px) { .batch-layout { grid-template-columns: 1fr; } }
</style>
