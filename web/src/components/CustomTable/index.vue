<template>
    <div class="box">
        <!-- 搜索栏 -->
        <search ref="searchRef" :showSearch="option.showSearch" :searchLabelWidth="option.searchLabelWidth"
            :searchItem="option.searchItem" @search="handleSearch" :option="option">
            <template #custom-item="{ prop, queryParams }">
                <slot name='custom-item' :prop="prop" :queryParams="queryParams"></slot>
            </template>
        </search>
        <!-- 表格 -->
        <customTable :data="props.data" ref="tableRef" :option="option" :page="page" @refresh="tableRefresh"
            @isShowSearch="showSearch" :total="props.total" @selectAllChange="selectAll" @selectChange="select"
            @sortChange="sort" @menuChange="menu" @currentChange="current" @headerchange="header"
            @operationChange="handleOperation" @selectData="selectData" @columnFilterChange="columnFilterChange">
            <template #table-top>
                <slot name='table-top'></slot>
            </template>
            <template #menu-top="{ row }">
                <slot name='menu-top' :row="row"></slot>
            </template>
            <template #menu="{ row }">
                <slot name='menu' :row="row"></slot>
            </template>
            <template #custom="{ row, prop, index }">
                <slot name='table-custom' :row="row" :prop="prop" :index="index"></slot>
            </template>
        </customTable>
    </div>
</template>
<script setup>
import {reactive, ref} from "vue";
import search from "./components/search";
import customTable from "./components/customTable";
const props = defineProps({
    option: { type: Object, default: {} },
    data: { type: Array, default: [] },
    pageNum: { type: Number, default: 1 },
    pageSize: { type: Number, default: 10 },
    total: { type: Number, default: 0 },
});
const page = reactive({
    pageNum: props.pageNum,
    pageSize: props.pageSize,
    small: false,
    total: props.total,
    pageSizes: [10, 20, 30, 40, 50, 100],
    pagerCount: 7,
    background: true,
    layout: "total, sizes, prev, pager, next, jumper",
})
const emits = defineEmits([
    'search', 'refresh', 'selectAllChange', 'selectChange', 'sortChange', 'menuChange', 'currentChange', 'headerchange', 'operationChange', 'selectData'
])
// 基础配置项
const option = reactive(props.option);
const tableRef = ref();
const searchRef = ref();
/**
 * 配置搜索框显示隐藏
 */
const showSearch = (val) => option.showSearch = val;
/**
 * 搜索事件
 */
const handleSearch = (val) => {
    page.pageNum = 1;
    let paramsData = val;
    // 特殊处理多个字段合并为一个表单项查询场景
    for (let key in val) {
        if (paramsData[key] && key.includes("multiple")) {
            const textList = paramsData[key].split('_');
            if (textList.length >= 2) {
                paramsData[textList[0]] = textList[1];
                delete paramsData[key];
            }
        }
    };
    tableRef.value.clearData();
    emits('search', paramsData);

};



const columnFilterChange = (filters) => {
  searchRef.value.handleColumnChange(filters)
}



/**
 * 刷新表格
 */
const tableRefresh = () => emits('refresh');
/**
 * 全选功能
 */
const selectAll = (val) => emits('selectAllChange', val);
/**
 * 单选功能
 */
const select = (val) => emits('selectChange', val);
/**
 * 排序功能
 */
const sort = (val) => {
  let order = val.order;
  let column = val.column;
  let label = column.label;
  let prop = option.tableItem.filter(item => item.label === label)[0].prop;
  searchRef.value.orderBy(prop, order)
  tableRef.value.orderBy(prop, order)
};
/**
 * 操作选中
 */
const menu = (val) => emits('menuChange', val);
/**
 * 页码更新
 */
const current = (val) => emits('currentChange', val);
/**
* 表格左上按钮操作
*/
const header = (val) => emits('headerchange', val);
/**
* 批量操作
*/
const handleOperation = (val) => emits('operationChange', val);
/**
* 获取跨页多选数据
*/
const selectData = (val) => emits('selectData', val);
</script>
<style lang="scss" scoped></style>