package com.unifig.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.unifig.result.MsgCode;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import com.unifig.utils.JwtTokenUtil;
import com.unifig.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 权限(Token)验证
 *
 *
 * @email kaixin254370777@163.com
 * @date 2019-01-26 15:38
 */
@Configuration
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JwtTokenUtil jwtTokenUtil = SpringContextUtils.getBean(JwtTokenUtil.class);
        //UserTokenUtil userTokenUtil = SpringContextUtils.getBean(UserTokenUtil.class);
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());// The part after "Bearer "
            //String username = jwtTokenUtil.getUserNameFromToken(authToken);
            ResultData resultData = jwtTokenUtil.checkoutToken(authToken);
            LOGGER.info("checking resultData.code:{} resultData.message()", resultData.getCode(), resultData.getMsg());
            // Map<Object, Object> userInfo = jwtTokenUtil.getUserInfo(authToken);
            // UserCache userCache = userTokenUtil.getUserCacheFromUserMap(userInfo);
            // request.setAttribute("userCache",userCache);

            if (resultData.getCode() != MsgCode.SUCCESS.getCode()) {
                //token==null 用户未登陆
                String originHeads = request.getHeader("Origin");
                //设置允许跨域的配置
                // 这里填写你允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Origin", originHeads);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter out = response.getWriter();
                out.append(JSONObject.toJSONString(resultData));
                out.close();
                return false;
            }
            return true;

        }
        //response.setCharacterEncoding("UTF-8");
        //response.setContentType("application/json; charset=utf-8");
        //PrintWriter out = response.getWriter();
        //out.append(JSONObject.toJSONString(ResultData.result(false).setCode(MsgConstants.USER_UNAUTH)));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
