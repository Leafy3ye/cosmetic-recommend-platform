import request from '@/utils/request'

/**
 * 分页查询商品
 */
export function getProductPage(params) {
  return request({
    url: '/product/page',
    method: 'get',
    params
  })
}

/**
 * 获取商品详情
 */
export function getProductDetail(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

/**
 * 获取热销商品
 */
export function getHotProducts(limit = 10) {
  return request({
    url: '/product/hot',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取推荐商品
 */
export function getRecommendProducts(userId, limit = 10) {
  return request({
    url: `/product/recommend/${userId}`,
    method: 'get',
    params: { limit }
  })
}

/**
 * 添加商品
 */
export function addProduct(data) {
  return request({
    url: '/product/add',
    method: 'post',
    data
  })
}

/**
 * 更新商品
 */
export function updateProduct(data) {
  return request({
    url: '/product/update',
    method: 'put',
    data
  })
}

/**
 * 删除商品
 */
export function deleteProduct(id) {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

/**
 * 获取商家的商品列表（分页）
 */
export function getMerchantProductPage(params) {
  return request({
    url: '/product/merchant/page',
    method: 'get',
    params
  })
}

/**
 * 更新商品状态
 */
export function updateProductStatus(id, status) {
  return request({
    url: `/product/status/${id}`,
    method: 'put',
    params: { status }
  })
}