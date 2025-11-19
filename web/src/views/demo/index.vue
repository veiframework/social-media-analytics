<template>
	<div>
		<CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
			:total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
			@currentChange="handleChange" @selectData="selectData">
			<template #table-top></template>
			<template #table-custom="{ row, prop, index }">
				<template v-if="prop == 'custom'">
				</template>
			</template>
		</CustomTable>
		<CustomDialog :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
			@save="handleSave" @formChange="handleFormChange" @formBlur="handleFormBlur">
			<template #custom-item="{ prop, form }">
				<div v-if="prop == 'openTime'">
				</div>
			</template>
		</CustomDialog>
		<CustomInfo :option="optionInfo" :visible="infoVisible" :rowData="rowData" @cancel="infoVisible = false">
		</CustomInfo>
	</div>
</template>

<script setup name="system_user">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import CustomInfo from "@/components/CustomInfo";
import useUserStore from '@/store/modules/user';
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref } from "vue";
// import { getSiteListApi } from "@/api/station";
// import { getBlackListApi } from "@/api/userInfo";
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
/**
 * 请求列表
 */
const getData = () => {
	const query = { ...queryPramas.value }
	query.pageNum = pageNum.value;
	query.pageSize = pageSize.value;
	// getBlackListApi(query).then(res => {
	// 	tableData.value = res.data.records;
	// 	total.value = res.data.total;
	// })
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
			const { id, stationId, pileId, chargePort, useStatus, remark } = row;
			form.value = { id, stationId, pileId, chargePort, useStatus, remark };
			visible.value = true;
			break;
		case "delete":
			ElMessageBox.confirm('是否删除该站点名称？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning', }).then(() => {
				delGunApi(row.id).then(res => resMsg(res))
			}).catch(() => { })
			break;
	}
}
/**
 * 表单保存
 */
const handleSave = (val) => {
	const formData = { ...val };
	val.id ? editGunApi(formData, formData.id).then(res => resMsg(res)) : addGunApi(formData).then(res => resMsg(res))
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
 * 跨页选择Id
 */
const selectData = (val) => {
	console.log(val)
}
/**
 * 表单选项变更操作
 */
const handleFormChange = (val) => {
	const { prop, form } = val
	switch (prop) {
		case "template":
			break;
	}
}
/**
 * 表单输入项失焦操作
 */
const handleFormBlur = (val) => {
	const { prop, form } = val
	switch (prop) {
		case "ratedV":
			break;
	}
}
// 表格配置项
const option = reactive({
	showSearch: true,  // 显示隐藏
	searchLabelWidth: 90,  // 搜索标题宽度
	/** 搜索字段配置项 */
	searchItem: [
		{ type: "input", label: "文本", prop: "input", max: 99, verify: "", isOmit: false, default: null },
		{ type: "select", label: "选择", prop: "select", customValue: "customValue", customLabel: "customLabel", filterable: true, isOmit: false, dicData: [], default: null },
		{ type: "date", label: "日期", prop: "date", format: "YYYY-MM-DD", valueFormat: "YYYY-MM-DD", category: "date", isOmit: true, default: null },
		{
			type: "select", label: "站点类型", prop: "type", default: null, filterable: true,
			dicData: [{ value: 0, label: '关闭' }, { value: 1, label: '开启' }]
		},
		{
			type: "select", label: "站点状态", prop: "stationStatus_multiple", isOmit: true, default: null, filterable: true,
			dicData: [
				{ value: "stationStatus_0", label: "可用" }, { value: "stationStatus_1", label: "不可用" },
				{ value: "stationPut_0", label: "开放" }, { value: "stationPut_1", label: "不开放" }
			]
		},
		// {
		// 	type: "select_remote", label: "站点名称", prop: "stationIds", customValue: "id", customLabel: "name", default: null, defaultText: null, multiple: true,
		// 	limit: computed(() => { return 2 }), request: getSiteListApi, requestParams: [{ key: 'regionId', value: 'form' }],
		// 	key: "name", itemNum: 20, span: 2, judge: (from) => { return from.cardId }
		// },
		{ type: "datetime", label: "时间", prop: "datetime", format: "YYYY-MM-DD HH:mm:ss", valueFormat: "YYYY-MM-DD HH:mm:ss", category: "datetime", isOmit: true, default: null },
		{ type: "daterange", label: "日期", prop: "daterange", format: "YYYY-MM-DD", valueFormat: "YYYY-MM-DD", category: "daterange", isOmit: true, default: null },
		{
			type: "datetimerange", label: "时间", prop: "datetimerange", format: "YYYY-MM-DD HH:mm:ss", valueFormat: "YYYY-MM-DD HH:mm:ss",
			category: "datetimerange", isOmit: true, default: []
		},
	],
	/** 表格顶部左侧 button 配置项 */
	headerBtn: [
		{ key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", hasPermi: [], disabled: false },
	],
	operation: [
		{ key: "delete", text: "删除", isShow: true, hasPermi: [] },
	],
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
		{ type: 'text', label: '文本', prop: 'text', width: 120, fixed: false, sortable: false, isShow: true, color: 'red', isOmit: true },
		{
			type: 'tag', label: '标签', prop: 'tag', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true,
			dicData: [{ value: '0', label: '不开放' }, { value: '1', label: '开放' }]
		},
		{
			type: 'double', label: '多级标题', prop: '', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true, children: [
				{ label: '文本', prop: 'text1', width: 120, sortable: false, color: 'red' },
				{ label: '文本', prop: 'text2', width: 120, sortable: false, color: 'red' },
			]
		},
		{ type: 'img', label: '图片', prop: '', width: 120, fixed: false, sortable: false, isShow: true, imgWidth: 60, fit: 'cover', isOmit: true },
		{ type: 'custom', label: '自定义', prop: '', width: 120, fixed: false, sortable: false, isShow: true, isOmit: true },
	],
	/** 操作菜单配置项 */
	menu: {
		isShow: true,
		width: 260,
		fixed: false,
	},
	menuItemBtn: [
		{ type: 'primary', isShow: true, icon: 'Edit', label: '编辑', value: 'edit', hasPermi: [], judge: (row) => { return row.tag == 1 } },
		{ type: 'more', isShow: true, hasPermi: [] },
	],
	/** 更多菜单配置项（type: 'more'） 配置时生效 */
	moreItem: [
		{ isShow: true, label: '编辑2', value: "edit", hasPermi: [] },
		{ isShow: true, label: '编辑3', value: "edit2", hasPermi: [] },
	],
	/** page 分页配置项 */
	isShowPage: true,
})
// 表单配置项
const optionDialog = reactive({
	dialogTitle: 'dialog 标题',
	dialogClass: 'dialog_xs',
	labelWidth: '120',
	formitem: [
		{ type: "input", label: "文本", prop: "input", max: 99, verify: "", default: null },
		{ type: "select", label: "选择", prop: "select", customValue: "customValue", customLabel: "customLabel", filterable: true, dicData: [], default: null },
		// {
		// 	type: "select_remote", label: "站点名称", prop: "stationId", customValue: "id", customLabel: "name", default: null, defaultText: null,
		// 	request: getSiteListApi, key: "name", itemNum: 20, disabled: false,
		// },
		{ type: "date", label: "日期", prop: "date", format: "YYYY-MM-DD", valueFormat: "YYYY-MM-DD", category: "date", default: null },
		{ type: "datetime", label: "时间", prop: "datetime", format: "YYYY-MM-DD HH:mm:ss", valueFormat: "YYYY-MM-DD HH:mm:ss", category: "datetime", default: null },
		{ type: "daterange", label: "日期", prop: "daterange", format: "YYYY-MM-DD", valueFormat: "YYYY-MM-DD", category: "daterange", default: null },
		{
			type: "datetimerange", label: "时间", prop: "datetimerange", format: "YYYY-MM-DD HH:mm:ss", valueFormat: "YYYY-MM-DD HH:mm:ss",
			category: "datetimerange", default: []
		},
		{ type: "radio", label: "状态", prop: "status", default: 1, isOpen: true },
		{
			type: "radio_btn", label: "修改类型", prop: "status", default: 1,
			dicData: [{ value: '0', label: '不开放' }, { value: '1', label: '开放' }]
		},
		{
			type: "radio", label: "修改类型", prop: "status", default: 1,
			dicData: [{ value: '0', label: '不开放' }, { value: '1', label: '开放' }]
		},
		{ type: "custom", label: "开放时间", prop: "openTime", default: 1, isOpen: true, span: 2 },
		{ type: "upload", label: "上传", prop: "upload", fileType: 'img', path: 'avatar' },
		{ type: "address", label: "所在地区", labels: '详细地址', default: null, span: 2 },
		{ type: "map", prop: "map", label: "地图", default: null, span: 2 },
		{ type: "rich", label: "内容", prop: "content", span: 2, path: 'notives' },
		{ type: "input", label: "备注", prop: "input", category: 'textarea', max: 99, verify: "", default: null, span: 2, rows: 3 },
	],
	rules: {
		username: [{ required: true, message: '请输入站点名称', trigger: 'blur' }],
		select: [{ required: true, message: '请选择所属区域', trigger: 'change' }],
	}
})
// 详情配置项
const optionInfo = reactive({
	dialogClass: 'dialog_md',
	labelWidth: '100',
	/** 表格详情配置项 */
	tableInfoItem: [
		{
			title: '站点基本信息', column: 2, infoData: [
				{ type: 'text', label: '站点名称', prop: 'name', isShow: true, span: 2 },
				{ type: 'text', label: '所属区域', prop: 'name', isShow: true },
				{
					type: 'tag', label: '站点类型', prop: 'tag', isShow: true,
					dicData: [{ value: '0', label: '不开放', type: 'info' }, { value: '1', label: '开放' }]
				},
				{ type: 'text', label: '详细地址', prop: 'name', isShow: true, span: 2 },
			]
		},
		{
			title: '设备输出信息', column: 2, infoData: [
				{ type: 'text', label: '直流总功率', prop: 'name', isShow: true },
				{ type: 'text', label: '交流总功率', prop: 'name', isShow: true },
			]
		},
	],
})
</script>

<style scoped></style>
