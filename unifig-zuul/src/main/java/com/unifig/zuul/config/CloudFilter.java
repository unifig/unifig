package com.unifig.zuul.config;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @ClassName CloudFilter
 * @Description 跨域配置
 * @date 2017年12月13日 上午11:41:44
 */
@Configuration
public class CloudFilter extends ZuulFilter {

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
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        HttpServletRequest request = ctx.getRequest();
        String[] allowDomains = {"http://admin.ratelll.com","http://localhost:8888","http://114.67.88.118:81","http://119.3.213.62","http://admin.yirisanxian.com/","https://api.yirisanxian.com/","https://ypyx-api.sandinfo.com/","http://ypyx-admin.sandinfo.com"};
        Set allowOrigins = new HashSet(Arrays.asList(allowDomains));
        String originHeads = request.getHeader("Origin");
        if(allowOrigins.contains(originHeads)){
            //设置允许跨域的配置
            // 这里填写你允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
            response.setHeader("Access-Control-Allow-Origin", originHeads);
        }
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Access-Control-Allow-Origin", "http://unifig-app.ratelll.com:8092");
//        response.addHeader("Access-Control-Allow-Origin", "http://unifig-admin.ratelll.com:8091");
//        response.addHeader("Access-Control-Allow-Origin", "http://unifig-app.ratelll.com:8092");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        return null;
    }

}