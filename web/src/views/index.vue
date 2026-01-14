<template>
  <div class="dashboard-container">

    <Watermark text="仅供分析不得传播©lumengshop.com"/>

    <!-- 平台数据统计卡片 -->
    <el-card class="dashboard-top-echart">
      <ECharts :height="chartHeight(0.2)" :option="cardChart" :hideSearch="true"/>
    </el-card>
    <!-- 社交媒体账号统计表格 -->
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
        <!-- 由于使用了 dicData，不再需要自定义模板 -->
      </template>
    </CustomTable>


  </div>
</template>

<script setup name="Dashboard">
import {ref, reactive, onMounted, computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {getAccountStatistic, getPlatformStatistic} from '@/api/dashboard'
import {getDicts} from '@/api/system/dict/data'
import CustomTable from "@/components/CustomTable"
import {groupUserApi} from "@/api/group-user.js";
import Watermark from "@/components/Watermark/index.vue";
import {socialMediaAccountSelector} from "@/api/social-media-account.js";
import ECharts from '@/components/Echarts'


// 表格数据
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 查询参数
const queryParams = ref({})

// 字典数据
const platformDict = ref([])
const userDict = ref([])
const socialMediaAccountTypeDict = ref([])
const accountListDict = ref([])

// 平台统计数据
const platformStats = ref([])
const platformLoading = ref(false)

// 获取字典数据
const getDict = async () => {
  try {
    const platformRes = await getDicts('social_media_platform')
    const socialMediaAccountTypeRes = await getDicts('social_media_account_type')
    platformDict.value = platformRes.data.map(i => ({
      label: i.dictLabel,
      value: i.dictValue,
      elTagType: i.listClass
    })) || []
    socialMediaAccountTypeDict.value = socialMediaAccountTypeRes.data.map(i => ({
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

const getUserList2 = async () => {
  const res = await groupUserApi().getUserQuerySelector()
  userDict.value = res.data.map(i => ({
    label: i.userId_dictText,
    value: i.userId,
  }))
}
const getAccountList = async () => {
  const res = await socialMediaAccountSelector()
  accountListDict.value = res.data.map(i => ({
    label: i.nickname,
    value: i.id,
  }))
}

const cardChart = ref({})

const chartHeight = (percent = 1) => {
  // percent: 0 ～ 1 之间的小数，例如 0.6 表示 60%
  return (window.innerHeight ) * percent;
};

// 表格配置项
const option = reactive({
  showSearch: false,
  searchLabelWidth: 120,
  descFields: new Set(["thumbNum"]),
  /** 搜索字段配置项 */
  searchItem: [
    // {
    //   type: "input",
    //   label: "社交帐号昵称",
    //   prop: "nickname",
    //   placeholder: "社交帐号昵称",
    //   default: ""
    // }
  ],
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    // { key: "export", text: "导出", icon: "Download", isShow: true, type: "primary", disabled: false }
  ],
  /** 表格顶部右侧 toobar 配置项 */
  toolbar: {isShowToolbar: true, isShowSearch: false},
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
      type: 'tag',
      label: '员工',
      prop: 'userId',
      width: 90,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: userDict
    },
    {
      type: 'tag',
      label: '昵称',
      prop: 'id',
      width: 130,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: accountListDict
    }, {
      type: 'text',
      label: '社交账号ID',
      prop: 'uid',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      noFilter: true
    }, {
      type: 'tag',
      label: '账号类型',
      prop: 'type',
      width: 80,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: socialMediaAccountTypeDict
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
      label: '播放量',
      prop: 'playNum',
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

  ],
  /** 分页配置项 */
  isShowPage: true
})

// 获取数据
const getData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryParams.value,
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
const handleSearch = (searchParams) => {
  queryParams.value = searchParams
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
      let result = response.data
      // 1. 提取平台名称（用于 xAxis）
      const platforms = result.map(item => item.platformId_dictText);

// 2. 定义互动类型字段和显示名称
      const metrics = [
        {key: 'playNum', name: '播放量'},
        {key: 'thumbNum', name: '点赞数'},
        {key: 'commentNum', name: '评论数'},
        {key: 'shareNum', name: '分享数'},
        {key: 'collectNum', name: '收藏数'}
      ];

// 3. 用 for 循环生成 series
      const series = [];
      for (let i = 0; i < metrics.length; i++) {
        const metric = metrics[i];
        series.push({
          name: metric.name,
          type: 'bar',
          data: result.map(item => item[metric.key] || 0), // 防止 undefined
          label: {
            show: true,
            position: 'top',
            formatter: function (params) {
              const value = params.value;
              if (value >= 1e8) {
                return (value / 1e8).toFixed(1) + '亿';
              } else if (value >= 1e4) {
                return (value / 1e4).toFixed(1) + '万';
              } else {
                return value.toLocaleString();
              }
            },
            fontSize: 12
          }
        });
      }

// 4. 配置图表
      cardChart.value = {
        title: {
          text: '各平台互动数据对比',
          show: false
        },
        grid: {
          left: '10%',
          right: '10%',
          bottom: '10%',
          top: '20%'
        },
        // barGap: '80%',        // 同一组内柱子间距（变小 → 柱子更靠近）
        barCategoryGap: '30%', // 不同平台组之间的间距（变大 → 组间更宽松）
        legend: {},
        tooltip: {
          trigger: 'axis',
          axisPointer: {type: 'shadow'}
        },
        xAxis: {
          type: 'category',
          data: platforms,

        },
        yAxis: {
          type: 'log',
          name: ''
        },
        series: series
      };

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
  getUserList2()
  getAccountList()
}

// 初始化
init()
</script>

<style scoped>
.dashboard-container {
}

.dashboard-top-echart{
  margin: 0 5px;
}

.platform-cards {
  padding-left: 10px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
}

.card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 8px;
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
