import request from './request'

export function getNoticeList(params) {
  return request({ url: '/notices', method: 'get', params })
}

export function getNoticeDetail(id) {
  return request({ url: '/notices/' + id, method: 'get' })
}

export function createNotice(data) {
  return request({ url: '/notices', method: 'post', data })
}

export function updateNotice(id, data) {
  return request({ url: '/notices/' + id, method: 'put', data })
}

export function deleteNotice(id) {
  return request({ url: '/notices/' + id, method: 'delete' })
}

export function getUnreadCount() {
  return request({ url: '/notices/unread-count', method: 'get' })
}
