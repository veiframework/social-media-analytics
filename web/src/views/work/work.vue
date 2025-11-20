<template>
  <div>
    <!-- 作品管理表格 -->
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

    <!-- 详情弹窗 -->
    <CustomInfo
        :option="optionInfo"
        :visible="infoVisible"
        :rowData="rowData"
        @cancel="infoVisible = false"
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
  exportWorkApi
} from '@/api/work'
import {getDicts} from '@/api/system/dict/data'
import CustomTable from "@/components/CustomTable"
import CustomInfo from "@/components/CustomInfo"
import settings from "@/settings.js";

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
const mediaTypeDict = ref([])
const platformDict = ref([])
const statusDict = ref([])

// 详情弹窗
const infoVisible = ref(false)
const rowData = ref({})

// 获取字典数据
const getDict = async () => {
  try {
    const [typeRes, mediaTypeRes, platformRes, statusRes] = await Promise.all([
      getDicts('work_type'),
      getDicts('media_type'),
      getDicts('social_media_platform'),
      getDicts('work_status')
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
  } catch (error) {
    console.error('获取字典数据失败:', error)
    // 如果字典获取失败，使用默认值
    typeDict.value = []
    mediaTypeDict.value = []
    platformDict.value = []
    statusDict.value = []
  }
}

// 获取数据
const getData = async () => {
  loading.value = true
  try {
    const params = {
      descFields: "playNum,accountId",
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryParams.value
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
const selectData = (data) => {
  console.log('选择数据:', data)
}

// 头部操作处理
const handleHeader = (key) => {
  switch (key) {
    case 'export':
      handleExport()
      break
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

// 表格配置项
const option = reactive({
  showSearch: true,
  searchLabelWidth: 90,
  /** 搜索字段配置项 */
  searchItem: [
    {
      type: "select",
      label: "作品类型",
      prop: "type",
      default: null,
      filterable: true,
      dicData: typeDict
    },
    // {
    //   type: "select",
    //   label: "作品状态",
    //   prop: "status",
    //   default: null,
    //   filterable: true,
    //   dicData: statusDict
    // },
    {
      type: "select",
      label: "平台",
      prop: "platformId",
      default: null,
      filterable: true,
      dicData: platformDict
    },
    {
      type: "input",
      label: "描述",
      prop: "description",
      default: null,
      placeholder: "请输入作品描述"
    }
  ],
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    {key: "export", text: "导出", icon: "Download", isShow: true, type: "primary", disabled: false},
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
      label: '描述',
      prop: 'description',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    },{
      type: 'text',
      label: '昵称',
      prop: 'accountId_dictText',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true,
      showOverflowTooltip: true
    },
    {
      type: 'tag',
      label: '平台',
      prop: 'platformId',
      width: 100,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: platformDict
    },
    {
      type: 'tag',
      label: '作品类型',
      prop: 'type',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: typeDict
    },

    // {
    //   type: 'tag',
    //   label: '作品状态',
    //   prop: 'status',
    //   width: 120,
    //   fixed: false,
    //   sortable: false,
    //   isShow: true,
    //   dicData: statusDict
    // },
    {
      type: 'text',
      label: '点赞数量',
      prop: 'thumbNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true
    },
    {
      type: 'text',
      label: '收藏数量',
      prop: 'collectNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true
    },
    {
      type: 'text',
      label: '评论量',
      prop: 'commentNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true
    },
    {
      type: 'text',
      label: '播放量',
      prop: 'playNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true
    },
    {
      type: 'text',
      label: '分享量',
      prop: 'shareNum',
      width: 100,
      fixed: false,
      sortable: true,
      isShow: true
    },

    {
      type: 'text',
      label: '发布时间',
      prop: 'postTime',
      width: 180,
      fixed: false,
      sortable: true,
      isShow: true
    },

  ],
  /** 操作菜单配置项 */
  menu: {
    isShow: true,
    width: 100,
    fixed: 'right'
  },
  menuItemBtn: [
    {
      type: 'primary',
      isShow: true,
      icon: 'View',
      label: '详情',
      value: 'detail'
    }
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
        },
        // {
        //   type: 'tag',
        //   label: '作品状态',
        //   prop: 'status',
        //   isShow: true,
        //   dicData: statusDict
        // }
      ]
    },
    {
      title: '作品内容',
      column: 1,
      infoData: [
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
        },
        {
          type: 'text',
          label: '第三方作品ID',
          prop: 'workUid',
          isShow: true,
          span: 2
        }
      ]
    },
    {
      title: '统计数据',
      column: 3,
      infoData: [
        {
          type: 'text',
          label: '点赞数量',
          prop: 'thumbNum',
          isShow: true
        },
        {
          type: 'text',
          label: '收藏数量',
          prop: 'collectNum',
          isShow: true
        },
        {
          type: 'text',
          label: '评论量',
          prop: 'commentNum',
          isShow: true
        },
        {
          type: 'text',
          label: '播放量',
          prop: 'playNum',
          isShow: true
        },
        {
          type: 'text',
          label: '分享量',
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