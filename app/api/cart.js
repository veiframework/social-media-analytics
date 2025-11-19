import { get, post, put, del } from '../utils/request.js';

/**
 * 添加商品到购物车
 * @param {Object} data - 购物车数据
 * @param {string} data.goodsId - 商品ID
 * @param {string} data.specId - 规格ID
 * @param {number} data.quantity - 数量
 */
export const addToCart = (data) => {
  return post('/admin-api/app/wines-car/add', data);
};

/**
 * 获取购物车列表
 */
export const getCartList = (params) => {
  return get('/admin-api/app/wines-car/list',params);
};

/**
 * 更新购物车商品数量
 * @param {string} id - 购物车项ID
 * @param {Object} data - 更新数据
 */
export const updateCartItem = (id, data) => {
  data.id = id;
  return put(`/admin-api/app/wines-car/update`, data);
};

export const updateCartNum = (id, data) => {
  data.id = id;
  return post(`/admin-api/app/wines-car/edit-check`, data);
};

/**
 * 是否全选
 * @param {number} isAll 
 * @returns 
 */
export const batchSelect = (data) => {
  return post(`/admin-api/app/wines-car/batch-edit`, data);
};

/**
 * 删除购物车商品支持数组可批量删除
 * 
 * @param {string} id - 购物车项ID
 */
export const deleteCartItem = (id) => {
  let arr = [];
  arr.push(id);
  return post(`/admin-api/app/wines-car/del`,{ids:arr});
};
