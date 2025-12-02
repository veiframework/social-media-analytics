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

export function createByWechatVideoId(data) {
  return request({
    url: `${prefix}/wechat-video-id`,
    method: 'post',
    data: data
  })
}

export function createByWorkShareUrl(data) {
  return request({
    url: `${prefix}/share-link`,
    method: 'post',
    data: data
  })
}

export function delWork(id) {
  return request({
    url: `${prefix}/${id}`,
    method: 'delete'
  })
}
