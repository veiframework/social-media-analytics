<template>
    <div>
       

        <!-- 分组管理表格 -->
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
            @selectData="selectData"
        >
            <template #table-top></template>
            <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'status'">
                    <el-tag :type="row.status=='OPEN' ? 'success' : 'danger'">
                        {{ row.status=='OPEN' ? '开放报名' : '报名已满' }}
                    </el-tag>
                </template>
            </template>
        </CustomTable>

        <!-- 分组表单弹窗 -->
        <CustomDialog 
            :form="form" 
            :option="optionDialog" 
            :visible="visible" 
            @cancel="visible = false" 
            @save="handleSave"
        >
            <template #custom-item="{ prop, form }">
                <div v-if="prop == 'sessionDate'">
                    <div class="session-date-container">
                        <div v-for="(date, index) in sessionDates" :key="index" class="session-date-item">
                            <el-date-picker
                                v-model="sessionDates[index]"
                                type="date"
                                format="YYYY-MM-DD"
                                value-format="YYYY-MM-DD"
                                placeholder="选择日期"
                                @change="updateSessionDate"
                                style="width: 200px;"
                                clearable
                            />
                            <el-button 
                                type="danger" 
                                size="small" 
                                icon="Delete" 
                                @click="removeSessionDate(index)"
                                :disabled="sessionDates.length <= 1"
                                style="margin-left: 8px;"
                            >
                                删除
                            </el-button>
                        </div>
                        <el-button 
                            type="primary" 
                            size="small" 
                            icon="Plus" 
                            @click="addSessionDate"
                            style="margin-top: 8px;"
                        >
                            添加场次日期
                        </el-button>
                        <div v-if="form.sessionDate" class="session-date-preview">
                            <small style="color: #909399;">
                                当前场次日期：{{ form.sessionDate }}
                            </small>
                        </div>
                    </div>
                </div>
                
                <div v-if="prop == 'sessionTime'">
                    <div class="session-time-container">
                        <div v-for="(time, index) in sessionTimes" :key="index" class="session-time-item">
                            <el-time-picker
                                v-model="sessionTimes[index]"
                                format="HH:mm"
                                value-format="HH:mm"
                                placeholder="选择时间"
                                @change="updateSessionTime"
                                style="width: 200px;"
                                clearable
                            />
                            <el-button 
                                type="danger" 
                                size="small" 
                                icon="Delete" 
                                @click="removeSessionTime(index)"
                                :disabled="sessionTimes.length <= 1"
                                style="margin-left: 8px;"
                            >
                                删除
                            </el-button>
                        </div>
                        <el-button 
                            type="primary" 
                            size="small" 
                            icon="Plus" 
                            @click="addSessionTime"
                            style="margin-top: 8px;"
                        >
                            添加场次时间
                        </el-button>
                        <div v-if="form.sessionTime" class="session-time-preview">
                            <small style="color: #909399;">
                                当前场次时间：{{ form.sessionTime }}
                            </small>
                        </div>
                    </div>
                </div>
            </template>
        </CustomDialog>
    </div>
</template>

<script setup name="CompetitionGroup">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref, getCurrentInstance, nextTick, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Location, User, Back } from "@element-plus/icons-vue";

import { 
    getCompetitionGroupsApi,
    addCompetitionGroupApi,
    editCompetitionGroupApi,
    delCompetitionGroupApi,
    getCompetitionById
} from "@/api/competition";

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();

// 当前赛事信息
const currentCompetition = ref({});

// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 表单参数
const form = ref({});
const visible = ref(false);

// 场次时间管理
const sessionTimes = ref(['']);

// 场次日期管理
const sessionDates = ref(['']);

// 赛事状态字典
const competitionStatus = [
    { value: 'NOT_STARTED', label: '未开始报名' },
    { value: 'REGISTRATION_OPEN', label: '报名中' },
    { value: 'REGISTRATION_CLOSED', label: '报名结束' },
    { value: 'IN_PROGRESS', label: '进行中' },
    { value: 'FINISHED', label: '已结束' },
];

// 分组状态字典
const groupStatus = [
    { value: 'OPEN', label: '开放报名' },
    { value: 'FULL', label: '报名已满' },
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
 * 获取状态文本
 */
const getStatusText = (status) => {
    const statusItem = competitionStatus.find(item => item.value === status);
    return statusItem ? statusItem.label : status;
}

/**
 * 获取赛事信息
 */
const getCompetitionInfo = async () => {
    const competitionId = route.params.id || route.query.competitionId;
    if (!competitionId) {
        ElMessage.error('缺少赛事ID参数');
        handleBack();
        return;
    }

    // 先设置一个默认的赛事信息，避免页面显示问题
    currentCompetition.value = {
        id: competitionId,
        name: route.query.competitionName || '赛事名称',
        location: '赛事地点',
        organizer: '主办方',
        status: 'REGISTRATION_OPEN'
    };

    try {
        // 通过列表接口获取赛事信息（简化处理）
        const res = await getCompetitionById(  competitionId  );
        if (res.code === 200 && res.data) {
            currentCompetition.value = res.data;
        } else {
            // 如果获取失败，使用默认信息但不跳转回去
            console.warn('获取赛事详细信息失败，使用默认信息');
        }
    } catch (err) {
        console.error('获取赛事信息失败:', err);
        // 获取失败时也不跳转，使用默认信息
        console.warn('获取赛事详细信息失败，使用默认信息');
    }
}

/**
 * 请求列表
 */
const getData = () => {
    const competitionId = route.params.id || route.query.competitionId;
    if (!competitionId) {
        ElMessage.error('缺少赛事ID参数');
        return;
    }

    const query = { 
        ...queryPramas.value,
        competitionId: competitionId
    };
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    
    getCompetitionGroupsApi(query).then(res => {
        if (res.code === 200) {
            tableData.value = res.data.records || res.data.list || [];
            total.value = res.data.total || 0;
        }
    }).catch(err => {
        console.error('获取分组列表失败:', err);
        ElMessage.error('获取分组列表失败');
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
            const competitionId = route.params.id || route.query.competitionId;
            form.value = {
                competitionId: competitionId,
                status: 'OPEN',
                sessionTime: '',
                sessionDate: ''
            };
            sessionTimes.value = [''];
            sessionDates.value = [''];
            optionDialog.formitem.forEach(i=>{
              if(i.prop=='coverImage'){
                i.default = currentCompetition.value.coverImage
              }
            })
            visible.value = true;
            break;
    }
}

/**
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
    switch (value) {
        case "edit":
            form.value = { ...row };
            // 解析场次时间字符串为数组
            if (row.sessionTime) {
                sessionTimes.value = row.sessionTime.split(',').map(time => time.trim());
            } else {
                sessionTimes.value = [''];
            }
            // 解析场次日期字符串为数组
            if (row.sessionDate) {
                sessionDates.value = row.sessionDate.split(',').map(date => date.trim());
            } else {
                sessionDates.value = [''];
            }
            visible.value = true;
            break;
        case "ranking":
            // 跳转到排名管理页面
            router.push({
                path: `/competition/ranking/${currentCompetition.value.id}/${row.id}`,
                query: {
                    competitionName: currentCompetition.value.name,
                    groupName: row.name
                }
            });
            break;
        case "registration":
            // 跳转到报名管理页面
            router.push({
                path: '/competition/registration',
                query: {
                    groupId: row.id,
                    competitionId: currentCompetition.value.id,
                    competitionName: currentCompetition.value.name,
                    groupName: row.name
                }
            });
            break;
        case "delete":
            ElMessageBox.confirm('是否删除该分组？', '提示', { 
                confirmButtonText: '确认', 
                cancelButtonText: '取消', 
                type: 'warning' 
            }).then(() => {
                delCompetitionGroupApi(row.id).then(res => {
                    if (res.code === 200) {
                        ElMessage.success(res.msg || '删除成功');
                        getData();
                    } else {
                        ElMessage.error(res.msg || '删除失败');
                    }
                });
            }).catch(() => {});
            break;
    }
}

/**
 * 表单保存
 */
const handleSave = (val) => {
    // 验证场次时间
    const validTimes = sessionTimes.value.filter(time => time && time.trim());
    if (validTimes.length === 0) {
        ElMessage.error('请至少选择一个场次时间');
        return;
    }
    
    // 验证场次日期
    const validDates = sessionDates.value.filter(date => date && date.trim());
    if (validDates.length === 0) {
        ElMessage.error('请至少选择一个场次日期');
        return;
    }
    
    const formData = { ...val };
    // 将场次时间数组转换为逗号分隔的字符串
    formData.sessionTime = validTimes.join(',');
    // 将场次日期数组转换为逗号分隔的字符串
    formData.sessionDate = validDates.join(',');
    
    if (val.id) {
        editCompetitionGroupApi(formData, formData.id).then(res => {
            if (res.code === 200) {
                ElMessage.success(res.msg || '编辑成功');
                visible.value = false;
                getData();
            } else {
                ElMessage.error(res.msg || '编辑失败');
            }
        });
    } else {
        addCompetitionGroupApi(formData).then(res => {
            if (res.code === 200) {
                ElMessage.success(res.msg || '新增成功');
                visible.value = false;
                getData();
            } else {
                ElMessage.error(res.msg || '新增失败');
            }
        });
    }
}

/**
 * 返回赛事列表
 */
const handleBack = () => {
    router.push('/competition');
}

/**
 * 跨页选择Id
 */
const selectData = (val) => {
    console.log('选中数据:', val);
}

/**
 * 添加场次时间
 */
const addSessionTime = () => {
    sessionTimes.value.push('');
}

/**
 * 删除场次时间
 */
const removeSessionTime = (index) => {
    if (sessionTimes.value.length > 1) {
        sessionTimes.value.splice(index, 1);
        updateSessionTime();
    }
}

/**
 * 更新场次时间到表单
 */
const updateSessionTime = () => {
    const validTimes = sessionTimes.value.filter(time => time && time.trim());
    form.value.sessionTime = validTimes.join(',');
    
    // 触发表单验证
    nextTick(() => {
        if (form.value.sessionTime) {
            // 手动触发验证更新
        }
    });
}

/**
 * 添加场次日期
 */
const addSessionDate = () => {
    sessionDates.value.push('');
}

/**
 * 删除场次日期
 */
const removeSessionDate = (index) => {
    if (sessionDates.value.length > 1) {
        sessionDates.value.splice(index, 1);
        updateSessionDate();
    }
}

/**
 * 更新场次日期到表单
 */
const updateSessionDate = () => {
    const validDates = sessionDates.value.filter(date => date && date.trim());
    form.value.sessionDate = validDates.join(',');
    
    // 触发表单验证
    nextTick(() => {
        if (form.value.sessionDate) {
            // 手动触发验证更新
        }
    });
}

// 表格配置项
const option = reactive({
    showSearch: true,
    searchLabelWidth: 90,
    /** 搜索字段配置项 */
    searchItem: [
        { 
            type: "input", 
            label: "分组名称", 
            prop: "name", 
            placeholder: "请输入分组名称",
            max: 50, 
            default: null 
        },
        {
            type: "select", 
            label: "状态", 
            prop: "status", 
            default: null, 
            filterable: true,
            dicData: groupStatus
        }
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        { key: "add", text: "新增分组", icon: "Plus", isShow: true, type: "primary", disabled: false }
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
      { type: 'img', label: '封面图', prop: 'coverImage', width: 100, fixed: false, sortable: false, isShow: true },
      {
            type: 'text', 
            label: '分组名称', 
            prop: 'name', 
            width: 200, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },{
        type: 'text',
        label: '报名人数',
        prop: 'paidNum',
        width: 200,
        fixed: false,
        sortable: false,
        isShow: true
      },{
        type: 'text',
        label: '未支付人数',
        prop: 'pendingNum',
        width: 200,
        fixed: false,
        sortable: false,
        isShow: true
      },
        { 
            type: 'text', 
            label: '分组描述', 
            prop: 'description', 
            width: 250, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            showOverflowTooltip: true
        },
        { 
            type: 'text', 
            label: '完赛积分', 
            prop: 'points', 
            width: 100, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            align: 'center'
        },
        { 
            type: 'text', 
            label: '报名费', 
            prop: 'fee', 
            width: 100, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            align: 'center'
        },
        { 
            type: 'text', 
            label: '最小年龄', 
            prop: 'minAge', 
            width: 100, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            align: 'center'
        },
        { 
            type: 'text', 
            label: '最大年龄', 
            prop: 'maxAge', 
            width: 100, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            align: 'center'
        },
        { 
            type: 'text', 
            label: '场次日期', 
            prop: 'sessionDate', 
            width: 200, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            align: 'center'
        },
        { 
            type: 'text', 
            label: '场次时间', 
            prop: 'sessionTime', 
            width: 200, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            align: 'center'
        },
        { 
            type: 'custom', 
            label: '状态', 
            prop: 'status', 
            width: 120, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            align: 'center'
        }
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 280,
        fixed: 'right'
    },
    menuItemBtn: [
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
            icon: 'Trophy', 
            label: '排名管理', 
            value: 'ranking' 
        },
        { 
            type: 'info', 
            isShow: true, 
            icon: 'UserFilled', 
            label: '报名管理', 
            value: 'registration' 
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
    dialogTitle: '分组信息',
    dialogClass: 'dialog_md',
    labelWidth: '120px',
    formitem: [
        { 
            type: "input", 
            label: "分组名称", 
            prop: "name",
            placeholder: "请输入分组名称",
            max: 100, 
            default: null 
        },{
        type: "upload",
        label: "图片上传",
        prop: "coverImage",
        fileType: "img",
        path: "competition",
        uploadType: "ali",
        default: null,
        span: 24
      },
        { 
            type: "input",
            label: "分组描述", 
            prop: "description", 
            span: 24,
            default: null 
        },
        { 
            type: "input", 
            label: "报名费",
            prop: "fee",
            placeholder: "请输入报名费",
            category: "number",
            min: 1,
            default: null
        },
        { 
            type: "input", 
            label: "完赛积分",
            prop: "points",
            placeholder: "请输入完赛积分",
            category: "number",
            min: 0,
            default: 0
        },
        {
            type: "input",
            label: "最小年龄",
            prop: "minAge",
            placeholder: "请输入最小年龄",
            category: "number",
            min: 1,
            default: null
        }, 
        {
            type: "input",
            label: "最大年龄",
            prop: "maxAge",
            placeholder: "请输入最大年龄",
            category: "number",
            min: 1,
            default: null
        },
        {
            type: "custom",
            label: "场次日期",
            prop: "sessionDate",
            span: 24,
            default: null
        },
        {
            type: "custom",
            label: "场次时间",
            prop: "sessionTime",
            span: 24,
            default: null
        },
        { 
            type: "radio", 
            label: "状态",
            prop: "status",
            default: 'OPEN',
            dicData: groupStatus
        }
    ],
    rules: {
        name: [{ required: true, message: '请输入分组名称', trigger: 'blur' }],
        fee: [{ required: true, message: '请输入报名费', trigger: 'blur' }],
        minAge: [{ required: true, message: '请输入最小年龄', trigger: 'blur' }],
        maxAge: [{ required: true, message: '请输入最大年龄', trigger: 'blur' }],
        status: [{ required: true, message: '请选择状态', trigger: 'blur' }],
        coverImage: [{ required: true, message: '请上传封面图', trigger: 'blur' }],
        sessionDate: [
            { 
                required: true, 
                message: '请选择场次日期', 
                trigger: 'change',
                validator: (rule, value, callback) => {
                    const validDates = sessionDates.value.filter(date => date && date.trim());
                    if (validDates.length === 0) {
                        callback(new Error('请至少选择一个场次日期'));
                    } else {
                        callback();
                    }
                }
            }
        ],
        sessionTime: [
            { 
                required: true, 
                message: '请选择场次时间', 
                trigger: 'change',
                validator: (rule, value, callback) => {
                    const validTimes = sessionTimes.value.filter(time => time && time.trim());
                    if (validTimes.length === 0) {
                        callback(new Error('请至少选择一个场次时间'));
                    } else {
                        callback();
                    }
                }
            }
        ],
    }
});

// 初始化数据
onMounted(async () => {
    await getCompetitionInfo();
    getData();
});
</script>

<style scoped>
.competition-info-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
    color: white;
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.info-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
}

.info-content h2 {
    margin: 0 0 16px 0;
    font-size: 28px;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.info-details {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    align-items: center;
}

.info-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    opacity: 0.9;
}

.info-item .el-icon {
    font-size: 18px;
}

.info-actions {
    flex-shrink: 0;
}

.info-actions .el-button {
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: white;
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;
}

.info-actions .el-button:hover {
    background: rgba(255, 255, 255, 0.3);
    border-color: rgba(255, 255, 255, 0.5);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.session-date-container,
.session-time-container {
    width: 100%;
}

.session-date-item,
.session-time-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
}

.session-date-item:last-child,
.session-time-item:last-child {
    margin-bottom: 0;
}

:deep(.el-date-picker),
:deep(.el-time-picker) {
    width: 200px;
}

.session-date-preview,
.session-time-preview {
    margin-top: 8px;
    padding: 8px;
    background-color: #f5f7fa;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .info-header {
        flex-direction: column;
        gap: 16px;
    }
    
    .info-details {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }
    
    .info-actions {
        width: 100%;
    }
    
    .info-actions .el-button {
        width: 100%;
    }
    
    .competition-info-card {
        padding: 16px;
    }
    
    .info-content h2 {
        font-size: 24px;
    }
}
</style>