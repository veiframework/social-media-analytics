<template>
    <div>
        <!-- 报名管理表格 -->
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
                    <el-tag 
                        :type="row.status=='PAID' ? 'success' : (row.status=='PENDING' ? 'warning' : 'danger')"
                    >
                        {{ getStatusText(row.status) }}
                    </el-tag>
                </template>
            </template>
        </CustomTable>

        <!-- 详情弹窗 -->
        <CustomInfo 
            :option="optionInfo" 
            :visible="infoVisible" 
            :rowData="rowData" 
            @cancel="infoVisible = false"
        />
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listRegistration, getRegistration, updateRegistrationStatus } from '@/api/registration'
import { getDicts } from '@/api/system/dict/data'
import CustomTable from "@/components/CustomTable";
import CustomInfo from "@/components/CustomInfo";
const route = useRoute()
const router = useRouter()

// 页面数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 查询参数
const queryParams = ref({
    groupId: route.query.groupId || null,
    competitionId: route.query.competitionId || null
})

// 字典数据
const statusDict = ref([])

// 详情弹窗
const infoVisible = ref(false)
const rowData = ref({})

// 获取字典数据
const getDict = async () => {
    try {
        const statusRes = await getDicts('registration_status')
        statusDict.value = statusRes.data.map(i=>{
            return {
                label: i.dictLabel,
                value: i.dictValue
            }
        }) || []
    } catch (error) {
        console.error('获取字典数据失败:', error)
    }
}

// 获取状态文本
const getStatusText = (status) => {
    const dict = statusDict.value.find(item => item.value === status)
    return dict ? dict.label : status
}

// 获取数据
const getData = async () => {
    loading.value = true
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            status: "PAID",
            ...queryParams.value
        }
        const response = await listRegistration(params)
        tableData.value = response.data.records || []
        total.value = response.data.total || 0
    } catch (error) {
        console.error('获取数据失败:', error)
        ElMessage.error('获取数据失败')
    } finally {
        loading.value = false
    }
}

// 搜索处理
const handleSearch = (searchParams) => {
    queryParams.value = { ...queryParams.value, ...searchParams }
    pageNum.value = 1
    getData()
}

// 分页处理
const handleChange = (page) => {
    pageNum.value = page.page;
    pageSize.value = page.limit;
    getData()
}

// 表头处理
const handleHeader = (data) => {
    console.log('表头操作:', data)
}

// 选择数据
const selectData = (data) => {
    console.log('选择数据:', data)
}

// 菜单操作处理
const handleMenu = async (val) => {
   
    const { index, row, value } = val;
    switch (value) {
        case 'detail':
            await showDetail(row.id)
            break
        case 'images':
            // 跳转到个人赛事图片管理页面
            router.push({
                path: '/competition/registration/images',
                query: {
                    registrationId: row.id,
                    userId: row.userId,
                    participantName: row.participantName,
                    competitionId: row.competitionId,
                    competitionName: row.competitionId_dictText
                }
            })
            break
        case 'refund':
            await handleRefund(row)
            break
    }
}

// 显示详情
const showDetail = async (id) => {
    try {
        const response = await getRegistration(id)
        rowData.value = response.data || {}
        infoVisible.value = true
    } catch (error) {
        console.error('获取详情失败:', error)
        ElMessage.error('获取详情失败')
    }
}

// 处理退款
const handleRefund = async (row) => {
    try {
        console.log(row)
        await ElMessageBox.confirm(
            `确认为报名记录"${row.participantName}"退款吗？`,
            '确认操作',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        
        await updateRegistrationStatus({
            id: row.id,
            status: 'REFUNDED'
        })
        
        ElMessage.success('操作成功')
        getData()
    } catch (error) {
        if (error !== 'cancel') {
            console.error('退款操作失败:', error)
            ElMessage.error('操作失败')
        }
    }
}

// 表格配置
const option = reactive({
    showSearch: true,
    searchLabelWidth: 90,
    /** 搜索字段配置项 */
    searchItem: [
        { 
            type: "input", 
            label: "参赛姓名", 
            prop: "participantName", 
            placeholder: "请输入参赛青少年姓名",
            max: 50, 
            default: null 
        },
        { 
            type: "input", 
            label: "家长手机", 
            prop: "guardianPhone", 
            placeholder: "请输入家长手机号",
            max: 11, 
            default: null 
        },
        {
            type: "select", 
            label: "报名状态", 
            prop: "status", 
            default: null, 
            filterable: true,
            dicData: statusDict
        } 
    ],
    /** 表格顶部左侧 button 配置项 */
    headerBtn: [],
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
            type: 'text',
            label: '参赛青少年姓名', 
            prop: 'participantName', 
            width: 120, 
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '年龄', 
            prop: 'participantAge', 
            width: 80,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '身份证号', 
            prop: 'participantIdCard', 
            width: 160,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '家长姓名', 
            prop: 'guardianName', 
            width: 100,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '家长手机号', 
            prop: 'guardianPhone', 
            width: 120,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '赛事名称', 
            prop: 'competitionId_dictText', 
            width: 150,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '分组名称', 
            prop: 'groupId_dictText', 
            width: 120,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'custom',
            label: '报名状态', 
            prop: 'status', 
            width: 100,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '支付金额', 
            prop: 'paymentAmount', 
            width: 100,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '支付时间', 
            prop: 'paymentTime', 
            width: 150,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '比赛日期', 
            prop: 'competitionDate', 
            width: 120,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '比赛场次', 
            prop: 'competitionSession', 
            width: 120,
            fixed: false,
            sortable: false,
            isShow: true
        },
        { 
            type: 'text',
            label: '报名时间', 
            prop: 'createTime', 
            width: 150,
            fixed: false,
            sortable: true,
            isShow: true
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
            icon: 'View', 
            label: '详情', 
            value: 'detail' 
        },
        { 
            type: 'success', 
            isShow: true, 
            icon: 'Picture', 
            label: '赛事图片', 
            value: 'images' 
        },
        { 
            type: 'danger', 
            isShow: true,
            judge: (row) => { return row.status !== 'REFUNDED' }, 
            icon: 'RefreshLeft', 
            label: '退款', 
            value: 'refund' 
        }
    ],
    /** page 分页配置项 */
    isShowPage: true
})

// 详情配置项
const optionInfo = reactive({
    dialogClass: 'dialog_lg',
    labelWidth: '140px',
    /** 表格详情配置项 */
    tableInfoItem: [
        {
            title: '基本信息', 
            column: 2, 
            infoData: [
                { 
                    type: 'tag', 
                    label: '报名状态', 
                    prop: 'status', 
                    isShow: true,
                    dicData: [
                        { value: 'PENDING', label: '待支付', type: 'warning' },
                        { value: 'PAID', label: '已支付', type: 'success' },
                        { value: 'REFUNDED', label: '已退款', type: 'danger' }
                    ]
                }
            ]
        },
        {
            title: '参赛者信息', 
            column: 2, 
            infoData: [
                { 
                    type: 'text', 
                    label: '参赛青少年姓名', 
                    prop: 'participantName', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '参赛青少年年龄', 
                    prop: 'participantAge', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '身份证号', 
                    prop: 'participantIdCard', 
                    isShow: true,
                    span: 2
                },
                 
                { 
                    type: 'text', 
                    label: '家长姓名', 
                    prop: 'guardianName', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '家长手机号', 
                    prop: 'guardianPhone', 
                    isShow: true,
                    span: 2
                }
            ]
        },
        {
            title: '赛事信息', 
            column: 2, 
            infoData: [
                { 
                    type: 'text', 
                    label: '赛事名称', 
                    prop: 'competitionId_dictText', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '分组名称', 
                    prop: 'groupId_dictText', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '比赛日期', 
                    prop: 'competitionDate', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '比赛场次', 
                    prop: 'competitionSession', 
                    isShow: true 
                }
            ]
        },
        {
            title: '支付信息', 
            column: 2, 
            infoData: [
                { 
                    type: 'text', 
                    label: '支付金额', 
                    prop: 'paymentAmount', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '支付时间', 
                    prop: 'paymentTime', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '退款原因', 
                    prop: 'refundReason', 
                    isShow: true,
                    span: 2
                }
            ]
        },
        {
            title: '时间信息', 
            column: 2, 
            infoData: [
                { 
                    type: 'text', 
                    label: '报名时间', 
                    prop: 'createTime', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '更新时间', 
                    prop: 'updateTime', 
                    isShow: true 
                }
            ]
        }
    ]
})

// 初始化
onMounted(async () => {
    await getDict()
    getData()
})
</script>

<style scoped>
/* 自定义样式 */
</style>