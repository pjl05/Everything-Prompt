import { defineStore } from 'pinia'
import { authApi } from '../api/auth'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        userInfo: null
    }),

    getters: {
        isLoggedIn: state => !!state.token,
        isVip: state => state.userInfo?.role === 'VIP',
        isAdmin: state => state.userInfo?.role === 'ADMIN'
    },

    actions: {
        async login(credentials) {
            const result = await authApi.login(credentials)
            this.token = result.token
            this.userInfo = result.user
            localStorage.setItem('token', this.token)
            return result
        },

        async register(data) {
            const result = await authApi.register(data)
            this.token = result.token
            this.userInfo = result.user
            localStorage.setItem('token', this.token)
            return result
        },

        async fetchUserInfo() {
            if (!this.token) return
            try {
                this.userInfo = await authApi.getCurrentUser()
            } catch (e) {
                this.logout()
            }
        },

        logout() {
            this.token = ''
            this.userInfo = null
            localStorage.removeItem('token')
        }
    }
})
