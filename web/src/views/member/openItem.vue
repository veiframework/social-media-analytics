<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
            @currentChange="handleChange">
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'months'">
                    {{ row.months == -1 ? "连续包月" : row.months }}
                </template>
            </template>
        </CustomTable>
        <CustomDialog :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
            @save="handleSave">
            <template #custom-item="{ prop, form }">
                <template v-if="prop == 'chargePort'">

                </template>
            </template>
        </CustomDialog>
        <CustomDialog :form="configForm" :option="configOptionDialog" :visible="configVisible"
            @cancel="configVisible = false" @save="handleConfigSave">
            <template #custom-item="{ prop, form }">
                <template v-if="prop == 'chargePort'">

                </template>
            </template>
        </CustomDialog>
    </div>
</template>

<script setup name="OpenItem">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import {onActivated, onMounted, reactive, ref} from "vue";
import { ElMessageBox, ElMessage, ElLoading } from "element-plus";
import useUserStore from '@/store/modules/user';
import { editOpenApi, getOpenListApi } from "@/api/member";
import { regularAmount, regularNumber, regularNumberDo } from "@/utils/verify";
const { proxy } = getCurrentInstance();
const { charge_available } = proxy.useDict('charge_available');
const userStore = useUserStore();
// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(100);
// 表单参数
const form = ref({});
const visible = ref(false);
const configForm = ref({});
const configVisible = ref(false);
// 自定义参数



onMounted(async () => {

  getData()
})

/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value }
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    getOpenListApi(query).then(res => {
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
            getMemberConfigApi().then(res => {
                configForm.value = { id: 1, ...res.data };
                configVisible.value = true;
            })
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
            form.value = { ...row };
            visible.value = true;
            break;
    }
}
/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val };
    editOpenApi(formData, formData.id).then(res => resMsg(res))
}
const handleConfigSave = (val) => {
    const formData = { ...val };
    delete formData.id;
    delete formData.preferentialPeriod;
    delete formData.userPreferentialNum;
    setMemberConfigApi(formData).then(res => resMsg(res))
}
const resMsg = (res) => {
    if (res.code == 200) {
        ElMessage.success(res.msg);
        visible.value = false;
        configVisible.value = false;
        getData();
    } else {
        ElMessage.error(res.msg)
    }
}
// 表格配置项
const option = reactive({
    showSearch: true,  // 显示隐藏
    searchLabelWidth: 60,  // 搜索标题宽度
    /** 搜索字段配置项 */
    searchItem: [
        {
            type: "select", label: "状态", prop: "status", isOmit: true, default: null,
            dicData: [{ value: 0, label: '下架' }, { value: 1, label: '上架' }]
        },
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        // {
        //     key: "add", text: "会员优惠配置", icon: "Setting", isShow: true, type: "primary", disabled: false,
        //     hasPermi: [
        //         'charge:member:preferential:config:list', 'charge:member:preferential:config:edit',
        //         'marketing:member:open:options:list', 'marketing:member:open:options:set'
        //     ]
        // },
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
        {
            type: 'text', label: '开通类型', prop: 'typeName', width: 120, fixed: false, sortable: false, isShow: true,
        },
        { type: 'custom', label: '开通周期(月)', prop: 'months', width: 100, fixed: false, sortable: false, isShow: true },
        { type: 'text', label: '包含时长(天)', prop: 'days', width: 120, fixed: false, sortable: false, isShow: true },
        { type: 'text', label: '原价(元)', prop: 'originalPrice', width: 130, fixed: false, sortable: false, isShow: true },
        { type: 'text', label: '售价(元)', prop: 'price', width: 130, fixed: false, sortable: false, isShow: true },
        {
            type: 'tag', label: '上架状态', prop: 'status', width: 100, fixed: false, sortable: false, isShow: true,
            dicData: [{ value: 0, label: '下架' }, { value: 1, label: '上架', type: 'success' }]
        },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 120,
        fixed: false,
    },
    menuItemBtn: [
        { type: 'primary', isShow: true, icon: 'Edit', label: '编辑', value: 'edit', hasPermi: ['charge:member:preferential:config:edit'] },
    ],
    /** page 分页配置项 */
    isShowPage: true,
})
// 表单配置项
const optionDialog = reactive({
    dialogClass: 'dialog_xs',
    labelWidth: 'auto',
    formitem: [
      { type: "input", label: "名称", prop: "typeName", max: 20,  default: null,  span: 2 },
      { type: "input", label: "原价", prop: "originalPrice", max: 20, verify: regularNumberDo, default: null, append: '元', span: 2 },
        { type: "input", label: "售价", prop: "price", max: 20, verify: regularNumberDo, default: null, append: '元', span: 2 },
        {
            type: "radio", label: "状态", prop: "status", default: 1,
            dicData: [{ value: 0, label: '下架' }, { value: 1, label: '上架' }]
        },
    ],
    rules: {
      typeName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
         originalPrice: [{ required: true, message: '请输入价格', trigger: 'blur' }, { pattern: regularAmount, message: '请输入正确的价格', trigger: 'blur' }],
        price: [{ required: true, message: '请输入价格', trigger: 'blur' }, { pattern: regularAmount, message: '请输入正确的价格', trigger: 'blur' }],
    }
})
const configOptionDialog = reactive({
    dialogTitle: '会员优惠配置',
    dialogClass: 'dialog_xs',
    labelWidth: 'auto',
    formitem: [
        { type: "input", label: "最大优惠金额", prop: "maxPerOrderPreferentialMoney", max: 15, verify: regularNumberDo, default: null, append: '元/单', span: 2 },
        { type: "input", label: "最大优惠次数", prop: "maxPreferentialNum", max: 10, verify: regularNumber, default: null, append: '次/30天', span: 2 },
        {
            type: "input", label: "最低充电时间", prop: "minChargeTime", max: 10, verify: regularNumber, default: null, append: '秒', span: 2,
            tooltip: "达到最低充电时长后，订单才会使用会员优惠！"
        },
    ],
    rules: {
        maxPerOrderPreferentialMoney: [
            { required: true, message: '请输入最大单笔优惠金额', trigger: 'blur' },
            { pattern: regularAmount, message: '请输入正确的优惠金额', trigger: 'blur' }
        ],
        maxPreferentialNum: [{ required: true, message: '请输入最大优惠次数', trigger: 'blur' }],
        minChargeTime: [{ required: true, message: '请输入最低充电时间', trigger: 'blur' }],
    }
})
</script>
<style scoped lang="scss"></style>
