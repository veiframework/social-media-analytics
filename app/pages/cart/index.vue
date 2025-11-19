<template>
  <view class="cart-container">
    <!-- 购物车列表 -->
    <view v-if="cartList.length > 0" class="cart-content">
      <!-- 购物车商品列表 -->
      <view class="cart-list">
        <view 
          v-for="(item, index) in cartList" 
          :key="item.id"
          class="cart-item"
        >
          <!-- 选择框 -->
          <view class="item-select" @tap="toggleSelect(index)">
            <view class="checkbox" :class="{ checked: item.selected }">
              <text v-if="item.selected" class="check-icon">✓</text>
            </view>
          </view>
          
          <!-- 商品信息 -->
          <view class="item-info">
            <image 
              :src="item.picUrl" 
              class="item-image"
              mode="aspectFill"
            />
            <view class="item-info-right">
              <view class="item-details">
              <view class="item-name">{{ item.goodsName || item.name }}</view>
              <view class="item-spec">
                规格: {{ item.specifications }}
              </view>
              <view class="item-price">¥{{ item.amount || item.price }}</view>
            </view>
            <view class="quantity-control">
              <view class="quantity-btn increase" @tap="increaseQuantity(index)">+</view>

              
              <input 
                class="quantity-input" 
                type="number" 
                v-model="item.number"
                @blur="updateQuantity(index)"
              />
              <view 
                class="quantity-btn decrease" 
                @tap="decreaseQuantity(index)"
              >-</view>
           </view>
            </view>
            
          </view>
          
          <!-- 数量控制 -->
         
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view v-else class="empty-cart">
      <text class="empty-text">购物车空空如也</text>
    </view>
    
    <!-- 底部结算栏 -->
    <view v-if="cartList.length > 0" class="bottom-bar">
      <view class="select-all" @tap="toggleSelectAll">
        <view class="checkbox" :class="{ checked: isAllSelected }">
          <text v-if="isAllSelected" class="check-icon">✓</text>
        </view>
        <text class="select-text">全选</text>
      </view>
      
      <view class="total-info">
        <text class="total-text">合计: </text>
        <text class="total-price">¥{{ totalPrice  }}</text>
      </view>
      
      <button 
        class="checkout-btn" 
        :class="{ disabled: selectedItems.length === 0 }"
        @tap="checkout"
      >
        去支付({{ selectedItems.length }})
      </button>
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
import { getCartList, updateCartItem, deleteCartItem,updateCartNum ,batchSelect} from '../../api/cart.js';

export default {
  data() {
    return {
      cartList: [],
      loading: false,
      totalPrice: 0,
      group: null
    }
  },
  computed: {
    // 选中的商品
    selectedItems() {
      return this.cartList.filter(item => item.selected);
    },
    
    // 是否全选
    isAllSelected() {
      return this.cartList.length > 0 && this.cartList.every(item => item.selected);
    },
    
    // 总价
    
  },
  onLoad() {
    let group = uni.getStorageSync('group');
    if(group){
      this.group = group;
    }
    this.loadCartList();
  },
  onShow() {
    // 从其他页面返回时刷新购物车
    this.loadCartList();
  },
  onPullDownRefresh() {
    this.loadCartList().finally(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    async loadCartList() {
      try {
        this.loading = true;

        const data = await getCartList({group: this.group});
        this.cartList = (data.carList || []).map(item => ({
          ...item,
          selected: item.checked // 默认不选中
        }));
        this.totalPrice = data.totalPrice;
        this.freight = data.freight;
        console.log('购物车列表:', this.cartList);
      } catch (error) {
        console.error('加载购物车失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },
    
    // 切换单个商品选择状态
    toggleSelect(index) {
      this.cartList[index].selected = !this.cartList[index].selected;
      this.isAllSelected = this.cartList.every(item => item.checked == 1)
      this.updateCartItemApi(this.cartList[index]);
    },
    
    // 切换全选状态
    toggleSelectAll() {
      const allSelected = this.isAllSelected;
      this.cartList.forEach(item => {
        item.selected = !allSelected;
      });
      this.onSelectAll()
    },
    
    // 减少数量
    decreaseQuantity(index) {
      const item = this.cartList[index];
      item.selected = true;
      
        item.number--;
        this.updateQuantity(index);

      
    },
    
    // 增加数量
    increaseQuantity(index) {
      const item = this.cartList[index];
      item.selected = true;
      item.number++;
       this.updateQuantity(index);
    },
    
    // 更新数量
    updateQuantity(index) {
      const item = this.cartList[index];
      console.log(item);
      item.selected = true;

      if (item.number < 1) {
        this.deleteCartItemApi(index);
      }else{
        this.updateCartItemApi(item);
      }
    },
    
    // 调用API更新购物车商品
    async updateCartItemApi(item) {
      try {
     let res=   await updateCartNum(item.id, {
          number: item.number,
          checked: item.selected?1:0,
          group: this.group
        });
        this.totalPrice = res.totalPrice
        this.freight = res.freight
      } catch (error) {
        console.error('更新购物车失败:', error);
        uni.showToast({
          title: '更新失败',
          icon: 'none'
        });
      }
    },
    
    // 删除购物车商品
    async deleteCartItemConfirm(index) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这件商品吗？',
        success: async (res) => {
          if (res.confirm) {
            await this.deleteCartItemApi(index);
          }
        }
      });
    },
    
    async deleteCartItemApi(index) {
      try {
        const item = this.cartList[index];
        await deleteCartItem(item.id);
        this.cartList.splice(index, 1);
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        });
        this.loadCartList();
      } catch (error) {
        console.error('删除购物车商品失败:', error);
        uni.showToast({
          title: '删除失败',
          icon: 'none'
        });
      }
    },
    async onSelectAll ()  {
      let data = {
        batchType: this.isAllSelected?1:0,
          group: this.group
      }
      let res=await batchSelect(data)
      this.totalPrice = res.totalPrice
      
    },
    // 去购物
    goShopping() {
      uni.switchTab({
        url: '/pages/mall/index'
      });
    },
    
    // 结算
    checkout() {
      if (this.selectedItems.length === 0) {
        uni.showToast({
          title: '请选择要结算的商品',
          icon: 'none'
        });
        return;
      }
      
      // 跳转到创建订单页面，传递选中的商品数据
      const orderItems = this.selectedItems.map(item => ({
        id: item.id,
        goodsId: item.goodsId,
        goodsName: item.goodsName,
        picUrl: item.picUrl,
        specifications: item.specifications,
        specificationId: item.specificationId,
        amount: item.price,
        number: item.number
      }));
      
      const itemsParam = encodeURIComponent(JSON.stringify(orderItems));
      uni.navigateTo({
        url: `/pages/order/create?items=${itemsParam}`+'&totalPrice='+this.totalPrice+'&fromCart=true'+'&freight='+this.freight
      });
    }
  }
}
</script>

<style scoped>
.cart-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.cart-content {
  padding: 0 24rpx;
}

.cart-list {
  padding-top: 24rpx;
}

.cart-item {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.item-select {
  flex-shrink: 0;
}

.checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
}

.checkbox.checked {
  background: #4285f4;
  border-color: #4285f4;
}

.check-icon {
  color: #ffffff;
  font-size: 24rpx;
  font-weight: bold;
}

.item-info {
  flex: 1;
  display: flex;
  gap: 16rpx;
}

.item-image {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.item-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.item-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  line-height: 1.4;
}

.item-spec {
  font-size: 24rpx;
  color: #666666;
}

.item-price {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
  margin-top: auto;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 16rpx;
  flex-shrink: 0;
  flex-direction: row-reverse;
}

.quantity-btn {
  width: 48rpx;
  height: 48rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
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
  border-radius: 8rpx;
  font-size: 24rpx;
  background: #ffffff;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 48rpx;
  text-align: center;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 32rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 48rpx;
}

.go-shopping-btn {
  background: #4285f4;
  color: #ffffff;
  border-radius: 44rpx;
  padding: 24rpx 48rpx;
  font-size: 28rpx;
  font-weight: bold;
  border: none;
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

.select-all {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.select-text {
  font-size: 28rpx;
  color: #333333;
}

.total-info {
  flex: 1;
  text-align: right;
}

.total-text {
  font-size: 24rpx;
  color: #666666;
}

.total-price {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
}

.checkout-btn {
  background: #4285f4;
  color: #ffffff;
  border-radius: 44rpx;
  padding: 5rpx 48rpx;
  font-size: 28rpx;
  font-weight: bold;
  border: none;
  min-width: 200rpx;
}

.checkout-btn.disabled {
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
.item-info-right{
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;
}
</style>
