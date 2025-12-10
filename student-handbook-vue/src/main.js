import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 创建Vue应用实例
const app = createApp(App)

// 配置路由
app.use(router)

// 挂载应用
app.mount('#app')

// 动态加载企业微信JS-SDK
function loadWeChatSDK() {
  // 检查是否在企业微信环境中
  const isWeChatWork = navigator.userAgent.includes('wxwork');
  
  if (isWeChatWork) {
    const script = document.createElement('script');
    script.src = 'https://res.wx.qq.com/open/js/jweixin-1.2.0.js';
    script.onload = () => {
      console.log('企业微信JS-SDK加载成功');
    };
    script.onerror = () => {
      console.error('企业微信JS-SDK加载失败');
    };
    document.head.appendChild(script);
  }
}

// 页面加载时尝试加载企业微信SDK
loadWeChatSDK();