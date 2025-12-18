import {get, post, put} from "../utils/request";

const prefix = '/admin-api/social-media/work'

export function getWorkListApi(params) {
    return get(prefix, params)
}

export function getWorkApi(id) {
    return get(`${prefix}/${id}`)
}

export function createWorkByShareLink(shareLink, accountType) {
    return post(`${prefix}/share-link/task`, {shareLink, accountType},{
        showLoading: true,
        showError: true
    })
}