package com.sp.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.sp.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 企业微信家校应用工具类
 * 用于获取家校场景下的用户身份信息
 */
@Component
public class WeChatWorkSchoolUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatWorkSchoolUtils.class);
    
    @Value("${wechat.work.corpId}")
    private String corpId;
    
    @Value("${wechat.work.secret}")
    private String secret;
    
    // 家校获取用户信息接口
    private static final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    private static final String SCHOOL_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/school/getuserinfo";
    private static final String SCHOOL_USER_DETAIL_URL = "https://qyapi.weixin.qq.com/cgi-bin/school/user/get";
    
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
     * 根据code获取家校用户信息（家长或学生）
     * @param code 通过成员授权获取的code
     * @return 用户信息
     * @throws Exception 获取失败时抛出异常
     */
    public JSONObject getSchoolUserInfo(String code) throws Exception {
        String accessToken = getAccessToken();
        
        StringBuilder urlBuilder = new StringBuilder(SCHOOL_USER_INFO_URL);
        urlBuilder.append("?access_token=").append(accessToken);
        urlBuilder.append("&code=").append(code);
        
        logger.info("准备获取家校用户信息，URL: {}", urlBuilder.toString());
        
        String response = HttpUtils.sendGet(urlBuilder.toString());
        logger.info("获取家校用户信息响应长度: {}", response != null ? response.length() : 0);
        
        if (response == null || response.isEmpty()) {
            logger.error("获取家校用户信息失败，响应为空");
            throw new Exception("获取家校用户信息失败，响应为空");
        }
        
        JSONObject jsonObject = JSONObject.parseObject(response);
        logger.info("获取家校用户信息结果: {}", jsonObject.toJSONString());
        return jsonObject;
    }
    
    /**
     * 根据userid获取家校用户详细信息（家长或学生）
     * @param userid 家校通讯录的userid
     * @return 用户详细信息
     * @throws Exception 获取失败时抛出异常
     */
    public JSONObject getSchoolUserDetail(String userid) throws Exception {
        String accessToken = getAccessToken();
        
        StringBuilder urlBuilder = new StringBuilder(SCHOOL_USER_DETAIL_URL);
        urlBuilder.append("?access_token=").append(accessToken);
        urlBuilder.append("&userid=").append(userid);
        
        logger.info("准备获取家校用户详细信息，URL: {}", urlBuilder.toString());
        
        String response = HttpUtils.sendGet(urlBuilder.toString());
        logger.info("获取家校用户详细信息响应长度: {}", response != null ? response.length() : 0);
        
        if (response == null || response.isEmpty()) {
            logger.error("获取家校用户详细信息失败，响应为空");
            throw new Exception("获取家校用户详细信息失败，响应为空");
        }
        
        JSONObject jsonObject = JSONObject.parseObject(response);
        logger.info("获取家校用户详细信息结果: {}", jsonObject.toJSONString());
        return jsonObject;
    }
}