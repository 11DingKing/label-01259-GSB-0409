<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-visual">
        <div class="visual-content">
          <div class="visual-logo">
            <svg viewBox="0 0 60 60" fill="none">
              <circle cx="30" cy="30" r="30" fill="white" fill-opacity="0.2"/>
              <path d="M30 18V30L38 34" stroke="white" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="30" cy="30" r="14" stroke="white" stroke-width="4"/>
            </svg>
          </div>
          <h2>加入 Creatorly</h2>
          <p>开启你的创作之旅</p>
          <div class="visual-stats">
            <div class="stat-item">
              <span class="stat-value">1,000+</span>
              <span class="stat-label">创作者</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">50K+</span>
              <span class="stat-label">内容</span>
            </div>
          </div>
        </div>
        <div class="visual-decoration">
          <div class="deco-circle deco-circle-1"></div>
          <div class="deco-circle deco-circle-2"></div>
        </div>
      </div>
      
      <div class="register-form-wrapper">
        <div class="register-form-content">
          <div class="form-header">
            <h1>创建账户</h1>
            <p>填写以下信息完成注册</p>
          </div>
          
          <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleRegister" class="register-form">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名 (3-20个字符)" size="large" :prefix-icon="User" />
            </el-form-item>
            
            <el-form-item prop="nickname">
              <el-input v-model="form.nickname" placeholder="昵称 (选填)" size="large" :prefix-icon="UserFilled" />
            </el-form-item>
            
            <el-form-item prop="email">
              <el-input v-model="form.email" placeholder="邮箱 (选填)" size="large" :prefix-icon="Message" />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="密码 (6-20个字符)" size="large" :prefix-icon="Lock" show-password />
            </el-form-item>
            
            <el-form-item prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" :prefix-icon="Lock" show-password />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleRegister" class="register-btn">
                注册
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="form-footer">
            <span>已有账户？</span>
            <router-link to="/login">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { User, UserFilled, Lock, Message } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', nickname: '', email: '', password: '', confirmPassword: '' })

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register({ username: form.username, nickname: form.nickname || form.username, email: form.email, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-lg;
  background: $background-color;
}

.register-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background: $card-background;
  border-radius: $border-radius-2xl;
  box-shadow: $shadow-xl;
  overflow: hidden;
}

.register-visual {
  position: relative;
  width: 420px;
  background: $gradient-accent;
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
  
  h2 { font-size: $font-size-2xl; font-weight: $font-weight-bold; margin-bottom: $spacing-sm; }
  p { opacity: 0.9; margin-bottom: $spacing-xl; }
  
  .visual-stats {
    display: flex;
    justify-content: center;
    gap: $spacing-2xl;
    .stat-item {
      text-align: center;
      .stat-value { display: block; font-size: $font-size-2xl; font-weight: $font-weight-bold; }
      .stat-label { font-size: $font-size-sm; opacity: 0.8; }
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
    .deco-circle-1 { width: 200px; height: 200px; top: -60px; left: -60px; }
    .deco-circle-2 { width: 150px; height: 150px; bottom: -40px; right: -40px; }
  }
}

.register-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-2xl;
}

.register-form-content {
  width: 100%;
  max-width: 360px;
}

.form-header {
  margin-bottom: $spacing-xl;
  h1 { font-size: $font-size-2xl; font-weight: $font-weight-bold; color: $text-primary; margin-bottom: $spacing-xs; }
  p { color: $text-muted; }
}

.register-form {
  :deep(.el-input__wrapper) {
    padding: 12px 16px;
    border-radius: $border-radius-lg !important;
  }
}

.register-btn {
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
