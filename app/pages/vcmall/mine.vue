<template>
  <view class="mine-container">


    <!-- 用户信息区域 -->
    <view class="user-info-section">
      <view class="user-avatar" @tap="goToSettings">
        <image :src="userInfo.avatar || '/static/mine/default-avatar.png'" class="avatar-image" mode="aspectFill" />
      </view>
      <view class="user-details">
        <view class="user-name-row">
          <text class="user-name">{{ userInfo.nickname || 'admin' }}</text>
          <image v-if="userInfo.extendParams.memGrade==2" src="/static/vcmall/membership.png" class="vip-icon" mode="aspectFit" />
        </view>
        <text class="user-phone">{{ formatPhone(userInfo.phone) }}</text>
      </view>
    </view>

    <!-- 我的订单区域 -->
    <view class="order-section">
      <view class="section-header">
        <text class="section-title">我的订单</text>
        <view class="view-all" @tap="goToAllOrders">
          <text class="view-all-text">查看全部</text>
          <image src="/static/arrow.png" class="arrow-icon" mode="aspectFit" />
        </view>
      </view>
      
      <view class="order-status-grid">
        <view 
          class="order-status-item" 
          v-for="(item, index) in orderStatusList" 
          :key="index"
          @tap="goToOrdersByStatus(item.status)"
        >
          <view class="status-icon-container">
            <image :src="item.icon" class="status-icon" mode="aspectFit" />
            <view v-if="item.count > 0" class="status-badge">{{ item.count }}</view>
          </view>
          <text class="status-text">{{ item.name }}</text>
        </view>
      </view>
    </view>

    <!-- 常用服务区域 -->
    <view class="service-section">
      <view class="section-header">
        <text class="section-title">常用服务</text>
      </view>
      
      <view class="service-grid">

        <view  class="service-item"  v-for="(item, index) in serviceList"  :key="index" >

          <template v-if="item.name==='联系客服'">
            <button class="service-icon-container" open-type="contact" >
              <image :src="item.icon" class="service-icon" mode="aspectFit" />
            </button>
            <text class="service-text">{{ item.name }}</text>
          </template>

          <template v-else>
            <view class="service-icon-container"  @tap="goToService(item)">
              <image :src="item.icon" class="service-icon" mode="aspectFit" />
            </view>
            <text class="service-text">{{ item.name }}</text>
          </template>

        </view>

      </view>
    </view>
  </view>
</template>

<script>
import { getUserInfo } from '../../api/user.js';


export default {
  name: 'Mine',
  components: {},
  data() {
    return {
      userInfo: {

      },
      orderStatusList: [
        {
          name: '待付款',
          status: 0,
          icon: '/static/vcmall/order-pending.png',
          count: 0
        },
        {
          name: '待发货',
          status: 1,
          icon: '/static/vcmall/order-delivery.png',
          count: 0
        },
        {
          name: '待收货',
          status: 3,
          icon: '/static/vcmall/order-receive.png',
          count: 0
        },
        {
          name: '退款',
          status: 6,
          icon: '/static/vcmall/order-refund.png',
          count: 0
        }
      ],
      serviceList: [
        {
          name: '地址管理',
          icon: '/static/vcmall/address.png',
          url: '/pages/address/index'
        },
        {
          name: '成为会员',
          icon: '/static/vcmall/membership.png',
          url: '/pages/vcmall/memberOption'
        },
        {
          name: '我的推广',
          icon: '/static/vcmall/promotion.png',
          url: '/pages/vcmall/promotion'
        },
        {
          name: '联系客服',
          icon: '/static/vcmall/customer-service.png',
          url: '/pages/customer-service/index'
        }
      ]
    }
  },
  onLoad() {
  },
  onShow() {
    // 页面显示时刷新数据
    this.loadUserInfo();

  },
  methods: {
    // 加载用户信息
    async loadUserInfo() {
      try {
        const data = await getUserInfo();
        this.userInfo = {
          ...this.userInfo,
          ...data
        };
        this.orderStatusList[0].count = this.userInfo.extendParams.notPayNum
        this.orderStatusList[1].count = this.userInfo.extendParams.paidNum
        this.orderStatusList[2].count = this.userInfo.extendParams.shippedNum
        this.orderStatusList[3].count = this.userInfo.extendParams.refundNum
      
        console.log('用户信息加载成功:', data);
      } catch (error) {
        console.error('加载用户信息失败:', error);
      }
    },



    // 格式化手机号
    formatPhone(phone) {
      if (!phone) return '';
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    },

    // 显示更多操作
    showMoreActions() {
      uni.showActionSheet({
        itemList: ['设置', '关于我们', '意见反馈'],
        success: (res) => {
          switch (res.tapIndex) {
            case 0:
              this.goToSettings();
              break;
            case 1:
              this.goToAbout();
              break;
            case 2:
              this.goToFeedback();
              break;
          }
        }
      });
    },

    // 跳转到扫码页面
    goToScan() {
      uni.scanCode({
        success: (res) => {
          console.log('扫码结果:', res);
          // 处理扫码结果
        },
        fail: (error) => {
          console.error('扫码失败:', error);
        }
      });
    },

    // 跳转到个人资料
    goToProfile() {
      uni.navigateTo({
        url: '/pages/profile/edit'
      });
    },

    // 跳转到全部订单
    goToAllOrders() {
      uni.setStorageSync('fromMine',-1);

      uni.switchTab({
        url: '/pages/order/list'
      });
    },

    // 根据状态跳转到订单列表
    goToOrdersByStatus(status) {
      uni.setStorageSync('fromMine', status);
      uni.switchTab({
        url: `/pages/order/list`
      });
    },

    // 跳转到服务页面
    goToService(service) {
      if (service.url) {
        uni.navigateTo({
          url: service.url
        });
      }
    },

    // 跳转到设置页面
    goToSettings() {
      // 跳转到设置页面，传递用户信息
      const userInfoStr = encodeURIComponent(JSON.stringify(this.userInfo));
      uni.navigateTo({
        url: `/pages/settings/index?userInfo=${userInfoStr}`
      });
    },

    // 跳转到关于我们
    goToAbout() {
      uni.navigateTo({
        url: '/pages/about/index'
      });
    },

    // 跳转到意见反馈
    goToFeedback() {
      uni.navigateTo({
        url: '/pages/feedback/index'
      });
    }
  }
}
</script>

<style scoped>
.mine-container {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 顶部导航栏 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #fff;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.more-btn {
  display: flex;
  gap: 6rpx;
  padding: 10rpx;
}

.dot {
  width: 6rpx;
  height: 6rpx;
  background: #666;
  border-radius: 50%;
}

.scan-btn {
  width: 60rpx;
  height: 60rpx;
  background: #333;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scan-icon {
  width: 30rpx;
  height: 30rpx;
}

/* 用户信息区域 */
.user-info-section {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  background: #F5F5F5;
  margin-bottom: 20rpx;
}

.user-avatar {
  margin-right: 30rpx;
}

.avatar-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  border: 4rpx solid #e0e0e0;
}

.user-details {
  flex: 1;
}

.user-name-row {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
}

.user-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-right: 15rpx;
}

.vip-icon {
  width: 40rpx;
  height: 40rpx;
}

.user-phone {
  font-size: 28rpx;
  color: #666;
}

/* 我的订单区域 */
.order-section {
  background: #fff;
  margin: 30rpx;
  border-radius: 30rpx;

}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.view-all {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.view-all-text {
  font-size: 26rpx;
  color: #999;
}

.arrow-icon {
  width: 24rpx;
  height: 24rpx;
}

.order-status-grid {
  display: flex;
  padding: 40rpx 30rpx;
}

.order-status-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.status-icon-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-icon {
  width: 60rpx;
  height: 60rpx;
}

.status-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  background: #ff4757;
  color: #fff;
  font-size: 20rpx;
  font-weight: bold;
  min-width: 32rpx;
  height: 32rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
  box-sizing: border-box;
}

.status-text {
  font-size: 24rpx;
  color: #333;
  text-align: center;
}

/* 常用服务区域 */
.service-section {
  background: #fff;
  margin: 30rpx;
  border-radius: 30rpx;
}

.service-grid {
  display: flex;
  padding: 40rpx 30rpx;
}

.service-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.service-icon-container {
  width: 80rpx;
  height: 80rpx;
  background: #f8f9fa;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
    margin: 0;
}

.service-icon-container::after{
  border: none;
}
.service-icon {
  width: 50rpx;
  height: 50rpx;
}

.service-text {
  font-size: 24rpx;
  color: #333;
  text-align: center;
}

/* 响应式适配 */
@media screen and (max-width: 750rpx) {
  .order-status-grid {
    flex-wrap: wrap;
  }
  
  .order-status-item {
    flex-basis: 50%;
    margin-bottom: 30rpx;
  }
  
  .service-grid {
    flex-wrap: wrap;
  }
  
  .service-item {
    flex-basis: 50%;
    margin-bottom: 30rpx;
  }
}
</style>
