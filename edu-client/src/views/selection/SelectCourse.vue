<template>
  <div class="page">
    <el-card>
      <template #header><span>可选课程</span></template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="code" label="课程编号" width="110" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacherName" label="授课教师" width="100" />
        <el-table-column prop="credit" label="学分" width="60" />
        <el-table-column prop="semester" label="学期" width="110" />
        <el-table-column label="选课情况" width="100">
          <template #default="{row}">{{row.enrolled}}/{{row.capacity}}</template>
        </el-table-column>
        <el-table-column prop="classroom" label="教室" width="100" />
        <el-table-column prop="schedule" label="上课时间" />
        <el-table-column label="操作" width="100">
          <template #default="{row}">
            <el-button size="small" type="success" :disabled="row.enrolled >= row.capacity" @click="handleSelect(row)">
              {{ row.enrolled >= row.capacity ? '已满' : '选课' }}
            </el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCourseList } from '@/api/course'
import { selectCourse } from '@/api/selection'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, status: 1 })

async function fetchData() {
  loading.value = true
  try {
    const res = await getCourseList(query)
    const data = res.data
    tableData.value = data.records
    total.value = data.total
  } finally { loading.value = false }
}

async function handleSelect(row) {
  await ElMessageBox.confirm('确定选修"' + row.name + '"？', '选课确认', { type: 'info' })
  try {
    await selectCourse(row.id)
    ElMessage.success('选课成功')
    fetchData()
  } catch (e) {}
}

onMounted(fetchData)
</script>
