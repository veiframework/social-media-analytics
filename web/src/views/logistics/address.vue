<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
            @currentChange="handleChange">
            <template #table-top></template>
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'isDefault'">
                    <el-tag v-if="row.isDefault == 1" type="success">是</el-tag>
                    <el-tag v-else type="info">否</el-tag>
                </template>
                <template v-else-if="prop == 'area'">
                    {{ row.province }}{{ row.city }}{{ row.area }}
                </template>
            </template>
        </CustomTable>
        
        <!-- 新增/编辑地址弹窗 -->
        <CustomDialog :option="addressDialogOption" :visible="addressVisible" :form="addressData"
            @cancel="addressVisible = false" @save="confirmAddress" @formChange="handleFormChange">
        </CustomDialog>
        
        <!-- 物流开关设置弹窗 -->
        <CustomDialog :option="switchDialogOption" :visible="switchVisible" :form="switchData"
            @cancel="switchVisible = false" @save="confirmSwitch" @formChange="handleSwitchChange">
        </CustomDialog>

        <!-- 物流账号管理弹窗 -->
        <el-dialog 
            title="物流账号管理" 
            v-model="accountVisible" 
            width="80%" 
            :close-on-click-modal="false"
            :close-on-press-escape="false">
            <LogisticsAccount :currentShopId="currentShopId"/>
        </el-dialog>


    </div>
</template>

<script setup name="logistics-address">
import CustomTable from "@/components/CustomTable/index.vue";
import CustomDialog from "@/components/CustomDialog/index.vue";
import LogisticsAccount from "./account.vue";

import { ElMessage, ElMessageBox } from "element-plus";
import { computed, reactive, ref } from "vue";
import useAppStore from "@/store/modules/app";

import {
  listAddress,
  addAddress,
  updateAddress,
  delAddress,
  getLogisticsStatus,
  getAllAddress,
  getUserAddress,
  setShopStatus
} from "@/api/logistics/address";

import { getRegionAllApi } from '@/api/system/region';
import {getAllShop} from "@/api/shop";

const pinia = useAppStore();

// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 地址弹窗参数
const addressVisible = ref(false);
const addressData = ref({});
const currentAddress = ref(null);

// 物流开关弹窗参数
const switchVisible = ref(false);
const switchData = ref({});

// 物流账号管理弹窗参数
const accountVisible = ref(false);

// 省市区数据
const provinces = ref([]);
const citys = ref([]);
const areas = ref([]);
const shops = ref([]);
const currentShopId = ref(null)
/**
 * 初始化数据
 */
const init = () => {
  getAllShopApi()
  getData();
  getRegionData();
};

/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value, pageNum: pageNum.value, pageSize: pageSize.value };
    listAddress(query).then(res => {
        tableData.value = res.data.records;
        total.value = res.data.total;
    })
}

/**
 * 获取省份数据
 */
const getRegionData = () => {
  getRegionAllApi({level: 1}).then(res => {
        provinces.value = res.data;
    })
}

const getSubRegion = async (parentId) => {
  let res= await getRegionAllApi({parentId: parentId})
  return res.data
}

const  getAllShopApi = ()=>{
   getAllShop({}).then(res=>{
    shops.value = res.data.map(i=>({
      label: i.name,
      value: i.id
    }))
  })
}

const handleFormChange = async (val) => {
  const { prop, form } = val;
   if(prop=='provinceCode'){
    let obj= provinces.value.filter(i=>i.areaCode==form.provinceCode)[0]
      form.province = obj.label
      form.cityCode= null
     form.areaCode =null
     citys.value = await getSubRegion( obj.id)
   }
   if(prop=='cityCode') {
     let obj= citys.value.filter(i=>i.areaCode==form.cityCode)[0]
     form.city = obj.label
     form.areaCode =null
     areas.value = await getSubRegion( obj.id)

   }
   if(prop=='areaCode'){
     let obj= areas.value.filter(i=>i.areaCode==form.areaCode)[0]
     form.area = obj.label
   }
};

const handleSwitchChange = (val) => {
  const { prop, form } = val;
  if(prop=='shopId'){
    getLogisticsStatus({shopId: form.shopId}).then(res=>{
      form.status = res.data
    })
  }
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
            // 打开新增地址弹窗
            currentAddress.value = null;
            addressData.value = {
                name: '',
                mobile: '',
                provinceCode: '',
                cityCode: '',
                areaCode: '',
                address: '',
                isDefault: 0
            };
            addressVisible.value = true;
            break;
        case "addAccount":
            // 打开物流账号管理弹窗
            accountVisible.value = true;
            break;
        case "switch":
            // 打开物流开关弹窗
            switchData.value = {
                shopId: '',
                status: 0
            };
            switchVisible.value = true;
            break;
    }
}

/**
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "edit":
            // 编辑地址
            handleEdit(row);
            break;
        case "bindLogistic":
          accountVisible.value = true;
          currentShopId.value = row.shopId
          break;
        case "del":
            // 删除地址
            ElMessageBox.confirm('是否确认删除该地址？', '提示', { 
                confirmButtonText: '确认', 
                cancelButtonText: '取消', 
                type: 'warning' 
            }).then(() => {
                handleDelete(row);
            }).catch(() => {});
            break;
    }
}

/**
 * 编辑地址
 */
const handleEdit = async (row) => {
    currentAddress.value = row;
    let province= provinces.value.filter(i=>i.areaCode==row.provinceCode)[0]
    citys.value = await getSubRegion(province.id)
    let city= citys.value.filter(i=>i.areaCode==row.cityCode)[0]
    areas.value = await getSubRegion(city.id)

    addressData.value = { ...row };
    addressData.value.provinceCode = addressData.value.provinceCode +''
    addressData.value.cityCode =  addressData.value.cityCode+''
    addressData.value.areaCode =  addressData.value.areaCode+''
    addressVisible.value = true;
}

/**
 * 确认保存地址
 */
const confirmAddress = (val) => {
  const formData = { ...val };
  if (val.id) {
    updateAddress(formData, formData.id).then(res => resMsg(res));
  } else {
    addAddress(formData).then(res => resMsg(res));
  }
}
/**
 * 响应消息处理
 */
const resMsg = (res) => {
  if (res.code === 200) {
    ElMessage.success(res.msg || '操作成功');
    addressVisible.value = false;
    getData();
  } else {
    ElMessage.error(res.msg || '操作失败');
  }
}
/**
 * 删除地址
 */
const handleDelete = (row) => {
    delAddress({ id: row.id }).then(res => {
        if (res.code == 200) {
            ElMessage.success('删除成功');
            getData();
        } else {
            ElMessage.error(res.msg || '删除失败');
        }
    }).catch(error => {
        ElMessage.error('删除失败');
        console.error('删除错误:', error);
    });
}

/**
 * 确认物流开关设置
 */
const confirmSwitch = (formData) => {
    if (!formData.shopId) {
        ElMessage.error('请选择客户');
        return;
    }
    
    setShopStatus({
        shopId: formData.shopId,
        status: formData.status
    }).then(res => {
        if (res.code == 200) {
            ElMessage.success('设置成功');
            switchVisible.value = false;
        } else {
            ElMessage.error(res.msg || '设置失败');
        }
    }).catch(error => {
        ElMessage.error('设置失败');
        console.error('设置错误:', error);
    });
}

// 表格配置项
const option = reactive({
    showSearch: false,  // 显示隐藏搜索
    searchLabelWidth: 90,  // 搜索标题宽度
    /** 搜索字段配置项 */
    searchItem: [],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        { type: 'primary', isShow: true, icon: 'Plus', text: '新增地址', key: 'add', hasPermi: [] },
      { type: 'primary', isShow: true, icon: 'Plus', text: '绑定物流账号', key: 'addAccount', hasPermi: [] },
      { type: 'success', isShow: true, icon: 'Setting', text: '是否开启物流', key: 'switch', hasPermi: [] },
    ],
    operation: [],
    /** 表格顶部右侧 toobar 配置项 */
    toolbar: { isShowToolbar: true, isShowSearch: false },
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
        { type: 'custom', label: '是否默认', prop: 'isDefault', width: 100, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '客户', prop: 'shopName', width: 120, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '姓名', prop: 'name', width: 120, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '电话', prop: 'mobile', width: 140, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'custom', label: '地区', prop: 'area', width: 200, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '详细地址', prop: 'address', width: 300, fixed: false, sortable: false, isShow: true, isOmit: false },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 160,
        fixed: false,
    },
    menuItemBtn: [
        { type: 'primary', isShow: true, icon: 'Edit', label: '编辑', value: 'edit', hasPermi: [] },
        // { type: 'primary', isShow: true, icon: 'Edit', label: '绑定物流账号', value: 'bindLogistic', hasPermi: [] },
        { type: 'danger', isShow: true, icon: 'Delete', label: '删除', value: 'del', hasPermi: [] }
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
});

// 地址弹窗配置项
const addressDialogOption = reactive({
    dialogTitle: computed(() => currentAddress.value ? '编辑地址' : '新增地址'),
    labelWidth: '120px',
    formitem: [
      {
        type: 'select',
        label: '客户',
        prop: 'shopId',
        placeholder: '请选择客户',
        dicData: shops,
        default: null
      },
        {
            type: 'input',
            label: '姓名',
            prop: 'name',
            placeholder: '请输入姓名',
            maxlength: 20,
          default: null
        },
        {
            type: 'input',
            label: '电话',
            prop: 'mobile',
            placeholder: '请输入手机号',
            maxlength: 11,
        },
        {
            type: 'select',
            label: '省份',
            prop: 'provinceCode',
            placeholder: '请选择省份',
            dicData: provinces,
            customLabel: "label",
            customValue: "areaCode"

        },
        {
            type: 'select',
            label: '城市',
            prop: 'cityCode',
            placeholder: '请选择城市',
            dicData: citys,
            customLabel: "label",
            customValue: "areaCode"
        },
        {
            type: 'select',
            label: '区县',
            prop: 'areaCode',
            placeholder: '请选择区县',
            dicData: areas,
            customLabel: "label",
            customValue: "areaCode"
        },
        {
            type: 'input',
            label: '详细地址',
            prop: 'address',
            placeholder: '请输入详细地址',
            maxlength: 200,
            span: 2,
          default: null

        },
        {
            type: 'radio',
            label: '设为默认',
            prop: 'isDefault',
          default: 1,
          fixed: false,
          sortable: false,
          isShow: true,
          dicData: [
            { value: 1, label: '是' },
            { value: 0, label: '否' }
          ]
        }
    ],
    rules: {
        shopId: [{ required: true, message: '请选择客户', trigger: 'blur' }],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        provinceCode: [{ required: true, message: '请选择省份', trigger: 'change' }],
        cityCode: [{ required: true, message: '请选择城市', trigger: 'change' }],
        areaCode: [{ required: true, message: '请选择区县', trigger: 'change' }],
        address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
    }
});

// 物流开关弹窗配置项
const switchDialogOption = reactive({
    dialogTitle: '是否开启物流',
    labelWidth: '120px',
    isShowFooter: true,
    formitem: [
        {
            type: 'select',
            label: '客户',
            prop: 'shopId',
            placeholder: '请选择客户',
            maxlength: 50,
            dicData: shops
        },
        {
            type: 'radio',
            label: '物流开关',
            prop: 'status',
          default: 1,
          dicData: [
            { value: 1, label: '是' },
            { value: 0, label: '否' }
          ]
        }
    ],
    rules: {
        shopId: [{ required: true, message: '请选择客户', trigger: 'blur' }],
        status: [{ required: true, message: '请选择开关', trigger: 'blur' }],
    }
});

init();
</script>

<style scoped lang="scss">

</style>