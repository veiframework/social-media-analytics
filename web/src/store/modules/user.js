import { login, logout, getInfo } from '@/api/login';
import { getToken, setToken, removeToken } from '@/utils/auth';
import moment from 'moment';
import { ElMessage } from 'element-plus';
import useClipboard from 'vue-clipboard3'
import axios from 'axios';
import { getAllCommunityApi, getAllLockerTemplateApi } from '@/api/base';
import {uploadLocalFile} from "@/utils/uploadFile";
const { toClipboard } = useClipboard();
import settings from '@/settings'

const useUserStore = defineStore('user', {
    state: () => ({
        token: getToken(),
        id: '',
        name: '',
        avatar: '',
        roles: [],
        permissions: [],
        communityList: [],
        templateList: [],
    }),
    actions: {
        // 登录
        login(userInfo) {
            const username = userInfo.username.trim();
            const password = userInfo.password;
            const code = userInfo.code;
            const uuid = userInfo.uuid;
            return new Promise((resolve, reject) => {
                login(username, password, code, uuid).then(res => {
                    let data = res.data;
                    setToken(data.access_token);
                    this.token = data.access_token;
                    resolve();
                }).catch(error => {
                    reject(error);
                })
            })
        },
        // 获取用户信息
        getInfo() {
            return new Promise((resolve, reject) => {
                getInfo().then(res => {
                    const user = res.user;
                    const avatar = user.avatar;
                    if (res.roles && res.roles.length > 0) { // 验证返回的roles是否是一个非空数组
                        this.roles = res.roles;
                        this.permissions = res.permissions;
                    } else {
                        this.roles = ['ROLE_DEFAULT']
                    }
                    this.id = user.userId;
                    this.name = user.nickName ? user.nickName : user.userName;
                    this.avatar = avatar;

                    resolve(res);
                }).catch(error => reject(error));
            })
        },
        // 更新小区信息
        updateCommunity() {
            getAllCommunityApi().then(res => this.communityList = res.data);
        },
        // 更新柜子模板
        updateTemplate() {
            getAllLockerTemplateApi().then(res => this.templateList = res.data);
        },
        // 退出系统
        logOut() {
            return new Promise((resolve, reject) => {
                logout(this.token).then(() => {
                    this.token = '';
                    this.roles = [];
                    this.permissions = [];
                    removeToken();
                    resolve();
                }).catch(error => reject(error));
            })
        },
        // 导入
        importFile(path) {
            return new Promise((resolve) => {
                const fileInput = document.createElement('input');
                fileInput.type = 'file';
                fileInput.style.display = 'none';
                fileInput.addEventListener('change', () => uploadPrivateFile({ path, file: fileInput.files[0] }).then((res) => resolve(res)));
                document.body.appendChild(fileInput);
                fileInput.click();
            })
        },
        importLocalFile(path) {
            return new Promise((resolve) => {
                const fileInput = document.createElement('input');
                fileInput.type = 'file';
                fileInput.style.display = 'none';
                fileInput.addEventListener('change', () => uploadLocalFile({ path, file: fileInput.files[0] }).then((res) => resolve(res.replace(settings.uploadHost+"/",''))));
                document.body.appendChild(fileInput);
                fileInput.click();
            })
        },
        // 导出 excel 表功能（Blob 导出）
        exportFile(data, text) {
            const blob = new Blob([data], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = text + '.xlsx';
            link.click();
            //释放内存
            window.URL.revokeObjectURL(link.href);
        },
        // 导出 excel 表功能（Url 导出）
        exportFileUrl(url, filename = "file") {
            const link = document.createElement('a');
            link.href = url;
            link.download = filename; // 强制下载而非预览
            document.body.appendChild(link); // 必须添加到 DOM
            link.click();
            document.body.removeChild(link); // 移除元素
            window.URL.revokeObjectURL(link.href); // 释放内存
        },
        // // 导出 excel 表功能（私有 Url 导出）
        // exportPrivateFileUrl(url, text) {
        // 	return new Promise((resolve, reject) => {
        // 		getPrivateFileApi(url).then(res => {
        // 			this.exportFile(res, text);
        // 			resolve();
        // 		}).catch(err => reject(err));
        // 	})
        // },
        // 获取日期时间
        getDate() {
            const startDate = moment().format('YYYY-MM') + '-01 00:00:00';
            const endDate = moment().format('YYYY-MM-DD') + ' 23:59:59';
            return [startDate, endDate];
        },
        getSevenDate() {
            const currentDate = moment().format('YYYY-MM-DD HH:mm:ss');
            const sevenDaysAgo = moment().subtract(7, 'days').format('YYYY-MM-DD HH:mm:ss');
            return [sevenDaysAgo, currentDate];
        },
        // 获取日期
        getDay() {
            const startDate = moment().format('YYYY-MM') + '-01';
            const endDate = moment().format('YYYY-MM-DD');
            return [startDate, endDate];
        },
        //	复制文本
        copyText(text) {
            return new Promise(async (resolve, reject) => {
                try {
                    await toClipboard(text)
                    ElMessage.success('链接复制成功');
                    resolve()
                } catch (e) {
                    ElMessage.warning('该浏览器不支持自动复制');
                    reject()
                }
            })
        },
    }
})

export default useUserStore
