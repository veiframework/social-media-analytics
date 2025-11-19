<template>
  <view class="container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-loading></uni-loading>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 主要内容 -->
    <view v-else>
      <!-- 顶部用户信息区域 -->
      <view class="header-section">
        <view class="header-section-left"></view>
        <view class="header-section-right"></view>
        <view class="user-info">
          <view class="avatar-container">
            <image class="avatar" :src="userInfo.avatar" mode="aspectFill" @error="handleAvatarError"></image>
          </view>
          <view class="user-details">
            <text class="username">{{ userInfo.name }}</text>
            <text class="phone">{{ userInfo.phone }}</text>
          </view>
        </view>
        
        <!-- 统计数据 -->
        <UserStatsCard :stats="statsData" @statClick="handleStatClick" />
      </view>
      
      <view class="section-title">其他服务</view>

      <!-- 其他服务 -->
       
        <view class="services-grid">
          <ServiceItem 
            v-for="service in services" 
            :key="service.type"
            :icon="service.icon"
            :label="service.label"
            :type="service.type"
            @click="handleServiceClick"
          />
        </view>
    </view>
  </view>
</template>

<script>
import ServiceItem from '@/components/ServiceItem.vue'
import UserStatsCard from '@/components/UserStatsCard.vue'
import { getUserInfo } from '@/api/user.js'

export default {
  components: {
    ServiceItem,
    UserStatsCard
  },
  data() {
    return {
      userInfo: {
        name: '未知',
        phone: '',
        avatar: '/static/logo.png' // 使用项目中的logo作为默认头像
      },
      loading: false,
      statsData: [
        { value: 0, label: '赛事订单' },
        { value: 0, label: '商城订单' },
        { value: 0, label: '我的积分' }
      ],
      services: [
        {
          type: 'registration',
          label: '报名信息',
          icon: '/static/mine/ranking_mine.png'
        },
        {
          type: 'address',
          label: '收件地址',
          icon: '/static/mine/mall_address_mine.png'
        },
        {
          type: 'photos',
          label: '参赛照片',
          icon: '/static/mine/images_mine.png'
        },
        {
          type: 'service',
          label: '客服',
          icon: '/static/mine/client_mine.png'
        },
        {
          type: 'settings',
          label: '设置',
          icon: '/static/mine/setting_mine.png'
        }
      ]
    }
  },
  async onShow() {
    await this.loadUserInfo();
  },
  
  onPullDownRefresh() {
    this.loadUserInfo().then(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    /**
     * 加载用户信息
     */
    async loadUserInfo() {
      try {
        this.loading = true;
        const response = await getUserInfo();
        
        // 适配不同的响应格式
        let userData = null;
        if (response && response.data) {
          userData = response.data;
        } else if (response && response.code === 200) {
          userData = response.data || response;
        } else if (response) {
          userData = response;
        }
        
        if (userData) {
          // 更新用户信息 - 支持多种字段名
          this.userInfo = {
            id: userData.id,
            name: userData.nickname  || '未知',
            phone: this.formatPhone(userData.phone || ''),
            avatar: userData.avatar  || '/static/logo.png'
          };
          
          // 更新统计数据 - 支持多种数据结构
         
            this.statsData = [
              { value: userData.extendParams.competitionOrderNum  || 0, label: '赛事订单' },
              { value: userData.extendParams.mallOrderNum   || 0, label: '商城订单' },
              { value: userData.points  || 0, label: '我的积分' }
            ];
          
          
          console.log('用户信息加载成功:', this.userInfo);
        } else {
          throw new Error('用户信息数据为空');
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        
        // 根据错误类型显示不同的提示
        let errorMessage = '获取用户信息失败';
        if (error.code === 401) {
          errorMessage = '登录已过期，请重新登录';
        } else if (error.code === 403) {
          errorMessage = '没有权限访问用户信息';
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
      }
    },
    
    /**
     * 格式化手机号
     */
    formatPhone(phone) {
      if (!phone) return '';
      
      // 如果已经是脱敏格式，直接返回
      if (phone.includes('*')) return phone;
      
      // 对手机号进行脱敏处理
      if (phone.length === 11) {
        return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
      }
      
      return phone;
    },
    
    /**
     * 头像加载失败处理
     */
    handleAvatarError() {
      this.userInfo.avatar = '/static/logo.png';
    },
    handleServiceClick(type) {
      console.log('点击服务:', type);
      // 根据不同的服务类型进行相应的处理
      switch(type) {
        case 'registration':
          // 跳转到报名信息列表页面
          uni.navigateTo({
            url: '/pages/registration/index'
          });
          break;
        case 'address':
          // 跳转到收件地址页面
          uni.navigateTo({
            url: '/pages/address/index'
          });
          break;
        case 'photos':
          // 跳转到参赛照片页面
          uni.navigateTo({
            url: '/pages/photos/index'
          });
          break;
        case 'service':
          // 微信客服功能通过 button open-type="contact" 直接处理
          // 无需额外处理逻辑
          break;
        case 'settings':
          // 跳转到设置页面，传递用户信息
          const userInfoStr = encodeURIComponent(JSON.stringify(this.userInfo));
          uni.navigateTo({
            url: `/pages/settings/index?userInfo=${userInfoStr}`
          });
          break;
      }
    },
    
    /**
     * 处理统计数据点击事件
     */
    handleStatClick(data) {
      console.log('点击统计数据:', data);
      
      switch(data.type) {
        case 'score':
          // 跳转到积分记录页面，传递当前积分
          uni.navigateTo({
            url: `/pages/score/index?totalScore=${data.stat.value}`
          });
          break;
        case 'competition_order':
          // 跳转到赛事订单页面
          uni.navigateTo({
            url: '/pages/competition/orders'
          });
          break;
        case 'mall_order':
        uni.navigateTo({
            url: '/pages/order/list'
          });
          break;
        default:
          console.log('未知的统计类型:', data.type);
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

/* 顶部用户信息区域 */
.header-section {
  padding: 60rpx 40rpx 32rpx 40rpx;
  position: relative;
  z-index: 9999;
}

.user-info {
  display: flex;
  align-items: center;
  
}

.avatar-container {
  margin-right: 30rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.phone {
  font-size: 28rpx;
  color: #333;
}




.section-title {
  font-size: 44rpx;
  font-weight: bold;
  color: #333;
  
  padding-left: 20rpx;
  padding-bottom: 20rpx;
}

.services-grid {
  background: #fff;
  border-radius: 20rpx;
  margin: 0 20rpx;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400rpx;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #999;
}
.header-section-left{
  position: absolute;
  width: 314px;
  height: 318px;
  flex-shrink: 0;
  border-radius: 318px;
  background: radial-gradient(50% 50% at 50% 50%, var(--Color, #83BB0B) 0%, rgba(131, 187, 11, 0.00) 100%);
  top: -300rpx;
    left: -200rpx;
    z-index: -1;
}

.header-section-right{
  width: 270px;
height: 248px;
flex-shrink: 0;
border-radius: 259px;
background: radial-gradient(50% 50% at 50% 50%, var(--, rgba(255, 107, 1, 0.72)) 0%, rgba(255, 107, 1, 0.00) 100%);
position: absolute;
top: -260rpx;
    right: -80rpx;
    z-index: -1;
}

</style>