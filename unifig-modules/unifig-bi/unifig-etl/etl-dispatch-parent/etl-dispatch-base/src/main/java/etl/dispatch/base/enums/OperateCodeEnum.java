package etl.dispatch.base.enums;

 /**
 * 操作日志类型编码枚举类，用于在需要记录日志的service层的相关方法上标记注解，标识此方法是什么操作。
 *
 *
 */
 
public enum OperateCodeEnum {
    UNKNOW("UNKNOW"),
    /** ADD_USER:增加用户*/
    ADD_USER("ADD_USER"),
    /** UPDATE_USER:修改用户*/
    UPDATE_USER("UPDATE_USER"), 
    /** DEL_USER:删除用户*/
    DEL_USER("DEL_USER"),
    /** USER_AUTH:用户授权*/
    USER_AUTH("USER_AUTH"), 
    /** EXPORT_DATA:导出数据*/
    EXPORT_DATA("EXPORT_DATA"), 
    /** EXPORT_LIST:导出列表*/
    EXPORT_LIST("EXPORT_LIST"),
    /**LOOK:查看对象*/
    LOOK("LOOK");
    private String value;
    
    public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private OperateCodeEnum(String value) {
		this.value=value;
	}
}