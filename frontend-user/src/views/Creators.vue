<template>
  <div class="creators-page">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">发现创作者</h1>
        <p class="page-desc">探索各领域优秀创作者，关注你感兴趣的内容</p>
      </div>
    </div>
    
    <div class="container">
      <div class="filter-bar">
        <div class="category-filter">
          <button 
            :class="['category-btn', { active: selectedCategory === '' }]"
            @click="selectedCategory = ''; loadCreators()"
          >全部</button>
          <button 
            v-for="cat in categories"
            :key="cat"
            :class="['category-btn', { active: selectedCategory === cat }]"
            @click="selectedCategory = cat; loadCreators()"
          >{{ cat }}</button>
        </div>
        
        <el-input
          v-model="keyword"
          placeholder="搜索创作者"
          :prefix-icon="Search"
          clearable
          @clear="loadCreators"
          @keyup.enter="loadCreators"
          class="search-input"
        />
      </div>
      
      <div class="creator-grid" v-loading="loading">
        <CreatorCard
          v-for="creator in creators"
          :key="creator.id"
          :creator="creator"
          @update="loadCreators"
        />
      </div>
      
      <div v-if="!loading && creators.length === 0" class="empty-state">
        <el-empty description="暂无创作者">
          <template #image>
            <svg viewBox="0 0 120 120" fill="none" class="empty-icon">
              <circle cx="60" cy="60" r="50" fill="#f5f5f5"/>
              <circle cx="60" cy="45" r="15" fill="#e0e0e0"/>
              <path d="M35 85c0-14 11-25 25-25s25 11 25 25" stroke="#e0e0e0" stroke-width="8" stroke-linecap="round"/>
            </svg>
          </template>
        </el-empty>
      </div>
      
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadCreators"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCreatorList, getCategories } from '@/api/creator'
import CreatorCard from '@/components/CreatorCard.vue'
import { Search } from '@element-plus/icons-vue'

const creators = ref([])
const categories = ref([])
const loading = ref(false)
const selectedCategory = ref('')
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadCreators = async () => {
  loading.value = true
  try {
    const res = await getCreatorList({
      page: currentPage.value,
      size: pageSize.value,
      category: selectedCategory.value || undefined,
      keyword: keyword.value || undefined,
    })
    creators.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadCategories()
  loadCreators()
})
</script>

<style lang="scss" scoped>
.creators-page {
  min-height: calc(100vh - 72px);
  background: $background-color;
}

.page-header {
  background: $gradient-dark;
  padding: $spacing-3xl 0;
  text-align: center;
  
  .page-title {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    color: white;
    margin-bottom: $spacing-sm;
  }
  
  .page-desc {
    color: rgba(white, 0.7);
    font-size: $font-size-lg;
  }
}

.filter-bar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: $spacing-xl 0;
  gap: $spacing-lg;
  
  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.category-filter {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  flex: 1;
  
  .category-btn {
    padding: 8px 20px;
    border: 2px solid $border-color;
    background: $card-background;
    border-radius: $border-radius-full;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-secondary;
    cursor: pointer;
    transition: all $transition-fast;
    white-space: nowrap;
    
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
}

.search-input {
  width: 240px;
  flex-shrink: 0;
  
  @media (max-width: 768px) {
    width: 100%;
  }
}

.creator-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;
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
    color: $text-muted;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  padding: $spacing-2xl 0;
}
</style>
