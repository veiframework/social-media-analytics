<template>
  <view class="dashboard">
    <view class="platform-grid">
      <view v-for="i in platformData" :key="i.platformId" class="platform-card-wrapper">
        <view class="platform-card" :style="getPlatformColor(i.platformId)">
          <view class="platform-card-header">
            <text class="platform-card-title">{{ i.platformId_dictText }}</text>
            <view class="platform-icon">{{ getPlatformIcon(i.platformId) }}</view>
          </view>
          
          <view class="metrics-container">
            <view class="metric-item">
              <text class="metric-label">作品总数</text>
              <text class="metric-value">{{ formatNumber(i.workNum) }}</text>
            </view>
            <view class="metric-item">
              <text class="metric-label">总点赞数</text>
              <text class="metric-value">{{ formatNumber(i.thumbNum) }}</text>
            </view>
            <view class="metric-item">
              <text class="metric-label">总收藏数</text>
              <text class="metric-value">{{ formatNumber(i.collectNum) }}</text>
            </view>
            <view class="metric-item">
              <text class="metric-label">总评论数</text>
              <text class="metric-value">{{ formatNumber(i.commentNum) }}</text>
            </view>
            <view class="metric-item">
              <text class="metric-label">总分享数</text>
              <text class="metric-value">{{ formatNumber(i.shareNum) }}</text>
            </view>
            <view class="metric-item">
              <text class="metric-label">总播放量</text>
              <text class="metric-value">{{ formatNumber(i.playNum) }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="platformData.length === 0" class="loading-state">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载数据中...</text>
    </view>
  </view>
</template>

<script>
import { onShow } from "@dcloudio/uni-app";
import { getPlatformStatistic } from "../../api/dashboard";

export default {
  data() {
    return {
      platformData: [],
      platformColors: {
        'douyin': '#FF7A45', // 抖音
        'kuaishou': '#00C1DE', // 快手
        'wechatvideo': '#00A0E9', // 视频号
        'xiaohongshu': '#FE2C55', // 小红书
        'xigua': '#FFD60A', // 西瓜视频
        default: '#5AC8FA' // 默认
      },
      platformIcons: {
        'douyin': '🎵',
        'kuaishou': '⚡',
        'wechatvideo': '📹',
        'xiaohongshu': '📕',
        'xigua': '🍉',
        default: '📱'
      }
    };
  },
  methods: {
    async getPlatformStatistics() {
      const res = await getPlatformStatistic();
      this.platformData = res;
    },
    getPlatformColor(platformId) {
      const color = this.platformColors[platformId] || this.platformColors.default;
      return {
        '--platform-color': color,
        '--platform-color-light': this.lightenColor(color, 20)
      };
    },
    getPlatformIcon(platformId) {
      return this.platformIcons[platformId] || this.platformIcons.default;
    },
    formatNumber(num) {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'w';
      }
      if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'k';
      }
      return num.toString();
    },
    lightenColor(color, percent) {
      // 简单的颜色提亮函数
      const num = parseInt(color.replace("#", ""), 16);
      const amt = Math.round(2.55 * percent);
      const R = (num >> 16) + amt;
      const G = (num >> 8 & 0x00FF) + amt;
      const B = (num & 0x0000FF) + amt;
      return "#" + (
        0x1000000 +
        (R < 255 ? (R < 1 ? 0 : R) : 255) * 0x10000 +
        (G < 255 ? (G < 1 ? 0 : G) : 255) * 0x100 +
        (B < 255 ? (B < 1 ? 0 : B) : 255)
      ).toString(16).slice(1);
    }
  },
  onShow() {
    this.getPlatformStatistics();
  }
};
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 30rpx 20rpx;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 40rpx;
  padding: 30rpx 0;
}

.dashboard-title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #cea156;
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 10rpx rgba(206, 161, 86, 0.1);
}

.dashboard-subtitle {
  display: block;
  font-size: 28rpx;
  color: #6c757d;
}

.platform-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(600rpx, 1fr));
  gap: 30rpx;
}

.platform-card-wrapper {
  animation: fadeInUp 0.6s ease-out;
}

.platform-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.platform-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 8rpx;
  background-color: var(--platform-color, #cea156);
}

.platform-card:hover {
  transform: translateY(-8rpx);
  box-shadow: 0 20rpx 60rpx rgba(206, 161, 86, 0.2);
}

.platform-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid rgba(206, 161, 86, 0.1);
}

.platform-card-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
}

.platform-icon {
  width: 80rpx;
  height: 80rpx;
  background-color: var(--platform-color-light, rgba(206, 161, 86, 0.2));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  box-shadow: 0 4rpx 15rpx rgba(206, 161, 86, 0.2);
}

.metrics-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.metric-item {
  background-color: #f8f9fa;
  padding: 20rpx;
  border-radius: 12rpx;
  text-align: center;
  transition: all 0.3s ease;
  border: 2rpx solid transparent;
}

.metric-item:hover {
  background-color: rgba(206, 161, 86, 0.1);
  border-color: #cea156;
  transform: translateY(-2rpx);
}

.metric-label {
  display: block;
  font-size: 24rpx;
  color: #6c757d;
  margin-bottom: 8rpx;
  font-weight: 500;
}

.metric-value {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: var(--platform-color, #cea156);
  line-height: 1.2;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 500rpx;
  color: #f3f3f3;
}

.loading-spinner {
  width: 80rpx;
  height: 80rpx;
  border: 8rpx solid rgba(166, 163, 156, 0.3);
  border-top: 8rpx solid #b7b3ac;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #6c757d;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media screen and (max-width: 768rpx) {
  .platform-grid {
    grid-template-columns: 1fr;
  }
  
  .metrics-container {
    grid-template-columns: 1fr;
  }
  
  .dashboard-title {
    font-size: 40rpx;
  }
  
  .platform-card {
    padding: 20rpx;
  }
}
</style>