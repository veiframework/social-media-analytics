import {get, post, put} from "../utils/request";

const prefix = '/admin-api/social-media/work'

export function getWorkListApi(params) {
    return get(prefix, params)
}

export function getWorkApi(id) {
    return get(`${prefix}/${id}`)
}
