import request from './request'

export const authApi = {
    register(data) {
        return request.post('/auth/register', data)
    },
    login(data) {
        return request.post('/auth/login', data)
    },
    logout() {
        return request.post('/auth/logout')
    },
    getCurrentUser() {
        return request.get('/auth/me')
    }
}
