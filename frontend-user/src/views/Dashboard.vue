<template>
  <div class="dashboard-page">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">数据看板</h1>
        <p class="page-desc">实时追踪你的创作数据表现</p>
      </div>
    </div>
    
    <div class="container">
      <div class="stats-grid" v-loading="loading">
        <div class="stat-card">
          <div class="stat-icon icon-income"><el-icon><Wallet /></el-icon></div>
          <div class="stat-info">
            <span class="label">总收入</span>
            <span class="value">¥{{ analytics.totalIncome?.toFixed(2) || '0.00' }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon icon-views"><el-icon><View /></el-icon></div>
          <div class="stat-info">
            <span class="label">总浏览</span>
            <span class="value">{{ formatCount(analytics.totalViews) }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon icon-likes"><el-icon><Star /></el-icon></div>
          <div class="stat-info">
            <span class="label">总点赞</span>
            <span class="value">{{ formatCount(analytics.totalLikes) }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon icon-fans"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <span class="label">粉丝数</span>
            <span class="value">{{ formatCount(analytics.totalFollowers) }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon icon-contents"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <span class="label">作品数</span>
            <span class="value">{{ analytics.totalContents || 0 }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon icon-comments"><el-icon><ChatDotRound /></el-icon></div>
          <div class="stat-info">
            <span class="label">评论数</span>
            <span class="value">{{ formatCount(analytics.totalComments) }}</span>
          </div>
        </div>
      </div>
      
      <div class="charts-grid">
        <div class="chart-card">
          <h3 class="chart-title">收入趋势（近7天）</h3>
          <v-chart :option="incomeChartOption" autoresize class="chart" />
        </div>
        <div class="chart-card">
          <h3 class="chart-title">浏览趋势（近7天）</h3>
          <v-chart :option="viewChartOption" autoresize class="chart" />
        </div>
        <div class="chart-card">
          <h3 class="chart-title">新增粉丝（近7天）</h3>
          <v-chart :option="fansChartOption" autoresize class="chart" />
        </div>
      </div>
      
      <div class="rewards-section">
        <h2 class="section-title">最近打赏</h2>
        <div class="rewards-list" v-loading="rewardsLoading">
          <div v-for="reward in rewards" :key="reward.id" class="reward-item">
            <el-avatar :size="44" :src="reward.avatar">{{ reward.username?.charAt(0) }}</el-avatar>
            <div class="reward-info">
              <span class="username">{{ reward.username }}</span>
              <span class="message">{{ reward.message || '打赏了你' }}</span>
            </div>
            <div class="reward-amount">+¥{{ reward.amount }}</div>
          </div>
          <div v-if="!rewardsLoading && rewards.length === 0" class="empty-state">
            <el-empty description="暂无打赏记录" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getAnalyticsOverview } from '@/api/analytics'
import { getRewardList } from '@/api/interaction'

use([CanvasRenderer, LineChart, BarChart, GridComponent, TooltipComponent])

const analytics = ref({})
const loading = ref(false)
const rewards = ref([])
const rewardsLoading = ref(false)

const formatCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + 'w'
  if (count >= 1000) return (count / 1000).toFixed(1) + 'k'
  return count.toString()
}

const incomeChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: analytics.value.incomeChart?.map(i => i.date) || [],
    axisLine: { lineStyle: { color: '#E8E8F0' } },
    axisLabel: { color: '#8E8EA9' },
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    splitLine: { lineStyle: { color: '#F0F0F8' } },
    axisLabel: { color: '#8E8EA9' },
  },
  series: [{
    data: analytics.value.incomeChart?.map(i => i.value) || [],
    type: 'line',
    smooth: true,
    lineStyle: { color: '#FF6B6B', width: 3 },
    areaStyle: {
      color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [{ offset: 0, color: 'rgba(255, 107, 107, 0.3)' }, { offset: 1, color: 'rgba(255, 107, 107, 0)' }],
      },
    },
    itemStyle: { color: '#FF6B6B' },
  }],
}))

const viewChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: analytics.value.viewChart?.map(i => i.date) || [],
    axisLine: { lineStyle: { color: '#E8E8F0' } },
    axisLabel: { color: '#8E8EA9' },
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    splitLine: { lineStyle: { color: '#F0F0F8' } },
    axisLabel: { color: '#8E8EA9' },
  },
  series: [{
    data: analytics.value.viewChart?.map(i => i.value) || [],
    type: 'bar',
    barWidth: '60%',
    itemStyle: {
      color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [{ offset: 0, color: '#4ECDC4' }, { offset: 1, color: '#44A08D' }],
      },
      borderRadius: [6, 6, 0, 0],
    },
  }],
}))

const fansChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: analytics.value.fansChart?.map(i => i.date) || [],
    axisLine: { lineStyle: { color: '#E8E8F0' } },
    axisLabel: { color: '#8E8EA9' },
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    splitLine: { lineStyle: { color: '#F0F0F8' } },
    axisLabel: { color: '#8E8EA9' },
  },
  series: [{
    data: analytics.value.fansChart?.map(i => i.value) || [],
    type: 'line',
    smooth: true,
    lineStyle: { color: '#667eea', width: 3 },
    areaStyle: {
      color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [{ offset: 0, color: 'rgba(102, 126, 234, 0.3)' }, { offset: 1, color: 'rgba(102, 126, 234, 0)' }],
      },
    },
    itemStyle: { color: '#667eea' },
  }],
}))

const loadAnalytics = async () => {
  loading.value = true
  try {
    const res = await getAnalyticsOverview()
    analytics.value = res.data
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

const loadRewards = async () => {
  rewardsLoading.value = true
  try {
    const res = await getRewardList({ page: 1, size: 5 })
    rewards.value = res.data.records
  } catch (error) { console.error(error) }
  finally { rewardsLoading.value = false }
}

onMounted(() => { loadAnalytics(); loadRewards() })
</script>

<style lang="scss" scoped>
.dashboard-page {
  min-height: calc(100vh - 72px);
  background: $background-color;
}

.page-header {
  background: linear-gradient(135deg, #667eea, #764ba2);
  padding: $spacing-2xl 0;
  
  .page-title {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    color: white;
    margin-bottom: $spacing-xs;
  }
  
  .page-desc { color: rgba(white, 0.8); }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: $spacing-md;
  margin: -$spacing-lg 0 $spacing-xl;
  
  @media (max-width: 1200px) { grid-template-columns: repeat(3, 1fr); }
  @media (max-width: 768px) { grid-template-columns: repeat(2, 1fr); }
}

.stat-card {
  background: $card-background;
  border-radius: $border-radius-xl;
  box-shadow: $shadow-lg;
  padding: $spacing-md;
  display: flex;
  align-items: center;
  gap: $spacing-md;
  
  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: $border-radius-lg;
    display: flex;
    align-items: center;
    justify-content: center;
    .el-icon { font-size: 24px; color: white; }
    
    &.icon-income { background: $gradient-primary; }
    &.icon-views { background: $gradient-accent; }
    &.icon-likes { background: linear-gradient(135deg, #F1C40F, #F39C12); }
    &.icon-fans { background: linear-gradient(135deg, #EC4899, #DB2777); }
    &.icon-contents { background: linear-gradient(135deg, #3498DB, #2980B9); }
    &.icon-comments { background: linear-gradient(135deg, #9B59B6, #8E44AD); }
  }
  
  .stat-info {
    .label { display: block; font-size: $font-size-xs; color: $text-muted; }
    .value { font-size: $font-size-lg; font-weight: $font-weight-bold; color: $text-primary; }
  }
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;
  
  @media (max-width: 1024px) { grid-template-columns: 1fr; }
}

.chart-card {
  background: $card-background;
  border-radius: $border-radius-xl;
  box-shadow: $shadow-md;
  padding: $spacing-lg;
  
  .chart-title {
    font-size: $font-size-base;
    font-weight: $font-weight-semibold;
    margin-bottom: $spacing-md;
    color: $text-primary;
  }
  
  .chart { height: 250px; }
}

.rewards-section {
  background: $card-background;
  border-radius: $border-radius-xl;
  box-shadow: $shadow-md;
  padding: $spacing-xl;
  margin-bottom: $spacing-xl;
  
  .section-title {
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    margin-bottom: $spacing-lg;
    color: $text-primary;
  }
}

.reward-item {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-md;
  border-radius: $border-radius-lg;
  transition: background $transition-fast;
  
  &:hover { background: $background-color; }
  
  .el-avatar { border: 2px solid $card-background; box-shadow: $shadow-sm; }
  
  .reward-info {
    flex: 1;
    .username { font-weight: $font-weight-semibold; color: $text-primary; margin-right: $spacing-sm; }
    .message { color: $text-muted; font-size: $font-size-sm; }
  }
  
  .reward-amount {
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $success-color;
    background: rgba($success-color, 0.1);
    padding: $spacing-xs $spacing-md;
    border-radius: $border-radius-full;
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  padding: $spacing-xl;
}
</style>
