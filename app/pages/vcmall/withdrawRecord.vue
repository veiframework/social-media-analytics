<template>
  <view class="container">
    <!-- 提现记录列表 -->
    <view class="withdraw-list">
      <view 
        v-for="(item, index) in withdrawList" 
        :key="index"
        class="withdraw-item"
      >
        <view class="withdraw-info">
          <view class="withdraw-amount">
            <text class="amount-text">{{ formatAmount(item.amount) }}元</text>
            <view class="status-badge" :class="getStatusClass(item.status)">
              {{ getStatusText(item.status) }}
            </view>
          </view>
          <view class="withdraw-detail">
            <text class="apply-time">申请时间：{{ item.applyTime }}</text>
            <text class="process-time" v-if="item.processTime">处理时间：{{ item.processTime }}</text>
          </view>
          <view class="withdraw-remark" v-if="item.remark">
            <text class="remark-text">备注：{{ item.remark }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="hasMore && withdrawList.length > 0" class="load-more" @tap="loadMoreRecords">
      <text class="load-text">加载更多</text>
    </view>
    
    <!-- 没有更多数据 -->
    <view v-else-if="withdrawList.length > 0" class="no-more">
      <text class="no-more-text">没有更多数据了</text>
    </view>
    
    <!-- 空状态 -->
    <view v-else class="empty-state">
      <view class="empty-icon">💰</view>
      <text class="empty-text">暂无提现记录</text>
      <text class="empty-tip">您还没有申请过提现</text>
    </view>
  </view>
</template>

<script>
import { getCommissionList } from '../../api/promotion.js';

export default {
  name: 'WithdrawRecord',
  data() {
    return {
      withdrawList: [],
      currentPage: 1,
      pageSize: 10,
      hasMore: true,
      loading: false
    }
  },
  onLoad() {
    this.loadWithdrawRecords();
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMoreRecords();
    }
  },
  methods: {
    // 加载提现记录
    async loadWithdrawRecords(isRefresh = false) {
      if (this.loading) return;
      
      this.loading = true;
      
      try {
        const page = isRefresh ? 1 : this.currentPage;
        const result = await getCommissionList({
          pageNum: page,
          pageSize: this.pageSize,
          type: 'withdraw'
        });
        
        if (result && result.records) {
          const newList = result.records.map(item => ({
            id: item.id,
            amount: item.amount || 0,
            status: item.status || 'pending',
            applyTime: item.createTime || '',
            processTime: item.updateTime || '',
            remark: item.remark || ''
          }));
          
          if (isRefresh) {
            this.withdrawList = newList;
            this.currentPage = 1;
          } else {
            this.withdrawList = [...this.withdrawList, ...newList];
          }
          
          this.hasMore = result.records.length === this.pageSize;
          this.currentPage = page + 1;
        }
      } catch (error) {
        console.error('加载提现记录失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 加载更多记录
    loadMoreRecords() {
      this.loadWithdrawRecords(false);
    },

    // 刷新数据
    async refreshData() {
      await this.loadWithdrawRecords(true);
      uni.stopPullDownRefresh();
    },

    // 格式化金额
    formatAmount(amount) {
      if (amount === null || amount === undefined) return '0.00';
      return parseFloat(amount).toFixed(2);
    },

    // 获取状态样式类
    getStatusClass(status) {
      switch (status) {
        case 'waiting':
          return 'status-pending';
        case 'received':
          return 'status-completed';
        default:
          return 'status-pending';
      }
    },

    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 'waiting':
          return '即将到帐';
        case 'received':
          return '已到账';
        default:
          return '即将到帐';
      }
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

/* 提现记录列表 */
.withdraw-list {
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
}

.withdraw-item {
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.withdraw-item:last-child {
  border-bottom: none;
}

.withdraw-info {
  width: 100%;
}

.withdraw-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.amount-text {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
}

.status-badge {
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status-pending {
  background: #fff3e0;
  color: #ff9800;
}

.status-processing {
  background: #e3f2fd;
  color: #2196f3;
}

.status-completed {
  background: #e8f5e8;
  color: #4caf50;
}

.status-failed {
  background: #ffebee;
  color: #f44336;
}

.withdraw-detail {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-bottom: 15rpx;
}

.apply-time,
.process-time {
  font-size: 26rpx;
  color: #666666;
}

.withdraw-remark {
  padding: 15rpx;
  background: #f8f9fa;
  border-radius: 8rpx;
}

.remark-text {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.4;
}

/* 加载状态 */
.load-more,
.no-more {
  text-align: center;
  padding: 40rpx 0;
}

.load-text,
.no-more-text {
  font-size: 28rpx;
  color: #999999;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 120rpx 40rpx;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  display: block;
  font-size: 32rpx;
  color: #666666;
  margin-bottom: 15rpx;
}

.empty-tip {
  display: block;
  font-size: 26rpx;
  color: #999999;
}

/* 响应式适配 */
@media screen and (max-width: 750rpx) {
  .container {
    padding: 15rpx;
  }
  
  .withdraw-item {
    padding: 25rpx 20rpx;
  }
  
  .amount-text {
    font-size: 32rpx;
  }
}
</style>
