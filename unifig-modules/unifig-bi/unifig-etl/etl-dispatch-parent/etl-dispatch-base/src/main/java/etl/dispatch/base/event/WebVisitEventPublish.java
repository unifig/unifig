package etl.dispatch.base.event;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import etl.dispatch.base.enums.LoginAuthResultEnums;
import etl.dispatch.base.enums.LoginTypeEnum;
import etl.dispatch.base.enums.OperateCodeEnum;
import etl.dispatch.base.event.impl.LoginEvent;
import etl.dispatch.base.event.impl.OperateEvent;
import etl.dispatch.base.event.impl.VisitResEvent;
import etl.dispatch.base.holder.SpringContextHolder;

/**
 * @Title:Web事件发布 对应WebLoggingEventListener(ApplicationListener),由WebLoggingEventListener继续处理
 * 例如发布日志：WebVisitEventPublish.getInstance().operateEvent(OperateCodeEnum.LOOK, "跳转到内页查看", false, null, destObj, resCode, startTime, new Date());
 *
 */
@Component
public class WebVisitEventPublish implements ApplicationEventPublisherAware {
	private ApplicationEventPublisher eventPublisher;

	public void loginEvent(LoginAuthResultEnums loginResult) {
		LoginEvent loginEvent = new LoginEvent(this, LoginTypeEnum.LOGIN, DateTime.now().toDate(), loginResult);
		this.eventPublisher.publishEvent(loginEvent);
	}

	public void logoutEvent(boolean isTimeout) {
		LoginEvent loginEvent = new LoginEvent(this, isTimeout ? LoginTypeEnum.TIMEOUT : LoginTypeEnum.LOGOUT, DateTime.now().toDate());
		this.eventPublisher.publishEvent(loginEvent);
	}

	public void visitMenuEvent(String menuId, Date visitBeginTime, Date visitEndTime, boolean isSuccess, String errorMsg) {
		VisitResEvent visitResEvent = new VisitResEvent(this, menuId, visitBeginTime, visitEndTime, isSuccess, errorMsg);
		this.eventPublisher.publishEvent(visitResEvent);
	}

	/**
	 * @Title: operateEvent 功能操作事件
	 * @param operateCodeEnum 操作编码枚举值,如:EXPORT_DATA
	 * @param content 操作内容 如：UPDATE_USER 内容为修改信息,EXPORT_DATA 内容为SQL语句
	 * @param isError  操作是否失败
	 * @param ErrorMsg 如果错误，错误信息
	 * @param destObj 操作目标对象,如:EXPORT_DATA 目标对象为menuId,ADD_USER 目标对象为userId
	 * @param menuId  操作菜单id
	 * @beginTime 操作开始时间
	 * @endTime 操作结束时间
	 */
	public void operateEvent(OperateCodeEnum operateCodeEnum, String content, boolean isError, String ErrorMsg, String destObj, String menuId, Date beginTime, Date endTime) {
		OperateEvent event = new OperateEvent(this, menuId, isError, ErrorMsg, beginTime, endTime, operateCodeEnum, destObj, content);
		this.eventPublisher.publishEvent(event);
	}

	public void serviceInvokeEvent(String interCode, String invokeCont, boolean isFail, String errMsg, Date invokeBeginTime, Date invokeEndTime) {

	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	public static WebVisitEventPublish getInstance() {
		return SpringContextHolder.getBean(WebVisitEventPublish.class);
	}
}
