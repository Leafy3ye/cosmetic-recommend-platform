<template>
  <div class="products-page">
    <div class="search-filter">
      <el-input
        v-model="searchParams.keyword"
        placeholder="搜索商品"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
      
      <el-button @click="goBack">返回首页</el-button>
    </div>
    
    <el-divider />
    
    <el-row :gutter="20" v-loading="loading">
      <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="product in productList" :key="product.id">
        <ProductCard :product="product" @click="goToDetail(product.id)" />
      </el-col>
    </el-row>
    
    <el-empty v-if="!loading && productList.length === 0" description="暂无商品" />
    
    <div class="pagination">
      <el-pagination
        v-model:current-page="searchParams.current"
        v-model:page-size="searchParams.size"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadProducts"
        @current-change="loadProducts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getProductPage } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const productList = ref([])
const total = ref(0)

const searchParams = reactive({
  current: 1,
  size: 12,
  keyword: route.query.keyword || '',
  categoryId: route.query.categoryId || null
})

onMounted(() => {
  loadProducts()
})

const loadProducts = async () => {
  try {
    loading.value = true
    const res = await getProductPage(searchParams)
    productList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载商品失败：', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchParams.current = 1
  loadProducts()
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.products-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  min-height: calc(100vh - 120px);
}

.search-filter {
  display: flex;
  gap: 20px;
  align-items: center;
}

.search-filter .el-input {
  flex: 1;
  max-width: 600px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding: 20px 0;
}
</style>
