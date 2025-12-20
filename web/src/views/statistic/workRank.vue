<template>
  <div class="work_rank_box">
    <search :showSearch="option.showSearch" :searchLabelWidth="option.searchLabelWidth"
            :searchItem="option.searchItem" @search="handleSearch" :option="option">
    </search>
    <el-card class="m5">
      <ECharts :height="500" :option="douyinChart" @handleClick="handleClick" :hideSearch="true"/>
    </el-card>
    <el-card class="m5">
      <ECharts :height="500" :option="xiaohongshuChart" @handleClick="handleClick" :hideSearch="true"/>
    </el-card>
    <el-card class="m5">
      <ECharts :height="500" :option="wechatvideoChart" @handleClick="handleClick" :hideSearch="true"/>
    </el-card>
    <!-- 详情弹窗 -->
    <CustomInfo
        :option="optionInfo"
        :visible="infoVisible"
        :rowData="rowData"
        @cancel="infoVisible = false">
      <template #custom="{ row, prop }">
        <template v-if="prop==='topics'">
          <el-tag v-if="row.topics" style="margin-right: 5px" v-for="(item, indx) in row.topics.split(',')" :key="indx">
            {{ item }}
          </el-tag>
        </template>
      </template>
    </CustomInfo>


  </div>

</template>

<script setup name="WorkRank">
import {reactive, ref} from "vue";
import search from "@/components/CustomTable/components/search";
import {getLeaderList, getWorkListApi} from "@/api/statistic.js";
import CustomTable from "@/components/CustomTable"
import {getDicts} from "@/api/system/dict/data.js";
import {listSocialMediaAccount} from "@/api/social-media-account.js";
import {groupUserApi} from "@/api/group-user.js";
import ECharts from '@/components/Echarts'
import CustomInfo from "@/components/CustomInfo/index.vue";
import {getWorkApi} from "@/api/work.js";
import {ElMessage} from "element-plus";
import Template from "@/views/base/template.vue";

const leaders = ref([])


const init = () => {
  userLeaders()
  getDict()
  getAccountList()
  getUserList()
}

const typeDict = ref([])
const mediaTypeDict = ref([])
const platformDict = ref([])
const statusDict = ref([])
const userDict = ref([])
const syncWorkStatusDict = ref([])
const socialMediaAccountTypeDict = ref([])
const socialMediaCustomTypeDict = ref([])
const accountListDict = ref([])
const workStateDict = ref([])

const douyinChart = ref({})
const xiaohongshuChart = ref({})
const wechatvideoChart = ref({})


const getUserList = async () => {
  const res = await groupUserApi().getUserQuerySelector()
  userDict.value = res.data.map(i => ({
    label: i.userId_dictText,
    value: i.userId,
  }))
}

const getAccountList = async () => {
  const res = await listSocialMediaAccount({pageNum: 1, pageSize: 9999999})
  accountListDict.value = res.data.records.map(i => ({
    label: i.nickname,
    value: i.id,
  }))
}

// 获取字典数据
const getDict = async () => {
  try {
    const [typeRes, mediaTypeRes, platformRes, statusRes, socialMediaAccountTypeRes, socialMediaCustomTypeRes, syncDictRes, workStateDictRes] = await Promise.all([
      getDicts('work_type'),
      getDicts('media_type'),
      getDicts('social_media_platform'),
      getDicts('work_status'),
      getDicts("social_media_account_type"),
      getDicts("social_media_custom_type"),
      getDicts('sync_work_status'),
      getDicts('work_state')
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
  } catch (error) {
    console.error('获取字典数据失败:', error)
    // 如果字典获取失败，使用默认值
    typeDict.value = []
    mediaTypeDict.value = []
    platformDict.value = []
    statusDict.value = []
  }
}


const douyinData = ref([])
const xiaohongshuData = ref([])
const wechatvideoData = ref([])

const getDouyinData = async (queryParams) => {
  let params = {...queryParams}
  params.platformId = 'douyin'
  params.pageSize = 10
  params.pageNum = 1
  params.descFields = 'playNum'
  params.searchCount = false
  let res = await getWorkListApi(params)
  douyinData.value = res.data
  let x = []
  let y = []
  let arr = []
  for (let i = res.data.length - 1; i >= 0; i--) {
    const item = res.data[i];
    y.push(item.accountId_dictText)
    x.push({
      value: item.playNum,
      name: item.accountId_dictText,
      id: item.id
    })
    arr.push(item)
  }
  douyinChart.value = {
    title: {
      text: '抖音',
      show: true,  // 强制显示标题
      textStyle: {
        color: '#060716',  // 可自定义颜色
        fontSize: 16    // 可自定义字体大小
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      confine: true,
      formatter: function (params) {
        const param = params[0];
        const dataIndex = param.dataIndex;
        const item = arr[dataIndex]; // 获取原始数据

        // 显示详细指标信息
        return `${item.description}<br/> 播放数: ${item.playNum}<br/> ${item.userId_dictText}<br/> <span style="color: #00bcd4">点击查看详情</span>`;
      }
    },
    legend: {},
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01]
    },
    yAxis: {
      type: 'category',
      data: y
    },
    series: [
      {
        name: queryParams.startCreateTime ? queryParams.startCreateTime + '至' + queryParams.endCreateTime : '全部',
        type: 'bar',
        barMaxWidth: 40,
        data: x,
        label: {
          show: true,
          position: 'right',
          color: '#333',
          fontSize: 12
        },
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              {offset: 0, color: '#060716'},
              {offset: 1, color: '#312e2f'}
            ]
          }
        }
      },
    ],
  }
}

const getXiaohongshuData = async (queryParams) => {
  let params = {...queryParams}
  params.platformId = 'xiaohongshu'
  params.pageSize = 10
  params.pageNum = 1
  params.descFields = 'thumbNum'
  params.searchCount = false
  let res = await getWorkListApi(params)
  xiaohongshuData.value = res.data
  let x = []
  let y = []
  let arr = []
  for (let i = res.data.length - 1; i >= 0; i--) {
    const item = res.data[i];
    y.push(item.accountId_dictText)
    x.push({
      value: item.thumbNum,
      name: item.accountId_dictText,
      id: item.id
    })
    arr.push(item)
  }
  xiaohongshuChart.value = {
    title: {
      text: '小红书',
      show: true,  // 强制显示标题
      textStyle: {
        color: '#f10f0f',  // 可自定义颜色
        fontSize: 16    // 可自定义字体大小
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      confine: true,
      formatter: function (params) {
        const param = params[0];
        const dataIndex = param.dataIndex;
        const item = arr[dataIndex]; // 获取原始数据

        // 显示详细指标信息
        return `${item.description}<br/> 点赞数: ${item.thumbNum}<br/> ${item.userId_dictText}<br/> <span style="color: #00bcd4">点击查看详情</span>`;
      }
    },
    legend: {},
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01]
    },
    yAxis: {
      type: 'category',
      data: y
    },
    series: [
      {
        name: queryParams.startCreateTime ? queryParams.startCreateTime + '至' + queryParams.endCreateTime : '全部',
        type: 'bar',
        barMaxWidth: 40,
        data: x,
        label: {
          show: true,
          position: 'right',
          color: '#333',
          fontSize: 12
        },
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              {offset: 0, color: '#ee0c1a'},
              {offset: 1, color: '#ec0a0a'}
            ]
          }
        }
      },
    ],
  }

}


const getWechatvideoData = async (queryParams) => {
  let params = {...queryParams}
  params.platformId = 'wechatvideo'
  params.pageSize = 10
  params.pageNum = 1
  params.descFields = 'thumbNum'
  params.searchCount = false
  let res = await getWorkListApi(params)
  wechatvideoData.value = res.data
  let x = []
  let y = []
  let arr = []
  for (let i = res.data.length - 1; i >= 0; i--) {
    const item = res.data[i];
    y.push(item.accountId_dictText)
    x.push({
      value: item.thumbNum,
      name: item.accountId_dictText,
      id: item.id
    })
    arr.push(item)
  }
  wechatvideoChart.value = {
    title: {
      text: '微信视频号',
      show: true,  // 强制显示标题
      textStyle: {
        color: '#07c160',  // 可自定义颜色
        fontSize: 16    // 可自定义字体大小
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      confine: true,
      formatter: function (params) {
        const param = params[0];
        const dataIndex = param.dataIndex;
        const item = arr[dataIndex]; // 获取原始数据

        // 显示详细指标信息
        return `${item.description}<br/> 点赞数: ${item.thumbNum}<br/> ${item.userId_dictText}<br/> <span style="color: #00bcd4">点击查看详情</span>`;
      }
    },
    legend: {},
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01]
    },
    yAxis: {
      type: 'category',
      data: y
    },
    series: [
      {
        name: queryParams.startCreateTime ? queryParams.startCreateTime + '至' + queryParams.endCreateTime : '全部',
        type: 'bar',
        data: x,
        barMaxWidth: 40,
        label: {
          show: true,
          position: 'right',
          color: '#333',
          fontSize: 12
        },
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              {offset: 0, color: '#07c160'},
              {offset: 1, color: '#07c160'}
            ]
          }
        }
      },
    ],
  }

}


const handleClick = async (params) => {
  let workId = params.data.id
  try {
    const response = await getWorkApi(workId)
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


const userLeaders = async () => {
  let res = await getLeaderList()
  leaders.value = res.data.map(item => ({
    label: item.nickName,
    value: item.userId
  }));
}

const thisWeek2 = () => {
  let arr = thisWeek();
  let arr2 = []
  for (let i = 0; i < arr.length; i++) {
    arr2[i] = formatDate(arr[i], "YYYY-MM-DD HH:mm:ss")
  }
  return arr2;
}

const thisWeek = () => {
  const now = new Date();
  const dayOfWeek = now.getDay(); // 0表示周日，1表示周一，...，6表示周六
  const start = new Date(now);
  const end = new Date(now);

  // 计算本周一（假设周一为一周开始）
  const diffToMonday = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
  start.setDate(now.getDate() - diffToMonday);
  start.setHours(0, 0, 0, 0);

  // 计算本周日
  const diffToSunday = dayOfWeek === 0 ? 0 : 7 - dayOfWeek;
  end.setDate(now.getDate() + diffToSunday);
  // 往后多出1天，结束时间取0点
  end.setDate(end.getDate() + 1);
  end.setHours(0, 0, 0, 0);

  return [start, end];
}

function formatDate(date, format) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return format
      .replace('YYYY', year)
      .replace('MM', month)
      .replace('DD', day)
      .replace('HH', hours)
      .replace('mm', minutes)
      .replace('ss', seconds);
}

const option = reactive({
  showSearch: true,
  searchLabelWidth: 90,
  searchItem: [
    {
      type: "select",
      label: "员工",
      prop: "userId",
      default: null,
      filterable: true,
      dicData: leaders
    },
    {
      type: "datetimerange",
      label: "周期",
      prop: "CreateTime",
      format: "YYYY-MM-DD HH:mm:ss",
      valueFormat: "YYYY-MM-DD HH:mm:ss",
      category: "datetimerange",
      default: thisWeek2(),
      shortcuts: [
        {
          text: '本周',
          value: thisWeek()
        },
        {
          text: '本月',
          value: () => {
            const now = new Date();

            // 本月第一天
            const start = new Date(now.getFullYear(), now.getMonth(), 1);
            start.setHours(0, 0, 0, 0);

            // 下月第一天
            const end = new Date(now.getFullYear(), now.getMonth() + 1, 1);
            end.setHours(0, 0, 0, 0);

            return [start, end];
          }
        },

      ]
    },

  ]
})

const infoVisible = ref(false)
const rowData = ref({})

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

const handleSearch = (queryParams) => {
  getDouyinData(queryParams)
  getXiaohongshuData(queryParams)
  getWechatvideoData(queryParams)
}

init()

</script>

<style scoped>

</style>