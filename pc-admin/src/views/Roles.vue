<template>
  <div class="roles">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAddRole">新增角色</el-button>
        </div>
      </template>

      <el-table :data="roles" style="width: 100%" v-loading="loading">
        <el-table-column prop="roleId" label="角色ID" width="100" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleDesc" label="角色描述" min-width="200" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEditRole(scope.row)">编辑</el-button>
            <el-button type="primary" link @click="handleEditPerm(scope.row)">配置权限</el-button>
            <el-button type="danger" link @click="handleDeleteRole(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="roleDialogVisible"
      :title="roleForm.roleId ? '编辑角色' : '新增角色'"
      width="500px"
      @close="resetRoleForm"
    >
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述" prop="roleDesc">
          <el-input v-model="roleForm.roleDesc" type="textarea" :rows="3" placeholder="请输入角色描述" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRole" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="permDialogVisible"
      :title="`${currentRole?.roleName} - 权限配置`"
      width="600px"
      @close="resetPermForm"
    >
      <el-tree
        ref="permTreeRef"
        :data="permTree"
        :props="treeProps"
        show-checkbox
        node-key="permId"
        :default-checked-keys="checkedPermIds"
        :default-expanded-keys="expandedPermIds"
        @check="handleCheckChange"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span>{{ data.permName }}</span>
            <el-tag v-if="data.menuType === 2" size="small" type="info">按钮</el-tag>
          </span>
        </template>
      </el-tree>

      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePerms" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const roles = ref([])
const permDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const currentRole = ref(null)
const permTreeRef = ref(null)
const roleFormRef = ref(null)
const saving = ref(false)
const checkedPermIds = ref([])
const expandedPermIds = ref([])

const treeProps = {
  children: 'children',
  label: 'permName'
}

const permTree = ref([])

const roleForm = reactive({
  roleId: null,
  roleName: '',
  roleDesc: ''
})

const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ]
}

const loadRoles = async () => {
  loading.value = true
  try {
    const data = await request.get('/admin/role/list')
    roles.value = data
  } catch (error) {
    console.error('加载角色列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleAddRole = () => {
  roleForm.roleId = null
  roleForm.roleName = ''
  roleForm.roleDesc = ''
  roleDialogVisible.value = true
}

const handleEditRole = (row) => {
  roleForm.roleId = row.roleId
  roleForm.roleName = row.roleName
  roleForm.roleDesc = row.roleDesc
  roleDialogVisible.value = true
}

const handleSaveRole = async () => {
  if (!roleFormRef.value) return
  
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        if (roleForm.roleId) {
          await request.post('/admin/role/update', roleForm)
          ElMessage.success('角色更新成功')
        } else {
          await request.post('/admin/role/add', roleForm)
          ElMessage.success('角色添加成功')
        }
        roleDialogVisible.value = false
        loadRoles()
      } catch (error) {
        console.error('保存角色失败', error)
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDeleteRole = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？删除后不可恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post('/admin/role/delete', { roleId: row.roleId })
      ElMessage.success('角色删除成功')
      loadRoles()
    } catch (error) {
      console.error('删除角色失败', error)
    }
  }).catch(() => {})
}

const handleEditPerm = async (row) => {
  currentRole.value = row
  permDialogVisible.value = true
  
  try {
    const data = await request.get('/admin/role/perm/get', { params: { roleId: row.roleId } })
    const menuPerms = data.filter(p => p.menuType === 1)
    permTree.value = buildTree(menuPerms)
    
    checkedPermIds.value = getCheckedPermIds(menuPerms)
    expandedPermIds.value = [1, 2, 3, 4, 5, 6]
  } catch (error) {
    console.error('加载权限列表失败', error)
  }
}

const buildTree = (perms) => {
  const map = {}
  const roots = []
  
  perms.forEach(perm => {
    map[perm.permId] = { ...perm, children: [] }
  })
  
  perms.forEach(perm => {
    if (perm.parentId === 0) {
      roots.push(map[perm.permId])
    } else if (map[perm.parentId]) {
      map[perm.parentId].children.push(map[perm.permId])
    }
  })
  
  return roots
}

const getCheckedPermIds = (perms) => {
  return perms.filter(p => p.checked).map(p => p.permId)
}

const handleCheckChange = (data, checkedInfo) => {
  const checkedKeys = checkedInfo.checkedKeys
  checkedPermIds.value = checkedKeys
}

const handleSavePerms = async () => {
  if (!permTreeRef.value) return
  
  const checkedKeys = permTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = permTreeRef.value.getHalfCheckedKeys()
  const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
  
  saving.value = true
  try {
    await request.post('/admin/role/perm/save', {
      roleId: currentRole.value.roleId,
      permIds: allCheckedKeys
    })
    ElMessage.success('权限保存成功')
    permDialogVisible.value = false
  } catch (error) {
    console.error('保存权限失败', error)
  } finally {
    saving.value = false
  }
}

const resetPermForm = () => {
  checkedPermIds.value = []
  expandedPermIds.value = []
  currentRole.value = null
}

const resetRoleForm = () => {
  roleForm.roleId = null
  roleForm.roleName = ''
  roleForm.roleDesc = ''
}

onMounted(() => {
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

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tree-node .el-tag {
  margin-left: 8px;
}
</style>
