<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" :pageSize="pageSize" @currentChange="handleChange" @menuChange="handleMenu" @headerchange="handleHeader">
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'months'">
                    {{ row.months == -1 ? "连续包月" : row.months }}
                </template>
<!--                <template v-if="prop == 'endDate'">-->
<!--                  {{ row.endDate }} 23:59:59-->
<!--                </template>-->
<!--                <template v-if="prop == 'periodStartTime'">-->
<!--                  {{ formatDateTime(row.periodStartTime, 'YYYY-MM-DD') }} - {{ addDays(row.periodStartTime, row.days) }}-->
<!--                </template>-->
            </template>
        </CustomTable>

    </div>
</template>

<script setup name="OpenRecord">
import { getOpenRecordListApi, refundMemberApi } from "@/api/member";
import CustomTable from "@/components/CustomTable";
import useUserStore from '@/store/modules/user';
import { regularNumber } from "@/utils/verify";
import { ElMessage, ElMessageBox } from "element-plus";
import {onMounted, reactive, ref} from "vue";
const userStore = useUserStore();
// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(100);

const asyncExportRef = ref();




onMounted(async () => {

  getData()
})
//计算会员周期结束时间
function addDays(startDate, days) {
  const date = new Date(startDate); // 确保是Date对象
  console.log(date);
  if (isNaN(date)) {
    throw new Error('Invalid date');
  }
  date.setDate(date.getDate() + days);
  console.log(date);
  return formatDateTime(date, 'YYYY-MM-DD');
}
//修改日期格式
function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hour = String(d.getHours()).padStart(2, '0');
  const minute = String(d.getMinutes()).padStart(2, '0');
  const second = String(d.getSeconds()).padStart(2, '0');

  return format
      .replace('YYYY', year)
      .replace('MM', month)
      .replace('DD', day)
      .replace('HH', hour)
      .replace('mm', minute)
      .replace('ss', second);
}
/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value }
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    query.status = "1,2,3,4"
    getOpenRecordListApi(query).then(res => {
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
    case "download":
      asyncExportRef.value.handleExport();
      break;
    case "downloadView":
      asyncExportRef.value.viewExport();
      break;
  }
}
/**
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "money":

            ElMessageBox.prompt('请输入退款原因', '提示', { type: 'warning', confirmButtonText: '确认', cancelButtonText: '取消' }).then(({ value }) => {
              const refundData = {
                refundFee: parseFloat(row.price), // 退款金额
                outTradeNo: row.id, // 商户订单号（使用订单ID）
                totalFee: parseFloat(row.price), // 订单总金额
                tradeType: 'JSAPI',
                extendParams: {
                  "tenantId": '0'
                },
                businessTypeCode: "mallMember",
                reason: value
              };

                refundMemberApi(refundData).then(res => {
                  if (res.code == 500) {
                    ElMessage.error(res.msg || '退款失败');
                  } else if (res.refundResult == true) {
                    // 退款成功，更新订单状态
                    ElMessage.success('退款成功');
                  } else {
                    ElMessage.error(res.message || '退款失败');
                  }
                  getData();
                })
            }).catch(() => {

            })
            break;
    }
}
const option = reactive({
    showSearch: true,  // 显示隐藏
    searchLabelWidth: 90,  // 搜索标题宽度
    /** 搜索字段配置项 */
    searchItem: [
        { type: "input", label: "手机号", prop: "phone", max: 11, verify: regularNumber, isOmit: false, default: null, span: 1 },
        {
            type: "select", label: "会员类型", prop: "type", isOmit: true, default: null,
            dicData: [ { value: 1, label: '单次月卡' }, { value: 2, label: '单次季卡' }, { value: 3, label: '单次年卡' }]
        },
        // {
        //   type: "select", label: "办理城市", prop: "regionId", isOmit: true, default: null,
        //   dicData: [{ value: "1775129162131808257", label: '郑州' }, { value: "1775129382483763201", label: '洛阳' }, { value: "1775129382483763202", label: '成都' }, { value: "1839165643738345473", label: '重庆' }]
        // },
        {
            type: "datetimerange", label: "办理时间", prop: "Time", format: "YYYY-MM-DD HH:mm:ss", valueFormat: "YYYY-MM-DD HH:mm:ss",
            category: "datetimerange", default: userStore.getDate()
        },
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
      // { key: "download", text: "导出", icon: "Download", isShow: true, type: "primary", hasPermi: ['marketing:monthCard:consumption:export'], disabled: false },
      // { key: "downloadView", text: "查看导出记录", icon: "View", isShow: true, type: "primary", hasPermi: ['marketing:monthCard:consumption:export'], disabled: false },
    ],
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
      { type: 'text', label: '手机号', prop: 'phone', width: 120, fixed: false, sortable: false, isShow: true },
      //{ type: 'custom', label: '开通周期(月)', prop: 'months', width: 120, fixed: false, sortable: false, isShow: true },
      { type: 'text', label: '售价(元)', prop: 'price', width: 130, fixed: false, sortable: false, isShow: true },
      { type: 'text', label: '有效期(天)', prop: 'days', width: 120, fixed: false, sortable: false, isShow: true },
      //{ type: 'text', label: '启用时间', prop: 'startDate', width: 160, fixed: false, sortable: false, isShow: true },
      { type: 'text', label: '办理时间', prop: 'openTime', width: 160, fixed: false, sortable: false, isShow: true },
      { type: 'text', label: '过期时间', prop: 'endDate', width: 160, fixed: false, sortable: false, isShow: true },
      // { type: 'custom', label: '当前周期', prop: 'periodStartTime', width: 185, fixed: false, sortable: false, isShow: true },
      // { type: 'text', label: '已使用次数', prop: 'preferentialNum', width: 120, fixed: false, sortable: false, isShow: true },
      {
        type: 'tag', label: '会员类型', prop: 'type', width: 120, fixed: false, sortable: false, isShow: true,
        dicData: [
          { value: 0, label: '连续包月' }, { value: 1, label: '单次月卡', type: 'success' }, { value: -1, label: '赠送会员', type: 'info' },
          { value: 2, label: '单次季卡', type: 'warning' }, { value: 3, label: '单次年卡', type: 'danger' }
        ]
      },
      //{ type: 'text', label: '原价(元)', prop: 'originalPrice', width: 130, fixed: false, sortable: false, isShow: true },
      {
        type: 'tag', label: '会员状态', prop: 'status', width: 120, fixed: false, sortable: false, isShow: true,
        dicData: [
          { value: 0, label: '待支付' }, { value: 1, label: '已支付', type: 'success' },
          { value: 2, label: '退款中', type: 'warning' }, { value: 3, label: '已退款', type: 'info' }, { value: 4, label: '退款失败', type: 'danger' }
        ]
      },
      {
        type: 'tag', label: '会员来源', prop: 'source', width: 120, fixed: false, sortable: false, isShow: true,
        dicData: [{ value: 1, label: '自行购买' }, { value: 2, label: '充值活动赠送' }]
      },
      // {
      //   type: 'tag', label: '办理城市', prop: 'regionId', width: 120, fixed: false, sortable: false, isShow: true,
      //   dicData: [{ value: "1775129162131808257", label: '郑州' }, { value: "1775129382483763201", label: '洛阳' }, { value: "1775129382483763202", label: '成都' }, { value: "1839165643738345473", label: '重庆' }]
      // },
      { type: 'text', label: '退款原因', prop: 'refundRemarks', width: 280, fixed: false, sortable: false, isShow: true },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 180,
        fixed: false,
    },
    menuItemBtn: [
        {
            type: 'primary', isShow: true, icon: 'Money', label: '申请退款', value: 'money', hasPermi: ['marketing:member:open:record:refund'],
            judge: (row) => (row.status == 1 || row.status == 4) && row.source == 1
        },
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
})
</script>

<style scoped></style>
