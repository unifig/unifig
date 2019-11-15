package com.unifig.bi.analysis.controller;


import com.unifig.bi.analysis.service.StSmsPromotionService;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 活动 前端控制器
 * </p>
 *
 *
 * @since 2019-03-21
 */
@RestController
@RequestMapping("/sms/promotion")
public class StSmsPromotionController {

    @Autowired
    private StSmsPromotionService stSmsPromotionService;

    /**
     * 活动参与量
     *
     * @param startDate
     * @param stopDate
     * @return
     */
    @RequestMapping(value = "/line", method = RequestMethod.GET)
    @ResponseBody
    public ResultData line(@RequestParam(value = "startDate", required = true) java.sql.Date startDate,
                           @RequestParam(value = "stopDate", required = true) java.sql.Date stopDate,
                           @RequestParam(value = "bannerId", required = false) String promotionId) {

        Map<String, Object> response = stSmsPromotionService.line(startDate, stopDate, promotionId);
        return ResultData.result(true).setData(response);
    }
}

