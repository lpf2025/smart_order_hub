<template>
  <div class="home">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%)">
              <el-icon><Shop /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.merchantCount }}</div>
              <div class="stat-label">门店总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%)">
              <el-icon><Food /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.dishCount }}</div>
              <div class="stat-label">菜品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%)">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCount }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%)">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单趋势</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-icon><DataAnalysis /></el-icon>
            <p>订单趋势图表</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热销菜品</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-icon><Trophy /></el-icon>
            <p>热销菜品排行</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-orders">
      <template #header>
        <div class="card-header">
          <span>最新订单</span>
          <el-button type="primary" link @click="$router.push('/orders')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentOrders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="merchantName" label="门店" width="120" />
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
        <el-table-column prop="createdAt" label="下单时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({
  merchantCount: 0,
  dishCount: 0,
  orderCount: 0,
  userCount: 0
})

const recentOrders = ref([])

const loadStats = async () => {
  try {
    const merchants = await request.post('/merchant/list')
    stats.value.merchantCount = Array.isArray(merchants) ? merchants.length : 0
  } catch (error) {
    console.error('加载门店统计失败', error)
  }

  try {
    const dishes = await request.post('/dish/list', {})
    stats.value.dishCount = Array.isArray(dishes) ? dishes.length : 0
  } catch (error) {
    console.error('加载菜品统计失败', error)
  }

  try {
    const users = await request.post('/user/list')
    stats.value.userCount = Array.isArray(users) ? users.length : 0
  } catch (error) {
    console.error('加载用户统计失败', error)
  }
}

const loadRecentOrders = async () => {
  try {
    const data = await request.post('/order/merchant', {
      merchantId: 1,
      page: 1,
      size: 5
    })
    recentOrders.value = data.records || []
    stats.value.orderCount = data.total || 0
  } catch (error) {
    console.error('加载订单失败', error)
  }
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
  console.log('Home组件已挂载')
  loadStats()
  loadRecentOrders()
})
</script>

<style scoped>
.home {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  overflow: hidden;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.charts-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 48px;
}

.chart-placeholder p {
  font-size: 14px;
  margin-top: 20px;
}

.recent-orders {
  border-radius: 12px;
  overflow: hidden;
}
</style>
