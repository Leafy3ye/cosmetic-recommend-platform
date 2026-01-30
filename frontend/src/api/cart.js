import request from '@/utils/request'

/**
 * 添加到购物车
 */
export function addToCart(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    params: data
  })
}

/**
 * 获取购物车列表
 */
export function getCartList(userId) {
  return request({
    url: `/cart/list/${userId}`,
    method: 'get'
  })
}

/**
 * 更新购物车商品数量
 */
export function updateCartQuantity(data) {
  return request({
    url: '/cart/update',
    method: 'put',
    params: data
  })
}

/**
 * 删除购物车商品
 */
export function deleteCartItem(cartId) {
  return request({
    url: `/cart/${cartId}`,
    method: 'delete'
  })
}

/**
 * 清空购物车
 */
export function clearCart(userId) {
  return request({
    url: `/cart/clear/${userId}`,
    method: 'delete'
  })
}

/**
 * 切换选中状态
 */
export function toggleCheck(data) {
  return request({
    url: '/cart/check',
    method: 'put',
    params: data
  })
}
