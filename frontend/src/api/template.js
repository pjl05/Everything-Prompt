import request from './request'

export const templateApi = {
    getCategories() {
        return request.get('/categories')
    },
    getTemplates(params) {
        return request.get('/templates', { params })
    },
    getTemplateById(id) {
        return request.get(`/templates/${id}`)
    }
}
