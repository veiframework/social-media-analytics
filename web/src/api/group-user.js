import request from '@/utils/request'

const API_PATH = '/admin-api/social-media/group-user'

export function groupUserApi() {
  return {
    // 获取全部
    getAll: (query) => {
      return request({
        url: `${API_PATH}/all`,
        method: 'get',
        params: query
      })
    },
    // 批量关联员工
    batchAdd: (data) => {
      return request({
        url: `${API_PATH}/batch`,
        method: 'post',
        data
      })
    },
    // 删除
    delete: (ids) => {
      return request({
        url: `${API_PATH}/${ids}`,
        method: 'delete'
      })
    },
    // 获取用户下拉列表
    getUserSelector: () => {
      return request({
        url: `${API_PATH}/user/selector`,
        method: 'get'
      })
    },
    // 获取用户下拉列表
    getUserQuerySelector: () => {
      return request({
        url: `${API_PATH}/user/query/selector`,
        method: 'get'
      })
    }
  }
}