package etl.dispatch.base.enums;
import java.util.ArrayList;
import java.util.List;
/**
 * BaseExceptionEnums是一个基础异常枚举类，主要记录一些无需进行分类的异常代码，
 * 通过转换后的代码格式直接匹配国际化配置文件中的key值并获取其对应的中文描述，
 * 开发人员也无需在抛出异常的里面写大量的中文描述，为此该类还提供了一些基本的操作方法，
 * 例如：获取枚举类数字编码、获取所有枚举类等。
 * 
 *
 *
 */
public enum BaseExceptionEnums {
    /**
     * 在国际化配置文件中不存在对应的中文描述时，统一用该代码处理。
     */
    NO_MESSAGE("9999"),
    
    /**
     * 不存在缓存区域的代码。
     */
    NO_EXIST_CACHE("0001"),
    
    /**
     * 导出fucioncharts图片异常。
     */
    EXPORT_FCHART_FAIL("0002");

    private String code;
    
    /**
     * BaseExceptionEnums的构造器会自动把定义在枚举对象中的值赋给成员变量code，并通过调用getCode方法获取其值。
     * @param code 枚举对象中定义的代码值
     */
    BaseExceptionEnums(String code) {
        this.code = code;
    }
    
    /**
     * 获取枚举对象对应的code值。<br/>
     * 详细描述：获取BaseExceptionEnums中定义枚举对象的code值(例如：NO_MESSAGE("9999")中的9999)。<br/>
     * 使用方式：直接通过BaseExceptionEnums.NO_MESSAGE.getCode()获取对应的code值。
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
     * 详细描述：根据传入的枚举对象，进行格式化转换后可直接匹配国际化配置文件中对应的key值，即可获取中文描述信息。<br/>
     * 使用方式：直接通过BaseExceptionEnums.getPropertiesCode(BaseExceptionEnums.NO_MESSAGE)获取国际化配置文件中对应的key值。
     * @param type 枚举类中定义的枚举对象。
     * @return 国际化配置文件中对应的key值。
     */
    public static String getPropertiesCode(Object type) {
        String propertiesKey = "";
        if(type instanceof BaseExceptionEnums){
            propertiesKey = type.getClass().getSimpleName() + "." + ((BaseExceptionEnums)type).getCode();
        }
        return propertiesKey;
    }

    /**
     * 获取BaseExceptionEnums中所有枚举对象。<br/>
     * 详细描述：获取BaseExceptionEnums中所有枚举对象(例如：NO_MESSAGE("9999")中的NO_MESSAGE)。<br/>
     * 使用方式：直接通过BaseExceptionEnums.getAllBaseEnums()获取。
     * @return 该枚举类中所有枚举对象的集合。
     */
    public static List<Object> getAllBaseEnums() {
        List<Object> list = new ArrayList<Object>();
        for (BaseExceptionEnums e : BaseExceptionEnums.values()) {
            list.add(e);
        }
        return list;
    }

    /**
     * 获取BaseExceptionEnums中所有枚举对象的code。<br/>
     * 详细描述：获取BaseExceptionEnums中所有枚举对象的code(例如：NO_MESSAGE("9999")中的9999)。<br/>
     * 使用方式：直接通过BaseExceptionEnums.getAllBaseEnumsCode()获取。
     * @return 该枚举类中所有枚举对象的code集合。
     */
    public static List<Object> getAllBaseEnumsCode() {
        List<Object> list = new ArrayList<Object>();
        for (BaseExceptionEnums e : BaseExceptionEnums.values()) {
            list.add(e.code);
        }
        return list;
    }

    /**
     * 获取BaseExceptionEnums中所有格式化后的枚举对象。<br/>
     * 详细描述：获取BaseExceptionEnums中所有格式化后的枚举对象(格式为：类型.code)。<br/>
     * 使用方式：直接通过BaseExceptionEnums.getAllBaseEnumunifigCode()获取。
     * @return 该枚举类中所有枚举对象的集合(格式为：类型.code)。
     */
    public static List<Object> getAllBaseEnumunifigCode() {
        List<Object> list = new ArrayList<Object>();
        for (BaseExceptionEnums e : BaseExceptionEnums.values()) {
            list.add(e + "." + e.code);
        }
        return list;
    }
}