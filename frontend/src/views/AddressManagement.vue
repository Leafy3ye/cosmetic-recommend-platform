<template>
  <div class="address-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <el-button :icon="ArrowLeft" circle @click="goBack" class="back-btn" />
      <h2>收货地址管理</h2>
      <div class="placeholder"></div>
    </div>

    <!-- 地址列表 -->
    <div class="address-container" v-loading="loading">
      <div v-if="addressList.length === 0" class="empty-state">
        <el-empty description="暂无收货地址">
          <el-button type="primary" @click="showAddDialog">添加地址</el-button>
        </el-empty>
      </div>

      <div v-else class="address-list">
        <div 
          v-for="addr in addressList" 
          :key="addr.id" 
          class="address-item"
          :class="{ 'is-default': addr.isDefault }"
        >
          <div class="address-content">
            <div class="address-header">
              <span class="receiver-name">{{ addr.receiverName }}</span>
              <span class="receiver-phone">{{ addr.receiverPhone }}</span>
              <el-tag v-if="addr.isDefault" type="danger" size="small">默认</el-tag>
            </div>
            <div class="address-detail">
              <el-icon><Location /></el-icon>
              <span>{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}</span>
            </div>
          </div>
          <div class="address-actions">
            <el-button 
              @click="handleEdit(addr)" 
              :icon="Edit"
              class="action-btn edit-btn"
              size="small"
            >
              编辑
            </el-button>
            <el-button 
              @click="handleDelete(addr.id)" 
              :icon="Delete"
              class="action-btn delete-btn"
              size="small"
            >
              删除
            </el-button>
            <el-button 
              v-if="!addr.isDefault"
              @click="handleSetDefault(addr.id)"
              class="action-btn default-btn"
              size="small"
            >
              设为默认
            </el-button>
          </div>
        </div>
      </div>

      <!-- 添加地址按钮 -->
      <div class="add-button-container" v-if="addressList.length > 0">
        <el-button type="primary" :icon="Plus" @click="showAddDialog" class="add-btn">
          添加新地址
        </el-button>
      </div>
    </div>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑地址' : '添加地址'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="addressForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区/县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区/县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input 
            v-model="addressForm.detailAddress" 
            type="textarea" 
            :rows="3"
            placeholder="请输入详细地址（街道、门牌号等）" 
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Location, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserAddress, updateUserAddress } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const addressList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const addressForm = ref({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const rules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区/县', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadAddressList()
})

// 加载地址列表
const loadAddressList = async () => {
  try {
    loading.value = true
    const userId = userStore.userInfo?.userId
    if (!userId) {
      router.push('/login')
      return
    }
    
    const res = await getUserAddress(userId)
    // 如果有地址信息，包装成数组（因为当前设计是用户只有一个地址）
    if (res.data && res.data.receiverName) {
      addressList.value = [{
        id: res.data.id,
        receiverName: res.data.receiverName,
        receiverPhone: res.data.receiverPhone,
        province: res.data.province,
        city: res.data.city,
        district: res.data.district,
        detailAddress: res.data.detailAddress,
        isDefault: res.data.isDefault || 1
      }]
    } else {
      addressList.value = []
    }
  } catch (error) {
    console.error('加载地址失败：', error)
    ElMessage.error('加载地址失败')
  } finally {
    loading.value = false
  }
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  addressForm.value = {
    id: null,
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  }
  dialogVisible.value = true
}

// 编辑地址
const handleEdit = (addr) => {
  isEdit.value = true
  addressForm.value = { ...addr }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const userId = userStore.userInfo?.userId
    if (!userId) {
      ElMessage.error('用户信息异常，请重新登录')
      return
    }

    await updateUserAddress(userId, {
      receiverName: addressForm.value.receiverName,
      receiverPhone: addressForm.value.receiverPhone,
      province: addressForm.value.province,
      city: addressForm.value.city,
      district: addressForm.value.district,
      detailAddress: addressForm.value.detailAddress,
      isDefault: addressForm.value.isDefault ? 1 : 0
    })
    
    ElMessage.success(isEdit.value ? '地址更新成功' : '地址添加成功')
    dialogVisible.value = false
    loadAddressList()
  } catch (error) {
    console.error('操作失败：', error)
    ElMessage.error('操作失败')
  }
}

// 删除地址
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const userId = userStore.userInfo?.userId
    if (!userId) {
      ElMessage.error('用户信息异常，请重新登录')
      return
    }

    // 清空地址信息
    await updateUserAddress(userId, {
      receiverName: null,
      receiverPhone: null,
      province: null,
      city: null,
      district: null,
      detailAddress: null,
      isDefault: 0
    })
    
    ElMessage.success('删除成功')
    loadAddressList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败：', error)
      ElMessage.error('删除失败')
    }
  }
}

// 设为默认地址（当前设计下用户只有一个地址，所以这个功能暂不需要）
const handleSetDefault = async (id) => {
  ElMessage.info('当前地址已是默认地址')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.address-page {
  min-height: calc(100vh - 60px);
  background: #f5f7fa;
  padding: 20px;
}

.page-header {
  max-width: 1200px;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  padding: 20px 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.back-btn {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
  color: white;
  transition: all 0.3s;
}

.back-btn:hover {
  transform: translateX(-3px);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.3);
}

.placeholder {
  width: 40px;
}

.address-container {
  max-width: 1200px;
  margin: 0 auto;
}

.empty-state {
  background: white;
  border-radius: 16px;
  padding: 60px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: 2px solid transparent;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #FFD4D7;
  box-shadow: 0 4px 20px rgba(255, 154, 158, 0.15);
  transform: translateY(-2px);
}

.address-item.is-default {
  border-color: #FF9A9E;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.03) 0%, rgba(254, 207, 239, 0.03) 100%);
}

.address-content {
  flex: 1;
  margin-bottom: 15px;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 12px;
}

.receiver-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.receiver-phone {
  font-size: 16px;
  color: #666;
}

.address-detail {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #666;
  font-size: 15px;
  line-height: 1.6;
}

.address-detail .el-icon {
  color: #FF9A9E;
  font-size: 18px;
  margin-top: 2px;
}

.address-actions {
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
  border: 1px solid transparent;
  min-width: 90px;
}

.edit-btn {
  color: #FF9A9E;
  background: rgba(255, 154, 158, 0.1);
  border-color: #FFD4D7;
}

.edit-btn:hover {
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
}

.delete-btn:hover {
  color: white;
  background: linear-gradient(135deg, #f56c6c 0%, #ff8585 100%);
  border-color: #f56c6c;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
}

.default-btn {
  color: #409eff;
  background: rgba(64, 158, 255, 0.1);
  border-color: #d4e5ff;
}

.default-btn:hover {
  color: white;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.add-button-container {
  margin-top: 20px;
  text-align: center;
}

.add-btn {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
  border-radius: 25px;
  padding: 12px 40px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.3);
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.4);
}

@media (max-width: 768px) {
  .address-header {
    flex-wrap: wrap;
  }
  
  .address-actions {
    flex-wrap: wrap;
  }
}
</style>
