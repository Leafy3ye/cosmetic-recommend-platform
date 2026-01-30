<template>
  <div class="profile-page">
    <h2>个人中心</h2>
    
    <el-card class="profile-card">
      <div class="user-info">
        <el-avatar :size="100" :src="userInfo?.avatar" :icon="UserFilled" />
        <div class="info-text">
          <h3>{{ userInfo?.nickname || '用户' }}</h3>
          <p>用户名：{{ userInfo?.username }}</p>
          <p>角色：{{ getRoleText(userInfo?.role) }}</p>
        </div>
      </div>
    </el-card>
    
    <el-card class="menu-card">
      <el-menu>
        <!-- 商家专属菜单 -->
        <template v-if="userInfo?.role === 1">
          <el-menu-item index="0" @click="goToMerchantProducts">
            <el-icon><Box /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="1" @click="goToMerchantOrders">
            <el-icon><ShoppingBag /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
        </template>
        <!-- 普通用户菜单 -->
        <template v-else>
          <el-menu-item index="1" @click="goToOrders">
            <el-icon><ShoppingBag /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="2" @click="goToFavorite">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
        </template>
        <el-menu-item index="3" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, ShoppingBag, Star, SwitchButton, Box } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const getRoleText = (role) => {
  const roleMap = {
    0: '普通用户',
    1: '商家',
    2: '管理员'
  }
  return roleMap[role] || '未知'
}

const goToMerchantProducts = () => {
  router.push('/merchant/products')
}

const goToMerchantOrders = () => {
  router.push('/merchant/orders')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToFavorite = () => {
  ElMessage.info('收藏功能待开发')
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
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 120px);
}

.profile-page h2 {
  margin-bottom: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 20px;
}

.info-text h3 {
  font-size: 24px;
  margin-bottom: 15px;
}

.info-text p {
  margin-bottom: 10px;
  color: #666;
}

.menu-card {
  margin-bottom: 20px;
}
</style>
