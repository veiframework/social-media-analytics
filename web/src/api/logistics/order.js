import request from '@/utils/request'

const prefix = '/admin-api';

// 查询物流订单列表
export function listOrder(query) {
  return request({
    url: prefix + '/logistics-order/list',
    method: 'get',
    params: query
  })
}

// 创建物流订单
export function createOrder(data) {
  return request({
    url: prefix + '/logistics-order/create',
    method: 'post',
    data: data
  })
}

// 取消物流订单
export function cancelOrder(data) {
  return request({
    url: prefix + '/logistics-order/cancel',
    method: 'post',
    data: data
  })
}

// 查看物流轨迹
export function getTrajectory(query) {
  return request({
    url: prefix + '/logistics-order/get-path',
    method: 'get',
    params: query
  })
}

// 查看物流面单
export function getOrder(query) {
  return request({
    url: prefix + '/logistics-order/get-order',
    method: 'get',
    params: query
  })
}

// 获取物流订单详情
export function getOrderInfo(query) {
  return request({
    url: prefix + '/logistics-order/order-picking-info',
    method: 'get',
    params: query
  })
}
