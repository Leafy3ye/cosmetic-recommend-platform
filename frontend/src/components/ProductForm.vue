<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑商品' : '添加商品'"
    width="70%"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入商品名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="品牌" prop="brand">
            <el-input v-model="formData.brand" placeholder="请输入品牌名称" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品分类" prop="categoryId">
            <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
              <el-option label="护肤品" :value="1" />
              <el-option label="彩妆" :value="2" />
              <el-option label="面膜" :value="3" />
              <el-option label="洁面" :value="4" />
              <el-option label="精华" :value="5" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="商品状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio :label="0">下架</el-radio>
              <el-radio :label="1">上架</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品价格" prop="price">
            <el-input-number
              v-model="formData.price"
              :precision="2"
              :step="0.01"
              :min="0"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="原价" prop="originalPrice">
            <el-input-number
              v-model="formData.originalPrice"
              :precision="2"
              :step="0.01"
              :min="0"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="库存数量" prop="stock">
            <el-input-number
              v-model="formData.stock"
              :min="0"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="适用肤质" prop="skinType">
            <el-select v-model="formData.skinType" placeholder="请选择适用肤质" style="width: 100%">
              <el-option label="所有肤质" value="所有肤质" />
              <el-option label="干性肤质" value="干性肤质" />
              <el-option label="油性肤质" value="油性肤质" />
              <el-option label="混合肤质" value="混合肤质" />
              <el-option label="敏感肤质" value="敏感肤质" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="功效" prop="effect">
        <el-input v-model="formData.effect" placeholder="如：保湿、美白、抗衰老等" />
      </el-form-item>

      <el-form-item label="商品主图" prop="image">
        <el-input v-model="formData.image" placeholder="请输入图片URL" />
      </el-form-item>

      <el-form-item label="商品描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请输入商品描述"
        />
      </el-form-item>

      <el-form-item label="商品标签" prop="tags">
        <el-input v-model="formData.tags" placeholder="多个标签用逗号分隔，如：热销,新品,推荐" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        {{ isEdit ? '保存' : '添加' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addProduct, updateProduct } from '@/api/product'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  product: {
    type: Object,
    default: null
  },
  merchantId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = ref(false)
const formRef = ref(null)
const loading = ref(false)
const isEdit = ref(false)

const formData = reactive({
  id: null,
  name: '',
  brand: '',
  categoryId: null,
  status: 1,
  price: 0,
  originalPrice: 0,
  stock: 0,
  skinType: '',
  effect: '',
  image: '',
  description: '',
  tags: '',
  merchantId: props.merchantId
})

const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  brand: [
    { required: true, message: '请输入品牌名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ],
  image: [
    { required: true, message: '请输入商品主图URL', trigger: 'blur' }
  ]
}

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val) {
    if (props.product) {
      isEdit.value = true
      Object.assign(formData, props.product)
    } else {
      isEdit.value = false
      resetForm()
    }
  }
})

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    name: '',
    brand: '',
    categoryId: null,
    status: 1,
    price: 0,
    originalPrice: 0,
    stock: 0,
    skinType: '',
    effect: '',
    image: '',
    description: '',
    tags: '',
    merchantId: props.merchantId
  })
  formRef.value?.clearValidate()
}

const handleClose = () => {
  emit('update:visible', false)
  resetForm()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const data = { ...formData }
    
    if (isEdit.value) {
      await updateProduct(data)
      ElMessage.success('商品更新成功')
    } else {
      await addProduct(data)
      ElMessage.success('商品添加成功')
    }
    
    emit('success')
    handleClose()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
:deep(.el-dialog__body) {
  padding: 20px 30px;
}
</style>
