// 认证状态管理工具
import { clearLoginInfo } from '../api/auth.js';

/**
 * 全局认证状态管理
 */
class AuthManager {
  constructor() {
    this.isLoggedIn = false;
    this.userInfo = null;
    this.token = null;
    this.init();
  }

  /**
   * 初始化认证状态
   */
  init() {
    this.token = uni.getStorageSync('token');
    this.userInfo = uni.getStorageSync('userInfo');
    this.isLoggedIn = !!this.token;
  }

  /**
   * 设置登录状态
   * @param {String} token 
   * @param {Object} userInfo 
   */
  setLoginState(token, userInfo = null) {
    this.token = token;
    this.userInfo = userInfo;
    this.isLoggedIn = true;

    // 保存到本地存储
    uni.setStorageSync('token', token);
    if (userInfo) {
      uni.setStorageSync('userInfo', userInfo);
    }

    // 更新全局状态
    this.updateGlobalData();
  }

  /**
   * 清除登录状态
   */
  clearLoginState() {
    this.token = null;
    this.userInfo = null;
    this.isLoggedIn = false;

    // 清除本地存储
    clearLoginInfo();

    // 更新全局状态
    this.updateGlobalData();
  }

  /**
   * 获取token
   */
  getToken() {
    return this.token || uni.getStorageSync('token');
  }

  /**
   * 获取用户信息
   */
  getUserInfo() {
    return this.userInfo || uni.getStorageSync('userInfo');
  }

  /**
   * 检查是否已登录
   */
  checkLoginStatus() {
    const token = this.getToken();
    this.isLoggedIn = !!token;
    return this.isLoggedIn;
  }

  /**
   * 更新全局数据
   */
  updateGlobalData() {
    const app = getApp();
    if (app) {
      app.globalData = app.globalData || {};
      app.globalData.isLoggedIn = this.isLoggedIn;
      app.globalData.userInfo = this.userInfo;
      app.globalData.token = this.token;
    }
  }

  /**
   * 处理登录过期
   */
  handleTokenExpired() {
    console.log('Token已过期，清除登录状态');
    this.clearLoginState();
    
    // 可以在这里添加重新登录的逻辑
    // 或者跳转到登录页面
    
    return false;
  }
}

// 创建全局实例
const authManager = new AuthManager();

export default authManager;