<template>
  <div class="dashboard">
    <div class="welcome-bar">
      <div>
        <h2 class="greeting">欢迎回来，{{ userName }}</h2>
        <p class="greeting-sub">{{ roleLabel }} · 祝您工作愉快</p>
      </div>
      <div class="date-badge">{{ today }}</div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="(item, idx) in statCards" :key="item.title">
        <div class="stat-card" :style="{ animationDelay: idx * 0.08 + 's' }">
          <div class="stat-icon" :style="{ background: item.gradient }">
            <el-icon :size="24"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-title">{{ item.title }}</div>
          </div>
          <div class="stat-bg" :style="{ background: item.gradient }" />
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-card" shadow="never">
      <template #header><span class="card-label">快捷操作</span></template>
      <div class="quick-actions">
        <template v-if="role === 'ADMIN'">
          <div class="action-item" @click="$router.push('/users/list')">
            <el-icon :size="20"><UserFilled /></el-icon><span>用户管理</span>
          </div>
          <div class="action-item" @click="$router.push('/courses/list')">
            <el-icon :size="20"><Reading /></el-icon><span>课程管理</span>
          </div>
          <div class="action-item" @click="$router.push('/notices/list')">
            <el-icon :size="20"><Bell /></el-icon><span>发布公告</span>
          </div>
        </template>
        <template v-if="role === 'TEACHER'">
          <div class="action-item" @click="$router.push('/courses/list')">
            <el-icon :size="20"><Reading /></el-icon><span>我的课程</span>
          </div>
          <div class="action-item" @click="$router.push('/courses/list?tab=grades')">
            <el-icon :size="20"><TrophyBase /></el-icon><span>成绩录入</span>
          </div>
        </template>
        <template v-if="role === 'STUDENT'">
          <div class="action-item" @click="$router.push('/selections/available')">
            <el-icon :size="20"><Select /></el-icon><span>我要选课</span>
          </div>
          <div class="action-item" @click="$router.push('/grades/my')">
            <el-icon :size="20"><TrendCharts /></el-icon><span>查看成绩</span>
          </div>
        </template>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getRole, getUser } from '@/utils/auth'

const role = computed(() => getRole())
const user = getUser()
const userName = user?.realName || user?.username || '用户'

const today = new Date().toLocaleDateString('zh-CN', { year:'numeric', month:'long', day:'numeric', weekday:'long' })

const roleLabelMap = { ADMIN: '系统管理员', TEACHER: '教师', STUDENT: '学生' }
const roleLabel = computed(() => roleLabelMap[role.value] || '')

const allStatCards = {
  ADMIN: [
    { title: '系统用户', value: '-', icon: 'User', gradient: 'linear-gradient(135deg, #667eea, #764ba2)' },
    { title: '开设课程', value: '-', icon: 'Reading', gradient: 'linear-gradient(135deg, #11998e, #38ef7d)' },
    { title: '未读通知', value: '-', icon: 'Bell', gradient: 'linear-gradient(135deg, #f093fb, #f5576c)' },
    { title: '系统状态', value: '正常', icon: 'Monitor', gradient: 'linear-gradient(135deg, #4facfe, #00f2fe)' }
  ],
  TEACHER: [
    { title: '我的课程', value: '-', icon: 'Reading', gradient: 'linear-gradient(135deg, #667eea, #764ba2)' },
    { title: '授课学生', value: '-', icon: 'User', gradient: 'linear-gradient(135deg, #11998e, #38ef7d)' },
    { title: '待批成绩', value: '-', icon: 'TrophyBase', gradient: 'linear-gradient(135deg, #f093fb, #f5576c)' },
    { title: '未读通知', value: '-', icon: 'Bell', gradient: 'linear-gradient(135deg, #4facfe, #00f2fe)' }
  ],
  STUDENT: [
    { title: '已选课程', value: '-', icon: 'Select', gradient: 'linear-gradient(135deg, #667eea, #764ba2)' },
    { title: '平均绩点', value: '-', icon: 'TrendCharts', gradient: 'linear-gradient(135deg, #11998e, #38ef7d)' },
    { title: '通过课程', value: '-', icon: 'CircleCheck', gradient: 'linear-gradient(135deg, #f093fb, #f5576c)' },
    { title: '未读通知', value: '-', icon: 'Bell', gradient: 'linear-gradient(135deg, #4facfe, #00f2fe)' }
  ]
}

const statCards = computed(() => allStatCards[role.value] || allStatCards.STUDENT)
</script>

<style scoped>
.dashboard { max-width: 1200px; }

/* 欢迎栏 */
.welcome-bar {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 24px; padding: 24px 28px;
  background: #fff; border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.greeting { font-size: 22px; font-weight: 700; color: #1f2937; margin: 0; }
.greeting-sub { font-size: 13px; color: #9ca3af; margin: 4px 0 0; }
.date-badge {
  font-size: 13px; color: #6b7280;
  padding: 8px 16px; background: #f9fafb; border-radius: 8px;
}

/* 统计卡片 */
.stat-row { margin-bottom: 20px; }
.stat-card {
  position: relative;
  padding: 22px 20px; border-radius: 14px; background: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  display: flex; align-items: center; gap: 16px;
  overflow: hidden; cursor: default;
  transition: transform 0.25s, box-shadow 0.25s;
  animation: fadeUp 0.5s ease-out both;
}
.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.08);
}
.stat-bg {
  position: absolute; top: -20px; right: -20px;
  width: 80px; height: 80px; border-radius: 50%; opacity: 0.1;
}
.stat-icon {
  width: 50px; height: 50px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center; color: #fff;
  flex-shrink: 0;
}
.stat-info { flex: 1; position: relative; z-index: 1; }
.stat-value { font-size: 28px; font-weight: 700; color: #1f2937; line-height: 1.1; }
.stat-title { font-size: 13px; color: #9ca3af; margin-top: 4px; }

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 快捷操作 */
.quick-card {
  border-radius: 12px; border: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.quick-card :deep(.el-card__header) {
  padding: 16px 20px 0; border-bottom: none;
}
.quick-card :deep(.el-card__body) { padding: 12px 20px 20px; }
.card-label { font-size: 15px; font-weight: 600; color: #374151; }
.quick-actions { display: flex; gap: 16px; }
.action-item {
  display: flex; align-items: center; gap: 8px;
  padding: 12px 20px; border-radius: 10px;
  background: #f9fafb; cursor: pointer;
  font-size: 14px; font-weight: 500; color: #374151;
  transition: all 0.2s;
}
.action-item:hover { background: #e8f4fd; color: #1890ff; }
</style>
