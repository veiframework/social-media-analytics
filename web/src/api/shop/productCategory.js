import request from '@/utils/request';

// 查询商品分类列表
export function listProductCategory(query) {
  return request({
    url: '/shop-api/shop/product-category/all',
    method: 'get',
    params: query
  });
}

// 查询商品分类详细
export function getProductCategory(id) {
  return request({
    url: '/shop-api/shop/product-category/' + id,
    method: 'get'
  });
}

// 新增商品分类
export function addProductCategory(data) {
  return request({
    url: '/shop-api/shop/product-category',
    method: 'post',
    data: data
  });
}

// 修改商品分类
export function updateProductCategory(data) {
  return request({
    url: '/shop-api/shop/product-category',
    method: 'put',
    data: data
  });
}

// 删除商品分类
export function deleteProductCategory(id) {
  return request({
    url: '/shop-api/shop/product-category/' + id,
    method: 'delete'
  });
}

// 修改商品分类状态
export function updateProductCategoryState(id, state) {
  return request({
    url: '/shop-api/shop/product-category/state/' + state + '/id/' + id,
    method: 'put'
  });
}
