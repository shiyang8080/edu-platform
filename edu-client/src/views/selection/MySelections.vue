<template>
  <div class="page">
    <el-card>
      <template #header><span>我的选课</span></template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="code" label="课程编号" width="110" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacherName" label="授课教师" width="100" />
        <el-table-column prop="credit" label="学分" width="60" />
        <el-table-column prop="semester" label="学期" width="110" />
        <el-table-column prop="classroom" label="教室" width="100" />
        <el-table-column prop="schedule" label="上课时间" />
        <el-table-column label="操作" width="100">
          <template #default="{row}">
            <el-button size="small" type="danger" @click="handleDrop(row)">退课</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMySelections } from '@/api/selection'
import { dropCourse } from '@/api/selection'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])

async function fetchData() {
  loading.value = true
  try {
    const res = await getMySelections()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

async function handleDrop(row) {
  await ElMessageBox.confirm('确定退选课程"' + row.name + '"？', '退课确认', { type: 'warning' })
  try {
    await dropCourse(row.id)
    ElMessage.success('退课成功')
    fetchData()
  } catch (e) {}
}

onMounted(fetchData)
</script>
