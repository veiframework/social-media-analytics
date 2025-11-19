<template>
  <view class="promotion-card-container">
    <!-- 顶部背景区域 -->
    <view class="header-section">
      <view class="background-overlay"></view>
<!--      <view class="header-content">-->
<!--        <view class="title-section">-->
<!--          <text class="main-title">邀请好友注册下单</text>-->
<!--          <text class="sub-title">即可获得大量提成</text>-->
<!--        </view>-->
<!--      </view>-->
      <image src="/static/vcmall/promotion_cover.png" class="icon-image" mode="widthFix"></image>

    </view>

    <!-- 邀请流程说明 -->
    <view class="invitation-process">
      <view class="process-title">
        <view class="process-title-dot"></view>
        <view class="process-title-text">简单两步,立得优惠</view>
        <view class="process-title-dot"></view>
      </view>
      
      <view class="process-steps">
        <!-- 步骤1: 微信分享 -->
        <view class="step-item">
          <view class="step-icon">
            <image src="/static/vcmall/promotion_wx.png" class="icon-image" mode="aspectFit"></image>
          </view>
          <view class="step-content">
            <view class="step-title">微信分享</view>
            <view class="step-desc">邀请好友注册</view>
          </view>
        </view>

        <!-- 连接线 -->
        <view class="step-connector"></view>

        <!-- 步骤2: 新人下单成功 -->
        <view class="step-item">
          <view class="step-icon">
            <image src="/static/vcmall/promotion_order.png" class="icon-image" mode="aspectFit"></image>
          </view>
          <view class="step-content">
            <view class="step-title">新人下单成功</view>
            <view class="step-desc">邀请人即可获得积分</view>
          </view>
        </view>
      </view>

      <!-- 邀请好友按钮 -->
      <view class="invite-button-section">
        <button class="invite-button" @tap="drawPoster">
          邀请好友
        </button>
      </view>
    </view>



    <!-- 邀请记录 -->
    <view class="invitation-records" v-if="invitedUsers.length > 0">
      <view class="process-title">
        <view class="process-title-dot"></view>
        <view class="process-title-text">邀请记录</view>
        <view class="process-title-dot"></view>
      </view>
      <view class="stats-list">
        <view class="stats-item" v-for="(item, index) in invitedUsers" :key="index">
          <view class="stats-info">
            <view class="user-name">{{ item.userName }}</view>
            <view class="user-detail">
              <text class="join-time">邀请时间：{{ item.joinTime }}</text>
            </view>
          </view>
          
          <view class="stats-data">
            <view class="stats-amount">
              <text class="amount-value">{{ item.status }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 自定义蒙层显示Canvas -->
    <view class="canvas-modal" v-if="showPop" @tap="closePopup">
      <view class="canvas-content" @tap.stop>
        <view class="canvas-header">
          <text class="canvas-title">分享邀请海报</text>
          <text class="close-btn" @tap="closePopup">×</text>
        </view>
        <view class="canvas-container">
          <canvas class="my-canvas" canvas-id="myCanvas"></canvas>
        </view>
        <view class="canvas-actions">
         
          <button class="share-btn" @tap="shareImg">分享好友</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPromotionCard, getInvitedUsers, generatePromotionQrCode } from '../../api/promotion.js'
import { getUserInfo } from '../../api/user.js';
import { getApiConfig } from '../../config/index';
const apiConfig = getApiConfig();

export default {
  name: 'PromotionCard',
  data() {
    return {
      showShareModal: false,
      promotionData: {},
      invitedUsers: [],
      inviteLink: '',
      qrCodeUrl: '',
      showPop: false,
      userInfo: {},
    }
  },
  onLoad() {
    this.loadInvitedUsers()
  },
  async onShow() {
  await  this.loadUserInfo()
    await  this.generatePromotionQrCodeRequest()
    // 页面显示时刷新邀请记录
    await  this.loadInvitedUsers()
  },
  methods: {
   async generatePromotionQrCodeRequest(){
     let params = {
       width: 340,
       height: 340,
       shopId: 12,
       content: apiConfig.baseURL + '?inviteLoginId='+ this.userInfo.id
     };
     let res = await generatePromotionQrCode(params);
     this.qrCodeUrl = res.msg
   },
    async loadUserInfo(){
      this.userInfo = await getUserInfo();
    },
    /**
     * 加载邀请用户列表
     */
    async loadInvitedUsers() {
      try {
        const result = await getInvitedUsers()
        if (result) {
          this.invitedUsers = result.map(item => ({
            userName: item.nickname || item.userName || '未知用户',
            joinTime: item.inviteTime,
            status: item.memberExpireDate == null ? '普通用户' : '会员'
          }))
        }
      } catch (error) {
        console.error('加载邀请用户失败:', error)
        this.invitedUsers = []
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
    },

    


// 画圆角矩形 ctx、x起点、y起点、w宽度、h高度、r圆角半径、fillColor填充颜色、strokeColor边框颜色
roundRect (ctx, x, y, w, h, r, fillColor, strokeColor, btn)  {
    // 开始绘制
    ctx.beginPath();
    // 绘制左上角圆弧 Math.PI = 180度
    // 圆心x起点、圆心y起点、半径、以3点钟方向顺时针旋转后确认的起始弧度、以3点钟方向顺时针旋转后确认的终止弧度
    ctx.arc(x + r, y + r, r, Math.PI, Math.PI * 1.5);
    // 绘制border-top
    // 移动起点位置 x终点、y终点
    ctx.moveTo(x + r, y);
    // 画一条线 x终点、y终点
    ctx.lineTo(x + w - r, y);
    // 绘制右上角圆弧
    ctx.arc(x + w - r, y + r, r, Math.PI * 1.5, Math.PI * 2);
    // 绘制border-right
    ctx.lineTo(x + w, y + h - r);
    // 绘制右下角圆弧
    ctx.arc(x + w - r, y + h - r, r, 0, Math.PI * 0.5);
    // 绘制左下角圆弧
    ctx.arc(x + r, y + h - r, r, Math.PI * 0.5, Math.PI);
    // 绘制border-left
    ctx.lineTo(x, y + r);
    if (btn == 'btn') {
        const grd = ctx.createLinearGradient(0, 0, 200, 0); //渐变色
        grd.addColorStop(0, fillColor);
        grd.addColorStop(1, strokeColor);
        // 因为边缘描边存在锯齿，最好指定使用 transparent 填充
        ctx.setFillStyle(grd);
        // 对绘画区域填充
        ctx.fill();
    } else {
        if (fillColor) {
            // 因为边缘描边存在锯齿，最好指定使用 transparent 填充
            ctx.setFillStyle(fillColor);
            // 对绘画区域填充
            ctx.fill();
        }
        if (strokeColor) {
            // 因为边缘描边存在锯齿，最好指定使用 transparent 填充
            ctx.setStrokeStyle(strokeColor);
            // 画出当前路径的边框
            ctx.stroke();
        }
    }
    // 关闭一个路径
    ctx.closePath();
    // 剪切，剪切之后的绘画绘制剪切区域内进行，需要save与restore 这个很重要 不然没办法保存
    ctx.clip();
},
 async drawPoster  ()   {
    let avatar =  await this.getImageInfo(this.userInfo.avatar)
    let qrcode = await this.getImageInfo(apiConfig.baseURL+'/'+ this.qrCodeUrl)
    let image = '/static/vcmall/promotion_cover.png'
    this.showPop= true
    let myCanvas = uni.createCanvasContext("myCanvas");
    myCanvas.clearRect(0, 0, 300, 375);
    myCanvas.setFillStyle("#ffffff");
    myCanvas.fillRect(0, 0, 300, 375);
    // 主图
    myCanvas.drawImage(image, 10, 10, 280, 234);
    // 大标题
    // myCanvas.setFillStyle("#ffffff");
    // myCanvas.font = "bold 21px Arial";
    // myCanvas.fillText("邀请好友获得购买积分", 45, 50);
    // // 小标题
    // myCanvas.save()
    // this.roundRect(myCanvas, 70, 70, 160, 28, 14, '#FEE6DE');
    // myCanvas.setFillStyle("#FD745E");
    // myCanvas.font = "14px Arial";
    // myCanvas.fillText("一起来体验吧", 106, 90);
    // myCanvas.restore()
    // 用户头像
    myCanvas.save()
    this.roundRect(myCanvas, 20, 250, 40, 40, 20, '#f1f1f1');
    myCanvas.drawImage(avatar, 20, 250, 40, 40);
    myCanvas.restore()
    // 分享码
    myCanvas.drawImage(qrcode, 200, 270, 90, 90);
    // 商家名称
    myCanvas.setFillStyle("#000000");
    myCanvas.font = "16px Arial";
    // myCanvas.fillText("杜康控股", 20, 315);
    // 主标题
        let name = "邀请好友获得提成";
        myCanvas.setFillStyle("#000000");
        myCanvas.font = "14px Arial";
        myCanvas.fillText(name, 20, 340);
    //副标
    let subtitle = "长按图片识别二维码";
    myCanvas.font = "13px Arial";
    myCanvas.setFillStyle("#000000");
    myCanvas.fillText(subtitle, 20, 360);
    //用户昵称
    myCanvas.font = "16px Arial";
    myCanvas.setFillStyle("#999999");
    // myCanvas.fillText(username ? username : '默认用户', 70, 280);
    myCanvas.draw(true);
  },
    getImageInfo  (image) {
      return new Promise((resolve, reject) => {
        uni.getImageInfo({
          src: image,
          success(res) {
            resolve(res.path)
          },
          fail(err) {
            console.log(err);
            uni.showToast({
              title: '海报生成失败' + image,
              duration: 2000,
              icon: 'none'
            });
          }
        });
      })
    },
  /**
   * 关闭弹窗
   */
  closePopup() {
    this.showPop = false;
  },

  /**
   * 保存图片
   */
  saveImg() {
    uni.canvasToTempFilePath({
      canvasId: 'myCanvas',
      success: (res) => {
        uni.saveImageToPhotosAlbum({
          filePath: res.tempFilePath,
          success: () => {
            uni.showToast({
              title: '保存成功',
              icon: 'success'
            });
          },
          fail: (err) => {
            console.error('保存失败:', err);
            if (err.errMsg.includes('auth')) {
              uni.showModal({
                title: '提示',
                content: '需要授权访问相册才能保存图片',
                confirmText: '去设置',
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    uni.openSetting();
                  }
                }
              });
            } else {
              uni.showToast({
                title: '保存失败',
                icon: 'none'
              });
            }
          }
        });
      },
      fail: (err) => {
        console.error('生成图片失败:', err);
        uni.showToast({
          title: '生成图片失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 分享图片
   */
  shareImg() {
    uni.canvasToTempFilePath({
      canvasId: 'myCanvas',
      success: (res) => {
        // #ifdef MP-WEIXIN
        wx.showShareImageMenu({
          path: res.tempFilePath,
          success: () => {
            console.log('分享成功');
          },
          fail: (err) => {
            console.error('分享失败:', err);
          }
        });
        // #endif

        // #ifdef APP-PLUS
        plus.share.sendWithSystem({
          type: 'image',
          pictures: [res.tempFilePath]
        }, () => {
          console.log('分享成功');
        }, (err) => {
          console.error('分享失败:', err);
        });
        // #endif

        // 其他平台提示保存到相册
        // #ifndef MP-WEIXIN || APP-PLUS
        uni.showModal({
          title: '提示',
          content: '请先保存图片到相册，然后手动分享',
          confirmText: '保存图片',
          success: (modalRes) => {
            if (modalRes.confirm) {
              this.saveImg();
            }
          }
        });
        // #endif
      },
      fail: (err) => {
        console.error('生成图片失败:', err);
        uni.showToast({
          title: '生成图片失败',
          icon: 'none'
        });
      }
    });
  }

  },

   
}
</script>

<style scoped>
.promotion-card-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff726f 0%, #ff726f 100%);
}

/* 顶部背景区域 */
.header-section {
  position: relative;
  height: 500rpx;
  background: linear-gradient(135deg, #ff726f 0%, #ff726f 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  opacity: 0.3;
}

.header-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #ffffff;
}

.title-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.main-title {
  font-size: 48rpx;
  font-weight: bold;
  margin-bottom: 16rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.sub-title {
  font-size: 28rpx;
  opacity: 0.9;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

/* 邀请流程说明 */
.invitation-process {
  background: #ffffff;
  margin: 40rpx 32rpx 0 32rpx;
  border-radius: 24rpx;
  padding: 40rpx 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  position: relative;
}

.process-title {
  background-color: #ffcc00;
  border-radius: 20rpx;
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: space-between;
  left: 30%;
  top: -40rpx;
  padding: 20rpx 10rpx;
  width: 40%;
  
}
.process-title-dot {
  width: 10rpx;
  height: 10rpx;
  background: #ff726f;
  border-radius: 50%;
}
.process-title-text {
  font-size: 24rpx;
  font-weight: bold;
  color: #333333;
}

.process-steps {
  margin-top: 30rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.step-icon {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #ff726f 0%, #ff726f 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(102, 126, 234, 0.3);
}

.icon-image {
  /*width: 60rpx;*/
  /*height: 60rpx;*/
  width: 100%;
}

.step-content {
  text-align: center;
}

.step-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 8rpx;
}

.step-desc {
  font-size: 24rpx;
  color: #666666;
  line-height: 1.4;
}

.step-connector {
  width: 80rpx;
  height: 4rpx;
  background: linear-gradient(to right, #ff726f, #ff726f);
  margin: 0 20rpx;
  margin-top: -60rpx;
}

/* 邀请好友按钮 */
.invite-button-section {
  padding: 32rpx 40rpx 0 40rpx;
}

.invite-button {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #ffcc00 0%, #ffcc00 100%);
  color: #000;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(255, 216, 155, 0.4);
  border: none;
}

.invite-button:active {
  transform: translateY(2rpx);
  box-shadow: 0 6rpx 20rpx rgba(255, 216, 155, 0.4);
}

/* 邀请记录 */
.invitation-records {
  margin: 80rpx 32rpx 40rpx;
  position: relative;
}

.records-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
  padding-left: 10rpx;
}

/* 推广统计样式 */
.stats-list {
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  padding-top: 30rpx;
}

.stats-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.stats-item:last-child {
  border-bottom: none;
}

.stats-info {
  flex: 1;
}

.user-name {
  font-size: 30rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 10rpx;
}

.user-detail {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.join-time {
  font-size: 26rpx;
  color: #666666;
}

.stats-data {
  text-align: right;
}

.stats-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}

.amount-value {
  font-size: 28rpx;
  color: #ff5722;
  font-weight: bold;
}

/* Canvas蒙层弹窗 */
.canvas-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 40rpx;
}

.canvas-content {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  max-width: 90%;
  max-height: 90%;
  display: flex;
  flex-direction: column;
  animation: fadeInScale 0.3s ease-out;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.canvas-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.canvas-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.close-btn {
  font-size: 48rpx;
  color: #999999;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f5f5f5;
}

.close-btn:active {
  background: #e0e0e0;
}

.canvas-container {
  display: flex;
  justify-content: center;
  margin-bottom: 32rpx;
}

.my-canvas {
  width: 600rpx;
  height: 750rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 12rpx;
  background: #ffffff;
}

.canvas-actions {
  display: flex;
  gap: 24rpx;
  justify-content: center;
}

.save-btn,
.share-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: bold;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.save-btn {
  background: linear-gradient(135deg, #ffcc00 0%, #ffcc00 100%);
  color: #ffffff;
}

.save-btn:active {
  background: linear-gradient(135deg, #ffcc00 0%, #ffcc00 100%);
}

.share-btn {
  background: linear-gradient(135deg, #ffd89b 0%, #ffcc00 100%);
  color: #ffffff;
}

.share-btn:active {
  background: linear-gradient(135deg, #ffcd7a 0%, #ffcc00 100%);
}

/* 响应式适配 */
@media screen and (max-width: 750rpx) {
  .process-steps {
    flex-direction: column;
  }
  
  .step-connector {
    width: 4rpx;
    height: 60rpx;
    margin: 20rpx 0;
  }
  
  .step-item {
    margin-bottom: 20rpx;
  }

  .canvas-modal {
    padding: 20rpx;
  }

  .my-canvas {
    width: 500rpx;
    height: 625rpx;
  }

  .canvas-actions {
    flex-direction: column;
    gap: 16rpx;
  }

  .save-btn,
  .share-btn {
    flex: none;
    width: 100%;
  }
}
</style>
