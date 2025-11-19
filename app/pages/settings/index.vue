<template>
  <view class="container">
    <!-- 设置列表 -->
    <view class="settings-list">
      <!-- 头像设置 -->
      <view class="setting-item" @click="changeAvatar">
        <text class="setting-label">头像</text>
        <view class="setting-value">
          <image class="avatar-preview" :src="userInfo.avatar" mode="aspectFill"></image>
          <text class="arrow">></text>
        </view>
      </view>

      <!-- 昵称设置 -->
      <view class="setting-item" @click="changeNickname">
        <text class="setting-label">昵称</text>
        <view class="setting-value">
          <text class="value-text">{{ userInfo.name || userInfo.nickname}}</text>
          <text class="arrow">></text>
        </view>
      </view>

      <!-- 手机号设置 -->
      <view class="setting-item" @click="changePhone">
        <text class="setting-label">手机号</text>
        <view class="setting-value">
          <text class="value-text">{{ formatPhone(userInfo.phone) }}</text>
          <text class="arrow">></text>
        </view>
      </view>

      <!-- 用户协议 -->
      <view class="setting-item" @click="viewUserAgreement">
        <text class="setting-label">用户协议</text>
        <view class="setting-value">
          <text class="arrow">></text>
        </view>
      </view>
    </view>

   

    <!-- 昵称修改弹窗 -->
    <view v-if="showNicknamePopup" class="popup-mask" @click="cancelNicknameEdit">
      <view class="nickname-popup" @click.stop>
        <view class="popup-title">修改昵称</view>
        <input 
          class="nickname-input" 
          v-model="newNickname" 
          placeholder="请输入新昵称"
          maxlength="20"
        />
        <view class="popup-buttons">
          <button class="cancel-btn" @click="cancelNicknameEdit">取消</button>
          <button class="confirm-btn" @click="confirmNicknameEdit" :loading="updateLoading">确认</button>
        </view>
      </view>
    </view>

    <!-- 手机号修改弹窗 -->
    <view v-if="showPhonePopup" class="popup-mask" @click="cancelPhoneEdit">
      <view class="phone-popup" @click.stop>
        <view class="popup-title">修改手机号</view>
        <input 
          class="phone-input" 
          v-model="newPhone" 
          placeholder="请输入新手机号"
          type="number"
          maxlength="11"
        />
        <view class="popup-buttons">
          <button class="cancel-btn" @click="cancelPhoneEdit">取消</button>
          <button class="confirm-btn" @click="confirmPhoneEdit" :loading="updateLoading">确认</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { updateUserInfo } from '@/api/user.js'
import { selectAndUploadAvatar } from '@/utils/upload.js'
import authManager from '@/utils/auth.js'
import { getApiConfig } from '@/config/index.js';  
const apiConfig = getApiConfig();
const BASE_URL = apiConfig.baseURL;
export default {
  data() {
    return {
      userInfo: {
        name: '',
        avatar: '/static/logo.png',
        phone: ''
      },
      newNickname: '',
      newPhone: '',
      updateLoading: false,
      logoutLoading: false,
      showNicknamePopup: false,
      showPhonePopup: false
    }
  },
  
  onLoad(options) {
    // 从上一页传递的用户信息
    if (options.userInfo) {
      try {
        this.userInfo = JSON.parse(decodeURIComponent(options.userInfo));
        console.log('接收到的用户信息:', this.userInfo);
      } catch (error) {
        console.error('解析用户信息失败:', error);
        // 如果解析失败，从本地存储获取
        this.getUserInfoFromStorage();
      }
    } else {
      // 如果没有传递参数，从本地存储获取
      this.getUserInfoFromStorage();
    }
  },

  onUnload() {
    // 页面卸载时，通知上一页刷新用户信息
    const pages = getCurrentPages();
    if (pages.length > 0) {
      const prevPage = pages[pages.length - 1];
      if (prevPage && prevPage.route === 'pages/profile/index') {
        // 触发上一页重新加载用户信息
        prevPage.$vm.loadUserInfo && prevPage.$vm.loadUserInfo();
      }
    }
  },

  methods: {
    /**
     * 从本地存储获取用户信息
     */
    getUserInfoFromStorage() {
      const storedUserInfo = authManager.getUserInfo();
      if (storedUserInfo) {
        this.userInfo = {
          name: storedUserInfo.nickname || storedUserInfo.name || '未知',
          avatar: storedUserInfo.avatar || '/static/logo.png',
          phone: storedUserInfo.phone || ''
        };
      }
    },

    // 格式化手机号显示
    formatPhone(phone) {
      if (!phone) return '未绑定';
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    },



    /**
     * 更换头像
     */
    async changeAvatar() {
      try {
        console.log('选择并上传头像');
        // 选择并上传头像
        const uploadResult = await selectAndUploadAvatar();

        // 获取上传后的图片URL - 适配不同的返回格式
        let avatarUrl = BASE_URL +'/'+ uploadResult.data.url;
        
        
        
        if (!avatarUrl) {
          console.error('上传结果:', uploadResult);
          throw new Error('上传接口返回的图片地址为空');
        }

        // 更新本地头像
        this.userInfo.avatar = avatarUrl;

        // 更新用户信息到服务器
        await this.updateUserProfile({
          id: this.userInfo.id,
          avatar: avatarUrl,
          nickname: this.userInfo.nickname
        });

        uni.showToast({
          title: '头像更新成功',
          icon: 'success'
        });
      } catch (error) {
        console.error('更换头像失败:', error);
        
        let errorMessage = '头像更新失败';
        if (error.message) {
          errorMessage = error.message;
        }
        
        uni.showToast({
          title: errorMessage,
          icon: 'none'
        });
      }
    },

    /**
     * 修改昵称
     */
    changeNickname() {
      this.newNickname = this.userInfo.name;
      this.showNicknamePopup = true;
    },

    /**
     * 修改手机号
     */
    changePhone() {
      this.newPhone = this.userInfo.phone || '';
      this.showPhonePopup = true;
    },

    /**
     * 取消昵称修改
     */
    cancelNicknameEdit() {
      this.newNickname = '';
      this.showNicknamePopup = false;
    },

    /**
     * 取消手机号修改
     */
    cancelPhoneEdit() {
      this.newPhone = '';
      this.showPhonePopup = false;
    },

    /**
     * 确认昵称修改
     */
    async confirmNicknameEdit() {
      if (!this.newNickname.trim()) {
        uni.showToast({
          title: '昵称不能为空',
          icon: 'none'
        });
        return;
      }

      try {
        this.updateLoading = true;
        
        await this.updateUserProfile({
          id: this.userInfo.id,
          avatar: this.userInfo.avatar,
          nickname: this.newNickname.trim()
        });

        this.userInfo.name = this.newNickname.trim();
        this.showNicknamePopup = false;
        
        uni.showToast({
          title: '昵称更新成功',
          icon: 'success'
        });
      } catch (error) {
        console.error('更新昵称失败:', error);
        uni.showToast({
          title: '昵称更新失败',
          icon: 'none'
        });
      } finally {
        this.updateLoading = false;
      }
    },

    /**
     * 确认手机号修改
     */
    async confirmPhoneEdit() {
      if (!this.newPhone.trim()) {
        uni.showToast({
          title: '手机号不能为空',
          icon: 'none'
        });
        return;
      }

      // 验证手机号格式
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.newPhone.trim())) {
        uni.showToast({
          title: '请输入正确的手机号',
          icon: 'none'
        });
        return;
      }

      try {
        this.updateLoading = true;
        
        await this.updateUserProfile({
          id: this.userInfo.id,
          avatar: this.userInfo.avatar,
          nickname: this.userInfo.nickname,
          phone: this.newPhone.trim()
        });

        this.userInfo.phone = this.newPhone.trim();
        this.showPhonePopup = false;
        
        uni.showToast({
          title: '手机号更新成功',
          icon: 'success'
        });
      } catch (error) {
        console.error('更新手机号失败:', error);
        uni.showToast({
          title: '手机号更新失败',
          icon: 'none'
        });
      } finally {
        this.updateLoading = false;
      }
    },

    /**
     * 更新用户资料
     */
    async updateUserProfile(data) {
      try {
        const response = await updateUserInfo(data);
        
        // 适配不同的响应格式
        if (response && (response.code === 200 || response.success || response.status === 'success')) {
          console.log('用户信息更新成功');
          
          // 通知上一页更新用户信息
          const pages = getCurrentPages();
          if (pages.length > 1) {
            const prevPage = pages[pages.length - 2];
            if (prevPage && prevPage.route === 'pages/profile/index') {
              // 更新上一页的用户信息
              if (data.nickname) {
                prevPage.$vm.userInfo.name = data.nickname;
              }
              if (data.avatar) {
                prevPage.$vm.userInfo.avatar = data.avatar;
              }
              if (data.phone) {
                prevPage.$vm.userInfo.phone = data.phone;
              }
            }
          }
          
          return response;
        } else {
          throw new Error(response.message || response.msg || '更新失败');
        }
      } catch (error) {
        console.error('更新用户信息失败:', error);
        
        // 根据错误类型显示不同提示
        let errorMessage = '更新失败';
        if (error.code === 401) {
          errorMessage = '登录已过期，请重新登录';
        } else if (error.code === 403) {
          errorMessage = '没有权限修改用户信息';
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        throw new Error(errorMessage);
      }
    },

    /**
     * 查看用户协议
     */
    viewUserAgreement() {
      uni.navigateTo({
        url: '/pages/agreement/index'
      });
    },

    /**
     * 退出登录
     */
    logout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              this.logoutLoading = true;
              
              // 清除本地登录状态
              authManager.clearLoginState();
              
              // 跳转到首页并刷新应用状态
              uni.reLaunch({
                url: '/pages/loading/loading'
              });
              
              uni.showToast({
                title: '已退出登录',
                icon: 'success'
              });
            } catch (error) {
              console.error('退出登录失败:', error);
              uni.showToast({
                title: '退出失败',
                icon: 'none'
              });
            } finally {
              this.logoutLoading = false;
            }
          }
        }
      });
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 设置列表 */
.settings-list {
  background: #fff;
  margin-top: 0;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx 40rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-item:active {
  background: #f8f8f8;
}

.setting-label {
  font-size: 30rpx;
  color: #333;
}

.setting-value {
  display: flex;
  align-items: center;
}

.value-text {
  font-size: 28rpx;
  color: #666;
  margin-right: 20rpx;
}

.arrow {
  font-size: 24rpx;
  color: #ccc;
}

.avatar-preview {
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
  margin-right: 20rpx;
}

/* 退出登录 */
.logout-section {
 position: absolute;
 bottom: 0;
width: 100%;
}

.logout-btn {
  width: 100%;
  height: 88rpx;
  background: #fff;
  color: #FF6060;
  font-size: 32rpx;
  
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-btn:active {
  background: #ff3742;
}

/* 修改弹窗通用样式 */
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.nickname-popup, .phone-popup {
  width: 600rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  margin: 0 40rpx;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 40rpx;
}

.nickname-input, .phone-input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  margin-bottom: 40rpx;
}

.nickname-input:focus, .phone-input:focus {
  border-color: #007aff;
}

.popup-buttons {
  display: flex;
  justify-content: space-between;
}

.cancel-btn, .confirm-btn {
  width: 45%;
  height: 70rpx;
  border-radius: 10rpx;
  font-size: 28rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cancel-btn {
  background: #f0f0f0;
  color: #666;
}

.confirm-btn {
  background: #83BB0B;
  color: #fff;
}

.cancel-btn:active {
  background: #e0e0e0;
}

.confirm-btn:active {
  background: #83BB0B
}
</style>