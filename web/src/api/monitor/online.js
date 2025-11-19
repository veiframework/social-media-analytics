import request from '@/utils/request'
const prefix = '/admin-api'

// 查询在线用户列表
export function list(query) {
  return request({
    url: prefix+'/online/list',
    method: 'get',
    params: query
  })
}

// 强退用户
export function forceLogout(tokenId) {
  return request({
    url: prefix+'/online/' + tokenId,
    method: 'delete'
  })
}
