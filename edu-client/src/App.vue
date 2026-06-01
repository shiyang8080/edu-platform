<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* ===== 全局样式 ===== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #334155;
  background: #f1f5f9;
}

/* 滚动条美化 */
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

/* Element Plus 全局覆盖 */
.el-card {
  border-radius: 12px !important;
  border: none !important;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04) !important;
}
.el-card__header {
  padding: 16px 20px !important;
  border-bottom: 1px solid #f1f5f9 !important;
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
}
.el-card__body {
  padding: 20px !important;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}
.el-table th.el-table__cell {
  background: #f8fafc !important;
  color: #64748b;
  font-weight: 600;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.el-table .el-table__row:hover > td {
  background: #f8fafc !important;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
  transition: all 0.3s !important;
}
.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(102,126,234,0.35) !important;
}
.el-button--primary:active {
  transform: translateY(0);
}

.el-button--small {
  border-radius: 6px !important;
}

.el-tabs__item {
  font-weight: 500;
}
.el-tabs__active-bar {
  background: #667eea !important;
}
.el-tabs__item.is-active {
  color: #667eea !important;
}

.el-pagination {
  font-weight: 500;
}

.el-dialog {
  border-radius: 14px !important;
}
.el-dialog__header {
  padding: 20px 24px 0 !important;
  font-weight: 600;
}
.el-dialog__body {
  padding: 20px 24px !important;
}

.el-input__wrapper {
  border-radius: 8px !important;
  transition: all 0.2s;
}

.el-tag {
  border-radius: 6px !important;
  font-weight: 500;
}

.el-message {
  border-radius: 10px !important;
}
</style>
