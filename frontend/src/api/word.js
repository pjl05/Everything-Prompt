import request from './request'

export const wordApi = {
  getMyWords() {
    return request.get('/words')
  },
  addWord(data) {
    return request.post('/words', data)
  },
  updateWord(id, data) {
    return request.put(`/words/${id}`, data)
  },
  deleteWord(id) {
    return request.delete(`/words/${id}`)
  }
}
