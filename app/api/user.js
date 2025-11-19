// 用户相关API接口
import { get, post, put, del } from '../utils/request.js';

const prefix = '/admin-api';

/**
 * 获取用户信息
 * @returns {Promise}
 */
export const getUserInfo = () => {
  return get(prefix + '/app-user/api/info');
};

/**
 * 更新用户信息
 * @param {Object} data 用户信息
 * @returns {Promise}
 */
export const updateUserInfo = (data) => {
  return put(prefix + '/app-user/api/info', data);
};

/**
 * 获取用户赛事订单列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getUserRegistrations = (params = {}) => {
  return get(prefix + '/app/user/registration', {
    pageNum: 1,
    pageSize: 10,
    ...params
  });
};

/**
 * 获取用户报名信息列表
 * @returns {Promise}
 */
export const getRegistrationInfoList = () => {
  return get(prefix + '/app/user/registration/info');
};

/**
 * 获取用户报名信息
 * @returns {Promise}
 */
export const getUserRegistrationInfo = () => {
  return get(prefix + '/app/user/registration/info');
};

/**
 * 添加用户报名信息
 * @param {Object} data 报名信息
 * @returns {Promise}
 */
export const addRegistrationInfo = (data) => {
  return post(prefix + '/app/user/registration/info', data);
};

/**
 * 修改用户报名信息
 * @param {String} id 报名信息ID
 * @param {Object} data 报名信息
 * @returns {Promise}
 */
export const updateRegistrationInfo = (id, data) => {
  return put(prefix + '/app/user/registration/info/'+id, data);
};

/**
 * 删除用户报名信息
 * @param {String} ids 报名信息IDs
 * @returns {Promise}
 */
export const deleteRegistrationInfo = (ids) => {
  return del(prefix + `/app/user/registration/info/${ids}`);
};

/**
 * 设置默认报名信息
 * @param {String} id 报名信息ID
 * @returns {Promise}
 */
export const setDefaultRegistrationInfo = (id) => {
  return put(prefix + `/app/user/${id}/default`);
};

/**
 * 获取用户积分记录
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getUserScoreRecords = (params = {}) => {
  return get(prefix + '/app/user/score', {
    pageNum: 1,
    pageSize: 10,
    ...params
  });
};

/**
 * 获取用户参赛照片
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getUserCompetitionImages = (params = {}) => {
  return get(prefix + '/app/user/personal/images', {
    pageNum: 1,
    pageSize: 20,
    imageType: 'PERSONAL',
    status: 'ACTIVE',
    ...params
  });
};

/**
 * 获取赛事详情
 * @param {String} id 赛事ID
 * @returns {Promise}
 */
export const getCompetitionDetail = (id) => {
  return get(prefix + `/app/user/competition/${id}`);
};

/**
 * 获取赛事排行榜
 * @param {String} competitionId 赛事ID
 * @returns {Promise}
 */
export const getCompetitionRanking = (competitionId) => {
  return get(prefix + `/app/user/competition/ranking/${competitionId}`);
};

/**
 * 获取订单详情
 * @param {String} id 订单ID
 * @returns {Promise}
 */
export const getRegistrationDetail = (id) => {
  return get(prefix + `/app/user/registration/${id}`);
};

/**
 * 获取赛事分组列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getCompetitionGroups = (params = {}) => {
  return get(prefix + '/app/user/competition/group', params);
};

/**
 * 获取赛事分组详情
 * @param {String} groupId 分组ID
 * @returns {Promise}
 */
export const getCompetitionGroupDetail = (groupId) => {
  return get(prefix + `/app/user/competition/group/${groupId}`);
};

/**
 * 提交赛事报名
 * @param {Object} data 报名数据
 * @returns {Promise}
 */
export const submitCompetitionRegistration = (data) => {
  return post(prefix + '/app/user/competition/registration', data);
};

/**
 * 微信退款
 * @param {Object} data 退款数据
 * @returns {Promise}
 */
export const wechatRefund = (data) => {
  return post(prefix + '/payment/refund/wechat', data);
};