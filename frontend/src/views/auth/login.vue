<template>
  <div class="login-page">
    <div class="login-container glass-card">
      <div class="login-header">
        <h1 class="text-gradient">欢迎回来</h1>
        <p>登录到 Everything-Prompt</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <button type="submit" class="btn-gradient btn-block" @click.prevent="handleLogin">登录</button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        还没有账号?
        <router-link to="/register" class="text-gradient">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const form = ref({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    await userStore.login(form.value)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 验证失败
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(135deg, #F7FAFC 0%, #EDF2F7 100%);
}
.login-container {
  width: 100%;
  max-width: 420px;
  padding: 48px;
}
.login-header {
  text-align: center;
  margin-bottom: 40px;
}
.login-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}
.login-header p {
  color: var(--text-secondary);
}
.login-form {
  margin-bottom: 24px;
}
.btn-block {
  width: 100%;
  height: 48px;
  font-size: 16px;
}
.login-footer {
  text-align: center;
  color: var(--text-secondary);
}
.login-footer a {
  font-weight: 600;
  text-decoration: none;
}
</style>
