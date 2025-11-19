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
            :src="item.imageUrl" 
            class="banner-image"
            mode="aspectFill"
            :lazy-load="true"
          />
          <!-- <view class="banner-overlay">
            <view class="banner-title">{{ item.title }}</view>
          </view> -->
        </view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script>
export default {
  name: 'BannerSwiper',
  props: {
    bannerList: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    handleBannerClick(item) {
      this.$emit('bannerClick', item);
      
      // 根据跳转类型处理点击事件
      if (item.jumpType && item.jumpUrl) {
        // 这里可以根据业务需求实现跳转逻辑
        console.log('Banner clicked:', item);
      }
    }
  }
}
</script>

<style scoped>
.banner-container {
  width: 100%;
  height: 60vh; /* 占据60%的屏幕高度 */
  overflow: hidden;
  margin: 0;
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
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  padding: 40rpx 20rpx 20rpx;
}

.banner-title {
  color: #ffffff;
  font-size: 32rpx;
  font-weight: bold;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}
</style>