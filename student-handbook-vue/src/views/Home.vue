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
        <h3>当前微信用户信息</h3>
        <div v-if="userInfoLoading" class="loading">正在获取用户信息...</div>
        <div v-else-if="userInfoError" class="error">获取用户信息失败: {{ userInfoError }}</div>
        <div v-else class="user-info">
          <p><strong>用户ID:</strong> {{ currentUserInfo.userid || currentUserInfo.UserId || 'N/A' }}</p>
          <p><strong>用户名:</strong> {{ currentUserInfo.name || currentUserInfo.Name || 'N/A' }}</p>
          <p><strong>部门:</strong> {{ currentUserInfo.department || currentUserInfo.Department || 'N/A' }}</p>
          <p><strong>职位:</strong> {{ currentUserInfo.position || currentUserInfo.Position || 'N/A' }}</p>
          <p><strong>手机号:</strong> {{ currentUserInfo.mobile || currentUserInfo.Mobile || 'N/A' }}</p>
          <p><strong>邮箱:</strong> {{ currentUserInfo.email || currentUserInfo.Email || 'N/A' }}</p>
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
      userInfoError: null
    }
  },
  mounted() {
    // 页面加载时检查URL参数中是否有code
    this.checkWeChatAuthCode();
  },
  methods: {
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
        console.error('微信授权错误，错误码:', errcode);
        return;
      }
      
      if (code && state === 'wechat_test') {
        console.log('检测到微信授权code:', code);
        // 如果有code，尝试获取用户信息
        this.showUserInfoModal = true;
        this.userInfoLoading = true;
        this.userInfoError = null;
        
        try {
          const response = await axios.get(`${API_ENDPOINTS.WECHAT_USER_INFO}?code=${code}`);
          if (response.data.code === 200) {
            console.log('通过code获取用户信息成功:', response.data.data);
            this.currentUserInfo = response.data.data;
            this.userInfoLoading = false;
          } else {
            this.userInfoLoading = false;
            this.userInfoError = response.data.msg;
          }
        } catch (error) {
          console.error('通过code获取用户信息失败:', error);
          this.userInfoLoading = false;
          this.userInfoError = error.message || '获取用户信息失败';
        }
      }
    },
    
    // 测试微信用户信息获取
    async testWeChatUserInfo() {
      // 显示模态框
      this.showUserInfoModal = true;
      this.userInfoLoading = true;
      this.userInfoError = null;
      this.currentUserInfo = {};
      
      try {
        // 检查是否在微信环境中
        const isWeChat = navigator.userAgent.includes('MicroMessenger');
        if (isWeChat) {
          // 尝试通过OAuth2方式获取用户信息
          await this.getWeChatUserInfoByOAuth();
        } else {
          // 如果没有在微信环境中，显示提示信息
          this.userInfoLoading = false;
          this.userInfoError = '请在微信或企业微信环境中打开应用';
        }
      } catch (error) {
        this.userInfoLoading = false;
        this.userInfoError = error.message || '获取用户信息时发生错误';
      }
    },
    
    // 通过OAuth2方式获取微信用户信息
    async getWeChatUserInfoByOAuth() {
      try {
        // 使用企业微信可信域名作为回调地址
        const redirectUri = encodeURIComponent('http://mo-stu-sys.org-assistant.com/sp-api/wechat/oauth/callback');
        const state = 'wechat_test'; // 固定state值用于识别
        const appId = 'ww04fad852e91fd490'; // 企业微信应用ID
        const agentId = '1000032'; // 企业微信应用agentId
        
        // 构造适合手机端的企业微信OAuth2授权链接
        const authUrl = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${redirectUri}&response_type=code&scope=snsapi_base&agentid=${agentId}&state=${state}#wechat_redirect`;
        
        // 重定向到授权页面
        window.location.href = authUrl;
      } catch (error) {
        this.userInfoLoading = false;
        this.userInfoError = error.message || '发起微信授权失败';
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

.user-info p {
  margin: 10px 0;
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

.loading, .error {
  text-align: center;
  padding: 20px;
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