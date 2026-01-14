<template>
  <view class="account-detail-container">
    <Watermark text="仅供分析不得传播©lumengshop.com"/>


    <!-- 账号统计信息 -->
    <view class="account-stats">
      <view class="stat-item">
        <text class="stat-value">{{ accountStats.playNum || 0 }}</text>
        <text class="stat-label">播放量</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ accountStats.thumbNum || 0 }}</text>
        <text class="stat-label">点赞量</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ accountStats.commentNum || 0 }}</text>
        <text class="stat-label">评论数</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ accountStats.shareNum || 0 }}</text>
        <text class="stat-label">分享数</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ accountStats.collectNum || 0 }}</text>
        <text class="stat-label">收藏数</text>
      </view>
    </view>

    <!-- 作品列表区域 -->
    <WorkList v-if="accountId" :accountId="accountId"/>
  </view>
</template>

<script>
import {ref, onMounted} from 'vue'
import WorkList from '../work/work.vue'
import Watermark from '../../components/Watermark.vue'
import {getAccountStatistic} from '../../api/dashboard'

export default {
  name: 'AccountDetail',
  components: {
    WorkList,
    Watermark
  },
  setup() {
    // 账号ID
    const accountId = ref('')
    // 账号昵称
    const accountNickname = ref('')
    // 账号统计信息
    const accountStats = ref({
      playNum: 0,
      thumbNum: 0,
      commentNum: 0,
      shareNum: 0,
      forwardNum: 0
    })
    // 加载状态
    const loading = ref(false)

    // 获取账号详情
    const getAccountDetail = async () => {
      try {
        loading.value = true
        let params = {
          id: accountId.value,
          pageNum: 1,
          pageSize: 10
        }
        const res = await getAccountStatistic(params)
        let arr = res.records || []
        if (arr.length > 0) {
          // 更新统计信息
          let obj = arr[0]
          accountStats.value = {
            playNum: obj.playNum || 0,
            thumbNum: obj.thumbNum || 0,
            commentNum: obj.commentNum || 0,
            shareNum: obj.shareNum || 0,
            collectNum: obj.collectNum || 0
          }
        }
      } catch (error) {
        console.error('获取账号详情失败', error)
      } finally {
        loading.value = false
      }
    }

    // 页面加载时获取参数和详情
    onMounted(() => {
      // 获取路由参数
      const pages = getCurrentPages()
      const currentPage = pages[pages.length - 1]
      accountId.value = currentPage.options.id || ''
      accountNickname.value = decodeURIComponent(currentPage.options.nickname || '')
      
      // 设置导航栏标题为账号昵称
      if (accountNickname.value) {
        uni.setNavigationBarTitle({
          title: accountNickname.value
        })
      }
      
      // 获取账号详情
      if (accountId.value) {
        getAccountDetail()
      }
    })

    return {
      accountId,
      accountNickname,
      accountStats
    }
  }
}
</script>

<style scoped>
.account-detail-container {
  width: 100%;
  height: 100vh;
  background-color: #fff;
}

.back-button {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.back-icon {
  font-size: 36rpx;
  margin-right: 10rpx;
  color: #333;
}

.back-text {
  font-size: 28rpx;
  color: #333;
}

.account-stats {
  display: flex;
  justify-content: space-around;
  padding: 30rpx 0;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 5rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #666;
}
</style>