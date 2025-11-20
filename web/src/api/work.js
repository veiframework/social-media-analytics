import request from '@/utils/request'

const prefix = '/admin-api/social-media/work'

export function getWorkListApi(params) {
  return request({
    url: prefix,
    method: 'get',
    params
  })
}

export function getWorkApi(id) {
  return request({
    url: `${prefix}/${id}`,
    method: 'get'
  })
}

export function exportWorkApi(params) {
  return request({
    url: `${prefix}/export`,
    method: 'get',
    params
  })
}