<template>
  <!-- 弹窗遮罩层 -->
  <view class="modal-mask" v-if="visible" @tap="handleClose">
    <!-- 弹窗内容区域 -->
    <view class="modal-content" @tap.stop>
      <!-- 弹窗标题 -->
      <view class="modal-title" v-if="title">{{ title }}</view>
      
      <!-- 插槽内容 -->
      <slot></slot>
    </view>
  </view>
</template>

<script setup>
// 定义props
const props = defineProps({
  // 控制弹窗显示/隐藏
  visible: {
    type: Boolean,
    default: false
  },
  // 弹窗标题
  title: {
    type: String,
    default: ''
  }
})

// 定义emit
const emit = defineEmits(['close'])

// 关闭弹窗
const handleClose = () => {
  emit('close')
}
</script>

<style scoped>
/* 弹窗遮罩层 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

/* 弹窗内容 */
.modal-content {
  background-color: white;
  width: 80%;
  max-width: 500rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-sizing: border-box;
}

/* 弹窗标题 */
.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30rpx;
  color: #333;
}
</style>