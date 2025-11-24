import request from '@/utils/request'

const prefix = '/admin-api'


// 获取告警器列表
export function getAlarmListApi(params) {
    return request({
        url: prefix + '/social-media/work/alarm',
        method: 'get',
        params: params
    })
}

// 获取告警器详情
export function getAlarmApi(id) {
    return request({
        url: prefix + `/social-media/work/alarm/${id}`,
        method: 'get'
    })
}

// 添加告警器
export function addAlarmApi(data) {
    return request({
        url: prefix + '/social-media/work/alarm',
        method: 'post',
        data: data
    })
}

// 编辑告警器
export function editAlarmApi(id, data) {
    return request({
        url: prefix + `/social-media/work/alarm/${id}`,
        method: 'put',
        data: data
    })
}

// 删除告警器
export function delAlarmApi(ids) {
    return request({
        url: prefix + `/social-media/work/alarm/${ids}`,
        method: 'delete'
    })
}

// 导出告警器
export function exportAlarmApi(params) {
    return request({
        url: prefix + '/social-media/work/alarm/export',
        method: 'get',
        params: params,
        responseType: 'blob'
    })
}

export function getAlarmWebhookApi(params) {
    return request({
        url: prefix + '/social-media/work/alarm/webhook',
        method: 'get',
        params: params,
    })
}

export function addAlarmWebhookApi(data) {
    return request({
        url: prefix + '/social-media/work/alarm/webhook',
        method: 'post',
        data: data,
    })
}