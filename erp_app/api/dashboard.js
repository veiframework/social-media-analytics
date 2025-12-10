import {get, post, put} from "../utils/request";

const prefix = '/admin-api/social-media/dashboard'

export function getAccountStatistic(params) {
    return get(prefix + '/statistic/account', params)
}

export function getPlatformStatistic() {
    return get(prefix + '/statistic/platform')
}