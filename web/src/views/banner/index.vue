<template>
  <div>
    <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
                 :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
                 @currentChange="handleChange" @selectData="selectData">
      <template #table-top></template>
    </CustomTable>

    <CustomDialog :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
                  @save="handleSave" @formChange="handleFormChange">
      <template #custom-item="{ prop, form }">
        <div v-if="prop == 'competitionId'" style="width: 100%;">
          <el-select
              v-model="form.competitionId"
              placeholder="请选择赛事"
              clearable
              style="width: 100%;">
            <el-option
                v-for="item in competitionList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </div>
      </template>
    </CustomDialog>

    <!-- 轮播图详情弹窗 -->
    <CustomInfo 
        :option="optionInfo" 
        :rowData="currentDetail" 
        :visible="detailVisible" 
        @cancel="detailVisible = false"
    >
        <template #custom="{ row, prop }">
            <template v-if="prop === 'status'">
                <el-tag :type="row.status === '1' ? 'success' : 'danger'">
                    {{ banner_status.find(item => item.value === row.status)?.label || '未知' }}
                </el-tag>
            </template>
            <template v-if="prop === 'jumpType'">
                <el-tag>
                    {{ banner_jump_type.find(item => item.value === row.jumpType)?.label || '未知' }}
                </el-tag>
            </template>
        </template>
    </CustomInfo>
  </div>
</template>

<script setup name="Banner">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import CustomInfo from "@/components/CustomInfo";
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref, getCurrentInstance, toRefs, computed } from "vue";
import {
  listBanner,
  getBanner,
  addBanner,
  updateBanner,
  delBanner,
  getCompetitionList
} from "@/api/banner";

const { proxy } = getCurrentInstance();
const { banner_jump_type, banner_status } = proxy.useDict('banner_jump_type', 'banner_status');

// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 表单参数
const form = ref({});
const visible = ref(false);

// 详情弹窗参数
const detailVisible = ref(false);
const currentDetail = ref({});

// 赛事列表
const competitionList = ref([]);

/**
 * 初始化数据
 */
const init = () => {
  getData();
  getCompetitionOptions();
};

/**
 * 请求列表
 */
const getData = () => {
  const query = { ...queryPramas.value };
  query.pageNum = pageNum.value;
  query.pageSize = pageSize.value;

  listBanner(query).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.records || res.data.list || [];
      total.value = res.data.total || 0;
    }
  }).catch(err => {
    console.error('获取轮播图列表失败:', err);
    ElMessage.error('获取轮播图列表失败');
  });
};

/**
 * 获取赛事选项
 */
const getCompetitionOptions = () => {
  getCompetitionList().then(res => {
    if (res.code === 200) {
      competitionList.value = res.data || [];
    }
  }).catch(err => {
    console.error('获取赛事列表失败:', err);
  });
};

/**
 * 筛选查询
 */
const handleSearch = (val) => {
  pageNum.value = 1;
  queryPramas.value = val;
  getData();
};

/**
 * 分页切换
 */
const handleChange = (val) => {
  pageNum.value = val.page;
  pageSize.value = val.limit;
  getData();
};

/**
 * 顶部按钮事件
 */
const handleHeader = (key) => {
  switch (key) {
    case 'add':
      form.value = {
        status: '1',
        jumpType: '0'
      };
      visible.value = true;
      break;
    case 'delete':
      handleBatchDelete();
      break;
  }
};

/**
 * 行操作事件
 */
const handleMenu = (val) => {
  const { index, row, value } = val;
  switch (value) {
    case 'detail':
      getBanner(row.id).then(res => {
        if (res.code === 200) {
          currentDetail.value = res.data;
          detailVisible.value = true;
        }
      }).catch(err => {
        ElMessage.error('获取轮播图详情失败');
      });
      break;
    case 'edit':
      getBanner(row.id).then(res => {
        if (res.code === 200) {
          form.value = res.data;
          visible.value = true;
        }
      }).catch(err => {
        ElMessage.error('获取轮播图详情失败');
      });
      break;
    case 'delete':
      ElMessageBox.confirm('是否确认删除轮播图"' + row.title + '"？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delBanner(row.id).then(res => resMsg(res));
      }).catch(() => {});
      break;
  }
};

/**
 * 多选数据
 */
const selectData = (val) => {
  console.log('选中数据:', val);
};

/**
 * 批量删除
 */
const handleBatchDelete = () => {
  ElMessage.warning('请在表格中选择要删除的数据');
};

/**
 * 保存
 */
const handleSave = (val) => {
  const formData = { ...val };
  if (val.id) {
    updateBanner(formData.id, formData).then(res => resMsg(res));
  } else {
    addBanner(formData).then(res => resMsg(res));
  }
};

/**
 * 响应消息处理
 */
const resMsg = (res) => {
  if (res.code === 200) {
    ElMessage.success(res.msg || '操作成功');
    visible.value = false;
    getData();
  } else {
    ElMessage.error(res.msg || '操作失败');
  }
};

/**
 * 表单变化
 */
const handleFormChange = (val) => {
  const { prop, form } = val;
  // 当跳转类型改变时，清空相关字段
  if (prop === 'jumpType') {
    if (val.value === '0') { // 无跳转
      form.jumpUrl = '';
      form.competitionId = '';
    } else if (val.value === '1') { // 赛事详情
      form.jumpUrl = '';
      // 保留 competitionId，用户可以选择赛事
    } else if (val.value === '2') { // 外部链接
      form.competitionId = '';
      // 保留 jumpUrl，用户可以输入外部链接
    }
  }
};

// 表格配置项
const option = reactive({
  showSearch: true,
  searchLabelWidth: 90,
  /** 搜索字段配置项 */
  searchItem: [
    {
      type: "input",
      label: "标题",
      prop: "title",
      placeholder: "请输入轮播图标题",
      max: 50,
      default: null
    },
    {
      type: "select",
      label: "状态",
      prop: "status",
      default: null,
      filterable: true,
      dicData: banner_status
    },
    {
      type: "select",
      label: "跳转类型",
      prop: "jumpType",
      default: null,
      filterable: true,
      dicData: banner_jump_type
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
      type: 'text',
      label: '轮播图标题',
      prop: 'title',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true
    },
    {
      type: 'img',
      label: '图片预览',
      prop: 'imageUrl',
      width: 60,
      fixed: false,
      sortable: false,
      isShow: true
    },
    {
      type: 'tag',
      label: '跳转类型',
      prop: 'jumpType',
      width: 120,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: banner_jump_type
    },

    {
      type: 'text',
      label: '关联赛事',
      prop: 'competitionId_dictText',
      width: 150,
      fixed: false,
      sortable: false,
      isShow: true
    },
    {
      type: 'tag',
      label: '状态',
      prop: 'status',
      width: 100,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: banner_status
    },
    {
      type: 'text',
      label: '创建时间',
      prop: 'createTime',
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
});

// 表单配置项
const optionDialog = reactive({
  dialogTitle: '轮播图信息',
  dialogClass: 'dialog_md',
  labelWidth: '120px',
  formitem: [
    {
      type: "input",
      label: "轮播图标题",
      prop: "title",
      placeholder: "请输入轮播图标题",
      max: 100,
      default: null
    },
    {
      type: "upload",
      label: "轮播图片",
      prop: "imageUrl",
      fileType: "img",
      path: "banner",
      uploadType: "ali",
      default: null
    },
    {
      type: "select",
      label: "跳转类型",
      prop: "jumpType",
      default: '0',
      dicData: banner_jump_type,
      judge: (form)=>{
        let result=  form.jumpType === '0';
        if(result){
          form.jumpUrl='';
          form.competitionId=''
        }
        return true;
      }
    },
    {
      type: "input",
      label: "跳转地址",
      prop: "jumpUrl",
      placeholder: "请输入跳转地址",
      max: 500,
      disabled: true,
      judge: (form) => {
        let result=  form.jumpType === '1';
        if(result){
          form.jumpUrl='/pages/competition/detail'
        }
        return result;
      }
    },
    {
      type: "custom",
      label: "关联赛事",
      prop: "competitionId",
      default: null,
      judge: (form) => form.jumpType === '1'
    },
    {
      type: "radio",
      label: "状态",
      prop: "status",
      default: '1',
      dicData: banner_status
    },
    {
      type: "input",
      label: "备注",
      prop: "remark",
      span: 24,
      default: null
    }
  ],
  rules: {
    title: [{ required: true, message: '请输入轮播图标题', trigger: 'blur' }],
    imageUrl: [{ required: true, message: '请上传轮播图片', trigger: 'change' }],
    jumpType: [{ required: true, message: '请选择跳转类型', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }],
    competitionId: [
      {
        required: true,
        message: '请选择关联赛事',
        validator: (rule, value, callback) => {
          // 获取当前表单数据
          const currentForm = form.value;
          if (currentForm.jumpType === '1' && !value) {
            callback(new Error('选择赛事详情时必须选择关联赛事'));
          } else {
            callback();
          }
        },
        trigger: 'change'
      }
    ]
  }
});

// CustomInfo配置项 - 使用计算属性支持条件显示
const optionInfo = computed(() => ({
    dialogClass: 'dialog_lg',
    labelWidth: '30',
    tableInfoItem: [
        {
            title: '基本信息',
            column: 2,
            infoData: [
                { 
                    type: 'text', 
                    label: '轮播图标题', 
                    prop: 'title', 
                    isShow: true 
                },
                { 
                    type: 'custom', 
                    label: '状态', 
                    prop: 'status', 
                    isShow: true 
                },
                { 
                    type: 'custom', 
                    label: '跳转类型', 
                    prop: 'jumpType', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '关联赛事', 
                    prop: 'competitionId_dictText', 
                    isShow: currentDetail.value?.jumpType === '1'
                }
            ]
        },
        {
            title: '轮播图片',
            column: 1,
            infoData: [
                { 
                    type: 'img', 
                    label: '图片', 
                    prop: 'imageUrl', 
                    imgWidth: 400,
                    fit: 'contain',
                    isShow: !!currentDetail.value?.imageUrl
                }
            ]
        },
        {
            title: '备注信息',
            column: 1,
            infoData: [
                { 
                    type: 'text', 
                    label: '备注内容', 
                    prop: 'remark', 
                    isShow: !!currentDetail.value?.remark
                }
            ]
        },
        {
            title: '创建信息',
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
                    prop: 'createBy_dictText', 
                    isShow: true 
                },
                { 
                    type: 'text', 
                    label: '更新人', 
                    prop: 'updateBy_dictText', 
                    isShow: true 
                }
            ]
        }
    ]
}));

// 初始化
init();
</script>

<style scoped>
.el-tag {
  margin: 0;
}

:deep(.el-button + .el-button) {
  margin-left: 8px;
}


</style>