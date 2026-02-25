<template>
  <header class="top-header">
    <div class="header-container">
      <!-- 左侧LOGO -->
      <div class="logo" @click="goHome">
        <div class="logo-symbol">C</div>
        <div class="logo-info">
          <span class="brand-name">Cosmetic</span>
          <span class="brand-desc">美妆精选</span>
        </div>
      </div>
      
      <!-- 中间导航 -->
      <nav class="nav-menu">
        <a href="/" class="nav-item" :class="{ active: activePage === 'home' }">首页</a>
        <a href="/products" class="nav-item" :class="{ active: activePage === 'all' }">全部商品</a>
        <a href="/products?type=new" class="nav-item" :class="{ active: activePage === 'new' }">新品</a>
        <a href="/products?type=hot" class="nav-item" :class="{ active: activePage === 'hot' }">热卖</a>
      </nav>
      
      <!-- 右侧操作区 -->
      <div class="header-actions">
        <!-- 搜索框 -->
        <div class="search-wrapper">
          <el-icon class="search-icon"><Search /></el-icon>
          <input 
            type="text" 
            v-model="searchKeyword"
            placeholder="搜索商品"
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            搜索
          </button>
        </div>
        
        <!-- 城市选择 -->
        <div class="city-select">
          <el-icon><Location /></el-icon>
          <span class="city-name">{{ currentCity }}</span>
        </div>
        
        <!-- 购物车 (商家隐藏) -->
        <div 
          v-if="!userInfo || userInfo.role !== 1" 
          class="action-item cart-item" 
          :class="{ 'active': activePage === 'cart' }"
          @click="goToCart"
        >
          <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
            <el-icon><ShoppingCart /></el-icon>
          </el-badge>
        </div>
        
        <!-- 登录/用户 -->
        <div v-if="!isLogin" class="action-item login-btn" @click="goToLogin">
          <el-icon><User /></el-icon>
          <span>登录</span>
        </div>
        
        <el-dropdown v-else @command="handleUserCommand" class="user-dropdown">
          <div class="action-item user-item">
            <el-avatar :size="32" :src="userInfo?.avatar" :icon="UserFilled" />
            <span class="username">{{ userInfo?.nickname || '用户' }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <template v-if="userInfo?.role === 1">
                <el-dropdown-item command="products">商品管理</el-dropdown-item>
                <el-dropdown-item command="merchantOrders">订单管理</el-dropdown-item>
              </template>
              <template v-else>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="favorite">我的收藏</el-dropdown-item>
              </template>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Location, ShoppingCart, User, UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getCartList } from '@/api/cart'
import { initCity } from '@/utils/location'

// Props
defineProps({
  activePage: {
    type: String,
    default: '' // home, all, new, hot, cart, 空字符串表示无高亮
  }
})

const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const currentCity = ref('定位中...')
const cartCount = ref(0)

const userInfo = computed(() => userStore.userInfo)
const isLogin = computed(() => !!userInfo.value)

onMounted(async () => {
  // 初始化城市定位
  currentCity.value = await initCity()
  
  if (isLogin.value) {
    loadCartCount()
  }
})

const loadCartCount = async () => {
  // 商家账户不加载购物车数量
  if (userInfo.value?.role === 1) {
    return
  }
  
  try {
    const res = await getCartList(userInfo.value.userId)
    cartCount.value = res.data?.length || 0
  } catch (error) {
    console.error('加载购物车数量失败：', error)
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/products?keyword=${searchKeyword.value}`)
  }
}

const goHome = () => {
  router.push('/')
}

const goToCart = () => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 商家账户无法访问购物车
  if (userInfo.value?.role === 1) {
    ElMessageBox.alert(
      '当前为商家账户！若要购买商品，请切换账户！',
      '提示',
      {
        confirmButtonText: '切换账户',
        type: 'warning',
        center: true,
        callback: () => {
          // 退出登录并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          userStore.clearUserInfo()
          router.push('/login')
        }
      }
    )
    return
  }
  
  router.push('/cart')
}

const goToLogin = () => {
  router.push('/login')
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'products':
      router.push('/merchant/products')
      break
    case 'merchantOrders':
      router.push('/merchant/orders')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'favorite':
      router.push('/favorites')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    userStore.clearUserInfo()
    ElMessage.success('已退出登录')
    router.push('/')
  }).catch(() => {})
}
</script>

<style scoped>
/* 顶部导航栏 - 完全复制首页样式 */
.top-header {
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 60px;
}

/* LOGO */
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  flex-shrink: 0;
  transition: opacity 0.3s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-symbol {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
  color: white;
  letter-spacing: -1px;
}

.logo-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: 0.5px;
}

.brand-desc {
  font-size: 11px;
  color: #999;
  font-weight: 500;
  letter-spacing: 1px;
}

/* 导航菜单 */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 40px;
  flex: 1;
}

.nav-item {
  font-size: 15px;
  color: #333;
  text-decoration: none;
  font-weight: 500;
  position: relative;
  transition: color 0.3s;
  padding: 8px 0;
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  transform: scaleX(0);
  transition: transform 0.3s;
}

.nav-item:hover,
.nav-item.active {
  color: #FF9A9E;
}

.nav-item.active::after {
  transform: scaleX(1);
}

.nav-item:hover::after {
  transform: scaleX(1);
}

/* 右侧操作区 */
.header-actions {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-shrink: 0;
}

/* 搜索框 */
.search-wrapper {
  position: relative;
  width: 380px;
  display: flex;
  align-items: center;
  border: 2px solid #e8e8e8;
  border-radius: 24px;
  background: #fff;
  transition: all 0.3s;
  overflow: hidden;
}

.search-wrapper:focus-within {
  border-color: #FF9A9E;
  box-shadow: 0 0 0 3px rgba(255, 154, 158, 0.15);
}

.search-icon {
  position: absolute;
  left: 16px;
  font-size: 18px;
  color: #999;
  z-index: 1;
  pointer-events: none;
}

.search-input {
  flex: 1;
  height: 42px;
  padding: 0 16px 0 44px;
  border: none;
  font-size: 14px;
  outline: none;
  background: transparent;
}

.search-input::placeholder {
  color: #bbb;
}

.search-btn {
  height: 42px;
  padding: 0 32px;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  color: white;
  border: none;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.search-btn:hover {
  background: linear-gradient(135deg, #FF7B7F 0%, #FFB8D8 100%);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.4);
}

.search-btn:active {
  transform: scale(0.98);
}

/* 城市选择 */
.city-select {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #f8f8f8;
  border-radius: 20px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.city-select:hover {
  background: #efefef;
  color: #FF9A9E;
}

.city-select .el-icon {
  font-size: 16px;
}

.city-name {
  font-weight: 500;
}

/* 操作项 */
.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.action-item .el-icon {
  font-size: 20px;
}

.action-item:hover {
  background: #f5f5f5;
}

/* 购物车 */
.cart-item {
  position: relative;
}

.cart-item.active {
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.1) 0%, rgba(254, 207, 239, 0.1) 100%);
  color: #FF9A9E;
}

.cart-item :deep(.el-badge__content) {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
}

/* 登录按钮 */
.login-btn {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  color: white;
}

.login-btn:hover {
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.4);
  transform: translateY(-1px);
}

/* 用户信息 */
.user-item {
  gap: 10px;
}

.username {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-dropdown :deep(.el-dropdown-menu) {
  margin-top: 8px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .city-select {
    display: none;
  }
  
  .search-wrapper {
    width: 300px;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .search-wrapper {
    width: 200px;
  }
}
</style>
