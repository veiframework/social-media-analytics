import request from '@/utils/request'

const prefix = '/admin-api'


// 获取政策文件列表
export function getOpenListApi(params) {
    return request({
        url: '/admin-api/member/option/page',
        method: 'get',
        params
    })
}

// 新增政策文件
export function editOpenApi(data) {
    return request({
        url: '/admin-api/member/option',
        method: 'put',
        data
    })
}

// 获取政策文件列表
export function getOpenRecordListApi(params) {
    return request({
        url: '/admin-api/member/record/page',
        method: 'get',
        params
    })
}


// 微信退款
export function refundMemberApi(data) {
    return request({
        url: prefix + '/payment/refund/wechat',
        method: 'post',
        data: data
    })
}