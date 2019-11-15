package etl.dispatch.boot.authentication;

import javax.servlet.http.HttpServletRequest;

import etl.dispatch.boot.entity.ConfUserInfo;
import etl.dispatch.boot.response.VisitsResult;

/**
 *
 */
public interface IAuthTokenLoginService {

    /***
     * 登录验证方法
     */
	public VisitsResult authc(String loginName, String loginPass, HttpServletRequest request);

    /**
     * 用户注销方法
     */
    public VisitsResult logout(String adoptToken);

	ConfUserInfo getCurrentUser(String adoptToken);

	boolean isAuthenticated(String adoptToken);
}
