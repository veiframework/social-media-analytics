<template>
  <view class="add-work-form">
    <!-- 分享链接输入 -->
    <view class="form-item">
      <text class="form-label">作品分享链接：</text>
      <textarea
          class="form-input"
          v-model="formData.shareLink"
          placeholder="请输入作品分享链接"
      ></textarea>
    </view>

    <!-- 账号类型选择 -->
    <view class="form-item">
      <text class="form-label">账号类型：</text>
      <picker
          class="form-picker"
          @change="handleAccountTypeChange"
          :range="accountTypeOptions"
          :range-key="'label'"
      >
        <view class="picker-text">{{ selectedAccountType.label }}</view>
      </picker>
    </view>

    <!-- 账号类型选择 -->
    <view class="form-item">
      <text class="form-label">业务类型：</text>
      <picker
          class="form-picker"
          @change="handleCustomTypeChange"
          :range="customTypeOptions"
          :range-key="'label'"
      >
        <view class="picker-text">{{ selectedCustomType.label }}</view>
      </picker>
    </view>

    <!-- 提交按钮 -->
    <view class="form-actions">
      <button class="cancel-btn" @click="handleCancel">取消</button>
      <button class="submit-btn" @click="handleSubmit" :disabled="submitting">
        {{ submitting ? '提交中...' : '提交' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import {createWorkByShareLink, getDicts} from '../api/work.js'
import {extractUrlFromText} from '../utils/common.js'

// 定义props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

// 定义emit
const emit = defineEmits(['close', 'success'])

// 表单数据
const formData = reactive({
  shareLink: '',
  accountType: ''
})

// 提交状态
const submitting = ref(false)

// 账号类型选项
const accountTypeOptions = ref([
  {label: '请选择', value: null},
  {label: '个人', value: 'individual'},
  {label: '素人', value: 'amateur'},
])

// 选中的账号类型
const selectedAccountType = computed({
  get() {
    return accountTypeOptions.value.find(item => item.value === formData.accountType) || accountTypeOptions.value[0]
  },
  set(val) {
    formData.accountType = val.value
  }
})

const customTypeOptions = ref([
  {label: '请选择', value: null},
])

// 选中的业务类型
const selectedCustomType = computed({
  get() {
    return customTypeOptions.value.find(item => item.value === formData.customType) || customTypeOptions.value[0]
  },
  set(val) {
    formData.customType = val.value
  }
})

// 账号类型变化处理
const handleAccountTypeChange = (e) => {
  const index = e.detail.value
  selectedAccountType.value = accountTypeOptions.value[index]
}

const handleCustomTypeChange = (e) => {
  const index = e.detail.value
  selectedCustomType.value = customTypeOptions.value[index]
}

// 取消按钮处理
const handleCancel = () => {
  formData.shareLink = ''
  formData.accountType = ''
  emit('close')
}


onMounted(async () => {
  let res = await getDicts("social_media_custom_type")
  let arr = [{label: '请选择', value: null}]
  res.forEach(i => arr.push({
    label: i.dictLabel,
    value: i.dictValue,
  }))
  customTypeOptions.value=arr;

})


// 提交按钮处理
const handleSubmit = async () => {
  // 表单验证
  if (!formData.shareLink.trim()) {
    uni.showToast({
      title: '请输入作品分享链接',
      icon: 'none'
    })
    return
  }

  if (!formData.accountType) {
    uni.showToast({
      title: '请选择账号类型',
      icon: 'none'
    })
    return
  }

  if (!formData.customType) {
    uni.showToast({
      title: '请选择业务类型',
      icon: 'none'
    })
    return
  }

  try {
    submitting.value = true
    let extractedUrl = extractUrlFromText(formData.shareLink)
    // 调用API创建作品
    await createWorkByShareLink(extractedUrl, formData.accountType, formData.customType)
    // 提交成功提示
    uni.showToast({
      title: '作品创建成功',
      icon: 'success'
    })

    // 通知父组件提交成功
    emit('success')

    // 关闭弹窗
    emit('close')

    // 重置表单
    formData.shareLink = ''
    formData.accountType = ''
    formData.customType = ''
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.add-work-form {
  width: 100%;
}

.form-item {
  margin-bottom: 30 rpx;
  padding-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28 rpx;
  color: #333;
  margin-bottom: 10 rpx;
  font-weight: 500;
}

.form-input {
  width: 100%;
  height: 120 rpx;
  padding: 20 rpx;
  background-color: #f5f5f5;
  border-radius: 8 rpx;
  font-size: 28 rpx;
  color: #333;
  box-sizing: border-box;
}

.form-picker {
  padding: 20 rpx;
  background-color: #f5f5f5;
  border-radius: 8 rpx;
  font-size: 28 rpx;
  color: #333;
}

.picker-text {
  font-size: 28 rpx;
  color: #333;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 50 rpx;
}

.cancel-btn, .submit-btn {
  flex: 1;
  height: 80 rpx;
  font-size: 32 rpx;
  border-radius: 8 rpx;
  margin: 0 10 rpx;
  border: none;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
}

.submit-btn {
  background-color: #cea156;
  color: #fff;
}

.submit-btn:disabled {
  opacity: 0.6;
}
</style>