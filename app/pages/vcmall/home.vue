<template>
  <view class="mall-home-container">
   

    <!-- 轮播图 -->
    <view class="banner-section">
      <swiper 
        class="banner-swiper"
        :indicator-dots="true"
        :autoplay="true"
        :interval="3000"
        :duration="500"
        :circular="true"
        indicator-color="rgba(255, 255, 255, 0.5)"
        indicator-active-color="#ffffff"
      >
        <swiper-item v-for="(item, index) in bannerList" :key="index">
          <view class="banner-item" @tap="handleBannerClick(item)">
            <image 
              :src="item.gallery" 
              class="banner-image"
              mode="aspectFill"
            />
            <!-- <view class="banner-content">
              <view class="banner-title">{{ item.title }}</view>
              <view class="banner-subtitle">{{ item.subtitle }}</view>
              <view class="banner-price">
                <text class="price-label">最低</text>
                <text class="price-value">{{ item.price }}</text>
              </view>
              <view class="banner-description">{{ item.description }}</view>
              <view class="banner-button">
                <text class="button-text">{{ item.buttonText }}</text>
              </view>
            </view> -->
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 主要功能卡片区域 -->
    <view class="main-features">
      <!-- 我要洗鞋 -->
      <view class="feature-card shoe-cleaning" @tap="goToShoeService">
        <view class="card-icon">
          <image src="/static/vcmall/wash.png" class="icon-image" mode="aspectFit" />
        </view>
        <view class="card-content">
          <text class="card-title">我要洗鞋</text>
          <text class="card-subtitle">在线下单</text>
        </view>
        <view class="card-button">
          <text class="button-text">下单</text>
        </view>
      </view>

      <!-- 我的订单 -->
      <view class="feature-card my-orders" @tap="goToOrders">
        <view class="card-icon">
          <image src="/static/vcmall/allOrder.png" class="icon-image" mode="aspectFit" />
        </view>
        <view class="card-content">
          <text class="card-title">我要买鞋</text>
          <text class="card-subtitle">在线下单</text>
        </view>
        <view class="card-button">
          <text class="button-text">下单</text>
        </view>
      </view>
    </view>

    <!-- 底部功能入口 -->
    <view class="bottom-features">
      <!-- 我的推广 -->
      <view class="bottom-card" @tap="goToPromotion">
        <view class="bottom-icon">
          <image src="/static/vcmall/promotion.png" class="icon-image" mode="aspectFit" />
        </view>
        <text class="bottom-title">我的推广</text>
        <text class="bottom-subtitle">推广有收益</text>
      </view>

      <!-- 成为会员 -->
      <view class="bottom-card" @tap="goToMembership">
        <view class="bottom-icon">
          <image src="/static/vcmall/membership.png" class="icon-image" mode="aspectFit" />
        </view>
        <text class="bottom-title">成为会员</text>
        <text class="bottom-subtitle">会员福利</text>
      </view>

      <!-- 联系客服 -->
      <view class="bottom-card" >
        <button class="bottom-icon" open-type="contact">
          <image src="/static/vcmall/customer-service.png" class="icon-image" mode="aspectFit" />
        </button>
        <text class="bottom-title">联系客服</text>
        <text class="bottom-subtitle">使用规则</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getMallHomeInfo } from '../../api/mall.js';

export default {
  name: 'MallHome',
  data() {
    return {
      bannerList: [
        
      ]
    }
  },
  onLoad() {
    this.loadBannerData();
  },
  methods: {
    // 加载轮播图数据
    async loadBannerData() {
      try {
        const data = await getMallHomeInfo();
        console.log(data);
        if (data) {
          this.bannerList = data.bannerList;
        }
        console.log('加载轮播图数据成功:', data);
      } catch (error) {
        console.error('加载轮播图数据失败:', error);
        // 保持默认数据
      }
    },

    // 轮播图点击事件
    handleBannerClick(item) {
      console.log('轮播图点击:', item);
      if (item.url) {
        uni.navigateTo({
          url: item.url
        });
      }
    },

    // 跳转到洗鞋服务
    goToShoeService() {
      uni.navigateTo({
        url: '/pages/vcmall/goods?group=shoe'
      });
    },

    // 跳转到我的订单
    goToOrders() {
      uni.navigateTo({
        url: '/pages/vcmall/goods?group=mall'
      });
    },

    // 跳转到推广页面
    goToPromotion() {
      uni.navigateTo({
        url: '/pages/vcmall/promotion'
      });
    },

    // 跳转到会员页面
    goToMembership() {
      uni.navigateTo({
        url: '/pages/vcmall/memberOption'
      });
    },

    // 跳转到客服页面
    goToCustomerService() {
      uni.navigateTo({
        url: '/pages/customer-service/index'
      });
    }
  }
}
</script>

<style scoped>
.mall-home-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 0;
}

/* 顶部标题 */
.header {
  padding: 40rpx 0 20rpx;
  text-align: center;
}

.app-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

/* 轮播图区域 */
.banner-section {
  margin-bottom: 30rpx;
}

.banner-swiper {
  height: 400rpx;
  border-radius: 20rpx;
  overflow: hidden;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 20rpx;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.banner-content {
  position: absolute;
  left: 40rpx;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
}

.banner-title {
  font-size: 48rpx;
  font-weight: bold;
  color: #f5f5f5;
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.banner-subtitle {
  font-size: 36rpx;
  color: #f5f5f5;
  margin-bottom: 15rpx;
}

.banner-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 20rpx;
}

.price-label {
  font-size: 28rpx;
  color: #f5f5f5;
  margin-right: 10rpx;
}

.price-value {
  font-size: 48rpx;
  font-weight: bold;
  color: #ff6b35;
}

.banner-description {
  font-size: 24rpx;
  color: #666;
  line-height: 1.5;
  margin-bottom: 30rpx;
  white-space: pre-line;
}

.banner-button {
  position: absolute;
  right: -200rpx;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.9);
  padding: 15rpx 30rpx;
  border-radius: 50rpx;
  backdrop-filter: blur(10rpx);
}

.button-text {
  font-size: 28rpx;
  font-weight: bold;
  color: #f5f5f5;
  
}

/* 主要功能卡片 */
.main-features {
  display: flex;
  gap: 20rpx;
  margin: 0 30rpx 40rpx;
}

.feature-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20rpx;
  padding: 40rpx 30rpx;
  text-align: center;
  position: relative;
  backdrop-filter: blur(10rpx);
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.card-icon {
  margin-bottom: 20rpx;
}

.icon-image {
  width: 80rpx;
  height: 80rpx;
}

.card-content {
  margin-bottom: 30rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 15rpx;
}

.card-subtitle {
  font-size: 24rpx;
  color: #666;
  display: block;
  line-height: 1.4;
}

.card-button {
  background: #333;
  color: #fff;
  padding: 20rpx 40rpx;
  border-radius: 50rpx;
  font-size: 28rpx;
  font-weight: bold;
}

/* 底部功能入口 */
.bottom-features {
  display: flex;
  gap: 20rpx;
  margin: 0 30rpx;
  padding-bottom: 40rpx;
}

.bottom-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20rpx;
  padding: 30rpx 20rpx;
  text-align: center;
  backdrop-filter: blur(10rpx);
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.bottom-icon {
  background: transparent;
  padding: 0;
  margin: 0;
  border: none;
  height: 84rpx;
}
.bottom-icon::after {
  border: none;
}
.bottom-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.bottom-subtitle {
  font-size: 22rpx;
  color: #666;
  display: block;
}

/* 响应式适配 */
@media screen and (max-width: 750rpx) {
  .main-features {
    flex-direction: column;
  }
  
  .bottom-features {
    flex-wrap: wrap;
  }
  
  .bottom-card {
    flex-basis: calc(50% - 10rpx);
  }
}
</style>
