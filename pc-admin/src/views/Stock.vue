<template>
  <div class="stock-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存管理</span>
          <el-button type="primary" @click="handleRefresh">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="stockList" style="width: 100%">
        <el-table-column prop="dishName" label="菜品名称" />
        <el-table-column prop="stock" label="库存数量" />
        <el-table-column prop="sales" label="销量" />
        <el-table-column prop="monthSales" label="本月销量" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const stockList = ref([])

const loadStockList = () => {
  ElMessage.info('库存管理功能开发中')
}

const handleRefresh = () => {
  loadStockList()
}

const handleEdit = (row) => {
  ElMessage.info('编辑库存功能开发中')
}

onMounted(() => {
  loadStockList()
})
</script>

<style scoped>
.stock-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
