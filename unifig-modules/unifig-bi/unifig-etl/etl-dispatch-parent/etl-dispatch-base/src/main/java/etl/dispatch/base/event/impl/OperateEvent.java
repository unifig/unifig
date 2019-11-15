package etl.dispatch.base.event.impl;

import java.util.Date;

import etl.dispatch.base.enums.OperateCodeEnum;
import etl.dispatch.base.event.AbstractWebVisitEvent;

/**
 * @Title:功能操作事件,如（导出、上传、下载、增删改等）
 *
 */

public class OperateEvent extends AbstractWebVisitEvent {
	private static final long serialVersionUID = -7372309216314198520L;

	public OperateEvent(Object source) {
		super(source);
	}

	private String resId;// 资源编码
	private boolean isError;// 是否错误
	private String operateErrorMsg;// 操作报错信息
	private Date operateBeginTime;
	private Date operateEndTime;
	private OperateCodeEnum operateType; // 操作类型
	private String operateObjId; // 操作对象id
	private String operateCont;// 操作内容

	public OperateEvent(Object source, String resId, boolean isError, String operateErrorMsg, Date operateBeginTime, Date operateEndTime, OperateCodeEnum operateType, String operateObjId, String operateCont) {
		super(source);
		this.resId = resId;
		this.isError = isError;
		this.operateErrorMsg = operateErrorMsg;
		this.operateBeginTime = operateBeginTime;
		this.operateEndTime = operateEndTime;
		this.operateType = operateType;
		this.operateObjId = operateObjId;
		this.operateCont = operateCont;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public boolean isSuccess() {
		return isError;
	}

	public void setSuccess(boolean isError) {
		this.isError = isError;
	}

	public String getOperateErrorMsg() {
		return operateErrorMsg;
	}

	public void setOperateErrorMsg(String operateErrorMsg) {
		this.operateErrorMsg = operateErrorMsg;
	}

	public Date getOperateBeginTime() {
		return operateBeginTime;
	}

	public void setOperateBeginTime(Date operateBeginTime) {
		this.operateBeginTime = operateBeginTime;
	}

	public Date getOperateEndTime() {
		return operateEndTime;
	}

	public void setOperateEndTime(Date operateEndTime) {
		this.operateEndTime = operateEndTime;
	}

	public OperateCodeEnum getOperateType() {
		return operateType;
	}

	public void setOperateType(OperateCodeEnum operateType) {
		this.operateType = operateType;
	}

	public String getOperateObjId() {
		return operateObjId;
	}

	public void setOperateObjId(String operateObjId) {
		this.operateObjId = operateObjId;
	}

	public String getOperateCont() {
		return operateCont;
	}

	public void setOperateCont(String operateCont) {
		this.operateCont = operateCont;
	}
}
