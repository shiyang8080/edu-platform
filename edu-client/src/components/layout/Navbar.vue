<template>
  <div class="navbar">
    <div class="nav-left">
      <div class="collapse-btn" @click="$emit('toggleSidebar')">
        <el-icon :size="20">
          <Fold v-if="!isCollapsed" />
          <Expand v-else />
        </el-icon>
      </div>
      <el-breadcrumb separator="›" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="pageTitle">{{ pageTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="nav-right">
      <!-- 通知 -->
      <el-badge :value="unreadCount" :hidden="!unreadCount" :max="99" class="nav-icon-btn">
        <div class="icon-btn" @click="$router.push('/notices/list')" title="通知公告">
          <el-icon :size="19"><Bell /></el-icon>
        </div>
      </el-badge>

      <!-- 用户下拉 -->
      <el-dropdown trigger="click" class="user-dropdown">
        <div class="user-trigger">
          <el-avatar :size="34" class="avatar">
            {{ userName.charAt(0) }}
          </el-avatar>
          <span class="user-name">{{ userName }}</span>
          <el-icon :size="14"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/profile')">
              <el-icon><User /></el-icon>个人信息
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getUnreadCount } from '@/api/notice'
import { Bell, Fold, Expand, ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'

defineProps({ isCollapsed: Boolean })
defineEmits(['toggleSidebar'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userName = computed(() => userStore.userName || '用户')
const unreadCount = ref(0)

const pageTitleMap = {
  '/dashboard': '工作台',
  '/users/list': '用户管理',
  '/courses/list': '课程管理',
  '/selections/my': '我的选课',
  '/selections/available': '可选课程',
  '/grades/my': '我的成绩',
  '/grades/statistics': '成绩统计',
  '/notices/list': '通知公告',
  '/profile': '个人信息'
}
const pageTitle = computed(() => {
  const path = route.path
  if (path.startsWith('/courses/detail/')) return '课程详情'
  if (path.startsWith('/grades/course/')) return '成绩录入'
  if (path.startsWith('/notices/detail/')) return '通知详情'
  return pageTitleMap[path] || ''
})

function fetchUnread() {
  getUnreadCount().then(res => { unreadCount.value = res.data || 0 }).catch(() => {})
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

onMounted(fetchUnread)
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

/* 左侧 */
.nav-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  width: 34px; height: 34px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 8px; cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
}
.collapse-btn:hover {
  background: #f1f5f9;
  color: #1890ff;
}
.breadcrumb :deep(.el-breadcrumb__inner) {
  color: #94a3b8;
  font-size: 13px;
}
.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #475569;
  font-weight: 500;
}

/* 右侧 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.nav-icon-btn { line-height: 1; }
.icon-btn {
  width: 34px; height: 34px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 8px; cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
}
.icon-btn:hover {
  background: #f1f5f9;
  color: #1890ff;
}

/* 用户 */
.user-dropdown { cursor: pointer; }
.user-trigger {
  display: flex; align-items: center; gap: 8px;
  padding: 4px 10px 4px 4px;
  border-radius: 10px;
  transition: background 0.2s;
}
.user-trigger:hover { background: #f8fafc; }
.avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; font-weight: 600; font-size: 14px;
}
.user-name {
  font-size: 14px; font-weight: 500; color: #334155;
  max-width: 80px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
</style>
