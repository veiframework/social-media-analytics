<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
            @currentChange="handleChange">
            <template #table-top></template>
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'orderInfoVos'">
                    <div class="body">
                        <div class="order" v-for="(item, index) in row.orderInfoVos" :key="index">
                            <el-image style="width: 70px;" class="img" :src="item.goodsImg" fit="contain" />
                            <div class="content">
                                <div class="info">
                                    <div class="title">{{ item.goodsName }}</div>
                                    <el-tag>{{ item.specifications }}</el-tag>
                                </div>
                                <div class="info">

                                  <div v-if="item.amount != null" class="price">￥{{ item.amount }}</div>
                                    <div class="num">*<span style="margin-left: 1px;">{{ item.goodsNum }}</span></div>
                                </div>
                                <div class="info_bottom">
                                  <el-tag  >{{ item.goodsGroup }}</el-tag>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
                <template v-else-if="prop == 'status'">
                    <el-tag v-if="row.status == 0" type="warning">待支付</el-tag>
                    <el-tag v-else-if="row.status == 1 && row.deliveryStatus == 0" type="info">待发货</el-tag>
                    <el-tag v-else-if="row.status == 1 && row.deliveryStatus == 1" type="warning">发货中</el-tag>
                    <el-tag v-else-if="row.status == 2" type="danger">超时未支付</el-tag>
                    <el-tag v-else-if="row.status == 3" type="primary">已发货</el-tag>
                    <el-tag v-else-if="row.status == 4" type="success">已完成</el-tag>
                  <el-tag v-else-if="row.status == 5" type="success">退款中</el-tag>
                  <el-tag v-else-if="row.status == 6" type="info">已退款</el-tag>
                  <el-tag v-else-if="row.status == 7" type="info">申请退款</el-tag>
                  <el-tag v-else-if="row.status == 8" type="info">退款驳回</el-tag>
                  <el-tag v-else-if="row.status == 9" type="info">退款失败</el-tag>
                  <el-tag v-else-if="row.status == 102" type="info">待接单</el-tag>
                  <el-tag v-else-if="row.status == 201" type="info">已接单</el-tag>
                </template>
                <template v-else-if="prop == 'payWay'">
                    <el-tag v-if="row.payWay == 2">线下支付</el-tag>
                    <el-tag v-else type="success">微信支付</el-tag>
                </template>
            </template>
        </CustomTable>
        <CustomInfo :option="optionInfo" :visible="infoVisible" :rowData="rowData" @cancel="infoVisible = false">
            <template #custom="{ prop, row }">
              <div v-if="prop == 'buyer'">
                {{ row.addressVo?.userName }}/{{ row.addressVo?.telNumber }}
                <div>
                  {{ `${row.addressVo?.provinceName} ${row.addressVo?.cityName} ${row.addressVo?.countyName}
                    ${row.addressVo?.detailInfo}` }}
                </div>
              </div>

                <div v-else-if="prop == 'nickname'">{{ row.userVo?.nickname }}</div>
                <div v-else-if="prop == 'phone'">{{ row.userVo?.phone }}</div>
                <template v-else-if="prop == 'payWay'">
                    <el-tag v-if="row.payWay == 2">线下支付</el-tag>
                    <el-tag v-else type="success">微信支付</el-tag>
                </template>
                <div v-else-if="prop == 'memGrade'">
                    <el-tag v-if="row.memGrade == 1" type="info">普通用户</el-tag>
                    <el-tag v-else-if="row.memGrade == 2">会员用户</el-tag>
                    <el-tag v-else-if="row.memGrade == 3" type="success">VIP 用户</el-tag>
                </div>
                <div v-else-if="prop == 'orderInfoVos'">
                    <el-descriptions style="margin-top: 15px;" v-for="item in row.orderInfoVos"
                        :title="`商品编号：${item.goodsNo}`" :column="pinia.device == 'desktop' ? 2 : 1" border>
                        <el-descriptions-item label="商品类型："> {{ item.goodsTypeName }} </el-descriptions-item>
                        <el-descriptions-item label="商品名称："> {{ item.goodsName }} </el-descriptions-item>
                        <el-descriptions-item label="商品图片：">
                            <el-image style="width: 60px; height: 60px" :src="item.goodsImg" :zoom-rate="1.2"
                                :max-scale="7" :min-scale="0.2" :preview-src-list="[item.goodsImg]" :initial-index="0"
                                fit="cover" />
                        </el-descriptions-item>
                        <el-descriptions-item label="商品规格："> {{ item.specifications }} </el-descriptions-item>
                        <el-descriptions-item label="商品单价："> {{ item.unitPrice }} 元</el-descriptions-item>
                        <el-descriptions-item label="商品数量："> {{ item.goodsNum }} </el-descriptions-item>
                        <el-descriptions-item label="订单状态：">
                            <el-tag v-if="row.status == 0" type="warning">待支付</el-tag>
                            <el-tag v-else-if="row.status == 1 && row.deliveryStatus == 0" type="info">待发货</el-tag>
                            <el-tag v-else-if="row.status == 1 && row.deliveryStatus == 1" type="warning">发货中</el-tag>
                            <el-tag v-else-if="row.status == 2" type="danger">超时未支付</el-tag>
                            <el-tag v-else-if="row.status == 3" type="primary">已发货</el-tag>
                            <el-tag v-else-if="row.status == 4" type="success">已完成</el-tag>
                          <el-tag v-else-if="row.status == 5" type="success">退款中</el-tag>
                          <el-tag v-else-if="row.status == 6" type="info">已退款</el-tag>
                          <el-tag v-else-if="row.status == 7" type="info">申请退款</el-tag>
                          <el-tag v-else-if="row.status == 8" type="info">退款驳回</el-tag>
                          <el-tag v-else-if="row.status == 9" type="info">退款失败</el-tag>
                          <el-tag v-else-if="row.status == 102" type="info">待接单</el-tag>
                          <el-tag v-else-if="row.status == 201" type="info">已接单</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="订单价格："> {{ item.amount }} 元</el-descriptions-item>
                    </el-descriptions>
                </div>
                <div v-else-if="prop == 'logistics'">
                    <el-descriptions style="margin-top: 15px;" v-for="item in row.orders" :title="`物流单号：${item.mailNo}`"
                        :column="pinia.device == 'desktop' ? 2 : 1" border>
                        <el-descriptions-item label="物流进度：">
                            <el-timeline style="max-width: 600px">
                                <el-timeline-item v-for="(itm, index) in item.steps" :key="index"
                                    :timestamp="itm.operationDescribe">
                                    {{ itm.operationTime }}
                                </el-timeline-item>
                            </el-timeline>
                        </el-descriptions-item>
                    </el-descriptions>
                </div>
                <div v-else-if="prop == 'status'">
                    <el-tag v-if="row.status == 0" type="warning">待支付</el-tag>
                    <el-tag v-else-if="row.status == 1 && row.deliveryStatus == 0" type="info">待发货</el-tag>
                    <el-tag v-else-if="row.status == 1 && row.deliveryStatus == 1" type="warning">发货中</el-tag>
                    <el-tag v-else-if="row.status == 2" type="danger">超时未支付</el-tag>
                    <el-tag v-else-if="row.status == 3" type="primary">已发货</el-tag>
                    <el-tag v-else-if="row.status == 4" type="success">已完成</el-tag>
                  <el-tag v-else-if="row.status == 5" type="success">退款中</el-tag>
                  <el-tag v-else-if="row.status == 6" type="info">已退款</el-tag>
                  <el-tag v-else-if="row.status == 7" type="info">申请退款</el-tag>
                  <el-tag v-else-if="row.status == 8" type="info">退款驳回</el-tag>
                  <el-tag v-else-if="row.status == 9" type="info">退款失败</el-tag>
                  <el-tag v-else-if="row.status == 102" type="info">待接单</el-tag>
                  <el-tag v-else-if="row.status == 201" type="info">已接单</el-tag>
                </div>
            </template>
        </CustomInfo>
        
        <!-- 发货弹窗 -->
        <CustomDialog :option="shipmentDialogOption" :visible="shipmentVisible" :rowData="shipmentData" 
            @cancel="shipmentVisible = false" @save="confirmShipment">
        </CustomDialog>
        
        <!-- 驳回退款弹窗 -->
        <CustomDialog :option="rejectRefundDialogOption" :visible="rejectRefundVisible" :rowData="rejectRefundData" 
            @cancel="rejectRefundVisible = false" @save="confirmRejectRefund">
        </CustomDialog>
        
        <!-- 物流取货弹窗 -->
        <CustomDialog :option="logisticsPickupDialogOption" :visible="logisticsPickupVisible" :rowData="logisticsPickupData" 
            @cancel="logisticsPickupVisible = false" @save="confirmLogisticsPickup"  @formChange="handleFormChange">
        </CustomDialog>
    </div>
</template>

<script  setup name="order">
import CustomTable from "@/components/CustomTable/index.vue";
import CustomInfo from "@/components/CustomInfo/index.vue";
import CustomDialog from "@/components/CustomDialog/index.vue";

import { ElMessage, ElMessageBox } from "element-plus";
import { computed, reactive, ref } from "vue";
import useAppStore from "@/store/modules/app";
import setting from '@/settings'

  
import {
  listOrder,
  listOfflineOrder,
  getOrder,
  delOrder,
  setTrackingNo,
  getOrderLogistics,
  completeReceipt,
  wechatRefund,
  rejectRefund,
  createLogisticsOrder,
  getReceiverAddressList
} from "@/api/order";
import {
  listAccount,
  getAllAccount,
  getDeliveryList,
  getDeliveryServiceList,
  bindAccount,
  unbindAccount
} from "@/api/logistics/account";
import { getAllAddress } from "@/api/logistics/address";
import { getRegionAllApi } from '@/api/system/region';


const pinia = useAppStore
// 表格参数
const queryPramas = ref({});
const tableData = ref([{}]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(100);
// 详情参数
const infoVisible = ref(false);
const rowData = ref();
// 发货弹窗参数
const shipmentVisible = ref(false);
const shipmentData = ref({});
const currentShipmentOrder = ref(null);
// 驳回退款弹窗参数
const rejectRefundVisible = ref(false);
const rejectRefundData = ref({});
const currentRejectOrder = ref(null);
// 物流取货弹窗参数
const logisticsPickupVisible = ref(false);
const logisticsPickupData = ref({});
const currentLogisticsOrder = ref(null);
// 快递公司列表
const accountList = ref([]);
// 快递服务类型列表
const deliveryServiceList = ref([]);
const receiverAddressList = ref([]);
const shopAddressList = ref([]);
const defaultShopAddress = ref(null);
// 自定义参数
const pickTimeDict = ref([
                { value: '08:00-09:00', label: '08:00-09:00' },
                { value: '09:00-10:00', label: '09:00-10:00' },
                { value: '10:00-11:00', label: '10:00-11:00' },
                { value: '11:00-12:00', label: '11:00-12:00' },
                { value: '12:00-13:00', label: '12:00-13:00' },
                { value: '13:00-14:00', label: '13:00-14:00' },
                { value: '14:00-15:00', label: '14:00-15:00' },
                { value: '15:00-16:00', label: '15:00-16:00' },
                { value: '16:00-17:00', label: '16:00-17:00' },
                { value: '17:00-18:00', label: '17:00-18:00' },
                { value: '18:00-19:00', label: '18:00-19:00' },
                { value: '19:00-20:00', label: '19:00-20:00' },
                { value: '20:00-21:00', label: '20:00-21:00' },
                { value: '21:00-22:00', label: '21:00-22:00' },
            ]);

// 当前选择的日期（响应式）
const selectedPickupDate = ref(0);

// 计算可用的时间段
const availableTimeSlots = computed(() => {
    console.log('计算可用时间段 - 选择的日期:', selectedPickupDate.value);
    
    // 如果不是今天，返回所有时间段
    if (selectedPickupDate.value !== 0) {
        console.log('返回所有时间段 - 不是今天');
        return pickTimeDict.value;
    }
    
    // 如果是今天，过滤掉已经过去的时间段
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();
    const currentTime = currentHour * 60 + currentMinute; // 转换为分钟
    
    
    let filteredSlots = pickTimeDict.value.filter(timeSlot => {
      // 如果是今天，检查时间段是否已过
      const timeSlotHour = parseInt(timeSlot.value.split(':')[0]);
      return timeSlotHour > currentHour;
    });
    
    
    return filteredSlots;
});


// 省市区数据
const senderProvinces = ref([]);
const senderCitys = ref([]);
const senderAreas = ref([]);

const revProvinces = ref([]);
const revCitys = ref([]);
const revAreas = ref([]);

/**
 * 初始化数据
 */


 const init = () => {
    getRegionData();
    getAllAddressData();
    getAllAccountData()
  getData();
};


/**
 * 获取省份数据
 */
 const getRegionData = () => {
  getRegionAllApi({level: 1}).then(res => {
        senderProvinces.value = res.data;
        revProvinces.value = res.data;
    })
}

const getSubRegion = async (parentId) => {
  let res= await getRegionAllApi({parentId: parentId})
  return res.data
}

const getAllAddressData = async () => {
  let res= await getAllAddress({})
  return res.data
}

const getReceiverAddressListData = async (userId) => {
  let res= await getReceiverAddressList({userId: userId})
  return res.data
}
/**
 * 请求列表
 */
const getData = () => {
    const query= { ...queryPramas.value, pageNum: pageNum.value, pageSize: pageSize.value };
    switch (query.status) {
        case 1:
            query.deliveryStatus = 0;
            break;
        case 5:
            query.status = 1;
            query.deliveryStatus = 1;
            break;
    }
    listOrder(query).then(res => {
        tableData.value = res.data.records;
        total.value = res.data.total;
    })
}
/**
 * 筛选查询
 */
const handleSearch = (val) => {
    pageNum.value = 1;
    queryPramas.value = val;
    getData();
}
/**
 * 分页查询
 */
const handleChange = (val) => {
    pageNum.value = val.page;
    pageSize.value = val.limit;
    getData();
}
/**
 * 表格左侧按钮
 */
const handleHeader = (key) => {
    switch (key) {
        case "add":
            break;
    }
}

/**
 * 获取快递公司列表
 */
 const getAllAccountData = () => {
    getAllAccount({}).then(res => {
        accountList.value = res.data;
    })
}

/**
 * 解析上门时间范围字符串
 * @param {string} pickTimeRange - 格式如 "2025-09-02 15:00-16:00"
 * @returns {object} - 返回解析后的日期和时间段
 */
const parsePickTimeRange = (pickTimeRange) => {
    if (!pickTimeRange) {
        return getDefaultTimeSlot(); // 使用智能默认值
    }
    
    try {
        const [datePart, timePart] = pickTimeRange.split(' ');
        const pickDate = new Date(datePart);
        const today = new Date();
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1);
        const dayAfterTomorrow = new Date(today);
        dayAfterTomorrow.setDate(today.getDate() + 2);
        
        // 判断是今天、明天还是后天
        let pickupDate = 0; // 默认今天
        if (pickDate.toDateString() === today.toDateString()) {
            pickupDate = 0; // 今天
        } else if (pickDate.toDateString() === tomorrow.toDateString()) {
            pickupDate = 1; // 明天
        } else if (pickDate.toDateString() === dayAfterTomorrow.toDateString()) {
            pickupDate = 2; // 后天
        }
        
        let selectedTimeRange = timePart || '14:00-15:00';
        
        // 如果是今天，检查时间段是否还可用
        if (pickupDate === 0) {
            const now = new Date();
            const currentTime = now.getHours() * 60 + now.getMinutes();
            const [endHour, endMinute] = selectedTimeRange.split('-')[1].split(':').map(Number);
            const endTime = endHour * 60 + endMinute;
            
            // 如果时间段已过期，选择下一个可用的时间段
            if (endTime <= currentTime) {


              const availableSlot = pickTimeDict.value.find(slot => {
                    const [slotEndHour, slotEndMinute] = slot.value.split('-')[1].split(':').map(Number);
                    const slotEndTime = slotEndHour * 60 + slotEndMinute;
                    return slotEndTime > currentTime;
                });
                
                if (availableSlot) {
                    selectedTimeRange = availableSlot.value;
                } else {
                    // 今天没有可用时间段，改为明天
                    pickupDate = 1;
                    selectedTimeRange = '14:00-15:00';
                }
            }
        }
        
        return {
            pickupDate,
            pickupTimeRange: selectedTimeRange
        };
    } catch (error) {
        console.error('解析上门时间失败:', error);
        return getDefaultTimeSlot();
    }
}

/**
 * 获取智能默认时间段
 * @returns {object} - 返回合适的默认日期和时间段
 */
const getDefaultTimeSlot = () => {
    const now = new Date();
    const currentTime = now.getHours() * 60 + now.getMinutes();
    
    // 查找今天还可用的时间段
    const availableToday = pickTimeDict.value.find(slot => {
        const [endHour, endMinute] = slot.value.split('-')[1].split(':').map(Number);
        const endTime = endHour * 60 + endMinute;
        return endTime > currentTime;
    });
    
    if (availableToday) {
        return { pickupDate: 0, pickupTimeRange: availableToday.value };
    } else {
        // 今天没有可用时间段，选择明天
        return { pickupDate: 1, pickupTimeRange: '14:00-15:00' };
    }
}

const getDeliveryServiceListData = (param) => {
    if(!param){
        return;
    }
    getDeliveryServiceList({deliveryId: param}).then(res => {
        deliveryServiceList.value = res.data;
    })
}
const handleFormChange = async (val) => {
    const { prop, form } = val;

  let acceptOrder= currentLogisticsOrder.value.status==102

  if(prop=='sendAddrList'){
   acceptOrder? await take():await rev()
  }

  if(prop=='revAddrList'){
    acceptOrder? await rev():await take()

  }

  async function take() {
    let obj = receiverAddressList.value.find(i => i.id == (acceptOrder ? form.sendAddrList+'':form.revAddrList+''))
    if(acceptOrder){
      form.senderName = obj.userName
      form.senderMobile = obj.telNumber
      form.senderProvinceCode = obj.provinceCode + ''
      form.senderProvince = obj.provinceName
      form.senderCityCode = obj.cityCode + ''
      form.senderCity = obj.cityName
      form.senderAreaCode = obj.countyCode + ''
      form.senderArea = obj.countyName
      form.senderAddress = obj.detailInfo
    }else{
      form.receiverName = obj.userName
      form.receiverMobile = obj.telNumber
      form.receiverProvinceCode = obj.provinceCode + ''
      form.receiverProvince = obj.provinceName
      form.receiverCityCode = obj.cityCode + ''
      form.receiverCity = obj.cityName
      form.receiverAreaCode = obj.countyCode + ''
      form.receiverArea = obj.countyName
      form.receiverAddress = obj.detailInfo
    }

    if(acceptOrder){
      let province = senderProvinces.value.filter(i => i.areaCode == obj.provinceCode + '')[0]
      senderCitys.value = await getSubRegion(province.id)
      let city = senderCitys.value.filter(i => i.areaCode == obj.cityCode + '')[0]
      senderAreas.value = await getSubRegion(city.id)
    }else{
      let province = revProvinces.value.filter(i => i.areaCode == obj.provinceCode + '')[0]
      revCitys.value = await getSubRegion(province.id)
      let city = revCitys.value.filter(i => i.areaCode == obj.cityCode + '')[0]
      revAreas.value = await getSubRegion(city.id)
    }
  }

  async function rev() {
    let obj = shopAddressList.value.find(i => i.id == (acceptOrder ? form.revAddrList+'':form.sendAddrList+''))
    if(acceptOrder){
      form.receiverName = obj.name
      form.receiverMobile = obj.mobile
      form.receiverProvinceCode = obj.provinceCode + ''
      form.receiverProvince = obj.province
      form.receiverCityCode = obj.cityCode + ''
      form.receiverCity = obj.city
      form.receiverAreaCode = obj.areaCode + ''
      form.receiverArea = obj.area
      form.receiverAddress = obj.address
    }else{
      form.senderName = obj.name
      form.senderMobile = obj.mobile
      form.senderProvinceCode = obj.provinceCode + ''
      form.senderProvince = obj.province
      form.senderCityCode = obj.cityCode + ''
      form.senderCity = obj.city
      form.senderAreaCode = obj.areaCode + ''
      form.senderArea = obj.area
      form.senderAddress = obj.address
    }

    if(acceptOrder){
      let province = revProvinces.value.filter(i => i.areaCode == obj.provinceCode + '')[0]
      revCitys.value = await getSubRegion(province.id)
      let city = revCitys.value.filter(i => i.areaCode == obj.cityCode + '')[0]
      revAreas.value = await getSubRegion(city.id)
    }else{
      let province = senderProvinces.value.filter(i => i.areaCode == obj.provinceCode + '')[0]
      senderCitys.value = await getSubRegion(province.id)
      let city = senderCitys.value.filter(i => i.areaCode == obj.cityCode + '')[0]
      senderAreas.value = await getSubRegion(city.id)
    }
  }

  if(prop=='senderProvinceCode'){
    let obj= senderProvinces.value.filter(i=>i.areaCode==form.senderProvinceCode)[0]
    form.senderProvince = obj.label
    form.senderCityCode= null
    form.senderAreaCode =null
     senderCitys.value = await getSubRegion( obj.id)
     
   }
   if(prop=='senderCityCode') {
     let obj= senderCitys.value.filter(i=>i.areaCode==form.senderCityCode)[0]
     form.senderCity = obj.label
     form.senderAreaCode =null
     senderAreas.value = await getSubRegion( obj.id)

   }
   if(prop=='senderAreaCode'){
     let obj= senderAreas.value.filter(i=>i.areaCode==form.senderAreaCode)[0]
     form.senderArea = obj.label
   }


   if(prop=='receiverProvinceCode'){
    let obj= revProvinces.value.filter(i=>i.areaCode==form.receiverProvinceCode)[0]
    form.receiverProvince = obj.label
    form.receiverCityCode= null
    form.receiverAreaCode =null
     revCitys.value = await getSubRegion( obj.id)
     
   }
   if(prop=='receiverCityCode') {
     let obj= revCitys.value.filter(i=>i.areaCode==form.receiverCityCode)[0]
     form.receiverCity = obj.label
     form.receiverAreaCode =null
     revAreas.value = await getSubRegion( obj.id)

   }
   if(prop=='receiverAreaCode'){
     let obj= revAreas.value.filter(i=>i.areaCode==form.receiverAreaCode)[0]
     form.receiverArea = obj.label
   }


    if(prop == 'deliveryId'){
        getDeliveryServiceListData(form.deliveryId)
    }
    
    // 处理日期变化
    if(prop == 'pickupDate'){
        console.log('日期变化:', form.pickupDate);
        
        // 更新响应式日期变量，这会触发计算属性重新计算
        selectedPickupDate.value = form.pickupDate;
        logisticsPickupData.value.pickupDate = form.pickupDate;
        
        // 如果选择今天，检查当前选择的时间段是否还可用
        if(form.pickupDate === 0) {
            // 延迟执行，确保计算属性已更新
            setTimeout(() => {
                const currentTimeRange = form.pickupTimeRange;
                const availableSlots = availableTimeSlots.value;
                console.log('当前选择的时间段:', currentTimeRange);
                console.log('可用时间段:', availableSlots);
                
                // 如果当前选择的时间段不在可用列表中，自动选择第一个可用的时间段
                if(!availableSlots.find(slot => slot.value === currentTimeRange)) {
                    if(availableSlots.length > 0) {
                        // 更新表单数据
                        logisticsPickupData.value.pickupTimeRange = availableSlots[0].value;
                    } else {
                        ElMessage.warning('今天已没有可用的取件时间段，请选择明天或后天');
                        // 自动切换到明天
                        selectedPickupDate.value = 1;
                        logisticsPickupData.value.pickupDate = 1;
                        logisticsPickupData.value.pickupTimeRange = '14:00-15:00';
                    }
                }
            }, 100);
        }
    }
}
/**
 * 操作选中
 */
const handleMenu = async (val) => {
    const { index, row, value } = val;
    switch (value) {
        
        case "view":
            getOrder(row.id).then((res) => {
                rowData.value = res.data.orderVo;
                infoVisible.value = true;
                // getLogisticsApi({ id: row.id }).then(res => { if (rowData.value) rowData.value.orders = res.data?.orders || null })
            })
            break;
        case "set":
            // 打开发货弹窗
            currentShipmentOrder.value = row;
            shipmentData.value = {
                trackingNo: ''
            };
            shipmentVisible.value = true;
            break;
        case "del":
            ElMessageBox.confirm('订单是否删除？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning', }).then(() => {
                delOrder({ orderId: row.id }).then(res => resMsg(res))
            }).catch(() => { })
            break;
        case "agree-refund":
            // 同意退款
            ElMessageBox.confirm(
                `确认同意退款吗？\n订单号：${row.orderNo}\n退款金额：￥${row.amount}`, 
                '确认退款', 
                { 
                    confirmButtonText: '确认退款', 
                    cancelButtonText: '取消', 
                    type: 'warning',
                    dangerouslyUseHTMLString: false
                }
            ).then(() => {
                handleAgreeRefund(row);
            }).catch(() => { })
            break;
        case "reject-refund":
            // 打开驳回退款弹窗
            currentRejectOrder.value = row;
            rejectRefundData.value = {
                rejectReason: ''
            };
            rejectRefundVisible.value = true;
            break;
        case "send-logistics":
        case "take-logistics":

              let list =   await getReceiverAddressListData(row.userId)
              receiverAddressList.value =   list

              if(value=='take-logistics'){
                var address= list.find(item => item.id == row.receiverAddressId)
                var province= senderProvinces.value.filter(i=>i.areaCode==address.provinceCode)[0]
                senderCitys.value=  await getSubRegion(province.id)
                var city= senderCitys.value.filter(i=>i.areaCode==address.cityCode)[0]
                senderAreas.value=  await getSubRegion(city.id)
              }else{
                var address= list.find(item => item.id == row.receiverAddressId)
                var province= revProvinces.value.filter(i=>i.areaCode==address.provinceCode)[0]
                revCitys.value=  await getSubRegion(province.id)
                var city= revCitys.value.filter(i=>i.areaCode==address.cityCode)[0]
                revAreas.value=  await getSubRegion(city.id)
              }

              let shopAddrList= await getAllAddressData()
              let shopDefaultAddr= shopAddrList.default
              shopAddressList.value= shopAddrList.list
              defaultShopAddress.value= shopDefaultAddr

          if(value=='take-logistics'){
            var province= revProvinces.value.filter(i=>i.areaCode==shopDefaultAddr.provinceCode)[0]
            revCitys.value=  await getSubRegion(province.id)
            var city= revCitys.value.filter(i=>i.areaCode==shopDefaultAddr.cityCode)[0]
            revAreas.value=  await getSubRegion(city.id)
          }else{
            var province= senderProvinces.value.filter(i=>i.areaCode==shopDefaultAddr.provinceCode)[0]
            senderCitys.value=  await getSubRegion(province.id)
            var city= senderCitys.value.filter(i=>i.areaCode==shopDefaultAddr.cityCode)[0]
            senderAreas.value=  await getSubRegion(city.id)
          }


            // 打开物流取货弹窗，获取订单详情以填充表单
            getOrder(row.id).then((res) => {
                currentLogisticsOrder.value = row;
                const orderDetail = res.data.orderVo;
                const addressInfo = orderDetail.addressVo || {};
                
                // 解析上门时间
                const parsedTime = parsePickTimeRange(orderDetail.pickTimeRange);
                 
                // 更新响应式日期变量
                selectedPickupDate.value = parsedTime.pickupDate;
                
                logisticsPickupData.value = {
                    orderType: 1, // 1表示取货订单
                    orderNo: row.orderNo, // 订单编号（只读显示）
                    deliveryId: '',
                    deliveryName: '',
                    bizId: '',
                    customRemark: '',
                    tagid: 0,
                    pickupDate: parsedTime.pickupDate,
                    pickupTimeRange: parsedTime.pickupTimeRange,
                    // 发件人信息（需要手动填写）
                    senderName: '',
                    senderTel: '',
                    senderMobile: '',
                    senderCompany: '',
                    senderPostCode: '',
                    senderCountry: '中国',
                    senderProvince: value=='take-logistics'?  address.provinceName :shopDefaultAddr.province,
                    senderProvinceCode: '',
                    senderCity: value=='take-logistics'? address.cityName  :shopDefaultAddr.city,
                    senderCityCode: '', 
                    senderArea: value=='take-logistics'? address.countyName  :shopDefaultAddr.area,
                    senderAreaCode: '',
                    senderAddress: value=='take-logistics'? address.detailInfo  :shopDefaultAddr.address,
                    // 收件人信息（从订单地址自动填充）
                    receiverName:  '',
                    receiverTel: '',
                    receiverMobile:  '',
                    receiverCompany: '',
                    receiverPostCode: '',
                    receiverCountry: '中国',
                    receiverProvince: value=='take-logistics'? shopDefaultAddr.province : address.provinceName,
                    receiverProvinceCode: '',
                    receiverCity: value=='take-logistics'? shopDefaultAddr.city : address.cityName,
                    receiverCityCode:  '',
                    receiverArea: value=='take-logistics'? shopDefaultAddr.area : address.countyName,
                    receiverAreaCode:  '',
                    receiverAddress: value=='take-logistics'? shopDefaultAddr.address : address.detailInfo,
                    // 包裹信息（从订单商品信息填充）
                    cargoCount: 1,
                    cargoWeight: 1.0,
                    cargoSpaceX: 10,
                    cargoSpaceY: 10,
                    cargoSpaceZ: 10,
                    // 商品信息（从订单商品填充）
                    goodsName: orderDetail.orderInfoVos?.[0]?.goodsName || '运动鞋清洗',
                    goodsCount: orderDetail.orderInfoVos?.reduce((sum, item) => sum + item.goodsNum, 0) || 1,
                    // 增值服务
                    useInsured: 0,
                    insuredValue: 0.0,
                    serviceType: 0,
                    serviceName: '',
                    shopImgUrl: orderDetail.orderInfoVos?.[0]?.goodsImg || '',
                    shopGoodsName: orderDetail.orderInfoVos?.[0]?.goodsName || '',
                    shopGoodsCount: orderDetail.orderInfoVos?.reduce((sum, item) => sum + item.goodsNum, 0) || 1,
                };

                logisticsPickupVisible.value = true;
            }).catch((error) => {
                ElMessage.error('获取订单详情失败');
                console.error('获取订单详情错误:', error);
            });
            
          
            break;
    }
}
const resMsg = (res) => {
    if (res.code == 200) {
        ElMessage.success(res.msg);
        getData();
    } else {
        ElMessage.error(res.msg)
    }
}

/**
 * 确认发货
 */
const confirmShipment = (formData) => {
    console.log(formData);
    if (!formData.trackingNo || formData.trackingNo.trim() === '') {
        ElMessage.error('请输入物流单号');
        return;
    }
    
    const params = {
        orderId: currentShipmentOrder.value.id,
        trackingNo: formData.trackingNo.trim()
    };
    
    setTrackingNo(params).then(res => {
        if (res.code == 200) {
            ElMessage.success('发货成功');
            shipmentVisible.value = false;
            getData();
        } else {
            ElMessage.error(res.msg || '发货失败');
        }
    }).catch(error => {
        ElMessage.error('发货失败');
        console.error('发货错误:', error);
    });
}

/**
 * 处理同意退款
 */
const handleAgreeRefund = (order) => {
    const refundData = {
        refundFee: parseFloat(order.amount), // 退款金额
        outTradeNo: order.orderNo, // 商户订单号
        totalFee: parseFloat(order.amount), // 订单总金额
        tradeType: 'JSAPI',
        extendParams: {
            "tenantId": '0'
        },
        businessTypeCode: "mall"
    };
    
    wechatRefund(refundData).then(res => {
    
        if (res.code == 500) {
            ElMessage.error(res.msg || '退款失败');
           
        }else if(res.refundResult == true){
            ElMessage.success('退款成功');
            getData();
        } else {
            ElMessage.error(res.message || '退款失败');
        }
    }).catch(error => {
        ElMessage.error('退款失败');
        console.error('退款错误:', error);
    });
}

/**
 * 确认驳回退款
 */
const confirmRejectRefund = (formData) => {
    if (!formData.rejectReason || formData.rejectReason.trim() === '') {
        ElMessage.error('请输入驳回原因');
        return;
    }
    
    const params = {
        orderId: currentRejectOrder.value.id,
        rejectReason: formData.rejectReason.trim()
    };
    
    rejectRefund(params).then(res => {
        if (res.code == 200) {
            ElMessage.success('驳回退款成功');
            rejectRefundVisible.value = false;
            getData();
        } else {
            ElMessage.error(res.msg || '驳回退款失败');
        }
    }).catch(error => {
        ElMessage.error('驳回退款失败');
        console.error('驳回退款错误:', error);
    });
}

/**
 * 确认物流取货
 */
const confirmLogisticsPickup = (formData) => {
    console.log(formData);
    // 验证必填字段
    if (!formData.deliveryId) {
        ElMessage.error('请选择快递账号');
        return;
    }
    
    // 构造商品详情列表
    const cargoDetailList = [{
        name: formData.goodsName || '运动鞋清洗',
        count: formData.goodsCount || 1
    }];
    
    // 构造商品信息列表（用于物流展示）
    const shopDetailList = [{
        shopImgUrl: formData.shopImgUrl || '',
        shopGoodsName: formData.shopGoodsName || '运动鞋清洗',
        shopGoodsCount: formData.shopGoodsCount || 1
    }];
    let deliveryId = formData.deliveryId;
    let deliveryName = accountList.value.find(item => item.deliveryId == deliveryId)?.deliveryName;
    let serviceType = formData.serviceType;
    let serviceName = deliveryServiceList.value.find(item => item.service_type == serviceType)?.service_name;
    let bizId = accountList.value.find(item => item.deliveryId == deliveryId)?.bizId;

    // 组合上门时间
    const generatePickupDateTime = (pickupDate, pickupTimeRange) => {
        const today = new Date();
        const targetDate = new Date(today);
        targetDate.setDate(today.getDate() + (pickupDate || 0));
        
        const dateStr = targetDate.toISOString().split('T')[0]; // 格式：YYYY-MM-DD
        return `${dateStr} ${pickupTimeRange || '14:00-15:00'}`;
    };

    const pickupDateTime = generatePickupDateTime(formData.pickupDate, formData.pickupTimeRange);
    let date = pickupDateTime.split(' ')[0]
    let time = pickupDateTime.split(' ')[1].split('-')[1]
    let expectTime = new Date(date + ' ' + time+":00").getTime()/1000
    const logisticsParams = {
        orderId: currentLogisticsOrder.value.id,
        orderType:   currentLogisticsOrder.value.status==102 ? 1: 2, // 1表示取货订单
        deliveryId: formData.deliveryId,
        deliveryName: deliveryName,
        bizId: bizId,
        customRemark: formData.customRemark || '',
        tagid: formData.tagid || 0,
        expectTime: expectTime, // 传递完整的日期时间字符串
        takeMode: 0, // 默认上门取件
        
        // 发件人信息
        senderName: formData.senderName,
        senderTel: formData.senderTel || '',
        senderMobile: formData.senderMobile,
        senderCompany: formData.senderCompany || '',
        senderPostCode: formData.senderPostCode || '',
        senderCountry: formData.senderCountry || '中国',
        senderProvinceCode: formData.senderProvinceCode,
        senderCityCode: formData.senderCityCode,
        senderAreaCode: formData.senderAreaCode,
        senderAddress: formData.senderAddress,
        
        senderProvince: logisticsPickupData.value.senderProvince,
        senderCity: logisticsPickupData.value.senderCity,
        senderArea: logisticsPickupData.value.senderArea,

        // 收件人信息
        receiverName: formData.receiverName,
        receiverTel: formData.receiverTel || '',
        receiverMobile: formData.receiverMobile,
        receiverCompany: formData.receiverCompany || '',
        receiverPostCode: formData.receiverPostCode || '',
        receiverCountry: formData.receiverCountry || '中国',
        receiverProvinceCode: formData.receiverProvinceCode,
        receiverCityCode: formData.receiverCityCode,
        receiverAreaCode: formData.receiverAreaCode,
        receiverAddress: formData.receiverAddress,

        receiverProvince: logisticsPickupData.value.receiverProvince,
        receiverCity: logisticsPickupData.value.receiverCity,
        receiverArea: logisticsPickupData.value.receiverArea,
        
        // 包裹信息
        cargoCount: formData.cargoCount || 1,
        cargoWeight: formData.cargoWeight || 1.0,
        cargoSpaceX: formData.cargoSpaceX || 10,
        cargoSpaceY: formData.cargoSpaceY || 10,
        cargoSpaceZ: formData.cargoSpaceZ || 10,
        cargoDetailList: cargoDetailList,
        shopDetailList: shopDetailList,
        
        // 增值服务
        useInsured: formData.useInsured || 0,
        insuredValue: formData.insuredValue || 0.0,
        serviceType: formData.serviceType || 0,
        serviceName: serviceName || ''
    };
    
    // 调用物流订单创建接口
    createLogisticsOrder(logisticsParams).then(res => {
        if (res.code == 200) {
            ElMessage.success('物流取货订单创建成功');
            logisticsPickupVisible.value = false;
            getData();
        } else {
            ElMessage.error(res.msg || '物流取货订单创建失败');
        }
    }).catch(error => {
        ElMessage.error('物流取货订单创建失败');
        console.error('物流取货错误:', error);
    });
}
// 表格配置项
const option = reactive({
    showSearch: true,  // 显示隐藏
    searchLabelWidth: 90,  // 搜索标题宽度
    /** 搜索字段配置项 */
    searchItem: [
        { type: "input", label: "订单号", prop: "orderNo", max: 20, verify: null, isOmit: false, default: null },
        {
            type: "select", label: "订单状态", prop: "status", default: null,
            dicData: [
                { value: 0, label: '待支付' }, { value: 1, label: '待发货' }, { value: 5, label: '退款中' }, { value: 3, label: '已发货' },
                { value: 2, label: '超时未支付' }, { value: 4, label: '已完成' }, { value: 6, label: '已退款' }, { value: 7, label: '申请退款' }
              , { value: 8, label: '退款驳回' }, { value: 9, label: '退款失败' }
            ]
        },
        {
            type: "datetimerange", label: "创建时间", prop: "Time", format: "YYYY-MM-DD HH:mm:ss", valueFormat: "YYYY-MM-DD HH:mm:ss",
            category: "datetimerange", isOmit: true, default: []
        },
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [],
    operation: [],
    /** 表格顶部右侧 toobar 配置项 */
    toolbar: { isShowToolbar: true, isShowSearch: true },
    openSelection: false,		// 是否开启多选
    /** 序号下标配置项 */
    index: {
        openIndex: true,
        indexFixed: true,
        indexWidth: 70
    },
    /** 行列合并配置项 */
    methods: () => { },
    /** 表格字段配置项 */
    tableItem: [
        { type: 'text', label: '订单号', prop: 'orderNo', width: 190, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'custom', label: '订单信息', prop: 'orderInfoVos', width: 300, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '订单金额', prop: 'amount', width: 110, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'custom', label: '订单状态', prop: 'status', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'custom', label: '支付方式', prop: 'payWay', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '创建时间', prop: 'insertTime', width: 160, fixed: false, sortable: false, isShow: true, isOmit: false },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 240,
        fixed: false,
    },
    menuItemBtn: [
        { type: 'primary', isShow: true, icon: 'View', label: '详情', value: 'view', hasPermi: [] },
        {
            type: 'primary', isShow: true, icon: 'Edit', label: '订单发货', value: 'set', hasPermi: [],
            judge: (row) => { return row.status == 1 && row.deliveryStatus == 0 && setting.order.setTrackNo}
        },
        {
            type: 'success', isShow: true, icon: 'Check', label: '同意退款', value: 'agree-refund', hasPermi: [],
            judge: (row) => { return row.status == 7||row.status == 9 }
        },
        {
            type: 'warning', isShow: true, icon: 'Close', label: '驳回退款', value: 'reject-refund', hasPermi: [],
            judge: (row) => { return row.status == 7 }
        },
        {
            type: 'danger', isShow: true, icon: 'Delete', label: '删除', value: 'del', hasPermi: [],
            judge: (row) => { return row.status == 2 || (row.status == 1 && row.deliveryStatus == 0 && row.payWay == 2) }
        },
        {
            type: 'primary', isShow: true, icon: 'Edit', label: '物流取货', value: 'take-logistics', hasPermi: [],
            judge: (row) => { return row.status == 102 }
        },{
            type: 'primary', isShow: true, icon: 'Edit', label: '物流送货', value: 'send-logistics', hasPermi: [],
            judge: (row) => { return row.status == 201 || row.status == 1}
        },
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
})
// 详情配置项
const optionInfo = reactive({
    dialogClass: 'dialog_md',
    labelWidth: 20,
    /** 表格详情配置项 */
    tableInfoItem: [
        {
          title: '购买人信息', column: 2, infoData: [
            { type: 'custom', label: '购买人姓名/手机号', prop: 'buyer', isShow: true },

          ]
        },
        {
            title: '订单信息', column: 2, infoData: [
                { type: 'custom', label: '购买人昵称', prop: 'nickname', isShow: true },
                { type: 'custom', label: '支付方式', prop: 'payWay', isShow: true },
                { type: 'text', label: '订单号', prop: 'orderNo', isShow: true },
                { type: 'text', label: '订单金额', prop: 'amount', isShow: true, append: '元' },
                { type: 'custom', label: '订单状态', prop: 'status', isShow: true },
                { type: 'text', label: '退款原因', prop: 'refundReason', isShow: true },
                { type: 'text', label: '退款驳回原因', prop: 'rejectReason', isShow: true },
                { type: 'text', label: '创建时间', prop: 'insertTime', isShow: true },
                { type: 'custom', label: '订单详细', prop: 'orderInfoVos', isShow: true, span: 2 },
                { type: 'text', label: '取货单号', prop: 'pickTrackingNo', isShow: true, span: 2 },
                { type: 'text', label: '送货单号', prop: 'trackingNo', isShow: true, span: 2 },
            ]
        },
    ],
})

// 发货弹窗配置项
const shipmentDialogOption = reactive({
    dialogTitle: '订单发货',
    labelWidth: '120px',
    isShowFooter: true,
    formitem: [
        
        {
            type: 'input',
            label: '物流单号',
            prop: 'trackingNo',
            placeholder: '请输入物流单号',
            maxlength: 50,
        }
    ],
    rules: {
        trackingNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }],
  }
})

// 驳回退款弹窗配置项
const rejectRefundDialogOption = reactive({
    dialogTitle: '驳回退款',
    labelWidth: '120px',
    isShowFooter: true,
    formitem: [
        {
            type: 'input',
            label: '驳回原因',
            prop: 'rejectReason',
            placeholder: '请输入驳回原因',
            maxlength: 200,
            rows: 4,
        }
    ],
    rules: {
        rejectReason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }],
    }
})

// 物流取货弹窗配置项
const logisticsPickupDialogOption = reactive({
    dialogTitle: '物流取货',
    labelWidth: '120px',
    isShowFooter: true,
    dialogClass: 'dialog_lg',
    formitem: [
        {
            type: 'input',
            label: '订单编号',
            prop: 'orderNo',
            isShow: true,
            disabled: true,
            default: computed(() => {
                return logisticsPickupData.value.orderNo;
            })
        },
        {
            type: 'select',
            label: '快递账号',
            prop: 'deliveryId',
            placeholder: '请选择快递账号',
            dicData: computed(() => accountList.value.map(item => ({
                label: item.deliveryName,
                value: item.deliveryId
            })))
        },
        {
            type: 'select',
            label: '服务类型',
            prop: 'serviceType',
            placeholder: '请选择服务类型',
            dicData: computed(() => deliveryServiceList.value.map(item => ({
                label: item.service_name,
                value: item.service_type
            })))
        },
     
        {
            type: 'title',
            label: '上门预约时间',
            span: 24
        },
        {
            type: 'radio',
            label: '上门日期',
            prop: 'pickupDate',
            dicData: [
                { value: 0, label: '今天' },
                { value: 1, label: '明天' },
                { value: 2, label: '后天' }
            ],
            default: computed(() => {
                return selectedPickupDate.value;
            })
        },
        {
            type: 'radio',
            label: '上门时间',
            prop: 'pickupTimeRange',
            dicData: availableTimeSlots,
            default: computed(() => {
                return logisticsPickupData.value.pickupTimeRange;
            })
        },
      
        // 发件人信息
        {
            type: 'title',
            label: '发件人信息',
            span: 24
        },{
            type: 'select',
            label: '发件人地址',
            prop: 'sendAddrList',
            placeholder: '请选择地址',
            default: computed(() => {
                if(currentLogisticsOrder.value.status==102){
                    return currentLogisticsOrder.value.receiverAddressId
                }else{
                    return   defaultShopAddress.value.id
                }
            }),
            dicData: computed(() => {
                if(currentLogisticsOrder.value.status==102){
                 return   receiverAddressList.value.map(item => ({
                        label: item.provinceName+item.cityName+item.countyName+item.detailInfo,
                        value: item.id
                    }))
                }else{
                    return   shopAddressList.value.map(item => ({
                        label: item.province+item.city+item.area+item.address,
                        value: item.id
                    }))
                }
            })
        },
        {
            type: 'input',
            label: '发件人姓名',
            prop: 'senderName',
            placeholder: '请输入发件人姓名',
            default: computed(() => {
                if(currentLogisticsOrder.value.status==102){
                   let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.userName
                }else{
                    return defaultShopAddress.value.name
                }
            })
        },
        {
            type: 'input',
            label: '发件人电话',
            prop: 'senderMobile',
            placeholder: '请输入发件人手机号',
            default: computed(() => {
                if(currentLogisticsOrder.value.status==102){
                   let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.telNumber
                }else{
                    return defaultShopAddress.value.mobile
                }
            })
        },
       
       
        {
            type: 'select',
            label: '发件省份',
            prop: 'senderProvinceCode',
            placeholder: '请选择省份',
            dicData: senderProvinces, // 需要从字典获取
            customLabel: "label",
            customValue: "areaCode",
            default: computed( () => {
                if(currentLogisticsOrder.value.status==102){
                   let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.provinceCode+''
                }else{
                    return  defaultShopAddress.value.provinceCode+''
                }
            })
        },
        {
            type: 'select',
            label: '发件城市',
            prop: 'senderCityCode',
            placeholder: '请选择城市',
            dicData: senderCitys, // 需要从字典获取
            customLabel: "label",
            customValue: "areaCode",
            default: computed(() => {
                if(currentLogisticsOrder.value.status==102){
                   let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.cityCode+''
                }else{
                    return  defaultShopAddress.value.cityCode+''
                }
            })
        },
        {
            type: 'select',
            label: '发件区县',
            prop: 'senderAreaCode',
            placeholder: '请选择区县',
            dicData: senderAreas, // 需要从字典获取
            customLabel: "label",
            customValue: "areaCode",
            default: computed(() => {
                if(currentLogisticsOrder.value.status==102){
                   let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.countyCode+''
                }else{
                    return defaultShopAddress.value.areaCode+''
                }
            })
        },
        {
            type: 'input',
            label: '发件详细地址',
            prop: 'senderAddress',
            placeholder: '请输入详细地址',
            default: computed(() => {
                if(currentLogisticsOrder.value.status==102){
                   let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.detailInfo
                }else{
                    return defaultShopAddress.value.address
                }
            })
        },
        // 收件人信息
        {
            type: 'title',
            label: '收件人信息',
            span: 24
        },
        {
            type: 'select',
            label: '收件人地址',
            prop: 'revAddrList',
            placeholder: '请选择地址',
            dicData: computed(() => {
                if(currentLogisticsOrder.value.status==102){
                 return   shopAddressList.value.map(item => ({
                        label: item.province+item.city+item.area+item.address,
                        value: item.id
                    }))
                }else{
                    return   receiverAddressList.value.map(item => ({
                        label: item.provinceName+item.cityName+item.countyName+item.detailInfo,
                        value: item.id
                    }))
                }
            }),
            default: computed( () => {
                if(currentLogisticsOrder.value.status==102){
                   return defaultShopAddress.value.id
                }else{
                    return currentLogisticsOrder.value.receiverAddressId
                }
            })
        },
        {
            type: 'input',
            label: '收件人姓名',
            prop: 'receiverName',
            placeholder: '请输入收件人姓名',
            default: computed( () => {
                if(currentLogisticsOrder.value.status==102){
                   return defaultShopAddress.value.name
                }else{
                    let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.userName
                }
            })
        },
        {
            type: 'input',
            label: '收件人电话',
            prop: 'receiverMobile',
            placeholder: '请输入收件人手机号',
            default: computed( () => {
                if(currentLogisticsOrder.value.status==102){
                   return defaultShopAddress.value.mobile
                }else{
                    let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.telNumber
                }
            })
        },
       
       
        {
            type: 'select',
            label: '收件省份',
            prop: 'receiverProvinceCode',
            placeholder: '请选择省份',
            dicData: revProvinces, // 需要从字典获取
            customLabel: "label",
            customValue: "areaCode",
            default: computed( () => {
                if(currentLogisticsOrder.value.status==102){
                   return defaultShopAddress.value.provinceCode+''
                }else{
                    let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.provinceCode+''
                }
            })
        },
        {
            type: 'select',
            label: '收件城市',
            prop: 'receiverCityCode',
            placeholder: '请选择城市',
            dicData: revCitys, // 需要从字典获取
            customLabel: "label",
            customValue: "areaCode",
            default: computed( () => {
                if(currentLogisticsOrder.value.status==102){
                   return defaultShopAddress.value.cityCode+''
                }else{
                    let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.cityCode+''
                }
            })
        },
        {
            type: 'select',
            label: '收件区县',
            prop: 'receiverAreaCode',
            placeholder: '请选择区县',
            dicData: revAreas, // 需要从字典获取
            customLabel: "label",
            customValue: "areaCode",
            default: computed( () => {
                if(currentLogisticsOrder.value.status==102){
                   return defaultShopAddress.value.areaCode+''
                }else{
                    let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.countyCode+''
                }
            })
        },
        {
            type: 'input',
            label: '收件详细地址',
            prop: 'receiverAddress',
            placeholder: '请输入详细地址',
            default: computed( () => {
                if(currentLogisticsOrder.value.status==102){
                   return defaultShopAddress.value.address
                }else{
                    let addressId= currentLogisticsOrder.value.receiverAddressId
                   let address= receiverAddressList.value.find(item => item.id == addressId)
                   return address.detailInfo
                }
            })
        },
        // 包裹信息
        {
            type: 'title',
            label: '包裹信息',
            span: 24   
        },
        {
            type: 'input',
            category: 'number',
            label: '包裹数量',
            prop: 'cargoCount',
            placeholder: '请输入包裹数量',
            default: 1,
            min: 1,
        },
        {
            type: 'input',
            category: 'number',
            label: '包裹重量(kg)',
            prop: 'cargoWeight',
            placeholder: '请输入包裹重量',
            min: 0.1,
            step: 0.1,
            default: 1,
        },
        {
            type: 'input',
            category: 'number',
            label: '包裹长度(cm)',
            prop: 'cargoSpaceX',
            placeholder: '请输入包裹长度',
            min: 1,
            default: 10,
        },
        {
            type: 'input',
            category: 'number',
            label: '包裹宽度(cm)',
            prop: 'cargoSpaceY',
            placeholder: '请输入包裹宽度',
            min: 1,
            default: 10,
        },
        {
            type: 'input',
            category: 'number',
            label: '包裹高度(cm)',
            prop: 'cargoSpaceZ',
            placeholder: '请输入包裹高度',
            min: 1,
            default: 10,
        },
        {
            type: 'input',
            label: '商品名称',
            prop: 'goodsName',
            placeholder: '请输入商品名称',
            default: computed(() => {
                return logisticsPickupData.value.goodsName;
            })
        },
        {
            type: 'number',
            label: '商品数量',
            prop: 'goodsCount',
            placeholder: '请输入商品数量',
            min: 1,
            default: computed(() => {
                return logisticsPickupData.value.goodsCount;
            })
        },
      
        {
            type: 'radio',
            label: '是否保价',
            prop: 'useInsured',
            default: 0,
            dicData: [
                { value: 0, label: '否' },
                { value: 1, label: '是' }
            ]
        },
        {
            type: 'input',
            category: 'number',
            label: '保价金额(元)',
            prop: 'insuredValue',
            placeholder: '请输入保价金额',
            min: 0,
            step: 0.01,
            default: null,
        },
        {
            type: 'input',
            label: '备注信息',
            prop: 'customRemark',
            placeholder: '请输入备注信息',
            maxlength: 200,
            default: null,
        },
        {
            type: 'title',
            label: '商品信息(会展示到物流服务通知和电子面单中)',
            span: 24   
        },
        {
            type: 'input',
            label: '商品名称',
            prop: 'shopGoodsName',
            placeholder: '请输入商品名称',
            default: computed(() => {
                return logisticsPickupData.value.shopGoodsName;
            })
        },
        {
            type: 'upload',
            fileType: "img",
            disabled: true,
            label: '商品图片',
            prop: 'shopImgUrl',
            placeholder: '请输入商品名称',
            default: computed(() => {
                return logisticsPickupData.value.shopImgUrl;
            })
        },
        {
            type: 'input',
            category: 'number',
            label: '商品数量',
            prop: 'shopGoodsCount',
            placeholder: '请输入商品数量',
            min: 1,
            default: computed(() => {
                return logisticsPickupData.value.shopGoodsCount;
            })
        },

    ],
    rules: {
        deliveryId: [{ required: true, message: '请选择快递账号', trigger: 'change' }],
        serviceType: [{ required: true, message: '请选择服务类型', trigger: 'blur' }],
        bizId: [{ required: true, message: '请输入客户编码', trigger: 'blur' }],
        senderName: [{ required: true, message: '请输入发件人姓名', trigger: 'blur' }],
        senderMobile: [
            { required: true, message: '请输入发件人手机号', trigger: 'blur' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        senderProvinceCode: [{ required: true, message: '请选择发件省份', trigger: 'change' }],
        senderCityCode: [{ required: true, message: '请选择发件城市', trigger: 'change' }],
        senderAreaCode: [{ required: true, message: '请选择发件区县', trigger: 'change' }],
        senderAddress: [{ required: true, message: '请输入发件详细地址', trigger: 'blur' }],
        receiverName: [{ required: true, message: '请输入收件人姓名', trigger: 'blur' }],
        receiverMobile: [
            { required: true, message: '请输入收件人手机号', trigger: 'blur' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        receiverProvinceCode: [{ required: true, message: '请选择收件省份', trigger: 'change' }],
        receiverCityCode: [{ required: true, message: '请选择收件城市', trigger: 'change' }],
        receiverAreaCode: [{ required: true, message: '请选择收件区县', trigger: 'change' }],
        receiverAddress: [{ required: true, message: '请输入收件详细地址', trigger: 'blur' }],
        cargoCount: [{ required: true, message: '请输入包裹数量', trigger: 'blur' }],
        cargoWeight: [{ required: true, message: '请输入包裹重量', trigger: 'blur' }],
        cargoSpaceX: [{ required: true, message: '请输入包裹长度', trigger: 'blur' }],
        cargoSpaceY: [{ required: true, message: '请输入包裹宽度', trigger: 'blur' }],
        cargoSpaceZ: [{ required: true, message: '请输入包裹高度', trigger: 'blur' }],
        goodsName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        goodsCount: [{ required: true, message: '请输入商品数量', trigger: 'blur' }]
    }
})

init();
</script>

<style scoped>
.body {
    display: flex;
    flex-direction: column;
    width: 100%;

    .order {
        background-color: white;
        margin: 0px;
        width: 100%;
        display: flex;
        align-items: center;
        margin-bottom: 5px;

        .img {
            width: 46px;
            height: 46px;
            margin: 0px;
            flex-grow: 0;
        }

        .content {
            width: 100%;
            padding: 0px;
            margin-left: 10px;
            overflow: hidden;
            flex-grow: 1;
            .info_bottom{
              float: left;
            }
            .info {
                width: 100%;
                margin: 0px;
                display: flex;
                align-items: center;
                justify-content: space-between;

                .title {
                    width: 10px;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    text-align: left;
                    flex-grow: 1;
                }

                .num {
                    width: 30px;
                    text-align: right;
                    flex-grow: 0;
                }

                .price {
                    color: red;
                }
            }
        }
    }
}
</style>
