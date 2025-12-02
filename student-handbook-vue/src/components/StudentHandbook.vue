<template>
  <div class="student-handbook" 
       @touchstart="handleTouchStart"
       @touchmove="handleTouchMove"
       @touchend="handleTouchEnd">
    <div class="header-container">
      <!-- 导航栏按钮 -->
      <div class="nav-button-container" style="display: none;">
        <el-button class="nav-button" type="primary" icon="Menu" @click="toggleNavigation"></el-button>
      </div>
      
      <!-- 页面标题 -->
      <h2 class="page-title">學生手冊</h2>
      
      <!-- 左右方向按钮 -->
<div class="navigation-buttons">
        <el-button class="nav-arrow prev-button" type="primary" icon="ArrowLeft" @click="prevPage" :disabled="currentPage === 1"></el-button>
        <el-button class="nav-arrow next-button" type="primary" icon="ArrowRight"@click="nextPage" :disabled="currentPage >= totalPages"></el-button>
      </div>
    </div>
    
    <!-- 卡片式数据展示 -->
    <div class="handbook-container" v-loading="loading">
      <div 
        class="handbook-card"
        v-for="(item,index) in paginatedGroupedHandbookList" 
        :key="index"
        :style="{ backgroundColor: getCardBackgroundColor(index) }"
      >
        <div class="card-header">
          <h3 class="card-title">{{ item.timeRange }}</h3>
        </div>
        
        <div class="card-content">
          <!-- 按类别分组显示条目 -->
          <div v-for="(categoryGroup, categoryIndex) in item.categoryGroups" 
               :key="categoryIndex"
               class="category-group">
            <!-- 类别标签 -->
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
            
            <!-- 该类别下的条目 -->
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
      
      <!-- 空状态 -->
      <div v-if="paginatedGroupedHandbookList.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无数据" />
      </div>
    </div>
    
    <!-- 回到顶部按钮 -->
    <div class="back-to-top" v-show="showBackToTop" @click="scrollToTop">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 8L6 14L7.41 15.41L12 10.83L16.59 15.41L18 14L12 8Z" fill="currentColor"/>
      </svg>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { API_ENDPOINTS } from '@/config/api.js'

export default {
  name: 'StudentHandbook',
  components: {
  },
  data() {
    return {
      loading: false,
      allGroupedHandbookList: [], // 存储所有分组后的数据
      currentPage: 1, // 当前页码
      pageSize:7, //每页显示条数
      isMobile: false,
      showBackToTop: false,
      showNavigation: false, // 控制导航菜单的显示
      
      // 滑动相关数据
      touchStartX: 0,
      touchStartY: 0,
      touchEndX: 0,
      touchEndY: 0
    }
  },
  computed: {
    // 计算总页数
    totalPages() {
      return Math.ceil(this.allGroupedHandbookList.length / this.pageSize);
    },
    
    // 计算当前页需要显示的数据
paginatedGroupedHandbookList() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = startIndex + this.pageSize;
      return this.allGroupedHandbookList.slice(startIndex, endIndex);
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
    //检查是否为移动设备
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
    
    // 触摸开始事件
    handleTouchStart(event) {
      const touch = event.touches[0];
      this.touchStartX= touch.clientX;
      this.touchStartY = touch.clientY;
    },
    
    // 触摸移动事件
    handleTouchMove(event) {
      // 可以在这里添加一些视觉反馈
    },
    
    // 触摸结束事件
    handleTouchEnd(event) {
      const touch = event.changedTouches[0];
      this.touchEndX = touch.clientX;
      this.touchEndY = touch.clientY;
      
      this.handleSwipeGesture();
    },
    
    // 处理滑动手势
    handleSwipeGesture() {
      const deltaX = this.touchEndX - this.touchStartX;
      const deltaY= this.touchEndY - this.touchStartY;
      const absDeltaX = Math.abs(deltaX);
      const absDeltaY = Math.abs(deltaY);
      
      // 判断是否为有效的向右滑动
      // 条件：水平滑动距离大于垂直滑动距离，且水平滑动距离足够大（50px），且方向为向右
      if (absDeltaX > absDeltaY && absDeltaX > 50 && deltaX > 0) {
        // 回到首页
        this.$router.push('/');
      }
    },
    
    // 获取学生手册列表
async fetchHandbookList() {
      this.loading = true
      try {
        // 使用配置文件中的API端点
        const response = await axios.get(API_ENDPOINTS.STUDENT_HANDBOOK_LIST)
        
        // 根据后端返回的数据结构处理数据
        let rawData = [];
       if (response.data.rows) {
          rawData = response.data.rows;
        } else if(Array.isArray(response.data)) {
          // 如果后端直接返回数组
          rawData = response.data;
        } else {
          // 如果是其他结构，尝试直接使用
          rawData = response.data;
        }
        
        //按时间分组数据
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
      
      //按时间分组
      data.forEach(item => {
        const timeKey = item.startTime; // 只使用开始时间作为分组键
        if (!grouped[timeKey]) {
          grouped[timeKey] = {
            timeRange: item.startTime, // 卡片标题只显示开始时间
            entries:[],
            categories: {} // 用于存储类别分组
          };
        }
        
        // 添加条目到总列表
        grouped[timeKey].entries.push({
          subject: item.subject,
          content: item.content,
          category: item.category
        });
        
        //按类别分组
       const category = item.category || '未分类';
        if (!grouped[timeKey].categories[category]) {
          grouped[timeKey].categories[category] = {
            category: category,
            entries: []
          };
        }
        grouped[timeKey].categories[category].entries.push({
          subject:item.subject,
          content: item.content,
          category: item.category
        });
      });
      
      // 转换为数组并按时间顺序排序（从早到晚）
      this.allGroupedHandbookList = Object.values(grouped).sort((a, b) => {
        // 将日期字符串转换为实际日期对象进行比较
        const dateA = this.parseDate(a.timeRange);
        const dateB = this.parseDate(b.timeRange);
        return dateA - dateB;
      });
      
      // 对每个时间分组内的类别进行排序，并将类别对象转换为数组
     this.allGroupedHandbookList.forEach(item => {
        // 转换类别对象为数组
        item.categoryGroups = Object.values(item.categories);
        
        // 对类别进行排序
        item.categoryGroups.sort((a, b) => {
          //确保"未分类"排在最后
          if(a.category === '未分类') return 1;
          if (b.category === '未分类') return -1;
          return a.category.localeCompare(b.category);
        });
        
        // 对每个类别内的条目进行排序
        item.categoryGroups.forEach(categoryGroup => {
          categoryGroup.entries.sort((a, b) => {
            // 先按科目排序
            const subjectComparison = a.subject.localeCompare(b.subject);
            if (subjectComparison !== 0) {
              return subjectComparison;
            }
            // 如果科目相同，按内容排序
            return a.content.localeCompare(b.content);
          });
        });
});
      
      // 重置到第一页
      this.currentPage = 1;
    },

    // 解析日期字符串为Date对象的辅助函数
    parseDate(dateString) {
      // 假设日期格式为 dd/mm/yyyy或 d/m/yyyy
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
        '#e3f2fd', // 柢和的浅蓝色 1
        '#f3e5f5'  //柢和的浅紫色
      ];
      // 使用索引循环选择颜色
      return colors[index % colors.length];
    },
    
    // 上一页
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        // 滚动到顶部
        window.scrollTo({
          top: 0,
          behavior: 'smooth'
        });
      }
    },
    // 下一页
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        // 滚动到顶部
        window.scrollTo({
          top: 0,
          behavior: 'smooth'
        });
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
  background-color: #f5f9ff !important; /* 使用更柔和的浅蓝灰色背景 */
}

.header-container {
  display: flex;
  align-items: center;
  background-color: #f5f9ff; /* 使用更柔和的浅蓝灰色背景 */
  padding: 0 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-title {
  margin: 0 !important;
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  padding: 10px 0 !important;
  white-space: nowrap;
  flex-grow: 1; /* 使标题占据剩余空间 */
  text-align: center; /* 标题居中 */
}

.handbook-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 15px;/* 减小卡片之间的间距 */
  margin: 5px 0 !important; /* 统一上下边距为5px */
  padding: 0 15px;
  background-color: #f5f9ff!important; /* 使用更柔和的浅蓝灰色背景 */
}

.handbook-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0,0.1);
  transition: all 0.3s ease;
  transform: translateY(0);
}

.handbook-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center; /* 添加这行使日期水平居中 */
  padding: 10px; /* 减小卡片头部内边距 */
  border-bottom: 2px solid #606266;
}

.card-title {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center;
}

.card-content{
  padding: 10px; /* 减小卡片内容区内边距 */
  text-align: left;
}

.category-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 4px 0; /*减小类别容器间距 */
  gap:4px; /* 减小类别标签间距 */
}

.category-row{
  display: flex;
  justify-content: center;
}

.category-badge {
  background-color: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: bold;
transition: all 0.3s ease;
}

.handbook-card:hover .category-badge {
  transform: scale(1.05);
}

/* 测验/考试类别的特殊样式 */
.exam-badge {
  background-color: #f44336 !important; /* 红色背景用于突出显示测验/考试类别 */
}

.card-field {
  display: flex;
  margin-bottom: 15px; /* 减小条目间距 */
  font-size: 18px;
  text-align: left;
  line-height: 1.5;
}

.card-field:last-child {
margin-bottom: 0;
}

.field-value {
  flex: 1;
  color: #606266;
  transition: all 0.3s ease;
}

.handbook-card:hover .field-value {
  color: #333;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 20px 0;
}

/* 回到顶部按钮样式 */
.back-to-top {
  position: fixed;
  bottom: 10px;
  right: 10px;
  width: 40px;
  height: 40px;
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

/*导航按钮样式 */
.navigation-buttons {
  display: flex;
  margin-left: auto;
  gap: 10px;
}

.nav-arrow {
  padding: 8px 12px;
}

.prev-button, .next-button {
  background-color: #409eff;
  border-color: #409eff;
}

.prev-button:hover, .next-button:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

/*手机端分页优化 */
</style>