<template>
  <div class="publish-page">
    <div class="container-narrow">
      <div class="publish-card">
        <div class="publish-header">
          <div class="header-icon">
            <svg viewBox="0 0 64 64" fill="none">
              <circle cx="32" cy="32" r="32" fill="url(#publish-gradient)"/>
              <path d="M20 32H44M32 20V44" stroke="white" stroke-width="4" stroke-linecap="round"/>
              <defs>
                <linearGradient id="publish-gradient" x1="0" y1="0" x2="64" y2="64">
                  <stop stop-color="#4ECDC4"/><stop offset="1" stop-color="#44A08D"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1>{{ isEdit ? '编辑内容' : '发布新内容' }}</h1>
          <p>分享你的创作，与粉丝建立连接</p>
        </div>
        
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" v-loading="loading" class="publish-form">
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" placeholder="给你的内容起个吸引人的标题" maxlength="100" show-word-limit size="large" />
          </el-form-item>
          
          <el-form-item label="内容类型" prop="contentType">
            <div class="content-type-selector">
              <div v-for="type in contentTypes" :key="type.value" :class="['type-card', { active: form.contentType === type.value }]" @click="form.contentType = type.value">
                <div class="type-icon" v-html="type.icon"></div>
                <span class="type-label">{{ type.label }}</span>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item label="封面图片">
            <div class="upload-section">
              <el-upload
                class="cover-uploader"
                :show-file-list="false"
                :before-upload="beforeCoverUpload"
                :http-request="handleCoverUpload"
                accept="image/*"
              >
                <div v-if="form.coverImage" class="cover-preview-box">
                  <img :src="form.coverImage" alt="封面预览" />
                  <div class="cover-actions">
                    <el-button type="primary" size="small" circle><el-icon><Upload /></el-icon></el-button>
                    <el-button type="danger" size="small" circle @click.stop="form.coverImage = ''"><el-icon><Delete /></el-icon></el-button>
                  </div>
                </div>
                <div v-else class="upload-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <span>点击上传封面图片</span>
                  <span class="upload-tip">推荐尺寸 1200x630，支持 jpg/png</span>
                </div>
              </el-upload>
            </div>
          </el-form-item>
          
          <!-- 视频上传 -->
          <el-form-item v-if="form.contentType === 2" label="视频文件">
            <div class="upload-section">
              <el-upload
                v-if="!form.mediaUrl"
                class="media-uploader"
                :show-file-list="false"
                :before-upload="beforeVideoUpload"
                :http-request="handleVideoUpload"
                accept="video/*"
              >
                <div class="upload-placeholder media-placeholder">
                  <el-icon class="upload-icon"><VideoCamera /></el-icon>
                  <span>点击上传视频文件</span>
                  <span class="upload-tip">支持 mp4/webm/mov，最大 500MB</span>
                </div>
              </el-upload>
              <div v-else class="media-preview">
                <video :src="form.mediaUrl" controls class="video-preview"></video>
                <el-button type="danger" size="small" @click="form.mediaUrl = ''" class="remove-media">
                  <el-icon><Delete /></el-icon>移除视频
                </el-button>
              </div>
            </div>
            <el-progress v-if="uploadProgress > 0 && uploadProgress < 100" :percentage="uploadProgress" :stroke-width="8" />
          </el-form-item>
          
          <!-- 音频上传 -->
          <el-form-item v-if="form.contentType === 3" label="音频文件">
            <div class="upload-section">
              <el-upload
                v-if="!form.mediaUrl"
                class="media-uploader"
                :show-file-list="false"
                :before-upload="beforeAudioUpload"
                :http-request="handleAudioUpload"
                accept="audio/*"
              >
                <div class="upload-placeholder media-placeholder">
                  <el-icon class="upload-icon"><Headset /></el-icon>
                  <span>点击上传音频文件</span>
                  <span class="upload-tip">支持 mp3/wav/ogg，最大 100MB</span>
                </div>
              </el-upload>
              <div v-else class="media-preview">
                <audio :src="form.mediaUrl" controls class="audio-preview"></audio>
                <el-button type="danger" size="small" @click="form.mediaUrl = ''" class="remove-media">
                  <el-icon><Delete /></el-icon>移除音频
                </el-button>
              </div>
            </div>
            <el-progress v-if="uploadProgress > 0 && uploadProgress < 100" :percentage="uploadProgress" :stroke-width="8" />
          </el-form-item>
          
          <!-- 图集上传 -->
          <el-form-item v-if="form.contentType === 4" label="图集图片">
            <div class="upload-section">
              <el-upload
                class="gallery-uploader"
                :file-list="galleryFiles"
                :before-upload="beforeGalleryUpload"
                :http-request="handleGalleryUpload"
                :on-remove="handleGalleryRemove"
                :on-preview="handleGalleryPreview"
                list-type="picture-card"
                accept="image/*"
                multiple
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
              <div class="upload-tip">支持多张图片上传，每张最大 10MB</div>
            </div>
            <el-dialog v-model="previewVisible" title="图片预览" width="800px">
              <img :src="previewUrl" style="width: 100%" alt="预览" />
            </el-dialog>
          </el-form-item>
          
          <el-form-item label="内容简介">
            <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="简要描述你的内容，吸引读者点击" maxlength="200" show-word-limit />
          </el-form-item>
          
          <el-form-item :label="contentLabel" prop="content">
            <el-input v-model="form.content" type="textarea" :rows="8" :placeholder="contentPlaceholder" />
          </el-form-item>
          
          <el-form-item label="付费设置">
            <div class="paid-setting">
              <div class="paid-toggle"><el-switch v-model="isPaid" active-text="付费内容" inactive-text="免费内容" :active-color="'#ff6b6b'" /></div>
              <transition name="fade">
                <div v-if="isPaid" class="price-input-wrapper">
                  <span class="price-label">定价</span>
                  <div class="price-input-box">
                    <el-input-number v-model="form.price" :min="0.01" :max="9999" :precision="2" :step="1" controls-position="right" />
                  </div>
                  <span class="price-unit">元</span>
                </div>
              </transition>
            </div>
            <div class="paid-tip" v-if="isPaid"><el-icon><InfoFilled /></el-icon><span>付费内容需要用户购买后才能查看完整内容</span></div>
          </el-form-item>
          
          <el-form-item>
            <div class="form-actions">
              <el-button size="large" @click="handleSave(0)" :loading="saving"><el-icon><Document /></el-icon>保存草稿</el-button>
              <el-button type="primary" size="large" @click="handleSave(1)" :loading="saving"><el-icon><Promotion /></el-icon>{{ isEdit ? '更新发布' : '立即发布' }}</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createContent, updateContent, getContentDetail } from '@/api/content'
import { uploadImage, uploadVideo, uploadAudio } from '@/api/upload'
import { ElMessage } from 'element-plus'
import { Plus, Upload, Delete, InfoFilled, Document, Promotion, VideoCamera, Headset } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const saving = ref(false)
const isPaid = ref(false)
const isEdit = computed(() => !!route.params.id)
const uploadProgress = ref(0)
const galleryFiles = ref([])
const galleryUrls = ref([])
const previewVisible = ref(false)
const previewUrl = ref('')

const contentTypes = [
  { value: 1, label: '文章', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>' },
  { value: 2, label: '视频', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2" ry="2"/></svg>' },
  { value: 3, label: '音频', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18V5l12-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/></svg>' },
  { value: 4, label: '图集', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>' },
]

const form = reactive({ title: '', contentType: 1, coverImage: '', summary: '', content: '', mediaUrl: '', price: 9.9, status: 1 })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }], contentType: [{ required: true, message: '请选择内容类型', trigger: 'change' }], content: [{ required: true, message: '请输入正文内容', trigger: 'blur' }] }

const contentLabel = computed(() => ({ 1: '文章正文', 2: '视频描述', 3: '音频描述', 4: '图集描述' }[form.contentType] || '正文内容'))
const contentPlaceholder = computed(() => ({ 1: '在这里写下你的精彩文章...', 2: '在这里添加视频的详细介绍...', 3: '在这里添加音频的详细介绍...', 4: '在这里添加图集的详细介绍...' }[form.contentType] || '在这里写下你的精彩内容...'))

// 封面上传
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt10M) { ElMessage.error('图片大小不能超过 10MB'); return false }
  return true
}

const handleCoverUpload = async ({ file }) => {
  try {
    const res = await uploadImage(file)
    // 如果返回的是相对路径，添加后端地址前缀
    const url = res.data.startsWith('http') ? res.data : `http://localhost:8080${res.data}`
    form.coverImage = url
    ElMessage.success('封面上传成功')
  } catch (error) {
    console.error(error)
  }
}

// 视频上传
const beforeVideoUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  const isLt500M = file.size / 1024 / 1024 < 500
  if (!isVideo) { ElMessage.error('只能上传视频文件'); return false }
  if (!isLt500M) { ElMessage.error('视频大小不能超过 500MB'); return false }
  return true
}

const handleVideoUpload = async ({ file }) => {
  uploadProgress.value = 10
  try {
    const res = await uploadVideo(file)
    const url = res.data.startsWith('http') ? res.data : `http://localhost:8080${res.data}`
    form.mediaUrl = url
    uploadProgress.value = 100
    ElMessage.success('视频上传成功')
    setTimeout(() => { uploadProgress.value = 0 }, 1000)
  } catch (error) {
    uploadProgress.value = 0
    console.error(error)
  }
}

// 音频上传
const beforeAudioUpload = (file) => {
  const isAudio = file.type.startsWith('audio/')
  const isLt100M = file.size / 1024 / 1024 < 100
  if (!isAudio) { ElMessage.error('只能上传音频文件'); return false }
  if (!isLt100M) { ElMessage.error('音频大小不能超过 100MB'); return false }
  return true
}

const handleAudioUpload = async ({ file }) => {
  uploadProgress.value = 10
  try {
    const res = await uploadAudio(file)
    const url = res.data.startsWith('http') ? res.data : `http://localhost:8080${res.data}`
    form.mediaUrl = url
    uploadProgress.value = 100
    ElMessage.success('音频上传成功')
    setTimeout(() => { uploadProgress.value = 0 }, 1000)
  } catch (error) {
    uploadProgress.value = 0
    console.error(error)
  }
}

// 图集上传
const beforeGalleryUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt10M) { ElMessage.error('图片大小不能超过 10MB'); return false }
  return true
}

const handleGalleryUpload = async ({ file }) => {
  try {
    const res = await uploadImage(file)
    const url = res.data.startsWith('http') ? res.data : `http://localhost:8080${res.data}`
    galleryUrls.value.push(url)
    // 更新 galleryFiles 以便预览
    galleryFiles.value.push({ url, name: file.name })
    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error(error)
  }
}

const handleGalleryRemove = (file) => {
  const index = galleryUrls.value.indexOf(file.url)
  if (index > -1) {
    galleryUrls.value.splice(index, 1)
    galleryFiles.value.splice(index, 1)
  }
}

const handleGalleryPreview = (file) => {
  previewUrl.value = file.url
  previewVisible.value = true
}

const loadContent = async () => {
  if (!route.params.id) return
  loading.value = true
  try {
    const res = await getContentDetail(route.params.id)
    const data = res.data
    form.title = data.title
    form.contentType = data.contentType
    form.coverImage = data.coverImage || ''
    form.summary = data.summary || ''
    form.content = data.content
    form.mediaUrl = data.mediaUrl || ''
    form.price = data.price || 9.9
    form.status = data.status
    isPaid.value = data.isPaid === 1
    // 如果是图集，解析图片URL并同步更新 galleryFiles
    if (data.contentType === 4 && data.mediaUrl) {
      const urls = data.mediaUrl.split('\n').filter(url => url.trim()).map(url => 
        url.startsWith('http') ? url : `http://localhost:8080${url}`
      )
      galleryUrls.value = urls
      // 同步更新 galleryFiles 以便 el-upload 组件显示预览
      galleryFiles.value = urls.map((url, index) => ({
        url,
        name: `图片${index + 1}`
      }))
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSave = async (status) => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  saving.value = true
  try {
    // 处理媒体URL
    let mediaUrl = form.mediaUrl
    if (form.contentType === 4) {
      mediaUrl = galleryUrls.value.join('\n')
    }
    
    const data = {
      ...form,
      mediaUrl,
      isPaid: isPaid.value ? 1 : 0,
      price: isPaid.value ? form.price : 0,
      status
    }
    
    if (isEdit.value) {
      await updateContent(route.params.id, data)
      ElMessage.success('更新成功')
    } else {
      await createContent(data)
      ElMessage.success(status === 1 ? '发布成功' : '已保存为草稿')
    }
    router.push('/creator-center')
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

onMounted(() => { loadContent() })
</script>


<style lang="scss" scoped>
.publish-page { min-height: calc(100vh - 72px); padding: $spacing-2xl 0; background: $background-color; }
.container-narrow { max-width: 800px; margin: 0 auto; padding: 0 $spacing-lg; }
.publish-card { background: $card-background; border-radius: $border-radius-2xl; box-shadow: $shadow-lg; padding: $spacing-2xl; }
.publish-header { text-align: center; margin-bottom: $spacing-xl;
  .header-icon { width: 80px; height: 80px; margin: 0 auto $spacing-lg; svg { width: 100%; height: 100%; } }
  h1 { font-size: $font-size-2xl; font-weight: $font-weight-bold; color: $text-primary; margin-bottom: $spacing-sm; }
  p { color: $text-muted; }
}
.publish-form {
  :deep(.el-form-item__label) { font-weight: $font-weight-medium; color: $text-primary; font-size: $font-size-base; }
  :deep(.el-input__wrapper), :deep(.el-textarea__inner) { border-radius: $border-radius-lg !important; }
  :deep(.el-textarea__inner) { font-size: $font-size-base; line-height: 1.8; }
}
.content-type-selector { display: grid; grid-template-columns: repeat(4, 1fr); gap: $spacing-md;
  @media (max-width: 600px) { grid-template-columns: repeat(2, 1fr); }
  .type-card { display: flex; flex-direction: column; align-items: center; gap: $spacing-sm; padding: $spacing-lg; border: 2px solid $border-color; border-radius: $border-radius-lg; cursor: pointer; transition: all $transition-fast;
    &:hover { border-color: $accent-color; transform: translateY(-2px); }
    &.active { border-color: $accent-color; background: linear-gradient(135deg, rgba(78, 205, 196, 0.1), rgba(68, 160, 141, 0.1));
      .type-icon { color: $accent-color; }
      .type-label { color: $accent-color; font-weight: $font-weight-semibold; }
    }
    .type-icon { width: 32px; height: 32px; color: $text-muted; transition: color $transition-fast; svg { width: 100%; height: 100%; } }
    .type-label { font-size: $font-size-sm; color: $text-secondary; transition: all $transition-fast; }
  }
}
.upload-section { width: 100%; }
.upload-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: $spacing-xl; border: 2px dashed $border-color; border-radius: $border-radius-lg; cursor: pointer; transition: all $transition-fast;
  &:hover { border-color: $primary-color; background: rgba($primary-color, 0.05); }
  .upload-icon { font-size: 48px; color: $text-muted; margin-bottom: $spacing-sm; }
  span { color: $text-secondary; font-size: $font-size-sm; }
  .upload-tip { color: $text-muted; font-size: $font-size-xs; margin-top: $spacing-xs; }
}
.media-placeholder { min-height: 200px; }
.cover-preview-box { position: relative; width: 100%; border-radius: $border-radius-lg; overflow: hidden;
  img { width: 100%; max-height: 300px; object-fit: cover; }
  .cover-actions { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); display: flex; gap: $spacing-sm; opacity: 0; transition: opacity $transition-fast; }
  &:hover .cover-actions { opacity: 1; }
}
.media-preview { display: flex; flex-direction: column; gap: $spacing-md;
  .video-preview { width: 100%; max-height: 400px; border-radius: $border-radius-lg; background: #000; }
  .audio-preview { width: 100%; }
  .remove-media { align-self: flex-start; }
}
.gallery-uploader {
  :deep(.el-upload-list--picture-card .el-upload-list__item) { width: 120px; height: 120px; border-radius: $border-radius-lg; }
  :deep(.el-upload--picture-card) { width: 120px; height: 120px; border-radius: $border-radius-lg; }
}
.upload-tip { font-size: $font-size-xs; color: $text-muted; margin-top: $spacing-sm; }
.paid-setting { display: flex; align-items: center; gap: $spacing-xl; flex-wrap: wrap;
  .paid-toggle { :deep(.el-switch__label) { font-weight: $font-weight-medium; } }
  .price-input-wrapper { display: flex; align-items: center; gap: $spacing-md; padding: $spacing-sm $spacing-lg; background: $background-color; border-radius: $border-radius-lg; border: 1px solid $border-color;
    .price-label { font-size: $font-size-sm; color: $text-secondary; font-weight: $font-weight-medium; }
    .price-input-box {
      :deep(.el-input-number) { width: 140px;
        .el-input__wrapper { background: $card-background; box-shadow: none; border: 1px solid $border-color; border-radius: $border-radius-md; padding: 0 $spacing-sm; }
        .el-input-number__decrease, .el-input-number__increase { background: transparent; border: none; color: $text-secondary; &:hover { color: $primary-color; } }
      }
    }
    .price-unit { font-size: $font-size-sm; color: $text-muted; font-weight: $font-weight-medium; }
  }
}
.paid-tip { display: flex; align-items: center; gap: $spacing-xs; margin-top: $spacing-sm; font-size: $font-size-sm; color: $text-muted; .el-icon { color: $warning-color; } }
.form-actions { display: flex; gap: $spacing-md; justify-content: flex-end; width: 100%; padding-top: $spacing-lg; border-top: 1px solid $border-light;
  .el-button { min-width: 140px; height: 48px !important; font-size: $font-size-base; font-weight: $font-weight-semibold; .el-icon { margin-right: $spacing-xs; } }
}
.fade-enter-active, .fade-leave-active { transition: opacity $transition-fast, transform $transition-fast; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateX(-10px); }
</style>
