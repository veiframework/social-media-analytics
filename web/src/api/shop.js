import request from '@/utils/request'
const prefix = '/admin-api';



// 查询订单列表
export function getAllShop(query) {
    return request({
        url: prefix + '/shop/all',
        method: 'get',
        params: query
    })
}

