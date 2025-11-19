<template>
    <div>
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
            <template #table-custom="{ row, prop, index }">

                <template v-if="prop == 'imageType'">
                    <el-tag :type="row.imageType === 'HIGHLIGHT' ? 'primary' : 'success'">
                        {{ row.imageType === 'HIGHLIGHT' ? '现场集锦' : '个人照片' }}
                    </el-tag>
                </template>
                <template v-if="prop == 'status'">
                    <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
                        {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
                    </el-tag>
                </template>
                <template v-if="prop == 'description'">
                    <div 
                        v-if="row.description" 
                        style="max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"
                        :title="row.description"
                    >
                        {{ row.description }}
                    </div>
                    <span v-else style="color: #909399; font-style: italic;">暂无描述</span>
                </template>
              
                <template v-if="prop == 'createTime'">
                    <span>{{ row.createTime ? new Date(row.createTime).toLocaleString() : '' }}</span>
                </template>
            </template>
        </CustomTable>
        
                <!-- 图片表单弹窗 -->
        <CustomDialog 
            :form="form" 
            :option="optionDialog" 
            :visible="visible" 
            @cancel="visible = false"
            @save="handleSave"
            @formChange="handleFormChange"
        >
        </CustomDialog>

        <!-- 图片详情弹窗 -->
        <CustomInfo 
            :option="optionInfo" 
            :rowData="currentDetail" 
            :visible="detailVisible" 
            @cancel="detailVisible = false"
        >
            <template #custom="{ row, prop }">
                <template v-if="prop === 'imageType'">
                    <el-tag :type="row.imageType === 'HIGHLIGHT' ? 'primary' : 'success'">
                        {{ row.imageType === 'HIGHLIGHT' ? '现场集锦' : '个人照片' }}
                    </el-tag>
                </template>
                <template v-if="prop === 'status'">
                    <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
                        {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
                    </el-tag>
                </template>
            </template>
        </CustomInfo>
    </div>
</template>

<script setup name="CompetitionImage">
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCompetitionImageListApi, addCompetitionImageApi, editCompetitionImageApi, delCompetitionImageApi } from '@/api/competitionImage'
import { getAllCompetitionApi } from '@/api/competition'
import { getAllUsersApi } from '@/api/user'
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import CustomInfo from "@/components/CustomInfo";
// 响应式数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const visible = ref(false)
const form = ref({})
const queryPramas = ref({})
const competitionOptions = ref([])
const userOptions = ref([])

// 详情弹窗参数
const detailVisible = ref(false)
const currentDetail = ref({})

// 表格配置项
const option = reactive({
    showSearch: true,
    searchLabelWidth: 90,
    /** 搜索字段配置项 */
    searchItem: [
        { 
            type: "select", 
            label: "赛事名称", 
            prop: "competitionId", 
            placeholder: "请选择赛事",
            dicData: competitionOptions,
         
            default: null 
        },
        { 
            type: "input", 
            label: "图片标题", 
            prop: "title", 
            placeholder: "请输入图片标题",
            max: 50, 
            default: null 
        },
        { 
            type: "select", 
            label: "图片类型", 
            prop: "imageType", 
            placeholder: "请选择图片类型",
            dicData: [
                { label: '现场集锦', value: 'HIGHLIGHT' },
                { label: '个人照片', value: 'PERSONAL' }
            ],
            default: null 
        },
        { 
            type: "select", 
            label: "状态", 
            prop: "status", 
            placeholder: "请选择状态",
            dicData: [
                { label: '启用', value: 'ACTIVE' },
                { label: '禁用', value: 'INACTIVE' }
            ],
            default: null 
        }
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [
        { key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false }
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
            label: '图片预览', 
            prop: 'imageUrl', 
            width: 120, 
            fixed: false, 
            sortable: false, 
            isShow: true
        },
          
        { 
            type: 'text', 
            label: '图片标题', 
            prop: 'title', 
            width: 150, 
            fixed: false, 
            sortable: false, 
            isShow: true
        },  {
        type: 'text',
        label: '关联赛事',
        prop: 'competitionId_dictText',
        width: 150,
        fixed: false,
        sortable: false,
        isShow: true
        },
        
        { 
            type: 'text', 
            label: '参赛人', 
            prop: 'participantId_dictText', 
            width: 200, 
            fixed: false, 
            sortable: false, 
            isShow: true
        },{ 
            type: 'custom', 
            label: '图片类型', 
            prop: 'imageType', 
            width: 120, 
            fixed: false, 
            sortable: false, 
            isShow: true
        },
        { 
            type: 'custom', 
            label: '状态', 
            prop: 'status', 
            width: 100, 
            fixed: false, 
            sortable: false, 
            isShow: true
        },
        { 
            type: 'text', 
            label: '创建时间', 
            prop: 'createTime', 
            width: 160, 
            fixed: false, 
            sortable: true, 
            isShow: true
        }
    ],
    /** 操作菜单配置项 */
    menu: {
        isShow: true,
        width: 180,
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
            type: 'danger', 
            isShow: true, 
            icon: 'Delete', 
            label: '删除', 
            value: 'delete' 
        }
    ],
    /** page 分页配置项 */
    isShowPage: true
})

// 表单配置项
const optionDialog = reactive({
    dialogTitle: '图片信息',
    dialogClass: 'dialog_lg',
    labelWidth: '120px',
    formitem: [
        { 
            type: "select", 
            label: "图片类型", 
            prop: "imageType", 
            placeholder: "请选择图片类型",
            dicData: [
                { label: '现场集锦', value: 'HIGHLIGHT' },
                { label: '个人照片', value: 'PERSONAL' }
            ],
            default: 'HIGHLIGHT',
            span: 12,
            judge: (form) => {
                // 当类型改变时，清空相关字段
                if (form.imageType === 'HIGHLIGHT') {
                    form.participantId = '';
                } else if (form.imageType === 'PERSONAL') {
                    form.competitionId = '';
                }
                return true;
            }
        },
        { 
            type: "select", 
            label: "赛事名称", 
            prop: "competitionId", 
            placeholder: "请选择赛事",
            dicData: competitionOptions,
           
            default: null,
            span: 12,
            judge: (form) => form.imageType === 'HIGHLIGHT'
        },
        { 
            type: "select", 
            label: "选择用户", 
            prop: "participantId", 
            placeholder: "请选择用户",
            dicData: userOptions,
           
            default: null,
            span: 12,
            judge: (form) => form.imageType === 'PERSONAL'
        },
        { 
            type: "upload", 
            label: "图片上传", 
            prop: "imageUrl", 
            fileType: "img",
            path: "competition",
            uploadType: "ali",
            default: null,
            span: 24
        },
        { 
            type: "input", 
            label: "图片标题", 
            prop: "title", 
            placeholder: "请输入图片标题",
            max: 100,
            default: null,
            span: 12
        },{ 
            type: "input", 
            label: "图片描述", 
            prop: "description", 
            placeholder: "请输入图片描述",
            default: null,
            span: 24
        },
        { 
            type: "radio", 
            label: "状态", 
            prop: "status", 
            dicData: [
                { label: '启用', value: 'ACTIVE' },
                { label: '禁用', value: 'INACTIVE' }
            ],
            default: 'ACTIVE',
            span: 12
        }
        
    ],
    rules: {
        imageType: [{ required: true, message: '请选择图片类型', trigger: 'change' }],
        imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }],
        competitionId: [
            {
                required: true,
                message: '请选择赛事',
                validator: (rule, value, callback) => {
                    const currentForm = form.value;
                    if (currentForm.imageType === 'HIGHLIGHT' && !value) {
                        callback(new Error('现场集锦必须选择赛事'));
                    } else {
                        callback();
                    }
                },
                trigger: 'change'
            }
        ],
        participantId: [
            {
                required: true,
                message: '请选择用户',
                validator: (rule, value, callback) => {
                    const currentForm = form.value;
                    if (currentForm.imageType === 'PERSONAL' && !value) {
                        callback(new Error('个人照片必须选择用户'));
                    } else {
                        callback();
                    }
                },
                trigger: 'change'
            }
        ]
    }
})

// 生命周期
onMounted(() => {
    getData()
    loadCompetitionOptions()
    loadUserOptions()
})

/**
 * 获取数据
 */
const getData = async () => {
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            ...queryPramas.value
        }
        const res = await getCompetitionImageListApi(params)
        if (res.code === 200) {
            tableData.value = res.data.records || []
            total.value = res.data.total || 0
        }
    } catch (error) {
        console.error('获取数据失败:', error)
        ElMessage.error('获取数据失败')
    }
}

/**
 * 加载赛事选项
 */
const loadCompetitionOptions = async () => {
    try {
        const res = await getAllCompetitionApi()
        if (res.code === 200) {
            competitionOptions.value = res.data.map(i=>{
             return {  label: i.name,
                value: i.id }
            }) || []
        }
    } catch (error) {
        console.error('获取赛事列表失败:', error)
    }
}

/**
 * 加载用户选项
 */
const loadUserOptions = async () => {
    try {
        const res = await getAllUsersApi()
        if (res.code === 200) {
            userOptions.value = res.data.map(user => ({
                label: user.nickname || user.username,
                value: user.id
            })) || []
        }
    } catch (error) {
        console.error('获取用户列表失败:', error)
    }
}

/**
 * 获取赛事名称
 */
const getCompetitionName = (competitionId) => {
    const competition = competitionOptions.value.find(item => item.id === competitionId)
    return competition ? competition.name : competitionId
}

/**
 * 筛选查询
 */
const handleSearch = (val) => {
    pageNum.value = 1
    queryPramas.value = val
    getData()
}

/**
 * 分页查询
 */
const handleChange = (val) => {
    pageNum.value = val.page
    pageSize.value = val.limit
    getData()
}

/**
 * 表格左侧按钮
 */
const handleHeader = (key) => {
    switch (key) {
        case "add":
            form.value = {
                status: 'ACTIVE',
                imageType: 'HIGHLIGHT'
            }
            visible.value = true
            break
    }
}

/**
 * 操作选中
 */
const handleMenu = (val) => {
    const { index, row, value } = val
    switch (value) {
        case "detail":
            currentDetail.value = { ...row }
            detailVisible.value = true
            break
        case "edit":
            form.value = { ...row }
            visible.value = true
            break
        case "delete":
            ElMessageBox.confirm('是否删除该图片？', '提示', { 
                confirmButtonText: '确认', 
                cancelButtonText: '取消', 
                type: 'warning' 
            }).then(() => {
                delCompetitionImageApi(row.id).then(res => resMsg(res))
            }).catch(() => {})
            break
    }
}

/**
 * 表单保存
 */
const handleSave = (val) => {
    const formData = { ...val }
    if (val.id) {
        editCompetitionImageApi(formData, formData.id).then(res => resMsg(res))
    } else {
        addCompetitionImageApi(formData).then(res => resMsg(res))
    }
}

/**
 * 响应消息处理
 */
const resMsg = (res) => {
    if (res.code === 200) {
        ElMessage.success(res.msg || '操作成功')
        visible.value = false
        getData()
    } else {
        ElMessage.error(res.msg || '操作失败')
    }
}



/**
 * 表单变化处理
 */
const handleFormChange = (val) => {
    const { prop, form } = val;
    // 当图片类型改变时，清空相关字段
    if (prop === 'imageType') {
        if (val.value === 'HIGHLIGHT') {
            form.participantId = '';
        } else if (val.value === 'PERSONAL') {
            form.competitionId = '';
        }
    }
}

const selectData = (selection) => {
    // 处理选中数据
}

// CustomInfo配置项 - 使用计算属性支持条件显示
const optionInfo = computed(() => ({
    dialogClass: 'dialog_lg',
    labelWidth: '30',
    tableInfoItem: [
        {
            title: '基本信息',
            column: 2,
            infoData: [
                { 
                    type: 'text', 
                    label: '图片标题', 
                    prop: 'title', 
                    isShow: true 
                },
                { 
                    type: 'custom', 
                    label: '图片类型', 
                    prop: 'imageType', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '关联赛事', 
                    prop: 'competitionId_dictText', 
                    isShow: currentDetail.value?.imageType === 'HIGHLIGHT'
                },
                { 
                    type: 'text', 
                    label: '参赛人', 
                    prop: 'participantId_dictText', 
                    isShow: currentDetail.value?.imageType === 'PERSONAL'
                },
                { 
                    type: 'custom', 
                    label: '状态', 
                    prop: 'status', 
                    isShow: true 
                }
            ]
        },
        {
            title: '图片预览',
            column: 1,
            infoData: [
                { 
                    type: 'img', 
                    label: '图片', 
                    prop: 'imageUrl', 
                    imgWidth: 400,
                    fit: 'contain',
                    isShow: !!currentDetail.value?.imageUrl
                }
            ]
        },
        {
            title: '图片描述',
            column: 1,
            infoData: [
                { 
                    type: 'text', 
                    label: '描述内容', 
                    prop: 'description', 
                    isShow: !!currentDetail.value?.description
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
}))
</script>

<style scoped>
/* 图片管理页面样式 */


</style>