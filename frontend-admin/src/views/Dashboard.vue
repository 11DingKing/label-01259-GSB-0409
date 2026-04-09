<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h1 class="page-title">数据概览</h1>
    </div>
    
    <div class="stats-grid" v-loading="loading">
      <div class="stat-card">
        <div class="stat-icon users"><el-icon><User /></el-icon></div>
        <div class="stat-info">
          <span class="value">{{ stats.totalUsers || 0 }}</span>
          <span class="label">总用户数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon creators"><el-icon><UserFilled /></el-icon></div>
        <div class="stat-info">
          <span class="value">{{ stats.totalCreators || 0 }}</span>
          <span class="label">创作者数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon contents"><el-icon><Document /></el-icon></div>
        <div class="stat-info">
          <span class="value">{{ stats.totalContents || 0 }}</span>
          <span class="label">内容数</span>
        </div>
      </div>
      <div class="stat-card pending">
        <div class="stat-icon"><el-icon><Clock /></el-icon></div>
        <div class="stat-info">
          <span class="value">{{ stats.pendingCreators || 0 }}</span>
          <span class="label">待审核</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rewards"><el-icon><Present /></el-icon></div>
        <div class="stat-info">
          <span class="value">¥{{ stats.totalRewards?.toFixed(2) || '0.00' }}</span>
          <span class="label">打赏总额</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purchases"><el-icon><Wallet /></el-icon></div>
        <div class="stat-info">
          <span class="value">¥{{ stats.totalPurchases?.toFixed(2) || '0.00' }}</span>
          <span class="label">购买总额</span>
        </div>
      </div>
    </div>
    
    <div class="info-cards">
      <div class="info-card">
        <h3>本周新增</h3>
        <div class="info-row">
          <span>新用户</span>
          <span class="value">{{ stats.newUsersThisWeek || 0 }}</span>
        </div>
        <div class="info-row">
          <span>新内容</span>
          <span class="value">{{ stats.newContentsThisWeek || 0 }}</span>
        </div>
      </div>
      <div class="info-card">
        <h3>快捷操作</h3>
        <div class="quick-actions">
          <router-link to="/creators">
            <el-button type="primary">审核创作者</el-button>
          </router-link>
          <router-link to="/contents">
            <el-button>管理内容</el-button>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '@/api'

const stats = ref({})
const loading = ref(false)

const loadStats = async () => {
  loading.value = true
  try {
    const res = await getStatistics()
    stats.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => loadStats())
</script>

<style lang="scss" scoped>
@use '@/styles/variables.scss' as *;

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: $spacing-md;
  margin-bottom: $spacing-lg;
  
  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }
  
  @media (max-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.stat-card {
  background: $card-background;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  display: flex;
  align-items: center;
  gap: $spacing-md;
  box-shadow: $shadow-md;
  
  &.pending {
    border-left: 4px solid $warning-color;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: $border-radius-md;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      font-size: 24px;
      color: white;
    }
    
    &.users { background: linear-gradient(135deg, #3B82F6, #2563EB); }
    &.creators { background: linear-gradient(135deg, #6366F1, #4F46E5); }
    &.contents { background: linear-gradient(135deg, #10B981, #059669); }
    &.rewards { background: linear-gradient(135deg, #F59E0B, #D97706); }
    &.purchases { background: linear-gradient(135deg, #EC4899, #DB2777); }
  }
  
  .stat-info {
    .value {
      display: block;
      font-size: $font-size-xl;
      font-weight: 700;
      color: $text-primary;
    }
    
    .label {
      font-size: $font-size-xs;
      color: $text-secondary;
    }
  }
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.info-card {
  background: $card-background;
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  box-shadow: $shadow-md;
  
  h3 {
    font-size: $font-size-base;
    font-weight: 600;
    margin-bottom: $spacing-md;
    color: $text-primary;
  }
  
  .info-row {
    display: flex;
    justify-content: space-between;
    padding: $spacing-sm 0;
    border-bottom: 1px solid $border-color;
    
    &:last-child {
      border-bottom: none;
    }
    
    .value {
      font-weight: 600;
      color: $primary-color;
    }
  }
  
  .quick-actions {
    display: flex;
    gap: $spacing-md;
  }
}
</style>
