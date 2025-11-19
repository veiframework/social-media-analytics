<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
            @currentChange="handleChange">
            <template #custom-item="{ prop, queryParams }">
                <template v-if="prop == 'communityId'">
                    <el-select v-model="queryParams.communityId" placeholder="请选择小区" style="width: 100%"
                        @change="(val) => changeCommunity(val, queryParams)">
                        <el-option v-for="item in userStore.communityList" :key="item.id" :label="item.communityName"
                            :value="item.id" />
                    </el-select>
                </template>
                <template v-if="prop == 'apartmentId'">
                    <el-select v-model="queryParams.apartmentId" placeholder="请选择小区" style="width: 100%"
                        :disabled="!queryParams.communityId">
                        <el-option v-for="item in apartmentList" :key="item.id" :label="item.apartmentName"
                            :value="item.id" />
                    </el-select>
                </template>
            </template>
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'age'">
                    <el-tag style="margin-right: 5px;" v-for="item in row.age.split(',')">{{ item }}</el-tag>
                </template>
            </template>
        </CustomTable>
        <CustomDialog :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
            @save="handleSave" @formChange="handleFormChange">
            <template #custom-item="{ prop, form }">
                <div v-if="prop == 'age'" style="width: 100%;">
                    <el-input-tag v-model="form.age" placeholder="请输入年龄并通过回车添加" aria-label="输入完成后按回车键即可添加" />
                </div>
            </template>
        </CustomDialog>
    </div>
</template>

<script setup name="Roomdetail">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import useUserStore from '@/store/modules/user';
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, reactive, ref } from "vue";
import { getTenantListApi, addTenantApi, editTenantApi, delTenantApi, getAllUnitApi, exportTenantApi } from "@/api/base/index";
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
const apartmentList = ref([]);
const apartmentList_form = ref([]);
const changeCommunity = (val, queryParams) => {
    queryParams.apartmentId = null;
    getAllUnitApi({ communityId: val }).then(res => apartmentList.value = res.data);
}
/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value }
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    getTenantListApi(query).then(res => {
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
            form.value = {};
            visible.value = true;
            break;
        case "download":
            exportTenantApi({ ...queryPramas.value }).then(res => {
                userStore.exportFileUrl(window.location.origin + "/" + res.data.filename);
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
            const { id, communityId, apartmentId, roomNo, peopleNum, age, serviceDetail } = row;
            form.value = { id, communityId, apartmentId, roomNo, peopleNum, age: age.split(','), serviceDetail };
            visible.value = true;
            break;
        case "delete":
            ElMessageBox.confirm('是否删除该住户？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning', }).then(() => {
                delTenantApi(row.id).then(res => resMsg(res))
            }).catch(() => { })
            break;
    }
}
/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val };
    formData.age = formData.age.join(',');
    val.id ? editTenantApi(formData, formData.id).then(res => resMsg(res)) : addTenantApi(formData).then(res => resMsg(res))
}
const resMsg = (res) => {
    if (res.code == 200) {
        ElMessage.success(res.msg);
        visible.value = false;
        getData();
        userStore.updateCommunity();
    } else {
        ElMessage.error(res.msg)
    }
}
/**
 * 表单选项变更操作
 */
const handleFormChange = (val) => {
    const { prop, form } = val
    switch (prop) {
        case "communityId":
            form.apartmentId = null;
            getAllUnitApi({ communityId: form.communityId }).then(res => apartmentList_form.value = res.data);
            break;
    }
}
// 表格配置项
const option = reactive({
    showSearch: true,  // 显示隐藏
    searchLabelWidth: 70,  // 搜索标题宽度
    /** 搜索字段配置项 */
    searchItem: [
        { type: "custom", label: "小区", prop: "communityId", isOmit: false, default: null },
        { type: "custom", label: "单元", prop: "apartmentId", isOmit: false, default: null },
        { type: "input", label: "房间号", prop: "roomNo", max: 99, verify: "", isOmit: false, default: null },
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        {
            key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false,
            hasPermi: ['com.chargehub.locker.roomdetail.domain.LockerRoomDetail:crud:CREATE']
        },
        {
            key: "download", text: "导出", icon: "Download", isShow: true, type: "primary", disabled: false,
            hasPermi: ['com.chargehub.locker.roomdetail.domain.LockerRoomDetail:crud:EXPORT']
        },
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
        { type: 'text', label: '小区', prop: 'communityId_dictText', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '单元', prop: 'apartmentId_dictText', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '房间号', prop: 'roomNo', width: 160, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '人数', prop: 'peopleNum', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'custom', label: '年龄分布', prop: 'age', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '服务详情', prop: 'serviceDetail', width: 300, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '管理员', prop: 'adminId_dictText', width: 160, fixed: false, sortable: false, isShow: true, isOmit: true },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 200,
        fixed: false,
    },
    menuItemBtn: [
        {
            type: 'primary', isShow: true, icon: 'Edit', label: '编辑', value: 'edit',
            hasPermi: ['com.chargehub.locker.roomdetail.domain.LockerRoomDetail:crud:EDIT']
        },
        {
            type: 'danger', isShow: true, icon: 'Delete', label: '删除', value: 'delete',
            hasPermi: ['com.chargehub.locker.roomdetail.domain.LockerRoomDetail:crud:DELETE']
        },
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
})
// 表单配置项
const optionDialog = reactive({
    dialogTitle: '住户配置',
    dialogClass: 'dialog_xs',
    labelWidth: 'auto',
    formitem: [
        {
            type: "select", label: "小区", prop: "communityId", customValue: "id", customLabel: "communityName",
            filterable: true, dicData: computed(() => { return userStore.communityList }), default: null, span: 2
        },
        {
            type: "select", label: "单元", prop: "apartmentId", customValue: "id", customLabel: "apartmentName",
            filterable: true, dicData: computed(() => { return apartmentList_form.value }), default: null, span: 2
        },
        { type: "input", label: "房间号", prop: "roomNo", max: 99, verify: "", default: null, span: 2 },
        { type: "input", label: "人数", prop: "peopleNum", max: 2, verify: regularNumber, default: null, span: 2 },
        { type: "custom", label: "年龄分布", prop: "age", default: [], span: 2 },
        { type: "input", label: "服务详情", prop: "serviceDetail", category: 'textarea', max: 99, verify: "", default: null, span: 2, rows: 3 },
    ],
    rules: {
        communityId: [{ required: true, message: '请选择小区', trigger: 'change' }],
        apartmentId: [{ required: true, message: '请选择单元', trigger: 'change' }],
        roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
        peopleNum: [{ required: true, message: '请输入人数', trigger: 'blur' }],
        age: [{ required: true, message: '请输入年龄分布', trigger: 'blur' }],
        serviceDetail: [{ required: true, message: '请输入服务详情', trigger: 'blur' }],
    }
})
</script>

<style scoped></style>
