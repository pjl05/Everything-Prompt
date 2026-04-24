<template>
  <div class="word-list-page">
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
          <a href="/word/list" class="nav-link active">私有词库</a>
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
        <div class="header-content">
          <h1>私有<span class="text-gradient">词库</span></h1>
          <p>管理你的专属提示词词条</p>
        </div>
        <el-button type="primary" class="btn-gradient" @click="showAddDialog = true">添加词条</el-button>
      </div>

      <div class="group-tabs">
        <el-tag v-for="group in groups" :key="group" :type="activeGroup === group ? 'primary' : 'info'" @click="activeGroup = group" class="group-tag">{{ group }}</el-tag>
      </div>

      <div class="word-list">
        <div v-for="word in filteredWords" :key="word.id" class="word-card glass-card">
          <div class="word-header">
            <h3>{{ word.title }}</h3>
            <el-tag size="small">{{ word.groupName }}</el-tag>
          </div>
          <p class="word-content">{{ word.content }}</p>
          <div class="word-actions">
            <el-button size="small" type="primary" @click="useWord(word)">使用</el-button>
            <el-button size="small" @click="editWord(word)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteWord(word.id)">删除</el-button>
          </div>
        </div>
      </div>

      <div v-if="words.length === 0" class="empty-state">
        <p>暂无词条，添加你的第一个词条吧</p>
      </div>
    </div>

    <el-dialog v-model="showAddDialog" :title="editingWord ? '编辑词条' : '添加词条'" width="500px">
      <el-form :model="wordForm" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="wordForm.title" placeholder="输入词条标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="wordForm.content" type="textarea" :rows="4" placeholder="输入词条内容" />
        </el-form-item>
        <el-form-item label="分组">
          <el-input v-model="wordForm.groupName" placeholder="输入分组名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" class="btn-gradient" @click="saveWord">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { wordApi } from '../../api/word'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const words = ref([])
const activeGroup = ref('全部')
const showAddDialog = ref(false)
const editingWord = ref(null)
const wordForm = ref({ title: '', content: '', groupName: '默认分组' })

const groups = computed(() => {
  const set = new Set(words.value.map(w => w.groupName))
  return ['全部', ...set]
})

const filteredWords = computed(() => {
  if (activeGroup.value === '全部') return words.value
  return words.value.filter(w => w.groupName === activeGroup.value)
})

const useWord = (word) => {
  navigator.clipboard.writeText(word.content)
  ElMessage.success('已复制到剪贴板')
}

const editWord = (word) => {
  editingWord.value = word
  wordForm.value = { title: word.title, content: word.content, groupName: word.groupName }
  showAddDialog.value = true
}

const deleteWord = async (id) => {
  try {
    await wordApi.deleteWord(id)
    ElMessage.success('删除成功')
    fetchWords()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const saveWord = async () => {
  try {
    if (editingWord.value) {
      await wordApi.updateWord(editingWord.value.id, wordForm.value)
      ElMessage.success('更新成功')
    } else {
      await wordApi.addWord(wordForm.value)
      ElMessage.success('添加成功')
    }
    showAddDialog.value = false
    editingWord.value = null
    wordForm.value = { title: '', content: '', groupName: '默认分组' }
    fetchWords()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const fetchWords = async () => {
  try {
    words.value = await wordApi.getMyWords()
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  if (userStore.isLoggedIn) await userStore.fetchUserInfo()
  if (userStore.isVip) await fetchWords()
})
</script>

<style scoped>
.main-content { padding-top: 100px; padding-bottom: 60px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
.header-content h1 { font-size: 36px; margin-bottom: 8px; }
.group-tabs { display: flex; gap: 12px; margin-bottom: 24px; flex-wrap: wrap; }
.group-tag { cursor: pointer; }
.word-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 24px; }
.word-card { padding: 24px; }
.word-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.word-header h3 { font-size: 18px; }
.word-content { color: var(--text-secondary); font-size: 14px; margin-bottom: 16px; line-height: 1.6; }
.word-actions { display: flex; gap: 8px; }
.empty-state { text-align: center; padding: 60px 0; color: var(--text-muted); }
</style>
