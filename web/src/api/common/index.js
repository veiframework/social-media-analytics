import request from '@/utils/request'
const lokcerApi = "/locker-api";
// 文件服务管理
const fileUrl = "/file-api";
// 获取上传参数（阿里云）
export function aliyunStsToken(params) {
    return request({
        url: fileUrl + '/get-token',
        method: 'get',
        params
    })
}
// 私有导出文档
export function getPrivateFileApi(url) {
    return request({
        url: chargeUrl + '/' + encodeURIComponent(url),
        method: 'get',
        responseType: 'blob',
    })
}
// 获取上传参数
export function getOBSTokenApi(params) {
    return request({
        url: fileUrl + '/get-token',
        method: 'get',
        params
    })
}
// 获取私有上传参数
export function getPrivateOBSTokenApi(params) {
    return request({
        url: fileUrl + '/get-private-obs-token',
        method: 'get',
        params,
    })
}
// 省市区全部数据
export function getRegionAllApi(params) {
    return request({
        url: lokcerApi + '/regions/all',
        method: 'get',
        params
    })
}