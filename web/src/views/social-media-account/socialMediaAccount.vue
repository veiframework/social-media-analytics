<template>
  <div>
    <!-- 社交媒体账号管理表格 -->
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
    >
      <template #table-top></template>
      <template #table-custom="{ row, prop, index }">
        <!-- 自定义模板 -->
        <template v-if="prop == 'autoSync'">
          <el-switch v-model="row.autoSync" active-value="enable" inactive-value="disable"
                     @change="handleAutoSyncChange(row)"/>
        </template>
      </template>
    </CustomTable>

    <!-- 社交媒体账号表单弹窗 -->
    <CustomDialog
        :form="form"
        :option="optionDialog"
        :visible="visible"
        @cancel="visible = false"
        @save="handleSave"
    />

    <!-- 分享链接添加弹窗 -->
    <CustomDialog
        :form="shareLinkForm"
        :option="optionShareLink"
        :visible="shareLinkVisible"
        @cancel="shareLinkVisible = false"
        @save="handleShareLink"
    />

    <!-- 微信视频号名称 -->
    <CustomDialog
        :form="wechatVideoForm"
        :option="wechatVideoOption"
        :visible="wechatVideoVisible"
        @cancel="wechatVideoVisible = false"
        @save="handleWechatVideoForm"
    />
  </div>
</template>

<script setup name="SocialMediaAccount">
import {ref, reactive, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Link} from "@element-plus/icons-vue";
import {ElLoading} from 'element-plus'
import {
  listSocialMediaAccount,
  getSocialMediaAccount,
  addSocialMediaAccount,
  updateSocialMediaAccount,
  delSocialMediaAccount,
  syncWork,
  createByShareLink, createByWechatVideoNickname,
  updateAutoSync
} from '@/api/social-media-account'
import CustomTable from "@/components/CustomTable"
import CustomDialog from "@/components/CustomDialog"
import {getDicts} from "@/api/system/dict/data.js";
import {groupUserApi} from "@/api/group-user.js";
import Template from "@/views/base/template.vue";

// 页面数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 查询参数
const queryParams = ref({})

// 表单数据
const form = ref({})
const visible = ref(false)

const wechatVideoForm = ref({})
const wechatVideoVisible = ref(false)


// 分享链接表单数据
const shareLinkForm = ref({})
const shareLinkVisible = ref(false)
let loadingOpt = {
  lock: true,
  text: '加载中……',
  background: 'rgba(0, 0, 0, .1)',
}

const socialMediaTypeDict = ref([])
const syncWorkStatusDict = ref([])
const socialMediaAccountTypeDict = ref([])
const userDict = ref([])
const autoSyncDict = ref([])


const getUserList3 = async () => {
  const res = await groupUserApi().getUserQuerySelector()
  userDict.value = res.data.map(i => ({
    label: i.userId_dictText,
    value: i.userId,
  }))
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

    const response = await listSocialMediaAccount(params)
    if (response.code === 200) {
      tableData.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取社交媒体账号列表失败')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const getDict = async () => {
  try {
    const typeRes = await getDicts('social_media_platform')
    const syncDictRes = await getDicts('sync_work_status')
    const socialMediaAccountTypeRes = await getDicts('social_media_account_type')
    const autoSyncDictRes = await getDicts('social_media_account_auto_sync')

    socialMediaTypeDict.value = typeRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    syncWorkStatusDict.value = syncDictRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    socialMediaAccountTypeDict.value = socialMediaAccountTypeRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    autoSyncDict.value = autoSyncDictRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
  } catch (error) {
    console.error('获取字典数据失败:', error)
  }
}
const handleAutoSyncChange = async (value) => {
  if (!value.id) {
    return
  }
  await updateAutoSync(value)
}
// 搜索处理
const handleSearch = (searchParams) => {
  queryParams.value = searchParams
  pageNum.value = 1
  getData()
}

// 分页处理
const handleChange = (page) => {
  pageNum.value = page.page
  pageSize.value = page.limit
  getData()
}

// 头部操作处理
const handleHeader = (key) => {
  switch (key) {
    case 'add':
      form.value = {}
      visible.value = true
      break
    case 'shareLink':
      shareLinkForm.value = {}
      shareLinkVisible.value = true
      break
    case 'wechatVideo':
      wechatVideoForm.value = {}
      wechatVideoVisible.value = true
      break
  }
}

// 菜单操作处理
const handleMenu = async (val) => {
  const {index, row, value} = val
  switch (value) {
    case 'edit':
      await handleEdit(row.id)
      break
    case 'delete':
      await handleDelete(row.id)
      break
    case 'syncWork':
      await handleSyncWork(row.id)
      break
  }
}

// 编辑社交媒体账号
const handleEdit = async (id) => {
  try {
    const response = await getSocialMediaAccount(id)
    if (response.code === 200) {
      form.value = {...response.data}
      visible.value = true
    } else {
      ElMessage.error(response.msg || '获取社交媒体账号信息失败')
    }
  } catch (error) {
    console.error('获取社交媒体账号信息失败:', error)
    ElMessage.error('获取社交媒体账号信息失败')
  }
}

// 删除社交媒体账号
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该社交媒体账号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await delSocialMediaAccount(id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      await getData()
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

const handleSyncWork = async (accountId) => {
  try {
    await ElMessageBox.confirm('确定要同步作品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await syncWork(accountId)
    if (response.code === 200) {
      ElMessage.success('同步作品需要时间，请稍后前往《作品管理》查看~')
      await getData()
    } else {
      ElMessage.error(response.msg || '同步失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('同步失败:', error)
      ElMessage.error('同步失败')
    }
  }
}

// 保存社交媒体账号
const handleSave = async (val) => {
  const formData = {...val}
  let loading = ElLoading.service(loadingOpt)
  try {
    let res;
    if (val.id) {
      res = await updateSocialMediaAccount(formData)
    } else {
      res = await addSocialMediaAccount(formData)
    }
    if (res.code === 200) {
      ElMessage.success(res.msg || '操作成功')
      visible.value = false
      await getData();
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } finally {
    loading.close()
  }
}

// 处理分享链接
const handleShareLink = async (val) => {
  let loading = ElLoading.service(loadingOpt)
  try {
    const formData = {...val}
    let res = await createByShareLink(formData)
    if (res.code === 200) {
      ElMessage.success(res.msg || '通过主页分享链接添加成功')
      shareLinkVisible.value = false
      await getData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } finally {
    loading.close()
  }
}

// 通过微信视频号名称添加
const handleWechatVideoForm = async (val) => {
  let loading = ElLoading.service(loadingOpt)
  try {
    const formData = {...val}
    let res = await createByWechatVideoNickname(formData)
    if (res.code === 200) {
      ElMessage.success(res.msg || '通过微信视频号名称添加成功')
      wechatVideoVisible.value = false
      await getData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } finally {
    loading.close()
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
      label: "员工账号",
      prop: "userId",
      default: null,
      filterable: true,
      dicData: userDict
    },
    {
      type: "select",
      label: "账号类型",
      prop: "type",
      default: null,
      filterable: true,
      dicData: socialMediaAccountTypeDict
    }, {
      type: "select",
      label: "平台类型",
      prop: "platformId",
      default: null,
      filterable: true,
      dicData: socialMediaTypeDict
    }, {
      type: "select",
      label: "同步状态",
      prop: "syncWorkStatus",
      default: null,
      filterable: true,
      dicData: syncWorkStatusDict
    },
    {
      type: "input",
      label: "用户昵称",
      prop: "nickname",
      default: null,
      placeholder: "请输入用户昵称"
    }
  ],
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    // {key: "add", text: "新增账号", icon: "Plus", isShow: true, type: "primary", disabled: false},
    {key: "shareLink", text: "通过主页分享链接添加", icon: "Link", isShow: true, type: "primary", disabled: false},
    {key: "wechatVideo", text: "通过微信视频号名称添加", icon: "Link", isShow: true, type: "success", disabled: false}

  ],
  /** 表格顶部右侧 toobar 配置项 */
  toolbar: {isShowToolbar: true, isShowSearch: true},
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
      label: '平台',
      prop: 'platformId',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: socialMediaTypeDict
    },
    {
      type: 'text',
      label: '社交平台用户ID',
      prop: 'uid',
      width: 150,
      fixed: false,
      sortable: false,
      isShow: true
    }, {
      type: 'text',
      label: '员工',
      prop: 'userId_dictText',
      width: 180,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    },
    {
      type: 'text',
      label: '用户昵称',
      prop: 'nickname',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    }, {
      type: 'tag',
      label: '账号类型',
      prop: 'type',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: socialMediaAccountTypeDict
    }, {
      type: 'tag',
      label: '作品同步状态',
      prop: 'syncWorkStatus',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: syncWorkStatusDict
    }, {
      type: 'custom',
      label: '自动同步',
      prop: 'autoSync',
      width: 80,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: autoSyncDict
    },
    {
      type: 'text',
      label: '创建时间',
      prop: 'createTime',
      width: 180,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true,
      formatter: (row, column, cellValue) => {
        return cellValue ? new Date(cellValue).toLocaleString() : '-';
      }
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
      type: 'primary',
      isShow: true,
      icon: 'Edit',
      label: '编辑',
      value: 'edit',
      judge: (row) => {
        return row.syncWorkStatus !== '1'
      },
    },
    {
      type: 'danger',
      isShow: true,
      icon: 'Delete',
      label: '删除',
      value: 'delete',
      judge: (row) => {
        return row.syncWorkStatus !== '1'
      },
    },
    {
      type: 'primary',
      isShow: true,
      icon: 'Delete',
      label: '同步作品',
      judge: (row) => {
        return row.syncWorkStatus !== '1'
      },
      value: 'syncWork'
    }
  ],
  /** page 分页配置项 */
  isShowPage: true
})

// 表单配置项
const optionDialog = reactive({
  dialogTitle: '社交媒体账号信息',
  dialogClass: 'dialog_lg',
  labelWidth: '180px',
  formitem: [

    {
      type: "select",
      label: "平台",
      prop: "platformId",
      placeholder: "请输入平台ID",
      dicData: socialMediaTypeDict
    }, {
      type: "select",
      label: "账号类型",
      prop: "type",
      placeholder: "请选择账号类型",
      dicData: socialMediaAccountTypeDict
    },
    {
      type: "input",
      label: "社交平台用户ID",
      prop: "uid",
      placeholder: "请输入社交平台用户ID",
    },
    {
      type: "input",
      label: "社交平台用户加密ID",
      prop: "secUid",
      placeholder: "请输入第三方用户加密ID"
    },
    {
      type: "input",
      label: "用户昵称",
      prop: "nickname",
      placeholder: "请输入用户昵称"
    },

    {
      type: "datetime-picker",
      label: "过期时间",
      prop: "expireTime",
      placeholder: "请选择过期时间",
      format: "YYYY-MM-DD HH:mm:ss",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    },
    {
      type: "input",
      label: "社交平台openId",
      prop: "openId",
      placeholder: "请输入第三方openId"
    }
  ],
  rules: {
    userId: [{required: true, message: '请输入用户ID', trigger: 'blur'}],
    platformId: [{required: true, message: '请输入平台ID', trigger: 'blur'}],
    uid: [{required: true, message: '请输入社交平台用户ID', trigger: 'blur'}],
    type: [{required: true, message: '请选择账号类型', trigger: 'blur'}]

  }
})

// 分享链接表单配置项
const optionShareLink = reactive({
  dialogTitle: '通过主页分享链接添加账号',
  dialogClass: 'dialog_md',
  labelWidth: '120px',
  formitem: [
    {
      type: "input",
      label: "分享链接",
      prop: "shareLink",
      placeholder: "请输入分享链接",
    },
    {
      type: "select",
      label: "账号类型",
      prop: "type",
      dicData: socialMediaAccountTypeDict,
      placeholder: "请选择账号类型",
    }
  ],
  rules: {
    shareLink: [{required: true, message: '请输入分享链接', trigger: 'blur'}],
    type: [{required: true, message: '请选择账号类型', trigger: 'blur'}]
  }
})

// 分享链接表单配置项
const wechatVideoOption = reactive({
  dialogTitle: '微信视频号名称',
  dialogClass: 'dialog_md',
  labelWidth: '140px',
  formitem: [
    {
      type: "input",
      label: "微信视频号名称",
      prop: "nickname",
      placeholder: "请输入微信视频号名称",
    },
    {
      type: "select",
      label: "账号类型",
      prop: "type",
      dicData: socialMediaAccountTypeDict,
      placeholder: "请选择账号类型",
    }
  ],
  rules: {
    nickname: [{required: true, message: '请输入微信视频号名称', trigger: 'blur'}],
    type: [{required: true, message: '请选择账号类型', trigger: 'blur'}]
  }
})

/**
 * 初始化数据
 */
const init = () => {
  getDict()
  getUserList3()
  getData()
}

// 初始化
init()
</script>

<style scoped>
/* 自定义样式 */
</style>