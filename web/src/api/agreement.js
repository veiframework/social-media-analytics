import request from '@/utils/request'

const prefix = '/admin-api'

// 获取协议列表（分页）
export const getAgreementListApi = (params) => {
  return request({
    url: prefix + '/agreement',
    method: 'get',
    params
  })
}

// 获取协议详情
export const getAgreementApi = (id) => {
  return request({
    url: prefix + '/agreement/' + id,
    method: 'get'
  })
}

// 创建协议
export const addAgreementApi = (data) => {
  return request({
    url: prefix + '/agreement',
    method: 'post',
    data
  })
}

// 更新协议
export const editAgreementApi = (data) => {
  return request({
    url: prefix + '/agreement/'+data.id,
    method: 'put',
    data
  })
}

// 删除协议
export const delAgreementApi = (id) => {
  return request({
    url: prefix + '/agreement/' + id,
    method: 'delete'
  })
}

// 导出协议Excel
export const exportAgreementApi = (params) => {
  return request({
    url: prefix + '/agreement/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
