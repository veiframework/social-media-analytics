// 应用配置文件
const config = {
  // API配置
  api: {
    baseURL: 'https://cabinet-api-dev.vchaoxi.com', // 根据实际后端地址修改
    timeout: 10000
  },
  
  // 应用信息
  app: {
    name: '赛事小程序',
    version: '1.0.0',
    appId: 'wxb33b804dbe8d870b'
  },

  
  // 页面配置
  pages: {
    // 首页配置
    home: {
      bannerAutoplay: true,
      bannerInterval: 3000,
      competitionPageSize: 10
    }
  },
  
  // 存储键名
  storage: {
    token: 'user_token',
    userInfo: 'user_info'
  },
  servicePhone: '13800138000'
};

// 获取API配置
export const getApiConfig = () => {
  return config.api;
};

// 获取应用配置
export const getAppConfig = () => {
  return config.app;
};

// 获取页面配置
export const getPageConfig = (pageName) => {
  return config.pages[pageName] || {};
};

// 获取存储配置
export const getStorageConfig = () => {
  return config.storage;
};
export const getServicePhone = () => {
  return config.servicePhone;
};
export default config;