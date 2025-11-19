<template>
	<div class="box">
		<div class="echart" ref="chartDom" :style="{ 'height': props.height + 'px' }"></div>
		<div class="days" v-if="!hideSearch">
			<el-date-picker style="width: 120px;" v-model="month" type="month" placeholder="请选择" @change="monthChange"
				value-format="YYYY-MM" />
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, onBeforeMount, watch } from "vue";
import * as Echarts from "echarts";
import * as lodash from "lodash";
import useAppStore from "@/store/modules/app";
import moment from "moment";
const appStore = useAppStore();
const props = defineProps({
	option: {
		type: Object,
		default: {}
	},
	height: {
		type: Number,
		default: 300
	},
	hideSearch: {
		type: Boolean,
		default: false,
	}
});
const emit = defineEmits(['monthChange']);
const month = ref(moment().format("YYYY-MM"));
const monthChange = () => {
	emit('monthChange', month.value);
}
// 获取节点
const chartDom = ref();
let myCharts = null;
// 页面变化时自适应图形页面
const echartsChange = () => myCharts.resize();
// 节流
const cancalDebounce = lodash.debounce(echartsChange, 100, { leading: false, trailing: true })
// 初始化图
const initEcharts = () => {
	myCharts = Echarts.init(chartDom.value, { renderer: 'svg' });
	myCharts.showLoading();
}
// 监听 sidbar 变化
watch(() => appStore.sidebar.opened, () => {
	setTimeout(() => { cancalDebounce() }, 300);
}, { deep: true })
// 监听数据变化
watch(() => props.option, (news) => {
	if (news && news.series && news.series[0].data && news.series[0].data.length > 0) {
		myCharts.hideLoading();
		myCharts.setOption(props.option);
	}
}, { deep: true, immediate: true })
onMounted(() => {
	initEcharts();
})
onBeforeMount(() => {
	window.addEventListener('resize', cancalDebounce, { passive: true });
})
onBeforeUnmount(() => {
	window.removeEventListener('resize', cancalDebounce)
	myCharts.dispose()
})
</script>

<style scoped lang="scss">
.box {
	position: relative;

	.echart {
		width: 100%;
		margin: 0 auto;
		height: 100%;
	}

	.days {
		position: absolute;
		right: 5px;
		top: 0;
	}
}
</style>
