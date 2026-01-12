import request from '@/utils/request'

const prefix = '/admin-api'


// 获取告警器列表
export function getPriorityListApi(params) {
    return request({
        url: prefix + '/social-media/work-priority',
        method: 'get',
        params: params
    })
}

// 获取告警器详情
export function getPriorityApi(id) {
    return request({
        url: prefix + `/social-media/work-priority/${id}`,
        method: 'get'
    })
}

// 添加告警器
export function addPriorityApi(data) {
    return request({
        url: prefix + '/social-media/work-priority',
        method: 'post',
        data: data
    })
}

// 编辑告警器
export function editPriorityApi(id, data) {
    return request({
        url: prefix + `/social-media/work-priority/${id}`,
        method: 'put',
        data: data
    })
}

// 删除告警器
export function delPriorityApi(ids) {
    return request({
        url: prefix + `/social-media/work-priority/${ids}`,
        method: 'delete'
    })
}




