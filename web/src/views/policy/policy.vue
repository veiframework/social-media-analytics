<template>
  <div>
    <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
                 :total="total" @headerchange="handleHeader" :pageSize="pageSize" @currentChange="handleChange"
                 @menuChange="handleMenuBtn">
    </CustomTable>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @close="handleDialogClose">
      <policy-form 
        ref="formRef"
        :type="dialogType" 
        :data="currentData"
        @callback="handleFormCallback"
      />
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="政策文件详情" width="800px" @close="handleDetailDialogClose">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">
          <div v-html="detailData.content" style="white-space: pre-wrap;"></div>
        </el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.sequence }}</el-descriptions-item>
        <el-descriptions-item label="启用/禁用">{{ detailData.disabled =='0' ? '启用':'禁用' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="Policy">
import CustomTable from "@/components/CustomTable";
import {reactive, ref, computed, onMounted, nextTick} from "vue";
import {ElLoading} from "element-plus";
import useUserStore from '@/store/modules/user';
import {getPolicyList, addPolicy, updatePolicy, deletePolicy, getPolicyDetail} from "@/api/policy";
import PolicyForm from './policy-form.vue';

const {proxy} = getCurrentInstance();
const userStore = useUserStore();

// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 弹窗相关
const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref('add');
const currentData = ref({});
const formRef = ref();

// 详情弹窗相关
const detailDialogVisible = ref(false);
const detailData = ref({});

/**
 * 请求列表
 */
const getData = () => {
  const query = {...queryPramas.value}
  query.pageNum = pageNum.value;
  query.pageSize = pageSize.value;
  getPolicyList(query).then(res => {
    tableData.value = res.data.records;
    total.value = res.data.total;
  })
}

/**
 * 筛选查询
 */
const handleSearch = (val) => {
  pageNum.value = 1;
  queryPramas.value = val;
  getData();
}

/**
 * 分页查询
 */
const handleChange = (val) => {
  pageNum.value = val.page;
  pageSize.value = val.limit;
  getData();
}

/**
 * 表单回调处理
 */
const handleFormCallback = () => {
  dialogVisible.value = false;
  getData();
}

/**
 * 获取详情数据
 */
const getDetail = async (id) => {
  try {
    const res = await getPolicyDetail(id);
    if (res.data) {
      currentData.value = res.data;
      dialogVisible.value = true;
    }
      } catch (error) {
    proxy.$modal.msgError('获取详情失败');
  }
}

const getDetail2 = async (id) => {
  try {
    const res = await getPolicyDetail(id);
    if (res.data) {
      detailData.value = res.data;
      detailDialogVisible.value = true;
    }
  } catch (error) {
    proxy.$modal.msgError('获取详情失败');
  }
}

/**
 * 表格左侧按钮
 */
const handleHeader = (key) => {
  switch (key) {
    case "add":
      // 先重置所有状态
      dialogVisible.value = false;
      currentData.value = {};
      dialogType.value = 'add';
      dialogTitle.value = '新增政策文件';
      // 使用nextTick确保状态更新后再显示弹窗
      nextTick(() => {
        dialogVisible.value = true;
      });
      break;
    case "edit":
      if (tableData.value && tableData.value.length > 0) {
        // 先重置所有状态
        dialogVisible.value = false;
        currentData.value = {};
        dialogType.value = 'edit';
        dialogTitle.value = '编辑政策文件';
        // 获取详情后再显示弹窗
        getDetail(tableData.value[0].id);
      } else {
        proxy.$modal.msgError('请先选择要编辑的记录');
      }
      break;
    case "delete":
      if (tableData.value && tableData.value.length > 0) {
        proxy.$modal.confirm('确认删除选中的政策文件吗？').then(() => {
          deletePolicy(tableData.value[0].id).then(() => {
            proxy.$modal.msgSuccess('删除成功');
            getData();
          });
        });
      } else {
        proxy.$modal.msgError('请先选择要删除的记录');
      }
      break;
  }
}

/**
 * 操作按钮点击事件
 */
const handleMenuBtn = (val) => {
  if (val.value === 'view') {
    getDetail2(val.row.id);
  } else if (val.value === 'edit') {
    // 先重置所有状态
    dialogVisible.value = false;
    currentData.value = {};
    dialogType.value = 'edit';
    dialogTitle.value = '编辑政策文件';
    // 获取详情后再显示弹窗
    getDetail(val.row.id);
  } else if (val.value === 'delete') {
    proxy.$modal.confirm('确认删除该政策文件吗？').then(() => {
      deletePolicy(val.row.id).then(() => {
        proxy.$modal.msgSuccess('删除成功');
        getData();
      });
    });
  }
}

/**
 * 弹窗关闭处理
 */
const handleDialogClose = () => {
  dialogVisible.value = false;
  // 延迟重置状态，确保弹窗完全关闭
  setTimeout(() => {
    dialogType.value = 'add';
    dialogTitle.value = '';
    currentData.value = {};
  }, 100);
}

/**
 * 详情弹窗关闭处理
 */
const handleDetailDialogClose = () => {
  detailDialogVisible.value = false;
  detailData.value = {};
}

// 表格配置项
const option = reactive({
  showSearch: true,  // 显示隐藏
  searchLabelWidth: 100,  // 搜索标题宽度
  /** 搜索字段配置项 */
  searchItem: [
    {type: "input", label: "标题", prop: "title", max: 99, verify: "", default: null, span: 2}
  ],
  /** 表格顶部左侧 button 配置项 */
  headerBtn: [
    {
      key: "add", text: "新增", icon: "Plus", isShow: true, type: "primary", disabled: false,
      hasPermi: ['com.chargehub.locker.askquestion.domain.ChargePolicyInformation:crud:CREATE']
    }
  ],
  operation: [],
  /** 表格顶部右侧 toobar 配置项 */
  toolbar: {isShowToolbar: true, isShowSearch: true},
  openSelection: false,		// 是否开启多选
  /** 序号下标配置项 */
  index: {
    openIndex: true,
    indexFixed: true,
    indexWidth: 70
  },
  /** 行列合并配置项 */
  methods: () => { },
  /** 表格字段配置项 */
  tableItem: [
    {type: 'text', label: '标题', prop: 'title', width: 200, fixed: false, sortable: false, isShow: true},
    {type: 'text', label: '内容', prop: 'content', width: 300, fixed: false, sortable: false, isShow: false},
    {
      type: "tag", label: "启用/禁用", prop: "disabled", width: 110, sortable: false, isShow: true, isOmit: true,
      dicData: [{ label: '启用', value: 0, type: 'success' }, { label: '禁用', value: 1 }]
    },
    {type: 'text', label: '创建人', prop: 'createBy', width: 120, fixed: false, sortable: false, isShow: false},
    {type: 'text', label: '创建时间', prop: 'createTime', width: 160, fixed: false, sortable: false, isShow: true},
    {type: 'text', label: '更新人', prop: 'updateBy', width: 120, fixed: false, sortable: false, isShow: false},
    {type: 'text', label: '更新时间', prop: 'updateTime', width: 160, fixed: false, sortable: false, isShow: true}
  ],
  /** 操作菜单配置项 */
  menu: {
    isShow: true,
    width: 180,
    fixed: 'right',
  },
  menuItemBtn: [
    {
      text: '查看详情',
      type: 'primary',
      icon: 'View',
      isShow: true,
      hasPermi: ['com.chargehub.locker.askquestion.domain.ChargePolicyInformation:crud:DETAILS'],
      value: 'view'
    },
    {
      text: '编辑',
      type: 'primary',
      icon: 'Edit',
      isShow: true,
      hasPermi: ['com.chargehub.locker.askquestion.domain.ChargePolicyInformation:crud:EDIT'],
      value: 'edit'
    },
    {
      text: '删除',
      type: 'danger',
      icon: 'Delete',
      isShow: true,
      hasPermi: ['com.chargehub.locker.askquestion.domain.ChargePolicyInformation:crud:DELETE'],
      value: 'delete'
    }
  ],
  /** page 分页配置项 */
  isShowPage: true,
})

// 初始化加载数据
onMounted(() => {
  getData();
})
</script> 

<style scoped lang="scss"></style> 