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

export function syncWork(data) {
    return request({
        url: `${prefix}/sync/work/` + data,
        method: 'get',
        data: data
    })
}