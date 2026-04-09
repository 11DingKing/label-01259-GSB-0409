<template>
  <div class="creator-detail-page" v-loading="loading">
    <div class="creator-header">
      <div class="header-bg">
        <div class="bg-pattern"></div>
      </div>
      <div class="container">
        <div class="creator-profile">
          <div class="profile-main">
            <div class="avatar-wrapper">
              <el-avatar :size="120" :src="creator.avatar">{{ creator.penName?.charAt(0) }}</el-avatar>
            </div>
            <div class="profile-info">
              <div class="name-row">
                <h1 class="pen-name">{{ creator.penName }}</h1>
                <el-tag class="category-tag">{{ creator.category }}</el-tag>
              </div>
              <p class="bio">{{ creator.bio || '这位创作者很神秘，什么都没写~' }}</p>
              <div class="stats-row">
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
                  <span class="value">{{ formatCount(creator.totalLikes || 0) }}</span>
                  <span class="label">获赞</span>
                </div>
              </div>
            </div>
          </div>
          <div class="profile-actions">
            <el-button v-if="!creator.isFollowed" type="primary" size="large" @click="handleFollow" :loading="followLoading" class="action-btn follow-btn">
              <el-icon><Plus /></el-icon>关注
            </el-button>
            <el-button v-else size="large" @click="handleUnfollow" :loading="followLoading" class="action-btn following-btn">
              <el-icon><Check /></el-icon>已关注
            </el-button>
            <el-button size="large" @click="showRewardDialog = true" class="action-btn reward-btn">
              <el-icon><Present /></el-icon>打赏
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="creator-content container">
      <div class="content-header">
        <h2 class="section-title">TA的作品</h2>
        <span class="content-count">共 {{ total }} 个作品</span>
      </div>
      <div class="content-grid" v-loading="contentsLoading">
        <ContentCard v-for="content in contents" :key="content.id" :content="content" />
      </div>
      <el-empty v-if="!contentsLoading && contents.length === 0" description="暂无作品" />
      <div class="pagination" v-if="total > pageSize">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadContents" />
      </div>
    </div>
    
    <el-dialog v-model="showRewardDialog" title="" width="480px" class="reward-dialog" :show-close="true">
      <div class="reward-content">
        <div class="reward-header">
          <el-avatar :size="72" :src="creator.avatar">{{ creator.penName?.charAt(0) }}</el-avatar>
          <h3 class="creator-name">打赏 {{ creator.penName }}</h3>
          <p class="reward-desc">您的支持是创作者最大的动力</p>
        </div>
        <div class="reward-amounts">
          <button v-for="amount in presetAmounts" :key="amount" :class="['amount-btn', { active: rewardAmount === amount && !isCustom }]" @click="selectAmount(amount)">
            <span class="amount-value">¥{{ amount }}</span>
          </button>
        </div>
        <div class="custom-amount">
          <label class="amount-label">自定义金额</label>
          <div class="amount-input-wrapper">
            <span class="currency-symbol">¥</span>
            <el-input v-model.number="customAmount" type="number" placeholder="输入金额" size="large" @input="handleCustomInput" @focus="isCustom = true" />
          </div>
        </div>
        <div class="reward-message-section">
          <label class="message-label">留言（选填）</label>
          <el-input v-model="rewardMessage" type="textarea" placeholder="写下你想对创作者说的话..." :rows="3" resize="none" />
        </div>
        <div class="reward-summary">
          <span class="summary-label">打赏金额</span>
          <span class="summary-value">¥{{ displayAmount }}</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="showRewardDialog = false">取消</el-button>
          <el-button type="primary" size="large" @click="handleReward" :loading="rewardLoading" :disabled="!displayAmount || displayAmount <= 0">确认打赏</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCreatorDetail } from '@/api/creator'
import { getCreatorContents } from '@/api/content'
import { followCreator, unfollowCreator, reward } from '@/api/interaction'
import ContentCard from '@/components/ContentCard.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const creator = ref({})
const loading = ref(false)
const followLoading = ref(false)
const contents = ref([])
const contentsLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const showRewardDialog = ref(false)
const presetAmounts = [5, 10, 20, 50, 100, 200]
const rewardAmount = ref(10)
const customAmount = ref('')
const isCustom = ref(false)
const rewardMessage = ref('')
const rewardLoading = ref(false)

const displayAmount = computed(() => isCustom.value && customAmount.value ? customAmount.value : rewardAmount.value)
const selectAmount = (amount) => { rewardAmount.value = amount; customAmount.value = ''; isCustom.value = false }
const handleCustomInput = () => { isCustom.value = true }
const formatCount = (count) => { if (!count) return '0'; if (count >= 10000) return (count / 10000).toFixed(1) + 'w'; if (count >= 1000) return (count / 1000).toFixed(1) + 'k'; return count.toString() }

const loadCreator = async () => { loading.value = true; try { const res = await getCreatorDetail(route.params.id); creator.value = res.data } catch (error) { console.error(error) } finally { loading.value = false } }
const loadContents = async () => { contentsLoading.value = true; try { const res = await getCreatorContents(route.params.id, { page: currentPage.value, size: pageSize.value }); contents.value = res.data.records; total.value = res.data.total } catch (error) { console.error(error) } finally { contentsLoading.value = false } }
const handleFollow = async () => { if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return } followLoading.value = true; try { await followCreator(creator.value.id); ElMessage.success('关注成功'); creator.value.isFollowed = true; creator.value.followerCount++ } catch (error) { console.error(error) } finally { followLoading.value = false } }
const handleUnfollow = async () => { followLoading.value = true; try { await unfollowCreator(creator.value.id); ElMessage.success('已取消关注'); creator.value.isFollowed = false; creator.value.followerCount-- } catch (error) { console.error(error) } finally { followLoading.value = false } }
const handleReward = async () => { if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return } const amount = displayAmount.value; if (!amount || amount < 0.01) { ElMessage.warning('请输入有效的打赏金额'); return } rewardLoading.value = true; try { await reward({ creatorId: creator.value.id, amount: amount, message: rewardMessage.value }); ElMessage.success('打赏成功！'); showRewardDialog.value = false; rewardMessage.value = ''; customAmount.value = ''; isCustom.value = false; rewardAmount.value = 10; userStore.fetchUserInfo() } catch (error) { console.error(error) } finally { rewardLoading.value = false } }

onMounted(() => { loadCreator(); loadContents() })
</script>


<style lang="scss" scoped>
.creator-header {
  position: relative;
  padding: $spacing-3xl 0;
  margin-bottom: $spacing-xl;
  .header-bg { position: absolute; inset: 0; background: $gradient-primary;
    .bg-pattern { position: absolute; inset: 0; background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E"); }
  }
}
.creator-profile { position: relative; background: $card-background; border-radius: $border-radius-2xl; padding: $spacing-xl; box-shadow: $shadow-xl; display: flex; align-items: flex-start; justify-content: space-between; gap: $spacing-xl;
  @media (max-width: 768px) { flex-direction: column; align-items: center; text-align: center; }
}
.profile-main { display: flex; align-items: flex-start; gap: $spacing-xl; flex: 1;
  @media (max-width: 768px) { flex-direction: column; align-items: center; }
  .avatar-wrapper { flex-shrink: 0; .el-avatar { border: 4px solid $card-background; box-shadow: $shadow-lg; background: $gradient-warm; } }
  .profile-info { flex: 1; min-width: 0; }
  .name-row { display: flex; align-items: center; gap: $spacing-md; margin-bottom: $spacing-sm; @media (max-width: 768px) { justify-content: center; } }
  .pen-name { font-size: $font-size-2xl; font-weight: $font-weight-bold; color: $text-primary; margin: 0; }
  .category-tag { background: $gradient-primary; border: none; color: white; font-weight: $font-weight-semibold; }
  .bio { color: $text-secondary; margin-bottom: $spacing-lg; max-width: 500px; line-height: 1.6; }
  .stats-row { display: flex; align-items: center; gap: $spacing-xl; @media (max-width: 768px) { justify-content: center; }
    .stat-item { text-align: center; .value { display: block; font-size: $font-size-xl; font-weight: $font-weight-bold; color: $text-primary; } .label { font-size: $font-size-sm; color: $text-muted; } }
    .stat-divider { width: 1px; height: 40px; background: $border-color; }
  }
}
.profile-actions { display: flex; gap: $spacing-sm; flex-shrink: 0; @media (max-width: 768px) { width: 100%; justify-content: center; }
  .action-btn { min-width: 120px; height: 44px !important; font-weight: $font-weight-semibold; border-radius: $border-radius-lg !important; }
  .follow-btn { background: $gradient-primary !important; border: none !important; box-shadow: 0 4px 14px rgba(255, 107, 107, 0.35); }
  .following-btn { background: $background-color !important; border: 2px solid $border-color !important; color: $text-secondary !important; &:hover { border-color: $danger-color !important; color: $danger-color !important; } }
  .reward-btn { background: $gradient-accent !important; border: none !important; color: white !important; box-shadow: 0 4px 14px rgba(78, 205, 196, 0.35); }
}
.creator-content { padding-bottom: $spacing-3xl;
  .content-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: $spacing-lg;
    .section-title { font-size: $font-size-2xl; font-weight: $font-weight-bold; color: $text-primary; margin: 0; }
    .content-count { font-size: $font-size-sm; color: $text-muted; }
  }
}
.content-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: $spacing-lg; min-height: 200px;
  @media (max-width: 1024px) { grid-template-columns: repeat(2, 1fr); }
  @media (max-width: 640px) { grid-template-columns: 1fr; }
}
.pagination { display: flex; justify-content: center; margin-top: $spacing-2xl; }

.reward-dialog {
  :deep(.el-dialog) { border-radius: $border-radius-2xl !important; overflow: hidden; }
  :deep(.el-dialog__header) { display: none; }
  :deep(.el-dialog__body) { padding: 0; }
  :deep(.el-dialog__footer) { padding: $spacing-lg $spacing-xl $spacing-xl; border-top: 1px solid $border-light; }
}
.reward-content { padding: $spacing-xl; }
.reward-header { text-align: center; margin-bottom: $spacing-xl;
  .el-avatar { margin-bottom: $spacing-md; border: 3px solid $card-background; box-shadow: $shadow-lg; }
  .creator-name { font-size: $font-size-xl; font-weight: $font-weight-bold; color: $text-primary; margin: 0 0 $spacing-xs; }
  .reward-desc { font-size: $font-size-sm; color: $text-muted; margin: 0; }
}
.reward-amounts { display: grid; grid-template-columns: repeat(3, 1fr); gap: $spacing-sm; margin-bottom: $spacing-lg;
  .amount-btn { padding: $spacing-md; border: 2px solid $border-color; background: $card-background; border-radius: $border-radius-lg; cursor: pointer; transition: all $transition-fast;
    &:hover { border-color: $primary-color; background: rgba($primary-color, 0.05); }
    &.active { border-color: $primary-color; background: rgba($primary-color, 0.1); .amount-value { color: $primary-color; } }
    .amount-value { font-size: $font-size-lg; font-weight: $font-weight-bold; color: $text-primary; }
  }
}
.custom-amount { margin-bottom: $spacing-lg;
  .amount-label { display: block; font-size: $font-size-sm; font-weight: $font-weight-medium; color: $text-secondary; margin-bottom: $spacing-sm; }
  .amount-input-wrapper { position: relative;
    .currency-symbol { position: absolute; left: 16px; top: 50%; transform: translateY(-50%); font-size: $font-size-lg; font-weight: $font-weight-bold; color: $text-primary; z-index: 1; }
    :deep(.el-input__wrapper) { padding-left: 40px !important; border-radius: $border-radius-lg !important; }
    :deep(.el-input__inner) { font-size: $font-size-lg; font-weight: $font-weight-semibold; }
  }
}
.reward-message-section { margin-bottom: $spacing-lg;
  .message-label { display: block; font-size: $font-size-sm; font-weight: $font-weight-medium; color: $text-secondary; margin-bottom: $spacing-sm; }
  :deep(.el-textarea__inner) { border-radius: $border-radius-lg !important; font-size: $font-size-base; }
}
.reward-summary { display: flex; justify-content: space-between; align-items: center; padding: $spacing-md; background: $gradient-warm; border-radius: $border-radius-lg;
  .summary-label { font-size: $font-size-base; color: $text-secondary; }
  .summary-value { font-size: $font-size-2xl; font-weight: $font-weight-bold; color: $primary-color; }
}
.dialog-footer { display: flex; gap: $spacing-md; justify-content: flex-end;
  .el-button { min-width: 100px; height: 44px !important; font-weight: $font-weight-semibold; border-radius: $border-radius-lg !important; }
}
</style>
