import request from '@/utils/request'
const prefix = '/admin-api'

// 查询操作日志列表
export function list(query) {
  return request({
    url: prefix+'/operlog/list',
    method: 'get',
    params: query
  })
}

// 删除操作日志
export function delOperlog(operId) {
  return request({
    url: prefix+'/operlog/' + operId,
    method: 'delete'
  })
}

// 清空操作日志
export function cleanOperlog() {
  return request({
    url: prefix+'/operlog/clean',
    method: 'delete'
  })
}
