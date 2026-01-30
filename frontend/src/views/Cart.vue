<template>
  <div class="cart-page">
    <h2>购物车</h2>
    
    <div class="cart-content" v-loading="loading">
      <el-empty v-if="!loading && cartList.length === 0" description="购物车是空的" />
      
      <div v-else>
        <el-table :data="cartList" style="width: 100%">
          <el-table-column type="selection" width="55" />
          
          <el-table-column label="商品" min-width="300">
            <template #default="{ row }">
              <div class="product-info">
                <img :src="row.productImage || defaultImage" class="product-image" />
                <div>
                  <div class="product-name">{{ row.productName }}</div>
                  <div class="product-brand">{{ row.productBrand }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="price" label="单价" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="数量" width="150">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="99"
                size="small"
                @change="handleQuantityChange(row)"
              />
            </template>
          </el-table-column>
          
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" text @click="handleDelete(row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="cart-footer">
          <div class="footer-left">
            <el-button @click="goBack">继续购物</el-button>
            <el-button type="danger" text @click="handleClearCart">清空购物车</el-button>
          </div>
          
          <div class="footer-right">
            <div class="total-price">
              总计：<span class="price">¥{{ totalPrice }}</span>
            </div>
            <el-button type="primary" size="large" @click="handleCheckout">
              去结算
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, updateCartQuantity, deleteCartItem, clearCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const cartList = ref([])
const defaultImage = 'https://via.placeholder.com/80x80?text=Product'

const totalPrice = computed(() => {
  return cartList.value.reduce((sum, item) => {
    return sum + item.price * item.quantity
  }, 0).toFixed(2)
})

onMounted(() => {
  loadCartList()
})

const loadCartList = async () => {
  try {
    loading.value = true
    const userId = userStore.userInfo?.userId
    if (!userId) {
      router.push('/login')
      return
    }
    
    const res = await getCartList(userId)
    cartList.value = res.data || []
  } catch (error) {
    console.error('加载购物车失败：', error)
  } finally {
    loading.value = false
  }
}

const handleQuantityChange = async (row) => {
  try {
    await updateCartQuantity({
      cartId: row.id,
      quantity: row.quantity
    })
  } catch (error) {
    console.error('更新数量失败：', error)
  }
}

const handleDelete = async (cartId) => {
  try {
    await ElMessageBox.confirm('确定要删除这件商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCartItem(cartId)
    ElMessage.success('删除成功')
    loadCartList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败：', error)
    }
  }
}

const handleClearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const userId = userStore.userInfo?.userId
    await clearCart(userId)
    ElMessage.success('购物车已清空')
    loadCartList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空购物车失败：', error)
    }
  }
}

const handleCheckout = () => {
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车是空的')
    return
  }
  
  ElMessage.info('结算功能待开发')
  // TODO: 实现结算功能
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.cart-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  min-height: calc(100vh - 120px);
}

.cart-page h2 {
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  background: #f5f5f5;
}

.product-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}

.product-brand {
  font-size: 14px;
  color: #999;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.total-price {
  font-size: 18px;
}

.total-price .price {
  font-size: 24px;
  margin-left: 10px;
}
</style>
