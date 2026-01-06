// 封装axios的请求工具函数
import axios from 'axios'
import { ElMessage } from 'element-plus' // 导入Element Plus的消息组件

// 创建axios实例
const getBaseURL = () => {
  // 在测试环境中，API请求的baseURL应该是空的，因为API_ENDPOINTS已经包含了完整的路径
  if (import.meta.env.MODE === 'test') {
    return '';
  }
  return import.meta.env.VITE_API_BASE_URL || '';
};

const service = axios.create({
  baseURL: getBaseURL(), // api的base_url
  timeout: 30000 // 请求超时时间
});

// request拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      // 让每个请求携带自定义token
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    // Do something with request error
    console.log(error) // for debug
    Promise.reject(error)
  }
)

// response拦截器
service.interceptors.response.use(
  response => {
    // 检查响应数据结构
    const res = response.data;
    
    // 如果后端返回了token信息，保存到本地存储
    if (res.code === 200 && res.data && res.data.token) {
      // 将token保存到本地存储
      localStorage.setItem('token', res.data.token);
    }
    
    return response;
  },
  error => {
    console.log('err' + error)// for debug
    if (error.response && error.response.status === 401) {
      // token过期或无效
      localStorage.removeItem('token')
      // 重定向到登录页面
      window.location.href = '/login'
      ElMessage.error('登录已过期，请重新登录')
    } else if (error.response && error.response.status === 403) {
      // 无权限访问
      const token = localStorage.getItem('token')
      if (!token) {
        // 如果没有token，重定向到登录页面
        window.location.href = '/login'
      } else {
        // 如果有token但无权限，可能是token无效，清除并跳转到登录
        localStorage.removeItem('token')
        window.location.href = '/login'
        ElMessage.error('权限不足或登录已失效，请重新登录')
      }
    }
    return Promise.reject(error)
  }
)

export default service