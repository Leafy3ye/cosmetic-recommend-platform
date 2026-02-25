<template>
  <div class="favorites-page">
    <AppHeader />

    <div class="page-content">
      <div class="page-header">
        <h2>我的收藏</h2>
        <el-button @click="router.push('/profile')">返回个人中心</el-button>
      </div>

      <div v-loading="loading">
        <el-empty v-if="!loading && products.length === 0" description="暂无收藏商品" />

        <div v-else class="grid">
          <div v-for="item in products" :key="item.id" class="card-wrap">
            <ProductCard :product="item" @click="goToDetail(item.id)" />
            <div class="card-actions">
              <el-button type="danger" link @click="handleRemove(item.id)">取消收藏</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getFavoriteProducts, removeFavorite } from '@/api/favorite'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import ProductCard from '@/components/ProductCard.vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const products = ref([])

onMounted(() => {
  loadFavorites()
})

const loadFavorites = async () => {
  const userId = userStore.userInfo?.userId
  if (!userId) {
    router.push('/login')
    return
  }

  loading.value = true
  try {
    const res = await getFavoriteProducts(userId, 50)
    products.value = res.data || []
  } catch (error) {
    console.error('加载收藏失败：', error)
    ElMessage.error('加载收藏失败')
  } finally {
    loading.value = false
  }
}

const handleRemove = async (productId) => {
  const userId = userStore.userInfo?.userId
  if (!userId) return
  try {
    await removeFavorite(userId, productId)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (error) {
    console.error('取消收藏失败：', error)
    ElMessage.error('取消收藏失败')
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}
</script>

<style scoped>
.favorites-page { min-height: 100vh; background: #f5f7fa; display: flex; flex-direction: column; }
.page-content { flex: 1; max-width: 1400px; margin: 0 auto; width: 100%; padding: 30px 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.card-wrap { background: #fff; border-radius: 8px; padding: 8px; }
.card-actions { text-align: right; padding: 0 8px 8px; }
@media (max-width: 1200px) { .grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .grid { grid-template-columns: repeat(2, 1fr); } }
</style>

