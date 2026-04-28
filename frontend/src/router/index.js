import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    { path: '/', component: () => import('../views/home/index.vue') },
    { path: '/templates', component: () => import('../views/template/list.vue') },
    { path: '/templates/:id', component: () => import('../views/template/detail.vue') },
    { path: '/generator', component: () => import('../views/generator/index.vue') },
    { path: '/login', component: () => import('../views/auth/login.vue') },
    { path: '/register', component: () => import('../views/auth/register.vue') },
    { path: '/vip/templates', component: () => import('../views/vip/templates.vue') },
    { path: '/word/list', component: () => import('../views/word/list.vue') },
    { path: '/favorite/list', component: () => import('../views/favorite/list.vue') },
    { path: '/batch', component: () => import('../views/batch/index.vue') },
    { path: '/ai/chat', component: () => import('../views/ai/chat.vue') },
    { path: '/ai/chat/:sessionId', component: () => import('../views/ai/chat.vue') },
    { path: '/admin', component: () => import('../layout/admin.vue'), meta: { requiresAdmin: true },
      children: [
        { path: '', component: () => import('../views/admin/dashboard.vue') },
        { path: 'templates', component: () => import('../views/admin/templates.vue') },
        { path: 'categories', component: () => import('../views/admin/categories.vue') },
        { path: 'users', component: () => import('../views/admin/users.vue') }
      ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
