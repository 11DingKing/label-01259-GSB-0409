<template>
  <div class="logs-page">
    <div class="page-header">
      <h1 class="page-title">操作日志</h1>
      <el-input v-model="keyword" placeholder="搜索操作" clearable @clear="loadLogs" @keyup.enter="loadLogs" style="width: 240px">
        <template #append>
          <el-button @click="loadLogs"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>
    
    <div class="card table-card">
      <el-table :data="logs" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" show-overflow-tooltip />
        <el-table-column prop="operation" label="操作" width="150" show-overflow-tooltip />
        <el-table-column prop="method" label="方法" min-width="250" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="duration" label="耗时(ms)" width="100" />
        <el-table-column prop="createdAt" label="操作时间" width="180" show-overflow-tooltip />
      </el-table>
      
      <div class="pagination" v-if="total > 0">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadLogs" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOperationLogs } from '@/api'

const logs = ref([])
const loading = ref(false)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(15)
const total = ref(0)

const loadLogs = async () => {
  loading.value = true
  try {
    const res = await getOperationLogs({ page: currentPage.value, size: pageSize.value, keyword: keyword.value || undefined })
    logs.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => loadLogs())
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

.pagination {
  display: flex;
  justify-content: center;
  margin-top: $spacing-lg;
}
</style>
