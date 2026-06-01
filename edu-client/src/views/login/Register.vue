<template>
  <div class="register-page">
    <div class="bg-circle bg-circle-1" />
    <div class="bg-circle bg-circle-2" />

    <div class="register-card">
      <div class="card-header-area">
        <div class="logo-icon">
          <el-icon :size="32"><School /></el-icon>
        </div>
        <h1 class="title">工商综合教学管理</h1>
        <p class="subtitle">学生注册</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" class="custom-input" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="realName">
              <el-input v-model="form.realName" placeholder="真实姓名" class="custom-input" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password class="custom-input" />
        </el-form-item>
        <el-form-item prop="confirmPwd">
          <el-input v-model="form.confirmPwd" type="password" placeholder="确认密码" show-password class="custom-input" />
        </el-form-item>
        <el-form-item prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio-button :label="1">男</el-radio-button>
            <el-radio-button :label="2">女</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item prop="phone">
              <el-input v-model="form.phone" placeholder="手机号" class="custom-input" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="email">
              <el-input v-model="form.email" placeholder="邮箱" class="custom-input" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="register-btn" @click="handleRegister">
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="card-footer">
        已有账号？<router-link to="/login" class="link">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { School } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '', password: '', confirmPwd: '', realName: '', gender: 1, phone: '', email: ''
})

const validateConfirmPwd = (_rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '长度3-50', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const { username, password, realName, gender, phone, email } = form
    await register({ username, password, realName, gender, phone, email })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.06);
  animation: float 20s ease-in-out infinite;
}
.bg-circle-1 { width: 420px; height: 420px; top: -10%; left: -8%; animation-delay: 0s; }
.bg-circle-2 { width: 300px; height: 300px; bottom: -8%; right: -5%; animation-delay: -10s; }

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(25px, -25px) scale(1.04); }
}

.register-card {
  position: relative;
  z-index: 1;
  width: 480px;
  padding: 36px 36px 24px;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(16px);
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0,0,0,0.12), 0 2px 8px rgba(0,0,0,0.06);
  animation: cardIn 0.5s ease-out;
}

@keyframes cardIn {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-header-area { text-align: center; margin-bottom: 24px; }
.logo-icon {
  width: 56px; height: 56px;
  margin: 0 auto 10px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 14px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}
.title { font-size: 20px; font-weight: 700; color: #1f2937; letter-spacing: 2px; margin: 0 0 4px; }
.subtitle { font-size: 12px; color: #9ca3af; margin: 0; }

.custom-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e5e7eb inset;
  transition: all 0.3s;
}
.custom-input :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px #c4b5fd inset; }
.custom-input :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px rgba(102,126,234,0.3) inset; }

.register-btn {
  width: 100%; height: 46px;
  font-size: 16px; font-weight: 600;
  letter-spacing: 6px; border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2); border: none;
  transition: all 0.3s;
}
.register-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(102,126,234,0.4); }

.card-footer { text-align: center; font-size: 14px; color: #9ca3af; }
.link { color: #667eea; margin-left: 4px; text-decoration: none; font-weight: 600; }
.link:hover { color: #764ba2; }
</style>
