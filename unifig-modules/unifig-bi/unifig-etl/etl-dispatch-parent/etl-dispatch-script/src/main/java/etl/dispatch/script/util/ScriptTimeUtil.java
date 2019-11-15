package etl.dispatch.script.util;

import java.util.Date;

import etl.dispatch.util.DateUtil;

/**
 * 脚本执行时间工具类
 *
 *
 */
public class ScriptTimeUtil {

	/**
	 * 获取当前时间的周一
	 * @return
	 */
	public static String optime_monday() {
		return DateUtil.getWeekMondayByDate(new Date(), 1);
	}
	
	/**
	 * 获取当时时间的周日
	 * @return
	 */
	public static String optime_sunday() {
		return DateUtil.getWeekSundayByDate(new Date(), 1);
	}
	
	/**
	 * 获取昨天日期
	 * @return
	 */
	public static String optime_yesday(){
		return DateUtil.getSysStrCurrentDate("yyyyMMdd", -1);
	}
	
	/**
	 * 获得上个月日期
	 * @return
	 */
	public static String optime_lastmonth(){
		return DateUtil.getYearMonth(new Date(), -1*1);
	}
	
	/**
	 * 获得前几个月日期
	 * @return
	 */
	public static String optime_befor_month(int i){
		return DateUtil.getYearMonth(new Date(), -1*i);
	}
	
	/**
	 * 查询传入时间的月第一天
	 * @return
	 */
	public static String optime_month_first(){
		return DateUtil.getFirstDayByDate(new Date(), -1*1);
	}
	
	/**
	 * 查询传入时间的月最后一天
	 * @return
	 */
	public static String optime_month_last(){
		return DateUtil.getLastDayByDate(new Date(),  -1*1);
	}
}
