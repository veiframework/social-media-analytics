// 收件地址相关API
import { get, post, put, del } from '../utils/request.js';

/**
 * 获取收件地址列表
 * @returns {Promise} 地址列表数据
 */
export const getAddressList = () => {
  return get('/admin-api/app/wines-receiver-address/list');
};

/**
 * 添加收件地址
 * @param {Object} addressData 地址数据
 * @returns {Promise} 添加结果
 */
export const addAddress = (addressData) => {
  addressData.detailInfo = addressData.detailInfoNew;
  return post('/admin-api/app/wines-receiver-address/add', addressData);
};

/**
 * 更新收件地址
 * @param {number} id 地址ID
 * @param {Object} addressData 地址数据
 * @returns {Promise} 更新结果
 */
export const updateAddress = (id, addressData) => {
  addressData.id = id;
  addressData.detailInfo = addressData.detailInfoNew;
  return post(`/admin-api/app/wines-receiver-address/edit`, addressData);
};

/**
 * 删除收件地址
 * @param {number} id 地址ID
 * @returns {Promise} 删除结果
 */
export const deleteAddress = (id) => {
  return post(`/admin-api/app/wines-receiver-address/del`,{id:id});
};

/**
 * 设置默认地址
 * @param {number} id 地址ID
 * @returns {Promise} 设置结果
 */
export const setDefaultAddress = (id) => {
  return put(`/admin-api/app/wines-receiver-address/default/${id}`);
};

/**
 * 获取地区数据
 * @param {Object} params 查询参数 {level: 级别, parentId: 父级ID}
 * @returns {Promise} 地区数据
 */
export const getRegions = (params) => {
  return get('/admin-api/regions/app/all', params);
};