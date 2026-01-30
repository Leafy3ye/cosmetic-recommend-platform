<template>
  <div class="side-toolbar">
    <!-- 账号 -->
    <div class="tool-item" @click="handleAccount">
      <el-icon><User /></el-icon>
      <span>账号</span>
    </div>

    <!-- 购物袋 -->
    <div class="tool-item cart-item" @click="handleCart">
      <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
        <el-icon><ShoppingBag /></el-icon>
      </el-badge>
      <span>购物袋</span>
    </div>

    <!-- 签到 -->
    <div class="tool-item" @click="handleCheckIn">
      <el-icon><Calendar /></el-icon>
      <span>签到</span>
    </div>

    <!-- 收藏 -->
    <div class="tool-item" @click="handleFavorite">
      <el-icon><Star /></el-icon>
      <span>收藏</span>
    </div>

    <!-- 历史 -->
    <div class="tool-item" @click="handleHistory">
      <el-icon><Clock /></el-icon>
      <span>历史</span>
    </div>

    <!-- 客服 -->
    <div class="tool-item" @click="handleCustomerService">
      <el-icon><Service /></el-icon>
      <span>客服</span>
    </div>

    <!-- 反馈 -->
    <div class="tool-item" @click="handleFeedback">
      <el-icon><ChatDotSquare /></el-icon>
      <span>反馈</span>
    </div>

    <!-- 回到顶部 -->
    <div class="tool-item" @click="scrollToTop" v-show="showBackTop">
      <el-icon><Top /></el-icon>
      <span>顶部</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, ShoppingBag, Calendar, Star, Clock, 
  Service, ChatDotSquare, Top 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const cartCount = ref(0)
const showBackTop = ref(false)

// 监听滚动显示回到顶部按钮
const handleScroll = () => {
  showBackTop.value = window.scrollY > 300
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  // TODO: 获取购物车数量
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleAccount = () => {
  if (!userStore.userInfo) {
    router.push('/login')
  } else {
    router.push('/profile')
  }
}

const handleCart = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
  } else {
    router.push('/cart')
  }
}

const handleCheckIn = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
  } else {
    ElMessage.success('签到成功！获得10积分')
  }
}

const handleFavorite = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
  } else {
    ElMessage.info('收藏功能开发中')
  }
}

const handleHistory = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
  } else {
    ElMessage.info('浏览历史功能开发中')
  }
}

const handleCustomerService = () => {
  ElMessage.info('客服咨询：请添加微信 XXX')
}

const handleFeedback = () => {
  ElMessage.info('反馈功能开发中')
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}
</script>

<style scoped>
.side-toolbar {
  position: fixed;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 999;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.tool-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 12px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 1px solid #f0f0f0;
  min-width: 70px;
}

.tool-item:last-child {
  border-bottom: none;
}

.tool-item:hover {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  color: white;
}

.tool-item .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.tool-item span {
  font-size: 12px;
  white-space: nowrap;
}

.cart-item .el-badge {
  margin-bottom: 4px;
}

/* 悬浮效果（使用阴影而不是移动） */
.side-toolbar:hover {
  box-shadow: 0 8px 32px rgba(255, 154, 158, 0.3);
  transform: translateY(-50%) scale(1.02);
}

/* 响应式 */
@media (max-width: 768px) {
  .side-toolbar {
    right: 10px;
  }
  
  .side-toolbar:not(:hover) {
    transform: translateY(-50%) scale(0.9);
  }
  
  .side-toolbar:hover {
    transform: translateY(-50%) scale(0.95);
  }
  
  .tool-item {
    padding: 12px 8px;
    min-width: 60px;
  }
  
  .tool-item .el-icon {
    font-size: 20px;
  }
  
  .tool-item span {
    font-size: 11px;
  }
}
</style>
