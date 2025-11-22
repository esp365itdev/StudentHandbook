package com.sp.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.alibaba.fastjson.JSONObject;
import com.sp.common.core.text.Convert;

/**
 * 日志工具类
 *
 */
public class LogUtils {
    /**
     * 操作日志记录
     */
    public static final String OPER_LOG = "oper_log";
    
    /**
     * 异常日志记录
     */
    public static final String EXCEPTION_LOG = "exception_log";
    
    /**
     * 性能日志记录
     */
    public static final String PERFORMANCE_LOG = "performance_log";
    
    /**
     * 业务日志记录
     */
    public static final String BUSINESS_LOG = "business_log";
    
    /**
     * 安全日志记录
     */
    public static final String SECURITY_LOG = "security_log";

    /**
     * 获取操作日志记录器
     *
     * @return Logger
     */
    public static Logger getOperLog() {
        return LoggerFactory.getLogger(OPER_LOG);
    }

    /**
     * 获取异常日志记录器
     *
     * @return Logger
     */
    public static Logger getExceptionLog() {
        return LoggerFactory.getLogger(EXCEPTION_LOG);
    }

    /**
     * 获取性能日志记录器
     *
     * @return Logger
     */
    public static Logger getPerformanceLog() {
        return LoggerFactory.getLogger(PERFORMANCE_LOG);
    }

    /**
     * 获取业务日志记录器
     *
     * @return Logger
     */
    public static Logger getBusinessLog() {
        return LoggerFactory.getLogger(BUSINESS_LOG);
    }

    /**
     * 获取安全日志记录器
     *
     * @return Logger
     */
    public static Logger getSecurityLog() {
        return LoggerFactory.getLogger(SECURITY_LOG);
    }

    /**
     * 记录操作日志
     *
     * @param title 标题
     * @param operObj 操作对象
     * @param operResult 操作结果
     */
    public static void operLog(String title, Object operObj, Object operResult) {
        MDC.put("title", title);
        if (operObj != null) {
            MDC.put("operObj", operObj.toString());
        }
        if (operResult != null) {
            MDC.put("operResult", operResult.toString());
        }
        getOperLog().info("操作日志: {}", title);
        clearMDC();
    }

    /**
     * 记录业务日志
     *
     * @param module 模块
     * @param businessType 业务类型
     * @param content 内容
     */
    public static void businessLog(String module, String businessType, String content) {
        MDC.put("module", module);
        MDC.put("businessType", businessType);
        MDC.put("content", content);
        getBusinessLog().info("业务日志: 模块={}, 类型={}, 内容={}", module, businessType, content);
        clearMDC();
    }

    /**
     * 记录安全日志
     *
     * @param action 操作行为
     * @param user 用户
     * @param ip IP地址
     * @param status 状态
     */
    public static void securityLog(String action, String user, String ip, String status) {
        MDC.put("action", action);
        MDC.put("user", user);
        MDC.put("ip", ip);
        MDC.put("status", status);
        getSecurityLog().info("安全日志: 操作={}, 用户={}, IP={}, 状态={}", action, user, ip, status);
        clearMDC();
    }

    /**
     * 记录性能日志
     *
     * @param method 方法名
     * @param time 耗时(ms)
     * @param params 参数
     */
    public static void performanceLog(String method, long time, Object... params) {
        MDC.put("method", method);
        MDC.put("time", String.valueOf(time));
        if (params != null && params.length > 0) {
            MDC.put("params", Arrays.toString(params));
        }
        getPerformanceLog().info("性能日志: 方法={}, 耗时={}ms", method, time);
        clearMDC();
    }

    /**
     * 记录异常日志
     *
     * @param ex 异常
     * @param msg 消息
     */
    public static void exceptionLog(Exception ex, String msg) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw, true));
        String exceptionAsString = sw.toString();
        
        MDC.put("exception", ex.getClass().getSimpleName());
        MDC.put("message", msg);
        MDC.put("stackTrace", exceptionAsString);
        getExceptionLog().error("异常日志: 类型={}, 消息={}, 堆栈={}", ex.getClass().getSimpleName(), msg, exceptionAsString);
        clearMDC();
    }

    /**
     * 清除MDC上下文
     */
    private static void clearMDC() {
        MDC.clear();
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return JSONObject.toJSONString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    /**
     * 获取请求参数
     *
     * @param request HttpServletRequest
     * @return 参数字符串
     */
    public static String getParameterString(HttpServletRequest request) {
        try {
            Map<String, String[]> paramsMap = request.getParameterMap();
            return JSONObject.toJSONString(paramsMap);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest
     * @return IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}