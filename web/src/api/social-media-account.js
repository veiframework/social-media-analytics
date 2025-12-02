import request from '@/utils/request'

// 社交媒体账号API
const prefix = '/admin-api/social-media/account'

export function listSocialMediaAccount(query) {
    return request({
        url: `${prefix}`,
        method: 'get',
        params: query
    })
}

export function getSocialMediaAccount(id) {
    return request({
        url: `${prefix}/${id}`,
        method: 'get'
    })
}

export function addSocialMediaAccount(data) {
    return request({
        url: `${prefix}`,
        method: 'post',
        data: data
    })
}

export function updateSocialMediaAccount(data) {
    return request({
        url: `${prefix}/` + data.id,
        method: 'put',
        data: data
    })
}

export function delSocialMediaAccount(id) {
    return request({
        url: `${prefix}/${id}`,
        method: 'delete'
    })
}

export function delBatchSocialMediaAccount(ids) {
    return request({
        url: `${prefix}/batch`,
        method: 'delete',
        data: ids
    })
}

export function createByShareLink(data) {
    return request({
        url: `${prefix}/share-link`,
        method: 'post',
        data: data
    })
}

export function createByWechatVideoNickname(data) {
    return request({
        url: `${prefix}/wechat-video-nickname`,
        method: 'post',
        data: data
    })
}

export function syncWork(data) {
    return request({
        url: `${prefix}/sync/work/` + data,
        method: 'get',
    })
}

export function syncAllWork() {
    return request({
        url: `${prefix}/sync/work/`,
        method: 'get',
    })
}

export function updateAutoSync(value) {
    return request({
        url: `${prefix}/account/` + value.id + "/sync/" + value.autoSync,
        method: 'get',
    })
}