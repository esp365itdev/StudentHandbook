package com.sp.web.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sp.common.utils.WeChatWorkSchoolUtils;
import com.sp.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学校通知定时发送任务
 * 每天下午6点定时发送学校通知给家长和学生
 */
@Component
public class SchoolNoticeTask {

    private static final Logger logger = LoggerFactory.getLogger(SchoolNoticeTask.class);

    @Autowired
    private WeChatWorkSchoolUtils weChatWorkSchoolUtils;

    /**
     * 每天下午6点执行学校通知发送任务（北京时间）
     */
    @Scheduled(cron = "0 0 18 * * ?", zone = "Asia/Shanghai")
    public void sendSchoolNotice() {
        logger.info("开始执行定时学校通知发送任务");

        try {
            // 构造请求参数
            Map<String, Object> noticeRequest = new HashMap<>();
            noticeRequest.put("recv_scope", 0);

            noticeRequest.put("to_parent_userid", new ArrayList<>());
            noticeRequest.put("to_student_userid", new ArrayList<>());

            List<String> toPartyIds = new ArrayList<>();
            toPartyIds.add("16593");
            noticeRequest.put("to_party", toPartyIds);
            noticeRequest.put("toall", 0);
            noticeRequest.put("msgtype", "text");
            noticeRequest.put("agentid", 1000033);

            Map<String, String> textContent = new HashMap<>();
            textContent.put("content", "請點擊連接查看今日學生手冊：https://mo-stu-sys.org-assistant.com/sp-api/");
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
            String response = HttpUtils.sendPost(url, JSON.toJSONString(noticeRequest));
            logger.info("定时发送学校通知完成，响应结果: {}", response);

            // 解析响应结果
            JSONObject result = JSONObject.parseObject(response);
            logger.info("学校通知发送结果: errcode={}, errmsg={}", result.getInteger("errcode"), result.getString("errmsg"));
        } catch (Exception e) {
            logger.error("定时发送学校通知失败", e);
        }

        logger.info("定时学校通知发送任务执行完成");
    }
}