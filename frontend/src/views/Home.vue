<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <AppHeader active-page="home" />

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 轮播图Banner -->
      <section class="banner-section">
        <el-carousel height="450px" :interval="4000" arrow="always" indicator-position="outside">
          <el-carousel-item v-for="(banner, index) in banners" :key="index">
            <div class="banner-item" :style="{ backgroundImage: `url(${banner.image})` }">
              <div class="banner-content">
                <h1 class="banner-title">{{ banner.title }}</h1>
                <p class="banner-subtitle">{{ banner.subtitle }}</p>
                <el-button type="primary" size="large" round @click="handleSearch">立即抢购</el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- 活动区块 -->
      <section class="activity-section">
        <div class="activity-grid">
          <!-- 限时秒杀 -->
          <div class="activity-card seckill" @click="goToProducts('seckill')">
            <div class="card-header">
              <h3><el-icon><Clock /></el-icon> 限时秒杀</h3>
              <p>大牌爆款拼手速</p>
            </div>
            <div class="card-image">
              <img v-if="activityProducts[0]?.image" :src="activityProducts[0].image" :alt="activityProducts[0].name" class="product-img" />
              <div v-else class="placeholder-image">
                <el-icon><Picture /></el-icon>
                <span>商品图片</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button type="primary" size="large" round>马上抢</el-button>
            </div>
          </div>

          <!-- 3折疯抢 -->
          <div class="activity-card discount" @click="goToProducts('discount')">
            <div class="card-header">
              <h3><el-icon><TrendCharts /></el-icon> 3折疯抢</h3>
              <p>精选品牌超值让利</p>
            </div>
            <div class="card-image">
              <img v-if="activityProducts[1]?.image" :src="activityProducts[1].image" :alt="activityProducts[1].name" class="product-img" />
              <div v-else class="placeholder-image">
                <el-icon><Picture /></el-icon>
                <span>商品图片</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button type="primary" size="large" round>立即抢购</el-button>
            </div>
          </div>

          <!-- 品牌特卖 -->
          <div class="activity-card brand" @click="goToProducts('brand')">
            <div class="card-header">
              <h3><el-icon><Star /></el-icon> 品牌特卖</h3>
              <p>奢享大牌低价来袭</p>
            </div>
            <div class="card-image">
              <img v-if="activityProducts[2]?.image" :src="activityProducts[2].image" :alt="activityProducts[2].name" class="product-img" />
              <div v-else class="placeholder-image">
                <el-icon><Picture /></el-icon>
                <span>商品图片</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button type="primary" size="large" round>查看更多</el-button>
            </div>
          </div>

          <!-- 天天低价 -->
          <div class="activity-card daily" @click="goToProducts('daily')">
            <div class="card-header">
              <h3><el-icon><Sunrise /></el-icon> 天天低价</h3>
              <p>惊喜低价天天来袭</p>
            </div>
            <div class="card-image">
              <img v-if="activityProducts[3]?.image" :src="activityProducts[3].image" :alt="activityProducts[3].name" class="product-img" />
              <div v-else class="placeholder-image">
                <el-icon><Picture /></el-icon>
                <span>商品图片</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button type="primary" size="large" round>查看详情</el-button>
            </div>
          </div>
        </div>
      </section>

      <!-- 为你推荐 (只对普通用户显示) -->
      <section v-if="userInfo && userInfo.role === 0" class="recommend-section">
        <div class="section-header">
          <h2 class="section-title"><el-icon><Discount /></el-icon> 为你推荐</h2>
          <p class="section-subtitle">根据你的浏览记录为你精选</p>
        </div>
        <div class="product-grid" v-loading="loading">
          <ProductCard 
            v-for="product in recommendProducts" 
            :key="product.id"
            :product="product"
            @click="goToDetail(product.id)"
          />
        </div>
      </section>

      <!-- 热销商品 -->
      <section class="hot-section">
        <div class="section-header">
          <h2 class="section-title"><el-icon><TrophyBase /></el-icon> 热销榜单</h2>
          <p class="section-subtitle">大家都在买的好物</p>
        </div>
        <div class="product-grid" v-loading="loading">
          <ProductCard 
            v-for="(product, index) in hotProducts" 
            :key="product.id"
            :product="{ ...product, rank: index + 1 }"
            @click="goToDetail(product.id)"
          />
        </div>
      </section>
    </main>

    <!-- 右侧固定侧边栏 -->
    <SideToolbar />

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-main">
          <!-- 品牌信息 -->
          <div class="footer-brand">
            <div class="brand-logo">
              <div class="logo-symbol">C</div>
              <div class="logo-info">
                <span class="brand-name">Cosmetic</span>
                <span class="brand-slogan">美妆精选</span>
              </div>
            </div>
            <p class="brand-desc">化妆品推荐平台</p>
            <p class="brand-desc">为你精选优质美妆产品 · 毕业设计项目</p>
            <div class="social-links">
              <a href="javascript:;" class="social-icon"><el-icon><ChatDotRound /></el-icon></a>
              <a href="javascript:;" class="social-icon"><el-icon><Position /></el-icon></a>
              <a href="javascript:;" class="social-icon"><el-icon><Share /></el-icon></a>
            </div>
          </div>

          <!-- 链接分组 -->
          <div class="footer-links">
            <div class="link-column">
              <h4 class="column-title">购物指南</h4>
              <a href="javascript:;">购物流程</a>
              <a href="javascript:;">会员权益</a>
              <a href="javascript:;">支付方式</a>
              <a href="javascript:;">常见问题</a>
            </div>
            
            <div class="link-column">
              <h4 class="column-title">配送服务</h4>
              <a href="javascript:;">配送时间</a>
              <a href="javascript:;">配送范围</a>
              <a href="javascript:;">运费说明</a>
              <a href="javascript:;">验货签收</a>
            </div>
            
            <div class="link-column">
              <h4 class="column-title">售后服务</h4>
              <a href="javascript:;">退换货政策</a>
              <a href="javascript:;">退款说明</a>
              <a href="javascript:;">在线客服</a>
              <a href="javascript:;">联系我们</a>
            </div>
            
            <div class="link-column">
              <h4 class="column-title">关于我们</h4>
              <a href="javascript:;">品牌故事</a>
              <a href="javascript:;">加入我们</a>
              <a href="javascript:;">隐私政策</a>
              <a href="javascript:;">用户协议</a>
            </div>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <div class="footer-container">
          <p class="copyright">© 2026 化妆品推荐平台. All Rights Reserved. | 基于SpringBoot+Vue3的智能推荐系统</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Clock, TrendCharts, Star, Sunrise, Discount, 
  TrophyBase, Picture
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getRecommendProducts, getHotProducts } from '@/api/product'
import { useUserStore } from '@/stores/user'
import ProductCard from '@/components/ProductCard.vue'
import SideToolbar from '@/components/SideToolbar.vue'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const userStore = useUserStore()

const recommendProducts = ref([])
const hotProducts = ref([])
const activityProducts = ref([]) // 活动区块商品
const loading = ref(false)

const userInfo = computed(() => userStore.userInfo)

// Banner数据
const banners = ref([
  {
    title: '新春焕新季',
    subtitle: '精选美妆 限时特惠 全场低至3折起',
    gradient: 'linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%)',
    image: '/banner1.jpg'
  },
  {
    title: '品牌特卖',
    subtitle: '大牌低价 抢先体验 新品上市',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    image: '/banner2.jpg'
  },
  {
    title: '限时秒杀',
    subtitle: '每日精选 超值优惠 错过再等一年',
    gradient: 'linear-gradient(135deg, #FFA8B4 0%, #FFD4E0 100%)',
    image: '/banner3.jpg'
  }
])

onMounted(async () => {
  loadActivityProducts()
  // 只有普通用户才加载推荐商品
  if (userInfo.value?.role === 0) {
    loadRecommendProducts()
  }
  loadHotProducts()
})

// 加载活动区块商品（随机4个）
const loadActivityProducts = async () => {
  try {
    const res = await getHotProducts(100) // 获取更多商品用于随机
    const allProducts = res.data || []
    
    // 只获取有图片的商品
    const productsWithImage = allProducts.filter(p => p.image)
    
    if (productsWithImage.length > 0) {
      // 随机打乱数组并取前4个
      const shuffled = productsWithImage.sort(() => 0.5 - Math.random())
      activityProducts.value = shuffled.slice(0, 4)
    }
  } catch (error) {
    console.error('加载活动商品失败：', error)
  }
}

const loadRecommendProducts = async () => {
  // 只有普通用户才加载推荐商品
  if (!userInfo.value || userInfo.value.role !== 0) {
    return
  }
  
  loading.value = true
  try {
    const userId = userInfo.value.userId
    const res = await getRecommendProducts(userId, 12)
    recommendProducts.value = res.data || []
  } catch (error) {
    console.error('加载推荐商品失败：', error)
    // 不显示错误提示，静默失败
  } finally {
    loading.value = false
  }
}

const loadHotProducts = async () => {
  loading.value = true
  try {
    const res = await getHotProducts(12)
    hotProducts.value = res.data || []
  } catch (error) {
    console.error('加载热销商品失败：', error)
    ElMessage.error('加载热销商品失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const goToProducts = (type) => {
  router.push({ path: '/products', query: { type } })
}

const handleSearch = () => {
  router.push('/products')
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 主内容 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* Banner轮播 */
.banner-section {
  margin-bottom: 40px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.banner-item {
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 80px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.banner-content {
  position: relative;
  z-index: 2;
  color: white;
  text-align: center;
  max-width: 800px;
}

.banner-title {
  font-size: 56px;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5);
  animation: fadeInUp 0.8s ease;
}

.banner-subtitle {
  font-size: 24px;
  margin-bottom: 40px;
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.5);
  animation: fadeInUp 1s ease;
}

.banner-image {
  display: none;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 活动区块 */
.activity-section {
  margin-bottom: 50px;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.activity-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
  position: relative;
}

.activity-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FF9A9E, #FECFEF);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.activity-card:hover::before {
  transform: scaleX(1);
}

.activity-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.card-header h3 {
  font-size: 22px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.card-header p {
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
}

.card-image {
  margin: 20px 0;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 8px;
}

/* 真实商品图片 */
.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-card:hover .product-img {
  transform: scale(1.1);
}

/* 预留图片容器样式 */
.placeholder-image {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #909399;
  transition: all 0.3s;
}

.placeholder-image .el-icon {
  font-size: 48px;
}

.placeholder-image span {
  font-size: 14px;
}

.activity-card:hover .placeholder-image {
  background: linear-gradient(135deg, #e4e7ed 0%, #dcdfe6 100%);
  transform: scale(1.05);
}

.card-footer {
  margin-top: 20px;
}

/* 商品区块 */
.recommend-section,
.hot-section {
  margin-bottom: 60px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-title .el-icon {
  font-size: 40px;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-subtitle {
  font-size: 16px;
  color: #909399;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

/* 底部 */
/* 页脚样式 */
.footer {
  background: #1a1a1a;
  color: #fff;
  margin-top: 80px;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-main {
  display: grid;
  grid-template-columns: 1.5fr 2.5fr;
  gap: 80px;
  padding: 60px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

/* 品牌信息区 */
.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.footer-brand .logo-symbol {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 800;
  color: white;
}

.footer-brand .logo-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.footer-brand .brand-name {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
}

.footer-brand .brand-slogan {
  font-size: 13px;
  color: #999;
  font-weight: 500;
  letter-spacing: 1px;
}

.brand-desc {
  color: #999;
  font-size: 14px;
  line-height: 1.8;
  margin: 0;
}

.social-links {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 18px;
  transition: all 0.3s ease;
  text-decoration: none;
}

.social-icon:hover {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  color: white;
  transform: translateY(-3px);
}

/* 链接区域 */
.footer-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
}

.link-column {
  display: flex;
  flex-direction: column;
}

.column-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 20px;
  letter-spacing: 0.5px;
}

.link-column a {
  color: #999;
  text-decoration: none;
  font-size: 14px;
  line-height: 2.2;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
  width: fit-content;
}

.link-column a:hover {
  color: #FF9A9E;
  padding-left: 8px;
}

.link-column a::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #FF9A9E, #FECFEF);
  transition: width 0.3s ease;
}

.link-column a:hover::before {
  width: 4px;
}

/* 底部版权信息 */
.footer-bottom {
  padding: 24px 0;
  background: #0f0f0f;
}

.copyright {
  text-align: center;
  color: #666;
  font-size: 13px;
  margin: 0;
  letter-spacing: 0.5px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .activity-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .footer-main {
    grid-template-columns: 1fr;
    gap: 50px;
  }
  
  .footer-links {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .banner-title {
    font-size: 36px;
  }
  
  .banner-subtitle {
    font-size: 18px;
  }
  
  .activity-grid,
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .footer-main {
    padding: 40px 0;
  }
  
  .footer-links {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}

@media (max-width: 480px) {
  .activity-grid,
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
