import request from './request'

export function getMySelections() {
  return request({ url: '/selections/my', method: 'get' })
}

export function selectCourse(courseId) {
  return request({ url: '/selections/select/' + courseId, method: 'post' })
}

export function dropCourse(courseId) {
  return request({ url: '/selections/drop/' + courseId, method: 'delete' })
}

export function getCourseStudents(courseId) {
  return request({ url: '/selections/course/' + courseId + '/students', method: 'get' })
}
