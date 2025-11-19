<template>
  <view class="edit-container">
    <!-- 收件人信息标题 -->
    <view class="section-header">
      <text class="section-title">收件人信息</text>
    </view>

    <!-- 收货人信息 -->
    <view class="form-section">
      <view class="form-group">
        <text class="form-label">姓名</text>
        <input 
          class="form-input" 
          v-model="formData.userName" 
          placeholder="请输入姓名"
          maxlength="20"
        />
      </view>

      <view class="form-group">
        <text class="form-label">电话</text>
        <input 
          class="form-input" 
          v-model="formData.telNumber" 
          placeholder="请输入手机号"
          type="number"
          maxlength="11"
        />
      </view>
    </view>

    <!-- 收件地址标题 -->
    <view class="section-header">
      <text class="section-title">收件地址</text>
    </view>

    <!-- 地址信息 -->
    <view class="form-section">
      <!-- 地址选择 -->
      <view class="form-group">
        <text class="form-label">省市区</text>
        <picker 
          mode="multiSelector"
          :value="regionValue"
          :range="[provinceList, cityList, countyList]"
          range-key="label"
          @change="handleRegionChange"
          @columnchange="handleColumnChange"
          class="region-picker-wrapper"
        >
          <view class="region-picker">
            <text class="region-text" :class="{ placeholder: !selectedRegion }">
              {{ selectedRegion || '请选择省市区' }}
            </text>
            <image src="/static/arrow.png" class="picker-arrow" mode="aspectFill" />
          </view>
        </picker>
      </view>

      <!-- 详细地址 -->
      <view class="form-group address-group">
        <text class="form-label">详细地址</text>
        <textarea 
          class="form-textarea" 
          v-model="formData.detailInfoNew" 
          placeholder="请输入详细地址"
          maxlength="200"
        />
      </view>

      <!-- 设为默认地址 -->
      <view class="form-group checkbox-group">
        <text class="form-label">默认地址</text>
        <switch 
          :checked="formData.isDefault === 1" 
          @change="handleDefaultChange"
          color="#000000"
        />
      </view>
    </view>

    <!-- 底部操作按钮 -->
    <view class="bottom-actions">
      <view v-if="isEdit" class="delete-btn" @tap="deleteAddressConfirm">
        <text class="delete-btn-text">删除</text>
      </view>
      <view class="save-btn" @tap="saveAddress">
        <text class="btn-text">保存</text>
      </view>
    </view>
  </view>
</template>

<script>
import { addAddress, updateAddress, deleteAddress, getRegions } from '../../api/address.js';

export default {
  data() {
    return {
      isEdit: false,
      addressId: null,
      formData: {
        userName: '',
        telNumber: '',
        provinceName: '河南省',
        cityName: '郑州市',
        countyName: '中原区',
        provinceId: '1909867435002024004',
        cityId: '1909867435002024005',
        countyId: '1909867435002024006',
        streetName: '',
        detailInfoNew: '',
        postalCode: '',
        isDefault: 1
      },
      selectedRegion: '',
      regionValue: [0, 0, 0],
      // 地区数据
      provinceList: [],
      cityList: [],
      countyList: []
    }
  },
  onLoad(options) {
    if (options.address) {
      try {
        const address = JSON.parse(decodeURIComponent(options.address));
        this.isEdit = true;
        this.addressId = address.id;
        this.formData = {
          userName: address.userName || '',
          telNumber: address.telNumber || '',
          provinceName: address.provinceName || '',
          cityName: address.cityName || '',
          countyName: address.countyName || '',
          provinceCode: address.provinceCode || '',
          cityCode: address.cityCode || '',
          countyCode: address.countyCode || '',
          provinceId: address.provinceId || '',
          cityId: address.cityId || '',
          countyId: address.countyId || '',
          streetName: address.streetName || '',
          detailInfoNew: address.detailInfoNew || address.detailInfo || '',
          postalCode: address.postalCode || '',
          isDefault: address.isDefault || 0
        };
      } catch (error) {
        console.error('解析地址数据失败:', error);
      }
    }

    // 设置页面标题
    uni.setNavigationBarTitle({
      title: this.isEdit ? '编辑地址' : '新建地址'
    });

    // 加载地区数据
    this.loadRegionsData();
  },
  methods: {
    // 加载地区数据
    async loadRegionsData() {
      try {
        // 加载省份数据
        await this.loadProvinceList();
        
        // 如果是编辑模式，恢复选中状态
          await this.initEditModeRegions();

        console.log('地区数据加载成功:', this.provinceList.length);
      } catch (error) {
        console.error('加载地区数据失败:', error);
        uni.showToast({
          title: '地区数据加载失败',
          icon: 'none'
        });
      }
    },

    // 加载省份列表
    async loadProvinceList() {
      try {
        const data = await getRegions({ level: 1 });
        this.provinceList = data.all || [];
      } catch (error) {
        console.error('加载省份数据失败:', error);
        throw error;
      }
    },

    // 编辑模式下初始化地区选择
    async initEditModeRegions() {
      try {
        // 找到对应的省份索引
        const provinceIndex = this.provinceList.findIndex(item => {
          if(this.formData.provinceId){
          return  item.id === this.formData.provinceId
          }else{
            return item.areaCode === this.formData.provinceCode+''
          }
        });
        if (provinceIndex >= 0) {
          this.regionValue[0] = provinceIndex;
          
          // 加载城市列表
          await this.loadCityList(this.provinceList[provinceIndex].id);
          
          // 找到对应的城市索引
          const cityIndex = this.cityList.findIndex(item =>{
              if(this.formData.cityId){
                return  item.id === this.formData.cityId
              }else{
                return  item.areaCode === this.formData.cityCode+''
              }
            });
          if (cityIndex >= 0) {
            this.regionValue[1] = cityIndex;
            
            // 加载区县列表
            await this.loadCountyList(this.cityList[cityIndex].id);

            // 找到对应的区县索引
            const countyIndex = this.countyList.findIndex(item => {
              if(this.formData.countyId){
                return item.id === this.formData.countyId
              }else{
                return item.areaCode === this.formData.countyCode+''
              }
            });
            if (countyIndex >= 0) {
              this.regionValue[2] = countyIndex;
              this.updateSelectedRegion();
            }
          }
        }
        console.log(this.cityList)
        console.log(this.countyList)
      } catch (error) {
        console.error('初始化编辑模式地区选择失败:', error);
      }
    },

    // 加载城市列表
    async loadCityList(provinceId) {
      try {
        const data = await getRegions({ level: 2, parentId: provinceId });
        this.cityList = data.all || [];
        // 重置区县列表
        this.countyList = [];
      } catch (error) {
        console.error('加载城市数据失败:', error);
        this.cityList = [];
        this.countyList = [];
        throw error;
      }
    },

    // 加载区县列表
    async loadCountyList(cityId) {
      try {
        const data = await getRegions({ level: 3, parentId: cityId });
        this.countyList = data.all || [];
      } catch (error) {
        console.error('加载区县数据失败:', error);
        this.countyList = [];
        throw error;
      }
    },

    // 更新选中地区显示
    updateSelectedRegion() {
      const parts = [
        this.formData.provinceName,
        this.formData.cityName,
        this.formData.countyName
      ].filter(part => part && part.trim());
      
      this.selectedRegion = parts.join(' ');
    },

    // 处理列变化事件（三级联动）
    async handleColumnChange(e) {
      const { column, value } = e.detail;
      
      try {
        if (column === 0) {
          // 省份变化，重置城市和区县
          this.regionValue[1] = 0;
          this.regionValue[2] = 0;
          const selectedProvince = this.provinceList[value];
          if (selectedProvince) {
            await this.loadCityList(selectedProvince.id);
          }
        } else if (column === 1) {
          // 城市变化，重置区县
          this.regionValue[2] = 0;
          const selectedCity = this.cityList[value];
          if (selectedCity) {
            await this.loadCountyList(selectedCity.id);
          }
        }
      } catch (error) {
        console.error('加载下级地区数据失败:', error);
        uni.showToast({
          title: '加载地区数据失败',
          icon: 'none'
        });
      }
    },

    // 处理地区选择确认
    handleRegionChange(e) {
      const [provinceIndex, cityIndex, countyIndex] = e.detail.value;
      
      // 更新选中的索引
      this.regionValue = [provinceIndex, cityIndex, countyIndex];
      
      // 获取选中的地区信息
      const selectedProvince = this.provinceList[provinceIndex];
      const selectedCity = this.cityList[cityIndex];
      const selectedCounty = this.countyList[countyIndex];
      
      if (selectedProvince) {
        this.formData.provinceName = selectedProvince.label;
        this.formData.provinceId = selectedProvince.id;
        this.formData.provinceCode = selectedProvince.areaCode;
      }
      
      if (selectedCity) {
        this.formData.cityName = selectedCity.label;
        this.formData.cityId = selectedCity.id;
        this.formData.cityCode = selectedCity.areaCode;
      }
      
      if (selectedCounty) {
        this.formData.countyName = selectedCounty.label;
        this.formData.countyId = selectedCounty.id;
        this.formData.countyCode = selectedCounty.areaCode;
      }
      
      // 清空街道信息
      this.formData.streetName = '';
      
      // 更新显示
      this.updateSelectedRegion();
      
      console.log('选中地区:', {
        province: { name: this.formData.provinceName, id: this.formData.provinceId },
        city: { name: this.formData.cityName, id: this.formData.cityId },
        county: { name: this.formData.countyName, id: this.formData.countyId }
      });
    },

    handleDefaultChange(e) {
      this.formData.isDefault = e.detail.value ? 1 : 0;
    },

    validateForm() {
      if (!this.formData.userName.trim()) {
        uni.showToast({
          title: '请输入收货人姓名',
          icon: 'none'
        });
        return false;
      }

      if (!this.formData.telNumber.trim()) {
        uni.showToast({
          title: '请输入手机号码',
          icon: 'none'
        });
        return false;
      }

      // 简单的手机号验证
      const phoneReg = /^1[3-9]\d{9}$/;
      if (!phoneReg.test(this.formData.telNumber)) {
        uni.showToast({
          title: '请输入正确的手机号码',
          icon: 'none'
        });
        return false;
      }

      if (!this.formData.provinceName || !this.formData.cityName || !this.formData.countyName ||
          !this.formData.provinceId || !this.formData.cityId || !this.formData.countyId) {
        uni.showToast({
          title: '请选择所在地区',
          icon: 'none'
        });
        return false;
      }

      if (!this.formData.detailInfoNew.trim()) {
        uni.showToast({
          title: '请输入详细地址',
          icon: 'none'
        });
        return false;
      }

      return true;
    },

    async saveAddress() {
      if (!this.validateForm()) {
        return;
      }

      try {
        uni.showLoading({
          title: '保存中...'
        });

        if (this.isEdit) {
          await updateAddress(this.addressId, this.formData);
        } else {
          await addAddress(this.formData);
        }

        uni.hideLoading();
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        });

        // 返回上一页
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);

      } catch (error) {
        uni.hideLoading();
        console.error('保存地址失败:', error);
        uni.showToast({
          title: '保存失败',
          icon: 'none'
        });
      }
    },

    deleteAddressConfirm() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这个收件地址吗？',
        success: (res) => {
          if (res.confirm) {
            this.deleteAddressItem();
          }
        }
      });
    },

    async deleteAddressItem() {
      try {
        uni.showLoading({
          title: '删除中...'
        });

        await deleteAddress(this.addressId);

        uni.hideLoading();
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        });

        // 返回上一页
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);

      } catch (error) {
        uni.hideLoading();
        console.error('删除地址失败:', error);
        uni.showToast({
          title: '删除失败',
          icon: 'none'
        });
      }
    }
  }
}
</script>

<style scoped>
.edit-container {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 120rpx;
}

.section-header {
  background: #F5F5F5;
  padding: 32rpx 24rpx 16rpx 24rpx;
}

.section-title {
  font-size: 32rpx;
  color: #333333;
  font-weight: bold;
}

.form-section {
  background: #ffffff;
  margin: 0 24rpx 24rpx 24rpx;
  border-radius: 0;
  padding: 0;
}

.form-group {
  display: flex;
  align-items: center;
  padding: 24rpx 20rpx;
  border-bottom: 1rpx solid #F0F0F0;
  min-height: 48rpx;
}

.form-group:last-child {
  border-bottom: none;
}

.address-group {
  align-items: flex-start;
  padding-top: 24rpx;
  padding-bottom: 24rpx;
}

.checkbox-group {
  align-items: center;
}

.form-label {
  width: 120rpx;
  font-size: 28rpx;
  color: #333333;
  line-height: 48rpx;
  flex-shrink: 0;
}

.form-input {
  flex: 1;
  height: 48rpx;
  font-size: 28rpx;
  color: #333333;
  background: transparent;
  text-align: right;
}

.form-input::placeholder {
  color: #CCCCCC;
}

.form-textarea {
  flex: 1;
  min-height: 120rpx;
  font-size: 28rpx;
  color: #333333;
  background: transparent;
  line-height: 1.5;
  margin-top: 8rpx;
  text-align: right;
}

.form-textarea::placeholder {
  color: #CCCCCC;
}

.region-picker-wrapper {
  flex: 1;
}

.region-picker {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48rpx;
}

.region-text {
  font-size: 28rpx;
  color: #333333;
  text-align: right;
  flex: 1;
}

.region-text.placeholder {
  color: #CCCCCC;
}

.picker-arrow {
  width: 10rpx;
  height: 20rpx;
  padding-left: 10rpx;
}

.bottom-actions {
  position: fixed;
  bottom: 40rpx;
  left: 24rpx;
  right: 24rpx;
  display: flex;
  gap: 24rpx;
  height: 88rpx;
}

.delete-btn {
  width: 160rpx;
  background: #F56C6C;
  border: 2rpx solid #CCCCCC;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;

}

.delete-btn-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.save-btn {
  flex: 2;
  background: #000;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: bold;
}
</style>
