<template>
    <div>
        <!-- 用户列表表格 -->
        <CustomTable 
            :data="tableData" 
            :option="option" 
            @search="handleSearch" 
            :pageNum="pageNum" 
            @refresh="getData"
            :total="total" 
            @headerchange="handleHeader" 
            @menuChange="handleMenu" 
            :pageSize="pageSize"
            @currentChange="handleChange" 
            @selectData="selectData">
            <template #table-top></template>
            <template #table-custom="{ row, prop, index }">

                <template v-if="prop == 'createTime'">
                    {{ formatDateTime(row.createTime) }}
                </template>
                <template v-if="prop == 'updateTime'">
                    {{ formatDateTime(row.updateTime) }}
                </template>
            </template>
        </CustomTable>

        <!-- 用户详情弹窗 -->
        <CustomInfo 
            :option="optionInfo" 
            :rowData="currentUser" 
            :visible="detailVisible" 
            @cancel="detailVisible = false"
        >
            <template #custom="{ row, prop }">
                <template v-if="prop === 'avatar'">
                    <div style="text-align: center;">
                        <el-avatar :src="row.avatar" :size="80" v-if="row.avatar">
                            <template #error>
                                <el-icon><User /></el-icon>
                            </template>
                        </el-avatar>
                        <el-avatar v-else :size="80">
                            <el-icon><User /></el-icon>
                        </el-avatar>
                    </div>
                </template>
                <template v-if="prop === 'createTime'">
                    {{ formatDateTime(row.createTime) }}
                </template>
                <template v-if="prop === 'updateTime'">
                    {{ formatDateTime(row.updateTime) }}
                </template>
            </template>
        </CustomInfo>

        <!-- 用户编辑弹窗 -->
        <CustomDialog 
            :form="form" 
            :option="optionDialog" 
            :visible="visible" 
            @cancel="visible = false"
            @save="handleSave">
        </CustomDialog>
    </div>
</template>

<script setup name="User">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import CustomInfo from "@/components/CustomInfo";
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref, getCurrentInstance, computed } from "vue";
import { User } from '@element-plus/icons-vue';

import settings from '@/settings'

import { 
    getUserListApi, 
    addUserApi, 
    editUserApi, 
    delUserApi,
    getUserDetailApi,
    exportUserApi
} from "@/api/user";

const { proxy } = getCurrentInstance();

// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 表单参数
const form = ref({});
const visible = ref(false);

// 详情参数
const detailVisible = ref(false);
const currentUser = ref(null);

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
    if (!dateTime) return '-';
    return new Date(dateTime).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

/**
 * 获取用户列表
 */
const getData = () => {
    const params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        ...queryPramas.value
    };
    
    getUserListApi(params).then(res => {
        if (res.code === 200) {
            tableData.value = res.data.records || [];
            total.value = res.data.total || 0;
        } else {
            ElMessage.error(res.msg || '获取用户列表失败');
        }
    }).catch(error => {
        console.error('获取用户列表失败:', error);
        ElMessage.error('获取用户列表失败');
    });
}

/**
 * 搜索
 */
const handleSearch = (val) => {
    queryPramas.value = val;
    pageNum.value = 1;
    getData();
}

/**
 * 头部操作
 */
const handleHeader = (key) => {
    switch (key) {
        case "add":
            handleAdd();
            break;
        case "export":
            handleExport();
            break;
    }
}

/**
 * 新增用户
 */
const handleAdd = () => {
    form.value = {};
    visible.value = true;
}

/**
 * 导出用户
 */
const handleExport = () => {
    ElMessageBox.confirm('确认导出所有用户数据？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        exportUserApi(queryPramas.value).then(res => {
            // 创建下载链接
            const url = window.URL.createObjectURL(new Blob([res]));
            const link = document.createElement('a');
            link.style.display = 'none';
            link.href = url;
            link.setAttribute('download', `用户列表_${new Date().getTime()}.xlsx`);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
            ElMessage.success('导出成功');
        }).catch(error => {
            console.error('导出失败:', error);
            ElMessage.error('导出失败');
        });
    }).catch(() => {});
}

/**
 * 菜单操作
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "edit":
            handleEdit(row);
            break;
        case "detail":
            handleDetail(row);
            break;
        case "delete":
            handleDelete(row);
            break;
    }
}

/**
 * 编辑用户
 */
const handleEdit = (row) => {
    form.value = { ...row };
    visible.value = true;
}

/**
 * 查看用户详情
 */
const handleDetail = (row) => {
    getUserDetailApi(row.id).then(res => {
        if (res.code === 200) {
            currentUser.value = res.data;
            detailVisible.value = true;
        } else {
            ElMessage.error(res.msg || '获取用户详情失败');
        }
    }).catch(error => {
        console.error('获取用户详情失败:', error);
        ElMessage.error('获取用户详情失败');
    });
}

/**
 * 删除用户
 */
const handleDelete = (row) => {
    ElMessageBox.confirm(`确认删除用户"${row.nickname}"？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        delUserApi(row.id).then(res => {
            if (res.code === 200) {
                ElMessage.success(res.msg || '删除成功');
                getData();
            } else {
                ElMessage.error(res.msg || '删除失败');
            }
        }).catch(error => {
            console.error('删除失败:', error);
            ElMessage.error('删除失败');
        });
    }).catch(() => {});
}

/**
 * 分页变化
 */
const handleChange = (val) => {
    pageNum.value = val.page;
    pageSize.value = val.limit;
    getData();
}

/**
 * 选择数据
 */
const selectData = (val) => {
    // 批量操作预留
    console.log('选择的数据:', val);
}

/**
 * 保存用户
 */
const handleSave = (val) => {
    if (val.id) {
        // 编辑
        editUserApi(val, val.id).then(res => {
            if (res.code === 200) {
                ElMessage.success(res.msg || '编辑成功');
                visible.value = false;
                getData();
            } else {
                ElMessage.error(res.msg || '编辑失败');
            }
        }).catch(error => {
            console.error('编辑失败:', error);
            ElMessage.error('编辑失败');
        });
    } else {
        // 新增
        addUserApi(val).then(res => {
            if (res.code === 200) {
                ElMessage.success(res.msg || '新增成功');
                visible.value = false;
                getData();
            } else {
                ElMessage.error(res.msg || '新增失败');
            }
        }).catch(error => {
            console.error('新增失败:', error);
            ElMessage.error('新增失败');
        });
    }
}

// 表格配置项
const option = reactive({
    showSearch: true,
    searchLabelWidth: 90,
    /** 搜索字段配置项 */
    searchItem: [
        
        { 
            type: "input", 
            label: "昵称", 
            prop: "nickname", 
            placeholder: "请输入昵称" 
        },
        { 
            type: "input", 
            label: "手机号", 
            prop: "phone", 
            placeholder: "请输入手机号" 
        }
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        // { key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false },
        // { key: "export", text: "导出", icon: "Download", isShow: true, type: "success", disabled: false }
    ],
    /** 表格顶部右侧 toobar 配置项 */
    toolbar: { isShowToolbar: true, isShowSearch: true },
    openSelection: false,
    /** 序号下标配置项 */
    index: {
        openIndex: true,
        indexFixed: true,
        indexWidth: 70
    },
    /** 表格字段配置项 */
    tableItem: [
        
        { 
            type: 'img',
            label: '头像', 
            prop: 'avatar', 
            width: 80, 
            fixed: false, 
            sortable: false, 
            isShow: true
        },
        
        { 
            type: 'text', 
            label: '昵称', 
            prop: 'nickname', 
            width: 120, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '手机号', 
            prop: 'phone', 
            width: 130, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },
      {
        type: 'text',
        label: '积分',
        prop: 'points',
        width: 130,
        fixed: false,
        sortable: false,
        isShow: settings.appUser.score
      },
        { 
            type: 'custom', 
            label: '创建时间', 
            prop: 'createTime', 
            width: 160, 
            fixed: false, 
            sortable: true, 
            isShow: true 
        },
        { 
            type: 'custom', 
            label: '更新时间', 
            prop: 'updateTime', 
            width: 160, 
            fixed: false, 
            sortable: true, 
            isShow: true 
        }
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 200,
        fixed: 'right'
    },
    menuItemBtn: [
        { 
            type: 'primary', 
            isShow: true, 
            icon: 'View', 
            label: '详情', 
            value: 'detail' 
        },
        { 
            type: 'warning', 
            isShow: true, 
            icon: 'Edit', 
            label: '编辑', 
            value: 'edit' 
        },
        { 
            type: 'danger', 
            isShow: true, 
            icon: 'Delete', 
            label: '删除', 
            value: 'delete' 
        }
    ],
    /** page 分页配置项 */
    isShowPage: true
});

// 表单配置项
const optionDialog = reactive({
    dialogTitle: '用户信息',
    dialogClass: 'dialog_md',
    labelWidth: '120px',
    formitem: [
        
        { 
            type: "input", 
            label: "昵称", 
            prop: "nickname", 
            placeholder: "请输入昵称",
            max: 50,
            default: null 
        },
        { 
            type: "input", 
            label: "手机号", 
            prop: "phone", 
            placeholder: "请输入手机号",
            max: 11,
            default: null 
        },
      {
        type: "input",
        label: "积分",
        prop: "points",
        placeholder: "请输入积分",
        category: "number",
        min: 1,
        default: 0,
        judge: (row) => { return settings.appUser.score }
      }
         
    ],
    rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        points: [{ required: true, message: '请输入积分', trigger: 'blur' }]
    }
});

// CustomInfo配置项
const optionInfo = reactive({
    dialogClass: 'dialog_lg',
    labelWidth: '30',
    tableInfoItem: [
        {
            title: '头像信息',
            column: 1,
            infoData: [
                { 
                    type: 'custom', 
                    label: '用户头像', 
                    prop: 'avatar', 
                    isShow: true 
                }
            ]
        },
        {
            title: '基本信息',
            column: 2,
            infoData: [
                { 
                    type: 'text', 
                    label: '昵称', 
                    prop: 'nickname', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '手机号', 
                    prop: 'phone', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '积分', 
                    prop: 'points', 
                    isShow: settings.appUser.score 
                }
            ]
        },
        {
            title: '微信信息',
            column: 2,
            infoData: [
                { 
                    type: 'text', 
                    label: '微信OpenID', 
                    prop: 'openid', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '微信UnionID', 
                    prop: 'unionid', 
                    isShow: true 
                }
            ]
        },
        {
            title: '创建信息',
            column: 2,
            infoData: [
                { 
                    type: 'custom', 
                    label: '创建时间', 
                    prop: 'createTime', 
                    isShow: true 
                },
                { 
                    type: 'custom', 
                    label: '更新时间', 
                    prop: 'updateTime', 
                    isShow: true 
                }
            ]
        }
    ]
});

// 初始化数据
getData();
</script>

<style scoped>


:deep(.el-avatar) {
    border: 2px solid #ebeef5;
}

:deep(.el-table) {
    border-radius: 8px;
    overflow: hidden;
}

:deep(.el-table th) {
    background-color: #f5f7fa;
    color: #606266;
    font-weight: 600;
}

:deep(.el-button + .el-button) {
    margin-left: 8px;
}
</style>