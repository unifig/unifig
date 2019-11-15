package etl.dispatch.util;
import java.sql.Date;
import java.sql.Timestamp;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

 /**
 * 获取当前日期和时间的工具类
 * 
 *
 *
 */
public class CurrentTimeUtil {
    /** 
     * 获取当前日期和时间。<br/>
     * 详细描述：获取当前日期和时间，返回String类型,输出格式：yyyy-MM-dd HH:mm:ss。<br/>
     * 使用方式：
     * @return String 输出格式：yyyy-MM-dd HH:mm:ss的字符串。
    */ 
    public static String getDateTime() {
        return DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
    
    /** 
     * 获取当前日期和时间<br/>
     * 详细描述：获取当前日期和时间，返回Timestamp类型，输出格式：yyyy-MM-dd HH:mi:ss.ff。<br/>
     * 使用方式：
     * @return Timestamp 输出格式：yyyy-MM-dd HH:mi:ss.ff的形式。
    */ 
    public static Timestamp getTimestamp() {
        return new java.sql.Timestamp(new java.util.Date().getTime());
    }

    /** 
     * 获取当前日期。<br/>
     * 详细描述：获取当前日期，返回String类型，输出格式：yyyy-MM-dd。<br/>
     * 使用方式：
     * @return String 输出格式：yyyy-MM-dd的字符串。
    */ 
    public static String getDate() {
        return LocalDate.now().toString();
    }

    /**
     * @Title: getSqlDate
     * @Description:
     * @return
     */
    /** 
     * 获取当前日期。<br/>
     * 详细描述：获取当前日志，返回Date类型，输出格式：yyyy-MM-dd。<br/>
     * 使用方式：
     * @return Date 输出格式为：yyyy-MM-dd。
    */ 
    public static Date getSqlDate() {
        return new java.sql.Date(new java.util.Date().getTime());
    }

    /** 
     * 获取当前时间<br/>
     * 详细描述：获取当前时间，返回String类型，输出格式：HH:mm:ss。<br/>
     * 使用方式：
     * @param String 输出格式为HH:mm:ss的字符串。
    */ 
    public static String getTime() {
        return LocalTime.now().toString("HH:mm:ss");
    }
}