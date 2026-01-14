<template>
  <view class="account-container">
    <Watermark text="仅供分析不得传播©lumengshop.com"/>

    <!-- 搜索栏 -->
    <view class="search-bar">
      <input class="search-input" type="text" v-model="searchParams.nickname" placeholder="请输入昵称搜索"
             @confirm="handleSearch"/>
      <button class="search-btn" @tap="handleSearch">搜索</button>
      <button class="reset-btn" @tap="handleReset">重置</button>
    </view>

    <!-- 账号列表 -->
    <scroll-view
        class="account-list"
        @scrolltolower="handleLoadMore"
        scroll-y="true"
        :refresher-enabled="refreshing"
        :refresher-triggered="triggered"
        @refresherrefresh="onRefresh"
        lower-threshold="60"
        refresher-background="#f5f5f5"
        @refresherpulling="onPulling"
    >
      <view class="account-item" v-for="item in accountList" :key="item.id" @tap="navigateToAccountDetail(item.id, item.nickname)">
        <view class="account-header">
          <text class="account-name">{{ item.nickname }}</text>
          <text class="account-type" :style="platformStyle(item.platformId)">{{ item.platformId_dictText }}</text>
        </view>
        <view class="account-info">
          <text class="info-item">账号类型：{{ item.type_dictText }}</text>
          <view class="info-item">UID：{{ item.uid }}</view>
        </view>
        <view class="account-info">
          <view class="info-item">员工：{{ item.userId_dictText }}</view>
          <text class="info-item">创建时间：{{ item.createTime }}</text>
        </view>

      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="accountList.length === 0 && !loading">
        <text>暂无账号数据</text>
      </view>

      <!-- 加载提示 -->
      <view class="loading-more" v-if="loading">
        <text>加载中...</text>
      </view>

      <!-- 已加载全部 -->
      <view class="load-all" v-if="accountList.length > 0 && currentPage >= totalPages">
        <text>已加载全部数据</text>
      </view>
    </scroll-view>

    <!-- 自定义tabbar -->
    <bottom-bar></bottom-bar>
  </view>
</template>

<script>
import {ref, onMounted} from 'vue'
import {listSocialMediaAccount, syncWork, updateAutoSync} from '../../api/account.js'
import BottomBar from "../../components/BottomBar.vue";
import Watermark from "../../components/Watermark.vue";

export default {
  components: {
    Watermark,
    BottomBar

  },

  setup() {
    // 账号列表数据
    const accountList = ref([])
    // 当前页码
    const currentPage = ref(1)
    // 每页条数
    const pageSize = ref(10)
    // 总页数
    const totalPages = ref(0)
    // 总条数
    const totalCount = ref(0)
    // 搜索参数
    const searchParams = ref({
      nickname: '',
      userId: '',
      type: '',
      platformId: '',
      syncWorkStatus: ''
    })
    // 加载状态
    const loading = ref(false)
    // 刷新状态
    const refreshing = ref(true)
    const triggered = ref(false)

    const computeButton = ((item) => {
      if (item.syncWorkStatus === '2') {
        return true
      } else if (item.syncWorkStatus === '0') {
        return true
      } else {
        return false
      }
    })

    const computeStatus = ((item) => {
      if (item.syncWorkStatus === '0') {
        return {color: 'grey'}
      } else if (item.syncWorkStatus === '1') {
        return {color: '#cea156'}
      } else if (item.syncWorkStatus === '2') {
        return {color: 'green'}
      } else if (item.syncWorkStatus === '3') {
        return {color: 'red'}
      }
    })

    // 获取账号列表
    const getAccountList = async (isLoadMore = false) => {
      // 如果已经加载到最后一页，不再加载
      if (isLoadMore && currentPage.value >= totalPages.value) {
        return
      }

      try {
        loading.value = true
        const res = await listSocialMediaAccount(
            searchParams.value.userId,
            searchParams.value.type,
            searchParams.value.platformId,
            searchParams.value.syncWorkStatus,
            searchParams.value.nickname,
            currentPage.value,
            pageSize.value
        )

        // 如果是加载更多，追加数据；否则替换数据
        if (isLoadMore) {
          accountList.value = accountList.value.concat(res.records || [])
        } else {
          accountList.value = res.records || []
        }

        totalCount.value = res.total || 0
        totalPages.value = Math.ceil(totalCount.value / pageSize.value)
      } catch (error) {
        console.error('获取账号列表失败', error)
      } finally {
        loading.value = false
      }
    }

    // 搜索
    const handleSearch = () => {
      currentPage.value = 1
      getAccountList()
    }

    // 重置搜索条件
    const handleReset = () => {
      searchParams.value = {
        nickname: '',
        userId: '',
        type: '',
        platformId: '',
        syncWorkStatus: ''
      }
      currentPage.value = 1
      getAccountList()
    }

    // 滚动加载更多
    const handleLoadMore = () => {
      if (!loading.value && currentPage.value < totalPages.value) {
        currentPage.value++
        getAccountList(true)
      }
    }

    // 下拉刷新
    const onRefresh = async () => {
      refreshing.value = true
      try {
        // 重置页码
        currentPage.value = 1
        // 重新获取数据
        await getAccountList()
      } catch (error) {
        console.error('刷新数据失败', error)
      } finally {
        triggered.value = false;
      }
    }
    const onPulling = async (e) => {
      if (e.detail.deltaY < 0) {
        return
      }
      triggered.value = true
    }
    // 同步作品
    const handleSync = async (id) => {
      try {
        uni.showLoading({
          title: '开始同步...',
          mask: true
        });
        await syncWork(id)
        setTimeout(function () {
          uni.hideLoading();
          getAccountList()
        }, 3000);
        // 重新获取列表
      } catch (error) {
        console.error('同步失败', error)
        uni.hideLoading();
      }
    }

    // 切换自动同步
    const handleToggleAutoSync = async (item) => {
      try {
        await updateAutoSync(item.id, !item.autoSync)
        uni.showToast({
          title: item.autoSync ? '关闭自动同步成功' : '开启自动同步成功',
          icon: 'success'
        })
        // 更新本地数据
        item.autoSync = !item.autoSync
      } catch (error) {
        console.error('切换自动同步失败', error)
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

    // 跳转到账号详情页
    const navigateToAccountDetail = (id, nickname) => {
      uni.navigateTo({
        url: `/pages/account/account-detail?id=${id}&nickname=${encodeURIComponent(nickname)}`
      })
    }

    // 页面加载时获取数据
    onMounted(() => {
      getAccountList()
    })

    return {
      accountList,
      currentPage,
      pageSize,
      totalPages,
      totalCount,
      searchParams,
      loading,
      refreshing,
      triggered,
      handleSearch,
      handleReset,
      handleLoadMore,
      onRefresh,
      onPulling,
      handleSync,
      handleToggleAutoSync,
      computeStatus,
      computeButton,
      platformStyle,
      navigateToAccountDetail
    }
  }
}
</script>

<style scoped>
.account-container {
  background-color: #fff;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  position: fixed;
  z-index: 666;
  width: 100%;
}

.search-input {
  flex: 1;
  height: 80rpx;
  border: 1rpx solid #ddd;
  border-radius: 40rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  margin: 15rpx;
}

.search-btn, .reset-btn {
  height: 60rpx;
  padding: 0 30rpx;
  font-size: 24rpx;
  border-radius: 40rpx;
  margin-left: 15rpx;
  border: none;
}

.search-btn {
  background-color: #cea156;
  color: #fff;
}

.reset-btn::after {
  border: none;
}

.reset-btn {
  background-color: #fff;
  color: #666;
  border: 1rpx solid #ddd;
  margin-right: 15rpx;
}

.account-list {
  padding-top: 140rpx;
  margin-bottom: 20rpx;
  height: 80vh; /* 设置固定高度以便滚动 */
  overflow-y: auto;
  background-color: #f5f5f5;

  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.account-item {
  padding: 20rpx;
  margin: 0 20rpx 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.account-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
  padding-bottom: 15rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.account-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  width: 74%;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.account-type {
  font-size: 24rpx;
  color: #007aff;
  background-color: rgba(0, 122, 255, 0.1);
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
}

.account-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10rpx;
}

.info-item {
  width: 300rpx;
  font-size: 26rpx;
  color: #666;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.account-actions {
  margin-top: 20rpx;
}

.action-btn {
  font-size: 18rpx;
  border-radius: 35rpx;
}

.sync-btn {
  background-color: #cea156;
  color: #fff;
}

.auto-sync-btn {
  background-color: #cea156;
  color: #fff;
}

.empty-state {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}

.loading-more {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 28rpx;
}

.load-all {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 28rpx;
}

.account-number {
  width: 240rpx;
  text-overflow: ellipsis;
  overflow: hidden;
  text-align: right;
}

.account-number-left {
  text-align: right;
}
</style>