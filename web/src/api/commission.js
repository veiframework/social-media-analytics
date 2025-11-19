import request from '@/utils/request'

const prefix = '/admin-api';

// 查询提成记录列表
export function listCommissionRecord(query) {
  return request({
    url: prefix + '/commission/record/page',
    method: 'get',
    params: query
  })
}

// 更新提成记录状态
export function updateCommissionStatus(data) {
  return request({
    url: prefix + '/commission/record/status',
    method: 'post',
    data: data
  })
}

// 导出提成记录
export function exportCommissionRecord(query) {
  return request({
    url: prefix + '/commission/record/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 获取提成统计数据
export function getCommissionStats(query) {
  return request({
    url: prefix + '/commission/record/stats',
    method: 'get',
    params: query
  })
}

// 获取提成配置
export function getCommissionConfig() {
  return request({
    url: prefix + '/commission/record/commission/rate/config',
    method: 'get'
  })
}

// 更新提成比率
export function updateCommissionRate(data) {
  return request({
    url: prefix + '/commission/record/commission/rate/config',
    method: 'post',
    data: data
  })
}
