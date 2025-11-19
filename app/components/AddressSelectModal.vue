<template>
  <view v-if="visible" class="address-modal-overlay" @tap="handleClose">
    <view class="address-modal" @tap.stop>
      <view class="modal-title">选择收货地址</view>
      
      <view class="address-list">
        <view 
          v-for="(address, index) in addressList" 
          :key="address.id"
          class="address-option"
          :class="{ active: selectedAddress && selectedAddress.id === address.id }"
          @tap="handleSelectAddress(address)"
        >
          <view class="address-info">
            <view class="contact-line">
              <text class="contact-name">{{ address.userName }}</text>
              <text class="contact-phone">{{ address.telNumber }}</text>
              <view v-if="address.isDefault === 1" class="default-tag">默认</view>
            </view>
            <view class="address-detail">
              <text class="address-text">{{ formatAddress(address) }}</text>
            </view>
          </view>
          <view class="select-icon" v-if="selectedAddress && selectedAddress.id === address.id">✓</view>
        </view>
      </view>
      
      <view class="address-modal-footer">
        <button class="add-address-btn" @tap="handleAddAddress">新增地址</button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'AddressSelectModal',
  props: {
    // 控制弹窗显示
    visible: {
      type: Boolean,
      default: false
    },
    // 地址列表
    addressList: {
      type: Array,
      default: () => []
    },
    // 当前选中的地址
    selectedAddress: {
      type: Object,
      default: null
    }
  },
  methods: {
    // 格式化地址显示
    formatAddress(address) {
      if (!address) return '';
      const parts = [
        address.provinceName,
        address.cityName,
        address.countyName,
        address.detailInfoNew || address.detailInfo
      ].filter(part => part && part.trim());
      
      return parts.join('');
    },

    // 选择地址
    handleSelectAddress(address) {
      this.$emit('select', address);
    },

    // 关闭弹窗
    handleClose() {
      this.$emit('close');
    },

    // 新增地址
    handleAddAddress() {
      this.$emit('add-address');
    }
  }
}
</script>

<style scoped>
/* 地址选择弹窗样式 */
.address-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1001;
  display: flex;
  align-items: flex-end;
}

.address-modal {
  width: 100%;
  background: #ffffff;
  border-radius: 32rpx 32rpx 0 0;
  max-height: 70vh;
  overflow-y: auto;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  text-align: center;
  padding: 32rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.address-list {
  padding: 0 24rpx;
}

.address-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.address-option:last-child {
  border-bottom: none;
}

.address-option.active {
  background: rgba(126, 211, 33, 0.1);
  margin: 0 -24rpx;
  padding: 24rpx;
}

.address-info {
  flex: 1;
}

.contact-line {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.contact-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
}

.contact-phone {
  font-size: 24rpx;
  color: #666666;
}

.default-tag {
  background: #ff6b35;
  color: #ffffff;
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.address-detail {
  margin-top: 8rpx;
}

.address-text {
  font-size: 24rpx;
  color: #666666;
  line-height: 1.4;
}

.select-icon {
  width: 40rpx;
  height: 40rpx;
  background: #7ED321;
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: bold;
}

.address-modal-footer {
  padding: 24rpx;
  border-top: 1rpx solid #f0f0f0;
}

.add-address-btn {
  width: 100%;
  height: 88rpx;
  background: #7ED321;
  color: #ffffff;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
}
</style>
