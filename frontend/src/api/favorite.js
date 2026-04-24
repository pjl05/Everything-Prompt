import request from './request'

export const favoriteApi = {
  getMyFavorites() {
    return request.get('/favorites')
  },
  addFavorite(templateId) {
    return request.post(`/favorites/${templateId}`)
  },
  removeFavorite(templateId) {
    return request.delete(`/favorites/${templateId}`)
  }
}
