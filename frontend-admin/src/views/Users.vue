<template>
  <div class="users-page">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <el-input v-model="keyword" placeholder="搜索用户" clearable @clear="loadUsers" @keyup.enter="loadUsers" style="width: 240px">
        <template #append>
          <el-button @click="loadUsers"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>
    
    <div class="card table-card">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="nickname" label="昵称" width="120" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="balance" label="余额" width="100">
          <template #default="{ row }">¥{{ row.balance?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.role === 2" type="danger" size="small">管理员</el-tag>
            <el-tag v-else-if="row.role === 1" type="success" size="small">创作者</el-tag>
            <el-tag v-else type="info" size="small">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" text type="danger" size="small" @click="handleStatus(row.id, 0)">禁用</el-button>
            <el-button v-else text type="success" size="small" @click="handleStatus(row.id, 1)">启用</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadUsers" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, updateUserStatus } from '@/api'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList({ page: currentPage.value, size: pageSize.value, keyword: keyword.value || undefined })
    users.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleStatus = async (id, status) => {
  try {
    await updateUserStatus(id, status)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => loadUsers())
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
