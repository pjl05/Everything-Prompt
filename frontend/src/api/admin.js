import request from './request'

export const adminApi = {
  // Dashboard
  getDashboard() {
    return request.get('/admin/stats/dashboard')
  },
  
  // Templates
  getTemplates(params) {
    return request.get('/admin/templates', { params })
  },
  createTemplate(data) {
    return request.post('/admin/templates', data)
  },
  updateTemplate(id, data) {
    return request.put(`/admin/templates/${id}`, data)
  },
  deleteTemplate(id) {
    return request.delete(`/admin/templates/${id}`)
  },
  updateTemplateStatus(id, status) {
    return request.put(`/admin/templates/${id}/status`, null, { params: { status } })
  },
  
  // Categories
  getCategories() {
    return request.get('/admin/categories')
  },
  createCategory(data) {
    return request.post('/admin/categories', data)
  },
  updateCategory(id, data) {
    return request.put(`/admin/categories/${id}`, data)
  },
  deleteCategory(id) {
    return request.delete(`/admin/categories/${id}`)
  },
  updateCategorySort(id, sortOrder) {
    return request.put(`/admin/categories/${id}/sort`, null, { params: { sortOrder } })
  },
  
  // Users
  getUsers(params) {
    return request.get('/admin/users', { params })
  },
  getUserDetail(id) {
    return request.get(`/admin/users/${id}`)
  },
  updateUserStatus(id, status) {
    return request.put(`/admin/users/${id}/status`, null, { params: { status } })
  },
  grantVip(id, days) {
    return request.put(`/admin/users/${id}/vip`, null, { params: { days } })
  }
}
