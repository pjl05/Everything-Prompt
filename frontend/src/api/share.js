import request from './request'

export const shareApi = {
  createShare(data) {
    return request.post('/share', data)
  },
  getShareList(categoryId, page, size) {
    return request.get('/share/list', { categoryId, page, size })
  },
  getShareById(id) {
    return request.get(`/share/${id}`)
  },
  getMyShares(page, size) {
    return request.get('/share/my', { page, size })
  },
  toggleLike(id) {
    return request.post(`/share/${id}/like`)
  },
  getLikeStatus(id) {
    return request.get(`/share/${id}/like/status`)
  },
  deleteShare(id) {
    return request.delete(`/share/${id}`)
  }
}
