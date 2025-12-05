package com.sp.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.sp.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 家校通讯录工具类
 * 用于获取学生或家长的身份信息
 */
public class SchoolContactUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(SchoolContactUtils.class);
    
    /**
     * 企业微信API地址
     */
    private static final String GET_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/school/user/get";
    
    /**
     * 根据用户ID获取学生或家长信息
     * @param accessToken 企业微信应用的access_token
     * @param userId 用户ID（学生或家长的userid）
     * @return 用户信息对象
     */
    public static JSONObject getUserInfo(String accessToken, String userId) {
        try {
            StringBuilder urlBuilder = new StringBuilder(GET_USER_INFO_URL);
            urlBuilder.append("?access_token=").append(accessToken);
            urlBuilder.append("&userid=").append(userId);
            
            String response = HttpUtils.sendGet(urlBuilder.toString());
            logger.info("获取家校通讯录用户信息结果: {}", response);
            
            JSONObject result = JSONObject.parseObject(response);
            logger.info("解析后的JSON对象: {}", result.toJSONString());
            
            return result;
        } catch (Exception e) {
            logger.error("获取家校通讯录用户信息异常", e);
            return null;
        }
    }
    
    /**
     * 判断用户是否为学生
     * @param userInfo 用户信息对象
     * @return true表示是学生，false表示不是学生（可能是家长或其他）
     */
    public static boolean isStudent(JSONObject userInfo) {
        if (userInfo == null || !userInfo.containsKey("student")) {
            return false;
        }
        return true;
    }
    
    /**
     * 判断用户是否为家长
     * @param userInfo 用户信息对象
     * @return true表示是家长，false表示不是家长（可能是学生或其他）
     */
    public static boolean isParent(JSONObject userInfo) {
        if (userInfo == null || !userInfo.containsKey("parent")) {
            return false;
        }
        return true;
    }
    
    /**
     * 获取学生信息
     * @param userInfo 用户信息对象
     * @return 学生信息对象
     */
    public static JSONObject getStudentInfo(JSONObject userInfo) {
        if (userInfo != null && userInfo.containsKey("student")) {
            return userInfo.getJSONObject("student");
        }
        return null;
    }
    
    /**
     * 获取家长信息
     * @param userInfo 用户信息对象
     * @return 家长信息对象
     */
    public static JSONObject getParentInfo(JSONObject userInfo) {
        if (userInfo != null && userInfo.containsKey("parent")) {
            return userInfo.getJSONObject("parent");
        }
        return null;
    }
}