<template>
  <view class="container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-loading></uni-loading>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 空状态 -->
    <view v-else-if="!loading && photos.length === 0" class="empty-container">
      <image class="empty-image" src="/static/registration/registration_no_data.png" mode="aspectFit"></image>
      <text class="empty-text">暂无参赛照片</text>
      <text class="empty-tip">参加赛事后照片将在这里显示</text>
    </view>
    
    <!-- 照片列表 -->
    <view v-else class="photos-container">
      <view class="photos-grid">
        <view 
          v-for="(photo, index) in photos" 
          :key="photo.id || index"
          class="photo-item"
          @click="previewImage(photo, index)"
        >
          <image 
            :src="photo.imageUrl" 
            mode="aspectFill" 
            class="photo-image"
            :lazy-load="true"
            @error="handleImageError(index)"
          ></image>
          <view v-if="photo.competitionName" class="photo-info">
            <text class="competition-name">{{ photo.competitionName }}</text>
            <text class="upload-time">{{ photo.createTime }}</text>
          </view>
        </view>
      </view>
      
      <!-- 加载更多 -->
      <view v-if="hasMore" class="load-more" @click="loadMore">
        <text class="load-more-text">{{ loadingMore ? '加载中...' : '加载更多' }}</text>
      </view>
      
      <!-- 没有更多 -->
      <!-- <view v-else-if="photos.length > 0" class="no-more">
        <text class="no-more-text">没有更多照片了</text>
      </view> -->
    </view>
  </view>
</template>

<script>
import { getUserCompetitionImages } from '@/api/user.js'

export default {
  data() {
    return {
      photos: [],
      loading: false,
      loadingMore: false,
      hasMore: true,
      currentPage: 1,
      pageSize: 20
    }
  },
  
  async onLoad(options) {
    if(options && options.competitionId){
      this.competitionId = options.competitionId;
    }
    
    await this.loadPhotos();
  },
  
  onPullDownRefresh() {
    this.refreshPhotos().then(() => {
      uni.stopPullDownRefresh();
    });
  },
  
  onReachBottom() {
    if (this.hasMore && !this.loadingMore) {
      this.loadMore();
    }
  },
  
  methods: {
    /**
     * 加载参赛照片
     */
    async loadPhotos(isLoadMore = false) {
      try {
        if (isLoadMore) {
          this.loadingMore = true;
        } else {
          this.loading = true;
          this.currentPage = 1;
        }
        
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          imageType: 'PERSONAL'
        };
        if(this.competitionId){
          params.competitionId = this.competitionId;
          params.imageType = 'HIGHLIGHT';
        }
        
        const response = await getUserCompetitionImages(params);
        // 适配不同的响应格式
         
          // 支持不同的数据结构
          let newPhotos = response.records;
          
          
          if (isLoadMore) {
            this.photos = [...this.photos, ...newPhotos];
          } else {
            this.photos = newPhotos;
          }
          console.log('newPhotos',this.photos);
          // 判断是否还有更多数据
          this.hasMore = newPhotos.length >= this.pageSize;
          
          console.log('参赛照片加载成功:', this.photos);
        
      } catch (error) {
        console.error('获取参赛照片失败:', error);
        
        let errorMessage = '获取参赛照片失败';
        if (error.code === 401) {
          errorMessage = '登录已过期，请重新登录';
        } else if (error.code === 403) {
          errorMessage = '没有权限访问照片';
        } else if (error.message && error.message.includes('network')) {
          errorMessage = '网络连接失败，请检查网络';
        }
        
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 2000
        });
        
        // 如果是首次加载失败，设置空数据
        if (!isLoadMore) {
          this.photos = [];
          this.hasMore = false;
        }
      } finally {
        this.loading = false;
        this.loadingMore = false;
      }
    },
    
    /**
     * 刷新照片列表
     */
    async refreshPhotos() {
      this.currentPage = 1;
      this.hasMore = true;
      await this.loadPhotos(false);
    },
    
    /**
     * 加载更多照片
     */
    async loadMore() {
      if (!this.hasMore || this.loadingMore) return;
      
      this.currentPage++;
      await this.loadPhotos(true);
    },
    
    /**
     * 预览图片
     */
    previewImage(photo, index) {
      const urls = this.photos.map(p => p.imageUrl).filter(url => url);
      console.log('urls',urls);
      console.log('photo',photo);
      uni.previewImage({
        current: photo.imageUrl,
        urls: urls,
        indicator: 'number',
        loop: true
      });
    },
    
    /**
     * 图片加载失败处理
     */
    handleImageError(index) {
      console.warn('图片加载失败:', this.photos[index]);
      // 可以设置默认图片或移除该项
      // this.photos[index].imageUrl = '/static/logo.png';
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
        
        return `${year}-${month}-${day}`;
      } catch (error) {
        console.warn('时间格式化失败:', timeStr, error);
        return timeStr;
      }
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400rpx;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #999;
}

/* 空状态 */
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40rpx;
}

.empty-image {
  width: 300rpx;
  height: 300rpx;
  margin-bottom: 40rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.empty-tip {
  font-size: 28rpx;
  color: #999;
}

/* 照片列表 */
.photos-container {
  padding: 20rpx;
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.photo-item {
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 300rpx;
}

.photo-info {
  padding: 20rpx;
}

.competition-name {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  display: block;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upload-time {
  font-size: 24rpx;
  color: #999;
  display: block;
}

/* 加载更多 */
.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  margin-top: 20rpx;
}

.load-more-text {
  font-size: 28rpx;
  color: #83BB0B;
}

.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  margin-top: 20rpx;
}

.no-more-text {
  font-size: 28rpx;
  color: #999;
}

/* 响应式适配 */
@media (max-width: 750rpx) {
  .photos-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15rpx;
  }
  
  .photo-image {
    height: 250rpx;
  }
}

@media (min-width: 1000rpx) {
  .photos-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>