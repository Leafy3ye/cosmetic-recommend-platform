import request from '@/utils/request'

/**
 * 记录用户行为
 */
export function recordBehavior(userId, productId, behaviorType) {
  return request({
    url: '/behavior/record',
    method: 'post',
    params: { userId, productId, behaviorType }
  })
}

/**
 * 获取用户足迹商品
 */
export function getHistoryProducts(userId, limit = 20) {
  return request({
    url: `/behavior/history/${userId}`,
    method: 'get',
    params: { limit }
  })
}

