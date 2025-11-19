<template>
  <view class="groups-page">
    <!-- 页面头部 -->
 

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 分组列表 -->
    <view v-else class="groups-container">
      <view v-if="groupsList.length === 0" class="empty-container">
        <image class="empty-icon" src="/static/registration/registration_no_data.png" mode="aspectFit"></image>
        <text class="empty-text">暂无可报名的分组</text>
      </view>
      
      <view v-else class="groups-list">
        <view 
          v-for="(group, index) in groupsList" 
          :key="group.id"
          class="group-item"
          @click="handleGroupSelect(group)"
        >
          <!-- 分组卡片 -->
          <view class="group-card">
            <!-- 分组图片 -->
            <view class="group-image-container">
              <image 
                class="group-image" 
                :src="group.coverImage || '/static/competition_detail/location.png'" 
                mode="aspectFill"
              ></image>
            </view>
            
            <!-- 分组信息 -->
            <view class="group-content">
              <!-- 分组标题 -->
              <view class="group-title">{{ group.name }}</view>
              
              <!-- 积分和标签行 -->
              <view class="points-tags-row">
                <!-- 积分信息 -->
                <view class="points-info">
                  <text class="points-text">+{{ group.points  }}积分</text>
                </view>
                
                <!-- 在售标签 -->
                <view class="sale-tag">
                  <text class="sale-text">在售</text>
                </view>
              </view>
              
              <!-- 价格信息 -->
              <view class="price-container">
                <text class="price-symbol">￥</text>
                <text class="price-amount">{{ group.fee   }}</text>
                <text class="price-unit">起</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部提示 -->
    <!-- <view class="bottom-tips" v-if="groupsList.length > 0">
      <view class="tip-item">
        <text class="tip-icon">💡</text>
        <text class="tip-text">请仔细阅读分组要求，选择适合的分组</text>
      </view>
      <view class="tip-item">
        <text class="tip-icon">⚠️</text>
        <text class="tip-text">报名费用需在报名时支付</text>
      </view>
    </view> -->

    <!-- 用户须知弹窗 -->
    <UserNoticeModal 
      :visible="showNoticeModal" 
      @close="handleNoticeClose"
      @confirm="handleNoticeConfirm"
    />
  </view>
</template>

<script>
import { getCompetitionGroups } from '@/api/user.js'
import UserNoticeModal from '@/components/UserNoticeModal.vue'

export default {
  name: 'CompetitionGroups',
  components: {
    UserNoticeModal
  },
  data() {
    return {
      competitionId: '',
      groupsList: [],
      loading: true,
      error: null,
      showNoticeModal: false,
      selectedGroup: null
    }
  },

  onLoad(options) {
    if (options.competitionId) {
      this.competitionId = options.competitionId
      this.loadCompetitionGroups()
    } else {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      })
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadCompetitionGroups(true)
  },

  methods: {
    /**
     * 加载赛事分组列表
     */
    async loadCompetitionGroups(isRefresh = false) {
      if (!this.competitionId) return

      try {
        this.loading = !isRefresh
        this.error = null

        const params = {
          competitionId: this.competitionId
        }

        const response = await getCompetitionGroups(params)
        
        // 适配不同的响应格式
        let result = null
        if (response && response.data) {
          result = response.data
        } else if (response && Array.isArray(response)) {
          result = response
        } else if (response) {
          result = response
        }

        if (Array.isArray(result)) {
          this.groupsList = result
          console.log('赛事分组加载成功:', result)
        } else {
          console.warn('分组数据格式异常:', result)
          this.groupsList = []
        }

        // 如果没有数据，添加测试数据用于展示效果
        if (this.groupsList.length === 0) {
          this.groupsList = [
            {
              id: '1',
              name: '公开组10-14岁公开组开组10-14岁公开组10-14岁公开组',
              description: '10-14岁公开组10-14岁公开组',
              ageRange: '10-14岁',
              registrationFee: '499',
              points: '300',
              status: 'OPEN',
              maxParticipants: 50,
              currentParticipants: 25,
              coverImage: '',
              sessionDate: '2025-07-28,2025-07-29,2025-07-30',
              sessionTime: '上午场 09:00-12:00,下午场 14:00-17:00,晚场 19:00-22:00'
            },
            {
              id: '2', 
              name: '公开组7-19岁',
              description: '适合7-19岁青少年参加',
              ageRange: '7-19岁',
              registrationFee: '499',
              points: '250',
              status: 'OPEN',
              maxParticipants: 30,
              currentParticipants: 30,
              coverImage: '',
              sessionDate: '2025-08-01,2025-08-02',
              sessionTime: '上午场 09:00-12:00,下午场 14:00-17:00'
            }
          ]
        }

      } catch (error) {
        console.error('获取赛事分组失败:', error)
        this.error = error.message || '加载失败'
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
        if (isRefresh) {
          uni.stopPullDownRefresh()
        }
      }
    },

    /**
     * 处理分组选择
     */
    handleGroupSelect(group) {
      if (!this.canSelectGroup(group)) {
        uni.showToast({
          title: this.getUnavailableReason(group),
          icon: 'none'
        })
        return
      }

      console.log('选择分组:', group)
      
      // 保存选中的分组信息
      this.selectedGroup = group
      
      // 显示用户须知弹窗
      this.showNoticeModal = true
    },

    /**
     * 关闭用户须知弹窗
     */
    handleNoticeClose() {
      this.showNoticeModal = false
      this.selectedGroup = null
    },

    /**
     * 确认用户须知，跳转到报名页面
     */
    handleNoticeConfirm() {
      this.showNoticeModal = false
      
      if (this.selectedGroup) {
        console.log('确认用户须知，跳转到报名页面:', this.selectedGroup)
        
        // 跳转到报名页面，传递分组信息
        uni.navigateTo({
          url: `/pages/registration/add?competitionId=${this.competitionId}&groupId=${this.selectedGroup.id}&groupName=${encodeURIComponent(this.selectedGroup.name)}`,
          success: () => {
            this.selectedGroup = null
          },
          fail: () => {
            uni.showToast({
              title: '报名功能开发中',
              icon: 'none'
            })
            this.selectedGroup = null
          }
        })
      }
    },

    /**
     * 判断是否可以选择分组
     */
    canSelectGroup(group) {
      if (!group) return false
      
      // 检查分组状态
      if (group.status === 'CLOSED' || group.status === 'FULL') {
        return false
      }
      
      // 检查报名人数限制
      if (group.maxParticipants && group.currentParticipants >= group.maxParticipants) {
        return false
      }
      
      return true
    },

    /**
     * 获取分组状态样式类
     */
    getGroupStatusClass(group) {
      if (!this.canSelectGroup(group)) {
        return 'status-unavailable'
      }
      return 'status-available'
    },

    /**
     * 获取分组状态文本
     */
    getGroupStatusText(group) {
      if (group.status === 'CLOSED') {
        return '已关闭'
      }
      if (group.status === 'FULL' || (group.maxParticipants && group.currentParticipants >= group.maxParticipants)) {
        return '已满员'
      }
      return '报名'
    },

    /**
     * 获取选择按钮文本
     */
    getSelectButtonText(group) {
      if (!this.canSelectGroup(group)) {
        if (group.status === 'CLOSED') {
          return '已关闭'
        }
        if (group.status === 'FULL' || (group.maxParticipants && group.currentParticipants >= group.maxParticipants)) {
          return '已满员'
        }
        return '不可选'
      }
      return '选择报名'
    },

    /**
     * 获取不可选择的原因
     */
    getUnavailableReason(group) {
      if (group.status === 'CLOSED') {
        return '该分组已关闭报名'
      }
      if (group.status === 'FULL' || (group.maxParticipants && group.currentParticipants >= group.maxParticipants)) {
        return '该分组报名人数已满'
      }
      return '该分组暂不可选择'
    }
  }
}
</script>

<style scoped>
.groups-page {
  min-height: 100vh;
  background: #f8f9fa;
  position: relative;
  padding-bottom: 200rpx; /* 为底部提示预留空间 */
}

/* 页面头部 */
.page-header {
  background: #ffffff;
  padding: 40rpx 30rpx 30rpx;
  text-align: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 10rpx;
}

.header-subtitle {
  font-size: 26rpx;
  color: #666666;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #e5e5e5;
  border-top: 4rpx solid #1890ff;
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
  padding: 120rpx 20rpx;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
  opacity: 0.6;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
}

/* 分组列表 */
.groups-container {
  padding: 20rpx;
}

.groups-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.group-item {
  background: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.group-item:active {
  transform: scale(0.98);
}

/* 分组卡片 */
.group-card {
  display: flex;
  height: 200rpx;
}

/* 分组图片 */
.group-image-container {
  width: 200rpx;
  height: 200rpx;
  flex-shrink: 0;
}

.group-image {
  width: 100%;
  height: 100%;
  border-radius: 12rpx 0 0 12rpx;
}

/* 分组内容 */
.group-content {
  flex: 1;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 分组标题 */
.group-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
  line-height: 1.3;
  margin-bottom: 12rpx;
  /* 限制显示两行，超出显示省略号 */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 积分和标签行 */
.points-tags-row {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
  gap: 16rpx;
}

/* 积分信息 */
.points-info {
  flex-shrink: 0;
}

.points-text {
  font-size: 20rpx;
  color: #fff;
  padding: 0rpx 12rpx 4rpx 12rpx;
  background: #83BB0B;
  border-radius: 12rpx;
}

/* 在售标签 */
.sale-tag {
  flex-shrink: 0;
}

.sale-text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 500;
  background: #83BB0B;
  padding: 0rpx 12rpx 4rpx 12rpx;
  border-radius: 12rpx;

}

/* 价格容器 */
.price-container {
  display: flex;
  align-items: baseline;
  margin-top: auto; /* 推到底部 */
}

.price-symbol {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
}

.price-amount {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-left: 2rpx;
}

.price-unit {
  font-size: 22rpx;
  margin-left: 4rpx;
  color: #666666;
}

/* 底部提示 */
.bottom-tips {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30rpx;
  background: #ffffff;
  border-radius: 16rpx 16rpx 0 0;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20rpx;
  font-size: 24rpx;
}

.tip-item:last-child {
  margin-bottom: 0;
}

.tip-icon {
  margin-right: 12rpx;
  font-size: 28rpx;
  line-height: 1;
}

.tip-text {
  color: #666666;
  line-height: 1.5;
  flex: 1;
}
</style>