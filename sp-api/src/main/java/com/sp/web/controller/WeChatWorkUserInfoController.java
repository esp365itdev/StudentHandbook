package com.sp.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.sp.common.annotation.Anonymous;
import com.sp.common.core.controller.BaseController;
import com.sp.common.core.domain.AjaxResult;
import com.sp.common.utils.WeChatWorkOAuth2Utils;
import com.sp.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信用户信息控制器
 * 用于获取企业微信用户的详细信息
 */
@RestController
@RequestMapping("/wechat/user")
public class WeChatWorkUserInfoController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatWorkUserInfoController.class);
    
    @Autowired
    private WeChatWorkOAuth2Utils weChatWorkOAuth2Utils;
    
    /**
     * 根据code获取企业微信用户详细信息
     * 
     * @param code 企业微信授权code
     * @return 用户详细信息
     */
    @Anonymous
    @GetMapping("/getUserInfoByCode")
    public AjaxResult getUserInfoByCode(@RequestParam String code) {
        logger.info("根据code获取企业微信用户详细信息，code: {}", code);
        
        try {
            // 1. 获取access_token
            logger.info("开始获取access_token");
            String accessToken = weChatWorkOAuth2Utils.getAccessToken();
            logger.info("获取access_token成功: {}", accessToken != null ? "access_token已获取" : "null");
            
            // 添加检查
            if (accessToken == null || accessToken.isEmpty()) {
                logger.error("获取access_token失败，返回空值");
                return AjaxResult.error("获取access_token失败");
            }
            
            // 2. 根据code获取用户基本信息（包含user_ticket）
            String getUserInfoUrl = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=" + accessToken + "&code=" + code;
            logger.info("准备请求用户基本信息，URL: {}", getUserInfoUrl);
            
            String userResponse = HttpUtils.sendGet(getUserInfoUrl);
            logger.info("获取用户基本信息响应长度: {}", userResponse != null ? userResponse.length() : 0);
            
            if (userResponse == null || userResponse.isEmpty()) {
                logger.error("获取用户基本信息失败，响应为空");
                return AjaxResult.error("获取用户基本信息失败，响应为空");
            }
            
            JSONObject userResult = JSONObject.parseObject(userResponse);
            logger.info("解析用户基本信息结果: {}", userResult.toJSONString());
            
            if (!userResult.containsKey("errcode") || userResult.getIntValue("errcode") == 0) {
                // 如果包含user_ticket，继续获取详细信息
                if (userResult.containsKey("user_ticket")) {
                    String userTicket = userResult.getString("user_ticket");
                    logger.info("发现user_ticket，准备获取详细信息");
                    
                    // 3. 根据user_ticket获取用户详细信息（包括手机号等敏感信息）
                    String getDetailUrl = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserdetail?access_token=" + accessToken;
                    JSONObject postData = new JSONObject();
                    postData.put("user_ticket", userTicket);
                    
                    logger.info("准备请求用户详细信息，URL: {}", getDetailUrl);
                    String detailResponse = HttpUtils.sendPost(getDetailUrl, postData.toJSONString());
                    logger.info("获取用户详细信息响应长度: {}", detailResponse != null ? detailResponse.length() : 0);
                    
                    if (detailResponse == null || detailResponse.isEmpty()) {
                        logger.error("获取用户详细信息失败，响应为空");
                        return AjaxResult.error("获取用户详细信息失败，响应为空");
                    }
                    
                    JSONObject detailResult = JSONObject.parseObject(detailResponse);
                    logger.info("解析用户详细信息结果: {}", detailResult.toJSONString());
                    
                    if (!detailResult.containsKey("errcode") || detailResult.getIntValue("errcode") == 0) {
                        logger.info("成功获取用户详细信息");
                        return AjaxResult.success("获取用户详细信息成功", detailResult);
                    } else {
                        String errorMsg = "获取用户详细信息失败: " + detailResult.getString("errmsg");
                        logger.error(errorMsg);
                        return AjaxResult.error(errorMsg);
                    }
                } else {
                    // 如果没有user_ticket，只返回基本信息
                    // 检查是否有UserId，如果有则获取用户详情
                    if (userResult.containsKey("UserId")) {
                        String userId = userResult.getString("UserId");
                        logger.info("发现UserId: {}，准备获取用户详情", userId);
                        
                        // 获取用户详情
                        String getUserDetailUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken + "&userid=" + userId;
                        logger.info("准备请求用户详情，URL: {}", getUserDetailUrl);
                        
                        String detailResponse = HttpUtils.sendGet(getUserDetailUrl);
                        logger.info("获取用户详情响应长度: {}", detailResponse != null ? detailResponse.length() : 0);
                        
                        if (detailResponse == null || detailResponse.isEmpty()) {
                            logger.warn("获取用户详情失败，响应为空，返回基本信息");
                            return AjaxResult.success("获取用户基本信息成功", userResult);
                        }
                        
                        JSONObject detailResult = JSONObject.parseObject(detailResponse);
                        logger.info("解析用户详情结果: {}", detailResult.toJSONString());
                        
                        if (!detailResult.containsKey("errcode") || detailResult.getIntValue("errcode") == 0) {
                            logger.info("成功获取用户详情");
                            return AjaxResult.success("获取用户详情成功", detailResult);
                        } else {
                            // 如果获取详情失败，返回基本信息
                            logger.warn("获取用户详情失败: {}，返回基本信息", detailResult.getString("errmsg"));
                            return AjaxResult.success("获取用户基本信息成功", userResult);
                        }
                    } else {
                        // 如果没有UserId，只返回基本信息
                        logger.info("未发现UserId，返回基本信息");
                        return AjaxResult.success("获取用户基本信息成功", userResult);
                    }
                }
            } else {
                String errorMsg = "获取用户基本信息失败: " + userResult.getString("errmsg");
                logger.error(errorMsg);
                return AjaxResult.error(errorMsg);
            }
        } catch (Exception e) {
            logger.error("获取企业微信用户信息失败", e);
            return AjaxResult.error("获取用户信息失败: " + e.getMessage());
        }
    }
}