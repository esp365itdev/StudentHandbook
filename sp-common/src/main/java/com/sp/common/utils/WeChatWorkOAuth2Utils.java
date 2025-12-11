package com.sp.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.sp.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * 企业微信OAuth2.0网页授权工具类
 * 用于企业微信应用的网页授权功能
 */
@Component
public class WeChatWorkOAuth2Utils {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatWorkOAuth2Utils.class);
    
    @Value("${wechat.work.corpId}")
    private String corpId;
    
    @Value("${wechat.work.agentId}")
    private String agentId;
    
    @Value("${wechat.work.secret}")
    private String secret;
    
    @Value("${wechat.work.redirectUri}")
    private String redirectUri;
    
    // 修改为OAuth2授权链接
    private static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    private static final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    private static final String USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";
    
    /**
     * 构造授权链接
     * @param state 用于防止csrf攻击，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * @return 授权链接
     */
    public String getAuthorizeUrl(String state) {
        StringBuilder urlBuilder = new StringBuilder(AUTHORIZE_URL);
        urlBuilder.append("?appid=").append(corpId);
        try {
            urlBuilder.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("URL编码失败", e);
            urlBuilder.append("&redirect_uri=").append(redirectUri);
        }
        urlBuilder.append("&response_type=code");
        urlBuilder.append("&scope=snsapi_base");
        urlBuilder.append("&agentid=").append(agentId);
        urlBuilder.append("&state=").append(state);
        urlBuilder.append("#wechat_redirect");
        logger.info("企业微信授权链接: {}", urlBuilder.toString());
        return urlBuilder.toString();
    }
    
    /**
     * 获取Access Token
     * @return Access Token
     * @throws Exception 获取失败时抛出异常
     */
    public String getAccessToken() throws Exception {
        StringBuilder urlBuilder = new StringBuilder(TOKEN_URL);
        urlBuilder.append("?corpid=").append(corpId);
        urlBuilder.append("&corpsecret=").append(secret);
        
        logger.info("准备获取access_token，URL: {}", urlBuilder.toString());
        
        String response = HttpUtils.sendGet(urlBuilder.toString());
        logger.info("获取access_token响应长度: {}", response != null ? response.length() : 0);
        
        if (response == null || response.isEmpty()) {
            logger.error("获取access_token失败，响应为空");
            throw new Exception("获取access_token失败，响应为空");
        }
        
        logger.debug("access_token响应内容(前100字符): {}", response.length() > 100 ? response.substring(0, 100) : response);
        
        JSONObject jsonObject = JSONObject.parseObject(response);
        
        if (jsonObject.containsKey("access_token")) {
            String token = jsonObject.getString("access_token");
            logger.info("成功获取access_token，长度: {}", token != null ? token.length() : 0);
            return token;
        } else {
            String errorMsg = "获取access_token失败: " + jsonObject.getString("errmsg");
            logger.error(errorMsg);
            throw new Exception(errorMsg);
        }
    }
    
    /**
     * 根据code获取用户信息
     * @param code 通过成员授权获取的code
     * @return 用户信息
     * @throws Exception 获取失败时抛出异常
     */
    public JSONObject getUserInfo(String code) throws Exception {
        String accessToken = getAccessToken();
        
        StringBuilder urlBuilder = new StringBuilder(USER_INFO_URL);
        urlBuilder.append("?access_token=").append(accessToken);
        urlBuilder.append("&code=").append(code);
        urlBuilder.append("&agentid=").append(agentId); // 添加agentid参数
        
        logger.info("准备获取用户信息，URL: {}", urlBuilder.toString());
        
        String response = HttpUtils.sendGet(urlBuilder.toString());
        logger.info("获取用户信息响应长度: {}", response != null ? response.length() : 0);
        
        if (response == null || response.isEmpty()) {
            logger.error("获取用户信息失败，响应为空");
            throw new Exception("获取用户信息失败，响应为空");
        }
        
        JSONObject jsonObject = JSONObject.parseObject(response);
        logger.info("获取企业微信用户信息结果: {}", jsonObject.toJSONString());
        return jsonObject;
    }
    
    /**
     * 获取企业ID
     * @return 企业ID
     */
    public String getCorpId() {
        return corpId;
    }
    
    /**
     * 获取应用ID
     * @return 应用ID
     */
    public String getAgentId() {
        return agentId;
    }
    
    /**
     * 获取应用密钥
     * @return 应用密钥
     */
    public String getSecret() {
        return secret;
    }
    
    /**
     * 获取回调地址
     * @return 回调地址
     */
    public String getRedirectUri() {
        return redirectUri;
    }
}