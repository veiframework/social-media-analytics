<template>
	<el-card v-show="props.showSearch" class="m5">
		<el-form :model="queryParams" :label-width="props.searchLabelWidth">
			<el-row>
				<template v-for="item, index in props.searchItem">
					<el-col v-if="customJudge(item.judge, queryParams)" v-show="!(isShow && item.isOmit)" :key="index"
						:xs="24"
						:sm="item.type == 'datetimerange' || item.type == 'daterange' ? 24 : 12 * (item.span ? item.span : 1)"
						:md="item.type == 'datetimerange' || item.type == 'daterange' ? 16 : 8 * (item.span ? item.span : 1)"
						:lg="item.type == 'datetimerange' || item.type == 'daterange' ? 12 : 6 * (item.span ? item.span : 1)"
						:xl="item.type == 'datetimerange' || item.type == 'daterange' ? 8 : 4 * (item.span ? item.span : 1)">
						<!-- 输入框 -->
						<el-form-item v-if="item.type == 'input'" :label="item.label + '：'">
							<el-input v-model.trim="queryParams[item.prop]" :placeholder="'请输入' + item.label"
								:maxlength="item.max" clearable
								@input="val => queryParams[item.prop] = val.replace(item.verify, '')" />
						</el-form-item>
						<!-- 下拉框 -->
						<el-form-item v-else-if="item.type == 'select'" :label="item.label + '：'">
							<el-select class="w100" v-model="queryParams[item.prop]" :placeholder="'请选择' + item.label"
								:filterable="item.filterable" clearable :multiple="item.multiple"
								:multiple-limit="item.limit">
								<el-option v-for="itm in item.dicData"
									:key="itm[item.customValue ? item.customValue : 'value']"
									:label="itm[item.customLabel ? item.customLabel : 'label']"
									:value="itm[item.customValue ? item.customValue : 'value']" />
							</el-select>
						</el-form-item>
						<!-- 下拉框 - 远程 -->
						<el-form-item v-else-if="item.type == 'select_remote'" :label="item.label + '：'">
							<el-select ref="seleRemotetRef" class="w100" v-model="queryParams[item.prop]" filterable
								remote reserve-keyword :placeholder="'请选择' + item.label" remote-show-suffix
								:loading="loading" clearable :multiple="item.multiple" :multiple-limit="item.limit"
								:remote-method="(query) => remoteMethod(query, item.request, item.key, item.itemNum)">
								<el-option v-for="itm in options"
									:key="itm[item.customValue ? item.customValue : 'value']"
									:label="itm[item.customLabel ? item.customLabel : 'label']"
									:value="itm[item.customValue ? item.customValue : 'value']" />
							</el-select>
						</el-form-item>
						<!-- 日期 -->
						<el-form-item v-else-if="item.type == 'date'" :label="item.label + '：'">
							<el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
								:default-time="item.defaultTime ? item.defaultTime : new Date(2000, 2, 1, 23, 59, 59)"
								v-model="queryParams[item.prop]" :type="item.category" placeholder="请选择日期"
								:disabled-date="item.disabledDate" />
						</el-form-item>
						<!-- 日期时间 -->
						<el-form-item v-else-if="item.type == 'datetime'" :label="item.label + '：'">
							<el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
								:default-time="item.defaultTime ? item.defaultTime : new Date(2000, 2, 1, 0, 0, 0)"
								v-model="queryParams[item.prop]" placeholder="请选择时间" :type="item.category"
								:disabled-date="item.disabledDate" />
						</el-form-item>
						<!-- 范围日期 -->
						<template v-else-if="item.type == 'daterange' && item.category != 'weekrange'">
							<el-form-item v-if="appStore.device == 'desktop'" :label="item.label + '：'">
								<el-date-picker class="w100" start-placeholder="开始日期" end-placeholder="结束日期"
									:format="item.format" :value-format="item.valueFormat"
									:default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 2, 1, 23, 59, 59)]"
									v-model="queryParams[item.prop]" @change="(val) => handleDate(val, item.prop)"
									:type="item.category" :disabled-date="item.disabledDate" />
							</el-form-item>
							<template v-else>
								<el-form-item :label="item.label + '：'">
									<el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
										v-model="queryParams[`start${item.prop}`]" placeholder="请选择开始日期"
										:type="item.category.replace('range', '')" :disabled-date="item.disabledDate"
										@change="() => queryParams[`end${item.prop}`] = undefined" />
								</el-form-item>
								<el-form-item :label="item.label + '：'">
									<el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
										v-model="queryParams[`end${item.prop}`]" placeholder="请选择结束日期"
										:type="item.category.replace('range', '')"
										:default-time="new Date(2000, 2, 1, 23, 59, 59)"
										:disabled="queryParams[`start${item.prop}`] ? false : true"
										:disabled-date="(time) => disabledDate(time, queryParams[`start${item.prop}`], item.disabledDate)" />
								</el-form-item>
							</template>
						</template>
						<!-- 范围周期 -->
						<template v-else-if="item.type == 'daterange' && item.category == 'weekrange'">
							<el-form-item v-if="appStore.device == 'desktop'" :label="item.label + '：'">
								<div style="display: flex;width: 100%;">
									<el-date-picker class="w100" :format="item.format"
										v-model="queryParams[`start${item.prop}`]" placeholder="请选择开始周期" type="week"
										@change="() => queryParams[`end${item.prop}`] = undefined"
										:disabled-date="item.disabledDate" />
									<span style="margin: 0 10px;"> - </span>
									<el-date-picker class="w100" :format="item.format"
										v-model="queryParams[`end${item.prop}`]" placeholder="请选择结束周期" type="week"
										:disabled="queryParams[`start${item.prop}`] ? false : true"
										:disabled-date="(time) => disabledDate(time, queryParams[`start${item.prop}`], item.disabledDate)" />
								</div>
							</el-form-item>
							<template v-else>
								<el-form-item :label="item.label + '：'">
									<el-date-picker class="w100" placeholder="请选择开始周期" :format="item.format"
										v-model="queryParams[`start${item.prop}`]" type="week"
										:disabled-date="item.disabledDate"
										@change="() => queryParams[`end${item.prop}`] = undefined" />
								</el-form-item>
								<el-form-item :label="item.label + '：'">
									<el-date-picker class="w100" :format="item.format"
										v-model="queryParams[`end${item.prop}`]" placeholder="请选择结束周期" type="week"
										:disabled="queryParams[`start${item.prop}`] ? false : true"
										:disabled-date="(time) => disabledDate(time, queryParams[`start${item.prop}`], item.disabledDate)" />
								</el-form-item>
							</template>
						</template>
						<!-- 范围日期时间 -->
						<template v-else-if="item.type == 'datetimerange'">
							<el-form-item v-if="appStore.device == 'desktop'" :label="item.label + '：'">
								<el-date-picker :shortcuts="item.shortcuts" class="w100" start-placeholder="开始时间" end-placeholder="结束时间"
									:format="item.format" :value-format="item.valueFormat"
									:default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 2, 1, 23, 59, 59)]"
									v-model="queryParams[item.prop]" @change="(val) => handleDate(val, item.prop)"
									:type="item.category" :disabled-date="item.disabledDate" />
							</el-form-item>
							<template v-else>
								<el-form-item :label="item.label + '：'">
									<el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
										v-model="queryParams[`start${item.prop}`]" placeholder="请选择开始时间"
										:type="item.category.replace('range', '')" :disabled-date="item.disabledDate"
										@change="() => queryParams[`end${item.prop}`] = undefined" />
								</el-form-item>
								<el-form-item :label="item.label + '：'">
									<el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
										v-model="queryParams[`end${item.prop}`]" placeholder="请选择结束时间"
										:type="item.category.replace('range', '')"
										:default-time="new Date(2000, 2, 1, 23, 59, 59)"
										:disabled="queryParams[`start${item.prop}`] ? false : true"
										:disabled-date="(time) => disabledDate(time, queryParams[`start${item.prop}`], item.disabledDate)" />
								</el-form-item>
							</template>
						</template>
						<!-- 自定义 -->
						<el-form-item v-else-if="item.type == 'custom'" :label="item.label + '：'" :prop="item.prop">
							<slot name='custom-item' :prop="item.prop" :queryParams="queryParams"></slot>
						</el-form-item>
					</el-col>
				</template>
				<el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
					<el-form-item label-width="10">
						<el-button type="primary" icon="search" @click="searchChange">搜索</el-button>
						<el-button icon="refresh" @click="resetChange">重置</el-button>
						<slot name='custom-button'></slot>
						<el-button @click="isShow = !isShow" :icon="isShow ? 'ArrowDown' : 'ArrowUp'" circle />
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</el-card>
</template>
<script setup>
import { onActivated, onMounted, ref, watch } from "vue";
import useAppStore from '@/store/modules/app';
import moment from "moment";
const appStore = useAppStore();
const props = defineProps({
	showSearch: { type: Boolean, default: true },
	searchLabelWidth: { type: Number, default: 80 },
	searchItem: {
		type: Array,
		default: []
	}
});
/**
 * 回传事件
 */
const emits = defineEmits(['search'])
// 远程检索控件参数
const MultipleProp = ref([]);
const loading = ref(false);
const options = ref([]);
const seleRemotetRef = ref();
const remoteMethod = (query, request, key, itemNum) => {
	if (query) {
		loading.value = true
		const queryData = { pageNum: 1, pageSize: itemNum ? itemNum : 10 };
		queryData[key] = query;
		request(queryData).then(res => {
			options.value = res.data.records;
			loading.value = false;
		})
	} else {
		options.value = []
	}
}
// 是否省略配置项中支持省略的搜索条件
const isShow = ref(false);
// 查询参数保存
const queryParams = ref({});

const ascFields = ref(new Set())
const descFields = ref(new Set())

// 日期范围选择时默认将原始数组分至两个参数
const handleDate = (val, prop) => {
	if (val && Array.isArray(val) && val.length > 0) {
		queryParams.value[`start${prop}`] = val[0]
		queryParams.value[`end${prop}`] = val[1]
	} else {
		queryParams.value[`start${prop}`] = undefined;
		queryParams.value[`end${prop}`] = undefined;
	}
}
// 查询事件
const searchChange = () => {
	let paramsData = { ...queryParams.value };
  paramsData.descFields = [...descFields.value].join(",")
  paramsData.ascFields = [...ascFields.value].join(",")
	for (const item of MultipleProp.value) { if (paramsData[item]?.length > 0) paramsData[item] = paramsData[item].join(',') };
	for (const key in paramsData) { if (Array.isArray(paramsData[key])) delete paramsData[key] };
	for (const item of props.searchItem) {
		switch (item.type) {
			case "datetimerange":
			case "daterange":
				delete paramsData[item.prop];
				if (item.category == 'weekrange' && paramsData[`start${item.prop}`] && paramsData[`end${item.prop}`]) {
					const startWeek = moment(paramsData[`start${item.prop}`]);
					const endWeek = moment(paramsData[`end${item.prop}`]);
					paramsData[`start${item.prop}`] = startWeek.isoWeekYear() + "-" + startWeek.isoWeek();
					paramsData[`end${item.prop}`] = endWeek.isoWeekYear() + "-" + endWeek.isoWeek();
				}
				break;
		}
	}
	emits('search', paramsData);
}

const orderBy = (prop, order)=>{
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
  searchChange()
}

// 重置事件
const resetChange = () => {
	queryParams.value = {}
	changeParams();
	searchChange();
}
// 更新默认参数
const changeParams = () => {
	MultipleProp.value = [];
	for (const item of props.searchItem) {
		switch (item.type) {
			case "datetimerange":
			case "daterange":
				if (item.default && Array.isArray(item.default) && item.default?.length > 0) {
					if (item.category == 'weekrange') {
						queryParams.value[`start${item.prop}`] = new Date(item.default[0]);
						queryParams.value[`end${item.prop}`] = new Date(item.default[1]);
					} else {
						queryParams.value[item.prop] = item.default;
						queryParams.value[`start${item.prop}`] = item.default[0];
						queryParams.value[`end${item.prop}`] = item.default[1];
					}
				}
				break;
			case 'select':
			case 'select_remote':
				if (item.multiple) MultipleProp.value.push(item.prop);
				if (item.default == null) break;
				queryParams.value[item.prop] = item.default;
				if (!item.defaultText) break;
				if (item.type == 'select_remote') setTimeout(() => seleRemotetRef.value[0].selectedLabel = item.defaultText, 0);
				break;
			default:
				if (!item.default) break;
				queryParams.value[item.prop] = item.default;
				break;
		}
	}
}
/**
 * 自定义校验规则
 */
const customJudge = (judge, from) => { return judge ? judge(from) : true }
/**
 * 禁止选择范围
 */
const disabledDate = (time, now, disabledFun) => {
	if (now) {
		const nowTime = moment(now).valueOf();
		return (time.getTime() + 24 * 60 * 60 * 1000) <= nowTime || (disabledFun ? disabledFun(time) : false);
	}
	return false;
}
/**
 * 监听设备
 */
watch(() => appStore.device, (news) => {
	isShow.value = news == 'desktop' ? false : true;
}, { deep: true, immediate: true })
onActivated(() => {
	changeParams();
	searchChange();
})
defineExpose({ orderBy });
</script>
<style lang="scss" scoped>
:deep(.el-card__body) {
	padding-bottom: 0px !important;
}
</style>