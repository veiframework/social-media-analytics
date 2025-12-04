<template>
  <!-- 使用ModalMask组件 -->
  <ModalMask :visible="visible" title="修改密码" @close="handleClose">
    <!-- 表单内容 -->
    <form @submit="formSubmit" @reset="formReset">
      <view class="uni-form-item uni-column">
        <view class="title">旧密码</view>
        <input class="uni-input" v-model="form.oldPassword" type="password" placeholder="请输入旧密码"/>
      </view>

      <view class="uni-form-item uni-column">
        <view class="title">新密码</view>
        <input class="uni-input" v-model="form.newPassword" type="password" placeholder="请输入新密码"/>
      </view>

      <view class="uni-form-item uni-column">
        <view class="title">确认新密码</view>
        <input class="uni-input" v-model="form.confirmPassword" type="password" placeholder="请确认新密码"/>
      </view>

      <!-- 按钮区域 -->
      <view class="button-group">
        <button class="cancel-btn" form-type="reset" @tap="handleClose">取消</button>
        <button class="confirm-btn" form-type="submit">确认</button>
      </view>
    </form>
  </ModalMask>
</template>

<script setup>
import {ref} from 'vue'
import {editPassword} from "../api/login"
import ModalMask from './ModalMask.vue'
// 定义props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

// 定义emit
const emit = defineEmits(['close', 'submit'])

// 表单数据
const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单重置
const formReset = () => {
  form.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

// 表单提交
const formSubmit = async () => {
  // 表单验证
  if (!form.value.oldPassword) {
    uni.showToast({title: '请输入旧密码', icon: 'none'})
    return
  }

  if (!form.value.newPassword) {
    uni.showToast({title: '请输入新密码', icon: 'none'})
    return
  }

  if (form.value.newPassword !== form.value.confirmPassword) {
    uni.showToast({title: '两次输入的密码不一致', icon: 'none'})
    return
  }

  await editPassword(
      form.value.oldPassword,
      form.value.newPassword
  )


  // 关闭弹窗
  handleClose()
}

// 关闭弹窗
const handleClose = () => {
  formReset()
  emit('close')
}
</script>

<style scoped>
/* 表单样式 */
.uni-form-item {
  margin-bottom: 30rpx;
}

.title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.uni-input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 8rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
}

/* 按钮组 */
.button-group {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}

.cancel-btn, .confirm-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  line-height: 80rpx;
}

.cancel-btn {
  background-color: white;
  color: #666;
  border: 2rpx solid #e8e8e8;
}

.confirm-btn {
  background-color: #cea156;
  color: white;
}
</style>
