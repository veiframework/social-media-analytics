<template>
  <div>


    <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
                 :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
                 @currentChange="handleChange">
      <template #table-top></template>
      <template #table-custom="{ row, prop, index }">
        
      </template>
    </CustomTable>

    <!-- 提成比例配置弹窗 -->
    <CustomDialog :form="commissionForm" :option="commissionDialogOption" :visible="commissionVisible" 
                  @cancel="commissionVisible = false" @save="handleCommissionSave">
    </CustomDialog>
   
  </div>
</template>

<script setup name="CommissionRecord">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import { ElMessage, ElMessageBox } from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";
import { reactive, ref, getCurrentInstance, computed } from "vue";
import { listCommissionRecord, updateCommissionStatus, exportCommissionRecord, getCommissionStats, getCommissionConfig, updateCommissionRate } from "@/api/commission";

const { proxy } = getCurrentInstance();

// 表格参数
const queryParams = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 状态更新弹窗参数
const statusVisible = ref(false);
const statusForm = ref({});

// 提成比例配置弹窗参数
const commissionVisible = ref(false);
const commissionForm = ref({});

// 统计数据
const stats = ref({
  totalCommission: 0,
  totalWithdraw: 0,
  totalRecords: 0,
  pendingRecords: 0
});

// 表格配置
const option = reactive({
  showSearch: true,
  searchLabelWidth: 80,
  toolbar: { isShowToolbar: true, isShowSearch: true },
   /** 序号下标配置项 */
   index: {
    openIndex: true,
    indexFixed: true,
    indexWidth: 70
  },
  searchItem: [
    {
      label: "状态",
      prop: "status",
      type: "select",
      placeholder: "请选择类型",
      dicData: [
      { label: "已审批", value: "received", type: 'success' },
      { label: "待审批", value: "waiting", type: 'info' }
      ]
    } 
  ],
  tableItem: [
    {
      type: 'text',
      label: '提现人',
      prop: 'sourceLoginId_dictText',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true
    },
    {
      type: 'text',
      label: '手机号',
      prop: 'phone',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true
    },
    {
      type: 'text',
      label: '提现金额',
      prop: 'amount',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true,
       
    },
     
    {
      type: 'tag',
      label: '状态',
      prop: 'status',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true,
      dicData: [
        { label: "已审批", value: "received", type: 'success' },
        { label: "待审批", value: "waiting", type: 'info' }
      ]
    },
      
    {
        type: 'text',
      label: '申请时间',
      prop: 'createTime',
      width: 200,
      fixed: false,
      sortable: false,
      isShow: true
    }
  ],
  menu:  
    {
        isShow: true,
    width: 180,
    fixed: 'right'
    }
   ,
  menuItemBtn: [
    
    {
      type: 'primary',
      isShow: true,
      judge: (form) => {
        return form.status === 'waiting';
      },
      icon: 'Edit',
      label: '审批通过',
      value: 'approve'
    },
     
  ],
  headerBtn: [
  { key: "commissionRate", text: "提成比例", icon: "Edit", isShow: true, type: "warning", disabled: false },
  ]
});

// 提成比例配置弹窗配置
const commissionDialogOption = reactive({
  dialogTitle: "提成比例配置",
  rules: {
    commissionRate: [{ required: true,  trigger: 'blur' ,validator: (rule, value, callback) => {
            if (value === undefined) {
                callback(new Error('请输入提成比例'))
            }
            if (value >= 100) {
                callback(new Error('比例需要小于100'))
            }
            if (value <= 0) {
                callback(new Error('比例需要大于0'))
            }
            callback()
        },}],
  },
  formitem: [
     
    { 
            type: "input", 
            label: "提成比例(%)",
            prop: "commissionRate",
            placeholder: "请输入提成比例",
            category: "number",
            min: 1,
            default: computed(() => {
                return commissionForm.value.commissionRate || 1;
            })
        },
    
  ]
});

 

/**
 * 初始化数据
 */
const init = () => {
  getData();
};

/**
 * 获取列表数据
 */
const getData = () => {
  const query = { 
    ...queryParams.value, 
    pageNum: pageNum.value, 
    pageSize: pageSize.value ,
    type: 'withdraw'
  };
  
  listCommissionRecord(query).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.records || [];
      total.value = res.data.total || 0;
      tableData.value.forEach(item => {
        item.amount = Math.abs(item.amount);
      });
    }
  }).catch(error => {
    console.error('获取提成记录列表失败:', error);
    ElMessage.error('获取数据失败');
  });
};

/**
 * 搜索
 */
const handleSearch = (params) => {
  queryParams.value = params;
  pageNum.value = 1;
  getData();
};

/**
 * 页码改变
 */
const handleChange = (page) => {
  pageNum.value = page;
  getData();
};

/**
 * 头部按钮点击
 */
const handleHeader = (key) => {
  if (key === 'commissionRate') {
    handleCommissionRate();
  }
};

/**
 * 菜单操作
 */
const handleMenu = (val) => {
    const { index, row, value } = val;
  if (value === 'approve') {
    handleApprove(row);
  }
};

/**
 * 审批通过
 */
const handleApprove = (row) => {
  ElMessageBox.confirm(`确认审批通过该提现申请吗？\n提现金额：¥${Math.abs(row.amount)}`, "审批确认", {
    confirmButtonText: "确认审批",
    cancelButtonText: "取消",
    type: "warning",
    dangerouslyUseHTMLString: false
  }).then(() => {
    // 直接更新状态为已审批
    const updateData = {
      id: row.id,
      status: 'received'
    };
    
    updateCommissionStatus(updateData).then(res => {
      if (res.code === 200) {
        ElMessage.success('审批通过成功');
        getData(); // 刷新列表
      } else {
        ElMessage.error(res.msg || '审批失败');
      }
    }).catch(error => {
      console.error('审批失败:', error);
      ElMessage.error('审批失败');
    });
  }).catch(() => {
    // 用户取消操作，不做任何处理
  });
};

/**
 * 提成比例配置
 */
const handleCommissionRate = () => {
  // 获取当前提成配置
  getCommissionConfig().then(res => {
    if (res.code === 200) {
      commissionForm.value = {
        commissionRate: res.data?.commissionRate || 0,
        remark: res.data?.remark || ''
      };
      console.log(commissionForm.value);
    } else {
      // 如果获取失败，使用默认值
      commissionForm.value = {
        commissionRate: 0,
        remark: ''
      };
    }
    commissionVisible.value = true;
  }).catch(error => {
    console.error('获取提成配置失败:', error);
    // 出错时也显示弹窗，使用默认值
    commissionForm.value = {
      commissionRate: 0,
      remark: ''
    };
    commissionVisible.value = true;
  });
};

/**
 * 保存提成比例配置
 */
const handleCommissionSave = (form) => {
  updateCommissionRate(form).then(res => {
    if (res.code === 200) {
      ElMessage.success('提成比例配置更新成功');
      commissionVisible.value = false;
    } else {
      ElMessage.error(res.msg || '提成比例配置更新失败');
    }
  }).catch(error => {
    console.error('更新提成比例配置失败:', error);
    ElMessage.error('提成比例配置更新失败');
  });
};

/**
 * 更新状态
 */
const handleStatusUpdate = (row) => {
  statusForm.value = {
    id: row.id,
    status: row.status
  };
  statusVisible.value = true;
};

/**
 * 保存状态更新
 */
const handleStatusSave = (form) => {
  updateCommissionStatus(form).then(res => {
    if (res.code === 200) {
      ElMessage.success('状态更新成功');
      statusVisible.value = false;
      getData(); // 刷新列表
    } else {
      ElMessage.error(res.msg || '状态更新失败');
    }
  }).catch(error => {
    console.error('更新状态失败:', error);
    ElMessage.error('状态更新失败');
  });
};

/**
 * 获取统计数据
 */
const getStatsData = () => {
  getCommissionStats(queryParams.value).then(res => {
    if (res.code === 200) {
      stats.value = res.data || {};
    }
  }).catch(error => {
    console.error('获取统计数据失败:', error);
  });
};

/**
 * 导出数据
 */
const handleExport = () => {
  ElMessageBox.confirm('是否确认导出所有提成记录数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    const query = { ...queryParams.value };
    exportCommissionRecord(query).then(response => {
      // 创建下载链接
      const blob = new Blob([response], { 
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
      });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `提成记录_${proxy.parseTime(new Date(), '{y}{m}{d}_{h}{i}{s}')}.xlsx`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      ElMessage.success('导出成功');
    }).catch(error => {
      console.error('导出失败:', error);
      ElMessage.error('导出失败');
    });
  });
};

// 初始化
init();
</script>

<style lang="scss" scoped>
.stats-cards {
  .box-card {
    border: none;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
      transform: translateY(-2px);
    }
  }
  
  .stat-item {
    text-align: center;
    padding: 10px;
    
    .stat-value {
      font-size: 24px;
      font-weight: bold;
      line-height: 1.2;
      margin-bottom: 8px;
    }
    
    .stat-label {
      font-size: 14px;
      color: #909399;
      font-weight: normal;
    }
  }
}

:deep(.el-card__body) {
  padding: 20px !important;
}
</style>
