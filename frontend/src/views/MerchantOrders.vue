<template>
  <div class="merchant-orders-container">
    <AppHeader />
    
    <div class="merchant-orders-page">
      <div class="page-header">
      <div class="header-left">
        <el-button class="back-btn" @click="$router.push('/profile')" :icon="ArrowLeft" round>返回个人中心</el-button>
        <h2>订单管理</h2>
      </div>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="待评价" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="orderList"
        border
        style="width: 100%"
      >
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="120" />
        <el-table-column prop="receiverPhone" label="联系电话" width="130" />
        <el-table-column prop="receiverAddress" label="收货地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleViewDetail(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="success"
              size="small"
              link
              @click="handleDeliver(row)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ currentOrder.payAmount }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            {{ currentOrder.receiverAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="下单时间" :span="2">
            {{ currentOrder.createTime }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.payTime" label="支付时间" :span="2">
            {{ currentOrder.payTime }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.deliveryTime" label="发货时间" :span="2">
            {{ currentOrder.deliveryTime }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.remark" label="备注" :span="2">
            {{ currentOrder.remark }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    </div>
    
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getMerchantOrderPage, deliverOrder } from '@/api/order'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const userStore = useUserStore()
const route = useRoute()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)

const queryParams = reactive({
  merchantId: userInfo.value?.userId,
  current: 1,
  size: 10,
  status: null
})

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
    1: 'danger',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

const handleQuery = async () => {
  try {
    loading.value = true
    const params = {
      ...queryParams,
      merchantId: userInfo.value?.userId
    }
    const res = await getMerchantOrderPage(params)
    orderList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error(error.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryParams.status = null
  queryParams.current = 1
  handleQuery()
}

const handleViewDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleDeliver = async (row) => {
  try {
    await ElMessageBox.confirm('确定要发货吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deliverOrder(row.id)
    ElMessage.success('发货成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '发货失败')
    }
  }
}

onMounted(() => {
  if (!userInfo.value || userInfo.value.role !== 1) {
    ElMessage.error('您没有权限访问该页面')
    return
  }

  const queryStatus = route.query.status
  if (queryStatus !== undefined) {
    queryParams.status = parseInt(queryStatus)
  }

  handleQuery()
})
</script>

<style scoped>
.merchant-orders-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.merchant-orders-page {
  max-width: 1600px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 80px;
  min-height: calc(100vh - 120px);
  flex: 1;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  color: #FF9A9E;
  border: 1px solid #FFD4D7;
  background: white;
  font-weight: 500;
  transition: all 0.3s;
}

.back-btn:hover {
  color: white;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border-color: #FF9A9E;
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.3);
}

.page-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filter-card :deep(.el-card__body) {
  padding: 24px;
}

.filter-card :deep(.el-form-item) {
  margin-bottom: 0;
}

.filter-card :deep(.el-form-item__label) {
  font-weight: 500;
  color: #555;
}

.filter-card :deep(.el-button--primary) {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
  border-radius: 8px;
  padding: 10px 24px;
}

.filter-card :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #FF7B7F 0%, #FFB8D8 100%);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.3);
}

.table-card {
  margin-bottom: 30px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.table-card :deep(.el-card__body) {
  padding: 24px;
}

.table-card :deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

.table-card :deep(.el-table th) {
  background: #fafafa;
  color: #333;
  font-weight: 600;
}

.table-card :deep(.el-table td),
.table-card :deep(.el-table th) {
  padding: 16px 0;
}

.table-card :deep(.el-button.is-link) {
  font-weight: 500;
}

.price {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 16px;
}

.order-detail {
  padding: 10px 0;
}

.order-detail :deep(.el-descriptions__label) {
  font-weight: 500;
  color: #555;
}

.order-detail :deep(.el-descriptions__content) {
  color: #333;
}

:deep(.el-pagination) {
  display: flex;
  gap: 8px;
}

:deep(.el-pager li) {
  border-radius: 6px;
  min-width: 32px;
  height: 32px;
  line-height: 32px;
}

:deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  color: white;
}

:deep(.el-pagination button) {
  border-radius: 6px;
}

/* 标签样式 */
:deep(.el-tag) {
  border-radius: 6px;
  padding: 4px 12px;
  font-weight: 500;
  border: none;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 20px;
}

:deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: white;
}

:deep(.el-dialog__footer) {
  padding: 20px;
  background: #fafafa;
}

/* 响应式 */
@media (max-width: 1400px) {
  .merchant-orders-page {
    padding: 30px 50px;
  }
}

@media (max-width: 768px) {
  .merchant-orders-page {
    padding: 20px;
  }
  
  .page-header h2 {
    font-size: 24px;
  }
}
</style>
