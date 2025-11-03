<template>
  <div class="home-container">
    <div class="welcome-section">
      <div class="logo-badge">
        <svg width="60" height="60" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 14L12 20" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
          <path d="M12 4L12 6" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
          <circle cx="12" cy="10" r="6" stroke="#409EFF" stroke-width="2"/>
          <path d="M5 19L6.5 16.5" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
          <path d="M19 19L17.5 16.5" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
          <path d="M17.5 16.5L15.5 13.5" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
          <path d="M6.5 16.5L8.5 13.5" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      <h1 class="welcome-title">歡迎使用學生系統</h1>
    </div>
    
    <div class="image-container">
      <img src="../logo/sp.jpg" alt="School Logo" class="school-logo">
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
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  methods: {
    goToStudentHandbook() {
      // 跳轉到學生手冊頁面
      this.$router.push('/handbook');
    },
    
    goToParentNotice() {
      // 暫時不調整任何頁面，僅顯示提示信息
      this.$message.info('家校通知功能正在開發中');
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
  margin: 20px 0 30px 0;
  animation: fadeInDown 1s ease;
  position: relative;
  z-index: 1;
}

.logo-badge {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #409eff, #1a73e8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  animation: pulse 2s infinite;
}

.welcome-title {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  letter-spacing: 1px;
}

.image-container {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
  animation: fadeIn 1.5s ease;
  position: relative;
  z-index: 1;
}

.school-logo {
  max-width: 90%;
  height: auto;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  transform: translateY(0);
}

.school-logo:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

/* 按鈕容器樣式 */
.buttons-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
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

.feature-button:hover::before {
  transform: translateX(100%);
}

.feature-button:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.feature-button:active {
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

/* 手機屏幕適配 - 調整間距 */
@media (max-width: 768px) {
  .buttons-container {
    gap: 20px;
    max-width: 280px;
  }
  
  .image-container {
    margin-bottom: 30px;
  }
  
  .welcome-title {
    font-size: 28px;
  }
  
  .button-text {
    font-size: 22px;
  }
}

/* 平板和桌面屏幕適配 */
@media (min-width: 769px) {
  .buttons-container {
    gap: 30px;
    max-width: 350px;
  }
  
  .image-container {
    margin-bottom: 40px;
  }
  
  .welcome-title {
    font-size: 32px;
  }
  
  .button-text {
    font-size: 24px;
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