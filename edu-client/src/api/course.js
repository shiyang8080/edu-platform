import request from './request'

export function getCourseList(params) {
  return request({ url: '/courses', method: 'get', params })
}

export function getCourseDetail(id) {
  return request({ url: '/courses/' + id, method: 'get' })
}

export function createCourse(data) {
  return request({ url: '/courses', method: 'post', data })
}

export function updateCourse(id, data) {
  return request({ url: '/courses/' + id, method: 'put', data })
}

export function deleteCourse(id) {
  return request({ url: '/courses/' + id, method: 'delete' })
}

export function getMyTeachCourses(params) {
  return request({ url: '/courses/my-teach', method: 'get', params })
}
