<template>
  <view class="container">
    <!-- 协议内容 -->
    <view class="agreement-content">
      <!-- 动态协议内容 -->
      <template>
        <view class="dynamic-content">
          <rich-text 
            class="rich-content" 
            :nodes="agreementContent.content"
            @tap="handleRichTextTap"
          ></rich-text>
        </view>
      </template>

      <!-- 静态内容作为备用 -->
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      agreementContent: null // 存储协议内容
    }
  },

  onLoad() {
    // 页面加载时获取协议内容
    this.loadAgreementContent()
  },

  methods: {
    /**
     * 加载协议内容
     */
    loadAgreementContent() {
      try {
        const agreementList = uni.getStorageSync('agreementList')
        console.log('获取到的协议列表:', agreementList)
        
        if (agreementList && Array.isArray(agreementList)) {
          // 查找type为'1'的协议
          const targetAgreement = agreementList.find(item => item.type === '1')
          if (targetAgreement) {
            this.agreementContent = targetAgreement
            console.log('找到type=1的协议内容:', targetAgreement)
          } else {
            console.log('未找到type=1的协议内容')
            this.agreementContent = null
          }
        } else {
          console.log('本地缓存中没有协议列表数据')
          this.agreementContent = null
        }
      } catch (error) {
        console.error('加载协议内容失败:', error)
        this.agreementContent = null
      }
    },

    /**
     * 处理富文本点击事件
     */
    handleRichTextTap(e) {
      console.log('富文本点击事件:', e)
      // 这里可以处理富文本中的链接点击等事件
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 协议内容 */
.agreement-content {
  background: #fff;
  margin: 0 20rpx 20rpx;
  border-radius: 20rpx;
  padding: 40rpx;
}

.agreement-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 40rpx;
}

/* 动态内容容器 */
.dynamic-content {
  width: 100%;
}

/* 富文本内容 */
.rich-content {
  width: 100%;
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  text-align: justify;
}

.agreement-section {
  margin-bottom: 40rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.section-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  display: block;
}
</style>