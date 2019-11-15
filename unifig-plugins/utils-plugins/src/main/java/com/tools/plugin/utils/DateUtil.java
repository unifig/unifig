package com.tools.plugin.utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Title: 日期、时间工具类
 */
public class DateUtil extends DateUtils {
    public final static long DATE_MILLIS = 60 * 60 * 24 * 1000;

    public final static String DAY_HAVINTERVAL = "yyyy-MM-dd";

    public final static String DAY_UNINTERVAL = "yyyyMMdd";

    public final static String MONTH_HAVINTERVAL = "yyyy-MM";

    public final static String MONTH_UNINTERVAL = "yyyyMM";

    public final static String TIME_HAVINTERVAL = "yyyy-MM-dd HH:mm:ss";

    public final static String TIME_UNINTERVAL = "yyyymmdd hh:mm:ss";
    
    /**
     * 生日算年龄
     * @param birthday
     * @return
     */
	public static int getAgeByBirth(Date birthday) {
		int age = 0;
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());// 当前时间

			Calendar birth = Calendar.getInstance();
			birth.setTime(birthday);

			if (birth.after(now)) {// 如果传入的时间，在当前时间的后面，返回0岁
				age = 0;
			} else {
				age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
				if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
					age += 1;
				}
			}
			return age;
		} catch (Exception e) {// 兼容性更强,异常后返回数据
			return 0;
		}
	}
    

    /**
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws Exception {
    	String dt_times_slice ="7,30,60";
    	String[] timesSlice = dt_times_slice.split(",");
		for (String timeSlice : timesSlice) {
			int intSlice = NumberUtils.intValue(timeSlice);
			for (int i = 1; i <= intSlice; i++) {
				System.out.println(timeSlice+ ":"+DateUtil.getSysStrCurrentDate("yyyyMMdd",-1*i));
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date bithday = format.parse("1994-08-23 17:20:20");
        
    	System.out.println(DateUtil.getAgeByBirth(bithday));
    	System.out.println(DateUtil.getSysStrCurrentDate("yyyyMMdd", -7));
    	
    	System.out.println(format(getDateTime("20170629",DAY_UNINTERVAL,-1)));
    	System.out.println(getMondayByDate(new Date()));
    	System.out.println(getSundayByDate(new Date()));
    	
    	System.out.println(getWeekMondayByDate(new Date(),1));
    	System.out.println(getWeekSundayByDate(new Date(),1));
    	
    	System.out.println(getWeekMondayByDate(new Date(),2));
    	System.out.println(getWeekSundayByDate(new Date(),2));
    	
     	System.out.println(getDateInMonth("2017-07-01",2));
     	
     	System.out.println(getFirstDayByDate(new Date()));
     	System.out.println(getLastDayByDate(new Date()));
     	
     	System.out.println(getFirstDayByDate(new Date(), -1*2));
     	System.out.println(getLastDayByDate(new Date(), -1*2));
     	
     	System.out.println(format(Long.valueOf("1501138887219")));
     	
     	if (Long.valueOf("1501062389447") >= DateUtil.getDateTime(DateUtil.getSysStrCurrentDate("yyyy-MM-dd", -1) + " 00:00:00", "yyyy-MM-dd HH:mm:ss")) {
			System.out.println("new");
		} else {
			System.out.println("old");
		}
     	
    	System.out.println(format(getDateTime("20170723","yyyyMMdd",-1)));
    	
    	System.out.println(getDateTime("20170722","yyyyMMdd",-1));
    	System.out.println(getDateTime("20170723","yyyyMMdd",-1));
     	
     	
    }
    
    /**
     * 查询传入时间的周一
     * @param time
     * @return
     */
	public static String getMondayByDate(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		String monday = sdf.format(cal.getTime());
		return monday.replace("-", "");
	}
	
	/**
	 * 查询传入时间的上周周一
	 * @param time
	 * @return
	 */
	public static String getWeekMondayByDate(Date time, int weekOrder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 设置时间格式
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		Date date = getDate(sdf.format(cal.getTime()), "yyyyMMdd");
		date = addDay(date, -7 * weekOrder);
		return sdf.format(date);
	} 
	
	/**
	 * 查询传入时间的周日
	 * @param time
	 * @return
	 */
	public static String getSundayByDate(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, 6);
		String sunday = sdf.format(cal.getTime());
		return sunday.replace("-", "");
	}  
	
	/**
	 * 查询传入时间的上周周日
	 * @param time
	 * @return
	 */
	public static String getWeekSundayByDate(Date time, int weekOrder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 设置时间格式
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, 6);

		Date date = getDate(sdf.format(cal.getTime()), "yyyyMMdd");
		date = addDay(date, -7 * weekOrder);
		return sdf.format(date);
	}  
	
	 /**
     * 查询传入时间的月第一天
     * @param time
     * @return
     */
	public static String getFirstDayByDate(Date time, int monthOrder) {
		Date date = addMonth(time, monthOrder);
		return getFirstDayByDate(date);
	}
    
	 /**
     * 查询传入时间的月最后一天
     * @param time
     * @return
     */
	public static String getLastDayByDate(Date time, int monthOrder) {
		Date date = addMonth(time, monthOrder);
		return getLastDayByDate(date);
	}
	
	 /**
     * 查询传入时间的月第一天
     * @param time
     * @return
     */
	public static String getFirstDayByDate(Date time) {
		String month = getYearMonth(time);
		return getFirstDayByTime(month,DateUtil.MONTH_UNINTERVAL,DAY_UNINTERVAL);
	}
    
	 /**
     * 查询传入时间的月最后一天
     * @param time
     * @return
     */
	public static String getLastDayByDate(Date time) {
		String month = getYearMonth(time);
		return getLastDayByTime(month,DateUtil.MONTH_UNINTERVAL,DAY_UNINTERVAL);
	}
	
	
	/**
	 * 得到日期时间字符串 精确到秒
	 */
	public static String format(Long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date(time));
	}

	/**
	 * long转date
	 * @param time
	 * @return
	 */
	public static Date format2Date(Long time) {
		Date date = new Date(time);
		return date;
	}
	
	/**
	 * 得到日期时间字符串 精确到秒
	 */
	public static String format() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}
	
    /**
     * 根据传入时间，返回日期时间
     * @param strTime
     * @return
     */
    public static String getCurDateTime(String strTime) {
        if (strTime == null) {
            strTime = "";
        }
        long lCurDate = System.currentTimeMillis();
        Date dCurDate = new Date(lCurDate);
        String strCurDate = DateFormat.getDateInstance().format(dCurDate);
        String strRetrun = strCurDate + " " + strTime;
        return strRetrun;
    }

    /**
     * 日期上加一天
     * @param strDateTime
     * @param count
     * @return
     */
	public static long getDateTime(String strDateTime, String pattern, int count) {
		long time = getDateTime(strDateTime, pattern);
		if (time > 0l) {
			Date day = format2Date(time);
			day = addDay(day, count);
			return day.getTime();
		}
		return 0l;
	}
	
    /**
     * 将字符串转换为long 类型
     * @param strDateTime
     * @return
     */
	public static long getDateTime(String strDateTime, String pattern) {
		if (!StringUtil.isNullOrEmpty(strDateTime) && !StringUtil.isNullOrEmpty(pattern)) {
			return getDate(strDateTime, pattern).getTime();
		}
		return 0l;
	}

    /**
     * 得到系统时间
     * @return
     * @throws Exception
     */
    public static Date getDate() {
        Date date = null;
        Calendar myDate = Calendar.getInstance();
        myDate.setTime(new java.util.Date());
        date = myDate.getTime();
        return date;
    }

    /**
     * 根据格式获取日期型数�?
     * @param str
     * @param pattern
     * @return
     */
    public static Date getDate(String str, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat();

        if (str == null || str.equals("")) {
            return null;
        }
        sdf.applyPattern(pattern);
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
        }
        return date;
    }

    /**
     * 根据格式获取日期型数�?
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(pattern);
        return sdf.format(date);
    }

    /**
     * 查询系统当前时间，时区为中国标准时间
     * @param format 格式如：yyyy-MM-dd HH:mm:ss
     * @return 返回类型为String
     */
    public static String getSysStrCurrentDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE);
        Date date = new Date();
        String currentDate = sdf.format(date);
        return currentDate;
    }
    
    /**
     * 查询当前时间加上天，时区为中国标准时间
     * @param format
     * @param count
     * @return
     */
    public static String getSysStrCurrentDate(String format, int count) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE);
        Date date = new Date();
        date = addDay(date, count);
        String currentDate = sdf.format(date);
        return currentDate;
    }
    
    /**
     * 不带前缀0的日期
     * @return
     */
	public static String getSysStrCurrentDate() {
		String year = getYear(new Date());
		String month = getMonth(new Date());
		String day = getDay(new Date());
		if (month.startsWith("0")) {
			month = month.substring(1);
		}
		if (day.startsWith("0")) {
			day = day.substring(1);
		}
		return year + month + day;
	}

    /**
     * 查询系统当前时间，时区为中国标准时间,返回类型为Date *
     * @param format 格式如：yyyy-MM-dd HH:mm:ss *
     * @return 返回类型为Date
     */
    public static Date getSysDateCurrentDate(String format) {
        String curDateString = getSysStrCurrentDate(format);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE);
        Date currentDate = null;
        try {
            currentDate = sdf.parse(curDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentDate;
    }

    /**
     * 得到当前系统时间戳
     * @return java.sql.Timestamp
     */
    public static Timestamp getSysCurrentTimestamp() {
        return new java.sql.Timestamp(new java.util.Date().getTime());
    }

    /**
     * 把一个日期格式转换成另一个日期格式
     * @param dateTime     String
     * @param srcFromat    String
     * @param targetFormat String
     * @return String
     */
    @SuppressWarnings("null")
    public static String transformStrFormat(String dateTime, String srcFromat, String targetFormat) {
        String s = null;
        try {
            if (dateTime != null || dateTime.trim().length() > 0) {
                s = formatDateToStr(formatStrToDate(dateTime, srcFromat), targetFormat);
            } else {
                s = s + "";
                s = s.trim();
            }
        } catch (Exception ex) {
            s = dateTime;
        }
        return s;
    }

    /**
     * 把时间串按照响应的格式转换成日期对象
     * @param dateTime 时间串
     * @param format   指定的格式
     * @return 返回java.util.Date的对象, 转换失败时返回当前的时间对象
     */
    public static java.util.Date formatStrToDate(String dateTime, String format) throws Exception {
        if (dateTime == null || format == null) {
            {
                throw new Exception("日期转换失败:dateStr or format is null");
            }
        }
        try {
            SimpleDateFormat dateFormater = new SimpleDateFormat(format);
            return dateFormater.parse(dateTime);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 将Date类型的日期转换成String类型
     * @param date   传入的时间
     * @param format 格式，例如yyyy-MM-dd
     * @return 以"yyyy-MM-dd"格式返回日期类型
     */
    public static String formatDateToStr(java.util.Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String dateOfStr = "";
        try {
            dateOfStr = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateOfStr;
    }

    /**
     * 传入两个日期，计算这两个日期的差值天数(日期值小的参数在前)
     * @param startDate 前一个日期值
     * @param endDate   后一个日期值
     * @return 传入两个日期，计算这两个日期的差值天数(日期值小的参数在前)
     */
    public static int getIntervalDays(java.sql.Date startDate, java.sql.Date endDate) {
        long startdate = startDate.getTime();
        long enddate = endDate.getTime();
        long interval = enddate - startdate;
        int intervaldays = (int) (interval / DATE_MILLIS);
        return intervaldays;
    }

    /**
     * 传入两个日期，计算这两个日期的差值天数(日期值小的参数在前)
     * @param startDate 前一个日期值
     * @param endDate   后一个日期值
     * @return 传入两个日期，计算这两个日期的差值天数(日期值小的参数在前)
     */
    public static long getIntervalDaysForUtilDate(java.util.Date startDate, java.util.Date endDate) {
        long startdate = startDate.getTime();
        long enddate = endDate.getTime();
        long interval = enddate - startdate;
        long intervaldays = interval / DATE_MILLIS;
        return intervaldays;
    }

    // ------------------------------------在日期时间上增加/减少数量---------------------------------------------//

    /**
     * 在日期上增加年
     * @param date
     * @param count
     * @return
     */
    public static Date addYear(Date date, int count) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.YEAR, count);
        return gc.getTime();
    }

    /**
     * 在日期上增加月
     * @param date
     * @param count
     * @return
     */
    public static Date addMonth(Date date, int count) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.MONTH, count);
        return gc.getTime();
    }

    /**
     * 在日期上增加天
     * @param date
     * @param count
     * @return
     */
	public static Date addDay(Date date, int count) {
		if (count > 0 || count < 0) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date);
			gc.add(Calendar.DATE, count);
			return gc.getTime();
		} else {
			return date;
		}
	}

    /**
     * 在日期上增加小时
     * @param date
     * @param count
     * @return
     */
    public static Date addHour(Date date, int count) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.HOUR, count);
        return gc.getTime();
    }

    /**
     * 在日期上增加分钟
     * @param date
     * @param count
     * @return
     */
    public static Date addMinute(Date date, int count) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.MINUTE, count);
        return gc.getTime();
    }

    /**
     * 在日期上增加秒
     * @param date
     * @param count
     * @return
     */
    public static Date addSecond(Date date, int count) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.SECOND, count);
        return gc.getTime();
    }

    /**
     * 在日期上增加年
     * @param dateTime
     * @param informate
     * @param outformate
     * @param flag
     * @return
     */
    public static String addYear(String dateTime, String informate, String outformate, Integer flag) {
        Date dataDate = null;
        try {
            dataDate = formatStrToDate(dateTime, informate);
        } catch (Exception e) {
            e.printStackTrace();

        }
        dataDate = DateUtil.addYear(dataDate, flag);
        return formatDateToStr(dataDate, outformate);
    }

    /**
     * 在日期上增加月
     * @param dateTime
     * @param informate
     * @param outformate
     * @param flag
     * @return
     */
    public static String addMonth(String dateTime, String informate, String outformate, Integer flag) {
        Date dataDate = null;
        try {
            dataDate = formatStrToDate(dateTime, informate);
        } catch (Exception e) {
            e.printStackTrace();

        }
        dataDate = DateUtil.addMonth(dataDate, flag);
        return formatDateToStr(dataDate, outformate);
    }

    /**
     * 在日期上增加天
     * @param dateTime
     * @param informate
     * @param outformate
     * @param flag
     * @return
     */
    public static String addDay(String dateTime, String informate, String outformate, Integer flag) {
        Date dataDate = null;
        try {
            dataDate = formatStrToDate(dateTime, informate);
        } catch (Exception e) {
            e.printStackTrace();

        }
        dataDate = DateUtil.addDay(dataDate, flag);
        return formatDateToStr(dataDate, outformate);
    }

    /**
     * 在日期上增加小时
     * @param dateTime
     * @param informate
     * @param outformate
     * @param flag
     * @return
     */
    public static String addHour(String dateTime, String informate, String outformate, Integer flag) {
        Date dataDate = null;
        try {
            dataDate = formatStrToDate(dateTime, informate);
        } catch (Exception e) {
            e.printStackTrace();

        }
        dataDate = DateUtil.addHour(dataDate, flag);
        return formatDateToStr(dataDate, outformate);
    }

    /**
     * 在日期上增加分钟
     * @param dateTime
     * @param informate
     * @param outformate
     * @param flag
     * @return
     */
    public static String addMinute(String dateTime, String informate, String outformate, Integer flag) {
        Date dataDate = null;
        try {
            dataDate = formatStrToDate(dateTime, informate);
        } catch (Exception e) {
            e.printStackTrace();

        }
        dataDate = DateUtil.addMinute(dataDate, flag);
        return formatDateToStr(dataDate, outformate);
    }

    /**
     * 在日期上增加秒
     * @param dateTime
     * @param informate
     * @param outformate
     * @param flag
     * @return
     */
    public static String addSecond(String dateTime, String informate, String outformate, Integer flag) {
        Date dataDate = null;
        try {
            dataDate = formatStrToDate(dateTime, informate);
        } catch (Exception e) {
            e.printStackTrace();

        }
        dataDate = DateUtil.addSecond(dataDate, flag);
        return formatDateToStr(dataDate, outformate);
    }

    // ------------ Date comparison wrapper ----------//

    /**
     * 获取指定时间所在月的第几天
     * @param currentTime 日期 yyyy-MM-dd
     * @param seq         第几天
     * @return
     */
    public static String getDateInMonth(String currentTime, int seq) {
        if (currentTime == null || seq < 1) {
            return null;
        }
        String date = null;
        if (currentTime != null && currentTime.trim().length() == 10) {
            date = currentTime.substring(0, 8);
            if (seq < 10) {
                date = date + "0" + seq;
            } else {
                date = date + seq;
            }
        }
        return date;
    }

    /**
     * 获取指定时间所在月的第一天
     * @param dateTime 当前日期
     * @return 返回格式 yyyyMMdd
     */
    public static String getFirstDayByTime(String dateTime, String inFormat, String outFormat) {
        // 获取转换后的时间
        String transformTime = DateUtil.transformStrFormat(dateTime, inFormat, DateUtil.DAY_UNINTERVAL);
        if (transformTime != null && transformTime.trim().length() == 8) {
            transformTime = transformTime.substring(0, 6) + "01";
        }
        // 把当前月第一天时间转换格式返回
        transformTime = DateUtil.transformStrFormat(transformTime, DateUtil.DAY_UNINTERVAL, outFormat);
        return transformTime;
    }

    /**
     * 获取指定时间所在月的月末一天
     * @param dateTime 当前日期
     * @return 返回格式 yyyyMMdd
     */
    public static String getLastDayByTime(String dateTime, String inFormat, String outFormat) {
        // 获取转换后的月时间
        String transformTime = DateUtil.transformStrFormat(dateTime, inFormat, DateUtil.DAY_UNINTERVAL);
        if (transformTime != null && transformTime.trim().length() == 8) {
            transformTime = transformTime.substring(0, 6);
        }
        // 当前月的月末一天
        transformTime = getLastDayByMonth(transformTime, DateUtil.MONTH_UNINTERVAL, outFormat);
        return transformTime;
    }

    /**
     * 根据指定的年月获取当月的第一天
     * @param yearMonth
     * @param inFormat  输入格式 默认为yyyy-MM,yyyyMM
     * @param outFormat 输出格式,默认为yyyy-MM-dd ,yyyyMMdd
     * @return
     */
    public static String getFirstDayByMonth(String yearMonth, String inFormat, String outFormat) {
        String outTime = null;
        if (inFormat == null || inFormat.trim().equalsIgnoreCase("")) {
            inFormat = DateUtil.MONTH_HAVINTERVAL;
        }
        if (outFormat == null || outFormat.trim().equalsIgnoreCase("")) {
            outFormat = DateUtil.DAY_HAVINTERVAL;
        }
        try {
            SimpleDateFormat sf = new SimpleDateFormat(inFormat);
            Date date = sf.parse(yearMonth);
            Calendar cTime = Calendar.getInstance();
            cTime.setTime(date);
            outTime = formatDateToStr(cTime.getTime(), outFormat);
        } catch (ParseException pExp) {
            pExp.printStackTrace();
        }
        return outTime;
    }

    /**
     * 获取指定的年月的月最后一天
     * @param yearMonth
     * @param inFormat  输入格式 默认为yyyy-MM
     * @param outFormat 输出格式,默认为yyyy-MM-dd
     * @return
     */
    public static String getLastDayByMonth(String yearMonth, String inFormat, String outFormat) {
        String outTime = null;
        if (inFormat == null || inFormat.trim().equalsIgnoreCase("")) {
            inFormat = DateUtil.MONTH_HAVINTERVAL;
        }
        if (outFormat == null || outFormat.trim().equalsIgnoreCase("")) {
            outFormat = DateUtil.DAY_HAVINTERVAL;
        }
        try {
            SimpleDateFormat sf = new SimpleDateFormat(inFormat);
            Date date = sf.parse(yearMonth);
            Calendar cTime = Calendar.getInstance();
            cTime.setTime(date);
            cTime.add(Calendar.MONTH, 1);
            cTime.set(Calendar.DATE, 0);
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTime(new Date());
            if ((nowTime.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) && (nowTime.get(Calendar.MONTH) == cTime.get(Calendar.MONTH))) {
                outTime = formatDateToStr(nowTime.getTime(), outFormat);
            } else {
                outTime = formatDateToStr(cTime.getTime(), outFormat);
            }
        } catch (ParseException pExp) {
            pExp.printStackTrace();
        }
        return outTime;
    }

    /**
     * 由年份和月份构造日期对象
     * @return Date
     */
    public static Date getDate(int year, int month) {
        // 如果年份或月份小于1，那么显然是不可能的，返回 null
        if (year < 1 || month < 1)
            return null;
        Date date = null;
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
        // 按照 yearMonthFormat 定义的格式把参数传化成字符串
        String dateStr = new Integer(year).toString() + "-" + new Integer(month).toString();
        try {
            // 把字符串类型的时间转化成日期类型
            date = yearMonthFormat.parse(dateStr);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }

    /**
     * 由年份、月份、日期构造日期对象
     * @param year  the year like 1900.
     * @param month the month between 1-12.
     * @param day   the day of the month between 1-31.
     * @return Date
     */
    public static Date getDate(int year, int month, int day) {
        // 如果年份、月份或日期小于1，那么显然是不可能的，返回 null
        if (year < 1 || month < 1 || day < 1)
            return null;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.DAY_HAVINTERVAL);
        // 按照 dateFormat 定义的格式把参数传化成字符串
        String dateStr = new Integer(year).toString() + "-" + new Integer(month).toString() + "-" + new Integer(day).toString();
        try {
            // 把字符串类型的时间转化成日期类型
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }

    /**
     * 获得某月最后一天的日期
     * @param date
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Date getLastDayDateOfTheMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date lastDate = calendar.getTime();
        lastDate.setDate(lastDay);
        return lastDate;
    }

    /**
     * 获得某月的天数
     * @param dateOfString
     * @return
     */
    public static int getLastDayOfTheMonth(String dateOfString) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat(DateUtil.MONTH_HAVINTERVAL);
        Date date = null;
        try {
            date = yearMonthFormat.parse(dateOfString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        final int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return lastDay;
    }

    /**
     * 获得某日期的年份
     * @param date
     * @return
     */
    public static String getYear(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        return yearFormat.format(date);
    }

    /**
     * 获得某日期的月份
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat yearFormat = new SimpleDateFormat("MM");
        return yearFormat.format(date);
    }

    /**
     * 获得某日期的年份月份
     * @param date
     * @return
     */
    public static String getYearMonth(Date date, int monthOrder) {
    	Date times = addMonth(date, monthOrder);
    	return getYearMonth(times);
    }
    /**
     * 获得某日期的年份月份
     * @param date
     * @return
     */
    public static String getYearMonth(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat yearFormat = new SimpleDateFormat(DateUtil.MONTH_UNINTERVAL);
        return yearFormat.format(date);
    }
    
    /**
     * 获得某日期是几号
     * @param date
     * @return
     */
    public static String getDay(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat yearFormat = new SimpleDateFormat("dd");
        return yearFormat.format(date);
    }

    /**
     * 将一个日期字符串转化成Calendar 字符串格式为yyyy-MM-dd
     * @param strDate
     * @param inFormat
     * @return
     */
    public static Calendar parseStringToCalendar(String strDate, String inFormat) {
        try {
            java.util.Date date = formatStrToDate(strDate, inFormat);
            if (date == null) {
                return null;
            }
            Calendar ca = Calendar.getInstance();
            ca.setTime(date);
            return ca;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 传入两个月份字符串，查询中间包含的月份
     * @param startMonth 开始月份,格式为"yyyyMM"
     * @param endMonth   结束月份,格式为"yyyyMM"
     * @return String[] 月份字符数组,格式为"yyyyMM"
     * @created 2009-07-30 16:39:58
     */
    public static String[] getIntervalMonths(String startMonth, String endMonth, String inFormat) {
        String startTime = "";
        String endTime = "";
        if (startMonth != null && startMonth.length() > 5) {
            startTime = startMonth.substring(0, 4) + "-" + startMonth.substring(4, 6) + "-01";
        } else {
            return null;
        }
        Calendar startCal = parseStringToCalendar(startTime, inFormat);
        int startYear = startCal.get(Calendar.YEAR);
        int startMon = startCal.get(Calendar.MONTH) + 1;
        if (endMonth != null && endMonth.length() > 5) {
            endTime = endMonth.substring(0, 4) + "-" + endMonth.substring(4, 6) + "-01";
        } else {
            return null;
        }
        Calendar endCal = parseStringToCalendar(endTime, inFormat);
        int endYear = endCal.get(Calendar.YEAR);
        int endMon = endCal.get(Calendar.MONTH) + 1;
        try {
            Integer monthSize = (endYear - startYear) * 12 + endMon - startMon + 1;
            if (monthSize > 0) {
                String[] months = new String[monthSize];
                Calendar startCa = startCal;
                for (int i = 0; i < monthSize; i++) {
                    if (i == 0)
                        startCa.add(Calendar.MONTH, 0);
                    else
                        startCa.add(Calendar.MONTH, 1);
                    String yearMonth = String.valueOf(startCa.get(Calendar.YEAR));
                    int month = startCa.get(Calendar.MONTH) + 1;
                    if (month < 10) {
                        yearMonth += "0" + String.valueOf(month);
                    } else {
                        yearMonth += String.valueOf(month);
                    }
                    months[i] = yearMonth;
                }
                return months;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得某个时间前n月，格式为"yyyymm" 2009-11-20添加
     * @throws ParseException
     */
    @SuppressWarnings("unused")
    public static String getBeforeMonth(String str, int n) throws ParseException {
        if (StringUtil.isNullOrEmpty(str))
            return null;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
        Date date = df.parse(str);
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        if (ca == null) {
            return null;
        }
        ca.add(Calendar.MONTH, -n);
        String strDate = String.valueOf(ca.get(Calendar.YEAR));
        int intMonth = ca.get(Calendar.MONTH) + 1;
        if (intMonth < 10) {
            strDate += "0" + intMonth;
        } else {
            strDate += intMonth;
        }
        return strDate;
    }

    /**
     * 取得上年同月，格式为"yyyymm" 2009-11-20添加
     * @throws ParseException
     */
    public static String getSameMonthInLastYear(String str) throws ParseException {
        return getBeforeMonth(str, 12);
    }

    /**
     * 获得当年的第一个月，返回格式如"200808" 2009-11-23 add
     * @return
     */
    public static String getFirstMonthOfYear() {
        Date date = new Date();
        String year = DateUtil.getYear(date);
        String month = "01";
        return year + month;
    }

    /**
     * 获得某日期的年份缩写,如2009年,则返回09
     * @param date
     * @return
     */
    public static String getYear4Short(Date date) {
        String yearStr = getYear(date);
        if (StringUtils.isNotEmpty(yearStr)) {
            return yearStr.substring(2);
        }
        return null;
    }

    /**
     * 依据时间查询第几周
     * @param date   日期
     * @param format 如yyyy-MM-dd
     * @return 返回本年的第几周，如2009-11-01为第45周,则返回200945
     */
    public static int getWeekOrderByDate(String date, String format) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(formatStrToDate(date, format));
            Integer n = cal.get(Calendar.WEEK_OF_YEAR);
            Integer year = cal.get(Calendar.YEAR);
            String letter = n + "";
            if (n.toString().length() == 1)
                letter = "0" + n;
            if (cal.get(Calendar.MONTH) == 11 && "01".equals(letter))
                year++;
            return Integer.valueOf(year + "" + letter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 年，周，及周几返回具体时间
     * @param year       2009
     * @param weekOfYear 52
     * @param dayOfWeek  最好传入的时候用Calendar.MONDAY
     * @return 以yyyy-MM-dd形式返回日期串 功能:通过传入年份、一年中的周、星期几返回当天的日期
     */
    public static String getDayOfWeek(Integer year, Integer weekOfYear, Integer dayOfWeek) {
        DateFormat df = new SimpleDateFormat(DateUtil.DAY_HAVINTERVAL);
        Calendar rightNow = Calendar.getInstance();
        rightNow.set(Calendar.YEAR, year);
        rightNow.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        rightNow.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return df.format(rightNow.getTime());
    }

    /**
     * @param year       2009
     * @param weekOfYear 52
     * @param dayOfWeek  最好传入的时候用Calendar.MONDAY
     * @return 以yyyy-MM-dd形式返回日期串 功能:通过传入年份、当前周数,返回下一周星期几返回当天的日期
     */
    public static String getDayOfNextWeek(Integer year, Integer weekOfYear, Integer dayOfWeek, Integer flag) {
        DateFormat df = new SimpleDateFormat(DateUtil.DAY_HAVINTERVAL);
        Calendar rightNow = Calendar.getInstance();
        rightNow.set(Calendar.YEAR, year);
        rightNow.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        rightNow.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        if (null == flag)
            rightNow.add(Calendar.DATE, 7);
        else
            rightNow.add(Calendar.DATE, -7);
        return df.format(rightNow.getTime());
    }

    /**
     * @param weekOrder
     * @param flag,要求:weekOrder符合yyyy**格式
     * @return 通过录入周ID, flag, 返回上周或下周的周ID, 如输入200951, null, 则返回200952;录入200951,flag,  则返回200950
     * @throws Exception
     */
    public static int getNextWeekOrderByWeekOrder(Integer weekOrder, Integer flag) throws Exception {
        if ((weekOrder + "").length() != 6) {
            throw new Exception("不符合指定的6位格式");
        }
        int year = Integer.valueOf((weekOrder + "").substring(0, 4));
        int weekOfyear = Integer.valueOf((weekOrder + "").substring(4, 6));
        if (null == flag) {// 向下计算
            return getWeekOrderByDate(getDayOfNextWeek(year, weekOfyear, Calendar.SATURDAY, null), DateUtil.DAY_HAVINTERVAL);
        } else {
            return getWeekOrderByDate(getDayOfNextWeek(year, weekOfyear, Calendar.SATURDAY, 2), DateUtil.DAY_HAVINTERVAL);
        }
    }

    /**
     * 得到当前时间的java.sql.Date对象
     * @return
     */
    public static java.sql.Date getSysCurrentSQLDate() {
        return new java.sql.Date(new java.util.Date().getTime());
    }

    /**
     * @param date
     * @return 功能:将util的Date转化为sql的Date
     */
    public static java.sql.Date utilDate2SQLDate(java.util.Date date) {
        if (date == null) {
            return getSysCurrentSQLDate();
        }
        return new java.sql.Date(date.getTime());
    }

    /**
     * 将java.util.Date转换为Calendar
     * @param date
     * @return
     */
    public static Calendar parseDateToCalendar(Date date) {
        if (date == null) {
            return null;
        }
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca;
    }

    /**
     * @param dateStr
     * @param format
     * @return 返回季度序号
     * 功能:如输入的日期在1-3月，返回YYYY01,在4-6月，返回YYYY02,输入的日期在7-9月，返回YYYY03,输入的月份在10-12月，返回YYYY04
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String getSeasonIdByDate(String dateStr, String format) {
        try {
            String month = DateUtil.getMonth(DateUtil.formatStrToDate(dateStr, format));
            List springList = new ArrayList();
            springList.add("01");
            springList.add("02");
            springList.add("03");
            List summerList = new ArrayList();
            summerList.add("04");
            summerList.add("05");
            summerList.add("06");
            List autumnList = new ArrayList();
            autumnList.add("07");
            autumnList.add("08");
            autumnList.add("09");
            List winterList = new ArrayList();
            winterList.add("10");
            winterList.add("11");
            winterList.add("12");
            if (springList.contains(month)) {
                return DateUtil.getYear(DateUtil.formatStrToDate(dateStr, format)) + "01";
            } else if (summerList.contains(month)) {
                return DateUtil.getYear(DateUtil.parseDate(dateStr, format)) + "02";
            } else if (autumnList.contains(month)) {
                return DateUtil.getYear(DateUtil.parseDate(dateStr, format)) + "03";
            } else
                return DateUtil.getYear(DateUtil.parseDate(dateStr, format)) + "04";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param seasonId 季节ID
     * @param format   返回的日期格式
     * @return 功能:通过输入季节序号，返回该季节的开始日期，如200901(春季),则返回2009-01-01
     */
    public static String getBeginDateBySeasonId(String seasonId, String format) {
        if ("01".equalsIgnoreCase(seasonId.substring(4, 6))) {// 春
            return DateUtil.getFirstDayByMonth(seasonId.substring(0, 4) + "01", DateUtil.MONTH_UNINTERVAL, format);
        } else if ("02".equalsIgnoreCase(seasonId.substring(4, 6))) {// 夏
            return DateUtil.getFirstDayByMonth(seasonId.substring(0, 4) + "04", DateUtil.MONTH_UNINTERVAL, format);
        } else if ("03".equalsIgnoreCase(seasonId.substring(4, 6))) {// 秋
            return DateUtil.getFirstDayByMonth(seasonId.substring(0, 4) + "07", DateUtil.MONTH_UNINTERVAL, format);
        } else {// 冬
            return DateUtil.getFirstDayByMonth(seasonId.substring(0, 4) + "10", DateUtil.MONTH_UNINTERVAL, format);
        }
    }

    /**
     * @param seasonId
     * @param format
     * @return 功能:通过输入季节序号，返回该季节的结束日期，如200901(春季),则返回2009-03-31
     */
    public static String getLastDateBySeasonId(String seasonId, String format) {
        if ("01".equalsIgnoreCase(seasonId.substring(4, 6))) {// 春
            return DateUtil.getLastDayByMonth(seasonId.substring(0, 4) + "03", DateUtil.MONTH_UNINTERVAL, format);
        } else if ("02".equalsIgnoreCase(seasonId.substring(4, 6))) {// 夏
            return DateUtil.getLastDayByMonth(seasonId.substring(0, 4) + "06", DateUtil.MONTH_UNINTERVAL, format);
        } else if ("03".equalsIgnoreCase(seasonId.substring(4, 6))) {// 秋
            return DateUtil.getLastDayByMonth(seasonId.substring(0, 4) + "09", DateUtil.MONTH_UNINTERVAL, format);
        } else {// 冬
            return DateUtil.getLastDayByMonth(seasonId.substring(0, 4) + "12", DateUtil.MONTH_UNINTERVAL, format);
        }
    }

    /**
     * @param seasonId :季节ID
     * @param flag     :如果为null,表示向前一个季度，否则向后一个季度
     * @return
     */
    public static String getNextSeasonByPreSeasonId(String seasonId, String flag) {
        try {
            String date = getLastDateBySeasonId(seasonId, DateUtil.DAY_HAVINTERVAL);
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtil.formatStrToDate(date, DateUtil.DAY_HAVINTERVAL));
            cal.add(Calendar.MONTH, flag == null ? 3 : -3);
            java.util.Date uDate = cal.getTime();
            return getSeasonIdByDate(formatDateToStr(uDate, DateUtil.DAY_HAVINTERVAL), DateUtil.DAY_HAVINTERVAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // -----------------malinfei------------------------

    /**
     * @param date 时间字符串
     * @param n    对date 月份的操作月数 为正数 就是向后加N个月 为负数就是向前+n个月
     * @return 返回月份格式为yyyyMM
     */
    @SuppressWarnings("null")
    public static String getStringOperMonth(String date, String inFormat, int n) {
        if (date == null && date.equals("")) {
            return null;
        }
        date = date.replaceAll("-", "");
        StringBuffer sb = new StringBuffer(date);
        sb.insert(4, "-");
        date = date.length() == 6 ? sb.append("-01").toString() : sb.insert(7, "-").toString();
        Calendar calendar = parseStringToCalendar(date, inFormat);
        calendar.add(Calendar.MONTH, n);
        Date theDate = calendar.getTime();
        String time = new SimpleDateFormat("yyyyMM").format(theDate);
        return time;
    }

    /**
     * 获取指定日期至(指定日期-n)之间的日期集合
     * @param date 日期,格式：yyyy-MM-dd
     * @param n
     * @return
     */
    public static String[] getDateAryBeforeNDay(String date, String inFormat, String outFormat, int n) {
        if (date == null || n < 1) {
            return null;
        }
        String[] days = new String[n];
        for (int i = 0; i < n; i++) {
            days[i] = getStringBeforeNDay(date, inFormat, outFormat, n - i - 1);
        }
        return days;
    }

    /**
     * 获取当月第一天至制定日期(date)之间的日期集合
     * @param date 日期,格式:yyyy-MM-dd
     * @return
     */
    public static String[] getMonthDaysAry(String date, String inFormat, String outFormat) {
        if (date == null) {
            return null;
        }
        String day = date.substring(date.lastIndexOf("-") + 1);
        int dayCount = 0;
        if (day != null && day.length() > 0) {
            dayCount = Integer.parseInt(day);
        }
        String[] days = new String[dayCount];
        for (int i = 0; i < dayCount; i++) {
            days[i] = getStringBeforeNDay(date, inFormat, outFormat, dayCount - i - 1);
        }
        return days;
    }

    /**
     * 取得某个时间前n天,格式为yyyy-mm-dd
     */
    public static String getStringBeforeNDay(String str, String inFormat, String outFormat, int n) {
        Calendar ca = parseStringToCalendar(str, inFormat);
        if (ca == null) {
            return null;
        }
        ca.add(Calendar.DATE, -n);
        String strDate = ca.get(Calendar.YEAR) + "-";
        int intMonth = ca.get(Calendar.MONTH) + 1;
        if (intMonth < 10) {
            strDate += "0" + intMonth + "-";
        } else {
            strDate += intMonth + "-";
        }
        int intDay = ca.get(Calendar.DATE);
        if (intDay < 10) {
            strDate += "0" + intDay;
        } else {
            strDate += intDay;
        }
        strDate = transformStrFormat(strDate, DateUtil.DAY_HAVINTERVAL, outFormat);
        return strDate;
    }

    /**
     * 取得某个时间后n天,格式为yyyy-mm-dd
     */
    public static String getStringAfterNDay(String str, String inFormat, String outFormat, int n) {
        Calendar ca = parseStringToCalendar(str, inFormat);
        if (ca == null) {
            return null;
        }
        ca.add(Calendar.DATE, n);
        String strDate = ca.get(Calendar.YEAR) + "-";
        int intMonth = ca.get(Calendar.MONTH) + 1;
        if (intMonth < 10) {
            strDate += "0" + intMonth + "-";
        } else {
            strDate += intMonth + "-";
        }
        int intDay = ca.get(Calendar.DATE);
        if (intDay < 10) {
            strDate += "0" + intDay;
        } else {
            strDate += intDay;
        }
        strDate = transformStrFormat(strDate, DateUtil.DAY_HAVINTERVAL, outFormat);
        return strDate;
    }

    /**
     * 传入两个日期字符串，查询中间包含的日期字符
     * @param startDay 开始日期,格式为"yyyy-MM-dd"
     * @param endDay   结束日期,格式为"yyyy-MM-dd"
     * @return String[] 日期字符数组,格式为"yyyy-MM-dd"
     * @created 2010-02-10 11:19:58
     */
    public static String[] getIntervalDays(String startDay, String endDay, String inFormat, String outFormat) {
        if (startDay == null || endDay == null) {
            return null;
        }
        try {
            Date startDate = formatStrToDate(startDay, inFormat);
            Date endDate = formatStrToDate(endDay, inFormat);
            long dayCount_Long = getIntervalDaysForUtilDate(startDate, endDate);
            int dayCount = Integer.valueOf(String.valueOf(dayCount_Long)) + 1;
            String[] days = new String[dayCount];
            for (int i = 0; i < dayCount; i++) {
                days[i] = getStringBeforeNDay(endDay, inFormat, outFormat, dayCount - i - 1);
            }
            return days;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断当前时间是否在时间date2之前 时间格式 2005-4-21 16:16:34
     */
    public static boolean isDateBefore(String date1, String date2, String format) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            return df.parse(date1).before(df.parse(date2));
        } catch (ParseException e) {
            System.out.print("[SYS] " + e.getMessage());
            return false;
        }
    }

    /**
     * 判断当前时间是否在时间date2之前 时间格式 2005-4-21 16:16:34
     */
    public static boolean isDateBefore(String date2) {
        try {
            Date date1 = new Date();
            DateFormat df = DateFormat.getDateTimeInstance();
            return date1.before(df.parse(date2));
        } catch (ParseException e) {
            System.out.print("[SYS] " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取中文格式年月日的时间串
     */
    public static String getChineseTime(String str, String format) {
        try {
            if (str != null && !"".equals(str) && !"null".equals(str)) {
                Date date = DateUtil.formatStrToDate(str, format);
                if (date == null) {
                    return "未知";
                }
                Calendar cIn = Calendar.getInstance();
                cIn.setTime(date);
                int inYear = cIn.get(Calendar.YEAR);
                int day = cIn.get(Calendar.DAY_OF_MONTH);

                if ("YYYYMMDD".equals(format.toUpperCase())) {
                    return Integer.parseInt(DateUtil.getMonth(date)) + "月" + day + "日";
                } else if ("YYYYMM".equals(format.toUpperCase())) {
                    return inYear + "年" + Integer.parseInt(DateUtil.getMonth(date)) + "月";
                }
            }
            return "未知";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断两个日期是否间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int dateDifference(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        return sdf.format(date1).compareTo(sdf.format(date2));
    }

    /**
     * 判断两个日期是否相同
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isDifferent(Date d1, Date d2) {
        int i = dateDifference(d1, d2);
        return i == 0 ? false : true;
    }

    /**
     * 获取两个日期的年份间隔 如果有违例，请使用@exception/throws [违例类型]
     * [违例说明：异常的注释必须说明该异常的含义及什么条件下抛出该
     * @see [类、类#方法、类#成员]
     */
    public static int getYearPeriod(Date start, Date end) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(start);
        int y1 = gc.get(Calendar.YEAR);
        gc.setTime(end);
        int y2 = gc.get(Calendar.YEAR);
        return y2 - y1;
    }

    /**
     * 获取两个日期的月份间隔 如果有违例，请使用@exception/throws [违例类型]
     * [违例说明：异常的注释必须说明该异常的含义及什么条件下抛出该
     * @see [类、类#方法、类#成员]
     */
    public static int getMonthPeriod(Date start, Date end) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(start);
        int y1 = gc.get(Calendar.YEAR);
        int m1 = gc.get(Calendar.MONTH);
        gc.setTime(end);
        int y2 = gc.get(Calendar.YEAR);
        int m2 = gc.get(Calendar.MONTH);
        return (y2 - y1) * 12 + m2 - m1;
    }

    /**
     * 获取两个日期的天数间隔 如果有违例，请使用@exception/throws [违例类型]
     * [违例说明：异常的注释必须说明该异常的含义及什么条件下抛出该
     * @see [类、类#方法、类#成员]
     */
    public static long getDayPeriod(Date start, Date end) {
        return (end.getTime() - start.getTime()) / DATE_MILLIS;
    }

    /**
     * 比较时间前后 如果有违例，请使用@exception/throws [违例类型] [违例说明：异常的注释必须说明该异常的含义及什么条件下抛出该
     * @see [类、类#方法、类#成员]
     */
    public static boolean isBetweenStartAndEnd(Date startTime, Date endTime, Date toCompareTime) {
        return toCompareTime.compareTo(startTime) >= 0 && toCompareTime.compareTo(endTime) <= 0;
    }

    /**
     * 详细描述：取得某年某周的最后一天 ,对于交叉:2008-12-29到2009-01-04属于2008年的最后一周, 2009-01-04为 2008年最后一周的最后一天
     * @param year 年份
     * @param week 周
     * @return String yyyyMMdd格式的日期
     */
    public static String getLastDayOfWeek(int year, int week) {
        Calendar calLast = Calendar.getInstance();
        calLast.set(year, 0, 7);
        Date firstDate = getLastDayOfWeek(calLast.getTime());

        Calendar firstDateCal = Calendar.getInstance();
        firstDateCal.setTime(firstDate);

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, (week - 1) * 7);
        Date lastDate = getLastDayOfWeek(cal.getTime());
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        return sf.format(lastDate);
    }

    /**
     * 取得某天所在周的最后一天
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
        return c.getTime();
    }

    /**
     * 根据数字获取两位数的字符串，个位前面补零。<br/>
     * 详细描述：根据数字获取两位数的字符串，如果小于10则在前面补0，如果大于10则直接返回。<br/>
     * 使用方式：Util.getNum(i)调用。
     * @param i 数字。
     * @return 格式化后的字符类型数字。
     */
    public static String getNum(int i) {
        String ai = "";
        if (i < 10) {
            ai = "0" + i;
        } else {
            ai = i + "";
        }
        return ai;
    }

    /**
     * 获取传入月份的天数。<br/>
     * 详细描述：根据传入的月份返回这个月的最大天数。<br/>
     * 使用方式：Util.getDaysByMoth(moth)调用。
     * @param moth 需要判断的月份。
     * @return 这个月的天数。
     */
    public static int getDaysByMoth(int moth) {
        int moths = 0;
        switch (moth) {
            case 1:
                moths = 31;
                break;
            case 2:
                moths = 28;
                break;
            case 3:
                moths = 31;
                break;
            case 4:
                moths = 30;
                break;
            case 5:
                moths = 31;
                break;
            case 6:
                moths = 30;
                break;
            case 7:
                moths = 31;
                break;
            case 8:
                moths = 31;
                break;
            case 9:
                moths = 30;
                break;
            case 10:
                moths = 31;
                break;
            case 11:
                moths = 30;
                break;
            case 12:
                moths = 31;
                break;
            default:
                moths = 31;
                break;
        }
        return moths;
    }

    public static String getCurrentTime() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String ymd = df.format(new Date()).toString();
        return ymd;
    }

    /**
     * 对日期数据进行格式化
     * @param date    日期
     * @param pattern 格式类型
     * @return 格式化后的日期字符串
     */
    public static String convertDate(Date date, String pattern) {
        if (null == date)
            return null;
        if (StringUtil.isNullOrEmpty(pattern))
            pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        String ymd = df.format(date).toString();
        return ymd;
    }

    /**
     * 返回前一天日期
     * @param format
     * @return
     */
    public static String getYesterdayDate(String format) {
        if (format == null) {
            format = DateUtil.DAY_HAVINTERVAL;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE);
        return sdf.format(calendar.getTime());
    }

    /**
     * 返回上一月日期
     * @param format
     * @return
     */
    public static String getLastMonthDate(String format) {
        if (format == null) {
            format = DateUtil.DAY_HAVINTERVAL;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE);
        return sdf.format(calendar.getTime());
    }

    /**
     * 返回上一周日期
     * @param format
     * @return
     */
    public static String getLastWeekDate(String format) {
        if (format == null) {
            format = DateUtil.DAY_HAVINTERVAL;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE);
        return sdf.format(calendar.getTime());
    }
}
