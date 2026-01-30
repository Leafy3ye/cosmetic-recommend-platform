import request from '@/utils/request'

/**
 * 创建订单
 */
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

/**
 * 根据订单号获取订单
 */
export function getOrderByNo(orderNo) {
  return request({
    url: `/order/no/${orderNo}`,
    method: 'get'
  })
}

/**
 * 分页查询用户订单
 */
export function getUserOrderPage(userId, params) {
  return request({
    url: `/order/page/${userId}`,
    method: 'get',
    params
  })
}

/**
 * 取消订单
 */
export function cancelOrder(orderId) {
  return request({
    url: `/order/cancel/${orderId}`,
    method: 'put'
  })
}

/**
 * 支付订单
 */
export function payOrder(orderId, payType) {
  return request({
    url: `/order/pay/${orderId}`,
    method: 'put',
    params: { payType }
  })
}

/**
 * 确认收货
 */
export function confirmOrder(orderId) {
  return request({
    url: `/order/confirm/${orderId}`,
    method: 'put'
  })
}

/**
 * 发货（商家）
 */
export function deliverOrder(orderId) {
  return request({
    url: `/order/deliver/${orderId}`,
    method: 'put'
  })
}

/**
 * 分页查询商家订单
 */
export function getMerchantOrderPage(params) {
  return request({
    url: '/order/merchant/page',
    method: 'get',
    params
  })
}