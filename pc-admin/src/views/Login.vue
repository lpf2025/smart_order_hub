<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>智能点餐系统</h1>
        <p>管理后台</p>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="mobile">
          <el-input
            v-model="loginForm.mobile"
            placeholder="请输入手机号"
            prefix-icon="User"
            maxlength="11"
          />
        </el-form-item>
        
        <el-form-item prop="code">
          <div class="code-input">
            <el-input
              v-model="loginForm.code"
              placeholder="请输入验证码"
              prefix-icon="Key"
              maxlength="6"
            />
            <el-button
              :disabled="countdown > 0"
              @click="handleSendCode"
              class="code-btn"
            >
              {{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item prop="remember">
          <el-checkbox v-model="loginForm.remember">记住密码（7天内免登录）</el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            @click="handleLogin"
            :loading="loading"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const countdown = ref(0)

const loginForm = reactive({
  mobile: '',
  code: '',
  remember: false
})

const rules = {
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码格式不正确', trigger: 'blur' }
  ]
}

const handleSendCode = async () => {
  if (!loginForm.mobile) {
    ElMessage.warning('请先输入手机号')
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(loginForm.mobile)) {
    ElMessage.warning('手机号格式不正确')
    return
  }
  
  try {
    await request.post('/admin/send-code', { mobile: loginForm.mobile })
    ElMessage.success('验证码已发送')
    
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error('发送验证码失败', error)
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const data = await request.post('/admin/login', {
          mobile: loginForm.mobile,
          code: loginForm.code
        })
        
        localStorage.setItem('adminToken', data.token)
        localStorage.setItem('userInfo', JSON.stringify(data.userInfo))
        localStorage.setItem('menuList', JSON.stringify(data.menuList))
        localStorage.setItem('buttonPermissions', JSON.stringify(data.buttonPermissions || []))
        
        if (loginForm.remember) {
          localStorage.setItem('rememberLogin', 'true')
        } else {
          localStorage.removeItem('rememberLogin')
        }
        
        ElMessage.success('登录成功')
        window.location.href = '/'
      } catch (error) {
        console.error('登录失败', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
}

.login-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.code-input {
  display: flex;
  gap: 10px;
}

.code-input .el-input {
  flex: 1;
}

.code-btn {
  width: 120px;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
}
</style>
