<template>
  <view class="content">
    <!-- 顶部用户信息区域 -->
    <view class="header">
      <view class="user-info">
        <image class="avatar" :src="user.avatar || '/static/avatar.png'" mode="aspectFill"></image>
        <view class="user-details">
          <text class="username">{{ user.nickName || '用户' }}</text>
          <text class="user-id">({{ roles }})</text>
        </view>
      </view>
    </view>

    <!-- 功能网格 -->
    <view class="function-grid">
      <view class="grid-item" v-for="item in functionItems" :key="item.id">
        <text class="grid-text">{{ item.text }}</text>
      </view>
    </view>

    <!-- 修改密码弹窗 -->
    <edit-password
        :visible="showPasswordModal"
        @close="showPasswordModal = false"
        @submit="handlePasswordSubmit"
    />
    <!-- 功能区域 -->
    <view class="main">
      <!-- 底部按钮 -->
      <view class="bottom-buttons">
        <button class="bind-btn" @tap="editPassword">修改密码</button>
        <button class="logout-btn" @tap="handleLogout">退出登录</button>
      </view>
    </view>
    
    <!-- 自定义tabbar -->
  </view>
  <bottom-bar></bottom-bar>

</template>

<script>
import {ref, onMounted, computed} from 'vue'
import store from "../../utils/store";
import {getInfo} from "../../api/login";
import editPassword from "../../components/editPassword.vue";
import BottomBar from "../../components/BottomBar.vue";

export default {
  components: {
    BottomBar,
    editPassword,
  },
  setup() {

    const user = ref({})

    // 控制修改密码弹窗显示
    const showPasswordModal = ref(false)

    const roles = computed(() => {
      try {
        return user.value.roles.map(i => i.roleName).join(',')
      } catch (e) {

      }
    })

    // 功能菜单数据
    const functionItems = ref([])

    onMounted(() => {
      loadUserInfo()
    })
    // 获取用户信息
    const loadUserInfo = async () => {
      try {
        let res = await getInfo();
        // 如果本地没有用户信息，则尝试重新获取
        store.setUserInfo(res.user)
        store.setRolesAndPermissions(res.roles, res.permissions)
        user.value = res.user
      } catch (error) {
        console.error('获取用户信息失败', error)
        store.logout()
        uni.redirectTo({url: '/pages/login/login'})
      }
    }

    // 绑定企业微信
    const handleBind = () => {
      uni.showToast({
        title: '绑定功能开发中',
        icon: 'none'
      })
    }

    // 打开修改密码弹窗
    const editPassword = async () => {
      showPasswordModal.value = true
    }

    // 处理密码修改提交
    const handlePasswordSubmit = (formData) => {
      console.log('密码修改提交:', formData)
      // 这里可以添加调用API的逻辑
      uni.showToast({
        title: '密码修改成功',
        icon: 'success'
      })
    }
    // 退出登录
    const handleLogout = async () => {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: async (res) => {
          if (res.confirm) {
            // 使用store的logout方法清除所有状态
            store.logout()
            // 清除记住的密码
            uni.removeStorageSync('username')
            uni.removeStorageSync('password')
            uni.removeStorageSync('rememberMe')
            // 跳转到登录页
            uni.redirectTo({url: '/pages/login/login'})
          }
        }
      })
    }

    return {
      user,
      roles,
      functionItems,
      handleBind,
      editPassword,
      handleLogout,
      showPasswordModal,
      handlePasswordSubmit
    }
  }
}
</script>

<style scoped>
.content {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 顶部用户信息区域 */
.header {
  background-color: #cea156;
  padding: 40rpx 30rpx 20rpx;
  color: white;
  border-radius: 0 0 0 120rpx;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background-color: white;
  margin-right: 20rpx;
}

.user-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.username {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.user-id {
  font-size: 28rpx;
  opacity: 0.8;
  margin-bottom: 8rpx;
}

.user-status {
  font-size: 24rpx;
  opacity: 0.7;
}

/* 标签区域 */
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.tag {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.2);
  padding: 10rpx 20rpx;
  border-radius: 8rpx;
}

.tag-text {
  font-size: 24rpx;
  font-weight: bold;
  margin-bottom: 4rpx;
}

.tag-value {
  font-size: 20rpx;
  opacity: 0.8;
}

/* 主内容区域 */
.main {
  padding: 30rpx;
  padding-bottom: 160rpx;
}

/* 功能网格 */
.function-grid {
  padding: 0 15rpx;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30rpx;
  margin-bottom: 40rpx;
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: white;
  padding: 30rpx 20rpx;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.grid-icon {
  width: 60rpx;
  height: 60rpx;
  margin-bottom: 16rpx;
}

.grid-text {
  font-size: 26rpx;
  color: #333;
}

/* 底部按钮 */
.bottom-buttons {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.bind-btn, .logout-btn {
  width: 100%;
  height: 80rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  line-height: 80rpx;
}

.bind-btn {
  background-color: white;
  color: #cea156;
  border: 2rpx solid #cea156;
}

.logout-btn {
  background-color: #cea156;
  color: white;
}
</style>
