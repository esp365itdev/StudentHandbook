package com.sp.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;

/**
 * 企业微信回调工具类
 * 用于处理企业微信回调相关的签名验证和消息解密
 */
public class WeChatWorkCallbackUtils {
    
    /**
     * 验证签名
     *
     * @param token Token
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param signature 签名
     * @return 验证结果
     */
    public static boolean verifySignature(String token, String timestamp, String nonce, String signature) {
        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);
        
        StringBuilder content = new StringBuilder();
        for (String s : arr) {
            content.append(s);
        }
        
        MessageDigest md;
        String tmpStr = null;
        
        try {
            md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(content.toString().getBytes(StandardCharsets.UTF_8));
            tmpStr = byteToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA1算法不可用" + e.getMessage());
        }
        
        System.out.println("计算得出的签名为: " + tmpStr + ", 企业微信传递的签名为: " + signature);
        
        return tmpStr != null && tmpStr.equals(signature);
    }
    
    /**
     * 验证签名（用于URL验证，包含echostr参数）
     *
     * @param token Token
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param echostr 加密的随机字符串
     * @param signature 签名
     * @return 验证结果
     */
    public static boolean verifySignatureWithEchoStr(String token, String timestamp, String nonce, String echostr, String signature) {
        String[] arr = new String[]{token, timestamp, nonce, echostr};
        Arrays.sort(arr);
        
        StringBuilder content = new StringBuilder();
        for (String s : arr) {
            content.append(s);
        }
        
        MessageDigest md;
        String tmpStr = null;
        
        try {
            md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(content.toString().getBytes(StandardCharsets.UTF_8));
            tmpStr = byteToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA1算法不可用" + e.getMessage());
        }
        
        System.out.println("参与签名的参数排序后: " + Arrays.toString(arr));
        System.out.println("拼接后的字符串: " + content.toString());
        System.out.println("计算得出的签名为: " + tmpStr + ", 企业微信传递的签名为: " + signature);
        
        return tmpStr != null && tmpStr.equalsIgnoreCase(signature);
    }
    
    /**
     * 字节数组转十六进制字符串
     *
     * @param hash 字节数组
     * @return 十六进制字符串
     */
    private static String byteToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    
    /**
     * 解密echostr
     *
     * @param echostr 加密的echostr
     * @param encodingAesKey AES密钥
     * @return 解密后的echostr
     * @throws Exception 解密异常
     */
    public static String decryptEchoStr(String echostr, String encodingAesKey) throws Exception {
        // 处理encodingAesKey，确保它是有效的Base64字符串
        String processedEncodingAesKey = encodingAesKey.trim();
        if (processedEncodingAesKey.length() == 43) {
            processedEncodingAesKey += "=";
        }
        
        System.out.println("处理后的echostr: " + echostr);
        System.out.println("处理后的encodingAesKey: " + processedEncodingAesKey);
        
        // 对密文进行Base64解码
        byte[] aesKey = decodeBase64(processedEncodingAesKey);
        byte[] encryptedData = decodeBase64(echostr);
        
        // 使用AESKey做AES解密
        byte[] decryptedData = decrypt(aesKey, encryptedData);
        
        // 去掉头部的16个随机字节
        byte[] content = Arrays.copyOfRange(decryptedData, 16, decryptedData.length);
        
        // 获取msg长度（前4个字节）
        int msgLen = 0;
        for (int i = 0; i < 4; i++) {
            msgLen <<= 8;
            msgLen |= content[i] & 0xFF;
        }
        
        // 截取msg_len长度的部分即为msg
        return new String(Arrays.copyOfRange(content, 4, 4 + msgLen), StandardCharsets.UTF_8);
    }
    
    /**
     * 解密方法
     *
     * @param key 密钥
     * @param encryptedData 加密数据
     * @return 解密后的数据
     * @throws Exception 解密异常
     */
    private static byte[] decrypt(byte[] key, byte[] encryptedData) throws Exception {
        byte[] iv = Arrays.copyOfRange(key, 0, 16);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(encryptedData);
    }
    
    /**
     * Base64解码
     *
     * @param content 待解码内容
     * @return 解码后的字节数组
     */
    private static byte[] decodeBase64(String content) {
        return java.util.Base64.getDecoder().decode(content);
    }
}