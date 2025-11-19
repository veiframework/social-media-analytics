// 上传功能使用示例
import { uploadFile, uploadImage, chooseAndUploadImage } from '../api/upload.js';
import { selectAndUploadAvatar, selectAndUploadCompetitionImages } from './upload.js';

/**
 * 示例1：简单的图片上传
 */
export const exampleSimpleImageUpload = async () => {
  try {
    const result = await chooseAndUploadImage({
      count: 1,
      uploadOptions: {
        module: 'user',
        type: 'image'
      }
    });
    
    console.log('上传结果:', result);
    return result;
  } catch (error) {
    console.error('上传失败:', error);
    throw error;
  }
};

/**
 * 示例2：批量上传参赛照片
 */
export const exampleBatchUploadImages = async () => {
  try {
    const results = await selectAndUploadCompetitionImages({
      maxCount: 6
    });
    
    console.log('批量上传结果:', results);
    return results;
  } catch (error) {
    console.error('批量上传失败:', error);
    throw error;
  }
};

/**
 * 示例3：上传用户头像
 */
export const exampleUploadAvatar = async () => {
  try {
    const result = await selectAndUploadAvatar();
    
    const avatarUrl = result.data?.url || result.url;
    console.log('头像上传成功，URL:', avatarUrl);
    
    return avatarUrl;
  } catch (error) {
    console.error('头像上传失败:', error);
    throw error;
  }
};

/**
 * 示例4：自定义上传配置
 */
export const exampleCustomUpload = async (filePath) => {
  try {
    const result = await uploadFile(filePath, {
      name: 'customFile',
      type: 'document',
      module: 'competition',
      formData: {
        category: 'registration_document',
        description: '报名资料'
      },
      loadingText: '上传文档中...'
    });
    
    console.log('自定义上传结果:', result);
    return result;
  } catch (error) {
    console.error('自定义上传失败:', error);
    throw error;
  }
};