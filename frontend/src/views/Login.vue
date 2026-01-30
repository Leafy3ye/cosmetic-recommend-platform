<template>
  <div class="login-page">
    <!-- 顶部导航 -->
    <header class="login-header">
      <div class="header-content">
        <div class="brand-logo" @click="goHome">
          <div class="logo-symbol">C</div>
          <div class="logo-info">
            <span class="brand-name">Cosmetic</span>
            <span class="brand-slogan">美妆精选</span>
          </div>
        </div>
        <div class="header-right">
          <span class="tip-text">已有账号？</span>
          <el-button text class="switch-btn" @click="goHome">返回首页</el-button>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <div class="login-main">
      <div class="login-container">
        <!-- 左侧品牌展示区 -->
        <div class="brand-section">
          <div class="brand-content">
            <div class="brand-icon">
              <div class="icon-circle">
                <el-icon><Present /></el-icon>
              </div>
            </div>
            <h1 class="brand-title">化妆品推荐平台</h1>
            <p class="brand-subtitle">为你精选优质美妆产品</p>
            <div class="feature-list">
              <div class="feature-item">
                <el-icon class="feature-icon"><Select /></el-icon>
                <span>智能推荐 · 精准匹配</span>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon"><Star /></el-icon>
                <span>正品保障 · 品质优选</span>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon"><TrophyBase /></el-icon>
                <span>专业服务 · 贴心体验</span>
              </div>
            </div>
          </div>
          <div class="decoration-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>

        <!-- 右侧表单区 -->
        <div class="form-section">
          <div class="form-wrapper">
            <!-- Tab切换 -->
            <div class="tab-buttons">
              <button 
                :class="['tab-btn', { active: activeTab === 'login' }]"
                @click="activeTab = 'login'"
              >
                登录
              </button>
              <button 
                :class="['tab-btn', { active: activeTab === 'register' }]"
                @click="activeTab = 'register'"
              >
                注册
              </button>
              <div class="tab-indicator" :style="{ transform: activeTab === 'register' ? 'translateX(100%)' : 'translateX(0)' }"></div>
            </div>

            <!-- 登录表单 -->
            <div v-show="activeTab === 'login'" class="form-container">
              <h2 class="form-title">欢迎回来</h2>
              <p class="form-subtitle">登录您的账户，继续精彩体验</p>
              
              <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="auth-form">
                <el-form-item prop="username">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><User /></el-icon>
                    <input 
                      v-model="loginForm.username" 
                      placeholder="用户名" 
                      class="custom-input"
                    />
                  </div>
                </el-form-item>
                <el-form-item prop="password">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <input 
                      v-model="loginForm.password" 
                      :type="showPassword ? 'text' : 'password'"
                      placeholder="密码" 
                      class="custom-input"
                      @keyup.enter="handleLogin"
                    />
                    <el-icon class="eye-icon" @click="showPassword = !showPassword">
                      <component :is="showPassword ? View : Hide" />
                    </el-icon>
                  </div>
                </el-form-item>
                <el-button 
                  class="submit-btn" 
                  :loading="loading"
                  @click="handleLogin"
                >
                  {{ loading ? '登录中...' : '登录' }}
                </el-button>
              </el-form>
            </div>

            <!-- 注册表单 -->
            <div v-show="activeTab === 'register'" class="form-container">
              <h2 class="form-title">创建账户</h2>
              <p class="form-subtitle">加入我们，开启美妆之旅</p>
              
              <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="auth-form">
                <el-form-item prop="username">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><User /></el-icon>
                    <input 
                      v-model="registerForm.username" 
                      placeholder="用户名（4-20位字母、数字或下划线）" 
                      class="custom-input"
                    />
                  </div>
                </el-form-item>
                <el-form-item prop="password">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <input 
                      v-model="registerForm.password" 
                      :type="showPassword ? 'text' : 'password'"
                      placeholder="密码（6-20位）" 
                      class="custom-input"
                    />
                    <el-icon class="eye-icon" @click="showPassword = !showPassword">
                      <component :is="showPassword ? View : Hide" />
                    </el-icon>
                  </div>
                </el-form-item>
                <el-form-item prop="nickname">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><UserFilled /></el-icon>
                    <input 
                      v-model="registerForm.nickname" 
                      placeholder="昵称" 
                      class="custom-input"
                    />
                  </div>
                </el-form-item>
                <el-form-item prop="role">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Avatar /></el-icon>
                    <select v-model="registerForm.role" class="custom-select">
                      <option value="" disabled>选择角色</option>
                      <option :value="0">普通用户</option>
                      <option :value="1">商家</option>
                    </select>
                  </div>
                </el-form-item>
                <el-button 
                  class="submit-btn"
                  :loading="loading"
                  @click="handleRegister"
                >
                  {{ loading ? '注册中...' : '创建账户' }}
                </el-button>
              </el-form>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-main">
          <!-- 品牌信息 -->
          <div class="footer-brand">
            <div class="brand-logo-footer">
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, Lock, UserFilled, Avatar, Present, Select, Star, 
  TrophyBase, View, Hide, ChatDotRound, Position, Share 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login, register } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)
const showPassword = ref(false)

// 登录表单
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 注册表单
const registerFormRef = ref()
const registerForm = reactive({
  username: '',
  password: '',
  nickname: '',
  role: ''
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '用户名必须为4-20位字母、数字或下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须为6-20位', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 登录
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const res = await login(loginForm)
    
    // 保存token和用户信息
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    userStore.setUserInfo(res.data)
    
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败：', error)
  } finally {
    loading.value = false
  }
}

// 注册
const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    await register(registerForm)
    
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    
    // 清空注册表单
    registerForm.username = ''
    registerForm.password = ''
    registerForm.nickname = ''
    registerForm.role = ''
  } catch (error) {
    console.error('注册失败：', error)
  } finally {
    loading.value = false
  }
}

// 返回首页
const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
/* 整体布局 */
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #fef5f8 100%);
}

/* 顶部导航 */
.login-header {
  background: white;
  padding: 20px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s;
}

.brand-logo:hover {
  transform: scale(1.05);
}

.logo-symbol {
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

.logo-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
}

.brand-slogan {
  font-size: 12px;
  color: #999;
  letter-spacing: 1px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.tip-text {
  color: #666;
  font-size: 14px;
}

.switch-btn {
  color: #FF9A9E;
  font-weight: 500;
}

.switch-btn:hover {
  color: #FF7B7F;
}

/* 主内容区 */
.login-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.login-container {
  width: 100%;
  max-width: 1100px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
}

/* 左侧品牌展示区 */
.brand-section {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  padding: 80px 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-content {
  position: relative;
  z-index: 2;
}

.brand-icon {
  margin-bottom: 40px;
}

.icon-circle {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: white;
  backdrop-filter: blur(10px);
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin-bottom: 16px;
  line-height: 1.3;
}

.brand-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 50px;
  line-height: 1.6;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  color: white;
  font-size: 16px;
  font-weight: 500;
}

.feature-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

/* 装饰点 */
.decoration-dots {
  position: absolute;
  bottom: 40px;
  right: 40px;
  display: flex;
  gap: 12px;
  z-index: 1;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
}

/* 右侧表单区 */
.form-section {
  padding: 60px;
  display: flex;
  align-items: center;
}

.form-wrapper {
  width: 100%;
}

/* Tab切换 */
.tab-buttons {
  display: flex;
  position: relative;
  background: #f5f5f5;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 40px;
}

.tab-btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  background: transparent;
  font-size: 16px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
  position: relative;
  z-index: 2;
}

.tab-btn.active {
  color: #FF9A9E;
}

.tab-indicator {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
  z-index: 1;
}

/* 表单容器 */
.form-container {
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 14px;
  color: #999;
  margin-bottom: 32px;
}

/* 自定义表单 */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 0;
  width: 100%;
}

.auth-form :deep(.el-form-item__content) {
  width: 100%;
}

.auth-form :deep(.el-form-item__error) {
  padding-top: 4px;
  font-size: 12px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  background: #f8f9fa;
  border: 2px solid transparent;
  border-radius: 12px;
  transition: all 0.3s;
}

.input-wrapper:focus-within {
  background: white;
  border-color: #FF9A9E;
  box-shadow: 0 0 0 4px rgba(255, 154, 158, 0.1);
}

.input-icon {
  position: absolute;
  left: 16px;
  font-size: 20px;
  color: #999;
  z-index: 1;
}

.custom-input,
.custom-select {
  flex: 1;
  height: 52px;
  padding: 0 16px 0 52px;
  border: none;
  background: transparent;
  font-size: 15px;
  color: #333;
  outline: none;
}

.custom-input::placeholder {
  color: #bbb;
}

.custom-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23999' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 16px center;
  padding-right: 40px;
}

.custom-select option {
  background: white;
  color: #333;
}

.eye-icon {
  position: absolute;
  right: 16px;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}

.eye-icon:hover {
  color: #FF9A9E;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 52px;
  margin-top: 12px;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.3);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 页脚样式 */
.footer {
  background: #1a1a1a;
  color: #fff;
  margin-top: auto;
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

.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.brand-logo-footer {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
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
@media (max-width: 1024px) {
  .login-container {
    grid-template-columns: 1fr;
  }

  .brand-section {
    display: none;
  }

  .form-section {
    padding: 40px;
  }

  .footer-main {
    grid-template-columns: 1fr;
    gap: 50px;
  }

  .footer-links {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .header-content {
    padding: 0 20px;
  }

  .tip-text {
    display: none;
  }

  .form-section {
    padding: 30px 20px;
  }

  .form-title {
    font-size: 24px;
  }

  .footer-links {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}
</style>
