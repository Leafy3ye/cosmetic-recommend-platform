<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const protectedPaths = [
  '/cart',
  '/orders',
  '/profile',
  '/favorites',
  '/footprints',
  '/address',
  '/merchant/products',
  '/merchant/orders'
]

const isProtectedPath = (path) => {
  return protectedPaths.some(p => path === p || path.startsWith(`${p}/`))
}

const onAuthExpired = async () => {
  // 清理Pinia内存态，保证右上角立即变为未登录
  userStore.clearUserInfo()

  // 只有受保护页面才跳转登录；公开页面（如首页）保持可访问
  const currentPath = router.currentRoute.value.path
  if (isProtectedPath(currentPath)) {
    await router.replace('/login')
  }
}

onMounted(() => {
  window.addEventListener('auth-expired', onAuthExpired)
})

onUnmounted(() => {
  window.removeEventListener('auth-expired', onAuthExpired)
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol',
    'Noto Color Emoji';
}
</style>

