<template>
  <div class="student-handbook" 
       @touchstart="handleTouchStart"
       @touchmove="handleTouchMove"
       @touchend="handleTouchEnd">
    <div class="header-container">
      <!-- 頁面標題和按鈕容器 -->
      <div class="title-and-buttons">
        <!-- 返回首頁按鈕 -->
        <el-button class="home-btn" type="primary" @click="goHome">
          <template #icon>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8.354 1.146a.5.5 0 0 0-.708 0l-6 6A.5.5 0 0 0 1.5 7.5v7a.5.5 0 0 0 .5.5h4.5a.5.5 0 0 0 .5-.5V14h3v1.5a.5.5 0 0 0 .5.5H14a.5.5 0 0 0 .5-.5v-7a.5.5 0 0 0-.146-.354L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293L8.354 1.146ZM11.5 14v-6h-3v6h3Z"/>
            </svg>
          </template>
          返回首頁
        </el-button>
        
        <!-- 用戶切換按鈕 -->
        <el-button class="user-switch-btn" type="info" @click="toggleUserMenu">
          <template #icon>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4zM10 9.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0z"/>
            </svg>
          </template>
          切換學生
        </el-button>
        
        <!-- 左右方向按鈕 -->
        <div class="navigation-buttons">
          <el-button class="nav-arrow prev-button" :class="{'active': activeButton === 'today'}" type="primary" icon="ArrowLeft" @click="showTodayData" :disabled="false">當天</el-button>
          <el-button class="nav-arrow next-button" :class="{'active': activeButton === 'future'}" type="primary" icon="ArrowRight" @click="showNextSevenDaysData" :disabled="false">未來七天</el-button>
        </div>
      </div>
    </div>
    
    <!-- 卡片式數據展示 -->
    <div class="handbook-container" v-loading="loading">
      <div 
        class="handbook-card"
        v-for="(item,index) in paginatedGroupedHandbookList"
        :key="index"
      >
        <div class="card-header">
          <h3 class="card-title">{{ item.timeRange }}</h3>
        </div>
        
        <div class="card-content">
          <!-- 按類別分組顯示條目 -->
          <div v-for="(categoryGroup, categoryIndex) in item.categoryGroups" 
               :key="categoryIndex"
               class="category-group">
            <!-- 類別標籤 -->
            <div class="category-container">
              <div class="category-row">
                <span 
                  class="category-badge"
                  :class="{ 'exam-badge': categoryGroup.category === '測驗' || categoryGroup.category === '考试' }"
                >
                  {{ categoryGroup.category }}
                </span>
              </div>
            </div>
            
            <!-- 該類別下的條目 -->
            <div 
              class="card-field" 
              v-for="(entry, entryIndex) in categoryGroup.entries" 
              :key="entryIndex"
              :class="{ 'exam-field': entry.category === '測驗' || entry.category === '考试' }"
            >
             <span class="field-value">{{ entry.subject }} : {{ entry.content }}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 空狀態 -->
      <div v-if="paginatedGroupedHandbookList.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无数据" />
      </div>
    </div>
    <!-- 回到頂部按鈕 -->
    <div class="back-to-top" v-show="showBackToTop" @click="scrollToTop">
      <span>回到頂部</span>
    </div>
  </div>
</template>

<script>
import service from '@/utils/request.js'
import { API_ENDPOINTS } from '@/config/api.js'
import { ElMessage } from 'element-plus'

export default {
  name: 'StudentHandbook',
  components: {
  },
  data() {
    return {
      loading: false,
      allGroupedHandbookList: [], // 存儲所有分組後的數據
      currentPage: 1, // 當前頁碼
      pageSize: 7, //每頁顯示條數
      isMobile: false,
      showBackToTop: false,
      showNavigation: false, // 控制導航菜單的顯示
      showUserMenu: false, // 控制用戶菜單的顯示
      viewMode: 'today', // 視圖模式: 'today' 或 'nextSevenDays'
      activeButton: 'today', // 追蹤當前選中的按鈕
      
      // 滑動相關數據
      touchStartX: 0,
      touchStartY: 0,
      touchEndX: 0,
      touchEndY: 0
    }
  },
  computed: {
    // 計算總頁數
    totalPages() {
      return Math.ceil(this.allGroupedHandbookList.length / this.pageSize);
    },
    
    // 計算當前頁需要顯示的數據
    paginatedGroupedHandbookList() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = startIndex + this.pageSize;
      return this.allGroupedHandbookList.slice(startIndex, endIndex);
    }
  },
  mounted() {
    this.checkIsMobile()
    this.activeButton = 'today'; // 初始化時設置當天按鈕為活躍狀態
    this.fetchHandbookList()
    window.addEventListener('resize', this.checkIsMobile)
    // 添加滾動事件監聽器
    window.addEventListener('scroll', this.handleScroll)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.checkIsMobile)
    // 移除滾動事件監聽器
    window.removeEventListener('scroll', this.handleScroll)
  },
  methods: {
    //檢查是否為移動設備
    checkIsMobile() {
      this.isMobile = window.innerWidth < 768
    },
    
    // 切換導航菜單顯示狀態
    toggleNavigation() {
      this.showNavigation = !this.showNavigation
    },
    
    // 返回首頁
    goHome() {
      this.$router.push('/');
    },
    
    // 切換用戶菜單顯示狀態
    toggleUserMenu() {
      this.showUserMenu = !this.showUserMenu;
      // 這裡可以添加用戶切換的邏輯
      if (this.showUserMenu) {
        // 顯示用戶選擇菜單，可以添加彈窗或其他UI元素
        alert('用戶切換菜單');
      }
    },
    
    //處理滾動事件，控制回到頂部按鈕的顯示
    handleScroll() {
      // 當滾動超過300px時顯示回到頂部按鈕
      this.showBackToTop = window.pageYOffset > 300
    },
    
    // 滾動到頂部
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth' // 平滑滾動
      })
    },
    
    // 觸摸開始事件
    handleTouchStart(event) {
      const touch = event.touches[0];
      this.touchStartX = touch.clientX;
      this.touchStartY = touch.clientY;
    },
    
    // 觸摸移動事件
    handleTouchMove(event) {
      // 可以在這裡添加一些視覺反饋
    },
    
    // 觸摸結束事件
    handleTouchEnd(event) {
      const touch = event.changedTouches[0];
      this.touchEndX = touch.clientX;
      this.touchEndY = touch.clientY;
      
      this.handleSwipeGesture();
    },
    
    //處理滑動手勢
    handleSwipeGesture() {
      const deltaX = this.touchEndX - this.touchStartX;
      const deltaY = this.touchEndY - this.touchStartY;
      const absDeltaX = Math.abs(deltaX);
      const absDeltaY = Math.abs(deltaY);
      
      //判斷是否為有效的向右滑動
      // 條件：水平滑動距離大於垂直滑動距離，且水平滑動距離足夠大（50px），且方向為向右
      if (absDeltaX > absDeltaY && absDeltaX > 50 && deltaX > 0) {
        // 回到首頁
        this.$router.push('/');
      }
    },
    
    // 獲取學生手冊列表（從class_log表）
    async fetchHandbookList() {
      this.loading = true
      try {
        // 使用封裝的service實例，確保攜帶token
        const response = await service.get(API_ENDPOINTS.STUDENT_HANDBOOK_LIST)
        
        // 根據後端返回的數據結構處理數據（現在是class_log表的結構）
        let rawData = [];
        if (response.data && response.data.rows) {
          rawData = response.data.rows;
        } else if(Array.isArray(response.data)) {
          // 如果後端直接返回數組
          rawData = response.data;
        } else {
          // 如果是其他結構，嘗試直接使用
          rawData = response.data;
        }
        
        // 按時間分組數據
        console.log('原始數據:', rawData);
        this.groupDataByTime(rawData);
        
        // 根據當前視圖模式過濾數據
        if (this.viewMode === 'nextSevenDays') {
          this.showNextSevenDaysDataInner();
        } else {
          // 默認為顯示今天數據
          this.showTodayDataInner();
        }
        
        console.log('獲取到的數據:', response.data)
      } catch (error) {
        console.error('獲取學生手冊列表失敗:', error)
        ElMessage.error('獲取數據失敗: ' + (error.message || '未知錯誤'))
        // 使用空數組，不顯示示例數據
        this.groupDataByTime([]);
        // 即使出现错误也要应用当天视图
        this.showTodayDataInner();
      } finally {
        this.loading = false
      }
    },
    
    // 根據日期篩選數據：當前頁顯示今天，下頁顯示未來7天
    filterDataByDate() {
      const todayData = [];
      const nextSevenDaysData = [];
      
      this.allGroupedHandbookList.forEach(item => {
        if (this.isToday(item.timeRange)) {
          todayData.push(item);
        } else if (this.isInNextSevenDays(item.timeRange)) {
          nextSevenDaysData.push(item);
        }
      });
      
      // 更新數據為今天和未來7天的數據
      this.allGroupedHandbookList = [...todayData, ...nextSevenDaysData];
      
      // 重置到第一頁
      this.currentPage = 1;
    },
    
    //按時間分組數據
    groupDataByTime(data) {
      const grouped = {};
      
      console.log('處理每項數據:', data);
      //按時間分組，使用class_log表的字段
      data.forEach(item => {
        // 过滤非'功課'和'測驗'类型的条目
        if (item.courseType !== '功課' && item.courseType !== '測驗') {
          return;
        }
        
        // 使用startDate作为分组键
        const timeKey = item.startDate || item.updateDate || '未設定日期'; 
        if (!grouped[timeKey]) {
          grouped[timeKey] = {
            timeRange: timeKey, // 使用日期作为卡片标题
            entries: [],
            categories: {} // 用於存儲類別分組
          };
        }
        
        //添加條目到總列表
        grouped[timeKey].entries.push({
          subject: item.course || '未設定課程',
          content: item.content || '無內容',
          category: item.courseType || '未分類'
        });
        
        //按類別分組
        const category = item.courseType || '未分類';
        if (!grouped[timeKey].categories[category]) {
          grouped[timeKey].categories[category] = {
            category: category,
            entries: []
          };
        }
        grouped[timeKey].categories[category].entries.push({
          subject: item.course || '未設定課程',
          content: item.content || '無內容',
          category: item.courseType || '未分類'
        });
      });
      
      //轉換為數組並按時間順序排序（從早到晚）
      this.allGroupedHandbookList = Object.values(grouped).sort((a, b) => {
        // 將日期字符串轉換為實際日期對象進行比較
        const dateA = this.parseDate(a.timeRange);
        const dateB = this.parseDate(b.timeRange);
        return dateA - dateB;
      });
      
      //對每個時間分組內的類別進行排序，並將類別對象轉換為數組
      this.allGroupedHandbookList.forEach(item => {
        // 只保留'功課'和'測驗'类别，过滤掉其他类别
        item.categoryGroups = Object.values(item.categories).filter(categoryGroup => {
          return categoryGroup.category === '功課' || categoryGroup.category === '測驗';
        });
        
        // 對類別進行排序
        item.categoryGroups.sort((a, b) => {
          // 确保'測驗'排在前面，然后是'功課'
          if(a.category === '測驗' && b.category !== '測驗') return -1;
          if(b.category === '測驗' && a.category !== '測驗') return 1;
          if(a.category === '功課' && b.category !== '功課') return -1;
          if(b.category === '功課' && a.category !== '功課') return 1;
          return a.category.localeCompare(b.category);
        });
        
        // 對每個類別內的條目進行排序
        item.categoryGroups.forEach(categoryGroup => {
          categoryGroup.entries.sort((a, b) => {
            //先按科目排序
            const subjectComparison = a.subject.localeCompare(b.subject);
            if (subjectComparison !== 0) {
              return subjectComparison;
            }
            // 如果科目相同，按內容排序
            return a.content.localeCompare(b.content);
          });
        });
      });
      
      // 重置到第一頁
      this.currentPage = 1;
    },
    
    // 檢查是否是今天
    isToday(dateString) {
      const today = new Date();
      const checkDate = this.parseDate(dateString);
      
      return today.getDate() === checkDate.getDate() &&
             today.getMonth() === checkDate.getMonth() &&
             today.getFullYear() === checkDate.getFullYear();
    },
    
    // 檢查是否是未來7天內
    isInNextSevenDays(dateString) {
      const today = new Date();
      const checkDate = this.parseDate(dateString);
      
      // 設置今天開始時間
      const startDate = new Date(today);
      startDate.setHours(0, 0, 0, 0);
      
      // 設置7天後的時間
      const endDate = new Date(today);
      endDate.setDate(today.getDate() + 7);
      endDate.setHours(23, 59, 59, 999);
      
      return checkDate >= startDate && checkDate <= endDate;
    },

    // 解析日期字符串為Date對象的輔助函數
    parseDate(dateString) {
      // 支持多種日期格式：dd/mm/yyyy, yyyy-mm-dd, yyyy-mm-dd HH:MM:SS
      if (!dateString) return new Date();
      
      // 如果是包含时间的完整日期格式 (yyyy-mm-dd HH:MM:SS)
      if (typeof dateString === 'string' && dateString.includes(':')) {
        return new Date(dateString);
      }
      
      // 如果是 dd/mm/yyyy 格式
      if (typeof dateString === 'string' && dateString.includes('/')) {
        const parts = dateString.split('/');
        const day = parseInt(parts[0], 10);
        const month = parseInt(parts[1], 10) - 1; // 月份從0開始
        const year = parseInt(parts[2], 10);
        return new Date(year, month, day);
      }
      
      // 如果是 yyyy-mm-dd 格式
      if (typeof dateString === 'string' && dateString.includes('-')) {
        return new Date(dateString);
      }
      
      // 默认返回当前日期
      return new Date();
    },
    
    // 顯示當天數據
    showTodayData() {
      this.viewMode = 'today';
      this.activeButton = 'today'; // 更新活動按鈕狀態
      // 重新獲取數據，這會觸發相應的過濾
      this.fetchHandbookList();
      // 滾動到頂部
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    },
    
    // 顯示未來七天數據
    showNextSevenDaysData() {
      this.viewMode = 'nextSevenDays';
      this.activeButton = 'future'; // 更新活動按鈕狀態
      // 重新獲取數據，這會觸發相應的過濾
      this.fetchHandbookList();
      // 滾動到頂部
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    },
    
    // 顯示當天數據（內部函數）
    showTodayDataInner() {
      const todayData = [];
      
      this.allGroupedHandbookList.forEach(item => {
        if (this.isToday(item.timeRange)) {
          todayData.push(item);
        }
      });
      
      // 更新數據為今天數據
      this.allGroupedHandbookList = todayData;
      
      // 重置到第一頁
      this.currentPage = 1;
    },
    
    // 顯示未來七天數據（內部函數）
    showNextSevenDaysDataInner() {
      const nextSevenDaysData = [];
      
      this.allGroupedHandbookList.forEach(item => {
        if (this.isInNextSevenDays(item.timeRange)) {
          nextSevenDaysData.push(item);
        }
      });
      
      // 更新數據為未來七天的數據
      this.allGroupedHandbookList = nextSevenDaysData;
      
      // 重置到第一頁
      this.currentPage = 1;
    },
    
    // 上一页 - 显示当天数据
    prevPage() {
      this.currentPage = 1; // 跳转到第一页，显示当天数据
    },
    
    // 下一页 - 显示未来10天数据
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    }
  }
}
</script>

<style scoped>
.student-handbook {
  padding: 0 !important;
  margin: 0 !important;
  position: relative;
  top: 0;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%) !important; /* 更淺的藍色漸變背景 */
  min-height: 100vh;
}

.header-container {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #7dd3fc 0%, #bae6fd 100%); /* 更淺的藍色漸變 */
  padding: 15px 30px; /* 減少上方內邊距 */
  box-shadow: 0 4px 6px rgba(125, 211, 252, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
  justify-content: center; /* 讓內容居中 */
}

.title-and-buttons {
  display: flex;
  align-items: center;
  justify-content: center; /* 內容居中對齊 */
  width: 100%;
  max-width: 800px; /* 限制最大寬度 */
  gap: 15px; /* 組件間距 */
}

.page-title {
  margin: 0 0 5px 0 !important; /* 按照項目規範設置與卡片內容間距為5px */
  font-size: 32px; /* 按照規範增大字體 */
  font-weight: 700;
  color: #0284c7; /* 淺藍色文字 */
  padding: 0 !important; /* 移除標題內邊距 */
  white-space: nowrap;
  flex-grow: 1; /* 擴展佔據空間，幫助居中 */
  text-align: center; /* 文字居中 */
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.5); /* 淺色陰影 */
}

.home-btn {
  margin-right: 0; /* 移除固定間距，使用gap控制 */
  padding: 12px 18px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%); /* 橙色漸層 */
  color: #92400e; /* 深橙色文字 */
  border: none;
  box-shadow: 0 4px 6px rgba(245, 158, 11, 0.2);
  transition: all 0.3s ease;
  white-space: nowrap;
  font-weight: 600;
  font-size: 15px;
}

.home-btn:hover {
  background: linear-gradient(135deg, #fbbf24 0%, #fcd34d 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 10px rgba(245, 158, 11, 0.3);
}

.user-switch-btn {
  margin-right: 0; /* 移除固定間距，使用gap控制 */
  padding: 12px 18px;
  border-radius: 8px;
  background: linear-gradient(135deg, #2563eb 0%, #dbeafe 100%); /* 按照項目規範的淺藍色漸變 */
  color: #1e3a8a; /* 按照項目規範的深藍色文字 */
  border: none;
  box-shadow: 0 4px 6px rgba(147, 197, 253, 0.2);
  transition: all 0.3s ease;
  white-space: nowrap;
  font-weight: 600;
  font-size: 15px;
}

.user-switch-btn:hover {
  background: linear-gradient(135deg, #dbeafe 0%, #eff6ff 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 10px rgba(147, 197, 253, 0.3);
}

.navigation-buttons {
  display: flex;
  gap: 12px;
  flex-shrink: 0; /* 防止按鈕容器收縮 */
}

.handbook-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
  margin: 15px 0 50px 0 !important; /* 減少上方邊距 */
  padding: 0 25px 0 25px; /* 移除底部內邊距，由按鈕提供空間 */
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%) !important; /* 更淺的藍色漸變背景 */
}

.handbook-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); /* 更輕微的陰影 */
  transition: all 0.3s ease;
  transform: translateY(0);
  background: white;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%;
  border: none;
  position: relative;
  overflow: visible;
}

.handbook-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #7dd3fc, #bae6fd, #bae6fd); /* 更淺的藍色條 */
  z-index: 1;
}

.handbook-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 20px rgba(125, 211, 252, 0.2); /* 淺藍色陰影 */
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center; /* 按照項目規範設置居中對齊 */
  padding: 20px;
  border-bottom: 2px solid #606266; /* 按照項目規範設置分割線 */
  background: linear-gradient(135deg, #7dd3fc 0%, #bae6fd 100%); /* 更淺的藍色漸變 */
  color: #0284c7; /* 淺藍色文字 */
}

.card-title {
  margin: 0;
  font-size: 24px; /* 按照規範增大字體 */
  font-weight: 700;
  color: #0284c7; /* 淺藍色文字 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center; /* 按照項目規範設置居中對齊 */
}

.card-content {
  padding: 25px; /* 按照規範增加內邊距 */
  text-align: left; /* 按照規範設置左對齊 */
  flex-grow: 1;
}

.category-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 12px 0;
  gap: 10px;
}

.category-row {
  display: flex;
  justify-content: flex-start;
  width: 100%;
}

.category-badge {
  background: #409eff; /* 普通類別使用藍色背景 */
  color: white; /* 白色文字 */
  padding: 8px 16px;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600; /* 粗體 */
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(125, 211, 252, 0.2);
}

.category-badge:hover {
  transform: scale(1.05);
}

/* 測驗/考試類別的特殊樣式 */
.exam-badge {
  background: #e91e63 !important; /* 粉紅色背景 */
  color: white; /* 白色文字 */
  font-weight: 600; /* 粗體 */
}

.card-field {
  display: flex;
  margin-bottom: 16px;
  font-size: 16px; /* 按照規範增大字體 */
  text-align: left;
  line-height: 1.6;
  padding: 10px 0;
  border-bottom: 1px solid #e0f2fe; /* 使用更協調的淺藍色分割線 */
  transition: all 0.3s ease;
  border-radius: 6px;
  background: #f8fafc;
  padding-left: 15px;
}

.card-field:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.field-value {
  flex: 1;
  color: #475569;
  transition: all 0.3s ease;
  font-weight: 500;
}

.handbook-card:hover .field-value {
  color: #0284c7; /* 懸停時文字變成淺藍色 */
  transform: translateX(5px);
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 50px 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin: 20px;
}

/* 回到頂部按鈕樣式 */
.back-to-top {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background: linear-gradient(135deg, #7dd3fc 0%, #bae6fd 100%); /* 更淺的藍色漸變 */
  color: #0284c7; /* 淺藍色文字 */
  border-radius: 0; /* 長方形 */
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 -2px 8px rgba(125, 211, 252, 0.3);
  z-index: 1000;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 16px;
}

.back-to-top:hover {
  background: linear-gradient(135deg, #bae6fd 0%, #e0f2fe 100%);
  box-shadow: 0 -4px 12px rgba(125, 211, 252, 0.4);
}

.back-to-top span {
  color: #0284c7; /* 淺藍色文字 */
}

/* 導航按鈕樣式 */
.nav-arrow {
  padding: 12px 18px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 15px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #60a5fa 0%, #93c5fd 100%); /* 按照用戶要求的新漸變配色 */
  color: #1e40af; /* 深藍色文字 */
  border: none;
  box-shadow: 0 4px 6px rgba(96, 165, 250, 0.2);
  white-space: nowrap; /* 防止文字換行 */
  min-width: auto; /* 避免按鈕過大 */
}

.nav-arrow:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  opacity: 0.6;
  color: #6b7280;
}

.nav-arrow.active {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%); /* 統一的高亮藍色漸層 */
  color: white; /* 白色文字提高對比度 */
  box-shadow: 0 6px 12px rgba(37, 99, 235, 0.4);
  transform: translateY(-2px);
}

.prev-button:hover, .next-button:hover {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  transform: translateY(-3px);
  box-shadow: 0 6px 10px rgba(37, 99, 235, 0.3);
}

/* 手機端優化 */
@media (max-width: 768px) {
  .handbook-container {
    padding: 0 15px 30px;
    gap: 20px;
  }
  
  .handbook-card {
    border-radius: 10px;
  }
  
  .page-title {
    font-size: 26px;
  }
  
  .card-title {
    font-size: 20px;
  }
  
  .card-content {
    padding: 20px;
  }
  
  .category-badge {
    padding: 6px 14px;
    font-size: 13px;
  }
  
  .card-field {
    font-size: 15px;
  }
}
</style>