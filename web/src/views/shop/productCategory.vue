<template>
  <div class="app-container">

    <!-- 按钮组 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['product:category:create']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Sort"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch"  @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 树形组件 -->
    <div v-if="refreshTable" v-loading="loading" class="tree-container">
      <el-tree
        :data="productCategoryList"
        node-key="id"
        :default-expand-all="isExpandAll"
        :props="{
          id: 'id',
          parentId: 'parentId',
          label: 'categoryName',
          isLeaf: (node) => !node.hasChildren
        }"
      >
        <template #default="{ node, data }">
          <div class="tree-node-content">
            <span class="category-name">{{ node.label }}</span>
            <div class="tree-node-actions">
              <span class="sequence">排序: {{ data.sequence }}</span>
              <dict-tag :options="social_media_account_auto_sync" :value="data.state" style="margin: 0 10px;" />
              <span class="create-time">{{ parseTime(data.createTime) }}</span>
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(data)" v-hasPermi="['product:category:edit']">修改</el-button>
              <el-button link type="primary" icon="Plus" @click.stop="handleAdd(data)" v-hasPermi="['product:category:create']">新增子分类</el-button>
              <el-button link type="danger" icon="Delete" @click.stop="handleDelete(data)" v-hasPermi="['product:category:delete']">删除</el-button>
            </div>
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 添加或修改分类对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="categoryRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24" v-if="form.parentId !== '0'">
            <el-form-item label="上级分类" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="categoryOptions"
                :props="{ value: 'id', label: 'categoryName', children: 'children' }"
                value-key="id"
                placeholder="选择上级分类"
                check-strictly
                :tree-node-props="{ checkable: true }"
                default-expand-all
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sequence">
              <el-input-number v-model="form.sequence" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="分类状态">
              <el-radio-group v-model="form.state" default="enable">
                <el-radio
                  v-for="dict in social_media_account_auto_sync"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProductCategory">
import { ref, reactive, toRefs, nextTick } from 'vue';
import { getCurrentInstance } from 'vue';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';
import { listProductCategory, addProductCategory, updateProductCategory, deleteProductCategory } from '@/api/shop/productCategory';

const { proxy } = getCurrentInstance();
const { social_media_account_auto_sync } = useDict("social_media_account_auto_sync");

const productCategoryList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const title = ref("");
const categoryOptions = ref([]);
const isExpandAll = ref(true);
const refreshTable = ref(true);

const data = reactive({
  form: {},
  queryParams: {
    ascFields: 'sequence',
    descFields: 'id',
  },
  rules: {
    categoryName: [{ required: true, message: "分类名称不能为空", trigger: "blur" }],
    sequence: [{ required: true, message: "排序不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询分类列表 */
function getList() {
  loading.value = true;
  listProductCategory(queryParams.value).then(response => {
    // 转换为树形结构
    productCategoryList.value = proxy.handleTree(response.data, "id", "parentId", "children");
    loading.value = false;
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    parentId: '0',
    categoryName: undefined,
    sequence: 0,
    state: 'enable'
  };
  if (proxy.$refs["categoryRef"]) {
    proxy.$refs["categoryRef"].resetFields();
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  if (proxy.$refs["queryRef"]) {
    proxy.$refs["queryRef"].resetFields();
  }
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd(row) {
  reset();
  listProductCategory().then(response => {
    // 转换为树形结构，用于树形选择器
    categoryOptions.value = proxy.handleTree(response.data, "id", "parentId", "children");
  });
  if (row != undefined) {
    form.value.parentId = row.id;
  }
  open.value = true;
  title.value = "添加分类";
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  listProductCategory().then(response => {
    // 创建数据副本，避免修改原始数据
    const dataCopy = JSON.parse(JSON.stringify(response.data));
    
    // 找出所有需要移除的节点ID（当前节点及其所有后代）
    const getNodesToRemove = (nodes, targetId) => {
      // 创建Set存储需要移除的节点ID
      const nodesToRemove = new Set();
      
      // 添加目标节点
      nodesToRemove.add(targetId);
      
      // 创建一个Map，按parentId分组节点
      const nodesByParentId = new Map();
      nodes.forEach(node => {
        const children = nodesByParentId.get(node.parentId) || [];
        children.push(node);
        nodesByParentId.set(node.parentId, children);
      });
      
      // 递归收集所有后代节点
      const collectDescendants = (parentId) => {
        const children = nodesByParentId.get(parentId) || [];
        children.forEach(child => {
          nodesToRemove.add(child.id);
          collectDescendants(child.id); // 递归收集子节点的后代
        });
      };
      
      // 从目标节点开始收集所有后代
      collectDescendants(targetId);
      
      return nodesToRemove;
    };
    
    // 获取需要移除的节点ID集合
    const nodesToRemove = getNodesToRemove(dataCopy, row.id);
    
    // 过滤掉需要移除的节点
    const filteredData = dataCopy.filter(node => !nodesToRemove.has(node.id));
    
    // 转换为树形结构，用于树形选择器
    categoryOptions.value = proxy.handleTree(filteredData, "id", "parentId", "children");
  });
  form.value = { ...row };
  open.value = true;
  title.value = "修改分类";
}

/** 提交按钮 */
function submitForm() {
  if (proxy.$refs["categoryRef"]) {
    proxy.$refs["categoryRef"].validate(valid => {
      if (valid) {
        if (form.value.id != undefined) {
          updateProductCategory(form.value).then(response => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          });
        } else {
          addProductCategory(form.value).then(response => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
          });
        }
      }
    });
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  // 确认对话框
  proxy.$modal.confirm(`确定要删除分类 "${row.categoryName}" 吗？${row.hasChildren ? ' 该分类下存在子分类，删除后将一同删除。' : ''}`).then(() => {
    // 调用删除API
    deleteProductCategory(row.id).then(response => {
      proxy.$modal.msgSuccess("删除成功");
      // 刷新分类列表
      getList();
    }).catch(() => {
      proxy.$modal.msgError("删除失败");
    });
  }).catch(() => {
    // 用户取消删除操作
  });
}





// 初始化列表
getList();
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.tree-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  background-color: #fff;
}

/* 调整树节点之间的间距 */
:deep(.el-tree-node) {
  margin: 8px 0;
}

/* 调整树连接线的样式 */
:deep(.el-tree-node__content) {
  height: auto;
  padding: 8px 2px;
}

.tree-node-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  font-size: 14px; /* 调大字体 */
}

.category-name {
  flex: 1;
  font-weight: 500;
  font-size: 15px; /* 分类名称字体稍大 */
}

.tree-node-actions {
  display: flex;
  align-items: center;
  gap: 20px; /* 增加操作项之间的间距 */
  flex: 2;
  font-size: 14px; /* 操作项字体 */
}

.sequence {
  font-size: 13px; /* 排序字体 */
  color: #909399;
}

.create-time {
  font-size: 13px; /* 创建时间字体 */
  color: #909399;
}
</style>