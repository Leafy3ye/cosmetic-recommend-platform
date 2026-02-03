<template>
  <div class="product-detail-container">
    <AppHeader />
    
    <div class="product-detail" v-loading="loading">
      <div class="detail-content" v-if="product">
      <el-row :gutter="40">
        <el-col :xs="24" :md="12">
          <div class="product-image">
            <img :src="product.image || defaultImage" :alt="product.name" />
          </div>
        </el-col>
        
        <el-col :xs="24" :md="12">
          <div class="product-info">
            <h1 class="product-name">{{ product.name }}</h1>
            <p class="product-brand">品牌：{{ product.brand }}</p>
            
            <div class="product-price-box">
              <div class="price-item">
                <span class="label">价格：</span>
                <span class="price">¥{{ product.price }}</span>
                <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
              </div>
              <div class="sales-info">
                已售 {{ product.sales || 0 }} 件
              </div>
            </div>
            
            <el-divider />
            
            <div class="product-attrs">
              <div class="attr-item" v-if="product.skinType">
                <span class="label">适用肤质：</span>
                <span>{{ product.skinType }}</span>
              </div>
              <div class="attr-item" v-if="product.effect">
                <span class="label">功效：</span>
                <span>{{ product.effect }}</span>
              </div>
              <div class="attr-item">
                <span class="label">库存：</span>
                <span>{{ product.stock }} 件</span>
              </div>
            </div>
            
            <el-divider />
            
            <div class="quantity-box">
              <span class="label">数量：</span>
              <el-input-number v-model="quantity" :min="1" :max="product.stock" />
            </div>
            
            <div class="action-buttons">
              <el-button type="primary" size="large" :icon="ShoppingCart" @click="handleAddToCart">
                加入购物车
              </el-button>
              <el-button size="large" @click="goBack">返回</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <el-divider />
      
      <div class="product-description">
        <h3>商品详情</h3>
        <p>{{ product.description || '暂无详细描述' }}</p>
      </div>
    </div>
    </div>
    
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const product = ref(null)
const quantity = ref(1)
const defaultImage = 'https://via.placeholder.com/600x600?text=Product+Image'

onMounted(() => {
  loadProductDetail()
})

const loadProductDetail = async () => {
  try {
    loading.value = true
    const res = await getProductDetail(route.params.id)
    product.value = res.data
  } catch (error) {
    console.error('加载商品详情失败：', error)
    ElMessage.error('商品不存在')
    router.back()
  } finally {
    loading.value = false
  }
}

const handleAddToCart = async () => {
  try {
    const userId = userStore.userInfo?.userId
    if (!userId) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }
    
    // 检查是否为商家账户
    if (userStore.userInfo?.role === 1) {
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
    
    await addToCart({
      userId,
      productId: product.value.id,
      quantity: quantity.value
    })
    
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败：', error)
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.product-detail-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.product-detail {
  max-width: 1600px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 80px;
  background: white;
  min-height: calc(100vh - 120px);
  flex: 1;
}

.detail-content {
  margin-top: 20px;
}

.product-image {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
  background: #f8f9fa;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.product-image:hover {
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.product-image img {
  width: 100%;
  height: auto;
  display: block;
}

.product-info {
  padding: 20px 0;
}

.product-name {
  font-size: 32px;
  color: #1a1a1a;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.3;
}

.product-brand {
  font-size: 16px;
  color: #888;
  margin-bottom: 24px;
}

.product-price-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe8e8 100%);
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  border: 1px solid #ffe0e0;
}

.price-item {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price-item .label {
  font-size: 16px;
  color: #666;
}

.price {
  font-size: 36px;
  color: #ff4d4f;
  font-weight: 700;
}

.original-price {
  font-size: 18px;
  color: #999;
  text-decoration: line-through;
}

.sales-info {
  color: #999;
  font-size: 15px;
}

.product-attrs {
  margin: 24px 0;
  background: #fafafa;
  padding: 20px;
  border-radius: 12px;
}

.attr-item {
  margin-bottom: 16px;
  font-size: 16px;
  display: flex;
  align-items: center;
}

.attr-item:last-child {
  margin-bottom: 0;
}

.attr-item .label {
  color: #666;
  margin-right: 12px;
  min-width: 90px;
  font-weight: 500;
}

.attr-item span:not(.label) {
  color: #333;
}

.quantity-box {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 32px 0;
}

.quantity-box .label {
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.quantity-box :deep(.el-input-number) {
  width: 150px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: 32px;
}

.action-buttons :deep(.el-button) {
  flex: 1;
  height: 50px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
}

.action-buttons :deep(.el-button--primary) {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
}

.action-buttons :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #FF7B7F 0%, #FFB8D8 100%);
  box-shadow: 0 4px 16px rgba(255, 154, 158, 0.4);
  transform: translateY(-2px);
}

.product-description {
  padding: 40px 0;
  margin-top: 40px;
  border-top: 1px solid #eee;
}

.product-description h3 {
  font-size: 24px;
  margin-bottom: 24px;
  color: #1a1a1a;
  font-weight: 600;
}

.product-description p {
  font-size: 16px;
  line-height: 2;
  color: #555;
}

/* 响应式 */
@media (max-width: 1400px) {
  .product-detail {
    padding: 30px 50px;
  }
}

@media (max-width: 768px) {
  .product-detail {
    padding: 20px;
  }
  
  .product-name {
    font-size: 24px;
  }
  
  .price {
    font-size: 28px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons :deep(.el-button) {
    width: 100%;
  }
}
</style>
