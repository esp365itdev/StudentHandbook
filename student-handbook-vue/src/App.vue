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
  background-color: #eef5ff !important; /* 调整为与卡片更搭配的浅蓝背景 */
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
  background-color: #eef5ff !important; /* 调整为与卡片更搭配的浅蓝背景 */
  min-height: 100vh;
}
</style>