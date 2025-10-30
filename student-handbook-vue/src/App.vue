<template>
  <div id="app">
    <StudentHandbook />
  </div>
</template>

<script setup>
import StudentHandbook from './components/StudentHandbook.vue'
</script>

<style>
html, body {
  background-color: #f5f9ff !important; /* 使用更柔和的浅蓝灰色背景 */
  margin: 0;
  padding: 0;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 20px;
  background-color: #f5f9ff !important; /* 使用更柔和的浅蓝灰色背景 */
  min-height: 100vh;
}
</style>