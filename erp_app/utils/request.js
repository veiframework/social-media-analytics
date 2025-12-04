// 统一请求封装
import {getApiConfig} from './config.js';
import store  from "./store";
const apiConfig = getApiConfig();
const BASE_URL = apiConfig.baseURL;

// 请求拦截器
const request = (options) => {
    return new Promise((resolve, reject) => {
        // 显示加载提示
        if (options.showLoading !== false) {
            uni.showLoading({
                title: '加载中...',
                mask: true,
                duration: 1000
            });
        }

        // 获取token（如果有的话）
        const token = store.getToken();
        let addToken = options.addToken == null ? true : options.addToken;
        uni.request({
            url: BASE_URL + options.url,
            method: options.method || 'GET',
            data: options.data || {},
            header: {
                'Content-Type': 'application/json',
                ...(addToken ? {
                    'Authorization': token ? `Bearer ${token}` : ''
                } : {}),
                ...options.header
            },
            success: (res) => {
                uni.hideLoading();
                if (res.statusCode === 200) {
                    // 根据后端返回结构调整
                    if (res.data.code === 200) {
                        resolve(res.data.data || res.data);
                    } else if (res.data.code === 401) {
                        // 认证失败，清除本地token并提示重新登录
                        uni.removeStorageSync('token');
                        uni.removeStorageSync('userInfo');

                        if (options.showError !== false) {
                            uni.showToast({
                                title: '登录已过期，请重新登录',
                                icon: 'none',
                                duration: 2000
                            });
                        }

                        // 可以在这里触发重新登录逻辑
                        // 或者跳转到登录页面
                        reject({code: 401, message: '登录已过期'});
                    } else {
                        // 业务错误
                        if (options.showError !== false) {
                            uni.showToast({
                                title: res.data.msg || res.data.message || '请求失败',
                                icon: 'none',
                                duration: 2000,
                                success: res => {
                                    reject(res.data);
                                }
                            });
                        } else {
                            reject(res.data);

                        }
                    }
                } else if (res.statusCode === 401) {
                    // 认证失败，清除本地token并提示重新登录
                    uni.removeStorageSync('token');
                    uni.removeStorageSync('userInfo');

                    if (options.showError !== false) {
                        uni.showToast({
                            title: '登录已过期，请重新登录',
                            icon: 'none',
                            duration: 2000
                        });
                    }
                    uni.navigateTo({
                        url: '/pages/login/login',
                    })
                    // 可以在这里触发重新登录逻辑
                    // 或者跳转到登录页面
                    reject({code: 401, message: '登录已过期'});
                } else {
                    // 其他HTTP错误
                    if (options.showError !== false) {
                        uni.showToast({
                            title: `请求失败: ${res.statusCode}`,
                            icon: 'none',
                            duration: 2000
                        });
                    }
                    reject(res);
                }
            },
            fail: (error) => {
                uni.hideLoading();

                if (options.showError !== false) {
                    uni.showToast({
                        title: '网络连接失败',
                        icon: 'none',
                        duration: 2000
                    });
                }
                reject(error);
            }
        });
    });
};

// GET请求
export const get = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'GET',
        data,
        ...options
    });
};

// POST请求
export const post = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'POST',
        data,
        ...options
    });
};

// PUT请求
export const put = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'PUT',
        data,
        ...options
    });
};

// DELETE请求
export const del = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'DELETE',
        data,
        ...options
    });
};

export default request;