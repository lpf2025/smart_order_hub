<template>
  <div class="orders">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <el-button type="primary" @click="loadOrders">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="门店">
          <el-select v-model="searchForm.merchantId" placeholder="请选择门店" clearable style="width: 200px">
            <el-option label="全部" :value="null" />
            <el-option
              v-for="merchant in merchants"
              :key="merchant.id"
              :label="merchant.name"
              :value="merchant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" :value="null" />
            <el-option label="待接单" :value="1" />
            <el-option label="待出餐" :value="2" />
            <el-option label="待配送" :value="3" />
            <el-option label="配送中" :value="4" />
            <el-option label="已完成" :value="5" />
            <el-option label="已取消" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadOrders">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="orders" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="merchantName" label="门店" width="150" />
        <el-table-column prop="tableNo" label="桌号" width="100">
          <template #default="scope">
            {{ scope.row.tableNo || '外卖' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="scope">
            <span style="color: #ff4d4f; font-weight: bold">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.payStatus === 1 ? 'success' : 'warning'">
              {{ scope.row.payStatus === 1 ? '已支付' : '未支付' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleView(scope.row)">查看</el-button>
            <el-button 
              type="danger" 
              link 
              v-if="scope.row.status === 1"
              @click="handleCancel(scope.row)"
            >取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadOrders"
        @current-change="loadOrders"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const orders = ref([])
const merchants = ref([])

const searchForm = reactive({
  merchantId: null,
  orderNo: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      merchantId: searchForm.merchantId,
      page: pagination.page,
      size: pagination.size
    }
    
    if (searchForm.status !== null) {
      params.status = searchForm.status
    }
    
    const data = await request.post('/order/merchant', params)
    orders.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载订单列表失败', error)
  } finally {
    loading.value = false
  }
}

const loadMerchants = async () => {
  try {
    const data = await request.post('/merchant/list')
    merchants.value = data
  } catch (error) {
    console.error('加载门店列表失败', error)
  }
}

const resetSearch = () => {
  searchForm.merchantId = null
  searchForm.orderNo = ''
  searchForm.status = null
  pagination.page = 1
  loadOrders()
}

const handleView = (row) => {
  ElMessage.info('订单详情功能开发中')
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post('/order/cancel', { id: row.id })
      ElMessage.success('取消成功')
      loadOrders()
    } catch (error) {
      console.error('取消失败', error)
    }
  })
}

const getStatusText = (status) => {
  const map = {
    1: '待接单',
    2: '待出餐',
    3: '待配送',
    4: '配送中',
    5: '已完成',
    6: '已取消'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'success',
    6: 'danger'
  }
  return map[status] || ''
}

onMounted(() => {
  loadMerchants()
  loadOrders()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}
</style>
