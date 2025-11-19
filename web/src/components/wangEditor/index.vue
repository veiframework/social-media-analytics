<template>
	<div class="body">
		<Toolbar class="tooler" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
		<Editor class="editor" v-model="valueHtml" :defaultConfig="editorConfig" :mode="mode" @onCreated="handleCreated"
			@onChange="handleBlur" />
	</div>
</template>
<script lang="ts" setup>
import '@wangeditor/editor/dist/css/style.css'; // 引入 css
import { onBeforeUnmount, ref, shallowRef, onMounted } from 'vue';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { IToolbarConfig } from '@wangeditor/editor';
import { DomEditor } from '@wangeditor/editor';
import { uploadFile, uploadLocalFile } from "@/utils/uploadFile";
import { uploadToAliyun,uploadToOSS } from '@/utils/ossUpload';

// 接收传值
interface UploadFileProps {
	text: string,
	path: string,
	uploadType?: string, // 新增：上传类型配置
}
const props = withDefaults(defineProps<UploadFileProps>(), {
	text: '',
	path: '',
	uploadType: 'aliyun',
});
interface UploadEmits {
	(event: 'handleBlur', val: string): void
}
const emit = defineEmits<UploadEmits>()
const handleBlur = () => {
	emit('handleBlur', valueHtml.value) //1212:传个父组件的数据
}
const mode = ref('default')
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()
// 内容 HTML
const valueHtml = ref()
/* 工具栏配置 */
const toolbarConfig: Partial<IToolbarConfig> = {}
toolbarConfig.excludeKeys = ['group-video']
// 模拟 ajax 异步获取内容
onMounted(() => {
	const text = props.text
	setTimeout(() => {
		// const toolbar: any = DomEditor.getToolbar(editorRef.value);
		// const curToolbarConfig = toolbar.getConfig();
		// console.log(curToolbarConfig.toolbarKeys); // 当前菜单排序和分组
		valueHtml.value = text
	}, 0)
})
// 编辑栏配置
const editorConfig: Partial<any> = {
	placeholder: '请输入内容...',
	MENU_CONF: {}
}
editorConfig.MENU_CONF["uploadImage"] = {
	customUpload(file: any, insertFn: any) {
		// 根据 uploadType 选择不同的上传方式
		const uploadPath = props.path ? props.path : 'avatar';
    if (props.uploadType === 'ali') {
      // 使用本地上传 /admin-api/upload 接口
      uploadToOSS({ file: file, path: uploadPath }).then((res: any) => {
        if (!res) return;
        insertFn(res);
      }).catch((error: any) => {
        console.error('wangEditor 本地上传失败:', error);
      });
    }else
		if (props.uploadType === 'local') {
			// 使用本地上传 /admin-api/upload 接口
			uploadLocalFile({ file: file, path: uploadPath }).then((res: any) => {
				if (!res) return;
				insertFn(res);
			}).catch((error: any) => {
				console.error('wangEditor 本地上传失败:', error);
			});
		} else if (props.uploadType === 'obs') {
			// 使用 OBS 上传
			uploadFile({ file: file, path: uploadPath }).then((res: any) => {
				if (!res) return;
				insertFn(res);
			}).catch((error: any) => {
				console.error('wangEditor OBS 上传失败:', error);
			});
		} else {
			// 默认使用阿里云上传
			uploadToAliyun({ file: file, path: uploadPath }).then((res: any) => {
				if (!res) return;
				insertFn(res);
			}).catch((error: any) => {
				console.error('wangEditor 阿里云上传失败:', error);
			});
		}
	},
};
editorConfig.autoFocus = false
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
	const editor = editorRef.value
	if (editor == null) return
	editor.destroy()
})
const handleCreated = (editor: any) => {
	editorRef.value = editor // 记录 editor 实例，重要！
}
defineExpose({ valueHtml })
</script>
<style scoped lang="scss">
$ed-color: #ccc;

.body {
	border: 1px solid $ed-color;

	.tooler {
		border-bottom: 1px solid $ed-color;
	}

	.editor {
		height: 350px !important;
		overflow-y: hidden;
	}
}
</style>