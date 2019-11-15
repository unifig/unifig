package etl.dispatch.util;
/**
 * ajax请求返回信息封装的对象，包含message自定义信息和异常堆栈信息，最终会把该对象转换为json数据。
 * 
 *
 *
 */
public class AjaxUtil {
    private String message;
    private String stackTrace;
    /**
     * AjaxUtil默认无参构造器。
     */
    public AjaxUtil(){}
    
    /**
     * AjaxUtil有参构造器。
     */
    public AjaxUtil(String message,String stackTrace){
        this.message = message;
        this.stackTrace = stackTrace;
    }
    
    /**
     * 封装ajax前台返回信息对象。<br/>
     * 详细描述：封装ajax前台返回信息对象，包含message信息，和异常堆栈信息。<br/>
     * 使用方式：实例化AjaxUtil后调用getExceptionJson方法。
     * @param message 自定义的描述信息。
     * @param stackTrace 异常堆栈信息。
     * @return ajax前台返回信息对象。
     */
    public static AjaxUtil getExceptionJson(String message,String stackTrace){
        return new AjaxUtil(message,stackTrace);
    }
    
    /**
     * ajax前台返回信息的get方法。
     * @return ajax前台返回信息。
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * ajax前台返回信息的set方法。
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * ajax前台返回异常堆栈信息的get方法。
     * @return ajax前台返回异常堆栈信息。
     */
    public String getStackTrace() {
        return stackTrace;
    }
    
    /**
     * ajax前台返回异常堆栈信息的set方法。
     */
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}