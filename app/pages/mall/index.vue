<template>
  <view class="mall-container">
    <!-- 轮播图 -->
    <!-- <MallBannerSwiper 
      :bannerList="bannerList" 
      @bannerClick="handleBannerClick"
    /> -->
    
    <!-- 商品列表 -->
    <view class="goods-section">
      <!-- <view class="section-header">
        <text class="section-title">精选商品</text>
        <text class="goods-count">共{{ goodsList.length }}款商品</text>
      </view> -->
      
      <view class="goods-grid">
        <view 
          class="goods-item" 
          v-for="(goods, index) in goodsList" 
          :key="goods.id"
        >
          <GoodsCard :goods="goods" @goodsClick="handleGoodsClick" />
        </view>
      </view>
      
      <!-- 暂无数据 -->
      <view v-if="goodsList.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无商品数据</text>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 悬浮购物车按钮 -->
    <view class="floating-cart" @tap="goToCart">
      <view class="cart-icon-container">
        <image class="cart-icon" src="/static/goods_detail/cart.png" mode="aspectFit" />
        <view v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</view>
      </view>
      <text class="cart-text">购物车</text>
    </view>
  </view>
</template>

<script>
import MallBannerSwiper from '../../components/MallBannerSwiper.vue';
import GoodsCard from '../../components/GoodsCard.vue';
import { getMallHomeInfo } from '../../api/mall.js';
import { getCartList } from '../../api/cart.js';

export default {
  components: {
    MallBannerSwiper,
    GoodsCard
  },
  data() {
    return {
      bannerList: [],
      goodsList: [],
      promotionGood: null,
      loading: false,
      cartCount: 0
    }
  },
  onLoad() {
    this.loadMallData();
    this.loadCartCount();
  },
  onShow() {
    // 从其他页面返回时刷新购物车数量
    this.loadCartCount();
  },
  onPullDownRefresh() {
    this.loadMallData().finally(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    async loadMallData() {
      try {
        this.loading = true;
        const data = await getMallHomeInfo();
        
        this.bannerList = data.bannerList || [];
        this.goodsList = data.goodsList || [];
        this.promotionGood = data.promotionGood;
        
        console.log('商城数据加载成功:', data);
      } catch (error) {
        console.error('加载商城数据失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },
    
    handleBannerClick(banner) {
      console.log('轮播图点击:', banner);
      // 轮播图点击事件已在组件内部处理跳转
    },
    
    handleGoodsClick(goods) {
      console.log('商品点击:', goods);
      // 商品点击事件已在组件内部处理跳转
    },

    async loadCartCount() {
      try {
        const data = await getCartList();
        this.cartCount = data.carList ? data.carList.length : 0;
      } catch (error) {
        console.error('加载购物车数量失败:', error);
        this.cartCount = 0;
      }
    },

    goToCart() {
      uni.navigateTo({
        url: '/pages/cart/index'
      });
    }
  }
}
</script>

<style scoped>
.mall-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.goods-section {
  padding: 0 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 0 24rpx;
}

.section-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
}

.goods-count {
  font-size: 24rpx;
  color: #999999;
}

.goods-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24rpx;
  padding-top: 24rpx;
}

.goods-item {
  width: 100%;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 120rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
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

/* 悬浮购物车按钮 */
.floating-cart {
  position: fixed;
  right: 32rpx;
  bottom: 120rpx;
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 999;
  transition: all 0.3s ease;
  border: solid 1rpx;
  background-color: #fff;
}

.floating-cart:active {
  transform: scale(0.95);
}

.cart-icon-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4rpx;
}

.cart-icon {
  width: 60rpx;
  height: 60rpx;
}

.cart-badge {
  position: absolute;
  top: -12rpx;
  right: -26rpx;
  background: #ff4757;
  color: #ffffff;
  border-radius: 50%;
  min-width: 32rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18rpx;
  font-weight: bold;
  padding: 0 6rpx;
  border: 3rpx solid #ffffff;
  box-sizing: border-box;
}

.cart-text {
  font-size: 20rpx;
  color: #000;
  font-weight: bold;
}

/* 响应式布局 */
@media screen and (min-width: 750px) {
  .goods-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (min-width: 1200px) {
  .goods-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>