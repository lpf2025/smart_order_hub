<template>
  <div class="merchants">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>门店管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加门店
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入门店名称"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">查询</el-button>
          </template>
        </el-input>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="merchants" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="Logo" width="100">
          <template #default="scope">
            <el-image
              v-if="scope.row.logoUrl"
              :src="scope.row.logoUrl"
              :preview-src-list="[scope.row.logoUrl]"
              style="width: 60px; height: 60px"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="门店名称" width="150" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="businessHours" label="营业时间" width="120" />
        <el-table-column prop="monthSales" label="月销量" width="100" align="right" />
        <el-table-column prop="perCapita" label="人均消费" width="100" align="right">
          <template #default="scope">
            ¥{{ scope.row.perCapita || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="80" align="center" />
        <el-table-column prop="minOrder" label="起送价" width="100" align="right">
          <template #default="scope">
            ¥{{ scope.row.minOrder || 20 }}
          </template>
        </el-table-column>
        <el-table-column prop="deliveryFee" label="配送费" width="100" align="right">
          <template #default="scope">
            ¥{{ scope.row.deliveryFee || 5 }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '营业中' : '休息中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleDetail(scope.row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadMerchants"
        @current-change="loadMerchants"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="resetForm"
    >
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="基础信息" name="basic">
          <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
            <el-form-item label="门店名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入门店名称" />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="门店地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入门店地址" />
            </el-form-item>
            <el-form-item label="营业时间" prop="businessHours">
              <el-input v-model="form.businessHours" placeholder="请输入营业时间，如：09:00-22:00" />
            </el-form-item>
            <el-form-item label="人均消费" prop="perCapita">
              <el-input-number v-model="form.perCapita" :min="0" :precision="2" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="起送价" prop="minOrder">
              <el-input-number v-model="form.minOrder" :min="0" :precision="2" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="配送费" prop="deliveryFee">
              <el-input-number v-model="form.deliveryFee" :min="0" :precision="2" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="配送方式" prop="serviceModes" required>
              <el-checkbox-group v-model="form.serviceModesArray">
                <el-checkbox :label="1">外卖</el-checkbox>
                <el-checkbox :label="2">自取</el-checkbox>
              </el-checkbox-group>
              <div class="form-tip">至少选择一种配送方式</div>
            </el-form-item>
            <el-form-item label="支持预约配送" prop="canReserve">
              <el-radio-group v-model="form.canReserve">
                <el-radio :label="1">支持</el-radio>
                <el-radio :label="0">不支持</el-radio>
              </el-radio-group>
              <div class="form-tip">关闭后，用户下单时将无法选择预约配送</div>
            </el-form-item>
            <el-form-item label="门店Logo" prop="logoUrl">
              <el-upload
                class="logo-uploader"
                :action="uploadUrl"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
                :before-upload="beforeUpload"
              >
                <img v-if="form.logoUrl" :src="form.logoUrl" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div class="upload-tip">建议尺寸：200x200像素，支持jpg/png格式</div>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">营业中</el-radio>
                <el-radio :label="2">休息中</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="支付配置" name="pay">
          <el-form :model="payConfigForm" label-width="120px">
            <el-form-item label="收款方式" required>
              <el-checkbox-group v-model="payConfigForm.supportPayTypesArray">
                <el-checkbox label="wechat">微信支付</el-checkbox>
                <el-checkbox label="alipay">支付宝支付</el-checkbox>
              </el-checkbox-group>
              <div class="form-tip">至少选择一种收款方式</div>
            </el-form-item>

            <el-collapse v-model="payConfigForm.activeCollapse">
              <el-collapse-item name="wechat" v-if="payConfigForm.supportPayTypesArray.includes('wechat')">
                <template #title>
                  <span style="font-weight: bold">微信支付配置</span>
                </template>
                <el-form-item label="商户号" required>
                  <el-input v-model="payConfigForm.wechatConfig.mchId" placeholder="请输入微信商户号" maxlength="32" />
                </el-form-item>
                <el-form-item label="应用ID" required>
                  <el-input v-model="payConfigForm.wechatConfig.appId" placeholder="请输入微信应用ID" maxlength="32" />
                </el-form-item>
                <el-form-item label="商户密钥" required>
                  <el-input v-model="payConfigForm.wechatConfig.apiKey" type="password" placeholder="请输入商户密钥" maxlength="64" show-password />
                </el-form-item>
                <el-form-item label="回调地址">
                  <el-input :value="wechatCallbackUrl" readonly />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleTestPayConfig('wechat')" :loading="testingPayConfig">
                    测试配置
                  </el-button>
                </el-form-item>
              </el-collapse-item>

              <el-collapse-item name="alipay" v-if="payConfigForm.supportPayTypesArray.includes('alipay')">
                <template #title>
                  <span style="font-weight: bold">支付宝支付配置</span>
                </template>
                <el-form-item label="商户PID" required>
                  <el-input v-model="payConfigForm.alipayConfig.pid" placeholder="请输入支付宝商户PID" maxlength="32" />
                </el-form-item>
                <el-form-item label="应用ID" required>
                  <el-input v-model="payConfigForm.alipayConfig.appId" placeholder="请输入支付宝应用ID" maxlength="32" />
                </el-form-item>
                <el-form-item label="应用私钥" required>
                  <el-input v-model="payConfigForm.alipayConfig.privateKey" type="textarea" :rows="4" placeholder="请输入应用私钥" />
                </el-form-item>
                <el-form-item label="支付宝公钥" required>
                  <el-input v-model="payConfigForm.alipayConfig.publicKey" type="textarea" :rows="4" placeholder="请输入支付宝公钥" />
                </el-form-item>
                <el-form-item label="回调地址">
                  <el-input :value="alipayCallbackUrl" readonly />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleTestPayConfig('alipay')" :loading="testingPayConfig">
                    测试配置
                  </el-button>
                </el-form-item>
              </el-collapse-item>
            </el-collapse>

            <el-form-item style="margin-top: 20px">
              <el-button type="primary" @click="handleSavePayConfig">保存配置</el-button>
              <el-button @click="handleResetPayConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="门店详情"
      width="700px"
    >
      <div class="merchant-detail" v-if="currentMerchant">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="门店ID">{{ currentMerchant.id }}</el-descriptions-item>
          <el-descriptions-item label="门店名称">{{ currentMerchant.name }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentMerchant.phone }}</el-descriptions-item>
          <el-descriptions-item label="门店地址" :span="2">{{ currentMerchant.address }}</el-descriptions-item>
          <el-descriptions-item label="营业时间">{{ currentMerchant.businessHours }}</el-descriptions-item>
          <el-descriptions-item label="月销量">{{ currentMerchant.monthSales || 0 }}</el-descriptions-item>
          <el-descriptions-item label="人均消费">¥{{ currentMerchant.perCapita || 0 }}</el-descriptions-item>
          <el-descriptions-item label="评分">{{ currentMerchant.rating || 5.0 }}</el-descriptions-item>
          <el-descriptions-item label="起送价">¥{{ currentMerchant.minOrder || 20 }}</el-descriptions-item>
          <el-descriptions-item label="配送费">¥{{ currentMerchant.deliveryFee || 5 }}</el-descriptions-item>
          <el-descriptions-item label="配送方式">
            <el-tag v-if="currentMerchant.serviceModes" type="success">
              {{ getServiceModesText(currentMerchant.serviceModes) }}
            </el-tag>
            <el-tag v-else type="info">未设置</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支持预约配送">
            <el-tag :type="currentMerchant.canReserve === 1 ? 'success' : 'info'">
              {{ currentMerchant.canReserve === 1 ? '支持' : '不支持' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentMerchant.status === 1 ? 'success' : 'danger'">
              {{ currentMerchant.status === 1 ? '营业中' : '休息中' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="detail-logo" v-if="currentMerchant.logoUrl">
          <el-image
            :src="currentMerchant.logoUrl"
            :preview-src-list="[currentMerchant.logoUrl]"
            fit="cover"
            style="width: 200px; height: 200px"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEditFromDetail">编辑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const merchants = ref([])
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('添加门店')
const formRef = ref(null)
const currentMerchant = ref(null)
const searchKeyword = ref('')
const activeTab = ref('basic')
const testingPayConfig = ref(false)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  phone: '',
  address: '',
  businessHours: '',
  logoUrl: '',
  status: 1,
  monthSales: 0,
  perCapita: 0,
  rating: 5.0,
  minOrder: 20,
  deliveryFee: 5,
  canReserve: 1,
  serviceModes: '1,2',
  serviceModesArray: [1, 2]
})

const payConfigForm = reactive({
  merchantId: null,
  supportPayTypesArray: ['wechat'],
  activeCollapse: [],
  wechatConfig: {
    mchId: '',
    appId: '',
    apiKey: ''
  },
  alipayConfig: {
    pid: '',
    appId: '',
    privateKey: '',
    publicKey: ''
  }
})

const uploadUrl = 'http://localhost:8081/api/upload/image'
const wechatCallbackUrl = 'https://域名/api/pay/wechat/callback'
const alipayCallbackUrl = 'https://域名/api/pay/alipay/callback'

const rules = {
  name: [{ required: true, message: '请输入门店名称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入门店地址', trigger: 'blur' }],
  serviceModes: [{ 
    type: 'array', 
    required: true, 
    message: '至少选择一种配送方式', 
    trigger: 'change',
    validator: (rule, value, callback) => {
      if (!value || value.length === 0) {
        callback(new Error('至少选择一种配送方式'))
      } else {
        callback()
      }
    }
  }]
}

const loadMerchants = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    if (searchKeyword.value) {
      params.name = searchKeyword.value
    }
    
    const data = await request.post('/merchant/list', params)
    merchants.value = data
    pagination.total = data.length
  } catch (error) {
    console.error('加载门店列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadMerchants()
}

const handleReset = () => {
  searchKeyword.value = ''
  pagination.page = 1
  loadMerchants()
}

const handleAdd = () => {
  dialogTitle.value = '添加门店'
  dialogVisible.value = true
}

const handleDetail = (row) => {
  currentMerchant.value = row
  detailDialogVisible.value = true
}

const handleEditFromDetail = () => {
  detailDialogVisible.value = false
  handleEdit(currentMerchant.value)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该门店吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post('/merchant/delete', { id: row.id })
      ElMessage.success('删除成功')
      loadMerchants()
    } catch (error) {
      console.error('删除失败', error)
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = { ...form }
        submitData.serviceModes = form.serviceModesArray.join(',')
        
        if (form.id) {
          await request.post('/merchant/save', submitData)
          ElMessage.success('更新成功')
        } else {
          await request.post('/merchant/save', submitData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadMerchants()
      } catch (error) {
        console.error('提交失败', error)
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
    name: '',
    phone: '',
    address: '',
    businessHours: '',
    logoUrl: '',
    status: 1,
    monthSales: 0,
    perCapita: 0,
    rating: 5.0,
    minOrder: 20,
    deliveryFee: 5,
    canReserve: 1,
    serviceModes: '1,2',
    serviceModesArray: [1, 2]
  })
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.logoUrl = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const getServiceModesText = (serviceModes) => {
  if (!serviceModes) return '未设置'
  const modes = serviceModes.split(',').map(Number)
  const text = []
  if (modes.includes(1)) text.push('外卖')
  if (modes.includes(2)) text.push('自取')
  return text.join('、') || '未设置'
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑门店'
  console.log('编辑门店数据:', row)
  console.log('logoUrl:', row.logoUrl)
  Object.assign(form, row)
  
  if (row.serviceModes) {
    form.serviceModesArray = row.serviceModes.split(',').map(Number)
  } else {
    form.serviceModesArray = [1, 2]
  }
  
  console.log('表单数据:', form)
  dialogVisible.value = true
  activeTab.value = 'basic'
  
  payConfigForm.merchantId = row.id
  await loadPayConfig(row.id)
}

const loadPayConfig = async (merchantId) => {
  try {
    const data = await request.get('/admin/pay/config/get', { merchantId })
    if (data) {
      payConfigForm.supportPayTypesArray = data.supportPayTypes ? data.supportPayTypes.split(',') : ['wechat']
      
      if (data.wechatConfig) {
        try {
          payConfigForm.wechatConfig = JSON.parse(data.wechatConfig)
        } catch (e) {
          payConfigForm.wechatConfig = { mchId: '', appId: '', apiKey: '' }
        }
      }
      
      if (data.alipayConfig) {
        try {
          payConfigForm.alipayConfig = JSON.parse(data.alipayConfig)
        } catch (e) {
          payConfigForm.alipayConfig = { pid: '', appId: '', privateKey: '', publicKey: '' }
        }
      }
    }
  } catch (error) {
    console.error('加载支付配置失败', error)
  }
}

const handleSavePayConfig = async () => {
  if (payConfigForm.supportPayTypesArray.length === 0) {
    ElMessage.error('至少选择一种收款方式')
    return
  }
  
  try {
    const submitData = {
      merchantId: payConfigForm.merchantId,
      supportPayTypes: payConfigForm.supportPayTypesArray.join(','),
      wechatConfig: payConfigForm.supportPayTypesArray.includes('wechat') 
        ? JSON.stringify(payConfigForm.wechatConfig) 
        : null,
      alipayConfig: payConfigForm.supportPayTypesArray.includes('alipay') 
        ? JSON.stringify(payConfigForm.alipayConfig) 
        : null
    }
    
    await request.post('/admin/pay/config/save', submitData)
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error('保存支付配置失败', error)
  }
}

const handleResetPayConfig = () => {
  payConfigForm.supportPayTypesArray = ['wechat']
  payConfigForm.wechatConfig = { mchId: '', appId: '', apiKey: '' }
  payConfigForm.alipayConfig = { pid: '', appId: '', privateKey: '', publicKey: '' }
}

const handleTestPayConfig = async (payType) => {
  testingPayConfig.value = true
  try {
    const data = await request.post('/admin/pay/config/test', {
      merchantId: payConfigForm.merchantId,
      payType: payType
    })
    
    if (data && data.msg) {
      ElMessage.success(data.msg)
    } else {
      ElMessage.success('配置有效，可正常收款')
    }
  } catch (error) {
    console.error('测试支付配置失败', error)
  } finally {
    testingPayConfig.value = false
  }
}

onMounted(() => {
  loadMerchants()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.logo-uploader {
  display: inline-block;
}

.logo-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.logo-uploader :deep(.el-upload:hover) {
  border-color: #409EFF;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.logo {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.merchant-detail {
  padding: 10px 0;
}

.detail-logo {
  margin-top: 20px;
  text-align: center;
}
</style>
