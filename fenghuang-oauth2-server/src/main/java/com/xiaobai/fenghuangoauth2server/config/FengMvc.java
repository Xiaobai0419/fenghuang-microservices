package com.xiaobai.fenghuangoauth2server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@EnableWebMvc //只要引入Web模块，Spring Boot会自动配置SpringMVC,不需要手动加注解
@Order(1)
@Configuration//SpringMVC配置必须加这个注解，否则扫描不到！
public class FengMvc  extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login").setViewName("login");
//        registry.addViewController("/oauth/confirm_access").setViewName("authorize");
    }
}
