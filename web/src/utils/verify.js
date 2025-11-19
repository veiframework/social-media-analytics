/**
 * 输入验证方法
 */

// 金额输入验证
export function verifyAmount(value) {
	if (isNaN(value)) return ''
	return value.toString() ? value.toString().match(/^\d+(?:\.\d{0,2})?/)[0] : ''
}
// 数字输入验证
export function verifyNumber(value) {
	return value.replace(/[^\d]/g, '')
}
// 数字小数点验证
export function verifyNumberDoSix(value) {
	if (isNaN(value)) return ''
	return value.toString() ? value.toString().match(/^\d+(?:\.\d{0,6})?/)[0] : ''
}
// 保留小数位数（4）
export const formatDecimal = (num) => {
	if (typeof num !== 'number') return null;
	const str = num.toString();
	const decimalIndex = str.indexOf('.');
	return decimalIndex !== -1 && str.length - decimalIndex - 1 >= 5 ? parseFloat(num.toFixed(4)) : num;
}

/**
 * 输入验证正则
 */

// 数字验证
export const regularNumber = /[^\d]/g
// 数字字母验证
export const regularNumALet = /[^\w\.\/]/ig
// 包含中文字符验证
export const regularChinese = /[\u4e00-\u9fa5]/
// 只存在数字和小数点验证
export const regularNumberDo = /[^0-9.]/g


/**
 * 提交验证正则
 */
// 只存在数字验证
export const regularNumbers = /^\d+$/
// 身份证验证
export const regularIDCard = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
// 手机号验证 （注：搜索校验使用数字验证，此正则对于搜索校验不生效）
export const regularPhone = /^1[3456789]\d{9}$/
// 金额验证
export const regularAmount = /^\d+(\.\d{1,2})?$/
// 比例验证[0 - 100]整数
export const regularProportion = /^([0-9]{1,2}|100)$/
// 折扣幅度验证
export const regularAmplitude = /^((0\.[1-9]{1})|(([1-9]{1})(\.\d{1})?))$/
// 金额验证（保留4位）
export const regularAmount4 = /^\d+(\.\d{1,4})?$/