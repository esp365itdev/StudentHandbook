package com.sp.common.utils;

/**
 * 企业微信工具类测试
 */
public class WeChatWorkUtilsTest {
    
    public static void main(String[] args) {
        // 测试获取access_token
        String accessToken = WeChatWorkUtils.getAccessToken();
        System.out.println("Access Token: " + accessToken);
        
        if (accessToken != null) {
            // 测试发送文本消息给所有用户
            System.out.println("测试发送文本消息:");
            String result = WeChatWorkUtils.sendTextMessage("@all", "这是一条测试消息");
            System.out.println("发送结果: " + result);
            
            // 解析发送结果
            parseResult(result);
            
            // 测试发送Markdown消息给所有用户
            System.out.println("\n测试发送Markdown消息:");
            String markdownResult = WeChatWorkUtils.sendMarkdownMessage("@all", "## 这是一条Markdown测试消息\n\n*内容*\n\n> 引用文本");
            System.out.println("发送结果: " + markdownResult);
            
            // 解析发送结果
            parseResult(markdownResult);
        } else {
            System.out.println("获取 Access Token 失败");
        }
    }
    
    private static void parseResult(String result) {
        try {
            java.util.Map<String, Object> resultMap = com.sp.common.json.JSON.unmarshal(result, java.util.Map.class);
            if (resultMap != null && resultMap.containsKey("errcode")) {
                String errcode = resultMap.get("errcode").toString();
                String errmsg = resultMap.get("errmsg").toString();
                System.out.println("错误代码: " + errcode);
                System.out.println("错误信息: " + errmsg);
                
                if ("0".equals(errcode)) {
                    System.out.println("消息发送成功！");
                } else {
                    System.out.println("消息发送失败！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}