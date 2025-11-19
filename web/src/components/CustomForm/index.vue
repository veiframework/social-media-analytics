<template>
    <el-form ref="formRef" :model="form" :label-width="props.option.labelWidth ? props.option.labelWidth : 'auto'"
        :rules="props.option.rules" :label-position="appStore.screenWidth > 500 ? 'right' : 'top'" scroll-to-error
        class="form_max" status-icon>
        <el-row class="row_class">
            <template v-for="item, index in props.option.formitem">
                <el-col v-if="customJudge(item.judge, form)" :xs="24" :sm="12 * (item.span ? item.span : 1)"
                    :md="12 * (item.span ? item.span : 1)" :key="index">
                    <div v-if="item.type == 'title'" style="font-weight: bold;font-size: 16px;">
                        <p>{{ item.label }}</p>
                        <el-divider />
                    </div>
                    <!-- 输入框 -->
                    <el-form-item v-else-if="item.type == 'input'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-tooltip v-if="item.tooltip" :content="item.tooltip" placement="top" effect="customized">
                            <el-input v-model.trim="form[item.prop]" :maxlength="item.max" :type="item.category"
                                :placeholder="item.placeholder ? item.placeholder : '请输入' + item.label" clearable
                                @input="val => form[item.prop] = val.replace(item.verify, '')"
                                :autosize="{ minRows: item.rows ? item.rows : 3, maxRows: item.maxRows ? item.maxRows : 10 }"
                                @blur="formBlur(item.prop)" :disabled="item.disabled"
                                :show-word-limit="item.category == 'textarea'">
                                <template v-if="item.append" #append>{{ item.append }}</template>
                                <template v-if="item.prepend" #prepend>{{ item.prepend }}</template>
                            </el-input>
                        </el-tooltip>
                        <el-input v-else v-model.trim="form[item.prop]" :maxlength="item.max" :type="item.category"
                            :placeholder="item.placeholder ? item.placeholder : '请输入' + item.label" clearable
                            @input="val => form[item.prop] = val.replace(item.verify, '')"
                            :autosize="{ minRows: item.rows ? item.rows : 3, maxRows: item.maxRows ? item.maxRows : 10 }"
                            @blur="formBlur(item.prop)" :disabled="item.disabled"
                            :show-word-limit="item.category == 'textarea'">
                            <template v-if="item.append" #append>{{ item.append }}</template>
                            <template v-if="item.prepend" #prepend>{{ item.prepend }}</template>
                        </el-input>
                    </el-form-item>
                    <!-- 下拉框 -->
                    <el-form-item v-else-if="item.type == 'select'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-tooltip v-if="item.limit" :content="`该选择项限制选择 ${item.limit} 个`" placement="top"
                            effect="customized">
                            <el-select class="w100" v-model="form[item.prop]" :placeholder="'请选择' + item.label"
                                @change="formChange(item.prop)" :filterable="item.filterable" clearable collapse-tags
                                :multiple="item.multiple" :multiple-limit="item.limit" :disabled="item.disabled"
                                collapse-tags-tooltip :max-collapse-tags="2">
                                <el-option v-for="items in item.dicData"
                                    :key="items[item.customValue ? item.customValue : 'value']"
                                    :label="items[item.customLabel ? item.customLabel : 'label']"
                                    :value="items[item.customValue ? item.customValue : 'value']" />
                            </el-select>
                        </el-tooltip>
                        <el-select v-else class="w100" v-model="form[item.prop]" :placeholder="'请选择' + item.label"
                            @change="formChange(item.prop)" :filterable="item.filterable" clearable collapse-tags
                            :multiple="item.multiple" :multiple-limit="item.limit" :disabled="item.disabled"
                            collapse-tags-tooltip :max-collapse-tags="2">
                            <el-option v-for="items in item.dicData"
                                :key="items[item.customValue ? item.customValue : 'value']"
                                :label="items[item.customLabel ? item.customLabel : 'label']"
                                :value="items[item.customValue ? item.customValue : 'value']" />
                        </el-select>
                    </el-form-item>
                    <!-- 下拉框 - 远程 -->
                    <el-form-item v-else-if="item.type == 'select_remote'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-tooltip v-if="item.limit" :content="`该选择项限制选择 ${item.limit} 个`" placement="top"
                            effect="customized">
                            <el-select ref="seleRemotetRef" class="w100" v-model="form[item.prop]" filterable remote
                                reserve-keyword :placeholder="'请选择' + item.label" remote-show-suffix :loading="loading"
                                :remote-method="(query) => remoteMethod(query, item.request, item.key, item.itemNum, item.requestParams)"
                                :disabled="item.disabled" :multiple="item.multiple" :multiple-limit="item.limit"
                                @change="formChange(item.prop)">
                                <el-option v-for="itm in options"
                                    :key="itm[item.customValue ? item.customValue : 'value']"
                                    :label="itm[item.customLabel ? item.customLabel : 'label']"
                                    :value="itm[item.customValue ? item.customValue : 'value']" />
                            </el-select>
                        </el-tooltip>
                        <el-select v-else ref="seleRemotetRef" class="w100" v-model="form[item.prop]" filterable remote
                            reserve-keyword :placeholder="'请选择' + item.label" remote-show-suffix :loading="loading"
                            :remote-method="(query) => remoteMethod(query, item.request, item.key, item.itemNum, item.requestParams)"
                            :disabled="item.disabled" :multiple="item.multiple" :multiple-limit="item.limit"
                            @change="formChange(item.prop)">
                            <el-option v-for="itm in options" :key="itm[item.customValue ? item.customValue : 'value']"
                                :label="itm[item.customLabel ? item.customLabel : 'label']"
                                :value="itm[item.customValue ? item.customValue : 'value']" />
                        </el-select>
                    </el-form-item>
                    <!-- 单选 -->
                    <el-form-item v-else-if="item.type == 'radio'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-radio-group v-model="form[item.prop]" @change="formChange(item.prop)"
                            :disabled="item.disabled">
                            <template v-if="item.isOpen">
                                <el-radio :label="'1'">开启</el-radio>
                                <el-radio :label="'0'">关闭</el-radio>
                            </template>
                            <template v-else>
                                <el-radio v-for="items in item.dicData"
                                    :key="items[item.customValue ? item.customValue : 'value']"
                                    :label="items[item.customValue ? item.customValue : 'value']">
                                    <template v-if="item.tooltip && items['value'] == true">
                                        <el-tooltip :content="item.tooltip" placement="top" effect="customized">
                                            {{ items[item.customLabel ? item.customLabel : 'label'] }}
                                        </el-tooltip>
                                    </template>
                                    <template v-else>
                                        {{ items[item.customLabel ? item.customLabel : 'label'] }}
                                    </template>
                                </el-radio>
                            </template>
                        </el-radio-group>
                    </el-form-item>
                    <!-- 单选按钮型 -->
                    <el-form-item v-else-if="item.type == 'radio_btn'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-radio-group v-model="form[item.prop]" @change="formChange(item.prop)"
                            :disabled="item.disabled">
                            <el-radio-button v-for="items in item.dicData"
                                :key="items[item.customValue ? item.customValue : 'value']"
                                :label="items[item.customValue ? item.customValue : 'value']">
                                {{ items[item.customLabel ? item.customLabel : 'label'] }}
                            </el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                    <!-- 多选 -->
                    <el-form-item v-else-if="item.type == 'checkbox'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-checkbox-group v-model="form[item.prop]" @change="formChange(item.prop)"
                            :disabled="item.disabled">
                            <el-checkbox v-for="items in item.dicData"
                                :key="items[item.customValue ? item.customValue : 'value']"
                                :label="items[item.customValue ? item.customValue : 'value']">
                                {{ items[item.customLabel ? item.customLabel : 'label'] }}
                            </el-checkbox>
                        </el-checkbox-group>
                    </el-form-item>
                    <!-- 日期 -->
                    <el-form-item v-else-if="item.type == 'date'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
                            :disabled="item.disabled" @change="formChange(item.prop)" v-model="form[item.prop]"
                            :default-time="item.defaultTime ? item.defaultTime : new Date(2000, 2, 1, 23, 59, 59)"
                            :type="item.category" placeholder="请选择日期" :disabled-date="item.disabledDate" />
                    </el-form-item>
                    <!-- 日期时间 -->
                    <el-form-item v-else-if="item.type == 'datetime'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
                            :disabled="item.disabled" @change="formChange(item.prop)" v-model="form[item.prop]"
                            :default-time="item.defaultTime ? item.defaultTime : new Date(2000, 2, 1, 0, 0, 0)"
                            placeholder="请选择日期" :type="item.category" :disabled-date="item.disabledDate" />
                    </el-form-item>
                    <!-- 范围日期 -->
                    <template v-else-if="item.type == 'daterange'">
                        <el-form-item v-if="appStore.device == 'desktop'" :label="item.label + '：'" :prop="item.prop"
                            :required="customRequired(item.prop, item.required, form)">
                            <el-date-picker class="w100" start-placeholder="开始日期" end-placeholder="结束日期"
                                :disabled="item.disabled" :format="item.format" :value-format="item.valueFormat"
                                v-model="form[item.prop]" @change="(val) => handleDate(val, item.prop)"
                                :type="item.category" :disabled-date="item.disabledDate"
                                :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 2, 1, 23, 59, 59)]" />
                        </el-form-item>
                        <template v-else>
                            <el-form-item :label="item.label + '：'" :prop="`start${item.prop}`"
                                :rules="[{ required: true, message: '请选择开始日期', trigger: 'blur' }]">
                                <el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
                                    v-model="form[`start${item.prop}`]" placeholder="请选择开始日期" :disabled="item.disabled"
                                    :type="item.category.replace('range', '')" :disabled-date="item.disabledDate"
                                    @change="() => form[`end${item.prop}`] = undefined" />
                            </el-form-item>
                            <el-form-item :label="item.label + '：'" :prop="`end${item.prop}`"
                                :rules="[{ required: true, message: '请选择结束日期', trigger: 'blur' }]">
                                <el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
                                    v-model="form[`end${item.prop}`]" placeholder="请选择结束日期"
                                    :type="item.category.replace('range', '')"
                                    :default-time="new Date(2000, 2, 1, 23, 59, 59)"
                                    :disabled="(form[`start${item.prop}`] ? false : true) || item.disabled"
                                    :disabled-date="(time) => disabledDate(time, form[`start${item.prop}`], item.disabledDate)" />
                            </el-form-item>
                        </template>
                    </template>
                    <!-- 范围日期时间 -->
                    <template v-else-if="item.type == 'datetimerange'">
                        <el-form-item v-if="appStore.device == 'desktop'" :label="item.label + '：'" :prop="item.prop"
                            :required="customRequired(item.prop, item.required, form)">
                            <el-date-picker class="w100" start-placeholder="开始时间" end-placeholder="结束时间"
                                :disabled="item.disabled" :format="item.format" :value-format="item.valueFormat"
                                v-model="form[item.prop]" @change="(val) => handleDate(val, item.prop)"
                                :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 2, 1, 23, 59, 59)]"
                                :type="item.category" :disabled-date="item.disabledDate" />
                        </el-form-item>
                        <template v-else>
                            <el-form-item :label="item.label + '：'" :prop="`start${item.prop}`"
                                :rules="[{ required: true, message: '请选择开始日期', trigger: 'blur' }]">
                                <el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
                                    v-model="form[`start${item.prop}`]" placeholder="请选择开始时间" :disabled="item.disabled"
                                    :type="item.category.replace('range', '')" :disabled-date="item.disabledDate"
                                    @change="() => form[`end${item.prop}`] = undefined" />
                            </el-form-item>
                            <el-form-item :label="item.label + '：'" :prop="`end${item.prop}`"
                                :rules="[{ required: true, message: '请选择结束日期', trigger: 'blur' }]">
                                <el-date-picker class="w100" :format="item.format" :value-format="item.valueFormat"
                                    v-model="form[`end${item.prop}`]" placeholder="请选择结束时间"
                                    :type="item.category.replace('range', '')"
                                    :default-time="new Date(2000, 2, 1, 23, 59, 59)"
                                    :disabled="(form[`start${item.prop}`] ? false : true) || item.disabled"
                                    :disabled-date="(time) => disabledDate(time, form[`start${item.prop}`], item.disabledDate)" />
                            </el-form-item>
                        </template>
                    </template>
                    <!-- 上传 -->
                    <el-form-item v-else-if="item.type == 'upload'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <el-upload class="avatar-uploader" action='' :show-file-list="false"
                            :http-request="(req) => uploadImg(item.type, req, item.prop, item.fileType, item.path, item.uploadType)"
                            :disabled="item.disabled">
                            <img v-if="form[item.prop]" :src="form[item.prop]" class="avatar" />
                            <el-icon v-else class="avatar-uploader-icon">
                                <Plus />
                            </el-icon>
                        </el-upload>
                    </el-form-item>
                    <!-- 多张上传 -->
                    <el-form-item v-else-if="item.type == 'uploads'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)" >
                        <el-upload v-if="form[item.prop]" class="avatar-uploader" v-model:file-list="form[item.prop]"
                            action='' :disabled="item.disabled" list-type="picture-card" multiple
                            :on-preview="handlePictureCardPreview"
                            :http-request="(req) => uploadImg(item.type, req, item.prop, item.fileType, item.path, item.uploadType)">
                            <el-icon class="avatar-uploader-icon">
                                <Plus />
                            </el-icon>
                        </el-upload>
                    </el-form-item>
                    <!-- 区域地址选择 -->
                    <template v-else-if="item.type == 'address'">
                        <el-form-item :label="(item.label ? item.label : '区域') + '：'"
                            :prop="item.prop ? item.prop : 'province'">
                            <el-form-item label-width="0" prop="province">
                                <el-select :disabled="item.disabled" style="width: 195px;margin:0 5px 5px 0;"
                                    v-model="form.province" filterable placeholder="请选择省" @change="setProvince">
                                    <el-option v-for="item in provinces" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item>
                            <el-form-item label-width="0" :disabled="item.disabled"
                                :label-width="appStore.screenWidth > 768 ? '7' : '0'" prop="city">
                                <el-select style="width: 195px;margin:0 5px 5px 0;" v-model="form.city"
                                    placeholder="请选择市" filterable @change="setCity">
                                    <el-option v-for="item in citys" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item>
                            <el-form-item label-width="0" :disabled="item.disabled"
                                :label-width="appStore.screenWidth > 768 ? '7' : '0'" prop="town">
                                <el-select style="width: 196px;margin:0 5px 5px 0;" v-model="form.town"
                                    placeholder="请选择区/县" filterable @change="setCounty">
                                    <el-option v-for="item in areas" :key="item.value" :label="item.label"
                                        :value="item.value" />
                                </el-select>
                            </el-form-item>
                        </el-form-item>
                        <el-form-item :label="(item.labels ? item.labels : '详细地址') + '：'"
                            :prop="item.prop ? item.prop : 'address'">
                            <el-input :disabled="item.disabled" v-model.trim="form.address" type="textarea" :rows="3"
                                placeholder="请输入详细地址" @blur="setAddress"></el-input>
                        </el-form-item>
                    </template>
                    <!-- map 选择 -->
                    <template v-else-if="item.type == 'map'">
                        <el-form-item :label="(item.label ? item.label : '地图') + '：'"
                            :prop="item.prop ? item.prop : 'longitde'">
                            <AMap ref="aMapRef" style="height: 400px;" @changeLnglat="changeLnglat"
                                @changeAddress="changeAddress">
                            </AMap>
                        </el-form-item>
                        <div :style="{ display: appStore.screenWidth > 768 ? 'flex' : '' }">
                            <el-form-item style="width: 100%;" label="经度：" prop="longitde">
                                <el-input :disabled="item.disabled" v-model="form.longitde"
                                    @input="(val) => form.longitde = verifyNumberDoSix(val)"
                                    @blur="formBlurMap('longitde')" placeholder="请输入"></el-input>
                            </el-form-item>
                            <el-form-item style="width: 100%;" label="纬度：" prop="latitude">
                                <el-input :disabled="item.disabled" v-model="form.latitude"
                                    @input="(val) => form.latitude = verifyNumberDoSix(val)"
                                    @blur="formBlurMap('latitude')" placeholder="请输入"></el-input>
                            </el-form-item>
                        </div>
                    </template>
                    <!-- 富文本框 -->
                    <el-form-item v-else-if="item.type == 'rich'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <wangeditor ref="wadtrRef" :text="form[item.prop]" :path="item.path"
                            :uploadType="item.uploadType" @handleBlur="(val) => form[item.prop] = val">
                        </wangeditor>
                    </el-form-item>
                    <!-- 自定义 -->
                    <el-form-item v-else-if="item.type == 'custom'" :label="item.label + '：'" :prop="item.prop"
                        :required="customRequired(item.prop, item.required, form)">
                        <slot v-if="props.visible" name='custom-item' :prop="item.prop" :form="form"></slot>
                    </el-form-item>
                    <!-- divider -->
                    <el-divider v-else-if="item.type == 'divider'" />
                </el-col>
            </template>
        </el-row>
        <el-dialog v-model="dialogVisible">
            <img w-full style="width: 100%;" :src="dialogImageUrl" alt="Preview Image" />
        </el-dialog>
    </el-form>
</template>
<script setup>
import { nextTick, ref, watch } from 'vue';
import useAppStore from '@/store/modules/app';
import { ElMessage, ElLoading } from 'element-plus';
import AMap from '@/components/Amap/change';
import wangeditor from "@/components/wangEditor";
import { uploadFile, uploadLocalFile } from "@/utils/uploadFile";
import { verifyNumberDoSix } from "@/utils/verify";
import moment from 'moment';
import { getRegionAllApi } from '@/api/common';

import { uploadToOSS } from '@/utils/ossUpload'


const appStore = useAppStore();
const props = defineProps({
    option: { type: Object, default: {} },
    form: { type: Object, default: {} },
    visible: { type: Boolean, default: false },
});
const emits = defineEmits(['formChange', 'formBlur']);
// 远程检索控件参数
const loading = ref(false);
const options = ref([]);
const seleRemotetRef = ref();
const remoteMethod = (query, request, key, itemNum, requestParams) => {
    if (query) {
        loading.value = true
        const queryData = { pageNum: 1, pageSize: itemNum ? itemNum : 10 };
        if (requestParams && Array.isArray(requestParams)) {
            for (const item of requestParams) { queryData[item.key] = item.value == "form" ? form.value[item.key] : item.value }
        }
        queryData[key] = query;
        request(queryData).then(res => {
            options.value = res.data.records;
            loading.value = false;
        })
    } else {
        options.value = []
    }
}
const dialogImageUrl = ref('');
const dialogVisible = ref(false);
const handlePictureCardPreview = (uploadFile) => {
    dialogImageUrl.value = uploadFile.url
    dialogVisible.value = true
}
const form = ref({});
const formRef = ref();
// 日期范围选择时默认将原始数组分至两个参数
const handleDate = (val, prop) => {
    if (val && Array.isArray(val) && val.length > 0) {
        form.value[`start${prop}`] = val[0]
        form.value[`end${prop}`] = val[1]
    }
    formChange(prop);
}
/**
 * 省市区联动模块
 */
const provinces = ref([]);
const getProviceData = () => {
    return getRegionAllApi({ level: 1, ascFields: 'areaCode', disablePurview: true }).then(res => {
        provinces.value = res.data.map(item => { return { value: item.id, label: item.label, id: item.id } });
    })
}
let provinceName = '';
const citys = ref([]);
let cityName = '';
const areas = ref([]);
let areaName = '';
const setProvince = () => {
    form.value.city = null;
    cityName = "";
    form.value.town = null;
    areaName = "";
    changeCity();
    configAddress();
}
const changeCity = () => {
    const selectedProvince = provinces.value.find(province => province.value == form.value.province);
    if (!selectedProvince) return;
    provinceName = selectedProvince.label;
    getRegionAllApi({ level: 2, ascFields: 'areaCode', parentId: selectedProvince.id, disablePurview: true }).then(res => {
        citys.value = res.data.map(item => { return { value: item.id, label: item.label, id: item.id } });
    })

}
const configAddress = () => form.value.address = provinceName + (cityName != provinceName ? cityName : "") + areaName;
const setCity = () => {
    form.value.town = null;
    areaName = "";
    changeCounty();
    configAddress();
}
const changeCounty = () => {
    const selectedcity = citys.value.find(city => city.value == form.value.city);
    if (!selectedcity) return;
    cityName = selectedcity.label;
    getRegionAllApi({ level: 3, ascFields: 'areaCode', parentId: selectedcity.id, disablePurview: true }).then(res => {
        areas.value = res.data.map(item => { return { value: item.id, label: item.label, id: item.id } });
    })
}
const setCounty = () => {
    const selectedArea = areas.value.find(area => area.value == form.value.town);
    if (!selectedArea) return;
    areaName = selectedArea.label;
    setAddress();
    configAddress();
}
let oldAddress = "";
const setAddress = () => {
    if (oldAddress == form.value.address) return;
    const hasMapType = props.option.formitem.some(item => item.type == 'map');
    if (!hasMapType) return;
    aMapRef.value[0].geoCode(form.value.address)
    oldAddress = form.value.address;
}
/**
 * 地图配置模块
 */
const aMapRef = ref();
const changeLnglat = (coordinate) => {
    form.value.longitde = coordinate.lng;
    form.value.latitude = coordinate.lat;
}
const changeAddress = async (data) => {
    if(provinces.value.length==0){
      form.value.address=data.formattedAddress
      return
    }
    const { formattedAddress, addressComponent } = data;
    const { province: provinceName, city: cityName, district: areaName } = addressComponent || {};
    if (form.value.address === formattedAddress) return;
    form.value.address = formattedAddress;
    // 省
    const selectedProvince = provinces.value.find(province => province.label === provinceName);
    if (!selectedProvince) {
        form.value.province = undefined;
        form.value.city = undefined;
        form.value.town = undefined;
        ElMessage.warning('定位省份未开通！');
        return;
    }
    if (form.value.province !== selectedProvince.value) {
        form.value.province = selectedProvince.value;
        await getRegionAllApi({ level: 2, ascFields: 'areaCode', parentId: selectedProvince.id, disablePurview: true }).then(res => {
            citys.value = res.data.map(item => { return { value: item.id, label: item.label, id: item.id } });
        })
    }
    // 市
    const actualCityName = cityName || provinceName;
    const selectedCity = citys.value.find(city => city.label === actualCityName);
    if (!selectedCity) {
        form.value.city = undefined;
        form.value.town = undefined;
        ElMessage.warning('定位城市未开通！');
        return;
    }
    if (form.value.city !== selectedCity.value) {
        form.value.city = selectedCity.value;
        await getRegionAllApi({ level: 3, ascFields: 'areaCode', parentId: selectedCity.id, disablePurview: true }).then(res => {
            areas.value = res.data.map(item => { return { value: item.id, label: item.label, id: item.id } });
        })
    }
    // 区
    const selectedArea = areas.value.find(area => area.label === areaName);
    if (!selectedArea) {
        await getRegionAllApi({ level: 3, ascFields: 'areaCode', parentId: selectedCity.id, disablePurview: true }).then(res => {
            areas.value = res.data.map(item => { return { value: item.id, label: item.label, id: item.id } });
        })
    }
    if (form.value.town !== selectedArea.value) form.value.town = selectedArea?.value;
};
/**
 * 富文本编辑模块
 */
const wadtrRef = ref();
/**
 * 自定义校验规则
 */
const customJudge = (judge, from) => { return judge ? judge(from) : true };
/**
 * 自定义校验状态
 */
const customRequired = (prop, required, from) => {
    if (required) {
        return required(from);
    } else if (props.option.rules[prop]) {
        return props.option.rules[prop][0].required
    } else {
        return false;
    }
};
/**
 * 表单组件 change
 */
const formChange = (prop) => emits('formChange', { prop, form: form.value });
/**
 * 表单组件 blur
 */
const formBlur = (prop) => emits('formBlur', { prop, form: form.value });
/**
 * map 组件 blur
 */
const formBlurMap = (prop) => {
    if (form.value.longitde && form.value.latitude && aMapRef.value) aMapRef.value[0].blurMarker({ lng: form.value.longitde, lat: form.value.latitude })
    emits('formBlur', { prop, form: form.value })
};
//上传图片
const uploadImg = (type, req, prop, fileType, path, uploadType = 'obs') => {
    const loading = ElLoading.service({ lock: true, text: "上传中。。。", background: "rgba(0, 0, 0, 0.7)" });
    if (fileType == 'img') {
        const imgType = ["image/jpeg", "image/png"];
        if (!imgType.includes(req.file.type)) {
            ElMessage.warning("选择的图片格式有误，请重新选择")
            loading.close();
            return
        }
        if (req.file.size >= (5 * 1024 * 1024)) {
            ElMessage.warning("图片文件不能超过5MB，请重新选择")
            loading.close();
            return
        }
    }
    
    // 根据上传类型选择不同的上传方式
    const uploadMethod = uploadType === 'local' ? uploadLocalFile : uploadType==='ali'? uploadToOSS :uploadFile;
    
    uploadMethod({ file: req.file, path: path ? path : 'avatar' }).then(res => {
        if (!res) return;
        if (type == 'uploads') {
            if (!Array.isArray(form.value[prop])) form.value[prop] = []
            form.value[prop][form.value[prop].length - 1].url = res;
        } else {
            form.value[prop] = res;
        }
        loading.close();
    }).catch(error => {
        console.error('上传失败:', error);
        ElMessage.error(error.message || '上传失败');
        loading.close();
    })
}
const changeUploadsFile = () => {
    for (const item of props.option.formitem) {
        switch (item.type) {
            case "uploads":
                if (Array.isArray(form.value[item.prop]) && form.value[item.prop].length > 0) {
                    const data = form.value[item.prop].map(item => {
                        const match = item.match(/\/([^/]+)$/);
                        return { name: match ? match[1] : '未知名称', url: item }
                    })
                    form.value[item.prop] = data;
                } else {
                    form.value[item.prop] = []
                }
                break;
        }
    }
}

const parseAddress = (address) =>{
  aMapRef.value[0].geoCode(address)
}

const setMarker = () => {
    if (aMapRef.value) aMapRef.value[0].setMarker({ lng: form.value.longitde, lat: form.value.latitude });
}
// 监听表单显示时应做的初始化操作
watch(() => props.visible, (news) => {
    if (!news) return;
    form.value = props.form;
    changeUploadsFile();
    const formRanges = [];
    const formOpSelRemote = [];
    const formOptionRich = [];
    let isAddress = false;
    for (const obj of props.option.formitem) {
        if (obj.type.includes("range") && obj.category !== "weekrange") {
            formRanges.push(obj);
        } else if (obj.type === "select_remote" && (!obj.judge || obj.judge(form.value))) {
            formOpSelRemote.push(obj);
        } else if (obj.type === "rich" && (!obj.judge || obj.judge(form.value))) {
            formOptionRich.push(obj);
        } else if (obj.type == "address") {
            isAddress = true;
        }
    }
    formRanges.forEach(item => {
        form.value[item.prop] = [props.form[`start${item.prop}`] || undefined, props.form[`end${item.prop}`] || undefined];
    });
    nextTick(() => {
        if (form.value.id) {
            // 地图坐标更新
            setMarker();
            // 远程搜索项赋值
            for (const item of formOpSelRemote) {
                const prop = item.prop;
                const textProp = `${prop}Text`;
                const values = form.value[prop];
                const texts = form.value[textProp];
                if (Array.isArray(values) && Array.isArray(texts)) {
                    values.forEach((value, index) => {
                        const setData = {};
                        setData[item.customValue || 'value'] = value;
                        setData[item.customLabel || 'label'] = texts[index];
                        options.value.push(setData);
                    });
                } else if (values && texts) {
                    const setData = {};
                    setData[item.customValue || 'value'] = values;
                    setData[item.customLabel || 'label'] = texts;
                    options.value.push(setData);
                }
            }
            // 需要延迟加载组件
            setTimeout(async () => {
                // 富文本项项赋值
                for (const index in formOptionRich) { wadtrRef.value[index].valueHtml = form.value[formOptionRich[index].prop] };
                if (isAddress) {
                    getProviceData();
                    getRegionAllApi({ level: 3, ascFields: 'areaCode', parentId: form.value.city, disablePurview: true }).then(res => {
                        areas.value = res.data.map(item => { return { value: item.id, label: item.label, id: item.id } });
                    })
                    getRegionAllApi({ level: 2, ascFields: 'areaCode', parentId: form.value.province, disablePurview: true }).then(res => {
                        citys.value = res.data.map(item => { return { value: item.id, label: item.label, id: item.id } });
                    })
                }
            }, 100);
        } else {
            if (isAddress) getProviceData();
            // 初始化默认值
            const newObj = props.option.formitem.reduce((acc, obj) => {
                if ((!obj.judge || (obj.judge && obj.judge(form.value))) && obj.type != 'custom') acc[obj.prop] = obj.default;
                return acc;
            }, {});
            form.value = { ...form.value, ...newObj };
            changeUploadsFile();
            options.value = [];
            // 远程搜索项赋值
            for (const item of formOpSelRemote) {
                const { default: defaults, defaultText, customValue, customLabel } = item;
                if (Array.isArray(defaults) && Array.isArray(defaultText)) {
                    defaults.forEach((value, index) => {
                        const setData = {
                            [customValue || 'value']: value,
                            [customLabel || 'label']: defaultText[index],
                        };
                        options.value.push(setData);
                    });
                } else if (defaults && defaultText) {
                    const setData = {
                        [customValue || 'value']: defaults,
                        [customLabel || 'label']: defaultText,
                    };
                    options.value.push(setData);
                }
            }
            // 需要延迟加载组件
            setTimeout(() => {
                // 清除表单校验效果
                formRef.value.clearValidate();
                // 地图恢复至初始坐标
                if (aMapRef.value) aMapRef.value[0].reloadMap();
                // 富文本恢复至初始内容
                for (const index in formOptionRich) { wadtrRef.value[index].valueHtml = '<p><br></p>' };
            }, 100);
        }
    })

}, { deep: true, immediate: true })
/**
 * 禁止选择范围
 */
const disabledDate = (time, now, disabledFun) => {
    if (now) {
        const nowTime = moment(now).valueOf();
        return (time.getTime() + 24 * 60 * 60 * 1000) <= nowTime || (disabledFun ? disabledFun(time) : false);
    }
    return false;
}
defineExpose({ form, formRef, setMarker, parseAddress })
</script>
<style scoped lang="scss">
.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
}

.row_class {
    padding: 0 20px;
}

@media screen and (max-width: 768px) {
    .row_class {
        padding: 0;
    }
}
</style>
<style>
.avatar-uploader .el-upload {
    width: 178px;
    height: 178px;
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.avatar-uploader .el-upload-list__item {
    width: 178px;
    height: 178px;
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
}

.el-popper.is-customized {
    padding: 6px 12px;
    background: linear-gradient(90deg, rgb(159, 229, 151), rgb(204, 229, 129));
}

.el-popper.is-customized .el-popper__arrow::before {
    background: linear-gradient(45deg, #b2e68d, #bce689);
    right: 0;
}
</style>