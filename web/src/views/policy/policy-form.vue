<template>
    <div v-if="visible">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入标题" />
            </el-form-item>
            <el-form-item label="内容" prop="content">
                <el-input type="textarea" v-model="form.content" :min-height="300" />
            </el-form-item>

          <el-form-item label="启用/禁用" prop="disabled">
            <el-switch v-model="form.disabled" active-value="1" inactive-value="0" ></el-switch>
          </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
        </div>
    </div>
</template>

<script setup name="PolicyForm">
import { reactive, ref, watch } from 'vue';
import { addPolicy, updatePolicy } from "@/api/policy";
import Editor from '@/components/Editor';

const props = defineProps({
    type: {
        type: String,
        default: 'add'
    },
    data: {
        type: Object,
        default: () => ({})
    }
});

const emit = defineEmits(['callback']);

const formRef = ref();
const visible = ref(true);

const form = reactive({
    id: undefined,
    title: '',
    type: 0,
    content: '',
    sequence: 0,
    disabled: '0'
});

// 清空表单方法
const resetForm = () => {
    form.id = undefined;
    form.title = '';
    form.content = '';
    form.sequence = 0;
    form.type = 0;
    form.disabled = '0';
    formRef.value?.resetFields();
};

// 监听type变化，强制重新渲染组件
watch(
    () => props.type,
    (newType) => {
        visible.value = false;
        setTimeout(() => {
            visible.value = true;
            if (newType === 'add') {
                resetForm();
            } else if (newType === 'edit' && props.data) {
                // 编辑时，确保数据被正确加载
                form.id = props.data.id;
                form.title = props.data.title || '';
                form.content = props.data.content || '';
                form.sequence = props.data.sequence || 0;
                form.type = props.data.type || 0;
              form.disabled = props.data.disabled || '0';
            }
        }, 0);
    },
    { immediate: true }  // 添加immediate确保初始化时也执行
);

// 监听data变化，更新表单数据
watch(
    () => props.data,
    (newData) => {
        if (props.type === 'edit' && newData) {
            form.id = newData.id;
            form.title = newData.title || '';
            form.content = newData.content || '';
            form.sequence = newData.sequence || 0;
            form.type = newData.type || 0;
          form.disabled = props.data.disabled || '0';
        }
    },
    { deep: true, immediate: true }  // 添加immediate确保初始化时也执行
);

const rules = {
    title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
};

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            if (props.type === 'add') {
                addPolicy(form).then(() => {
                    resetForm();
                    emit('callback');
                });
            } else {
                updatePolicy(form).then(() => {
                    resetForm();
                    emit('callback');
                });
            }
        }
    });
};

const cancel = () => {
    resetForm();
    emit('callback');
};

// 暴露方法给父组件
defineExpose({
    resetForm
});
</script>

<style scoped>
.dialog-footer {
    text-align: right;
    margin-top: 20px;
}
</style> 