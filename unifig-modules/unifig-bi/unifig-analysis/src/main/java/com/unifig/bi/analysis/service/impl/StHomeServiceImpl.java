package com.unifig.bi.analysis.service.impl;

import com.unifig.bi.analysis.mapper.*;
import com.unifig.bi.analysis.service.StHomeService;
import com.unifig.bi.analysis.vo.StReportVo;
import com.unifig.utils.StringUtil;
import com.tools.plugin.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * banner 服务实现类
 * </p>
 *
 *
 * @since 2019-03-22
 */
@Service
public class StHomeServiceImpl implements StHomeService {

    @Autowired
    private StUmsInstallMapper stUmsInstallMapper;

    @Autowired
    private StUmsRegisterMapper stUmsRegisterMapper;

    @Autowired
    private StUmsBindingMapper stUmsBindingMapper;

    @Autowired
    private StUmsOnlineMapper stUmsOnlineMapper;

    @Autowired
    private StUmsInnetMapper stUmsInnetMapper;

    @Override
    public Map<String, Object> overview(String deptId) {

        Date date = new Date();
        String year = DateUtil.getYear(date);
        String month = DateUtil.getMonth(date);
        String day = DateUtil.getDay(date);


        //年 月 日 注册量
        Map<String, Object> registerCountParemMap = new HashMap<>();
        registerCountParemMap.put("tableName", "st_ums_register");
        if (StringUtils.isNotEmpty(deptId)) {
            registerCountParemMap.put("deptId", deptId);

        }
        registerCountParemMap.put("year", year);
        registerCountParemMap.put("month", month);
        registerCountParemMap.put("day", day);
        Map<String, Object> registerCount = stUmsRegisterMapper.countRegister(registerCountParemMap);


        //年 月 日 安装总量
        Map<String, Object> installCountParemMap = new HashMap<>();
        installCountParemMap.put("tableName", "st_ums_install");
        if (StringUtils.isNotEmpty(deptId)) {
            installCountParemMap.put("deptId", deptId);

        }
        installCountParemMap.put("year", year);
        installCountParemMap.put("month", month);
        installCountParemMap.put("day", day);
        Map<String, Object> installCount = stUmsInstallMapper.countInstall(installCountParemMap);

        Map<String, Object> result = new HashMap<>();
        if (installCount != null) {
            result.putAll(installCount);

        }
        if(registerCount!=null){
            result.putAll(registerCount);
        }


        return result;
    }

    @Override
    public Map<String, StReportVo> report(String deptId, String type) {

        Date date = new Date();
        String year = DateUtil.getYear(date);
        String month = DateUtil.getMonth(date);
        String day = DateUtil.getDay(date);
        Map<String, Object> dayReportMap = new HashMap<>();
        if ("day".equals(type)) {
            if (!StringUtil.isBlankOrNull(deptId)) {
                dayReportMap.put("deptId", deptId);
            }

            dayReportMap.put("year", year);
            dayReportMap.put("month", month);

            //key 日期  value 数据
            Map<String, StReportVo> dayResult = new LinkedHashMap<String, StReportVo>();

            List<Map<String, Object>> dayInstallReport = stUmsInstallMapper.countDayReport(dayReportMap);
            List<Map<String, Object>> dayInnetReport = stUmsInnetMapper.countDayReport(dayReportMap);
            List<Map<String, Object>> dayOnlineReport = stUmsOnlineMapper.countDayReport(dayReportMap);
            List<Map<String, Object>> dayRegisterReport = stUmsRegisterMapper.countDayReport(dayReportMap);
            List<Map<String, Object>> dayBindingReport = stUmsBindingMapper.countDayReport(dayReportMap);

            for (Map<String, Object> dayInstallReportMap : dayInstallReport) {
                long dayCount = (long) dayInstallReportMap.get("dayCount");
                String staticDate = (String) dayInstallReportMap.get("staticDate");
                StReportVo stReportVo = dayResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    dayResult.put(staticDate, stReportVo);
                }
                stReportVo.setInstallCount(dayCount);
                stReportVo.setStaticDate(staticDate);
            }

            for (Map<String, Object> dayInnetMap : dayInnetReport) {
                long dayCount = (long) dayInnetMap.get("dayCount");
                String staticDate = (String) dayInnetMap.get("staticDate");
                StReportVo stReportVo = dayResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    dayResult.put(staticDate, stReportVo);
                }
                stReportVo.setInnetCount(dayCount);
                stReportVo.setStaticDate(staticDate);

            }
            for (Map<String, Object> dayOnlineMap : dayOnlineReport) {
                long dayCount = (long) dayOnlineMap.get("dayCount");
                String staticDate = (String) dayOnlineMap.get("staticDate");
                StReportVo stReportVo = dayResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    dayResult.put(staticDate, stReportVo);
                }
                stReportVo.setOnlineCount(dayCount);
                stReportVo.setStaticDate(staticDate);
            }

            for (Map<String, Object> dayBindingMap : dayBindingReport) {
                long dayCount = (long) dayBindingMap.get("dayCount");
                String staticDate = (String) dayBindingMap.get("staticDate");
                StReportVo stReportVo = dayResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    dayResult.put(staticDate, stReportVo);
                }
                stReportVo.setBindingCount(dayCount);
                stReportVo.setStaticDate(staticDate);
            }
            for (Map<String, Object> dayRegisterMap : dayRegisterReport) {
                long dayCount = (long) dayRegisterMap.get("dayCount");
                String staticDate = (String) dayRegisterMap.get("staticDate");
                StReportVo stReportVo = dayResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    dayResult.put(staticDate, stReportVo);
                }
                stReportVo.setRegisterCount(dayCount);
                stReportVo.setStaticDate(staticDate);
            }
            return dayResult;
        } else if ("week".equals(type)) {
            Map<String, Object> weekReportMap = new HashMap<>();
            if (StringUtils.isNotEmpty(deptId)) {
                if (!StringUtil.isBlankOrNull(deptId)) {
                    weekReportMap.put("deptId", deptId);
                }
            }
            weekReportMap.put("year", year);

            //key 日期  value 数据
            Map<String, StReportVo> weekResult = new LinkedHashMap<String, StReportVo>();

            List<Map<String, Object>> weekInstallReport = stUmsInstallMapper.countWeekReport(weekReportMap);
            List<Map<String, Object>> weekInnetReport = stUmsInnetMapper.countWeekReport(weekReportMap);
            List<Map<String, Object>> weekOnlineReport = stUmsOnlineMapper.countWeekReport(weekReportMap);
            List<Map<String, Object>> weekRegisterReport = stUmsRegisterMapper.countWeekReport(weekReportMap);
            List<Map<String, Object>> weekBindingReport = stUmsBindingMapper.countWeekReport(weekReportMap);

            for (Map<String, Object> weekInstallReportMap : weekInstallReport) {
                long weekCount = (long) weekInstallReportMap.get("weekCount");
                String staticDate = String.valueOf(weekInstallReportMap.get("staticDate"));
                StReportVo stReportVo = weekResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    weekResult.put(staticDate, stReportVo);
                }
                stReportVo.setInstallCount(weekCount);
                stReportVo.setStaticDate(staticDate);
            }

            for (Map<String, Object> weekInnetMap : weekInnetReport) {
                long weekCount = (long) weekInnetMap.get("weekCount");
                String staticDate = String.valueOf(weekInnetMap.get("staticDate"));
                StReportVo stReportVo = weekResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    weekResult.put(staticDate, stReportVo);
                }
                stReportVo.setInnetCount(weekCount);
                stReportVo.setStaticDate(staticDate);

            }
            for (Map<String, Object> weekOnlineMap : weekOnlineReport) {
                long weekCount = (long) weekOnlineMap.get("weekCount");
                String staticDate = String.valueOf(weekOnlineMap.get("staticDate"));
                StReportVo stReportVo = weekResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    weekResult.put(staticDate, stReportVo);
                }
                stReportVo.setOnlineCount(weekCount);
                stReportVo.setStaticDate(staticDate);

            }

            for (Map<String, Object> weekBindingMap : weekRegisterReport) {
                long weekCount = (long) weekBindingMap.get("weekCount");
                String staticDate = String.valueOf(weekBindingMap.get("staticDate"));
                StReportVo stReportVo = weekResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    weekResult.put(staticDate, stReportVo);
                }
                stReportVo.setBindingCount(weekCount);
                stReportVo.setStaticDate(staticDate);

            }
            for (Map<String, Object> weekRegisterMap : weekBindingReport) {
                long weekCount = (long) weekRegisterMap.get("weekCount");
                String staticDate = String.valueOf(weekRegisterMap.get("staticDate"));
                StReportVo stReportVo = weekResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    weekResult.put(staticDate, stReportVo);
                }
                stReportVo.setRegisterCount(weekCount);
                stReportVo.setStaticDate(staticDate);

            }
            weekResult.remove("0");
            return weekResult;

        } else if ("month".equals(type)) {

            Map<String, Object> monthReportMap = new HashMap<>();
            if (StringUtils.isNotEmpty(deptId)) {
                    monthReportMap.put("deptId", deptId);
            }
            monthReportMap.put("year", year);

            //key 日期  value 数据
            Map<String, StReportVo> monthResult = new LinkedHashMap<String, StReportVo>();

            List<Map<String, Object>> monthInstallReport = stUmsInstallMapper.countMonthReport(monthReportMap);
            List<Map<String, Object>> monthInnetReport = stUmsInnetMapper.countMonthReport(monthReportMap);
            List<Map<String, Object>> monthOnlineReport = stUmsOnlineMapper.countMonthReport(monthReportMap);
            List<Map<String, Object>> monthRegisterReport = stUmsRegisterMapper.countMonthReport(monthReportMap);
            List<Map<String, Object>> monthBindingReport = stUmsBindingMapper.countMonthReport(monthReportMap);

            for (Map<String, Object> monthInstallReportMap : monthInstallReport) {
                long monthCount = (long) monthInstallReportMap.get("monthCount");
                String staticDate = String.valueOf(monthInstallReportMap.get("staticDate"));
                StReportVo stReportVo = monthResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    monthResult.put(staticDate, stReportVo);
                }
                stReportVo.setInstallCount(monthCount);
                stReportVo.setStaticDate(staticDate);

            }

            for (Map<String, Object> monthInnetMap : monthInnetReport) {
                long monthCount = (long) monthInnetMap.get("monthCount");
                String staticDate = String.valueOf(monthInnetMap.get("staticDate"));
                StReportVo stReportVo = monthResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    monthResult.put(staticDate, stReportVo);
                }
                stReportVo.setInnetCount(monthCount);
                stReportVo.setStaticDate(staticDate);

            }
            for (Map<String, Object> monthOnlineMap : monthOnlineReport) {
                long monthCount = (long) monthOnlineMap.get("monthCount");
                String staticDate = String.valueOf(monthOnlineMap.get("staticDate"));
                StReportVo stReportVo = monthResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    monthResult.put(staticDate, stReportVo);
                }
                stReportVo.setOnlineCount(monthCount);
                stReportVo.setStaticDate(staticDate);

            }

            for (Map<String, Object> monthBindingMap : monthRegisterReport) {
                long monthCount = (long) monthBindingMap.get("monthCount");
                String staticDate = String.valueOf(monthBindingMap.get("staticDate"));
                StReportVo stReportVo = monthResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    monthResult.put(staticDate, stReportVo);
                }
                stReportVo.setBindingCount(monthCount);
                stReportVo.setStaticDate(staticDate);

            }
            for (Map<String, Object> monthRegisterMap : monthBindingReport) {
                long monthCount = (long) monthRegisterMap.get("monthCount");
                String staticDate = String.valueOf(monthRegisterMap.get("staticDate"));
                StReportVo stReportVo = monthResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    monthResult.put(staticDate, stReportVo);
                }
                stReportVo.setRegisterCount(monthCount);
                stReportVo.setStaticDate(staticDate);

            }
            return monthResult;

        } else if ("year".equals(type)) {
            Map<String, Object> yearReportMap = new HashMap<>();
            if (StringUtils.isNotEmpty(deptId)) {
                    yearReportMap.put("deptId", deptId);
            }
            yearReportMap.put("year", year);

            //key 日期  value 数据
            Map<String, StReportVo> yearResult = new LinkedHashMap<String, StReportVo>();

            List<Map<String, Object>> yearInstallReport = stUmsInstallMapper.countYearReport(yearReportMap);
            List<Map<String, Object>> yearInnetReport = stUmsInnetMapper.countYearReport(yearReportMap);
            List<Map<String, Object>> yearOnlineReport = stUmsOnlineMapper.countYearReport(yearReportMap);
            List<Map<String, Object>> yearRegisterReport = stUmsRegisterMapper.countYearReport(yearReportMap);
            List<Map<String, Object>> yearBindingReport = stUmsBindingMapper.countYearReport(yearReportMap);

            for (Map<String, Object> yearInstallReportMap : yearInstallReport) {
                long yearCount = (long) yearInstallReportMap.get("yearCount");
                String staticDate = String.valueOf(yearInstallReportMap.get("staticDate"));
                StReportVo stReportVo = yearResult.get(yearCount);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    yearResult.put(staticDate, stReportVo);
                }
                stReportVo.setInstallCount(yearCount);
                stReportVo.setStaticDate(staticDate);

            }

            for (Map<String, Object> yearInnetMap : yearInnetReport) {
                long yearCount = (long) yearInnetMap.get("yearCount");
                String staticDate = String.valueOf(yearInnetMap.get("staticDate"));
                StReportVo stReportVo = yearResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    yearResult.put(staticDate, stReportVo);
                }
                stReportVo.setInnetCount(yearCount);
                stReportVo.setStaticDate(staticDate);

            }
            for (Map<String, Object> yearOnlineMap : yearOnlineReport) {
                long yearCount = (long) yearOnlineMap.get("yearCount");
                String staticDate = String.valueOf(yearOnlineMap.get("staticDate"));
                StReportVo stReportVo = yearResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    yearResult.put(staticDate, stReportVo);
                }
                stReportVo.setOnlineCount(yearCount);
                stReportVo.setStaticDate(staticDate);

            }

            for (Map<String, Object> yearRegisterMap : yearRegisterReport) {
                long yearCount = (long) yearRegisterMap.get("yearCount");
                String staticDate = String.valueOf(yearRegisterMap.get("staticDate"));
                StReportVo stReportVo = yearResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    yearResult.put(staticDate, stReportVo);
                }
                stReportVo.setRegisterCount(yearCount);
                stReportVo.setStaticDate(staticDate);

            }

            for (Map<String, Object> yearBindingMap : yearBindingReport) {
                long yearCount = (long) yearBindingMap.get("yearCount");
                String staticDate = String.valueOf(yearBindingMap.get("staticDate"));
                StReportVo stReportVo = yearResult.get(staticDate);
                if (stReportVo == null) {
                    stReportVo = new StReportVo();
                    yearResult.put(staticDate, stReportVo);
                }
                stReportVo.setBindingCount(yearCount);
                stReportVo.setStaticDate(staticDate);

            }
            return yearResult;
        }
        return null;
    }

}