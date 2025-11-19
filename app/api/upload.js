// 文件上传相关API接口
import { getApiConfig } from '../config/index.js';

const apiConfig = getApiConfig();
const BASE_URL = apiConfig.baseURL;
const prefix = '/admin-api';

/**
 * 上传单个文件
 * @param {String} filePath 文件本地路径
 * @param {Object} options 上传选项
 * @returns {Promise}
 */
export const uploadFile = (filePath, options = {}) => {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = uni.getStorageSync('token');
    
    // 显示上传进度
    if (options.showLoading !== false) {
      uni.showLoading({
        title: options.loadingText || '上传中...',
        mask: true
      });
    }

    uni.uploadFile({
      url: BASE_URL + prefix + '/upload',
      filePath: filePath,
      name: 'file', // 后端接收的参数名，确保与后端期望的字段名一致
      formData: {
        // 不再添加额外的formData，让后端直接接收file对象
        ...options.formData // 只传递用户指定的额外数据
      },
      header: {
        'Authorization': token ? `Bearer ${token}` : '',
        // 移除Content-Type，让uni-app自动设置正确的multipart/form-data
        ...options.header
      },
      success: (res) => {
        uni.hideLoading();
        
        try {
          // 尝试解析JSON响应
          let data;
          if (typeof res.data === 'string') {
            data = JSON.parse(res.data);
          } else {
            data = res.data;
          }
          
          if (data.code === 200 || data.success || res.statusCode === 200) {
            console.log('文件上传成功:', data);
            // 返回完整的响应数据
            resolve(data);
          } else {
            throw new Error(data.message || data.msg || '上传失败');
          }
        } catch (parseError) {
          console.error('解析上传响应失败:', parseError, 'Raw response:', res.data);
          // 如果解析失败，但状态码是200，可能是直接返回URL
          if (res.statusCode === 200) {
            resolve(res.data);
          } else {
            reject(new Error('上传响应格式错误'));
          }
        }
      },
      fail: (error) => {
        uni.hideLoading();
        console.error('文件上传失败:', error);
        
        let errorMessage = '上传失败';
        if (error.errMsg) {
          if (error.errMsg.includes('fail timeout')) {
            errorMessage = '上传超时，请重试';
          } else if (error.errMsg.includes('fail abort')) {
            errorMessage = '上传被取消';
          } else if (error.errMsg.includes('fail')) {
            errorMessage = '网络异常，请检查网络连接';
          }
        }
        
        reject(new Error(errorMessage));
      }
    });
  });
};

/**
 * 上传图片（专用于图片上传）
 * @param {String} filePath 图片本地路径
 * @param {Object} options 上传选项
 * @returns {Promise}
 */
export const uploadImage = (filePath, options = {}) => {
  return uploadFile(filePath, {
    type: 'image',
    name: 'image',
    loadingText: '上传图片中...',
    ...options
  });
};

/**
 * 上传用户头像
 * @param {String} filePath 头像文件路径
 * @returns {Promise}
 */
export const uploadAvatar = (filePath) => {
  return uploadImage(filePath, {
    module: 'user',
    formData: {
      category: 'avatar'
    },
    loadingText: '上传头像中...'
  });
};

/**
 * 上传多个文件
 * @param {Array} filePaths 文件路径数组
 * @param {Object} options 上传选项
 * @returns {Promise}
 */
export const uploadMultipleFiles = async (filePaths, options = {}) => {
  const results = [];
  const errors = [];
  
  try {
    uni.showLoading({
      title: `上传中... (0/${filePaths.length})`,
      mask: true
    });

    for (let i = 0; i < filePaths.length; i++) {
      try {
        // 更新进度提示
        uni.showLoading({
          title: `上传中... (${i + 1}/${filePaths.length})`,
          mask: true
        });

        const result = await uploadFile(filePaths[i], {
          ...options,
          showLoading: false // 由外层控制loading
        });
        
        results.push(result);
      } catch (error) {
        errors.push({
          index: i,
          filePath: filePaths[i],
          error: error
        });
      }
    }

    uni.hideLoading();

    if (errors.length > 0) {
      console.warn('部分文件上传失败:', errors);
      uni.showToast({
        title: `${results.length}个文件上传成功，${errors.length}个失败`,
        icon: 'none'
      });
    }

    return {
      success: results,
      errors: errors,
      total: filePaths.length
    };
  } catch (error) {
    uni.hideLoading();
    throw error;
  }
};

/**
 * 选择并上传图片
 * @param {Object} options 选择和上传选项
 * @returns {Promise}
 */
export const chooseAndUploadImage = (options = {}) => {
  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count: options.count || 1,
      sizeType: options.sizeType || ['compressed'],
      sourceType: options.sourceType || ['album', 'camera'],
      success: async (res) => {
        try {
          if (options.count === 1) {
            // 单张图片上传
            const result = await uploadImage(res.tempFilePaths[0], options.uploadOptions);
            resolve(result);
          } else {
            // 多张图片上传
            const result = await uploadMultipleFiles(res.tempFilePaths, options.uploadOptions);
            resolve(result);
          }
        } catch (error) {
          reject(error);
        }
      },
      fail: (error) => {
        console.error('选择图片失败:', error);
        reject(new Error('选择图片失败'));
      }
    });
  });
};