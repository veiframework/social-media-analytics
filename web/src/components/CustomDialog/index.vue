<template>
	<el-dialog :title="props.option.dialogTitle ? props.option.dialogTitle : props.form.id ? '编辑' : '新增'"
		:close-on-press-escape="false" v-model="visible" :class="props.option.dialogClass" :before-close="beforeClose"
		draggable :close-on-click-modal="false">
		<CustomForm ref="formRef" :form="props.form" :option="props.option" :visible="visible"
			@formChange="handleFormChange" @formBlur="handleFormBlur">
			<template #custom-item="{ prop, form }">
				<slot name='custom-item' :prop="prop" :form="form"></slot>
			</template>
		</CustomForm>
    <slot name='content'></slot>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="cancel">取 消</el-button>
				<el-button type="primary" @click="save">确 定</el-button>
			</span>
		</template>
	</el-dialog>
</template>
<script setup>
import { ref, watch } from 'vue';
import CustomForm from "../CustomForm";
const props = defineProps({
	option: { type: Object, default: {} },
	form: { type: Object, default: {} },
	visible: { type: Boolean, default: false },

});
const emits = defineEmits(['save', 'cancel', 'formChange', 'formBlur']);
const formRef = ref();
const visible = ref(false);
watch(() => props.visible, (news) => { visible.value = news })
/**
 * 表单组件 change
 */
const beforeClose = () => cancel();
const cancel = () => {
	formRef.value.formRef.resetFields()
	formRef.value.form = {}
	emits('cancel');
};
const save = () => {
	formRef.value.formRef.validate(val => {
		if (!val) return;
		const fromParmas = { ...formRef.value.form }
		for (const item of props.option.formitem) {
			switch (item.type) {
				case "uploads":
					fromParmas[item.prop] = fromParmas[item.prop].map(obj => obj.url)
					break;
			}
		}
		emits('save', fromParmas);
	})
};
const handleFormChange = (val) => emits('formChange', val)
const handleFormBlur = (val) => emits('formBlur', val)
defineExpose({ formRef })
</script>

<style lang="scss" scoped></style>