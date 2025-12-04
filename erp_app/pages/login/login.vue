<template>
  <view class="login-container">
    <view class="login-form" v-if="!logged">
      <view class="title">欢迎登录</view>

      <view class="input-group">
        <view class="input-label">账号</view>
        <input class="input" type="text" v-model="loginForm.username" placeholder="请输入账号"/>
      </view>

      <view class="input-group">
        <view class="input-label">密码</view>
        <input class="input" password v-model="loginForm.password" placeholder="请输入密码"
               @confirm="handleLogin"/>
      </view>

      <view class="input-group" v-if="captchaEnabled">
        <view class="input-label">验证码</view>
        <view class="code-container">
          <input class="input code-input" type="text" v-model="loginForm.code" placeholder="请输入验证码"/>
          <image class="code-image" :src="codeUrl" @tap="getCode" mode="aspectFit"></image>
        </view>
      </view>


      <button class="login-button" @tap="handleLogin">登录</button>
    </view>
  </view>
</template>

<script>
import {ref, onMounted, computed} from 'vue'
import {getCodeImg, login, getInfo} from '../../api/login.js'
import store from '../../utils/store.js'

export default {
  setup() {
    const loginForm = ref({
      username: '',
      password: '',
      code: '',
      uuid: ''
    })

    const rememberMe = ref(true)
    const captchaEnabled = ref(true)
    const codeUrl = ref('')

    const logged = computed(() => {
      return !!store.getToken()
    })

    // 获取验证码
    const getCode = async () => {
      if (logged.value) {
        uni.switchTab({url: '/pages/index/index'})
        return
      }
      const res = await getCodeImg()
      captchaEnabled.value = res.captchaEnabled !== undefined ? res.captchaEnabled : true
      if (captchaEnabled.value) {
        codeUrl.value = 'data:image/gif;base64,' + res.img
        loginForm.value.uuid = res.uuid
      }
    }

    // 登录方法
    const handleLogin = async () => {
      // 表单验证
      if (!loginForm.value.username) {
        return uni.showToast({title: '请输入账号', icon: 'none'})
      }
      if (!loginForm.value.password) {
        return uni.showToast({title: '请输入密码', icon: 'none'})
      }
      if (captchaEnabled.value && !loginForm.value.code) {
        return uni.showToast({title: '请输入验证码', icon: 'none'})
      }

      try {
        const res = await login(
            loginForm.value.username,
            loginForm.value.password,
            loginForm.value.code,
            loginForm.value.uuid
        )

        // 存储token和用户信息
        store.setToken(res.access_token)
        // 获取用户信息
        const userRes = await getInfo()
        store.setUserInfo(userRes.user)
        store.setRolesAndPermissions(userRes.roles, userRes.permissions)

        // 记住密码
        if (rememberMe.value) {
          uni.setStorageSync('username', loginForm.value.username)
          uni.setStorageSync('password', loginForm.value.password)
          uni.setStorageSync('rememberMe', true)
        } else {
          uni.removeStorageSync('username')
          uni.removeStorageSync('password')
          uni.removeStorageSync('rememberMe')
        }

        // 登录成功，跳转到首页
        uni.switchTab({url: '/pages/index/index'})
      } catch (error) {
        // 重新获取验证码
        if (captchaEnabled.value) {
          getCode()
        }
      }
    }


    onMounted(() => {

      getCode()
    })

    return {
      logged,
      loginForm,
      rememberMe,
      captchaEnabled,
      codeUrl,
      handleLogin,
      getCode,
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  width: 85%;
  max-width: 400px;
  background-color: #fff;
  padding: 30rpx;
  border-radius: 12rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 40rpx;
  color: #333;
}

.input-group {
  margin-bottom: 30rpx;
}

.input-label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.input {
  width: 100%;
  height: 80rpx;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.code-container {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.code-input {
  flex: 1;
}

.code-image {
  width: 160rpx;
  height: 80rpx;
  border-radius: 8rpx;
}

.remember-container {
  margin-bottom: 40rpx;
  font-size: 28rpx;
  color: #666;
}

.login-button {
  width: 100%;
  height: 88rpx;
  background-color: #cea156;
  color: #fff;
  font-size: 32rpx;
  border-radius: 8rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>