<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @menuChange="handleMenu" :pageSize="pageSize" @currentChange="handleChange">
            <template #table-top></template>
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'custom'">
                </template>
            </template>
        </CustomTable>
    </div>
</template>

<script setup name="Cell">
import CustomTable from "@/components/CustomTable";
import useUserStore from '@/store/modules/user';
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref } from "vue";
import { getBoxListApi, changeBoxStatusApi } from "@/api/base/index";
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
    getBoxListApi(query).then(res => {
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
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "ready":
        case "fault":
            ElMessageBox.confirm(`是否将该格口设为${row.status == 1 ? '故障' : '可用'}？`, '提示',
                { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(() => {
                    changeBoxStatusApi({ id: row.id, status: row.status == 1 ? 3 : 1 }).then(res => resMsg(res))
                }).catch(() => { })
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
// 表格配置项
const option = reactive({
    showSearch: true,  // 显示隐藏
    searchLabelWidth: 90,  // 搜索标题宽度
    /** 搜索字段配置项 */
    searchItem: [
        { type: "input", label: "柜子编号", prop: "lockerNo", max: 99, verify: "", isOmit: false, default: null },
        { type: "input", label: "柜子名称", prop: "lockerName", max: 99, verify: "", isOmit: false, default: null },
        { type: "input", label: "箱格号", prop: "boxNo", max: 99, verify: "", isOmit: false, default: null },
        {
            type: "select", label: "箱格状态", prop: "status", filterable: true, default: null,
            dicData: [{ label: '可用', value: 1 }, { label: '故障', value: 3 }]
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
        { type: 'text', label: '柜子编号', prop: 'lockerNo', width: 100, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '柜子名称', prop: 'lockerNo_dictText', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '箱格号', prop: 'boxNo', width: 80, fixed: false, sortable: false, isShow: true, isOmit: true },
        {
            type: "tag", label: "箱格状态", prop: "status", width: 90, sortable: false, isShow: true, isOmit: true,
            dicData: [{ label: '可用', value: 1, type: 'success' }, { label: '故障', value: 3, type: 'danger' }]
        },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 120,
        fixed: false,
    },
    menuItemBtn: [
        { type: 'primary', isShow: true, icon: 'Edit', label: '设为可用', value: 'ready', hasPermi: ['locker:box:state'], judge: (row) => row.status != 1 },
        { type: 'danger', isShow: true, icon: 'Edit', label: '设为故障', value: 'fault', hasPermi: ['locker:box:state'], judge: (row) => row.status == 1 },
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
})
</script>

<style scoped></style>
