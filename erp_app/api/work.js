import {get, post, put} from "../utils/request";

const prefix = '/admin-api/social-media/work'

export function getWorkListApi(params) {
    return get(prefix, params)
}

export function getWorkApi(id) {
    return get(`${prefix}/${id}`)
}

export function createWorkByShareLink(shareLink, accountType, customType) {
    return post(`${prefix}/share-link/task`, {shareLink, accountType, customType}, {
        showLoading: true,
        showError: true
    })
}

export function getDicts(dictType) {
    return get('/admin-api/dict/data/type/' + dictType)
}