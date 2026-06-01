<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知公告</span>
          <el-button v-if="role==='ADMIN'" type="primary" size="small" @click="showAddDialog">发布公告</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading" @row-click="goDetail" style="cursor:pointer">
        <el-table-column prop="title" label="标题">
          <template #default="{row}">
            <el-badge :is-dot="!row.read" :hidden="row.read !== false">
              {{ row.title }}
            </el-badge>
          </template>
        </el-table-column>
        <el-table-column prop="noticeType" label="类型" width="90">
          <template #default="{row}">
            <el-tag size="small">{{ typeLabel(row.noticeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优先级" width="80">
          <template #default="{row}">
            <el-tag :type="priorityTag(row.priority)" size="small">{{ priorityLabel(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="100" v-if="role==='ADMIN'">
          <template #default="{row}">
            <el-button size="small" type="danger" @click.stop="handleDelete(row)">删除</el-button>
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

    <el-dialog title="发布公告" v-model="dialogVisible" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.noticeType">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="课程相关" value="COURSE" />
            <el-option label="成绩相关" value="GRADE" />
            <el-option label="一般公告" value="GENERAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-radio-group v-model="form.priority">
            <el-radio :label="0">普通</el-radio>
            <el-radio :label="1">重要</el-radio>
            <el-radio :label="2">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="5" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticeList, createNotice, deleteNotice } from '@/api/notice'
import { getRole } from '@/utils/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const role = computed(() => getRole())
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10 })
const dialogVisible = ref(false)
const form = reactive({ title: '', content: '', noticeType: 'GENERAL', priority: 0 })

function typeLabel(t) {
  return { SYSTEM: '系统', COURSE: '课程', GRADE: '成绩', GENERAL: '一般' }[t] || t
}
function priorityTag(p) {
  return { 0: '', 1: 'warning', 2: 'danger' }[p] || ''
}
function priorityLabel(p) {
  return { 0: '普通', 1: '重要', 2: '紧急' }[p] || '普通'
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getNoticeList(query)
    const data = res.data
    tableData.value = data.records
    total.value = data.total
  } finally { loading.value = false }
}

function goDetail(row) {
  router.push('/notices/detail/' + row.id)
}

function showAddDialog() {
  Object.assign(form, { title: '', content: '', noticeType: 'GENERAL', priority: 0 })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.title || !form.content) { ElMessage.warning('请填写标题和内容'); return }
  try {
    await createNotice({ ...form })
    ElMessage.success('发布成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {}
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  try {
    await deleteNotice(row.id)
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
