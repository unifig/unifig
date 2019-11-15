package etl.dispatch.boot.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.boot.authentication.IAuthTokenLoginService;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.util.MD5;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.ip.InternetProtocol;

public class PermissionsAuthFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(PermissionsAuthFilter.class);
	private static String loginAction = "/user/login";

	@Override
	public void destroy() {
		System.out.println("请求访问权限过滤器销毁");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if(httpRequest.getMethod().equals("OPTIONS")){
			chain.doFilter(request, response);
			return ;
		}
		String requestUri = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
		IAuthTokenLoginService tokenPrivilegeService = SpringContextHolder.getBean("iAuthTokenLoginService", IAuthTokenLoginService.class);
		// 获取token令牌
		String adoptToken = request.getParameter("adoptToken");
		//判断OPTIONS 直接返回
		if(httpRequest.getMethod().equals("OPTIONS")){
			chain.doFilter(request, response);
			return ;
		}

		//判断adoptToken是否为空  不是登录接口    否则 返回
		if (StringUtil.isNullOrEmpty(adoptToken) && !requestUri.equals(loginAction)) {
			String curOrigin = httpRequest.getHeader("Origin");
			httpResponse.setHeader("Access-Control-Allow-Origin", curOrigin);
			httpResponse.setHeader("Access-Control-Allow-Headers", "*");
			httpResponse.setHeader("Access-Control-Allow-Methods", "*");
			httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
			httpResponse.getWriter().write(JSON.toJSONString(new ResponseCommand(ResponseCommand.STATUS_LOGIN_ERROR, new VisitsResult(new NewMapUtil("message", "Request token is not invalid, Please login again to get the new token ").get()))));
			return;
		}
		// 未传递令牌且非登录请求
		if (!StringUtil.isNullOrEmpty(adoptToken) && !requestUri.equals(loginAction)) {
			//判断 请求token是否恶意
			String md5Addr = MD5.encryptToHex(InternetProtocol.getRemoteAddr(WebUtils.toHttp(request)));
			if(!StringUtil.isNullOrEmpty(md5Addr)){
				if(!adoptToken.startsWith(md5Addr)){
					String curOrigin = httpRequest.getHeader("Origin");
					httpResponse.setHeader("Access-Control-Allow-Origin", curOrigin);
					httpResponse.setHeader("Access-Control-Allow-Headers", "*");
					httpResponse.setHeader("Access-Control-Allow-Methods", "*");
					httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
					httpResponse.getWriter().write(JSON.toJSONString(new ResponseCommand(ResponseCommand.STATUS_LOGIN_ERROR, new VisitsResult(new NewMapUtil("message", "Request token is not invalid, Please login again to get the new token ").get()))));
					return;
				}
			}
			if(!tokenPrivilegeService.isAuthenticated(adoptToken)){
				String curOrigin = httpRequest.getHeader("Origin");
				httpResponse.setHeader("Access-Control-Allow-Origin", curOrigin);
				httpResponse.setHeader("Access-Control-Allow-Headers", "*");
				httpResponse.setHeader("Access-Control-Allow-Methods", "*");
				httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
				httpResponse.getWriter().write(JSON.toJSONString(new ResponseCommand(ResponseCommand.STATUS_LOGIN_ERROR, new VisitsResult(new NewMapUtil("message", "Request token is not invalid, Please login again to get the new token ").get()))));
				return;
			}

		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		System.out.println("请求访问权限过滤器初始化");

	}
	
	
	
}
