<template>
    <div class="app-container">
        <el-row :gutter="20">
            <!--用户数据-->
            <el-col :span="24" :xs="24">
                <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
                    <el-form-item label="手机号" prop="userName">
                        <el-input v-model="queryParams.userName" placeholder="请输入手机号" clearable style="width: 240px"
                            @keyup.enter="handleQuery" />
                    </el-form-item>
                    <el-form-item label="用户名" prop="nickName">
                        <el-input v-model="queryParams.nickName" placeholder="请输入用户名" clearable style="width: 240px"
                            @keyup.enter="handleQuery" />
                    </el-form-item>
                    <el-form-item label="状态" prop="status">
                        <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 240px">
                            <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label"
                                :value="dict.value" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="创建时间" style="width: 308px;">
                        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange"
                            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                    </el-form-item>
                </el-form>
                <el-row :gutter="10" class="mb8">
                    <el-col :span="1.5">
                        <el-button type="primary" plain icon="Plus" @click="handleAdd"
                            v-hasPermi="['system:user:add']">新增</el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
                            v-hasPermi="['system:user:remove']">删除</el-button>
                    </el-col>
                </el-row>
                <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange" border>
                    <el-table-column type="selection" width="50" align="center" />
                    <el-table-column label="用户编号" align="center" key="userId" prop="userId" />
                    <el-table-column label="用户名" align="center" key="nickName" prop="nickName"
                        :show-overflow-tooltip="true" />
                    <el-table-column label="手机号" align="center" key="phonenumber" prop="phonenumber"
                        :show-overflow-tooltip="true" />
                    <el-table-column label="状态" align="center" key="status">
                        <template #default="scope">
                            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1"
                                @change="handleStatusChange(scope.row)"></el-switch>
                        </template>
                    </el-table-column>
                    <el-table-column label="创建时间" align="center" prop="createTime" width="160">
                        <template #default="scope">
                            <span>{{ parseTime(scope.row.createTime) }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
                        <template #default="scope">
                            <el-tooltip content="配置柜子" placement="top">
                                <el-button link type="primary" icon="Setting" @click="handleData(scope.row)"
                                    v-hasPermi="['system:user:edit']"></el-button>
                            </el-tooltip>
                            <el-tooltip content="修改" placement="top">
                                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                                    v-hasPermi="['system:user:edit']"></el-button>
                            </el-tooltip>
                            <el-tooltip content="删除" placement="top">
                                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                                    v-hasPermi="['system:user:remove']"></el-button>
                            </el-tooltip>
                            <el-tooltip content="重置密码" placement="top">
                                <el-button link type="primary" icon="Key" @click="handleResetPwd(scope.row)"
                                    v-hasPermi="['system:user:resetPwd']"></el-button>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                </el-table>
                <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                    v-model:limit="queryParams.pageSize" @pagination="getList" />
            </el-col>
        </el-row>
        <!-- 添加或修改用户配置对话框 -->
        <el-dialog :title="title" v-model="open" class="dialog_xs" append-to-body>
            <el-form :model="form" :rules="rules" ref="userRef" label-width="90px" class="form_max">
                <el-row>
                    <el-col :xs="24">
                        <el-form-item label="用户名" prop="nickName">
                            <el-input v-model="form.nickName" placeholder="请输入用户名" maxlength="30" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :xs="24">
                        <el-form-item label="手机号" prop="phonenumber">
                            <el-input v-model="form.phonenumber" placeholder="请输入用户手机号" maxlength="11" />
                        </el-form-item>
                    </el-col>
                    <el-col :xs="24">
                        <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
                            <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20"
                                show-password />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :xs="24">
                        <el-form-item label="状态">
                            <el-radio-group v-model="form.status">
                                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.value">
                                    {{ dict.label }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
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
        <!-- 用户导入对话框 -->
        <el-dialog title="配置用户数据源" v-model="dataSource.open" class="dialog_sm" append-to-body>
            <el-form :model="dataSource.form" :rules="rules" ref="userRef" label-width="100" class="form_max">
                <el-form-item label="小区：" prop="apartmentId">
                    <el-transfer v-model="dataSource.form.apartmentId" filterable :filter-method="filterMethod"
                        :data="transferData" :titles="['未选择', '已选择']" filter-placeholder="输入进行检索" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="dataSource.open = false">取 消</el-button>
                    <el-button type="primary" @click="save">确 定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup name="Delivery">
import { changeUserStatus, listUser, resetUserPwd, delUser, getUser, updateUser, addUser } from "@/api/system/user";
import useUserStore from '@/store/modules/user';
import { useRouter } from "vue-router";
import { bindDeliverymanApi, getAllUnitApi } from "@/api/base";
const userStore = useUserStore();
const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

const userList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const initPassword = ref(undefined);
/*** 用户导入参数 */
const dataSource = reactive({
    open: false,
    form: {}
});

const data = reactive({
    form: {},
    queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        deptId: undefined,
    },
    rules: {
        nickName: [{ required: true, message: "用户名不能为空", trigger: "blur" }],
        password: [
            { required: true, message: "用户密码不能为空", trigger: "blur" },
            { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" },
            { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }]
        ,
        phonenumber: [
            { required: true, message: "用户手机号不能为空", trigger: "blur" },
            { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }],
    }
});

const { queryParams, form, rules } = toRefs(data);
/** 查询用户列表 */
function getList() {
    loading.value = true;
    queryParams.value.roleId = 154;
    listUser(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
        loading.value = false;
        userList.value = res.rows;
        total.value = res.total;
    });
};
/** 搜索按钮操作 */
function handleQuery() {
    queryParams.value.pageNum = 1;
    getList();
};
/** 重置按钮操作 */
function resetQuery() {
    dateRange.value = [];
    proxy.resetForm("queryRef");
    queryParams.value.deptId = undefined;
    handleQuery();
};
/** 删除按钮操作 */
function handleDelete(row) {
    const userIds = row.userId || ids.value;
    proxy.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function () {
        return delUser(userIds);
    }).then(() => {
        getList();
        proxy.$modal.msgSuccess("删除成功");
    }).catch(() => { });
};
/** 用户状态修改  */
function handleStatusChange(row) {
    let text = row.status === "0" ? "启用" : "停用";
    proxy.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗?').then(function () {
        return changeUserStatus(row.userId, row.status);
    }).then(() => {
        proxy.$modal.msgSuccess(text + "成功");
    }).catch(function () {
        row.status = row.status === "0" ? "1" : "0";
    });
};
/** 重置密码按钮操作 */
function handleResetPwd(row) {
    proxy.$prompt('请输入"' + row.userName + '"的新密码', "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        closeOnClickModal: false,
        inputPattern: /^.{5,20}$/,
        inputErrorMessage: "用户密码长度必须介于 5 和 20 之间",
        inputValidator: (value) => {
            if (/<|>|"|'|\||\\/.test(value)) {
                return "不能包含非法字符：< > \" ' \\\ |"
            }
        },
    }).then(({ value }) => {
        resetUserPwd(row.userId, value).then(response => {
            proxy.$modal.msgSuccess("修改成功，新密码是：" + value);
        });
    }).catch(() => { });
};
/** 选择条数  */
function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.userId);
    single.value = selection.length != 1;
    multiple.value = !selection.length;
};
/** 重置操作表单 */
function reset() {
    form.value = {
        userId: undefined,
        deptId: undefined,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        sex: undefined,
        status: "0",
        remark: undefined,
        postIds: [],
        roleIds: [154]
    };
    proxy.resetForm("userRef");
};
/** 取消按钮 */
function cancel() {
    open.value = false;
    reset();
};
/** 新增按钮操作 */
function handleAdd() {
    reset();
    getUser().then(() => {
        open.value = true;
        title.value = "添加用户";
        form.value.password = initPassword.value;
    });
};
/** 修改按钮操作 */
function handleUpdate(row) {
    reset();
    const userId = row.userId || ids.value;
    getUser(userId).then(response => {
        form.value = response.data;
        form.value.postIds = response.postIds;
        form.value.roleIds = response.roleIds;
        open.value = true;
        title.value = "修改用户";
        form.password = "";
    });
};
/** 提交按钮 */
function submitForm() {
    proxy.$refs["userRef"].validate(valid => {
        if (valid) {
            form.value.userName = form.value.phonenumber;
            if (form.value.userId != undefined) {
                updateUser(form.value).then(response => {
                    proxy.$modal.msgSuccess("修改成功");
                    open.value = false;
                    getList();
                });
            } else {
                addUser(form.value).then(response => {
                    proxy.$modal.msgSuccess("新增成功");
                    open.value = false;
                    getList();
                });
            }
        }
    });
};
getList();
// 自定义参数
const transferData = ref([])
const filterMethod = (query, item) => {
    return item.label.toLowerCase().includes(query.toLowerCase())
}
/** 配置柜子 */
function handleData(row) {
    getAllUnitApi({ currentAdminId: row.userId }).then(res => {
        const allData = res.data.map(item => {
            return {
                key: item.id, disabled: item.disabledSelected,
                label: item.communityId_dictText + item.apartmentName
            }
        })
        transferData.value = allData;
        dataSource.form.userId = row.userId;
        dataSource.form.apartmentId = res.data.filter(obj => obj.currentSelected).map(obj => obj.id + "");
        dataSource.open = true;
    })
};
// 保存
const save = () => {
    proxy.$refs["userRef"].validate(valid => {
        if (!valid) return;
        bindDeliverymanApi(dataSource.form).then(() => {
            proxy.$modal.msgSuccess("配置成功");
            dataSource.open = false;
            getList();
        });
    });
}
</script>
