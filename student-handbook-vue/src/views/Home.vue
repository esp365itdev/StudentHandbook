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

      <!-- <button
          class="feature-button success-button"
          @click="goToParentNotice"
      >
        <div class="button-content">
          <span class="button-icon">📢</span>
          <span class="button-text">家校通知</span>
        </div>
      </button> -->
    </div>
  </div>
  
</template>

<script>
import service from '@/utils/request.js'

export default {
  name: 'Home',
  data() {
    return {}
  },
  mounted() {
    // 检查是否存在token，如果没有则重定向到登录页面
    this.checkToken();
    // 检查URL参数中是否有token（来自微信授权回调）
    this.checkTokenFromUrl();

  },

  methods: {
    // 检查URL参数中的token
    checkTokenFromUrl() {
      const urlParams = new URLSearchParams(window.location.search);
      const token = urlParams.get('token');
      
      if (token) {
        // 保存token到本地存储
        localStorage.setItem('token', token);
        
        // 清除URL中的token参数，避免在地址栏显示敏感信息
        urlParams.delete('token');
        const newUrl = window.location.pathname + 
          (urlParams.toString() ? '?' + urlParams.toString() : '') + 
          window.location.hash;
        window.history.replaceState({}, document.title, newUrl);
        
        this.$message.success('登錄成功');
      }
    },
    
    // 检查是否存在token
    checkToken() {
      const token = localStorage.getItem('token');
      if (!token) {
        // 如果没有token，重定向到登录页面
        this.$router.push('/login');
      }
    },
    goToStudentHandbook() {
      // 跳轉到學生手冊頁面
      this.$router.push('/handbook');
    },
    goToParentNotice() {
      // 暫時不調整任何頁面，僅顯示提示信息
      this.$message.info('家校通知功能正在開發中');
    },
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
  background: radial-gradient(circle, rgba(64, 158, 255, 0.05) 0, transparent 70%);
  z-index: 0;
  animation: rotate 20s linear infinite;
}

.welcome-section {
  text-align: center;
  margin: 20px 0 15px 0;
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
  gap: 15px;
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

.info-button {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
  color: white;
}

.danger-button {
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
  color: white;
}


/* 手機屏幕適配 - 調整間距 */
@media (max-width: 768px) {
  .buttons-container {
    gap: 12px;
    max-width: 280px;
  }

  .welcome-section {
    margin: 20px 0 10px 0;
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