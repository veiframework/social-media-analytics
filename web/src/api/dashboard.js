import request from '@/utils/request'

const prefix = '/admin-api/social-media/dashboard'
  
export function getAccountStatistic(params) {
    return request({
        url: prefix + '/statistic/account',
        method: 'get',
        params
    })
}

export function getPlatformStatistic() {
    return request({
        url: prefix + '/statistic/platform',
        method: 'get'
    })
}