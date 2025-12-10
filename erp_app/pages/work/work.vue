<template>
  <view class="work-container">
    <!-- 筛选栏 -->
    <view class="filter-bar">
      <!-- 时间范围筛选 -->
      <view class="filter-item">
        <text class="filter-label">发布时间：</text>
        <picker class="filter-picker" @change="handleTimeRangeChange" :range="timeRangeOptions" :range-key="'label'">
          <view class="picker-text">{{ selectedTimeRange.label }}</view>
        </picker>
      </view>

      <!-- 折叠/展开按钮 -->
      <view class="filter-item toggle-btn" @click="toggleFilter">
        <text class="toggle-text">{{ showAllFilters ? '收起筛选' : '更多筛选' }}</text>
        <text class="toggle-icon">{{ showAllFilters ? '▲' : '▼' }}</text>
      </view>

      <!-- 其他筛选条件（默认隐藏） -->
      <view v-if="showAllFilters" class="extra-filters">
        <!-- 排序字段筛选 -->
        <view class="filter-item">
          <text class="filter-label">排序字段：</text>
          <picker class="filter-picker" @change="handleSortFieldChange" :range="sortFieldOptions" :range-key="'label'">
            <view class="picker-text">{{ selectedSortField.label }}</view>
          </picker>
        </view>

        <!-- 社交平台筛选 -->
        <view class="filter-item">
          <text class="filter-label">社交平台：</text>
          <picker class="filter-picker" @change="handlePlatformChange" :range="platformOptions" :range-key="'label'">
            <view class="picker-text">{{ selectedPlatform.label }}</view>
          </picker>
        </view>

        <!-- 作品描述搜索 -->
        <view class="filter-item search-item">
          <text class="filter-label">作品描述：</text>
          <input 
            class="search-input" 
            type="text" 
            placeholder="请输入作品描述关键词" 
            v-model="descriptionKeyword"
            @confirm="handleDescriptionSearch"
          />
          <button class="search-btn" @click="handleDescriptionSearch">搜索</button>
        </view>
      </view>


    </view>

    <!-- 作品列表 -->
    <scroll-view
        class="work-list"
        @scrolltolower="handleLoadMore"
        scroll-y="true"
        refresher-enabled="true"
        :refresher-triggered="refreshing"
        @refresherrefresh="onRefresh"
        lower-threshold="60"
        refresher-background="#f5f5f5"
    >
      <view class="work-item" v-for="item in workList" :key="item.id" @click="navigateToDetail(item.id)">
        <view class="work-header">
          <text class="work-title">{{ item.description }}</text>
          <text class="work-platform">{{ item.platformId_dictText }}</text>
        </view>
        <view class="work-stats">
          <text class="stat-item">播放量：{{ item.playNum || 0 }}</text>
          <text class="stat-item">点赞数：{{ item.thumbNum || 0 }}</text>
          <text class="stat-item">评论数：{{ item.commentNum || 0 }}</text>
          <text class="stat-item">点赞增长量：{{ item.thumbNumUp || 0 }}
            <text class="trend" :class="{ 'trend-up': item.thumbNumChange > 0, 'trend-down': item.thumbNumChange < 0 }">
              <text v-if="item.thumbNumChange > 0">↑{{ Math.abs(item.thumbNumChange) || 0 }}</text>
              <text v-else-if="item.thumbNumChange < 0">↓{{ Math.abs(item.thumbNumChange) || 0 }}</text>
            </text>
          </text>
          <text class="stat-item">播放增长量：{{ item.playNumUp || 0 }}
            <text class="trend" :class="{ 'trend-up': item.playNumChange > 0, 'trend-down': item.playNumChange < 0 }">
              <text v-if="item.playNumChange > 0">↑{{ Math.abs(item.playNumChange) || 0 }}</text>
              <text v-else-if="item.playNumChange < 0">↓{{ Math.abs(item.playNumChange) || 0 }}</text>
            </text>
          </text>
        </view>
        <view class="work-info">
          <text class="info-item">作者：{{ item.accountId_dictText }}</text>
          <text class="info-item">发布时间：{{ item.postTime }}</text>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="workList.length === 0 && !loading">
        <text>暂无作品数据</text>
      </view>

      <!-- 加载提示 -->
      <view class="loading-more" v-if="loading">
        <text>加载中...</text>
      </view>

      <!-- 已加载全部 -->
      <view class="load-all" v-if="workList.length > 0 && currentPage >= totalPages">
        <text>已加载全部数据</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import {ref, onMounted} from 'vue'
import {getWorkListApi} from '../../api/work.js'

export default {
  setup() {
    // 作品列表数据
    const workList = ref([])
    // 当前页码
    const currentPage = ref(1)
    // 每页条数
    const pageSize = ref(10)
    // 总页数
    const totalPages = ref(0)
    // 总条数
    const totalCount = ref(0)
    // 加载状态
    const loading = ref(false)
    // 刷新状态
    const refreshing = ref(false)
    // 筛选栏展开状态
    const showAllFilters = ref(false)

    // 时间范围选项
    const timeRangeOptions = ref([
      {label: '全部', value: null},
      {label: '近7天', value: 7},
      {label: '近15天', value: 15},
      {label: '1个月', value: 30},
      {label: '2个月', value: 60},
      {label: '3个月', value: 90},
      {label: '近半年', value: 180}
    ])

    // 选中的时间范围
    const selectedTimeRange = ref(timeRangeOptions.value[0])
    // 开始日期
    const startDate = ref('')
    // 结束日期
    const endDate = ref('')

    // 排序字段选项 - 包含升序和降序
    const sortFieldOptions = ref([

      {label: '创建时间-升序', value: 'createTime', order: 'asc'},
      {label: '创建时间-降序', value: 'createTime', order: 'desc'},
      {label: '发布时间-升序', value: 'postTime', order: 'asc'},
      {label: '发布时间-降序', value: 'postTime', order: 'desc'},
      {label: '点赞数-升序', value: 'thumbNum', order: 'asc'},
      {label: '点赞数-降序', value: 'thumbNum', order: 'desc'},
      {label: '点赞增长量-升序', value: 'thumbNumUp', order: 'asc'},
      {label: '点赞增长量-降序', value: 'thumbNumUp', order: 'desc'},
      {label: '播放量-升序', value: 'playNum', order: 'asc'},
      {label: '播放量-降序', value: 'playNum', order: 'desc'},
      {label: '播放增长量-升序', value: 'playNumUp', order: 'asc'},
      {label: '播放增长量-降序', value: 'playNumUp', order: 'desc'},
      {label: '收藏数-升序', value: 'collectNum', order: 'asc'},
      {label: '收藏数-降序', value: 'collectNum', order: 'desc'},
      {label: '评论数-升序', value: 'commentNum', order: 'asc'},
      {label: '评论数-降序', value: 'commentNum', order: 'desc'},
      {label: '分享数-升序', value: 'shareNum', order: 'asc'},
      {label: '分享数-降序', value: 'shareNum', order: 'desc'}
    ])

    // 选中的排序字段
    const selectedSortField = ref(sortFieldOptions.value[1]) // 默认选择创建时间降序

    // 社交平台选项
    const platformOptions = ref([
      {label: '全部', value: null},
      {label: '抖音', value: 'douyin'},
      {label: '小红书', value: 'xiaohongshu'},
      {label: '视频号', value: 'wechatvideo'},
      {label: 'B站', value: 'bilibili'}
    ])

    // 选中的社交平台
    const selectedPlatform = ref(platformOptions.value[0])

    // 作品描述搜索关键词
    const descriptionKeyword = ref('')

    // 计算时间范围的开始和结束日期
    const calculateDateRange = (days) => {
      // 如果是"全部"选项，返回null
      if (days === null) {
        return {
          startDate: null,
          endDate: null
        }
      }
      
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - days)
      
      // 格式化日期为YYYY-MM-DD HH:mm:ss
      const formatDate = (date) => {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      }
      
      return {
        startDate: formatDate(start),
        endDate: formatDate(end)
      }
    }

    // 获取作品列表
    const getWorkList = async (isLoadMore = false) => {
      // 如果已经加载到最后一页，不再加载
      if (isLoadMore && currentPage.value >= totalPages.value) {
        return
      }

      try {
        loading.value = true

        // 计算时间范围
        const dateRange = calculateDateRange(selectedTimeRange.value.value)
        startDate.value = dateRange.startDate
        endDate.value = dateRange.endDate
        
        // 构造请求参数
        const params = {
          pageNum: currentPage.value,
          pageSize: pageSize.value
        }

        // 添加作品描述模糊搜索参数
        if (descriptionKeyword.value) {
          params.description = descriptionKeyword.value
        }

        // 添加社交平台筛选参数
        if (selectedPlatform.value && selectedPlatform.value.value) {
          params.platformId = selectedPlatform.value.value
        }
        
        // 仅当时间范围不是"全部"时，才添加时间参数
        if (dateRange.startDate && dateRange.endDate) {
          params.startPostTime = startDate.value
          params.endPostTime = endDate.value
        }
        if (selectedSortField.value) {
          if (selectedSortField.value.order === 'desc') {
            params.descFields = selectedSortField.value.value
          } else {
            params.ascFields = selectedSortField.value.value
          }
        }

        // 调用API获取作品列表
        const res = await getWorkListApi(params)

        // 如果是加载更多，追加数据；否则替换数据
        if (isLoadMore) {
          workList.value = workList.value.concat(res.records || [])
        } else {
          workList.value = res.records || []
        }

        totalCount.value = res.total || 0
        totalPages.value = Math.ceil(totalCount.value / pageSize.value)
      } catch (error) {
        console.error('获取作品列表失败', error)
      } finally {
        loading.value = false
      }
    }

    // 时间范围变化处理
    const handleTimeRangeChange = (e) => {
      const index = e.detail.value
      selectedTimeRange.value = timeRangeOptions.value[index]
      // 选择后立即搜索
      currentPage.value = 1
      getWorkList()
    }

    // 社交平台变化处理
    const handlePlatformChange = (e) => {
      const index = e.detail.value
      selectedPlatform.value = platformOptions.value[index]
      // 选择后立即搜索
      currentPage.value = 1
      getWorkList()
    }

    // 作品描述搜索处理
    const handleDescriptionSearch = () => {
      // 搜索时重置页码
      currentPage.value = 1
      getWorkList()
    }

    // 折叠/展开筛选栏
    const toggleFilter = () => {
      showAllFilters.value = !showAllFilters.value
    }

    // 排序字段变化处理
    const handleSortFieldChange = (e) => {
      const index = e.detail.value
      selectedSortField.value = sortFieldOptions.value[index]
      // 选择后立即搜索
      currentPage.value = 1
      getWorkList()
    }


    // 滚动加载更多
    const handleLoadMore = () => {
      if (!loading.value && currentPage.value < totalPages.value) {
        currentPage.value++
        getWorkList(true)
      }
    }

    // 下拉刷新
    const onRefresh = async () => {
      refreshing.value = true
      try {
        // 重置页码
        currentPage.value = 1
        // 重新获取数据
        await getWorkList()
      } catch (error) {
        console.error('刷新数据失败', error)
      } finally {
        refreshing.value = false
      }
    }

    // 跳转到详情页面
    const navigateToDetail = (id) => {
      uni.navigateTo({
        url: `/pages/work/work-detail?id=${id}`
      })
    }

    // 页面加载时获取数据
    onMounted(() => {
      getWorkList()
    })

    return {
      workList,
      currentPage,
      pageSize,
      totalPages,
      totalCount,
      loading,
      refreshing,
      showAllFilters,
      timeRangeOptions,
      selectedTimeRange,
      sortFieldOptions,
      selectedSortField,
      platformOptions,
      selectedPlatform,
      descriptionKeyword,
      handleTimeRangeChange,
      handleSortFieldChange,
      handlePlatformChange,
      handleDescriptionSearch,
      toggleFilter,
      handleLoadMore,
      onRefresh,
      navigateToDetail
    }
  }
}
</script>

<style scoped>
.work-container {
  padding: 20rpx;
  background-color: #fff;
}

.filter-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 20rpx;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.extra-filters {
  width: 100%;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-top: 15rpx;
}

.toggle-btn {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #007aff;
  font-size: 28rpx;
}

.toggle-text {
  margin-right: 8rpx;
}

.toggle-icon {
  font-size: 24rpx;
  transition: transform 0.3s;
}

.filter-item {
  display: flex;
  align-items: center;
  margin-right: 30rpx;
  margin-bottom: 15rpx;
}

.search-item {
  width: 100%;
  margin-right: 0;
}

.search-input {
  flex: 1;
  height: 60rpx;
  padding: 0 20rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 30rpx;
  font-size: 28rpx;
  background-color: #f5f5f5;
}

.search-btn {
  margin-left: 20rpx;
  height: 60rpx;
  padding: 0 30rpx;
  background-color: #007aff;
  color: #fff;
  font-size: 24rpx;
  border-radius: 30rpx;
  border: none;
}

.filter-label {
  font-size: 28rpx;
  color: #666;
  margin-right: 10rpx;
}

.filter-picker {
  padding: 10rpx 20rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #333;
}

.picker-text {
  font-size: 28rpx;
  color: #333;
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
}

.work-list {
  padding-top: 30rpx;
  margin-bottom: 20rpx;
  height: calc(90vh - 100rpx); /* 设置固定高度以便滚动 */
  overflow-y: auto;
  background-color: #f5f5f5;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.work-item {
  padding: 20rpx;
  margin: 0 20rpx 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.work-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
  padding-bottom: 15rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.work-title {
  height: 44rpx;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 10rpx;
}

.work-platform {
  font-size: 24rpx;
  color: #007aff;
  background-color: rgba(0, 122, 255, 0.1);
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
}

.work-stats {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 10rpx;
}

.stat-item {
  font-size: 26rpx;
  color: #666;
  margin-right: 30rpx;
  margin-bottom: 10rpx;
}

.trend {
  margin-left: 5rpx;
}

.trend-up {
  color: #07c160;
}

.trend-down {
  color: #ee0a24;
}

.work-info {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.info-item {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
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
</style>