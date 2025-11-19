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
            @save="handleSave" @formBlur="handleFormBlur">
            <template #custom-item="{ prop, form }">
                <div v-if="prop == 'roomList'" style="width: 100%;">
                    <el-table :data="form.roomList" max-height="400" border>
                        <el-table-column prop="floorNo" label="楼层号/房间号" min-width="110" align="center" fixed />
                        <el-table-column v-for="item in Number(form.roomNum)" :key="item" :prop="`roomNo${item}`"
                            :label="`房间号${item}`" min-width="120" align="center">
                            <template #default="{ row }">
                                <el-input v-model="row[`roomNo${item}`]" />
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </template>
        </CustomDialog>
    </div>
</template>

<script setup name="Unit">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import useUserStore from '@/store/modules/user';
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, reactive, ref } from "vue";
import { getUnitListApi, addUnitApi, editUnitApi, delUnitApi, bindDeliverymanApi } from "@/api/base/index";
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
    getUnitListApi(query).then(res => {
        tableData.value = res.data.records;
        for (const element of tableData.value) {
            element.QRCode = `${window.location.origin}?id=${element.id}`;
        }
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
            const { id, communityId, apartmentName, floorNum, roomNum, remark } = row;
            form.value = { id, communityId, apartmentName, floorNum, roomNum, roomList: deStructFloorReverse(row), remark };
            visible.value = true;
            break;
        case "delete":
            ElMessageBox.confirm('是否删除该小区？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning', }).then(() => {
                delUnitApi(row.id).then(res => resMsg(res))
            }).catch(() => { })
            break;
    }
}
/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val };
    formData.roomList = deStructFloor(formData);
    val.id ? editUnitApi(formData, formData.id).then(res => resMsg(res)) : addUnitApi(formData).then(res => resMsg(res))
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
 * 表单输入项失焦操作
 */
const handleFormBlur = (val) => {
    const { prop, form } = val
    switch (prop) {
        case "floorNum":
        case "roomNum":
            form.roomList = structFloor(form);
            break;
    }
}
/**
 * 楼层结构化
 */
const structFloor = (val) => {
    const { floorNum, roomNum } = val;
    const floorList = [];
    for (let i = 1; i <= floorNum; i++) {
        const floor = { floorNo: `${i}层` };
        for (let j = 1; j <= roomNum; j++) {
            floor[`roomNo${j}`] = `${i}${j.toString().padStart(2, '0')}`;
        }
        floorList.push(floor);
    }
    return floorList;
}
/**
 * 楼层解构
 */
const deStructFloor = (val) => {
    const { roomList } = val;
    const result = [];
    roomList.forEach(floor => {
        const floorNo = parseInt(floor.floorNo);
        Object.keys(floor).forEach(key => {
            if (key.startsWith('roomNo')) {
                result.push({
                    floorNo: floorNo,
                    roomNo: parseInt(floor[key])
                });
            }
        });
    });
    return result;
}
// 楼层逆解构
const deStructFloorReverse = (val) => {
    if (val.roomNum <= 0 || val.floorNum <= 0) return [];
    if (!val.roomList || val.roomList.length <= 0) return [];
    const result = [];
    const floorMap = new Map();
    val.roomList.forEach(item => {
        const { floorNo, roomNo } = item;
        if (!floorMap.has(floorNo)) floorMap.set(floorNo, { floorNo: `${floorNo}层`, roomList: [] });
        floorMap.get(floorNo).roomList.push(roomNo);
    });
    // 将 Map 转换为数组并排序
    floorMap.forEach((value, key) => {
        const floor = { floorNo: value.floorNo };
        // 对房间号进行排序
        value.roomList.sort((a, b) => a - b);
        // 将房间号转换为 roomNo1, roomNo2 等格式
        value.roomList.forEach((roomNo, index) => { floor[`roomNo${index + 1}`] = roomNo.toString(); });
        result.push(floor);
    });
    // 按楼层号排序
    return result.sort((a, b) => {
        const floorA = parseInt(a.floorNo);
        const floorB = parseInt(b.floorNo);
        return floorA - floorB;
    });
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
        { type: "input", label: "单元名称", prop: "apartmentName", max: 99, verify: "", isOmit: false, default: null },
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        {
            key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false,
            hasPermi: ['com.chargehub.locker.apartment.domain.LockerApartmentEntity:crud:CREATE']
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
        { type: 'text', label: '单元名称', prop: 'apartmentName', width: 200, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '楼层数', prop: 'floorNum', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'text', label: '房间/层', prop: 'roomNum', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
        { type: 'QRCode', label: '二维码', prop: 'QRCode', width: 240, fixed: false, sortable: false, isShow: true, isOmit: true },
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
            hasPermi: ['com.chargehub.locker.apartment.domain.LockerApartmentEntity:crud:EDIT']
        },
        {
            type: 'danger', isShow: true, icon: 'Delete', label: '删除', value: 'delete',
            hasPermi: ['com.chargehub.locker.apartment.domain.LockerApartmentEntity:crud:DELETE']
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
    dialogClass: 'dialog_md',
    labelWidth: '100',
    formitem: [
        {
            type: "select", label: "小区名称", prop: "communityId", customValue: "id", customLabel: "communityName", filterable: true,
            dicData: computed(() => { return userStore.communityList }), default: null
        },
        { type: "input", label: "单元名称", prop: "apartmentName", max: 99, verify: "", default: null },
        { type: "input", label: "楼层", prop: "floorNum", max: 2, verify: regularNumber, default: null, append: "层" },
        { type: "input", label: "房间", prop: "roomNum", max: 2, verify: regularNumber, default: null, append: "间/层" },
        { type: "custom", label: "房间分布", prop: "roomList", default: null, span: 2, judge: (form) => { return form.roomList && form.roomList.length > 0 } },
        { type: "input", label: "备注", prop: "remark", category: 'textarea', max: 99, verify: "", default: null, span: 2, rows: 3 },
    ],
    rules: {
        communityId: [{ required: true, message: '请选择小区名称', trigger: 'change' }],
        apartmentName: [{ required: true, message: '请输入单元名称', trigger: 'blur' }],
        floorNum: [{ required: true, message: '请输入楼层数量', trigger: 'blur' }],
        roomNum: [{ required: true, message: '请输入每层房间数量', trigger: 'blur' }],
    }
})
</script>

<style scoped></style>
