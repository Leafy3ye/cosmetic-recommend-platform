import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)

  // 初始化用户信息
  const initUserInfo = () => {
    const info = localStorage.getItem('userInfo')
    if (info) {
      userInfo.value = JSON.parse(info)
    }
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 清空用户信息
  const clearUserInfo = () => {
    userInfo.value = null
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }

  // 刷新用户信息
  const refreshUserInfo = async (userId) => {
    try {
      const res = await getUserInfo(userId)
      if (res.code === 200 && res.data) {
        setUserInfo(res.data)
      }
    } catch (error) {
      console.error('刷新用户信息失败：', error)
      throw error
    }
  }

  // 初始化
  initUserInfo()

  return {
    userInfo,
    setUserInfo,
    clearUserInfo,
    refreshUserInfo
  }
})
