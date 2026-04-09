<template>
  <div class="contents-page">
    <div class="page-header">
      <h1 class="page-title">内容管理</h1>
      <el-input v-model="keyword" placeholder="搜索内容" clearable @clear="loadContents" @keyup.enter="loadContents" style="width: 240px">
        <template #append>
          <el-button @click="loadContents"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>
    
    <div class="card table-card">
      <el-table :data="contents" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="creatorName" label="创作者" width="120" show-overflow-tooltip />
        <el-table-column prop="contentType" label="类型" width="80">
          <template #default="{ row }">{{ contentTypeMap[row.contentType] }}</template>
        </el-table-column>
        <el-table-column prop="isPaid" label="付费" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isPaid" type="warning" size="small">¥{{ row.price }}</el-tag>
            <el-tag v-else type="info" size="small">免费</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'info' : 'danger'" size="small">
              {{ statusMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button text type="primary" size="small" @click="handleView(row)">详情</el-button>
              <el-button v-if="row.status === 1" text type="warning" size="small" @click="handleStatus(row.id, 2)">下架</el-button>
              <el-button v-else-if="row.status === 2" text type="success" size="small" @click="handleStatus(row.id, 1)">上架</el-button>
              <el-popconfirm title="确定删除此内容？" @confirm="handleDelete(row.id)">
                <template #reference>
                  <el-button text type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination" v-if="total > 0">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadContents" />
      </div>
    </div>
    
    <!-- 内容详情弹窗 -->
    <el-dialog v-model="detailVisible" title="内容详情" width="800px" top="5vh">
      <div v-if="currentContent" class="content-detail">
        <div class="detail-header">
          <h2>{{ currentContent.title }}</h2>
          <div class="detail-meta">
            <el-tag size="small">{{ contentTypeMap[currentContent.contentType] }}</el-tag>
            <el-tag v-if="currentContent.isPaid" type="warning" size="small">¥{{ currentContent.price }}</el-tag>
            <el-tag v-else type="info" size="small">免费</el-tag>
            <el-tag :type="currentContent.status === 1 ? 'success' : currentContent.status === 0 ? 'info' : 'danger'" size="small">
              {{ statusMap[currentContent.status] }}
            </el-tag>
          </div>
        </div>
        
        <el-descriptions :column="2" border class="detail-info">
          <el-descriptions-item label="ID">{{ currentContent.id }}</el-descriptions-item>
          <el-descriptions-item label="创作者">{{ currentContent.creatorName }}</el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ currentContent.viewCount }}</el-descriptions-item>
          <el-descriptions-item label="点赞数">{{ currentContent.likeCount }}</el-descriptions-item>
          <el-descriptions-item label="评论数">{{ currentContent.commentCount }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ currentContent.createdAt }}</el-descriptions-item>
        </el-descriptions>
        
        <div v-if="currentContent.coverImage" class="detail-cover">
          <h4>封面图片</h4>
          <el-image :src="currentContent.coverImage" fit="cover" style="max-width: 400px; max-height: 200px; border-radius: 8px;" :preview-src-list="[currentContent.coverImage]" />
        </div>
        
        <div v-if="currentContent.summary" class="detail-summary">
          <h4>内容简介</h4>
          <p>{{ currentContent.summary }}</p>
        </div>
        
        <!-- 媒体内容 -->
        <div v-if="currentContent.mediaUrl" class="detail-media">
          <h4>媒体内容</h4>
          <template v-if="currentContent.contentType === 2">
            <video :src="getFullUrl(currentContent.mediaUrl)" controls style="max-width: 100%; border-radius: 8px;"></video>
          </template>
          <template v-else-if="currentContent.contentType === 3">
            <audio :src="getFullUrl(currentContent.mediaUrl)" controls style="width: 100%;"></audio>
          </template>
          <template v-else-if="currentContent.contentType === 4">
            <div class="gallery-preview">
              <el-image v-for="(url, index) in getGalleryUrls(currentContent.mediaUrl)" :key="index" :src="getFullUrl(url)" fit="cover" style="width: 120px; height: 120px; border-radius: 8px; margin: 4px;" :preview-src-list="getGalleryUrls(currentContent.mediaUrl).map(u => getFullUrl(u))" :initial-index="index" />
            </div>
          </template>
        </div>
        
        <div class="detail-content">
          <h4>正文内容</h4>
          <div class="content-text">{{ currentContent.content }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getContentList, getAdminContentDetail, updateContentStatus, deleteContent } from '@/api'
import { ElMessage } from 'element-plus'

const contents = ref([])
const loading = ref(false)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentContent = ref(null)

const contentTypeMap = { 1: '文章', 2: '视频', 3: '音频', 4: '图集' }
const statusMap = { 0: '草稿', 1: '已发布', 2: '已下架' }

const loadContents = async () => {
  loading.value = true
  try {
    const res = await getContentList({ page: currentPage.value, size: pageSize.value, keyword: keyword.value || undefined })
    contents.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleView = async (row) => {
  try {
    const res = await getAdminContentDetail(row.id)
    currentContent.value = { ...res.data, creatorName: row.creatorName }
    detailVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const getFullUrl = (url) => {
  if (!url) return ''
  return url.startsWith('http') ? url : `http://localhost:8080${url}`
}

const getGalleryUrls = (mediaUrl) => {
  if (!mediaUrl) return []
  return mediaUrl.split('\n').filter(url => url.trim())
}

const handleStatus = async (id, status) => {
  try {
    await updateContentStatus(id, status)
    ElMessage.success('操作成功')
    loadContents()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await deleteContent(id)
    ElMessage.success('删除成功')
    loadContents()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => loadContents())
</script>

<style lang="scss" scoped>
@use '@/styles/variables.scss' as *;

.table-card {
  :deep(.el-table) {
    .el-table__cell {
      white-space: nowrap;
    }
  }
}

.action-buttons {
  display: flex;
  flex-wrap: nowrap;
  gap: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: $spacing-lg;
}

.content-detail {
  .detail-header {
    margin-bottom: $spacing-lg;
    h2 {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: $spacing-sm;
    }
    .detail-meta {
      display: flex;
      gap: $spacing-sm;
    }
  }
  
  .detail-info {
    margin-bottom: $spacing-lg;
  }
  
  .detail-cover, .detail-summary, .detail-media, .detail-content {
    margin-bottom: $spacing-lg;
    h4 {
      font-size: 14px;
      font-weight: 600;
      color: $text-secondary;
      margin-bottom: $spacing-sm;
    }
  }
  
  .detail-summary p {
    color: $text-secondary;
    line-height: 1.6;
  }
  
  .gallery-preview {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-sm;
  }
  
  .content-text {
    background: $background-color;
    padding: $spacing-md;
    border-radius: 8px;
    white-space: pre-wrap;
    line-height: 1.8;
    max-height: 300px;
    overflow-y: auto;
  }
}
</style>
