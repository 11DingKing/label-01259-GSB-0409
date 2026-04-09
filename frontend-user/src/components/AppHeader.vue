<template>
  <header class="header" :class="{ 'header-scrolled': isScrolled }">
    <div class="header-content container">
      <router-link to="/" class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="20" cy="20" r="20" fill="url(#logo-gradient)"/>
            <path d="M12 20L18 26L28 14" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
            <defs>
              <linearGradient id="logo-gradient" x1="0" y1="0" x2="40" y2="40">
                <stop stop-color="#FF6B6B"/>
                <stop offset="1" stop-color="#FF8E53"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <span class="logo-text">Creatorly</span>
      </router-link>
      
      <nav class="nav">
        <router-link to="/" class="nav-link">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </router-link>
        <router-link to="/creators" class="nav-link">
          <el-icon><User /></el-icon>
          <span>发现创作者</span>
        </router-link>
        <router-link v-if="userStore.isCreator" to="/creator-center" class="nav-link">
          <el-icon><Edit /></el-icon>
          <span>创作中心</span>
        </router-link>
      </nav>
      
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <div class="balance-badge" v-if="userStore.userInfo?.balance">
            <el-icon><Wallet /></el-icon>
            <span>¥{{ userStore.userInfo.balance.toFixed(2) }}</span>
          </div>
          
          <el-dropdown trigger="click" @command="handleCommand" placement="bottom-end">
            <div class="user-avatar-wrapper">
              <el-avatar :size="40" :src="userStore.userInfo?.avatar" class="user-avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="avatar-status"></div>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown">
                <div class="dropdown-header">
                  <el-avatar :size="48" :src="userStore.userInfo?.avatar">
                    {{ userStore.userInfo?.nickname?.charAt(0) }}
                  </el-avatar>
                  <div class="dropdown-user-info">
                    <span class="dropdown-username">{{ userStore.userInfo?.nickname }}</span>
                    <span class="dropdown-email">@{{ userStore.userInfo?.username }}</span>
                  </div>
                </div>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="follows">
                  <el-icon><Star /></el-icon>我的关注
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isCreator" command="dashboard">
                  <el-icon><DataAnalysis /></el-icon>数据看板
                </el-dropdown-item>
                <el-dropdown-item v-if="!userStore.isCreator" command="apply">
                  <el-icon><Promotion /></el-icon>申请入驻
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" class="logout-item">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login">
            <el-button class="btn-login" plain>登录</el-button>
          </router-link>
          <router-link to="/register">
            <el-button type="primary" class="btn-register">免费注册</el-button>
          </router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'follows':
      router.push('/my-follows')
      break
    case 'dashboard':
      router.push('/dashboard')
      break
    case 'apply':
      router.push('/apply-creator')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}
</script>

<style lang="scss" scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 72px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 1000;
  transition: all $transition-base;
  border-bottom: 1px solid transparent;

  &.header-scrolled {
    background: rgba(255, 255, 255, 0.95);
    border-bottom-color: $border-light;
    box-shadow: $shadow-sm;
  }
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  text-decoration: none;
  
  .logo-icon {
    width: 40px;
    height: 40px;
    
    svg {
      width: 100%;
      height: 100%;
    }
  }
  
  .logo-text {
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
    letter-spacing: -0.02em;
  }
}

.nav {
  display: flex;
  gap: $spacing-xs;
  
  @media (max-width: 768px) {
    display: none;
  }
  
  .nav-link {
    display: inline-flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-sm $spacing-md;
    color: $text-secondary;
    font-weight: $font-weight-medium;
    border-radius: $border-radius-full;
    transition: all $transition-fast;
    
    .el-icon {
      font-size: 18px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
    }
    
    span {
      line-height: 1;
    }
    
    &:hover {
      color: $primary-color;
      background: rgba($primary-color, 0.08);
    }
    
    &.router-link-active {
      color: $primary-color;
      background: rgba($primary-color, 0.1);
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: $spacing-md;
}

.balance-badge {
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  padding: $spacing-xs $spacing-md;
  background: linear-gradient(135deg, #FFF9F7, #FFE8E0);
  border-radius: $border-radius-full;
  font-weight: $font-weight-semibold;
  color: $primary-dark;
  font-size: $font-size-sm;
  
  .el-icon {
    font-size: 16px;
    display: inline-flex;
    align-items: center;
  }
  
  span {
    line-height: 1;
  }
}

.user-avatar-wrapper {
  position: relative;
  cursor: pointer;
  
  .user-avatar {
    border: 2px solid $card-background;
    box-shadow: $shadow-md;
    transition: all $transition-fast;
  }
  
  .avatar-status {
    position: absolute;
    bottom: 2px;
    right: 2px;
    width: 12px;
    height: 12px;
    background: $success-color;
    border: 2px solid $card-background;
    border-radius: 50%;
  }
  
  &:hover .user-avatar {
    transform: scale(1.05);
    box-shadow: $shadow-lg;
  }
}

.user-dropdown {
  padding: 0 !important;
  min-width: 220px;
  border-radius: $border-radius-lg !important;
  overflow: hidden;
  
  .dropdown-header {
    display: flex;
    align-items: center;
    gap: $spacing-md;
    padding: $spacing-lg;
    background: $card-background;
    border-bottom: 1px solid $border-light;
  }
  
  .dropdown-user-info {
    display: flex;
    flex-direction: column;
  }
  
  .dropdown-username {
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }
  
  .dropdown-email {
    font-size: $font-size-sm;
    color: $text-muted;
  }
  
  :deep(.el-dropdown-menu__item) {
    padding: 12px 16px;
    font-size: $font-size-sm;
    background: $card-background !important;
    
    .el-icon {
      margin-right: $spacing-sm;
      font-size: 16px;
    }
    
    &:hover {
      background: $background-color !important;
      color: $primary-color;
    }
  }
  
  .logout-item {
    color: $danger-color !important;
    
    &:hover {
      background: rgba($danger-color, 0.05) !important;
    }
  }
}

.btn-login {
  border: 2px solid $border-color !important;
  color: $text-primary !important;
  font-weight: $font-weight-medium;
  
  &:hover {
    border-color: $primary-color !important;
    color: $primary-color !important;
  }
}

.btn-register {
  font-weight: $font-weight-semibold;
}
</style>
