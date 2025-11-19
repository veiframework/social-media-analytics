<template>
  <div>
    <!-- 协议管理表格 -->
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
        <!-- 由于使用了 tag 类型和 dicData，不再需要自定义模板 -->
      </template>
    </CustomTable>

    <!-- 协议表单弹窗 -->
    <CustomDialog 
      :form="form" 
      :option="optionDialog" 
      :visible="visible" 
      @cancel="visible = false" 
      @save="handleSave"
    />

    <!-- 详情弹窗 -->
    <CustomInfo 
      :option="optionInfo" 
      :visible="infoVisible" 
      :rowData="rowData" 
      @cancel="infoVisible = false"
    />
  </div>
</template>

<script setup name="Agreement">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getAgreementListApi, 
  getAgreementApi, 
  addAgreementApi, 
  editAgreementApi, 
  delAgreementApi,
  exportAgreementApi
} from '@/api/agreement'
import { getDicts } from '@/api/system/dict/data'
import CustomTable from "@/components/CustomTable"
import CustomDialog from "@/components/CustomDialog"
import CustomInfo from "@/components/CustomInfo"

// 页面数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 查询参数
const queryParams = ref({})

// 字典数据
const typeDict = ref([])

// 表单数据
const form = ref({})
const visible = ref(false)

// 详情弹窗
const infoVisible = ref(false)
const rowData = ref({})

// 获取字典数据
const getDict = async () => {
  try {
    const typeRes = await getDicts('agreement_type')
    
    typeDict.value = typeRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue
    })) || []
  } catch (error) {
    console.error('获取字典数据失败:', error)
    // 如果字典获取失败，使用默认值
    typeDict.value = [
      { label: '用户协议', value: 'USER_AGREEMENT' },
      { label: '隐私政策', value: 'PRIVACY_POLICY' },
      { label: '服务条款', value: 'SERVICE_TERMS' }
    ]
  }
}

// 由于使用了 dicData 自动处理标签，移除手动处理函数

// 获取数据
const getData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryParams.value
    }
    
    const response = await getAgreementListApi(params)
    if (response.code === 200) {
      tableData.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取协议列表失败')
    }
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
  pageNum.value = page.page
  pageSize.value = page.limit
  getData()
}

// 选择数据
const selectData = (data) => {
  console.log('选择数据:', data)
}

// 头部操作处理
const handleHeader = (key) => {
  switch (key) {
    case 'add':
      form.value = {}
      visible.value = true
      break
    case 'export':
      handleExport()
      break
  }
}

// 新增协议功能已合并到 handleHeader 中

// 导出协议
const handleExport = async () => {
  try {
    const params = { ...queryParams.value }
    const response = await exportAgreementApi(params)
    
    // 创建下载链接
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `协议列表_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 菜单操作处理
const handleMenu = async (val) => {
  const { index, row, value } = val
  switch (value) {
    case 'detail':
      await showDetail(row.id)
      break
    case 'edit':
      await handleEdit(row.id)
      break
    case 'delete':
      await handleDelete(row.id)
      break
  }
}

// 显示详情
const showDetail = async (id) => {
  try {
    const response = await getAgreementApi(id)
    if (response.code === 200) {
      rowData.value = response.data
      infoVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取协议详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 编辑协议
const handleEdit = async (id) => {
  try {
    const response = await getAgreementApi(id)
    if (response.code === 200) {
      form.value = { ...response.data }
      visible.value = true
    } else {
      ElMessage.error(response.msg || '获取协议信息失败')
    }
  } catch (error) {
    console.error('获取协议信息失败:', error)
    ElMessage.error('获取协议信息失败')
  }
}

// 删除协议
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该协议吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await delAgreementApi(id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getData()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 保存协议
const handleSave = (val) => {
  const formData = { ...val }
  if (val.id) {
    editAgreementApi(formData).then(res => resMsg(res))
  } else {
    addAgreementApi(formData).then(res => resMsg(res))
  }
}

// 响应消息处理
const resMsg = (res) => {
  if (res.code === 200) {
    ElMessage.success(res.msg || '操作成功')
    visible.value = false
    getData()
  } else {
    ElMessage.error(res.msg || '操作失败')
  }
}

// 表格配置项
const option = reactive({
  showSearch: true,
  searchLabelWidth: 90,
  /** 搜索字段配置项 */
  searchItem: [
    {
      type: "select",
      label: "协议类型",
      prop: "type",
      default: null,
      filterable: true,
      dicData: typeDict
    }
  ],
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    { key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false },
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
      type: 'tag',
      label: '协议类型',
      prop: 'type',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: typeDict
    },
    {
      type: 'text',
      label: '内容摘要',
      prop: 'content',
      width: 300,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    },
    {
      type: 'text',
      label: '创建时间',
      prop: 'createTime',
      width: 180,
      fixed: false,
      sortable: true,
      isShow: true
    },
    {
      type: 'text',
      label: '更新时间',
      prop: 'updateTime',
      width: 180,
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
  dialogTitle: '协议信息',
  dialogClass: 'dialog_lg',
  labelWidth: '120px',
  formitem: [
    {
      type: "select",
      label: "协议类型",
      prop: "type",
      default: null,
      dicData: typeDict
    },
    {
      type: "rich",
      label: "协议内容",
      prop: "content",
      placeholder: "请输入协议内容",
      uploadType: "ali",
      span: 24,
      default: null
    },
    
  ],
  rules: {
    type: [{ required: true, message: '请选择协议类型', trigger: 'change' }],
    content: [{ required: true, message: '请输入协议内容', trigger: 'blur' }]
  }
})

// 详情配置项
const optionInfo = reactive({
  dialogClass: 'dialog_lg',
  labelWidth: '120px',
  /** 表格详情配置项 */
  tableInfoItem: [
    {
      title: '基本信息', 
      column: 1, 
      infoData: [
        { 
          type: 'tag', 
          label: '协议类型',
          prop: 'type', 
          isShow: true,
          dicData: typeDict
        }
      ]
    },
    {
      title: '协议内容', 
      column: 1, 
      infoData: [
        { 
          type: 'html', 
          label: '协议内容', 
          prop: 'content', 
          isShow: true,
          span: 2
        },
        
      ]
    },
    {
      title: '时间信息', 
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
        }
      ]
    }
  ]
})

/**
 * 初始化数据
 */
const init = () => {
  getDict()
  getData()
}

// 初始化
init()
</script>

<style scoped>
/* 自定义样式 */
</style>
