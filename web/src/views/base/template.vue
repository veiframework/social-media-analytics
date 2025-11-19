<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
            @currentChange="handleChange">
            <template #table-top></template>
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'boxNum'">
                    {{ row.conf.boxNum || 0 }}
                </template>
            </template>
        </CustomTable>
        <CustomDialog :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
            @save="handleSave">
            <template #custom-item="{ prop, form }">
                <div v-if="prop == 'openTime'">
                </div>
            </template>
        </CustomDialog>
    </div>
</template>

<script setup name="Template">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import useUserStore from '@/store/modules/user';
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref } from "vue";
import { getLockerTemplateListApi, addLockerTemplateApi, editLockerTemplateApi, delLockerTemplateApi } from "@/api/base/index";
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
/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value }
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    getLockerTemplateListApi(query).then(res => {
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
    }
}
/**
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "edit":
            const { id, lockerType, name, conf } = row;
            form.value = { id, lockerType, name, boxNum: conf.boxNum || 0 };
            visible.value = true;
            break;
        case "delete":
            ElMessageBox.confirm('是否删除该模板？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning', }).then(() => {
                delLockerTemplateApi(row.id).then(res => resMsg(res))
            }).catch(() => { })
            break;
    }
}
/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val };
    const boxObj = { boxNum: formData.boxNum || 0, boxSize: { superLarge: [], middle: [], large: [], small: [], mini: [] } };
    boxObj.boxSize.middle = Array.from({ length: boxObj.boxNum }, (_, index) => index + 1);
    formData.conf = boxObj;
    delete formData.boxNum;
    val.id ? editLockerTemplateApi(formData, formData.id).then(res => resMsg(res)) : addLockerTemplateApi(formData).then(res => resMsg(res))
}
const resMsg = (res) => {
    if (res.code == 200) {
        ElMessage.success(res.msg);
        visible.value = false;
        getData();
        userStore.updateTemplate();
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
            type: "select", label: "模板类型", prop: "lockerType", filterable: true, default: null,
            dicData: [{ label: '主柜模板', value: 0 }, { label: '副柜模板', value: 1 }]
        },
        { type: "input", label: "模板名称", prop: "name", max: 99, verify: "", isOmit: false, default: null },
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        {
            key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false,
            hasPermi: ['com.chargehub.locker.template.domain.LockerConfTemplate:crud:CREATE']
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
        {
            type: "tag", label: "模板类型", prop: "lockerType", width: 200, sortable: false, isShow: true, isOmit: true,
            dicData: [{ label: '主柜模板', value: 0, type: 'success' }, { label: '副柜模板', value: 1 }]
        },
        { type: 'text', label: '模板名称', prop: 'name', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'custom', label: '格口数量', prop: 'boxNum', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
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
            hasPermi: ['com.chargehub.locker.template.domain.LockerConfTemplate:crud:EDIT']
        },
        {
            type: 'danger', isShow: true, icon: 'Delete', label: '删除', value: 'delete',
            hasPermi: ['com.chargehub.locker.template.domain.LockerConfTemplate:crud:DELETE']
        },
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
})
// 表单配置项
const optionDialog = reactive({
    dialogTitle: '模板配置',
    dialogClass: 'dialog_xs',
    labelWidth: 'auto',
    formitem: [
        {
            type: "radio_btn", label: "模板类型", prop: "lockerType", default: 0, span: 2,
            dicData: [{ value: 0, label: '主柜模板' }, { value: 1, label: '副柜模板' }]
        },
        { type: "input", label: "模板名称", prop: "name", max: 99, verify: "", default: null, span: 2 },
        { type: "input", label: "箱格数量", prop: "boxNum", max: 99, verify: regularNumber, default: null, span: 2, append: "个" },
    ],
    rules: {
        lockerType: [{ required: true, message: '请选择模板类型', trigger: 'change' }],
        name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
        boxNum: [{ required: true, message: '请输入箱格数量', trigger: 'blur' }],
    }
})
</script>

<style scoped></style>
