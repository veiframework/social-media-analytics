<template>
    <view class="competition-detail">
    <!-- 初始化加载状态 -->
    <view v-if="initLoading" class="init-loading-container">
      <view class="init-loading-content">
        <view class="loading-spinner"></view>
        <text class="init-loading-text">应用初始化中...</text>
      </view>
    </view>

    <!-- 赛事详情内容 -->
    <view v-else class="detail-content">
      <!-- 赛事头图 -->
      <view class="competition-header">
        <image 
          class="competition-image" 
          :src="competitionDetail.coverImage" 
          mode="aspectFill"
        ></image>
        
      </view>

      <!-- 赛事基本信息 -->
      <view class="info-section">
        <view class="section-title">
            <view class="section-title-text">{{ competitionDetail.name }}</view>
            <button class="share-icon-container" open-type="share">
                <image class="share-icon" src="/static/competition_detail/share.png" mode="aspectFit"></image>
                <view class="share-icon-text">分享</view>
            </button>
            

        </view>
        <view class="info-item">
          <view class="info-content">
            <view class="info-label">比赛时间：</view>
            <view class="info-value">{{ formatDate(competitionDetail.startTime) + '~' + formatDate(competitionDetail.endTime) }}</view>
          </view>
        </view>
        <view class="info-item">
          <view class="info-content">
            <view class="info-label">报名时间：</view>
            <view class="info-value">{{ formatDate(competitionDetail.registrationStartTime) + '~' + formatDate(competitionDetail.registrationDeadline)  }}</view>
          </view>
        </view>
        <view class="info-item">
          <view class="info-content" @tap="handleLocationClick">
            <view class="info-label">比赛地点：</view>
            <view class="info-value location-clickable">
              <image class="location-icon" src="/static/competition_detail/location.png" mode="aspectFit"></image>
              
              {{ competitionDetail.address }}
                
            </view>
          </view>
        </view>
        <view class="info-item">
          <view class="info-content">
            <view class="info-label">主办方：</view>
            <view class="info-value">{{ competitionDetail.organizer }}</view>
          </view>
        </view>
         
      </view>

      <!-- 赛事介绍 -->
      <view class="description-section" v-if="competitionDetail.description">
        <view class="description-title">赛事介绍</view>
        <rich-text 
          class="description-content" 
          :nodes="competitionDetail.description"
        ></rich-text>
      </view>

       

      <!-- 底部操作按钮 -->
      <view class="bottom-actions">
        <!-- 比赛结束状态显示现场集锦和排行榜按钮 -->
        <view v-if="competitionDetail.status === 'FINISHED'" class="finished-actions">
          <button 
            class="action-btn highlights-btn"
            @click="handleViewHighlights"
          >
            现场集锦
          </button>
          <button 
            class="action-btn ranking-btn"
            @click="handleViewRanking"
          >
            排行榜
          </button>
        </view>
        
        <!-- 其他状态显示报名按钮 -->
        <button 
          v-else
          class="register-btn" 
          :class="{ disabled: !canRegister }"
          :disabled="!canRegister"
          @click="handleRegister"
        >
          {{ competitionDetail.status_dictText }}
        </button>
      </view>
    </view>

   
  </view>
</template>

<script>
import { getCompetitionDetail } from '@/api/user.js'

export default {
  name: 'CompetitionDetail',
  data() {
    return {
      competitionId: '',
      competitionDetail: null,
      loading: true,
      initLoading: true, // 初始化loading状态
      appReady: false   // 应用是否初始化完成
    }
  },

  computed: {
    canRegister() {
      return this.competitionDetail && 
             this.competitionDetail.status === 'REGISTRATION_OPEN'
    }
  },

  onLoad(options) {
    console.log('赛事详情页面onLoad开始');
    
    // 保存页面参数
    if (options.id) {
      this.competitionId = options.id;
    } else {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
      return;
    }
    
    // 检查应用初始化状态
    this.checkAppInitStatus(() => {
      // 应用初始化完成后执行页面逻辑
      this.handlePageLoad();
    });
  },

  // 小程序分享配置
  onShareAppMessage() {
    if (!this.competitionDetail) {
      return {
        title: '精彩赛事等你来',
        path: `/pages/competition/detail?id=${this.competitionId}`,
        imageUrl: '/static/logo.png'
      };
    }

    // 构建分享标题，包含更多信息
    const shareTitle = `${this.competitionDetail.name} - ${this.formatDate(this.competitionDetail.startTime)}开赛`;
    
    return {
      title: shareTitle,
      path: `/pages/competition/detail?id=${this.competitionId}`,
      imageUrl: this.competitionDetail.coverImage || '/static/logo.png'
    };
  },



  methods: {
    /**
     * 检查应用初始化状态
     */
    checkAppInitStatus(callback) {
      console.log('检查应用初始化状态');
      
      const startTime = Date.now();
      const maxWaitTime = 5000; // 最大等待5秒
      
      const checkInterval = setInterval(() => {
        const app = getApp();
        const globalData = app.globalData || {};
        
        console.log('检查globalData:', {
          initCompleted: globalData.initCompleted,
          isLoggedIn: globalData.isLoggedIn,
          hasToken: !!globalData.token,
          hasUserInfo: !!globalData.userInfo,
          initError: globalData.initError
        });
        
        // 检查是否初始化完成
        if (globalData.initCompleted) {
          console.log('应用初始化完成');
          clearInterval(checkInterval);
          this.appReady = true;
          this.initLoading = false;
          callback && callback();
          return;
        }
        
        // 检查是否初始化失败
        if (globalData.initError) {
          console.log('应用初始化失败:', globalData.initError);
          clearInterval(checkInterval);
          this.handleInitError(globalData.initError);
          return;
        }
        
        // 检查超时
        const elapsed = Date.now() - startTime;
        if (elapsed > maxWaitTime) {
          console.log('应用初始化超时');
          clearInterval(checkInterval);
          this.handleInitError('应用初始化超时，请重试');
          return;
        }
        
      }, 200); // 每200ms检查一次
    },

    /**
     * 处理初始化错误
     */
    handleInitError(error) {
      this.initLoading = false;
      
      const errorMessage = typeof error === 'string' ? error : '应用初始化失败';
      
      uni.showModal({
        title: '初始化失败',
        content: errorMessage + '\n\n是否重新尝试？',
        success: (res) => {
          if (res.confirm) {
            // 重新初始化
            this.initLoading = true;
            this.appReady = false;
            
            // 重新触发应用初始化
            const app = getApp();
            if (app && app.initApp) {
              app.initApp().then(() => {
                console.log('重新初始化成功');
                this.checkAppInitStatus(() => {
                  this.handlePageLoad();
                });
              }).catch((err) => {
                console.error('重新初始化失败:', err);
                this.handleInitError(err);
              });
            } else {
              // 如果应用没有initApp方法，直接重试检查
              setTimeout(() => {
                this.checkAppInitStatus(() => {
                  this.handlePageLoad();
                });
              }, 1000);
            }
          } else {
            // 用户取消，返回上一页
            uni.navigateBack();
          }
        }
      });
    },

    /**
     * 页面加载逻辑
     */
    handlePageLoad() {
      console.log('开始加载赛事详情');
      this.loadCompetitionDetail();
    },

    /**
     * 处理地点点击 - 跳转到地图页面
     */
    handleLocationClick() {
      if (!this.competitionDetail || !this.competitionDetail.address) {
        uni.showToast({
          title: '地点信息不完整',
          icon: 'none'
        });
        return;
      }

      const location = this.competitionDetail.address;
      const competitionName = this.competitionDetail.name || '比赛地点';
      
      // 构建跳转参数
      const params = {
        name: encodeURIComponent(competitionName),
        address: encodeURIComponent(location)
      };
      
      // 添加比赛时间信息
      if (this.competitionDetail.startTime && this.competitionDetail.endTime) {
        const timeStr = `${this.formatDate(this.competitionDetail.startTime)}~${this.formatDate(this.competitionDetail.endTime)}`;
        params.competitionTime = encodeURIComponent(timeStr);
      }
      
      // 添加主办方信息
      if (this.competitionDetail.organizer) {
        params.organizer = encodeURIComponent(this.competitionDetail.organizer);
      }
      
      // 如果有经纬度信息，也传递过去
      if (this.competitionDetail.latitude && this.competitionDetail.longitde) {
        params.latitude = this.competitionDetail.latitude;
        params.longitude = this.competitionDetail.longitde;
      }
      
      // 跳转到地图页面
      const paramStr = Object.keys(params).map(key => `${key}=${params[key]}`).join('&');
      uni.navigateTo({
        url: `/pages/map/location?${paramStr}`,
        fail: () => {
          uni.showToast({
            title: '无法打开地图页面',
            icon: 'none'
          });
        }
      });
    },








     
    /**
     * 加载赛事详情
     */
    async loadCompetitionDetail() {
      if (!this.competitionId) return

      try {
        this.loading = true
        
        const response = await getCompetitionDetail(this.competitionId)
        
        // 适配不同的响应格式
        let result = null
        if (response && response.data) {
          result = response.data
        } else if (response) {
          result = response
        }

        if (result) {
          // 处理富文本内容
          if (result.description) {
            result.description = this.addWordBreakToAll(this.processRichText(result.description))
          }
          
          this.competitionDetail = result
          
          // 动态设置页面标题
          if (result.name) {
            uni.setNavigationBarTitle({
              title: result.name.length > 10 ? result.name.substring(0, 10) + '...' : result.name
            })
          }
          
          console.log('赛事详情加载成功:', result)
        } else {
          throw new Error('获取赛事详情失败')
        }

      } catch (error) {
        console.error('获取赛事详情失败:', error)
        
        let errorMessage = '获取赛事详情失败'
        if (error.code === 401) {
          errorMessage = '登录已过期，请重新登录'
        } else if (error.code === 404) {
          errorMessage = '赛事不存在或已下线'
        } else if (error.message && error.message.includes('network')) {
          errorMessage = '网络连接失败，请检查网络'
        }
        
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 2000
        })
        
        // 如果是404错误，延迟返回上一页
        if (error.code === 404) {
          setTimeout(() => {
            uni.navigateBack()
          }, 2000)
        }
        
      } finally {
        this.loading = false
      }
    },

    /**
     * 格式化日期
     */
    formatDate(dateString) {
      return dateString.split(' ')[0]
    },

    /**
     * 获取状态文本
     */
    getStatusText(status) {
      const statusMap = {
        'REGISTRATION_OPEN': '报名中',
        'REGISTRATION_CLOSED': '报名结束',
        'IN_PROGRESS': '进行中',
        'FINISHED': '已结束',
        'CANCELLED': '已取消'
      }
      return statusMap[status] || '未知状态'
    },

    /**
     * 获取状态样式类
     */
    getStatusClass(status) {
      return {
        'status-open': status === 'REGISTRATION_OPEN',
        'status-closed': status === 'REGISTRATION_CLOSED',
        'status-progress': status === 'IN_PROGRESS',
        'status-finished': status === 'FINISHED',
        'status-cancelled': status === 'CANCELLED'
      }
    },

    /**
     * 获取操作按钮文本
     */
    getActionButtonText() {
      if (!this.competitionDetail) return '加载中...'
      
      switch (this.competitionDetail.status) {
        case 'REGISTRATION_OPEN':
          return '立即报名'
        case 'REGISTRATION_CLOSED':
          return '报名已结束'
        case 'IN_PROGRESS':
          return '比赛进行中'
        case 'FINISHED':
          return '比赛已结束'
        case 'CANCELLED':
          return '比赛已取消'
        default:
          return '暂不可用'
      }
    },

    /**
     * 处理报名
     */
    handleRegister() {
      if (!this.canRegister) return
      
      console.log('处理报名:', this.competitionDetail)
      
      // 跳转到赛事分组页面
      uni.navigateTo({
        url: `/pages/competition/groups?competitionId=${this.competitionId}`,
        fail: () => {
          uni.showToast({
            title: '赛事分组功能开发中',
            icon: 'none'
          })
        }
      })
    },

    /**
     * 查看现场集锦
     */
    handleViewHighlights() {
      console.log('查看现场集锦:', this.competitionDetail)
      
      // 跳转到现场集锦页面
      uni.navigateTo({
        url: `/pages/photos/index?competitionId=${this.competitionId}`,
        fail: () => {
          uni.showToast({
            title: '现场集锦功能开发中',
            icon: 'none'
          })
        }
      })
    },

    /**
     * 查看排行榜
     */
    handleViewRanking() {
      console.log('查看排行榜:', this.competitionDetail)
      
      // 跳转到排行榜页面
      uni.navigateTo({
        url: `/pages/competition/ranking?competitionId=${this.competitionId}`,
        fail: () => {
          uni.showToast({
            title: '排行榜功能开发中',
            icon: 'none'
          })
        }
      })
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
.competition-detail {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 初始化加载状态 */
.init-loading-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.init-loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
}

.loading-spinner {
  width: 64rpx;
  height: 64rpx;
  border: 4rpx solid #f3f3f3;
  border-top: 4rpx solid #83BB0B;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.init-loading-text {
  font-size: 28rpx;
  color: #666666;
  text-align: center;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 20rpx;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #999;
}

/* 赛事头图 */
.competition-header {
  position: relative;
  height: 600rpx;
  overflow: hidden;
}

.competition-image {
  width: 100%;
  height: 100%;
}

.header-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  padding: 60rpx 30rpx 30rpx;
  color: white;
}

.competition-title {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  line-height: 1.4;
}

.competition-subtitle {
  font-size: 28rpx;
  opacity: 0.9;
  line-height: 1.3;
}

/* 信息区块 */
.info-section {
  background: white;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.1);
  position: absolute;
  width: 87%;
  top: 25%;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
 
  padding-bottom: 15rpx;
  display: flex;
  justify-content: space-between;
}

/* 信息项 */
.info-item {
  display: flex;
  align-items: center;
  padding: 10rpx 0;
  
}

.info-item:last-child {
  border-bottom: none;
}

.info-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 20rpx;
}

.info-content {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.info-label {
  font-size: 24rpx;
  color: #999;
   
}

.info-value {
  font-size: 24rpx;
  color: #333;
  font-weight: 500;
}

.location-clickable {
  color: #83BB0B !important;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8rpx;
  width: 80%;
  flex-direction: row-reverse;
}

.location-clickable:active {
  opacity: 0.7;
}

/* 状态样式 */
.status-open {
  color: #52c41a !important;
}

.status-closed {
  color: #ff4d4f !important;
}

.status-progress {
  color: #1890ff !important;
}

.status-finished {
  color: #666 !important;
}

.status-cancelled {
  color: #ff4d4f !important;
}

/* 赛事介绍区块 */
.description-section {
  margin: 200rpx 20rpx 20rpx 20rpx;
}

.description-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

/* 描述内容 */
.description-content,
.rules-content {
  font-size: 30rpx;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 富文本样式优化 */
.description-content {
  word-break: break-all;
  overflow-wrap: break-word;
}

/* 富文本内部元素样式 */
.description-content /deep/ p {
  margin: 0 0 16rpx 0;
  line-height: 1.6;
}

.description-content /deep/ img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 16rpx 0;
  border-radius: 8rpx;
}

.description-content /deep/ h1,
.description-content /deep/ h2,
.description-content /deep/ h3,
.description-content /deep/ h4,
.description-content /deep/ h5,
.description-content /deep/ h6 {
  margin: 24rpx 0 16rpx 0;
  font-weight: bold;
  color: #333;
}

.description-content /deep/ ul,
.description-content /deep/ ol {
  margin: 16rpx 0;
  padding-left: 40rpx;
}

.description-content /deep/ li {
  margin: 8rpx 0;
}

.description-content /deep/ strong {
  font-weight: bold;
  color: #333;
}

.description-content /deep/ em {
  font-style: italic;
}

.description-content /deep/ a {
  color: #1890ff;
  text-decoration: underline;
}

/* 底部操作 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.1);
}

/* 比赛结束状态的按钮容器 */
.finished-actions {
  display: flex;
  gap: 20rpx;
}

/* 通用按钮样式 */
.action-btn {
  flex: 1;
  height: 88rpx;
  border: none;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 现场集锦按钮 */
.highlights-btn {
  background: linear-gradient(45deg, #999999, #999999);
  color: white;
}

.highlights-btn:active {
  transform: scale(0.98);
}

/* 排行榜按钮 */
.ranking-btn {
  background: linear-gradient(45deg, #83BB0B, #9FD611);
  color: white;
}

.ranking-btn:active {
  transform: scale(0.98);
}

/* 报名按钮 */
.register-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(45deg, #83BB0B, #83BB0B);
  color: white;
  border: none;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-btn.disabled {
  background: #d9d9d9;
  color: #999;
}

.register-btn:not(.disabled):active {
  transform: scale(0.98);
}

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 20rpx;
}

.error-icon {
  width: 200rpx;
  height: 200rpx;
  opacity: 0.6;
  margin-bottom: 30rpx;
}

.error-text {
  font-size: 32rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.retry-btn {
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 25rpx;
  padding: 20rpx 40rpx;
  font-size: 30rpx;
}

.retry-btn:active {
  transform: scale(0.98);
}
.detail-content{
    padding-bottom: 220rpx; /* 为底部固定按钮预留空间 */
}

.location-icon{
    width: 24rpx;
    height: 24rpx;
}

.share-icon{
    width: 30rpx;
    height: 30rpx;
    margin-bottom: 5rpx;
}
.share-icon-container{
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    color: #83BB0B;
    font-size: 24rpx;
    width: 80rpx;
    background: transparent;
    border: none;
    padding: 0;
    margin: 0;
    line-height: 1;
}

.share-icon-container::after{
    border: none;
}
</style>