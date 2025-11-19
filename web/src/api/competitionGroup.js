import request from '@/utils/request'

// 查询分组列表
export function listCompetitionGroup(query) {
  return request({
    url: '/admin/competition/group',
    method: 'get',
    params: query
  })
}

// 查询分组详细
export function getCompetitionGroup(id) {
  return request({
    url: '/admin/competition/group/' + id,
    method: 'get'
  })
}

// 新增分组
export function addCompetitionGroup(data) {
  return request({
    url: '/admin/competition/group',
    method: 'post',
    data: data
  })
}

// 修改分组
export function updateCompetitionGroup(data) {
  return request({
    url: '/admin/competition/group/' + data.id,
    method: 'put',
    data: data
  })
}

// 删除分组
export function delCompetitionGroup(id) {
  return request({
    url: '/admin/competition/group/' + id,
    method: 'delete'
  })
}