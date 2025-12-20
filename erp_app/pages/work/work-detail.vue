<template>
  <view class="detail-container">
    <!-- 加载中提示 -->
    <view class="loading" v-if="loading">
      <text>加载中...</text>
    </view>

    <!-- 详情内容 -->
    <scroll-view v-else class="detail-content" scroll-y="true">
      <!-- 基本信息 -->
      <view class="detail-section">
        <!-- 统计数据 -->
        <view class="detail-section">
          <text class="section-title">统计数据</text>
          <view class="stats-grid">
            <view class="stat-item">
              <text class="stat-label">点赞数</text>
              <text class="stat-value">{{ workDetail.thumbNum || 0 }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">收藏数</text>
              <text class="stat-value">{{ workDetail.collectNum || 0 }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">评论数</text>
              <text class="stat-value">{{ workDetail.commentNum || 0 }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">播放量</text>
              <text class="stat-value">{{ workDetail.playNum || 0 }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">分享数</text>
              <text class="stat-value">{{ workDetail.shareNum || 0 }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">点赞增长量</text>
              <text class="stat-value">{{ workDetail.thumbNumUp || 0 }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">播放增长量</text>
              <text class="stat-value">{{ workDetail.playNumUp || 0 }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">点赞趋势</text>
              <text class="stat-value" :class="{ 'trend-up': workDetail.thumbNumChange > 0, 'trend-down': workDetail.thumbNumChange < 0 }">
                <text v-if="workDetail.thumbNumChange > 0">↑ </text>
                <text v-else-if="workDetail.thumbNumChange < 0">↓ </text>
                {{ Math.abs(workDetail.thumbNumChange) || 0 }}
              </text>
            </view>
            <view class="stat-item">
              <text class="stat-label">播放趋势</text>
              <text class="stat-value" :class="{ 'trend-up': workDetail.playNumChange > 0, 'trend-down': workDetail.playNumChange < 0 }">
                <text v-if="workDetail.playNumChange > 0">↑ </text>
                <text v-else-if="workDetail.playNumChange < 0">↓ </text>
                {{ Math.abs(workDetail.playNumChange) || 0 }}
              </text>
            </view>
          </view>
        </view>

        <text class="section-title">基本信息</text>
        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">平台</text>
            <text class="info-value platform-tag" :style="platformStyle(workDetail.platformId)">{{ workDetail.platformId_dictText }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">作品类型</text>
            <text class="info-value">{{ workDetail.type_dictText }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">媒体类型</text>
            <text class="info-value">{{ workDetail.mediaType_dictText }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">作品状态</text>
            <text class="info-value">{{ workDetail.state_dictText }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">员工</text>
            <text class="info-value">{{ workDetail.userId_dictText }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">账号</text>
            <text class="info-value">{{ workDetail.accountId_dictText }}</text>
          </view>
        </view>
      </view>

      <!-- 作品内容 -->
      <view class="detail-section">
        <text class="section-title">作品内容</text>
        <view class="content-block">
          <text class="info-label">标题</text>
          <view class="content-text">{{ workDetail.title || '暂无描述' }}</view>
        </view>
        <view class="content-block">
          <text class="info-label">话题</text>
          <view class="content-text-topic-box">
            <view class="content-text-topic"  v-if="workDetail.topics" :key="topic" v-for="topic in workDetail.topics.split(',')">{{ topic || '暂无描述' }}</view>
          </view>
        </view>
        <view class="content-block">
          <text class="info-label">描述</text>
          <view class="content-text">{{ workDetail.description || '暂无描述' }}</view>
        </view>
        <view class="content-block">
          <text class="info-label">作品链接</text>
          <view class="content-text link-text">{{ workDetail.url || '暂无链接' }}</view>
        </view>
        <view class="content-block">
          <text class="info-label">分享链接</text>
          <view class="content-text link-text">{{ workDetail.shareLink || '暂无分享链接' }}</view>
        </view>
        <view class="content-block">
          <text class="info-label">第三方作品ID</text>
          <view class="content-text">{{ workDetail.workUid || '暂无' }}</view>
        </view>
      </view>



      <!-- 时间信息 -->
      <view class="detail-section">
        <text class="section-title">时间信息</text>
        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">发布时间</text>
            <text class="info-value">{{ workDetail.postTime || '暂无' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">更新时间</text>
            <text class="info-value">{{ workDetail.updateTime || '暂无' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">创建时间</text>
            <text class="info-value">{{ workDetail.createTime || '暂无' }}</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getWorkApi } from '../../api/work.js'

export default {
  setup() {
    // 作品详情数据
    const workDetail = ref({})
    // 加载状态
    const loading = ref(true)
    // 获取作品ID
    const id = ref('')

    // 获取作品详情
    const getWorkDetail = async () => {
      try {
        loading.value = true
        const res = await getWorkApi(id.value)
        workDetail.value = res || {}
      } catch (error) {
        console.error('获取作品详情失败', error)
        uni.showToast({
          title: '获取详情失败',
          icon: 'none'
        })
      } finally {
        loading.value = false
      }
    }
    const platformStyle = (platformId) => {
      let platformColors = {
        'douyin': '#FF7A45', // 抖音
        'kuaishou': '#00C1DE', // 快手
        'wechatvideo': '#00A0E9', // 视频号
        'xiaohongshu': '#FE2C55', // 小红书
        'xigua': '#FFD60A', // 西瓜视频
        default: '#5AC8FA' // 默认
      }
      return 'color:' + platformColors[platformId]
    }
    // 页面加载时获取参数和详情
    onMounted(() => {
      // 获取路由参数
      const pages = getCurrentPages()
      const currentPage = pages[pages.length - 1]
      id.value = currentPage.options.id
      
      // 获取详情数据
      getWorkDetail()
    })

    return {
      workDetail,
      loading,
      platformStyle
    }
  }
}
</script>

<style scoped>
.detail-container {
  background-color: #f5f5f5;
  min-height: 100vh;
  box-sizing: border-box;
  width: 100%;
  padding: 0;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400rpx;
  color: #999;
  font-size: 28rpx;
}

.detail-content {
  background-color: #fff;
  border-radius: 0;
  padding: 20rpx;
  margin: 0;
  min-height: 100vh;
  box-sizing: border-box;
  width: 100%;
}

.detail-section {
  margin-bottom: 40rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.info-grid {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20rpx;
}

.info-item {
  width: 50%;
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.info-value {
  font-size: 28rpx;
  color: #333;
}

.platform-tag {
  color: #007aff;
  border-radius: 15rpx;
  display: inline-block;
}

.content-block {
  margin-bottom: 20rpx;
}

.content-text {
  font-size: 28rpx;
  color: #333;
  line-height: 44rpx;
  white-space: pre-wrap;
  user-select: text;
}

.content-text-topic-box{
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}

.content-text-topic{
  font-size: 28rpx;
  color: #007aff;
  line-height: 44rpx;
  white-space: pre-wrap;
  user-select: text;
  margin: 0 10rpx;
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
  background-color: rgba(0, 122, 255, 0.1);

}

.link-text {
  color: #007aff;
  text-decoration: underline;
  overflow-wrap: break-word;
  user-select: text;
}

.stats-grid {
  display: flex;
  flex-wrap: wrap;
}

.stat-item {
  width: 33.33%;
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.stat-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.trend-up {
  color: #07c160;
}

.trend-down {
  color: #ee0a24;
}
</style>
