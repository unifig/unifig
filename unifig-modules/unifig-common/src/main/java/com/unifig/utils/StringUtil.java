package com.unifig.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**匹配&或全角状态字符或标点*/
    public static final String PATTERN="&|[\uFE30-\uFFA0]|‘’|“”";

    public static String replaceSpecialtyStr(String str,String pattern,String replace){
        if(isBlankOrNull(pattern))
            pattern="\\s*|\t|\r|\n";//去除字符串中空格、换行、制表
        if(isBlankOrNull(replace))
            replace="";
        return Pattern.compile(pattern).matcher(str).replaceAll(replace);

    }
    public static boolean isBlankOrNull(String str){
        if(null==str)return true;
        //return str.length()==0?true:false;
        return str.length()==0;
    }

    /**清除数字和空格*/
    public static  String cleanBlankOrDigit(String str){
        if(isBlankOrNull(str))return "null";
        return Pattern.compile("\\d|\\s").matcher(str).replaceAll("");
    }

    /**
     * 判断字符串是否包含中文
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
