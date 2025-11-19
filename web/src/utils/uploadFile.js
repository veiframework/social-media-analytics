import axios from "axios";
import { getOBSTokenApi, getPrivateOBSTokenApi } from "@/api/common"
import { ElLoading } from "element-plus";
import request from '@/utils/request';
import { getToken } from '@/utils/auth';

import settings from '@/settings'

/**
 * 公共上传
 */
export const uploadFile = (data) => {
	const { file, path } = data;
	return new Promise((resolve, reject) => {
		const filePath = `${path}/${encodeURIComponent(file.name)}`
		getOBSTokenApi({ filePath, method: 'PUT' }).then(res => {
			const reader = new FileReader();
			reader.onload = function (event) {
				const binaryData = event.target.result; // 二进制数据
				axios.put(res.data.url, binaryData, { headers: res.data.headers })
					.then(() => resolve(decodeURIComponent(res.data.url))).catch(error => reject(error));
			};
			reader.readAsArrayBuffer(file);
		})
	})
}
/**
 * 私有上传
 */
export const uploadPrivateFile = (data) => {
	const loadingUp = ElLoading.service({ lock: true, text: '正在导入。。。', background: 'rgba(0, 0, 0, 0.7)' });
	const { file, path } = data;
	return new Promise((resolve, reject) => {
		const filePath = `${path}/${encodeURIComponent(file.name)}`
		getPrivateOBSTokenApi({ filePath, method: 'PUT' }).then(res => {
			const reader = new FileReader();
			reader.onload = function (event) {
				const binaryData = event.target.result; // 二进制数据
				axios.put(res.data.url, binaryData, { headers: res.data.headers })
					.then(() => resolve({ url: decodeURIComponent(res.data.url), loadingUp })).catch(error => reject(error));
			};
			reader.readAsArrayBuffer(file);
		})
	})
}

/**
 * 本地上传 - 使用 /admin-api/upload 接口
 */
export const uploadLocalFile = (data) => {
	const { file, path } = data;
	const loading = ElLoading.service({ 
		lock: true, 
		text: '正在上传...', 
		background: 'rgba(0, 0, 0, 0.7)' 
	});
	
	return new Promise((resolve, reject) => {
		const formData = new FormData();
		formData.append('file', file);
		if (path) {
			formData.append('path', path);
		}
		
		request({
			url: '/admin-api/upload',
			method: 'post',
			data: formData,
			headers: {
				'Content-Type': 'multipart/form-data',
				'Authorization': 'Bearer ' + getToken()
			}
		}).then(res => {
			loading.close();
			if (res.code === 200) {
				resolve( settings.uploadHost +'/'+res.data.url );
			} else {
				reject(new Error(res.msg || '上传失败'));
			}
		}).catch(error => {
			loading.close();
			reject(error);
		});
	});
}