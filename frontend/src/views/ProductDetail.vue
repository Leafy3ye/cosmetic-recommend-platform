<template>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'

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
.product-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  min-height: calc(100vh - 120px);
}

.product-image {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image img {
  width: 100%;
  height: auto;
}

.product-info {
  padding: 20px 0;
}

.product-name {
  font-size: 28px;
  color: #333;
  margin-bottom: 15px;
}

.product-brand {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.product-price-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff3f3;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.price-item {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 18px;
  color: #999;
  text-decoration: line-through;
}

.sales-info {
  color: #999;
}

.product-attrs {
  margin: 20px 0;
}

.attr-item {
  margin-bottom: 15px;
  font-size: 16px;
}

.attr-item .label {
  color: #666;
  margin-right: 10px;
}

.quantity-box {
  display: flex;
  align-items: center;
  gap: 15px;
  margin: 30px 0;
}

.quantity-box .label {
  font-size: 16px;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.action-buttons .el-button {
  flex: 1;
}

.product-description {
  padding: 20px 0;
}

.product-description h3 {
  font-size: 20px;
  margin-bottom: 20px;
}

.product-description p {
  font-size: 16px;
  line-height: 1.8;
  color: #666;
}
</style>
