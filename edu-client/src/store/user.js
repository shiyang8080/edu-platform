import { defineStore } from 'pinia'
import { getToken, setToken, setUser, getUser, removeToken, removeUser } from '@/utils/auth'
import { login as loginApi, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUser() || null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    role: (state) => state.userInfo?.role || '',
    userName: (state) => state.userInfo?.realName || state.userInfo?.username || '',
    userId: (state) => state.userInfo?.id || ''
  },

  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      const data = res.data
      this.token = data.token
      this.userInfo = {
        id: data.userId,
        username: data.username,
        realName: data.realName,
        role: data.role
      }
      setToken(data.token)
      setUser(this.userInfo)
      return data
    },

    async fetchUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
      setUser(res.data)
    },

    logout() {
      this.token = ''
      this.userInfo = null
      removeToken()
      removeUser()
    }
  }
})
