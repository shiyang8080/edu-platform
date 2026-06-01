<template>
  <div class="page">
    <el-card>
      <template #header><span>我的成绩</span></template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="courseName" label="课程" width="200" />
        <el-table-column prop="regularScore" label="平时成绩" width="100" />
        <el-table-column prop="midtermScore" label="期中成绩" width="100" />
        <el-table-column prop="finalScore" label="期末成绩" width="100" />
        <el-table-column prop="totalScore" label="总评成绩" width="100">
          <template #default="{row}"><b>{{ row.totalScore }}</b></template>
        </el-table-column>
        <el-table-column prop="gradePoint" label="绩点" width="80" />
        <el-table-column prop="gradeLevel" label="等级" width="80">
          <template #default="{row}">
            <el-tag :type="levelTag(row.gradeLevel)" size="small">{{ row.gradeLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyGrades } from '@/api/grade'

const loading = ref(false)
const tableData = ref([])

function levelTag(level) {
  return { '优秀': 'success', '良好': '', '中等': 'warning', '及格': 'warning', '不及格': 'danger' }[level] || 'info'
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getMyGrades()
    tableData.value = res.data || []
  } finally { loading.value = false }
})
</script>
