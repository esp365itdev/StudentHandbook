package com.sp.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.sp.common.annotation.Anonymous;
import com.sp.common.core.controller.BaseController;
import com.sp.common.core.domain.AjaxResult;
import com.sp.common.utils.WeChatWorkOAuth2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/**
 * 企业微信OAuth2.0网页授权控制器
 * 用于处理企业微信应用的网页授权回调功能
 */
@RestController
@RequestMapping("/wechat/oauth")
public class WeChatWorkOAuthController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatWorkOAuthController.class);
    
    @Autowired
    private WeChatWorkOAuth2Utils weChatWorkOAuth2Utils;
    
    /**
     * 发起企业微信授权请求
     * @param redirect 授权后重定向地址
     * @param session HttpSession
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    @Anonymous
    @GetMapping("/authorize")
    public void authorize(
            @RequestParam(required = false) String redirect,
            HttpSession session,
            HttpServletResponse response) throws IOException {
        
        logger.info("发起企业微信授权请求，重定向地址: {}", redirect);
        
        // 生成state参数，用于防止CSRF攻击
        String state = UUID.randomUUID().toString();
        
        // 将重定向地址和state存入session
        session.setAttribute("wechat_oauth_redirect", redirect);
        session.setAttribute("wechat_oauth_state", state);
        
        // 构造授权链接并重定向
        String authorizeUrl = weChatWorkOAuth2Utils.getAuthorizeUrl(state);
        logger.info("重定向到企业微信授权页面: {}", authorizeUrl);
        response.sendRedirect(authorizeUrl);
    }
    
    /**
     * 企业微信授权回调处理
     * 此接口专门用于处理企业微信应用的网页授权回调
     * @param code 企业微信授权code
     * @param state 状态码
     * @param session HttpSession
     * @return 授权结果
     */
    @Anonymous
    @GetMapping("/callback")
    public AjaxResult callback(
            @RequestParam String code,
            @RequestParam String state,
            HttpSession session) throws IOException {
        
        logger.info("接收到企业微信授权回调，code: {}, state: {}", code, state);
        
        try {
            // 验证state参数，防止CSRF攻击
            String savedState = (String) session.getAttribute("wechat_oauth_state");
            
            if (savedState == null || !savedState.equals(state)) {
                logger.warn("state参数验证失败，可能遭遇CSRF攻击");
                return AjaxResult.error("授权验证失败");
            }
            
            // 根据code获取用户信息
            JSONObject userInfo = weChatWorkOAuth2Utils.getUserInfo(code);
            
            if (userInfo.containsKey("UserId")) {
                // 获取用户信息成功
                String userId = userInfo.getString("UserId");
                logger.info("企业微信授权成功，用户ID: {}", userId);
                
                // 可以在这里处理用户登录逻辑
                // 例如：创建或更新本地用户信息，设置登录状态等
                
                // 清除临时session数据
                session.removeAttribute("wechat_oauth_state");
                
                // 重定向到首页并携带用户信息
                JSONObject result = new JSONObject();
                result.put("userId", userId);
                result.put("userInfo", userInfo);
                
                // 由于是API接口，返回JSON结果而不是重定向
                return AjaxResult.success("授权成功", result);
            } else {
                logger.error("获取企业微信用户信息失败: {}", userInfo.getString("errmsg"));
                return AjaxResult.error("获取用户信息失败: " + userInfo.getString("errmsg"));
            }
        } catch (Exception e) {
            logger.error("处理企业微信授权回调时发生错误", e);
            return AjaxResult.error("处理授权回调时发生错误: " + e.getMessage());
        }
    }
}