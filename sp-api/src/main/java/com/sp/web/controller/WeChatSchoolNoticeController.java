package com.sp.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sp.common.core.controller.BaseController;
import com.sp.common.utils.WeChatWorkSchoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业微信学校通知控制器
 * 用于发送学校通知给家长和学生
 */
@RestController
@RequestMapping("/wechat/school/notice")
public class WeChatSchoolNoticeController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatSchoolNoticeController.class);
    
    @Autowired
    private WeChatWorkSchoolUtils weChatWorkSchoolUtils;
    
    /**
     * 发送学校通知
     * @return 发送结果
     */
    @PostMapping("/send")
    public Object sendSchoolNotice() {
        logger.info("开始发送学校通知");
        
        try {
            // 构造请求参数
            Map<String, Object> noticeRequest = new HashMap<>();
            noticeRequest.put("recv_scope", 0);
            
            List<String> toParentUserIds = new ArrayList<>();
            toParentUserIds.add("e4634d7bc64afa3e3462c6d6e4f6fd74");
            noticeRequest.put("to_parent_userid", toParentUserIds);
            
            noticeRequest.put("to_student_userid", new ArrayList<>());
            noticeRequest.put("to_party", new ArrayList<>());
            noticeRequest.put("toall", 0);
            noticeRequest.put("msgtype", "text");
            noticeRequest.put("agentid", 1000033);
            
            Map<String, String> textContent = new HashMap<>();
            textContent.put("content", "測試通知");
            noticeRequest.put("text", textContent);
            
            noticeRequest.put("enable_id_trans", 0);
            noticeRequest.put("enable_duplicate_check", 0);
            noticeRequest.put("duplicate_check_interval", 1800);
            
            logger.info("构造通知参数: {}", JSON.toJSONString(noticeRequest));
            
            // 获取access_token
            String accessToken = weChatWorkSchoolUtils.getAccessToken();
            logger.info("成功获取access_token");
            
            // 构造请求URL
            String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/message/send?access_token=" + accessToken;
            
            // 发送POST请求
            String response = com.sp.common.utils.http.HttpUtils.sendPost(url, JSON.toJSONString(noticeRequest));
            logger.info("发送学校通知完成，响应结果: {}", response);
            
            // 解析响应结果
            JSONObject result = JSONObject.parseObject(response);
            return result;
        } catch (Exception e) {
            logger.error("发送学校通知失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("errcode", -1);
            errorResult.put("errmsg", "发送学校通知失败: " + e.getMessage());
            return errorResult;
        }
    }
}