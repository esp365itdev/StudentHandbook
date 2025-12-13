import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import StudentHandbook from '../components/StudentHandbook.vue'
import SendNotice from '../views/wechat/SendNotice.vue'

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
    },
    {
        path: '/wechat/send-notice',
        name: 'SendNotice',
        component: SendNotice
    }
]

const router = createRouter({
    history: createWebHistory('/sp-api/'),
    routes
})

export default router