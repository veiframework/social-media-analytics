import request from '@/utils/request'
const chargeUrl = "/admin-api";
/**
 * 省市区管理
 */
// 省市区全部数据
export function getRegionAllApi(params) {
	return request({
		url: chargeUrl + '/regions/all',
		method: 'get',
		params
	})
}
// 省市区列表
export function getRegionListApi(params) {
	return request({
		url: chargeUrl + '/regions',
		method: 'get',
		params
	})
}
// 新增省市区
export function addRegionApi(data) {
	return request({
		url: chargeUrl + '/regions',
		method: 'post',
		data
	})
}
// 修改省市区
export function editRegionApi(data, Id) {
	return request({
		url: chargeUrl + '/regions/' + Id,
		method: 'put',
		data
	})
}
// 删除省市区
export function delRegionApi(Id) {
	return request({
		url: chargeUrl + '/regions/' + Id,
		method: 'delete'
	})
}
// 修改省市用于检索状态
export function setRegionApi(data) {
	return request({
		url: chargeUrl + '/regions/visible',
		method: 'post',
		data
	})
}
// 修改该城市 SOC 值
export function setCitySocApi(data) {
	return request({
		url: chargeUrl + '/regions/soc',
		method: 'post',
		data
	})
}