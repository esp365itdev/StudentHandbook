package com.sp.web.controller;

import com.sp.common.core.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 * 用于负载均衡器的健康检查
 */
@RestController
public class HealthCheckController extends BaseController {
    
    /**
     * 根路径健康检查接口
     * @return 成功消息
     */
    @GetMapping("/")
    public String healthCheck() {
        return "OK";
    }
}