<template>
  <div>
    <!-- 员工分组管理表格 -->
    <CustomTable
        :data="tableData"
        :option="option"
        @refresh="getData"
        @search="handleSearch"
        :pageNum="pageNum"
        :pageSize="pageSize"
        :total="total"
        @headerchange="handleHeader"
        @menuChange="handleMenu"
        @currentChange="handleChange"
        @selectData="handleSelectData"
    >
      <template #table-top></template>
      <template #table-custom="{ row, prop, index }"></template>
    </CustomTable>

    <!-- 批量关联员工表单弹窗 -->
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

    <!-- 钉钉群通知配置弹窗 -->
    <CustomDialog
        :form="webhookForm"
        :option="webhookOption"
        :visible="webhookVisible"
        :loading="webhookLoading"
        @cancel="webhookVisible = false"
        @save="handleWebhookSave"
    />
  </div>
</template>

<script setup name="GroupUser">
import {ref, reactive, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {groupUserApi} from '@/api/group-user'
import CustomTable from "@/components/CustomTable"
import CustomDialog from "@/components/CustomDialog"
import CustomInfo from "@/components/CustomInfo"

// 导入告警相关API
import {getAlarmWebhookApi, addAlarmWebhookApi} from '@/api/alarm'

// 页面数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 查询参数
const queryParams = ref({})

// 用户下拉列表数据
const userOptions = ref([])

// 表单数据
const form = ref({})
const visible = ref(false)

// 详情数据
const infoVisible = ref(false)
const rowData = ref({})

// 钉钉群通知配置相关状态
const webhookForm = ref({})
const webhookVisible = ref(false)
const webhookLoading = ref(false)

// 表格配置项
const option = reactive({
  showSearch: false,
  searchLabelWidth: 90,
  /** 搜索字段配置项 */
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    {key: "batchAdd", text: "批量关联员工", icon: "Plus", isShow: true, type: "primary", disabled: false},
    {key: "dingdingWebhook", text: "钉钉群通知配置", icon: "Bell", isShow: true, type: "success", disabled: false},
  ],
  /** 表格顶部右侧 toobar 配置项 */
  toolbar: {isShowToolbar: true, isShowSearch: false},
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
      label: '组员',
      prop: 'userId_dictText',
      width: 150,
      fixed: false,
      sortable: false,
      isShow: true
    },


  ],
  /** 操作菜单配置项 */
  menu: {
    isShow: true,
    width: 120,
    fixed: 'right'
  },
  menuItemBtn: [

    {
      type: 'danger',
      isShow: true,
      icon: 'Delete',
      label: '删除',
      value: 'delete'
    }
  ],
  /** page 分页配置项 */
  isShowPage: false
})

// 表单配置项
const optionDialog = reactive({
  dialogTitle: '批量关联员工',
  dialogClass: 'dialog_md',
  labelWidth: '120px',
  formitem: [
    {
      type: "select",
      label: "选择员工",
      prop: "userIds",
      multiple: true,
      dicData: userOptions,
      placeholder: "请选择要关联的员工",
      default: null
    }
  ],
  rules: {
    userIds: [{required: true, message: '请选择要关联的员工', trigger: 'change'}]
  }
})

// 钉钉群通知配置表单
const webhookOption = reactive({
  dialogTitle: '钉钉群通知配置',
  dialogClass: 'dialog_md2',
  labelWidth: '120px',
  formitem: [
    {
      type: "input",
      label: "链接地址",
      prop: "webhook",
      placeholder: "请输入钉钉群机器人webhook地址",
      default: null
    },
    {
      type: "input",
      label: "关键字",
      prop: "token",
      placeholder: "请输入钉钉群机器人关键字",
      default: null
    },
    {
      type: "radio",
      label: "状态",
      prop: "disabled",
      placeholder: "请选择状态",
      default: '0',
      dicData: [
        {label: '启用', value: '0'},
        {label: '禁用', value: '1'}
      ]
    }
  ],
  rules: {
    webhook: [{required: true, message: '请输入webhook地址', trigger: 'blur'}],
    disabled: [{required: true, message: '请选择状态', trigger: 'blur'}],
  }
})

// 详情配置项
const optionInfo = reactive({
  dialogClass: 'dialog_md',
  labelWidth: '120px',
  /** 表格详情配置项 */
  tableInfoItem: [
    {
      title: '基本信息',
      column: 1,
      infoData: [
        {
          type: 'text',
          label: 'ID',
          prop: 'id',
          isShow: true
        },
        {
          type: 'text',
          label: '用户',
          prop: 'userId',
          isShow: true
        },
        {
          type: 'text',
          label: '父用户',
          prop: 'parentUserId',
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

// 获取表格数据
const getData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryParams.value
    }

    const response = await groupUserApi().getAll(params)
    if (response && response.data) {
      tableData.value = response.data
      total.value = response.data.length // 后端没有返回total，使用数据长度代替
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取分组员工数据失败:', error)
    ElMessage.error('获取分组员工数据失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取用户下拉列表
const getUserSelector = async () => {
  try {
    const response = await groupUserApi().getUserSelector()
    if (response && response.data) {
      userOptions.value = response.data.map(user => ({
        label: user.nickName,
        value: user.userId
      }))
    }
  } catch (error) {
    console.error('获取用户下拉列表失败:', error)
    ElMessage.error('获取用户下拉列表失败')
  }
}

// 搜索处理
const handleSearch = (searchParams) => {
  queryParams.value = {...queryParams.value, ...searchParams}
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
const handleSelectData = (data) => {
  console.log('选择数据:', data)
}

// 处理钉钉群通知配置保存
const handleWebhookSave = async (data) => {
  try {
    webhookLoading.value = true
    // 确保type字段为dingding_robot
    data.type = 'dingding_robot'
    console.log('钉钉群通知配置:', data)
    const response = await addAlarmWebhookApi(data)

    if (response && response.code === 200) {
      ElMessage.success('保存成功')
      webhookVisible.value = false
    } else {
      ElMessage.error(response?.message || '保存失败')
    }
  } catch (error) {
    console.error('保存钉钉群通知配置失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    webhookLoading.value = false
  }
}

// 获取钉钉群通知配置
const getDingdingWebhook = async () => {
  try {
    const response = await getAlarmWebhookApi({type: 'dingding_robot'})
    if (response && response.code === 200) {
      webhookForm.value = response.data || {};

    } else {

    }
  } catch (error) {
    console.error('获取钉钉群通知配置失败:', error)
    ElMessage.error('获取钉钉群通知配置失败')
    webhookForm.value = {type: 'dingding_robot'};
  }
}

// 头部操作处理
const handleHeader = (key) => {
  switch (key) {
    case 'batchAdd':
      form.value = {userIds: []}
      visible.value = true
      getUserSelector()
      break
    case 'delete':
      handleDelete()
      break
    case 'dingdingWebhook':
      // 获取钉钉群通知配置并显示弹窗
      getDingdingWebhook().then(() => {
        webhookVisible.value = true;
      })
      break
  }
}

// 菜单操作处理
const handleMenu = async (val) => {
  const {index, row, value} = val
  switch (value) {
    case 'detail':
      await showDetail(row)
      break
    case 'delete':
      await handleDelete([row.id])
      break
  }
}

// 显示详情
const showDetail = async (row) => {
  rowData.value = {...row}
  infoVisible.value = true
}

// 处理删除
const handleDelete = async (ids = []) => {
  // 如果没有传入ids，获取选中的行
  if (ids.length === 0) {
    const selectedRows = tableData.value.filter(row => row._checked)
    if (selectedRows.length === 0) {
      ElMessage.warning('请选择要删除的员工')
      return
    }
    ids = selectedRows.map(row => row.id)
  }

  try {
    await ElMessageBox.confirm('确定要删除选中的员工吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await groupUserApi().delete(ids.join(','))
    ElMessage.success('删除成功')
    getData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 保存表单
const handleSave = async (val) => {
  if (loading.value) return
  loading.value = true
  try {
    const data = {...val}
    await groupUserApi().batchAdd(data)
    ElMessage.success('批量关联员工成功')
    visible.value = false
    getData()
  } catch (error) {
    console.error('批量关联员工失败:', error)
    ElMessage.error('批量关联员工失败')
  } finally {
    loading.value = false
  }
}

/**
 * 初始化数据
 */
const init = () => {
}

// 初始化
init()
</script>

<style lang="scss" scoped>
.group-user-container {
  padding: 20px;
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.content-wrapper {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>