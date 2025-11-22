import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// 设置 axios 的基础 URL
axios.defaults.baseURL = './'

// 请求拦截器
axios.interceptors.request.use(
  config => {
    // 确保API请求使用正确的路径
    if (config.url.startsWith('/system/')) {
      // 对于部署在子路径的情况，需要正确处理URL
      const baseHref = document.querySelector('base')?.href || '';
      if (baseHref && !config.url.startsWith(baseHref)) {
        config.url = baseHref + config.url.substring(1);
      }
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    // 处理302重定向错误
    if (error.response?.status === 302) {
      console.warn('遇到302重定向，尝试直接访问API');
    }
    return Promise.reject(error);
  }
);

const app = createApp(App)

app.use(ElementPlus)
app.use(router)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')