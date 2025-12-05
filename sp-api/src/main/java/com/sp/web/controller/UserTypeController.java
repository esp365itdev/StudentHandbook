package com.sp.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.sp.common.annotation.Anonymous;
import com.sp.common.core.controller.BaseController;
import com.sp.common.core.domain.AjaxResult;
import com.sp.common.utils.SchoolContactUtils;
import com.sp.common.utils.WeChatWorkOAuth2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户类型控制器
 * 用于判断当前用户是学生还是家长
 */
@RestController
@RequestMapping("/user/type")
public class UserTypeController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserTypeController.class);
    
    @Autowired
    private WeChatWorkOAuth2Utils weChatWorkOAuth2Utils;
    
    /**
     * 检查用户类型（学生或家长）
     * @param code 企业微信授权code
     * @return 用户类型信息
     */
    @Anonymous
    @GetMapping("/check")
    public AjaxResult checkUserType(@RequestParam String code) {
        try {
            logger.info("开始检查用户类型，code: {}", code);
            
            // 根据code获取用户信息
            JSONObject userInfo = weChatWorkOAuth2Utils.getUserInfo(code);
            logger.info("获取到的用户信息: {}", userInfo.toJSONString());
            
            if (!userInfo.containsKey("UserId")) {
                logger.error("获取企业微信用户信息失败: {}", userInfo.getString("errmsg"));
                return AjaxResult.error("获取用户信息失败: " + userInfo.getString("errmsg"));
            }
            
            // 获取用户ID
            String userId = userInfo.getString("UserId");
            logger.info("企业微信用户ID: {}", userId);
            
            // 获取access_token
            String accessToken = weChatWorkOAuth2Utils.getAccessToken();
            logger.info("获取到的access_token: {}", accessToken);
            
            if (accessToken == null) {
                logger.error("获取access_token失败");
                return AjaxResult.error("获取access_token失败");
            }
            
            // 获取学生或家长详细信息
            JSONObject contactUserInfo = SchoolContactUtils.getUserInfo(accessToken, userId);
            logger.info("获取到的家校通讯录用户信息: {}", contactUserInfo != null ? contactUserInfo.toJSONString() : "null");
            
            if (contactUserInfo == null) {
                logger.error("获取家校通讯录用户信息失败");
                return AjaxResult.error("获取家校通讯录用户信息失败");
            }
            
            // 检查是否有错误信息
            if (contactUserInfo.containsKey("errcode") && !"0".equals(contactUserInfo.getString("errcode"))) {
                logger.error("获取家校通讯录用户信息失败: {}", contactUserInfo.getString("errmsg"));
                return AjaxResult.error("获取家校通讯录用户信息失败: " + contactUserInfo.getString("errmsg"));
            }
            
            // 判断用户类型
            JSONObject result = new JSONObject();
            result.put("userId", userId);
            
            if (SchoolContactUtils.isStudent(contactUserInfo)) {
                result.put("userType", "student");
                JSONObject studentInfo = SchoolContactUtils.getStudentInfo(contactUserInfo);
                result.put("studentInfo", studentInfo);
                logger.info("用户 {} 是学生, 姓名: {}", userId, studentInfo != null ? studentInfo.getString("name") : "未知");
            } else if (SchoolContactUtils.isParent(contactUserInfo)) {
                result.put("userType", "parent");
                JSONObject parentInfo = SchoolContactUtils.getParentInfo(contactUserInfo);
                result.put("parentInfo", parentInfo);
                logger.info("用户 {} 是家长, 姓名: {}", userId, parentInfo != null ? parentInfo.getString("name") : "未知");
            } else {
                result.put("userType", "unknown");
                logger.warn("无法确定用户 {} 的类型", userId);
            }
            
            logger.info("检查用户类型完成，结果: {}", result.toJSONString());
            return AjaxResult.success("获取用户类型成功", result);
        } catch (Exception e) {
            logger.error("检查用户类型时发生错误", e);
            return AjaxResult.error("检查用户类型时发生错误: " + e.getMessage());
        }
    }
}