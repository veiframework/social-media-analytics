<template>
  <div>
    <!-- 告警器管理表格 -->
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

    <!-- 告警器表单弹窗 -->
    <CustomDialog 
      :form="form" 
      :option="optionDialog" 
      :visible="visible" 
      @cancel="visible = false" 
      @save="handleSave"
    >
      <template #custom-item="{ prop, form }">
        <div v-if="prop == 'msgFields'">
          <div class="msg-field-container">
            <div 
              v-for="(field, index) in msgFields" 
              :key="field.id" 
              class="msg-field-item"
            >
              <div class="msg-field-index">{{ "{"+index+"}" }}</div>
              <el-input
                v-model="field.value"
                placeholder="请输入消息字段"
                clearable
                class="msg-field-input"
                required
              ></el-input>
              <el-button
                v-if="msgFields.length > 1"
                type="danger"
                icon="Delete"
                size="small"
                @click="removeMsgField(field.id)"
                class="msg-field-remove"
              ></el-button>
            </div>
            <el-button
              type="primary"
              icon="Plus"
              size="small"
              @click="addMsgField"
              class="msg-field-add"
            >
              添加字段
            </el-button>
          </div>
        </div>
      </template>
    </CustomDialog>

    <!-- 详情弹窗 -->
    <CustomInfo 
      :option="optionInfo" 
      :visible="infoVisible" 
      :rowData="rowData" 
      @cancel="infoVisible = false"
    />
  </div>
</template>

<script setup name="Alarm">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getAlarmListApi, 
  getAlarmApi, 
  addAlarmApi, 
  editAlarmApi, 
  delAlarmApi,
  exportAlarmApi
} from '@/api/alarm'
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
const typeDict = ref([
  { label: '间隔', value: 'interval' }
])

const stateDict = ref([
  { label: '正常', value: '0' },
  { label: '暂停', value: '1' }

])

// 表单数据
const form = ref({})
const visible = ref(false)

// 消息字段集合
const msgFields = ref([])

// 详情弹窗
const infoVisible = ref(false)
const rowData = ref({})

// 获取字典数据
const getDict = async () => {
  try {
    // 告警类型字典数据 - 如果后端有定义字典类型，可以取消注释以下代码
    const typeRes = await getDicts('alarm_type')
    typeDict.value = typeRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue
    })) || []

  } catch (error) {
    console.error('获取字典数据失败:', error)
  }
}

// 获取数据
const getData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryParams.value
    }
    
    const response = await getAlarmListApi(params)
    if (response.code === 200) {
      tableData.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取告警器列表失败')
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
  // 重置查询参数，只保留搜索条件
  queryParams.value = {}
  // 将有效的搜索条件添加到查询参数中
  Object.keys(searchParams).forEach(key => {
    if (searchParams[key] !== null && searchParams[key] !== undefined && searchParams[key] !== '') {
      queryParams.value[key] = searchParams[key]
    }
  })
  // 重置为第一页
  pageNum.value = 1
  getData()
}

// 分页处理
const handleChange = (page) => {
  pageNum.value = page.page || 1
  pageSize.value = page.limit || 10
  getData()
}

// 选择数据
const selectData = (data) => {
  console.log('选择数据:', data)
}

// 添加消息字段
const addMsgField = () => {
  const newId = msgFields.value.length > 0 ? Math.max(...msgFields.value.map(f => f.id)) + 1 : 0
  msgFields.value.push({ id: newId, value: '' })
}

// 删除消息字段
const removeMsgField = (id) => {
  const index = msgFields.value.findIndex(field => field.id === id)
  if (index > -1) {
    msgFields.value.splice(index, 1)
  }
}


// 头部操作处理
const handleHeader = (key) => {
  switch (key) {
    case 'add':
      // 清空表单数据，准备新增
      form.value = {
        type: 'interval', // 默认值
        startInterval: 0 // 默认初始间隔为0
      }
      // 初始化消息字段集合
      msgFields.value = []
      visible.value = true
      break
    case 'export':
      handleExport()
      break
  }
}

// 导出告警器
const handleExport = async () => {
  try {
    const params = { ...queryParams.value }
    const response = await exportAlarmApi(params)
    
    // 创建下载链接
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `告警器列表_${new Date().getTime()}.xlsx`
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
    const response = await getAlarmApi(id)
    if (response.code === 200) {
      rowData.value = response.data
      infoVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取告警器详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 编辑告警器
const handleEdit = async (id) => {
  try {
    const response = await getAlarmApi(id)
    if (response.code === 200) {
      form.value = { ...response.data }
      msgFields.value = response.data.msgFields.map((i,idx)=> {
        return {id:idx,value:i}
      }) || []
      // 初始化消息字段集合
      visible.value = true
    } else {
      ElMessage.error(response.msg || '获取告警器信息失败')
    }
  } catch (error) {
    console.error('获取告警器信息失败:', error)
    ElMessage.error('获取告警器信息失败')
  }
}

// 删除告警器
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该告警器吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await delAlarmApi(id)
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

// 保存告警器
const handleSave = (val) => {
  // 验证消息字段是否都填写了值
  const emptyFields = msgFields.value.filter(field => !field.value || field.value.trim() === '');
  if (emptyFields.length > 0) {
    ElMessage.error('消息字段不能为空，请填写所有消息字段');
    return;
  }
  
  // 处理消息字段集合，转换为字符串
  const formData = { ...val }
  formData.msgFields = msgFields.value.map(i=>i.value)
  
  if (val.id) {
    editAlarmApi(val.id, formData).then(res => resMsg(res))
  } else {
    addAlarmApi(formData).then(res => resMsg(res))
  }
}

// 响应消息处理
const resMsg = (res) => {
  if (res.code === 200) {
    ElMessage.success(res.msg || '操作成功')
    visible.value = false
    // 操作成功后刷新数据
    getData()
  } else {
    ElMessage.error(res.msg || '操作失败')
  }
}

// 表格配置项
const option = reactive({
  showSearch: false,
  searchLabelWidth: 90,
  /** 搜索字段配置项 */
  searchItem: [

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
      label: '告警类型',
      prop: 'type',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: typeDict
    },
    {
      type: 'text',
      label: '告警名称',
      prop: 'alarmName',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    },
    {
      type: 'text',
      label: '告警字段',
      prop: 'alarmField',
      width: 150,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    },
    {
      type: 'text',
      label: '告警表达式',
      prop: 'alarmExpression',
      width: 150,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    },
    {
      type: 'text',
      label: '初始间隔',
      prop: 'startInterval',
      width: 100,
      fixed: false,
      sortable: false,
      isShow: true
    },{
      type: 'text',
      label: 'cron',
      prop: 'cronExpression',
      width: 100,
      fixed: false,
      sortable: false,
      isShow: true
    },{
      type: 'tag',
      label: '状态',
      prop: 'state',
      width: 100,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: stateDict
    },
    {
      type: 'text',
      label: '创建时间',
      prop: 'createTime',
      width: 180,
      fixed: false,
      sortable: false,
      isShow: true
    },
    {
      type: 'text',
      label: '更新时间',
      prop: 'updateTime',
      width: 180,
      fixed: false,
      sortable: false,
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
  dialogTitle: '告警器信息',
  dialogClass: 'dialog_lg',
  labelWidth: '120px',
  formitem: [
    {
      type: "select",
      label: "告警类型",
      prop: "type",
      default: null,
      dicData: typeDict
    },{
      type: "radio",
      label: "状态",
      prop: "state",
      default: '0',
      dicData: stateDict
    },{
      type: "input",
      label: "cron",
      prop: "cronExpression",
      placeholder: "请输入cron表达式",
      max: 100,
      default: null
    },
    {
      type: "input",
      label: "告警名称",
      prop: "alarmName",
      placeholder: "请输入告警名称",
      default: null
    },{
      type: "input",
      label: "关键字",
      prop: "keyword",
      placeholder: "请输入关键字",
      default: null
    },{
      type: "input",
      label: "关键字值",
      prop: "keywordValue",
      placeholder: "请输入关键字值",
      default: null
    },
    {
      type: "input",
      label: "告警字段",
      prop: "alarmField",
      placeholder: "请输入告警字段",
      default: null
    },
    {
      type: "input",
      label: "告警表达式",
      prop: "alarmExpression",
      placeholder: "请输入告警表达式",
      default: null
    },
    {
      type: "input",
      label: "初始间隔",
      prop: "startInterval",
      placeholder: "请输入初始间隔",
      category: "number",
      default: null
    },
    // 消息字段改为自定义模板
    {
      type: "custom",
      label: "消息字段",
      prop: "msgFields",
      default: null
    },
    {
      type: "input",
      label: "消息模板",
      prop: "msgTemplate",
      placeholder: "请输入消息模板",
      default: null,
      category: "textarea",
      rows: 10
    }
  ],
  rules: {
    type: [{ required: true, message: '请选择告警类型', trigger: 'change' }],
    alarmName: [{ required: true, message: '请输入告警名称', trigger: 'blur' }],
    alarmField: [{ required: true, message: '请输入告警字段', trigger: 'blur' }],
    alarmExpression: [{ required: true, message: '请输入告警表达式', trigger: 'blur' }],
    msgTemplate: [{ required: true, message: '请输入消息模板', trigger: 'blur' }],
    cronExpression: [{ required: true, message: '请输入cron表达式', trigger: 'blur' }],
    state: [{ required: true, message: '请选择状态', trigger: 'blur' }]

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
      column: 2,
      infoData: [
        { 
          type: 'tag', 
          label: '告警类型',
          prop: 'type', 
          isShow: true,
          dicData: typeDict
        },{
          type: 'tag',
          label: '状态',
          prop: 'state',
          isShow: true,
          dicData: stateDict
        }, {
          type: 'text',
          label: '关键字',
          prop: 'keyword',
          isShow: true
        },{
          type: 'text',
          label: '关键字值',
          prop: 'keywordValue',
          isShow: true
        },
        { 
          type: 'text', 
          label: '告警名称', 
          prop: 'alarmName', 
          isShow: true 
        },
        { 
          type: 'text', 
          label: '告警字段', 
          prop: 'alarmField', 
          isShow: true 
        },
        { 
          type: 'text', 
          label: '告警表达式', 
          prop: 'alarmExpression', 
          isShow: true 
        },{
          type: 'text',
          label: 'cron表达式',
          prop: 'cronExpression',
          isShow: true
        },
        { 
          type: 'text', 
          label: '初始间隔', 
          prop: 'startInterval', 
          isShow: true 
        }
      ]
    },
    {
      title: '消息配置', 
      column: 1, 
      infoData: [
        { 
          type: 'text', 
          label: '消息字段', 
          prop: 'msgFields',
          isShow: true
        },
        { 
          type: 'text', 
          label: '消息模板', 
          prop: 'msgTemplate', 
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
      prop: 'creator',
      isShow: true 
    },
    {
      type: 'text',
      label: '更新人',
      prop: 'updater',
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
/* 消息字段集合样式 */
.msg-field-container {
  margin-top: 10px;
}

.msg-field-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  position: relative;
}

.msg-field-index {
  width: 30px;
  height: 30px;
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 14px;
  margin-right: 10px;
  flex-shrink: 0;
}

.msg-field-input {
  flex: 1;
}

.msg-field-remove {
  margin-left: 10px;
  flex-shrink: 0;
}

.msg-field-add {
  margin-top: 10px;
  margin-left: 40px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .msg-field-item {
    flex-wrap: wrap;
  }
  
  .msg-field-remove {
    margin-top: 5px;
    margin-left: 40px;
  }
}
</style>