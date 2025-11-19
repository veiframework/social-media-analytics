<template>
  <view class="container">
    <!-- 协议内容 -->
    <view class="agreement-content">
      <!-- 动态协议内容 -->
      <template v-if="agreementContent">
        <view class="dynamic-content">
          <rich-text 
            class="rich-content" 
            :nodes="agreementContent.content"
            @tap="handleRichTextTap"
          ></rich-text>
        </view>
      </template>

      <!-- 静态内容作为备用 -->
      <template v-else>
        <view class="agreement-title">会员服务协议</view>
        
        <view class="agreement-section">
          <text class="section-title">1. 会员服务条款</text>
          <text class="section-content">
            欢迎成为我们的会员用户。在购买和使用会员服务前，请仔细阅读并同意以下条款。
          </text>
        </view>

        <view class="agreement-section">
          <text class="section-title">2. 会员权益</text>
          <text class="section-content">
            会员用户可享受专属优惠价格、优先客服支持、专属功能等特权服务。具体权益以实际提供的服务为准。
          </text>
        </view>

        <view class="agreement-section">
          <text class="section-title">3. 会员费用</text>
          <text class="section-content">
            会员费用按照页面显示的价格收取。会员服务一经开通，费用不予退还，除非法律法规另有规定。
          </text>
        </view>

        <view class="agreement-section">
          <text class="section-title">4. 服务期限</text>
          <text class="section-content">
            会员服务期限以购买时选择的套餐为准。服务到期后，会员权益自动失效，如需继续享受请重新购买。
          </text>
        </view>

        <view class="agreement-section">
          <text class="section-title">5. 用户义务</text>
          <text class="section-content">
            用户应合法使用会员服务，不得利用会员权益进行违法违规行为，不得恶意刷单或滥用优惠。
          </text>
        </view>

        <view class="agreement-section">
          <text class="section-title">6. 服务变更</text>
          <text class="section-content">
            我们保留调整会员服务内容、价格的权利。如有重大变更，将提前通知用户。
          </text>
        </view>

        <view class="agreement-section">
          <text class="section-title">7. 争议解决</text>
          <text class="section-content">
            因会员服务产生的争议，双方应友好协商解决。协商不成的，可向有管辖权的人民法院提起诉讼。
          </text>
        </view>

        <view class="agreement-section">
          <text class="section-title">8. 其他条款</text>
          <text class="section-content">
            本协议的解释权归我们所有。如有未尽事宜，按照相关法律法规执行。
          </text>
        </view>
      </template>
    </view>
  </view>
</template>

<script>
export default {
  name: 'MemberAgreement',
  data() {
    return {
      agreementContent: null
    }
  },
  onLoad() {
    this.loadAgreementContent();
  },
  methods: {
    // 从本地存储加载协议内容
    loadAgreementContent() {
      try {
        // 从本地存储获取协议列表
        const agreementList = uni.getStorageSync('agreementList');
        if (agreementList) {

          // 查找type=3的会员服务协议
          const memberAgreement = agreementList.find(item => item.type === 3 || item.type === '3');
          
          if (memberAgreement) {
            this.agreementContent = {
              title: memberAgreement.title || memberAgreement.name || '会员服务协议',
              content: memberAgreement.content || memberAgreement.detail
            };
            console.log('找到会员服务协议:', this.agreementContent);
          } else {
            console.log('未找到type=3的会员服务协议，使用默认内容');
          }
        } else {
          console.log('本地存储中未找到agreementList，使用默认内容');
        }
      } catch (error) {
        console.error('加载协议内容失败:', error);
        // 使用默认静态内容
      }
    },

    // 处理富文本点击事件
    handleRichTextTap(e) {
      console.log('富文本点击事件:', e);
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #ffffff;
}

.agreement-content {
  padding: 40rpx 30rpx;
  line-height: 1.6;
}

.agreement-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333333;
  text-align: center;
  margin-bottom: 60rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.dynamic-content {
  color: #666666;
}

.rich-content {
  font-size: 28rpx;
  line-height: 1.8;
  color: #666666;
}

.agreement-section {
  margin-bottom: 50rpx;
}

.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
}

.section-content {
  display: block;
  font-size: 28rpx;
  color: #666666;
  line-height: 1.8;
  text-indent: 2em;
}

/* 富文本内容样式 */
:deep(.rich-content) h1,
:deep(.rich-content) h2,
:deep(.rich-content) h3 {
  font-weight: bold;
  color: #333333;
  margin: 30rpx 0 20rpx 0;
}

:deep(.rich-content) h1 {
  font-size: 36rpx;
}

:deep(.rich-content) h2 {
  font-size: 32rpx;
}

:deep(.rich-content) h3 {
  font-size: 30rpx;
}

:deep(.rich-content) p {
  margin-bottom: 20rpx;
  text-indent: 2em;
}

:deep(.rich-content) ul,
:deep(.rich-content) ol {
  margin: 20rpx 0;
  padding-left: 40rpx;
}

:deep(.rich-content) li {
  margin-bottom: 10rpx;
}

/* 响应式适配 */
@media screen and (max-width: 750rpx) {
  .agreement-content {
    padding: 30rpx 20rpx;
  }
  
  .agreement-title {
    font-size: 36rpx;
    margin-bottom: 40rpx;
  }
  
  .section-title {
    font-size: 30rpx;
  }
  
  .section-content {
    font-size: 26rpx;
  }
}
</style>
