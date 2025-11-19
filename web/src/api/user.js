import request from '@/utils/request'

const prefix = '/admin-api'

// 获取用户列表（分页）
export const getUserListApi = (params) => {
  return request({
    url: prefix + '/app-user',
    method: 'get',
    params
  })
}

// 获取全部用户
export const getAllUsersApi = (params) => {
  return request({
    url: prefix + '/app-user/all',
    method: 'get',
    params
  })
}

// 创建用户
export const addUserApi = (data) => {
  return request({
    url: prefix + '/app-user/user',
    method: 'post',
    data
  })
}

// 更新用户
export const editUserApi = (data, id) => {
  return request({
    url: prefix + `/app-user/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export const delUserApi = (ids) => {
  return request({
    url: prefix + `/app-user/${ids}`,
    method: 'delete'
  })
}

// 获取用户详情
export const getUserDetailApi = (id) => {
  return request({
    url: prefix + `/app-user/${id}`,
    method: 'get'
  })
}

// 导出用户Excel
export const exportUserApi = (params) => {
  return request({
    url: prefix + '/app-user/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}