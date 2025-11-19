// 上传工具函数
import { uploadFile, uploadImage, uploadAvatar } from '../api/upload.js';

/**
 * 图片压缩配置
 */
const IMAGE_COMPRESS_CONFIG = {
  quality: 80,
  maxWidth: 1080,
  maxHeight: 1080
};

/**
 * 文件大小限制（单位：MB）
 */
const FILE_SIZE_LIMITS = {
  image: 5,    // 图片最大5MB
  video: 50,   // 视频最大50MB
  document: 10 // 文档最大10MB
};

/**
 * 检查文件大小
 * @param {String} filePath 文件路径
 * @param {String} fileType 文件类型
 * @returns {Promise<boolean>}
 */
export const checkFileSize = (filePath, fileType = 'image') => {
  return new Promise((resolve, reject) => {
    uni.getFileInfo({
      filePath: filePath,
      success: (res) => {
        const fileSizeMB = res.size / (1024 * 1024);
        const limit = FILE_SIZE_LIMITS[fileType] || 5;
        
        if (fileSizeMB > limit) {
          reject(new Error(`文件大小不能超过${limit}MB`));
        } else {
          resolve(true);
        }
      },
      fail: (error) => {
        console.error('获取文件信息失败:', error);
        resolve(true); // 如果获取失败，允许上传
      }
    });
  });
};

/**
 * 选择并上传头像（带尺寸检查）
 * @param {Object} options 选项
 * @returns {Promise}
 */
export const selectAndUploadAvatar = async (options = {}) => {
  try {
    // 选择图片
    const chooseResult = await new Promise((resolve, reject) => {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: resolve,
        fail: reject
      });
    });

    const filePath = chooseResult.tempFilePaths[0];
    
    // 检查文件大小
    await checkFileSize(filePath, 'image');
    
    // 上传头像 - 使用简化的formData
    const uploadResult = await uploadFile(filePath, {
      name: 'file',
      loadingText: '上传头像中...',
      formData: {
        // 只传递必要的业务参数，让后端直接接收file对象
        ...options.formData
      }
    });
    
    return uploadResult;
  } catch (error) {
    console.error('选择上传头像失败:', error);
    throw error;
  }
};

/**
 * 选择并上传参赛照片
 * @param {Object} options 选项
 * @returns {Promise}
 */
export const selectAndUploadCompetitionImages = async (options = {}) => {
  try {
    const maxCount = options.maxCount || 9;
    
    // 选择图片
    const chooseResult = await new Promise((resolve, reject) => {
      uni.chooseImage({
        count: maxCount,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: resolve,
        fail: reject
      });
    });

    const filePaths = chooseResult.tempFilePaths;
    
    // 检查每个文件的大小
    for (const filePath of filePaths) {
      await checkFileSize(filePath, 'image');
    }
    
    // 批量上传
    const uploadResults = [];
    for (let i = 0; i < filePaths.length; i++) {
      try {
        uni.showLoading({
          title: `上传中... (${i + 1}/${filePaths.length})`,
          mask: true
        });

        const result = await uploadImage(filePaths[i], {
          module: 'competition',
          formData: {
            category: 'participant_photo'
          },
          showLoading: false
        });
        
        uploadResults.push(result);
      } catch (error) {
        console.error(`第${i + 1}张图片上传失败:`, error);
        // 继续上传其他图片
      }
    }

    uni.hideLoading();
    
    if (uploadResults.length === 0) {
      throw new Error('所有图片上传失败');
    }
    
    if (uploadResults.length < filePaths.length) {
      uni.showToast({
        title: `${uploadResults.length}/${filePaths.length}张图片上传成功`,
        icon: 'none'
      });
    }
    
    return uploadResults;
  } catch (error) {
    uni.hideLoading();
    console.error('选择上传参赛照片失败:', error);
    throw error;
  }
};

/**
 * 获取文件扩展名
 * @param {String} filePath 文件路径
 * @returns {String}
 */
export const getFileExtension = (filePath) => {
  const lastDotIndex = filePath.lastIndexOf('.');
  if (lastDotIndex === -1) return '';
  return filePath.substring(lastDotIndex + 1).toLowerCase();
};

/**
 * 检查文件类型是否支持
 * @param {String} filePath 文件路径
 * @param {Array} allowedTypes 允许的文件类型
 * @returns {Boolean}
 */
export const checkFileType = (filePath, allowedTypes = ['jpg', 'jpeg', 'png', 'gif']) => {
  const extension = getFileExtension(filePath);
  return allowedTypes.includes(extension);
};