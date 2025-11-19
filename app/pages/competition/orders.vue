<template>
  <view class="orders-page">
    
 
      <OrderCard
        v-for="order in orderList"
        :key="order.id"
        :order="order"
        @click="handleOrderClick"
        @action="handleOrderAction"
      />

      <!-- 加载更多 -->
      <view v-if="hasMore" class="load-more-container">
        <view v-if="loading" class="loading-more">
          <uni-loading size="24"></uni-loading>
          <text class="loading-more-text">加载更多...</text>
        </view>
      
      </view>
   

    <!-- 空状态 -->
    <view v-if="orderList.length== 0" class="empty-state">
      <image class="empty-icon" src="/static/registration/registration_no_data.png" mode="aspectFit"></image>
      <text class="empty-text">暂无赛事订单</text>
    </view>
  </view>
</template>

<script>
import { getUserRegistrations } from '@/api/user.js'
import OrderCard from '@/components/OrderCard.vue'

export default {
  components: {
    OrderCard
  },
  name: 'CompetitionOrders',
  data() {
    return {
      orderList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      hasMore: true,
      total: 0
    }
  },

 

  onShow() {
    // 从其他页面返回时刷新数据
    this.loadOrderList(true);
  },

  onPullDownRefresh() {
    this.handleRefresh();
  },

  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.handleLoadMore();
    }
  },

  methods: {
    /**
     * 加载订单列表
     */
    async loadOrderList(reset = false) {
      if (this.loading) return;

      try {
        this.loading = true;

        if (reset) {
          this.currentPage = 1;
          this.orderList = [];
          this.hasMore = true;
        }

        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
		  status: 'PAID'
        };

        const response = await getUserRegistrations(params);
        
        // 适配不同的响应格式
        let result = null;
        if (response && response.data) {
          result = response.data;
        } else if (response) {
          result = response;
        }

        if (result && result.records) {
          const newList = result.records;
          
          if (reset) {
            this.orderList = newList;
          } else {
            this.orderList = [...this.orderList, ...newList];
          }

          // 更新分页信息
          this.total = result.total || 0;
          this.hasMore = result.current < result.pages;
          
          if (!reset) {
            this.currentPage++;
          }

          console.log('订单列表加载成功:', {
            total: this.total,
            current: result.current,
            pages: result.pages,
            hasMore: this.hasMore
          });
        }

      } catch (error) {
        console.error('获取订单列表失败:', error);
        
        let errorMessage = '获取订单列表失败';
        if (error.code === 401) {
          errorMessage = '登录已过期，请重新登录';
        } else if (error.code === 403) {
          errorMessage = '没有权限访问订单信息';
        } else if (error.message && error.message.includes('network')) {
          errorMessage = '网络连接失败，请检查网络';
        }
        
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 2000
        });
      } finally {
        this.loading = false;
        uni.stopPullDownRefresh();
      }
    },

    /**
     * 处理下拉刷新
     */
    async handleRefresh() {
      console.log('刷新订单列表');
      await this.loadOrderList(true);
    },

    /**
     * 处理加载更多
     */
    async handleLoadMore() {
      if (!this.hasMore || this.loading) return;
      
      console.log('加载更多订单');
      this.currentPage++;
      await this.loadOrderList(false);
    },

    /**
     * 处理订单点击
     */
    handleOrderClick(order) {
      console.log('点击订单:', order);
      this.handleViewDetail(order);
    },

    /**
     * 处理订单操作
     */
    handleOrderAction({ action, order }) {
      switch(action) {
        case 'pay':
          this.handlePayment(order);
          break;
        case 'detail':
          this.handleViewDetail(order);
          break;
        case 'cancel':
          this.handleCancelOrder(order);
          break;
        default:
          console.log('未知操作:', action);
      }
    },

    /**
     * 处理支付
     */
    handlePayment(order) {
      console.log('处理支付:', order);
      uni.showToast({
        title: '支付功能开发中',
        icon: 'none'
      });
    },

    /**
     * 查看订单详情
     */
    handleViewDetail(order) {
      console.log('查看订单详情:', order);
      
      // 获取订单ID
      let orderId = null;
      
      if (order && order.id) {
        orderId = order.id;
      }
      
      if (orderId) {
        console.log('跳转到订单详情，ID:', orderId);
        uni.navigateTo({
          url: `/pages/competition/order-detail?id=${orderId}`,
          fail: (error) => {
            console.error('跳转订单详情失败:', error);
            uni.showToast({
              title: '跳转失败，请重试',
              icon: 'none'
            });
          }
        });
      } else {
        console.error('无法获取订单ID:', order);
        uni.showToast({
          title: '订单信息不完整，无法查看详情',
          icon: 'none'
        });
      }
    },

    /**
     * 取消订单
     */
    async handleCancelOrder(order) {
      const result = await uni.showModal({
        title: '确认取消',
        content: '确定要取消这个订单吗？',
        confirmText: '确定取消',
        cancelText: '再想想'
      });

      if (result.confirm) {
        console.log('取消订单:', order);
        uni.showToast({
          title: '取消订单功能开发中',
          icon: 'none'
        });
      }
    },

    /**
     * 跳转到赛事页面
     */
    goToCompetition() {
      uni.switchTab({
        url: '/pages/competition/index'
      });
    },

    /**
     * 获取模拟订单数据
     */
    getMockOrderData() {
      return [
        {
          id: '1',
          competitionName: '2025斯巴达勇士郑州站',
          competitionImage: '/static/logo.png',
          participantName: '林开赛',
          ageGroup: '19',
          competitionDate: '2025-07-28 10:00:00',
          competitionLocation: '李水天',
          status: 'REGISTRATION_OPEN',
          createTime: '2025-01-15 14:30:00'
        },
        {
          id: '2',
          competitionName: '2025斯巴达勇士郑州站',
          competitionImage: '/static/logo.png',
          participantName: '李水天',
          ageGroup: '19',
          competitionDate: '2025-07-28 10:00:00',
          competitionLocation: '李水天',
          status: 'REGISTRATION_OPEN',
          createTime: '2025-01-15 14:30:00'
        },
        {
          id: '3',
          competitionName: '2025斯巴达勇士郑州站',
          competitionImage: '/static/logo.png',
          participantName: '张三',
          ageGroup: '19',
          competitionDate: '2025-07-28 10:00:00',
          competitionLocation: '李水天',
          status: 'IN_PROGRESS',
          createTime: '2025-01-15 14:30:00'
        }
      ];
    }

  }
}
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx 20rpx 40rpx 20rpx;
}



/* 订单列表 */
.orders-list {
  padding: 20rpx;
}

/* 加载更多 */
.load-more-container {
  padding: 20rpx;
  text-align: center;
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.loading-more-text {
  font-size: 28rpx;
  color: #999;
}

.load-more-btn {
  background: #fff;
  color: #1890ff;
  border: 1rpx solid #1890ff;
  border-radius: 8rpx;
  padding: 16rpx 32rpx;
  font-size: 28rpx;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 20rpx;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #999;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 20rpx;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  opacity: 0.6;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.go-competition-btn {
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 25rpx;
  padding: 20rpx 40rpx;
  font-size: 30rpx;
}

.go-competition-btn:active {
  transform: scale(0.98);
}
</style>