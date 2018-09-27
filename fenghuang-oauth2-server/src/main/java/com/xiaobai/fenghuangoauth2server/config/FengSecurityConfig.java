package com.xiaobai.fenghuangoauth2server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Order(2)//默认值是一个很大的整数，代表最后加载，这里需要先加载Security相关，然后将创建的相关Bean体系注入认证服务器(认证服务器使用默认值，代表最后加载！！)
@Configuration
public class FengSecurityConfig extends WebSecurityConfigurerAdapter {
    /**需要注明哪个实现，不然报以下异常（多于一个实现，默认有一个基于内存的实现）：
     Could not autowire. There is more than one bean of 'UserDetailsService' type.
     Beans:
     fengSecurityService   (FengSecurityService.java) inMemoryUserDetailsManager   (UserDetailsServiceAutoConfiguration.class)
     */
    @Autowired
    @Qualifier("fengSecurityService")//选择一个实现（按名称注入）
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {//当前Spring Security加密实现原理：根据设置的PasswordEncoder的matches方法比对用户输入密码和数据库加密密码，旧版本不设置，验证时就会报下面错误
        //一定要在这里设置userDetailsService，否则userDetailsService无法在Spring Security中起作用！！
        auth.userDetailsService(userDetailsService).passwordEncoder(NoOpPasswordEncoder.getInstance());//高版本Spring兼容低版本Spring Security,无加密需要加入一个NoOpPasswordEncoder，否则报错：There is no PasswordEncoder mapped for the id "null"
        auth.parentAuthenticationManager(authenticationManagerBean());//原则上不用配，本来就是用父类AuthenticationManager
    }

    @Override
    @Bean//获取这个父类方法返回的Bean，用于向认证服务器注入！！
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().antMatchers("/health", "/css/**").anonymous()
//                .and().authorizeRequests().anyRequest().authenticated()//任何路径都需要验证
//                .and().formLogin().loginPage("/login").permitAll();//permitAll:登录页面需要放行，否则其无法访问！！

        http.formLogin().loginPage("/authentication/require")
                .loginProcessingUrl("/authentication/form")
                .and().authorizeRequests()
                .antMatchers("/authentication/require",
                        "/authentication/form",
                        "/**/*.js",
                        "/**/*.css",
                        "/**/*.jpg",
                        "/**/*.png",
                        "/**/*.woff2"
                )
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }
}
