import request from '@/utils/request'

/**
 * 用户注册
 */
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

/**
 * 更新用户信息
 */
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

/**
 * 获取用户地址
 */
export function getUserAddress(userId) {
  return request({
    url: `/user/${userId}/address`,
    method: 'get'
  })
}

/**
 * 更新用户地址
 */
export function updateUserAddress(userId, data) {
  return request({
    url: `/user/${userId}/address`,
    method: 'put',
    data
  })
}

/**
 * 上传用户头像
 */
export function uploadAvatar(userId, avatar) {
  return request({
    url: `/user/${userId}/avatar`,
    method: 'put',
    data: { avatar }
  })
}

/**
 * 获取商家统计数据
 */
export function getMerchantStats(merchantId) {
  return request({
    url: `/user/merchant/stats/${merchantId}`,
    method: 'get'
  })
}

