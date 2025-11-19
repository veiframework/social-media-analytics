import request from '@/utils/request'

const prefix = '/admin-api';

// 查询物流地址列表
export function listAddress(query) {
  return request({
    url: prefix + '/logistics-address/list',
    method: 'get',
    params: query
  })
}

// 新增物流地址
export function addAddress(data) {
  return request({
    url: prefix + '/logistics-address/add',
    method: 'post',
    data: data
  })
}

// 修改物流地址
export function updateAddress(data) {
  return request({
    url: prefix + '/logistics-address/edit',
    method: 'post',
    data: data
  })
}

// 删除物流地址
export function delAddress(data) {
  return request({
    url: prefix + '/logistics-address/del',
    method: 'post',
    data: data
  })
}

// 获取商家物流开关状态
export function getLogisticsStatus(query) {
  return request({
    url: prefix + '/logistics-address/get-shop-status',
    method: 'get',
    params: query
  })
}

// 查询所有商家物流地址
export function getAllAddress(query) {
  return request({
    url: prefix + '/logistics-address/all-addr',
    method: 'get',
    params: query
  })
}

// 查询用户物流地址
export function getUserAddress(query) {
  return request({
    url: prefix + '/user/all-addr',
    method: 'get',
    params: query
  })
}

// 设置商家物流状态
export function setShopStatus(data) {
  return request({
    url: prefix + '/logistics-address/set-shop-status',
    method: 'post',
    data: data
  })
}