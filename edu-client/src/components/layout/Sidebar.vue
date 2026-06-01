<template>
  <div class="sidebar">
    <!-- Logo区域 -->
    <div class="logo-area">
      <div class="logo-icon">
        <el-icon :size="isCollapsed ? 20 : 22"><School /></el-icon>
      </div>
      <transition name="fade">
        <div v-if="!isCollapsed" class="logo-text">
          <span class="logo-title">工商综合教学</span>
          <span class="logo-sub">管理平台</span>
        </div>
      </transition>
    </div>

    <!-- 菜单 -->
    <el-menu
      :default-active="activeMenu"
      background-color="#f8fafc"
      text-color="#475569"
      active-text-color="#1890ff"
      :collapse="isCollapsed"
      :collapse-transition="false"
      router
      class="sidebar-menu"
    >
      <el-menu-item index="/dashboard">
        <template #title>
          <div class="menu-item-content">
            <el-icon :size="18"><DataAnalysis /></el-icon>
            <span>工作台</span>
          </div>
        </template>
      </el-menu-item>

      <el-sub-menu v-if="role === 'ADMIN'" index="users">
        <template #title>
          <div class="menu-item-content">
            <el-icon :size="18"><UserFilled /></el-icon>
            <span>用户管理</span>
          </div>
        </template>
        <el-menu-item index="/users/list">
          <span class="sub-item">用户列表</span>
        </el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="courses">
        <template #title>
          <div class="menu-item-content">
            <el-icon :size="18"><Reading /></el-icon>
            <span>课程管理</span>
          </div>
        </template>
        <el-menu-item index="/courses/list">
          <span class="sub-item">课程列表</span>
        </el-menu-item>
        <el-menu-item v-if="role === 'TEACHER' || role === 'ADMIN'" index="/courses/list?tab=myTeach">
          <span class="sub-item">我的授课</span>
        </el-menu-item>
      </el-sub-menu>

      <template v-if="role === 'STUDENT'">
        <el-sub-menu index="selections">
          <template #title>
            <div class="menu-item-content">
              <el-icon :size="18"><Select /></el-icon>
              <span>选课管理</span>
            </div>
          </template>
          <el-menu-item index="/selections/my">
            <span class="sub-item">我的选课</span>
          </el-menu-item>
          <el-menu-item index="/selections/available">
            <span class="sub-item">可选课程</span>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <el-sub-menu index="grades">
        <template #title>
          <div class="menu-item-content">
            <el-icon :size="18"><TrophyBase /></el-icon>
            <span>成绩管理</span>
          </div>
        </template>
        <el-menu-item v-if="role === 'STUDENT'" index="/grades/my">
          <span class="sub-item">我的成绩</span>
        </el-menu-item>
        <el-menu-item v-if="role === 'STUDENT'" index="/grades/statistics">
          <span class="sub-item">成绩统计</span>
        </el-menu-item>
        <el-menu-item v-if="role === 'TEACHER' || role === 'ADMIN'" index="/courses/list?tab=grades">
          <span class="sub-item">成绩录入</span>
        </el-menu-item>
      </el-sub-menu>

      <el-menu-item index="/notices/list">
        <template #title>
          <div class="menu-item-content">
            <el-icon :size="18"><Bell /></el-icon>
            <span>通知公告</span>
          </div>
        </template>
      </el-menu-item>

      <el-menu-item index="/profile">
        <template #title>
          <div class="menu-item-content">
            <el-icon :size="18"><User /></el-icon>
            <span>个人信息</span>
          </div>
        </template>
      </el-menu-item>
    </el-menu>

    <!-- 底部版本 -->
    <div class="sidebar-footer" v-if="!isCollapsed">
      <span>v1.0.0</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { getRole } from '@/utils/auth'
import { School } from '@element-plus/icons-vue'

defineProps({ isCollapsed: Boolean })

const route = useRoute()
const role = computed(() => getRole())
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  border-right: 1px solid #e2e8f0;
}

/* Logo */
.logo-area {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  height: 64px;
  padding: 0 16px;
  border-bottom: 1px solid #e2e8f0;
  background: #fff;
}
.logo-icon {
  width: 38px; height: 38px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  flex-shrink: 0;
}
.logo-text {
  display: flex; flex-direction: column;
  line-height: 1.15;
}
.logo-title { font-size: 13px; font-weight: 700; color: #1e293b; }
.logo-sub { font-size: 10px; color: #94a3b8; }

/* 过渡动画 */
.fade-enter-active { transition: all 0.3s ease; }
.fade-leave-active { transition: all 0.15s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* 菜单 */
.sidebar-menu {
  flex: 1;
  border-right: none !important;
  padding: 8px 0;
  overflow-y: auto;
}
.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  margin: 2px 8px;
  border-radius: 10px;
  transition: all 0.2s;
}
.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: #e8f4fd !important;
}
.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #e8f4fd, #dbeafe) !important;
  color: #1890ff !important;
  font-weight: 600;
}
.sidebar-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0; top: 50%; transform: translateY(-50%);
  width: 3px; height: 20px;
  background: #1890ff;
  border-radius: 0 3px 3px 0;
}
.menu-item-content {
  display: flex; align-items: center; gap: 10px;
}
.sub-item { font-size: 13px; }

/* 折叠状态 */
.sidebar-menu :deep(.el-menu--collapse) {
  width: 64px;
}
.sidebar-menu :deep(.el-menu--collapse .el-menu-item),
.sidebar-menu :deep(.el-menu--collapse .el-sub-menu__title) {
  justify-content: center;
  padding: 0 !important;
}

/* 底部 */
.sidebar-footer {
  padding: 12px;
  text-align: center;
  font-size: 11px;
  color: #cbd5e1;
  border-top: 1px solid #e2e8f0;
}
</style>
