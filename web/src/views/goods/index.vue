<template>
  <div>
    <CustomTable :data="tableData" :option="option" @search="handleSearch" :pageNum="pageNum" @refresh="getData"
                 :total="total" @headerchange="handleHeader" @menuChange="handleMenu" :pageSize="pageSize"
                 @currentChange="handleChange" @selectData="selectData">
                <template #table-custom="{ row, prop, index }">
                <template v-if="prop == 'type'">
                    {{ row.typeVo?.name }}
                </template>
                <template v-else-if="prop == 'promotion'">
                    <el-switch style="margin-right: 10px;" v-model="row.isPromotion" width="55" inline-prompt
                        active-text="开启" inactive-text="关闭" @change="handlePromotionChange(row)" :active-value="1"
                        :inactive-value="0" />
                </template>
                <template v-else-if="prop == 'banner'">
                    <el-switch style="margin-right: 10px;" v-model="row.isBanner" width="55" inline-prompt
                        active-text="使用" inactive-text="取消" @change="handleBannerChange(row)" :active-value="1"
                        :inactive-value="0" />
                </template>
                <template v-else-if="prop == 'status'">
                    <el-switch style="margin-right: 10px;" v-model="row.status" width="55" inline-prompt
                        active-text="上架" inactive-text="下架" @change="handleStatusChange(row)" :active-value="1"
                        :inactive-value="0" />
                </template>
            </template>

    </CustomTable>

    <!-- 商品表单弹窗 -->
    <CustomDialog ref="formRef" :form="form" :option="optionDialog" :visible="visible" @cancel="visible = false"
                  @save="handleSave" @formChange="handleFormChange">
                  <template #custom-item="{ prop, form }">
                <div v-if="prop == 'goodsSpecificationVoList'" style="width: 100%;">
                    <el-table :data="form.goodsSpecificationVoList" style="width: 100%;" border>
                        <el-table-column prop="specification" label="规格名" align="center">
                            <template #default="scoped">
                                <el-input v-model="scoped.row.specification"
                                    @input="(val) => changeSpecification(scoped.$index,val)" placeholder="请输入规格名"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column prop="value" label="规格值" align="center">
                            <template #default="scoped">
                                <el-input v-model="scoped.row.value" placeholder="请输入规格值"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column prop="picUrl" label="规格图片" align="center" width="110">
                            <template #default="scoped">
                                <el-upload class="specification" :show-file-list="false"
                                    :before-upload="beforeAvatarUpload"
                                    :http-request="(req) => uploadImg(req, 'goods', scoped.$index)">
                                    <el-image v-if="scoped.row.picUrl" style="width: 70px; height: 70px"
                                        :src="scoped.row.picUrl" fit="cover" />
                                    <el-icon v-else>
                                        <Plus />
                                    </el-icon>
                                </el-upload>
                            </template>
                        </el-table-column>
                        <el-table-column prop="amount" label="原价(元)" align="center" width="120">
                            <template #default="scoped">
                                <el-input v-model="scoped.row.amount"
                                    @input="(val) => scoped.row.amount = verifyAmount(val)"
                                    placeholder="请输入原价"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column prop="memAmount" label="会员价(元)" align="center" width="120" v-if="setting.goods.memberPrice">
                            <template #default="scoped">
                                <el-input v-model="scoped.row.memAmount"
                                    @input="(val) => scoped.row.memAmount = verifyAmount(val)"
                                    placeholder="请输入会员价"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="100" align="center">
                            <template #default="scoped">
                                <el-button type="danger" link @click="delSpecification(scoped.$index)"
                                    icon="Delete">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-button type="primary" icon="Plus" @click="addSpecification" class="add">新增</el-button>
                </div>
            </template>

    </CustomDialog>

    <!-- 商品详情弹窗 -->
    <CustomInfo :visible="detailVisible" :option="optionInfo" :rowData="currentDetail" @cancel="detailVisible = false">
      <template #custom="{ prop, row }">
                <div v-if="prop == 'type'">
                    {{ row.typeVo?.name }}
                </div>
                <div v-if="prop == 'goodsSpecificationVoList'" style="width: 90%">
                    <el-table :data="row.goodsSpecificationVoList" style="width: 100%" border>
                        <el-table-column prop="specification" label="规格名" align="center" />
                        <el-table-column prop="value" label="规格值" align="center" />
                        <el-table-column prop="picUrl" label="规格图片" align="center" width="110">
                            <template #default="scoped">
                                <el-image v-if="scoped.row.picUrl" style="width: 80px; height: 80px"
                                    :src="scoped.row.picUrl" fit="cover" />
                            </template>
                        </el-table-column>
                        <el-table-column prop="amount" label="原价" align="center" width="110" />
<!--                        <el-table-column prop="memAmount" label="会员价" align="center" width="110" />-->
                    </el-table>
                </div>
            </template>

    </CustomInfo>
  </div>
</template>

<script setup name="Goods">
import CustomTable from "@/components/CustomTable";
import CustomDialog from "@/components/CustomDialog";
import CustomInfo from "@/components/CustomInfo";
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref, getCurrentInstance } from "vue";
import setting from '@/settings'
import {
  listGoods,
  getGoods,
  addGoods,
  updateGoods,
  delGoods,
  updateGoodsStatus,
  updateGoodsBanner,
  updateGoodsPromotion,
  getShopTypeList,
  listGoodsType
} from "@/api/goods";
import { uploadLocalFile } from "@/utils/uploadFile";
import { ElLoading } from "element-plus";
const { proxy } = getCurrentInstance();
const regularAmount = /^\d+(\.\d{1,2})?$/
const regularNumberDo = /[^0-9.]/g
// 表格参数
const queryPramas = ref({});
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 表单参数
const form = ref({});
const visible = ref(false);
const formRef = ref({});

const formRules = ref({
  goodsNo: [{ required: true, message: '请输入商品编号', trigger: 'blur' }],
  img: [{ required: true, message: '请上传商品主图', trigger: 'change' }],
  type: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入商品价格', trigger: 'blur' }, { pattern: regularAmount, message: '请输入正确的价格', trigger: 'blur' }],
  ...(setting.goods.memberPrice? {memAmount: [{ required: true, message: '请输入商品会员价', trigger: 'blur' }, { pattern: regularAmount, message: '请输入正确的价格', trigger: 'blur' }]}:{}),
  unit: [{ required: true, message: '请输入商品单位', trigger: 'blur' }],
  goodsSpecificationVoList: [{ required: true, message: '请录入商品规格', trigger: 'change' }],
});


// 详情弹窗参数
const detailVisible = ref(false);
const currentDetail = ref({});

// 商品分类列表
const goodsTypeList = ref([]);
const changeSpecification = (index,val) => {
    if (formRef.value?.formRef?.form) { formRef.value.formRef.form.goodsSpecificationVoList[index].specification = val};
}
const delSpecification = (index) => {
    if (formRef.value?.formRef?.form) formRef.value.formRef.form.goodsSpecificationVoList?.splice(index, 1);
}
const addSpecification = () => {
    if (formRef.value?.formRef?.form) {
        if (!formRef.value.formRef.form.goodsSpecificationVoList) formRef.value.formRef.form.goodsSpecificationVoList = []
        formRef.value.formRef.form.goodsSpecificationVoList?.push({ specification: '', value: '', picUrl: '', amount: 0, memAmount: 0 })
        changeSpecification(formRef.value.formRef.form.goodsSpecificationVoList[0].specification)
    }
}
const uploadImg = async (req, path, index) => {
    const loading = ElLoading.service({ lock: true, text: "上传中。。。", background: "rgba(0, 0, 0, 0.7)" });
    await uploadLocalFile({ file: req.file }).then((res) => {
        loading.close();
        if (formRef.value?.formRef?.form) formRef.value.formRef.form.goodsSpecificationVoList[index].picUrl = res;
    })
}
/**
 * 初始化数据
 */
const init = () => {
  getPramasData(()=>{
    getData();

  });
  

};

/**
 * 请求列表
 */
const getData = () => {
  const query = { ...queryPramas.value };
  query.pageNum = pageNum.value;
  query.pageSize = pageSize.value;

  listGoods(query).then(res => {
    if (res.code === 200) {
      const list = res.data.records || res.data.list || [];
      // 为每个商品添加开关状态
      tableData.value = list.map(item => ({
        ...item,
        statusSwitch: item.status,
        isBannerSwitch: item.isBanner,
        isPromotionSwitch: item.isPromotion,
        statusLoading: false,
        bannerLoading: false,
        promotionLoading: false
      }));
      total.value = res.data.total || 0;
    }
  }).catch(err => {
    console.error('获取商品列表失败:', err);
    ElMessage.error('获取商品列表失败');
  });
};
const verifyAmount = (value)=> {
	if (isNaN(value)) return '';
	// @ts-ignore
	return value.toString() ? value.toString().match(/^\d+(?:\.\d{0,2})?/)[0] : '';
}
/**
 * 获取商品分类选项
 */
const getGoodsTypeOptions = () => {
  getShopTypeList(12).then(res => {
    if (res.code === 200) {
      goodsTypeList.value = res.data.records || [];
    }
  }).catch(err => {
    console.error('获取商品分类列表失败:', err);
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
        isBanner: 0,
        isPromotion: 0,
        memAmount: 0
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
      getGoods(row.id).then(res => {
        if (res.code === 200) {
          currentDetail.value = res.data;
          detailVisible.value = true;
        }
      }).catch(err => {
        ElMessage.error('获取商品详情失败');
      });
      break;
    case 'edit':
      getGoods(row.id).then(res => {
        if (res.code === 200) {
          form.value = res.data;
          visible.value = true;
        }
      }).catch(err => {
        ElMessage.error('获取商品详情失败');
      });
      break;
    case 'delete':
      ElMessageBox.confirm('是否确认删除商品"' + row.name + '"？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delGoods({ id: row.id }).then(res => resMsg(res));
      }).catch(() => {});
      break;
  }
};

/**
 * 上下架状态变更
 */
const handleStatusChange = (row) => {
  if (!row.id) return;
  row.statusLoading = true;
  updateGoodsStatus({
    id: row.id,
    status: row.status ? 1 : 0
  }).then(res => commonMsg(res));
};

/**
 * 轮播图设置变更
 */
const handleBannerChange = (row) => {
  if (!row.id) return;
  row.bannerLoading = true;
  updateGoodsBanner({
    id: row.id,
    isBanner: row.isBanner ? 1 : 0
  }).then(res => commonMsg(res));
};

/**
 * 促销设置变更
 */
const handlePromotionChange = (row) => {
  if (!row.id) return;
  row.promotionLoading = true;
   
  updateGoodsPromotion({
    id: row.id,
    isPromotion: row.isPromotion ? 1 : 0
  }).then(res => commonMsg(res));
};

const commonMsg = (res) => {
    if (res.code == 200) {
        ElMessage.success(res.msg);
        visible.value = false;
        getData();
    } else {
        ElMessage.error(res.msg)
    }
}
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
  formData.shopId = 12;
  for (const item of formData.goodsSpecificationVoList) {
      if (!item.specification || !item.value) {
          ElMessage.warning("请将商品规格补充完整！");
          return;
      }
      item.vipAmount = item.memAmount;
  }
  formData.vipAmount = formData.memAmount;
  if (val.id) {
    updateGoods(formData).then(res => resMsg(res));
  } else {
    addGoods(formData).then(res => resMsg(res));
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
const typeList = ref([]);

const getPramasData = (callback) => {
  listGoodsType({ shopId: 12, pageNum: 1, pageSize: 1000,parentId: -2 }).then((res) => {
    typeList.value = res.data.records
    typeList.value.forEach(i=>{
      if(i.parentName){
        i.name = i.parentName+"-"+i.name
      }
    })
    if(callback){
      callback()
    }
  })
}
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
      label: "商品编号",
      prop: "goodsNo",
      placeholder: "请输入商品编号",
      max: 50,
      default: null
    },
    {
      type: "select",
      label: "商品分类",
      prop: "type",
      placeholder: "请选择商品分类",
      default: null,
      customValue: "id", customLabel: "name", filterable: true,
      dicData: computed(() => { return typeList.value })
    },
    {
      type: "input",
      label: "商品名称",
      prop: "name",
      placeholder: "请输入商品名称",
      max: 100,
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
  { type: 'text', label: '商品编号', prop: 'goodsNo', width: 120, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'custom', label: '商品分类', prop: 'type', width: 120, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '商品名称', prop: 'name', width: 120, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '商品价格', prop: 'amount', width: 120, fixed: false, sortable: false, isShow: true, isOmit: false },
        { type: 'text', label: '会员价', prop: 'memAmount', width: 120, fixed: false, sortable: false, isShow: setting.goods.memberPrice, isOmit: false },
        { type: 'img', label: '商品图片', prop: 'img', width: 120, fixed: false, sortable: false, isShow: true, isOmit: false, imgWidth: 60 },
        // { type: 'custom', label: '促销商品配置', prop: 'promotion', width: 110, fixed: 'right', isShow: true },
        { type: 'custom', label: '轮播图配置', prop: 'banner', width: 100, fixed: 'right', isShow: true },
        { type: 'custom', label: '上下架配置', prop: 'status', width: 100, fixed: 'right', isShow: true },
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
  dialogTitle: '商品信息',
  dialogClass: 'dialog_lg',
  labelWidth: '120px',
  formitem: [
  { type: "input", label: "商品编号", prop: "goodsNo", max: 20, verify: null, default: null, span: 2 },
        { type: "input", label: "商品名称", prop: "name", max: 20, verify: null, default: null },
        {
            type: "select", label: "商品分类", prop: "type", customValue: "id", customLabel: "name", filterable: true, default: null,
            dicData: computed(() => { return typeList.value })
        },
        { type: "input", label: "商品单位", prop: "unit", max: 2, verify: null, default: null },
        { type: "input", label: "商品价格", prop: "amount", max: 99, verify: regularNumberDo, default: null },
        { type: "input", label: "运费", prop: "freight", max: 99, verify: regularNumberDo, default: null, judge:(row)=>setting.goods.freight },
        { type: "input", label: "会员价", prop: "memAmount", max: 99, verify: regularNumberDo, default: null, judge:(row)=>setting.goods.memberPrice },
        { type: "upload", label: "商品主图", prop: "img", fileType: 'img', path: 'avatar', default: null, span: 2,uploadType: "ali" },
        { type: "uploads", label: "商品宣传图", prop: "galleryList", fileType: 'img', path: 'avatar', default: null, span: 2,uploadType: "ali" },
        {
            type: "custom", label: "商品规格", prop: "goodsSpecificationVoList",
            default: [{ specification: '', value: '', picUrl: '', amount: 0, memAmount: 0 }], required: () => { return true }, span: 2
        },
        { type: "input", label: "简介", prop: "brief", category: 'textarea', max: 999, verify: null, default: null, span: 2, rows: 3 },
        { type: "rich", label: "详情", prop: "details", span: 2, path: 'notives', default: null,uploadType: "ali" },

  ],
  rules:  formRules
});

// 详情配置项
const optionInfo = reactive({
  dialogTitle: '商品详情',
  dialogClass: 'dialog_lg',
  labelWidth: '30',
  tableInfoItem: [
  {
            title: '商品信息', column: 2, infoData: [
                { type: 'text', label: '商品编号', prop: 'goodsNo', isShow: true },
                { type: 'text', label: '商品名称', prop: 'name', isShow: true },
                { type: 'custom', label: '商品分类', prop: 'type', isShow: true },
                { type: 'text', label: '商品单位', prop: 'unit', isShow: true },
                // { type: 'text', label: '会员价', prop: 'memAmount', isShow: true, append: '元' },
                { type: 'img', label: '商品主图', prop: 'img', isShow: true, span: 2, imgWidth: 120 },
                { type: 'img', label: '商品宣传图', prop: 'galleryList', isShow: true, span: 2, imgWidth: 120 },
                { type: 'custom', label: '商品规格', prop: 'goodsSpecificationVoList', isShow: true, span: 2 },
                { type: 'text', label: '简介', prop: 'brief', isShow: true, span: 2 },
                { type: 'html', label: '详情', prop: 'details', isShow: true, span: 2 },
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

.goods-images {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
</style>
