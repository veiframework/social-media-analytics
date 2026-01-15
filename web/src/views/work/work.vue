<template>
  <div>
    <Watermark text="仅供分析不得传播©lumengshop.com"/>

    <!-- 作品管理表格 -->
    <CustomTable
        :data="tableData"
        :option="option"
        @search="handleSearch"
        v-model:page-num="pageNum"
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
        <template v-if="prop == 'thumbNumUp'">
          <div class="thumb_num_box">
            <div class="thumb_num_box_text">
              {{ Math.abs(row.thumbNumUp) }}
            </div>
            <template v-if="row.thumbNumChange>0">

              <div class="thumb_num_change" style="color: green;margin-top: -16px;padding-left: 6px;">
                <el-icon>
                  <arrow-up color="green"/>
                </el-icon>
                {{ row.thumbNumChange }}
              </div>
            </template>
            <template v-if="row.thumbNumChange<0">
              <div class="thumb_num_change" style="color: red;padding: 16px 0 0 6px">
                <el-icon>
                  <arrow-down color="red"/>
                </el-icon>
                {{ Math.abs(row.thumbNumChange) }}
              </div>
            </template>


          </div>
        </template>
        <template v-if="prop == 'playNumUp'">
          <div class="thumb_num_box">
            <div class="thumb_num_box_text">
              {{ Math.abs(row.playNumUp) }}
            </div>
            <template v-if="row.playNumChange>0">

              <div class="thumb_num_change" style="color: green;margin-top: -16px;padding-left: 6px;">
                <el-icon>
                  <arrow-up color="green"/>
                </el-icon>
                {{ row.playNumChange }}
              </div>
            </template>
            <template v-if="row.playNumChange<0">
              <div class="thumb_num_change" style="color: red;padding: 16px 0 0 6px">
                <el-icon>
                  <arrow-down color="red"/>
                </el-icon>
                {{ Math.abs(row.playNumChange) }}
              </div>
            </template>


          </div>
        </template>

      </template>
    </CustomTable>

    <!-- 详情弹窗 -->
    <CustomInfo
        :option="optionInfo"
        :visible="infoVisible"
        :rowData="rowData"
        @cancel="infoVisible = false">
      <template #custom="{ row, prop }">
        <template v-if="prop==='topics'&&row.topics">
          <el-tag style="margin-right: 5px" v-for="(item, indx) in row.topics.split(',')" :key="indx">
            {{ item }}
          </el-tag>
        </template>
      </template>
    </CustomInfo>


    <CustomDialog
        :option="shareLinkTaskOption"
        :visible="shareLinkTaskVisible"
        @cancel="shareLinkTaskVisible = false"
        @save="handleSaveShareLinkTask">
      <template #content>
        <CustomTable
            v-if="shareLinkTaskVisible"
            :data="shareLinkTable"
            :option="shareLinkTableOption"
            @search="handleShareLinkTaskSearch"
            v-model:page-num="shareLinkPageNum"
            @refresh="getShareLinkTask"
            :total="shareLinkTotal"
            @headerchange="handleShareLinkTaskHeader"
            @menuChange="handleShareLinkMenu"
            :pageSize="shareLinkPageSize"
            @currentChange="handleShareLinkChange"
        />
      </template>
    </CustomDialog>

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
    <!-- 播放数修正 -->
    <CustomDialog
        :form="editViewForm"
        :option="editViewOption"
        :visible="editViewVisible"
        @cancel="editViewVisible = false"
        @save="handleEditViewForm"
    />

  </div>
</template>

<script setup name="Work">
import useUserStore from "@/store/modules/user.js";

const userStore = useUserStore();

import {ref, reactive, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  getWorkListApi,
  getWorkApi,
  exportWorkApi,
  createByWechatVideoId,
  createByWorkShareUrl,
  delWork,
  updateViewCount,
  getShareLinkTaskPage,
  deleteShareLinkTask,
  retryShareLinkTask,
  createShareLinkTask,
  getWorkByShareLinkApi,
  syncWork
} from '@/api/work'
import {getDicts} from '@/api/system/dict/data'
import CustomTable from "@/components/CustomTable"
import CustomInfo from "@/components/CustomInfo"
import Watermark from "@/components/Watermark"
import settings from "@/settings.js";
import {syncAllWork, socialMediaAccountSelector} from "@/api/social-media-account.js";
import {groupUserApi} from '@/api/group-user'
import Template from "@/views/base/template.vue";
import CustomDialog from "@/components/CustomDialog/index.vue";
import {ElLoading} from 'element-plus'

// 页面数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
let loadingOpt = {
  lock: true,
  text: '加载中……',
  background: 'rgba(0, 0, 0, .1)',
}
// 查询参数
const queryParams = ref({})
const shareLinkTaskParams = ref({})

// 字典数据
const typeDict = ref([])
const mediaTypeDict = ref([])
const platformDict = ref([])
const statusDict = ref([])
const userDict = ref([])
const syncWorkStatusDict = ref([])
const priorityDict = ref([])

const workStateDict = ref([])
const socialMediaAccountTypeDict = ref([])
const socialMediaCustomTypeDict = ref([])
const accountListDict = ref([])
// 详情弹窗
const infoVisible = ref(false)
const rowData = ref({})

// 分享链接表单数据
const shareLinkForm = ref({})
const shareLinkVisible = ref(false)
const wechatVideoForm = ref({})
const wechatVideoVisible = ref(false)

const editViewVisible = ref(false)
const editViewForm = ref({})

const shareLinkTaskVisible = ref(false)

const shareLinkTable = ref([])
const shareLinkPageNum = ref(1)
const shareLinkPageSize = ref(10)
const shareLinkTotal = ref(0)
const createStatusDict = ref([
  {
    label: '待处理',
    value: 'wait',
    elTagType: 'info'
  }, {
    label: '处理中',
    value: 'processing',
    elTagType: 'warning'
  }, {
    label: '完成',
    value: 'success',
    elTagType: 'success'
  }, {
    label: '失败',
    value: 'fail',
    elTagType: 'danger'
  },
])

const getShareLinkTask = async () => {
  const params = {
    pageNum: shareLinkPageNum.value,
    pageSize: shareLinkPageSize.value,
    ...shareLinkTaskParams.value
  }
  let url = params.shareLink
  if (url) {
    params.shareLink = extractUrlFromText(url)
  }
  const response = await getShareLinkTaskPage(params)
  if (response.code === 200) {
    shareLinkTable.value = response.data.records || []
    shareLinkTotal.value = response.data.total || 0
  } else {
    ElMessage.error(response.msg || '获取作品列表失败')
  }
}

const handleShareLinkTaskHeader = (key) => {
  switch (key) {
    case 'shareLink':
      shareLinkForm.value = {}
      shareLinkVisible.value = true
      break
  }
}

const handleShareLinkMenu = async (val) => {
  const {index, row, value} = val
  switch (value) {
    case 'delete':
      await handleShareLinkTaskDelete(row.id)
      break
    case 'updateStatus':
      await handleCreateStatus(row.id)
      break
    case 'detailByLink':
      await showDetailByLink(row)
      break
  }
}

const handleShareLinkTaskDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await deleteShareLinkTask(id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      await getShareLinkTask()
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

const handleCreateStatus = async (id) => {
  let loading = ElLoading.service(loadingOpt)
  try {
    await retryShareLinkTask(id)
    await getShareLinkTask()
  } finally {
    loading.close()
  }
}

const handleShareLinkTaskSearch = (searchParams) => {
  shareLinkTaskParams.value = searchParams
  shareLinkPageNum.value = 1
  getShareLinkTask()
}

const handleShareLinkChange = (page) => {
  shareLinkPageNum.value = page.page
  shareLinkPageSize.value = page.limit
  getShareLinkTask()
}

const handleSaveShareLinkTask = () => {
  shareLinkTaskVisible.value = false
}

// 处理分享链接
const handleShareLink = async (val) => {
  let loading = ElLoading.service(loadingOpt)
  try {
    let shareLink = val.shareLink
    val.shareLink = extractUrlFromText(shareLink)
    const formData = {...val}
    let res = await createShareLinkTask(formData)
    if (res.code === 200) {
      ElMessage.success(res.msg || '通过分享链接添加成功')
      shareLinkVisible.value = false
      await getShareLinkTask()
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
    let res = await createByWechatVideoId(formData)
    if (res.code === 200) {
      ElMessage.success(res.msg || '通过微信视频ID添加成功')
      wechatVideoVisible.value = false
      await getData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } finally {
    loading.close()
  }
}

const showDetailByLink = async (row) => {
  let workId = row.workId
  const response = await getWorkByShareLinkApi({id: workId})
  rowData.value = response.data
  infoVisible.value = true
}

// 获取字典数据
const getDict = async () => {
  try {
    const [typeRes, mediaTypeRes, platformRes, statusRes, socialMediaAccountTypeRes, socialMediaCustomTypeRes, syncDictRes, workStateDictRes, priorityRes] = await Promise.all([
      getDicts('work_type'),
      getDicts('media_type'),
      getDicts('social_media_platform'),
      getDicts('work_status'),
      getDicts("social_media_account_type"),
      getDicts("social_media_custom_type"),
      getDicts('sync_work_status'),
      getDicts('work_state'),
      getDicts('work_priority')
    ])

    typeDict.value = typeRes.data.map(i => ({label: i.dictLabel, value: i.dictValue, elTagType: i.listClass})) || []
    mediaTypeDict.value = mediaTypeRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    platformDict.value = platformRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    statusDict.value = statusRes.data.map(i => ({label: i.dictLabel, value: i.dictValue, elTagType: i.listClass})) || []
    socialMediaAccountTypeDict.value = socialMediaAccountTypeRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    socialMediaCustomTypeDict.value = socialMediaCustomTypeRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    }))
    syncWorkStatusDict.value = syncDictRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    workStateDict.value = workStateDictRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    priorityDict.value = priorityRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
  } catch (error) {
    console.error('获取字典数据失败:', error)
    // 如果字典获取失败，使用默认值
    typeDict.value = []
    mediaTypeDict.value = []
    platformDict.value = []
    statusDict.value = []
  }
}

const getAccountList = async () => {
  const res = await socialMediaAccountSelector()
  accountListDict.value = res.data.map(i => ({
    label: i.nickname,
    value: i.id,
  }))
}

const getUserList = async () => {
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
    getUserList()
    getAccountList()
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryParams.value
    }
    let url = params.shareLink
    if (url) {
      params.shareLink = extractUrlFromText(url)
    }
    const response = await getWorkListApi(params)
    if (response.code === 200) {
      tableData.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取作品列表失败')
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
  queryParams.value = searchParams
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
    case 'export':
      handleExport()
      break
    case 'syncWork':
      handleSyncWork()
      break
    case 'shareLink':
      shareLinkTaskVisible.value = true
      break
    case 'wechatVideo':
      wechatVideoForm.value = {}
      wechatVideoVisible.value = true
      break
  }
}

const handleSyncWork = async () => {
  try {
    await ElMessageBox.confirm('确定要同步作品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await syncAllWork()
    if (response.code === 200) {
      ElMessage.success('同步作品需要时间，请稍后前往《作品管理》查看~')
      getData()
    } else {
    }
  } catch (error) {

  }
}

// 导出作品
const handleExport = async () => {
  try {
    const params = {...queryParams.value}
    const res = await exportWorkApi(params)
    let url = settings.uploadHost + "/" + res.data.filename
    userStore.exportFileUrl(url);
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 菜单操作处理
const handleMenu = async (val) => {
  const {index, row, value} = val
  switch (value) {
    case 'detail':
      await showDetail(row.id)
      break
    case 'delete':
      await handleDelete(row.id)
      break
    case 'viewEdit':
      await handleViewEdit(row)
      break
    case 'syncWork':
      await handleSyncWork2(row.id)
      break
  }
}

const handleSyncWork2 = async (workId) => {
  try {
    await ElMessageBox.confirm('确定要同步作品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await syncWork(workId)
    if (response.code === 200) {
      ElMessage.success('同步作品需要时间，请稍后前往《作品管理》查看~')
    } else {

    }
  } catch (error) {

  }
}

const handleEditViewForm = async (val) => {
  let loading = ElLoading.service(loadingOpt)
  try {
    let res = await updateViewCount(val)
    await getData()
    editViewVisible.value = false;
  } finally {
    loading.close()
  }
}

const handleViewEdit = async (row) => {
  editViewForm.value.accountId = row.accountId;
  editViewForm.value.workId = row.id;
  editViewVisible.value = true;
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除作品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await delWork(id)
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

// 显示详情
const showDetail = async (id) => {
  try {
    const response = await getWorkApi(id)
    if (response.code === 200) {
      rowData.value = response.data
      infoVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取作品详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}


const extractUrlFromText = (text) => {
  // 使用正则表达式匹配 URL
  const urlRegex = /(https?:\/\/[^\s]+)/;
  const match = text.match(urlRegex);
  if (match) {
    return match[1]; // 返回匹配到的链接
  } else {
    return null; // 没有找到链接时返回 null
  }
}

// 表格配置项
const option = reactive({
  showSearch: true,
  searchLabelWidth: 90,
  ascFields: new Set(["priority"]),
  descFields: new Set(["updateTime"]),
  /** 搜索字段配置项 */
  searchItem: [
    {
      type: "select",
      label: "社交帐号",
      prop: "accountId",
      default: null,
      filterable: true,
      dicData: accountListDict
    },
    {
      type: "datetimerange",
      label: "发布时间",
      prop: "PostTime",
      format: "YYYY-MM-DD HH:mm:ss",
      valueFormat: "YYYY-MM-DD HH:mm:ss",
      category: "datetimerange",
      default: null,
      shortcuts: [
        {
          text: '近7天',
          value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
          }
        },
        {
          text: '近15天',
          value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 15)
            return [start, end]
          }
        },
        {
          text: '近1个月',
          value: () => {
            const end = new Date()
            const start = new Date()
            start.setMonth(start.getMonth() - 1)
            return [start, end]
          }
        },
        {
          text: '近2个月',
          value: () => {
            const end = new Date()
            const start = new Date()
            start.setMonth(start.getMonth() - 2)
            return [start, end]
          }
        },
        {
          text: '近3个月',
          value: () => {
            const end = new Date()
            const start = new Date()
            start.setMonth(start.getMonth() - 3)
            return [start, end]
          }
        },
        {
          text: '近半年',
          value: () => {
            const end = new Date()
            const start = new Date()
            start.setMonth(start.getMonth() - 6)
            return [start, end]
          }
        }
      ]
    },
    // {
    //   type: "select",
    //   label: "员工账号",
    //   prop: "userId",
    //   default: null,
    //   filterable: true,
    //   dicData: userDict
    // },

    {
      type: "input",
      label: "描述",
      prop: "description",
      default: null,
      placeholder: "请输入作品描述"
    },
    {
      type: "input",
      label: "分享链接",
      prop: "shareLink",
      default: null,
      placeholder: "请输入分享链接",
      max: 15000
    }, {
      type: "select",
      label: "优先级",
      prop: "priority",
      default: null,
      placeholder: "请选择优先级",
      dicData: priorityDict
    },
  ],
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    {key: "export", text: "导出", icon: "Download", isShow: true, type: "primary", disabled: false},
    // {
    //   key: "syncWork",
    //   text: "同步作品",
    //   icon: "Refresh",
    //   isShow: true,
    //   type: "primary",
    //   disabled: false,
    //   hasPermi: ['sync:all:work']
    // },
    {key: "shareLink", text: "通过作品分享链接添加", icon: "Link", isShow: true, type: "primary", disabled: false},
    // {
    //   key: "wechatVideo",
    //   text: "通过微信视频ID添加(不稳定,可以在社交帐号同步账号数据)",
    //   icon: "Link",
    //   isShow: true,
    //   type: "success",
    //   disabled: false
    // }
  ],
  /** 表格顶部右侧 toobar 配置项 */
  toolbar: {isShowToolbar: true, isShowSearch: true},
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
      label: '平台',
      prop: 'platformId',
      width: 110,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: platformDict
    },
    {
      type: 'text',
      label: '描述',
      prop: 'description',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    }, {
      type: 'tag',
      label: '昵称',
      prop: 'accountId',
      width: 160,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: accountListDict
    },
    {
      type: 'tag',
      label: '业务类型',
      prop: 'customType',
      width: 110,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: socialMediaCustomTypeDict
    },
    {
      type: 'tag',
      label: '优先级',
      prop: 'priority',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true,
      dicData: priorityDict
    },


    {
      type: 'text',
      label: '点赞数',
      prop: 'thumbNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true
    }, {
      type: 'custom',
      label: '点赞增长量',
      prop: 'thumbNumUp',
      width: 120,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true
    }, {
      type: 'text',
      label: '播放量',
      prop: 'playNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true
    }, {
      type: 'custom',
      label: '播放增长量',
      prop: 'playNumUp',
      width: 120,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true
    },
    {
      type: 'text',
      label: '收藏数',
      prop: 'collectNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true
    },
    {
      type: 'text',
      label: '评论数',
      prop: 'commentNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true
    },

    {
      type: 'text',
      label: '分享数',
      prop: 'shareNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true
    },
    {
      type: 'tag',
      label: '作品状态',
      prop: 'state',
      width: 100,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: workStateDict
    }, {
      type: 'text',
      label: '创建时间',
      prop: 'createTime',
      width: 160,
      fixed: false,
      sortable: true,
      isShow: true,
      noFilter: true
    }, {
      type: 'tag',
      label: '作品类型',
      prop: 'type',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: typeDict
    }, {
      type: 'tag',
      label: '账号类型',
      prop: 'accountType',
      width: 100,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: socialMediaAccountTypeDict
    },
    {
      type: 'tag',
      label: '员工',
      prop: 'userId',
      width: 90,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: userDict
    },
    // {
    //   type: 'tag',
    //   label: '作品同步状态',
    //   prop: 'syncWorkStatus',
    //   width: 120,
    //   fixed: false,
    //   sortable: false,
    //   isShow: true,
    //   dicData: syncWorkStatusDict
    // }
  ],
  /** 操作菜单配置项 */
  menu: {
    isShow: true,
    width: 135,
    fixed: 'right'
  },
  moreItem: [
    // {
    //   type: 'primary',
    //   isShow: true,
    //   icon: 'View',
    //   label: '修正播放数',
    //   value: 'viewEdit',
    //   judge: (row) => row.platformId === 'xiaohongshu' || row.platformId === 'wechatvideo'
    // },
    {
      type: 'primary',
      isShow: true,
      icon: 'View',
      label: '详情',
      value: 'detail'
    }, {
      type: 'danger',
      isShow: true,
      icon: 'Delete',
      label: '删除',
      value: 'delete'
    },
  ],
  menuItemBtn: [
    {
      type: 'success',
      isShow: true,
      icon: 'Refresh',
      label: '同步',
      judge: (row) => {
        return row.syncWorkStatus !== '1'
      },
      value: 'syncWork'
    },
    {type: 'more', isShow: true, hasPermi: []},
  ],
  /** page 分页配置项 */
  isShowPage: true
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
          label: '平台',
          prop: 'platformId',
          isShow: true,
          dicData: platformDict
        },
        {
          type: 'tag',
          label: '作品类型',
          prop: 'type',
          isShow: true,
          dicData: typeDict
        },
        {
          type: 'tag',
          label: '媒体类型',
          prop: 'mediaType',
          isShow: true,
          dicData: mediaTypeDict
        }, {
          type: 'tag',
          label: '优先级',
          prop: 'priority',
          isShow: true,
          dicData: priorityDict
        },
        {
          type: 'tag',
          label: '员工',
          prop: 'userId',
          width: 180,
          fixed: false,
          sortable: false,
          isShow: true,
          dicData: userDict
        },
        {
          type: 'tag',
          label: '昵称',
          prop: 'accountId',
          width: 120,
          fixed: false,
          sortable: false,
          isShow: true,
          dicData: accountListDict
        },
        {
          type: 'tag',
          label: '账号类型',
          prop: 'accountType',
          width: 100,
          fixed: false,
          sortable: false,
          isShow: true,
          dicData: socialMediaAccountTypeDict
        },
      ]
    },
    {
      title: '作品内容',
      column: 1,
      infoData: [
        {
          type: 'text',
          label: '标题',
          prop: 'title',
          isShow: true,
          span: 2
        },
        {
          type: 'custom',
          label: '话题',
          prop: 'topics',
          isShow: true,
          span: 2
        },
        {
          type: 'text',
          label: '描述',
          prop: 'description',
          isShow: true,
          span: 2
        },
        {
          type: 'text',
          label: '作品链接',
          prop: 'url',
          isShow: true,
          span: 2
        }, {
          type: 'text',
          label: '分享链接',
          prop: 'shareLink',
          isShow: true,
          span: 2
        },
        {
          type: 'text',
          label: '第三方作品ID',
          prop: 'workUid',
          isShow: true,
          span: 2
        },
        {
          type: 'tag',
          label: '作品状态',
          prop: 'state',
          fixed: false,
          sortable: false,
          isShow: true,
          dicData: workStateDict,
          span: 2
        },
      ]
    },
    {
      title: '统计数据',
      column: 3,
      infoData: [
        {
          type: 'text',
          label: '点赞数',
          prop: 'thumbNum',
          isShow: true
        }, {
          type: 'text',
          label: '点赞增长量',
          prop: 'thumbNumUp',
          isShow: true,
        },
        {
          type: 'text',
          label: '收藏数',
          prop: 'collectNum',
          isShow: true
        },
        {
          type: 'text',
          label: '评论数',
          prop: 'commentNum',
          isShow: true
        },
        {
          type: 'text',
          label: '播放量',
          prop: 'playNum',
          isShow: true
        }, {
          type: 'text',
          label: '播放增长量',
          prop: 'playNumUp',
          isShow: true,
        },
        {
          type: 'text',
          label: '分享数',
          prop: 'shareNum',
          isShow: true
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
          label: '发布时间',
          prop: 'postTime',
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

// 分享链接表单配置项
const optionShareLink = reactive({
  dialogTitle: '通过作品分享链接添加账号',
  dialogClass: 'dialog_md',
  labelWidth: '120px',
  formitem: [
    {
      type: "input",
      label: "分享链接",
      prop: "shareLink",
      category: "textarea",
      placeholder: "请输入分享链接",
      span: 2
    },
    {
      type: "select",
      label: "账号类型",
      prop: "accountType",
      placeholder: "请选择账号类型",
      dicData: socialMediaAccountTypeDict,
    },
    {
      type: "select",
      label: "业务类型",
      prop: "customType",
      category: "select",
      placeholder: "请选择业务类型",
      dicData: socialMediaCustomTypeDict,
    },
  ],
  rules: {
    shareLink: [{required: true, message: '请输入分享链接', trigger: 'blur'}],
    accountType: [{required: true, message: '请选择账号类型', trigger: 'blur'}],
    customType: [{required: true, message: '请选择业务类型', trigger: 'blur'}]
  }
})

// 分享链接表单配置项
const wechatVideoOption = reactive({
  dialogTitle: '微信视频ID',
  dialogClass: 'dialog_md',
  labelWidth: '140px',
  formitem: [
    {
      type: "input",
      label: "微信视频ID",
      prop: "shareLink",
      placeholder: "请输入微信视频ID",
    }, {
      type: "select",
      label: "账号类型",
      prop: "accountType",
      placeholder: "请选择账号类型",
      dicData: socialMediaAccountTypeDict
    },
  ],
  rules: {
    shareLink: [{required: true, message: '请输入微信视频ID', trigger: 'blur'}],
    accountType: [{required: true, message: '请选择账号类型', trigger: 'blur'}]
  }
})

// 分享链接表单配置项
const editViewOption = reactive({
  dialogTitle: '修正播放数',
  dialogClass: 'dialog_md',
  labelWidth: '140px',
  formitem: [
    {
      type: "input",
      label: "播放数",
      prop: "playNum",
      category: "number",
      placeholder: "请输入播放数",
    },
  ],
  rules: {
    playNum: [{required: true, message: '请输入播放数', trigger: 'blur'}],
  }
})

const shareLinkTaskOption = ref({
  dialogTitle: '任务管理',
  dialogClass: 'shareLinkTaskDialogClass',
  labelWidth: '140px',
  formitem: [],
  rules: {}
})
const shareLinkTableOption = ref({
  showSearch: true,
  searchLabelWidth: 90,
  /** 搜索字段配置项 */
  searchItem: [
    {
      type: "input",
      label: "分享链接",
      prop: "shareLink",
      default: null,
      placeholder: "请输入分享链接",
      max: 5000
    },
  ],
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    {key: "shareLink", text: "新增作品链接", icon: "Link", isShow: true, type: "primary", disabled: false},
  ],
  /** 表格顶部右侧 toobar 配置项 */
  toolbar: {isShowToolbar: true, isShowSearch: true},
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
      label: '分享链接',
      prop: 'shareLink',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true,
      noFilter: true
    }, {
      type: 'tag',
      label: '业务类型',
      prop: 'customType',
      width: 80,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: socialMediaCustomTypeDict
    }, {
      type: 'tag',
      label: '账号类型',
      prop: 'accountType',
      width: 80,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: socialMediaAccountTypeDict
    },
    {
      type: 'tag',
      label: '状态',
      prop: 'createStatus',
      width: 80,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: createStatusDict
    },
    {
      type: 'text',
      label: '错误原因',
      prop: 'errorMsg',
      width: 160,
      fixed: false,
      sortable: false,
      isShow: true,
      noFilter: true
    }, {
      type: 'text',
      label: '创建时间',
      prop: 'createTime',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      noFilter: true
    },
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
      icon: 'View',
      value: 'detailByLink',
      label: '作品详情',
      judge: (row) => row.createStatus === 'success'
    },
    {
      type: 'primary',
      isShow: true,
      icon: 'View',
      label: '重试',
      value: 'updateStatus',
      judge: (row) => row.createStatus === 'fail' && row.retryCount === 0
    },
    {
      type: 'danger',
      isShow: true,
      icon: 'Delete',
      label: '删除',
      value: 'delete'
    },
  ],
  /** page 分页配置项 */
  isShowPage: true
})

/**
 * 初始化数据
 */
const init = () => {
  getDict()
}

// 初始化
init()
</script>
<style>
.shareLinkTaskDialogClass {
  width: 1400px;
}

.m5 {
  position: relative;
}

.watermark-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" opacity="0.15" rotate="-15"><text x="50%" y="50%" font-family="Arial" font-size="14" fill="black" text-anchor="middle" transform="rotate(-15, 100, 100)">作品管理</text></svg>');
  background-size: 200px 200px;
}
</style>

<style scoped>
/* 自定义样式 */
.thumb_num_box {
  height: 30px;
  display: flex;
  justify-content: center;
}

.thumb_num_change {
  font-size: 11px;
  display: flex;
  align-items: center;
}

.thumb_num_box_text {
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>