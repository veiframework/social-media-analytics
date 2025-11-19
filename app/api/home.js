// 首页相关API接口
import { get } from '../utils/request.js';

const prefix = '/admin-api';

/**
 * 获取首页轮播图
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getBannerList = (params = {}) => {
  return get(prefix+'/app/user/banner', {
    status: '1', // 只获取启用状态的轮播图
    ...params
  });
};

/**
 * 获取赛事列表（分页）
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getCompetitionList = (params = {}) => {
  return get(prefix+'/app/user/competition', {
    pageNum: 1,
    pageSize: 10,
    enabled: true, // 只获取启用的赛事
    ...params
  });
};

/**
 * 获取赛事详情
 * @param {String} id 赛事ID
 * @returns {Promise}
 */
export const getCompetitionDetail = (id) => {
  return get(prefix+`/admin/competition/${id}`);
};

/**
 * 获取赛事分组列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getCompetitionGroups = (params = {}) => {
  return get(prefix+'/app/user/competition/group', params);
};