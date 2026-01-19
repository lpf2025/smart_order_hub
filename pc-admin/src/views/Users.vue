<template>
  <div class="users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户管理</span>
        </div>
      </template>

      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="scope">
            <el-avatar :src="scope.row.avatarUrl" />
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="createdAt" label="注册时间" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleView(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadUsers"
        @current-change="loadUsers"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const users = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadUsers = async () => {
  loading.value = true
  try {
    const data = await request.post('/user/list')
    users.value = data || []
    pagination.total = data.length || 0
  } catch (error) {
    console.error('加载用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  ElMessage.info('用户详情功能开发中')
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
