package com.xiaobai.fenghuangapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@EnableOAuth2Sso
@SpringBootApplication
public class FenghuangApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FenghuangApiGatewayApplication.class, args);
	}
}//所有请求均访问网关，网关通过SSO客户端将请求先导向认证服务器，
//网关本身不要配置Spring Security认证体系（否则会直接导向网关本身的Spring Security），
//认证体系是通过@EnableOAuth2Sso注解和yml配置转向认证服务器的
//认证成功后的网关请求可访问网关路由资源

//经实验，只要引入了Spring Cloud OAuth2的两个相关依赖（表现为启动时后台打印一个随机生成的security password），无论是否有yml相关配置，网关请求会被导向
//网关本身的Spring Security认证体系（一个默认存在的登录页），因为没有配置任何用户名、密码信息，无论怎样都无法登陆成功，
//更无法跳转到任何其他服务
//如果带有@EnableOAuth2Sso注解不带yml配置，会导向一个不存在的login页（网关本身的Spring Security认证体系）

//如果拥有yml配置的认证服务器，不带@EnableOAuth2Sso注解，会导向一个正常登录页，
//使用"user"（用户名）/后台自动生成的那个密码可以登录成功并导向路由资源（猜是认证服务器认证体系，只不过用户名默认user,密码是随机生成的）

//再加上@EnableOAuth2Sso注解，输入认证服务器配置的用户名/密码才可登录成功并导向路由资源
//可见@EnableOAuth2Sso注解用于将请求导向认证服务器的认证体系，并使用其用户名/密码配置
