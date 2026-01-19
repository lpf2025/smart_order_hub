<template>
  <el-container class="app-container">
    <el-aside v-if="userInfo" width="200px" class="sidebar">
      <div class="logo">
        <h2>智能点餐</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#ff4d4f"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item v-for="menu in menuList" :key="menu.permId" :index="getMenuPath(menu.permCode)">
          <el-icon><component :is="getMenuIcon(menu.permCode)" /></el-icon>
          <span>{{ menu.permName }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container :class="{ 'no-sidebar': !userInfo }">
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute">{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <template v-if="userInfo">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-icon><UserFilled /></el-icon>
                {{ userInfo?.username || '管理员' }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="personal">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-link type="primary" @click="goToLogin">登录</el-link>
          </template>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route.meta.title || '')
const menuList = ref([])
const userInfo = ref(null)

const loadUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    userInfo.value = JSON.parse(userInfoStr)
  }
}

const loadMenuList = () => {
  const menuListStr = localStorage.getItem('menuList')
  if (menuListStr) {
    const allMenus = JSON.parse(menuListStr)
    console.log('原始菜单数据:', allMenus)
    
    const flattenMenus = (menus) => {
      let result = []
      menus.forEach(menu => {
        if (menu.menuType === 1) {
          if (!menu.children || menu.children.length === 0) {
            result.push(menu)
          } else {
            result = result.concat(flattenMenus(menu.children))
          }
        }
      })
      return result
    }
    
    menuList.value = flattenMenus(allMenus)
    console.log('加载的菜单列表:', menuList.value)
    menuList.value.forEach(menu => {
      const path = getMenuPath(menu.permCode)
      console.log(`菜单: ${menu.permName}, 编码: ${menu.permCode}, 路径: ${path}`)
    })
  }
}

const loadButtonPermissions = () => {
  const buttonPermissionsStr = localStorage.getItem('buttonPermissions')
  if (buttonPermissionsStr) {
    const buttonPermissions = JSON.parse(buttonPermissionsStr)
    console.log('加载的按钮权限:', buttonPermissions)
    return buttonPermissions
  }
  return []
}

const getMenuPath = (permCode) => {
  const pathMap = {
    'system:role': '/roles',
    'system:merchant:user': '/merchant-users',
    'merchant:list': '/merchants',
    'dish:list': '/dishes',
    'order:list': '/orders',
    'system:customer': '/users',
    'statistics:view': '/statistics'
  }
  const path = pathMap[permCode]
  console.log('菜单编码:', permCode, '路径:', path)
  return path || '/'
}

const getMenuIcon = (permCode) => {
  const iconMap = {
    'system:role': 'Setting',
    'system:merchant:user': 'UserFilled',
    'merchant:list': 'Shop',
    'dish:list': 'Food',
    'order:list': 'Document',
    'system:customer': 'User',
    'statistics:view': 'TrendCharts'
  }
  return iconMap[permCode] || 'Menu'
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确认退出登录？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      const token = localStorage.getItem('adminToken')
      if (token) {
        try {
          await request.post('/admin/logout', null, {
            headers: {
              'Authorization': token
            }
          })
        } catch (error) {
          console.error('退出登录失败', error)
        }
      }
      
      localStorage.removeItem('adminToken')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('menuList')
      localStorage.removeItem('rememberLogin')
      localStorage.removeItem('buttonPermissions')
      
      ElMessage.success('退出成功')
      window.location.href = '/login'
    } catch (error) {
      console.error('取消退出', error)
    }
  } else if (command === 'personal') {
    ElMessage.info('个人中心功能开发中')
  }
}

const goToLogin = () => {
  window.location.href = '/login'
}

onMounted(() => {
  loadUserInfo()
  loadMenuList()
})
</script>

<style scoped>
.app-container {
  height: 100vh;
}

.sidebar {
  background-color: #001529;
}

.no-sidebar {
  width: 100%;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
