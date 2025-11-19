<template>
  <view class="goods-container">
    <!-- 顶部横幅 -->
    <!-- <view class="top-banner">
      <view class="banner-content">
        <image class="gift-icon" src="/static/mine/gift.png" mode="aspectFit" />
        <text class="banner-text">充值送会员</text>
        <text class="banner-subtitle">下单更优惠</text>
      </view>
      <view class="banner-action">
        <text class="action-text">去看看</text>
      </view>
    </view> -->

    <!-- 主体内容区域 -->
    <view class="main-content">
      <!-- 左侧分类导航 -->
      <view class="category-sidebar">
        <view 
          class="category-item"
          :class="{ 'active': currentCategory === category.id }"
          v-for="category in categoryList" 
          :key="category.id"
          @tap="selectCategory(category)"
        >
          <text class="category-text">{{ category.name }}</text>
        </view>
      </view>

      <!-- 右侧商品列表 -->
      <view class="goods-content">
        <view class="goods-list">
          <view 
            class="goods-item"
            v-for="goods in filteredGoodsList"
            :key="goods.id"
            @tap="handleGoodsClick(goods)"
          >
            <view class="goods-image-container">
              <image 
                :src="goods.img" 
                class="goods-image"
                mode="aspectFill"
              />
            </view>
            
            <view class="goods-info">
              <view class="goods-name">{{ goods.name }}</view>
              <view class="goods-desc" v-if="userInfo.extendParams.memGrade==2">
                会员价
              </view>
              <view class="goods-price">
                <text class="price-symbol">¥</text>
                <text class="price-value">{{ userInfo.extendParams.memGrade==2 ? goods.memAmount: goods.amount }}</text>
                <text class="price-unit">/双</text>
              </view>
            </view>
            
            <view class="goods-action">
              <view class="add-btn" @tap.stop="addToCart(goods)">
                <text class="add-icon">+</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 暂无数据 -->
        <view v-if="filteredGoodsList.length === 0 && !loading" class="empty-state">
          <text class="empty-text">该分类暂无商品</text>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 底部栏 -->
    <view class="bottom-bar">
      <view class="cart-info">
        <view class="cart-icon-wrapper" @tap="goToCart">
          <image class="cart-icon" src="/static/goods_detail/cart.png" mode="aspectFit" />
          <view v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</view>
        </view>
        <view class="price-info">
          <text class="price-label">预计金额</text>
          <text class="total-price">¥{{ totalAmount.toFixed(2) }}</text>
        </view>
      </view>
      
      <view class="order-btn" @tap="goToOrder">
        <text class="order-text">去下单</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getMallHomeInfo,getGoodsType } from '../../api/mall.js';
import { addToCart, getCartList } from '../../api/cart.js';
import { getUserInfo } from '../../api/user.js';

export default {
  data() {
    return {
      loading: false,
      currentCategory: 'all',
      categoryList: [
        { id: 'all', name: '全部' },
        
      ],
      goodsList: [],
      group: null,
      cartCount: 0,
      totalAmount: 0,
      userInfo: {}
    }
  },
  computed: {
    filteredGoodsList() {
      if (this.currentCategory === 'all') {
        return this.goodsList;
      }
      return this.goodsList.filter(goods => goods.type === this.currentCategory);
    }
  },
  onLoad(options) {
    if(options.group){
      console.log('group',options.group);
      this.group = options.group;
      uni.setStorageSync('group', options.group);
    }
    this.loadGoodsType();
    this.loadGoodsData();
    this.loadCartCount();
  },
  onShow() {
    // 从其他页面返回时刷新购物车数量和金额
    this.loadCartCount();
    this.loadUserInfo()
  },
  onPullDownRefresh() {
    this.loadGoodsData().finally(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    async loadUserInfo(){
      this.userInfo = await getUserInfo();

    },
    async loadGoodsType() {
      const data = await getGoodsType({parentId:this.group=='shoe'? 337:338,shopId: 12});
      this.categoryList = [{id:'all' ,name:'全部'},...data];
    },
    async loadGoodsData() {
      try {
        this.loading = true;
        const data = await getMallHomeInfo({goodsParentType:this.group=='shoe'? 337:338 });
        
        // 为商品添加分类信息（模拟数据）
        this.goodsList = (data.goodsList || []).map((goods, index) => ({
          ...goods,
          category: this.getCategoryByIndex(index),
        }));
        
        console.log('商品数据加载成功:', this.goodsList);
      } catch (error) {
        console.error('加载商品数据失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    getCategoryByIndex(index) {
      const categories = ['sports', 'leather', 'basketball', 'wash', 'socks', 'curtain'];
      return categories[index % categories.length];
    },

    selectCategory(category) {
      this.currentCategory = category.id;
    },

    handleGoodsClick(goods) {
      console.log('商品点击:', goods);
      uni.navigateTo({
        url: `/pages/mall/detail?id=${goods.id}`
      });
    },

    async addToCart(goods) {
      try {
        const cartData = {
          goodsId: goods.id,
          specificationId: goods.goodsSpecificationVoList[0].id, // 如果没有规格ID，使用商品ID
          number: 1,
          group: this.group
        };
        
        await addToCart(cartData);
        
        // 添加成功后刷新购物车数量和金额
        this.loadCartCount();
        
        uni.showToast({
          title: '已添加到购物车',
          icon: 'success'
        });
      } catch (error) {
        console.error('添加购物车失败:', error);
        uni.showToast({
          title: '添加失败',
          icon: 'none'
        });
      }
    },

    async loadCartCount() {
      try {
        const data = await getCartList({group: this.group});
        if (data.carList && Array.isArray(data.carList)) {
          this.cartCount = data.carList.length;
          // 计算总金额
          this.totalAmount = data.totalPrice;
        } else {
          this.cartCount = 0;
          this.totalAmount = 0;
        }
      } catch (error) {
        console.error('加载购物车数量失败:', error);
        this.cartCount = 0;
        this.totalAmount = 0;
      }
    },

    goToCart() {
      uni.navigateTo({
        url: '/pages/cart/index'
      });
    },

    goToOrder() {
      if (this.cartCount === 0) {
        uni.showToast({
          title: '购物车为空',
          icon: 'none'
        });
        return;
      }
      
      uni.navigateTo({
        url: '/pages/cart/index'
      });
    }
  }
}
</script>

<style scoped>
.goods-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx; /* 为底部栏留出空间 */
}

/* 顶部横幅 */
.top-banner {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
  padding: 24rpx 32rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.banner-content {
  display: flex;
  align-items: center;
}

.gift-icon {
  width: 48rpx;
  height: 48rpx;
  margin-right: 16rpx;
}

.banner-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-right: 12rpx;
}

.banner-subtitle {
  font-size: 24rpx;
  color: #666666;
}

.banner-action {
  background: rgba(255, 255, 255, 0.8);
  padding: 12rpx 24rpx;
  border-radius: 24rpx;
}

.action-text {
  font-size: 24rpx;
  color: #333333;
}

/* 主体内容 */
.main-content {
  display: flex;
  height: calc(100vh - 120rpx);
}

/* 左侧分类导航 */
.category-sidebar {
  width: 200rpx;
  background: #ffffff;
  border-right: 1rpx solid #f0f0f0;
}

.category-item {
  padding: 32rpx 24rpx;
  text-align: center;
  border-bottom: 1rpx solid #f5f5f5;
  position: relative;
}

.category-item.active {
  background: #f8f8f8;
}

.category-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6rpx;
  height: 40rpx;
  background: #4285f4;
  border-radius: 0 6rpx 6rpx 0;
}

.category-text {
  font-size: 28rpx;
  color: #333333;
}

.category-item.active .category-text {
  color: #4285f4;
  font-weight: bold;
}

/* 右侧商品列表 */
.goods-content {
  flex: 1;
  background: #ffffff;
}

.goods-list {
  padding: 0 24rpx;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.goods-image-container {
  width: 120rpx;
  height: 120rpx;
  margin-right: 24rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.goods-image {
  width: 100%;
  height: 100%;
}

.goods-info {
  flex: 1;
  margin-right: 24rpx;
}

.goods-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  margin-bottom: 8rpx;
  line-height: 1.4;
}

.goods-desc {
  font-size: 24rpx;
  color: #ff4757;
  margin-bottom: 12rpx;

  background-color: pink;
  width: 80rpx;
  border-radius: 10rpx;
  text-align: center;
}

.goods-price {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 24rpx;
  color: #ff4757;
  font-weight: bold;
}

.price-value {
  font-size: 32rpx;
  color: #ff4757;
  font-weight: bold;
  margin: 0 4rpx;
}

.price-unit {
  font-size: 24rpx;
  color: #ff4757;
}

.goods-action {
  display: flex;
  align-items: center;
}

.add-btn {
  width: 60rpx;
  height: 60rpx;
  background: #4285f4;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-icon {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: bold;
  line-height: 1;
}

/* 空状态 */
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

/* 加载状态 */
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

/* 底部栏样式 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background: #ffffff;
  border-top: 1rpx solid #f0f0f0;
  display: flex;
  align-items: center;
  padding: 0 32rpx;
  z-index: 1000;
  box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.cart-info {
  flex: 1;
  display: flex;
  align-items: center;
}

.cart-icon-wrapper {
  position: relative;
  margin-right: 24rpx;
}

.cart-icon {
  width: 48rpx;
  height: 48rpx;
}

.cart-badge {
  position: absolute;
  top: -12rpx;
  right: -12rpx;
  background: #ff4757;
  color: #ffffff;
  border-radius: 50%;
  min-width: 32rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
  font-weight: bold;
  padding: 0 6rpx;
  border: 2rpx solid #ffffff;
  box-sizing: border-box;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.price-label {
  font-size: 24rpx;
  color: #666666;
  margin-bottom: 4rpx;
}

.total-price {
  font-size: 32rpx;
  color: #333333;
  font-weight: bold;
}

.order-btn {
  background: #4285f4;
  color: #ffffff;
  padding: 24rpx 48rpx;
  border-radius: 60rpx;
  margin-left: 32rpx;
}

.order-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: bold;
}
</style>
