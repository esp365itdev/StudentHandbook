// 封装axios的请求工具函数
import axios from 'axios'

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
    return response
  },
  error => {
    console.log('err' + error)// for debug
    if (error.response && error.response.status === 401) {
      // token过期或无效
      localStorage.removeItem('token')
    }
    return Promise.reject(error)
  }
)

export default service