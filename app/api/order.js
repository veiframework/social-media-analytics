import { get, post, put, del } from '../utils/request.js';

/**
 * 创建订单
 * @param {Object} data - 订单数据
 */
export const createOrder = (data) => {
  return post('/admin-api/app/order/create-order', data);
};

export const createOrderFromCart = (data) => {
  return post('/admin-api/app/order/create-order-by-car', data);
};

/**
 * 获取订单详情
 * @param {string} orderId - 订单ID
 */
export const getOrderDetail = (orderId) => {
  return get(`/admin-api/app/order/order-info?id=${orderId}`);
};

/**
 * 获取订单列表
 * @param {Object} params - 查询参数 {current: 当前页码, size: 分页大小, status: 订单状态}
 */
export const getOrderList = (params) => {
  return get('/admin-api/app/order/list', params);
};

/**
 * 取消订单
 * @param {string} orderId - 订单ID
 */
export const cancelOrder = (orderId) => {
  return post(`/admin-api/app/wines-order/cancel/${orderId}`);
};

/**
 * 确认收货
 * @param {string} orderId - 订单ID
 */
export const confirmOrder = (orderId) => {
  return post('/admin-api/app/order/complete_receipt',{orderId:orderId});
};

/**
 * 订单支付
 * @param {Object} data - 支付数据
 */
export const payOrder = (data) => {
  return post('/admin-api/app/order/pay-order', {orderId:data});
};

/**
 * 申请退款
 * @param {string} orderId - 订单ID
 */
export const applyRefund = (orderId) => {
  return post('/admin-api/app/order/apply-refund', {orderId: orderId});
};

/**
 * 申请退款（新接口）
 * @param {string} orderId - 订单ID
 */
export const requireRefund = (data) => {
  return post(`/admin-api/app/order/require/refund`,data);
};