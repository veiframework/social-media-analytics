<template>
    <div>
        

        <!-- 排名管理表格 -->
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
        </CustomTable>

        <!-- 成绩表单弹窗 -->
        <CustomDialog 
            :form="form" 
            :option="optionDialog" 
            :visible="visible" 
            @cancel="visible = false" 
            @save="handleSave"
        >
        </CustomDialog>
    </div>
</template>

<script setup name="CompetitionRanking">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import { reactive, ref, getCurrentInstance, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Back, Plus } from "@element-plus/icons-vue";

import { 
    getCompetitionScoreListApi,
    addCompetitionScoreApi,
    editCompetitionScoreApi,
    delCompetitionScoreApi,
    importCompetitionScoreApi,
    exportCompetitionScoreApi,
    downloadCompetitionScoreTemplateApi
} from "@/api/competition";
import useUserStore from '@/store/modules/user';

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 赛事和分组信息
const competitionId = ref('');
const groupId = ref('');
const competitionName = ref('');
const groupName = ref('');

// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 表单参数
const form = ref({});
const visible = ref(false);

/**
 * 初始化参数
 */
const initParams = () => {
    competitionId.value = route.params.competitionId || route.query.competitionId;
    groupId.value = route.params.groupId || route.query.groupId;
    competitionName.value = route.query.competitionName || '赛事';
    groupName.value = route.query.groupName || '分组';

    if (!competitionId.value || !groupId.value) {
        ElMessage.error('缺少必要参数');
        handleBack();
        return false;
    }
    return true;
}

/**
 * 请求列表
 */
const getData = () => {
    if (!competitionId.value || !groupId.value) return;

    const query = { 
        ...queryPramas.value,
        competitionId: competitionId.value,
        groupId: groupId.value
    };
    query.pageNum = pageNum.value;
    query.pageSize = pageSize.value;
    
    getCompetitionScoreListApi(query).then(res => {
        if (res.code === 200) {
            tableData.value = res.data.records || res.data.list || [];
            total.value = res.data.total || 0;
        }
    }).catch(err => {
        console.error('获取排名列表失败:', err);
        ElMessage.error('获取排名列表失败');
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
                competitionId: competitionId.value,
                groupId: groupId.value,
                pointsAwarded: '0'
            };
            visible.value = true;
            break;
        case "down":
            const loadingDown = ElLoading.service({ lock: true, text: '正在导出...', background: 'rgba(0, 0, 0, 0.7)' });
            downloadCompetitionScoreTemplateApi({groupId: groupId.value}).then(res => {
                userStore.exportFile(res, '排行榜模板');
                loadingDown.close();
            }).catch(err => {
                loadingDown.close();
                ElMessage.error('下载模板失败');
            });
            break;
        case "up":
            userStore.importLocalFile('importFile').then(resData => {
                importCompetitionScoreApi({ fileUrl: resData }, competitionId.value, groupId.value)
                .then((res) => {
                    ElMessage.success(res.msg);
                    getData();
                });
            }).catch((err) => {
                ElMessage.error(err.msg || '导入失败');
            });
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
            form.value = { 
                ...row,
                competitionId: competitionId.value,
                groupId: groupId.value
            };
            visible.value = true;
            break;
        case "delete":
            ElMessageBox.confirm('是否删除该成绩记录？', '提示', { 
                confirmButtonText: '确认', 
                cancelButtonText: '取消', 
                type: 'warning' 
            }).then(() => {
                delCompetitionScoreApi(row.id).then(res => {
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
    const formData = { ...val };
    
    if (val.id) {
        editCompetitionScoreApi(formData, formData.id).then(res => {
            if (res.code === 200) {
                ElMessage.success(res.msg || '编辑成功');
                visible.value = false;
                getData();
            } else {
                ElMessage.error(res.msg || '编辑失败');
            }
        });
    } else {
        addCompetitionScoreApi(formData).then(res => {
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
 * 返回分组管理
 */
const handleBack = () => {
    router.push({
        path: `/competition/group/${competitionId.value}`,
        query: {
            competitionId: competitionId.value
        }
    });
}

/**
 * 跨页选择Id
 */
const selectData = (val) => {
    console.log('选中数据:', val);
}

// 表格配置项
const option = reactive({
    showSearch: true,
    searchLabelWidth: 90,
    /** 搜索字段配置项 */
    searchItem: [
        { 
            type: "input", 
            label: "参赛者", 
            prop: "participantName", 
            placeholder: "请输入参赛者姓名",
            max: 50, 
            default: null 
        },
        { 
            type: "input", 
            label: "身份证号", 
            prop: "participantIdCard", 
            placeholder: "请输入身份证号",
            max: 20, 
            default: null 
        }
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        { key: "add", text: "录入成绩", icon: "Plus", isShow: true, type: "primary", disabled: false },
        {
            key: "down", 
            text: "下载模板", 
            icon: "Download", 
            isShow: true, 
            type: "primary", 
            disabled: false,
            hasPermi: ['com.chargehub.admin.competition.domain.CompetitionScore:crud:EXCEL_TEMPLATE']
        },
        {
            key: "up", 
            text: "导入模板", 
            icon: "Upload", 
            isShow: true, 
            type: "primary", 
            disabled: false,
            hasPermi: ['competition:score:import']
        }
    ],
    /** 表格顶部右侧 toobar 配置项 */
    toolbar: { isShowToolbar: true, isShowSearch: true },
    openSelection: false,
    /** 序号下标配置项 */
    index: {
        openIndex: true,
        indexFixed: true,
        indexWidth: 60
    },
    /** 表格字段配置项 */
    tableItem: [
        { 
            type: 'text', 
            label: '参赛者姓名', 
            prop: 'participantName', 
            width: 120, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '身份证号', 
            prop: 'participantIdCard', 
            width: 180, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        },
        { 
            type: 'text', 
            label: '比赛用时', 
            prop: 'competitionTime', 
            width: 120, 
            fixed: false, 
            sortable: false, 
            isShow: true,
            align: 'center'
        },
        { 
            type: 'text', 
            label: '排名', 
            prop: 'rank', 
            width: 80, 
            fixed: false, 
            sortable: true, 
            isShow: true,
            align: 'center'
        },
        { 
            type: 'text', 
            label: '录入时间', 
            prop: 'createTime', 
            width: 160, 
            fixed: false, 
            sortable: false, 
            isShow: true 
        }
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 150,
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
    dialogTitle: "成绩管理",
    dialogClass: "dialog_xs",
    labelWidth: "120px",
    formitem: [
        { 
            type: "input", 
            label: "参赛者姓名",
            prop: "participantName",
            placeholder: "请输入参赛者姓名",
            default: null
        },
        { 
            type: "input", 
            label: "身份证号",
            prop: "participantIdCard",
            placeholder: "请输入身份证号",
            default: null
        },
        { 
            type: "input", 
            label: "比赛用时",
            prop: "competitionTime",
            placeholder: "如：01:23:45",
            default: null
        },
        { 
            type: "input", 
            label: "排名",
            prop: "rank",
            placeholder: "请输入排名",
            category: "number",
            min: 1,
            default: null
        }
    ],
    rules: {
        participantName: [{ required: true, message: '请输入参赛者姓名', trigger: 'blur' }],
        participantIdCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
        rank: [{ required: true, message: '请输入排名', trigger: 'blur' }]
    }
});

// 初始化数据
onMounted(() => {
    if (initParams()) {
        getData();
    }
});
</script>

<style scoped>
.ranking-info-card {
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
    margin: 0 0 8px 0;
    font-size: 28px;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.info-content p {
    margin: 0;
    font-size: 16px;
    opacity: 0.9;
}

.info-actions {
    flex-shrink: 0;
    display: flex;
    gap: 12px;
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

.info-actions .el-button--primary {
    background: rgba(64, 158, 255, 0.8);
    border-color: rgba(64, 158, 255, 0.9);
}

.info-actions .el-button--primary:hover {
    background: rgba(64, 158, 255, 0.9);
    border-color: rgba(64, 158, 255, 1);
}

/* 响应式调整 */
@media (max-width: 768px) {
    .info-header {
        flex-direction: column;
        gap: 16px;
    }
    
    .info-actions {
        width: 100%;
        flex-direction: column;
    }
    
    .info-actions .el-button {
        width: 100%;
    }
    
    .ranking-info-card {
        padding: 16px;
    }
    
    .info-content h2 {
        font-size: 24px;
    }
}
</style>