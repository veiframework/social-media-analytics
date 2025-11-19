import { get, post } from '../utils/request.js';

const prefix = '/admin-api/mall/app-user';

/**
 * 获取推广收益数据
 * @returns {Promise} 收益数据
 */
export const getPromotionEarnings = () => {
  return get(`${prefix}/commission/income`);
};

/**
 * 获取佣金明细列表
 * @param {Object} params 查询参数 {current: 页码, size: 每页数量}
 * @returns {Promise} 佣金列表
 */
export const getCommissionList = (params) => {
  return get(`${prefix}/commission/records`, params);
};

/**
 * 申请提现
 * @param {Object} data 提现数据 {amount: 提现金额}
 * @returns {Promise} 提现结果
 */
export const withdrawEarnings = (data) => {
  return post(`${prefix}/commission/withdraw`, data);
};

/**
 * 获取推广的用户列表
 * @returns {Promise} 推广用户列表
 */
export const getInvitedUsers = () => {
  return get(`${prefix}/invited/list`);
};

/**
 * 更新邀请人的登录id
 * @param {string} inviteLoginId 邀请人登录ID
 * @returns {Promise} 更新结果
 */
export const updateInviteLoginId = (inviteLoginId) => {
  return post(`${prefix}/invited?inviteLoginId=${inviteLoginId}`);
};

/**
 * 获取推广名片信息
 * @returns {Promise} 推广名片数据
 */
export const getPromotionCard = () => {
  return get(`${prefix}/card`);
};

/**
 * 生成推广二维码
 * @returns {Promise} 二维码数据
 */
export const generatePromotionQrCode = (params) => {
  return get(`${prefix}/promotion/qrcode`,params);
};

/**
 * 获取佣金详情
 * @param {string} orderId 订单ID
 * @returns {Promise} 佣金详情
 */
export const getCommissionDetail = (orderId) => {
  return get(`${prefix}/commission/detail/${orderId}`);
};
