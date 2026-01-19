<template>
  <div class="statistics">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%)">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalOrders }}</div>
              <div class="stat-label">总订单数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%)">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.totalAmount }}</div>
              <div class="stat-label">总营业额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%)">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%)">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.avgOrderAmount }}</div>
              <div class="stat-label">平均客单价</div>
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
          <div ref="orderTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销售额趋势</span>
            </div>
          </template>
          <div ref="salesTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热销菜品排行</span>
            </div>
          </template>
          <div ref="hotDishesChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单状态分布</span>
            </div>
          </template>
          <div ref="orderStatusChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import request from '@/utils/request'

const echarts = window.echarts

const stats = reactive({
  totalOrders: 0,
  totalAmount: 0,
  totalUsers: 0,
  avgOrderAmount: 0
})

const orderTrendChart = ref(null)
const salesTrendChart = ref(null)
const hotDishesChart = ref(null)
const orderStatusChart = ref(null)

let orderTrendInstance = null
let salesTrendInstance = null
let hotDishesInstance = null
let orderStatusInstance = null

const loadStatistics = async () => {
  try {
    const orders = await request.post('/order/merchant', {
      merchantId: 1,
      page: 1,
      size: 1000
    })
    const users = await request.post('/user/list')
    
    const completedOrders = orders.records?.filter(o => o.status === 5) || []
    const totalAmount = completedOrders.reduce((sum, order) => sum + order.totalAmount, 0)
    
    stats.totalOrders = completedOrders.length
    stats.totalAmount = totalAmount.toFixed(2)
    stats.totalUsers = users.length || 0
    stats.avgOrderAmount = completedOrders.length > 0 
      ? (totalAmount / completedOrders.length).toFixed(2) 
      : '0.00'

    await nextTick()
    initCharts(orders.records || [])
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const initCharts = (orders) => {
  initOrderTrendChart(orders)
  initSalesTrendChart(orders)
  initHotDishesChart(orders)
  initOrderStatusChart(orders)
}

const initOrderTrendChart = (orders) => {
  if (!orderTrendChart.value) return
  
  orderTrendInstance = echarts.init(orderTrendChart.value)
  
  const dateMap = {}
  orders.forEach(order => {
    const date = order.createdAt ? order.createdAt.split(' ')[0] : new Date().toISOString().split('T')[0]
    if (!dateMap[date]) {
      dateMap[date] = 0
    }
    dateMap[date]++
  })
  
  const dates = Object.keys(dateMap).sort()
  const counts = dates.map(date => dateMap[date])
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates.length > 0 ? dates : ['暂无数据'],
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [{
      name: '订单数',
      type: 'line',
      smooth: true,
      data: counts.length > 0 ? counts : [0],
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
          { offset: 1, color: 'rgba(24, 144, 255, 0.05)' }
        ])
      },
      itemStyle: {
        color: '#1890ff'
      },
      lineStyle: {
        width: 3
      }
    }]
  }
  
  orderTrendInstance.setOption(option)
}

const initSalesTrendChart = (orders) => {
  if (!salesTrendChart.value) return
  
  salesTrendInstance = echarts.init(salesTrendChart.value)
  
  const dateMap = {}
  orders.forEach(order => {
    const date = order.createdAt ? order.createdAt.split(' ')[0] : new Date().toISOString().split('T')[0]
    if (!dateMap[date]) {
      dateMap[date] = 0
    }
    dateMap[date] += order.totalAmount || 0
  })
  
  const dates = Object.keys(dateMap).sort()
  const amounts = dates.map(date => dateMap[date].toFixed(2))
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>销售额: ¥{c}'
    },
    xAxis: {
      type: 'category',
      data: dates.length > 0 ? dates : ['暂无数据'],
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [{
      name: '销售额',
      type: 'line',
      smooth: true,
      data: amounts.length > 0 ? amounts : [0],
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
          { offset: 1, color: 'rgba(82, 196, 26, 0.05)' }
        ])
      },
      itemStyle: {
        color: '#52c41a'
      },
      lineStyle: {
        width: 3
      }
    }]
  }
  
  salesTrendInstance.setOption(option)
}

const initHotDishesChart = (orders) => {
  if (!hotDishesChart.value) return
  
  hotDishesInstance = echarts.init(hotDishesChart.value)
  
  const dishMap = {}
  orders.forEach(order => {
    if (order.items && Array.isArray(order.items)) {
      order.items.forEach(item => {
        if (!dishMap[item.dishName]) {
          dishMap[item.dishName] = 0
        }
        dishMap[item.dishName] += item.quantity || 0
      })
    }
  })
  
  const sortedDishes = Object.entries(dishMap)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 10)
  
  const dishNames = sortedDishes.map(item => item[0])
  const quantities = sortedDishes.map(item => item[1])
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      minInterval: 1
    },
    yAxis: {
      type: 'category',
      data: dishNames.length > 0 ? dishNames : ['暂无数据'],
      inverse: true
    },
    series: [{
      name: '销量',
      type: 'bar',
      data: quantities.length > 0 ? quantities : [0],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#ff7875' },
          { offset: 1, color: '#ff4d4f' }
        ])
      },
      barWidth: '60%'
    }]
  }
  
  hotDishesInstance.setOption(option)
}

const initOrderStatusChart = (orders) => {
  if (!orderStatusChart.value) return
  
  orderStatusInstance = echarts.init(orderStatusChart.value)
  
  const statusMap = {
    1: '待接单',
    2: '待出餐',
    3: '待配送',
    4: '配送中',
    5: '已完成',
    6: '已取消'
  }
  
  const statusCount = {}
  Object.values(statusMap).forEach(status => {
    statusCount[status] = 0
  })
  
  orders.forEach(order => {
    const statusName = statusMap[order.status] || '未知'
    if (statusCount[statusName] !== undefined) {
      statusCount[statusName]++
    }
  })
  
  const data = Object.entries(statusCount).map(([name, value]) => ({
    name,
    value
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '订单状态',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {c}'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      data: data
    }]
  }
  
  orderStatusInstance.setOption(option)
}

const handleResize = () => {
  orderTrendInstance?.resize()
  salesTrendInstance?.resize()
  hotDishesInstance?.resize()
  orderStatusInstance?.resize()
}

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  orderTrendInstance?.dispose()
  salesTrendInstance?.dispose()
  hotDishesInstance?.dispose()
  orderStatusInstance?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
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

.chart-container {
  height: 300px;
  width: 100%;
}
</style>
