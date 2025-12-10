<template>
  <div class="home-container">
    <div class="welcome-section">
      <div class="logo-badge">
        <!-- 在这里放置学校Logo -->
        <img src="../logo/sp.jpg" alt="School Logo" class="school-logo-img">
      </div>
      <h1 class="welcome-title">歡迎使用學生系統</h1>
    </div>
    
    <div class="buttons-container">
      <button 
        class="feature-button primary-button"
        @click="goToStudentHandbook"
      >
        <div class="button-content">
          <span class="button-icon">📘</span>
          <span class="button-text">學生手冊</span>
        </div>
      </button>
      
      <button 
        class="feature-button success-button"
        @click="goToParentNotice"
      >
        <div class="button-content">
          <span class="button-icon">📢</span>
          <span class="button-text">家校通知</span>
        </div>
      </button>
      
      <!-- 添加微信测试按钮 -->
      <button 
        class="feature-button warning-button"
        @click="testWeChatUserInfo"
      >
        <div class="button-content">
          <span class="button-icon">💬</span>
          <span class="button-text">微信用户测试</span>
        </div>
      </button>
    </div>
    
    <!-- 显示用户信息的模态框 -->
    <div v-if="showUserInfoModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h3>微信用户信息测试</h3>
        <div v-if="userInfoLoading" class="loading">
          <div class="loading-spinner"></div>
          <p>{{ currentLogMessage || '正在获取用户信息...' }}</p>
        </div>
        <div v-else-if="userInfoError" class="error">获取用户信息失败: {{ userInfoError }}</div>
        <div v-else class="user-info">
          <h4>用户信息</h4>
          <p><strong>用户ID:</strong> {{ currentUserInfo.userid || currentUserInfo.UserId || 'N/A' }}</p>
          <p><strong>用户名:</strong> {{ currentUserInfo.name || currentUserInfo.Name || 'N/A' }}</p>
          <p><strong>部门:</strong> {{ currentUserInfo.department || currentUserInfo.Department || 'N/A' }}</p>
          <p><strong>职位:</strong> {{ currentUserInfo.position || currentUserInfo.Position || 'N/A' }}</p>
          <p><strong>手机号:</strong> {{ currentUserInfo.mobile || currentUserInfo.Mobile || 'N/A' }}</p>
          <p><strong>邮箱:</strong> {{ currentUserInfo.email || currentUserInfo.Email || 'N/A' }}</p>
        </div>
        
        <!-- 日志显示区域 -->
        <div class="log-section">
          <h4>操作日志</h4>
          <div class="log-content">
            <div v-for="(log, index) in logs" :key="index" class="log-item">
              {{ log }}
            </div>
          </div>
        </div>
        
        <button class="close-button" @click="closeModal">关闭</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { API_ENDPOINTS } from '@/config/api.js'

export default {
  name: 'Home',
  data() {
    return {
      showUserInfoModal: false,
      currentUserInfo: {},
      userInfoLoading: false,
      userInfoError: null,
      currentLogMessage: '',
      logs: [] // 存储操作日志
    }
  },
  mounted() {
    // 页面加载时检查URL参数中是否有code
    this.checkWeChatAuthCode();
  },
  methods: {
    addToLog(message) {
      const timestamp = new Date().toLocaleTimeString();
      this.logs.push(`[${timestamp}] ${message}`);
      console.log(message);
    },
    
    goToStudentHandbook() {
      // 跳轉到學生手冊頁面
      this.$router.push('/handbook');
    },
    
    goToParentNotice() {
      // 暫時不調整任何頁面，僅顯示提示信息
      this.$message.info('家校通知功能正在開發中');
    },
    
    // 检查URL参数中是否有微信授权code
    async checkWeChatAuthCode() {
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get('code');
      const state = urlParams.get('state');
      
      // 检查是否有错误参数
      const errcode = urlParams.get('errcode');
      if (errcode) {
        this.addToLog(`微信授权错误，错误码: ${errcode}`);
        return;
      }
      
      if (code && state === 'wechat_test') {
        this.addToLog('检测到微信授权code，开始获取用户信息');
        // 如果有code，尝试获取用户信息
        this.showUserInfoModal = true;
        this.userInfoLoading = true;
        this.userInfoError = null;
        this.logs = []; // 清空之前的日志
        this.addToLog(`接收到的code: ${code.substring(0, 10)}...`); // 只显示前10位
        
        try {
          // 添加超时设置
          const source = axios.CancelToken.source();
          const timeout = setTimeout(() => {
            source.cancel('请求超时');
          }, 8000); // 8秒超时（略小于后端超时时间）
          
          this.currentLogMessage = '正在请求后端获取用户信息...';
          this.addToLog('发送请求到后端接口: ' + API_ENDPOINTS.WECHAT_USER_INFO);
          
          const response = await axios.get(`${API_ENDPOINTS.WECHAT_USER_INFO}?code=${code}`, {
            cancelToken: source.token
          });
          
          clearTimeout(timeout);
          
          this.addToLog('收到后端响应，状态码: ' + response.status);
          
          if (response.data.code === 200) {
            this.addToLog('成功获取用户信息');
            this.currentUserInfo = response.data.data;
            this.userInfoLoading = false;
            this.currentLogMessage = '';
          } else {
            this.userInfoLoading = false;
            this.userInfoError = response.data.msg;
            this.currentLogMessage = '';
            this.addToLog(`获取用户信息失败: ${response.data.msg}`);
          }
        } catch (error) {
          this.userInfoLoading = false;
          this.currentLogMessage = '';
          if (axios.isCancel(error)) {
            this.userInfoError = '请求超时，请稍后重试';
            this.addToLog('请求超时');
          } else {
            this.userInfoError = error.message || '获取用户信息失败';
            this.addToLog(`获取用户信息失败: ${error.message}`);
          }
        }
      }
    },
    
    // 测试微信用户信息获取
    async testWeChatUserInfo() {
      this.logs = []; // 清空之前的日志
      this.addToLog('开始微信用户信息测试');
      
      // 显示模态框
      this.showUserInfoModal = true;
      this.userInfoLoading = true;
      this.userInfoError = null;
      this.currentLogMessage = '正在检查环境...';
      
      try {
        // 检查是否在微信环境中
        const isWeChat = navigator.userAgent.includes('MicroMessenger');
        this.addToLog(`当前环境检查: ${isWeChat ? '微信环境' : '非微信环境'}`);
        
        if (isWeChat) {
          this.currentLogMessage = '正在跳转到微信授权页面...';
          this.addToLog('环境检查通过，准备跳转到微信授权页面');
          // 尝试通过OAuth2方式获取用户信息
          await this.getWeChatUserInfoByOAuth();
        } else {
          // 如果没有在微信环境中，显示提示信息
          this.userInfoLoading = false;
          this.userInfoError = '请在微信或企业微信环境中打开应用';
          this.currentLogMessage = '';
          this.addToLog('环境检查失败：请在微信或企业微信环境中打开应用');
        }
      } catch (error) {
        this.userInfoLoading = false;
        this.currentLogMessage = '';
        this.userInfoError = error.message || '获取用户信息时发生错误';
        this.addToLog(`发生错误: ${error.message}`);
      }
    },
    
    // 通过OAuth2方式获取微信用户信息
    async getWeChatUserInfoByOAuth() {
      try {
        this.addToLog('构建微信授权链接');
        // 使用企业微信可信域名作为回调地址
        const redirectUri = encodeURIComponent('https%3a%2f%2fmo-stu-sys.org-assistant.com%2fsp-api%2fwechat%2foauth%2fcallback');
        // 根据用户提供的信息，使用新的corpid
        const corpId = 'ww04fad852e91fd490'; // 企业微信应用ID
        const agentId = '1000032'; // 企业微信应用agentId
        
        // 构造适合手机端的企业微信OAuth2授权链接
        const authUrl = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corpId}&redirect_uri=${redirectUri}&response_type=code&scope=snsapi_base&agentid=${agentId}&state=#wechat_redirect`;
        
        this.addToLog('跳转到微信授权页面' + authUrl);
        // 重定向到授权页面
        window.location.href = authUrl;
      } catch (error) {
        this.userInfoLoading = false;
        this.currentLogMessage = '';
        this.userInfoError = error.message || '发起微信授权失败';
        this.addToLog(`发起微信授权失败: ${error.message}`);
      }
    },
    
    closeModal() {
      this.showUserInfoModal = false;
    }
  }
}
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f9ff;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.home-container::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.05) 0%, transparent 70%);
  z-index: 0;
  animation: rotate 20s linear infinite;
}

.welcome-section {
  text-align: center;
  margin: 20px 0 15px 0; /* 减少下方边距 */
  animation: fadeInDown 1s ease;
  position: relative;
  z-index: 1;
}

.logo-badge {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  animation: pulse 2s infinite;
  overflow: hidden;
}

.school-logo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.welcome-title {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  letter-spacing: 1px;
}

/* 按鈕容器樣式 */
.buttons-container {
  display: flex;
  flex-direction: column;
  gap: 15px; /* 减少按钮之间的间距 */
  width: 100%;
  max-width: 320px;
  animation: fadeInUp 1s ease;
  position: relative;
  z-index: 1;
}

.button-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.button-icon {
  font-size: 24px;
}

.button-text {
  font-size: 24px;
  font-weight: bold;
}

.feature-button {
  height: 80px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
  border: none;
  cursor: pointer;
  outline: none;
  transform: translateY(0);
  position: relative;
  overflow: hidden;
}

.feature-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.feature-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(120deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.feature-button:hover:not(:disabled)::before {
  transform: translateX(100%);
}

.feature-button:hover:not(:disabled) {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.feature-button:active:not(:disabled) {
  transform: translateY(1px);
}

.primary-button {
  background: linear-gradient(135deg, #409eff 0%, #1a73e8 100%);
  color: white;
}

.success-button {
  background: linear-gradient(135deg, #67c23a 0%, #4caf50 100%);
  color: white;
}

.warning-button {
  background: linear-gradient(135deg, #e6a23c 0%, #e67c12 100%);
  color: white;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 90%;
  width: 400px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  margin-top: 0;
  text-align: center;
}

.modal-content h4 {
  margin-top: 15px;
  margin-bottom: 10px;
  color: #303133;
}

.user-info p {
  margin: 8px 0;
  padding: 4px 0;
  border-bottom: 1px solid #eee;
}

.loading {
  text-align: center;
  padding: 20px;
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409eff;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  color: #f56c6c;
  text-align: center;
  padding: 20px;
}

/* 日志区域样式 */
.log-section {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.log-content {
  background-color: #f5f5f5;
  border-radius: 4px;
  padding: 10px;
  max-height: 200px;
  overflow-y: auto;
  font-family: monospace;
  font-size: 12px;
}

.log-item {
  margin: 5px 0;
  line-height: 1.4;
}

.close-button {
  display: block;
  width: 100%;
  padding: 10px;
  margin-top: 15px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.close-button:hover {
  background-color: #66b1ff;
}

/* 手機屏幕適配 - 調整間距 */
@media (max-width: 768px) {
  .buttons-container {
    gap: 12px; /* 在手机上进一步减少间距 */
    max-width: 280px;
  }

  .welcome-section {
    margin: 20px 0 10px 0; /* 在手机上减少间距 */
  }
  
  .welcome-title {
    font-size: 28px;
  }
  
  .button-text {
    font-size: 22px;
  }
  
  .logo-badge {
    width: 120px;
    height: 120px;
  }
  
  .modal-content {
    width: 90%;
    padding: 15px;
  }
}

/* 平板和桌面屏幕適配 */
@media (min-width: 769px) {
  .buttons-container {
    gap: 15px;
    max-width: 350px;
  }
  
  .welcome-section {
    margin: 20px 0 15px 0;
  }
  
  .welcome-title {
    font-size: 32px;
  }
  
  .button-text {
    font-size: 24px;
  }
  
  .logo-badge {
    width: 150px;
    height: 150px;
  }
}

/* 動畫效果 */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>