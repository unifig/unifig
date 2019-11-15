package etl.dispatch.base.holder;
import org.springframework.context.NoSuchMessageException;

import etl.dispatch.base.enums.BaseExceptionEnums;
 /**
 * ExceptionMessageHolder类是异常信息帮助类，主要用于将异常信息转化为国际化的中问描述，例如：根据
 * sql异常code获取对应的国际化中文描述。
 * 
 *
 *
 */
public class ExceptionMessageHolder {
    /** 
     * 根据sql异常code获取对应的国际化中文描述。<br/>
     * 详细描述：根据sql异常code获取对应的国际化中文描述。<br/>
     * 使用方式：用于对于需要进行异常处理的模块，将异常信息捕获后调用此方法，然后将对应的国际化中文描述信息绑定到页面，以便前台页面报错是快速锁定问题所在。
     * @param key 异常代码。
     * @param obj 国际化文件中定义的参数。
     * @return message sql异常code对应的国际化中文描述信息。
     */ 
	public static String getExceptionMessages(Object key, Object[] obj) {
		String message = "";
		try {
			message = SpringContextHolder.getApplicationContext().getMessage(key.toString(), obj, null);
		} catch (NoSuchMessageException e) {
			message = getExceptionMessages(BaseExceptionEnums.getPropertiesCode(BaseExceptionEnums.NO_MESSAGE), new Object[] { key });
		}
		return message;
	}
}