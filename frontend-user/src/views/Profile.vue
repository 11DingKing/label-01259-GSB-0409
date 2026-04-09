<template>
  <div class="profile-page">
    <div class="container-narrow">
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="userStore.userInfo?.avatar">
              {{ userStore.userInfo?.nickname?.charAt(0) }}
            </el-avatar>
            <div class="avatar-badge" v-if="userStore.isCreator">
              <el-icon><Check /></el-icon>
            </div>
          </div>
          <div class="profile-info">
            <h1>{{ userStore.userInfo?.nickname }}</h1>
            <p class="username">@{{ userStore.userInfo?.username }}</p>
            <div class="role-tags">
              <el-tag v-if="userStore.isCreator" type="success" effect="dark">创作者</el-tag>
              <el-tag v-else-if="userStore.isAdmin" type="danger" effect="dark">管理员</el-tag>
              <el-tag v-else effect="plain">普通用户</el-tag>
            </div>
          </div>
        </div>
        
        <div class="balance-card">
          <div class="balance-info">
            <span class="label">账户余额</span>
            <span class="value">¥{{ userStore.userInfo?.balance?.toFixed(2) || '0.00' }}</span>
          </div>
          <el-button type="primary" @click="showRechargeDialog = true">
            <el-icon><Plus /></el-icon>充值
          </el-button>
        </div>
      </div>
      
      <div class="settings-card">
        <h2 class="section-title">个人资料</h2>
        <el-form :model="form" label-position="top" class="profile-form">
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" placeholder="请输入昵称" size="large" />
          </el-form-item>
          <el-form-item label="头像URL">
            <el-input v-model="form.avatar" placeholder="请输入头像图片URL" size="large" />
            <div class="form-tip">支持 jpg、png 格式的图片链接</div>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" size="large" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleSave" :loading="saving" class="save-btn">
              保存修改
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="quick-links">
        <router-link to="/my-follows" class="quick-link">
          <el-icon><Star /></el-icon>
          <span>我的关注</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </router-link>
        <router-link v-if="userStore.isCreator" to="/creator-center" class="quick-link">
          <el-icon><Edit /></el-icon>
          <span>创作中心</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </router-link>
        <router-link v-if="userStore.isCreator" to="/dashboard" class="quick-link">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据看板</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </router-link>
        <router-link v-if="!userStore.isCreator" to="/apply-creator" class="quick-link">
          <el-icon><Promotion /></el-icon>
          <span>申请成为创作者</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </router-link>
      </div>
    </div>
    
    <!-- 充值弹窗 -->
    <el-dialog v-model="showRechargeDialog" title="账户充值" width="420px">
      <div class="recharge-content">
        <p class="recharge-tip">选择充值金额</p>
        <div class="amount-options">
          <button 
            v-for="amount in [10, 50, 100, 200, 500]" 
            :key="amount"
            :class="['amount-btn', { active: rechargeAmount === amount }]"
            @click="rechargeAmount = amount"
          >
            ¥{{ amount }}
          </button>
        </div>
        <el-input v-model.number="rechargeAmount" type="number" placeholder="自定义金额" size="large">
          <template #prepend>¥</template>
        </el-input>
      </div>
      <template #footer>
        <el-button @click="showRechargeDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge" :loading="recharging">确认充值</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile, recharge } from '@/api/auth'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const form = reactive({ nickname: '', avatar: '', email: '' })
const saving = ref(false)
const showRechargeDialog = ref(false)
const rechargeAmount = ref(100)
const recharging = ref(false)

const handleSave = async () => {
  saving.value = true
  try {
    await updateProfile(form)
    ElMessage.success('保存成功')
    userStore.fetchUserInfo()
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

const handleRecharge = async () => {
  if (!rechargeAmount.value || rechargeAmount.value < 1) {
    ElMessage.warning('请输入有效的充值金额')
    return
  }
  recharging.value = true
  try {
    await recharge({ amount: rechargeAmount.value })
    ElMessage.success('充值成功')
    showRechargeDialog.value = false
    userStore.fetchUserInfo()
  } catch (error) {
    console.error(error)
  } finally {
    recharging.value = false
  }
}

onMounted(() => {
  if (userStore.userInfo) {
    form.nickname = userStore.userInfo.nickname || ''
    form.avatar = userStore.userInfo.avatar || ''
    form.email = userStore.userInfo.email || ''
  }
})
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: calc(100vh - 72px);
  padding: $spacing-2xl 0;
  background: $background-color;
}

.container-narrow {
  max-width: 640px;
  margin: 0 auto;
  padding: 0 $spacing-lg;
}

.profile-card {
  background: $card-background;
  border-radius: $border-radius-2xl;
  box-shadow: $shadow-lg;
  padding: $spacing-xl;
  margin-bottom: $spacing-lg;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: $spacing-xl;
  margin-bottom: $spacing-xl;
  
  .avatar-wrapper {
    position: relative;
    
    .el-avatar {
      border: 4px solid $card-background;
      box-shadow: $shadow-lg;
    }
    
    .avatar-badge {
      position: absolute;
      bottom: 4px;
      right: 4px;
      width: 28px;
      height: 28px;
      background: $success-color;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 3px solid $card-background;
      
      .el-icon { font-size: 14px; color: white; }
    }
  }
  
  .profile-info {
    h1 {
      font-size: $font-size-2xl;
      font-weight: $font-weight-bold;
      color: $text-primary;
      margin-bottom: $spacing-xs;
    }
    
    .username {
      color: $text-muted;
      margin-bottom: $spacing-sm;
    }
  }
}

.balance-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-lg;
  background: $gradient-warm;
  border-radius: $border-radius-xl;
  
  .balance-info {
    .label {
      display: block;
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: $spacing-xs;
    }
    
    .value {
      font-size: $font-size-3xl;
      font-weight: $font-weight-bold;
      color: $primary-color;
    }
  }
}

.settings-card {
  background: $card-background;
  border-radius: $border-radius-2xl;
  box-shadow: $shadow-md;
  padding: $spacing-xl;
  margin-bottom: $spacing-lg;
  
  .section-title {
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
    margin-bottom: $spacing-lg;
  }
}

.profile-form {
  :deep(.el-form-item__label) {
    font-weight: $font-weight-medium;
    color: $text-primary;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: $border-radius-lg !important;
  }
  
  .form-tip {
    font-size: $font-size-xs;
    color: $text-muted;
    margin-top: $spacing-xs;
  }
  
  .save-btn {
    width: 100%;
    height: 48px !important;
    font-weight: $font-weight-semibold;
    margin-top: $spacing-sm;
  }
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
  
  .quick-link {
    display: flex;
    align-items: center;
    gap: $spacing-md;
    padding: $spacing-md $spacing-lg;
    background: $card-background;
    border-radius: $border-radius-lg;
    box-shadow: $shadow-sm;
    color: $text-primary;
    font-weight: $font-weight-medium;
    transition: all $transition-fast;
    
    .el-icon:first-child {
      font-size: 20px;
      color: $primary-color;
    }
    
    span { flex: 1; }
    
    .arrow {
      color: $text-muted;
      transition: transform $transition-fast;
    }
    
    &:hover {
      background: $gradient-warm;
      transform: translateX(4px);
      
      .arrow { transform: translateX(4px); }
    }
  }
}

.recharge-content {
  .recharge-tip {
    color: $text-secondary;
    margin-bottom: $spacing-md;
    text-align: center;
  }
  
  .amount-options {
    display: flex;
    gap: $spacing-sm;
    margin-bottom: $spacing-lg;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .amount-btn {
    padding: $spacing-sm $spacing-lg;
    border: 2px solid $border-color;
    background: $card-background;
    border-radius: $border-radius-full;
    font-weight: $font-weight-semibold;
    cursor: pointer;
    transition: all $transition-fast;
    
    &:hover {
      border-color: $primary-color;
      color: $primary-color;
    }
    
    &.active {
      background: $gradient-primary;
      border-color: transparent;
      color: white;
    }
  }
  
  :deep(.el-input) {
    .el-input__wrapper {
      border-radius: 0 $border-radius-lg $border-radius-lg 0 !important;
      box-shadow: none !important;
      border: 2px solid $border-color;
      border-left: none;
      padding: 8px 12px;
      
      &:hover, &:focus-within {
        border-color: $primary-color;
      }
    }
    
    .el-input-group__prepend {
      background: $background-color;
      border: 2px solid $border-color;
      border-right: none;
      box-shadow: none;
      border-radius: $border-radius-lg 0 0 $border-radius-lg !important;
      padding: 0 16px;
      font-weight: $font-weight-bold;
      font-size: $font-size-lg;
      color: $text-muted;
    }
    
    .el-input__inner {
      font-size: $font-size-xl;
      font-weight: $font-weight-bold;
      color: $primary-color;
      
      &::placeholder {
        color: $text-muted;
        font-weight: $font-weight-medium;
        font-size: $font-size-base;
      }
    }
  }
}
</style>
