package com.sp.common.utils;

import java.util.HashMap;
import java.util.Map;
import com.sp.common.utils.http.HttpUtils;
import com.sp.common.json.JSON;

/**
 * 企业微信工具类
 */
public class WeChatWorkUtils {
    
    // 企业微信配置
    private static final String CORP_ID = "ww04fad852e91fd490";
    private static final String AGENT_ID = "1000032";
    private static final String SECRET = "CqFIJ9HcirbRTxGUkLmsGzu1aLPJZarRtxgmziVzrfQ";
    
    // 企业微信API地址
    private static final String GET_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    private static final String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    /**
     * 获取Access Token
     * @return access_token
     */
    public static String getAccessToken() {
        String param = "corpid=" + CORP_ID + "&corpsecret=" + SECRET;
        String response = HttpUtils.sendGet(GET_TOKEN_URL, param);
        try {
            Map<String, Object> result = JSON.unmarshal(response, Map.class);
            if (result != null && result.containsKey("access_token")) {
                return result.get("access_token").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送文本消息
     * @param toUser 接收用户的账号列表，多个用 | 分隔，最多支持1000个
     * @param content 消息内容
     * @return 发送结果
     */
    public static String sendTextMessage(String toUser, String content) {
        return sendTextMessage(toUser, null, null, content, false);
    }

    /**
     * 发送文本消息
     * @param toUser 接收用户的账号列表，多个用 | 分隔，最多支持1000个
     * @param toParty 接收部门的ID列表，多个用 | 分隔，最多支持100个
     * @param toTag 接收标签的ID列表，多个用 | 分隔，最多支持100个
     * @param content 消息内容
     * @param safe 是否保密消息
     * @return 发送结果
     */
    public static String sendTextMessage(String toUser, String toParty, String toTag, String content, boolean safe) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return "{\"errcode\":-1,\"errmsg\":\"获取access_token失败\"}";
        }

        // 构造请求参数
        Map<String, Object> message = new HashMap<>();
        message.put("touser", toUser != null ? toUser : "@all");
        if (toParty != null) {
            message.put("toparty", toParty);
        }
        if (toTag != null) {
            message.put("totag", toTag);
        }
        message.put("msgtype", WeChatWorkMessageType.TEXT.getValue());
        message.put("agentid", AGENT_ID);
        
        Map<String, String> textContent = new HashMap<>();
        textContent.put("content", content);
        message.put("text", textContent);
        
        message.put("safe", safe ? "1" : "0");

        try {
            String jsonParams = JSON.marshal(message);
            String url = SEND_MESSAGE_URL + "?access_token=" + accessToken;
            return HttpUtils.sendPost(url, jsonParams, "application/json;charset=utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"errcode\":-1,\"errmsg\":\"发送消息异常：" + e.getMessage() + "\"}";
        }
    }
    
    /**
     * 发送Markdown消息
     * @param toUser 接收用户的账号列表，多个用 | 分隔，最多支持1000个
     * @param content markdown内容
     * @return 发送结果
     */
    public static String sendMarkdownMessage(String toUser, String content) {
        return sendMarkdownMessage(toUser, null, null, content, false);
    }
    
    /**
     * 发送Markdown消息
     * @param toUser 接收用户的账号列表，多个用 | 分隔，最多支持1000个
     * @param toParty 接收部门的ID列表，多个用 | 分隔，最多支持100个
     * @param toTag 接收标签的ID列表，多个用 | 分隔，最多支持100个
     * @param content markdown内容
     * @param safe 是否保密消息
     * @return 发送结果
     */
    public static String sendMarkdownMessage(String toUser, String toParty, String toTag, String content, boolean safe) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return "{\"errcode\":-1,\"errmsg\":\"获取access_token失败\"}";
        }

        // 构造请求参数
        Map<String, Object> message = new HashMap<>();
        message.put("touser", toUser != null ? toUser : "@all");
        if (toParty != null) {
            message.put("toparty", toParty);
        }
        if (toTag != null) {
            message.put("totag", toTag);
        }
        message.put("msgtype", WeChatWorkMessageType.MARKDOWN.getValue());
        message.put("agentid", AGENT_ID);
        
        Map<String, String> markdownContent = new HashMap<>();
        markdownContent.put("content", content);
        message.put("markdown", markdownContent);
        
        message.put("safe", safe ? "1" : "0");

        try {
            String jsonParams = JSON.marshal(message);
            String url = SEND_MESSAGE_URL + "?access_token=" + accessToken;
            return HttpUtils.sendPost(url, jsonParams, "application/json;charset=utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"errcode\":-1,\"errmsg\":\"发送消息异常：" + e.getMessage() + "\"}";
        }
    }
}