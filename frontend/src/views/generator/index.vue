<template>
  <div class="generator-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-text text-gradient" @click="$router.push('/')">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link">模板广场</a>
          <a href="/generator" class="nav-link" style="color: var(--color-primary);">Prompt生成器</a>
        </div>
      </div>
    </nav>

    <div class="container main-content">
      <div class="page-header">
        <h1>Prompt <span class="text-gradient">生成器</span></h1>
        <p>通过可视化表单生成专业的AI提示词</p>
      </div>

      <div class="generator-layout">
        <div class="generator-form glass-card">
          <h3>基础信息</h3>
          <el-form label-position="top">
            <el-form-item label="场景">
              <el-select v-model="form.scene" placeholder="选择使用场景">
                <el-option label="代码生成" value="code" />
                <el-option label="文章写作" value="writing" />
                <el-option label="数据分析" value="analysis" />
                <el-option label="图片生成" value="image" />
                <el-option label="问答咨询" value="qa" />
              </el-select>
            </el-form-item>
            <el-form-item label="角色">
              <el-select v-model="form.role" placeholder="选择AI角色">
                <el-option label="技术专家" value="expert" />
                <el-option label="文案助手" value="copywriter" />
                <el-option label="数据分析员" value="analyst" />
                <el-option label="创意设计师" value="designer" />
                <el-option label="私人顾问" value="advisor" />
              </el-select>
            </el-form-item>
            <el-form-item label="你的要求">
              <el-input v-model="form.requirement" type="textarea" :rows="4" placeholder="描述你希望AI完成的具体任务..." />
            </el-form-item>
            <el-form-item label="输出格式">
              <el-select v-model="form.format" placeholder="选择输出格式">
                <el-option label="Markdown" value="markdown" />
                <el-option label="JSON" value="json" />
                <el-option label="纯文本" value="text" />
                <el-option label="代码块" value="code" />
                <el-option label="列表" value="list" />
              </el-select>
            </el-form-item>
            <el-form-item label="约束条件">
              <el-input v-model="form.constraint" type="textarea" :rows="3" placeholder="添加任何特殊要求或限制..." />
            </el-form-item>
            <el-button type="primary" class="btn-gradient btn-block" @click="generate">生成 Prompt</el-button>
          </el-form>
        </div>

        <div class="generator-result glass-card">
          <div class="result-header">
            <h3>生成的 Prompt</h3>
            <el-button v-if="result" @click="copyResult">一键复制</el-button>
          </div>
          <div class="result-content">
            <div v-if="!result" class="result-placeholder">
              <p>填写左侧表单，点击生成 Prompt 按钮</p>
            </div>
            <div v-else class="result-text">
              <pre>{{ result }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const form = ref({
  scene: '',
  role: '',
  requirement: '',
  format: 'markdown',
  constraint: ''
})

const result = ref('')

const generate = () => {
  let prompt = '【角色】你是一个' + (form.value.role === 'expert' ? '技术专家' : form.value.role === 'copywriter' ? '文案助手' : 'AI助手') + '。\n\n'
  prompt += '【任务】' + form.value.requirement + '\n\n'
  prompt += '【输出格式】请使用 ' + form.value.format + ' 格式输出。\n\n'
  if (form.value.constraint) {
    prompt += '【约束条件】' + form.value.constraint
  }
  result.value = prompt
}

const copyResult = async () => {
  try {
    await navigator.clipboard.writeText(result.value)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped>
.main-content {
  padding-top: 100px;
  padding-bottom: 60px;
}
.page-header {
  margin-bottom: 40px;
}
.page-header h1 {
  font-size: 40px;
  margin-bottom: 8px;
}
.generator-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}
.generator-form {
  padding: 32px;
}
.generator-form h3 {
  margin-bottom: 24px;
  font-size: 18px;
}
.generator-result {
  padding: 32px;
  display: flex;
  flex-direction: column;
}
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.result-content {
  flex: 1;
  min-height: 300px;
}
.result-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
}
.result-text pre {
  background: #f8f9fa;
  padding: 16px;
  border-radius: var(--radius-md);
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 14px;
  line-height: 1.6;
}
.btn-block {
  width: 100%;
  margin-top: 16px;
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
