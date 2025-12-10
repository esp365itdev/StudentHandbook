package com.sp.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.sp.common.annotation.Anonymous;
import com.sp.common.core.controller.BaseController;
import com.sp.common.core.domain.AjaxResult;
import com.sp.common.utils.WeChatWorkUtils;
import com.sp.common.utils.WeChatWorkJSConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信JS-SDK配置控制器
 * 用于为企业微信JS-SDK提供config配置信息
 */
@RestController
@RequestMapping("/wechat/jsconfig")
public class WeChatWorkJSConfigController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatWorkJSConfigController.class);
    
    /**
     * 获取企业微信JS-SDK配置信息
     * 该接口用于前端页面调用企业微信JS-SDK时获取必要的配置参数
     * 
     * @param url 当前页面URL（不包含#及其后面部分）
     * @return JS-SDK配置信息
     */
    @Anonymous
    @GetMapping("/getConfig")
    public AjaxResult getConfig(@RequestParam String url) {
        logger.info("获取企业微信JS-SDK配置信息，URL: {}", url);
        
        try {
            // 获取JS-SDK配置信息
            Map<String, Object> config = WeChatWorkJSConfigUtils.generateConfig(url);
            
            logger.info("成功生成JS-SDK配置信息");
            return AjaxResult.success("获取配置成功", config);
        } catch (Exception e) {
            logger.error("获取企业微信JS-SDK配置信息失败", e);
            return AjaxResult.error("获取配置失败: " + e.getMessage());
        }
    }
}