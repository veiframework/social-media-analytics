import request from '@/utils/request'

export function getLeaderList() {
    return request({
        url: '/admin-api/social-media/group-user/user/leader',
        method: 'get',
    })
}

export function getWorkListApi(params) {
    return request({
        url: '/admin-api/social-media/work/rank',
        method: 'get',
        params
    })
}
