<template>
	<div>
		<CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getListData"
			:total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
			@currentChange="handleChange">
			<template #table-custom="{ row, prop, index }">
				<template v-if="prop == 'lockerName'">
					{{ row.orderType == 3 ? '上门订单' : row.lockerName }}
				</template>
				<template v-if="prop == 'shopDetailList'">
					<div class="body">
						<div class="order" v-for="(item, index) in row.shopDetailList" :key="index">
							<el-image style="width: 70px;" class="img" :src="item.goods_img_url" fit="contain" />
							<div class="content">
								<div class="info">
									<div class="title">{{ item.goods_name }}</div>
									<div class="num">*<span style="margin-left: 1px;">{{ item.goods_count }}</span></div>
								</div>
							</div>
						</div>
					</div>
				</template>
				<template v-if="prop == 'receiver'">
					<div class="body" style="text-align: left;">
						<p>用户名：{{ row.receiverName }}</p>
						<p>手机号：{{ row.receiverMobile }}</p>
						<p>地址：{{
							row.receiverProvince + row.receiverCity +
							row.receiverArea + row.receiverAddress }}</p>
					</div>
				</template>
				<template v-if="prop == 'status'">
					<el-tag v-if="row.status == 0">取消</el-tag>
					<el-tag v-else-if="row.status == 1" type="success">正常</el-tag>
				</template>
			</template>
		</CustomTable>
		<el-dialog title="物流轨迹" v-model="pathVisible" class="dialog_xs" :before-close="() => (pathVisible = false)">
			<div>
				<el-timeline>
					<el-timeline-item v-for="(item, index) in pathList" :key="index" :timestamp="item.action_time">
						{{ item.action_msg }}
					</el-timeline-item>
				</el-timeline>
			</div>
		</el-dialog>

		<el-dialog title="面单预览" v-model="printerVisible" class="dialog_md" :before-close="() => (printerVisible = false)">
			<el-scrollbar height="400px">
				<div v-html="print_html" id="printBox"></div>
			</el-scrollbar>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="printerVisible = false">取 消</el-button>
					<el-button type="primary" v-print="printObj">打印</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup lang="ts" name="logistics_order">
import CustomTable from "@/components/CustomTable/index.vue";
import { ref, reactive, onMounted, computed, toRefs } from 'vue';
import { listOrder, cancelOrder, getTrajectory, getOrder } from '@/api/logistics/order'
import { ElMessage, ElMessageBox } from 'element-plus';
import moment from 'moment';
import useAppStore from "@/store/modules/app";

const pinia = useAppStore();
const printObj = {
	id: "printBox",
	preview: false,
	popTitle: '',
	extraHead: '',
	previewBeforeOpenCallback() {
		console.log('正在加载预览窗口')
	},
	previewOpenCallback() {
		console.log('已经加载完预览窗口')
	},
	beforeOpenCallback() {
		console.log('打开之前')
	},
	openCallback() {
		console.log('执行了打印')
	},
	closeCallback() {
		console.log('关闭了打印工具')
	}
}
// 表格参数
const queryParams = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const data = reactive({
	agentIsCreate: 0,
	shopIsCreate: 0,
	pathVisible: false,
	printerVisible: false,
	print_html: "data:image/jpg;base64,"
})
const { agentIsCreate, shopIsCreate, pathVisible, printerVisible, print_html } = toRefs(data)

interface PathItem {
	action_time: string;
	action_msg: string;
	action_type?: string;
}
const pathList = ref<PathItem[]>([])

const getData = () => {
	// 可以根据需要添加初始化数据逻辑
}
//筛选参数
interface QueryParams {
	orderNo?: number,
	waybillId?: number,
	status?: number,
	shopId?: number,
	startTime?: string,
	endTime?: string,
	date?: Array<string>,
	siteName?: string,
	pageNum: number,
	pageSize: number
}
const query = reactive<QueryParams>({
	pageNum: pageNum.value,
	pageSize: pageSize.value,
});

// 获取表格数据
const getListData = () => {
	const queryParam = { ...query, pageNum: pageNum.value, pageSize: pageSize.value };
	listOrder(queryParam).then((res: any) => {
		tableData.value = res.data.records.map((item: any) => {
			item.shopDetailList = item.shopDetailList
			return item;
		})
		total.value = res.data.total;
	})
};
// 携参数查询
const handleSearch = (params: any) => {
	pageNum.value = 1;
	queryParams.value = params;
	// @ts-ignore
	for (const key in params) query[key] = params[key]
	getListData();
};

// 分页查询
const handleChange = (val: any) => {
	pageNum.value = val.page;
	pageSize.value = val.limit;
	getListData();
};

// 表格左侧按钮
const handleHeader = (key: string) => {
	// 可以根据需要添加按钮处理逻辑
};
// 详情
const rowData = ref<any>({})
const detailVisible = ref(false)
//取消订单
const handleCancel = (row: any) => {
	ElMessageBox.confirm('是否确认取消订单吗?', '提示', { type: 'warning' }).then(() => {
		cancelOrder({ id: row.id }).then((res: any) => {
			if (res.code == 200) {
				ElMessage.success("操作成功");
				getListData();
			}
		})
	}).catch(() => { })
}

//打印面单
const handlePrinter = (row: any) => {
	getOrder({ id: row.id }).then((res: any) => {
		print_html.value = decodeURIComponent(escape(window.atob(res.data.print_html)));
		printerVisible.value = true;
	})
}

//查看轨迹
const handleView = (row: any) => {
	getTrajectory({ id: row.id }).then((res: any) => {
		pathList.value = res.data.path_item_list.map((item: any) => {
			item.action_time = moment(item.action_time * 1000).format("YYYY-MM-DD HH:mm:ss");
			return item;
		});
		pathVisible.value = true;
	})
}

//打印面单
const onPrinter = () => {

}

// 操作菜单处理
const handleMenu = (val: any) => {
	const { index, row, value } = val;
	switch (value) {
		case "printer":
			handlePrinter(row);
			break;
		case "view":
			handleView(row);
			break;
		case "cancel":
			handleCancel(row);
			break;
	}
}

onMounted(() => {
	getListData();
	getData()
})
// 表格配置项
const option = reactive({
	showSearch: true,  // 显示隐藏搜索
	searchLabelWidth: 90,  // 搜索标题宽度
	/** 搜索字段配置项 */
	searchItem: [
		{ type: 'input', label: '订单号', prop: 'orderNo', placeholder: '请输入订单号' },
		{ type: 'input', label: '快递单号', prop: 'waybillId', placeholder: '请输入快递单号' },
		{
			type: 'select', label: '订单状态', prop: 'status', placeholder: '请选择订单状态',
			dicData: [
				{ label: '正常', value: 1 }, 
				{ label: '取消', value: 0 }
			]
		}
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
		indexFixed: false,
		indexWidth: 80
	},
	/** 行列合并配置项 */
	methods: () => { },
	/** 表格字段配置项 */
	tableItem: [
		{ type: 'text', label: '订单号', prop: 'orderNo', width: 200, fixed: false, sortable: false, isShow: true, isOmit: false },
		// { type: 'custom', label: '站点', prop: 'lockerName', width: 180, fixed: false, sortable: false, isShow: true, isOmit: false },
		{ type: 'text', label: '快递公司', prop: 'deliveryName', width: 200, fixed: false, sortable: false, isShow: true, isOmit: false },
		{ type: 'text', label: '快递单号', prop: 'waybillId', width: 300, fixed: false, sortable: false, isShow: true, isOmit: false },
		{ type: 'custom', label: '收货地址', prop: 'receiver', width: 300, fixed: false, sortable: false, isShow: true, isOmit: false },
		{ type: 'custom', label: '商品信息', prop: 'shopDetailList', width: 300, fixed: false, sortable: false, isShow: true, isOmit: false },
		{ type: 'custom', label: '订单状态', prop: 'status', width: 120, fixed: false, sortable: false, isShow: true, isOmit: false },
		{ type: 'text', label: '下单时间', prop: 'insertTime', width: 200, fixed: false, sortable: false, isShow: true, isOmit: false }
	],
	/** 操作菜单配置项 */
	menu: {
		isShow: true,
		width: 220,
		fixed: 'right',
	},
	menuItemBtn: [
		{ type: 'primary', isShow: true, icon: 'Printer', label: '打印面单', value: 'printer', hasPermi: [],
      judge: (row: any) => row.status == 1 },
		{ type: 'primary', isShow: true, icon: 'View', label: '查看轨迹', value: 'view', hasPermi: [],
      judge: (row: any) => row.status == 1 },
		{ type: 'danger', isShow: true, icon: 'Close', label: '取消订单', value: 'cancel', hasPermi: [], 
		  judge: (row: any) => row.status == 1 }
	],
	/** 更多菜单配置项（type: 'more'） 配置时生效 */
	moreItem: [],
	/** page 分页配置项 */
	isShowPage: true,
});
</script>
<style scoped lang="scss">
.body {
	display: flex;
	flex-direction: column;
	width: 100%;

	.order {
		background-color: white;
		margin: 0px;
		width: 100%;
		display: flex;
		align-items: center;
		margin-bottom: 5px;

		.img {
			width: 46px;
			height: 46px;
			margin: 0px;
			flex-grow: 0;
		}

		.content {
			width: 100%;
			padding: 0px;
			margin-left: 10px;
			overflow: hidden;
			flex-grow: 1;

			.info {
				width: 100%;
				margin: 0px;
				display: flex;
				align-items: center;
				justify-content: space-between;

				.title {
					width: 10px;
					white-space: nowrap;
					text-overflow: ellipsis;
					overflow: hidden;
					text-align: left;
					flex-grow: 1;
				}

				.num {
					width: 30px;
					text-align: right;
					flex-grow: 0;
				}

				.price {
					color: red;
				}
			}
		}
	}
}
</style>
<style>
@media print {
	* {
		-webkit-print-color-adjust: exact !important;
	}

	@page {
		size: auto;
		margin: 0mm auto;
	}
}
.inside-box .first{
	font-size: 28px !important;
	padding: 10px 40px !important;
}
.second .s-content{
	font-size: 28px !important;
}
.third .info-box,  .third .address, .sender-info{
	font-size: 30px !important;
}
.information{
	font-size: 24px !important;
}

.other-box .remark {
	position: relative;
}
.other-box .remark img{
	position: absolute;
	bottom: 20px;
	right: 20px;
	width: 140px !important;
	height: 140px !important;
}
</style>