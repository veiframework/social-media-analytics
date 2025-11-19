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
        <CustomDialog :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
            @save="handleSave" @formChange="handleFormChange">
            <template #custom-item="{ prop, form }">
                <div v-if="prop == 'deputyCount'" style="width: 100%;">

                </div>
            </template>
        </CustomDialog>
    </div>
</template>

<script setup name="UserInfo">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import useUserStore from '@/store/modules/user';
import { ElMessage } from "element-plus";
import { reactive, ref } from "vue";
import { getUserList, editUser } from "@/api/operation/index";
import { getAllUnitApi } from "@/api/base";
import { regularNumber } from "@/utils/verify";
const { proxy } = getCurrentInstance();
const { station_type } = proxy.useDict('station_type');
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
// 自定义参数
const unitData = ref([]);
/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value }
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    getUserList(query).then(res => {
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
        case "edit":
            const { id, apartmentId, communityId, floorNo, roomNo, phone, wxOpenId } = row;
            form.value = { id, apartmentId, communityId, floorNo, roomNo, phone, wxOpenId };
            handleFormChange({ prop: 'communityId', form: form.value })
            visible.value = true;
            break;
    }
}
/**
 * 表单选项变更操作
 */
const handleFormChange = (val) => {
    const { prop, form } = val
    switch (prop) {
        case "communityId":
            getAllUnitApi({ communityId: form.communityId }).then(res => unitData.value = res.data);
            break;
    }
}
/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val };
    editUser(formData, formData.id).then(res => resMsg(res))
}
const resMsg = (res) => {
    if (res.code == 200) {
        ElMessage.success(res.msg);
        visible.value = false;
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
        {
            type: "select", label: "小区名称", prop: "communityId", customValue: "id", customLabel: "communityName", filterable: true,
            dicData: computed(() => { return userStore.communityList }), default: null
        },
        { type: "input", label: "手机号", prop: "phone", max: 99, verify: "", isOmit: false, default: null },
        { type: "input", label: "微信号", prop: "wxOpenId", max: 99, verify: "", isOmit: false, default: null },
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
        { type: 'text', label: '小区名称', prop: 'communityId_dictText', width: 100, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '单元名称', prop: 'apartmentId_dictText', width: 150, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '楼层号', prop: 'floorNo', width: 80, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '房间号', prop: 'roomNo', width: 80, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '微信号', prop: 'wxOpenId', width: 80, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '手机号', prop: 'phone', width: 80, fixed: false, sortable: false, isShow: true, isOmit: true },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 120,
        fixed: false,
    },
    menuItemBtn: [
        { type: 'primary', isShow: true, icon: 'Edit', label: '修改信息', value: 'edit', hasPermi: ['locker:user:edit'] },
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
})
// 表单配置项
const optionDialog = reactive({
    dialogTitle: '用户信息',
    dialogClass: 'dialog_xs',
    labelWidth: 'auto',
    formitem: [
        {
            type: "select", label: "小区名称", prop: "communityId", customValue: "id", customLabel: "communityName", filterable: true,
            dicData: computed(() => { return userStore.communityList }), default: null, span: 2
        },
        {
            type: "select", label: "单元名称", prop: "apartmentId", customValue: "id", customLabel: "apartmentName", filterable: true,
            dicData: computed(() => { return unitData.value }), default: null, span: 2
        },
        { type: "input", label: "楼层", prop: "floorNo", max: 2, verify: regularNumber, default: null, span: 2 },
        { type: "input", label: "房间号", prop: "roomNo", max: 10, verify: "", default: null, span: 2 },
        { type: "input", label: "手机号", prop: "phone", max: 11, verify: regularNumber, default: null, span: 2 },
        { type: "input", label: "微信号", prop: "wxOpenId", max: 20, verify: "", default: null, span: 2 },
    ],
    rules: {
        communityId: [{ required: true, message: '请选择小区名称', trigger: 'change' }],
        apartmentId: [{ required: true, message: '请选择单元名称', trigger: 'change' }],
        mainLockerIndex: [{ required: true, message: '请输入主柜排列序号', trigger: 'blur' }],
    }
})
</script>

<style scoped></style>
