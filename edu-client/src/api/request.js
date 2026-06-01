import axios from 'axios'
import { getToken, logout } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// Request interceptor - add JWT token
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

// Response interceptor - handle errors
request.interceptors.response.use(
  response => {
    // 跳过 blob 等非 JSON 响应
    if (response.config.responseType === 'blob' || response.config.responseType === 'arraybuffer') {
      return response.data
    }
    const res = response.data
    if (res && typeof res === 'object' && res.code === 200) {
      return res
    }
    if (res && typeof res === 'object') {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 403) {
        logout()
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        logout()
        window.location.href = '/login'
      }
      ElMessage.error(error.response.data?.message || '服务器错误')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
