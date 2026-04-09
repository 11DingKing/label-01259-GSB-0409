<template>
  <article class="creator-card" @click="goToDetail">
    <div class="card-banner">
      <div class="banner-pattern"></div>
    </div>
    
    <div class="card-avatar">
      <el-avatar :size="80" :src="creator.avatar">
        {{ creator.penName?.charAt(0) }}
      </el-avatar>
      <span class="category-badge">{{ creator.category }}</span>
    </div>
    
    <div class="card-body">
      <h3 class="pen-name">{{ creator.penName }}</h3>
      <p class="bio">{{ creator.bio || '这位创作者很神秘，什么都没写~' }}</p>
      
      <div class="stats">
        <div class="stat-item">
          <span class="value">{{ formatCount(creator.followerCount) }}</span>
          <span class="label">粉丝</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="value">{{ formatCount(creator.contentCount) }}</span>
          <span class="label">作品</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="value">{{ formatCount(creator.totalLikes) }}</span>
          <span class="label">获赞</span>
        </div>
      </div>
    </div>
    
    <div class="card-footer">
      <el-button
        v-if="!creator.isFollowed"
        type="primary"
        class="follow-btn"
        @click.stop="handleFollow"
        :loading="loading"
      >
        <el-icon><Plus /></el-icon>
        <span>关注</span>
      </el-button>
      <el-button
        v-else
        class="following-btn"
        @click.stop="handleUnfollow"
        :loading="loading"
      >
        <el-icon><Check /></el-icon>
        <span class="btn-text">已关注</span>
      </el-button>
    </div>
  </article>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { followCreator, unfollowCreator } from '@/api/interaction'
import { ElMessage } from 'element-plus'

const props = defineProps({
  creator: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['update'])

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const formatCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + 'w'
  if (count >= 1000) return (count / 1000).toFixed(1) + 'k'
  return count.toString()
}

const goToDetail = () => {
  router.push(`/creator/${props.creator.id}`)
}

const handleFollow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  loading.value = true
  try {
    await followCreator(props.creator.id)
    ElMessage.success('关注成功')
    emit('update')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleUnfollow = async () => {
  loading.value = true
  try {
    await unfollowCreator(props.creator.id)
    ElMessage.success('已取消关注')
    emit('update')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.creator-card {
  background: $card-background;
  border-radius: $border-radius-xl;
  overflow: hidden;
  cursor: pointer;
  transition: all $transition-base;
  border: 1px solid $border-light;
  position: relative;
  display: flex;
  flex-direction: column;
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: $shadow-xl;
    border-color: transparent;
    
    .card-banner {
      transform: scale(1.05);
    }
    
    .pen-name {
      color: $primary-color;
    }
  }
}

.card-banner {
  height: 80px;
  background: $gradient-primary;
  position: relative;
  overflow: hidden;
  transition: transform $transition-slow;
  flex-shrink: 0;
  
  .banner-pattern {
    position: absolute;
    inset: 0;
    background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  }
}

.card-avatar {
  position: relative;
  display: flex;
  justify-content: center;
  margin-top: -40px;
  flex-shrink: 0;
  
  .el-avatar {
    border: 4px solid $card-background;
    box-shadow: $shadow-lg;
    background: $gradient-warm;
  }
  
  .category-badge {
    position: absolute;
    bottom: -4px;
    left: 50%;
    transform: translateX(-50%);
    padding: 4px 12px;
    background: $gradient-primary;
    border-radius: $border-radius-full;
    font-size: $font-size-xs;
    font-weight: $font-weight-bold;
    color: white;
    white-space: nowrap;
    box-shadow: 0 2px 8px rgba(255, 107, 107, 0.4);
  }
}

.card-body {
  padding: $spacing-lg $spacing-md;
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.pen-name {
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $spacing-xs;
  transition: color $transition-fast;
  letter-spacing: -0.01em;
}

.bio {
  font-size: $font-size-sm;
  color: $text-muted;
  margin-bottom: $spacing-md;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
  line-height: 1.5;
  flex: 1;
}

.stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-md 0;
  
  .stat-item {
    text-align: center;
    flex: 1;
    
    .value {
      display: block;
      font-size: $font-size-lg;
      font-weight: $font-weight-bold;
      color: $text-primary;
      line-height: 1.2;
    }
    
    .label {
      font-size: $font-size-xs;
      color: $text-muted;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
  }
  
  .stat-divider {
    width: 1px;
    height: 32px;
    background: $border-color;
    flex-shrink: 0;
  }
}

.card-footer {
  padding: 0 $spacing-md $spacing-md;
  flex-shrink: 0;
  
  .follow-btn,
  .following-btn {
    width: 100%;
    height: 44px;
    font-weight: $font-weight-semibold;
    
    .el-icon {
      margin-right: 4px;
    }
  }
  
  .follow-btn {
    background: $gradient-primary !important;
    border: none !important;
    box-shadow: 0 4px 14px rgba(255, 107, 107, 0.35);
    
    &:hover {
      box-shadow: 0 6px 20px rgba(255, 107, 107, 0.45);
    }
  }
  
  .following-btn {
    background: $background-color !important;
    border: 2px solid $border-color !important;
    color: $text-secondary !important;
    
    &:hover {
      border-color: $danger-color !important;
      color: $danger-color !important;
      background: rgba($danger-color, 0.05) !important;
      
      .btn-text {
        display: none;
      }
      
      &::after {
        content: '取消关注';
      }
    }
  }
}
</style>
