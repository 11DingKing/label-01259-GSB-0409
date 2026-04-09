<template>
  <div class="content-detail-page" v-loading="loading">
    <div class="container-narrow">
      <article class="article-card">
        <header class="article-header">
          <div class="header-meta">
            <el-tag class="content-type" size="small">{{ contentTypeText }}</el-tag>
            <span class="publish-time">{{ formatTime(content.createdAt) }}</span>
          </div>
          <h1 class="title">{{ content.title }}</h1>
          <div class="creator-row">
            <div class="creator-info" @click="goToCreator">
              <el-avatar :size="48" :src="content.creatorAvatar">{{ content.creatorName?.charAt(0) }}</el-avatar>
              <div class="creator-text">
                <span class="name">{{ content.creatorName }}</span>
                <span class="category">{{ content.creatorCategory || '创作者' }}</span>
              </div>
            </div>
            <div class="stats">
              <span class="stat-item"><el-icon><View /></el-icon>{{ content.viewCount }}</span>
              <span class="stat-item"><el-icon><Star /></el-icon>{{ content.likeCount }}</span>
              <span class="stat-item"><el-icon><ChatDotRound /></el-icon>{{ commentTotal }}</span>
            </div>
          </div>
        </header>
        
        <div v-if="content.coverImage" class="cover-image">
          <img :src="getFullUrl(content.coverImage)" :alt="content.title" />
        </div>
        
        <!-- 视频内容 -->
        <div v-if="content.contentType === 2 && content.mediaUrl" class="media-section">
          <video :src="getFullUrl(content.mediaUrl)" controls class="video-player"></video>
        </div>
        
        <!-- 音频内容 -->
        <div v-if="content.contentType === 3 && content.mediaUrl" class="media-section">
          <div class="audio-player-wrapper">
            <div class="audio-cover">
              <el-icon class="audio-icon"><Headset /></el-icon>
            </div>
            <audio :src="getFullUrl(content.mediaUrl)" controls class="audio-player"></audio>
          </div>
        </div>
        
        <!-- 图集内容 -->
        <div v-if="content.contentType === 4 && content.mediaUrl" class="media-section gallery-section">
          <el-image
            v-for="(img, index) in galleryImages"
            :key="index"
            :src="getFullUrl(img)"
            :preview-src-list="galleryImages.map(i => getFullUrl(i))"
            :initial-index="index"
            fit="cover"
            class="gallery-image"
          />
        </div>
        
        <div class="article-content">
          <div v-if="content.isPaid && !content.isPurchased && !isOwner" class="paid-notice">
            <div class="paid-icon"><el-icon><Lock /></el-icon></div>
            <h3>付费内容</h3>
            <p class="price">¥{{ content.price }}</p>
            <p class="desc">购买后即可查看完整内容</p>
            <el-button type="primary" size="large" :loading="purchaseLoading" @click="showPurchaseDialog = true">立即购买</el-button>
          </div>
          <div v-else class="content-body" v-html="formattedContent"></div>
        </div>
        
        <footer class="article-footer">
          <div class="action-buttons">
            <el-button :type="content.isLiked ? 'primary' : 'default'" size="large" @click="handleLike" :loading="likeLoading" class="action-btn">
              <el-icon><Star /></el-icon>{{ content.isLiked ? '已点赞' : '点赞' }}
            </el-button>
            <el-button size="large" @click="showRewardDialog = true" class="action-btn reward-btn">
              <el-icon><Present /></el-icon>打赏
            </el-button>
          </div>
        </footer>
      </article>
      
      <section class="comments-section">
        <div class="section-header">
          <h2>评论 <span class="count">({{ commentTotal }})</span></h2>
        </div>
        
        <div class="comment-input" v-if="userStore.isLoggedIn">
          <el-avatar :size="40" :src="userStore.userInfo?.avatar">{{ userStore.userInfo?.nickname?.charAt(0) }}</el-avatar>
          <div class="input-wrapper">
            <el-input v-model="commentText" type="textarea" placeholder="写下你的评论..." :rows="3" resize="none" />
            <el-button type="primary" @click="handleComment" :loading="commentLoading" :disabled="!commentText.trim()">发表评论</el-button>
          </div>
        </div>
        <div v-else class="login-tip"><router-link to="/login">登录</router-link>后参与评论</div>
        
        <div class="comments-list" v-loading="commentsLoading">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <el-avatar :size="40" :src="comment.avatar">{{ comment.username?.charAt(0) }}</el-avatar>
            <div class="comment-content">
              <div class="comment-header">
                <span class="username">{{ comment.username }}</span>
                <span class="time">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <p class="comment-text">{{ comment.commentText }}</p>
              <div class="comment-actions">
                <el-button text size="small" @click="replyTo = comment">回复</el-button>
              </div>
              <div v-if="comment.replies?.length" class="replies">
                <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                  <el-avatar :size="28" :src="reply.avatar">{{ reply.username?.charAt(0) }}</el-avatar>
                  <div class="reply-content">
                    <span class="username">{{ reply.username }}</span>
                    <span class="reply-text">{{ reply.commentText }}</span>
                  </div>
                </div>
              </div>
              <div v-if="replyTo?.id === comment.id" class="reply-input">
                <el-input v-model="replyText" placeholder="回复评论..." size="small" />
                <el-button size="small" type="primary" @click="handleReply(comment.id)">回复</el-button>
                <el-button size="small" @click="replyTo = null">取消</el-button>
              </div>
            </div>
          </div>
          <el-empty v-if="!commentsLoading && comments.length === 0" description="暂无评论，快来抢沙发吧" />
        </div>
      </section>
    </div>
    
    <el-dialog v-model="showPurchaseDialog" title="购买确认" width="400px" class="purchase-dialog" :show-close="true">
      <div class="purchase-content">
        <div class="purchase-info">
          <el-icon class="purchase-icon"><ShoppingCart /></el-icon>
          <h3>{{ content.title }}</h3>
          <p class="purchase-price">¥{{ content.price }}</p>
          <p class="purchase-desc">确定花费 ¥{{ content.price }} 购买此内容吗？</p>
          <p class="balance-info">当前余额：¥{{ userStore.userInfo?.balance?.toFixed(2) || '0.00' }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="showPurchaseDialog = false">取消</el-button>
          <el-button type="primary" size="large" @click="handlePurchase" :loading="purchaseLoading">确认购买</el-button>
        </div>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showRewardDialog" title="" width="480px" class="reward-dialog" :show-close="true">
      <div class="reward-content">
        <div class="reward-header">
          <el-avatar :size="72" :src="content.creatorAvatar">{{ content.creatorName?.charAt(0) }}</el-avatar>
          <h3 class="creator-name">打赏 {{ content.creatorName }}</h3>
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
import { getContentDetail, likeContent, purchaseContent } from '@/api/content'
import { getComments, addComment, reward } from '@/api/interaction'
import { ElMessage } from 'element-plus'
import { Headset } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const content = ref({})
const loading = ref(false)
const likeLoading = ref(false)
const purchaseLoading = ref(false)
const comments = ref([])
const commentsLoading = ref(false)
const commentTotal = ref(0)
const commentText = ref('')
const commentLoading = ref(false)
const replyTo = ref(null)
const replyText = ref('')
const showRewardDialog = ref(false)
const showPurchaseDialog = ref(false)
const presetAmounts = [5, 10, 20, 50, 100, 200]
const rewardAmount = ref(10)
const customAmount = ref('')
const isCustom = ref(false)
const rewardMessage = ref('')
const rewardLoading = ref(false)

const contentTypeMap = { 1: '文章', 2: '视频', 3: '音频', 4: '图集' }
const contentTypeText = computed(() => contentTypeMap[content.value.contentType] || '文章')
const isOwner = computed(() => userStore.userInfo?.creatorId === content.value.creatorId)
const formattedContent = computed(() => content.value.content?.replace(/\n/g, '<br>') || '')
const displayAmount = computed(() => isCustom.value && customAmount.value ? customAmount.value : rewardAmount.value)
const galleryImages = computed(() => content.value.mediaUrl ? content.value.mediaUrl.split('\n').filter(url => url.trim()) : [])

const getFullUrl = (url) => {
  if (!url) return ''
  return url.startsWith('http') ? url : `http://localhost:8080${url}`
}

const selectAmount = (amount) => { rewardAmount.value = amount; customAmount.value = ''; isCustom.value = false }
const handleCustomInput = () => { isCustom.value = true }
const formatTime = (time) => time ? new Date(time).toLocaleString('zh-CN') : ''

const loadContent = async () => { loading.value = true; try { const res = await getContentDetail(route.params.id); content.value = res.data } catch (error) { console.error(error) } finally { loading.value = false } }
const loadComments = async () => { commentsLoading.value = true; try { const res = await getComments(route.params.id, { page: 1, size: 50 }); comments.value = res.data.records; commentTotal.value = res.data.total } catch (error) { console.error(error) } finally { commentsLoading.value = false } }
const goToCreator = () => router.push(`/creator/${content.value.creatorId}`)
const handleLike = async () => { if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return } likeLoading.value = true; try { await likeContent(content.value.id); content.value.isLiked = !content.value.isLiked; content.value.likeCount += content.value.isLiked ? 1 : -1 } catch (error) { console.error(error) } finally { likeLoading.value = false } }

const handlePurchase = async () => { 
  if (!userStore.isLoggedIn) { 
    ElMessage.warning('请先登录'); 
    router.push('/login'); 
    return 
  } 
  
  purchaseLoading.value = true
  try { 
    await purchaseContent(content.value.id)
    ElMessage.success('购买成功')
    showPurchaseDialog.value = false
    await loadContent()
    await userStore.fetchUserInfo() 
  } catch (error) { 
    console.error(error) 
  } finally { 
    purchaseLoading.value = false 
  } 
}
const handleComment = async () => { if (!commentText.value.trim()) { ElMessage.warning('请输入评论内容'); return } commentLoading.value = true; try { await addComment({ contentId: content.value.id, commentText: commentText.value }); ElMessage.success('评论成功'); commentText.value = ''; loadComments() } catch (error) { console.error(error) } finally { commentLoading.value = false } }
const handleReply = async (parentId) => { if (!replyText.value.trim()) { ElMessage.warning('请输入回复内容'); return } try { await addComment({ contentId: content.value.id, parentId, commentText: replyText.value }); ElMessage.success('回复成功'); replyText.value = ''; replyTo.value = null; loadComments() } catch (error) { console.error(error) } }
const handleReward = async () => { if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return } const amount = displayAmount.value; if (!amount || amount < 0.01) { ElMessage.warning('请输入有效的打赏金额'); return } rewardLoading.value = true; try { await reward({ creatorId: content.value.creatorId, contentId: content.value.id, amount: amount, message: rewardMessage.value }); ElMessage.success('打赏成功！'); showRewardDialog.value = false; rewardMessage.value = ''; customAmount.value = ''; isCustom.value = false; rewardAmount.value = 10; userStore.fetchUserInfo() } catch (error) { console.error(error) } finally { rewardLoading.value = false } }

onMounted(() => { loadContent(); loadComments() })
</script>


<style lang="scss" scoped>
.content-detail-page { padding: $spacing-xl 0 $spacing-3xl; }
.container-narrow { max-width: 800px; margin: 0 auto; padding: 0 $spacing-lg; }
.article-card { background: $card-background; border-radius: $border-radius-xl; box-shadow: $shadow-lg; overflow: hidden; }
.article-header { padding: $spacing-xl;
  .header-meta { display: flex; align-items: center; gap: $spacing-md; margin-bottom: $spacing-md; }
  .content-type { background: rgba($primary-color, 0.1); color: $primary-color; border: none; }
  .publish-time { font-size: $font-size-sm; color: $text-muted; }
  .title { font-size: $font-size-3xl; font-weight: $font-weight-bold; color: $text-primary; margin-bottom: $spacing-lg; line-height: 1.3; }
}
.creator-row { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: $spacing-md; padding-top: $spacing-md; border-top: 1px solid $border-light; }
.creator-info { display: flex; align-items: center; gap: $spacing-md; cursor: pointer;
  .el-avatar { border: 2px solid $card-background; box-shadow: $shadow-sm; }
  .creator-text { display: flex; flex-direction: column; }
  .name { font-weight: $font-weight-semibold; color: $text-primary; &:hover { color: $primary-color; } }
  .category { font-size: $font-size-sm; color: $text-muted; }
}
.stats { display: flex; gap: $spacing-lg; .stat-item { display: inline-flex; align-items: center; gap: 4px; color: $text-muted; font-size: $font-size-sm; line-height: 1; .el-icon { display: inline-flex; align-items: center; } } }
.cover-image { img { width: 100%; max-height: 500px; object-fit: cover; } }

.media-section { padding: 0 $spacing-xl $spacing-xl; }
.video-player { width: 100%; max-height: 500px; border-radius: $border-radius-lg; background: #000; }
.audio-player-wrapper { 
  display: flex; flex-direction: column; align-items: center; gap: $spacing-lg; 
  padding: $spacing-xl; background: $gradient-warm; border-radius: $border-radius-xl;
  .audio-cover { 
    width: 120px; height: 120px; border-radius: 50%; 
    background: $gradient-primary; display: flex; align-items: center; justify-content: center;
    .audio-icon { font-size: 48px; color: white; }
  }
  .audio-player { width: 100%; max-width: 500px; }
}
.gallery-section { 
  display: grid; grid-template-columns: repeat(3, 1fr); gap: $spacing-md;
  @media (max-width: 640px) { grid-template-columns: repeat(2, 1fr); }
  .gallery-image { width: 100%; aspect-ratio: 1; border-radius: $border-radius-lg; cursor: pointer; }
}

.article-content { padding: $spacing-xl; .content-body { font-size: $font-size-lg; line-height: 1.9; color: $text-primary; } }
.paid-notice { text-align: center; padding: $spacing-3xl; background: $gradient-warm; border-radius: $border-radius-xl;
  .paid-icon { width: 80px; height: 80px; margin: 0 auto $spacing-md; background: $gradient-primary; border-radius: 50%; display: flex; align-items: center; justify-content: center; .el-icon { font-size: 36px; color: white; } }
  h3 { font-size: $font-size-xl; font-weight: $font-weight-bold; color: $text-primary; margin-bottom: $spacing-sm; }
  .price { font-size: $font-size-3xl; font-weight: $font-weight-bold; color: $primary-color; margin-bottom: $spacing-xs; }
  .desc { color: $text-muted; margin-bottom: $spacing-lg; }
}
.article-footer { padding: $spacing-lg $spacing-xl; border-top: 1px solid $border-light;
  .action-buttons { display: flex; gap: $spacing-md; }
  .action-btn { padding: 12px 24px !important; font-weight: $font-weight-semibold; border-radius: $border-radius-lg !important; }
  .reward-btn { background: $gradient-accent !important; border: none !important; color: white !important; }
}
.comments-section { margin-top: $spacing-xl; background: $card-background; border-radius: $border-radius-xl; box-shadow: $shadow-md; padding: $spacing-xl;
  .section-header { margin-bottom: $spacing-lg; h2 { font-size: $font-size-xl; font-weight: $font-weight-bold; color: $text-primary; .count { color: $text-muted; font-weight: $font-weight-normal; } } }
}
.comment-input { display: flex; gap: $spacing-md; margin-bottom: $spacing-xl; .input-wrapper { flex: 1; display: flex; flex-direction: column; gap: $spacing-sm; align-items: flex-end; } }
.login-tip { text-align: center; padding: $spacing-lg; background: $background-color; border-radius: $border-radius-lg; margin-bottom: $spacing-xl; color: $text-muted; a { color: $primary-color; font-weight: $font-weight-semibold; } }
.comment-item { display: flex; gap: $spacing-md; padding: $spacing-md 0; border-bottom: 1px solid $border-light; &:last-child { border-bottom: none; }
  .comment-content { flex: 1; }
  .comment-header { margin-bottom: $spacing-xs; .username { font-weight: $font-weight-semibold; color: $text-primary; margin-right: $spacing-sm; } .time { font-size: $font-size-xs; color: $text-muted; } }
  .comment-text { color: $text-primary; line-height: 1.6; }
}
.replies { margin-top: $spacing-md; padding-left: $spacing-md; border-left: 2px solid $border-color;
  .reply-item { display: flex; gap: $spacing-sm; margin-bottom: $spacing-sm; .username { font-weight: $font-weight-semibold; color: $primary-color; margin-right: $spacing-xs; } }
}
.reply-input { display: flex; gap: $spacing-sm; margin-top: $spacing-sm; .el-input { flex: 1; } }

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

.purchase-dialog {
  :deep(.el-dialog) { border-radius: $border-radius-2xl !important; overflow: hidden; }
  :deep(.el-dialog__header) { padding: $spacing-lg $spacing-xl; border-bottom: 1px solid $border-light; }
  :deep(.el-dialog__body) { padding: 0; }
  :deep(.el-dialog__footer) { padding: $spacing-lg $spacing-xl; border-top: 1px solid $border-light; }
}
.purchase-content { padding: $spacing-xl; }
.purchase-info { text-align: center;
  .purchase-icon { font-size: 48px; color: $primary-color; margin-bottom: $spacing-md; }
  h3 { font-size: $font-size-lg; font-weight: $font-weight-bold; color: $text-primary; margin-bottom: $spacing-sm; }
  .purchase-price { font-size: $font-size-2xl; font-weight: $font-weight-bold; color: $primary-color; margin-bottom: $spacing-md; }
  .purchase-desc { font-size: $font-size-base; color: $text-secondary; margin-bottom: $spacing-sm; }
  .balance-info { font-size: $font-size-sm; color: $text-muted; }
}
</style>
