<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" size="small" @click="showAddDialog">新增用户</el-button>
        </div>
      </template>
      <el-form :inline="true" :model="query" size="small">
        <el-form-item label="用户名"><el-input v-model="query.username" clearable /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" clearable placeholder="全部角色" style="width:130px">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button></el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="role" label="角色" width="80">
          <template #default="{row}">
            <el-tag :type="roleTag(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':'danger'" size="small">{{row.status===1?'启用':'禁用'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button size="small" :type="row.status===1?'warning':'success'" @click="toggleStatus(row)">{{row.status===1?'禁用':'启用'}}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="query.page" v-model:page-size="query.size"
          :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
          @current-change="fetchData" @size-change="fetchData" />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password :placeholder="isEdit?'留空不修改':''" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" :disabled="isEdit" style="width:130px">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUserList, createUser, updateUser, deleteUser, updateUserStatus } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, username: '', role: '' })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const form = reactive({ id: null, username: '', password: '', realName: '', role: 'STUDENT', phone: '', email: '' })

function roleTag(role) {
  return { ADMIN: 'danger', TEACHER: 'warning', STUDENT: 'info' }[role] || 'info'
}
function roleLabel(role) {
  return { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }[role] || role
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList(query)
    const data = res.data
    tableData.value = data.records
    total.value = data.total
  } finally { loading.value = false }
}

function showAddDialog() {
  dialogTitle.value = '新增用户'
  isEdit.value = false
  Object.assign(form, { id: null, username: '', password: '', realName: '', role: 'STUDENT', phone: '', email: '' })
  dialogVisible.value = true
}

function showEditDialog(row) {
  dialogTitle.value = '编辑用户'
  isEdit.value = true
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.username || (!isEdit.value && !form.password) || !form.realName) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    if (isEdit.value) {
      await updateUser(form.id, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createUser({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {}
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
    fetchData()
  } catch (e) {}
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该用户？', '提示', { type: 'warning' })
  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {}
}

onMounted(fetchData)
</script>

<style scoped>
.page { width: 100%; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
