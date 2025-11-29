import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import StudentHandbook from '../components/StudentHandbook.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/handbook',
    name: 'StudentHandbook',
    component: StudentHandbook
  }
]

const router = createRouter({
  history: createWebHistory('/sp-api/'),
  routes
})

export default router