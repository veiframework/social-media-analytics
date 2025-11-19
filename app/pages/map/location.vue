<template>
  <view class="map-container">
    <!-- 地图组件 -->
    <map
      class="map"
      :latitude="latitude"
      :longitude="longitude"
      :markers="markers"
      :show-location="true"
      :enable-3D="true"
      :enable-zoom="true"
      :enable-scroll="true"
      :enable-rotate="true"
      :enable-satellite="false"
      :enable-traffic="false"
      @markertap="onMarkerTap"
      @callouttap="onCalloutTap"
    >
    </map>
    
    <!-- 底部信息卡片 -->
    <view class="location-info">
      <view class="info-card">
        <view class="location-name">{{ locationName }}</view>
        
        <view class="info-item" v-if="competitionTime">
          <view class="info-label">比赛时间：</view>
          <view class="info-value">{{ competitionTime }}</view>
        </view>
        
        <view class="info-item" v-if="locationAddress">
          <view class="info-label">比赛地点：</view>
          <view class="info-value">{{ locationAddress }}</view>
        </view>
        
        <view class="info-item" v-if="organizer">
          <view class="info-label">主办方：</view>
          <view class="info-value">{{ organizer }}</view>
        </view>
        <button class="btn-nav" @click="openMap">导航至比赛地点</button>
      </view>
    </view>

  </view>
</template>

<script>
export default {
  name: 'LocationMap',
  data() {
    return {
      latitude: 39.908823, // 默认北京坐标
      longitude: 116.39747,
      locationName: '',
      locationAddress: '',
      competitionTime: '',
      organizer: '',
      markers: []
    }
  },

  onLoad(options) {
    console.log('地图页面参数:', options);
    
    // 获取传递的参数
    if (options.name) {
      this.locationName = decodeURIComponent(options.name);
    }
    
    if (options.address) {
      this.locationAddress = decodeURIComponent(options.address);
    }
    
    if (options.competitionTime) {
      this.competitionTime = decodeURIComponent(options.competitionTime);
    }
    
    if (options.organizer) {
      this.organizer = decodeURIComponent(options.organizer);
    }
    
    // 如果有经纬度参数，直接使用
    if (options.latitude && options.longitude) {
      this.latitude = parseFloat(options.latitude);
      this.longitude = parseFloat(options.longitude);
    }
    
    // 设置地图标记
    this.setupMarker();
    
    // 设置页面标题
    if (this.locationName) {
      uni.setNavigationBarTitle({
        title: this.locationName.length > 10 ? 
          this.locationName.substring(0, 10) + '...' : 
          this.locationName
      });
    }
  },

  methods: {
    openMap() {
      const latitude= Number(this.latitude)
      const longitude = Number(this.longitde || this.longitude)
      const name = this.locationName
      const address = this.locationAddress

      console.log(latitude)
      uni.openLocation({
        latitude,
        longitude,
        name,
        address
      })
},

    /**
     * 设置地图标记
     */
    setupMarker() {
      this.markers = [{
        id: 1,
        latitude: this.latitude,
        longitude: this.longitude,
        title: this.locationName || '目标位置',
        iconPath: '/static/map/marker.png', // 可以添加自定义标记图标
        width: 30,
        height: 30,
        callout: {
          content: this.locationName || '目标位置',
          color: '#333333',
          fontSize: 14,
          borderRadius: 4,
          bgColor: '#ffffff',
          padding: 8,
          display: 'ALWAYS'
        }
      }];
    },

    /**
     * 标记点击事件
     */
    onMarkerTap(e) {
      console.log('标记点击:', e);
    },

    /**
     * 气泡点击事件
     */
    onCalloutTap(e) {
      console.log('气泡点击:', e);
    }
  }
}
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 100vh;
  position: relative;
}

.map {
  width: 100%;
  height: 100%;
}

/* 底部信息卡片 */
.location-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: transparent;
  padding: 20rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.info-card {
  background: white;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.location-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  text-align: center;
}

/* 信息项 */
.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16rpx;
  padding: 8rpx 0;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 28rpx;
  color: #999;
  min-width: 140rpx;
  flex-shrink: 0;
}

.info-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  line-height: 1.5;
  flex: 1;
}
.btn-nav {
  font-size: 28rpx;
  background-color: #83BB0B;
  color: white;
  border-radius: 30rpx;
}
</style>
