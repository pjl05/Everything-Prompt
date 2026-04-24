import request from './request'

export const vipApi = {
  getStatus() {
    return request.get('/vip/status')
  },
  upgrade(days) {
    return request.post('/vip/upgrade', { days })
  }
}
