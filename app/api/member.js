// 会员相关API
import { get, post } from '../utils/request.js';

/**
 * 获取会员开通选项
 * @returns {Promise} 会员选项数据
 */
export const getMemberOptions = (params) => {
  return get('/admin-api/mall/app-user/member/option',params);
};

/**
 * 购买会员
 * @param {Object} data 购买数据
 * @returns {Promise} 购买结果
 */
export const purchaseMember = (data) => {
  return post('/admin-api/payment/wechat', data);
};

/**
 * 获取会员信息
 * @returns {Promise} 会员信息
 */
export const getMemberInfo = () => {
  return get('/admin-api/mall/app-user/member/info');
};



