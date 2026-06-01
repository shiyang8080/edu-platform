<template>
  <div class="page">
    <el-card>
      <template #header><span>个人信息</span></template>
      <el-form :model="form" label-width="80px" style="max-width:500px" v-loading="loading">
        <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="角色">
          <el-tag>{{ roleLabel(form.role) }}</el-tag>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="newPassword" type="password" show-password placeholder="留空不修改" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo } from '@/api/auth'
import { updateUser } from '@/api/user'
import { setUser } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const form = ref({})
const newPassword = ref('')

function roleLabel(r) {
  return { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }[r] || r
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getUserInfo()
    form.value = res.data
  } finally { loading.value = false }
})

async function handleSave() {
  const data = { ...form.value }
  if (newPassword.value) data.password = newPassword.value
  if (!data.realName) { ElMessage.warning('请输入姓名'); return }
  try {
    await updateUser(data.id, data)
    setUser(data)
    ElMessage.success('保存成功')
  } catch (e) {}
}
</script>

<style scoped>
.page { width: 100%; }
</style>
