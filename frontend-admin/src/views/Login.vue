<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧装饰区 -->
      <div class="login-banner">
        <div class="banner-content">
          <div class="logo-area">
            <div class="logo-icon">
              <el-icon><Setting /></el-icon>
            </div>
            <h1>创作者平台</h1>
            <p class="subtitle">管理控制台</p>
          </div>
          <div class="features">
            <div class="feature-item">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据可视化分析</span>
            </div>
            <div class="feature-item">
              <el-icon><User /></el-icon>
              <span>用户与创作者管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Document /></el-icon>
              <span>内容审核与监控</span>
            </div>
            <div class="feature-item">
              <el-icon><List /></el-icon>
              <span>操作日志追踪</span>
            </div>
          </div>
          <div class="banner-footer">
            <p>© 2024 Creator Platform. All rights reserved.</p>
          </div>
        </div>
        <div class="banner-decoration">
          <div class="circle circle-1"></div>
          <div class="circle circle-2"></div>
          <div class="circle circle-3"></div>
        </div>
      </div>
      
      <!-- 右侧登录区 -->
      <div class="login-form-area">
        <div class="form-container">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请登录您的管理员账户</p>
          </div>
          
          <!-- 测试账号提示 -->
          <div class="test-account-tip" @click="fillTestAccount">
            <el-icon><InfoFilled /></el-icon>
            <span>测试账号：<code>admin / 123456</code></span>
            <span class="click-hint">点击填充</span>
          </div>
          
          <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="管理员账号" 
                size="large"
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="登录密码" 
                size="large"
                show-password
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>
            
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                :loading="loading" 
                @click="handleLogin" 
                class="login-btn"
              >
                <span v-if="!loading">登 录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="form-footer">
            <p class="security-tip">
              <el-icon><InfoFilled /></el-icon>
              仅限管理员登录，请勿泄露账号信息
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }],
}

const fillTestAccount = () => {
  form.username = 'admin'
  form.password = '123456'
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables.scss' as *;

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: $card-background;
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

// 左侧装饰区
.login-banner {
  flex: 1;
  background: linear-gradient(135deg, #1e3a5f 0%, #0d1b2a 100%);
  padding: 48px;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  
  @media (max-width: 768px) {
    display: none;
  }
}

.banner-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.logo-area {
  margin-bottom: 48px;
  
  .logo-icon {
    width: 64px;
    height: 64px;
    background: linear-gradient(135deg, #3B82F6, #8B5CF6);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    
    .el-icon {
      font-size: 32px;
      color: white;
    }
  }
  
  h1 {
    font-size: 28px;
    font-weight: 700;
    color: white;
    margin-bottom: 8px;
  }
  
  .subtitle {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.6);
  }
}

.features {
  flex: 1;
  
  .feature-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    &:last-child {
      border-bottom: none;
    }
    
    .el-icon {
      font-size: 24px;
      color: #60A5FA;
    }
    
    span {
      font-size: 15px;
      color: rgba(255, 255, 255, 0.8);
    }
  }
}

.banner-footer {
  margin-top: auto;
  
  p {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.4);
  }
}

.banner-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  
  .circle {
    position: absolute;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(139, 92, 246, 0.1));
    
    &.circle-1 {
      width: 300px;
      height: 300px;
      top: -100px;
      right: -100px;
    }
    
    &.circle-2 {
      width: 200px;
      height: 200px;
      bottom: 50px;
      left: -50px;
    }
    
    &.circle-3 {
      width: 150px;
      height: 150px;
      bottom: -50px;
      right: 50px;
    }
  }
}

// 右侧登录区
.login-form-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: $card-background;
}

.form-container {
  width: 100%;
  max-width: 360px;
}

.form-header {
  text-align: center;
  margin-bottom: 24px;
  
  h2 {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 8px;
  }
  
  p {
    font-size: 15px;
    color: $text-secondary;
  }
}

.test-account-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #EEF2FF, #E0E7FF);
  border-radius: 12px;
  margin-bottom: 24px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: linear-gradient(135deg, #E0E7FF, #C7D2FE);
    transform: translateX(4px);
  }
  
  .el-icon {
    font-size: 18px;
    color: #6366F1;
  }
  
  span {
    font-size: 14px;
    color: #4F46E5;
  }
  
  code {
    font-family: monospace;
    background: rgba(99, 102, 241, 0.1);
    padding: 2px 8px;
    border-radius: 4px;
    font-weight: 600;
  }
  
  .click-hint {
    margin-left: auto;
    font-size: 12px;
    color: #818CF8;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .custom-input {
    :deep(.el-input__wrapper) {
      padding: 4px 16px;
      border-radius: 12px;
      box-shadow: 0 0 0 1px $border-color;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 0 0 1px $primary-color;
      }
      
      &.is-focus {
        box-shadow: 0 0 0 2px rgba($primary-color, 0.2), 0 0 0 1px $primary-color;
      }
    }
    
    :deep(.el-input__inner) {
      height: 48px;
      font-size: 15px;
    }
  }
  
  .input-icon {
    font-size: 18px;
    color: $text-placeholder;
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  :deep(.el-checkbox__label) {
    color: $text-secondary;
  }
}

.login-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  border: none;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(59, 130, 246, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  margin-top: 32px;
  
  .security-tip {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    font-size: 13px;
    color: $text-placeholder;
    
    .el-icon {
      font-size: 16px;
    }
  }
}
</style>
