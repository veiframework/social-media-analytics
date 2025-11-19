<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" :pageSize="pageSize" @currentChange="handleChange">
            <template #table-top></template>
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'custom'">
                </template>
            </template>
        </CustomTable>
    </div>
</template>

<script setup name="Deposit">
import CustomTable from "@/components/CustomTable";
import useUserStore from '@/store/modules/user';
import { reactive, ref } from "vue";
import { getStorageRecord } from "@/api/operation/index";
const { proxy } = getCurrentInstance();
const { station_type } = proxy.useDict('station_type');
const userStore = useUserStore();
// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(100);
/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value }
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    getStorageRecord(query).then(res => {
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
// 表格配置项
const option = reactive({
    showSearch: true,  // 显示隐藏
    searchLabelWidth: 90,  // 搜索标题宽度
    /** 搜索字段配置项 */
    searchItem: [
        { type: "input", label: "柜子编号", prop: "lockerNo", max: 99, verify: "", isOmit: false, default: null },
        { type: "input", label: "柜子名称", prop: "lockerName", max: 99, verify: "", isOmit: false, default: null },
        { type: "input", label: "箱格号", prop: "boxNo", max: 99, verify: "", isOmit: false, default: null },
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
        { type: 'text', label: '柜子编号', prop: 'lockerNo', width: 100, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '柜子名称', prop: 'lockerName', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '箱格号', prop: 'boxNo', width: 80, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '门牌号', prop: 'roomNo', width: 100, fixed: false, sortable: false, isShow: true, isOmit: true },
        {
            type: "tag", label: "用户类型", prop: "sendStatus", width: 90, sortable: false, isShow: true, isOmit: true,
            dicData: [{ label: '管理员', value: 1 }, { label: '用户', value: 0, type: 'success' }]
        },
        { type: 'text', label: '手机号', prop: 'phone', width: 100, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '操作时间', prop: 'createTime', width: 160, fixed: false, sortable: false, isShow: true, isOmit: true },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: false,
        width: 120,
        fixed: false,
    },
    menuItemBtn: [],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
})
</script>

<style scoped></style>
