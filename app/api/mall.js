// 商城相关API
import { get } from '../utils/request.js';

/**
 * 获取商城首页数据
 * @returns {Promise} 包含轮播图和商品列表的数据
 */
export const getMallHomeInfo = (params) => {
  return get('/admin/app/goods/home-info',params);
};


export const getGoodsType = (params) => {
  return get('/admin/app/goods/goods-type',params);
};
/**
 * 获取商品详情信息
 * @param {number} id 商品ID
 * @returns {Promise} 商品详情数据
 */
export const getGoodsInfo = (id) => {
  return get(`/admin-api/app/goods/goods-info?id=${id}`);
};
