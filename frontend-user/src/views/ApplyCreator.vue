<template>
  <div class="apply-creator-page">
    <div class="container-narrow">
      <div class="apply-card" v-loading="pageLoading">
        <!-- 已有创作者状态时只显示状态卡片 -->
        <template v-if="!pageLoading && existingCreator">
          <div class="apply-header">
            <div class="header-icon" :class="{ 'success-icon': existingCreator.status === 1, 'pending-icon': existingCreator.status === 0, 'rejected-icon': existingCreator.status === 2 }">
              <svg v-if="existingCreator.status === 1" viewBox="0 0 64 64" fill="none">
                <circle cx="32" cy="32" r="32" fill="url(#success-gradient)"/>
                <path d="M20 32L28 40L44 24" stroke="white" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
                <defs>
                  <linearGradient id="success-gradient" x1="0" y1="0" x2="64" y2="64">
                    <stop stop-color="#2ECC71"/><stop offset="1" stop-color="#27AE60"/>
                  </linearGradient>
                </defs>
              </svg>
              <svg v-else-if="existingCreator.status === 0" viewBox="0 0 64 64" fill="none">
                <circle cx="32" cy="32" r="32" fill="url(#pending-gradient)"/>
                <circle cx="32" cy="32" r="16" stroke="white" stroke-width="4" fill="none"/>
                <path d="M32 24V32L38 38" stroke="white" stroke-width="4" stroke-linecap="round"/>
                <defs>
                  <linearGradient id="pending-gradient" x1="0" y1="0" x2="64" y2="64">
                    <stop stop-color="#F1C40F"/><stop offset="1" stop-color="#F39C12"/>
                  </linearGradient>
                </defs>
              </svg>
              <svg v-else viewBox="0 0 64 64" fill="none">
                <circle cx="32" cy="32" r="32" fill="url(#rejected-gradient)"/>
                <path d="M24 24L40 40M40 24L24 40" stroke="white" stroke-width="4" stroke-linecap="round"/>
                <defs>
                  <linearGradient id="rejected-gradient" x1="0" y1="0" x2="64" y2="64">
                    <stop stop-color="#E74C3C"/><stop offset="1" stop-color="#C0392B"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <h1 v-if="existingCreator.status === 1">您已是创作者</h1>
            <h1 v-else-if="existingCreator.status === 0">申请审核中</h1>
            <h1 v-else>申请未通过</h1>
            <p v-if="existingCreator.status === 1">恭喜！您已成功成为创作者，快去创作中心开始创作吧</p>
            <p v-else-if="existingCreator.status === 0">您的创作者申请正在审核中，请耐心等待</p>
            <p v-else>很抱歉，您的申请未通过审核，可以重新申请</p>
          </div>
          
          <div class="status-actions">
            <div v-if="existingCreator.status === 0" class="status-info">
              <span>笔名：{{ existingCreator.penName }}</span>
              <span>领域：{{ existingCreator.category }}</span>
            </div>
            <router-link v-if="existingCreator.status === 1" to="/creator-center">
              <el-button type="primary" size="large" class="action-btn">进入创作中心</el-button>
            </router-link>
            <el-button v-if="existingCreator.status === 2" type="primary" size="large" @click="existingCreator = null" class="action-btn">重新申请</el-button>
          </div>
        </template>
        
        <!-- 未申请时显示申请表单 -->
        <template v-else-if="!pageLoading">
          <div class="apply-header">
            <div class="header-icon">
              <svg viewBox="0 0 64 64" fill="none">
                <circle cx="32" cy="32" r="32" fill="url(#apply-gradient)"/>
                <path d="M32 20V44M20 32H44" stroke="white" stroke-width="4" stroke-linecap="round"/>
                <defs>
                  <linearGradient id="apply-gradient" x1="0" y1="0" x2="64" y2="64">
                    <stop stop-color="#FF6B6B"/>
                    <stop offset="1" stop-color="#FF8E53"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <h1>申请成为创作者</h1>
            <p>加入我们，开启你的创作之旅，与粉丝建立深度连接</p>
          </div>
        
          <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="apply-form">
          <el-form-item label="笔名" prop="penName">
            <el-input v-model="form.penName" placeholder="请输入您的笔名，这将是您的创作者名称" size="large" />
          </el-form-item>
          
          <el-form-item label="创作领域" prop="category">
            <el-select v-model="form.category" placeholder="请选择您的创作领域" size="large" style="width: 100%">
              <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="个人简介" prop="bio">
            <el-input v-model="form.bio" type="textarea" :rows="4" placeholder="介绍一下自己和创作方向，让粉丝更了解你" />
          </el-form-item>
          
          <el-form-item label="封面图片URL（选填）">
            <el-input v-model="form.coverImage" placeholder="请输入封面图片URL" size="large" />
            <div class="form-tip">建议尺寸：1200x400，支持 jpg、png 格式</div>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" size="large" @click="handleApply" :loading="loading" class="submit-btn">
              提交申请
            </el-button>
          </el-form-item>
        </el-form>
        
          <div class="apply-tips">
            <h3>申请须知</h3>
            <ul>
              <li>申请提交后，我们将在1-3个工作日内完成审核</li>
              <li>请确保填写的信息真实有效</li>
              <li>成为创作者后，您可以发布原创内容并获得粉丝打赏</li>
            </ul>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { applyCreator, getMyCreatorProfile, getCategories } from '@/api/creator'
import { ElMessage } from 'element-plus'

const formRef = ref()
const loading = ref(false)
const pageLoading = ref(true)
const categories = ref([])
const existingCreator = ref(null)

const form = reactive({
  penName: '',
  category: '',
  bio: '',
  coverImage: '',
})

const rules = {
  penName: [{ required: true, message: '请输入笔名', trigger: 'blur' }],
  category: [{ required: true, message: '请选择创作领域', trigger: 'change' }],
}

const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const checkExistingCreator = async () => {
  try {
    console.log('Checking existing creator...')
    const res = await getMyCreatorProfile()
    console.log('getMyCreatorProfile response:', res)
    // 确保 res.data 存在且有 id
    if (res && res.data && res.data.id) {
      console.log('Found existing creator:', res.data)
      existingCreator.value = res.data
    } else {
      console.log('No creator data found')
      existingCreator.value = null
    }
  } catch (error) {
    // 忽略错误，用户可能还不是创作者
    console.error('checkExistingCreator error:', error)
    existingCreator.value = null
  } finally {
    pageLoading.value = false
  }
}

const handleApply = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await applyCreator(form)
    ElMessage.success('申请已提交，请等待审核')
    // 重新加载创作者状态
    await checkExistingCreator()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCategories()
  checkExistingCreator()
})
</script>

<style lang="scss" scoped>
.apply-creator-page {
  min-height: calc(100vh - 72px);
  padding: $spacing-2xl 0;
  background: $background-color;
}

.container-narrow {
  max-width: 640px;
  margin: 0 auto;
  padding: 0 $spacing-lg;
}

.apply-card {
  background: $card-background;
  border-radius: $border-radius-2xl;
  box-shadow: $shadow-lg;
  padding: $spacing-2xl;
}

.apply-header {
  text-align: center;
  margin-bottom: $spacing-xl;
  
  .header-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto $spacing-lg;
    svg { width: 100%; height: 100%; }
  }
  
  h1 {
    font-size: $font-size-2xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
    margin-bottom: $spacing-sm;
  }
  
  p {
    color: $text-muted;
    max-width: 400px;
    margin: 0 auto;
  }
}

.status-actions {
  text-align: center;
  margin-top: $spacing-xl;
  
  .status-info {
    display: flex;
    justify-content: center;
    gap: $spacing-xl;
    padding: $spacing-md;
    background: $background-color;
    border-radius: $border-radius-lg;
    
    span {
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
  
  .action-btn {
    min-width: 180px;
    height: 48px !important;
    font-size: $font-size-base;
    font-weight: $font-weight-semibold;
  }
}

.apply-form {
  :deep(.el-form-item__label) {
    font-weight: $font-weight-medium;
    color: $text-primary;
  }
  
  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    border-radius: $border-radius-lg !important;
  }
  
  .form-tip {
    font-size: $font-size-xs;
    color: $text-muted;
    margin-top: $spacing-xs;
  }
}

.submit-btn {
  width: 100%;
  height: 48px !important;
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  margin-top: $spacing-md;
}

.apply-tips {
  margin-top: $spacing-xl;
  padding: $spacing-lg;
  background: $background-color;
  border-radius: $border-radius-lg;
  
  h3 {
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    margin-bottom: $spacing-sm;
  }
  
  ul {
    margin: 0;
    padding-left: $spacing-lg;
    
    li {
      font-size: $font-size-sm;
      color: $text-muted;
      line-height: 1.8;
    }
  }
}
</style>
