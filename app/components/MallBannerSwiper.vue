<template>
  <view class="banner-container">
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
            :src="item.img" 
            class="banner-image"
            mode="aspectFill"
            :lazy-load="true"
          />
          <view class="banner-overlay">
            <view class="banner-title">{{ item.name }}</view>
            <view class="banner-price">
              <text class="member-price">¥{{ item.memAmount }}</text>
              <text class="original-price">¥{{ item.amount }}</text>
            </view>
          </view>
        </view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script>
export default {
  name: 'MallBannerSwiper',
  props: {
    bannerList: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    handleBannerClick(item) {
      this.$emit('bannerClick', item);
      
      // 跳转到商品详情页面
      uni.navigateTo({
        url: `/pages/mall/detail?id=${item.id}`
      });
    }
  }
}
</script>

<style scoped>
.banner-container {
  width: 100%;
  height: 400rpx;
  overflow: hidden;
  margin-bottom: 24rpx;
}

.banner-swiper {
  width: 100%;
  height: 100%;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  padding: 60rpx 24rpx 24rpx;
}

.banner-title {
  color: #ffffff;
  font-size: 32rpx;
  font-weight: bold;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
  margin-bottom: 12rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.banner-price {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.member-price {
  color: #ff6b35;
  font-size: 36rpx;
  font-weight: bold;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.original-price {
  color: #cccccc;
  font-size: 28rpx;
  text-decoration: line-through;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}
</style>
