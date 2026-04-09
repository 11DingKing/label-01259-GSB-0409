<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-shape hero-shape-1"></div>
        <div class="hero-shape hero-shape-2"></div>
        <div class="hero-shape hero-shape-3"></div>
      </div>
      
      <div class="hero-content container">
        <div class="hero-text">
          <span class="hero-badge">
            <el-icon><Star /></el-icon>
            支持原创创作者
          </span>
          <h1 class="hero-title">
            让创作<br/>
            <span class="gradient-text">更有价值</span>
          </h1>
          <p class="hero-desc">
            发现优质原创内容，关注喜爱的创作者，用打赏表达你的支持。
            加入我们，开启你的创作之旅。
          </p>
          <div class="hero-actions">
            <router-link to="/creators">
              <el-button type="primary" size="large" class="btn-explore">
                <el-icon><Search /></el-icon>
                发现创作者
              </el-button>
            </router-link>
            <router-link v-if="!userStore.isCreator" to="/apply-creator">
              <el-button size="large" class="btn-become">
                成为创作者
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </router-link>
          </div>
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-value">1,000+</span>
              <span class="stat-label">创作者入驻</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">50,000+</span>
              <span class="stat-label">优质内容</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">100,000+</span>
              <span class="stat-label">活跃用户</span>
            </div>
          </div>
        </div>
        <div class="hero-visual">
          <div class="visual-card visual-card-1">
            <img src="https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=400&h=300&fit=crop" alt="创作者" />
          </div>
          <div class="visual-card visual-card-2">
            <img src="https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=300&fit=crop" alt="团队协作" />
          </div>
          <div class="visual-card visual-card-3">
            <img src="https://images.unsplash.com/photo-1531482615713-2afd69097998?w=400&h=300&fit=crop" alt="创意工作" />
          </div>
        </div>
      </div>
    </section>
    
    <!-- Featured Creators -->
    <section class="section section-creators">
      <div class="container">
        <div class="section-header">
          <div class="section-title-group">
            <span class="section-label">精选推荐</span>
            <h2 class="section-title">热门创作者</h2>
          </div>
          <router-link to="/creators" class="view-all">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="creator-grid" v-loading="creatorsLoading">
          <CreatorCard
            v-for="creator in creators"
            :key="creator.id"
            :creator="creator"
            @update="loadCreators"
          />
        </div>
      </div>
    </section>
    
    <!-- Latest Contents -->
    <section class="section section-contents">
      <div class="container">
        <div class="section-header">
          <div class="section-title-group">
            <span class="section-label">最新发布</span>
            <h2 class="section-title">精彩内容</h2>
          </div>
          <div class="content-tabs">
            <button 
              :class="['tab-btn', { active: contentType === null }]"
              @click="contentType = null; loadContents()"
            >全部</button>
            <button 
              :class="['tab-btn', { active: contentType === 1 }]"
              @click="contentType = 1; loadContents()"
            >文章</button>
            <button 
              :class="['tab-btn', { active: contentType === 2 }]"
              @click="contentType = 2; loadContents()"
            >视频</button>
            <button 
              :class="['tab-btn', { active: contentType === 3 }]"
              @click="contentType = 3; loadContents()"
            >音频</button>
          </div>
        </div>
        <div class="content-grid" v-loading="contentsLoading">
          <ContentCard v-for="content in contents" :key="content.id" :content="content" />
        </div>
        <div class="load-more" v-if="hasMore">
          <el-button @click="loadMore" :loading="loadingMore" size="large" class="btn-load-more">
            加载更多内容
          </el-button>
        </div>
      </div>
    </section>
    
    <!-- Features -->
    <section class="section section-features">
      <div class="container">
        <div class="section-header section-header-center">
          <span class="section-label">为什么选择我们</span>
          <h2 class="section-title">平台优势</h2>
          <p class="section-desc">我们致力于为创作者和粉丝搭建最好的连接桥梁</p>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon icon-check">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
            </div>
            <h3>优质原创</h3>
            <p>严格审核机制，确保每一份内容都是高质量原创作品</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon icon-time">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
            </div>
            <h3>即时到账</h3>
            <p>打赏直达创作者账户，无中间抽成，支持即时提现</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon icon-chart">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/></svg>
            </div>
            <h3>数据透明</h3>
            <p>完整数据看板，实时追踪收益、粉丝增长和内容表现</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon icon-community">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/></svg>
            </div>
            <h3>互动社区</h3>
            <p>评论、打赏、关注，建立创作者与粉丝的深度连接</p>
          </div>
        </div>
      </div>
    </section>
    
    <!-- CTA Section - 只在未登录时显示 -->
    <section v-if="!userStore.isLoggedIn" class="section section-cta">
      <div class="container">
        <div class="cta-card">
          <div class="cta-content">
            <h2>准备好开始创作了吗？</h2>
            <p>加入数千名创作者，分享你的才华，获得应有的回报</p>
            <router-link to="/register">
              <el-button type="primary" size="large" class="btn-cta">
                立即开始 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </router-link>
          </div>
          <div class="cta-decoration">
            <div class="cta-circle cta-circle-1"></div>
            <div class="cta-circle cta-circle-2"></div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCreatorList } from '@/api/creator'
import { getContentList } from '@/api/content'
import ContentCard from '@/components/ContentCard.vue'
import CreatorCard from '@/components/CreatorCard.vue'

const userStore = useUserStore()
const creators = ref([])
const creatorsLoading = ref(false)
const contents = ref([])
const contentsLoading = ref(false)
const contentType = ref(null)
const currentPage = ref(1)
const hasMore = ref(true)
const loadingMore = ref(false)

const loadCreators = async () => {
  creatorsLoading.value = true
  try {
    const res = await getCreatorList({ page: 1, size: 4 })
    creators.value = res.data.records
  } catch (error) {
    console.error(error)
  } finally {
    creatorsLoading.value = false
  }
}

const loadContents = async () => {
  contentsLoading.value = true
  currentPage.value = 1
  try {
    const res = await getContentList({ page: 1, size: 8, contentType: contentType.value })
    contents.value = res.data.records
    hasMore.value = res.data.records.length >= 8
  } catch (error) {
    console.error(error)
  } finally {
    contentsLoading.value = false
  }
}

const loadMore = async () => {
  loadingMore.value = true
  currentPage.value++
  try {
    const res = await getContentList({ page: currentPage.value, size: 8, contentType: contentType.value })
    contents.value.push(...res.data.records)
    hasMore.value = res.data.records.length >= 8
  } catch (error) {
    console.error(error)
  } finally {
    loadingMore.value = false
  }
}

onMounted(() => {
  loadCreators()
  loadContents()
})
</script>

<style lang="scss" scoped>
.hero {
  position: relative;
  min-height: calc(100vh - 72px);
  display: flex;
  align-items: center;
  padding: $spacing-4xl 0;
  overflow: hidden;
  background: $background-warm;
}

.hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  .hero-shape {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.6;
  }
  .hero-shape-1 {
    width: 600px;
    height: 600px;
    background: rgba($primary-color, 0.15);
    top: -200px;
    right: 10%;
  }
  .hero-shape-2 {
    width: 400px;
    height: 400px;
    background: rgba($accent-color, 0.15);
    bottom: -100px;
    left: 10%;
  }
  .hero-shape-3 {
    width: 300px;
    height: 300px;
    background: rgba(#667eea, 0.1);
    top: 50%;
    left: 40%;
  }
}

.hero-content {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-3xl;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 $spacing-2xl;
  @media (min-width: 1600px) {
    max-width: 1600px;
    gap: $spacing-4xl;
  }
  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
    text-align: center;
    padding: 0 $spacing-lg;
  }
}

.hero-text {
  .hero-badge {
    display: inline-flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-xs $spacing-md;
    background: rgba($primary-color, 0.1);
    border-radius: $border-radius-full;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $primary-color;
    margin-bottom: $spacing-lg;
  }
  .hero-title {
    font-size: $font-size-5xl;
    font-weight: $font-weight-extrabold;
    line-height: 1.1;
    color: $text-primary;
    margin-bottom: $spacing-lg;
    letter-spacing: -0.03em;
    @media (max-width: 768px) { font-size: $font-size-4xl; }
    .gradient-text {
      background: $gradient-primary;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }
  .hero-desc {
    font-size: $font-size-lg;
    color: $text-secondary;
    line-height: 1.7;
    margin-bottom: $spacing-xl;
    max-width: 480px;
    @media (max-width: 1024px) { margin: 0 auto $spacing-xl; }
  }
}

.hero-actions {
  display: flex;
  gap: $spacing-md;
  margin-bottom: $spacing-2xl;
  @media (max-width: 1024px) { justify-content: center; }
  @media (max-width: 480px) { flex-direction: column; }
  .btn-explore {
    padding: 14px 32px !important;
    font-size: $font-size-base;
    height: auto !important;
  }
  .btn-become {
    padding: 14px 32px !important;
    font-size: $font-size-base;
    height: auto !important;
    background: transparent !important;
    border: 2px solid $border-color !important;
    color: $text-primary !important;
    &:hover {
      border-color: $primary-color !important;
      color: $primary-color !important;
    }
  }
}

.hero-stats {
  display: flex;
  gap: $spacing-2xl;
  @media (max-width: 1024px) { justify-content: center; }
  @media (max-width: 480px) { gap: $spacing-lg; }
  .stat-item {
    .stat-value {
      display: block;
      font-size: $font-size-2xl;
      font-weight: $font-weight-bold;
      color: $text-primary;
      @media (max-width: 480px) { font-size: $font-size-xl; }
    }
    .stat-label {
      font-size: $font-size-sm;
      color: $text-muted;
    }
  }
}

.hero-visual {
  position: relative;
  height: 500px;
  @media (min-width: 1600px) { height: 600px; }
  @media (max-width: 1024px) { display: none; }
  .visual-card {
    position: absolute;
    border-radius: $border-radius-xl;
    overflow: hidden;
    box-shadow: $shadow-xl;
    transition: all $transition-slow;
    img { width: 100%; height: 100%; object-fit: cover; }
    &:hover { transform: scale(1.05) !important; z-index: 10; }
  }
  .visual-card-1 { width: 280px; height: 200px; top: 0; right: 40px; transform: rotate(3deg); @media (min-width: 1600px) { width: 340px; height: 240px; } }
  .visual-card-2 { width: 260px; height: 180px; top: 180px; right: 180px; transform: rotate(-5deg); @media (min-width: 1600px) { width: 320px; height: 220px; top: 220px; right: 240px; } }
  .visual-card-3 { width: 240px; height: 160px; bottom: 40px; right: 60px; transform: rotate(2deg); @media (min-width: 1600px) { width: 300px; height: 200px; } }
}

.section { padding: $spacing-4xl 0; }
.section-creators { background: $card-background; }
.section-contents { background: $background-color; }

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: $spacing-xl;
  @media (max-width: 640px) { flex-direction: column; align-items: flex-start; gap: $spacing-md; }
}

.section-header-center {
  flex-direction: column;
  align-items: center;
  text-align: center;
  .section-desc { max-width: 500px; color: $text-secondary; margin-top: $spacing-sm; }
}

.section-title-group {
  .section-label {
    display: block;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $primary-color;
    text-transform: uppercase;
    letter-spacing: 0.1em;
    margin-bottom: $spacing-xs;
  }
  .section-title {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
    letter-spacing: -0.02em;
    margin: 0;
  }
}

.view-all {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  font-weight: $font-weight-semibold;
  color: $primary-color;
  transition: all $transition-fast;
  &:hover { color: $primary-dark; }
}

.content-tabs {
  display: flex;
  gap: $spacing-xs;
  background: $background-color;
  padding: 4px;
  border-radius: $border-radius-full;
  .tab-btn {
    padding: $spacing-sm $spacing-md;
    border: none;
    background: transparent;
    border-radius: $border-radius-full;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-secondary;
    cursor: pointer;
    transition: all $transition-fast;
    &:hover { color: $text-primary; }
    &.active { background: $card-background; color: $primary-color; box-shadow: $shadow-sm; }
  }
}

.creator-grid, .content-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;
  @media (max-width: 1024px) { grid-template-columns: repeat(2, 1fr); }
  @media (max-width: 640px) { grid-template-columns: 1fr; }
}

.load-more {
  text-align: center;
  margin-top: $spacing-2xl;
  .btn-load-more {
    padding: 14px 40px !important;
    background: $card-background !important;
    border: 2px solid $border-color !important;
    color: $text-primary !important;
    font-weight: $font-weight-semibold;
    &:hover { border-color: $primary-color !important; color: $primary-color !important; }
  }
}

.section-features { background: $background-warm; }

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;
  margin-top: $spacing-2xl;
  @media (max-width: 1024px) { grid-template-columns: repeat(2, 1fr); }
  @media (max-width: 640px) { grid-template-columns: 1fr; }
}

.feature-card {
  background: $card-background;
  border-radius: $border-radius-xl;
  padding: $spacing-xl;
  text-align: center;
  transition: all $transition-base;
  border: 1px solid $border-light;
  &:hover { transform: translateY(-8px); box-shadow: $shadow-lg; border-color: transparent; }
  .feature-icon {
    width: 64px;
    height: 64px;
    margin: 0 auto $spacing-md;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    svg { width: 32px; height: 32px; }
  }
  .icon-check { background: $gradient-primary; }
  .icon-time { background: $gradient-accent; }
  .icon-chart { background: linear-gradient(135deg, #667eea, #764ba2); }
  .icon-community { background: linear-gradient(135deg, #f093fb, #f5576c); }
  h3 { font-size: $font-size-lg; font-weight: $font-weight-bold; color: $text-primary; margin-bottom: $spacing-sm; }
  p { font-size: $font-size-sm; color: $text-muted; line-height: 1.6; }
}

.section-cta { background: $card-background; padding: $spacing-3xl 0; }

.cta-card {
  position: relative;
  background: $gradient-primary;
  border-radius: $border-radius-2xl;
  padding: $spacing-3xl;
  text-align: center;
  overflow: hidden;
}

.cta-content {
  position: relative;
  z-index: 1;
  h2 { font-size: $font-size-3xl; font-weight: $font-weight-bold; color: white; margin-bottom: $spacing-sm; @media (max-width: 640px) { font-size: $font-size-2xl; } }
  p { font-size: $font-size-lg; color: rgba(white, 0.9); margin-bottom: $spacing-xl; }
  .btn-cta {
    background: white !important;
    color: $primary-color !important;
    border: none !important;
    padding: 14px 40px !important;
    font-weight: $font-weight-bold;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    &:hover { transform: translateY(-2px); box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2); }
  }
}

.cta-decoration {
  position: absolute;
  inset: 0;
  .cta-circle { position: absolute; border-radius: 50%; background: rgba(white, 0.1); }
  .cta-circle-1 { width: 300px; height: 300px; top: -100px; right: -50px; }
  .cta-circle-2 { width: 200px; height: 200px; bottom: -80px; left: -40px; }
}
</style>
