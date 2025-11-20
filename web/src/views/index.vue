<template>
  <div class="dashboard-container">


    <!-- 社交媒体账号统计表格 -->
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
        <!-- 由于使用了 dicData，不再需要自定义模板 -->
      </template>
    </CustomTable>

    <!-- 平台数据统计卡片 -->
    <div class="platform-cards">
      <el-skeleton :rows="3" animated v-if="platformLoading">
        <template #template>
          <div class="card-skeleton">
            <div class="card-header-skeleton"></div>
            <div class="card-body-skeleton">
              <div class="stat-item-skeleton"></div>
              <div class="stat-item-skeleton"></div>
              <div class="stat-item-skeleton"></div>
            </div>
          </div>
        </template>
      </el-skeleton>

      <div v-else-if="platformStats.length === 0" class="empty-cards">
        <el-empty description="暂无平台统计数据"/>
      </div>

      <div v-else class="card" v-for="(item, index) in platformStats" :key="index">
        <div class="card-header">
          <span class="platform-name">{{ item.platformName }}</span>
        </div>
        <div class="card-body">
          <div class="stat-item">
            <span class="stat-label">作品总数</span>
            <span class="stat-value">{{ item.workCount }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">总点赞数</span>
            <span class="stat-value">{{ item.totalThumbNum }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">总收藏数</span>
            <span class="stat-value">{{ item.totalCollectNum }}</span>
          </div>

          <div class="stat-item">
            <span class="stat-label">总评论数</span>
            <span class="stat-value">{{ item.totalCommentNum }}</span>
          </div>

          <div class="stat-item">
            <span class="stat-label">总分享数</span>
            <span class="stat-value">{{ item.totalShareNum }}</span>
          </div>

          <div class="stat-item">
            <span class="stat-label">总播放量</span>
            <span class="stat-value">{{ item.totalPlayNum }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="Dashboard">
import {ref, reactive, onMounted, computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {getAccountStatistic, getPlatformStatistic} from '@/api/dashboard'
import {getDicts} from '@/api/system/dict/data'
import CustomTable from "@/components/CustomTable"

// 表格数据
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 查询参数
const queryParams = reactive({
  nickname: ''
})

// 字典数据
const platformDict = ref([])
const userDict = ref([])

// 平台统计数据
const platformStats = ref([])
const platformLoading = ref(false)

// 获取字典数据
const getDict = async () => {
  try {
    const platformRes = await getDicts('social_media_platform')
    platformDict.value = platformRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass

    })) || []
  } catch (error) {
    console.error('获取字典数据失败:', error)
    // 如果字典获取失败，使用默认值
    platformDict.value = []
  }
}

// 表格配置项
const option = reactive({
  showSearch: true,
  searchLabelWidth: 120,
  /** 搜索字段配置项 */
  searchItem: [
    {
      type: "input",
      label: "社交帐号昵称",
      prop: "nickname",
      placeholder: "社交帐号昵称",
      default: ""
    }
  ],
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    // { key: "export", text: "导出", icon: "Download", isShow: true, type: "primary", disabled: false }
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
  /** 操作菜单配置项 */
  menu: {
    isShow: false,
    width: 100,
    fixed: 'right'
  },
  /** 表格字段配置项 */
  tableItem: [
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
      type: 'text',
      label: '用户',
      prop: 'userId_dictText',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true
    }, {
      type: 'text',
      label: '社交账号昵称',
      prop: 'nickname',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true
    }, {
      type: 'text',
      label: '社交账号ID',
      prop: 'uid',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true
    },
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

  ],
  /** 分页配置项 */
  isShowPage: true
})

// 获取数据
const getData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const response = await getAccountStatistic(params)
    if (response.code === 200) {
      tableData.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取数据失败')
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = (val) => {
  Object.assign(queryParams, val)
  pageNum.value = 1
  getData()
}

// 分页变化
const handleChange = (val) => {
  pageNum.value = val.page
  pageSize.value = val.limit
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

// 菜单操作处理
const handleMenu = async (val) => {
  const {index, row, value} = val
  // 这里可以添加更多操作
}

// 导出
const handleExport = async () => {
  try {
    await ElMessageBox.confirm('确定要导出数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 这里需要添加导出API调用
    ElMessage.success('导出功能待实现')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('导出失败:', error)
      ElMessage.error('导出失败')
    }
  }
}

// 获取平台统计数据
const getPlatformStats = async () => {
  platformLoading.value = true
  try {
    const response = await getPlatformStatistic()
    if (response.code === 200) {
      // 处理平台统计数据，按平台分组并计算统计信息
      const platformMap = new Map()

      response.data.forEach(item => {
        const platformId = item.platformId
        if (!platformMap.has(platformId)) {
          // 查找平台名称
          const platformName = platformDict.value.find(p => p.value === platformId)?.label || '未知平台'
          platformMap.set(platformId, {
            platformId,
            platformName,
            workCount: 0,
            totalThumbNum: 0,
            totalPlayNum: 0,
            totalCollectNum: 0,
            totalCommentNum: 0,
            totalShareNum: 0
          })
        }

        const platformData = platformMap.get(platformId)
        platformData.workCount += item.workNum || 0
        platformData.totalThumbNum += item.thumbNum || 0
        platformData.totalPlayNum += item.playNum || 0
        platformData.totalCollectNum += item.collectNum || 0
        platformData.totalCommentNum += item.commentNum || 0
        platformData.totalShareNum += item.shareNum || 0
      })

      // 将Map转换为数组
      platformStats.value = Array.from(platformMap.values())
    } else {
      ElMessage.error(response.msg || '获取平台统计数据失败')
      platformStats.value = []
    }
  } catch (error) {
    console.error('获取平台统计数据失败:', error)
    ElMessage.error('获取平台统计数据失败')
    platformStats.value = []
  } finally {
    platformLoading.value = false
  }
}

/**
 * 初始化数据
 */
const init = () => {
  getDict().then(() => {
    // 等字典数据获取完成后再获取平台统计数据
    getPlatformStats()
  })
  getData()
}

// 初始化
init()
</script>

<style scoped>
.dashboard-container {
}

.platform-cards {
  padding-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
}

.card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: all 0.3s ease;
  width: 250px; /* 设置固定宽度 */
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
  margin-bottom: 15px;
}

.platform-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.table-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

/* 加载状态样式 */
.card-skeleton {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  width: 250px; /* 设置固定宽度 */
}

.card-header-skeleton {
  width: 60%;
  height: 24px;
  margin-bottom: 15px;
  border-radius: 4px;
}

.card-body-skeleton {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item-skeleton {
  width: 100%;
  height: 20px;
  border-radius: 4px;
}

/* 空数据样式 */
.empty-cards {
  width: 100%;
  padding: 40px 0;
  text-align: center;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }

  .platform-cards {
    gap: 15px;
  }

  .card {
    padding: 15px;
    width: calc(50% - 8px); /* 在移动端显示两列 */
  }

  .platform-name {
    font-size: 16px;
  }

  .stat-value {
    font-size: 16px;
  }

  .table-container {
    padding: 10px;
  }

  .card-skeleton {
    padding: 15px;
    width: calc(50% - 8px); /* 在移动端显示两列 */
  }
}
</style>
