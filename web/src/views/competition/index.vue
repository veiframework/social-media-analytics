<template>
    <div>
        <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
            :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
            @currentChange="handleChange" @selectData="selectData">
            <template #table-top></template>
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'status'">
                    <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                </template>

                <template v-if="prop == 'description'">
                    <div 
                        v-if="row.description" 
                        v-html="getDescriptionPreview(row.description)"
                        style="max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; line-height: 1.4;"
                        :title="stripHtmlTags(row.description)"
                    ></div>
                    <span v-else style="color: #909399; font-style: italic;">暂无描述</span>
                </template>
            </template>
        </CustomTable>
        <CustomDialog :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
            @save="handleSave" @formChange="handleFormChange">
        </CustomDialog>
        
        <!-- 通知配置弹窗 -->
        <el-dialog v-model="notifyConfigVisible" title="通知配置" width="500px" :close-on-click-modal="false">
            <el-form :model="notifyConfigForm" label-width="120px">
                <el-form-item label="提前时长">
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <el-input-number 
                            v-model="notifyConfigForm.duration" 
                            :min="1" 
                            :max="999"
                            style="width: 120px;"
                        />
                        <el-select 
                            v-model="notifyConfigForm.timeUnit" 
                            style="width: 100px;"
                        >
                            <el-option label="分钟" :value="0" />
                            <el-option label="小时" :value="1" />
                            <el-option label="天" :value="2" />
                        </el-select>
                    </div>
                    <div style="color: #909399; font-size: 12px; margin-top: 5px;">
                        设置在赛事开始前多长时间发送通知提醒
                    </div>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="notifyConfigVisible = false">取消</el-button>
                    <el-button type="primary" @click="updateNotifyConfig">保存</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 赛事详情弹窗 -->
        <CustomInfo 
            :option="optionInfo" 
            :rowData="currentCompetitionDetail" 
            :visible="detailVisible" 
            @cancel="detailVisible = false"
        >
            <template #custom="{ row, prop }">
                <template v-if="prop === 'status'">
                    <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                </template>
                <template v-if="prop === 'enabled'">
                    <el-tag :type="row.enabled ? 'success' : 'danger'">
                        {{ row.enabled ? '启用' : '禁用' }}
                    </el-tag>
                </template>
            </template>
        </CustomInfo>
    </div>
</template>

<script setup name="Competition">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import CustomInfo from "@/components/CustomInfo";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import { reactive, ref, getCurrentInstance, nextTick } from "vue";
import { useRouter } from "vue-router";
import { getToken } from "@/utils/auth";



import { 
    getCompetitionListApi, 
    addCompetitionApi, 
    editCompetitionApi, 
    delCompetitionApi,
    exportCompetitionApi
} from "@/api/competition";
import request from '@/utils/request';
import useUserStore from '@/store/modules/user';
const userStore = useUserStore();

const { proxy } = getCurrentInstance();
const router = useRouter();

// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 表单参数
const form = ref({});
const visible = ref(false);

// 通知配置相关参数
const notifyConfigVisible = ref(false);
const notifyConfigForm = ref({
    timeUnit: 2, // 0-分钟, 1-小时, 2-天
    duration: 1  // 提前多少+timeUnit
});

// 赛事详情相关参数
const detailVisible = ref(false);
const currentCompetitionDetail = ref({});

// 通知配置API函数
const getNotifyConfig = async () => {
    try {
        const response = await request({
            url: '/admin-api/admin/competition/notify/config',
            method: 'get'
        });
        if (response.data) {
            notifyConfigForm.value = {
                timeUnit: response.data.timeUnit || 2,
                duration: response.data.duration || 1
            };
        }
    } catch (error) {
        console.error('获取通知配置失败:', error);
        ElMessage.error('获取通知配置失败');
    }
};

const updateNotifyConfig = async () => {
    try {
        await request({
            url: '/admin-api/admin/competition/notify/config',
            method: 'put',
            data: notifyConfigForm.value
        });
        ElMessage.success('通知配置更新成功');
        notifyConfigVisible.value = false;
    } catch (error) {
        console.error('更新通知配置失败:', error);
        ElMessage.error('更新通知配置失败');
    }
};


// 赛事状态字典
const competitionStatus = [
    { value: 'NOT_STARTED', label: '未开始报名' },
    { value: 'REGISTRATION_OPEN', label: '报名中' },
    { value: 'REGISTRATION_CLOSED', label: '报名结束' },
    { value: 'IN_PROGRESS', label: '进行中' },
    { value: 'FINISHED', label: '已结束' },
];

/**
 * 获取状态类型
 */
const getStatusType = (status) => {
  const statusMap = {
    'NOT_STARTED': 'info',
    'REGISTRATION_OPEN': 'info',
    'REGISTRATION_CLOSED': 'warning',
    'IN_PROGRESS': 'primary',
    'FINISHED': 'success'
  };
  return statusMap[status] || 'info';
}




/**
 * 请求列表
 */
const getData = () => {
    const query = { ...queryPramas.value }
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    getCompetitionListApi(query).then(res => {
        if (res.code === 200) {
            tableData.value = res.data.records || res.data.list || [];
            total.value = res.data.total || 0;
        }
    }).catch(err => {
        console.error('获取赛事列表失败:', err);
        ElMessage.error('获取赛事列表失败');
    });
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
            form.value = {
                enabled: true,
                status: 'DRAFT'
            };
            visible.value = true;
            break;
        case "notifyConfig":
            // 打开通知配置弹窗
            getNotifyConfig();
            notifyConfigVisible.value = true;
            break;
        case "export":
            handleExport();
            break;
    }
}

/**
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "detail":
            openCompetitionDetail(row);
            break;
        case "edit":
            form.value = { ...row };
            visible.value = true;
            break;
        case "groupManagement":
            router.push({
                path: `/competition/group/${row.id}`,
                query: {
                    competitionId: row.id,
                    competitionName: row.name
                }
            });
            break;
        case "competitionImages":
            router.push({
                path: `/competition/images/${row.id}`,
                query: {
                    competitionId: row.id,
                    competitionName: row.name
                }
            });
            break;
        case "delete":
            ElMessageBox.confirm('是否删除该赛事？', '提示', { 
                confirmButtonText: '确认', 
                cancelButtonText: '取消', 
                type: 'warning' 
            }).then(() => {
                delCompetitionApi(row.id).then(res => resMsg(res))
            }).catch(() => {});
            break;
    }
}

/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val };
    if (val.id) {
        editCompetitionApi(formData, formData.id).then(res => resMsg(res));
    } else {
        addCompetitionApi(formData).then(res => resMsg(res));
    }
}

/**
 * 响应消息处理
 */
const resMsg = (res) => {
    if (res.code === 200) {
        ElMessage.success(res.msg || '操作成功');
        visible.value = false;
        getData();
    } else {
        ElMessage.error(res.msg || '操作失败');
    }
}

/**
 * 导出功能
 */
const handleExport = () => {
    const query = { ...queryPramas.value };
    exportCompetitionApi(query).then(res => {
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `赛事列表_${new Date().toISOString().slice(0, 10)}.xlsx`;
        link.click();
        window.URL.revokeObjectURL(url);
        ElMessage.success('导出成功');
    }).catch(err => {
        console.error('导出失败:', err);
        ElMessage.error('导出失败');
    });
}

/**
 * 跨页选择Id
 */
const selectData = (val) => {
    console.log('选中数据:', val);
}

/**
 * 表单选项变更操作
 */
const handleFormChange = (val) => {
    const { prop, form } = val;
    // 可以在这里处理表单联动逻辑
}



/**
 * 获取状态文本
 */
const getStatusText = (status) => {
    const statusItem = competitionStatus.find(item => item.value === status);
    return statusItem ? statusItem.label : status;
}

/**
 * 去除HTML标签，获取纯文本
 */
const stripHtmlTags = (html) => {
    if (!html) return '';
    const doc = new DOMParser().parseFromString(html, 'text/html');
    return doc.body.textContent || '';
}

/**
 * 获取富文本描述的预览内容
 */
const getDescriptionPreview = (html) => {
    if (!html) return '';
    const text = stripHtmlTags(html);
    return text.length > 50 ? text.substring(0, 50) + '...' : text;
}

/**
 * 打开赛事详情弹窗
 */
const openCompetitionDetail = (competition) => {
    currentCompetitionDetail.value = { ...competition };
    detailVisible.value = true;
}











// 表格配置项
const option = reactive({
    showSearch: true,
    searchLabelWidth: 90,
    /** 搜索字段配置项 */
    searchItem: [
        { 
            type: "input", 
            label: "赛事名称", 
            prop: "name", 
            placeholder: "请输入赛事名称",
            max: 50, 
            default: null 
        },
        { 
            type: "input", 
            label: "主办方", 
            prop: "organizer", 
            placeholder: "请输入主办方",
            max: 50, 
            default: null 
        },
        { 
            type: "input", 
            label: "赛事地点", 
            prop: "address",
            placeholder: "请输入赛事地点",
            max: 50, 
            default: null 
        },

        {
            type: "select", 
            label: "启用状态", 
            prop: "enabled", 
            default: null, 
            filterable: true,
            dicData: [
                { value: true, label: '启用' }, 
                { value: false, label: '禁用' }
            ]
        },
        { 
            type: "daterange", 
            label: "开始时间", 
            prop: "Time",
            format: "YYYY-MM-DD", 
            valueFormat: "YYYY-MM-DD", 
            category: "daterange", 
            default: null 
        },
        { 
            type: "daterange", 
            label: "报名时间", 
            prop: "registrationStartTime", 
            format: "YYYY-MM-DD", 
            valueFormat: "YYYY-MM-DD", 
            category: "daterange", 
            default: null 
        }
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        { key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false },
        { key: "notifyConfig", text: "通知配置", icon: "Bell", isShow: true, type: "warning", disabled: false }
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
            label: '封面图片', 
            prop: 'coverImage', 
            width: 100, 
            fixed: false, 
            sortable: false, 
            isShow: true
        },
        { 
            type: 'text', 
            label: '赛事名称', 
            prop: 'name', 
            width: 200, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '主办方', 
            prop: 'organizer', 
            width: 150, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '赛事地点', 
            prop: 'address',
            width: 150, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },

        { 
            type: 'custom', 
            label: '赛事状态', 
            prop: 'status', 
            width: 100, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '开始时间', 
            prop: 'startTime', 
            width: 120, 
            fixed: false, 
            sortable: true, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '结束时间', 
            prop: 'endTime', 
            width: 120, 
            fixed: false, 
            sortable: true, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '报名开始', 
            prop: 'registrationStartTime', 
            width: 120, 
            fixed: false, 
            sortable: true, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '报名结束', 
            prop: 'registrationDeadline',
            width: 120, 
            fixed: false, 
            sortable: true, 
            isShow: true 
        },
        {
            type: 'tag', 
            label: '启用状态', 
            prop: 'enabled', 
            width: 100, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            dicData: [
                { value: 'true', label: '启用' },
                { value: 'false', label: '禁用' }
            ]
        }
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 240,
        fixed: 'right'
    },
    menuItemBtn: [
        { 
            type: 'info', 
            isShow: true, 
            icon: 'View', 
            label: '详情', 
            value: 'detail' 
        },
        { 
            type: 'primary', 
            isShow: true, 
            icon: 'Edit', 
            label: '编辑', 
            value: 'edit' 
        },
        { 
            type: 'success', 
            isShow: true, 
            icon: 'UserFilled', 
            label: '分组管理', 
            value: 'groupManagement' 
        },
        { 
            type: 'warning', 
            isShow: true, 
            icon: 'Picture', 
            label: '赛事图片', 
            value: 'competitionImages' 
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
    dialogTitle: '赛事信息',
    dialogClass: 'dialog_lg',
    labelWidth: '120px',
    formitem: [
        { 
            type: "input", 
            label: "赛事名称", 
            prop: "name", 
            placeholder: "请输入赛事名称",
            max: 100, 
            default: null 
        },
        { 
            type: "input", 
            label: "主办方", 
            prop: "organizer", 
            placeholder: "请输入主办方",
            max: 100, 
            default: null 
        },
        { 
            type: "input", 
            label: "赛事地点", 
            prop: "address",
            placeholder: "请输入赛事地点",
            max: 200, 
            default: null 
        },
      { type: "map", label: "地图", default: null, span: 2 },

      {
        type: "upload",
        label: "封面图片(建议比例3:4)",
        prop: "coverImage",
        fileType: "img",
        path: "competition",
        uploadType: "ali",
        span: 12,
        default: null
      },
        { 
            type: "rich", 
            label: "赛事描述", 
            prop: "description", 
            path: "competition",
            uploadType: "ali",
            span: 24,
            default: null 
        },{
        type: "input",
        label: "赛事规则",
        prop: "rules",
        span: 24,
        default: null
      },{
        type: "input",
        label: "赛事奖品",
        prop: "prizes",
        span: 24,
        default: null
             },


        { 
            type: "datetime",
            label: "报名开始",
            prop: "registrationStartTime", 
            format: "YYYY-MM-DD",
            valueFormat: "YYYY-MM-DD HH:mm:ss", 
            default: null 
        },
        { 
            type: "datetime",
            label: "报名结束",
            prop: "registrationDeadline",
            format: "YYYY-MM-DD",
            valueFormat: "YYYY-MM-DD 23:59:59",
            default: null 
        },
      {
        type: "datetime",
        label: "开始时间",
        prop: "startTime",
        format: "YYYY-MM-DD",
        valueFormat: "YYYY-MM-DD HH:mm:ss",
        default: null
      },
      {
        type: "datetime",
        label: "结束时间",
        prop: "endTime",
        format: "YYYY-MM-DD",
        valueFormat: "YYYY-MM-DD 23:59:59",
        default: null
      },

        { 
            type: "radio", 
            label: "启用状态",
            prop: "enabled", 
            default: 'true',
            dicData: [
                { value: 'true', label: '启用' },
                { value: 'false', label: '禁用' }
            ]
        }
    ],
    rules: {
        name: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
        coverImage: [{ required: true, message: '请上传封面', trigger: 'blur' }],
        organizer: [{ required: true, message: '请输入主办方', trigger: 'blur' }],
      address: [{ required: true, message: '请输入赛事地点', trigger: 'blur' }],
        status: [{ required: true, message: '请选择赛事状态', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
        registrationStartTime: [{ required: true, message: '请选择报名开始时间', trigger: 'change' }],
        registrationEndTime: [{ required: true, message: '请选择报名结束时间', trigger: 'change' }],
      longitde: [{ required: true, message: '请选择经度', trigger: 'change' }],
      latitude: [{ required: true, message: '请选择纬度', trigger: 'change' }],
    }
});





// CustomInfo配置项
const optionInfo = reactive({
    dialogClass: 'dialog_lg',
    labelWidth: '30',
    tableInfoItem: [
        {
            title: '基本信息',
            column: 2,
            infoData: [
                { 
                    type: 'text', 
                    label: '赛事名称', 
                    prop: 'name', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '主办方', 
                    prop: 'organizer', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '赛事地点', 
                    prop: 'address',
                    isShow: true 
                },
                { 
                    type: 'custom', 
                    label: '赛事状态', 
                    prop: 'status', 
                    isShow: true 
                },
                { 
                    type: 'custom', 
                    label: '启用状态', 
                    prop: 'enabled', 
                    isShow: true 
                }
            ]
        },
        {
            title: '封面图片',
            column: 1,
            infoData: [
                { 
                    type: 'img', 
                    label: '封面图片', 
                    prop: 'coverImage', 
                    imgWidth: 300,
                    fit: 'cover',
                    isShow: true 
                }
            ]
        },
        {
            title: '时间信息',
            column: 2,
            infoData: [
                { 
                    type: 'text', 
                    label: '开始时间', 
                    prop: 'startTime', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '结束时间', 
                    prop: 'endTime', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '报名开始', 
                    prop: 'registrationStartTime', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '报名结束', 
                    prop: 'registrationDeadline', 
                    isShow: true 
                }
            ]
        },
        {
            title: '详细信息',
            column: 1,
            infoData: [
                { 
                    type: 'html', 
                    label: '赛事描述', 
                    prop: 'description', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '赛事规则', 
                    prop: 'rules', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '赛事奖品', 
                    prop: 'prizes', 
                    isShow: true 
                }
            ]
        },
        {
            title: '创建信息',
            column: 2,
            infoData: [
                { 
                    type: 'text', 
                    label: '创建时间', 
                    prop: 'createTime', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '更新时间', 
                    prop: 'updateTime', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '创建人', 
                    prop: 'createBy_dictText', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '更新人', 
                    prop: 'updateBy_dictText', 
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
.el-tag {
    margin: 0;
}





:deep(.el-dialog__body) {
    padding: 20px;
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