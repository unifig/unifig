package com.unifig.bi.analysis.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.bi.analysis.mapper.StSmsPromotionMapper;
import com.unifig.bi.analysis.model.StSmsPromotion;
import com.unifig.bi.analysis.service.StSmsPromotionService;
import com.unifig.bi.analysis.utils.AnanlysisUtils;
import com.tools.plugin.utils.NewMapUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

/**
 * <p>
 * 文章分类表 服务实现类
 * </p>
 *
 *
 * @since 2019-03-21
 */
@Service
public class StSmsPromotionServiceImpl extends ServiceImpl<StSmsPromotionMapper, StSmsPromotion> implements StSmsPromotionService {

    @Autowired
    private StSmsPromotionMapper stCmsPromotionMapper;

    @Override
    public Map<String, Object> line(Date startDate, Date stopDate, String promotionId) {
        int day = new Period(new DateTime(startDate), new DateTime(stopDate), PeriodType.days()).getDays();
        // 测试数据 等待修改
        String date = new DateTime(startDate).toString("yyyyMMdd");// data=new
        List<Map<String, Object>> paremList = new ArrayList<>();
        if (day == 0) {
            Map<String, Object> paremMap = new HashMap<>();
            paremMap.put("tableName", "st_sms_promotion");
            if (StringUtils.isNotEmpty(promotionId)) {
                paremMap.put("promotionId", promotionId);

            }

            paremMap.put("statisDate", date);
            paremList.add(paremMap);
            int dayIsStart = AnanlysisUtils.getDsDateDay(startDate, stopDate);
            List<Map<String, Object>> result = dayIsStart == -1 ? null : stCmsPromotionMapper.lineDay(paremList);
            result = AnanlysisUtils.getAllHour(result, new NewMapUtil().set("statisDate", date).set("joinPromotionCount", 0)
                    .get(), null);

            return ResultActiveUser(result, true);
        }
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
        int dayIsStart = AnanlysisUtils.getDsDateDay(startDate, stopDate);
        for (int i = 0; i <= dayIsStart; i++) {
            String stDate = DateTime.parse(AnanlysisUtils.getDsDate(date), format).plusDays(i).toString("yyyyMMdd");
            Map<String, Object> paremMap = new HashMap<>();
            paremMap.put("tableName", "st_sms_promotion");
            if (StringUtils.isNotEmpty(promotionId)) {
                paremMap.put("promotionId", promotionId);

            }
            paremMap.put("statisDate", stDate);
            paremList.add(paremMap);
        }
        List<Map<String, Object>> result = dayIsStart == -1 ? null : stCmsPromotionMapper.line(paremList);

        result = AnanlysisUtils.getAllDate(result, day, date, new NewMapUtil().set("statisDate", date).set("joinPromotionCount", 0)
                .get(), "statisDate");
        Collections.reverse(result);
        return ResultActiveUser(result, false);
    }


    /**
     * 处理图表数据返回给前段
     *
     * @param result 数据
     * @param isDay  是否是一天的数据
     * @return
     */
    private Map<String, Object> ResultActiveUser(List<Map<String, Object>> result, boolean isDay) {

        List<String> statisDate = new ArrayList<>();
        List<Object> hour = new ArrayList<>();
        List<Integer> readArticleCount = new ArrayList<>();
        result.forEach(re -> {
            if (!isDay) {
                statisDate.add(DateTime.parse(String.valueOf(re.get("statisDate")), AnanlysisUtils.format).toString("yyyy/MM/dd"));
            }
            hour.add(re.get("hour"));
            readArticleCount.add(Integer.valueOf(re.get("joinPromotionCount").toString()));
        });
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> signinUser = new HashMap<>();
        signinUser.put("x", isDay ? hour : statisDate);
        signinUser.put("y", readArticleCount);
        // 阅读量
        map.put("joinPromotionCount", signinUser);
        return map;
    }

}
