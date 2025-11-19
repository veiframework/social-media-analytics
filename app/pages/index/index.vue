<template>
  <view class="home-container">
    <!-- 主要内容区域 -->
    <scroll-view 
      class="main-content" 
      scroll-y="true"
      @scrolltolower="handleScrollToLower"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="handleRefresh"
    >
  

      <!-- 轮播图 -->
      <view class="banner-section">
        <BannerSwiper 
          :bannerList="bannerList" 
          @bannerClick="handleBannerClick"
        />
      </view>
      

      <view class="section-header">
          <text class="section-title">近期赛事</text>
        </view>

      <!-- 赛事列表 -->
      <CompetitionList 
        :competitionList="competitionList"
        :loading="loading"
        :showLoadMore="hasMore"
        @itemClick="handleCompetitionClick"
        @moreClick="handleMoreClick"
        @loadMore="handleLoadMore"
      />
    </scroll-view>
  </view>
</template>

<script>
import BannerSwiper from '../../components/BannerSwiper.vue';
import CompetitionList from '../../components/CompetitionList.vue';
import { getBannerList, getCompetitionList } from '../../api/home.js';

export default {
  components: {
    BannerSwiper,
    CompetitionList
  },
  data() {
    return {
      // 轮播图数据
      bannerList: [],
      
      // 赛事列表数据
      competitionList: [],
      currentPage: 0,
      pageSize: 5,
      hasMore: true,
      loading: false,
      refreshing: false,
      
      // 用户信息
      userInfo: null
    }
  },
  
  onLoad() {
   
  },
  
  onShow() {
    this.initPage();
    // 延迟请求订阅消息授权，避免影响页面加载
   
  },
  
  methods: {
    // 初始化页面
    async initPage() {
      try {
        this.loading = true;
        await Promise.all([
          this.loadBannerList(),
          this.loadCompetitionList(true)
        ]);
      } catch (error) {
        console.error('初始化页面失败:', error);
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 请求订阅消息授权
    requestSubscribeMessage() {
      // 检查是否已经授权过（24小时内不再重复请求）
      const lastAuthTime = uni.getStorageSync('last_subscribe_auth_time');
      const now = Date.now();
      const oneDay = 24 * 60 * 60 * 1000; // 24小时
      
      if (lastAuthTime && (now - lastAuthTime < oneDay)) {
        console.log('24小时内已请求过订阅消息授权');
        return;
      }

      // 订阅消息模板ID列表（需要替换为实际的模板ID）
      const templateIds = [
         "JYHNdBlc56miduP02bKhJPf4Qh9vxB7LCk4u6BQ0-y4"
      ];

      // 如果没有配置模板ID，则不请求授权
      if (templateIds.length === 0) {
        console.log('未配置订阅消息模板ID');
        return;
      }

      // 请求订阅消息授权
      uni.requestSubscribeMessage({
        tmplIds: templateIds,
        success: (res) => {
          console.log('订阅消息授权结果:', res);
          
          // 记录授权请求时间
          uni.setStorageSync('last_subscribe_auth_time', now);
          
          // 检查授权结果
          let acceptedCount = 0;
          let totalCount = templateIds.length;
          
          templateIds.forEach(templateId => {
            if (res[templateId] === 'accept') {
              console.log(`模板 ${templateId} 授权成功`);
              acceptedCount++;
            } else {
              console.log(`模板 ${templateId} 授权状态:`, res[templateId]);
            }
          });

          if (acceptedCount > 0) {
            // 有模板授权成功
            if (acceptedCount === totalCount) {
              uni.showToast({
                title: '消息订阅授权成功',
                icon: 'success',
                duration: 1500
              });
            } else {
              uni.showToast({
                title: `已授权${acceptedCount}/${totalCount}个消息`,
                icon: 'none',
                duration: 1500
              });
            }
            
            // 保存授权状态
            uni.setStorageSync('subscribe_message_partial_auth', true);
          } else {
            // 全部拒绝授权
            console.log('用户拒绝了所有订阅消息授权');
          }
        },
        fail: (err) => {
          console.error('订阅消息授权失败:', err);
          
          // 记录授权请求时间（避免频繁请求）
          uni.setStorageSync('last_subscribe_auth_time', now);
          
          // 不显示错误提示，避免打扰用户体验
          console.log('订阅消息授权请求失败，可能是用户取消或其他原因');
        }
      });
    },
    
    // 加载轮播图
    async loadBannerList() {
      try {
        const result = await getBannerList();
        this.bannerList = Array.isArray(result) ? result : [];
      } catch (error) {
        console.error('加载轮播图失败:', error);
        this.bannerList = [];
      }
    },
    
    // 加载赛事列表
    async loadCompetitionList(reset = false) {
      if (this.loading && !reset) return;
      
      try {
        this.loading = true;
        if(!reset){
          this.currentPage++;
        }
        const params = {
          pageNum: reset ? 1 : this.currentPage,
          pageSize: this.pageSize,
          enabled: true,
          openRegistration: true,
          descFields: 'startTime'
        };
        
        const result = await getCompetitionList(params);
        
        if (result && result.records) {
          if (reset) {
            this.competitionList = result.records;
            this.currentPage = 1;
          } else {
           
            this.competitionList = [...this.competitionList, ...result.records];
          }
          
          // 判断是否还有更多数据
          this.hasMore = result.current < result.pages;
          
          
        } else {
          this.competitionList = reset ? [] : this.competitionList;
          this.hasMore = false;
        }
      } catch (error) {
        console.error('加载赛事列表失败:', error);
        if (reset) {
          this.competitionList = [];
        }
        this.hasMore = false;
      } finally {
        this.loading = false;
      }
    },
    
    // 下拉刷新
    async handleRefresh() {
      this.refreshing = true;
      try {
        await Promise.all([
          this.loadBannerList(),
          this.loadCompetitionList(true)
        ]);
        uni.showToast({
          title: '刷新成功',
          icon: 'success',
          duration: 1500
        });
      } catch (error) {
        console.error('刷新失败:', error);
        uni.showToast({
          title: '刷新失败',
          icon: 'none'
        });
      } finally {
        this.refreshing = false;
      }
    },
    
    // 上拉加载更多
    handleScrollToLower() {
      if (this.hasMore && !this.loading) {
        this.handleLoadMore();
      }
    },
    
    // 加载更多
    handleLoadMore() {
      if (this.hasMore && !this.loading) {
        this.loadCompetitionList();
      }
    },
    
    // 刷新数据
    async refreshData() {
      await this.loadBannerList();
      await this.loadCompetitionList(true);
    },
    
    // 轮播图点击事件
    handleBannerClick(banner) {
      console.log('轮播图点击:', banner);
      // 根据jumpType处理跳转逻辑
      if (banner.competitionId) {
        // 跳转到赛事详情
        this.navigateToCompetitionDetail(banner.competitionId);
      }
    },
    
    // 赛事项目点击事件
    handleCompetitionClick(competition) {
      console.log('赛事点击:', competition);
      this.navigateToCompetitionDetail(competition.id);
      this.requestSubscribeMessage();
    },
    
    // 更多赛事点击
    handleMoreClick() {
      // 跳转到赛事列表页面
      uni.navigateTo({
        url: '/pages/competition/list'
      });
    },
    

    
    // 跳转到赛事详情
    navigateToCompetitionDetail(competitionId) {
      uni.navigateTo({
        url: `/pages/competition/detail?id=${competitionId}`,
        fail: () => {
          uni.showToast({
            title: '功能开发中',
            icon: 'none'
          });
        }
      });
    }
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f8f9fa;
}

.main-content {
  height: 100vh;
  padding-bottom: 100rpx; /* 为底部tabbar预留空间 */
}

.banner-section {
  margin: 0;
}

.section-header {
  padding: 30rpx 0 50rpx 20rpx;
  font-weight: bold;
  font-size: 36rpx;
  color: #333333;
}
</style>
