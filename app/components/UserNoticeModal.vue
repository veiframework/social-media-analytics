<template>
  <view v-if="visible" class="modal-overlay"    @tap="handleOverlayTap" @touchmove="handleOverlayTouchMove" >
    <view class="modal-container" @tap.stop>
      <!-- 弹窗头部 -->
      <view class="modal-header">
        <text class="modal-title">用户须知</text>
        <view class="close-btn" @tap="handleClose">
          <text class="close-icon">×</text>
        </view>
      </view>

      <!-- 弹窗内容 -->
      <scroll-view 
        class="modal-content" 
        scroll-y
        @scroll="handleScroll"
        @scrolltolower="handleScrollToLower"
        lower-threshold="50"
        :enable-back-to-top="false"
        :scroll-with-animation="false"
        :enhanced="true"
        :bounces="true"
        :show-scrollbar="true"
        :enable-passive="true"
        :scroll-anchoring="true"
      >
        <view class="content-wrapper">
          <!-- 动态协议内容 -->
          <view v-if="agreementContent" class="notice-section">
            <view class="section-title">{{ agreementContent.title || '用户协议' }}</view>
            <view class="section-content">
              <rich-text 
                class="rich-content" 
                :nodes="agreementContent.content"
                @tap="handleRichTextTap"
              ></rich-text>
            </view>
          </view>

          <!-- 静态内容作为备用 -->
          <template v-else>
            <view class="notice-section">
              <view class="section-title">参赛生命安全须知</view>
              <view class="section-content">
                <text class="content-text">1. 参赛者必须身体健康，无心脏病、高血压等不适宜剧烈运动的疾病。</text>
                <text class="content-text">2. 参赛者应根据自身身体状况，量力而行，如有不适应立即停止比赛。</text>
                <text class="content-text">3. 比赛过程中如发生意外，组委会将提供必要的医疗救助，但不承担相关责任。</text>
                <text class="content-text">4. 参赛者应遵守比赛规则，服从裁判和工作人员的指挥。</text>
                <text class="content-text">5. 参赛者应自行购买人身意外保险，组委会不承担保险责任。</text>
              </view>
            </view>

            <view class="notice-section">
              <view class="section-title">改期及退赛说明</view>
              <view class="section-content">
                <text class="content-text">1. 报名成功后，如需改期或退赛，请提前7天申请。</text>
                <text class="content-text">2. 比赛前7天内申请退赛，将扣除30%手续费。</text>
                <text class="content-text">3. 比赛前3天内申请退赛，将扣除50%手续费。</text>
                <text class="content-text">4. 比赛当天不接受退赛申请，报名费不予退还。</text>
                <text class="content-text">5. 因个人原因无法参赛，可申请转让给他人，需提前3天办理。</text>
                <text class="content-text">6. 因天气等不可抗力因素导致比赛取消，将全额退还报名费。</text>
              </view>
            </view>

            <view class="notice-section">
              <view class="section-title">其他重要事项</view>
              <view class="section-content">
                <text class="content-text">1. 参赛者应携带有效身份证件参加比赛。</text>
                <text class="content-text">2. 比赛期间禁止携带危险物品进入赛场。</text>
                <text class="content-text">3. 参赛者应遵守赛场秩序，不得影响其他选手比赛。</text>
                <text class="content-text">4. 组委会有权对比赛过程进行拍摄和录像，用于宣传推广。</text>
                <text class="content-text">5. 参赛者报名即视为同意以上所有条款。</text>
              </view>
            </view>

            <view class="notice-section">
              <view class="section-title">免责声明</view>
              <view class="section-content">
                <text class="content-text">1. 参赛者参加本次比赛属于自愿行为，应当具备相应的身体素质和技能水平。</text>
                <text class="content-text">2. 参赛者在比赛过程中发生的人身伤害和财产损失，组委会不承担任何责任。</text>
                <text class="content-text">3. 参赛者应自行评估参赛风险，如有疑虑请勿参加比赛。</text>
                <text class="content-text">4. 组委会保留对比赛规则的最终解释权。</text>
              </view>
            </view>

            <view class="notice-section">
              <view class="section-title">最终确认</view>
              <view class="section-content">
                <text class="content-text final-confirm">我已仔细阅读并完全理解以上所有条款，同意遵守相关规定，自愿承担参赛风险。</text>
              </view>
            </view>
          </template>

          <!-- 占位元素，确保可以滚动到底部 -->
          <view class="scroll-bottom-marker" id="scroll-bottom-marker"></view>
        </view>
      </scroll-view>

      <!-- 弹窗底部按钮 -->
      <view class="modal-footer">
        <!-- 调试信息 -->
      
        
        <button 
          class="confirm-btn" 
          :class="{ 'disabled': !canConfirm, 'enabled': canConfirm }"
          :disabled="!canConfirm"
          @tap="handleConfirm"
        >
          {{ confirmButtonText }}
        </button>
        
        
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'UserNoticeModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  
  data() {
    return {
      canConfirm: false,
      scrollTop: 0,
      maxScrollTop: 0,
      confirmButtonText: '请点击阅读参赛声明和改期及退赛说明并上滑看完本须知',
      bodyScrollTop: 0,
      agreementContent: null, // 存储协议内容
      scrollTimer: null, // 滚动节流定时器
      isScrolling: false // 滚动状态标记
    }
  },

  watch: {
    visible(newVal) {
      if (newVal) {
        // 弹窗显示时重置状态
        this.canConfirm = false
        this.scrollTop = 0
        this.maxScrollTop = 0
        
        // 加载协议内容
        this.loadAgreementContent()
        
        // 防止背景滚动
        this.preventBodyScroll()
        
        // 延迟检查内容高度，如果内容不够高则直接允许确认
        this.$nextTick(() => {
          setTimeout(() => {
            this.checkContentHeight()
          }, 300)
        })
      } else {
        // 弹窗关闭时恢复背景滚动
        this.restoreBodyScroll()
      }
    }
  },

  beforeDestroy() {
    // 清理定时器
    if (this.scrollTimer) {
      clearTimeout(this.scrollTimer)
      this.scrollTimer = null
    }
  },

  methods: {
    /**
     * 加载协议内容
     */
    loadAgreementContent() {
      try {
        const agreementList = uni.getStorageSync('agreementList')
        // console.log('获取到的协议列表:', agreementList)
        
        if (agreementList && Array.isArray(agreementList)) {
          // 查找type为0的协议
          const targetAgreement = agreementList.find(item => item.type === '0')
          if (targetAgreement) {
            this.agreementContent = targetAgreement
            // console.log('找到type=0的协议内容:', targetAgreement)
          } else {
            // console.log('未找到type=0的协议内容')
            this.agreementContent = null
          }
        } else {
          // console.log('本地缓存中没有协议列表数据')
          this.agreementContent = null
        }
      } catch (error) {
        console.error('加载协议内容失败:', error)
        this.agreementContent = null
      }
    },

    /**
     * 处理滚动事件 - 使用节流优化性能
     */
    handleScroll(e) {
      // 清除之前的定时器
      if (this.scrollTimer) {
        clearTimeout(this.scrollTimer)
      }
      
      // 标记正在滚动
      this.isScrolling = true
      
      // 节流处理滚动事件，减少频繁更新
      this.scrollTimer = setTimeout(() => {
        const scrollTop = e.detail.scrollTop
        
        // 只在滚动位置真正变化时才更新
        if (Math.abs(this.scrollTop - scrollTop) > 5) {
          this.scrollTop = scrollTop
          
          // 记录最大滚动位置
          if (scrollTop > this.maxScrollTop) {
            this.maxScrollTop = scrollTop
          }
        }
        
        this.isScrolling = false
      }, 16) // 约60fps的更新频率
    },

    /**
     * 滚动到底部事件
     */
    handleScrollToLower() {
      // console.log('滚动到底部了')
      this.canConfirm = true
    },

    /**
     * 检查内容高度
     */
    checkContentHeight() {
      // 使用uni.createSelectorQuery检查内容是否需要滚动
      const query = uni.createSelectorQuery().in(this)
      query.select('.modal-content').boundingClientRect()
      query.select('.content-wrapper').boundingClientRect()
      query.exec((res) => {
        // console.log('内容高度检查:', res)
        if (res && res[0] && res[1]) {
          const scrollViewHeight = res[0].height
          const contentHeight = res[1].height
          
          // console.log('滚动视图高度:', scrollViewHeight, '内容高度:', contentHeight)
          
          // 如果内容高度小于等于滚动视图高度，说明不需要滚动，直接允许确认
          if (contentHeight <= scrollViewHeight + 10) {
            this.canConfirm = true
            // console.log('内容不需要滚动，直接允许确认')
          }
        }
      })
    },

    /**
     * 处理遮罩层点击
     */
    handleOverlayTap() {
      // 可以选择是否允许点击遮罩层关闭
      // this.handleClose()
    },
    moveHandle(e) {},
    /**
     * 处理遮罩层触摸移动 - 只阻止非滚动区域的触摸
     */
    handleOverlayTouchMove(e) {
      // 只阻止遮罩层本身的触摸移动，不影响弹窗内容区域
      e.preventDefault()
      this.handleClose()
    },

    /**
     * 关闭弹窗
     */
    handleClose() {
      this.$emit('close')
    },

    /**
     * 确认按钮点击
     */
    handleConfirm() {
      if (!this.canConfirm) {
        uni.showToast({
          title: '请先阅读完所有内容',
          icon: 'none'
        })
        return
      }
      
      this.$emit('confirm')
    },

    /**
     * 处理富文本点击事件
     */
    handleRichTextTap(e) {
      // console.log('富文本点击事件:', e)
      // 这里可以处理富文本中的链接点击等事件
    },

    /**
     * 测试按钮点击（临时调试用）
     */
    testConfirm() {
      this.canConfirm = true
      // console.log('测试按钮：强制启用确认状态')
    },

    /**
     * 防止背景滚动
     */
    preventBodyScroll() {
      // 在uni-app中，主要通过CSS和事件处理来防止滚动穿透
      // 获取页面滚动信息
      uni.getSystemInfo({
        success: (res) => {
          this.systemInfo = res
        }
      })
      
      // 禁用页面滚动
      uni.pageScrollTo({
        scrollTop: 0,
        duration: 0
      })
    },

    /**
     * 恢复背景滚动
     */
    restoreBodyScroll() {
      // 在uni-app中主要通过移除弹窗来恢复滚动
      // 这里可以添加一些清理逻辑
      // console.log('恢复背景滚动')
    }
  }
}
</script>

<style scoped>
/* 弹窗遮罩 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  /* 防止滚动穿透，但允许内部滚动 */
  overflow: hidden;
}

/* 弹窗容器 */
.modal-container {
  background: #ffffff;
  border-radius: 16rpx;
  width: 100%;
  max-width: 600rpx;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  /* 允许触摸操作 */
  touch-action: manipulation;
}

/* 弹窗头部 */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.close-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  background: #f5f5f5;
}

.close-icon {
  font-size: 40rpx;
  color: #999999;
  line-height: 1;
}

/* 弹窗内容 */
.modal-content {
  flex: 1;
  max-height: 60vh;
  /* 启用硬件加速 */
  transform: translateZ(0);
  -webkit-transform: translateZ(0);
  /* 优化滚动性能 */
  -webkit-overflow-scrolling: touch;
  /* 减少重绘 */
  will-change: scroll-position;
  /* 允许所有方向的滚动和缩放 */
  touch-action: pan-y pinch-zoom;
}

.content-wrapper {
  padding: 30rpx;
  /* 优化渲染性能 */
  contain: layout style paint;
}

/* 须知章节 */
.notice-section {
  margin-bottom: 40rpx;
}

.notice-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
  padding-bottom: 10rpx;
  border-bottom: 2rpx solid #83BB0B;
}

.section-content {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.content-text {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.6;
  text-align: justify;
}

/* 富文本内容 */
.rich-content {
  width: 100%;
  font-size: 26rpx;
  color: #666666;
  line-height: 1.6;
  text-align: justify;
}

/* 最终确认文本 */
.final-confirm {
  font-weight: bold;
  color: #83BB0B;
  text-align: center;
  padding: 20rpx;
  background: #f0f9ff;
  border-radius: 8rpx;
  border: 1rpx solid #e6f7ff;
}

/* 滚动底部标记 */
.scroll-bottom-marker {
  height: 60rpx;
}

/* 弹窗底部 */
.modal-footer {
  padding: 30rpx;
  border-top: 1rpx solid #f0f0f0;
}

.confirm-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  font-weight: bold;
  border: none;
  transition: all 0.3s;
}

.confirm-btn.disabled {
  background: #f5f5f5;
  color: #cccccc;
}

.confirm-btn.enabled {
  background: #83BB0B;
  color: #ffffff;
}

.confirm-btn.enabled:active {
  background: #83BB0B;
  transform: scale(0.98);
}
</style>