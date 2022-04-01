package com.skcc.spring.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 데이터 암/복호화 해싱 처리
 */
public class CryptoNoHexUtil {
	
    private static String transformationRule = "AES/CBC/PKCS5Padding";
    private static String IV = "ABCDEFGHIJKLMNOP";
    
    /**
	 * AES 복호화 메소드
	 * @param cipherText
	 * @param encryptionKey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String cipherText, String encryptionKey) throws Exception {
		return decrypt(cipherText.getBytes("UTF-8"), encryptionKey);
	}
	
    /**
	 * AES 복호화 메소드
	 *
	 * @param cipherText
	 * @param encryptionKey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(byte[] cipherText, String encryptionKey) throws Exception {
    	Cipher cipher = Cipher.getInstance(transformationRule);
    	IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes("UTF-8"));
		SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
		cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
		byte[] decodeBase64ByteArray = Base64.getDecoder().decode(cipherText);
		byte[] decryptedByteArray = cipher.doFinal(decodeBase64ByteArray);
		return new String(decryptedByteArray, "UTF-8");
	}
	
}
