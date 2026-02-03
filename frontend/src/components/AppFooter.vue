<template>
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
</template>

<script setup>
import { ChatDotRound, Position, Share } from '@element-plus/icons-vue'
</script>

<style scoped>
/* 底部 - 完全复制首页样式 */
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
  .footer-main {
    grid-template-columns: 1fr;
    gap: 50px;
  }
  
  .footer-links {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .footer-main {
    padding: 40px 0;
  }
  
  .footer-links {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}
</style>
