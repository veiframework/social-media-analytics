<template>
  <view v-if="visible" class="modal-overlay" @tap="handleOverlayTap" @touchmove.prevent>
    <view class="modal-container" @tap.stop @touchmove.stop>
      <!-- 弹窗头部 -->
      <view class="modal-header">
        <text class="modal-title">选择报名信息</text>
        <view class="close-btn" @tap="handleClose">
          <text class="close-icon">×</text>
        </view>
      </view>

      <!-- 弹窗内容 -->
      <scroll-view class="modal-content" scroll-y @touchmove.stop>
        <view class="content-wrapper">
          <!-- 加载状态 -->
          <view v-if="loading" class="loading-container">
            <view class="loading-spinner"></view>
            <text class="loading-text">加载中...</text>
          </view>

          <!-- 空状态 -->
          <view v-else-if="registrationList.length === 0" class="empty-container">
            <image class="empty-icon" src="/static/registration/registration_no_data.png" mode="aspectFit"></image>
            <text class="empty-text">暂无报名信息</text>
            <text class="empty-desc">您可以先添加报名信息，方便下次快速填写</text>
          </view>

          <!-- 报名信息列表 -->
          <view v-else class="info-list">
            <view 
              v-for="(item, index) in registrationList" 
              :key="item.id"
              class="info-item"
              :class="{ 'default-item': item.isDefault === '1' }"
              @tap="selectInfo(item)"
            >
              <!-- 默认标签 -->
              <view v-if="item.isDefault === '1'" class="default-badge">
                <text class="default-text">默认</text>
              </view>

              <!-- 选手信息 -->
              <view class="info-content">
                <view class="info-row main-info">
                  <text class="child-name">{{ item.childName }}</text>
                  <text class="child-gender">{{ getGenderText(item.childGender) }}</text>
                </view>
                <view class="info-row">
                  <text class="info-label">身份证：</text>
                  <text class="info-value">{{ formatIdCard(item.childIdCard) }}</text>
                </view>
                <view class="info-row">
                  <text class="info-label">监护人：</text>
                  <text class="info-value">{{ item.guardianName }} {{ item.guardianPhone }}</text>
                </view>
              </view>

              <!-- 选择指示器 -->
              <view class="select-indicator">
                <text class="select-text">选择</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>

      <!-- 弹窗底部 -->
      <view class="modal-footer">
        <button class="add-new-btn" @tap="handleAddNew">
          <text class="add-new-text">+ 新增报名信息</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { getRegistrationInfoList } from '@/api/user.js'

export default {
  name: 'RegistrationInfoModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  
  data() {
    return {
      loading: false,
      registrationList: []
    }
  },

  watch: {
    visible(newVal) {
      if (newVal) {
        this.loadRegistrationList()
      }
    }
  },

  methods: {
    /**
     * 加载报名信息列表
     */
    async loadRegistrationList() {
      try {
        this.loading = true
        const response = await getRegistrationInfoList()
        this.registrationList = response || []
        console.log('加载报名信息列表:', this.registrationList)
      } catch (error) {
        console.error('获取报名信息失败:', error)
        uni.showToast({
          title: '获取报名信息失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },

    /**
     * 选择报名信息
     */
    selectInfo(item) {
      console.log('选择报名信息:', item)
      this.$emit('select', item)
      this.handleClose()
    },

    /**
     * 新增报名信息
     */
    handleAddNew() {
      this.$emit('addNew')
      this.handleClose()
    },

    /**
     * 关闭弹窗
     */
    handleClose() {
      this.$emit('close')
    },

    /**
     * 处理遮罩层点击
     */
    handleOverlayTap() {
      this.handleClose()
    },

    /**
     * 获取性别文本
     */
    getGenderText(gender) {
      return gender === '1' ? '男' : gender === '2' ? '女' : '未知'
    },

    /**
     * 格式化身份证号
     */
    formatIdCard(idCard) {
      if (!idCard) return ''
      if (idCard.length <= 6) return idCard
      return idCard.substring(0, 6) + '****' + idCard.substring(idCard.length - 4)
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
  z-index: 1000;
  padding: 40rpx;
  /* 防止滚动穿透 */
  overflow: hidden;
  touch-action: none;
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
  /* 恢复触摸操作 */
  touch-action: auto;
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
}

.content-wrapper {
  padding: 20rpx;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #e5e5e5;
  border-top: 4rpx solid #83BB0B;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 26rpx;
  color: #666666;
}

/* 空状态 */
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 20rpx;
}

.empty-icon {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 30rpx;
  opacity: 0.6;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: #cccccc;
  text-align: center;
  line-height: 1.4;
}

/* 报名信息列表 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.info-item {
  background: #ffffff;
  border: 2rpx solid #f0f0f0;
  border-radius: 12rpx;
  padding: 24rpx;
  position: relative;
  transition: all 0.3s;
}

.info-item:active {
  transform: scale(0.98);
  background: #f8f9fa;
}

.info-item.default-item {
  border-color: #83BB0B;
  background: #f6ffed;
}

/* 默认标签 */
.default-badge {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  background: #83BB0B;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
}

.default-text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 500;
}

/* 信息内容 */
.info-content {
  padding-right: 80rpx;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
  font-size: 26rpx;
}

.info-row:last-child {
  margin-bottom: 0;
}

.main-info {
  margin-bottom: 16rpx;
}

.child-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-right: 16rpx;
}

.child-gender {
  font-size: 24rpx;
  color: #666666;
  background: #f0f0f0;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
}

.info-label {
  color: #666666;
  min-width: 120rpx;
  flex-shrink: 0;
}

.info-value {
  color: #333333;
  flex: 1;
}

/* 选择指示器 */
.select-indicator {
  position: absolute;
  right: 24rpx;
  top: 50%;
  transform: translateY(-50%);
  background: #83BB0B;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
}

.select-text {
  font-size: 22rpx;
  color: #ffffff;
  font-weight: 500;
}

/* 弹窗底部 */
.modal-footer {
  padding: 30rpx;
  border-top: 1rpx solid #f0f0f0;
}

.add-new-btn {
  width: 100%;
  height: 88rpx;
  border: 2rpx dashed #83BB0B;
  border-radius: 12rpx;
  background: #f6ffed;
  color: #83BB0B;
  font-size: 28rpx;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-new-btn:active {
  background: #f0f9ff;
  transform: scale(0.98);
}

.add-new-text {
  font-size: 28rpx;
  color: #83BB0B;
  font-weight: 500;
}
</style>