<template>
  <view class="goods-card" @tap="handleGoodsClick">
    <view class="goods-image-container">
      <image 
        :src="goods.img" 
        class="goods-image"
        mode="aspectFill"
        :lazy-load="true"
      />
      <view v-if="goods.typeName" class="goods-type">{{ goods.typeName }}</view>
    </view>
    
    
  </view>
  <view class="price-container">
    <view class="goods-info">
      <view class="goods-name">{{ goods.name }}</view>
      
     
      
      <!-- <view class="goods-meta">
        <text class="sales-number">已售 {{ goods.salesNumber }}{{ goods.unit }}</text>
        <text class="goods-no">货号: {{ goods.goodsNo }}</text>
      </view> -->
    </view>
        <view class="price-row">
          <text class="original-price">¥{{ goods.amount }}</text>
        </view>
        <!-- <view class="price-row">
          <text class="price-label">会员价</text>
          <text class="member-price">¥{{ goods.memAmount }}</text>
        </view>
        <view v-if="goods.vipAmount !== goods.memAmount" class="price-row">
          <text class="price-label">VIP价</text>
          <text class="vip-price">¥{{ goods.vipAmount }}</text>
        </view> -->
      </view>
</template>

<script>
export default {
  name: 'GoodsCard',
  props: {
    goods: {
      type: Object,
      required: true,
      default: () => ({})
    }
  },
  methods: {
    handleGoodsClick() {
      this.$emit('goodsClick', this.goods);
      
      // 跳转到商品详情页面
      uni.navigateTo({
        url: `/pages/mall/detail?id=${this.goods.id}`
      });
    }
  }
}
</script>

<style scoped>
.goods-card {
  background: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  margin-bottom: 24rpx;
}

.goods-image-container {
  position: relative;
  width: 100%;
  height: 400rpx;
}

.goods-image {
  width: 100%;
  height: 100%;
}

.goods-type {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  background: rgba(0, 0, 0, 0.7);
  color: #ffffff;
  font-size: 24rpx;
  padding: 8rpx 12rpx;
  border-radius: 8rpx;
}

.goods-info {
  /* padding: 24rpx; */
}

.goods-name {
  font-size:24rpx;
  
  color: #333333;
  margin-bottom: 16rpx;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-container {
  margin-bottom: 16rpx;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.price-row:last-child {
  margin-bottom: 0;
}

.price-label {
  font-size: 24rpx;
  color: #666666;
}

.original-price {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
}

.member-price {
  font-size: 32rpx;
  color: #ff6b35;
  font-weight: bold;
}

.vip-price {
  font-size: 32rpx;
  color: #e91e63;
  font-weight: bold;
}

.goods-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 24rpx;
  color: #999999;
}

.sales-number {
  color: #ff6b35;
}

.goods-no {
  color: #cccccc;
}
</style>
