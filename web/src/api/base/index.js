import request from '@/utils/request'
const lokcerApi = "/locker-api";
/**
 * 小区管理
 */
// 获取小区列表
export function getCommunityListApi(params) {
    return request({
        url: lokcerApi + '/locker/community',
        method: 'get',
        params
    })
}
// 获取全部小区
export function getAllCommunityApi(params) {
    return request({
        url: lokcerApi + '/locker/community/all',
        method: 'get',
        params
    })
}
// 新增小区
export function addCommunityApi(data) {
    return request({
        url: lokcerApi + '/locker/community',
        method: 'post',
        data
    })
}
// 修改小区
export function editCommunityApi(data, id) {
    return request({
        url: lokcerApi + '/locker/community/' + id,
        method: 'put',
        data
    })
}
// 删除小区
export function delCommunityApi(id) {
    return request({
        url: lokcerApi + '/locker/community/' + id,
        method: 'delete'
    })
}
/**
 * 单元管理
 */
// 获取单元列表
export function getUnitListApi(params) {
    return request({
        url: lokcerApi + '/locker/apartment',
        method: 'get',
        params
    })
}
// 获取全部单元
export function getAllUnitApi(params) {
    return request({
        url: lokcerApi + '/locker/apartment/all',
        method: 'get',
        params
    })
}
// 新增单元
export function addUnitApi(data) {
    return request({
        url: lokcerApi + '/locker/apartment',
        method: 'post',
        data
    })
}
// 修改单元
export function editUnitApi(data, id) {
    return request({
        url: lokcerApi + '/locker/apartment/' + id,
        method: 'put',
        data
    })
}
// 删除单元
export function delUnitApi(id) {
    return request({
        url: lokcerApi + '/locker/apartment/' + id,
        method: 'delete'
    })
}
// 单元绑定管理员
export function bindDeliverymanApi(data) {
    return request({
        url: lokcerApi + '/locker/apartment/assign/admin',
        method: 'post',
        data
    })
}
/**
 * 柜子管理
 */
// 获取柜子列表
export function getLockerListApi(params) {
    return request({
        url: lokcerApi + '/locker/info',
        method: 'get',
        params,
    })
}
// 获取全部柜子
export function getAllLockerApi(params) {
    return request({
        url: lokcerApi + '/locker/info/all',
        method: 'get',
        params
    })
}
// 新增柜子
export function addLockerApi(data) {
    return request({
        url: lokcerApi + '/locker/info',
        method: 'post',
        data
    })
}
// 修改柜子
export function editLockerApi(data, id) {
    return request({
        url: lokcerApi + '/locker/info/' + id,
        method: 'put',
        data
    })
}
// 删除柜子
export function delLockerApi(id) {
    return request({
        url: lokcerApi + '/locker/info/' + id,
        method: 'delete'
    })
}
// 绑定 IMEI
export function bindImeiApi(data) {
    return request({
        url: lokcerApi + '/locker/info/imei',
        method: 'post',
        data
    })
}
// 解绑 IMEI
export function unbindImeiApi(id) {
    return request({
        url: lokcerApi + '/locker/info/unbind/imei/' + id,
        method: 'delete'
    })
}
// 修改可用状态
export function changeStatusApi(data) {
    return request({
        url: lokcerApi + '/locker/info/useStatus',
        method: 'post',
        data,
    })
}
// 重启柜子
export function restartLockerApi(id) {
    return request({
        url: lokcerApi + '/locker/info/restart/' + id,
        method: 'post'
    })
}
/**
 * 柜子模板
 */
// 获取柜子模板列表
export function getLockerTemplateListApi(params) {
    return request({
        url: lokcerApi + '/locker/template',
        method: 'get',
        params
    })
}
// 获取全部柜子模板
export function getAllLockerTemplateApi(params) {
    return request({
        url: lokcerApi + '/locker/template/all',
        method: 'get',
        params
    })
}
// 新增柜子模板
export function addLockerTemplateApi(data) {
    return request({
        url: lokcerApi + '/locker/template',
        method: 'post',
        data
    })
}
// 修改柜子模板
export function editLockerTemplateApi(data, id) {
    return request({
        url: lokcerApi + '/locker/template/' + id,
        method: 'put',
        data
    })
}
// 删除柜子模板
export function delLockerTemplateApi(id) {
    return request({
        url: lokcerApi + '/locker/template/' + id,
        method: 'delete'
    })
}
/**
 * 箱格管理
 */
// 获取箱格列表
export function getBoxListApi(params) {
    return request({
        url: lokcerApi + '/locker/box',
        method: 'get',
        params
    })
}
// 修改箱格状态
export function changeBoxStatusApi(data) {
    return request({
        url: lokcerApi + '/locker/box/state',
        method: 'post',
        data
    })
}
/**
 * 住户管理
 */
// 获取住户列表
export function getTenantListApi(params) {
    return request({
        url: lokcerApi + '/locker/room',
        method: 'get',
        params
    })
}
// 新增住户
export function addTenantApi(data) {
    return request({
        url: lokcerApi + '/locker/room',
        method: 'post',
        data
    })
}
// 修改住户
export function editTenantApi(data, id) {
    return request({
        url: lokcerApi + '/locker/room/' + id,
        method: 'put',
        data
    })
}
// 删除住户
export function delTenantApi(id) {
    return request({
        url: lokcerApi + '/locker/room/' + id,
        method: 'delete'
    })
}
// 导出住户信息
export function exportTenantApi(params) {
    return request({
        url: lokcerApi + '/locker/room/export',
        method: 'get',
        params,
    })
}