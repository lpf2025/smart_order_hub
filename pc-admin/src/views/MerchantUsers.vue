<template>
  <div class="merchant-users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商家用户管理</span>
          <el-select v-model="selectedMerchantId" placeholder="选择商户" clearable style="width: 200px" @change="loadMerchantUsers">
            <el-option v-for="merchant in merchants" :key="merchant.id" :label="merchant.name" :value="merchant.id" />
          </el-select>
        </div>
      </template>

      <el-table :data="merchantUsers" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="wxNickname" label="微信昵称" width="150" />
        <el-table-column prop="mobile" label="手机号" width="120" />
        <el-table-column prop="merchantId" label="绑定商户" width="120">
          <template #default="scope">
            {{ getMerchantName(scope.row.merchantId) }}
          </template>
        </el-table-column>
        <el-table-column label="发货权限" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.hasDeliveryPerm" :active-value="1" :inactive-value="0" @change="handleDeliveryPermChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadMerchantUsers"
        @current-change="loadMerchantUsers"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="editDialogVisible" title="编辑商家用户" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="微信昵称">
          <el-input v-model="editForm.wxNickname" disabled />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.mobile" />
        </el-form-item>
        <el-form-item label="绑定商户">
          <el-select v-model="editForm.merchantId" placeholder="选择商户" style="width: 100%">
            <el-option v-for="merchant in merchants" :key="merchant.id" :label="merchant.name" :value="merchant.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const merchantUsers = ref([])
const merchants = ref([])
const selectedMerchantId = ref(null)
const editDialogVisible = ref(false)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const editForm = reactive({
  id: null,
  wxNickname: '',
  mobile: '',
  merchantId: null
})

const loadMerchants = async () => {
  try {
    const data = await request.post('/merchant/list')
    merchants.value = data || []
  } catch (error) {
    console.error('加载商户列表失败', error)
  }
}

const loadMerchantUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (selectedMerchantId.value) {
      params.merchantId = selectedMerchantId.value
    }
    const response = await request.get('/admin/merchant/user/list', { params })
    merchantUsers.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载商家用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const getMerchantName = (merchantId) => {
  const merchant = merchants.value.find(m => m.id === merchantId)
  return merchant ? merchant.name : '未知'
}

const handleDeliveryPermChange = async (row) => {
  try {
    await request.post('/admin/merchant/user/delivery/perm/config', {
      userId: row.id,
      hasDeliveryPerm: row.hasDeliveryPerm
    })
    ElMessage.success('权限配置成功')
  } catch (error) {
    console.error('权限配置失败', error)
    ElMessage.error('权限配置失败')
    row.hasDeliveryPerm = row.hasDeliveryPerm === 1 ? 0 : 1
  }
}

const handleEdit = (row) => {
  editForm.id = row.id
  editForm.wxNickname = row.wxNickname
  editForm.mobile = row.mobile
  editForm.merchantId = row.merchantId
  editDialogVisible.value = true
}

const handleSaveEdit = async () => {
  try {
    await request.post('/admin/merchant/user/update', editForm)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadMerchantUsers()
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadMerchants()
  loadMerchantUsers()
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
