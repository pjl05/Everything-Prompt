import request from './request'

export const aiApi = {
  optimize(content) {
    return request.post('/ai/optimize', { content })
  },
  batchGenerate(requirement, count) {
    return request.post('/ai/batch', { requirement, count })
  },
  createSession(templateId) {
    return request.post('/chat/session', { templateId })
  },
  getSessions() {
    return request.get('/chat/sessions')
  },
  getMessages(sessionId) {
    return request.get(`/chat/sessions/${sessionId}/messages`)
  },
  sendMessage(sessionId, message) {
    return request.post(`/chat/sessions/${sessionId}/send`, { message })
  },
  deleteSession(sessionId) {
    return request.delete(`/chat/sessions/${sessionId}`)
  }
}
