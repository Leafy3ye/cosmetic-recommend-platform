<template>
  <div class="profile-page">
    <!-- 顶部导航栏 -->
    <AppHeader />

    <div class="profile-content">
      <!-- 顶部用户信息卡片 -->
    <div class="user-header-card">
      <div class="user-info-section">
        <div class="avatar-wrapper" @click="handleAvatarClick">
          <el-avatar :size="80" :src="userInfo?.avatar" :icon="UserFilled" class="user-avatar" />
          <div class="avatar-overlay">
            <el-icon><Camera /></el-icon>
            <span>更换头像</span>
          </div>
        </div>
        <div class="user-details">
          <div class="user-name-row">
            <h2>{{ userInfo?.nickname || '用户' }}</h2>
            <el-tag :type="getRoleType(userInfo?.role)" size="large">
              {{ getRoleText(userInfo?.role) }}
            </el-tag>
          </div>
          <p class="username">{{ userInfo?.username }}</p>
          <!-- 只有普通用户显示管理收货地址 -->
          <el-button 
            v-if="userInfo?.role === 0"
            size="small" 
            class="address-btn" 
            @click="goToAddressManagement"
            :icon="Location"
          >
            管理收货地址
          </el-button>
        </div>
      </div>
      <!-- 隐藏统计数据和领券中心 -->
      <!-- <div class="user-stats">...</div> -->
      <!-- <el-button>...</el-button> -->
    </div>

    <!-- 隐藏的上传头像input -->
    <input 
      ref="avatarInput" 
      type="file" 
      accept="image/*" 
      style="display: none" 
      @change="handleAvatarChange"
    />

    <!-- 内容区域 - 根据用户角色显示不同内容 -->
    <div class="content-grid">
      <!-- 普通用户内容 -->
      <template v-if="userInfo?.role === 0">
        <!-- 我的订单 -->
        <div class="section-card orders-card">
          <div class="card-header">
            <h3>
              <el-icon><ShoppingBag /></el-icon>
              我的订单
            </h3>
            <el-button text @click="goToOrders()">查看全部 ></el-button>
          </div>
          <div class="order-stats">
            <div class="order-stat-item" @click="goToOrders('0')">
              <el-badge :value="orderStats.unpaid" :hidden="!orderStats.unpaid" :max="99">
                <div class="stat-icon">
                  <el-icon :size="32"><WalletFilled /></el-icon>
                </div>
              </el-badge>
              <span>待付款</span>
            </div>
            <div class="order-stat-item" @click="goToOrders('1')">
              <el-badge :value="orderStats.unshipped" :hidden="!orderStats.unshipped" :max="99">
                <div class="stat-icon">
                  <el-icon :size="32"><Box /></el-icon>
                </div>
              </el-badge>
              <span>待发货</span>
            </div>
            <div class="order-stat-item" @click="goToOrders('2')">
              <el-badge :value="orderStats.shipped" :hidden="!orderStats.shipped" :max="99">
                <div class="stat-icon">
                  <el-icon :size="32"><Van /></el-icon>
                </div>
              </el-badge>
              <span>待收货</span>
            </div>
            <div class="order-stat-item" @click="goToOrders('3')">
              <el-badge :value="orderStats.toReview" :hidden="!orderStats.toReview" :max="99">
                <div class="stat-icon">
                  <el-icon :size="32"><ChatDotRound /></el-icon>
                </div>
              </el-badge>
              <span>待评价</span>
            </div>
            <div class="order-stat-item" @click="goToOrders('all')">
              <div class="stat-icon">
                <el-icon :size="32"><RefreshLeft /></el-icon>
              </div>
              <span>退款/售后</span>
            </div>
          </div>
        </div>

        <!-- 我的收藏 -->
        <div class="section-card favorites-card">
          <div class="card-header">
            <h3>
              <el-icon><Star /></el-icon>
              我的收藏
            </h3>
            <el-button text @click="goToFavorite">></el-button>
          </div>
          <div class="favorites-preview">
            <div v-if="favoriteProducts.length === 0" class="empty-state">
              <el-icon :size="48"><Star /></el-icon>
              <p>暂无收藏</p>
            </div>
            <div v-else class="product-grid">
              <div v-for="item in favoriteProducts" :key="item.id" class="product-item" @click="goToProduct(item.id)">
                <img :src="item.image || defaultImage" :alt="item.name" />
                <p class="product-name">{{ item.name }}</p>
                <p class="product-price">¥{{ item.price }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 我的足迹 -->
        <div class="section-card footprint-card">
          <div class="card-header">
            <h3>
              <el-icon><Clock /></el-icon>
              我的足迹
            </h3>
            <el-button text @click="goToFootprint">></el-button>
          </div>
          <div class="footprint-preview">
            <div v-if="footprintProducts.length === 0" class="empty-state">
              <el-icon :size="48"><Clock /></el-icon>
              <p>暂无浏览记录</p>
            </div>
            <div v-else class="product-grid">
              <div v-for="item in footprintProducts" :key="item.id" class="product-item" @click="goToProduct(item.id)">
                <img :src="item.image || defaultImage" :alt="item.name" />
                <p class="product-name">{{ item.name }}</p>
                <p class="product-price">¥{{ item.price }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 购物车 -->
        <div class="section-card cart-card">
          <div class="card-header">
            <h3>
              <el-icon><ShoppingCart /></el-icon>
              购物车
            </h3>
            <el-button text @click="goToCart">></el-button>
          </div>
          <div class="cart-preview">
            <div v-if="cartProducts.length === 0" class="empty-state">
              <el-icon :size="48"><ShoppingCart /></el-icon>
              <p>购物车是空的</p>
            </div>
            <div v-else class="product-grid">
              <div v-for="item in cartProducts" :key="item.id" class="product-item" @click="goToProduct(item.productId)">
                <img :src="item.productImage || defaultImage" :alt="item.productName" />
                <p class="product-name">{{ item.productName }}</p>
                <p class="product-price">¥{{ item.price }}</p>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 商家用户内容 -->
      <template v-else-if="userInfo?.role === 1">
        <!-- 订单管理 -->
        <div class="section-card orders-card">
          <div class="card-header">
            <h3>
              <el-icon><ShoppingBag /></el-icon>
              订单管理
            </h3>
            <el-button text @click="goToMerchantOrders()">查看全部 ></el-button>
          </div>
          <div class="order-stats">
            <div class="order-stat-item" @click="goToMerchantOrders('1')">
              <el-badge :value="merchantOrderStats.toShipCount" :hidden="!merchantOrderStats.toShipCount" :max="99">
                <div class="stat-icon">
                  <el-icon :size="32"><Box /></el-icon>
                </div>
              </el-badge>
              <span>待发货</span>
            </div>
            <div class="order-stat-item" @click="goToMerchantOrders('2')">
              <el-badge :value="merchantOrderStats.shippedCount" :hidden="!merchantOrderStats.shippedCount" :max="99">
                <div class="stat-icon">
                  <el-icon :size="32"><Van /></el-icon>
                </div>
              </el-badge>
              <span>待收货</span>
            </div>
            <div class="order-stat-item" @click="goToMerchantOrders('3')">
              <el-badge :value="merchantOrderStats.completedCount" :hidden="!merchantOrderStats.completedCount" :max="99">
                <div class="stat-icon">
                  <el-icon :size="32"><Select /></el-icon>
                </div>
              </el-badge>
              <span>已完成</span>
            </div>
            <div class="order-stat-item" @click="goToMerchantOrders('refund')">
              <div class="stat-icon">
                <el-icon :size="32"><RefreshLeft /></el-icon>
              </div>
              <span>退款/售后</span>
            </div>
          </div>
        </div>

        <!-- 商品管理 -->
        <div class="section-card merchant-card">
          <div class="card-header">
            <h3>
              <el-icon><Goods /></el-icon>
              商品管理
            </h3>
            <el-button text @click="goToProductManagement">管理商品 ></el-button>
          </div>
          <div v-loading="statsLoading" class="merchant-stats">
            <div class="merchant-stat-item" @click="goToProductManagement('on_sale')">
              <div class="stat-number">{{ merchantStats.onSaleProducts }}</div>
              <div class="stat-label">在售商品</div>
            </div>
            <div class="merchant-stat-item" @click="goToProductManagement('off_sale')">
              <div class="stat-number">{{ merchantStats.offSaleProducts }}</div>
              <div class="stat-label">已下架</div>
            </div>
            <div class="merchant-stat-item" @click="goToProductManagement('low_stock')">
              <div class="stat-number warning">{{ merchantStats.lowStockProducts }}</div>
              <div class="stat-label">库存预警</div>
            </div>
          </div>
        </div>

        <!-- 销售统计 -->
        <div class="section-card merchant-card">
          <div class="card-header">
            <h3>
              <el-icon><TrendCharts /></el-icon>
              销售统计
            </h3>
            <el-button text @click="goToSalesReport">查看详情 ></el-button>
          </div>
          <div v-loading="statsLoading" class="merchant-stats">
            <div class="merchant-stat-item">
              <div class="stat-number highlight">¥{{ merchantStats.todaySales }}</div>
              <div class="stat-label">今日销售额</div>
            </div>
            <div class="merchant-stat-item">
              <div class="stat-number">{{ merchantStats.todayOrders }}</div>
              <div class="stat-label">今日订单数</div>
            </div>
            <div class="merchant-stat-item">
              <div class="stat-number">¥{{ merchantStats.monthSales }}</div>
              <div class="stat-label">本月销售额</div>
            </div>
          </div>
        </div>

        <!-- 店铺信息 -->
        <div class="section-card merchant-card">
          <div class="card-header">
            <h3>
              <el-icon><Shop /></el-icon>
              店铺信息
            </h3>
            <el-button text @click="goToStoreSettings">管理店铺 ></el-button>
          </div>
          <div v-loading="statsLoading" class="store-info">
            <div class="info-item">
              <span class="label">店铺名称：</span>
              <span class="value">{{ userInfo?.nickname || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话：</span>
              <span class="value">{{ userInfo?.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">店铺评分：</span>
              <el-rate v-model="merchantStats.rating" disabled show-score text-color="#ff9800" />
            </div>
          </div>
        </div>
      </template>

      <!-- 管理员用户内容(暂时显示商家内容) -->
      <template v-else-if="userInfo?.role === 2">
        <!-- 可以后续扩展管理员专用界面 -->
        <div class="section-card full-card">
          <div class="card-header">
            <h3>
              <el-icon><Setting /></el-icon>
              管理员控制台
            </h3>
          </div>
          <div class="admin-panel">
            <el-button type="primary" @click="goToUserManagement">用户管理</el-button>
            <el-button type="primary" @click="goToProductManagement">商品管理</el-button>
            <el-button type="primary" @click="goToMerchantOrders">订单管理</el-button>
            <el-button type="primary" @click="goToSystemSettings">系统设置</el-button>
          </div>
        </div>
      </template>
    </div>

      <!-- 退出登录按钮 -->
      <div class="logout-section">
        <el-button size="large" class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </div>
    </div>

    <!-- 底部 -->
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  UserFilled, ShoppingBag, Star, SwitchButton, Box,
  WalletFilled, Van, ChatDotRound, RefreshLeft,
  Clock, ShoppingCart, Location, Camera,
  Goods, TrendCharts, Shop, Setting, Select
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getCartList } from '@/api/cart'
import { getHotProducts } from '@/api/product'
import { getFavoriteProducts } from '@/api/favorite'
import { getHistoryProducts } from '@/api/behavior'
import { uploadAvatar, getMerchantStats } from '@/api/user'
import { getUserOrderPage, getMerchantOrderPage } from '@/api/order'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const defaultImage = 'https://via.placeholder.com/120x120?text=Product'

// 头像上传input引用
const avatarInput = ref(null)

// 订单统计（各状态数量）
const orderStats = ref({
  unpaid: 0,     // 待付款(0)
  unshipped: 0,  // 待发货(1)
  shipped: 0,    // 待收货(2)
  toReview: 0    // 待评价(3)
})

// 收藏商品列表（示例数据，实际应从API获取）
const favoriteProducts = ref([])

// 浏览足迹列表（示例数据，实际应从API获取）
const footprintProducts = ref([])

// 购物车商品列表
const cartProducts = ref([])

// 商家订单统计（各状态数量）
const merchantOrderStats = ref({
  toShipCount: 0,      // 待发货(1)
  shippedCount: 0,     // 待收货(2)
  completedCount: 0,   // 新增已完成（未查看的）
  _totalCompleted: 0   // 已完成总数（用于持久化）
})

// 商家统计数据
const merchantStats = ref({
  toShip: 0,           // 待发货订单数
  refund: 0,           // 退款/售后订单数
  onSaleProducts: 0,   // 在售商品数
  offSaleProducts: 0,  // 已下架商品数
  lowStockProducts: 0, // 库存预警商品数
  todaySales: 0,       // 今日销售额
  todayOrders: 0,      // 今日订单数
  monthSales: 0,       // 本月销售额
  rating: 0            // 店铺评分
})

// 商家统计数据加载状态
const statsLoading = ref(false)

onMounted(async () => {
  if (userInfo.value?.role === 0) {
    loadOrderStats()
    await loadCartProducts()
    await loadRecommendProducts()
  } else if (userInfo.value?.role === 1) {
    loadMerchantOrderStats()
    await loadMerchantStats()
  }
})

// 加载各状态订单数量（一次查全部，前端计数）
const loadOrderStats = async () => {
  try {
    const userId = userInfo.value?.userId
    if (!userId) return
    const res = await getUserOrderPage(userId, { current: 1, size: 200 })
    const orders = res.data?.records || []
    orderStats.value = {
      unpaid: orders.filter(o => o.status === 0).length,
      unshipped: orders.filter(o => o.status === 1).length,
      shipped: orders.filter(o => o.status === 2).length,
      toReview: orders.filter(o => o.status === 3).length
    }
  } catch (error) {
    console.error('加载订单统计失败：', error)
  }
}

// 加载商家各状态订单数量
const loadMerchantOrderStats = async () => {
  try {
    const merchantId = userInfo.value?.userId
    if (!merchantId) return
    const res = await getMerchantOrderPage({ merchantId, current: 1, size: 200 })
    const orders = res.data?.records || []

    const totalCompleted = orders.filter(o => o.status === 3 || o.status === 4).length
    const seenKey = `merchant_seen_completed_${merchantId}`
    const seenCount = parseInt(localStorage.getItem(seenKey) || '0', 10)
    const newCompleted = Math.max(0, totalCompleted - seenCount)

    merchantOrderStats.value = {
      toShipCount: orders.filter(o => o.status === 1).length,
      shippedCount: orders.filter(o => o.status === 2).length,
      completedCount: newCompleted,
      _totalCompleted: totalCompleted
    }
  } catch (error) {
    console.error('加载商家订单统计失败：', error)
  }
}

// 加载购物车商品
const loadCartProducts = async () => {
  try {
    const userId = userInfo.value?.userId
    if (userId) {
      const res = await getCartList(userId)
      // 只显示前3个
      cartProducts.value = (res.data || []).slice(0, 3)
    }
  } catch (error) {
    console.error('加载购物车失败：', error)
  }
}

// 加载推荐商品（用于足迹展示）
const loadRecommendProducts = async () => {
  try {
    const userId = userInfo.value?.userId
    if (!userId) return

    const [favRes, historyRes, fallbackRes] = await Promise.all([
      getFavoriteProducts(userId, 3),
      getHistoryProducts(userId, 3),
      getHotProducts(6)
    ])

    favoriteProducts.value = favRes.data || []
    footprintProducts.value = historyRes.data || []

    // 兜底显示，避免页面空白
    const fallbackProducts = fallbackRes.data || []
    if (favoriteProducts.value.length === 0) {
      favoriteProducts.value = fallbackProducts.slice(0, 3)
    }
    if (footprintProducts.value.length === 0) {
      footprintProducts.value = fallbackProducts.slice(3, 6)
    }
  } catch (error) {
    console.error('加载商品失败：', error)
  }
}

// 加载商家统计数据
const loadMerchantStats = async () => {
  try {
    statsLoading.value = true
    const userId = userInfo.value?.userId
    if (!userId) {
      console.error('用户ID不存在')
      return
    }
    
    const res = await getMerchantStats(userId)
    if (res.code === 200 && res.data) {
      merchantStats.value = {
        toShip: res.data.toShip || 0,
        refund: res.data.refund || 0,
        onSaleProducts: res.data.onSaleProducts || 0,
        offSaleProducts: res.data.offSaleProducts || 0,
        lowStockProducts: res.data.lowStockProducts || 0,
        todaySales: res.data.todaySales || 0,
        todayOrders: res.data.todayOrders || 0,
        monthSales: res.data.monthSales || 0,
        rating: res.data.rating || 0
      }
      console.log('商家统计数据加载完成', merchantStats.value)
    }
  } catch (error) {
    console.error('加载商家统计失败：', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    statsLoading.value = false
  }
}

const getRoleText = (role) => {
  const roleMap = {
    0: '普通用户',
    1: '商家',
    2: '管理员'
  }
  return roleMap[role] || '未知'
}

const getRoleType = (role) => {
  const typeMap = {
    0: '',
    1: 'warning',
    2: 'danger'
  }
  return typeMap[role] || ''
}

const handleAvatarClick = () => {
  avatarInput.value?.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  // 验证文件大小（限制为2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  
  // 读取图片并上传到服务器
  const reader = new FileReader()
  reader.onload = async (e) => {
    try {
      const avatarData = e.target.result
      const userId = userInfo.value?.userId
      
      if (!userId) {
        ElMessage.error('用户信息无效')
        return
      }
      
      // 调用API上传头像
      await uploadAvatar(userId, avatarData)
      
      // 更新本地用户信息
      await userStore.refreshUserInfo(userId)
      
      ElMessage.success('头像上传成功')
    } catch (error) {
      console.error('头像上传失败：', error)
      ElMessage.error('头像上传失败')
    }
  }
  reader.readAsDataURL(file)
  
  // 清空input，允许重复选择同一个文件
  event.target.value = ''
}

const goToAddressManagement = () => {
  router.push('/address')
}

const goToOrders = (status) => {
  if (status) {
    router.push({ path: '/orders', query: { status } })
  } else {
    router.push('/orders')
  }
}

const goToFavorite = () => {
  router.push('/favorites')
}

const goToFootprint = () => {
  router.push('/footprints')
}

const goToCart = () => {
  router.push('/cart')
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

// 商家相关跳转方法
const goToMerchantOrders = (status) => {
  if (status === '3') {
    const merchantId = userInfo.value?.userId
    if (merchantId) {
      const seenKey = `merchant_seen_completed_${merchantId}`
      localStorage.setItem(seenKey, String(merchantOrderStats.value._totalCompleted || 0))
    }
    merchantOrderStats.value.completedCount = 0
  }
  if (status && status !== 'refund') {
    router.push({ path: '/merchant/orders', query: { status } })
    return
  }
  router.push('/merchant/orders')
}

const goToProductManagement = (tab) => {
  router.push('/merchant/products')
}

const goToSalesReport = () => {
  ElMessage.info('销售统计功能待开发')
}

const goToStoreSettings = () => {
  ElMessage.info('店铺设置功能待开发')
}

const goToUserManagement = () => {
  ElMessage.info('用户管理功能待开发')
}

const goToSystemSettings = () => {
  ElMessage.info('系统设置功能待开发')
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
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.profile-content {
  flex: 1;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 顶部用户信息卡片 */
.user-header-card {
  background: linear-gradient(135deg, #FFE5EC 0%, #FFF5F8 100%);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 20px rgba(255, 154, 158, 0.1);
  display: flex;
  align-items: center;
  gap: 30px;
  position: relative;
  overflow: hidden;
}

.user-header-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  border-radius: 50%;
}

.user-info-section {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
  z-index: 1;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.user-avatar {
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.2);
  transition: all 0.3s;
}

.avatar-wrapper:hover .user-avatar {
  transform: scale(1.05);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 12px;
  gap: 4px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay .el-icon {
  font-size: 24px;
}

.user-details {
  flex: 1;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 8px;
}

.user-name-row h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.username {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.user-id {
  font-size: 13px;
  color: #999;
  margin: 5px 0;
}

.address-btn {
  margin-top: 10px;
  border-radius: 8px;
  border: 1px solid #FFD4D7;
  color: #FF9A9E;
  background: white;
  transition: all 0.3s;
  max-width: 300px;
}

.address-btn:hover {
  background: rgba(255, 154, 158, 0.1);
  border-color: #FF9A9E;
  transform: translateX(3px);
}

/* 隐藏的统计数据样式（暂时不用） */

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.section-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.section-card:hover {
  box-shadow: 0 4px 20px rgba(255, 154, 158, 0.15);
  transform: translateY(-2px);
}

.orders-card {
  grid-column: 1 / -1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f5f5f5;
}

.card-header h3 {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.card-header h3 .el-icon {
  font-size: 22px;
  color: #FF9A9E;
}

/* 订单统计 */
.order-stats {
  display: flex;
  justify-content: space-around;
  gap: 20px;
}

.order-stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 15px;
  border-radius: 12px;
  transition: all 0.3s;
  flex: 1;
}

.order-stat-item:hover {
  background: rgba(255, 154, 158, 0.05);
  transform: translateY(-3px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.1) 0%, rgba(254, 207, 239, 0.1) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FF9A9E;
  position: relative;
}

.order-stat-item :deep(.el-badge__content) {
  border: none;
  box-shadow: 0 2px 6px rgba(245, 108, 108, 0.4);
}

.order-stat-item span {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.product-item {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  background: #f8f9fa;
  transition: all 0.3s;
}

.product-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.2);
}

.product-item img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  background: white;
}

.product-name {
  padding: 8px 10px 4px;
  font-size: 13px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}

.product-price {
  padding: 0 10px 8px;
  font-size: 16px;
  font-weight: 700;
  color: #FF9A9E;
  margin: 0;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #ccc;
}

.empty-state p {
  margin-top: 15px;
  font-size: 14px;
  color: #999;
}

/* 商家统计 */
.merchant-stats {
  display: flex;
  justify-content: space-around;
  gap: 20px;
  padding: 10px 0;
}

.merchant-stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 20px;
  border-radius: 12px;
  transition: all 0.3s;
  flex: 1;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.05) 0%, rgba(254, 207, 239, 0.05) 100%);
}

.merchant-stat-item:hover {
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.1) 0%, rgba(254, 207, 239, 0.1) 100%);
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.15);
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-number.highlight {
  color: #FF9A9E;
  font-size: 32px;
}

.stat-number.warning {
  color: #f56c6c;
}

.stat-label {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

/* 店铺信息 */
.store-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-size: 14px;
  color: #666;
  width: 100px;
  flex-shrink: 0;
}

.info-item .value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 管理员面板 */
.full-card {
  grid-column: 1 / -1;
}

.admin-panel {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  padding: 20px 0;
}

.admin-panel .el-button {
  flex: 1;
  min-width: 150px;
  border-radius: 12px;
  padding: 20px;
  font-size: 16px;
  font-weight: 600;
}

/* 退出登录 */
.logout-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.logout-btn {
  min-width: 200px;
  border-radius: 25px;
  border: 2px solid #f56c6c;
  color: #f56c6c;
  font-weight: 600;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #f56c6c;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(245, 108, 108, 0.3);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .user-stats {
    flex-direction: column;
    gap: 15px;
  }
  
  .merchant-stats {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .user-header-card {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .order-stats {
    flex-wrap: wrap;
  }
  
  .order-stat-item {
    flex: 0 0 calc(33.333% - 14px);
  }
  
  .merchant-stats {
    gap: 10px;
  }
  
  .admin-panel .el-button {
    min-width: 100%;
  }
}
</style>
