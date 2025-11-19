<template>
  <view class="order-detail-page">
    <!-- 加载状态 -->
  

    <!-- 订单详情内容 -->
    <view  class="detail-content">
      
      <!-- 赛事信息 -->
      <view class="section-title">赛事信息</view>
      <view class="info-section">
        <view class="info-item">
          <text class="label">比赛日期</text>
          <text class="value">{{ orderDetail.competitionDate   }}</text>
        </view>
        <view class="info-item">
          <text class="label">比赛场次</text>
          <text class="value">{{ orderDetail.competitionSession +"~"+ orderDetail.competitionEndSession   }}</text>
        </view>
      </view>

      <!-- 选手信息 -->
      <view class="section-title">选手信息</view>
      <view class="info-section">
        <view class="info-item">
          <text class="label">姓名</text>
          <text class="value">{{ orderDetail.participantName || '未填写' }}</text>
        </view>
        <view class="info-item">
          <text class="label">身份证号</text>
          <text class="value">{{ orderDetail.participantIdCard || '未填写' }}</text>
        </view>
        <view class="info-item">
          <text class="label">性别</text>
          <text class="value">{{ getGenderText(orderDetail.participantGender) }}</text>
        </view>
      </view>

      <!-- 监护人信息 -->
      <view class="section-title">监护人信息</view>
      <view class="info-section">
        <view class="info-item">
          <text class="label">姓名</text>
          <text class="value">{{ orderDetail.guardianName  }}</text>
        </view>
        <view class="info-item">
          <text class="label">手机号</text>
          <text class="value">{{ orderDetail.guardianPhone  }}</text>
        </view>
      </view>

      <!-- 支付信息 -->
      <view class="section-title">支付信息</view>
      <view class="info-section">
        <view class="info-item">
          <text class="label">支付时间</text>
          <text class="value">{{  orderDetail.paymentTime }}</text>
        </view>
        <view class="info-item">
          <text class="label">支付金额</text>
          <text class="value price">¥{{ orderDetail.paymentAmount   }}</text>
        </view>
      </view>

      <!-- 底部操作区域 -->
      <view class="bottom-actions" v-if="showActions">
        <button 
          v-if="orderDetail.status === 'PENDING'" 
          class="pay-btn" 
          @click="handlePay"
        >
          立即支付
        </button>
        <!-- <button 
          v-if="orderDetail.status === 'PENDING'" 
          class="cancel-btn" 
          @click="handleCancel"
        >
          取消订单
        </button>
        <button 
          v-if="orderDetail.status === 'PAID'" 
          class="refund-btn" 
          @click="handleRefund"
        >
          申请退款
        </button> -->
      </view>
    </view>

    <!-- 错误状态 -->
    
  </view>
</template>

<script>
import { getRegistrationDetail, wechatRefund } from '@/api/user.js'

export default {
  name: 'OrderDetail',
  data() {
    return {
      orderId: '',
      orderDetail: null,
      loading: false,
      showActions: true
    }
  },

  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      this.loadOrderDetail();
    } else {
      uni.showToast({
        title: '订单ID不能为空',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  },

  methods: {
    /**
     * 加载订单详情
     */
    async loadOrderDetail() {
      if (this.loading) return;

      try {
        this.loading = true;
        
        const response = await getRegistrationDetail(this.orderId);
        
        // 适配不同的响应格式
        let result = null;
        if (response && response.data) {
          result = response.data;
        } else if (response) {
          result = response;
        }

        if (result) {
          this.orderDetail = result;
          console.log('订单详情加载成功:', result);
        } else {
          throw new Error('订单详情数据为空');
        }

      } catch (error) {
        console.error('获取订单详情失败:', error);
        
        let errorMessage = '获取订单详情失败';
        if (error.code === 401) {
          errorMessage = '登录已过期，请重新登录';
        } else if (error.code === 403) {
          errorMessage = '没有权限访问订单详情';
        } else if (error.code === 404) {
          errorMessage = '订单不存在';
        } else if (error.message && error.message.includes('network')) {
          errorMessage = '网络连接失败，请检查网络';
        }
        
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 2000
        });
        
        this.orderDetail = null;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 格式化日期时间
     */
    formatDateTime(dateTime) {
      if (!dateTime) return '未支付';
      
      try {
        const date = new Date(dateTime);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      } catch (error) {
        console.error('日期格式化失败:', error);
        return dateTime;
      }
    },

    /**
     * 获取性别文本
     */
    getGenderText(gender) {
      if (gender === 'MALE' || gender === '男' || gender === '1') {
        return '男';
      } else if (gender === 'FEMALE' || gender === '女' || gender === '0') {
        return '女';
      }
      return '未填写';
    },

    /**
     * 处理支付
     */
    async handlePay() {
      const result = await uni.showModal({
        title: '确认支付',
        content: `确定要支付 ¥${this.orderDetail.paymentAmount } 吗？`,
        confirmText: '确定支付',
        cancelText: '取消'
      });

      if (result.confirm) {
        console.log('处理支付:', this.orderDetail);
        uni.showToast({
          title: '支付功能开发中',
          icon: 'none'
        });
      }
    },

    /**
     * 取消订单
     */
    async handleCancel() {
      const result = await uni.showModal({
        title: '确认取消',
        content: '确定要取消这个订单吗？取消后将无法恢复。',
        confirmText: '确定取消',
        cancelText: '再想想',
        confirmColor: '#ff4d4f'
      });

      if (result.confirm) {
        console.log('取消订单:', this.orderDetail);
        uni.showToast({
          title: '取消订单功能开发中',
          icon: 'none'
        });
      }
    },

    /**
     * 申请退款
     */
    async handleRefund() {
      const result = await uni.showModal({
        title: '确认退款',
        content: `确定要申请退款 ¥${this.orderDetail.paymentAmount} 吗？`,
        confirmText: '确定退款',
        cancelText: '取消',
        confirmColor: '#ff4d4f'
      });

      if (result.confirm) {
        try {
          uni.showLoading({
            title: '申请退款中...'
          });

          // 构建退款参数
          const refundData = {
            refundFee: parseFloat(this.orderDetail.paymentAmount), // 退款金额
            outTradeNo: this.orderDetail.outTradeNo || this.orderId, // 商户订单号
            totalFee: parseFloat(this.orderDetail.paymentAmount), // 订单总金额
            tradeType: 'JASAPI',
            extendParams: {
               "tenantId": '0'
            }
          };

          console.log('退款请求参数:', refundData);

          const response = await wechatRefund(refundData);

          if (response) {
            uni.showToast({
              title: '退款申请成功',
              icon: 'success'
            });

            // 重新加载订单详情以更新状态
            setTimeout(() => {
              this.loadOrderDetail();
            }, 1500);
          } else {
            throw new Error(response.message || response.msg || '退款申请失败');
          }

        } catch (error) {
          console.error('退款申请失败:', error);
          uni.showToast({
            title: error.message || '退款申请失败',
            icon: 'none'
          });
        } finally {
          uni.hideLoading();
        }
      }
    },

    /**
     * 跳转到赛事详情
     */
    goToCompetitionDetail() {
      if (this.orderDetail && this.orderDetail.competitionId) {
        uni.navigateTo({
          url: `/pages/competition/detail?id=${this.orderDetail.competitionId}`,
          fail: (error) => {
            console.error('跳转赛事详情失败:', error);
            uni.showToast({
              title: '跳转失败，请重试',
              icon: 'none'
            });
          }
        });
      } else {
        uni.showToast({
          title: '赛事信息不完整',
          icon: 'none'
        });
      }
    }
  }
}
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 20rpx;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #999;
}

/* 详情内容 */
.detail-content {
  padding: 20rpx;
  padding-bottom: 200rpx; /* 为底部按钮留出空间 */
}

/* 信息区块 */
.info-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 28rpx;
  color: #666;
  flex-shrink: 0;
  width: 160rpx;
}

.value {
  font-size: 28rpx;
  color: #333;
  text-align: right;
  flex: 1;
}

.value.price {
  color: #000;
  font-weight: 600;
  font-size: 32rpx;
}

/* 底部操作区域 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx;
  border-top: 1rpx solid #e8e8e8;
  display: flex;
  gap: 20rpx;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.pay-btn {
  flex: 1;
  background: #83BB0B;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  height: 88rpx;
  font-size: 32rpx;
  font-weight: 600;
}

.pay-btn:active {
  transform: scale(0.98);
  background: #83BB0B;
}

.cancel-btn {
  flex: 1;
  background: #fff;
  color: #ff4d4f;
  border: 1rpx solid #ff4d4f;
  border-radius: 8rpx;
  height: 88rpx;
  font-size: 32rpx;
}

.cancel-btn:active {
  transform: scale(0.98);
  background: #fff2f0;
}

.refund-btn {
  flex: 1;
  background: #ff4d4f;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  height: 88rpx;
  font-size: 32rpx;
  font-weight: 600;
}

.refund-btn:active {
  transform: scale(0.98);
  background: #d9363e;
}

.detail-btn {
  flex: 1;
  background: #83BB0B;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  height: 88rpx;
  font-size: 32rpx;
  font-weight: 600;
}

.detail-btn:active {
  transform: scale(0.98);
  background: #83BB0B;
}

/* 错误状态 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 20rpx;
}

.error-text {
  font-size: 32rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.retry-btn {
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  padding: 20rpx 40rpx;
  font-size: 30rpx;
}

.retry-btn:active {
  transform: scale(0.98);
  background: #83BB0B;
}
</style>