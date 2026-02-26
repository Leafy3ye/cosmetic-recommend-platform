<template>
  <div class="cart-page">
    <!-- 顶部导航栏 -->
    <AppHeader active-page="cart" />
    
    <div class="cart-content" v-loading="loading">
      <!-- 空购物车状态 -->
      <div v-if="!loading && cartList.length === 0" class="cart-container empty-state-container">
        <div class="empty-cart">
          <el-empty description="购物车是空的">
            <el-button type="primary" @click="goBack" class="go-shopping-btn">去逛逛</el-button>
          </el-empty>
        </div>
      </div>
      
      <!-- 购物车列表 -->
      <div v-else class="cart-container">
        <!-- 操作栏 -->
        <div class="cart-operations">
          <el-checkbox 
            v-model="selectAll" 
            @change="handleSelectAll"
            :indeterminate="isIndeterminate"
          >
            全选
          </el-checkbox>
          <div class="operation-info">
            <span class="selected-count">已选 {{ selectedCount }} 件商品</span>
            <el-button 
              @click="handleDeleteSelected"
              :disabled="selectedCount === 0"
              class="operation-btn delete-selected-btn"
              size="small"
            >
              删除选中
            </el-button>
            <el-button 
              @click="handleClearCart" 
              class="operation-btn clear-btn"
              size="small"
            >
              清空购物车
            </el-button>
          </div>
        </div>

        <!-- 购物车商品列表 -->
        <div class="cart-items">
          <div 
            v-for="item in cartList" 
            :key="item.id" 
            class="cart-item"
            :class="{ 'selected': item.selected }"
          >
            <el-checkbox 
              v-model="item.selected" 
              @change="handleItemSelect"
            />
            
            <div class="item-image" @click="goToProduct(item.productId)">
              <img :src="item.productImage || defaultImage" :alt="item.productName" @error="handleImageError" />
              <div class="image-overlay">
                <span>查看详情</span>
              </div>
            </div>
            
            <div class="item-info">
              <div class="item-name" @click="goToProduct(item.productId)">
                {{ item.productName }}
              </div>
              <div class="item-brand">{{ item.productBrand }}</div>
              <div class="item-tags">
                <el-tag size="small" type="success">正品保证</el-tag>
                <el-tag size="small" type="info">七天退换</el-tag>
              </div>
            </div>
            
            <div class="item-price">
              <div class="current-price">¥{{ formatPrice(item.price) }}</div>
            </div>
            
            <div class="item-quantity">
              <el-input-number
                v-model="item.quantity"
                :min="1"
                :max="99"
                @change="handleQuantityChange(item)"
              />
            </div>
            
            <div class="item-subtotal">
              <div class="subtotal-price">¥{{ formatPrice(item.price * item.quantity) }}</div>
            </div>
            
            <div class="item-actions">
              <el-button 
                @click="handleFavorite(item)"
                :icon="Star"
                class="action-btn favorite-btn"
                size="small"
              >
                移入收藏
              </el-button>
              <el-button 
                @click="handleDelete(item.id)"
                :icon="Delete"
                class="action-btn delete-btn"
                size="small"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 底部结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox 
              v-model="selectAll" 
              @change="handleSelectAll"
              :indeterminate="isIndeterminate"
            >
              全选
            </el-checkbox>
            <el-button @click="goBack" class="continue-shopping-btn">
              继续购物
            </el-button>
          </div>
          
          <div class="footer-right">
            <div class="price-info">
              <div class="price-item">
                <span class="label">商品总额：</span>
                <span class="value">¥{{ selectedTotalPrice }}</span>
              </div>
              <div class="price-item discount">
                <span class="label">优惠：</span>
                <span class="value">-¥0.00</span>
              </div>
              <div class="price-item total">
                <span class="label">应付总额：</span>
                <span class="value price-highlight">¥{{ selectedTotalPrice }}</span>
              </div>
            </div>
            <el-button 
              type="primary" 
              size="large" 
              @click="handleCheckout"
              :disabled="selectedCount === 0"
              class="checkout-button"
            >
              结算 ({{ selectedCount }})
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, Delete } from '@element-plus/icons-vue'
import { getCartList, updateCartQuantity, deleteCartItem, clearCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { getUserAddress } from '@/api/user'
import { useUserStore } from '@/stores/user'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const cartList = ref([])
const selectAll = ref(false)
const defaultImage = 'https://via.placeholder.com/120x120?text=Product'

// 已选商品数量
const selectedCount = computed(() => {
  return cartList.value.filter(item => item.selected).length
})

// 全选状态（部分选中）
const isIndeterminate = computed(() => {
  const count = selectedCount.value
  return count > 0 && count < cartList.value.length
})

// 已选商品总价
const selectedTotalPrice = computed(() => {
  return cartList.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
    .toFixed(2)
})

// 所有商品总价
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
    
    const res = await getCartList(userId)
    // 为每个商品添加 selected 属性
    cartList.value = (res.data || []).map(item => ({
      ...item,
      selected: false
    }))
  } catch (error) {
    console.error('加载购物车失败：', error)
  } finally {
    loading.value = false
  }
}

// 全选/取消全选
const handleSelectAll = (value) => {
  cartList.value.forEach(item => {
    item.selected = value
  })
}

// 单个商品选中状态改变
const handleItemSelect = () => {
  const allSelected = cartList.value.every(item => item.selected)
  selectAll.value = allSelected
}

const handleQuantityChange = async (row) => {
  try {
    await updateCartQuantity({
      cartId: row.id,
      quantity: row.quantity
    })
    ElMessage.success('数量已更新')
  } catch (error) {
    console.error('更新数量失败：', error)
    ElMessage.error('更新数量失败')
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

// 删除选中的商品
const handleDeleteSelected = async () => {
  const selectedItems = cartList.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请先选择要删除的商品')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.length} 件商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 批量删除
    await Promise.all(selectedItems.map(item => deleteCartItem(item.id)))
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

// 移入收藏
const handleFavorite = (item) => {
  ElMessage.success(`已将 ${item.productName} 移入收藏`)
  // TODO: 实现移入收藏功能
}

const handleCheckout = () => {
  const selectedItems = cartList.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请先选择要结算的商品')
    return
  }

  const firstItem = selectedItems[0]
  const totalAmount = Number(
    selectedItems.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0).toFixed(2)
  )

  ElMessageBox.confirm(
    `确认提交订单？共 ${selectedItems.length} 件商品，金额 ¥${totalAmount.toFixed(2)}`,
    '确认结算',
    {
      confirmButtonText: '提交订单',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      const userId = userStore.userInfo?.userId
      if (!userId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      const addressRes = await getUserAddress(userId)
      const addressData = addressRes.data || {}
      const receiverName = addressData.receiverName || userStore.userInfo?.nickname
      const receiverPhone = addressData.receiverPhone || userStore.userInfo?.phone
      const finalAddress = [
        addressData.province,
        addressData.city,
        addressData.district,
        addressData.detailAddress
      ].filter(Boolean).join(' ')

      if (!receiverName || !receiverPhone || !finalAddress) {
        ElMessage.warning('请先在地址管理中完善默认收货地址')
        router.push('/address')
        return
      }

      await createOrder({
        userId,
        merchantId: firstItem.merchantId || null,
        totalAmount,
        payAmount: totalAmount,
        receiverName,
        receiverPhone,
        receiverAddress: finalAddress,
        remark: `购物车结算，共${selectedItems.length}件`,
        items: selectedItems.map(item => ({
          productId: item.productId,
          productName: item.productName,
          productImage: item.productImage,
          price: Number(item.price),
          quantity: item.quantity
        }))
      })

      // 下单成功后移除本次结算商品
      await Promise.all(selectedItems.map(item => deleteCartItem(item.id)))

      ElMessage.success('订单创建成功，请前往订单页点击付款')
      router.push('/orders')
    } catch (error) {
      console.error('创建订单失败：', error)
      ElMessage.error('创建订单失败，请稍后重试')
    }
  }).catch(() => {})
}

const goBack = () => {
  router.push('/')
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 格式化价格显示
const formatPrice = (price) => {
  if (price === null || price === undefined || isNaN(price)) {
    return '0.00'
  }
  return Number(price).toFixed(2)
}

// 图片加载失败时使用默认图片
const handleImageError = (e) => {
  e.target.src = defaultImage
}
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

/* 内容区域 */
.cart-content {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 空购物车 */
.empty-cart {
  width: 100%;
  min-height: 420px;
  padding: 40px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-cart :deep(.el-empty) {
  width: 100%;
}

.empty-state-container {
  padding: 0;
}

.go-shopping-btn {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
  border-radius: 25px;
  padding: 12px 40px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.3);
}

.go-shopping-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.4);
  background: linear-gradient(135deg, #FF8A8E 0%, #FEBFDF 100%);
}

/* 购物车容器 */
.cart-container {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 操作栏 */
.cart-operations {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.operation-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.selected-count {
  color: #666;
  font-size: 14px;
}

/* 购物车商品列表 */
.cart-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: white;
  border: 2px solid #e8e8e8;
  border-radius: 16px;
  transition: all 0.3s;
}

.cart-item:hover {
  border-color: #FFD4D7;
  box-shadow: 0 4px 16px rgba(255, 154, 158, 0.15);
  transform: translateY(-2px);
}

.cart-item.selected {
  border-color: #FF9A9E;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.05) 0%, rgba(254, 207, 239, 0.05) 100%);
  box-shadow: 0 2px 12px rgba(255, 154, 158, 0.1);
}

.item-image {
  position: relative;
  width: 120px;
  height: 120px;
  flex-shrink: 0;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.item-image:hover img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: white;
  font-size: 14px;
}

.item-image:hover .image-overlay {
  opacity: 1;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.3s;
}

.item-name:hover {
  color: #667eea;
}

.item-brand {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.item-tags {
  display: flex;
  gap: 8px;
}

.item-price {
  width: 100px;
  text-align: center;
}

.current-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.item-quantity {
  width: 150px;
  display: flex;
  justify-content: center;
}

.item-subtotal {
  width: 120px;
  text-align: center;
}

.subtotal-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.item-actions {
  width: 140px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 操作按钮样式 */
.action-btn {
  width: 100%;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
  border: 1px solid transparent;
  text-align: center;
}

/* 重置按钮内部样式 */
.action-btn :deep(.el-button__text-wrapper) {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
}

/* 图标样式 */
.action-btn :deep(.el-icon) {
  margin: 0;
  vertical-align: middle;
}

.favorite-btn {
  color: #FF9A9E;
  background: rgba(255, 154, 158, 0.1);
  border-color: #FFD4D7;
}

.favorite-btn:hover {
  color: white;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border-color: #FF9A9E;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.3);
}

.delete-btn {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
  border-color: #ffd4d4;
  transform: translateX(-12px);
}

.delete-btn:hover {
  color: white;
  background: linear-gradient(135deg, #f56c6c 0%, #ff8585 100%);
  border-color: #f56c6c;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
}

/* 顶部操作栏按钮 */
.operation-btn {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s;
  padding: 8px 16px;
}

.delete-selected-btn {
  color: #f56c6c;
  background: white;
  border: 1px solid #ffd4d4;
}

.delete-selected-btn:hover:not(:disabled) {
  color: white;
  background: #f56c6c;
  border-color: #f56c6c;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
}

.delete-selected-btn:disabled {
  color: #ccc;
  background: #f5f5f5;
  border-color: #e8e8e8;
  cursor: not-allowed;
}

.clear-btn {
  color: #FF9A9E;
  background: white;
  border: 1px solid #FFD4D7;
}

.clear-btn:hover {
  color: white;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border-color: #FF9A9E;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 154, 158, 0.3);
}

/* 底部结算栏 */
.cart-footer {
  position: sticky;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(to right, #f8f9fa, #fff);
  border-top: 2px solid #e8e8e8;
  border-radius: 12px;
  margin-top: 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.continue-shopping-btn {
  color: #666;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 10px 20px;
  transition: all 0.3s;
}

.continue-shopping-btn:hover {
  color: #FF9A9E;
  border-color: #FFD4D7;
  background: rgba(255, 154, 158, 0.05);
  transform: translateY(-1px);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.price-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: right;
}

.price-item {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  font-size: 14px;
}

.price-item .label {
  color: #666;
}

.price-item .value {
  color: #333;
  font-weight: 500;
}

.price-item.discount .value {
  color: #67c23a;
}

.price-item.total {
  font-size: 16px;
  padding-top: 8px;
  border-top: 1px solid #e8e8e8;
}

.price-highlight {
  color: #f56c6c !important;
  font-size: 24px !important;
  font-weight: bold !important;
}

.checkout-button {
  min-width: 180px;
  height: 54px;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
  border-radius: 12px;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.3);
}

.checkout-button:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(255, 154, 158, 0.4);
  background: linear-gradient(135deg, #FF8A8E 0%, #FEBFDF 100%);
}

.checkout-button:active:not(:disabled) {
  transform: translateY(-1px);
}

.checkout-button:disabled {
  background: linear-gradient(135deg, #e0e0e0 0%, #f5f5f5 100%);
  box-shadow: none;
  cursor: not-allowed;
  opacity: 0.6;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .cart-item {
    flex-wrap: wrap;
  }
  
  .item-info {
    flex: 1 1 300px;
  }
  
  .item-price,
  .item-quantity,
  .item-subtotal,
  .item-actions {
    flex: 1 1 auto;
  }
}

@media (max-width: 768px) {
  .cart-operations {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .cart-footer {
    flex-direction: column;
    gap: 15px;
  }
  
  .footer-left,
  .footer-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .checkout-button {
    width: 100%;
  }
  
  .item-image {
    width: 80px;
    height: 80px;
  }
}
</style>
