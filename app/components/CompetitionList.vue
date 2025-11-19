<template>
  <view class="competition-container">
    
    <view class="competition-list">
      <view 
        v-for="(item, index) in competitionList" 
        :key="item.id || index"
        class="competition-item"
        @tap="handleItemClick(item)"
      >
        <view class="competition-image-container">
          <image 
            :src="item.coverImage || '/static/logo.png'" 
            class="competition-image"
            mode="aspectFill"
          />
          <view v-if="item.status" class="status-badge" :class="getStatusClass(item.competitionStatus)">
            {{ getStatusText(item.competitionStatus) }}
          </view>
        </view>
        
        <view class="competition-info">
          <view class="competition-title">{{ item.name }}</view>
          <view class="competition-meta">
            <text class="meta-text">{{ item.address }}  {{ formatDate(item.startTime) +'~'+ formatDate(item.endTime)}}</text>
          </view>
        </view>
      </view>
    </view>
    
    
    
    <!-- 空状态 -->
    <view v-if="competitionList.length === 0 && !loading" class="empty-state">
    
      <text class="empty-text">暂无赛事信息</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CompetitionList',
  props: {
    competitionList: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    showLoadMore: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleItemClick(item) {
      this.$emit('itemClick', item);
    },
    

    
    handleLoadMore() {
      this.$emit('loadMore');
    },
    
    formatDate(dateStr) {
      if (!dateStr) return '';
      let splitDate = dateStr.split(' ')[0];
      const date = new Date(splitDate);
      const month = date.getMonth() + 1; // getMonth()返回0-11，需要+1
      const day = date.getDate();
      return `${month}.${day}`;
    },
    
    getStatusText(status) {
      const statusMap = {
        'NOT_STARTED': '未开始报名',
        'REGISTRATION_OPEN': '报名中', 
        'REGISTRATION_CLOSED': '报名结束',
        'IN_PROGRESS': '比赛中',
        'FINISHED': '比赛结束'
      };
      return statusMap[status] || status;
    },
    
    getStatusClass(status) {
      return {
        'upcoming': status === 'NOT_STARTED',
        'ongoing': status === 'IN_PROGRESS' || status === '已报名' || status === 'REGISTRATION_OPEN',
        'finished': status === 'FINISHED',
        'cancelled': status === 'REGISTRATION_CLOSED' 
      };
    }
  }
}
</script>

<style scoped>
.competition-container {
  
  border-radius: 20rpx 20rpx 0 0;
  margin-top: -20rpx;
  position: relative;
  z-index: 2;
  padding: 0 20rpx;
  min-height: 40vh;
}

.section-header {
  padding: 30rpx 0 20rpx;
  text-align: center;
}

.section-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
}

.competition-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  padding-bottom: 40rpx;
}

.competition-item {
  background: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}

.competition-item:active {
  transform: scale(0.98);
}

.competition-image-container {
  position: relative;
  width: 100%;
  height: 240rpx;
  overflow: hidden;
}

.competition-image {
  width: 100%;
  height: 100%;
}

.status-badge {
  position: absolute;
  top: 20rpx;
  right: -45rpx;
  width: 180rpx;
  height: 45rpx;
  background: rgba(82, 196, 26, 0.95);
  color: #ffffff;
  font-size: 30rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: rotate(45deg);
  transform-origin: center;
  clip-path: polygon(20% 0%, 80% 0%, 100% 100%, 0% 100%);
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
  padding: 5rpx 0;
}

.status-badge.upcoming {
  background: rgba(24, 144, 255, 0.95);
}

.status-badge.ongoing {
  background: rgba(82, 196, 26, 0.95);
}

.status-badge.finished {
  background: rgba(140, 140, 140, 0.95);
}

.status-badge.cancelled {
  background: rgba(255, 77, 79, 0.95);
}

.competition-info {
  padding: 20rpx;
  background: #ffffff; 
}

.competition-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 12rpx;
  line-height: 1.4;
}

.competition-meta {
 
}

.meta-text {
  font-size: 24rpx;
  color: #666666;
}

.meta-date {
  font-size: 24rpx;
  color: #999999;
  padding-left: 50rpx;
}

.load-more {
  text-align: center;
  padding: 40rpx;
}

.load-more-text {
  color: #666666;
  font-size: 28rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding:  100rpx 20rpx;
}

.empty-icon {
  width: 120rpx;
  height: 120rpx;
  opacity: 0.3;
  margin-bottom: 20rpx;
}

.empty-text {
  color: #999999;
  font-size: 28rpx;
}
</style>