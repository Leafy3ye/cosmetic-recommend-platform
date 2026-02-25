import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

let handlingAuthExpired = false

const handleAuthExpired = (message = '登录已过期，请重新登录') => {
  if (handlingAuthExpired) return
  handlingAuthExpired = true

  ElMessage.error(message)
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')

  // 通知应用层清理内存态，并由路由权限决定是否跳转登录页
  window.dispatchEvent(new CustomEvent('auth-expired'))

  // 允许后续再次触发过期处理
  setTimeout(() => {
    handlingAuthExpired = false
  }, 1000)
}

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，则显示错误信息
    if (res.code !== 200) {
      // 401: Token过期或未登录
      if (res.code === 401) {
        handleAuthExpired(res.message || '登录已过期，请重新登录')
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误：', error)
    
    if (error.response) {
      const { status } = error.response
      
      if (status === 401) {
        handleAuthExpired('登录已过期，请重新登录')
      } else if (status === 403) {
        ElMessage.error('没有权限访问')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器错误')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }
    
    return Promise.reject(error)
  }
)

export default request

