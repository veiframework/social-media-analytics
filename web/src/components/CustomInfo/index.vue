<template>
	<el-dialog title="详情" v-model="visible" :class="props.option.dialogClass" :before-close="beforeClose" draggable>
		<div class="form_max">
			<el-descriptions v-for="item, index in props.option.tableInfoItem" :key="index" :title="item.title"
				:column="appStore.device == 'mobile' ? 1 : item.column ? item.column : 2" border
				:direction="appStore.screenWidth < 360 ? 'vertical' : 'horizontal'" style="margin-bottom: 30px;">
				<el-descriptions-item :label-class-name="`mobile_desLabel_${props.option.labelWidth}`" v-for="item, index in item.infoData"
					:key="index" :span="item.span" :label="item.label + (appStore.screenWidth > 500 ? '：' : '')">
					<!-- tag 标签类型 -->
					<div v-if="item.type == 'tag'" v-show="item.isShow" :label="item.label">
						<template v-for="itm, indx in item.dicData">
							<el-tag :type="itm.type" :color="item.color" :key="indx"
								v-if="props.rowData[item.prop] == itm[item.customValue ? item.customValue : 'value']">
								{{ itm[item.customLabel ? item.customLabel : 'label'] }}
							</el-tag>
						</template>
					</div>
					<!-- img 图片类型 -->
					<div v-else-if="item.type == 'img'" v-show="item.isShow" :label="item.label">
						<template v-if="props.rowData[item.prop] && Array.isArray(props.rowData[item.prop])">
							<el-image class="m5" v-for="itm, indx in props.rowData[item.prop]" :src="itm" :key="indx"
								preview-teleported :initial-index="indx" :preview-src-list="props.rowData[item.prop]"
								:fit="item.fit" :style="{ width: `${item.imgWidth}px` }"></el-image>
						</template>
						<template v-else>
							<el-image :preview-src-list="[props.rowData[item.prop]]" preview-teleported :fit="item.fit"
								:style="{ width: `${item.imgWidth}px` }" :src="props.rowData[item.prop]"></el-image>
						</template>
					</div>
					<!-- double 二级表头类型 -->
					<div v-else-if="item.type == 'double'" v-show="item.isShow" :label="item.label">
						<div v-for="i, ind in item.children" v-show="item.isShow" :label="i.label" :min-width="i.width"
							:key="ind">
							<span :style="{ color: i.color }"> {{ props.rowData[i.prop] }} </span>
						</div>
					</div>
					<!-- custom 自定义类型 -->
					<div v-else-if="item.type == 'custom'" v-show="item.isShow" :label="item.label">
						<slot name='custom' :row="props.rowData" :prop="item.prop"></slot>
					</div>
					<!-- html 标记语言类型 -->
					<div v-else-if="item.type == 'html'" v-show="item.isShow" :label="item.label">
						<div class="custom_html" :style="{ color: item.color }" v-html="props.rowData[item.prop]"></div>
					</div>
					<!-- text 文本类型 -->
					<div v-else-if="item.type == 'text'" v-show="item.isShow" :label="item.label">
						<span v-if="props.rowData[item.prop] != null && props.rowData[item.prop] != undefined"
							:style="{ color: item.color }">
							{{ props.rowData[item.prop] }} {{ item.append }}
						</span>
					</div>
				</el-descriptions-item>
			</el-descriptions>
		</div>
	</el-dialog>
</template>

<script setup>
import { watch } from 'vue';
import useAppStore from '@/store/modules/app';
const appStore = useAppStore();
const props = defineProps({
	option: { type: Object, default: {} },
	rowData: { type: Object, default: {} },
	visible: { type: Boolean, default: false },

});
const emits = defineEmits(['cancel']);
const visible = ref(false);
watch(() => props.visible, (news) => visible.value = news)
/**
 * 表单组件 change
 */
const beforeClose = () => cancel();
const cancel = () => {
	emits('cancel');
};
</script>
<style scoped lang="scss">
:deep(.custom_html) {
	p {
		padding: 0;
		margin: 10px;
	}
}

:deep(.mobile_desLabel) {
	width: 30%;
}

:deep(.mobile_desLabel_10) {
	width: 10%;
}

:deep(.mobile_desLabel_20) {
	width: 20%;
}

:deep(.mobile_desLabel_30) {
	width: 30%;
}

:deep(.mobile_desLabel_40) {
	width: 40%;
}

:deep(.mobile_desLabel_50) {
	width: 50%;
}
</style>