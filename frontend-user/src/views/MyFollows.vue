<template>
  <div class="my-follows-page">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">我的关注</h1>
        <p class="page-desc">管理你关注的创作者</p>
      </div>
    </div>
    
    <div class="container">
      <div class="creator-grid" v-loading="loading">
        <CreatorCard
          v-for="creator in creators"
          :key="creator.id"
          :creator="creator"
          @update="loadFollows"
        />
      </div>
      
      <div v-if="!loading && creators.length === 0" class="empty-state">
        <el-empty description="还没有关注任何创作者">
          <template #image>
            <svg viewBox="0 0 120 120" fill="none" class="empty-icon">
              <circle cx="60" cy="60" r="50" fill="#f5f5f5"/>
              <path d="M60 35L65 55H85L69 68L74 88L60 75L46 88L51 68L35 55H55L60 35Z" fill="#e0e0e0"/>
            </svg>
          </template>
          <router-link to="/creators">
            <el-button type="primary" size="large">去发现创作者</el-button>
          </router-link>
        </el-empty>
      </div>
      
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadFollows"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFollowList } from '@/api/interaction'
import CreatorCard from '@/components/CreatorCard.vue'

const creators = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadFollows = async () => {
  loading.value = true
  try {
    const res = await getFollowList({
      page: currentPage.value,
      size: pageSize.value,
    })
    creators.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFollows()
})
</script>

<style lang="scss" scoped>
.my-follows-page {
  min-height: calc(100vh - 72px);
  background: $background-color;
}

.page-header {
  background: $gradient-dark;
  padding: $spacing-2xl 0;
  text-align: center;
  
  .page-title {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    color: white;
    margin-bottom: $spacing-xs;
  }
  
  .page-desc {
    color: rgba(white, 0.7);
  }
}

.creator-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;
  padding: $spacing-xl 0;
  min-height: 200px;
  
  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }
  
  @media (max-width: 900px) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: 600px) {
    grid-template-columns: 1fr;
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  
  .empty-icon {
    width: 120px;
    height: 120px;
  }
  
  :deep(.el-empty) {
    padding: $spacing-3xl;
  }
  
  :deep(.el-empty__description) {
    margin-top: $spacing-md;
    margin-bottom: $spacing-lg;
    color: $text-muted;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  padding-bottom: $spacing-2xl;
}
</style>
