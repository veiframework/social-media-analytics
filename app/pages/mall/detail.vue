<template>
  <view class="goods-detail-container">
    <!-- 商品图片轮播 -->
    <view class="goods-gallery">
      <swiper 
        class="gallery-swiper"
        :indicator-dots="true"
        :autoplay="false"
        :circular="true"
        indicator-color="rgba(255, 255, 255, 0.5)"
        indicator-active-color="#ffffff"
      >
        <swiper-item v-for="(image, index) in galleryImages" :key="index">
          <image 
            :src="image" 
            class="gallery-image"
            mode="aspectFill"
            :lazy-load="true"
            @tap="previewImage(image)"
          />
        </swiper-item>
      </swiper>
    </view>

         <!-- 商品基本信息 -->
     <view class="goods-info-section">
       <!-- 价格信息 -->
      <view class="goods-header">
        <view class="price-section">
         <text class="main-price">¥{{ goodsInfo.amount }}</text>
       </view>

       <!-- 商品标题 -->
       <view class="goods-title">{{ goodsInfo.name }}</view>
      </view>

       <!-- 规格和脚码信息 -->
       <view class="goods-specs">
         <view class="spec-item" @tap="openSpecModal">
           <text class="spec-label">规格</text>
           <view class="spec-value" v-if="goodsInfo.goodsSpecificationVoList && goodsInfo.goodsSpecificationVoList.length > 0">
             {{ selectedSpec ? selectedSpec.value : '请选择规格' }} 
             <image src="/static/arrow.png" class="arrow" mode="aspectFill" />
           </view>
           
         </view>
         <view class="spec-item" @tap="openAddressModal">
           <text class="spec-label">收货地址</text>
           <view class="spec-value" :class="{ placeholder: !selectedAddress }">
             {{ displayAddress }} 
             <image src="/static/arrow.png" class="arrow" mode="aspectFill" />

           </view>
         </view>
         
       </view>
     </view>

              <!-- 商品详情 -->
     <view class="detail-header">
       <text class="detail-title">商品详情</text>
     </view>

     <!-- 商品详情内容 -->
     <view v-if="goodsInfo.details" class="detail-section">
       <view class="detail-content">
         <u-parse :content="goodsInfo.details" @navigate="handleHref"></u-parse>
       </view>
     </view>

     <!-- 规格选择弹窗 -->
     <view v-if="showSpecModal" class="spec-modal-overlay" @tap="closeSpecModal">
       <view class="spec-modal" @tap.stop>
        <view class="goods-name">{{ goodsInfo.name }}</view>
         <!-- 弹窗头部 -->
         <view class="modal-header">
           <view class="goods-info">
             <image :src="goodsInfo.img" class="goods-thumb" mode="aspectFill" />
             <view class="goods-brief">
               <view>
                <view class="goods-price">¥{{ selectedSpec ? selectedSpec.amount : goodsInfo.amount }}</view>
               <view class="selected-spec" v-if="selectedSpec">
                 已选: {{ selectedSpec.value }}
               </view>
               </view>
               
               <!-- 数量选择 -->
               <view class="quantity-selection-inline">
                 <text class="quantity-title-inline">数量</text>
                 <view class="quantity-control">
                   <view class="quantity-btn" :class="{ disabled: quantity <= 1 }" @tap="decreaseQuantity">-</view>
                   <input class="quantity-input" v-model="quantity" type="number" />
                   <view class="quantity-btn" @tap="increaseQuantity">+</view>
                 </view>
               </view>
             </view>
           </view>
         </view>
        <view class="spec-selection goods_no">
            <view class="spec-title">商品编号</view>
            <view class="spec-option active">{{ goodsInfo.goodsNo }}</view>
        </view>
         <!-- 规格选择 -->
         <view v-if="goodsInfo.goodsSpecificationVoList && goodsInfo.goodsSpecificationVoList.length > 0" class="spec-selection">
           <view class="spec-title">{{ selectedSpec? selectedSpec.value : '规格' }}</view>
           <view class="spec-options">
             <view 
               v-for="(spec, index) in goodsInfo.goodsSpecificationVoList" 
               :key="index"
               class="spec-option"
               :class="{ active: selectedSpec && selectedSpec.id === spec.id }"
               @tap="selectSpec(spec)"
             >
               {{ spec.specification }}
             </view>
           </view>
         </view>



         <!-- 弹窗底部按钮 -->
         <view class="modal-footer">
           <button class="modal-btn btn-cart" @tap="addToCartFromModal">加入购物车</button>
           <button class="modal-btn btn-buy" @tap="buyNowFromModal">立即购买</button>
         </view>
       </view>
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

         <!-- 底部操作栏 -->
     <view class="bottom-actions">
       <view class="nav-icons">
         <view class="nav-icon" @tap="goHome">
          <image class="icon-home" src="/static/goods_detail/home.png" mode="aspectFill"></image>

           <text class="icon-text">首页</text>
         </view>
         <view class="nav-icon" @tap="goCart">
           <view class="icon-container">
             <image class="icon-cart" src="/static/goods_detail/cart.png" mode="aspectFill"></image>
             <view v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</view>
           </view>
           <text class="icon-text">购物车</text>
         </view>
       </view>
       <view class="action-buttons">
         <button class="btn btn-cart" @tap="addToCart">加入购物车</button>
         <button class="btn btn-buy" @tap="buyNow">立即购买</button>
       </view>
     </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getGoodsInfo } from '../../api/mall.js';
import { getAddressList } from '../../api/address.js';
import { addToCart, getCartList } from '../../api/cart.js';
import AddressSelectModal from '../../components/AddressSelectModal.vue';

export default {
  components: {
    AddressSelectModal
  },
  data() {
    return {
      goodsId: null,
      goodsInfo: {},
      loading: false,
      // 规格选择弹窗相关
      showSpecModal: false,
      selectedSpec: null,
      quantity: 1,
      // 收货地址相关
      selectedAddress: null,
      addressList: [],
      showAddressModal: false,
      // 购物车相关
      cartCount: 0
    }
  },
  computed: {
    galleryImages() {
      const images = [];
      
      // 添加主图
      if (this.goodsInfo.img) {
        images.push(this.goodsInfo.img);
      }
      
      // 添加图册图片
      if (this.goodsInfo.galleryList && this.goodsInfo.galleryList.length > 0) {
        images.push(...this.goodsInfo.galleryList);
      }
      
      return images;
    },
    
    displayAddress() {
      if (!this.selectedAddress) {
        return '请选择地址';
      }
      
      const parts = [
        this.selectedAddress.provinceName,
        this.selectedAddress.cityName,
        this.selectedAddress.countyName,
        this.selectedAddress.detailInfoNew || this.selectedAddress.detailInfo
      ].filter(part => part && part.trim());
      
      return parts.join('');
    }
  },
  onLoad(options) {
    if (options.id) {
      this.goodsId = options.id;
      this.loadGoodsDetail();
      this.loadAddressList();
    }
  },
  onShow() {
    // 从地址页面返回时重新加载地址列表
    this.loadAddressList();
    this.loadCartCount();

  },
  onPullDownRefresh() {
    this.loadGoodsDetail().finally(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    handleHref(href,e){
      uni.navigateTo({
        url: `/pages/competition/web?title=''&src=`+href
      })
    },
    async loadGoodsDetail() {
      if (!this.goodsId) return;
      
      try {
        this.loading = true;
        const data = await getGoodsInfo(this.goodsId);
        this.goodsInfo = data.goodsInfo || {};
        
        // 设置页面标题
        if (this.goodsInfo.name) {
          uni.setNavigationBarTitle({
            title: this.goodsInfo.name
          });
        }
        this.goodsInfo.details = this.addWordBreakToAll(this.processRichText(this.goodsInfo.details))
        // 默认选择第一个规格
       if (this.goodsInfo.goodsSpecificationVoList && this.goodsInfo.goodsSpecificationVoList.length > 0 && !this.selectedSpec) {
         this.selectedSpec = this.goodsInfo.goodsSpecificationVoList[0];
       }
        console.log('商品详情加载成功:', this.goodsInfo);
      } catch (error) {
        console.error('加载商品详情失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },
    
    previewImage(current) {
      uni.previewImage({
        current,
        urls: this.galleryImages
      });
    },
    
    async loadAddressList() {
      try {
        const data = await getAddressList();
        this.addressList = data || [];
        
        // 自动选择默认地址
        const defaultAddress = this.addressList.find(addr => addr.isDefault === 1);
        if (defaultAddress) {
          this.selectedAddress = defaultAddress;
        }
        
        console.log('地址列表加载成功:', this.addressList);
      } catch (error) {
        console.error('加载地址列表失败:', error);
        // 不显示错误提示，因为用户可能还没有添加地址
      }
    },

    async loadCartCount() {
      try {
        const data = await getCartList();
        this.cartCount = data.carList ? data.carList.length : 0;
        console.log('购物车数量:', this.cartCount);
      } catch (error) {
        console.error('加载购物车数量失败:', error);
        this.cartCount = 0;
      }
    },

    openAddressModal() {
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

    async addToCart() {
       
      
      if (!this.selectedSpec) {
        uni.showToast({
          title: '请选择商品规格',
          icon: 'none'
        });
        return;
      }
      
      try {
        const cartData = {
          goodsId: this.goodsId,
          specificationId: this.selectedSpec.id,
          number: 1
        };
        
        await addToCart(cartData);
        
        // 加入成功后刷新购物车数量
        await this.loadCartCount();
        
        uni.showToast({
          title: '已加入购物车',
          icon: 'success'
        });
      } catch (error) {
        console.error('加入购物车失败:', error);
        uni.showToast({
          title: '加入购物车失败',
          icon: 'none'
        });
      }
    },
    
   buyNow() {
      // 检查是否选择了规格
      if (!this.selectedSpec) {
        uni.showToast({
          title: '请选择商品规格',
          icon: 'none'
        });
        return;
      }
      
      
      // 构建订单商品数据
      const orderItem = {
        id: this.goodsId,
        goodsId: this.goodsId,
        goodsName: this.goodsInfo.name,
        picUrl: this.goodsInfo.img,
        amount: this.selectedSpec.amount,
        number: 1,
        specifications: this.selectedSpec.value,
        specificationId: this.selectedSpec.id
      };
      
      // 跳转到创建订单页面
      const items = encodeURIComponent(JSON.stringify([orderItem]));
      const totalPrice = this.selectedSpec.amount;
      
      uni.navigateTo({
        url: `/pages/order/create?items=${items}&totalPrice=${totalPrice}&fromCart=false`+'&freight='+this.goodsInfo.freight
      });
     },
     
     goHome() {
       uni.switchTab({
         url: '/pages/vcmall/home'
       });
     },
     
     goCart() {
       uni.navigateTo({
         url: '/pages/cart/index'
       });
     },
     
     // 规格选择弹窗相关方法
     openSpecModal() {
       this.showSpecModal = true;
       
     },
     
     closeSpecModal() {
       this.showSpecModal = false;
     },
     
     selectSpec(spec) {
       this.selectedSpec = spec;
     },
     
     decreaseQuantity() {
       if (this.quantity > 1) {
         this.quantity--;
       }
     },
     
     increaseQuantity() {
       this.quantity++;
     },
     
     async addToCartFromModal() {
       if (!this.selectedSpec) {
         uni.showToast({
           title: '请选择规格',
           icon: 'none'
         });
         return;
       }
       
       
       
       try {
         const cartData = {
           goodsId: this.goodsId,
           specificationId: this.selectedSpec.id,
           number: this.quantity
         };
         
         await addToCart(cartData);
         
         // 加入成功后刷新购物车数量
         await this.loadCartCount();
         
         uni.showToast({
           title: `已加入购物车 数量:${this.quantity}`,
           icon: 'success'
         });
         
         this.closeSpecModal();
       } catch (error) {
         console.error('加入购物车失败:', error);
         uni.showToast({
           title: '加入购物车失败',
           icon: 'none'
         });
       }
     },
     
     buyNowFromModal() {
       if (!this.selectedSpec) {
         uni.showToast({
           title: '请选择规格',
           icon: 'none'
         });
         return;
       }
       
       // 检查是否选择了地址
       
       
       // 构建订单商品数据
       const orderItem = {
         id: this.goodsId,
         goodsId: this.goodsId,
         goodsName: this.goodsInfo.name,
         picUrl: this.goodsInfo.img,
         amount: this.selectedSpec.amount,
         number: this.quantity,
         specifications: this.selectedSpec.value,
         specificationId: this.selectedSpec.id
       };
       
       // 跳转到创建订单页面
       const items = encodeURIComponent(JSON.stringify([orderItem]));
       const totalPrice = (this.selectedSpec.amount * this.quantity).toFixed(2);
       
       uni.navigateTo({
         url: `/pages/order/create?items=${items}&totalPrice=${totalPrice}&fromCart=false`+'&freight='+this.goodsInfo.freight
       });
       
       this.closeSpecModal();
     },

    addWordBreakToAll(htmlString) {
      return htmlString.replace(/<([a-zA-Z0-9]+)([^>]*)>/g, (match, tagName, attrs) => {
        const styleAttr = attrs.match(/style="([^"]*)"/);
        if (styleAttr) {
          const existingStyle = styleAttr[1];
          return `<${tagName}${attrs.replace(`style="${existingStyle}"`, `style="word-break: break-all; ${existingStyle};"`)}>`;
        } else {
          return `<${tagName}${attrs} style="word-break: break-all;">`;
        }
        });
    },

    /**
     * 处理富文本内容
     */
    processRichText(htmlString) {
     return   htmlString.replace(/<img([^>]*)>/g, (match, attrs) => {
    const styleAttr = attrs.match(/style="([^"]*)"/);
    if (styleAttr) {
      const existingStyle = styleAttr[1];
      return `<img${attrs.replace(`style="${existingStyle}"`, `style="max-width: 100%; ${existingStyle};"`)}>`;
    } else {
      return `<img${attrs} style="max-width: 100%;">`;
    }
  });
    }
  }
}
</script>

<style scoped>
.goods-detail-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx; /* 为底部操作栏留空间 */
}

.goods-gallery {
  width: 100%;
  height: 750rpx;
  background: #ffffff;
}

.gallery-swiper {
  width: 100%;
  height: 100%;
}

.gallery-image {
  width: 100%;
  height: 100%;
}

 .goods-info-section {
   padding: 20rpx 24rpx 0 24rpx;
 }
 
.goods-header{
  background-color: #fff;
  padding: 24rpx 20rpx;
  margin-bottom: 24rpx;

}

 .price-section {
  margin-bottom: 24rpx;

 }
 
 .main-price {
   font-size: 64rpx;
   font-weight: bold;
   color: #333333;
 }
 
 .goods-title {
   font-size: 32rpx;
   color: #333333;
   line-height: 1.4;

 }
 
 .goods-specs {
   display: flex;
   flex-direction: column;
   gap: 24rpx;
   background: #ffffff;
 }
 
 .spec-item {
   display: flex;
   justify-content: space-between;
   align-items: center;
   padding: 24rpx 20rpx;
 }
 
 .spec-item:last-child {
   border-bottom: none;
 }
 
 .spec-label {
   font-size: 28rpx;
   color: #333333;
 }
 
 .spec-value {
   font-size: 28rpx;
   color: #666666;
 }

 .spec-value.placeholder {
   color: #cccccc;
 }

 .detail-header {
   padding: 16rpx 24rpx;
   margin-top: 20rpx;
 }
 
 .detail-title {
   font-size: 32rpx;
   font-weight: bold;
   color: #333333;
   padding-bottom: 16rpx;
   display: inline-block;
 }
 
 .detail-section {
   /* 商品详情容器 */
 }
 
 .detail-content {
  padding:0 24rpx 32rpx 24rpx;
  line-height: 1.6;
 }

 .bottom-actions {
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
 
 .nav-icons {
   display: flex;
   gap: 32rpx;
 }
 
 .nav-icon {
   display: flex;
   flex-direction: column;
   align-items: center;
   gap: 8rpx;
 }

 .icon-container {
   position: relative;
   display: flex;
   align-items: center;
   justify-content: center;
 }
 
 .icon-home, .icon-cart {
   font-size: 32rpx;
   width: 60rpx;
   height: 60rpx;
 }

 .cart-badge {
   position: absolute;
   top: -8rpx;
   right: -8rpx;
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
 
 .icon-text {
   font-size: 20rpx;
   color: #666666;
 }
 
 .action-buttons {
   display: flex;
   gap: 24rpx;
   flex: 1;
 }
 
 .btn {
   flex: 1;
   height: 88rpx;
   border-radius: 44rpx;
   font-size: 32rpx;
   font-weight: bold;
   border: none;
 }
 
 .btn-cart {
   background: #333333;
   color: #ffffff;
 }
 
 .btn-buy {
   background: #4285f4;
   color: #ffffff;
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

/* 规格选择弹窗样式 */
.spec-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}

.spec-modal {
  width: 100%;
  background: #ffffff;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 32rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.goods-info {
  display: flex;
  gap: 24rpx;
  flex: 1;
}

.goods-thumb {
  width: 300rpx;
  height: 300rpx;
  border-radius: 12rpx;
}

.goods-brief {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  justify-content: space-around;
}

.goods-price {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6b35;
}

.goods-stock {
  font-size: 24rpx;
  color: #999999;
}

.selected-spec {
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #666666;
}

.quantity-selection-inline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16rpx;
}

.quantity-title-inline {
  font-size: 24rpx;
  color: #666666;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #999999;
}

.spec-selection {
  padding: 32rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.spec-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 24rpx;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  flex-direction: column;
    align-items: baseline;
}

.spec-option {
  padding: 16rpx 32rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #666666;
  background: #ffffff;
}

.spec-option.active {
  border-color: #4285f4;
  color: #fff;
  background: #4285f4;
}



.quantity-control {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.quantity-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #e0e0e0;
  border-radius: 6rpx;
  font-size: 24rpx;
  color: #333333;
  background: #ffffff;
}

.quantity-btn.disabled {
  color: #cccccc;
  border-color: #f0f0f0;
}

.quantity-input {
  width: 80rpx;
  height: 48rpx;
  text-align: center;
  border: 2rpx solid #e0e0e0;
  border-radius: 6rpx;
  font-size: 24rpx;
}

.modal-footer {
  padding: 24rpx;
  display: flex;
  gap: 24rpx;
}

.modal-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
}

.modal-btn.btn-cart {
  background: #333333;
  color: #ffffff;
}

.modal-btn.btn-buy {
  background: #4285f4;
  color: #ffffff;
}
.goods-name{
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  text-align: center;
  border-bottom: 1rpx solid #E5E5E5;
  padding: 24rpx 0;
}
.spec-selection.goods_no{
  display: flex;
  flex-direction: column;
    align-items: baseline;
}
.arrow{
  width: 10rpx;
  height: 20rpx;
  padding-left: 10rpx;
}
</style>
