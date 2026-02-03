<template>
  <div class="products-page-container">
    <!-- 头部导航 -->
    <AppHeader :active-page="currentPage" />
    
    <!-- 主内容区 -->
    <div class="products-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2 class="page-title">{{ pageTitle }}</h2>
        <p class="page-subtitle">{{ pageSubtitle }}</p>
      </div>
      
      <!-- 商品列表 -->
      <el-row :gutter="20" v-loading="loading">
        <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="product in productList" :key="product.id">
          <ProductCard :product="product" @click="goToDetail(product.id)" />
        </el-col>
      </el-row>
      
      <el-empty v-if="!loading && productList.length === 0" description="暂无商品" />
      
      <!-- 分页 -->
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
    
    <!-- 底部 -->
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getProductPage } from '@/api/product'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
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
  categoryId: route.query.categoryId || null,
  type: route.query.type || null
})

// 根据路由参数确定当前页面
const currentPage = computed(() => {
  const type = route.query.type
  if (type === 'new') return 'new'
  if (type === 'hot') return 'hot'
  return 'all'
})

// 页面标题
const pageTitle = computed(() => {
  const type = route.query.type
  if (type === 'new') return '新品上市'
  if (type === 'hot') return '热卖商品'
  return '全部商品'
})

// 页面副标题
const pageSubtitle = computed(() => {
  const type = route.query.type
  if (type === 'new') return '最新鲜的美妆产品，第一时间体验'
  if (type === 'hot') return '人气爆款，销量见证品质'
  return '精选美妆产品，总有一款适合你'
})

onMounted(() => {
  loadProducts()
})

// 监听路由变化，重新加载商品
watch(() => route.query, (newQuery) => {
  searchParams.keyword = newQuery.keyword || ''
  searchParams.type = newQuery.type || null
  searchParams.categoryId = newQuery.categoryId || null
  searchParams.current = 1
  loadProducts()
}, { deep: true })

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

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}
</script>

<style scoped>
.products-page-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.products-content {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
  width: 100%;
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 0;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

/* 商品列表 */
.el-row {
  margin-bottom: 20px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 60px;
  padding: 30px 0;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 空状态 */
.el-empty {
  padding: 80px 0;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 响应式 */
@media (max-width: 768px) {
  .products-content {
    padding: 20px 10px;
  }
  
  .page-header {
    padding: 30px 20px;
    margin-bottom: 30px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
}
</style>
