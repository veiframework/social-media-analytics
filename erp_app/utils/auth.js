// token存储的键名
const TokenKey = 'Admin-Token'
const ExpiresInKey = 'Admin-Expires-In'

// 获取token
export function getToken() {
  return uni.getStorageSync(TokenKey)
}

// 设置token
export function setToken(token) {
  return uni.setStorageSync(TokenKey, token)
}

// 移除token
export function removeToken() {
  return uni.removeStorageSync(TokenKey)
}

// 获取过期时间
export function getExpiresIn() {
  return uni.getStorageSync(ExpiresInKey) || -1
}

// 设置过期时间
export function setExpiresIn(time) {
  return uni.setStorageSync(ExpiresInKey, time)
}

// 移除过期时间
export function removeExpiresIn() {
  return uni.removeStorageSync(ExpiresInKey)
}