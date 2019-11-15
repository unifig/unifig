package com.tools.plugin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Json字符串格式化
 *
 */
public class ForMatJSONStr {
	public static void main(String[] args) {
        String jsonStr = "{\"id\":\"1\",\"name\":\"a1\",\"obj\":{\"id\":11,\"name\":\"a11\",\"array\":[{\"id\":111,\"name\":\"a111\"},{\"id\":112,\"name\":\"a112\"}]}}";
        String fotmatStr = ForMatJSONStr.format(jsonStr);
        System.out.println(fotmatStr);
    }
     
	 /**
     * 格式化json
     * @param content
     * @return
     */
    public static String format(String content) {
        StringBuffer sb = new StringBuffer();
        int index = 0;
        int count = 0;
        while(index < content.length()){
            char ch = content.charAt(index);
            if(ch == '{' || ch == '['){
                sb.append(ch);
                sb.append('\n');
                count++;
                for (int i = 0; i < count; i++) {                   
                    sb.append('\t');
                }
            }
            else if(ch == '}' || ch == ']'){
                sb.append('\n');
                count--;
                for (int i = 0; i < count; i++) {                   
                    sb.append('\t');
                }
                sb.append(ch);
            } 
            else if(ch == ','){
                sb.append(ch);
                sb.append('\n');
                for (int i = 0; i < count; i++) {                   
                    sb.append('\t');
                }
            } 
            else {
                sb.append(ch);              
            }
            index ++;
        }
        return sb.toString();
    }
    /**
     * 把格式化的json紧凑
     * @param content
     * @return
     */
    public static String compactJson(String content) {
        String regEx="[\t\n]"; 
        Pattern p = Pattern.compile(regEx); 
        Matcher m = p.matcher(content);
        return m.replaceAll("").trim();
    }
}
