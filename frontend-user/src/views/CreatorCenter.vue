<template>
  <div class="creator-center-page">
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-text">
            <h1 class="page-title">创作中心</h1>
            <p class="page-desc">管理你的作品，查看数据表现</p>
          </div>
          <router-link to="/publish">
            <el-button type="primary" size="large">
              <el-icon><Plus /></el-icon>发布内容
            </el-button>
          </router-link>
        </div>
      </div>
    </div>
    
    <div class="container">
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon icon-content">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <span class="value">{{ total }}</span>
            <span class="label">作品数</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon icon-fans">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <span class="value">{{ creatorProfile?.followerCount || 0 }}</span>
            <span class="label">粉丝数</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon icon-income">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <span class="value">¥{{ (creatorProfile?.totalIncome ?? 0).toFixed(2) }}</span>
            <span class="label">总收入</span>
          </div>
        </div>
      </div>
      
      <div class="content-section">
        <div class="section-header">
          <h2 class="section-title">我的作品</h2>
          <div class="filter-tabs">
            <button :class="['tab-btn', { active: statusFilter === null }]" @click="statusFilter = null; loadContents()">全部</button>
            <button :class="['tab-btn', { active: statusFilter === 1 }]" @click="statusFilter = 1; loadContents()">已发布</button>
            <button :class="['tab-btn', { active: statusFilter === 0 }]" @click="statusFilter = 0; loadContents()">草稿</button>
          </div>
        </div>
        
        <div class="content-table" v-loading="loading">
          <el-table v-if="contents.length > 0" :data="contents" stripe style="width: 100%">
            <el-table-column prop="title" label="标题" min-width="200">
              <template #default="{ row }">
                <router-link :to="`/content/${row.id}`" class="content-link">{{ row.title }}</router-link>
              </template>
            </el-table-column>
            <el-table-column prop="contentType" label="类型" width="80">
              <template #default="{ row }">
                <el-tag size="small" effect="plain">{{ contentTypeMap[row.contentType] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="isPaid" label="付费" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.isPaid" type="warning" size="small" effect="dark">¥{{ row.price }}</el-tag>
                <el-tag v-else type="info" size="small" effect="plain">免费</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="viewCount" label="浏览" width="80" />
            <el-table-column prop="likeCount" label="点赞" width="80" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="dark">
                  {{ row.status === 1 ? '已发布' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <div class="action-btns">
                  <el-button class="edit-btn" size="small" @click="editContent(row.id)">
                    <el-icon><Edit /></el-icon>编辑
                  </el-button>
                  <el-popconfirm title="确定删除此内容？" @confirm="handleDelete(row.id)">
                    <template #reference>
                      <el-button class="delete-btn" size="small">
                        <el-icon><Delete /></el-icon>删除
                      </el-button>
                    </template>
                  </el-popconfirm>
                </div>
              </template>
            </el-table-column>
          </el-table>
          
          <div v-if="!loading && contents.length === 0" class="empty-state">
            <el-empty :description="emptyText">
              <router-link to="/publish">
                <el-button type="primary">发布第一篇内容</el-button>
              </router-link>
            </el-empty>
          </div>
        </div>
        
        <div class="pagination" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="loadContents"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyCreatorProfile } from '@/api/creator'
import { getMyContents, deleteContent } from '@/api/content'
import { ElMessage } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'

const router = useRouter()

const creatorProfile = ref(null)
const contents = ref([])
const loading = ref(false)
const statusFilter = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const contentTypeMap = { 1: '文章', 2: '视频', 3: '音频', 4: '图集' }

const emptyText = computed(() => {
  if (statusFilter.value === 0) return '暂无草稿内容'
  if (statusFilter.value === 1) return '暂无已发布内容'
  return '还没有发布任何内容'
})

const loadCreatorProfile = async () => {
  try {
    const res = await getMyCreatorProfile()
    // 确保返回的数据有默认值，且只有已通过审核的创作者才显示完整数据
    if (res.data && res.data.status === 1) {
      creatorProfile.value = {
        ...res.data,
        followerCount: res.data.followerCount || 0,
        contentCount: res.data.contentCount || 0,
        totalIncome: res.data.totalIncome || 0
      }
    } else {
      creatorProfile.value = { followerCount: 0, contentCount: 0, totalIncome: 0 }
    }
  } catch (error) {
    console.error(error)
    creatorProfile.value = { followerCount: 0, contentCount: 0, totalIncome: 0 }
  }
}

const loadContents = async () => {
  loading.value = true
  try {
    const res = await getMyContents({ page: currentPage.value, size: pageSize.value, status: statusFilter.value })
    contents.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const editContent = (id) => router.push(`/edit/${id}`)

const handleDelete = async (id) => {
  try {
    await deleteContent(id)
    ElMessage.success('删除成功')
    loadContents()
    loadCreatorProfile()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadCreatorProfile()
  loadContents()
})
</script>

<style lang="scss" scoped>
.creator-center-page {
  min-height: calc(100vh - 72px);
  background: $background-color;
}

.page-header {
  background: $gradient-primary;
  padding: $spacing-2xl 0;
  
  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .page-title {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    color: white;
    margin-bottom: $spacing-xs;
  }
  
  .page-desc {
    color: rgba(white, 0.8);
  }
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-lg;
  margin: -$spacing-xl 0 $spacing-xl;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    margin-top: $spacing-lg;
  }
}

.stat-card {
  background: $card-background;
  border-radius: $border-radius-xl;
  box-shadow: $shadow-lg;
  padding: $spacing-lg;
  display: flex;
  align-items: center;
  gap: $spacing-md;
  
  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: $border-radius-lg;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon { font-size: 28px; color: white; }
    
    &.icon-content { background: $gradient-primary; }
    &.icon-fans { background: $gradient-accent; }
    &.icon-income { background: linear-gradient(135deg, #667eea, #764ba2); }
  }
  
  .stat-info {
    .value {
      display: block;
      font-size: $font-size-2xl;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }
    
    .label {
      font-size: $font-size-sm;
      color: $text-muted;
    }
  }
}

.content-section {
  background: $card-background;
  border-radius: $border-radius-xl;
  box-shadow: $shadow-md;
  padding: $spacing-xl;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-lg;
  flex-wrap: wrap;
  gap: $spacing-md;
  
  .section-title {
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
    margin: 0;
  }
}

.filter-tabs {
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

.content-table {
  :deep(.el-table) {
    border-radius: $border-radius-lg;
    overflow: hidden;
  }
  
  :deep(.el-table__header th) {
    background: $background-color !important;
    font-weight: $font-weight-semibold;
  }
  
  .action-btns {
    display: flex;
    gap: $spacing-sm;
    
    .edit-btn {
      background: $primary-color !important;
      border: 1px solid $primary-color !important;
      color: white !important;
      font-weight: $font-weight-medium;
      
      &:hover {
        background: #e55a50 !important;
        border-color: #e55a50 !important;
      }
      
      .el-icon {
        margin-right: 4px;
      }
    }
    
    .delete-btn {
      background: transparent !important;
      border: 1px solid $danger-color !important;
      color: $danger-color !important;
      font-weight: $font-weight-medium;
      
      &:hover {
        background: rgba($danger-color, 0.1) !important;
      }
      
      .el-icon {
        margin-right: 4px;
      }
    }
  }
}

.content-link {
  color: $text-primary;
  font-weight: $font-weight-medium;
  
  &:hover { color: $primary-color; }
}

.empty-state {
  padding: $spacing-3xl $spacing-xl;
  display: flex;
  justify-content: center;
  
  :deep(.el-empty) {
    padding: $spacing-xl 0;
    
    .el-empty__image {
      width: 120px;
    }
    
    .el-empty__description {
      margin-top: $spacing-md;
      color: $text-muted;
    }
    
    .el-empty__bottom {
      margin-top: $spacing-lg;
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: $spacing-xl;
}
</style>
