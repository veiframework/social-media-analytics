import request from '@/utils/request'

const prefix = '/admin-api';

// 查询订单列表
export function listOrder(query) {
  return request({
    url: prefix + '/order/list',
    method: 'get',
    params: query
  })
}

// 查询线下支付订单列表
export function listOfflineOrder(query) {
  return request({
    url: prefix + '/order/offline_list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(id) {
  return request({
    url: prefix + '/order/info?id='+id,
    method: 'get'
  })
}

// 删除订单
export function delOrder(data) {
  return request({
    url: prefix + '/order/del',
    method: 'post',
    data: data
  })
}

// 设置物流单号
export function setTrackingNo(data) {
  return request({
    url: prefix + '/order/set-tracking-no',
    method: 'post',
    data: data
  })
}

// 查询订单物流信息
export function getOrderLogistics(id) {
  return request({
    url: prefix + '/order/order_logistics_info',
    method: 'get',
    params: { id }
  })
}

// 确认收货
export function completeReceipt(data) {
  return request({
    url: prefix + '/order/complete_receipt',
    method: 'post',
    data: data
  })
}

// 微信退款
export function wechatRefund(data) {
  return request({
    url: prefix + '/payment/refund/wechat',
    method: 'post',
    data: data
  })
}

// 驳回退款
export function rejectRefund(data) {
  return request({
    url: prefix + '/order/reject/refund',
    method: 'post',
    data: data
  })
}

// 创建物流订单
export function createLogisticsOrder(data) {
  return request({
    url: prefix + '/logistics-order/create',
    method: 'post',
    data: data
  })
}


export function getReceiverAddressList(query) {
  return request({
    url: prefix + '/receiver-address/list',
    method: 'get',
    params: query
  })
}