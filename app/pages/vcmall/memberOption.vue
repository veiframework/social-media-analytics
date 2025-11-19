<template>
  <view class="member-option-container">
    <!-- 会员卡选项列表 -->
    <view class="member-cards">
      <view 
        class="member-card" 
        v-for="(option, index) in memberOptions" 
        :key="option.id"
        :class="{ active: selectedOption === index }"
        @tap="selectOption(index)"
      >
        <!-- 卡片头部 -->
        <view class="card-header">
          <text class="card-title">{{ option.typeName }}</text>
          <view class="card-radio">
            <view v-if="selectedOption === index" class="radio-checked"></view>
          </view>
        </view>
        
                 <!-- 价格信息 -->
         <view class="price-section">
           <view class="current-price">
             <text class="price-symbol">¥</text>
             <text class="price-value">{{ option.price }}</text>
           </view>
           <text class="daily-price">{{ calculateDailyPrice(option) }}元/天</text>
         </view>
         
         <!-- 折扣信息 -->
         <view class="discount-badge">
           <text class="discount-text">¥{{ option.originalPrice }}限时{{ calculateDiscount(option) }}折</text>
         </view>
      </view>
    </view>

    <!-- 开通按钮 -->
    <view class="purchase-section">
      <view class="purchase-btn" @tap="purchaseMembership">
        <text class="purchase-text">{{ purchaseButtonText }}</text>
      </view>
    </view>

         <!-- 用户协议 -->
     <view class="agreement-section">
       <view class="agreement-checkbox" @tap="toggleAgreement">
         <view class="checkbox" :class="{ checked: agreementChecked }">
           <view v-if="agreementChecked" class="checkbox-icon">✓</view>
         </view>
         <text class="agreement-text">请仔细阅读并同意</text>
         <text class="agreement-link" @tap.stop="viewAgreement">《会员服务协议》</text>
       </view>
     </view>
  </view>
</template>

<script>
import { getMemberOptions, purchaseMember } from '../../api/member.js';
import {payOrder} from "../../api/order";
import { getUserInfo } from '../../api/user.js';
import { updateInviteLoginId } from '../../api/promotion.js';
export default {
  name: 'MemberOption',
     data() {
     return {
       selectedOption: 0,
       memberOptions: [
        
       ],
       loading: false,
       agreementChecked: false,
       userInfo: {}
     }
   },
  computed: {
    purchaseButtonText() {
      const selected = this.memberOptions[this.selectedOption];
      return selected ? `${selected.price}元立即开通${selected.typeName}` : '立即开通';
    }
  },
  onLoad(options) {
    if(options.inviteLoginId){
        updateInviteLoginId(options.inviteLoginId);
    }
    this.loadMemberOptions();
  },
  onShow(){
    this.loadUserInfo()
  },
  methods: {
         // 计算每日价格
     calculateDailyPrice(option) {
       if (!option.price || !option.days) {
         return '0.00';
       }
       const dailyPrice = parseFloat(option.price) / parseInt(option.days);
       return dailyPrice.toFixed(2);
     },

     // 计算折扣
     calculateDiscount(option) {
       if (!option.price || !option.originalPrice) {
         return '10';
       }
       const discount = (parseFloat(option.price) / parseFloat(option.originalPrice)) * 10;
       return discount.toFixed(1);
     },

     // 加载会员选项数据
     async loadMemberOptions() {
       try {
         this.loading = true;
         const data = await getMemberOptions({status:1});
         if (data && data.length > 0) {
           this.memberOptions = data;
         }
         console.log('会员选项加载成功:', data);
       } catch (error) {
         console.error('加载会员选项失败:', error);
         // 保持默认数据
       } finally {
         this.loading = false;
       }
     },
    async loadUserInfo(){
      this.userInfo = await getUserInfo()
    },
    // 选择会员选项
    selectOption(index) {
      this.selectedOption = index;
    },

         // 切换协议勾选状态
     toggleAgreement() {
       this.agreementChecked = !this.agreementChecked;
     },

     // 购买会员
     async purchaseMembership() {
       const selectedMember = this.memberOptions[this.selectedOption];
       if (!selectedMember) {
         uni.showToast({
           title: '请选择会员类型',
           icon: 'none'
         });
         return;
       }

       // 验证是否勾选协议
       if (!this.agreementChecked) {
         uni.showToast({
           title: '请先同意会员服务协议',
           icon: 'none'
         });
         return;
       }

       let params = {
         totalFee: selectedMember.price,
         tradeType: "JSAPI",
         body: "充值",
         openid: this.userInfo.openid,
         businessTypeCode: 'mallMember',
         extendParam:{
           tenantId: '0',
           memberOptionId: selectedMember.id
         }
       }

       let paymentParams = await purchaseMember(params);

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
           uni.switchTab({
             url: '/pages/vcmall/mine'
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
    },

         // 查看会员服务协议
     viewAgreement() {
       uni.navigateTo({
         url: '/pages/vcmall/memberAgreement'
       });
     }
  }
}
</script>

<style scoped>
.member-option-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40rpx 30rpx;
}

/* 会员卡列表 */
.member-cards {
  margin-bottom: 60rpx;
}

.member-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 40rpx 30rpx;
  margin-bottom: 30rpx;
  border: 3rpx solid transparent;
  position: relative;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.member-card.active {
  border-color: #ff6b35;
  box-shadow: 0 4rpx 20rpx rgba(255, 107, 53, 0.2);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.card-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.card-radio {
  width: 40rpx;
  height: 40rpx;
  border: 3rpx solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.member-card.active .card-radio {
  border-color: #ff6b35;
}

.radio-checked {
  width: 20rpx;
  height: 20rpx;
  background: #ff6b35;
  border-radius: 50%;
}

/* 价格区域 */
.price-section {
  display: flex;
  align-items: baseline;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.current-price {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 28rpx;
  color: #ff6b35;
  font-weight: bold;
}

.price-value {
  font-size: 60rpx;
  color: #ff6b35;
  font-weight: bold;
}

.daily-price {
  font-size: 24rpx;
  color: #666;
}

/* 折扣标签 */
.discount-badge {
  background: #ff6b35;
  border-radius: 8rpx;
  padding: 8rpx 20rpx;
  display: inline-block;
}

.discount-text {
  font-size: 22rpx;
  color: #ffffff;
  font-weight: bold;
}

/* 购买按钮 */
.purchase-section {
  margin-bottom: 40rpx;
}

.purchase-btn {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8f5a 100%);
  border-radius: 50rpx;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(255, 107, 53, 0.3);
}

.purchase-btn:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

.purchase-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: bold;
}

/* 用户协议 */
.agreement-section {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.agreement-checkbox {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #ddd;
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.checkbox.checked {
  border-color: #ff6b35;
  background: #ff6b35;
}

.checkbox-icon {
  color: #fff;
  font-size: 20rpx;
  font-weight: bold;
}

.agreement-text {
  font-size: 24rpx;
  color: #999;
}

.agreement-link {
  font-size: 24rpx;
  color: #ff6b35;
  text-decoration: underline;
  margin-left: 8rpx;
}

/* 响应式适配 */
@media screen and (max-width: 750rpx) {
  .member-option-container {
    padding: 30rpx 20rpx;
  }
  
  .member-card {
    padding: 30rpx 20rpx;
  }
  
  .price-value {
    font-size: 50rpx;
  }
}
</style>
