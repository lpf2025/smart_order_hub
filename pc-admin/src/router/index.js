import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/merchants',
    name: 'Merchants',
    component: () => import('@/views/Merchants.vue'),
    meta: { title: '门店管理', permCode: 'merchant:list' }
  },
  {
    path: '/dishes',
    name: 'Dishes',
    component: () => import('@/views/Dishes.vue'),
    meta: { title: '菜品管理', permCode: 'dish:list' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Orders.vue'),
    meta: { title: '订单管理', permCode: 'order:list' }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('@/views/Users.vue'),
    meta: { title: '客户管理', permCode: 'system:customer' }
  },
  {
    path: '/merchant-users',
    name: 'MerchantUsers',
    component: () => import('@/views/MerchantUsers.vue'),
    meta: { title: '商家用户管理', permCode: 'system:merchant:user' }
  },
  {
    path: '/roles',
    name: 'Roles',
    component: () => import('@/views/Roles.vue'),
    meta: { title: '角色管理', permCode: 'system:role' }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/views/Statistics.vue'),
    meta: { title: '数据统计', permCode: 'statistics:view' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('adminToken')
  
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
