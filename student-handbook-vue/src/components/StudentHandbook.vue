<template>
  <div class="student-handbook">
    <div class="header-container">
      <!-- 导航栏按钮 -->
      <div class="nav-button-container">
        <el-button class="nav-button" type="primary" icon="Menu" @click="toggleNavigation"></el-button>
      </div>
      
      <!-- 页面标题 -->
      <h2 class="page-title">學生手冊</h2>
      
      <!-- 占位元素，用于保持标题在导航按钮右侧 -->
      <div class="spacer"></div>
    </div>
    
    <!-- 卡片式数据展示 -->
    <div class="handbook-container" v-loading="loading">
      <div 
        class="handbook-card"
        v-for="(item, index) in groupedHandbookList" 
        :key="index"
        :style="{ backgroundColor: getCardBackgroundColor(index) }"
      >
        <div class="card-header">
          <h3 class="card-title">{{ item.timeRange }}</h3>
        </div>
        
        <div class="card-content">
          <div 
            class="card-field" 
            v-for="(entry, entryIndex) in item.entries" 
            :key="entryIndex"
            :class="{ 'exam-field': entry.category === '測驗' || entry.category === '考试' }"
          >
            <span class="field-value">{{ entry.subject }} ： {{ entry.content }}</span>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="groupedHandbookList.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无数据" />
      </div>
    </div>
    
    <!-- 回到顶部按钮 -->
    <div class="back-to-top" v-show="showBackToTop" @click="scrollToTop">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 8L6 14L7.41 15.41L12 10.83L16.59 15.41L18 14L12 8Z" fill="currentColor"/>
      </svg>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'StudentHandbook',
  components: {
  },
  data() {
    return {
      loading: false,
      handbookList: [],
      groupedHandbookList: [],
      dragItem: null,
      isMobile: false,
      showBackToTop: false,
      showNavigation: false // 控制导航菜单的显示
    }
  },
  mounted() {
    this.checkIsMobile()
    this.fetchHandbookList()
    window.addEventListener('resize', this.checkIsMobile)
    // 添加滚动事件监听器
    window.addEventListener('scroll', this.handleScroll)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.checkIsMobile)
    // 移除滚动事件监听器
    window.removeEventListener('scroll', this.handleScroll)
  },
  methods: {
    // 检查是否为移动设备
    checkIsMobile() {
      this.isMobile = window.innerWidth < 768
    },
    
    // 切换导航菜单显示状态
    toggleNavigation() {
      this.showNavigation = !this.showNavigation
    },
    
    // 处理滚动事件，控制回到顶部按钮的显示
    handleScroll() {
      // 当滚动超过300px时显示回到顶部按钮
      this.showBackToTop = window.pageYOffset > 300
    },
    
    // 滚动到顶部
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth' // 平滑滚动
      })
    },
    
    // 获取学生手册列表
    async fetchHandbookList() {
      this.loading = true
      try {
        // 使用 /api 前缀以便通过 Vite 代理转发到后端
        const response = await axios.get('/api/system/handbook/list')
        
        // 根据后端返回的数据结构处理数据
        let rawData = [];
        if (response.data.rows) {
          rawData = response.data.rows;
        } else if (Array.isArray(response.data)) {
          // 如果后端直接返回数组
          rawData = response.data;
        } else {
          // 如果是其他结构，尝试直接使用
          rawData = response.data;
        }
        
        // 按时间分组数据
        this.groupDataByTime(rawData);
        
        console.log('获取到的数据:', response.data)
      } catch (error) {
        console.error('获取学生手册列表失败:', error)
        this.$message.error('获取数据失败: ' + (error.message || '未知错误'))
        // 使用空数组，不显示示例数据
        this.groupDataByTime([]);
      } finally {
        this.loading = false
      }
    },
    
    // 按时间分组数据
    groupDataByTime(data) {
      const grouped = {};
      
      // 按时间分组
      data.forEach(item => {
        const timeKey = item.startTime; // 只使用开始时间作为分组键
        if (!grouped[timeKey]) {
          grouped[timeKey] = {
            timeRange: item.startTime, // 卡片标题只显示开始时间
            entries: []
          };
        }
        grouped[timeKey].entries.push({
          subject: item.subject,
          content: item.content,
          category: item.category // 添加类别信息
        });
      });
      
      // 转换为数组并按时间顺序排序（从早到晚）
      this.groupedHandbookList = Object.values(grouped).sort((a, b) => {
        // 将日期字符串转换为实际日期对象进行比较
        const dateA = this.parseDate(a.timeRange);
        const dateB = this.parseDate(b.timeRange);
        return dateA - dateB;
      });
    },

    // 解析日期字符串为Date对象的辅助函数
    parseDate(dateString) {
      // 假设日期格式为 dd/mm/yyyy 或 d/m/yyyy
      const parts = dateString.split('/');
      const day = parseInt(parts[0], 10);
      const month = parseInt(parts[1], 10) - 1; // 月份从0开始
      const year = parseInt(parts[2], 10);
      return new Date(year, month, day);
    },

    // 获取卡片背景颜色
    getCardBackgroundColor(index) {
      // 定义两组交替的浅色背景颜色，与整体背景协调
      const colors = [
        '#e3f2fd', // 柔和的浅蓝色 1
        '#f3e5f5'  // 柔和的浅紫色
      ];
      
      // 使用索引循环选择颜色
      return colors[index % colors.length];
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
}

.header-container {
  display: flex;
  align-items: center;
  background-color: #f5f9ff;
  padding: 0 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-button-container {
  flex: 0 0 auto;
  margin-right: 30px; /* 增加按钮与标题之间的间距 */
}

.page-title {
  margin: 0 !important;
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  padding: 15px 0 !important;
  white-space: nowrap;
}

.spacer {
  flex: 1;
}

.handbook-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
  margin-top: 20px !important;
  padding-top: 0 !important;
  position: relative;
  top: 0;
  padding: 0 15px;
  background-color: #f5f9ff;
}

/* 回到顶部按钮样式 */
.back-to-top {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 50px;
  height: 50px;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transition: all 0.3s ease;
}

.back-to-top:hover {
  background-color: #66b1ff;
  transform: translateY(-2px);
}

.back-to-top svg {
  /* 移除之前的旋转，因为现在使用了正确的向上箭头图标 */
}

/* 平板设备优化 */
@media (max-width: 1024px) {
  .handbook-container {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 8px;
  }
  
  .page-title {
    margin: 0 0 8px 0;
    padding: 4px 0;
  }
}

/* 手机设备优化 */
@media (max-width: 768px) {
  .handbook-container {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .page-title {
    font-size: 20px;
    margin: 0 0 6px 0;
    padding: 3px 0;
  }
  
  .card-content {
    padding: 12px;
  }
  
  .card-header {
    padding: 12px;
  }
  
  .card-field {
    margin-bottom: 8px;
  }
}

.handbook-card {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  background-color: #fff;
}

.handbook-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.card-header {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 2px solid #606266;
}

.card-title {
  flex: 1;
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center;
}

.card-content {
  padding: 20px;
  text-align: left;
}

.card-field {
  display: flex;
  margin-bottom: 12px;
  font-size: 18px;
  text-align: left;
  line-height: 1.5;
}

/* 测验/考试类别的特殊样式 */
.exam-field {
  background-color: #f8bbd0 !important; /* 粉红色背景用于突出显示测验/考试 */
  padding: 4px !important;
  border-radius: 4px !important;
}

.card-field:last-child {
  margin-bottom: 0;
}

.field-value {
  flex: 1;
  color: #606266;
}

.content-text {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 20px 0;
}

/* 手机端分页优化 */
@media (max-width: 768px) {

}

</style>