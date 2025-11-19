<template>
  <view class="score-page">
    <!-- 积分卡片 -->
    <view class="score-top">
    <view class="score-card">
       
      <view class="score-info">
        <text class="score-title">我的积分</text>
        <text class="score-value">{{ totalScore }}</text>
        <view class="score-subtitle" @click="showScoreHint">
            <image class="socre-hint-icon" src="/static/score/hint_icon.png" mode="aspectFit"></image>
            <view class="score-hint-text">如何获得积分</view>    
        </view>
      </view>
       
       
      
    </view>
    <image class="star-icon" src="/static/score/score_box_icon.png" mode="aspectFit"></image>
  </view>
    <!-- 积分记录标题 -->
    <view class="records-header">
      <text class="records-title">积分记录</text>
    </view>

    <!-- 积分记录列表 -->
    <view class="records-container">
      
      
      <view v-if="scoreRecords.length === 0" class="empty-container">
        <text class="empty-text">暂无积分记录</text>
      </view>
      
      <view v-else class="records-list">
        <view 
          v-for="(record, index) in scoreRecords" 
          :key="index"
          class="record-item"
        >
          <view class="record-content">
            <text class="record-title">{{ record.competitionId_dictText }}</text>
            <text class="record-time">获得时间：{{ record.createTime }}</text>
          </view>
          <text class="record-score positive">
            +{{ Math.abs(record.points) }}
          </text>
        </view>
      </view>
      
      <!-- 加载更多 -->
      <view v-if="loading && scoreRecords.length > 0" class="load-more">
        <uni-loading size="16"></uni-loading>
        <text class="load-more-text">加载中...</text>
      </view>
      
      <!-- 没有更多数据 -->
      <view v-if="!hasMore && scoreRecords.length > 0" class="no-more">
        <text class="no-more-text">没有更多数据了</text>
      </view>
    </view>

    <!-- 积分提示弹窗 -->
    <view v-if="showHintModal" class="hint-modal-overlay" @tap="closeHintModal" @touchmove.prevent>
      <view class="hint-modal-container" @tap.stop @touchmove.stop>
        <!-- 弹窗头部 -->
        <view class="hint-modal-header">
          <text class="hint-modal-title">如何获得积分</text>
          <view class="hint-close-btn" @tap="closeHintModal">
            <text class="hint-close-icon">×</text>
          </view>
        </view>

        <!-- 弹窗内容 -->
        <scroll-view class="hint-modal-content" scroll-y>
          <view class="hint-content-wrapper">
            <!-- 动态协议内容 -->
            <template v-if="scoreHintContent">
              <rich-text 
                class="hint-rich-content" 
                :nodes="scoreHintContent.content"
              ></rich-text>
            </template>

            <!-- 静态内容作为备用 -->
            <template v-else>
              <text class="hint-static-content">
                ⽤户（参赛⻘少年）成功参与赛事并完赛后，系统⾃动发放固定积分到⽤户账户。积分归属于参赛⻘少年。
              </text>
            </template>
          </view>
        </scroll-view>

        <!-- 弹窗底部 -->
        <view class="hint-modal-footer">
          <button class="hint-confirm-btn" @tap="closeHintModal">知道了</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserScoreRecords } from '@/api/user.js'

export default {
  name: 'ScorePage',
  data() {
    return {
      totalScore: 0, // 总积分，从用户信息获取
      scoreRecords: [
        
        
      ], // 积分记录列表
      loading: false,
      currentPage: 1,
      pageSize: 10,
      hasMore: true,
      scoreHintContent: null, // 存储积分提示协议内容
      showHintModal: false // 控制积分提示弹窗显示
    }
  },
  
  onLoad(options) {
    // 从参数中获取总积分
    if (options.totalScore) {
      this.totalScore = parseInt(options.totalScore);
    }
    // 加载积分提示协议内容
    this.loadScoreHintContent();
    // 首次加载时尝试获取真实数据，失败时使用模拟数据
    this.loadScoreRecords(true);
  },
  
  
  
  onPullDownRefresh() {
    this.loadScoreRecords(true).then(() => {
      uni.stopPullDownRefresh();
    });
  },
  
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadScoreRecords(false);
    }
  },
  
  methods: {
    /**
     * 加载积分提示协议内容
     */
    loadScoreHintContent() {
      try {
        const agreementList = uni.getStorageSync('agreementList')
        console.log('获取到的协议列表:', agreementList)
        
        if (agreementList && Array.isArray(agreementList)) {
          // 查找type为'1'的协议
          const targetAgreement = agreementList.find(item => item.type === '2')
          if (targetAgreement) {
            this.scoreHintContent = targetAgreement
            console.log('找到type=1的积分提示协议内容:', targetAgreement)
          } else {
            console.log('未找到type=1的积分提示协议内容')
            this.scoreHintContent = null
          }
        } else {
          console.log('本地缓存中没有协议列表数据')
          this.scoreHintContent = null
        }
      } catch (error) {
        console.error('加载积分提示协议内容失败:', error)
        this.scoreHintContent = null
      }
    },

    /**
     * 加载积分记录
     */
    async loadScoreRecords(reset = false) {
      if (this.loading) return;
      
      try {
        this.loading = true;
        
        const params = {
          pageNum: reset ? 1 : this.currentPage,
          pageSize: this.pageSize
        };
        
        const response = await getUserScoreRecords(params);
        console.log('积分记录响应:', response);
        
        let recordsData = [];
        let totalRecords = 0;
        
        // 适配不同的响应格式
      
            recordsData = response.records;
            totalRecords = response.total;
         
        
        // 如果API返回了数据，使用API数据
        if (recordsData.length > 0) {
          if (reset) {
            this.scoreRecords = recordsData;
            this.currentPage = 2;
          } else {
            this.scoreRecords = [...this.scoreRecords, ...recordsData];
            this.currentPage++;
          }
          
          // 判断是否还有更多数据
          this.hasMore = recordsData.length === this.pageSize && 
                        this.scoreRecords.length < totalRecords;
        } else if (reset) {
          // 如果是重置且API没有数据，保留模拟数据
          console.log('API无数据，使用模拟数据');
          this.hasMore = false;
        }
        
        console.log('积分记录加载成功:', this.scoreRecords);
        
      } catch (error) {
        console.error('获取积分记录失败:', error);
        
        // API调用失败时，如果是首次加载，保留模拟数据
        if (reset) {
          console.log('API调用失败，使用模拟数据');
          this.hasMore = false;
        } else {
          let errorMessage = '获取积分记录失败';
          if (error.code === 401) {
            errorMessage = '登录已过期，请重新登录';
          } else if (error.code === 403) {
            errorMessage = '没有权限访问积分记录';
          } else if (error.message && error.message.includes('network')) {
            errorMessage = '网络连接失败，请检查网络';
          }
          
          uni.showToast({
            title: errorMessage,
            icon: 'none',
            duration: 2000
          });
        }
      } finally {
        this.loading = false;
      }
    },
    
    /**
     * 格式化时间
     */
    formatTime(timeStr) {
      if (!timeStr) return '';
      
      try {
        const date = new Date(timeStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      } catch (error) {
        console.error('时间格式化失败:', error);
        return timeStr;
      }
    },
    
    /**
     * 显示积分获得提示
     */
    showScoreHint() {
      this.showHintModal = true;
    },

    /**
     * 关闭积分提示弹窗
     */
    closeHintModal() {
      this.showHintModal = false;
    }
  }
}
</script>

<style scoped>
.score-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 积分卡片 */
.score-card {
  margin: 20rpx;
  padding: 40rpx;
  position: absolute;
  width: 84%;
  bottom: -120rpx;
  z-index: 999;
  display: flex;
  justify-content: space-between;
  align-items: center;
 
  overflow: hidden;
  border-radius: 8px;
  background: linear-gradient(106deg, #DDCFB4 13.03%, #836939 86.05%);
flex-shrink: 0;
}

.score-info {
  display: flex;
  flex-direction: column;
}

.score-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.score-value {
  font-size: 72rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.score-subtitle {
  font-size: 24rpx;
  color: #000;
  opacity: 0.8;
  display: flex;
  align-items: center;
  
}

.score-icon {
  width: 100rpx;
  height: 100rpx;
  background: rgba(139, 69, 19, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.star-icon {
  position: absolute;
  width: 300rpx;
  height: 300rpx;
  top: 0;
  
  right: 0;
 
  z-index: 999;
}

/* 积分记录标题 */
.records-header {
    margin-top: 100rpx;
  padding: 30rpx 20rpx 20rpx 20rpx;
}

.records-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

/* 积分记录容器 */
.records-container {
  margin: 0 20rpx;
  
  overflow: hidden;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
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
  padding: 100rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 积分记录列表 */
.records-list {
  padding: 0;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 40rpx;
  background-color: #fff;
  border-radius: 4rpx;
  margin-bottom: 20rpx;
}

.record-item:last-child {
  border-bottom: none;
}

.record-content {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.record-title {
  font-size: 30rpx;
  color: #333;
  margin-bottom: 10rpx;
  font-weight: 500;
}

.record-time {
  font-size: 24rpx;
  color: #999;
}

.record-score {
  font-size: 32rpx;
  font-weight: bold;
  margin-left: 20rpx;
}

.record-score.positive {
  color: #4CAF50;
}

.record-score.negative {
  color: #F44336;
}

/* 加载更多 */
.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx 0;
}

.load-more-text {
  margin-left: 20rpx;
  font-size: 28rpx;
  color: #999;
}

/* 没有更多数据 */
.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx 0;
}

.no-more-text {
  font-size: 28rpx;
  color: #999;
}

.score-top{
    position: relative;
    background-color: #333;
    height: 240rpx;
    z-index: 1;
}
.socre-hint-icon{
    width: 26rpx;
    height: 26rpx;
}

.score-hint-text{
    padding-left: 6rpx;
}

/* 积分提示弹窗样式 */
.hint-modal-overlay {
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
  overflow: hidden;
  touch-action: none;
}

.hint-modal-container {
  background: #ffffff;
  border-radius: 16rpx;
  width: 100%;
  max-width: 600rpx;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  touch-action: auto;
}

.hint-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.hint-modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.hint-close-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  background: #f5f5f5;
}

.hint-close-icon {
  font-size: 40rpx;
  color: #999999;
  line-height: 1;
}

.hint-modal-content {
  flex: 1;
  max-height: 60vh;
}

.hint-content-wrapper {
  padding: 30rpx;
}

.hint-rich-content {
  width: 100%;
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
  text-align: justify;
}

.hint-static-content {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
  text-align: justify;
  display: block;
}

.hint-modal-footer {
  padding: 30rpx;
  border-top: 1rpx solid #f0f0f0;
}

.hint-confirm-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  font-weight: bold;
  border: none;
  background: #83BB0B;
  color: #ffffff;
  transition: all 0.3s;
}

.hint-confirm-btn:active {
  background: #6fa309;
  transform: scale(0.98);
}
</style>