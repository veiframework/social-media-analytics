<template>
  <view class="stats-container">
    <view 
      class="stat-item" 
      v-for="(stat, index) in stats" 
      :key="index"
      @click="handleStatClick(stat, index)"
    >
      <text class="stat-number">{{ stat.value }}</text>
      <text class="stat-label">{{ stat.label }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'UserStatsCard',
  props: {
    stats: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    handleStatClick(stat, index) {
      this.$emit('statClick', {
        stat: stat,
        index: index,
        type: this.getStatType(stat.label)
      });
    },
    
    getStatType(label) {
      if (label.includes('积分')) {
        return 'score';
      } else if (label.includes('赛事订单')) {
        return 'competition_order';
      } else if (label.includes('商城订单')) {
        return 'mall_order';
      }
      return 'unknown';
    }
  }
}
</script>

<style scoped>
.stats-container {
  display: flex;
  justify-content: space-between;
 
  
  padding: 40rpx 0;
  margin: 0 -10rpx;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.stat-label {
  font-size: 28rpx;
  color: #333;
}
</style>