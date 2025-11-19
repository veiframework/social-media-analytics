import request from '@/utils/request'

const prefix= '/admin-api';

// 查询轮播图列表
export function listBanner(query) {
  return request({
    url: prefix+'/admin/banner',
    method: 'get',
    params: query
  })
}

// 查询轮播图详细
export function getBanner(id) {
  return request({
    url: prefix+'/admin/banner/' + id,
    method: 'get'
  })
}

// 新增轮播图
export function addBanner(data) {
  return request({
    url: prefix+'/admin/banner',
    method: 'post',
    data: data
  })
}

// 修改轮播图
export function updateBanner(id, data) {
  return request({
    url: prefix+'/admin/banner/' + id,
    method: 'put',
    data: data
  })
}

// 删除轮播图
export function delBanner(ids) {
  return request({
    url: prefix+'/admin/banner/' + ids,
    method: 'delete'
  })
}






// 获取赛事下拉列表
export function getCompetitionList() {
  return request({
    url: prefix+'/admin/competition/all',
    method: 'get'
  })
}