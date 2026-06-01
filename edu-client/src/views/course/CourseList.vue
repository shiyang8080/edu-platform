<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button v-if="role==='ADMIN'||role==='TEACHER'" type="primary" @click="showAddDialog">新增课程</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="fetchData">
        <el-tab-pane label="全部课程" name="all" />
        <el-tab-pane v-if="role==='TEACHER'||role==='ADMIN'" label="我的授课" name="myTeach" />
      </el-tabs>

      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="课程名称">
          <el-input v-model="query.name" placeholder="输入课程名搜索" clearable style="width:180px" />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="query.semester" placeholder="如 2025-2026-1" clearable style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="query.name=''; query.semester=''; fetchData()">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading" style="width:100%">
        <el-table-column prop="code" label="课程编号" width="120" />
        <el-table-column prop="name" label="课程名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="teacherName" label="授课教师" width="100" />
        <el-table-column prop="credit" label="学分" width="65" align="center" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="选课情况" width="100" align="center">
          <template #default="{row}">{{row.enrolled}}/{{row.capacity}}</template>
        </el-table-column>
        <el-table-column prop="classroom" label="教室" width="110" show-overflow-tooltip />
        <el-table-column label="状态" width="85" align="center">
          <template #default="{row}">
            <el-tag :type="statusTag(row.status)" size="small">{{statusLabel(row.status)}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="$router.push('/courses/detail/'+row.id)">详情</el-button>

            <template v-if="role==='STUDENT'">
              <el-button size="small" type="success" @click="handleSelect(row)">选课</el-button>
            </template>

            <template v-if="role==='TEACHER'||role==='ADMIN'">
              <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
              <el-button size="small" type="warning" @click="$router.push('/grades/course/'+row.id)">成绩</el-button>
              <el-button v-if="role==='ADMIN'" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page" v-model:page-size="query.size"
          :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
          @current-change="fetchData" @size-change="fetchData" />
      </div>
    </el-card>

    <!-- 新增/编辑课程弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="课程编号"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="课程名称"><el-input v-model="form.name" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学分"><el-input-number v-model="form.credit" :min="0" :max="10" :step="0.5" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课时"><el-input-number v-model="form.classHours" :min="0" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="学期"><el-input v-model="form.semester" placeholder="2025-2026-1" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="0" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教室"><el-input v-model="form.classroom" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="上课时间"><el-input v-model="form.schedule" placeholder="如 周一1-2节,周三3-4节" /></el-form-item>
        <el-form-item label="课程简介"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getCourseList, getMyTeachCourses, createCourse, updateCourse, deleteCourse } from '@/api/course'
import { selectCourse } from '@/api/selection'
import { getRole } from '@/utils/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const role = computed(() => getRole())
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const activeTab = ref('all')
const query = reactive({ page: 1, size: 10, name: '', semester: '' })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const form = reactive({ id: null, code: '', name: '', credit: 0, semester: '', classHours: 0, capacity: 0, classroom: '', schedule: '', description: '' })

function statusTag(s) { const map = { 0: 'danger', 1: 'success', 2: 'info' }; return map[s] || 'info' }
function statusLabel(s) { const map = { 0: '停开', 1: '开设中', 2: '已结课' }; return map[s] || '未知' }

async function fetchData() {
  loading.value = true
  try {
    let res
    if (activeTab.value === 'myTeach') {
      res = await getMyTeachCourses(query)
    } else {
      res = await getCourseList({ ...query, status: 1 })
    }
    const data = res.data
    tableData.value = data.records
    total.value = data.total
  } catch (e) {
    ElMessage.error('加载课程失败: ' + (e.message || '未知错误'))
  } finally { loading.value = false }
}

function showAddDialog() {
  dialogTitle.value = '新增课程'
  isEdit.value = false
  Object.assign(form, { id: null, code: '', name: '', credit: 0, semester: '', classHours: 0, capacity: 0, classroom: '', schedule: '', description: '' })
  dialogVisible.value = true
}

function showEditDialog(row) {
  dialogTitle.value = '编辑课程'
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.name) { ElMessage.warning('请输入课程名称'); return }
  try {
    if (isEdit.value) {
      await updateCourse(form.id, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createCourse({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || '未知错误'))
  }
}

async function handleSelect(row) {
  try {
    await ElMessageBox.confirm('确定选修课程"' + row.name + '"？', '选课确认', { type: 'info' })
    await selectCourse(row.id)
    ElMessage.success('选课成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('选课失败: ' + (e.message || '未知错误'))
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除课程"' + row.name + '"？此操作不可恢复！', '删除确认', { type: 'warning' })
    await deleteCourse(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败: ' + (e.message || '未知错误'))
  }
}

onMounted(() => {
  const tab = router.currentRoute.value.query.tab
  if (tab === 'myTeach' || tab === 'grades') {
    activeTab.value = 'myTeach'
  }
  fetchData()
})
</script>

<style scoped>
.page { width: 100%; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-form { margin-bottom: 6px; }
.filter-form .el-form-item { margin-bottom: 0; }
.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
