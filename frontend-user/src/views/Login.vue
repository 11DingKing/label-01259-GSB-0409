<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-visual">
        <div class="visual-content">
          <div class="visual-logo">
            <svg viewBox="0 0 60 60" fill="none">
              <circle cx="30" cy="30" r="30" fill="white" fill-opacity="0.2"/>
              <path d="M18 30L27 39L42 21" stroke="white" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h2>Creatorly</h2>
          <p>支持原创，让创作更有价值</p>
          <div class="visual-features">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>发现优质创作者</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>支持原创内容</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>建立粉丝连接</span>
            </div>
          </div>
        </div>
        <div class="visual-decoration">
          <div class="deco-circle deco-circle-1"></div>
          <div class="deco-circle deco-circle-2"></div>
        </div>
      </div>
      
      <div class="login-form-wrapper">
        <div class="login-form-content">
          <div class="form-header">
            <h1>欢迎回来</h1>
            <p>登录您的账户继续创作之旅</p>
          </div>
          
          <!-- 测试账号提示 -->
          <div class="test-accounts">
            <div class="test-accounts-header">
              <el-icon><InfoFilled /></el-icon>
              <span>测试账号</span>
            </div>
            <div class="test-accounts-list">
              <div class="test-account" @click="fillTestAccount('testuser', '123456')">
                <span class="account-name">普通用户</span>
                <span class="account-info">testuser / 123456</span>
              </div>
              <div class="test-account" @click="fillTestAccount('creator_tech', '123456')">
                <span class="account-name">创作者</span>
                <span class="account-info">creator_tech / 123456</span>
              </div>
            </div>
          </div>
          
          <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin" class="login-form">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password />
            </el-form-item>
            
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>
            
            <el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">
                登录
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="form-footer">
            <span>还没有账户？</span>
            <router-link to="/register">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const fillTestAccount = (username, password) => {
  form.username = username
  form.password = password
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-lg;
  background: $background-color;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background: $card-background;
  border-radius: $border-radius-2xl;
  box-shadow: $shadow-xl;
  overflow: hidden;
}

.login-visual {
  position: relative;
  width: 420px;
  background: $gradient-primary;
  padding: $spacing-2xl;
  display: flex;
  align-items: center;
  justify-content: center;
  @media (max-width: 768px) { display: none; }
  
  .visual-content {
    position: relative;
    z-index: 1;
    text-align: center;
    color: white;
  }
  
  .visual-logo {
    width: 80px;
    height: 80px;
    margin: 0 auto $spacing-lg;
    svg { width: 100%; height: 100%; }
  }
  
  h2 { font-size: $font-size-3xl; font-weight: $font-weight-bold; margin-bottom: $spacing-sm; }
  p { opacity: 0.9; margin-bottom: $spacing-xl; }
  
  .visual-features {
    text-align: left;
    .feature-item {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      padding: $spacing-sm 0;
      opacity: 0.9;
      .el-icon { font-size: 18px; }
    }
  }
  
  .visual-decoration {
    position: absolute;
    inset: 0;
    .deco-circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(white, 0.1);
    }
    .deco-circle-1 { width: 200px; height: 200px; top: -60px; right: -60px; }
    .deco-circle-2 { width: 150px; height: 150px; bottom: -40px; left: -40px; }
  }
}

.login-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-2xl;
}

.login-form-content {
  width: 100%;
  max-width: 360px;
}

.form-header {
  margin-bottom: $spacing-lg;
  h1 { font-size: $font-size-2xl; font-weight: $font-weight-bold; color: $text-primary; margin-bottom: $spacing-xs; }
  p { color: $text-muted; }
}

.test-accounts {
  background: linear-gradient(135deg, #FFF9F7, #FFE8E0);
  border-radius: $border-radius-lg;
  padding: $spacing-md;
  margin-bottom: $spacing-lg;
  
  .test-accounts-header {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $primary-dark;
    margin-bottom: $spacing-sm;
    
    .el-icon { font-size: 16px; }
  }
  
  .test-accounts-list {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }
  
  .test-account {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $spacing-xs $spacing-sm;
    background: rgba(white, 0.7);
    border-radius: $border-radius-md;
    cursor: pointer;
    transition: all $transition-fast;
    
    &:hover {
      background: white;
      transform: translateX(4px);
    }
    
    .account-name {
      font-size: $font-size-xs;
      font-weight: $font-weight-medium;
      color: $text-secondary;
    }
    
    .account-info {
      font-size: $font-size-xs;
      color: $primary-color;
      font-family: monospace;
    }
  }
}

.login-form {
  :deep(.el-input__wrapper) {
    padding: 12px 16px;
    border-radius: $border-radius-lg !important;
  }
}

.form-options {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: $spacing-lg;
}

.login-btn {
  width: 100%;
  height: 48px !important;
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
}

.form-footer {
  text-align: center;
  margin-top: $spacing-lg;
  color: $text-muted;
  a { color: $primary-color; font-weight: $font-weight-semibold; margin-left: $spacing-xs; &:hover { color: $primary-dark; } }
}
</style>
