package etl.dispatch.base.enums;
import java.util.ArrayList;
import java.util.List;
/**
 * SQLExceptionsEnums是一个SQL异常枚举类，主要记录一些数据库的异常代码，
 * 通过转换后的代码格式直接匹配国际化配置文件中的key值并获取其对应的中文描述，
 * 开发人员也无需在抛出异常的里面写大量的中文描述，为此该类还提供了一些基本的操作方法，
 * 例如：获取枚举类数字编码、获取所有枚举类等。
 * 
 *
 *
 */
public enum SQLExceptionsEnums {
    /**
     * mysql数据库中表不存在的代码。
     */
    TABLE_NOT_EXISTS("1146"),
    
    /**
     * mysql数据库中sql语句语法错误的代码。
     */
    SQL_ERROR("1046");

    private String code;
    
    /**
     * SQLExceptionsEnums的构造器会自动把定义在枚举对象中的值赋给成员变量code，并通过调用getCode方法获取其值。
     * @param code 枚举对象中定义的代码值
     */
    SQLExceptionsEnums(String code) {
        this.code = code;
    }
    
    /**
     * 获取枚举对象对应的code值。<br/>
     * 详细描述：获取SQLExceptionsEnums中定义枚举对象的code值(例如：TABLE_NOT_EXISTS("1146")中的1146)。<br/>
     * 使用方式：直接通过SQLExceptionsEnums.TABLE_NOT_EXISTS.getCode()获取对应的code值。
     * @return 枚举对象中的code值。
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 对应code成员变量的set方法。
     */
    public void setCode(String code) {
        this.code = code;
    }
    
    /**
     * 根据枚举类型获取国际化文件中对应的key。<br/>
     * 详细描述：根据传入的参数，进行格式化转换后可直接匹配国际化配置文件中对应的key值，即可获取中文描述信息。<br/>
     * 使用方式：直接通过SQLExceptionsEnums.getPropertiesCode("1146")获取国际化配置文件中对应的key值。
     * @param type 数据库中的错误代码。
     * @return 国际化配置文件中对应的key值。
     */
    public static String getPropertiesCode(Object type) {
        return "SQLExceptionsEnums." + type;
    }

    /**
     * 获取SQLExceptionsEnums中所有枚举对象。<br/>
     * 详细描述：获取SQLExceptionsEnums中所有枚举对象(例如：TABLE_NOT_EXISTS("1146")中的TABLE_NOT_EXISTS)。<br/>
     * 使用方式：直接通过SQLExceptionsEnums.getAllExceptionsEnums()获取。
     * @return 该枚举类中所有枚举对象的集合。
     */
    public static List<Object> getAllExceptionsEnums() {
        List<Object> list = new ArrayList<Object>();
        for (SQLExceptionsEnums e : SQLExceptionsEnums.values()) {
            list.add(e);
        }
        return list;
    }

    /**
     * 获取SQLExceptionsEnums中所有枚举对象的code。<br/>
     * 详细描述：获取SQLExceptionsEnums中所有枚举对象的code(例如：TABLE_NOT_EXISTS("1146")中的1146)。<br/>
     * 使用方式：直接通过SQLExceptionsEnums.getAllExceptionsEnumsCode()获取。
     * @return 该枚举类中所有枚举对象的code集合。
     */
    public static List<Object> getAllExceptionsEnumsCode() {
        List<Object> list = new ArrayList<Object>();
        for (SQLExceptionsEnums e : SQLExceptionsEnums.values()) {
            list.add(e.code);
        }
        return list;
    }

    /**
     * 获取SQLExceptionsEnums中所有格式化后的枚举对象。<br/>
     * 详细描述：获取SQLExceptionsEnums中所有格式化后的枚举对象(格式为：类型.code)。<br/>
     * 使用方式：直接通过SQLExceptionsEnums.getAllExceptionsEnumunifigCode()获取。
     * @return 该枚举类中所有枚举对象的集合(格式为：类型.code)。
     */
    public static List<Object> getAllExceptionsEnumunifigCode() {
        List<Object> list = new ArrayList<Object>();
        for (SQLExceptionsEnums e : SQLExceptionsEnums.values()) {
            list.add(e + "-" + e.code);
        }
        return list;
    }
}