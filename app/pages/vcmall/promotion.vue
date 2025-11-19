<template>
  <view class="container">

    <!-- 收益展示区域 -->
         <view class="earnings-container">
         <view class="earnings-section">
             <!-- 当前收益行 -->
             <view class="earnings-row main-earnings">
                 <text class="earnings-label">当前收益</text>
                 <text class="earnings-amount">{{ formatAmount(earningsData.currentEarnings) }}</text>
             </view>
             
             <!-- 详细收益和提现按钮行 -->
             <view class="earnings-bottom">
                 <view class="earnings-details">
                     <view class="earnings-item">
                         <text class="detail-label">昨日收益：</text>
                         <text class="detail-value">{{ formatAmount(earningsData.yesterdayEarnings) }}</text>
                     </view>
                     <view class="earnings-item">
                         <text class="detail-label">历史收益：</text>
                         <text class="detail-value">{{ formatAmount(earningsData.totalEarnings) }}</text>
                     </view>
                     <view class="earnings-item" @tap="handleWithdrawRecord">
                         <text class="detail-label">点击查看提现记录</text>
                     </view>
                 </view>
                 
                 <view class="withdraw-btn" @tap="handleWithdraw">
                     <text class="withdraw-text">立即提现</text>
                 </view>
             </view>
         </view>
     </view>

    <!-- 推广邀请卡片 -->
    <view class="invitation-card">
      <view class="card-content">
        <view class="coin-icon">¥</view>
        <view class="invitation-text">
          <text class="invite-title">邀请好友</text>
          <text class="invite-subtitle">即可获取收益</text>
        </view>
        <button class="promotion-btn" @tap="goToPromotionCard">
          <text class="promotion-text">推广名片</text>
        </button>
      </view>
    </view>

    <!-- 功能卡片区域 -->
    <view class="function-cards">
      <view class="function-card" :class="{ active: currentTab === 'stats' }" @tap="switchTab('stats')">
        <view class="card-icon">
          <image class="icon-image" src="/static/vcmall/promotion.png" mode="aspectFit"></image>
        </view>
        <text class="card-title">推广人统计</text>
      </view>
      
      <view class="function-card" :class="{ active: currentTab === 'orders' }" @tap="switchTab('orders')">
        <view class="card-icon">
          <image class="icon-image" src="/static/vcmall/allOrder.png" mode="aspectFit"></image>
        </view>
        <text class="card-title">推广人订单</text>
      </view>
    </view>

    <!-- 佣金明细 -->
    <view class="commission-section">
      <view class="section-title">{{ getSectionTitle() }}</view>
      
      <!-- 推广人统计数据 -->
      <view v-if="currentTab === 'stats'" class="stats-list">
        <view 
          v-for="(item, index) in statsList" 
          :key="index"
          class="stats-item"
        >
          <view class="stats-info">
            <view class="user-name">{{ item.userName }}</view>
            <view class="user-detail">
              <text class="join-time">邀请时间：{{ item.joinTime }}</text>
            </view>
          </view>
          
          <view class="stats-data">
            <view class="stats-amount">
              <text class="amount-value">{{ item.status }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 推广人订单数据 -->
      <view v-else-if="currentTab === 'orders'" class="orders-list">
        <view 
          v-for="(item, index) in ordersList" 
          :key="index"
          class="order-item"
        >
          <view class="order-info">
            <view class="order-number">订单号 {{ item.orderNumber }}</view>
            <view class="order-detail">
                <text class="product-name" v-for="value in  item.goods" :key="index">{{ value }}</text>

              <text class="order-time">{{ item.orderTime }}</text>
            </view>
          </view>
          
          <view class="order-status">
            <view class="status-badge" :class="getStatusClass(item.status)">
              {{ getStatusText(item.status) }}
            </view>
            <view class="order-amount">
              <text class="amount-value">{{ formatAmount(item.orderAmount) }}元</text>
            </view>
          </view>
        </view>
      </view>

    

      <!-- 加载更多 -->
      <view v-if="hasMore" class="load-more" >
        <text class="load-text">加载更多</text>
      </view>
      
      <!-- 没有更多数据 -->
      <view v-else-if="commissionList.length > 0" class="no-more">
        <text class="no-more-text">没有更多数据了</text>
      </view>
      
      <!-- 空状态 -->
      <view v-else class="empty-state">
        <text class="empty-text">暂无佣金记录</text>
      </view>
    </view>
</view>

</template>

<script>
import { getPromotionEarnings, getCommissionList, withdrawEarnings, getInvitedUsers } from '../../api/promotion.js';
import { base64Encode, base64Decode } from '../../utils/base64.js';
export default {
     // 小程序分享配置
  onShareAppMessage(res) {
  let param = "/pages/vcmall/memberOption?inviteLoginId=" + this.earningsData.loginId
    return {
      title:'邀请好友',
      imageUrl:'/static/images/promotion-share.png',
      path: "/pages/loading/loading?toPage="+ base64Encode(param),
    };
  },
  name: 'Promotion',
  data() {
    return {
      earningsData: {
        currentEarnings: 0,
        yesterdayEarnings: 0,
        totalEarnings: 0
      },
      commissionList: [],
      statsList: [],
      ordersList: [],
      currentTab: 'orders', // commission, stats, orders
      currentPage: 1,
      pageSize: 4,
      hasMore: true,
      loading: false
    }
  },
  onLoad() {
   
  },
  onShow() {
    this.loadEarningsData();
    this.loadOrdersData();
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMoreCommissions();
    }
  },
  methods: {


    // 加载收益数据
    async loadEarningsData() {
      try {
        const result = await getPromotionEarnings();
        if (result) {
          this.earningsData = {
            currentEarnings: result.totalIncome || 0,
            yesterdayEarnings: result.yesterdayIncome || 0,
            totalEarnings: result.totalCommissionIncome || 0,
            loginId: result.loginId || ''
          };
        }
      } catch (error) {
        console.error('加载收益数据失败:', error);
      }
    },
 

    // 加载更多佣金记录
    loadMoreCommissions() {
      this.loadOrdersData();
    },

    // 刷新数据
    async refreshData() {
        this.currentPage = 1;
      this.hasMore = true;
      this.ordersList = [];
      this.statsList = [];
      await Promise.all([
        this.loadEarningsData(),
        this.loadOrdersData()
      ]);
     
      uni.stopPullDownRefresh();
    },

    // 处理提现
    async handleWithdraw() {
      if (this.earningsData.currentEarnings <= 0) {
        uni.showToast({
          title: '当前收益为0，无法提现',
          icon: 'none'
        });
        return;
      }

      uni.showModal({
        title: '确认提现',
        editable: true,
        content: this.formatAmount(this.earningsData.currentEarnings),
        success: async (res) => {
          if (res.confirm) {
            console.log(res);
            let   amount = res.content;
            if(amount ==null || amount =='' || amount ==undefined){
                uni.showToast({
                    title: '请输入提现金额',
                    icon: 'none'
                });
                return;
            }
            let exp =/^[+-]?\d*(\.\d*)?(e[+-]?\d+)?$/;
            if(!exp.test(amount)){
                uni.showToast({
                    title: '请输入正确的提现金额',
                    icon: 'none'
                });
              return
            }
            if(amount ==0){
                uni.showToast({
                    title: '提现金额不能为0',
                    icon: 'none'
                });
                return;
            }
            if(amount.split('.')[1].length > 2){
                uni.showToast({
                    title: '提现金额最多只能有2位小数',
                    icon: 'none'
                });
                return;
            }


            try {
              uni.showLoading({
                title: '提现中...'
              });
              
              const result = await withdrawEarnings({
                amount: amount
              });
              if(result.code == 200){
                uni.showToast({
                  title: '提现申请已提交',
                  icon: 'success'
                });
                uni.navigateTo({
                  url: '/pages/vcmall/withdrawRecord'
                });
              }else{
                uni.showToast({
                  title: result.msg,
                  icon: 'none'
                });
              }
              
            } catch (error) {
              uni.hideLoading();
              console.error('提现失败:', error);
              uni.showToast({
                title: error.message || '提现失败',
                icon: 'none'
              });
            }
          }
        }
      });
    },

    goToPromotionCard() {
      uni.navigateTo({
        url: '/pages/vcmall/promotionCard'
      });
    },

    // 查看提现记录
    handleWithdrawRecord() {
      uni.navigateTo({
        url: '/pages/vcmall/withdrawRecord'
      });
    },

    // 切换标签页
    switchTab(tab) {
      this.currentTab = tab;
      this.currentPage = 1;
        this.hasMore = true;
        this.ordersList = [];
        this.statsList = [];
      if (tab === 'stats') {
        this.loadStatsData();
      } else if (tab === 'orders') {
      
        this.loadOrdersData();
      }
    
    },

    // 加载推广统计数据
    async loadStatsData() {
      try {
        const result = await getInvitedUsers();
        if (result) {
          this.statsList = result.map(item => ({
            userName: item.nickname || item.userName || '未知用户',
            joinTime: item.inviteTime,
            status: item.memberExpireDate == null ? '普通用户' : '会员'
          }));
        }
      } catch (error) {
        console.error('加载推广统计失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      }
    },

    // 加载推广订单数据
    async loadOrdersData() {
      try {
        // 这里可以调用专门的推广订单接口
        // 暂时使用佣金记录数据作为示例
        const result = await getCommissionList({
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          type: 'commission'
        });
        if (result ) {
          let list = result.records.map(item => ({
            orderNumber: item.orderId,
            goods: item.goodsInfo ,
            orderTime: item.createTime ,
            status: item.status === 'received' ? 'completed' : 'pending',
            orderAmount: item.amount || 0 // 假设佣金是订单金额的10%
          }));
          this.ordersList = [...this.ordersList, ...list];
          this.currentPage++;
          this.hasMore = result.records.length === this.pageSize;
        }
      } catch (error) {
        console.error('加载推广订单失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      }
    },

    // 获取区域标题
    getSectionTitle() {
      switch (this.currentTab) {
        case 'stats':
          return '推广人统计';
        case 'orders':
          return '推广人订单';
        default:
          return '推广人订单';
      }
    },

    // 处理佣金详情
    handleCommissionDetail(item) {
      uni.navigateTo({
        url: `/pages/promotion/commissionDetail?orderId=${item.orderId}`
      });
    },

    // 格式化金额
    formatAmount(amount) {
      if (amount === null || amount === undefined) return '0.00';
      return parseFloat(amount).toFixed(2);
    },

    // 获取状态样式类
    getStatusClass(status) {
      switch (status) {
        case 'pending':
          return 'status-pending';
        case 'completed':
          return 'status-completed';
        default:
          return '';
      }
    },

    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 'pending':
          return '即将到账';
        case 'completed':
          return '已到账';
        default:
          return '未知状态';
      }
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
}



/* 收益展示区域 */
.earnings-container {
  padding-top: 20rpx;
}

.earnings-section {
  background: #ffffff;
  margin: 20rpx 30rpx;
  border-radius: 20rpx;
  padding: 20rpx 30rpx 40rpx 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

/* 当前收益行 */
.earnings-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main-earnings {
  margin-bottom: 30rpx;
  border-bottom: 1rpx #3333 dashed;
}

.earnings-label {
  font-size: 32rpx;
  color: #333333;
  font-weight: 500;
}

.earnings-amount {
  font-size: 80rpx;
  font-weight: bold;
  color: #333333;
}

/* 底部区域 */
.earnings-bottom {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.earnings-details {
  display: flex;
  flex-direction: column;
  gap: 15rpx;
}

.earnings-item {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.detail-label {
  font-size: 28rpx;
  color: #999999;
}

.detail-value {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.withdraw-btn {
  background: linear-gradient(135deg, #4285f4 0%, #1976d2 100%);
  border-radius: 8rpx;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.withdraw-text {
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 500;
}

/* 推广邀请卡片 */
.invitation-card {
  margin: 20rpx 30rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #00bcd4 0%, #2196f3 100%);
  padding: 40rpx 30rpx;
}

.card-content {
  display: flex;
  align-items: center;
  gap: 30rpx;
}

.coin-icon {
  width: 80rpx;
  height: 80rpx;
  background: linear-gradient(135deg, #ffbc02 0%, #f3cd21 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #ffffff;
  font-weight: bold;
}

.invitation-text {
  flex: 1;
}

.invite-title {
  display: block;
  font-size: 32rpx;
  color: #ffffff;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.invite-subtitle {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.promotion-btn {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 40rpx;
}

.promotion-text {
  font-size: 28rpx;
  color: #2196f3;
  font-weight: 500;
}

/* 功能卡片区域 */
.function-cards {
  display: flex;
  gap: 20rpx;
  margin: 20rpx 30rpx;
}

.function-card {
  flex: 1;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 40rpx 20rpx;
  text-align: center;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.card-icon {
  margin-bottom: 20rpx;
}

.icon-image {
  width: 60rpx;
  height: 60rpx;
}

.function-card.active {
  background: linear-gradient(135deg, #4285f4 0%, #1976d2 100%);
  color: #ffffff;
}

.function-card.active .card-title {
  color: #ffffff;
}

.card-title {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

/* 佣金明细 */
.commission-section {
  margin: 20rpx 30rpx 0;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
  padding-left: 10rpx;
}

.commission-list {
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
}

.commission-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.commission-item:last-child {
  border-bottom: none;
}

.commission-info {
  flex: 1;
}

.order-number {
  font-size: 30rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 10rpx;
}

.order-detail {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.product-name {
  font-size: 26rpx;
  color: #666666;
}

.order-time {
  font-size: 24rpx;
  color: #999999;
}

.commission-status {
  text-align: right;
}

.status-badge {
  display: inline-block;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  margin-bottom: 10rpx;
}

.status-pending {
  background: #fff3e0;
  color: #ff9800;
}

.status-completed {
  background: #e8f5e8;
  color: #4caf50;
}

.commission-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}

.amount-label {
  font-size: 24rpx;
  color: #999999;
}

.amount-value {
  font-size: 28rpx;
  color: #ff5722;
  font-weight: bold;
}

/* 推广统计样式 */
.stats-list {
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
}

.stats-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.stats-item:last-child {
  border-bottom: none;
}

.stats-info {
  flex: 1;
}

.user-name {
  font-size: 30rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 10rpx;
}

.user-detail {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.join-time {
  font-size: 26rpx;
  color: #666666;
}

.stats-data {
  text-align: right;
}

.stats-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}

/* 推广订单样式 */
.orders-list {
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
}

.order-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.order-info {
  flex: 1;
}

.order-detail {
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex-wrap: wrap;
}

.order-status {
  text-align: right;
}

.order-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}

/* 加载状态 */
.load-more,
.no-more,
.empty-state {
  text-align: center;
  padding: 40rpx 0;
}

.load-text,
.no-more-text,
.empty-text {
  font-size: 28rpx;
  color: #999999;
}

/* 响应式适配 */
@media screen and (max-width: 750rpx) {
  .earnings-section {
    margin: 15rpx 20rpx;
    padding: 30rpx 20rpx;
  }
  
  .earnings-amount {
    font-size: 70rpx;
  }
  
  .invitation-card {
    margin: 15rpx 20rpx;
    padding: 30rpx 20rpx;
  }
  
  .function-cards {
    margin: 15rpx 20rpx;
    gap: 15rpx;
  }
  
  .commission-section {
    margin: 15rpx 20rpx 0;
  }


}



</style>
