<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
            @currentChange="handleChange">
            <template #table-top></template>
        </CustomTable>
        
        <!-- 绑定物流账号弹窗 -->
        <CustomDialog   :option="bindDialogOption" :visible="bindVisible" :form="bindData"
            @cancel="bindVisible = false" @save="confirmBind">
        </CustomDialog>
    </div>
</template>

<script setup name="logistics-account">
import CustomTable from "@/components/CustomTable/index.vue";
import CustomDialog from "@/components/CustomDialog/index.vue";

import { ElMessage, ElMessageBox } from "element-plus";
import { computed, reactive, ref } from "vue";
import useAppStore from "@/store/modules/app";

import {
  listAccount,
  getAllAccount,
  getDeliveryList,
  getDeliveryServiceList,
  bindAccount,
  unbindAccount
} from "@/api/logistics/account";
import {getAllShop} from "@/api/shop";

const pinia = useAppStore();

// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 绑定弹窗参数
const bindVisible = ref(false);
const bindData = ref({});

// 快递公司列表
const deliveryList = ref([]);
const shops = ref([]);
const  props = defineProps({
  currentShopId: {type: Number, default: null}
})
/**
 * 初始化数据
 */
const init = () => {
  getAllShopApi()
  getData();
  getDeliveryListData();
};

/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value, pageNum: pageNum.value, pageSize: pageSize.value, shopId:props.currentShopId };
    listAccount(query).then(res => {
        tableData.value = res.data.records;
        total.value = res.data.total;
    })
}

/**
 * 获取快递公司列表
 */
const getDeliveryListData = () => {
    getDeliveryList({}).then(res => {
        deliveryList.value = res.data;
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
const  getAllShopApi = ()=>{
  getAllShop({}).then(res=>{
    shops.value = res.data.map(i=>({
      label: i.name,
      value: i.id
    }))
  })
}

/**
 * 表格左侧按钮
 */
const handleHeader = (key) => {
  console.log(key)
    switch (key) {
        case "add":
            // 打开绑定弹窗
            bindData.value = {
                shopId: props.currentShopId,
                deliveryId: '',
                bizId: '',
                password: '',
                remarkContent: ''
            };
            bindVisible.value = true;
            console.log(bindVisible.value)
            break;
    }
}

/**
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "unbind":
            // 解绑确认
            ElMessageBox.confirm('是否确认解绑该物流账号？', '提示', { 
                confirmButtonText: '确认', 
                cancelButtonText: '取消', 
                type: 'warning' 
            }).then(() => {
                handleUnbind(row);
            }).catch(() => {});
            break;
    }
}

/**
 * 确认绑定
 */
const confirmBind = (formData) => {
    if (!formData.deliveryId || !formData.bizId || !formData.password) {
        ElMessage.error('请填写完整信息');
        return;
    }
    
    // 设置快递公司名称
    const delivery = deliveryList.value.find(item => item.deliveryId == formData.deliveryId);
    if (delivery) {
        formData.deliveryName = delivery.deliveryName;
    }
    
    bindAccount(formData).then(res => {
        if (res.code == 200) {
            ElMessage.success('绑定成功');
            bindVisible.value = false;
            getData();
        } else {
            ElMessage.error(res.msg || '绑定失败');
        }
    }).catch(error => {
        ElMessage.error('绑定失败');
        console.error('绑定错误:', error);
    });
}

/**
 * 处理解绑
 */
const handleUnbind = (row) => {
    unbindAccount({ id: row.id }).then(res => {
        if (res.code == 200) {
            ElMessage.success('解绑成功');
            getData();
        } else {
            ElMessage.error(res.msg || '解绑失败');
        }
    }).catch(error => {
        ElMessage.error('解绑失败');
        console.error('解绑错误:', error);
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
        { type: 'primary', isShow: true, icon: 'Plus', text: '绑定物流账号', key: 'add', hasPermi: [] }
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
        { type: 'text', label: '客户编码', prop: 'bizId', width: 160, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '快递公司', prop: 'deliveryName', width: 200, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '快递公司ID', prop: 'deliveryId', width: 200, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '备注', prop: 'remarkContent', width: 300, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '绑定时间', prop: 'insertTime', width: 160, fixed: false, sortable: false, isShow: true, isOmit: false },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 120,
        fixed: false,
    },
    menuItemBtn: [
        { type: 'danger', isShow: true, icon: 'Delete', label: '解绑', value: 'unbind', hasPermi: [] }
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
});

// 绑定弹窗配置项
const bindDialogOption = reactive({
    dialogTitle: '绑定物流账号',
    labelWidth: '120px',
    isShowFooter: true,
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
            type: 'select',
            label: '快递公司',
            prop: 'deliveryId',
            placeholder: '请选择快递公司',
            dicData: computed(() => deliveryList.value.map(item => ({
                label: item.deliveryName,
                value: item.deliveryId
            })))
        },
        {
            type: 'input',
            label: '客户编码',
            prop: 'bizId',
            placeholder: '请输入快递公司客户编码',
            maxlength: 50,
        },
        {
            type: 'input',
            label: '客户密码',
            prop: 'password',
            placeholder: '请输入快递公司客户密码',
            maxlength: 50,
        },
        {
            type: 'input',
            label: '备注内容',
            prop: 'remarkContent',
            placeholder: '备注内容（提交EMS审核需要）',
            maxlength: 200,
            span: 2,
        }
    ],
    rules: {
        deliveryId: [{ required: true, message: '请选择快递公司', trigger: 'change' }],
        bizId: [{ required: true, message: '请输入快递公司客户编码', trigger: 'blur' }],
        password: [{ required: true, message: '请输入快递公司客户密码', trigger: 'blur' }],
    }
});

init();
</script>

<style scoped lang="scss">

</style>