<template>
  <div class="chat-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-text text-gradient" @click="$router.push('/')">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link">模板广场</a>
          <a href="/ai/chat" class="nav-link active">AI对话</a>
        </div>
      </div>
    </nav>

    <div class="chat-container">
      <div class="chat-sidebar glass-card">
        <div class="sidebar-header">
          <h3>对话历史</h3>
          <button class="btn-new-chat" @click="createNewChat">+ 新对话</button>
        </div>
        <div class="session-list">
          <div
            v-for="session in sessions"
            :key="session.id"
            class="session-item"
            :class="{ active: currentSession?.id === session.id }"
            @click="selectSession(session)"
          >
            <span class="session-title">{{ session.title }}</span>
            <button class="btn-delete" @click.stop="deleteSession(session.id)">×</button>
          </div>
        </div>
      </div>

      <div class="chat-main">
        <div class="messages-container" ref="messagesContainer">
          <div v-if="!currentSession" class="empty-state">
            <div class="empty-icon">💬</div>
            <h2>开始新对话</h2>
            <p>选择左侧"新对话"开始与AI对话</p>
          </div>
          <div v-else>
            <div
              v-for="(msg, index) in messages"
              :key="index"
              class="message"
              :class="msg.role"
            >
              <div class="message-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
              <div class="message-content">
                <pre>{{ msg.content }}</pre>
              </div>
            </div>
            <div v-if="loading" class="message assistant">
              <div class="message-avatar">🤖</div>
              <div class="message-content loading">
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
              </div>
            </div>
          </div>
        </div>

        <div class="input-area" v-if="currentSession">
          <div class="input-container">
            <textarea
              v-model="inputMessage"
              @keydown.enter.exact.prevent="sendMessage"
              placeholder="输入消息..."
              rows="1"
            ></textarea>
            <button class="btn-send" @click="sendMessage" :disabled="!inputMessage.trim() || loading">
              发送
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { aiApi } from '../../api/ai'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const sessions = ref([])
const currentSession = ref(null)
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)

const loadSessions = async () => {
  try {
    const res = await aiApi.getSessions()
    sessions.value = res.data || []
  } catch (e) {
    console.error('Failed to load sessions:', e)
  }
}

const createNewChat = async () => {
  try {
    const res = await aiApi.createSession()
    currentSession.value = res.data
    messages.value = []
    sessions.value.unshift(res.data)
    router.push(`/ai/chat/${res.data.id}`)
  } catch (e) {
    ElMessage.error('创建对话失败')
  }
}

const selectSession = async (session) => {
  currentSession.value = session
  router.push(`/ai/chat/${session.id}`)
  await loadMessages(session.id)
}

const loadMessages = async (sessionId) => {
  try {
    const res = await aiApi.getMessages(sessionId)
    messages.value = res.data || []
    scrollToBottom()
  } catch (e) {
    console.error('Failed to load messages:', e)
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''
  loading.value = true

  messages.value.push({ role: 'user', content: userMessage })
  scrollToBottom()

  try {
    const res = await aiApi.sendMessage(currentSession.value.id, userMessage)
    messages.value.push({ role: 'assistant', content: res.data })
    scrollToBottom()
  } catch (e) {
    ElMessage.error('发送消息失败')
    messages.value.pop()
  } finally {
    loading.value = false
  }
}

const deleteSession = async (sessionId) => {
  try {
    await aiApi.deleteSession(sessionId)
    sessions.value = sessions.value.filter(s => s.id !== sessionId)
    if (currentSession.value?.id === sessionId) {
      currentSession.value = null
      messages.value = []
      router.push('/ai/chat')
    }
    ElMessage.success('删除成功')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

onMounted(async () => {
  await loadSessions()
  if (route.params.sessionId) {
    const session = sessions.value.find(s => s.id === Number(route.params.sessionId))
    if (session) {
      currentSession.value = session
      await loadMessages(session.id)
    }
  }
})
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}
.chat-container {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 80px 20px 20px;
  height: calc(100vh - 60px);
  gap: 20px;
}
.chat-sidebar {
  width: 280px;
  flex-shrink: 0;
  padding: 20px;
  display: flex;
  flex-direction: column;
}
.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.sidebar-header h3 {
  font-size: 16px;
  color: var(--text-primary);
}
.btn-new-chat {
  background: var(--color-primary);
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 12px;
}
.session-list {
  flex: 1;
  overflow-y: auto;
}
.session-item {
  padding: 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  background: rgba(255,255,255,0.05);
}
.session-item:hover {
  background: rgba(255,255,255,0.1);
}
.session-item.active {
  background: var(--color-primary);
  color: white;
}
.session-title {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.btn-delete {
  background: none;
  border: none;
  color: inherit;
  font-size: 18px;
  cursor: pointer;
  opacity: 0.5;
}
.btn-delete:hover {
  opacity: 1;
}
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: rgba(255,255,255,0.05);
  border-radius: var(--radius-lg);
  overflow: hidden;
}
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-secondary);
}
.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}
.empty-state h2 {
  margin-bottom: 10px;
}
.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.message.user {
  flex-direction: row-reverse;
}
.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255,255,255,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.message.user .message-avatar {
  background: var(--color-primary);
}
.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  background: rgba(255,255,255,0.08);
}
.message.user .message-content {
  background: var(--color-primary);
  color: white;
}
.message-content pre {
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 14px;
  line-height: 1.5;
  font-family: inherit;
  margin: 0;
}
.loading {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
}
.typing-dot {
  width: 8px;
  height: 8px;
  background: var(--text-secondary);
  border-radius: 50%;
  animation: typing 1.4s infinite;
}
.typing-dot:nth-child(2) {
  animation-delay: 0.2s;
}
.typing-dot:nth-child(3) {
  animation-delay: 0.4s;
}
@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}
.input-area {
  padding: 20px;
  background: rgba(255,255,255,0.05);
}
.input-container {
  display: flex;
  gap: 12px;
  background: rgba(255,255,255,0.1);
  border-radius: var(--radius-lg);
  padding: 8px;
}
.input-container textarea {
  flex: 1;
  background: none;
  border: none;
  color: var(--text-primary);
  padding: 8px 12px;
  font-size: 14px;
  resize: none;
  outline: none;
}
.btn-send {
  background: var(--color-primary);
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-weight: 500;
}
.btn-send:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  max-width: 1400px;
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
