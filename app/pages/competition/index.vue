<template>
  <view class="competition-page">
    

    <!-- 赛事列表内容 -->
    <view class="competition-content">
      <!-- 初始加载状态 -->
      

      <!-- 赛事列表组件 -->
      <CompetitionList 
         
        :competitionList="competitionList"
        :loading="loading"
        :showLoadMore="hasMore"
        @itemClick="handleCompetitionClick"
        @loadMore="handleLoadMore"
      />

     
    </view>
  </view>
</template>

<script>
import CompetitionList from '../../components/CompetitionList.vue';
import { getCompetitionList } from '../../api/home.js';

export default {
  name: 'CompetitionPage',
  components: {
    CompetitionList
  },
  
  data() {
    return {
      // 赛事列表数据
      competitionList: [],
      currentPage: 0,
      pageSize: 10,
      hasMore: true,
      loading: false,
      refreshing: false,
      
      // 筛选状态
      currentTab: 'all',
      
      // 筛选标签
      filterTabs: [
        { label: '全部', value: 'all' },
        { label: '报名中', value: 'REGISTRATION_OPEN' },
        { label: '进行中', value: 'IN_PROGRESS' },
        { label: '已结束', value: 'FINISHED' }
      ]
    }
  },

  onLoad() {
    
  },

  onShow() {
    console.log('赛事页面加载');
    this.initPage();
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.handleRefresh();
  },

  // 上拉加载更多
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.handleLoadMore();
    }
  },

  methods: {
    /**
     * 初始化页面
     */
    async initPage() {
      console.log('初始化赛事页面');
      await this.loadCompetitionList(true);
    },

    /**
     * 加载赛事列表
     */
    async loadCompetitionList(reset = false) {
      if (this.loading) return;

      try {
        this.loading = true;
        
        if (reset) {
          this.currentPage = 1;
          this.competitionList = [];
          this.hasMore = true;
        }
        if(!reset){
          this.currentPage++;
        }
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          enabled: true,
           
          descFields: 'startTime'
        };

        
 
        const result = await getCompetitionList(params);
      

        if (result && result.records) {
          const newList = result.records;
          
          if (reset) {
            this.competitionList = newList;
          } else {
            this.competitionList = [...this.competitionList, ...newList];
          }

          // 判断是否还有更多数据
          this.hasMore = result.current < result.pages;
         
        }

      } catch (error) {
        console.error('加载赛事列表失败:', error);
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        });
      } finally {
        this.loading = false;
        
        // 停止下拉刷新
        if (this.refreshing) {
          uni.stopPullDownRefresh();
          this.refreshing = false;
        }
      }
    },

    /**
     * 处理赛事点击
     */
    handleCompetitionClick(item) {
      console.log('点击赛事:', item);
      
      // 跳转到赛事详情页
      uni.navigateTo({
        url: `/pages/competition/detail?id=${item.id}`,
        fail: (error) => {
          console.error('跳转赛事详情失败:', error);
          uni.showToast({
            title: '功能开发中',
            icon: 'none'
          });
        }
      });
    },

    /**
     * 加载更多
     */
    async handleLoadMore() {
      if (!this.hasMore || this.loading) return;
      
      console.log('加载更多赛事');
      await this.loadCompetitionList(false);
    },

    /**
     * 下拉刷新
     */
    async handleRefresh() {
      console.log('刷新赛事列表');
      this.refreshing = true;
      await this.loadCompetitionList(true);
    },

    /**
     * 刷新数据
     */
    async refreshData() {
      await this.loadCompetitionList(true);
    },

    /**
     * 处理标签切换
     */
    handleTabChange(tabValue) {
      if (this.currentTab === tabValue) return;
      
      console.log('切换标签:', tabValue);
      this.currentTab = tabValue;
      this.loadCompetitionList(true);
    }
  }
}
</script>

<style scoped>
.competition-page {
  min-height: 100vh;
  background: #f8f9fa;
}

/* 筛选头部 */
.filter-header {
  background: #ffffff;
  padding: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 10;
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: 15rpx;
  justify-content: center;
}

.filter-tab {
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #666666;
  background: #f5f5f5;
  transition: all 0.3s;
  min-width: 80rpx;
  text-align: center;
}

.filter-tab.active {
  color: #ffffff;
  background: #1890ff;
}

.filter-tab:active {
  transform: scale(0.98);
}

/* 内容区域 */
.competition-content {
  padding: 20rpx 0;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #e5e5e5;
  border-top: 4rpx solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: #999999;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 20rpx;
}

.empty-icon {
  width: 120rpx;
  height: 120rpx;
  opacity: 0.3;
  margin-bottom: 20rpx;
}

.empty-text {
  color: #999999;
  font-size: 28rpx;
  margin-bottom: 30rpx;
}

.retry-btn {
  background: #1890ff;
  color: #ffffff;
  border: none;
  border-radius: 25rpx;
  padding: 15rpx 30rpx;
  font-size: 26rpx;
}

.retry-btn:active {
  transform: scale(0.98);
}
</style>