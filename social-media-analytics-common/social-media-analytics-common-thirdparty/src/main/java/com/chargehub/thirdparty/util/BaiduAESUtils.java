package com.chargehub.thirdparty.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BaiduAESUtils {

    /**
	 * 对密文进行解密
	 *
	 * @param text 需要解密的密文
	 *
	 * @return 解密得到的明文
	 *
	 * @throws Exception 异常错误信息
	 */
	public static String decrypt(String text, String sessionKey, String ivStr) throws Exception {
		byte[] aesKey = Base64.decodeBase64(sessionKey + "=");
		byte[] original;
		//try {
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
		byte[] ivBytes = Base64.decodeBase64(ivStr);
		IvParameterSpec iv = new IvParameterSpec(ivBytes);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
		byte[] encrypted = Base64.decodeBase64(text);
		original = cipher.doFinal(encrypted);
		//} catch (Exception e) {
		//	throw new Exception(e);
		//}
		String xmlContent;
		//String fromClientId;
		//try {
		// 去除补位字符
		byte[] bytes = PKCS7Encoder.decode(original);
		// 分离16位随机字符串,网络字节序和ClientId
		byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
		int xmlLength = recoverNetworkBytesOrder(networkOrder);
		if (xmlLength > 65535) {
			/*
			 * 注意：开发者解密加密数据出现乱码或者偶发OOM，一般是sessionKey过期导致
			 *
			 * 开发者可以根据实际情况，改变判断值大小
			 */
			throw new RuntimeException("aesKey invalid");
		}
		xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), StandardCharsets.UTF_8);
		//fromClientId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), StandardCharsets.UTF_8);
		//} catch (Exception e) {
		//	throw new Exception(e);
		//}
		return xmlContent;
	}

	public static String getType(Object test) {
		return test.getClass().getName();

	}

	/**
	 * 还原4个字节的网络字节序
	 *
	 * @param orderBytes 字节码
	 *
	 * @return sourceNumber
	 */
	private static int recoverNetworkBytesOrder(byte[] orderBytes) {
		int sourceNumber = 0;
		int length = 4;
		int number = 8;
		for (int i = 0; i < length; i++) {
			sourceNumber <<= number;
			sourceNumber |= orderBytes[i] & 0xff;
		}
		return sourceNumber;
	}

	public static void main(String[] args) throws Exception {
			String text = "PQpXzfMl4DuN+sysAsA/U6JQ9mNgIcTlSHddQdZC9JNSxTAyqCALPPOvEe88L69W2P5/ATNnu0CJS30g09h72xzxKQ5OWeNW9Jw4vSzJPho=";
			String sessionKey = "00de4c32f42db73c6386a72d462eccb9";
			String ivStr = "00de4c32f42db73c6386aw==";
			System.out.println(decrypt(text, sessionKey, ivStr));
	}
}
