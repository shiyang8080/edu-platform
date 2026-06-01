<template>
  <div class="page">
    <el-card>
      <template #header><span>成绩统计</span></template>
      <el-row :gutter="20" v-if="stats">
        <el-col :span="6"><el-card shadow="hover"><div class="stat-label">课程总数</div><div class="stat-val">{{stats.totalCourses}}</div></el-card></el-col>
        <el-col :span="6"><el-card shadow="hover"><div class="stat-label">平均分</div><div class="stat-val">{{stats.averageScore}}</div></el-card></el-col>
        <el-col :span="6"><el-card shadow="hover"><div class="stat-label">平均绩点</div><div class="stat-val">{{stats.averageGpa}}</div></el-card></el-col>
        <el-col :span="6"><el-card shadow="hover"><div class="stat-label">通过/挂科</div><div class="stat-val">{{stats.passedCourses}}/{{stats.failedCourses}}</div></el-card></el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '@/api/grade'
import { getUser } from '@/utils/auth'

const stats = ref(null)
const user = getUser()

onMounted(async () => {
  if (user) {
    const res = await getStatistics(user.id)
    stats.value = res.data
  }
})
</script>

<style scoped>
.stat-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-val { font-size: 28px; font-weight: bold; color: #303133; }
</style>
