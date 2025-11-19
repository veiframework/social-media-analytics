<template>
  <view class="order-container">
    <!-- 收货地址 -->
    <view class="address-section" @tap="selectAddress">
      <view v-if="selectedAddress" class="address-info">
        <view class="address-header">
          <view class="contact-info">
            <text class="contact-name">{{ selectedAddress.userName }}</text>
            <text class="contact-phone">{{ selectedAddress.telNumber }}</text>
          </view>
          <image class="arrow-icon" src="/static/arrow.png" mode="aspectFit" />
        </view>
        <view class="address-detail">
          <image class="location-icon" src="/static/address/location.png" mode="aspectFit" />
          <text class="address-text">{{ formatAddress(selectedAddress) }}</text>
        </view>
      </view>
      <view v-else class="no-address">
        <text class="no-address-text">添加收件地址</text>
      </view>
    </view>

    <view class="receive-time-section" v-if="group==='shoe'">
      <view class="time-selector" @tap="selectPickupTime">
        <view v-if="selectedPickupTime" class="time-info">
          <view class="time-header">
            <view class="time-label">上门时间</view>
            <view class="time-detail">
               <text class="time-text">{{ formatPickupTime(selectedPickupTime) }}</text>
                <image class="arrow-icon" src="/static/arrow.png" mode="aspectFit" />
            </view>
          </view>
          
        </view>
        <view v-else class="no-time">
          <view class="time-header">
            <view class="time-label">上门时间</view>
            <view class="time-detail">
              <text class="no-time-text">请选择上门时间</text>
              <image class="arrow-icon" src="/static/arrow.png" mode="aspectFit" />
            </view>
            
          </view>
        </view>
      </view>

         <view class="time-header">
            <view class="time-label">收件类型</view>
            <text class="time-detail">上门取送</text>
          </view>
    </view>

    <!-- 商品列表 -->
    <view class="goods-section">
      <view class="section-title">订单详情</view>
      <view class="goods-list">
        <view v-for="(item, index) in orderItems" :key="item.id" class="goods-item">
          <image :src="item.picUrl" class="goods-image" mode="aspectFill" />
          <view class="goods-info">
            <view class="goods-name">{{ item.goodsName }}</view>
            <view class="goods-spec" v-if="item.specifications">
              规格: {{ item.specifications }}
            </view>
            <view class="goods-price-row">
              <text class="goods-price">¥{{ item.amount }}</text>
              <text class="goods-quantity">x{{ item.number }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 订单金额 -->
    <view class="amount-section">
      <view class="amount-row">
        <text class="amount-label">商品总价</text>
        <text class="amount-value">¥{{ totalAmount }}</text>
      </view>
      <view class="amount-row" v-if="freight!=null&&freight!=0">
        <text class="amount-label">商品运费</text>
        <text class="amount-value">¥{{ freight }}</text>
      </view>
      <view class="amount-row total-row">
        <text class="amount-label total-label">实付款</text>
        <text class="amount-value total-value">¥{{totalPrice }}</text>
      </view>
    </view>

    <!-- 订单备注 -->
     

    <!-- 底部提交按钮 -->
    <view class="bottom-bar">
      <view class="total-info">
        <text class="total-text">合计: </text>
        <text class="total-price">¥{{ totalPrice }}</text>
      </view>
      <button 
        class="submit-btn" 
        :class="{ disabled: !canSubmit }"
        @tap="submitOrder"
      >
        去支付
      </button>
    </view>

    <!-- 地址选择弹窗 -->
    <AddressSelectModal
      :visible="showAddressModal"
      :addressList="addressList"
      :selectedAddress="selectedAddress"
      @select="handleAddressSelect"
      @close="closeAddressModal"
      @add-address="goToAddAddress"
    />

    <!-- 时间选择弹窗 -->
    <view v-if="showTimeModal" class="time-modal-overlay" @tap="closeTimeModal">
      <view class="time-modal" @tap.stop>
        <view class="time-modal-header">
          <text class="time-modal-title">选择上门时间</text>
          <view class="time-modal-close" @tap="closeTimeModal">
            <text class="close-text">×</text>
          </view>
        </view>
        <view class="time-selector-content">
          <!-- 左侧日期选择 -->
          <view class="date-column">
            <view 
              v-for="date in availableDates" 
              :key="date.id"
              class="date-item"
              :class="{ active: selectedDate && selectedDate.id === date.id }"
              @tap="selectDate(date)"
            >
              <text class="date-text">{{ date.label }}</text>
            </view>
          </view>
          
          <!-- 右侧时间段选择 -->
          <view class="time-column">
            <view 
              v-for="timeSlot in availableTimeSlots" 
              :key="timeSlot.id"
              class="time-slot-item"
              :class="{ 
                active: selectedTimeSlot && selectedTimeSlot.id === timeSlot.id,
                disabled: !isTimeSlotAvailable(timeSlot)
              }"
              @tap="selectTimeSlot(timeSlot)"
            >
              <text class="time-slot-text">{{ timeSlot.label }}</text>
            </view>
          </view>
        </view>
        
        <!-- 确认按钮 -->
        <view class="time-modal-footer">
          <button 
            class="confirm-btn" 
            :class="{ disabled: !canConfirmTime }"
            @tap="confirmTimeSelection"
          >
            确认
          </button>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <text class="loading-text">{{ loadingText }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getAddressList } from '../../api/address.js';
import { createOrder,createOrderFromCart,payOrder } from '../../api/order.js';
import AddressSelectModal from '../../components/AddressSelectModal.vue';

export default {
  components: {
    AddressSelectModal
  },
  data() {
    return {
      orderItems: [],
      selectedAddress: null,
      addressList: [],
      orderRemark: '',
      orderSummary: {},
      loading: false,
      loadingText: '加载中...',
      showAddressModal: false,
      totalPrice: 0,
      group: null,
      selectedPickupTime: null,
      showTimeModal: false,
      timeOptions: [],
      selectedDate: null,
      selectedTimeSlot: null,
      availableDates: [],
      availableTimeSlots: [
        { id: '08:00-09:00', label: '08:00-09:00' },
        { id: '09:00-10:00', label: '09:00-10:00' },
        { id: '10:00-11:00', label: '10:00-11:00' },
        { id: '11:00-12:00', label: '11:00-12:00' },
        { id: '12:00-13:00', label: '12:00-13:00' },
        { id: '13:00-14:00', label: '13:00-14:00' },
        { id: '14:00-15:00', label: '14:00-15:00' },
        { id: '15:00-16:00', label: '15:00-16:00' },
        { id: '16:00-17:00', label: '16:00-17:00' },
        { id: '17:00-18:00', label: '17:00-18:00' },
        { id: '18:00-19:00', label: '18:00-19:00' },
        { id: '19:00-20:00', label: '19:00-20:00' },
        { id: '20:00-21:00', label: '20:00-21:00' },
        { id: '21:00-22:00', label: '21:00-22:00' },
      ],
      freight: null,
    }
  },
      computed: {
    totalAmount() {
      return this.orderItems.reduce((total, item) => {
        return total + (parseFloat(item.amount || 0) * parseInt(item.number || 0));
      }, 0).toFixed(2);
    },
    
    canSubmit() {
      const baseRequirement = this.selectedAddress && this.orderItems.length > 0;
      // 如果是鞋类商品，还需要选择上门时间
      if (this.group === 'shoe') {
        return baseRequirement && this.selectedPickupTime;
      }
      return baseRequirement;
    },
    
    canConfirmTime() {
      return this.selectedDate && this.selectedTimeSlot;
    },
  
  },
  onLoad(options) {
    let group = uni.getStorageSync('group');
    if(group){
      this.group = group;
    }
    // 从购物车传递的商品数据
    if (options.items) {
      try {
        this.orderItems = JSON.parse(decodeURIComponent(options.items));
        this.totalPrice = options.totalPrice;
        this.freight = options.freight;
        this.fromCart = options.fromCart;
      } catch (error) {
        console.error('解析订单商品数据失败:', error);
        uni.showToast({
          title: '数据异常',
          icon: 'none'
        });
      }
    }
    
    // 处理receivingTime回显
    if (options.receivingTime) {
      try {
        this.initReceivingTime(decodeURIComponent(options.receivingTime));
      } catch (error) {
        console.error('解析接收时间失败:', error);
      }
    }
    
    this.loadAddressList();
  },
  onShow() {
    // 从地址编辑页面返回时刷新地址列表
    this.loadAddressList();
  },
  methods: {
    async loadAddressList() {
      try {
        const data = await getAddressList();
        this.addressList = data || [];
        
        // 自动选择默认地址
        const defaultAddress = this.addressList.find(addr => addr.isDefault === 1);
        if (defaultAddress) {
          this.selectedAddress = defaultAddress;
        }
      } catch (error) {
        console.error('加载地址列表失败:', error);
      }
    },

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

    selectAddress() {
      if (this.addressList.length === 0) {
        uni.showModal({
          title: '提示',
          content: '您还没有收货地址，是否前往添加？',
          success: (res) => {
            if (res.confirm) {
              uni.navigateTo({
                url: '/pages/address/edit'
              });
            }
          }
        });
        return;
      }

      // 显示地址选择弹窗
      this.showAddressModal = true;
    },

    closeAddressModal() {
      this.showAddressModal = false;
    },

    handleAddressSelect(address) {
      this.selectedAddress = address;
      this.closeAddressModal();
    },

    goToAddAddress() {
      this.closeAddressModal();
      uni.navigateTo({
        url: '/pages/address/edit'
      });
    },

    generateAvailableDates() {
      const dates = [];
      const today = new Date();
      
      // 生成未来3天的日期选项
      for (let i = 0; i < 3; i++) {
        const date = new Date(today);
        date.setDate(today.getDate() + i);
        
        const dateStr = this.formatDate(date);
        const dayLabel = i === 0 ? '今天' : i === 1 ? '明天' : '后天';
        
        dates.push({
          id: dateStr,
          date: dateStr,
          label: dayLabel,
          dateObj: new Date(date)
        });
      }
      
      this.availableDates = dates;
    },

    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },

    formatPickupTime(timeOption) {
      if (!timeOption) return '';
      return timeOption.fullLabel;
    },

    selectPickupTime() {
      this.generateAvailableDates();
      // 如果已有选中的时间，解析并设置默认选中状态
      if (this.selectedPickupTime) {
        this.parseExistingTime();
      } else {
        // 默认选中今天
        if (this.availableDates.length > 0) {
          this.selectedDate = this.availableDates[0];
        }
      }
      this.showTimeModal = true;
    },

    closeTimeModal() {
      this.showTimeModal = false;
      // 清空临时选择
      this.selectedDate = null;
      this.selectedTimeSlot = null;
    },

    selectDate(date) {
      this.selectedDate = date;
    },

    selectTimeSlot(timeSlot) {
      if (this.isTimeSlotAvailable(timeSlot)) {
        this.selectedTimeSlot = timeSlot;
      }
    },

    isTimeSlotAvailable(timeSlot) {
      if (!this.selectedDate) return false;
      
      const now = new Date();
      const selectedDate = this.selectedDate.dateObj;
      
      // 如果不是今天，所有时间段都可选
      if (selectedDate.toDateString() !== now.toDateString()) {
        return true;
      }
      
      // 如果是今天，检查时间段是否已过
      const currentHour = now.getHours();
      const timeSlotHour = parseInt(timeSlot.id.split(':')[0]);
      
      return timeSlotHour > currentHour;
    },

    confirmTimeSelection() {
      if (!this.canConfirmTime) return;
      
      // 组合选中的日期和时间段
      const combinedTime = {
        id: `${this.selectedDate.id}_${this.selectedTimeSlot.id}`,
        date: this.selectedDate.date,
        timeRange: this.selectedTimeSlot.id,
        dateLabel: this.selectedDate.label,
        timeLabel: this.selectedTimeSlot.label,
        fullLabel: `${this.selectedDate.label} ${this.selectedTimeSlot.label}`
      };
      
      this.selectedPickupTime = combinedTime;
      this.showTimeModal = false;
    },

    parseExistingTime() {
      // 解析已有的receivingTime，设置默认选中状态
      if (!this.selectedPickupTime) return;
      
      const dateStr = this.selectedPickupTime.date;
      const timeRange = this.selectedPickupTime.timeRange;
      
      // 找到对应的日期
      const foundDate = this.availableDates.find(d => d.date === dateStr);
      if (foundDate) {
        this.selectedDate = foundDate;
      }
      
      // 找到对应的时间段
      const foundTimeSlot = this.availableTimeSlots.find(t => t.id === timeRange);
      if (foundTimeSlot) {
        this.selectedTimeSlot = foundTimeSlot;
      }
    },

    initReceivingTime(receivingTimeStr) {
      // 解析receivingTime字符串，格式可能是 "2024-01-15 11:00-12:00"
      try {
        const parts = receivingTimeStr.trim().split(' ');
        if (parts.length >= 2) {
          const dateStr = parts[0]; // 2024-01-15
          const timeRange = parts[1]; // 11:00-12:00
          
          // 解析日期，判断是今天、明天还是后天
          const targetDate = new Date(dateStr);
          const today = new Date();
          const diffDays = Math.floor((targetDate - today) / (1000 * 60 * 60 * 24));
          
          let dateLabel = '';
          if (diffDays === 0) {
            dateLabel = '今天';
          } else if (diffDays === 1) {
            dateLabel = '明天';
          } else if (diffDays === 2) {
            dateLabel = '后天';
          } else {
            dateLabel = dateStr; // 超出范围，直接显示日期
          }
          
          // 找到对应的时间段
          const foundTimeSlot = this.availableTimeSlots.find(t => t.id === timeRange);
          const timeLabel = foundTimeSlot ? foundTimeSlot.label : timeRange;
          
          // 设置选中的时间
          this.selectedPickupTime = {
            id: `${dateStr}_${timeRange}`,
            date: dateStr,
            timeRange: timeRange,
            dateLabel: dateLabel,
            timeLabel: timeLabel,
            fullLabel: `${dateLabel} ${timeLabel}`
          };
        }
      } catch (error) {
        console.error('解析receivingTime失败:', error);
      }
    },

    async submitOrder() {
      if (!this.canSubmit) {
        if (!this.selectedAddress) {
          uni.showToast({
            title: '请选择收货地址',
            icon: 'none'
          });
          return;
        }
        if (this.group === 'shoe' && !this.selectedPickupTime) {
          uni.showToast({
            title: '请选择上门取送时间',
            icon: 'none'
          });
          return;
        }
        return;
      }

      try {
        this.loading = true;
        this.loadingText = '正在创建订单...';

        if(this.fromCart==='true'){
          let group = uni.getStorageSync('group');
          const orderData = {
            receiveAddressId: this.selectedAddress.id,
            orderType: 2,
            group: group
         };
         
         // 如果是鞋类商品，添加取送时间信息
         if (this.group === 'shoe' && this.selectedPickupTime) {
           orderData.pickTimeRange = this.selectedPickupTime.date + ' ' + this.selectedPickupTime.timeRange;
           orderData.pickingLogistics = 1;
           orderData.deliveryLogistics = 1;
           orderData.orderType= 3
         }

          const result = await createOrderFromCart(orderData);
          let orderVo = result.orderVo;
          let paymentParams = await payOrder(orderVo.id);
          uni.requestPayment({
          provider: 'wxpay',
          timeStamp: paymentParams.timeStamp,
          nonceStr: paymentParams.nonceStr,
          package: paymentParams.package,
          signType: paymentParams.signType,
          paySign: paymentParams.paySign,
        success: (res) => {
          console.log('支付成功:', res);
          uni.showToast({
            title: '支付成功',
            icon: 'success'
          });
          
          // 延迟跳转到订单列表
            uni.redirectTo({
              url: '/pages/order/detail?id='+orderVo.id
            });
        },
        fail: (err) => {
          console.error('支付失败:', err);
          if (err.errMsg && err.errMsg.includes('cancel')) {
            uni.showToast({
              title: '支付已取消',
              icon: 'none'
            });
          } else {
            uni.showToast({
              title: '支付失败',
              icon: 'none'
            });
          }
        }
      });
        } else {
          // 直接购买订单
          const orderItems = this.orderItems.map(item => ({
            goodsId: item.goodsId,
            specificationId: item.specificationId,
            number: item.number
          }));
          
          const orderData = {
            receiveAddressId: this.selectedAddress.id,
            goodsNum: orderItems[0].number,
            goodsId: orderItems[0].goodsId,
            specificationId: orderItems[0].specificationId,
            orderType: 4
          };
          
          // 如果是鞋类商品，添加取送时间信息
          if (this.group === 'shoe' && this.selectedPickupTime) {
            orderData.pickTimeRange = this.selectedPickupTime.date + ' ' + this.selectedPickupTime.timeRange;
            orderData.pickingLogistics = 1;
            orderData.deliveryLogistics = 1;
            orderData.orderType= 3
          }

          const result = await createOrder(orderData);
          let orderVo = result.orderVo;
          let paymentParams = await payOrder(orderVo.id);
          
          uni.requestPayment({
            provider: 'wxpay',
            timeStamp: paymentParams.timeStamp,
            nonceStr: paymentParams.nonceStr,
            package: paymentParams.package,
            signType: paymentParams.signType,
            paySign: paymentParams.paySign,
            success: (res) => {
              console.log('支付成功:', res);
              uni.showToast({
                title: '支付成功',
                icon: 'success'
              });
              
              // 延迟跳转到订单详情
                uni.redirectTo({
                  url: '/pages/order/detail?id='+orderVo.id
                });
            },
            fail: (err) => {
              console.error('支付失败:', err);
              if (err.errMsg && err.errMsg.includes('cancel')) {
                uni.showToast({
                  title: '支付已取消',
                  icon: 'none'
                });
              } else {
                uni.showToast({
                  title: '支付失败',
                  icon: 'none'
                });
              }
            }
          });
        }
        
         

      } catch (error) {
        console.error('创建订单失败:', error);
        uni.showToast({
          title: '创建订单失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
.order-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.address-section {
  background: #ffffff;
  margin-bottom: 20rpx;
  padding: 32rpx 24rpx;
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contact-info {
  display: flex;
  gap: 24rpx;
  align-items: center;
}

.contact-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.contact-phone {
  font-size: 28rpx;
  color: #666666;
}

.arrow-icon {
  width: 32rpx;
  height: 32rpx;
}

.address-detail {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.location-icon {
  width: 32rpx;
  height: 32rpx;
}

.address-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.4;
  flex: 1;
}

.no-address {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20rpx 0;
}

.no-address-text {
  font-size: 28rpx;
  color: #999999;
  font-weight: bold;
  background-color: #f5f5f5;
  padding: 20rpx 40rpx;
  border-radius: 30rpx;
}

.goods-section {
  background: #ffffff;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  padding: 32rpx 24rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.goods-list {
  padding: 0 24rpx;
}

.goods-item {
  display: flex;
  gap: 16rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.goods-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  line-height: 1.4;
}

.goods-spec {
  font-size: 24rpx;
  color: #666666;
}

.goods-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.goods-price {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
}

.goods-quantity {
  font-size: 24rpx;
  color: #666666;
}

.amount-section {
  background: #ffffff;
  padding: 32rpx 24rpx;
  margin-bottom: 20rpx;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
}

.amount-label {
  font-size: 28rpx;
  color: #666666;
}

.amount-value {
  font-size: 28rpx;
  color: #333333;
}

.total-row {
  border-top: 1rpx solid #f0f0f0;
  margin-top: 16rpx;
  padding-top: 24rpx;
}

.total-label {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.total-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.remark-section {
  background: #ffffff;
  padding: 32rpx 24rpx;
  margin-bottom: 20rpx;
}

.remark-input {
  width: 100%;
  min-height: 120rpx;
  padding: 16rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #333333;
  background: #f8f8f8;
  margin-top: 16rpx;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  padding: 24rpx;
  box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.total-info {
  flex: 1;
}

.total-text {
  font-size: 24rpx;
  color: #666666;
}

.total-price {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.submit-btn {
  background: #4285f4;
  color: #ffffff;
  border-radius: 44rpx;
  padding: 5rpx 48rpx;
  font-size: 28rpx;
  font-weight: bold;
  border: none;
  min-width: 200rpx;
}

.submit-btn.disabled {
  background: #cccccc;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #666666;
}

/* 时间选择区域样式 */
.receive-time-section {
  background: #ffffff;
  margin-bottom: 20rpx;
  padding: 32rpx 24rpx 0  24rpx;
}

.time-selector {
  cursor: pointer;
}

.time-info {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.time-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 30rpx;
}

.time-label {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.time-detail {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.time-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.4;
  flex: 1;
}

.no-time {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.no-time-text {
  font-size: 28rpx;
  color: #999999;
  padding-left: 12rpx;
}

/* 时间选择弹窗样式 */
.time-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1001;
}

.time-modal {
  background: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  width: 100%;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.time-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
  flex-shrink: 0;
}

.time-modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.time-modal-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f5f5f5;
}

.close-text {
  font-size: 36rpx;
  color: #666666;
  line-height: 1;
}

.time-selector-content {
  display: flex;
  flex: 1;
  min-height: 0;
}

.date-column {
  width: 200rpx;
  background: #f8f8f8;
  padding: 24rpx 0;
  overflow-y: auto;
  flex-shrink: 0;
}

.date-item {
  padding: 32rpx 24rpx;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.date-item.active {
  background: #ffffff;
  border-right: 6rpx solid #4285f4;
}

.date-text {
  font-size: 28rpx;
  color: #666666;
  font-weight: 500;
}

.date-item.active .date-text {
  color: #4285f4;
  font-weight: bold;
}

.time-column {
  flex: 1;
  padding: 24rpx;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.time-slot-item {
  padding: 24rpx;
  border-radius: 12rpx;
  border: 1rpx solid #e0e0e0;
  background: #ffffff;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.time-slot-item.active {
  border-color: #4285f4;
  background: #f0f8e6;
}

.time-slot-item.disabled {
  background: #f5f5f5;
  border-color: #e0e0e0;
  opacity: 0.5;
  cursor: not-allowed;
}

.time-slot-text {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.time-slot-item.active .time-slot-text {
  color: #4285f4;
  font-weight: bold;
}

.time-slot-item.disabled .time-slot-text {
  color: #999999;
}

.time-modal-footer {
  padding: 24rpx;
  border-top: 1rpx solid #f0f0f0;
  flex-shrink: 0;
}

.confirm-btn {
  width: 100%;
  height: 88rpx;
  background: #4285f4;
  color: #ffffff;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-btn.disabled {
  background: #cccccc;
  cursor: not-allowed;
}


</style>
