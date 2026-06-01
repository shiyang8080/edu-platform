<template>
  <div class="login-page">
    <!-- 背景装饰圆 -->
    <div class="bg-circle bg-circle-1" />
    <div class="bg-circle bg-circle-2" />
    <div class="bg-circle bg-circle-3" />

    <div class="login-card">
      <div class="card-header-area">
        <div class="logo-icon">
          <el-icon :size="36"><School /></el-icon>
        </div>
        <h1 class="title">工商综合教学管理</h1>
        <p class="subtitle">TEACHING MANAGEMENT PLATFORM</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            class="custom-input"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="card-footer">
        <span>还没有账号？</span>
        <router-link to="/register" class="link">立即注册</router-link>
      </div>

      <div class="test-accounts">
        <el-collapse>
          <el-collapse-item title="测试账号（点击展开）" name="1">
            <div class="account-row"><span class="role admin">管理员</span> admin / 123456</div>
            <div class="account-row"><span class="role teacher">教师</span> teacher1 / 123456</div>
            <div class="account-row"><span class="role student">学生</span> student1 / 123456</div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const data = await userStore.login(form)
    ElMessage.success('欢迎回来，' + data.realName)
    router.push('/dashboard')
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

/* 背景装饰 */
.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.06);
  animation: float 20s ease-in-out infinite;
}
.bg-circle-1 {
  width: 500px; height: 500px;
  top: -15%; left: -10%;
  animation-delay: 0s;
}
.bg-circle-2 {
  width: 350px; height: 350px;
  bottom: -10%; right: -5%;
  animation-delay: -7s;
}
.bg-circle-3 {
  width: 200px; height: 200px;
  top: 40%; right: 15%;
  animation-delay: -14s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

/* 登录卡片 */
.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  padding: 40px 40px 24px;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(16px);
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0,0,0,0.12), 0 2px 8px rgba(0,0,0,0.06);
  animation: cardIn 0.6s ease-out;
}

@keyframes cardIn {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-header-area {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  width: 64px; height: 64px;
  margin: 0 auto 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 2px;
  margin: 0 0 6px;
}

.subtitle {
  font-size: 11px;
  color: #9ca3af;
  letter-spacing: 2px;
  margin: 0;
}

/* 输入框 */
.custom-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e5e7eb inset;
  transition: all 0.3s;
  padding: 0 12px;
}
.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c4b5fd inset;
}
.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102,126,234,0.3) inset;
}
.custom-input :deep(.el-input__inner) {
  height: 44px;
  font-size: 15px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 6px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  transition: all 0.3s;
}
.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(102,126,234,0.4);
}
.login-btn:active {
  transform: translateY(0);
}

.card-footer {
  text-align: center;
  font-size: 14px;
  color: #9ca3af;
}
.link {
  color: #667eea;
  margin-left: 4px;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s;
}
.link:hover { color: #764ba2; }

/* 测试账号 */
.test-accounts {
  margin-top: 20px;
  font-size: 13px;
}
.test-accounts :deep(.el-collapse-item__header) {
  font-size: 12px;
  color: #d1d5db;
  justify-content: center;
  border: none;
  background: transparent;
}
.test-accounts :deep(.el-collapse-item__wrap) {
  background: #f9fafb;
  border-radius: 8px;
  border: none;
}
.account-row {
  padding: 4px 0;
  color: #6b7280;
  font-size: 13px;
}
.role {
  display: inline-block;
  width: 42px;
  text-align: center;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  margin-right: 6px;
  color: #fff;
}
.role.admin { background: #ef4444; }
.role.teacher { background: #f59e0b; }
.role.student { background: #3b82f6; }
</style>
