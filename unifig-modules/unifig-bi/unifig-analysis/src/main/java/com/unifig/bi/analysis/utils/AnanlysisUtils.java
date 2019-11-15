package com.unifig.bi.analysis.utils;

import com.tools.plugin.utils.helper.MapListSortHelper;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据操作
 */
@Component
public class AnanlysisUtils {
    private static String dsDate = "20180901";
    public static DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");

    /**
     * 验证所选时间是否有数据
     *
     * @param startDate
     * @param stopDate
     * @return
     */
    public static int getDsDateDay(Date startDate, Date stopDate) {
        int isCurrentDate = new Period(new DateTime(new DateTime().toString("yyyy-MM-dd")).minusDays(1),
                new DateTime(stopDate), PeriodType.days()).getDays();
        if (isCurrentDate > 0) {
            stopDate = Date.valueOf(new DateTime().minusDays(1).toString("yyyy-MM-dd"));
        }
        int isStartDate = new Period(DateTime.parse(dsDate, format), new DateTime(startDate), PeriodType.days())
                .getDays();
        int isStopDate = new Period(DateTime.parse(dsDate, format), new DateTime(stopDate), PeriodType.days())
                .getDays();
        int isMaxCurrentDate = new Period(new DateTime(new DateTime().toString("yyyy-MM-dd")).minusDays(1),
                new DateTime(stopDate), PeriodType.days()).getDays();
        if (isStopDate < 0 || isMaxCurrentDate > 0) {
            return -1;
        }
        if (isStartDate < 0) {
            return new Period(DateTime.parse(dsDate, format), new DateTime(stopDate), PeriodType.days()).getDays();
        } else {
            return new Period(new DateTime(startDate), new DateTime(stopDate), PeriodType.days()).getDays();
        }
    }

    /**
     * 按天补全
     *
     * @param sourceList
     * @param defaultMap
     * @return
     */
    public static List<Map<String, Object>> getAllHour(List<Map<String, Object>> sourceList, Map<String, Object> defaultMap, String date) {
        if (sourceList == null || sourceList.isEmpty()) {
            sourceList = new ArrayList<>();
        }
        List<Map<String, Object>> resList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            boolean isExist = false;
            for (Map<String, Object> resultMap : sourceList) {
                String hourId = String.valueOf(resultMap.get("hourId"));
                if (hourId.equalsIgnoreCase(String.valueOf(i))) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                Map<String, Object> newMap = new HashMap<>();
                newMap.putAll(defaultMap);
                newMap.put(date == null ? "hour" : date,
                        String.format("%02d", i) + ":00-" + String.format("%02d", i) + ":59");
                newMap.put("hourId", i);
                resList.add(newMap);
            }
        }
        sourceList.addAll(resList);
        MapListSortHelper sortHelper = new MapListSortHelper();
        sortHelper.Sort(sourceList, "hourId", "asc", true);
        return sourceList;
    }

    /**
     * 得到最早有数据的一天
     *
     * @param date
     * @return
     */
    public static String getDsDate(String date) {
        int isStartDate = new Period(DateTime.parse(dsDate, format), DateTime.parse(date, format), PeriodType.days())
                .getDays();
        if (isStartDate < 0) {
            return dsDate;
        }
        return date;
    }

    /**
     * 补全日期
     *
     * @param sourceList
     * @param day
     * @param data
     * @param defaultMap
     * @param key
     * @return
     */
    public static List<Map<String, Object>> getAllDate(List<Map<String, Object>> sourceList, int day, String data,
                                                Map<String, Object> defaultMap, String key) {
        if (sourceList == null) {
            sourceList = new ArrayList<>();
        }
        List<Map<String, Object>> resList = new ArrayList<>();
        for (int i = 0; i <= day; i++) {
            boolean isExist = false;
            String stDate = DateTime.parse(data, format).plusDays(i).toString("yyyyMMdd");
            for (Map<String, Object> resultMap : sourceList) {
                String statisDate = String.valueOf(resultMap.get(key));
                if (statisDate.equalsIgnoreCase(String.valueOf(stDate))) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                Map<String, Object> newMap = new HashMap<>();
                newMap.putAll(defaultMap);
                newMap.put(key, Integer.valueOf(stDate));
                resList.add(newMap);
            }
        }
        sourceList.addAll(resList);
        MapListSortHelper sortHelper = new MapListSortHelper();
        sortHelper.Sort(sourceList, key, "desc", false);
        return sourceList;
    }
}
