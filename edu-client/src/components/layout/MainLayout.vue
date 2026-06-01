<template>
  <el-container class="main-container">
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="aside-wrapper">
      <Sidebar :isCollapsed="isCollapsed" />
    </el-aside>
    <el-container>
      <el-header class="header-wrapper">
        <Navbar :is-collapsed="isCollapsed" @toggle-sidebar="toggleSidebar" />
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'

const isCollapsed = ref(false)

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
.main-container {
  height: 100vh;
}
.aside-wrapper {
  background: #f8fafc;
  transition: width 0.3s ease;
  overflow: hidden;
}
.header-wrapper {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  padding: 0 24px;
  height: 64px;
  line-height: 64px;
  position: relative;
  z-index: 10;
}
.main-content {
  background: #f1f5f9;
  padding: 24px;
  min-height: calc(100vh - 64px);
  overflow-y: auto;
}

/* 页面切换动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
