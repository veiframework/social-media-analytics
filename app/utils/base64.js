/**
 * Base64 编码（uniapp兼容）
 * @param {string} str 要编码的字符串
 * @returns {string} base64编码后的字符串
 */
export function base64Encode(str) {
  try {
    // 处理中文字符，先进行URI编码
    const utf8Str = encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, (match, p1) => {
      return String.fromCharCode('0x' + p1);
    });
    
    // 使用btoa进行base64编码
    if (typeof btoa !== 'undefined') {
      return btoa(utf8Str);
    } else {
      // 如果btoa不存在，使用自定义实现
      return customBase64Encode(utf8Str);
    }
  } catch (error) {
    console.error('Base64编码失败:', error);
    return '';
  }
}

/**
 * Base64 解码（uniapp兼容）
 * @param {string} str base64编码的字符串
 * @returns {string} 解码后的字符串
 */
export function base64Decode(str) {
  try {
    let decodedStr = '';
    
    // 使用atob进行base64解码
    if (typeof atob !== 'undefined') {
      decodedStr = atob(str);
    } else {
      // 如果atob不存在，使用自定义实现
      decodedStr = customBase64Decode(str);
    }
    
    // 处理中文字符，进行URI解码
    return decodeURIComponent(decodedStr.split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
  } catch (error) {
    console.error('Base64解码失败:', error);
    return '';
  }
}

/**
 * 自定义Base64编码实现
 * @param {string} str 要编码的字符串
 * @returns {string} base64编码后的字符串
 */
function customBase64Encode(str) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
  let result = '';
  let i = 0;
  
  while (i < str.length) {
    const a = str.charCodeAt(i++);
    const b = i < str.length ? str.charCodeAt(i++) : 0;
    const c = i < str.length ? str.charCodeAt(i++) : 0;
    
    const bitmap = (a << 16) | (b << 8) | c;
    
    result += chars.charAt((bitmap >> 18) & 63);
    result += chars.charAt((bitmap >> 12) & 63);
    result += i - 2 < str.length ? chars.charAt((bitmap >> 6) & 63) : '=';
    result += i - 1 < str.length ? chars.charAt(bitmap & 63) : '=';
  }
  
  return result;
}

/**
 * 自定义Base64解码实现
 * @param {string} str base64编码的字符串
 * @returns {string} 解码后的字符串
 */
function customBase64Decode(str) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
  let result = '';
  let i = 0;
  
  // 移除非base64字符
  str = str.replace(/[^A-Za-z0-9+/]/g, '');
  
  while (i < str.length) {
    const encoded1 = chars.indexOf(str.charAt(i++));
    const encoded2 = chars.indexOf(str.charAt(i++));
    const encoded3 = chars.indexOf(str.charAt(i++));
    const encoded4 = chars.indexOf(str.charAt(i++));
    
    const bitmap = (encoded1 << 18) | (encoded2 << 12) | (encoded3 << 6) | encoded4;
    
    result += String.fromCharCode((bitmap >> 16) & 255);
    if (encoded3 !== 64) {
      result += String.fromCharCode((bitmap >> 8) & 255);
    }
    if (encoded4 !== 64) {
      result += String.fromCharCode(bitmap & 255);
    }
  }
  
  return result;
}