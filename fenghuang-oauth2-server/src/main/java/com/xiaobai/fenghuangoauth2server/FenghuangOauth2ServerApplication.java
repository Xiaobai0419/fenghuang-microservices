package com.xiaobai.fenghuangoauth2server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableDiscoveryClient
@SpringBootApplication
public class FenghuangOauth2ServerApplication {//只要引入了Eureka Client的pom依赖，无论这里是否开启@EnableDiscoveryClient，yml是否配置，启动时都会报找不到注册服务器的错误，因为Spring Boot会根据依赖自动配置加载相应模块

	public static void main(String[] args) {
		SpringApplication.run(FenghuangOauth2ServerApplication.class, args);
	}
}
