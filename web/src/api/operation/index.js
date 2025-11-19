import request from '@/utils/request'
const lokcerApi = "/locker-api";
/**
 * 用户管理
 */
// 用户列表
export function getUserList(params) {
    return request({
        url: lokcerApi + '/locker/user/page',
        method: 'get',
        params
    })
}
// 修改用户信息
export function editUser(data, id) {
    return request({
        url: lokcerApi + '/locker/user/' + id,
        method: 'put',
        data
    })
}
// 用户审核
export function auditUser(data) {
    return request({
        url: lokcerApi + '/locker/user/audit',
        method: 'put',
        data
    })
}
/**
 * 操作记录
 */
// 寄存记录
export function getStorageRecord(params) {
    return request({
        url: lokcerApi + '/locker/operation/record/send',
        method: 'get',
        params
    })
}
// 取件记录
export function getTakeRecord(params) {
    return request({
        url: lokcerApi + '/locker/operation/record/open',
        method: 'get',
        params
    })
}