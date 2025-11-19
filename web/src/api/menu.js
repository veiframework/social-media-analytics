import request from '@/utils/request'

const prefix = "/admin-api"
// 获取路由
export const getRouters = () => {
  return request({
    url: prefix+'/menu/getRouters',
    method: 'get'
  })
}