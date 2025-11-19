// 认证相关API接口
import { post, get } from '../utils/request.js';

/**
 * 微信小程序登录
 * @param {Object} params 登录参数
 * @param {String} params.wxMaAppId 微信小程序AppId
 * @param {String} params.wxMaCode 微信授权码
 * @returns {Promise}
 */
export const wechatLogin = (params) => {
  return post('/auth-api/login/wechat', params, {
    showLoading: false, // 登录时不显示loading，避免影响启动体验
    addToken:false
});
};

/**
 * 获取微信授权码
 * @returns {Promise}
 */
export const getWechatCode = () => {
  return new Promise((resolve, reject) => {
    // #ifdef MP-WEIXIN
    uni.login({
      provider: 'weixin',
      success: (res) => {
        if (res.code) {
          resolve(res.code);
        } else {
          reject(new Error('获取微信授权码失败'));
        }
      },
      fail: (error) => {
        reject(error);
      }
    });
    // #endif
    
    // #ifndef MP-WEIXIN
    // 非微信小程序环境，返回测试code
    resolve('123123');
    // #endif
  });
};

/**
 * 检查登录状态
 * @returns {Boolean}
 */
export const checkLoginStatus = () => {
  const token = uni.getStorageSync('token');
  return !!token;
};

/**
 * 获取协议列表
 * @returns {Promise}
 */
export const getAgreementList = () => {
  return get('/admin-api/agreement/app/list', {}, {
    showLoading: false // 在登录流程中不显示loading
  });
};

/**
 * 清除登录信息
 */
export const clearLoginInfo = () => {
  uni.removeStorageSync('token');
  uni.removeStorageSync('userInfo');
};