<template>
  <div class="dishes">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜品管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加菜品
            </el-button>
          </div>
        </div>
      </template>

      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索菜品名称"
          clearable
          style="width: 200px; margin-right: 10px"
          @clear="loadDishes"
          @keyup.enter="loadDishes"
        />
        <el-select
          v-model="filterMerchant"
          placeholder="选择门店"
          clearable
          style="width: 150px; margin-right: 10px"
          @change="loadDishes"
        >
          <el-option label="全部" value="" />
          <el-option
            v-for="merchant in merchants"
            :key="merchant.id"
            :label="merchant.name"
            :value="merchant.id"
          />
        </el-select>
        <el-select
          v-model="filterCategory"
          placeholder="选择分类"
          clearable
          style="width: 150px; margin-right: 10px"
          @change="loadDishes"
        >
          <el-option label="全部" value="" />
          <el-option label="热菜" value="热菜" />
          <el-option label="凉菜" value="凉菜" />
          <el-option label="主食" value="主食" />
          <el-option label="饮品" value="饮品" />
          <el-option label="甜点" value="甜点" />
        </el-select>
        <el-select
          v-model="sortBy"
          placeholder="排序方式"
          style="width: 120px; margin-right: 10px"
          @change="loadDishes"
        >
          <el-option label="创建时间" value="createdAt" />
          <el-option label="菜品名称" value="name" />
          <el-option label="价格" value="price" />
          <el-option label="销量" value="sales" />
        </el-select>
        <el-select
          v-model="sortOrder"
          placeholder="排序顺序"
          style="width: 120px; margin-right: 10px"
          @change="loadDishes"
        >
          <el-option label="升序" value="asc" />
          <el-option label="降序" value="desc" />
        </el-select>
        <el-button type="primary" @click="loadDishes">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table
        :data="paginatedDishes"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: '600' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="菜品图片" width="100" align="center">
          <template #default="{ row }">
            <div class="table-image">
              <el-image
                v-if="row.imageUrl"
                :src="row.imageUrl"
                :preview-src-list="[row.imageUrl]"
                fit="cover"
                :style="{ filter: row.status === 0 ? 'grayscale(10%)' : 'none' }"
              />
              <div v-else class="no-image">暂无图片</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜品名称" min-width="150">
          <template #default="{ row }">
            <span :style="{ color: row.status === 0 ? '#86909C' : '#303133' }">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="merchantName" label="所属门店" width="150">
          <template #default="{ row }">
            <span :style="{ color: row.status === 0 ? '#86909C' : '#606266' }">{{ row.merchantName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="所属分类" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.status === 0 ? '#86909C' : '#606266' }">{{ row.category }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100" align="right">
          <template #default="{ row }">
            <span :style="{ color: row.status === 0 ? '#86909C' : '#FF4D4F', fontWeight: 'bold' }">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.status === 0 ? '#86909C' : '#909399' }">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.status === 0 ? '#86909C' : '#909399' }">{{ row.sales }}</span>
          </template>
        </el-table-column>
        <el-table-column label="上架状态" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.status === 1" class="status-tag online">已上架</span>
            <span v-else class="status-tag offline">已下架</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadDishes"
          @current-change="loadDishes"
        />
      </div>

      <div class="batch-actions" v-if="selectedDishes.length > 0">
        <span>已选择 {{ selectedDishes.length }} 项</span>
        <el-button type="success" @click="handleBatchShelf(1)">批量上架</el-button>
        <el-button type="warning" @click="handleBatchShelf(0)">批量下架</el-button>
        <el-button type="danger" @click="handleBatchDelete">批量删除</el-button>
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜品名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入菜品名称" maxlength="50" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属门店" prop="merchantId">
              <el-select v-model="form.merchantId" placeholder="请选择门店" style="width: 100%">
                <el-option
                  v-for="merchant in merchants"
                  :key="merchant.id"
                  :label="merchant.name"
                  :value="merchant.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜品分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择菜品分类" style="width: 100%">
                <el-option label="热菜" value="热菜" />
                <el-option label="凉菜" value="凉菜" />
                <el-option label="主食" value="主食" />
                <el-option label="饮品" value="饮品" />
                <el-option label="甜点" value="甜点" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜品价格" prop="price">
              <el-input
                v-model="form.price"
                placeholder="请输入菜品价格（如：1k=1000）"
                @blur="handlePriceBlur"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input
                v-model="form.originalPrice"
                placeholder="请输入原价（如：1k=1000）"
                @blur="handleOriginalPriceBlur"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stock">
              <el-input
                v-model="form.stock"
                placeholder="请输入库存数量（如：1k=1000）"
                @blur="handleStockBlur"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜品状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="菜品图片">
          <image-upload
            v-model="form.imageUrls"
            :max-count="5"
            @change="handleImageChange"
          />
        </el-form-item>

        <el-form-item label="菜品描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入菜品描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="菜品详情"
      width="700px"
    >
      <div class="dish-detail" v-if="currentDish">
        <div class="detail-section">
          <h4>基础信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="菜品ID">{{ currentDish.id }}</el-descriptions-item>
            <el-descriptions-item label="菜品名称">{{ currentDish.name }}</el-descriptions-item>
            <el-descriptions-item label="所属门店">{{ currentDish.merchantName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="所属分类">{{ currentDish.category }}</el-descriptions-item>
            <el-descriptions-item label="菜品价格">
              <span style="color: #ff4d4f; font-weight: bold">¥{{ currentDish.price }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="原价">
              <span v-if="currentDish.originalPrice" style="color: #999; text-decoration: line-through">
                ¥{{ currentDish.originalPrice }}
              </span>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="库存数量">{{ currentDish.stock }}</el-descriptions-item>
            <el-descriptions-item label="菜品状态">
              <el-tag :type="currentDish.status === 1 ? 'success' : 'danger'">
                {{ currentDish.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="销量">{{ currentDish.sales }}</el-descriptions-item>
            <el-descriptions-item label="创建时间" :span="2">{{ formatTime(currentDish.createdAt) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section" v-if="currentDish.imageUrls && currentDish.imageUrls.length > 0">
          <h4>图片展示</h4>
          <div class="image-gallery">
            <div
              v-for="(url, index) in currentDish.imageUrls"
              :key="index"
              class="gallery-item"
            >
              <el-image
                :src="url"
                :preview-src-list="currentDish.imageUrls"
                :initial-index="index"
                fit="cover"
                style="width: 100%; height: 100%"
              />
              <div v-if="index === 0" class="cover-badge">封面</div>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="currentDish.description">
          <h4>菜品描述</h4>
          <p>{{ currentDish.description }}</p>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">返回</el-button>
        <el-button type="primary" @click="handleEditFromDetail">编辑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'
import ImageUpload from '@/components/ImageUpload.vue'

const loading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('添加菜品')
const formRef = ref(null)
const searchKeyword = ref('')
const filterCategory = ref('')
const filterMerchant = ref('')
const sortBy = ref('createdAt')
const sortOrder = ref('desc')
const selectedDishes = ref([])
const currentDish = ref(null)
const merchants = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const allDishes = ref([])

const paginatedDishes = computed(() => {
  const start = (pagination.page - 1) * pagination.size
  const end = start + pagination.size
  return allDishes.value.slice(start, end)
})

const parseShortcutInput = (value) => {
  if (typeof value === 'string') {
    const str = value.toLowerCase().trim()
    if (str.endsWith('k')) {
      return parseFloat(str.replace('k', '')) * 1000
    } else if (str.endsWith('w')) {
      return parseFloat(str.replace('w', '')) * 10000
    } else if (str.endsWith('m')) {
      return parseFloat(str.replace('m', '')) * 1000000
    }
    return parseFloat(str)
  }
  return value
}

const form = reactive({
  id: null,
  merchantId: null,
  name: '',
  category: '',
  price: 0,
  originalPrice: null,
  stock: 999,
  status: 1,
  imageUrls: [],
  description: ''
})

const rules = {
  merchantId: [{ required: true, message: '请选择所属门店', trigger: 'change' }],
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择菜品分类', trigger: 'change' }],
  price: [
    { required: true, message: '请输入菜品价格', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const num = parseShortcutInput(value)
        if (isNaN(num) || num < 0) {
          callback(new Error('请输入有效的价格'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const num = parseShortcutInput(value)
        if (isNaN(num) || num < 0 || !Number.isInteger(num)) {
          callback(new Error('请输入有效的库存数量'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

const loadDishes = async () => {
  loading.value = true
  try {
    const data = await request.post('/dish/merchant', {
      merchantId: filterMerchant.value || null,
      name: searchKeyword.value || null,
      category: filterCategory.value || null,
      status: null
    })
    
    const sortField = sortBy.value
    const order = sortOrder.value === 'asc' ? 1 : -1
    
    const sortedData = data.sort((a, b) => {
      if (a.status !== b.status) {
        return b.status - a.status
      }
      
      let comparison = 0
      if (sortField === 'price' || sortField === 'sales') {
        comparison = (a[sortField] || 0) - (b[sortField] || 0)
      } else if (sortField === 'name') {
        comparison = (a[sortField] || '').localeCompare(b[sortField] || '')
      } else {
        comparison = new Date(a[sortField] || 0) - new Date(b[sortField] || 0)
      }
      return comparison * order
    })
    
    allDishes.value = sortedData
    pagination.total = sortedData.length
  } catch (error) {
    console.error('加载菜品列表失败', error)
    ElMessage.error('加载菜品列表失败')
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

const switchGroup = (group) => {
  if (currentGroup.value === group) {
    currentGroup.value = 'all'
  } else {
    currentGroup.value = group
    if (group === 'online') {
      onlinePagination.page = 1
    } else if (group === 'offline') {
      offlinePagination.page = 1
    }
  }
}

const handleReset = () => {
  searchKeyword.value = ''
  filterMerchant.value = ''
  filterCategory.value = ''
  sortBy.value = 'createdAt'
  sortOrder.value = 'desc'
  pagination.page = 1
  loadDishes()
}

const handleAdd = () => {
  dialogTitle.value = '添加菜品'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜品'
  Object.assign(form, {
    id: row.id,
    merchantId: row.merchantId,
    name: row.name,
    category: row.category,
    price: row.price,
    originalPrice: row.originalPrice,
    stock: row.stock,
    status: row.status,
    imageUrls: row.imageUrls || (row.imageUrl ? [row.imageUrl] : []),
    description: row.description || ''
  })
  dialogVisible.value = true
}

const handleDetail = (row) => {
  currentDish.value = {
    ...row,
    imageUrls: row.imageUrls || (row.imageUrl ? [row.imageUrl] : [])
  }
  detailDialogVisible.value = true
}

const handleEditFromDetail = () => {
  detailDialogVisible.value = false
  handleEdit(currentDish.value)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该菜品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post('/dish/delete', { id: row.id })
      ElMessage.success('删除成功')
      loadDishes()
    } catch (error) {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  })
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  
  if (newStatus === 1 && row.stock <= 0) {
    ElMessage.warning('库存不足，无法上架')
    return
  }
  
  try {
    await request.post('/dish/save', {
      ...row,
      status: newStatus
    })
    ElMessage.success(`${action}成功`)
    loadDishes()
  } catch (error) {
    console.error(`${action}失败`, error)
    ElMessage.error(`${action}失败`)
  }
}

const handleSelectionChange = (selection) => {
  selectedDishes.value = selection
}

const handleBatchShelf = async (status) => {
  if (selectedDishes.value.length === 0) {
    ElMessage.warning('请选择要操作的菜品')
    return
  }
  
  const action = status === 1 ? '上架' : '下架'
  
  if (status === 1) {
    const invalidDishes = selectedDishes.value.filter(dish => dish.stock <= 0)
    if (invalidDishes.length > 0) {
      ElMessage.warning(`${invalidDishes.length}个菜品库存不足，无法上架`)
      return
    }
  }
  
  try {
    const promises = selectedDishes.value.map(dish => 
      request.post('/dish/save', { ...dish, status })
    )
    await Promise.all(promises)
    ElMessage.success(`成功${action}${selectedDishes.value.length}个菜品`)
    selectedDishes.value = []
    loadDishes()
  } catch (error) {
    console.error(`批量${action}失败`, error)
    ElMessage.error(`批量${action}失败`)
  }
}

const handleBatchDelete = async () => {
  if (selectedDishes.value.length === 0) {
    ElMessage.warning('请选择要删除的菜品')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的${selectedDishes.value.length}个菜品吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const promises = selectedDishes.value.map(dish => 
        request.post('/dish/delete', { id: dish.id })
      )
      await Promise.all(promises)
      ElMessage.success(`成功删除${selectedDishes.value.length}个菜品`)
      selectedDishes.value = []
      loadDishes()
    } catch (error) {
      console.error('批量删除失败', error)
      ElMessage.error('批量删除失败')
    }
  })
}

const handleImageChange = (urls) => {
  form.imageUrls = urls
}

const handlePriceBlur = () => {
  if (form.price !== '' && form.price !== null) {
    form.price = parseShortcutInput(form.price)
  }
}

const handleOriginalPriceBlur = () => {
  if (form.originalPrice !== '' && form.originalPrice !== null) {
    form.originalPrice = parseShortcutInput(form.originalPrice)
  }
}

const handleStockBlur = () => {
  if (form.stock !== '' && form.stock !== null) {
    form.stock = parseShortcutInput(form.stock)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          ...form,
          imageUrl: form.imageUrls.length > 0 ? form.imageUrls[0] : null
        }
        
        if (form.id) {
          await request.post('/dish/save', submitData)
          ElMessage.success('更新成功')
        } else {
          await request.post('/dish/save', submitData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadDishes()
      } catch (error) {
        console.error('提交失败', error)
        ElMessage.error('提交失败')
      }
    }
  })
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    id: null,
    merchantId: null,
    name: '',
    category: '',
    price: 0,
    originalPrice: null,
    stock: 999,
    status: 1,
    imageUrls: [],
    description: ''
  })
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  loadMerchants()
  loadDishes()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-bar {
  display: flex;
  margin-bottom: 20px;
}

.no-image {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 8px;
  font-size: 12px;
  color: #999;
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 0;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-top: 20px;
}

.batch-actions span {
  color: #606266;
  font-size: 14px;
}

.dish-detail {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
}

.gallery-item {
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.cover-badge {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.dish-detail p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.table-image {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.table-image .el-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.table-image .no-image {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
  color: #999;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
}

.status-tag {
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.status-tag.online {
  color: #52C41A;
  background-color: #F6FFED;
  border: 1px solid #52C41A;
}

.status-tag.offline {
  color: #86909C;
  background-color: #F2F3F5;
  border: 1px solid #C9CDD4;
}
</style>
