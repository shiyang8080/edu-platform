<template>
  <div class="page">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-button text @click="$router.back()"><el-icon><ArrowLeft /></el-icon>返回</el-button>
        </div>
      </template>
      <template v-if="course">
        <h2>{{ course.name }}</h2>
        <el-descriptions :column="2" border style="margin-top:20px">
          <el-descriptions-item label="课程编号">{{ course.code }}</el-descriptions-item>
          <el-descriptions-item label="授课教师">{{ course.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="学    分">{{ course.credit }}</el-descriptions-item>
          <el-descriptions-item label="学    期">{{ course.semester }}</el-descriptions-item>
          <el-descriptions-item label="课    时">{{ course.classHours }}</el-descriptions-item>
          <el-descriptions-item label="教    室">{{ course.classroom }}</el-descriptions-item>
          <el-descriptions-item label="上课时间">{{ course.schedule }}</el-descriptions-item>
          <el-descriptions-item label="课程容量">{{ course.enrolled }}/{{ course.capacity }}</el-descriptions-item>
          <el-descriptions-item label="课程简介" :span="2">{{ course.description || '暂无简介' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourseDetail } from '@/api/course'

const route = useRoute()
const course = ref(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getCourseDetail(route.params.id)
    course.value = res.data
  } finally { loading.value = false }
})
</script>

<style scoped>
.page { width: 100%; }
.card-header { display: flex; align-items: center; }
</style>
