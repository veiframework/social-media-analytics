import request from '@/utils/request'

const prefix = '/admin-api';

// 查询商品列表
export function listGoods(query) {
  return request({
    url: prefix + '/goods/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getGoods(id) {
  return request({
    url: prefix + '/goods/info',
    method: 'get',
    params: { id }
  })
}

// 新增商品
export function addGoods(data) {
  return request({
    url: prefix + '/goods/add',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateGoods(data) {
  return request({
    url: prefix + '/goods/edit',
    method: 'post',
    data: data
  })
}

// 删除商品
export function delGoods(data) {
  return request({
    url: prefix + '/goods/del',
    method: 'post',
    data: data
  })
}

// 商品上下架
export function updateGoodsStatus(data) {
  return request({
    url: prefix + '/goods/up-down',
    method: 'post',
    data: data
  })
}

// 设置/取消Banner
export function updateGoodsBanner(data) {
  return request({
    url: prefix + '/goods/set-cancel',
    method: 'post',
    data: data
  })
}

// 设置/取消促销
export function updateGoodsPromotion(data) {
  return request({
    url: prefix + '/goods/set-promotion',
    method: 'post',
    data: data
  })
}

// 商品排序
export function sortGoods(data) {
  return request({
    url: prefix + '/goods/goods-sort',
    method: 'post',
    data: data
  })
}

// 查询商品分类列表
export function listGoodsType(query) {
  return request({
    url: prefix + '/goods/type-list',
    method: 'get',
    params: query
  })
}

// 新增商品分类
export function addGoodsType(data) {
  return request({
    url: prefix + '/goods/type-add',
    method: 'post',
    data: data
  })
}

// 修改商品分类
export function updateGoodsType(data) {
  return request({
    url: prefix + '/goods/type-edit',
    method: 'post',
    data: data
  })
}

// 删除商品分类
export function delGoodsType(data) {
  return request({
    url: prefix + '/goods/type-del',
    method: 'post',
    data: data
  })
}

// 商品分类上下架
export function updateGoodsTypeStatus(data) {
  return request({
    url: prefix + '/goods/type-up-down',
    method: 'post',
    data: data
  })
}

// 商品分类排序
export function sortGoodsType(data) {
  return request({
    url: prefix + '/goods/type-sort',
    method: 'post',
    data: data
  })
}

// 获取商铺分类下拉列表
export function getShopTypeList(shopId) {
  return request({
    url: prefix + '/goods/shop-type',
    method: 'get',
    params: { shopId }
  })
}

// 获取门店商品分类和对应的商品信息
export function getTypeAndGoods(query) {
  return request({
    url: prefix + '/goods/type-and-goods',
    method: 'get',
    params: query
  })
}
