<template>
  <view class="container">
    <view class="form-container">
      <!-- 赛事信息部分 - 只有从赛事分组页面跳转过来才显示 -->
      <template v-if="isFromCompetition">
        <view class="section-header">
          <text class="section-title">赛事信息</text>
        </view>

        <view class="form-section">
          <view class="form-item">
            <text class="form-label">比赛日期</text>
            <view class="form-value clickable" @click="selectCompetitionDate">
              <text class="value-text">{{ competitionDate || '请选择' }}</text>
              <image src="/static/arrow.png" class="arrow" mode="aspectFill" />

            </view>
          </view>
          
          <view class="form-item">
            <text class="form-label">比赛场次</text>
            <view class="form-value clickable" @click="selectCompetitionSession">
              <text class="value-text">{{ competitionSession || '请选择' }}</text>
              <image src="/static/arrow.png" class="arrow" mode="aspectFill" />

            </view>
          </view>
        </view>
      </template>

      <!-- 选手信息部分 -->
      <view class="section-header">
          <text class="section-title">选手信息</text>
          <!-- 只有从赛事分组页面跳转过来才显示"选择常用报名信息"按钮 -->
          <view v-if="isFromCompetition" class="select-info-tag" @click="showSelectInfoModal">
            <text class="select-info-text">选择常用报名信息</text>
          </view>
        </view>

      <view class="form-section">
      
        
        <view class="form-item">
          <text class="form-label">姓名</text>
          <input 
            class="form-input" 
            v-model="formData.childName" 
            placeholder="请输入"
            maxlength="20"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">身份证号</text>
          <input 
            class="form-input" 
            v-model="formData.childIdCard" 
            placeholder="请输入"
            maxlength="18"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">性别</text>
          <view class="gender-selector">
            <view 
              class="gender-option"
              :class="{ active: formData.childGender === '1' }"
              @click="selectGender('1')"
            >
              <text class="gender-text">男</text>
            </view>
            <view 
              class="gender-option"
              :class="{ active: formData.childGender === '2' }"
              @click="selectGender('2')"
            >
              <text class="gender-text">女</text>
            </view>
          </view>
        </view>
      </view>

      <view class="section-header">
          <text class="section-title">监护人信息</text>
        </view>

      <!-- 监护人信息部分 -->
      <view class="form-section bottom">
        
        
        <view class="form-item">
          <text class="form-label">姓名</text>
          <input 
            class="form-input" 
            v-model="formData.guardianName" 
            placeholder="请输入"
            maxlength="20"
          />
        </view>
        
        <view class="form-item">
          <text class="form-label">手机号</text>
          <input 
            class="form-input" 
            v-model="formData.guardianPhone" 
            placeholder="请输入"
            type="number"
            maxlength="11"
          />
        </view>
      </view>
    </view>

    <!-- 底部操作按钮 -->
    <view class="bottom-actions">
      <!-- 从赛事分组页面跳转过来时显示报名按钮 -->
      <template v-if="isFromCompetition">
        <view class="register-btn" @click="submitRegistration">
          <text class="register-text">报名</text>
        </view>
      </template>
      
      <!-- 报名信息管理页面的按钮 -->
      <template v-else>
        <view v-if="isEdit" class="delete-btn" @click="deleteCurrentInfo">
          <text class="delete-text">删除</text>
        </view>
        <view class="save-btn" @click="saveRegistrationInfo">
          <text class="save-text">保存</text>
        </view>
      </template>
    </view>

    <!-- 报名信息选择弹窗 - 只有从赛事分组页面跳转过来才显示 -->
    <RegistrationInfoModal 
      v-if="isFromCompetition"
      :visible="showInfoModal" 
      @close="handleInfoModalClose"
      @select="handleInfoSelect"
      @addNew="handleAddNew"
    />
  </view>
</template>

<script>
import { addRegistrationInfo, updateRegistrationInfo, deleteRegistrationInfo, getCompetitionGroupDetail, submitCompetitionRegistration } from '@/api/user.js'
import RegistrationInfoModal from '@/components/RegistrationInfoModal.vue'

export default {
  components: {
    RegistrationInfoModal
  },
  data() {
    return {
      isEdit: false,
      editId: null,
      competitionId: '',
      groupId: '',
      groupName: '',
      showInfoModal: false,
      competitionDate: '',
      competitionSession: '',
      groupInfo: null, // 存储分组详情信息
      sessionDates: [], // 可选的比赛日期数组
      sessionTimes: [], // 可选的比赛场次数组
      isFromCompetition: false, // 是否从赛事分组页面跳转过来
      formData: {
        childName: '',
        childIdCard: '',
        childGender: '',
        guardianName: '',
        guardianPhone: ''
      }
    }
  },
  
  onLoad(options) {
    // 判断是否从赛事分组页面跳转过来
    this.isFromCompetition = !!(options.competitionId && options.groupId);
    
    // 处理从赛事分组页面传递的参数
    if (options.competitionId) {
      this.competitionId = options.competitionId;
      console.log('报名赛事ID:', this.competitionId);
    }
    if (options.groupId) {
      this.groupId = options.groupId;
      console.log('报名分组ID:', this.groupId);
      // 加载分组详情
      this.loadGroupDetail();
    }
    if (options.groupName) {
      this.groupName = decodeURIComponent(options.groupName);
      console.log('报名分组名称:', this.groupName);
    }

    if (options.item) {
      try {
        const item = JSON.parse(decodeURIComponent(options.item));
    
        this.isEdit = true;
        this.editId = item.id;
        this.formData = {
          childName: item.childName || '',
          childIdCard: item.childIdCard || '',
          childGender: item.childGender || '',
          guardianName: item.guardianName || '',
          guardianPhone: item.guardianPhone || ''
        };
        
        // 设置导航栏标题
        uni.setNavigationBarTitle({
          title: '编辑报名信息'
        });
      } catch (error) {
        console.error('解析编辑数据失败:', error);
      }
    } else {
      // 根据是否有分组信息设置标题
      const title = this.groupName ? `报名 - ${this.groupName}` : '添加报名信息';
      uni.setNavigationBarTitle({
        title: title
      });
    }
  },
  
  methods: {
    /**
     * 选择性别
     */
    selectGender(gender) {
      this.formData.childGender = gender;
    },

    /**
     * 显示报名信息选择弹窗
     */
    showSelectInfoModal() {
      // 只有从赛事分组页面跳转过来才允许显示
      if (!this.isFromCompetition) {
        return;
      }
      this.showInfoModal = true;
    },

    /**
     * 关闭报名信息选择弹窗
     */
    handleInfoModalClose() {
      this.showInfoModal = false;
    },

    /**
     * 选择报名信息
     */
    handleInfoSelect(item) {
      console.log('选择的报名信息:', item);
      
      // 将选中的信息填入表单
      this.formData = {
        childName: item.childName || '',
        childIdCard: item.childIdCard || '',
        childGender: item.childGender || '',
        guardianName: item.guardianName || '',
        guardianPhone: item.guardianPhone || ''
      };
      
      uni.showToast({
        title: '信息已填入',
        icon: 'success'
      });
    },

    /**
     * 新增报名信息
     */
    handleAddNew() {
      // 跳转到报名信息管理页面
      uni.navigateTo({
        url: '/pages/registration/add'
      });
    },

    /**
     * 加载分组详情
     */
    async loadGroupDetail() {
      if (!this.groupId) return;
      
      try {
        const response = await getCompetitionGroupDetail(this.groupId);
        this.groupInfo = response;
        
        // 解析日期和场次选项
        if (response.sessionDate) {
          this.sessionDates = response.sessionDate.split(',').map(date => date.trim()).filter(date => date);
          if(this.sessionDates.length == 1){
            this.competitionDate = this.sessionDates[0];
          }
        }
        if (response.sessionTime) {
          this.sessionTimes = response.sessionTime.split(',').map(time => time.trim()).filter(time => time);
          if(this.sessionTimes.length == 1){
            this.competitionSession = this.sessionTimes[0];
          }
        }
        console.log('分组详情加载成功:', response);
        console.log('可选日期:', this.sessionDates);
        console.log('可选场次:', this.sessionTimes);
      } catch (error) {
        console.error('加载分组详情失败:', error);
        
        // 如果API失败，使用模拟数据进行测试
        console.log('使用模拟数据');
       
      }
    },

    

    /**
     * 选择比赛日期
     */
    selectCompetitionDate() {
      if (this.sessionDates.length === 0) {
        uni.showToast({
          title: '暂无可选日期',
          icon: 'none'
        });
        return;
      }
      
      uni.showActionSheet({
        itemList: this.sessionDates,
        success: (res) => {
          this.competitionDate = this.sessionDates[res.tapIndex];
        }
      });
    },

    /**
     * 选择比赛场次
     */
    selectCompetitionSession() {
      if (this.sessionTimes.length === 0) {
        uni.showToast({
          title: '暂无可选场次',
          icon: 'none'
        });
        return;
      }
      
      uni.showActionSheet({
        itemList: this.sessionTimes,
        success: (res) => {
          this.competitionSession = this.sessionTimes[res.tapIndex];
        }
      });
    },

    /**
     * 验证表单数据
     */
    validateForm() {
      const { childName, childIdCard, childGender, guardianName, guardianPhone } = this.formData;
      
      if (!childName.trim()) {
        uni.showToast({
          title: '请输入选手姓名',
          icon: 'none'
        });
        return false;
      }
      
      if (!childIdCard.trim()) {
        uni.showToast({
          title: '请输入选手身份证号',
          icon: 'none'
        });
        return false;
      }
      
      // 身份证号格式验证
      const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
      if (!idCardRegex.test(childIdCard)) {
        uni.showToast({
          title: '请输入正确的身份证号',
          icon: 'none'
        });
        return false;
      }
      
      if (!childGender) {
        uni.showToast({
          title: '请选择选手性别',
          icon: 'none'
        });
        return false;
      }
      
      if (!guardianName.trim()) {
        uni.showToast({
          title: '请输入监护人姓名',
          icon: 'none'
        });
        return false;
      }
      
      if (!guardianPhone.trim()) {
        uni.showToast({
          title: '请输入监护人手机号',
          icon: 'none'
        });
        return false;
      }
      
      // 手机号格式验证
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(guardianPhone)) {
        uni.showToast({
          title: '请输入正确的手机号',
          icon: 'none'
        });
        return false;
      }
      
      return true;
    },

    /**
     * 验证赛事信息
     */
    validateCompetitionInfo() {
      if (!this.competitionDate) {
        uni.showToast({
          title: '请选择比赛日期',
          icon: 'none'
        });
        return false;
      }
      
      if (!this.competitionSession) {
        uni.showToast({
          title: '请选择比赛场次',
          icon: 'none'
        });
        return false;
      }
      
      return true;
    },

    /**
     * 保存报名信息
     */
    async saveRegistrationInfo() {
      if (!this.validateForm()) {
        return;
      }
      
      try {
        uni.showLoading({
          title: this.isEdit ? '更新中...' : '保存中...'
        });
        
        let response;
        if (this.isEdit) {
          response = await updateRegistrationInfo(this.editId, this.formData);
        } else {
          response = await addRegistrationInfo(this.formData);
        }
        
        if (response && response.code === 200) {
          uni.showToast({
            title: this.isEdit ? '更新成功' : '添加成功',
            icon: 'success'
          });
          
          // 延迟返回上一页
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          throw new Error(response.message || (this.isEdit ? '更新失败' : '添加失败'));
        }
      } catch (error) {
        console.error('保存失败:', error);
        uni.showToast({
          title: error.message || '保存失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },

    /**
     * 提交赛事报名
     */
    async submitRegistration() {
      // 验证选手信息
      if (!this.validateForm()) {
        return;
      }
      
      // 验证赛事信息
      if (!this.validateCompetitionInfo()) {
        return;
      }
      
      try {
        
        
        // 构建报名数据
        const registrationData = {
          competitionId: this.competitionId,
          groupId: this.groupId,
          competitionDate: this.competitionDate,
          competitionSession: this.competitionSession,
          participantName: this.formData.childName.trim(),
          participantIdCard: this.formData.childIdCard.trim(),
          participantGender: this.formData.childGender,
          guardianName: this.formData.guardianName.trim(),
          guardianPhone: this.formData.guardianPhone.trim()
        };
        
        console.log('提交报名数据:', registrationData);
        
        const response = await submitCompetitionRegistration(registrationData);
        console.log(response)
        if (response && response.result) {
          uni.hideLoading();
          
          // 从响应中提取微信支付参数
          const paymentParams = {
            timeStamp: response.timeStamp,
            nonceStr: response.nonceStr,
            package: response.package,
            signType: 'MD5',
            paySign: response.paySign
          };
          
          console.log('微信支付参数:', paymentParams);
          
          // 唤起微信支付
          this.requestWechatPayment(paymentParams);
        }else if(response.code === 500) {
          uni.showToast({
          title: response.msg || '报名失败',
          icon: 'none'
        });
        } else {
          throw new Error(response.msg || '报名失败');
        }
      } catch (error) {
        console.error('报名失败:', error);
        uni.showToast({
          title: error.message || '报名失败',
          icon: 'none'
        });
      } finally {
      }
    },

    /**
     * 唤起微信支付
     */
    requestWechatPayment(paymentParams) {
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: paymentParams.timeStamp,
        nonceStr: paymentParams.nonceStr,
        package: paymentParams.package,
        signType: paymentParams.signType,
        paySign: paymentParams.paySign,
        success: (res) => {
          console.log('支付成功:', res);
          uni.showToast({
            title: '支付成功',
            icon: 'success'
          });
          
          // 延迟返回上一页
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        },
        fail: (err) => {
          console.error('支付失败:', err);
          if (err.errMsg && err.errMsg.includes('cancel')) {
            uni.showToast({
              title: '支付已取消',
              icon: 'none'
            });
          } else {
            uni.showToast({
              title: '支付失败',
              icon: 'none'
            });
          }
        }
      });
    },

    /**
     * 删除当前报名信息
     */
    deleteCurrentInfo() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条报名信息吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              uni.showLoading({
                title: '删除中...'
              });
              
              const response = await deleteRegistrationInfo(this.editId);
              
              if (response && response.code === 200) {
                uni.showToast({
                  title: '删除成功',
                  icon: 'success'
                });
                
                // 延迟返回上一页
                setTimeout(() => {
                  uni.navigateBack();
                }, 1500);
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
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
}

.form-container {
  padding: 20rpx;
  padding-bottom: 200rpx;
}

.form-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

/* 选择信息标签 */
.select-info-tag {
  background: #83BB0B;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  transition: all 0.3s;
}

.select-info-tag:active {
  background: #6fa008;
  transform: scale(0.95);
}

.select-info-text {
  font-size: 24rpx;
  color: #ffffff;
  font-weight: 500;
}

.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  width: 160rpx;
  font-size: 28rpx;
  color: #333;
  flex-shrink: 0;
}

.form-input {
  flex: 1;
  height: 80rpx;
  padding: 0 20rpx;
  background: #fff;
  border-radius: 10rpx;
  font-size: 28rpx;
  color: #333;
  text-align: right;
}

/* 表单值显示 */
.form-value {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content:flex-end;
  height: 80rpx;
  padding: 0 20rpx;
  border-radius: 10rpx;
}

.value-text {
  font-size: 28rpx;
  color: #333;
}

.arrow-icon {
  font-size: 24rpx;
  color: #999;
  font-weight: bold;
}

/* 可点击的表单值 */
.form-value.clickable {
  cursor: pointer;
  transition: all 0.3s;
}

.form-value.clickable:active {
  background: #e9ecef;
  transform: scale(0.98);
}

.gender-selector {
  display: flex;
  gap: 20rpx;
  flex: 1;
}

.gender-option {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 10rpx;
  border: 2rpx solid transparent;
}

.gender-option.active {
  background: #83BB0B;
  border-color: #83BB0B;
}

.gender-text {
  font-size: 28rpx;
  color: #333;
}

.gender-option.active .gender-text {
  color: #fff;
}

.bottom-actions {
  position: fixed;
  bottom: 60rpx;
  left: 40rpx;
  right: 40rpx;
  display: flex;
  gap: 20rpx;
}

.save-btn {
  flex: 2;
  height: 100rpx;
  background: #000;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 20rpx rgba(131, 187, 11, 0.3);
}

.register-btn {
  width: 100%;
  height: 100rpx;
  background: #83BB0B;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 20rpx rgba(131, 187, 11, 0.3);
  transition: all 0.3s;
}

.register-btn:active {
  background: #6fa008;
  transform: scale(0.98);
}

.delete-btn {
  flex: 1;
  height: 100rpx;
  background: #F56C6C;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 20rpx rgba(245, 108, 108, 0.3);
}

.save-text,
.delete-text,
.register-text {
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
}
.arrow{
  width: 10rpx;
  height: 20rpx;
  padding-left: 10rpx;
}
.form-section.bottom{
  
}
</style>