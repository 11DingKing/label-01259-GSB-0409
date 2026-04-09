<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="logo">
        <el-icon><Setting /></el-icon>
        <span>管理后台</span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        router
        background-color="#1F2937"
        text-color="#9CA3AF"
        active-text-color="#FFFFFF"
      >
        <el-menu-item index="/">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/creators">
          <el-icon><UserFilled /></el-icon>
          <span>创作者审核</span>
        </el-menu-item>
        <el-menu-item index="/contents">
          <el-icon><Document /></el-icon>
          <span>内容管理</span>
        </el-menu-item>
        <el-menu-item index="/logs">
          <el-icon><List /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
      </el-menu>
    </aside>
    
    <div class="main-container">
      <header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32">{{ userStore.userInfo?.nickname?.charAt(0) }}</el-avatar>
              <span>{{ userStore.userInfo?.nickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const pageTitles = {
  '/': '数据概览',
  '/users': '用户管理',
  '/creators': '创作者审核',
  '/contents': '内容管理',
  '/logs': '操作日志',
}

const currentPageTitle = computed(() => pageTitles[route.path] || '')

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables.scss' as *;

.admin-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 220px;
  background: $sidebar-bg;
  flex-shrink: 0;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-sm;
    color: white;
    font-size: $font-size-lg;
    font-weight: 600;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    .el-icon {
      font-size: 24px;
      color: $primary-color;
    }
  }
  
  .el-menu {
    border-right: none;
    
    .el-menu-item {
      &:hover {
        background: rgba(255, 255, 255, 0.05);
      }
      
      &.is-active {
        background: $primary-color;
      }
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background: $card-background;
  box-shadow: $shadow-sm;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-lg;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  cursor: pointer;
  
  span {
    font-weight: 500;
  }
}

.content {
  flex: 1;
  padding: $spacing-lg;
  overflow-y: auto;
}
</style>
