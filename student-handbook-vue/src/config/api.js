// API 配置文件

// 定义通用的API端点路径
const apiEndpoints = {
  STUDENT_HANDBOOK_LIST: '/system/handbook/list'
};

// 根据环境设置配置
const config = {
  development: {
    baseURL: '/api'
  },
  test: {
    baseURL: ''
  },
  production: {
    baseURL: '/sp-api'
  }
};

// 获取当前环境的配置
const currentConfig = config.development;

// 构建完整的API端点URL
const API_ENDPOINTS = {};
for (const [key, value] of Object.entries(apiEndpoints)) {
  API_ENDPOINTS[key] = currentConfig.baseURL + value;
}

export { API_ENDPOINTS };

export default {
  API_ENDPOINTS
};