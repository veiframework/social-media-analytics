import { aliyunStsToken } from '@/api/common'
import OSS from "ali-oss";


import setting from '@/settings'

export const uploadToOSS = (data) => {
	const {file, path, pramas} = data;
	return new Promise((resolve, reject) => {

		const client = new OSS({
			region: setting.ossConfig.region,
			accessKeyId: setting.ossConfig.accessKeyId,
			accessKeySecret: setting.ossConfig.accessKeySecret,
			bucket: setting.ossConfig.bucketName
		});
		let arr = file.name.split('.')
		let extension = arr[arr.length-1]
		const fileKey = `/${path}/` + generateId(4)+ file.size +'.'+extension;
		client.multipartUpload(fileKey, file, {}).then((res) => {
			let url =  res.res.requestUrls[0].split('?')[0];
			console.log(url)
			resolve(url)
		}).catch(err => {
			console.log(err)
			reject(err);
		});
	})
}

// 一行代码实现
export const generateId = (randomLength = 6) => {
	const now = new Date();
	const timestamp = now.getFullYear() +
		String(now.getMonth() + 1).padStart(2, '0') +
		String(now.getDate()).padStart(2, '0') +
		String(now.getHours()).padStart(2, '0') +
		String(now.getMinutes()).padStart(2, '0') +
		String(now.getSeconds()).padStart(2, '0');
	const random = Math.random().toString().substr(2, randomLength);
	return timestamp + random;
};


// file 文件 category 文件夹名称 params 传递参数 callback 返回函数
export const uploadToAliyun = (data) => {
	const { file, path, pramas } = data;
	return new Promise((resolve, reject) => {
		aliyunStsToken(pramas).then(res => {
			const creds = res.data;
			const client = new OSS({
				region: "oss-cn-zhangjiakou",
				accessKeyId: creds.accessKeyId,
				accessKeySecret: creds.accessKeySecret,
				stsToken: creds.securityToken,
				bucket: creds.bucketName,
				endpoint: creds.endpoint
			});
			const fileKey = `/${path}/` + file.size + file.name;
			client.multipartUpload(fileKey, file, {}).then(() => {
				let url = `${creds.url}${fileKey}`
				resolve(url)
			}).catch(err => {
				reject(err);
			});
		});
	})
} 