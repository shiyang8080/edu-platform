<template>
  <div class="page">
    <el-card v-loading="loading">
      <template #header>
        <el-button text @click="$router.back()"><el-icon><ArrowLeft /></el-icon>返回列表</el-button>
      </template>
      <template v-if="notice">
        <h2>{{ notice.title }}</h2>
        <div style="color:#909399;margin:12px 0;font-size:14px">
          <el-tag size="small">{{ typeLabel(notice.noticeType) }}</el-tag>
          <span style="margin-left:12px">发布时间: {{ notice.publishTime }}</span>
        </div>
        <el-divider />
        <div class="content" v-html="notice.content"></div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getNoticeDetail } from '@/api/notice'

const route = useRoute()
const notice = ref(null)
const loading = ref(false)

function typeLabel(t) {
  return { SYSTEM: '系统', COURSE: '课程', GRADE: '成绩', GENERAL: '一般' }[t] || t
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getNoticeDetail(route.params.id)
    notice.value = res.data
  } finally { loading.value = false }
})
</script>

<style scoped>
.page { width: 100%; }
.content { line-height: 1.8; font-size: 15px; }
</style>
