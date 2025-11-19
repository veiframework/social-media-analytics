<template>
  <view class="container">
    <!-- 页面标题 -->
    

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-loading></uni-loading>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 报名信息列表 -->
     
      <view v-if="registrationList.length === 0" class="empty-state">
        <image class="empty-icon" src="/static/registration/registration_no_data.png" mode="aspectFit"></image>
        <text class="empty-text">暂无信息</text>
      
      </view>

      <view v-else>
        <view 
          v-for="(item, index) in registrationList" 
          :key="item.id"
          class="registration-item"
          :class="{ 'default-item': item.isDefault === '1' }"
        >
          <!-- 默认标签 -->
          <view v-if="item.isDefault === '1'" class="default-tag">
            <text class="default-text">默认</text>
          </view>

          <!-- 选手信息 -->
          <view class="info-section">
             
            <view class="info-row">
              <text class="info-label">选手姓名：</text>
              <text class="info-value">{{ item.childName }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">监护人姓名：</text>
              <text class="info-value">{{ item.guardianName }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">监护人手机号：</text>
              <text class="info-value">{{ item.guardianPhone }}</text>
            </view>
          </view>

         

          <!-- 操作按钮 -->
          <view class="action-buttons">
            <view class="action-btn edit-btn" @click="editRegistrationInfo(item)">
              <text class="btn-text">编辑</text>
            </view>
            <!-- <view 
              v-if="item.isDefault !== '1'"
              class="action-btn default-btn" 
              @click="setAsDefault(item.id)"
            >
              <text class="btn-text">设为默认</text>
            </view> -->
            <!-- <view class="action-btn delete-btn" @click="deleteRegistrationInfo(item.id, index)">
              <text class="btn-text">删除</text>
            </view> -->
          </view>
        </view>
      </view>
    </view>

    <!-- 底部新建按钮 -->
    <view class="bottom-add-btn" @click="addRegistrationInfo">
      <text class="bottom-add-text">新建报名信息</text>
    </view>
  
</template>

<script>
import { getRegistrationInfoList, deleteRegistrationInfo, setDefaultRegistrationInfo } from '@/api/user.js'

export default {
  data() {
    return {
      loading: false,
      registrationList: []
    }
  },
  
  async onShow() {
    await this.loadRegistrationList();
  },
  
  onPullDownRefresh() {
    this.loadRegistrationList().then(() => {
      uni.stopPullDownRefresh();
    });
  },
  
  methods: {
    /**
     * 加载报名信息列表
     */
    async loadRegistrationList() {
      try {
        this.loading = true;
        const response = await getRegistrationInfoList();
      
          this.registrationList = response;
      
      } catch (error) {
        console.error('获取报名信息失败:', error);
        uni.showToast({
          title: error.message || '获取报名信息失败',
          icon: 'none',
          duration: 2000
        });
      } finally {
        this.loading = false;
      }
    },

    /**
     * 添加报名信息
     */
    addRegistrationInfo() {
      uni.navigateTo({
        url: '/pages/registration/add'
      });
    },

    /**
     * 编辑报名信息
     */
    editRegistrationInfo(item) {
      const itemStr = encodeURIComponent(JSON.stringify(item));
      uni.navigateTo({
        url: `/pages/registration/add?item=${itemStr}`
      });
    },

    /**
     * 设为默认
     */
    async setAsDefault(id) {
      try {
        uni.showLoading({
          title: '设置中...'
        });
        
        const response = await setDefaultRegistrationInfo(id);
        
        if (response && response.code === 200) {
          uni.showToast({
            title: '设置成功',
            icon: 'success'
          });
          await this.loadRegistrationList();
        } else {
          throw new Error(response.message || '设置失败');
        }
      } catch (error) {
        console.error('设置默认失败:', error);
        uni.showToast({
          title: error.message || '设置失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },

    /**
     * 删除报名信息
     */
    deleteRegistrationInfo(id, index) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条报名信息吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              uni.showLoading({
                title: '删除中...'
              });
              
              const response = await deleteRegistrationInfo(id);
              
              if (response && response.code === 200) {
                uni.showToast({
                  title: '删除成功',
                  icon: 'success'
                });
                this.registrationList.splice(index, 1);
              } else {
                throw new Error(response.message || '删除失败');
              }
            } catch (error) {
              console.error('删除失败:', error);
              uni.showToast({
                title: error.message || '删除失败',
                icon: 'none'
              });
            } finally {
              uni.hideLoading();
            }
          }
        }
      });
    },

    /**
     * 格式化身份证号（脱敏处理）
     */
    formatIdCard(idCard) {
      if (!idCard) return '';
      if (idCard.length === 18) {
        return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2');
      }
      return idCard;
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40rpx 20rpx 200rpx 20rpx
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  padding: 0 20rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.add-btn {
  background: #83BB0B;
  padding: 16rpx 32rpx;
  border-radius: 40rpx;
}

.add-text {
  color: #fff;
  font-size: 28rpx;
}

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

.registration-list {
  padding: 0 20rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 20%;
    width: 100%;
}

.empty-icon {
  width: 375rpx;
  height: 400rpx;
  margin-bottom: 30rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 80rpx;
}

.empty-btn {
  background: #83BB0B;
  padding: 20rpx 40rpx;
  border-radius: 40rpx;
  width: 340rpx;
  text-align: center;
  height: 53rpx;
}

.empty-btn-text {
  color: #fff;
  font-size: 28rpx;
}

.registration-item {
  background: #fff;
  border-radius: 20rpx;
   
  margin-bottom: 32rpx;
  position: relative;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.default-item {
  border: 2rpx solid #83BB0B;
}

.default-tag {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  background: #83BB0B;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
}

.default-text {
  color: #fff;
  font-size: 24rpx;
}

.info-section {
  padding: 30rpx 30rpx 0 30rpx;
  
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  padding-bottom: 10rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.info-label {
  font-size: 28rpx;
  color: #666;
  
  flex-shrink: 0;
}

.info-value {
  font-size: 28rpx;
  color: #333;
  flex: 1;
  text-align: right;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
   
  padding: 10rpx 20rpx;
  border-top: 2rpx solid #f0f0f0;
}

.action-btn {
  padding: 16rpx 32rpx;
  border-radius: 40rpx;
  border: 2rpx solid;
}

.edit-btn {
  width: 108rpx;
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.20); 
  text-align: center;
}

.edit-btn .btn-text {
  color: #000;
 
}

.default-btn {
  background: #fff;
  border-color: #409EFF;
}

.default-btn .btn-text {
  color: #409EFF;
}

.delete-btn {
  background: #fff;
  border-color: #F56C6C;
}

.delete-btn .btn-text {
  color: #F56C6C;
}

.btn-text {
  font-size: 26rpx;
}

.bottom-add-btn {
  position: fixed;
  bottom: 60rpx;
  left: 40rpx;
  right: 40rpx;
  height: 100rpx;
  background: #83BB0B;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 20rpx rgba(131, 187, 11, 0.3);
  z-index: 999;
}

.bottom-add-text {
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
}
</style>