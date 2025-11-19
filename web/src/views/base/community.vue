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
            @save="handleSave">
            <template #custom-item="{ prop, form }">
                <div v-if="prop == 'openTime'">
                </div>
            </template>
        </CustomDialog>
    </div>
</template>

<script setup name="Community">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import useUserStore from '@/store/modules/user';
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref } from "vue";
import { getCommunityListApi, addCommunityApi, editCommunityApi, delCommunityApi } from "@/api/base/index";
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
    getCommunityListApi(query).then(res => {
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
            const { id, communityName, address, provinceId, cityId, countyId, remark } = row;
            form.value = { id, communityName, address, province: provinceId, city: cityId, town: countyId, remark };
            visible.value = true;
            break;
        case "delete":
            ElMessageBox.confirm('是否删除该小区？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning', }).then(() => {
                delCommunityApi(row.id).then(res => resMsg(res))
            }).catch(() => { })
            break;
    }
}
/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val };
    formData.provinceId = formData.province;
    formData.cityId = formData.city;
    formData.countyId = formData.town;
    delete formData.province;
    delete formData.city;
    delete formData.town;
    val.id ? editCommunityApi(formData, formData.id).then(res => resMsg(res)) : addCommunityApi(formData).then(res => resMsg(res))
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
// 表格配置项
const option = reactive({
    showSearch: true,  // 显示隐藏
    searchLabelWidth: 70,  // 搜索标题宽度
    /** 搜索字段配置项 */
    searchItem: [
        { type: "input", label: "名称", prop: "communityName", max: 99, verify: "", isOmit: false, default: null },
        { type: "input", label: "地址", prop: "address", max: 99, verify: "", isOmit: false, default: null, span: 2 },
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        {
            key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false,
            hasPermi: ['com.chargehub.locker.community.domain.LockerCommunityEntity:crud:CREATE']
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
        { type: 'text', label: '小区名称', prop: 'communityName', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '详细地址', prop: 'address', width: 240, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '备注', prop: 'remark', width: 300, fixed: false, sortable: false, isShow: true, isOmit: true },
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
            hasPermi: ['com.chargehub.locker.community.domain.LockerCommunityEntity:crud:EDIT']
        },
        {
            type: 'danger', isShow: true, icon: 'Delete', label: '删除', value: 'delete',
            hasPermi: ['com.chargehub.locker.community.domain.LockerCommunityEntity:crud:DELETE']
        },
    ],
    /** 更多菜单配置项（type: 'more'） 配置时生效 */
    moreItem: [],
    /** page 分页配置项 */
    isShowPage: true,
})
// 表单配置项
const optionDialog = reactive({
    dialogTitle: '小区配置',
    dialogClass: 'dialog_xs',
    labelWidth: 'auto',
    formitem: [
        { type: "input", label: "小区名称", prop: "communityName", max: 99, verify: "", default: null, span: 2 },
        { type: "address", label: "所在地区", labels: '详细地址', default: null, span: 2 },
        { type: "input", label: "备注", prop: "remark", category: 'textarea', max: 99, verify: "", default: null, span: 2, rows: 3 },
    ],
    rules: {
        communityName: [{ required: true, message: '请输入小区名称', trigger: 'blur' }],
        address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
        town: [{ required: true, message: '请选择区县', trigger: 'change' }],
    }
})
</script>

<style scoped></style>
