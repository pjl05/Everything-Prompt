import request from './request'

export const aiApi = {
  optimize(content) {
    return request.post('/ai/optimize', { content })
  },
  batchGenerate(requirement, count) {
    return request.post('/ai/batch', { requirement, count })
  }
}
