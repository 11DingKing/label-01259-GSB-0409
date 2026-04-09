<template>
  <article class="content-card" @click="goToDetail">
    <div class="card-cover">
      <img 
        v-if="content.coverImage" 
        :src="content.coverImage" 
        :alt="content.title"
        loading="lazy"
      />
      <div v-else class="cover-placeholder">
        <el-icon><Picture /></el-icon>
      </div>
      
      <div class="cover-overlay">
        <div class="overlay-actions">
          <el-button circle size="small" class="action-btn">
            <el-icon><View /></el-icon>
          </el-button>
        </div>
      </div>
      
      <div class="card-badges">
        <span class="type-badge">{{ contentTypeText }}</span>
        <span v-if="content.isPaid" class="paid-badge">
          <el-icon><Lock /></el-icon>
          ¥{{ content.price }}
        </span>
      </div>
    </div>
    
    <div class="card-body">
      <h3 class="title">{{ content.title }}</h3>
      <p class="summary">{{ content.summary || '暂无简介' }}</p>
      
      <div class="card-footer">
        <div class="creator-info" @click.stop="goToCreator">
          <el-avatar :size="28" :src="content.creatorAvatar">
            {{ content.creatorName?.charAt(0) }}
          </el-avatar>
          <span class="creator-name">{{ content.creatorName }}</span>
        </div>
        
        <div class="stats">
          <span class="stat-item">
            <el-icon><View /></el-icon>
            {{ formatCount(content.viewCount) }}
          </span>
          <span class="stat-item">
            <el-icon><Star /></el-icon>
            {{ formatCount(content.likeCount) }}
          </span>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  content: {
    type: Object,
    required: true,
  },
})

const router = useRouter()

const contentTypeMap = {
  1: '文章',
  2: '视频',
  3: '音频',
  4: '图集',
}

const contentTypeText = computed(() => contentTypeMap[props.content.contentType] || '文章')

const formatCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + 'w'
  if (count >= 1000) return (count / 1000).toFixed(1) + 'k'
  return count.toString()
}

const goToDetail = () => {
  router.push(`/content/${props.content.id}`)
}

const goToCreator = () => {
  router.push(`/creator/${props.content.creatorId}`)
}
</script>

<style lang="scss" scoped>
.content-card {
  background: $card-background;
  border-radius: $border-radius-lg;
  overflow: hidden;
  cursor: pointer;
  transition: all $transition-base;
  border: 1px solid $border-light;
  display: flex;
  flex-direction: column;
  height: 100%;
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: $shadow-lg;
    border-color: transparent;
    
    .card-cover img {
      transform: scale(1.08);
    }
    
    .cover-overlay {
      opacity: 1;
    }
    
    .title {
      color: $primary-color;
    }
  }
}

.card-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f5f5, #e8e8e8);
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform $transition-slow;
  }
  
  .cover-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: $gradient-warm;
    
    .el-icon {
      font-size: 48px;
      color: $text-placeholder;
    }
  }
  
  .cover-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to top, rgba(0,0,0,0.6) 0%, transparent 50%);
    opacity: 0;
    transition: opacity $transition-base;
    display: flex;
    align-items: flex-end;
    justify-content: center;
    padding-bottom: $spacing-md;
    
    .action-btn {
      background: rgba(255,255,255,0.9) !important;
      border: none !important;
      
      &:hover {
        background: white !important;
        transform: scale(1.1);
      }
    }
  }
  
  .card-badges {
    position: absolute;
    top: $spacing-sm;
    left: $spacing-sm;
    right: $spacing-sm;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }
  
  .type-badge {
    padding: 4px 12px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: $border-radius-full;
    font-size: $font-size-xs;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    box-shadow: $shadow-sm;
  }
  
  .paid-badge {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 4px 12px;
    background: $gradient-primary;
    border-radius: $border-radius-full;
    font-size: $font-size-xs;
    font-weight: $font-weight-bold;
    color: white;
    box-shadow: 0 2px 8px rgba(255, 107, 107, 0.4);
    
    .el-icon {
      font-size: 12px;
    }
  }
}

.card-body {
  padding: $spacing-md;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.title {
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
  transition: color $transition-fast;
  letter-spacing: -0.01em;
  min-height: 48px;
}

.summary {
  font-size: $font-size-sm;
  color: $text-muted;
  margin-bottom: $spacing-md;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.6;
  flex: 1;
  min-height: 44px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: $spacing-sm;
  border-top: 1px solid $border-light;
  margin-top: auto;
}

.creator-info {
  display: inline-flex;
  align-items: center;
  gap: $spacing-sm;
  min-width: 0;
  flex: 1;
  
  .el-avatar {
    border: 2px solid $card-background;
    box-shadow: $shadow-xs;
    flex-shrink: 0;
  }
  
  .creator-name {
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-secondary;
    transition: color $transition-fast;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1;
    
    &:hover {
      color: $primary-color;
    }
  }
}

.stats {
  display: flex;
  gap: $spacing-md;
  flex-shrink: 0;
  
  .stat-item {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: $font-size-xs;
    color: $text-muted;
    line-height: 1;
    
    .el-icon {
      font-size: 14px;
      display: inline-flex;
      align-items: center;
    }
  }
}
</style>
