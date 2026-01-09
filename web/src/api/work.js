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


export function updateViewCount(data) {
    return request({
        url: `${prefix}/view`,
        method: 'post',
        data: data
    })
}

export function createShareLinkTask(data) {
    return request({
        url: `${prefix}/share-link/task`,
        method: 'post',
        data: data
    })
}

export function retryShareLinkTask(id) {
    return request({
        url: `${prefix}/share-link/retry/` + id,
        method: 'post',
    })
}

export function getShareLinkTaskPage(params) {
    return request({
        url: `${prefix}/share-link/task`,
        method: 'get',
        params
    })
}

export function deleteShareLinkTask(id) {
    return request({
        url: `${prefix}/share-link/task/` + id,
        method: 'delete',
    })
}

export function getWorkByShareLinkApi(params) {
    return request({
        url: `${prefix}/share-link/detail`,
        method: 'get',
        params
    })
}

export function syncWork(data) {
    return request({
        url: `${prefix}/sync/` + data,
        method: 'get',
    })
}
