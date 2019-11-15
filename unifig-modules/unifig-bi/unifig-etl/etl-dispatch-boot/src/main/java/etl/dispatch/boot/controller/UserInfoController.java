package etl.dispatch.boot.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;

import etl.dispatch.base.enums.LoginAuthResultEnums;
import etl.dispatch.boot.abstracts.ServiceAbstract;
import etl.dispatch.boot.authentication.IAuthTokenLoginService;
import etl.dispatch.boot.entity.ConfUserInfo;
import etl.dispatch.boot.enums.StatusEnum;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfUserInfo;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;

@RestController
@RequestMapping(value = "/user")
public class UserInfoController extends ServiceAbstract{
	private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
	@Autowired
	private IAuthTokenLoginService loginAuthcService;
	@Autowired
	private IConfUserInfo iConfUserInfo;

	/**
	 * 登录
	 * 
	 * @param userName
	 * @param passWord
	 * @param request
	 * @return
	 */
	@PostMapping(value = "login")
	public Object login(@RequestParam(value = "adoptToken", required = false) String adoptToken, 
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "passWord", required = false) String passWord, HttpServletRequest request) {

		// 用户名或者密码为空
		if (!StringUtils.hasText(userName) || !StringUtils.hasText(passWord)) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(
					new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_PARAM_NULL.toString()).get()));
		}
		VisitsResult loginCommand = null;
		if (StringUtil.isNullOrEmpty(adoptToken)) {
			// 令牌为空，直接执行登录
			loginCommand = loginAuthcService.authc(userName, passWord, request);
		} else {
			// 获取令牌对应登录用户
			ConfUserInfo baseUser = loginAuthcService.getCurrentUser(adoptToken);
			String loginName = null;
			if (null != baseUser) {
				loginName = baseUser.getUserName();
			}
			// 令牌是否已登录，令牌用户的登录名 == 请求登录名参数
			if (loginAuthcService.isAuthenticated(adoptToken) && !StringUtil.isNullOrEmpty(loginName) && !StringUtil.isNullOrEmpty(userName)) {
				// 已登录，当前令牌有效状态
				if (userName.equals(loginName)) {
					return new ResponseCommand(ResponseCommand.STATUS_ERROR,  new VisitsResult(new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_REPEAT_LOGIN.toString()).get()));
				}
			}
			// 新登录，执行登录校验
			loginCommand = loginAuthcService.authc(loginName, passWord, request);
		}
		
		if (null == loginCommand) {
			//LoggingEventPublish.getInstance().loginEvent(LoginAuthResultEnums.LOGIN_FAIL_EXCEPTION);
			return new ResponseCommand(ResponseCommand.STATUS_ERROR,  new VisitsResult(new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_EXCEPTION.toString()).get()));
		}  else {
			//LoggingEventPublish.getInstance().loginEvent(LoginAuthResultEnums.LOGIN_SUCCESS);
			if(!StringUtil.isNullOrEmpty(loginCommand.getAdoptToken())){
				return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, loginCommand);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> result = (Map<String, Object>) loginCommand.getResult();
			if(result.get("message").equals(LoginAuthResultEnums.LOGIN_FAIL_USER_NOTEXSIST.getMsg())){
				return new ResponseCommand(ResponseCommand.STATUS_ERROR, loginCommand);
			}else {
				return new ResponseCommand(ResponseCommand.STATUS_LOGIN_ERROR, loginCommand);	
			}
		}
		
//		List<ConfUserInfo> list = iConfUserInfo.selectByMap(new NewMapUtil().set("user_name", userName).get());
//
//		if (list.size() > 1) {
//			logger.error(userName + ":用户名重复！！！！");
//		}
//		if (list == null || list.size() == 0 || list.isEmpty()) {
//			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(
//					new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_USER_NOTEXSIST.toString()).get()));
//		}

		
		
//		ConfUserInfo confUserInfo = list.get(0);
//		// 获取用户密码
//		String passWords = confUserInfo.getPassWord();
//		if (!StringUtil.isNullOrEmpty(passWords) && !StringUtil.isNullOrEmpty(userName)) {
//			// 数据库存储MD5密码
//			if (MD5.encryptToHex(passWord).equals(passWords)) {
//				HttpSession session = request.getSession();
//				session.setAttribute("user", confUserInfo);
//
//				return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, confUserInfo);
//			} else {
//				return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(
//						new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_PWD_INCORRECT.toString()).get()));
//			}
//		} else {
//			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(
//					new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_PWD_INCORRECT.toString()).get()));
//		}
	}

	/**
	 * 令牌注销登录
	 */
	@GetMapping(value = "logout")
	public Object logout(HttpServletRequest request,@RequestParam(value = "adoptToken", required = false) String adoptToken) {
		VisitsResult logoutResult = null;
		if (StringUtil.isNullOrEmpty(adoptToken)) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", etl.dispatch.base.enums.LogoutResultEnums.LOGOUT_FAIL_EXCEPTION.toString()).get()));
		} else {
			logoutResult = loginAuthcService.logout(adoptToken);
		}
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, logoutResult);
	}

	@RequestMapping(method=RequestMethod.POST)
	public Object insert( ConfUserInfo userInfo, HttpServletRequest request) {
		userInfo.setCuser(super.getUser(request).getName());
		userInfo.setCtime(new Date());
		boolean bool = userInfo.insert();
		
		if (bool) {
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(userInfo));
		}
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS,
				new VisitsResult(new NewMapUtil().set("message", "添加失败").get()));
	}
	
	@PutMapping
	public Object update( ConfUserInfo userInfo, HttpServletRequest request){
		userInfo.setMuser(super.getUser(request).getName());
		userInfo.setMtime(new Date());
		boolean bool = userInfo.updateById();
		if (bool) {
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(userInfo));
		}
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS,
				new VisitsResult(new NewMapUtil().set("message", "修改失败").get()));
	}
	
	@GetMapping
	public Object selectList(){
		List<ConfUserInfo>  list= iConfUserInfo.selectList(new EntityWrapper<ConfUserInfo>().where("status={0}", StatusEnum.ENABLE.getCode()));
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(list));
	}
	
	
	@DeleteMapping
	public Object delete(@RequestParam Integer id){
		ConfUserInfo entity=new ConfUserInfo();
		entity.setId(id);
		entity.setStatus(StatusEnum.DELETED.getCode());
		boolean bool = iConfUserInfo.updateById(entity);
		if (bool) {
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(bool));
		}
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS,
				new VisitsResult(new NewMapUtil().set("message", "删除失败").get()));
	}

}
