<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩录入 — {{ courseName }}</span>
          <el-button size="small" type="success" @click="handleExport">导出Excel</el-button>
        </div>
      </template>

      <!-- 提示信息 -->
      <el-alert v-if="tableData.length === 0 && !loading" title="该课程暂无选课学生" type="info" show-icon :closable="false" style="margin-bottom:16px" />
      <el-alert v-if="tableData.length > 0" title="成绩自动计算规则：平时×30% + 期中×30% + 期末×40%" type="info" :closable="false" style="margin-bottom:16px" />

      <el-table :data="tableData" border stripe v-loading="loading" v-if="tableData.length > 0">
        <el-table-column label="学号" width="80" prop="username" />
        <el-table-column label="姓名" width="100" prop="studentName" />
        <el-table-column label="平时成绩(30%)" width="130">
          <template #default="{row}">
            <el-input-number v-model="row.regularScore" :min="0" :max="100" :precision="1" size="small" controls-position="right" style="width:100px" @change="onScoreChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="期中成绩(30%)" width="130">
          <template #default="{row}">
            <el-input-number v-model="row.midtermScore" :min="0" :max="100" :precision="1" size="small" controls-position="right" style="width:100px" @change="onScoreChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="期末成绩(40%)" width="130">
          <template #default="{row}">
            <el-input-number v-model="row.finalScore" :min="0" :max="100" :precision="1" size="small" controls-position="right" style="width:100px" @change="onScoreChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="总评" width="80">
          <template #default="{row}">
            <b :style="{color: totalColor(row)}">{{ calcTotal(row) }}</b>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="70">
          <template #default="{row}">
            <el-tag :type="levelTag(calcLevel(row))" size="small">{{ calcLevel(row) || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" width="120">
          <template #default="{row}">
            <el-input v-model="row.remark" size="small" placeholder="可选备注" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="saveOne(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:16px" v-if="tableData.length > 0">
        <el-button type="success" :loading="savingAll" @click="saveAll">批量保存全部</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourseStudentGrades, saveGrade, updateGrade, batchSaveGrades, exportGrades } from '@/api/grade'
import { getCourseDetail } from '@/api/course'
import { ElMessage } from 'element-plus'

const route = useRoute()
const courseId = Number(route.params.id)
const courseName = ref('')
const loading = ref(false)
const savingAll = ref(false)
const tableData = ref([])

// ==== 计算函数 ====
function calcTotal(row) {
  const r = Number(row.regularScore) || 0
  const m = Number(row.midtermScore) || 0
  const f = Number(row.finalScore) || 0
  if (r === 0 && m === 0 && f === 0) return '-'
  return (r * 0.3 + m * 0.3 + f * 0.4).toFixed(1)
}

function calcLevel(row) {
  const t = parseFloat(calcTotal(row))
  if (isNaN(t)) return null
  if (t >= 90) return '优秀'
  if (t >= 80) return '良好'
  if (t >= 70) return '中等'
  if (t >= 60) return '及格'
  return '不及格'
}

function totalColor(row) {
  const l = calcLevel(row)
  if (!l) return '#303133'
  if (l === '不及格') return '#F56C6C'
  if (l === '优秀') return '#67C23A'
  return '#303133'
}

function levelTag(level) {
  const map = { '优秀': 'success', '良好': '', '中等': 'warning', '及格': 'warning', '不及格': 'danger' }
  return map[level] || 'info'
}

function onScoreChange(_row) {
  // 触发响应式更新（input-number 已双向绑定，仅用于标记变更）
}

// ==== 数据加载 ====
async function fetchData() {
  loading.value = true
  try {
    // 并行加载课程信息和学生成绩
    const [courseRes, gradesRes] = await Promise.all([
      getCourseDetail(courseId),
      getCourseStudentGrades(courseId)
    ])
    courseName.value = courseRes.data?.name || ''
    const list = gradesRes.data || []
    // 确保所有字段初始化为数字
    tableData.value = list.map(item => ({
      ...item,
      regularScore: item.regularScore != null ? Number(item.regularScore) : null,
      midtermScore: item.midtermScore != null ? Number(item.midtermScore) : null,
      finalScore: item.finalScore != null ? Number(item.finalScore) : null
    }))
  } catch (e) {
    ElMessage.error('加载数据失败: ' + (e.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// ==== 保存操作 ====
async function saveOne(row) {
  const dto = {
    studentId: row.studentId,
    courseId: courseId,
    regularScore: row.regularScore,
    midtermScore: row.midtermScore,
    finalScore: row.finalScore,
    remark: row.remark
  }
  try {
    if (row.gradeId) {
      await updateGrade(row.gradeId, dto)
    } else {
      await saveGrade(dto)
    }
    ElMessage.success('保存成功')
    // 刷新以获取后端计算的 totalScore 和 gradeId
    fetchData()
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || '未知错误'))
  }
}

async function saveAll() {
  savingAll.value = true
  const list = tableData.value.map(r => ({
    studentId: r.studentId,
    courseId: courseId,
    regularScore: r.regularScore,
    midtermScore: r.midtermScore,
    finalScore: r.finalScore,
    remark: r.remark
  }))
  try {
    await batchSaveGrades(list)
    ElMessage.success('批量保存成功')
    fetchData()
  } catch (e) {
    ElMessage.error('批量保存失败: ' + (e.message || '未知错误'))
  } finally {
    savingAll.value = false
  }
}

async function handleExport() {
  try {
    const blob = await exportGrades(courseId)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '成绩表.xlsx'
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败: ' + (e.message || '未知错误'))
  }
}

onMounted(fetchData)
</script>

<style scoped>
.page { width: 100%; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
