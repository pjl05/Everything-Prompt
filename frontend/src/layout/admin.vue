<template>
  <div class="admin-layout">
    <aside class="admin-sidebar glass-card">
      <div class="sidebar-header">
        <span class="logo-text text-gradient">管理后台</span>
      </div>
      <el-menu :default-active="activeMenu" router class="admin-menu">
        <el-menu-item index="/admin">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/templates">
          <el-icon><Document /></el-icon>
          <span>模板管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <div class="admin-main">
      <header class="admin-header glass-card">
        <div class="header-left">
          <h2>{{ pageTitle }}</h2>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar || '/default-avatar.png'" />
              <span>{{ userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/')">返回前台</el-dropdown-item>
                <el-dropdown-item @click="userStore.logout()">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { DataAnalysis, Document, Folder, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => {
  const map = {
    '/admin': '数据概览',
    '/admin/templates': '模板管理',
    '/admin/categories': '分类管理',
    '/admin/users': '用户管理'
  }
  return map[route.path] || '管理后台'
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #F7FAFC 0%, #EDF2F7 100%);
}

.admin-sidebar {
  width: 240px;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  padding: 20px 0;
  border-radius: 0;
  border-right: 1px solid rgba(0,0,0,0.1);
}

.sidebar-header {
  padding: 0 20px 20px;
  border-bottom: 1px solid rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
}

.admin-menu {
  border: none;
}

.admin-main {
  flex: 1;
  margin-left: 240px;
}

.admin-header {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 0;
  border-bottom: 1px solid rgba(0,0,0,0.1);
}

.header-left h2 {
  font-size: 20px;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.admin-content {
  padding: 24px;
}
</style>
