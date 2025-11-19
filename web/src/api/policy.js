import request from '@/utils/request'

// 获取政策文件列表
export function getPolicyList(params) {
  return request({
    url: '/admin-api/announcement/pc',
    method: 'get',
    params
  })
}

// 获取政策文件详情
export function getPolicyDetail(id) {
  return request({
    url: `/admin-api/announcement/pc/${id}`,
    method: 'get'
  })
}

// 新增政策文件
export function addPolicy(data) {
  return request({
    url: '/admin-api/announcement/pc',
    method: 'post',
    data
  })
}

// 更新政策文件
export function updatePolicy(data) {
  return request({
    url: `/admin-api/announcement/pc/${data.id}`,
    method: 'put',
    data
  })
}

// 删除政策文件
export function deletePolicy(id) {
  return request({
    url: `/admin-api/announcement/pc/${id}`,
    method: 'delete'
  })
} 