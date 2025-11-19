import request from '@/utils/request'

const prefix = '/admin-api';

// 查询物流账户列表
export function listAccount(query) {
  return request({
    url: prefix + '/logistics-account/list',
    method: 'get',
    params: query
  })
}

// 查询所有物流账户列表
export function getAllAccount(query) {
  return request({
    url: prefix + '/logistics-account/all-account',
    method: 'get',
    params: query
  })
}

// 查询快递公司列表
export function getDeliveryList(query) {
  return request({
    url: prefix + '/logistics-account/all-delivery',
    method: 'get',
    params: query
  })
}

// 查询快递服务类型列表
export function getDeliveryServiceList(query) {
  return request({
    url: prefix + '/logistics-account/delivery-service-type',
    method: 'get',
    params: query
  })
}

// 绑定物流账户
export function bindAccount(data) {
  return request({
    url: prefix + '/logistics-account/bind',
    method: 'post',
    data: data
  })
}

// 解绑物流账户
export function unbindAccount(data) {
  return request({
    url: prefix + '/logistics-account/unbind',
    method: 'post',
    data: data
  })
}
