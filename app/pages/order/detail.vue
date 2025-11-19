<template>
  <view class="order-detail-container">
    
    
    <!-- 订单详情内容 -->
    <view  class="detail-content">
      <!-- 订单状态卡片 -->
      <view class="status-card">
        <view class="status-info">
          <view class="status-top">
            <view class="status-left">订单状态</view>
            <text class="status-text" :class="getStatusClass(orderDetail.status)">
            {{ getStatusText(orderDetail.status) }}
          </text>
          </view>
          <view class="address-detail">
            <view class="address-top">
               <image class="icon-localtion" src="/static/address/location.png" mode="aspectFill"></image>
               <view class="address-text">
                {{ orderDetail.addressVo.provinceName }}{{ orderDetail.addressVo.cityName }}{{ orderDetail.addressVo.countyName }}{{ orderDetail.addressVo.detailInfoNew }}
              </view>
            </view>
            <view class="address-bottom">
                <view class="address-name">{{ orderDetail.addressVo.userName }}</view>
                <view class="address-phone">{{ orderDetail.addressVo.telNumber }}</view>
            </view>
          </view>
          <view class="tracking-no">
            <view class="tracking-no-title">快递单号</view>
            <view class="tracking-no-value">{{ orderDetail.trackingNo||'暂无' }}
            <text class="tracking-no-copy" @tap="copyTrackingNo" v-if="orderDetail.trackingNo">复制</text>

            </view>
            
        </view>

        </view>
      </view>

      <!-- 商品信息 -->
      <view class="goods-card">
        <view class="card-title">订单详情</view>
        <view class="goods-list">
          <view 
            v-for="(goods, index) in orderDetail.orderInfoVos" 
            :key="index"
            class="goods-item"
          >
            <image :src="goods.goodsImg" class="goods-image" mode="aspectFill" />
            <view class="goods-info">
              <view class="goods-name">{{ goods.goodsName }}</view>
              <view class="goods-spec" v-if="goods.specifications">
                规格：{{ goods.specifications }}
              </view>
              <view class="goods-type" v-if="goods.goodsTypeName">
                分类：{{ goods.goodsTypeName }}
              </view>
              <view class="goods-price-row">
                <text class="goods-price">¥{{ goods.unitPrice }}</text>
                <text class="goods-quantity">x{{ goods.goodsNum }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

             <!-- 订单金额 -->
       <view class="amount-card">
         <view class="card-title">订单金额</view>
         <view class="amount-row">
           <text class="amount-label">商品总额</text>
           <text class="amount-value">¥{{ totalGoodsPrice() }}</text>
         </view>
         <view class="amount-row" v-if="orderDetail.freight!=null&&orderDetail.freight!=0">
           <text class="amount-label">运费</text>
           <text class="amount-value">¥{{orderDetail.freight }}</text>
         </view>
         <view class="amount-row total">
           <text class="amount-label">实付金额</text>
           <text class="amount-value total-amount">¥{{ orderDetail.amount }}</text>
         </view>
       </view>

       <!-- 订单信息 -->
       <view class="order-info-card">
         <view class="order-info-row">
           <text class="info-label">订单编号</text>
           <text class="info-value">{{ orderDetail.orderNo }}</text>
         </view>
         <view class="order-info-row">
           <text class="info-label">创建时间</text>
           <text class="info-value">{{  orderDetail.insertTime }}</text>
         </view>
         <view class="order-info-row" v-if="orderDetail.payTime">
           <text class="info-label">付款时间</text>
           <text class="info-value">{{ orderDetail.payTime }}</text>
         </view>
       </view>
        
       </view>

      <view class="order-info-card" v-if="orderDetail.status === 8">
		  <view class="order-info-row">
		    <text class="info-label">退款驳回原因</text>
		    <text class="info-value">{{ orderDetail.rejectReason }}</text>
		  </view>
	  </view>

      <!-- 订单操作按钮 -->
      <view class="action-buttons">
        <button 
          v-if="orderDetail.status === 0" 
          class="action-btn pay-btn"
          @tap="payOrder(orderDetail.id)"
        >
          立即支付
        </button>
        <button 
          v-if="orderDetail.status === 1||orderDetail.status === 102"
          class="action-btn refund-btn"
          @tap="applyRefund(orderDetail)"
        >
          申请退款
        </button>
        <button 
          v-if="orderDetail.status === 3" 
          class="action-btn confirm-btn"
          @tap="confirmReceive(orderDetail.id)"
        >
          确认收货
        </button>
        <button class="action-btn refund-btn"
                @tap.stop="servicePhone"
             >
                联系客服
             </button>
      </view>

 

    <!-- 底部安全区域 -->
    <view class="safe-area"></view>
  </view>
</template>

<script>
import { getOrderDetail, payOrder as payOrderApi, confirmOrder, requireRefund } from '../../api/order.js';
import { getServicePhone } from '@/config/index.js';
export default {
  data() {
    return {
      orderId: '',
      orderDetail: null,
      loading: false
    }
  },
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      this.loadOrderDetail();
    }
  },
  methods: {
    totalGoodsPrice(){
      return this.orderDetail.orderInfoVos.reduce((total, item) => {
        return total + (parseFloat(item.unitPrice || 0) * parseInt(item.goodsNum || 0));
      }, 0).toFixed(2);
    },
    servicePhone(){
      uni.makePhoneCall({
        phoneNumber: getServicePhone()
      });
    },
    copyTrackingNo(){
      uni.setClipboardData({
        data: this.orderDetail.trackingNo,
        success: () => {
          uni.showToast({
            title: '复制成功',
            icon: 'success'
          });
        }
      });
    },
    // 加载订单详情
    async loadOrderDetail() {
      if (!this.orderId) return;
      
      try {
        this.loading = true;
        const data = await getOrderDetail(this.orderId);
        this.orderDetail = data.order;
      } catch (error) {
        console.error('加载订单详情失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return '';
      const date = new Date(timeStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },

    // 获取订单状态文本
    getStatusText(status) {
      const statusMap = {
        0: '待支付',
        1: '已支付',
        2: '超时未支付',
        3: '已发货',
        4: '已完成',
        5: '退款中',
        6: '已退款',
        7: '申请退款',
        8: '退款驳回',
        201: '已接单',
        102: '待接单'
      };
      return statusMap[status] || '未知状态';
    },

    // 获取订单状态样式类
    getStatusClass(status) {
      const classMap = {
        0: 'status-pending',
        1: 'status-paid',
        2: 'status-timeout',
        3: 'status-shipped',
        4: 'status-completed',
        5: 'status-refunding',
        6: 'status-refunded',
        7: 'status-refunding',
        8: 'status-refunded'
      };
      return classMap[status] || '';
    },

    // 支付订单
    async payOrder(orderId) {
      try {
        const paymentParams = await payOrderApi(orderId);
        
        uni.requestPayment({
          provider: 'wxpay',
          timeStamp: paymentParams.timeStamp,
          nonceStr: paymentParams.nonceStr,
          package: paymentParams.package,
          signType: paymentParams.signType,
          paySign: paymentParams.paySign,
          success: (res) => {
            console.log('支付成功:', res);
           
            if(paymentParams.result){
                setTimeout(() => {
                this.loadOrderDetail();
              }, 1500);
            }else{
              uni.showToast({
              title: '支付失败'+paymentParams.msg,
              icon: 'success'
            });
            }
            // 重新加载订单详情
           
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
      } catch (error) {
        console.error('发起支付失败:', error);
        uni.showToast({
          title: '支付发起失败',
          icon: 'none'
        });
      }
    },

    // 确认收货
    async confirmReceive(orderId) {
      try {
        await uni.showModal({
          title: '确认收货',
          content: '确定已收到商品吗？',
          success: async (res) => {
            if (res.confirm) {
              await confirmOrder(orderId);
      
              uni.showToast({
                title: '确认收货成功',
                icon: 'success'
              });

              // 重新加载订单详情
              this.loadOrderDetail();
            }
          }
        });
      } catch (error) {
        if (error.errMsg && error.errMsg.includes('cancel')) {
          return; // 用户取消操作
        }
        
        console.error('确认收货失败:', error);
        uni.showToast({
          title: '确认失败',
          icon: 'none'
        });
      }
    },

    // 申请退款
    async applyRefund(order) {
      try {
        await uni.showModal({
          title: '申请退款',
          editable: true,
          content: '',
          success: async (res) => {
            if (res.confirm) {
              try {
                uni.showLoading({
                  title: '申请退款中...'
                });
                let data = {
                    orderId : order.id,
                    refundReason : res.content,
                }
                const response = await requireRefund(data);

                if (response && response.code === 200) {
                  uni.showToast({
                    title: '退款申请成功',
                    icon: 'success'
                  });
                  // 重新加载订单详情
                  setTimeout(() => {
                    this.loadOrderDetail();
                  }, 1500);
                } else {
                  uni.showToast({
                    title: '退款申请失败：' + (response.msg || '未知错误'),
                    icon: 'none'
                  });
                }
              } catch (apiError) {
                console.error('退款申请失败:', apiError);
                uni.showToast({
                  title: '退款申请失败',
                  icon: 'none'
                });
              } finally {
                uni.hideLoading();
              }
            }
          }
        });
      } catch (error) {
        if (error.errMsg && error.errMsg.includes('cancel')) {
          return; // 用户取消操作
        }
        
        console.error('申请退款失败:', error);
        uni.showToast({
          title: '申请失败',
          icon: 'none'
        });
      }
    }
  }
}
</script>

<style scoped>
.order-detail-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

/* 加载和错误状态 */
.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.loading-text,
.error-text {
  font-size: 28rpx;
  color: #999999;
  margin-bottom: 24rpx;
}

.retry-btn {
  background: #7ED321;
  color: #ffffff;
  border-radius: 40rpx;
  padding: 16rpx 32rpx;
  font-size: 26rpx;
  border: none;
}

 /* 卡片通用样式 */
 .status-card,
 .goods-card,
 .amount-card,
 .address-card,
 .logistics-card,
 .order-info-card {
   background: #ffffff;
   border-radius: 16rpx;
   padding: 24rpx;
   margin-bottom: 20rpx;
 }

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
}

/* 订单状态卡片 */
.status-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.status-text {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.status-pending { color: #ff6b35; }
.status-paid { color: #7ED321; }
.status-timeout { color: #999999; }
.status-shipped { color: #007aff; }
.status-completed { color: #7ED321; }
.status-refunding { color: #ff9500; }
.status-refunded { color: #999999; }

.order-no,
.order-time,
.pay-time {
  font-size: 26rpx;
  color: #666666;
}

/* 商品信息 */
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.goods-item {
  display: flex;
  gap: 16rpx;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-image {
  width: 200rpx;
  height: 200rpx;
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

.goods-spec,
.goods-type {
  font-size: 24rpx;
  color: #999999;
}

.goods-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.goods-price {
  font-size: 28rpx;
  color: #000;
  font-weight: bold;
}

.goods-quantity {
  font-size: 24rpx;
  color: #666666;
}

/* 订单金额 */
.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
}

.amount-row.total {
  border-top: 1rpx solid #f0f0f0;
  padding-top: 16rpx;
  margin-top: 8rpx;
}

.amount-label {
  font-size: 26rpx;
  color: #666666;
}

.amount-value {
  font-size: 26rpx;
  color: #333333;
}

.total-amount {
  font-size: 30rpx;
  font-weight: bold;
  color: #000;
}

/* 收货地址 */
.address-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.address-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.receiver-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
}

.receiver-phone {
  font-size: 26rpx;
  color: #666666;
}

.address-detail {
  font-size: 26rpx;
  line-height: 1.4;
}

/* 物流信息 */
.logistics-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.tracking-no,
.delivery-time {
  font-size: 26rpx;
  color: #666666;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10rpx;

}
.tracking-no-title{
  font-size: 26rpx;
  color: #666666;
}
.tracking-no-value{
  font-size: 26rpx;
  color: #000;
}
/* 操作按钮 */
.action-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #f0f0f0;
  display: flex;
  gap: 16rpx;
  justify-content: flex-end;
}

.action-btn {
  border-radius: 40rpx;
  font-size: 28rpx;
  border: 1rpx solid #e0e0e0;
  background: #ffffff;
  color: #666666;
  margin: 0;
}

.pay-btn {
  background: #ff6b35;
  border-color: #ff6b35;
  color: #ffffff;
}

.refund-btn {
  border-color: #333333;
  color: #333333;
}

.confirm-btn {
  background: #333333;
  border-color: #333333;
  color: #ffffff;
}

/* 底部安全区域 */
.safe-area {
  height: calc(120rpx + env(safe-area-inset-bottom));
}

.address-top{
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-weight: bold;
  padding-bottom: 10rpx;
}
.address-bottom{
    display: flex;
  align-items: center;
  gap: 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
  padding-bottom: 20rpx;
}
.address-name{
  font-size: 26rpx;
  color: #999;
  padding-left: 48rpx;

}
.address-phone{
  font-size: 26rpx;
  color: #999;
}
.icon-localtion{
  width: 40rpx;
  height: 40rpx;
}
.status-top{
    border-bottom: 1rpx solid #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 10rpx;
    margin-bottom: 10rpx;
}
.status-left{
    font-size: 26rpx;
    color: #999;
}
.status-text{
    font-size: 26rpx;
    color: #000;
}
 .tracking-no-copy{
     color: #999;
 
 }

 /* 订单信息 */
 .order-info-row {
   display: flex;
   justify-content: space-between;
   align-items: center;
   padding: 16rpx 0;
   border-bottom: 1rpx solid #f0f0f0;
 }

 .order-info-row:last-child {
   border-bottom: none;
 }

 .info-label {
   font-size: 26rpx;
   color: #666666;
 }

 .info-value {
   font-size: 26rpx;
   color: #333333;
   font-weight: 500;
 }
 </style>
