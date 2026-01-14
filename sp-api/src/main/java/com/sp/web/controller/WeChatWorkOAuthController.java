package com.sp.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.sp.common.annotation.Anonymous;
import com.sp.common.core.controller.BaseController;
import com.sp.common.utils.WeChatWorkSchoolUtils;
import com.sp.common.utils.WeChatWorkOAuth2Utils;
import com.alibaba.fastjson.JSONObject;

import com.sp.system.service.DepartmentService;
import com.sp.system.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 企业微信授权回调
 */
@RestController
@RequestMapping("/wechat/oauth")
public class WeChatWorkOAuthController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatWorkOAuthController.class);
    
    @Autowired
    private WeChatWorkOAuth2Utils weChatWorkOAuth2Utils;
    
    @Autowired
    private WeChatWorkSchoolUtils weChatWorkSchoolUtils;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private TokenService tokenService;
    
    @Value("${sp.token.expireTime:7}")
    private int expireTimeInDays;
    
    /**
     * 企业微信授权回调处理
     * 此接口专门用于处理企业微信应用的网页授权回调
     * @param code 企业微信授权code
     * @param state 状态码
     * @param session HttpSession
     * @param response HttpServletResponse
     */
    @Anonymous
    @GetMapping("/callback")
    public void callback(
            @RequestParam String code,
            @RequestParam String state,
            HttpSession session,
            HttpServletResponse response) {
        
        logger.info("接收到企业微信授权回调，code: {}, state: {}", code, state);
        
        try {
            // 对于微信用户测试功能，我们允许特定的state值
            boolean isWechatTest = "wechat_test".equals(state);
            boolean isDefault = "default".equals(state);
            
            if (!isWechatTest && !isDefault) {
                // 验证state参数，防止CSRF攻击（除了微信测试情况和默认情况）
                String savedState = (String) session.getAttribute("wechat_oauth_state");
                
                if (savedState == null || !savedState.equals(state)) {
                    logger.warn("state参数验证失败，可能遭遇CSRF攻击");
                    // 返回错误页面
                    response.sendRedirect("/sp-api/login?error=invalid_state");
                    return;
                }
            }
            
            // 根据code获取家校用户信息（家长或学生）
            JSONObject userInfo = weChatWorkOAuth2Utils.getSchoolUserInfo(code);

            logger.info("企业微信家校授权成功，用户userInfo: {}", userInfo);
            
            // 检查是否包含家长(parent_userid)或学生(student_userid)信息
            if (userInfo.containsKey("parent_userid") || userInfo.containsKey("student_userid")) {
                // 获取用户信息成功
                String userId = userInfo.containsKey("parent_userid") ? 
                    userInfo.getString("parent_userid") : userInfo.getString("student_userid");
                logger.info("企业微信家校授权成功，用户ID: {}", userId);
                
                // 使用获取到的用户ID，调用接口获取详细的家校用户信息
                JSONObject schoolUserInfo = weChatWorkSchoolUtils.getSchoolUserDetail(userId);
                
                // 检查获取详细信息是否成功
                if (!schoolUserInfo.containsKey("errcode") || schoolUserInfo.getIntValue("errcode") == 0) {
                    logger.info("成功获取家校用户详细信息: {}", schoolUserInfo);
                    
                    /* // 检查详细信息中是否包含学生信息（在parent.children数组中）
                    if (schoolUserInfo.containsKey("parent") && schoolUserInfo.getJSONObject("parent").containsKey("children")) {
                        JSONArray childrenArray = schoolUserInfo.getJSONObject("parent").getJSONArray("children");
                        
                        if (childrenArray != null && !childrenArray.isEmpty()) {
                            for (int i = 0; i < childrenArray.size(); i++) {
                                JSONObject childObj = childrenArray.getJSONObject(i);
                                if (childObj.containsKey("student_userid")) {
                                    String studentUserid = childObj.getString("student_userid");
                                    logger.info("从家长信息中获取到第{}个学生的student_userid: {}", i+1, studentUserid);
                                    
                                    // 使用student_userid再次调用SCHOOL_USER_DETAIL_URL获取学生信息
                                    JSONObject studentInfo = weChatWorkSchoolUtils.getSchoolUserDetail(studentUserid);
                                    logger.info("使用student_userid获取学生信息结果: {}", studentInfo.toJSONString());
                                }
                            }
                        }
                    } */
                    
                    // 清除临时session数据（如果不是测试情况和默认情况）
                    if (!isWechatTest && !isDefault) {
                        session.removeAttribute("wechat_oauth_state");
                    }
                    
                    // 生成token（使用用户ID的哈希值作为用户ID，因为原ID可能是字符串）
                    // 这里我们使用一个简单的哈希算法来生成数字ID
                    long numericUserId = userId.hashCode();
                    if (numericUserId < 0) {
                        numericUserId = Math.abs(numericUserId);
                    }
                    if (numericUserId == 0) {
                        numericUserId = System.currentTimeMillis();
                    }
                    
                    String token = tokenService.createToken(numericUserId);
                    
                    logger.info("为用户 {} 生成token: {}", numericUserId, token);
                    
                    // 构建重定向URL，将token作为参数传递给前端
                    String redirectUrl = "/sp-api/?token=" + token;
                    
                    // 如果是测试状态，重定向到登录页面，否则重定向到首页
                    if (isWechatTest || isDefault) {
                        redirectUrl = "/sp-api/?token=" + token;
                    } else {
                        redirectUrl = "/sp-api/?token=" + token;
                    }
                    
                    // 重定向到前端页面
                    response.sendRedirect(redirectUrl);
                    return;
                } else {
                    logger.error("获取家校用户详细信息失败: {}", schoolUserInfo.getString("errmsg"));
                    // 重定向到错误页面
                    response.sendRedirect("/sp-api/login?error=user_detail_failed&message=" + 
                        java.net.URLEncoder.encode(schoolUserInfo.getString("errmsg"), "UTF-8"));
                    return;
                }
            } else {
                logger.error("获取企业微信家校用户信息失败: {}", userInfo.getString("errmsg"));
                // 重定向到错误页面
                response.sendRedirect("/sp-api/login?error=user_info_failed&message=" + 
                    java.net.URLEncoder.encode(userInfo.getString("errmsg"), "UTF-8"));
                return;
            }
        } catch (Exception e) {
            logger.error("处理企业微信家校授权回调时发生错误", e);
            try {
                // 重定向到错误页面
                response.sendRedirect("/sp-api/login?error=internal_error&message=" + 
                    java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            } catch (IOException ioException) {
                logger.error("重定向失败", ioException);
            }
        }
    }
}