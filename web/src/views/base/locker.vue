<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
            @currentChange="handleChange">
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
                    <el-input-number v-model="form.deputyCount" :min="0" :max="20" :step="1" style="width: 60%;" />
                    <el-popover placement="right" title="副柜模板" width="400" trigger="click">
                        <template #reference>
                            <el-button style="margin-left: 5px;">配置副柜模板</el-button>
                        </template>
                        <div class="popper_body">
                            <p class="popper_content" v-for="item in form.deputyCount" :key="item">
                                <span class="text">{{ item }}号副柜：</span>
                                <el-select class="item" v-model="form.deputyTemplateIds[item - 1]" placeholder="请选择副柜模板"
                                    :teleported="false" @change="(id) => selPopper(id, form)">
                                    <el-option v-for="itm in deputyData" :key="itm.id" :label="itm.name"
                                        :value="itm.id" />
                                </el-select>
                            </p>
                            <el-empty v-if="!form.deputyCount" description="未选择" />
                        </div>
                    </el-popover>
                </div>
            </template>
        </CustomDialog>
        <CustomInfo :option="optionInfo" :visible="infoVisible" :rowData="rowData" @cancel="infoVisible = false">
        </CustomInfo>
    </div>
</template>

<script setup name="Locker">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import CustomInfo from "@/components/CustomInfo";
import useUserStore from '@/store/modules/user';
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, reactive, ref } from "vue";
import { getLockerListApi, addLockerApi, editLockerApi, delLockerApi, getAllUnitApi, restartLockerApi, changeStatusApi, unbindImeiApi, bindImeiApi } from "@/api/base/index";
import { regularNumber, regularNumberDo } from "@/utils/verify";
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
// 详情参数
const infoVisible = ref(false);
const rowData = ref();
// 自定义参数
const deputyData = computed(() => { return userStore.templateList.filter(item => item.lockerType == 1) });
const mainData = computed(() => { return userStore.templateList.filter(item => item.lockerType == 0) });
const unitData = ref([]);
/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value }
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    getLockerListApi(query).then(res => {
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
            form.value = { deputyTemplateIds: [] };
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
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "view":
            rowData.value = row;
            infoVisible.value = true;
            break;
        case "edit":
            const {
                id, communityId, apartmentId, lockerName, lockerType, mainLockerIndex, mainTemplateId, deputyCount,
                imei, longitude, latitude, address, remark, useStatus, deputyTemplateIds
            } = row;
            form.value = {
                id, communityId, apartmentId, lockerName, lockerType, mainLockerIndex, mainTemplateId, deputyCount,
                imei, longitude, latitude, address, remark, useStatus, deputyTemplateIds: deputyTemplateIds ? deputyTemplateIds : []
            };
            handleFormChange({ prop: 'communityId', form: form.value })
            visible.value = true;
            break;
        case "delete":
            ElMessageBox.confirm('是否删除该柜子？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(() => {
                delLockerApi(row.id).then(res => resMsg(res))
            }).catch(() => { })
            break;
        case "start":
        case "stop":
            ElMessageBox.confirm(`是否${row.useStatus == "1" ? '停用' : '启用'}该柜子？`, '提示',
                { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(() => {
                    changeStatusApi({ id: row.id, useStatus: row.useStatus == "1" ? 0 : 1 }).then(res => resMsg(res))
                }).catch(() => { })
            break;
        case "restart":
            ElMessageBox.confirm('是否重启该柜子？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(() => {
                restartLockerApi(row.id).then(res => resMsg(res))
            }).catch(() => { })
            break;
        case "bind":
            ElMessageBox.prompt('请输入要绑定的imei串号', '提示', { confirmButtonText: '确定', cancelButtonText: '取消' }).then(({ value }) => {
                bindImeiApi({ id: row.id, imei: value }).then(res => resMsg(res))
            })
            break;
        case "unbind":
            ElMessageBox.confirm('是否解绑imei？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(() => {
                unbindImeiApi(row.id).then(res => resMsg(res))
            }).catch(() => { })
            break;
    }
}
/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val };
    if (!formData.id) formData.useStatus = 1;
    if (!formData.lockerType) {
        delete formData.mainTemplateId;
        delete formData.deputyTemplateIds;
        delete formData.mainLockerIndex;
        formData.deputyCount = 0;
    }
    if (!formData.deputyCount) formData.deputyTemplateIds = [];
    val.id ? editLockerApi(formData, formData.id).then(res => resMsg(res)) : addLockerApi(formData).then(res => resMsg(res))
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
/**
 * 生成副柜模板
 */
const selPopper = (id, form) => {
    for (let index = 0; index < form.deputyCount; index++) {
        form.deputyTemplateIds[index] = form.deputyTemplateIds[index] ? form.deputyTemplateIds[index] : id;
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
        {
            type: "select", label: "柜子类型", prop: "lockerType", filterable: true, default: null,
            dicData: [{ label: '共享柜', value: 0 }, { label: '专属柜', value: 1 }]
        },
        // {
        //     type: "select", label: "柜子状态", prop: "status", filterable: true, default: null,
        //     dicData: [
        //         { label: '正常', value: 0 }, { label: '故障', value: 1 }, { label: '待审核', value: 2 }, { label: '已驳回', value: 3 },
        //         { label: '待同步', value: 4 }, { label: '重置中', value: 5 }, { label: '移机中', value: 6 },
        //     ]
        // },
        {
            type: "select", label: "在线状态", prop: "onlineState", filterable: true, default: null,
            dicData: [{ label: '在线', value: 1 }, { label: '离线', value: 0 }]
        },
        { type: "input", label: "柜子编号", prop: "lockerNo", max: 99, verify: "", isOmit: false, default: null },
        { type: "input", label: "柜子名称", prop: "lockerName", max: 99, verify: "", isOmit: false, default: null },
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        {
            key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false,
            hasPermi: ['com.chargehub.locker.lockerinfo.domain.LockerInfoEntity:crud:CREATE']
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
        { type: 'text', label: '小区名称', prop: 'communityId_dictText', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '单元名称', prop: 'apartmentId_dictText', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '柜子编号', prop: 'lockerNo', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '柜子名称', prop: 'lockerName', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        {
            type: "tag", label: "柜子类型", prop: "lockerType", width: 110, sortable: false, isShow: true, isOmit: true,
            dicData: [{ label: '共享柜', value: 0, type: 'success' }, { label: '专属柜', value: 1 }]
        },
        // {
        //     type: "tag", label: "柜子状态", prop: "status", width: 110, sortable: false, isShow: true, isOmit: true,
        //     dicData: [
        //         { label: '正常', value: 0, type: 'success' }, { label: '故障', value: 1, type: 'danger' }, { label: '待审核', value: 2, type: 'info' },
        //         { label: '已驳回', value: 3, type: 'danger' }, { label: '待同步', value: 4 }, { label: '重置中', value: 5, type: 'warning' },
        //         { label: '移机中', value: 6, type: 'warning' },
        //     ]
        // },
        {
            type: "tag", label: "在线状态", prop: "onlineState", width: 110, sortable: false, isShow: true, isOmit: true,
            dicData: [{ label: '在线', value: 1, type: 'success' }, { label: '离线', value: 0, type: 'info' }]
        },
        { type: 'text', label: '经度', prop: 'longitude', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '纬度', prop: 'latitude', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '副柜数量', prop: 'deputyCount', width: 100, fixed: false, sortable: false, isShow: true, isOmit: true },
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 280,
        fixed: false,
    },
    menuItemBtn: [
        {
            type: 'primary', isShow: true, icon: 'View', label: '详情', value: 'view',
            hasPermi: ['com.chargehub.locker.lockerinfo.domain.LockerInfoEntity:crud:PAGE']
        },
        {
            type: 'primary', isShow: true, icon: 'Edit', label: '编辑', value: 'edit',
            hasPermi: ['com.chargehub.locker.lockerinfo.domain.LockerInfoEntity:crud:EDIT']
        },
        {
            type: 'danger', isShow: true, icon: 'Delete', label: '删除', value: 'delete',
            hasPermi: ['com.chargehub.locker.lockerinfo.domain.LockerInfoEntity:crud:DELETE']
        },
        { type: 'more', isShow: true, hasPermi: [] },
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [
        { isShow: true, label: '启用', value: "start", hasPermi: ['locker:info:useStatus'], judge: (row) => { return row.useStatus == "0" } },
        { isShow: true, label: '停用', value: "stop", hasPermi: ['locker:info:useStatus'], judge: (row) => { return row.useStatus == "1" } },
        { isShow: true, label: '重启', value: "restart", hasPermi: ['locker:info:restart'] },
        { isShow: true, label: '绑定imei', value: "bind", hasPermi: ['locker:info:imei'], judge: (row) => { return !row.imei } },
        { isShow: true, label: '解绑imei', value: "unbind", hasPermi: ['locker:unbind:imei'], judge: (row) => { return row.imei && true } },
    ],
    /** page 分页配置项 */
    isShowPage: true,
})
// 表单配置项
const optionDialog = reactive({
    dialogTitle: '柜子配置',
    dialogClass: 'dialog_md',
    labelWidth: '100',
    formitem: [
        {
            type: "select", label: "小区名称", prop: "communityId", customValue: "id", customLabel: "communityName", filterable: true,
            dicData: computed(() => { return userStore.communityList }), default: null
        },
        {
            type: "select", label: "单元名称", prop: "apartmentId", customValue: "id", customLabel: "apartmentName", filterable: true,
            dicData: computed(() => { return unitData.value }), default: null
        },
        { type: "input", label: "柜子名称", prop: "lockerName", max: 99, verify: "", default: null },
        {
            type: "radio_btn", label: "柜子类型", prop: "lockerType", default: 0,
            dicData: [{ value: 0, label: '共享柜' }, { value: 1, label: '专属柜' }]
        },
        {
            type: "input", label: "主柜序号", prop: "mainLockerIndex", max: 2, verify: regularNumber, default: null,
            judge: (form) => { return form.lockerType == 1 }
        },
        {
            type: "select", label: "主柜模板", prop: "mainTemplateId", customValue: "id", customLabel: "name", filterable: true,
            dicData: mainData, default: null, judge: (form) => { return form.lockerType == 1 }
        },
        { type: "custom", label: "副柜数量", prop: "deputyCount", default: null, judge: (form) => { return form.lockerType == 1 } },
        { type: "input", label: "imei", prop: "imei", max: 99, verify: "", default: null },
        { type: "input", label: "经度", prop: "longitude", max: 20, verify: regularNumberDo, default: null },
        { type: "input", label: "纬度", prop: "latitude", max: 20, verify: regularNumberDo, default: null },
        { type: "input", label: "详细地址", prop: "address", category: 'textarea', max: 99, verify: "", default: null, span: 2, rows: 3 },
        { type: "input", label: "备注", prop: "remark", category: 'textarea', max: 99, verify: "", default: null, span: 2, rows: 3 },
    ],
    rules: {
        communityId: [{ required: true, message: '请选择小区名称', trigger: 'change' }],
        apartmentId: [{ required: true, message: '请选择单元名称', trigger: 'change' }],
        lockerType: [{ required: true, message: '请选择柜子类型', trigger: 'change' }],
        mainTemplateId: [{ required: true, message: '请选择主柜模板', trigger: 'change' }],
        mainLockerIndex: [{ required: true, message: '请输入主柜排列序号', trigger: 'blur' }],
        deputyCount: [{ required: true, message: '请输入富贵模板数', trigger: 'blur' }],
        // imei: [{ required: true, message: '请输入设备 imei 标识', trigger: 'blur' }],
        longitude: [{ required: true, message: '请输入经度', trigger: 'blur' }],
        latitude: [{ required: true, message: '请输入纬度', trigger: 'blur' }],
        address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
    }
})
// 详情配置项
const optionInfo = reactive({
    dialogClass: 'dialog_md',
    labelWidth: '100',
    /** 表格详情配置项 */
    tableInfoItem: [
        {
            title: '基本信息', column: 2, infoData: [
                { type: 'text', label: '小区名称', prop: 'communityId_dictText', isShow: true },
                { type: 'text', label: '单元名称', prop: 'apartmentId_dictText', isShow: true },
                { type: 'text', label: '柜子编号', prop: 'lockerNo', isShow: true },
                { type: 'text', label: '柜子名称', prop: 'lockerName', isShow: true },
                {
                    type: 'tag', label: '柜子类型', prop: 'lockerType', isShow: true,
                    dicData: [{ label: '共享柜', value: 0, type: 'success' }, { label: '专属柜', value: 1 }]
                },
                // {
                //     type: 'tag', label: '柜子状态', prop: 'status', isShow: true,
                //     dicData: [
                //         { label: '正常', value: 0, type: 'success' }, { label: '故障', value: 1, type: 'danger' },
                //         { label: '待审核', value: 2, type: 'info' }, { label: '已驳回', value: 3, type: 'danger' },
                //         { label: '待同步', value: 4 }, { label: '重置中', value: 5, type: 'warning' },
                //         { label: '移机中', value: 6, type: 'warning' },
                //     ]
                // },
                {
                    type: 'tag', label: '在线状态', prop: 'onlineState', isShow: true,
                    dicData: [{ label: '在线', value: 1, type: 'success' }, { label: '离线', value: 0, type: 'info' }]
                },
                {
                    type: 'tag', label: '可用状态', prop: 'useStatus', isShow: true,
                    dicData: [{ label: '可用', value: 1, type: 'success' }, { label: '停用', value: 0, type: 'danger' }]
                },
            ]
        },
        {
            title: '设备信息', column: 2, infoData: [
                { type: 'text', label: '详细地址', prop: 'name', isShow: true, span: 2 },
                { type: 'text', label: '经度', prop: 'longitude', isShow: true },
                { type: 'text', label: '纬度', prop: 'latitude', isShow: true },
                { type: 'text', label: '副柜数量', prop: 'deputyCount', isShow: true },
                { type: 'text', label: 'imei', prop: 'imei', isShow: true },
                { type: 'text', label: '备注', prop: 'remark', isShow: true, span: 2 },
            ]
        },
    ],
})
</script>

<style scoped lang="scss">
.popper_body {

    .popper_content {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .text {
            flex-shrink: 0;
            flex-grow: 0;
        }

        .item {
            flex-shrink: 1;
            flex-grow: 1;
        }
    }
}
</style>
