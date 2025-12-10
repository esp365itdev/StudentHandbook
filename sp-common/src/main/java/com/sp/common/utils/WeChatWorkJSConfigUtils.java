package com.sp.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.sp.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 企业微信JS-SDK配置工具类
 * 用于生成企业微信JS-SDK所需的配置参数
 */
public class WeChatWorkJSConfigUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatWorkJSConfigUtils.class);
    
    // 企业微信配置
    private static final String CORP_ID = "ww04fad852e91fd490";
    private static final String AGENT_ID = "1000032";
    private static final String SECRET = "CqFIJ9HcirbRTxGUkLmsGzu1aLPJZarRtxgmziVzrfQ";
    
    // 企业微信API地址
    private static final String GET_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    private static final String GET_TICKET_URL = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket";
    
    /**
     * 生成企业微信JS-SDK配置参数
     * @param url 当前页面URL（不包含#及其后面部分）
     * @return JS-SDK配置参数
     * @throws Exception 生成过程中出现的异常
     */
    public static Map<String, Object> generateConfig(String url) throws Exception {
        // 获取access_token
        String accessToken = getAccessToken();
        
        // 获取jsapi_ticket
        String ticket = getJsApiTicket(accessToken);
        
        // 生成时间戳
        long timestamp = System.currentTimeMillis() / 1000;
        
        // 生成随机字符串
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        
        // 生成签名
        String signature = generateSignature(ticket, nonceStr, timestamp, url);
        
        // 构造配置参数
        Map<String, Object> config = new HashMap<>();
        config.put("appId", CORP_ID);
        config.put("timestamp", timestamp);
        config.put("nonceStr", nonceStr);
        config.put("signature", signature);
        
        return config;
    }
    
    /**
     * 获取Access Token
     * @return access_token
     * @throws Exception 获取失败时抛出异常
     */
    private static String getAccessToken() throws Exception {
        String param = "corpid=" + CORP_ID + "&corpsecret=" + SECRET;
        String response = HttpUtils.sendGet(GET_TOKEN_URL, param);
        JSONObject result = JSONObject.parseObject(response);
        
        if (result != null && result.containsKey("access_token")) {
            return result.getString("access_token");
        } else {
            throw new Exception("获取access_token失败: " + result.getString("errmsg"));
        }
    }
    
    /**
     * 获取jsapi_ticket
     * @param accessToken access_token
     * @return jsapi_ticket
     * @throws Exception 获取失败时抛出异常
     */
    private static String getJsApiTicket(String accessToken) throws Exception {
        String url = GET_TICKET_URL + "?access_token=" + accessToken;
        String response = HttpUtils.sendGet(url);
        JSONObject result = JSONObject.parseObject(response);
        
        if (result != null && result.containsKey("ticket")) {
            return result.getString("ticket");
        } else {
            throw new Exception("获取jsapi_ticket失败: " + result.getString("errmsg"));
        }
    }
    
    /**
     * 生成签名
     * @param ticket jsapi_ticket
     * @param nonceStr 随机字符串
     * @param timestamp 时间戳
     * @param url 当前页面URL
     * @return 签名
     * @throws Exception 生成过程中出现的异常
     */
    private static String generateSignature(String ticket, String nonceStr, long timestamp, String url) throws Exception {
        // 按照字典序排序参数
        Map<String, String> params = new HashMap<>();
        params.put("jsapi_ticket", ticket);
        params.put("noncestr", nonceStr);
        params.put("timestamp", String.valueOf(timestamp));
        params.put("url", url);
        
        // 按照键名排序
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        
        // 拼接参数
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        // 删除最后一个&
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        
        // SHA1加密
        return sha1(sb.toString());
    }
    
    /**
     * SHA1加密
     * @param str 待加密字符串
     * @return 加密结果
     * @throws NoSuchAlgorithmException 加密算法不存在异常
     */
    private static String sha1(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] digest = md.digest(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}