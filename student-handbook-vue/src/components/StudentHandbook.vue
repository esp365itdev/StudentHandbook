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
      groupedHandbookList: [],
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
            entries: [],
            categories: {} // 用于存储类别分组
          };
        }
        
        // 添加条目到总列表
        grouped[timeKey].entries.push({
          subject: item.subject,
          content: item.content,
          category: item.category
        });
        
        // 按类别分组
        const category = item.category || '未分类';
        if (!grouped[timeKey].categories[category]) {
          grouped[timeKey].categories[category] = {
            category: category,
            entries: []
          };
        }
        grouped[timeKey].categories[category].entries.push({
          subject: item.subject,
          content: item.content,
          category: item.category
        });
      });
      
      // 转换为数组并按时间顺序排序（从早到晚）
      this.groupedHandbookList = Object.values(grouped).sort((a, b) => {
        // 将日期字符串转换为实际日期对象进行比较
        const dateA = this.parseDate(a.timeRange);
        const dateB = this.parseDate(b.timeRange);
        return dateA - dateB;
      });
      
      // 对每个时间分组内的类别进行排序，并将类别对象转换为数组
      this.groupedHandbookList.forEach(item => {
        // 转换类别对象为数组
        item.categoryGroups = Object.values(item.categories);
        
        // 对类别进行排序
        item.categoryGroups.sort((a, b) => {
          // 确保"未分类"排在最后
          if (a.category === '未分类') return 1;
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
  background-color: #eef5ff !important; /* 调整为与卡片更搭配的浅蓝背景 */
}

.header-container {
  display: flex;
  align-items: center;
  background-color: #eef5ff; /* 调整为与卡片更搭配的浅蓝背景 */
  padding: 0 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-button-container {
  flex: 0 0 auto;
  margin-right: 20px; /* 调整按钮与标题之间的间距为20px */
}

.page-title {
  margin: 0 !important;
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  padding: 10px 0 !important; /* 减小标题的上下内边距 */
  white-space: nowrap;
}

.handbook-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 15px; /* 减小卡片之间的间距 */
  margin: 5px 0 !important; /* 统一上下边距为5px */
  padding: 0 15px;
  background-color: #eef5ff !important; /* 调整为与卡片更搭配的浅蓝背景 */
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center; /* 添加这行使日期水平居中 */
  padding: 15px; /* 减小卡片头部内边距 */
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

.card-content {
  padding: 15px; /* 减小卡片内容区内边距 */
  text-align: left;
}

.card-field {
  display: flex;
  margin-bottom: 15px; /* 减小条目间距 */
  font-size: 18px;
  text-align: left;
  line-height: 1.5;
}

.category-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 8px 0; /* 统一类别容器间距 */
  gap: 8px; /* 统一类别标签间距 */
}

.category-row {
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
}

/* 测验/考试类别的特殊样式 */
.exam-badge {
  background-color: #f44336 !important; /* 红色背景用于突出显示测验/考试类别 */
}

.card-field:last-child {
  margin-bottom: 0;
}

.field-value {
  flex: 1;
  color: #606266;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 20px 0;
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

/* 手机端分页优化 */
</style>