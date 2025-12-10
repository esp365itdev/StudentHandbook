// API 配置文件

// 定义通用的API端点路径
const apiEndpoints = {
  STUDENT_HANDBOOK_LIST: '/system/handbook/list',
  CHECK_USER_TYPE: '/user/type/check',
  WECHAT_JS_CONFIG: '/wechat/jsconfig/getConfig'
};

// 获取基础URL - 从环境变量读取或者使用默认值
const getBaseURL = () => {
  const envBaseUrl = import.meta.env.VITE_API_BASE_URL;
  if (envBaseUrl !== undefined && envBaseUrl !== null && envBaseUrl !== '') {
    return envBaseUrl;
  }
  // 默认值 - 在test环境下为空，在其他环境下为/api
  const nodeEnv = import.meta.env.NODE_ENV;
  if (nodeEnv === 'test') {
    return '';
  }
  return '/api';
};

// 构建完整的API端点URL
const API_ENDPOINTS = {};
const baseURL = getBaseURL();
for (const [key, value] of Object.entries(apiEndpoints)) {
  // 如果baseURL为空，则直接使用value（相对路径）
  API_ENDPOINTS[key] = baseURL ? baseURL + value : value;
}

export { API_ENDPOINTS };

export default {
  API_ENDPOINTS
};