<template>
	<el-card class="m5">
		<slot name='table-top'></slot>
		<div class="header">
			<!-- 顶部左侧按钮组 -->
			<div class="header_left">
				<!-- 按钮组（桌面版） -->
				<template v-if="appStore.device == 'desktop'">
					<el-button style="margin: 0px 5px 3px 0;" v-for="item in tableOption.headerBtn" :type="item.type"
						:disabled="item.disabled" v-show="item.isShow" :icon="item.icon" :key="item.key"
						@click="handleheader(item.key)" v-hasPermi="item.hasPermi">
						{{ item.text }}</el-button>
				</template>
				<!-- 按钮组（移动版） -->
				<el-dropdown v-else-if="tableOption.headerBtn.length > 0" @command="handleheader">
					<el-button plain>
						操作<el-icon class="el-icon--right"><arrow-down /></el-icon>
					</el-button>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item v-for="itm in tableOption.headerBtn" :key="itm.key" :command="itm.key">
								<el-button v-show="itm.isShow" :icon="itm.icon" plain link v-hasPermi="itm.hasPermi"
									:disabled="itm.disabled" :type="itm.type">
									{{ itm.text }}</el-button>
							</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
				<!-- 批量操作 -->
				<el-dropdown @command="operationChange" v-if="selectNum > 0 && tableOption.operation?.length > 0">
					<el-button style="margin-left: 5px;" plain>
						批量操作<el-icon class="el-icon--right"><arrow-down /></el-icon>
					</el-button>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item v-for="itm in tableOption.operation" :key="itm.key" :command="itm.key">
								<el-button :icon="itm.icon" link v-hasPermi="itm.hasPermi">
									{{ itm.text }}</el-button>
							</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
			</div>
			<!-- 右侧 toolbar 组 -->
			<div class="header_right">
				<right-toolbar v-model:showSearch="tableOption.toolbar.isShowToolbar" @queryTable="queryTable"
					:search="tableOption.toolbar.isShowSearch" @update:showSearch="showSearch"></right-toolbar>
			</div>
		</div>
		<!-- 多选跨页统计数 -->
		<div v-if="tableOption.openSelection" class="select_data">
			<el-icon class="select_icon">
				<InfoFilled />
			</el-icon>
			<div v-if="selectNum > 0"> 已选中 {{ selectNum }} 条记录(可跨页) |
				<el-button type="primary" link @click="clearData">清空</el-button>
			</div>
			<div v-else>未选中任何数据</div>
		</div>
		<!-- pc 端表格组件 -->
		<template v-if="appStore.device == 'desktop'">
			<el-table ref="tableRef" border @select-all="handleSelectionAllChange" @select="handleSelectionChange"
				:span-method="tableOption.methods" :data="props.data" :max-height="tableOption.maxHeight"
				@sort-change="sortChange" :header-cell-class-name="handleHeaderCellClass" @filter-change="columnFilterChange">
				<!-- 是否开启多选 -->
				<el-table-column v-if="tableOption.openSelection" type="selection" width="40" />
				<!-- 是否开启序号 -->
				<el-table-column v-if="tableOption.index.openIndex" :fixed="tableOption.index.indexFixed" label="序号"
					align="center" :width="tableOption.index.indexWidth">
					<template #default="scope">
						<span v-if="props.total > 0">
							{{ (props.page.pageNum - 1) * props.page.pageSize + scope.$index + 1 }}</span>
						<span v-else>{{ scope.$index + 1 }}</span>
					</template>
				</el-table-column>
				<template v-for="item, index in tableOption.tableItem" :key="index">
					<!-- tag 标签类型 -->
					<el-table-column v-if="item.type == 'tag' && item.isShow" :sortable="item.sortable"
						:label="item.label" align="center" :min-width="item.width" show-overflow-tooltip
						:fixed="item.fixed" :filters="item.noFilter? null : columnFilterData(item)" :column-key="item.prop" >
						<template #default="scope">
							<template v-for="itm, indx in item.dicData">
								<template v-if="itm.elTagType && itm.elTagType != 'default'">
									<el-tag :type="itm.elTagType == 'primary' ? '' : itm.elTagType" :color="item.color"
										:key="indx"
										v-if="scope.row[item.prop] == itm[item.customValue ? item.customValue : 'value']">
										{{ itm[item.customLabel ? item.customLabel : 'label'] }}
									</el-tag>
								</template>
								<template v-else>
									<el-tag :type="itm.type" :color="item.color" :key="indx"
										v-if="scope.row[item.prop] == itm[item.customValue ? item.customValue : 'value']">
										{{ itm[item.customLabel ? item.customLabel : 'label'] }}
									</el-tag>
								</template>
							</template>
						</template>
					</el-table-column>
					<!-- img 图片类型 -->
					<el-table-column v-else-if="item.type == 'img' && item.isShow" :label="item.label"
						:sortable="item.sortable" align="center" :min-width="item.width" :fixed="item.fixed">
						<template #default="scope">
							<template v-if="Array.isArray(scope.row[item.prop])">
								<el-image v-if="scope.row[item.prop].length > 0 && scope.row[item.prop][0] != ''"
									:preview-src-list="scope.row[item.prop]" preview-teleported :fit="item.fit"
									:style="{ width: `${item.imgWidth}px` }" :src="scope.row[item.prop][0]"></el-image>
							</template>
							<el-image v-else :preview-src-list="[scope.row[item.prop]]" preview-teleported
								:fit="item.fit" :style="{ width: `${item.imgWidth}px` }"
								:src="scope.row[item.prop]"></el-image>
						</template>
					</el-table-column>
					<!-- double 二级表头类型 -->
					<el-table-column v-else-if="item.type == 'double' && item.isShow" :label="item.label"
						:sortable="item.sortable" align="center" :min-width="item.width" :fixed="item.fixed">
						<template #default>
							<el-table-column v-for="itm, indx in item.children" :label="itm.label"
								:sortable="itm.sortable" align="center" :min-width="itm.width" show-overflow-tooltip
								:key="indx">
								<template #default="scope">
									<span :style="{ color: itm.color }"> {{ scope.row[itm.prop] }} </span>
								</template>
							</el-table-column>
						</template>
					</el-table-column>
					<!-- custom 自定义类型 -->
					<el-table-column v-else-if="item.type == 'custom' && item.isShow" :label="item.label" align="center"
						:min-width="item.width" :fixed="item.fixed" :sortable="item.sortable">
						<template #default="scope">
							<slot name='custom' :row="scope.row" :prop="item.prop" :index="scope.$index"></slot>
						</template>
					</el-table-column>
					<!-- text 文本类型 -->
					<el-table-column v-else-if="item.type == 'text' && item.isShow" align="center"
						:sortable="item.sortable" :fixed="item.fixed" :label="item.label" :min-width="item.width"
						show-overflow-tooltip :filters="item.noFilter? null :columnFilterTextData(item)" :column-key="item.prop">
						<template #default="scope">
							<span :style="{ color: item.color }">{{ scope.row[item.prop] }}</span>
						</template>
					</el-table-column>
					<!-- 二维码类型 -->
					<el-table-column v-else-if="item.type == 'QRCode' && item.isShow" align="center"
						:sortable="item.sortable" :fixed="item.fixed" :label="item.label" :min-width="item.width">
						<template #default="scope">
							<el-button type="primary" link @click="showCode(scope.row[item.prop])"
								icon="View">查看</el-button>
							<el-tooltip effect="dark" :content="scope.row[item.prop]" placement="top">
								<el-button type="primary" link @click="userStore.copyText(scope.row[item.prop])"
									icon="Link">复制</el-button>
							</el-tooltip>
						</template>
					</el-table-column>
					<!-- 链接类型 -->
					<el-table-column v-else-if="item.type == 'link' && item.isShow" align="center"
						:sortable="item.sortable" :fixed="item.fixed" :label="item.label" :min-width="item.width">
						<template #default="scope">
							<el-tooltip v-if="scope.row[item.prop]" effect="dark" :content="scope.row[item.prop]"
								placement="top">
								<el-button type="primary" link @click="userStore.copyText(scope.row[item.prop])"
									icon="Link">复制</el-button>
							</el-tooltip>
						</template>
					</el-table-column>
				</template>
				<!-- 操作栏 -->
				<el-table-column v-if="tableOption.menu.isShow" label="操作" align="center"
					:width="tableOption.menu.width" :fixed="tableOption.menu.fixed ? tableOption.menu.fixed : 'right'">
					<template #default="scope">
						<slot name='menu-top' :row="scope.row"></slot>
						<template v-for="item, index in tableOption.menuItemBtn" :key="index">
							<el-dropdown v-if="item.type == 'more' && customJudge(item.judge || false, scope.row)"
								teleported class="dropdown" v-show="item.isShow"
								@command="(val) => handleMenuBtn({ row: scope.row, value: val, index: scope.$index })">
								<span class="el-dropdown-link">
									更多
									<el-icon style="position: relative;top: 2px;">
										<arrow-down />
									</el-icon>
								</span>
								<template #dropdown>
									<el-dropdown-menu>
										<template v-for="itm, indx in tableOption.moreItem">
											<el-dropdown-item :command="itm.value" :key="indx"
												v-if="customJudge(itm.judge || false, scope.row) && itm.isShow">
												{{ itm.label }}
											</el-dropdown-item>
										</template>
										<slot name='menu-dropdown' :row="scope.row"></slot>
									</el-dropdown-menu>
								</template>
							</el-dropdown>
							<!-- 其他按钮 -->
							<el-button v-else-if="customJudge(item.judge || false, scope.row)" link :type="item.type"
								v-show="item.isShow" :icon="item.icon" v-hasPermi="item.hasPermi"
								@click.prevent="handleMenuBtn({ row: scope.row, value: item.value, index: scope.$index })">
								{{ item.label }}
							</el-button>
						</template>
						<slot name='menu' :row="scope.row"></slot>
					</template>
				</el-table-column>
			</el-table>
		</template>
		<!-- 移动端表格组件 -->
		<template v-else>
			<!-- 空数据组件 -->
			<el-empty v-if="props.data.length <= 0" description="没有数据了~" />
			<!-- 有数据组件 -->
			<div v-else v-for="itemData, indexData in props.data" class="mobile_data_class" :key="indexData">
				<el-descriptions style="width: 100%;"
					:direction="appStore.screenWidth < 360 ? 'vertical' : 'horizontal'" :column="1" border>
					<!-- 选择 -->
					<el-descriptions-item label-class-name="mobile_desLabel" v-if="tableOption.openSelection"
						:label="`选择${appStore.screenWidth > 500 ? '：' : ''}`">
						<el-checkbox v-model="mobileSelect[`id${indexData}`]" size="large"
							@change="() => handleSelectionChange(null, itemData)" />
					</el-descriptions-item>
					<!-- 序号 -->
					<el-descriptions-item label-class-name="mobile_desLabel" v-if="tableOption.index.openIndex"
						:label="`序号${appStore.screenWidth > 500 ? '：' : ''}`">
						<span v-if="props.total > 0">
							{{ (props.page.pageNum - 1) * props.page.pageSize + indexData + 1 }}</span>
						<span v-else>{{ indexData + 1 }}</span>
					</el-descriptions-item>
					<template v-for="item, index in tableOption.tableItem" :key="index">
						<!-- tag 标签类型 -->
						<el-descriptions-item label-class-name="mobile_desLabel"
							v-if="item.type == 'tag' && item.isShow"
							v-show="mobileItemOpen[`open${indexData}`] || !item.isOmit"
							:label="item.label + (appStore.screenWidth > 500 ? '：' : '')">
							<template v-for="itm, indx in item.dicData" :key="indx">
								<template v-if="itm.elTagType && itm.elTagType != 'default'">
									<el-tag :type="itm.elTagType == 'primary' ? '' : itm.elTagType" :color="item.color"
										v-if="itemData[item.prop] == itm[item.customValue ? item.customValue : 'value']">
										{{ itm[item.customLabel ? item.customLabel : 'label'] }}
									</el-tag>
								</template>
								<template v-else>
									<el-tag :type="itm.type" :color="item.color"
										v-if="itemData[item.prop] == itm[item.customValue ? item.customValue : 'value']">
										{{ itm[item.customLabel ? item.customLabel : 'label'] }}
									</el-tag>
								</template>
							</template>
						</el-descriptions-item>
						<!-- img 图片类型 -->
						<el-descriptions-item label-class-name="mobile_desLabel"
							v-else-if="item.type == 'img' && item.isShow"
							v-show="mobileItemOpen[`open${indexData}`] || !item.isOmit"
							:label="item.label + (appStore.screenWidth > 500 ? '：' : '')">
							<template v-if="Array.isArray(itemData[item.prop])">
								<el-image v-if="itemData[item.prop].length > 0 && itemData[item.prop][0] != ''"
									:preview-src-list="itemData[item.prop]" preview-teleported :fit="item.fit"
									:style="{ width: `${item.imgWidth}px` }" :src="itemData[item.prop][0]"></el-image>
							</template>
							<el-image v-else :preview-src-list="[itemData[item.prop]]" preview-teleported
								:fit="item.fit" :style="{ width: `${item.imgWidth}px` }"
								:src="itemData[item.prop]"></el-image>
						</el-descriptions-item>
						<!-- double 二级表头类型 -->
						<template v-else-if="item.type == 'double' && item.isShow">
							<el-descriptions-item label-class-name="mobile_desLabel" v-for="itm, indx in item.children"
								:label="item.label + (appStore.screenWidth > 500 ? '：' : '')"
								v-show="mobileItemOpen[`open${indexData}`] || !itm.isOmit" :key="indx">
								<span :style="{ color: itm.color }"> {{ itemData[itm.prop] }} </span>
							</el-descriptions-item>
						</template>
						<!-- custom 自定义类型 -->
						<el-descriptions-item label-class-name="mobile_desLabel"
							v-else-if="item.type == 'custom' && item.isShow"
							:label="item.label + (appStore.screenWidth > 500 ? '：' : '')"
							v-show="mobileItemOpen[`open${indexData}`] || !item.isOmit">
							<slot name='custom' :row="itemData" :prop="item.prop" :index="index"></slot>
						</el-descriptions-item>
						<!-- text 文本类型 -->
						<el-descriptions-item label-class-name="mobile_desLabel"
							v-else-if="item.type == 'text' && item.isShow"
							:label="item.label + (appStore.screenWidth > 500 ? '：' : '')"
							v-show="mobileItemOpen[`open${indexData}`] || !item.isOmit">
							<span :style="{ color: item.color }">{{ itemData[item.prop] }}</span>
						</el-descriptions-item>
						<!-- 二维码类型 -->
						<el-descriptions-item label-class-name="mobile_desLabel"
							v-else-if="item.type == 'QRCode' && item.isShow"
							:label="item.label + (appStore.screenWidth > 500 ? '：' : '')"
							v-show="mobileItemOpen[`open${indexData}`] || !item.isOmit">
							<el-button type="primary" link @click="showCode(itemData[item.prop])"
								icon="View">查看</el-button>
							<el-tooltip effect="dark" :content="itemData[item.prop]" placement="top">
								<el-button type="primary" link @click="userStore.copyText(itemData[item.prop])"
									icon="Link">复制</el-button>
							</el-tooltip>
						</el-descriptions-item>
						<!-- 链接类型 -->
						<el-descriptions-item label-class-name="mobile_desLabel"
							v-else-if="item.type == 'link' && item.isShow"
							:label="item.label + (appStore.screenWidth > 500 ? '：' : '')"
							v-show="mobileItemOpen[`open${indexData}`] || !item.isOmit">
							<el-tooltip v-if="itemData[item.prop]" effect="dark" :content="itemData[item.prop]"
								placement="top">
								<el-button type="primary" link @click="userStore.copyText(itemData[item.prop])"
									icon="Link">复制</el-button>
							</el-tooltip>
						</el-descriptions-item>
					</template>
				</el-descriptions>
				<!-- 省略展开关闭组件 -->
				<div class="open_class"
					@click="mobileItemOpen[`open${indexData}`] = !mobileItemOpen[`open${indexData}`]">
					<el-icon>
						<CaretTop v-if="mobileItemOpen[`open${indexData}`]" />
						<CaretBottom v-else />
					</el-icon>
				</div>
				<!-- 底部菜单 -->
				<div v-if="tableOption.menu.isShow" class="mobile_button_class">
					<slot name='menu-top' :row="itemData"></slot>
					<template v-for="item, index in tableOption.menuItemBtn" :key="index">
						<el-dropdown v-if="item.type == 'more' && customJudge(item.judge || false, itemData)" teleported
							v-show="item.isShow"
							@command="(val) => handleMenuBtn({ row: itemData, value: val, index })">
							<span class="el-dropdown-link">
								更多
								<el-icon style="position: relative;top: 2px;">
									<arrow-down />
								</el-icon>
							</span>
							<template #dropdown>
								<el-dropdown-menu>
									<template v-for="itm, indx in tableOption.moreItem" :key="indx">
										<el-dropdown-item :command="itm.value"
											v-if="customJudge(itm.judge || false, itemData) && itm.isShow">
											{{ itm.label }}
										</el-dropdown-item>
									</template>
									<slot name='menu-dropdown' :row="itemData"></slot>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
						<!-- 其他按钮 -->
						<el-button v-else-if="customJudge(item.judge || false, itemData)" link :type="item.type"
							v-show="item.isShow" :icon="item.icon" v-hasPermi="item.hasPermi"
							@click.prevent="handleMenuBtn({ row: itemData, value: item.value, index })">
							{{ item.label }}
						</el-button>
					</template>
					<slot name='menu' :row="itemData"></slot>
				</div>
			</div>
		</template>
		<!-- 分页组件 -->
		<div v-if="tableOption.isShowPage && props.total > 0">
			<!-- pc版 -->
			<Pagination v-if="appStore.screenWidth > 520" :total="props.total || 0" v-model:page="props.page.pageNum"
				v-model:limit="props.page.pageSize" :pageSizes="props.page.pageSizes"
				:pagerCount="props.page.pagerCount" :small="props.page.small" :background="props.page.background"
				:layout="appStore.device == 'desktop' ? props.page.layout : 'total, prev, pager, next'"
				@pagination="handleCurrentChange" />
			<!-- 移动版 -->
			<van-pagination v-else v-model="props.page.pageNum" :items-per-page="props.page.pageSize"
				:total-items="props.total || 0" mode="simple" @change="handleCurrentChange" />
		</div>
		<!-- 二维码查看组件 -->
		<el-dialog v-model="dialogVisible" title="二维码详情" class="dialog_xs" :before-close="() => dialogVisible = false">
			<div style="width: 100%;text-align: center;">
				<p>链接地址：<el-link type="primary" @click="userStore.copyText(QRCode)">{{ QRCode }}</el-link></p>
				<qrcode-vue v-if="dialogVisible" :value="QRCode"
					:size="appStore.screenWidth > 600 ? 500 : (appStore.screenWidth - 100)"></qrcode-vue>
			</div>
			<template #footer>
				<div class="dialog-footer">
					<el-button type="primary" @click="dialogVisible = false">确认</el-button>
				</div>
			</template>
		</el-dialog>
	</el-card>
</template>
<script setup>
import { nextTick, reactive, ref, watch } from "vue";
import useAppStore from '@/store/modules/app';
import useUserStore from '@/store/modules/user';
import { Pagination as VanPagination } from 'vant';
import QrcodeVue from 'qrcode.vue';
import 'vant/lib/index.css';
const appStore = useAppStore();
const userStore = useUserStore();
const props = defineProps({
	option: { type: Object, default: {} },
	data: { type: Array, default: [] },
	total: { type: Number, default: 0 },
	page: { type: Object, default: {} },
});
const emits = defineEmits([
	'headerchange', 'bodychange', 'refresh', 'isShowSearch', 'sortChange', 'selectChange', 'selectAllChange', 'menuChange', 'currentChange', 'allData',
	'operationChange', 'selectData','columnFilterChange'
])
const tableOption = reactive(props.option);
const tableRef = ref();
const selectNum = ref(0);
const selectData = ref({});
const mobileSelect = ref({});
const mobileItemOpen = ref({});
// 二维码显示
const dialogVisible = ref(false)
const QRCode = ref();
const showCode = (code) => {
	QRCode.value = code;
	dialogVisible.value = true
}

const columnFilterData = (item) => {
 return item.dicData.map(i=> {return {text:i.label,value:i.value}})
}

const columnFilterTextData = (item) => {
  const uniqueValues = [...new Set(props.data.map(i => i[item.prop]))];
  return uniqueValues
      .filter(value => value !== null && value !== undefined) // 过滤空值
      .map(value => ({ text: value, value: value }));
}

const columnFilterChange = (filters) =>{
  emits("columnFilterChange", filters)
}


/**
 * 自定义校验规则
 */
const customJudge = (judge, row) => { return judge ? judge(row) : true }
/**
 * 清空多选内容
 */
const clearData = () => {
	selectNum.value = 0
	selectData.value = {}
	nextTick(() => tableRef.value?.clearSelection());
	emits('selectData', []);
}
/**
 * 表格左侧顶部按钮事件
 */
const handleheader = (key) => emits('headerchange', key);
/**
 * 表格刷新
 */
const queryTable = () => emits('refresh');
/**
 * 搜索项显示隐藏配置
 */
const showSearch = (val) => emits('isShowSearch', val);
/**
 * 全选功能
 */
const handleSelectionAllChange = (val) => {
	const Ids = val.map((item) => { return item.id })
	selectData.value[`page${props.page.pageNum}`] = Ids
	selectNum.value = Object.values(selectData.value).reduce((acc, subArray) => acc + subArray.length, 0);
	emits('selectAllChange', selectData.value[`page${props.page.pageNum}`]);
	emits('selectData', Object.values(selectData.value).reduce((acc, arr) => acc.concat(arr), []));
};
/**
 * 单选功能
 */
const handleSelectionChange = (e, row) => {
	const Ids = selectData.value[`page${props.page.pageNum}`] || [];
	const index = Ids.indexOf(row.id);
	if (Ids && Array.isArray(Ids)) index > -1 ? Ids.splice(index, 1) : Ids.push(row.id);
	selectData.value[`page${props.page.pageNum}`] = Ids;
	selectNum.value = Object.values(selectData.value).reduce((acc, subArray) => acc + subArray.length, 0);
	emits('selectChange', row);
	emits('selectData', Object.values(selectData.value).reduce((acc, arr) => acc.concat(arr), []));
}

const handleHeaderCellClass = ({ row, column, rowIndex, columnIndex })=> {
  let label = column.label;
  let prop = tableOption.tableItem.filter(item => item.label === label)[0]?.prop;
  let hasAsc =  ascFields.value.has(prop)
  if(hasAsc){
      column.order = 'ascending'
  }
  let hasDesc = descFields.value.has(prop)
  if(hasDesc){
      column.order = 'descending'
  }
  return label;
}

const ascFields = ref(props.option?.ascFields||new Set())
const descFields = ref(props.option?.descFields||new Set())

const orderBy = (prop, order) => {
  ascFields.value.delete(prop)
  descFields.value.delete(prop)
  if (order) {
    let asc = order === 'ascending'
    if (asc) {
      ascFields.value.add(prop)
    } else {
      descFields.value.add(prop)
    }
  }
}


/**
 * 排序功能
 */
const sortChange = (val) => emits('sortChange', val);
/**
 * 操作按钮选中
 */
const handleMenuBtn = (val) => emits('menuChange', val);
/**
 * 页码更新
 */
let limit = 10;
const handleCurrentChange = (val) => {
	let params = {};
	if (!val.page) {
		params.page = val;
		params.limit = limit;
	} else {
		params = val;
		limit = val.limit;
	}
	emits('currentChange', params);
};
/**
* 批量操作
*/
const operationChange = (val) => emits('operationChange', val);
/**
 * 监听表格数据
 */
watch(() => props.data, (news) => {
	const pageData = selectData.value[`page${props.page.pageNum}`];
	if (pageData && pageData.length > 0) {
		const indexList = news.reduce((acc, item, index) => {
			mobileSelect.value[`id${index}`] = false;
			mobileItemOpen.value[`open${index}`] = false;
			const indexs = pageData.findIndex(items => items === item.id);
			if (indexs !== -1) acc.push(index);
			return acc;
		}, []);
		nextTick(() => {
			for (const item of indexList) {
				tableRef.value.toggleRowSelection(news[item], true);
				selectData.value[`id${item}`] = true;
			}
		})
	}
}, { deep: true, immediate: true })
defineExpose({ clearData,orderBy });
</script>
<style lang="scss" scoped>
.header {
	width: 100%;
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}

.header_left,
.header_right {
	margin-bottom: 10px;
}

.example-showcase .el-dropdown-link {
	cursor: pointer;
	color: var(--el-color-primary);
	display: flex;
	align-items: center;
}

.dropdown {
	margin-top: 3px;
	font-size: 14px;
}

.select_data {
	height: 40px;
	background-color: rgb(230, 247, 255);
	border: 1px solid rgb(145, 213, 255);
	padding: 10px 15px;
	font-size: 12px;
	margin-bottom: 6px;
	display: flex;
	align-items: center;

	.select_icon {
		margin-right: 5px;
	}
}

:deep(.mobile_desLabel) {
	width: 30%;
}

.mobile_data_class {
	width: 100%;
	border: 1px solid #DCDFE6;
	box-sizing: border-box;
	border-radius: 3px;
	margin-bottom: 20px;
}

.open_class {
	text-align: center;
	margin: 5px 0;
	border-bottom: 1px solid #DCDFE6;
	font-size: 20px;
}

.mobile_button_class {
	box-sizing: border-box;
	margin: 10px 5px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
</style>