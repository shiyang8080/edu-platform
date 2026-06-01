const TOKEN_KEY = 'edu_token'
const USER_KEY = 'edu_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUser() {
  const user = localStorage.getItem(USER_KEY)
  return user ? JSON.parse(user) : null
}

export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function removeUser() {
  localStorage.removeItem(USER_KEY)
}

export function getRole() {
  const user = getUser()
  return user ? user.role : null
}

export function isLoggedIn() {
  return !!getToken()
}

export function logout() {
  removeToken()
  removeUser()
}
