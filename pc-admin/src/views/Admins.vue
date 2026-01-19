<template>
  <div class="admins">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员管理</span>
          <el-button type="primary" @click="handleAddAdmin">新增管理员</el-button>
        </div>
      </template>

      <el-table :data="admins" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="mobile" label="手机号" width="150" />
        <el-table-column label="角色" width="200">
          <template #default="scope">
            <el-tag v-for="role in scope.row.roles" :key="role.roleId" style="margin-right: 5px">
              {{ role.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEditAdmin(scope.row)">编辑</el-button>
            <el-button type="primary" link @click="handleEditRoles(scope.row)">配置角色</el-button>
            <el-button type="danger" link @click="handleDeleteAdmin(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="adminDialogVisible"
      :title="adminForm.id ? '编辑管理员' : '新增管理员'"
      width="500px"
      @close="resetAdminForm"
    >
      <el-form :model="adminForm" :rules="adminRules" ref="adminFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="adminForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="adminForm.mobile" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="adminDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAdmin" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="roleDialogVisible"
      :title="`${currentAdmin?.username} - 角色配置`"
      width="400px"
      @close="resetRoleForm"
    >
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in allRoles" :key="role.roleId" :label="role.roleId">
          {{ role.roleName }}
        </el-checkbox>
      </el-checkbox-group>

      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const admins = ref([])
const adminDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const currentAdmin = ref(null)
const adminFormRef = ref(null)
const saving = ref(false)
const allRoles = ref([])
const selectedRoleIds = ref([])

const adminForm = reactive({
  id: null,
  username: '',
  mobile: ''
})

const adminRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const loadAdmins = async () => {
  loading.value = true
  try {
    const data = await request.get('/admin/list')
    admins.value = data
  } catch (error) {
    console.error('加载管理员列表失败', error)
  } finally {
    loading.value = false
  }
}

const loadRoles = async () => {
  try {
    const data = await request.get('/admin/role/list')
    allRoles.value = data
  } catch (error) {
    console.error('加载角色列表失败', error)
  }
}

const handleAddAdmin = () => {
  adminForm.id = null
  adminForm.username = ''
  adminForm.mobile = ''
  adminDialogVisible.value = true
}

const handleEditAdmin = (row) => {
  adminForm.id = row.id
  adminForm.username = row.username
  adminForm.mobile = row.mobile
  adminDialogVisible.value = true
}

const handleSaveAdmin = async () => {
  if (!adminFormRef.value) return
  
  await adminFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        if (adminForm.id) {
          await request.post('/admin/update', adminForm)
          ElMessage.success('管理员更新成功')
        } else {
          await request.post('/admin/add', adminForm)
          ElMessage.success('管理员添加成功')
        }
        adminDialogVisible.value = false
        loadAdmins()
      } catch (error) {
        console.error('保存管理员失败', error)
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDeleteAdmin = (row) => {
  ElMessageBox.confirm('确定要删除该管理员吗？删除后不可恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post('/admin/delete', { id: row.id })
      ElMessage.success('管理员删除成功')
      loadAdmins()
    } catch (error) {
      console.error('删除管理员失败', error)
    }
  }).catch(() => {})
}

const handleEditRoles = async (row) => {
  currentAdmin.value = row
  selectedRoleIds.value = row.roles.map(r => r.roleId)
  roleDialogVisible.value = true
}

const handleSaveRoles = async () => {
  saving.value = true
  try {
    await request.post('/admin/role/save', {
      adminId: currentAdmin.value.id,
      roleIds: selectedRoleIds.value
    })
    ElMessage.success('角色配置成功')
    roleDialogVisible.value = false
    loadAdmins()
  } catch (error) {
    console.error('保存角色失败', error)
  } finally {
    saving.value = false
  }
}

const resetAdminForm = () => {
  adminForm.id = null
  adminForm.username = ''
  adminForm.mobile = ''
}

const resetRoleForm = () => {
  selectedRoleIds.value = []
  currentAdmin.value = null
}

onMounted(() => {
  loadAdmins()
  loadRoles()
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