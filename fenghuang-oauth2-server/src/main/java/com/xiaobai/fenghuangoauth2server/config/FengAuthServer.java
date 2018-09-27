package com.xiaobai.fenghuangoauth2server.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Slf4j
@Order(3)//这注解可以不加，其他配置类上的也是
@Configuration
@EnableAuthorizationServer
public class FengAuthServer extends AuthorizationServerConfigurerAdapter {

//    @Autowired
//    private AuthenticationManager authenticationManager;
//    @Autowired
//    @Qualifier("fengSecurityService")//选择一个实现（按名称注入）
//    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        //这里不设置PasswordEncoder（这里是密码无加密情况下会报的异常，如果设置了其他passwordEncoder如BCrypt则不需要在这里再设置passwordEncoder）仍然会报There is no PasswordEncoder mapped for the id "null"，
        // 且资源服务器启动时报无法创建jwtTokenEnhancer异常，因为其使用其配置的client-id: client client-secret: secret登录认证服务器获取jwt相关，
        // 登录认证服务器，实际也是走Spring Security（默认是外面Web环境配置的Spring Security，但也可配置其他userDetailsService），只不过认证服务器是通过下面的ClientDetailsServiceConfigurer配置用户名、密码信息的，而不是通过普通Spring Security体系配置）
        oauthServer.passwordEncoder(NoOpPasswordEncoder.getInstance()).tokenKeyAccess("isAuthenticated()").checkTokenAccess("isAuthenticated()");//开启/oauth/token_key（解析jwt令牌所需要密钥的地址）认证访问，开启/oauth/check-token（token-info-uri，发现只有资源服务器配置了这个信息，用于获取其他服务器访问资源服务器，先被转向认证服务器，成功登录后存储的token信息）认证访问，参数里面是SPEL表达式
//        oauthServer.passwordEncoder(NoOpPasswordEncoder.getInstance()).tokenKeyAccess("isAuthenticated()");//上面一行可以替换成这行！！
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {//配置认证服务器可认证成功的client_id、secret、授权模式等匹配信息（可配置多个，与用户传递过来的client_id、secret等对比）
        clients.inMemory() // 使用in-memory存储
                .withClient("client") // client_id
                .secret("secret") // client_secret
                .authorizedGrantTypes("authorization_code", "refresh_token") // 该client允许的授权类型，这里设置为授权码模式，使用认证返回的授权码获取access_token，在sso体系中，各个服务（包括@EnableResourceServer注解的资源服务器在内）相当于客户端，配置了client-id: client client-secret: secret等信息，相当于浏览器或Postman（见下面授权码模式OAuth2.0流程注释）等使用Post等方式访问@EnableAuthorizationServer注解的认证服务器，带上参数：client_id=client&grant_type=authorization_code&redirect_uri=http://www.baidu.com&code=aO28s6
                .scopes("all","read","write") // 允许的授权范围
                .autoApprove(true);
    }

    /**
     授权码模式OAuth2.0流程：

     1.应用访问认证服务器参数：授权码模式&第三方注册的（这里是配置的）client_id&重定向地址&自定义state信息
     访问：http://localhost:8767/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://www.baidu.com&state=123
     出现登录页面，输入用户名：admin 密码；123456
     2.这是一个Spring Security登录，是基于用户名/密码方式授权的第一步
     点击Submit按钮，进入用户授权确认页面
     3.这一步使用Spring Security用户进行的授权，认证服务器跳转到1中传递的redirect_uri，并返回授权码
     点击Approve,跳转到baidu页面，后面携带了code和state参数
     https://www.baidu.com/?code=aO28s6&state=123
     4.客户端收到授权码，附上早先的"重定向URI"（必须与1中传递的完全一致！比如不能一个是http://www.baidu.com，一个是https://www.baidu.com，或者http://baidu.com），向认证服务器申请令牌。这一步实际上应该在客户端的后台的服务器上完成的，对用户不可见
     根据code换取access_code------------------------------>注意这里用post方法！！get方法得不到！（测试时使用postman）！
     http://localhost:8767/oauth/token
     参数：（Post Body里面的内容）
     client_id=client&grant_type=authorization_code&redirect_uri=http://www.baidu.com&code=aO28s6
     注意这个code要和上个步骤中获得的code保持一致
     这里不！能！缺！少！一个重要的头信息：Authorization: Basic Y2xpZW50OnNlY3JldA== （这里是"client"与"secret"的clientId:secret Base64值）
     Authorization的值为：Basic + （clientId:secret Base64值）
     （有一个例子里面，全程使用浏览器，发送这个请求后有一个弹出的Authorization框，让用户输入clientId和secret
     用户名输入client，密码是secret,点击确定得到access_token返回值）
     5.认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（access token）和更新令牌（refresh token）
     （实验发现只能获取一次，再次按此授权码获取提示授权码失效：Invalid authorization code:xxxx）
     返回：
     {
        "access_token": "38ef0fec-1b9f-4ee9-8c1a-95c03f6347a1",
        "token_type": "bearer",
        "expires_in": 42902,
        "scope": "app"
     }

     其他认证模式：

     所谓用户名/密码方式，是一次性将用户名、密码、client_id、client_secret传递给认证服务器，
     没有中间获取认证码，使用认证码访问获取的过程，是企业内部认证、资源服务的一种相对简单常用的方式

     客户端模式更是连用户名/密码体系都没有，直接用client_id、client_secret获取access_token
     */

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints.authenticationManager(authenticationManager)//配置认证所需的用户权限体系，这里使用Spring Security配置的用户权限体系
//                .userDetailsService(userDetailsService);//如果和登录使用一套体系，这里可以不配置，默认就是Spring Security的认证、权限系统配置，适合本身已有登录体系，需要扩展成sso体系或第三方oauth2.0系统的工程
        endpoints.tokenStore(jwtTokenStore()).accessTokenConverter(jwtAccessTokenConverter());//使用jwt（内包含用户全部信息，客户端可据此和获取解析jwt令牌所需要密钥接口来解密获取到的access_token，解析出客户端信息）生成的access_token并存储（存储是用于访问资源服务器时，被导向认证服务器，认证成功后，资源服务器取出、验证）
    }//在SSO体系中，客户端访问资源服务器（资源服务器并没有配置/oauth/authorize #请求认证的地址和/oauth/token #请求令牌的地址，因为它不需要认证，只是到认证服务器获取访问它的服务的认证信息，通过token-info-uri: ${auth-server}/oauth/check-token配置），被导向认证服务器获取access_token,再访问资源服务器，资源服务器根据access_token到（配置的）同一个认证服务器tokenStore（存储了刚刚认证成功并生成的客户端的token）验证access_token，通过后允许访问资源

    /**
     * JWTtokenStore
     * @return
     */
    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 生成JTW token
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("merryyou");//设置的jwt加密/解密密钥，认证服务器生成对应的获取密钥接口/oauth/token_key获取这里的密钥，供客户端获取到jwt后据此解密
        return converter;
    }

    /**
     * 构建header（根据授权码获取access_token的Post请求中不可缺少的Http认证头，否则返回401未认证！！）
     * @return
     */
    private static String getBasicAuthHeader(String CLIENT_ID,String CLIENT_SECRET){
        String auth = CLIENT_ID + ":" + CLIENT_SECRET;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }

    public static void main(String[] args) {
        String authHeader = getBasicAuthHeader("client","secret");
        log.info("AuthHeader:" + authHeader);
    }
}
/**
 授权码模式步骤如下：

 （A）用户访问客户端，后者将前者导向认证服务器。

 （B）用户选择是否给予客户端授权。

 （C）假设用户给予授权，认证服务器将用户导向客户端事先指定的"重定向URI"（redirection URI），同时附上一个授权码。

 （D）客户端收到授权码，附上早先的"重定向URI"，向认证服务器申请令牌。这一步是在客户端的后台的服务器上完成的，对用户不可见。

 （E）认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（access token）和更新令牌（refresh token）。

 下面是上面这些步骤所需要的参数。

 A步骤中，客户端申请认证的URI，包含以下参数：

 response_type：表示授权类型，必选项，此处的值固定为"code"
 client_id：表示客户端的ID，必选项
 redirect_uri：表示重定向URI，可选项
 scope：表示申请的权限范围，可选项
 state：表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值。
 下面是一个例子。


 GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
 &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
 Host: server.example.com

 C步骤中，服务器回应客户端的URI，包含以下参数：

 code：表示授权码，必选项。该码的有效期应该很短，通常设为10分钟，客户端只能使用该码一次，否则会被授权服务器拒绝。该码与客户端ID和重定向URI，是一一对应关系。
 state：如果客户端的请求中包含这个参数，认证服务器的回应也必须一模一样包含这个参数。
 下面是一个例子。


 HTTP/1.1 302 Found
 Location: https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA
 &state=xyz

 D步骤中，客户端向认证服务器申请令牌的HTTP请求，包含以下参数：

 grant_type：表示使用的授权模式，必选项，此处的值固定为"authorization_code"。
 code：表示上一步获得的授权码，必选项。
 redirect_uri：表示重定向URI，必选项，且必须与A步骤中的该参数值保持一致。
 client_id：表示客户端ID，必选项。
 下面是一个例子。


 POST /token HTTP/1.1
 Host: server.example.com
 Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
 Content-Type: application/x-www-form-urlencoded

 grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA
 &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb

 E步骤中，认证服务器发送的HTTP回复，包含以下参数：

 access_token：表示访问令牌，必选项。
 token_type：表示令牌类型，该值大小写不敏感，必选项，可以是bearer类型或mac类型。
 expires_in：表示过期时间，单位为秒。如果省略该参数，必须其他方式设置过期时间。
 refresh_token：表示更新令牌，用来获取下一次的访问令牌，可选项。
 scope：表示权限范围，如果与客户端申请的范围一致，此项可省略。
 下面是一个例子。


 HTTP/1.1 200 OK
 Content-Type: application/json;charset=UTF-8
 Cache-Control: no-store
 Pragma: no-cache

 {
 "access_token":"2YotnFZFEjr1zCsicMWpAA",
 "token_type":"example",
 "expires_in":3600,
 "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
 "example_parameter":"example_value"
 }

 从上面代码可以看到，相关参数使用JSON格式发送（Content-Type: application/json）。此外，HTTP头信息中明确指定不得缓存。
 */