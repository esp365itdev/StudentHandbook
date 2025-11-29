package com.sp.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射静态资源路径
        registry.addResourceHandler("/dist/**")
                .addResourceLocations("classpath:/dist/");
        
        // 其他静态资源配置
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
                
        // 添加对sp-api路径下dist目录的支持
        registry.addResourceHandler("/sp-api/dist/**")
                .addResourceLocations("classpath:/dist/");
                
        // 添加对sp-api路径下assets目录的支持
        registry.addResourceHandler("/sp-api/assets/**")
                .addResourceLocations("classpath:/dist/assets/");
                
        // 添加对sp-api根路径的支持
        registry.addResourceHandler("/sp-api/**")
                .addResourceLocations("classpath:/dist/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 将根路径映射到index.html
        registry.addViewController("/").setViewName("forward:/dist/index.html");
        registry.addViewController("/index").setViewName("forward:/dist/index.html");
        registry.addViewController("/index.html").setViewName("forward:/dist/index.html");
        // 添加对sp-api路径下index.html的支持
        registry.addViewController("/sp-api/").setViewName("forward:/dist/index.html");
        registry.addViewController("/sp-api").setViewName("forward:/dist/index.html");
    }
    
    /*
    @Bean
    public ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistration() {
        ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(
                new CustomDispatcherServlet(), "/sp-api/*");
        registration.setName("customDispatcherServlet");
        registration.setLoadOnStartup(1);
        return registration;
    }
    */
}