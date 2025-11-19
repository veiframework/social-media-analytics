<template>
  <div>
    <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
                 :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
                 @currentChange="handleChange" @selectData="selectData">
      <template #table-top></template>
      <template #table-custom="{ row, prop, index }">
        <!-- 状态开关 -->
        <div v-if="prop === 'statusConfig'">
          <el-switch
            v-model="row.statusSwitch"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
            @change="handleStatusChange(row)"
            :loading="row.statusLoading"
          />
        </div>
      </template>
    </CustomTable>

    <!-- 商品分类表单弹窗 -->
    <CustomDialog :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
                  @save="handleSave" @formChange="handleFormChange">
    </CustomDialog>

    <!-- 商品分类详情弹窗 -->
    <CustomInfo :visible="detailVisible" :option="optionInfo" :rowData="currentDetail" @cancel="detailVisible = false" />
  </div>
</template>

<script setup name="GoodsType">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import CustomInfo from "@/components/CustomInfo";
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref, getCurrentInstance } from "vue";
import {
  listGoodsType,
  addGoodsType,
  updateGoodsType,
  delGoodsType,
  updateGoodsTypeStatus
} from "@/api/goods";

const { proxy } = getCurrentInstance();

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

/**
 * 初始化数据
 */
const init = () => {
  getData();
};

/**
 * 请求列表
 */
const getData = () => {
  const query = { ...queryPramas.value };
  query.pageNum = pageNum.value;
  query.pageSize = pageSize.value;
  query.parentId = -1
  listGoodsType(query).then(res => {
    if (res.code === 200) {
      const list = res.data.records || res.data.list || [];
      // 为每个分类添加开关状态
      tableData.value = list.map(item => ({
        ...item,
        statusSwitch: item.status,
        statusLoading: false
      }));
      total.value = res.data.total || 0;
    }
  }).catch(err => {
    console.error('获取商品分类列表失败:', err);
    ElMessage.error('获取商品分类列表失败');
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
        status: 1,
        parentId: -1
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
      currentDetail.value = row;
      detailVisible.value = true;
      break;
    case 'edit':
      form.value = { ...row };
      visible.value = true;
      break;
    case 'delete':
      ElMessageBox.confirm('是否确认删除商品分组"' + row.name + '"？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delGoodsType({ id: row.id }).then(res => resMsg(res));
      }).catch(() => {});
      break;
  }
};

/**
 * 状态变更
 */
const handleStatusChange = (row) => {
  row.statusLoading = true;
  updateGoodsTypeStatus({
    id: row.id,
    status: row.statusSwitch
  }).then(res => {
    row.statusLoading = false;
    if (res.code === 200) {
      ElMessage.success('状态更新成功');
      row.status = row.statusSwitch;
    } else {
      ElMessage.error(res.msg || '状态更新失败');
      row.statusSwitch = row.status; // 恢复原状态
    }
  }).catch(err => {
    row.statusLoading = false;
    ElMessage.error('状态更新失败');
    row.statusSwitch = row.status; // 恢复原状态
  });
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
    updateGoodsType(formData).then(res => resMsg(res));
  } else {
    addGoodsType(formData).then(res => resMsg(res));
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
  // 可以在这里处理表单字段变化的逻辑
};

// 表格配置项
const option = reactive({
  showSearch: true,
  searchLabelWidth: 90,
  /** 搜索字段配置项 */
  searchItem: [
    {
      type: "input",
      label: "分组名称",
      prop: "name",
      placeholder: "请输入分类名称",
      max: 50,
      default: null
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
      label: '分组名称',
      prop: 'name',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true
    },
    
    // {
    //   type: 'custom',
    //   label: '状态配置',
    //   prop: 'statusConfig',
    //   width: 120,
    //   fixed: false,
    //   sortable: false,
    //   isShow: true
    // },
    
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
  dialogTitle: '商品分类信息',
  dialogClass: 'dialog_md',
  labelWidth: '120px',
  formitem: [
    {
      type: "input",
      label: "分组名称",
      prop: "name",
      placeholder: "请输入分组名称",
      max: 50,
      default: null
    },
    {
      type: "number",
      label: "排序序号",
      prop: "serial",
      placeholder: "请输入排序序号",
      min: 0,
      default: 1
    },
    {
      type: "radio",
      label: "状态",
      prop: "status",
      default: 1,
      dicData: [
        { label: '启用', value: 1 },
        { label: '禁用', value: 0 }
      ]
    }
  ],
  rules: {
    name: [{ required: true, message: '请输入分组名称', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }
});

// 详情配置项
const optionInfo = reactive({
  dialogTitle: '商品分组详情',
  dialogClass: 'dialog_md',
  labelWidth: '30',
  tableInfoItem: [
    {
      title: '基本信息',
      column: 2,
      infoData: [
        {
          type: 'text',
          label: '分组名称',
          prop: 'name',
          span: 1,
          isShow: true
        },
        {
          type: 'text',
          label: '排序序号',
          prop: 'serial',
          span: 1,
          isShow: true
        },
        {
          type: 'tag',
          label: '状态',
          prop: 'status',
          span: 1,
          isShow: true,
          dicData: [
            { label: '启用', value: 1, type: 'success' },
            { label: '禁用', value: 0, type: 'danger' }
          ]
        }
      ]
    },
    
  ]
});

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
