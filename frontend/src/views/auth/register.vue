<template>
  <div class="register-page">
    <div class="register-container glass-card">
      <div class="register-header">
        <h1 class="text-gradient">创建账号</h1>
        <p>加入 Everything-Prompt</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱(可选)" size="large" :prefix-icon="Message" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <button type="submit" class="btn-gradient btn-block" @click.prevent="handleRegister">注册</button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        已有账号?
        <router-link to="/login" class="text-gradient">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const form = ref({ username: '', email: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }]
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
    await userStore.register(form.value)
    ElMessage.success('注册成功')
    router.push('/')
  } catch (e) {
    // 验证失败
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(135deg, #F7FAFC 0%, #EDF2F7 100%);
}
.register-container {
  width: 100%;
  max-width: 420px;
  padding: 48px;
}
.register-header {
  text-align: center;
  margin-bottom: 40px;
}
.register-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}
.register-header p {
  color: var(--text-secondary);
}
.register-form {
  margin-bottom: 24px;
}
.btn-block {
  width: 100%;
  height: 48px;
  font-size: 16px;
}
.register-footer {
  text-align: center;
  color: var(--text-secondary);
}
.register-footer a {
  font-weight: 600;
  text-decoration: none;
}
</style>
