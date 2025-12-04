<template>
  <view class="content">
     首页
  </view>
</template>

<script>
import {
  onLoad,
  onShow
} from "@dcloudio/uni-app";
import {ref, onMounted} from 'vue'

export default {
  setup() {




    // 退出登录

    onShow(()=>{

    })
    onMounted(() => {

    })

    return {
    }
  }
}
</script>

<style scoped>
.content {
  padding: 30rpx;
}

.header {
  text-align: center;
  margin-bottom: 40rpx;
}

.welcome {
  font-size: 40rpx;
  color: #333;
  font-weight: bold;
}

.username {
  font-size: 36rpx;
  color: #007aff;
  display: block;
  margin-top: 20rpx;
}

.main {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.card {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.card-title {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.card-content {
  font-size: 28rpx;
  color: #666;
}

.logout-btn {
  background-color: #ff4d4f;
  color: #fff;
  font-size: 32rpx;
  border-radius: 8rpx;
  margin-top: 40rpx;
}
</style>
