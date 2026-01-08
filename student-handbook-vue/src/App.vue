<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script setup>
// 移除直接導入的StudentHandbook組件
</script>

<style>
html, body {
  background-color: #f5f9ff !important; /* 使用項目記憶中的柔和淺藍灰色調 */
  margin: 0;
  padding: 0;
  font-family: 'Roboto', 'Segoe UI', 'Helvetica Neue', Arial, sans-serif; /* 現代化字體 */
}

#app {
  font-family: 'Roboto', 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0;
  background-color: #f5f9ff !important; /* 使用項目記憶中的柔和淺藍灰色調 */
  min-height: 100vh;
  padding-top: 0; /* 移除頂部內邊距 */
}
</style>