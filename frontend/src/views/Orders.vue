<template>
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrderPage, cancelOrder, payOrder, confirmOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('all')
const loading = ref(false)
const orderList = ref([])
const total = ref(0)

const pageParams = reactive({
  current: 1,
  size: 10,
  status: null
})

onMounted(() => {
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

const handleTabClick = () => {
  pageParams.current = 1
  pageParams.status = activeTab.value === 'all' ? null : parseInt(activeTab.value)
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
    1: 'primary',
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
</script>

<style scoped>
.orders-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  min-height: calc(100vh - 120px);
}

.orders-page h2 {
  margin-bottom: 20px;
}

.orders-list {
  margin-top: 20px;
}

.order-item {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.order-body {
  display: flex;
  justify-content: space-between;
  padding: 20px;
}

.order-info p {
  margin-bottom: 8px;
  color: #666;
}

.order-amount {
  text-align: right;
}

.order-amount > div {
  margin-bottom: 10px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  background: #fafafa;
  border-top: 1px solid #eee;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
