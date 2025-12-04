import { getToken, setToken, removeToken } from './auth.js'

// 全局状态管理
class Store {
  constructor() {
    this.state = {
      token: getToken(),
      userInfo: null,
      roles: [],
      permissions: []
    }
  }

  // 设置token
  setToken(token) {
    this.state.token = token
    setToken(token)
  }

  // 获取token
  getToken() {
    return this.state.token
  }

  // 清除token
  clearToken() {
    this.state.token = null
    removeToken()
  }

  // 设置用户信息
  setUserInfo(userInfo) {
    this.state.userInfo = userInfo
    // 存储到本地，以便下次启动时恢复
    uni.setStorageSync('userInfo', userInfo)
  }

  // 获取用户信息
  getUserInfo() {
    if (!this.state.userInfo) {
      // 从本地存储恢复
      const userInfo = uni.getStorageSync('userInfo')
      if (userInfo) {
        this.state.userInfo = userInfo
      }
    }
    return this.state.userInfo
  }

  // 设置角色和权限
  setRolesAndPermissions(roles, permissions) {
    this.state.roles = roles || []
    this.state.permissions = permissions || []
    uni.setStorageSync('roles', roles)
    uni.setStorageSync('permissions', permissions)
  }

  // 获取角色
  getRoles() {
    if (!this.state.roles.length) {
      const roles = uni.getStorageSync('roles')
      if (roles) {
        this.state.roles = roles
      }
    }
    return this.state.roles
  }

  // 获取权限
  getPermissions() {
    if (!this.state.permissions.length) {
      const permissions = uni.getStorageSync('permissions')
      if (permissions) {
        this.state.permissions = permissions
      }
    }
    return this.state.permissions
  }

  // 检查是否有权限
  hasPermission(permission) {
    return this.state.permissions.includes(permission)
  }

  // 检查是否有角色
  hasRole(role) {
    return this.state.roles.includes(role)
  }

  // 登出，清除所有状态
  logout() {
    this.state = {
      token: null,
      userInfo: null,
      roles: [],
      permissions: []
    }
    // 清除本地存储
    removeToken()
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('roles')
    uni.removeStorageSync('permissions')
  }

  // 初始化
  init() {
    // 从本地存储恢复状态
    this.state.token = getToken()
    this.state.userInfo = uni.getStorageSync('userInfo') || null
    this.state.roles = uni.getStorageSync('roles') || []
    this.state.permissions = uni.getStorageSync('permissions') || []
  }
}

// 导出单例
export default new Store()