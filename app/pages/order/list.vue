<template>
  <view class="order-container">
    <!-- 订单状态筛选 -->
    <view class="status-tabs">
      <view 
        v-for="(tab, index) in statusTabs" 
        :key="index"
        class="tab-item"
        :class="{ active: currentStatus === tab.status }"
        @tap="switchStatus(tab.status)"
      >
        {{ tab.label }}
      </view>
    </view>

    <!-- 订单列表 -->
    <view class="order-list">
      <view v-if="loading" class="loading-container">
        <text class="loading-text">加载中...</text>
      </view>
      
      <view v-else-if="orderList.length === 0" class="empty-container">
        <text class="empty-text">暂无订单</text>
      </view>
      
      <view v-else>
        <view 
          v-for="order in orderList" 
          :key="order.id"
          class="order-item"
          @tap="goToOrderDetail(order.id)"
        >
          <!-- 订单头部信息 -->
          <view class="order-header">
            <view class="order-info">
              <text class="order-no">订单编号：{{ order.orderNo }}</text>
            </view>
            <view class="order-status">
              <text class="status-text" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </text>
            </view>
          </view>

          <!-- 商品列表 -->
          <view class="goods-list">
            <view 
              v-for="(goods, gIndex) in order.orderInfoVos" 
              :key="gIndex"
              class="goods-item"
            >
              <image :src="goods.goodsImg" class="goods-image" mode="aspectFill" />
              <view class="goods-info">
                <view class="goods-name">{{ goods.goodsName }}</view>
                <view class="goods-spec" v-if="goods.specifications">
                  规格：{{ goods.specifications }}
                </view>
                <view class="goods-price-row">
                  <text class="goods-price">¥{{ goods.amount }}</text>
                  <text class="goods-quantity">x{{ goods.goodsNum }}</text>
                </view>

              </view>
              
            </view>
          </view>

          

          <!-- 订单操作按钮 -->
          <view class="order-actions">
             
            <button 
              v-if="order.status === 0" 
              class="action-btn pay-btn"
              @tap.stop="payOrder(order.id)"
            >
              立即支付
            </button>
            <button 
              v-if="order.status === 1 || order.status === 102"
              class="action-btn refund-btn"
              @tap.stop="applyRefund(order.id)"
            >
              申请退款
            </button>
            <button 
              v-if="order.status === 3" 
              class="action-btn confirm-btn"
              @tap.stop="confirmReceive(order.id)"
            >
              确认收货
            </button>
            <button 
            v-if="order.status === 4 || order.status === 6 || order.status === 5 || order.status === 2 || order.status === 7 || order.status === 8" 
              class="action-btn detail-btn"
              @tap.stop="goToOrderDetail(order.id)"
            >
              查看详情
            </button>
            <button class="action-btn detail-btn"
                @tap.stop="servicePhone"
             >
                联系客服
             </button>
          </view>
        </view>
        
        <!-- 加载更多提示 -->
        <view v-if="loadingMore" class="loading-more">
          <text class="loading-more-text">加载中...</text>
        </view>
        
        <!-- 没有更多数据提示 -->
        <view v-else-if="orderList.length > 0 && pagination.current >= pagination.pages" class="no-more">
          <text class="no-more-text">没有更多数据了</text>
        </view>
      </view>
    </view>

    <!-- 底部安全区域 -->
    <view class="safe-area"></view>
  </view>
</template>

<script>
import { getOrderList, cancelOrder as cancelOrderApi, confirmOrder, payOrder as payOrderApi, requireRefund as applyRefundApi } from '../../api/order.js';
import { getRegistrationDetail, wechatRefund } from '@/api/user.js'
import { getServicePhone } from '@/config/index.js';
export default {
  data() {
    return {
      currentStatus: -1, // -1表示全部，0待支付，1已支付，2超时未支付，3已发货，4收货完成，5退款操作，6已退款
      orderList: [],
      loading: false,
      loadingMore: false, // 加载更多状态
      pagination: {
        current: 1,    // 当前页码
        size: 10,      // 分页大小
        total: 0,      // 总记录数
        pages: 0       // 总页数
      },
      statusTabs: [
        { label: '全部', status: -1 },
        { label: '待付款', status: 0 },
        { label: '待发货', status: 1 },
        { label: '待收货', status: 3 },
        { label: '已完成', status: 4 },
        { label: '已退款', status: 6 }

      ]
    }
  },
  onLoad(options) {
    if(options.status){
      this.currentStatus = Number.parseInt(options.status);
    }
  },
  onShow() {
    // 从其他页面返回时刷新订单列表
    let fromMineStatus = uni.getStorageSync('fromMine');
    if(fromMineStatus!=null){
      if(fromMineStatus==''){
        fromMineStatus = 0
      }
        this.currentStatus = fromMineStatus;
      uni.setStorageSync('fromMine',-1);
    }
    this.loadOrderList();
  },
  onPullDownRefresh() {
    this.loadOrderList(true).then(() => {
      uni.stopPullDownRefresh();
    });
  },
  onReachBottom() {
    // 上拉加载更多
    this.loadMoreOrders();
  },
  methods: {
    servicePhone(){
      uni.makePhoneCall({
        phoneNumber: getServicePhone()
        
      });
    },
    // 加载订单列表
    async loadOrderList(refresh = false) {
      try {
        this.loading = true;
        
        // 如果是刷新，重置分页
        if (refresh) {
          this.pagination.current = 1;
        }
        
        const params = {
          pageNum: this.pagination.current,
          pageSize: this.pagination.size,
        };


        // 如果不是全部状态，添加状态筛选
        if (this.currentStatus !== -1) {
          params.status = this.currentStatus;
        }
        const data = await getOrderList(params);
        
        // 更新分页信息
        this.pagination.total = data.total || 0;
        this.pagination.pages = data.pages || 0;
        this.pagination.current = data.current || 1;
        
        // 如果是刷新或第一页，直接替换数据；否则追加数据
        if (refresh || this.pagination.current === 1) {
          this.orderList = data.records || [];
        } else {
          this.orderList = [...this.orderList, ...(data.records || [])];
        }
      } catch (error) {
        console.error('加载订单列表失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 加载更多订单
    async loadMoreOrders() {
      // 如果已经是最后一页或正在加载，则不执行
      if (this.loadingMore || this.pagination.current >= this.pagination.pages) {
        return;
      }
      
      try {
        this.loadingMore = true;
        this.pagination.current += 1;
        
        const params = {
          pageNum: this.pagination.current,
          pageSize: this.pagination.size
        };
        
        // 如果不是全部状态，添加状态筛选
        if (this.currentStatus !== -1) {
          params.status = this.currentStatus;
        }
        
        const data = await getOrderList(params);
        
        // 追加新数据
        if (data.records && data.records.length > 0) {
          this.orderList = [...this.orderList, ...data.records];
        }
        
        // 更新分页信息
        this.pagination.total = data.total || 0;
        this.pagination.pages = data.pages || 0;
      } catch (error) {
        console.error('加载更多失败:', error);
        // 加载失败时回退页码
        this.pagination.current -= 1;
        uni.showToast({
          title: '加载更多失败',
          icon: 'none'
        });
      } finally {
        this.loadingMore = false;
      }
    },

    // 切换订单状态
    switchStatus(status) {
      this.currentStatus = status;
      // 重置分页并加载第一页数据
      this.pagination.current = 1;
      this.loadOrderList(true);
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

    // 跳转到订单详情
    goToOrderDetail(orderId) {
      uni.navigateTo({
        url: `/pages/order/detail?id=${orderId}`
      });
    },

    // 取消订单
    async cancelOrder(orderId) {
      uni.navigateTo({
        url: `/pages/order/detail?id=${orderId}`
      });
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
                this.loadOrderList(true);
              }, 1500);
            }else{
              uni.showToast({
              title: '支付失败'+paymentParams.msg,
              icon: 'success'
            });
            }
            // 重新加载订单列表
           
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

              // 重新加载订单列表
                this.loadOrderList(true);
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
    async applyRefund(orderId) {
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
                    orderId : orderId,
                    refundReason : res.content,
                }
                const response = await applyRefundApi(data);

                if (response && response.code === 200) {
                  uni.showToast({
                    title: '退款申请成功',
                    icon: 'success'
                  });
                  // 重新加载订单列表
                  setTimeout(() => {
                    this.loadOrderList(true);
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
.order-container {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 状态筛选标签 */
.status-tabs {
  background: #ffffff;
  display: flex;
  padding: 0 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 32rpx 0;
  font-size: 28rpx;
  color: #666666;
  position: relative;
}

.tab-item.active {
  color: #4285f4;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background: #4285f4;
  border-radius: 2rpx;
}

/* 订单列表 */
.order-list {
  padding: 20rpx 24rpx;
}

.loading-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.loading-text,
.empty-text {
  font-size: 28rpx;
  color: #999999;
  margin-top: 24rpx;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
}

/* 订单项 */
.order-item {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
  margin-bottom: 16rpx;
}

.order-info {
  flex: 1;
}

.order-no {
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  display: block;
  margin-bottom: 8rpx;
}

.order-time {
  font-size: 24rpx;
  color: #999999;
}

.order-status {
  text-align: right;
}

.status-text {
  font-size: 26rpx;
  font-weight: bold;
}

.status-pending { color: #ff6b35; }
.status-paid { color: #333333; }
.status-timeout { color: #333333; }
.status-shipped { color: #333333; }
.status-completed { color: #333333; }
.status-refunding { color: #333333; }
.status-refunded { color: #333333; }

/* 商品列表 */
.goods-list {
  margin-bottom: 16rpx;
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
  width: 220rpx;
  height: 220rpx;
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
  font-size: 26rpx;
  color: #333333;
  font-weight: bold;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-spec {
  font-size: 22rpx;
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
  color: #333333;
  font-weight: bold;
}

.goods-quantity {
  font-size: 22rpx;
  color: #666666;
}

/* 订单金额 */
.order-amount {
  text-align: right;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
  margin-bottom: 16rpx;
}

.amount-label {
  font-size: 26rpx;
  color: #666666;
}

.amount-value {
  font-size: 32rpx;
  color: #333333;
  font-weight: bold;
}

/* 订单操作按钮 */
.order-actions {
  display: flex;
  gap: 16rpx;
  flex-direction: row-reverse;
}

.action-btn {
  border-radius: 40rpx;
  font-size: 24rpx;
  border: 1rpx solid #e0e0e0;
  background: #ffffff;
  color: hsl(0, 0%, 40%);
  margin: 0;
}

.cancel-btn {
  border-color: #999999;
  color: #999999;
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
  border-color: black;
  color: #ffffff;
}

.detail-btn {
  border-color: #333333;
  color: #333333;
}

/* 加载更多提示 */
.loading-more,
.no-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx 0;
}

.loading-more-text,
.no-more-text {
  font-size: 24rpx;
  color: #999999;
}

/* 底部安全区域 */
.safe-area {
  height: 40rpx;
}
</style>
