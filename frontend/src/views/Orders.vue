<template>
  <div class="orders-container">
    <AppHeader />
    
    <div class="orders-page">
      <h2>我的订单</h2>
      
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="待收货" name="2" />
        <el-tab-pane label="待评价" name="3" />
        <el-tab-pane label="已完成" name="4" />
      </el-tabs>
      
      <div class="orders-list" v-loading="loading">
        <el-empty v-if="!loading && orderList.length === 0" description="暂无订单" />
        
        <div v-else>
          <div class="order-item" v-for="order in orderList" :key="order.id">
            <div class="order-header">
              <span>订单号：{{ order.orderNo }}</span>
              <span>{{ formatTime(order.createTime) }}</span>
              <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
            </div>
            
            <div class="order-body">
              <!-- 商品明细 -->
              <div class="order-items" v-if="order.items && order.items.length">
                <div class="order-item-row" v-for="item in order.items" :key="item.id">
                  <img :src="item.productImage || defaultImage" :alt="item.productName" class="item-thumb" @error="handleImageError" />
                  <div class="item-detail">
                    <span class="item-name">{{ item.productName }}</span>
                    <span class="item-spec">¥{{ item.price }} × {{ item.quantity }}</span>
                  </div>
                  <span class="item-subtotal">¥{{ item.subtotal }}</span>
                </div>
              </div>

              <div class="order-meta">
                <div class="order-info">
                  <p>收货人：{{ order.receiverName }}</p>
                  <p>电话：{{ order.receiverPhone }}</p>
                  <p>地址：{{ order.receiverAddress }}</p>
                </div>
                
                <div class="order-amount">
                  <div>订单总额：<span class="price">¥{{ order.totalAmount }}</span></div>
                  <div>实付金额：<span class="price">¥{{ order.payAmount }}</span></div>
                </div>
              </div>
            </div>
            
            <div class="order-footer">
              <el-button v-if="order.status === 0" type="primary" @click="handlePay(order.id)">
                去支付
              </el-button>
              <el-button v-if="order.status === 0" @click="handleCancel(order.id)">
                取消订单
              </el-button>
              <el-button v-if="order.status === 2" type="primary" @click="handleConfirm(order.id)">
                确认收货
              </el-button>
              <el-button @click="viewDetail(order.id)">查看详情</el-button>
            </div>
          </div>
          
          <div class="pagination">
            <el-pagination
              v-model:current-page="pageParams.current"
              v-model:page-size="pageParams.size"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="loadOrders"
            />
          </div>
        </div>
      </div>
    </div>
    
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrderPage, cancelOrder, payOrder, confirmOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref('all')
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const defaultImage = 'https://via.placeholder.com/60x60?text=Product'

const pageParams = reactive({
  current: 1,
  size: 10,
  status: null
})

onMounted(() => {
  // 支持从个人中心携带订单状态参数跳转
  const queryStatus = route.query.status
  if (queryStatus !== undefined) {
    activeTab.value = String(queryStatus)
    pageParams.status = queryStatus === 'all' ? null : parseInt(queryStatus)
  }
  loadOrders()
})

const loadOrders = async () => {
  try {
    loading.value = true
    const userId = userStore.userInfo?.userId
    if (!userId) {
      router.push('/login')
      return
    }
    
    const res = await getUserOrderPage(userId, pageParams)
    orderList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载订单失败：', error)
  } finally {
    loading.value = false
  }
}

const handleTabClick = (tab) => {
  const tabName = String(tab?.paneName ?? activeTab.value)
  pageParams.current = 1
  pageParams.status = tabName === 'all' ? null : parseInt(tabName)
  loadOrders()
}

const handlePay = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要支付该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    // 模拟支付，默认使用支付宝支付（payType = 1）
    await payOrder(orderId, 1)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败：', error)
    }
  }
}

const handleCancel = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelOrder(orderId)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败：', error)
    }
  }
}

const handleConfirm = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await confirmOrder(orderId)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败：', error)
    }
  }
}

const viewDetail = (orderId) => {
  ElMessage.info('订单详情页面待开发')
  // TODO: 跳转到订单详情页
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '待评价',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: '',
    2: 'primary',
    3: 'success',
    4: 'success',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const handleImageError = (e) => {
  e.target.src = defaultImage
}
</script>

<style scoped>
.orders-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.orders-page {
  max-width: 1600px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 80px;
  min-height: calc(100vh - 120px);
  flex: 1;
}

.orders-page h2 {
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
}

.orders-page :deep(.el-tabs) {
  margin-bottom: 30px;
}

.orders-page :deep(.el-tabs__nav-wrap) {
  padding: 0;
}

.orders-page :deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 30px;
  height: 50px;
  line-height: 50px;
}

.orders-list {
  margin-top: 20px;
}

.order-item {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  margin-bottom: 24px;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.order-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background: linear-gradient(to right, #fafafa, #fff);
  border-bottom: 1px solid #eee;
}

.order-header span:first-child {
  font-size: 15px;
  color: #666;
  font-weight: 500;
}

.order-header span:nth-child(2) {
  font-size: 14px;
  color: #999;
}

.order-body {
  padding: 0;
}

.order-items {
  padding: 20px 30px;
  border-bottom: 1px dashed #eee;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.item-thumb {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  background: #f5f5f5;
  flex-shrink: 0;
}

.item-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.item-detail .item-name {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-detail .item-spec {
  font-size: 13px;
  color: #999;
}

.item-subtotal {
  font-size: 15px;
  font-weight: 600;
  color: #ff4d4f;
  flex-shrink: 0;
}

.order-meta {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 30px;
  gap: 40px;
}

.order-info {
  flex: 1;
}

.order-info p {
  margin-bottom: 12px;
  color: #555;
  font-size: 15px;
  line-height: 1.6;
  display: flex;
  align-items: center;
}

.order-info p::before {
  content: '';
  width: 4px;
  height: 4px;
  background: #FF9A9E;
  border-radius: 50%;
  margin-right: 12px;
}

.order-amount {
  text-align: right;
  min-width: 200px;
}

.order-amount > div {
  margin-bottom: 12px;
  font-size: 15px;
  color: #666;
}

.price {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 20px;
  margin-left: 8px;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 30px;
  background: #fafafa;
  border-top: 1px solid #eee;
}

.order-footer :deep(.el-button) {
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.order-footer :deep(.el-button--primary) {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
}

.order-footer :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #FF7B7F 0%, #FFB8D8 100%);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.4);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-bottom: 20px;
}

.pagination :deep(.el-pagination) {
  gap: 8px;
}

.pagination :deep(.el-pager li) {
  border-radius: 8px;
  min-width: 36px;
  height: 36px;
  line-height: 36px;
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  color: white;
}

/* 空状态优化 */
.orders-list :deep(.el-empty) {
  padding: 80px 0;
}

.orders-list :deep(.el-empty__description) {
  font-size: 16px;
  color: #999;
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: 6px;
  padding: 6px 16px;
  font-size: 13px;
  font-weight: 500;
  border: none;
}

/* 响应式 */
@media (max-width: 1400px) {
  .orders-page {
    padding: 30px 50px;
  }
}

@media (max-width: 768px) {
  .orders-page {
    padding: 20px;
  }
  
  .order-body {
    flex-direction: column;
    gap: 20px;
  }
  
  .order-amount {
    text-align: left;
  }
}
</style>
