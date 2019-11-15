package etl.dispatch.boot.abstracts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import etl.dispatch.boot.authentication.IAuthTokenLoginService;
import etl.dispatch.boot.entity.ConfUserInfo;

@Component
public abstract class ServiceAbstract {
	@Autowired
	private IAuthTokenLoginService iAuthTokenLoginService;

	public ConfUserInfo getUser(HttpServletRequest request) {
		String adoptToken = request.getParameter("adoptToken");
		ConfUserInfo currentUser = iAuthTokenLoginService.getCurrentUser(adoptToken);
		return currentUser;
	}
}
