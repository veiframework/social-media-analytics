<template>
    <div>
        <el-row class="box">
            <el-col :xs="24" :sm="12" :lg="8">
                <el-card class="m5">
                    <div class="left">
                        <el-breadcrumb class="breadcrumb">
                            <el-breadcrumb-item><span class="bread_text">省份</span></el-breadcrumb-item>
                            <el-breadcrumb-item>{{ province }}</el-breadcrumb-item>
                        </el-breadcrumb>
                        <el-button v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:CREATE']"
                            type="primary" icon="Plus" link size="large" @click="add(1)">新增</el-button>
                    </div>
                    <p>
                        <el-input v-model="provinceSearch" size="small" placeholder="请输入检索内容" />
                    </p>
                    <el-table ref="provinceRef" :data="provinceFilterList" style="width: 100%" border max-height="650"
                        highlight-current-row @current-change="(row) => handle(1, row)">
                        <el-table-column fixed type="index" label="序号" align="center" width="60" />
                        <el-table-column prop="label" label="名称" align="center" />
                        <el-table-column label="操作" align="center" width="130" fixed="right">
                            <template #default="scoped">
                                <el-button type="primary" icon="Edit" link size="small"
                                    @click="handleForm(1, scoped.row)"
                                    v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:EDIT']">编辑</el-button>
                                <el-button type="danger" icon="Delete" link size="small"
                                    @click="handleDel(1, scoped.row)"
                                    v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:DELETE']">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="8">
                <el-card class="m5">
                    <div class="center">
                        <el-breadcrumb class="breadcrumb">
                            <el-breadcrumb-item><span class="bread_text">城市</span></el-breadcrumb-item>
                            <el-breadcrumb-item>{{ city }}</el-breadcrumb-item>
                        </el-breadcrumb>
                        <el-button v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:CREATE']"
                            type="primary" icon="Plus" link size="large" @click="add(2)">新增</el-button>
                    </div>
                    <p>
                        <el-input v-model="citySearch" size="small" placeholder="请输入检索内容" />
                    </p>
                    <el-table ref="cityRef" :data="cityFilterList" style="width: 100%" border max-height="650"
                        highlight-current-row @current-change="(row) => handle(2, row)">
                        <el-table-column fixed type="index" label="序号" align="center" width="60" />
                        <el-table-column prop="label" label="名称" align="center" />
                        <el-table-column label="操作" align="center" width="130" fixed="right">
                            <template #default="scoped">
                                <el-button type="primary" icon="Edit" link size="small"
                                    @click="handleForm(2, scoped.row)"
                                    v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:EDIT']">编辑</el-button>
                                <el-button type="danger" icon="Delete" link size="small"
                                    @click="handleDel(2, scoped.row)"
                                    v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:DELETE']">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="8">
                <el-card class="m5">
                    <div class="right">
                        <el-breadcrumb class="breadcrumb">
                            <el-breadcrumb-item><span class="bread_text">地区</span></el-breadcrumb-item>
                            <el-breadcrumb-item>{{ region }}</el-breadcrumb-item>
                        </el-breadcrumb>
                        <el-button v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:CREATE']"
                            type="primary" icon="Plus" link size="large" @click="add(3)">新增</el-button>
                    </div>
                    <p>
                        <el-input v-model="regionSearch" size="small" placeholder="请输入检索内容" />
                    </p>
                    <el-table ref="regionRef" :data="regionFilterList" style="width: 100%" border max-height="650"
                        highlight-current-row @current-change="(row) => handle(3, row)">
                        <el-table-column fixed type="index" label="序号" align="center" width="60" />
                        <el-table-column prop="label" label="名称" align="center" />
                        <el-table-column label="操作" align="center" width="130" fixed="right">
                            <template #default="scoped">
                                <el-button type="primary" icon="Edit" link size="small"
                                    @click="handleForm(3, scoped.row)"
                                    v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:EDIT']">编辑</el-button>
                                <el-button type="danger" icon="Delete" link size="small"
                                    @click="handleDel(3, scoped.row)"
                                    v-hasPermi="['com.chargehub.charge.region.domain.RegionEntity:crud:DELETE']">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>
        <el-dialog v-model="dialogVisible" :title="`${type == 1 ? '省份' : type == 2 ? '城市' : '地区'}配置`"
            :before-close="cancel" class="dialog_xs">
            <el-form ref="ruleFormRef" style="width: 100%;" :model="form" :rules="rules" label-width="auto" status-icon>
                <el-form-item :label="`${type == 1 ? '省份' : type == 2 ? '城市' : '地区'}Code：`" prop="areaCode">
                    <el-input v-model="form.areaCode" placeholder="请输入Code" />
                </el-form-item>
                <el-form-item :label="`${type == 1 ? '省份' : type == 2 ? '城市' : '地区'}名称：`" prop="label">
                    <el-input v-model="form.label" placeholder="请输入名称" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div>
                    <el-button @click="cancel">取消</el-button>
                    <el-button type="primary" @click="save">确认</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup name="Region">
import { getRegionAllApi, addRegionApi, editRegionApi, delRegionApi, setRegionApi } from '@/api/system/region';
import { ElMessage, ElMessageBox } from 'element-plus';
import { nextTick, onActivated, ref } from 'vue';
onActivated(() => {
    getData();
})
const type = ref();
const dialogVisible = ref(false);
const ruleFormRef = ref();
const form = ref({});
const rules = {
    areaCode: [{ required: true, message: '请输入省市区Code', trigger: 'blur' }],
    label: [{ required: true, message: '请输入省市区名称', trigger: 'blur' }],
}
// 省份参数
const provinceRef = ref();
const province = ref("");
let provinceRow = {};
const provinceSearch = ref('');
const provinceList = ref([]);
const provinceFilterList = computed(() =>
    provinceList.value.filter((data) => !provinceSearch.value || data.label.toLowerCase().includes(provinceSearch.value.toLowerCase()))
);
// 城市参数
const cityRef = ref();
const city = ref("");
let cityRow = {};
const citySearch = ref('');
const cityList = ref([]);
const cityFilterList = computed(() =>
    cityList.value.filter((data) => !citySearch.value || data.label.toLowerCase().includes(citySearch.value.toLowerCase()))
);
// 地区参数
const regionRef = ref();
const region = ref("");
const regionSearch = ref('');
let regionRow = {};
const regionList = ref([]);
const regionFilterList = computed(() =>
    regionList.value.filter((data) => !regionSearch.value || data.label.toLowerCase().includes(regionSearch.value.toLowerCase()))
);

const getData = () => {
    cityList.value = [];
    provinceList.value = [];
    regionList.value = [];
    getRegionAllApi({ level: 1, ascFields: 'areaCode' }).then(res => {
        provinceList.value = res.data;
        provinceRow = provinceList.value[0]
        province.value = provinceRow.label;
        nextTick(() => {
            provinceRef.value.setCurrentRow(provinceRow);
        })
    })
}

const add = (num) => {
    form.value = {}
    switch (num) {
        case 1:
            break;
        case 2:
            form.value.parentId = provinceRow.id;
            break;
        case 3:
            form.value.parentId = cityRow.id;
            break;
    }
    type.value = num;
    form.value.level = num;
    dialogVisible.value = true;
}

const handle = (num, row) => {
    switch (num) {
        case 1:
            cityList.value = [];
            regionList.value = [];
            provinceRow = row;
            nextTick(() => {
                if (!row) return;
                province.value = row?.label || '';
                getRegionAllApi({ level: 2, parentId: row?.id, ascFields: 'areaCode' }).then(res => {
                    cityList.value = res.data;
                    cityRow = cityList.value[0];
                    city.value = cityRow?.label;
                    nextTick(() => {
                        cityRef.value.setCurrentRow(cityRow);
                    });
                })
            })
            break;
        case 2:
            regionList.value = [];
            regionFilterList.value = [];
            cityRow = row;
            nextTick(() => {
                if (!row) return;
                city.value = row?.label || '';
                getRegionAllApi({ level: 3, parentId: row?.id, ascFields: 'areaCode' }).then(res => {
                    regionList.value = res.data;
                    regionRow = regionList.value[0];
                    region.value = regionRow?.label;
                    nextTick(() => {
                        regionRef.value.setCurrentRow(regionRow);
                    })
                })
            })
            break;
        case 3:
            regionRow = row;
            nextTick(() => {
                if (!row) return;
                region.value = row?.label || '';
            })
            break;
    }
}

const handleForm = (num, row) => {
    form.value = {};
    type.value = num;
    form.value.id = row.id;
    form.value.label = row.label;
    form.value.areaCode = row.areaCode;
    if (row.parentId) form.value.parentId = row.parentId;
    form.value.level = num;
    dialogVisible.value = true;
}

const handleDel = (num, row) => {
    type.value = num;
    ElMessageBox.confirm('是否删除该信息？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning', }).then(() => {
        delRegionApi(row.id).then(res => resMsg(res))
    }).catch(() => { })
}

const cancel = () => {
    dialogVisible.value = false;
}

const save = () => {
    ruleFormRef.value.validate(val => {
        if (!val) return;
        form.value.id ? editRegionApi(form.value, form.value.id).then(res => resMsg(res)) : addRegionApi(form.value).then(res => resMsg(res))
    })
}

const resMsg = (res) => {
    if (res.code == 200) {
        ElMessage.success(res.msg);
        cancel();
        switch (type.value) {
            case 1:
                getData();
                break;
            case 2:
                handle(1, provinceRow);
                break;
            case 3:
                handle(2, cityRow);
                break;
        }
    } else {
        ElMessage.error(res.msg)
    }
}
</script>
<style lang="scss" scoped>
.box {
    padding: 3px;

    .left,
    .center,
    .right {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        .bread_text {
            font-weight: bold;
        }
    }
}

.m5 {
    margin: 5px;
}
</style>