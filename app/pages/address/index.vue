<template>
  <view class="address-container">
    <!-- 地址列表 -->
    <view class="address-list">
      <view 
        v-for="(address, index) in addressList" 
        :key="address.id"
        class="address-item"
      >
        <!-- 地址信息 -->
        <view class="address-info">
          <view class="address-detail">
          <image class="icon-localtion" src="/static/address/location.png" mode="aspectFill"></image>
          <text class="address-text">{{ formatAddress(address) }}</text>
          </view>
          <view class="address-header">
            <view class="contact-info">
              <text class="contact-name">{{ address.userName }}</text>
              <text class="contact-phone">{{ address.telNumber }}</text>
            </view>
            <view v-if="address.isDefault === 1" class="default-tag">默认</view>
          </view>
          
         
        </view>

        <!-- 操作按钮 -->
        <view class="address-actions">
          <view class="action-btn" @tap="editAddress(address)">编辑</view>
        </view>
      </view>

      <!-- 暂无数据 -->
      <view v-if="addressList.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无收件地址</text>
        <text class="empty-tip">点击下方按钮添加地址</text>
      </view>
    </view>

    <!-- 新建地址按钮 -->
    <view class="add-address-btn" @tap="addAddress">
      <text class="btn-text">新建收件地址</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script>
import { getAddressList } from '../../api/address.js';

export default {
  data() {
    return {
      addressList: [],
      loading: false
    }
  },
  onLoad() {
    this.loadAddressList();
  },
  onShow() {
    // 从其他页面返回时重新加载数据
    this.loadAddressList();
  },
  onPullDownRefresh() {
    this.loadAddressList().finally(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    async loadAddressList() {
      try {
        this.loading = true;
        const data = await getAddressList();
        this.addressList = data || [];
        console.log('地址列表加载成功:', this.addressList);
      } catch (error) {
        console.error('加载地址列表失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    formatAddress(address) {
      const parts = [
        address.provinceName,
        address.cityName,
        address.countyName,
        address.streetName,
        address.detailInfoNew || address.detailInfo
      ].filter(part => part && part.trim());
      
      return parts.join('');
    },

    addAddress() {
      uni.navigateTo({
        url: '/pages/address/edit'
      });
    },

    editAddress(address) {
      const addressStr = encodeURIComponent(JSON.stringify(address));
      uni.navigateTo({
        url: `/pages/address/edit?address=${addressStr}`
      });
    },




  }
}
</script>

<style scoped>
.address-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.address-list {
  padding: 24rpx;
}

.address-item {
  background: #ffffff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}

.address-info {
  padding: 32rpx 24rpx;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
  padding-top: 16rpx;
  padding-left: 56rpx;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.contact-name {
  font-size: 28rpx;
  color: #999;
}

.contact-phone {
  font-size: 28rpx;
  color: #999;
}

.default-tag {
  background: #ff6b35;
  color: #ffffff;
  font-size: 20rpx;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
}

.address-detail {
  margin-top: 16rpx;
  display: flex;
  align-items: center;
}

.address-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.5;
  padding-left: 16rpx;
}

.address-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
   
  padding: 10rpx 20rpx;
  border-top: 2rpx solid #f0f0f0;
}

.action-btn {
 
  text-align: center;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #000;
  border-radius: 40rpx;
  
  padding: 16rpx 32rpx;

  width: 108rpx;
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.20); 
  
}



.action-btn.primary {
  color: #ff6b35;
  font-weight: bold;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
  gap: 16rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #999999;
}

.empty-tip {
  font-size: 24rpx;
  color: #cccccc;
}

.add-address-btn {
  position: fixed;
  bottom: 40rpx;
  left: 24rpx;
  right: 24rpx;
  background: #333333;
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: bold;
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60rpx 0;
}

.loading-text {
  font-size: 28rpx;
  color: #666666;
}
.icon-localtion{
  width: 40rpx;
  height: 40rpx;

}
</style>
