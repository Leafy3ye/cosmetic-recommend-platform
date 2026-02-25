import request from '@/utils/request'

/**
 * 添加收藏
 */
export function addFavorite(userId, productId) {
  return request({
    url: '/favorite/add',
    method: 'post',
    params: { userId, productId }
  })
}

/**
 * 取消收藏
 */
export function removeFavorite(userId, productId) {
  return request({
    url: '/favorite/remove',
    method: 'delete',
    params: { userId, productId }
  })
}

/**
 * 获取用户收藏商品列表
 */
export function getFavoriteProducts(userId, limit = 20) {
  return request({
    url: `/favorite/user/${userId}`,
    method: 'get',
    params: { limit }
  })
}

