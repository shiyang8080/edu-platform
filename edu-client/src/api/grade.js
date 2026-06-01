import request from './request'

export function getMyGrades() {
  return request({ url: '/grades/my', method: 'get' })
}

export function getCourseGrades(courseId) {
  return request({ url: '/grades/course/' + courseId, method: 'get' })
}

export function getCourseStudentGrades(courseId) {
  return request({ url: '/grades/course/' + courseId + '/students', method: 'get' })
}

export function saveGrade(data) {
  return request({ url: '/grades', method: 'post', data })
}

export function updateGrade(id, data) {
  return request({ url: '/grades/' + id, method: 'put', data })
}

export function batchSaveGrades(data) {
  return request({ url: '/grades/batch', method: 'post', data })
}

export function getStatistics(studentId) {
  return request({ url: '/grades/statistics/' + studentId, method: 'get' })
}

export function exportGrades(courseId) {
  return request({ url: '/grades/export/' + courseId, method: 'get', responseType: 'blob' })
}
