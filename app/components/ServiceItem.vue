<template>
  <!-- 微信客服按钮 -->
  <button v-if="type === 'service'" 
          open-type="contact" 
          class="service-item wechat-service-btn" 
          :class="{ 'active': isActive }"
          @click="handleClick">
    <image class="service-icon" :src="icon" mode="aspectFit"></image>
    <text class="service-label">{{ label }}</text>
  </button>
  
  <!-- 普通服务项 -->
  <view v-else 
        class="service-item" 
        @click="handleClick" 
        :class="{ 'active': isActive }">
    <image class="service-icon" :src="icon" mode="aspectFit"></image>
    <text class="service-label">{{ label }}</text>
  </view>
</template>

<script>
export default {
  name: 'ServiceItem',
  props: {
    icon: {
      type: String,
      required: true
    },
    label: {
      type: String,
      required: true
    },
    type: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      isActive: false
    }
  },
  methods: {
    handleClick() {
      this.isActive = true;
      setTimeout(() => {
        this.isActive = false;
      }, 150);
      
      this.$emit('click', this.type);
    }
  }
}
</script>

<style scoped>
.service-item {
  width: 170rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 20rpx;
  box-sizing: border-box;
  border-radius: 20rpx;
  transition: background-color 0.15s ease;
}

.service-item.active {
  background: #f0f0f0;
}

/* 微信客服按钮样式 */
.wechat-service-btn {
  border: none;
  background: transparent;
  outline: none;
}

.wechat-service-btn::after {
  border: none;
}

.service-icon {
  width: 60rpx;
  height: 60rpx;
  margin: 15rpx 0;
}

.service-label {
  font-size: 24rpx;
  color: #333;
  text-align: center;
}
</style>