package com.sp.framework.interceptor;

import com.sp.common.annotation.Anonymous;
import com.sp.common.utils.StringUtils;
import com.sp.system.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Token验证拦截器
 *
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Value("${sp.token.enabled}")
    private boolean tokenEnabled;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 明确放行静态资源路径
        if (requestURI.startsWith("/sp-api/assets/") || 
            requestURI.startsWith("/sp-api/dist/") ||
            requestURI.startsWith("/assets/") ||
            requestURI.startsWith("/dist/")) {
            return true;
        }

        // 如果是OPTIONS请求，直接通过
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        
        // 如果token验证未启用，直接通过
        if (!tokenEnabled) {
            return true;
        }

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查是否有Anonymous注解，有则跳过token验证
        if (method.getDeclaringClass().isAnnotationPresent(Anonymous.class) ||
                method.isAnnotationPresent(Anonymous.class)) {
            return true;
        }

        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        
        // 处理Bearer token格式
        if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "前缀
        }

        // 验证token
        if (StringUtils.isEmpty(token) || !tokenService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"code\":403,\"msg\":\"无效的token\"}");
            return false;
        }

        return true;
    }
}