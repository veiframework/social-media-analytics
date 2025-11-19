import request from '@/utils/request'

const prefix = '/admin-api'


// 查询报名记录列表
export function listRegistration(query) {
  return request({
    url: prefix+'/admin/competition/registration',
    method: 'get',
    params: query
  })
}

// 查询报名记录详细
export function getRegistration(id) {
  return request({
    url: prefix+'/admin/competition/registration/' + id,
    method: 'get'
  })
}

// 新增报名记录
export function addRegistration(data) {
  return request({
    url: prefix+'/admin/competition/registration',
    method: 'post',
    data: data
  })
}

// 修改报名记录
export function updateRegistration(data) {
  return request({
    url: prefix+'/admin/competition/registration/' + data.id,
    method: 'put',
    data: data
  })
}

// 删除报名记录
export function delRegistration(id) {
  return request({
    url: prefix+'/admin/competition/registration/' + id,
    method: 'delete'
  })
}

// 更新报名状态
export function updateRegistrationStatus(data) {
  return request({
    url: prefix+'/admin/competition/registration/status',
    method: 'post',
    data: data
  })
}

// 导出报名记录
export function exportRegistration(query) {
  return request({
    url: prefix+'/admin/competition/registration/export',
    method: 'get',
    params: query
  })
}