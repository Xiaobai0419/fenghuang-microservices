package com.xiaobai.fenghuangapigateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class FengAccessFilter extends ZuulFilter{
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
//        RequestContext currentContext = RequestContext.getCurrentContext();
//        HttpServletRequest request = currentContext.getRequest();
//        log.info("send{} request to {}",request.getMethod(),request.getRequestURI().toCharArray());
//        String accessToken = request.getParameter("accessToken");
//        if(StringUtils.isBlank(accessToken)){
//            log.error("accessToken is empty");
//            currentContext.setSendZuulResponse(false);//表示该请求不进行路由
//            currentContext.setResponseStatusCode(401);
//        }
//        log.info("accessToken {}" ,accessToken);
        return null;
    }
}
/**
 启动相应的服务，访问相应的链接，当不添加accessToken是会抛出401错误。
 上面说到我们需要实现抽象类的ZuulFilter的四个抽象方法；
 1. filterType：过滤器类型，他决定过滤器在请求的哪个生命周期执行。在Zuul中有四种不同的生命周期过滤器：
        pre：可以在请求被路由之前调用；
        routing：在路由请求是调用；
        post：在routing和error过滤器之后被调用；
        error：处理请求是发生错误是被调用
 2. filterOrder：过滤器的执行顺序，数值越小优先级越高
 3. shouldFilter：判断过滤器是否需要执行
 4. run： 过滤器的具体逻辑。上面的run方法中判断请求是否带有accessToken参数，如果没有则是非法请求，
    使用 currentContext.setSendZuulResponse(false);表示该请求不进行路由。然后设置响应码。
 */