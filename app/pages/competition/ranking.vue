<template>
  <view class="ranking-page">
    <!-- 顶部背景图 -->
    <view class="ranking-header">
        <view class="header-left">
            <view class="header-title">赛事</view>
            <view class="header-subtitle">排行榜</view>
        </view>
        <image class="header-right" src="/static/ranking/post2.png" mode="aspectFill"></image>
    </view>

    <!-- 排行榜内容 -->
    <view class="ranking-content">
      <!-- 加载状态 -->
      <view v-if="loading && rankingList.length === 0" class="loading-container">
        <uni-loading size="24"></uni-loading>
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 空状态 -->
      <view v-else-if="!loading && rankingList.length === 0" class="empty-container">
        <text class="empty-text">暂无排行榜数据</text>
      </view>

      <!-- 第一名特殊卡片 -->
      <view v-if="currentParticipantInfo" class="first-place-card">
        <view class="first-place-content">
          <view class="user-info-first">
            <image  v-if="displayMode === 'participant'"
              class="user-avatar-first" 
              :src="currentParticipantInfo.avatar || '/static/ranking/child_avatar.png'" 
              mode="aspectFill"
            ></image>
            <view class="user-details-first">
              <view class="name-row">
                <text class="user-name-first">{{ currentParticipantInfo.name }}</text>
              
                <text class="user-label-first">{{ displayMode === 'participant' ? '本人' : '分组' }}</text>
                <view v-if="hasMultipleOptions" class="small-arrow" @click="switchToNext">
                  <text class="small-arrow-icon">→</text>
                </view>
              </view>
              <text class="rank-text-first" v-if="displayMode === 'participant'">NO.{{ currentParticipantInfo.rank }}</text>
             
            </view>
          </view>
          <view class="ranking-info-first">
            <view class="time-display">
              <text class="time-text-first" v-if="displayMode === 'participant'">{{ formatTime(currentParticipantInfo.time) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 其他排名列表 -->
      <view v-if="rankingList.length > 1" class="other-rankings">
        <view 
          v-for="(item, index) in rankingList" 
          :key="item.id || index"
          class="ranking-item"
          :class="{ 'second-place': index === 0, 'third-place': index === 1 }"
        >
          <!-- 排名标识 -->
          <view class="ranking-badge" :class="getRankingBadgeClass(item.rank)">
            <text class="ranking-number">{{ item.rank }}</text>
          </view>

          <!-- 用户信息 -->
          <view class="user-info">
            <image 
              class="user-avatar" 
              :src="item.avatar || '/static/ranking/child_avatar.png'" 
              mode="aspectFill"
            ></image>
            <view class="user-details">
              <text class="user-name">{{ item.name || item.userName  }}</text>
            </view>
          </view>

          <!-- 成绩时间 -->
          <view class="ranking-time">
            <text class="time-text">{{ formatTime(item.time) }}</text>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="loading && rankingList.length > 0" class="load-more">
        <uni-loading size="16"></uni-loading>
        <text class="load-more-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getCompetitionRanking } from '@/api/user.js'

export default {
  name: 'CompetitionRanking',
  data() {
    return {
      competitionId: '',
      rankingList: [],
      loading: true,
      error: null,
      // 新增数据处理相关状态
      rawRankingData: [], // 原始排行榜数据
      currentDisplayData: [], // 当前显示的排行榜数据
      currentParticipantIndex: 0, // 当前选中的参赛人索引
      currentGroupIndex: 0, // 当前选中的分组索引
      displayMode: 'participant', // 显示模式：'participant' 或 'group'
      hasMultipleOptions: false, // 是否有多个选项可切换
      currentTitle: '', // 当前显示的标题
      currentParticipantInfo: null // 当前参赛人的信息
    }
  },

  onLoad(options) {
    if (options.competitionId) {
      this.competitionId = options.competitionId
      this.loadRankingData()
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
    this.loadRankingData().then(() => {
      uni.stopPullDownRefresh()
    })
  },

  methods: {
    /**
     * 加载排行榜数据
     */
    async loadRankingData() {
      if (!this.competitionId) return

      try {
        this.loading = true
        this.error = null

        const response = await getCompetitionRanking(this.competitionId)
        
        // 适配不同的响应格式
        let result = null
        if (response && response.data) {
          result = response.data
        } else if (response) {
          result = response
        }

        if (Array.isArray(result)) {
          this.rawRankingData = result
          this.processRankingData()
        } else if (result && Array.isArray(result.records)) {
          this.rawRankingData = result.records
          this.processRankingData()
        }  

        console.log('排行榜数据加载成功:', this.rawRankingData)

      } catch (error) {
        console.error('获取排行榜失败:', error)
        
       
        this.processRankingData()
        
        let errorMessage = '获取排行榜失败'
        if (error.code === 401) {
          errorMessage = '登录已过期，请重新登录'
        } else if (error.code === 404) {
          errorMessage = '排行榜数据不存在'
        } else if (error.message && error.message.includes('network')) {
          errorMessage = '网络连接失败，请检查网络'
        }
        
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 2000
        })
        
      } finally {
        this.loading = false
      }
    },

   

    /**
     * 处理排行榜数据
     */
    processRankingData() {
      if (!this.rawRankingData || this.rawRankingData.length === 0) {
        this.rankingList = []
        this.currentDisplayData = []
        this.hasMultipleOptions = false
        this.currentTitle = ''
        this.currentParticipantInfo = null
        return
      }

      // 判断显示模式：有参赛人身份证号则显示参赛人，否则显示分组
      const hasParticipants = this.rawRankingData.some(item => item.idCard)
      this.displayMode = hasParticipants ? 'participant' : 'group'
      
      if (hasParticipants) {
        // 参赛人模式
        this.hasMultipleOptions = this.rawRankingData.length > 1
        this.currentParticipantIndex = Math.min(this.currentParticipantIndex, this.rawRankingData.length - 1)
        const currentParticipant = this.rawRankingData[this.currentParticipantIndex]
        this.currentTitle = currentParticipant.participantName
        this.currentDisplayData = currentParticipant.rankList || []
        
        // 保存当前参赛人的信息，用于第一名卡片展示
        this.currentParticipantInfo = {
          name: currentParticipant.participantName,
          rank: currentParticipant.rank,
          time: currentParticipant.competitionTime,
          avatar: '/static/ranking/child_avatar.png'
        }
      } else {
        // 分组模式
        const groups = this.rawRankingData.filter(item => item.groupId)
        this.hasMultipleOptions = groups.length > 1
        this.currentGroupIndex = Math.min(this.currentGroupIndex, groups.length - 1)
        const currentGroup = groups[this.currentGroupIndex]
        this.currentTitle = currentGroup ? currentGroup.groupName : ''
        this.currentDisplayData = currentGroup ? currentGroup.rankList || [] : []
        
        // 分组模式下，第一名卡片显示分组信息
        this.currentParticipantInfo = currentGroup ? {
          name: currentGroup.groupName,
          rank: 1, // 分组模式下可以显示为1
          time: '', // 分组可能没有时间信息
          avatar: '/static/ranking/child_avatar.png'
        } : null
      }

      // 转换数据格式以匹配现有的渲染逻辑
      this.rankingList = this.currentDisplayData.map(item => ({
        id: item.id,
        name: item.participantName,
        userName: item.participantName,
        avatar: '/static/ranking/child_avatar.png',
        time: item.competitionTime,
        rank: item.rank,
        extra: '参赛者'
      }))
    },

    /**
     * 切换到下一个选项（参赛人或分组）
     */
    switchToNext() {
      if (!this.hasMultipleOptions) return

      if (this.displayMode === 'participant') {
        this.currentParticipantIndex = (this.currentParticipantIndex + 1) % this.rawRankingData.length
      } else {
        const groups = this.rawRankingData.filter(item => item.groupId)
        this.currentGroupIndex = (this.currentGroupIndex + 1) % groups.length
      }

      this.processRankingData()
    },

    /**
     * 获取排名标识样式类
     */
    getRankingBadgeClass(rank) {
      if (rank === 1) return 'badge-first'
      if (rank === 2) return 'badge-second'
      if (rank === 3) return 'badge-third'
      return 'badge-normal'
    },

    /**
     * 格式化时间
     */
    formatTime(timeStr) {
      if (!timeStr) return '--'
      return timeStr
    }
  }
}
</script>

<style scoped>
.ranking-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #242A09, #242A09);
}

/* 顶部背景 */
.ranking-header {
  background: linear-gradient(135deg, #242A09, #242A09);
  height: 400rpx;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-around;
  color: white;
}

.header-title {
  font-weight: bold;
  margin-bottom: 10rpx;
  color: #83BB0B;
}

.header-subtitle {
    font-weight: bold;
  
}

/* 排行榜内容 */
.ranking-content {
  padding: 20rpx;
}



/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 20rpx;
  background: white;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #999;
}

/* 空状态 */
.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100rpx 20rpx;
  background: white;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 第一名特殊卡片 */
.first-place-card {
  background: linear-gradient(135deg, #83BB0B, #9FD611);
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(131, 187, 11, 0.3);
}

.first-place-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info-first {
  display: flex;
  align-items: center;
  flex: 1;
}

.user-avatar-first {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  background: #f0f0f0;
  border: 4rpx solid white;
}

.user-details-first {
  display: flex;
  flex-direction: column;
}

.name-row {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}

.user-name-first {
  font-size: 36rpx;
  font-weight: bold;
  color: white;
  margin-right: 8rpx;
}

.small-arrow {
  width: 24rpx;
  height: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12rpx;
  cursor: pointer;
  margin-left: 10rpx;
}

.small-arrow-icon {
  color: white;
  font-size: 20rpx;
  font-weight: bold;
}

.small-arrow:active {
  transform: scale(0.9);
}

.user-label-first {
  font-size: 24rpx;
  color: #83BB0B;
  background-color: #fff;
  border-radius: 10rpx;
  padding: 0 10rpx;
}

.rank-text-first {
  font-size: 28rpx;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.9);
}

.ranking-info-first {
  text-align: right;
  color: white;
}



.time-display {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
}

.time-text-first {
  font-size: 28rpx;
  color: white;
}

.time-number {
  font-size: 36rpx;
  font-weight: bold;
  margin: 0 4rpx;
}

.time-unit {
  font-size: 24rpx;
  margin: 0 8rpx 0 0;
}

/* 其他排名 */
.other-rankings {
  margin-top: 20rpx;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 25rpx 30rpx;
  background: white;
  border-radius: 12rpx;
  margin-bottom: 15rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.ranking-item.second-place {
  border-left: 6rpx solid #FFD700;
}

.ranking-item.third-place {
  border-left: 6rpx solid #CD7F32;
}

/* 排名标识 */
.ranking-badge {
  width: 50rpx;
  height: 50rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 25rpx;
  flex-shrink: 0;
}

.ranking-badge.badge-first {
  background: linear-gradient(45deg,  #CD7F32, #B8860B);
}

.ranking-badge.badge-second {
  background: linear-gradient(45deg, #FFD700, #FFA500);
}

.ranking-badge.badge-third {
  background: linear-gradient(45deg,#1b7dbe, #32a6d4   );
}

.ranking-badge.badge-normal {
  background: #f0f0f0;
}

.ranking-number {
  font-size: 24rpx;
  font-weight: bold;
  color: white;
}

.ranking-badge.badge-normal .ranking-number {
  color: #666;
}

/* 用户信息 */
.user-info {
  flex: 1;
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 70rpx;
  height: 70rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  background: #f0f0f0;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 300rpx;
}

/* 排名时间 */
.ranking-time {
  text-align: right;
}

.time-text {
  font-size: 26rpx;
  color: #666;
  font-weight: 500;
}

/* 加载更多 */
.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx 0;
  background: white;
  border-radius: 12rpx;
  margin-top: 20rpx;
}

.load-more-text {
  margin-left: 20rpx;
  font-size: 28rpx;
  color: #999;
}

.header-left {
  font-size: 90rpx;
  display: flex;
  flex-direction: column;
}
.header-right {
   width: 404rpx;
   height: 403rpx;
}
</style>