<template>
  <view class="order-card" @click="handleClick">
    <view class="card-content">
      <!-- 赛事缩略图 -->
      <view class="competition-image">
        <image 
          :src="order.competitionId_dictText.split('=')[1]" 
          mode="aspectFill"
          class="image"
        />
      </view>

      <!-- 赛事信息 -->
      <view class="competition-info">
        <!-- 赛事标题 -->
        <view class="competition-title">
          <text class="competition-name">{{ order.competitionId_dictText.split('=')[0]  }}</text>
        </view>

        <!-- 时间和地点 -->
        <view class="time-location">
          <view class="time-info">
			  <image src="/static/competition_order/clock.png"   mode="aspectFill" class="time-icon" />
            <text class="time-text">{{ order.competitionDate + ' ' + order.competitionSession +'-' + order.competitionEndSession }}</text>
          </view>
          <view class="location-info">
			<image src="/static/competition_order/participant_kid.png"   mode="aspectFill" class="location-icon" />
            <text class="location-text">{{ order.participantName    }}</text>
          </view>
        </view>

        <!-- 标签区域 -->
        <view class="tags-container">
          <!-- 年龄组标签 -->
          <view class="age-tag">
            <text class="age-text">{{ order.groupId_dictText }}</text>
          </view>
          
          
          <!-- 状态标签 -->
          <view class="status-tag" :class="getStatusClass(order.competitionStatus)">
            <text class="status-text">{{  order.competitionStatus }}</text>
          </view>
        </view>
      </view>
    </view>

    
  </view>
</template>

<script>
export default {
  name: 'OrderCard',
  props: {
    order: {
      type: Object,
      required: true,
      default: () => ({})
    },
    showActions: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    handleClick() {
      this.$emit('click', this.order);
    },

    handleAction(action, order) {
      this.$emit('action', { action, order });
    },

    /**
     * 格式化日期
     */
    formatDate(dateString) {
      if (!dateString) return '--';
      
      try {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      } catch (error) {
        console.error('日期格式化失败:', error);
        return dateString;
      }
    },

    /**
     * 格式化比赛日期
     */
    formatCompetitionDate(dateString) {
      if (!dateString) return '2025-07-28 10:00-12:00';
      
      try {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        
        // 假设比赛持续2小时
        const endHours = String(date.getHours() + 2).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}-${endHours}:${minutes}`;
      } catch (error) {
        console.error('日期格式化失败:', error);
        return '2025-07-28 10:00-12:00';
      }
    },

  

    /**
     * 获取状态样式类
     */
    getStatusClass(status) {
      // 根据状态返回不同的样式类
      const classMap = {
        '待支付': 'status-registration',
        'PAID': 'status-paid',
        'CONFIRMED': 'status-confirmed',
        'CANCELLED': 'status-cancelled',
        '已退款': 'status-cancelled',
        '比赛结束': 'status-cancelled',
        '比赛中': 'status-progress',
        '待开赛': 'status-registration',
        '超时未支付': 'status-registration'
      };
      return classMap[status]  ;
    }
  }
}
</script>

<style scoped>
.order-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.card-content {
  display: flex;
  padding: 24rpx;
}

/* 赛事缩略图 */
.competition-image {
  width: 220rpx;
  height: 220rpx;
  border-radius: 12rpx;
  overflow: hidden;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.image {
  width: 100%;
  height: 100%;
}

/* 赛事信息区域 */
.competition-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

/* 赛事标题 */
.competition-title {
  margin-bottom: 16rpx;
}

.competition-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
}

/* 时间和地点 */
.time-location {
  margin-bottom: 16rpx;
}

.time-info,
.location-info {
  display: flex;
  align-items: center;
  margin-bottom: 6rpx;
}

.time-icon,
.location-icon {
  width: 24rpx;
  height: 24rpx;
  margin-right: 8rpx;
  
}

.time-text,
.location-text {
  font-size: 24rpx;
  color: #666;
  line-height: 1.4;
}

/* 标签容器 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  align-items: center;
}

/* 年龄组标签 */
.age-tag {
  background: #83BB0B;
  border-radius: 8rpx;
   padding: 0rpx 12rpx 6rpx 12rpx;
}

.age-text {
  font-size: 22rpx;
  color: #fff;
  font-weight: 500;
}

/* 参赛者标签 */
.participant-tag {
  background: #f0f0f0;
  border-radius: 8rpx;
  padding: 6rpx 12rpx;
}

.participant-text {
  font-size: 22rpx;
  color: #666;
}

/* 状态标签 */
.status-tag {
  border-radius: 8rpx;
  padding: 0rpx 12rpx 6rpx 12rpx;
}

.status-text {
  font-size: 22rpx;
  font-weight: 500;
}

/* 不同状态的样式 */
.status-registration {
  background: #EBEBEB;
}

.status-registration .status-text {
  color: #333;
}

.status-progress {
  background: #fa8c16;
}

.status-progress .status-text {
  color: #fff;
}

.status-paid {
  background: #e6f7ff;
}

.status-paid .status-text {
  color: #1890ff;
}

.status-confirmed {
  background: #e6f7ff;
}

.status-confirmed .status-text {
  color: #1890ff;
}

.status-cancelled {
  background: #333;
 
}

.status-cancelled .status-text {
  color: #ffff
}

.status-refunded {
  background: #f9f0ff;
}

.status-refunded .status-text {
  color: #722ed1;
}

/* 订单操作 */
.order-actions {
  display: flex;
  gap: 16rpx;
  justify-content: flex-end;
  padding: 0 24rpx 24rpx;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 20rpx;
  margin-top: 20rpx;
}

.action-btn {
  padding: 12rpx 24rpx;
  border-radius: 8rpx;
  font-size: 26rpx;
  border: none;
  min-width: 120rpx;
}

.pay-btn {
  background: #ff4d4f;
  color: #fff;
}

.detail-btn {
  background: #1890ff;
  color: #fff;
}

.cancel-btn {
  background: #fff;
  color: #999;
  border: 1rpx solid #d9d9d9;
}

.action-btn:active {
  transform: scale(0.98);
}
</style>