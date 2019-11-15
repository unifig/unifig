package etl.dispatch.base.event.impl;

import java.util.Date;

import etl.dispatch.base.event.AbstractWebVisitEvent;

/**
 * @Title:资源访问事件,如:(菜单点击、报表点击等)
 *
 */
public class VisitResEvent extends AbstractWebVisitEvent {
	private static final long serialVersionUID = -5137266049278359826L;

	public VisitResEvent(Object source) {
		super(source);
	}

	private String resId;// 资源编码

	private boolean isSuccess;// 访问结果 1:正常访问 2:访问报错

	private String visitErrorMsg;// 访问报错信息

	private Date visitBeginTime;

	private Date visitEndTime;

	public Date getVisitBeginTime() {
		return visitBeginTime;
	}

	public void setVisitBeginTime(Date visitBeginTime) {
		this.visitBeginTime = visitBeginTime;
	}

	public Date getVisitEndTime() {
		return visitEndTime;
	}

	public void setVisitEndTime(Date visitEndTime) {
		this.visitEndTime = visitEndTime;
	}

	public VisitResEvent(Object source, String resId, Date visitBeginTime, Date visitEndTime, boolean isSuccess, String visitErrorMsg) {
		super(source);
		this.resId = resId;
		this.isSuccess = isSuccess;
		this.visitErrorMsg = visitErrorMsg;
		this.visitBeginTime = visitBeginTime;
		this.visitEndTime = visitEndTime;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getVisitErrorMsg() {
		return visitErrorMsg;
	}

	public void setVisitErrorMsg(String visitErrorMsg) {
		this.visitErrorMsg = visitErrorMsg;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}
}
