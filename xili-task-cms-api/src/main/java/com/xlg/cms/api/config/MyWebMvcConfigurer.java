package com.xlg.cms.api.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.xlg.cms.api.interceptor.AdminHandlerInterceptor;

@Configuration
//废弃：public class MyWebMvcConfigurer extends WebMvcConfigurerAdapter{
public class MyWebMvcConfigurer implements WebMvcConfigurer {


    @Resource
    AdminHandlerInterceptor adminHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器 拦截规则
        //多个拦截器时 以此添加 执行顺序按添加顺序
        registry.addInterceptor(adminHandlerInterceptor).addPathPatterns("/**")
                .excludePathPatterns("", "/", "/login", "/login.html", "/logout", "/error", "/error/", "/captcha",
                        "/js/**", "/html/**", "/image/**", "/css/**", "/favicon.ico", "/libs/*", "/layer/*", "*.js",
                        "/iview/*", "/AdminLTE/*", "/font-awesome/*", "/bootstrap/*", "*.css");

    }

}