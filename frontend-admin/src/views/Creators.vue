<template>
  <div class="creators-page">
    <div class="page-header">
      <h1 class="page-title">创作者审核</h1>
    </div>
    
    <div class="card table-card">
      <el-table :data="creators" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="penName" label="笔名" width="120" show-overflow-tooltip />
        <el-table-column prop="category" label="领域" width="100" />
        <el-table-column prop="bio" label="简介" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已通过</el-tag>
            <el-tag v-else type="danger" size="small">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button text type="success" size="small" @click="handleAudit(row.id, 1)">通过</el-button>
              <el-button text type="danger" size="small" @click="handleAudit(row.id, 2)">拒绝</el-button>
            </template>
            <span v-else class="text-secondary">已处理</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && creators.length === 0" description="暂无待审核的创作者申请" />
      
      <div class="pagination" v-if="total > 0">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadCreators" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPendingCreators, auditCreator } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const creators = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadCreators = async () => {
  loading.value = true
  try {
    const res = await getPendingCreators({ page: currentPage.value, size: pageSize.value })
    creators.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAudit = async (id, status) => {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定${action}该创作者申请？`, '确认操作', { type: 'warning' })
    await auditCreator(id, status)
    ElMessage.success('操作成功')
    loadCreators()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(() => loadCreators())
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

.text-secondary {
  color: $text-secondary;
  font-size: $font-size-sm;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: $spacing-lg;
}
</style>
