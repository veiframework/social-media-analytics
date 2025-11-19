<template>
  <view class="loading-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="circle circle-1"></view>
      <view class="circle circle-2"></view>
      <view class="circle circle-3"></view>
    </view>

    <!-- 主要内容 -->
    <view class="loading-content">
      <!-- Logo区域 -->
      <view class="logo-section">
<!--        <text class="app-name">赛事小程序</text>-->
        <text class="app-slogan">正在进入小程序, 请稍等...</text>
      </view>

      <!-- 加载状态区域 -->
      <view class="loading-section">
        <!-- 加载动画 -->
        <view class="loading-animation">
          <view class="loading-spinner">
            <view class="spinner-dot dot-1"></view>
            <view class="spinner-dot dot-2"></view>
            <view class="spinner-dot dot-3"></view>
          </view>
        </view>

        <!-- 加载文本 -->
        <view class="loading-text-area">
          <text class="loading-text">{{ loadingText }}</text>
          <text class="loading-desc">{{ loadingDesc }}</text>
        </view>

        <!-- 进度条 -->
        <view class="progress-container">
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: progressWidth + '%' }"></view>
          </view>
          <text class="progress-text">{{ Math.round(progressWidth) }}%</text>
        </view>
      </view>

      <!-- 错误状态 -->
      <view v-if="hasError" class="error-section">
        <view class="error-icon">⚠️</view>
        <text class="error-message">{{ errorMessage }}</text>
        <button class="retry-btn" @tap="retryInit">重试</button>
      </view>
    </view>

    <!-- 底部信息 -->
    <view class="footer-info">
      <text class="version-text">版本 {{ appVersion }}</text>
      <text class="copyright">© 2024 赛事小程序</text>
    </view>
  </view>
</template>

<script>
import { getAppConfig } from '../../config/index.js';
import { base64Encode, base64Decode } from '../../utils/base64.js';

export default {
  data() {
    return {
      loadingText: '正在启动...',
      loadingDesc: '请稍候',
      progressWidth: 0,
      hasError: false,
      errorMessage: '',
      appVersion: '1.0.0',
      checkInterval: null,
      maxWaitTime: 30000, // 最大等待时间30秒
      startTime: null,
      toPage: null
    }
  },

  async onLoad(options) {
    console.log('加载页面启动');
    if(options.toPage){
     this.toPage = base64Decode(options.toPage);
     console.log('toPage',this.toPage);
    }
    // 获取应用版本信息
    this.appVersion = getAppConfig().version || '1.0.0';
    
    // 开始等待应用初始化
    this.startWaitingForInit();
  },
  onShow() {
   
  },
  onUnload() {
    // 清理定时器
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
    }
  },

  methods: {
    /**
     * 开始等待应用初始化
     */
    startWaitingForInit() {
      this.hasError = false;
      this.startTime = Date.now();
      
      // 开始进度动画
      this.startProgressAnimation();
      
      // 开始检查应用初始化状态
      this.checkAppInitStatus();
    },

    /**
     * 检查应用初始化状态
     */
    checkAppInitStatus() {
      console.log('开始检查应用初始化状态');
      
      this.checkInterval = setInterval(() => {
        const app = getApp();
        const globalData = app.globalData || {};
        
        console.log('检查全局数据:', globalData);
        
        // 检查是否初始化完成
        if (globalData.initCompleted) {
          console.log('应用初始化完成，准备跳转');
          this.onInitCompleted();
          return;
        }
        
        // 检查是否初始化失败
        if (globalData.initError) {
          console.log('应用初始化失败:', globalData.initError);
          this.onInitError(globalData.initError);
          return;
        }
        
        // 检查超时
        const elapsed = Date.now() - this.startTime;
        if (elapsed > this.maxWaitTime) {
          console.log('应用初始化超时');
          this.onInitError('初始化超时，请重试');
          return;
        }
        
        // 更新加载状态
        this.updateLoadingStatus(elapsed);
        
      }, 100); // 每500ms检查一次
    },

    /**
     * 更新加载状态
     */
    updateLoadingStatus(elapsed) {
      const progress = Math.min((elapsed / this.maxWaitTime) * 100, 95); // 最多到95%
      
      if (elapsed < 3000) {
        this.loadingText = '正在启动...';
        this.loadingDesc = '初始化应用环境';
      } else if (elapsed < 8000) {
        this.loadingText = '获取授权...';
        this.loadingDesc = '正在获取微信授权';
      } else if (elapsed < 15000) {
        this.loadingText = '登录中...';
        this.loadingDesc = '正在验证用户身份';
      } else if (elapsed < 25000) {
        this.loadingText = '加载配置...';
        this.loadingDesc = '正在加载应用配置';
      } else {
        this.loadingText = '即将完成...';
        this.loadingDesc = '请稍候片刻';
      }
      
      this.progressWidth = progress;
    },

    /**
     * 开始进度动画
     */
    startProgressAnimation() {
      // 初始进度动画到10%
      this.animateProgress(10);
    },

    /**
     * 初始化完成处理
     */
    async onInitCompleted() {
      // 清理定时器
      if (this.checkInterval) {
        clearInterval(this.checkInterval);
        this.checkInterval = null;
      }
      
      // 完成进度动画
      this.loadingText = '准备就绪...';
      this.loadingDesc = '即将进入应用';
      await this.animateProgress(100);
      
      // 稍微延迟一下，让用户看到100%的状态
        this.navigateToHome();
    },

    /**
     * 初始化错误处理
     */
    onInitError(error) {
      // 清理定时器
      if (this.checkInterval) {
        clearInterval(this.checkInterval);
        this.checkInterval = null;
      }
      
      this.showError(typeof error === 'string' ? error : error.message || '初始化失败');
    },

    /**
     * 动画更新进度条
     */
    async animateProgress(targetProgress) {
      const startProgress = this.progressWidth;
      const duration = 200; // 动画持续时间
      const steps = 30; // 动画步数
      const stepProgress = (targetProgress - startProgress) / steps;
      const stepDuration = duration / steps;

      for (let i = 0; i < steps; i++) {
        this.progressWidth = startProgress + stepProgress * (i + 1);
        await this.delay(stepDuration);
      }
      
      this.progressWidth = targetProgress;
    },

    /**
     * 跳转到首页
     */
    navigateToHome() {
      if(this.toPage){
        uni.redirectTo({
          url: this.toPage
        });
      } else{
         console.log('跳转到首页');
        // 使用reLaunch确保清除加载页面
          uni.reLaunch({
          url: '/pages/vcmall/home',
          success: () => {
            console.log('成功跳转到首页');
          },
          fail: (error) => {
            console.error('跳转首页失败:', error);
            this.showError('跳转失败，请重试');
          }
        });
      }
      
    },

    /**
     * 显示错误信息
     */
    showError(message) {
      this.hasError = true;
      this.errorMessage = message;
      this.loadingText = '初始化失败';
      this.loadingDesc = '请检查网络连接';
    },

    /**
     * 重试初始化
     */
    async retryInit() {
      console.log('重试初始化');
      
      // 重置应用全局状态
      const app = getApp();
      if (app.globalData) {
        app.globalData.initCompleted = false;
        app.globalData.initError = null;
      }
      
      // 重置loading状态
      this.hasError = false;
      this.progressWidth = 0;
      this.startTime = null;
      
      // 触发应用重新初始化
      try {
        if (app.forceReinit) {
          await app.forceReinit();
        }
      } catch (error) {
        console.error('触发重新初始化失败:', error);
      }
      
      // 重新开始等待
      this.startWaitingForInit();
    },

    /**
     * 延迟函数
     */
    delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    }
  }
}
</script>

<style scoped>
.loading-page {
  min-height: 100vh;
  /* background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 60rpx 40rpx 40rpx;
  position: relative;
  overflow: hidden;
  color: #000;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200rpx;
  height: 200rpx;
  top: 10%;
  right: -50rpx;
  animation-delay: 0s;
}

.circle-2 {
  width: 150rpx;
  height: 150rpx;
  bottom: 20%;
  left: -30rpx;
  animation-delay: 2s;
}

.circle-3 {
  width: 100rpx;
  height: 100rpx;
  top: 60%;
  right: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.1;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.2;
  }
}

/* 主要内容 */
.loading-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 600rpx;
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 80rpx;
}

.app-logo {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.15);
  margin-bottom: 30rpx;
}

.app-name {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.app-slogan {
  display: block;
  font-size: 28rpx;
 
  font-weight: 300;
}

/* 加载区域 */
.loading-section {
  width: 100%;
  text-align: center;
}

.loading-animation {
  margin-bottom: 40rpx;
}

.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8rpx;
}

.spinner-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
 
  animation: bounce 1.4s ease-in-out infinite both;
}

.dot-1 {
  animation-delay: -0.32s;
}

.dot-2 {
  animation-delay: -0.16s;
}

.dot-3 {
  animation-delay: 0s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.loading-text-area {
  margin-bottom: 40rpx;
}

.loading-text {
  display: block;
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
  margin-bottom: 10rpx;
}

.loading-desc {
  display: block;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}

/* 进度条 */
.progress-container {
  width: 100%;
  margin-bottom: 20rpx;
}

.progress-bar {
  width: 100%;
  height: 6rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3rpx;
  overflow: hidden;
  margin-bottom: 10rpx;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #888a8b 0%, #1b1d1d 100%);
  border-radius: 3rpx;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 22rpx;
 
}

/* 错误状态 */
.error-section {
  text-align: center;
  padding: 40rpx 0;
}

.error-icon {
  font-size: 60rpx;
  margin-bottom: 20rpx;
}

.error-message {
  display: block;
  font-size: 28rpx;
  color: #ff6b6b;
  margin-bottom: 30rpx;
  background: rgba(255, 255, 255, 0.9);
  padding: 20rpx;
  border-radius: 12rpx;
}

.retry-btn {
  background: #333;
  color: #fff;
  border: none;
  border-radius: 40rpx;
  padding: 10rpx 20rpx;
  font-size: 28rpx;
  font-weight: 500;
  
}

.retry-btn:active {
  transform: scale(0.98);
}

/* 底部信息 */
.footer-info {
  text-align: center;
  margin-top: 40rpx;
}

.version-text,
.copyright {
  display: block;
  font-size: 22rpx;
  color: #000;
  line-height: 1.5;
}
</style>