package etl.dispatch.base.exception;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import etl.dispatch.base.holder.ExceptionMessageHolder;

/**
 * BaseRuntimeException是一个自定义的基础运行时异常，本身继承了RuntimeException，
 * 如果开发人员需要自定义异常信息可以继承该类，同时还提供了对异常的message信息进行封装的功能，
 * 因此开发人员只需要在抛出异常时，里面传入枚举类中定义的对象，便自动根据对象获取国际化配置中对应的中文描述，
 * 同时还提供了给异常中文描述设置参数的功能。
 * 
 *
 *
 */
public class BaseRuntimeException extends RuntimeException{
    private static final Logger logger = LoggerFactory.getLogger(BaseRuntimeException.class);
	private static final long serialVersionUID = 1401593546385403720L;
	private Object[] object = null;
	private Object code = null;
	
	/**
	 * BaseRuntimeException默认构造器，可直接实例化无需传参数。
	 */
	public BaseRuntimeException(){
		super();
	}
	
	/**
	 * BaseRuntimeException的有参数构造器，其参数会赋值给code变量(系统支持该参数为枚举类定义的对象)。<br/>
	 * 详细描述：BaseRuntimeException进行实例化时传入的message参数(系统默认支持该参数为枚举类定义的对象)会赋值给定义好的code变量，
	 *        以便为了获取国际化中对应的中文描述。</br>
	 * 使用方式：throw new BaseRuntimeException(message)，message可以是自定义中文描述，也可以使用枚举类定义好的对象。
	 * @param message 可以是一段中文描述，会自动替换父类的异常信息，也可以是枚举类中定义好的对象，描述信息会自动读取国际化中配置好的。
	 */
	public BaseRuntimeException(Object message){
		code = message;
	}
	
	/**
	 * BaseRuntimeException的有参数构造器，直接抛出传入的异常类型。<br/>
	 * 详细描述：根据传入的异常类型，通过调用父类的方法直接抛出该异常，内部的信息没有做任何处理，仅仅只是异常类变为BaseRuntimeException。</br>
	 * 使用方式：throw new BaseRuntimeException(e)，e为catch中捕获到的异常对象。
	 * @param cause 所有错误和异常对象的超类。
	 */
	public BaseRuntimeException(Throwable cause){
		super(cause);
	}
	
	/**
     * BaseRuntimeException的有参数构造器，其参数会赋值给code变量(系统支持该参数为枚举类定义的对象)。<br/>
     * 详细描述：BaseRuntimeException进行实例化时传入的message参数(系统默认支持该参数为枚举类定义的对象)会赋值给定义好的code变量，
     *        以便为了获取国际化中对应的中文描述。</br>
     * 使用方式：throw new BaseRuntimeException(message)，message可以是自定义中文描述，也可以使用枚举类定义好的对象。
     * @param message 可以是一段中文描述，会自动替换父类的异常信息，也可以是枚举类中定义好的对象，描述信息会自动读取国际化中配置好的。
     * @param cause 所有错误和异常对象的超类。
     */
	public BaseRuntimeException(Object message,Throwable cause){
		code = message;
	}
	
	/**
	 * 为自定义异常类设置参数。<br/>
	 * 详细描述：可以在抛出异常类后面调用该方法设置参数数组，目的是为了匹配国际化文件中定义的中文描述进行参数替换，必须按照顺序。</br>
	 * 使用方式：throw new BaseRuntimeException(e).setParams(new Object[]{"参数值"})，进行参数设置后还会返回该异常类。
	 * @param obj 参数数组，是用来和国际化中定义的参数进行匹配，顺序必须一致，获取到的信息会自动把参数替换为数组中的值。
	 * @return this即为该类。
	 */
	public BaseRuntimeException setParams(Object[] obj){
	    object = obj;
	    return this;
	}
	
	/**
	 * 获取国际化配置好的中文描述替换抛出异常的messge信息。<br/>
	 * 详细描述：该方法重写了父类的getMessage方法，所以会把抛出异常的描述信息自动替换为国际化中配置好的信息。</br>
	 * 使用方式：该方法无需调用，因为是重写了父类的getMessage方法，所以会把抛出异常的描述信息自动替换为国际化中配置好的信息。
	 * @return 国际化配置中的中文描述信息
	 */
    public String getMessage() {
	    String message = "";
	    if (null != code && !"".equals(code) && !"null".equals(code)) {
	    	message = code.toString();
	    }
        Class<?> codeClass = code.getClass();
        Method[] methodArray = codeClass.getMethods();
        for (int i = 0;i < methodArray.length;i++) {
            Method method = methodArray[i];
            method.setAccessible(true);
            if (method.getName().equals("getPropertiesCode")) {
                Object value = null;
                try {
                    value = method.invoke(codeClass, new Object[]{code});
                } catch (IllegalArgumentException e) {
                    logger.error("BaseRuntimeException class (getMessage method) IllegalArgumentException：", e);
                } catch (IllegalAccessException e) {
                    logger.error("BaseRuntimeException class (getMessage method) IllegalAccessException：", e);
                } catch (InvocationTargetException e) {
                    logger.error("BaseRuntimeException class (getMessage method) InvocationTargetException：", e);
                }
                message = ExceptionMessageHolder.getExceptionMessages(value, object);
            }
        }
        return message;
    }
}