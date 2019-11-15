package com.unifig.bi.analysis.controller;


import com.unifig.bi.analysis.service.StSmsVippageService;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 专享 前端控制器
 * </p>
 *
 *
 * @since 2019-03-22
 */
@RestController
@RequestMapping("/sms/vippage")
public class StSmsVippageController {
    @Autowired
    private StSmsVippageService stSmsVippageService;

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
                           @RequestParam(value = "stopDate", required = true) java.sql.Date stopDate) {

        Map<String, Object> response = stSmsVippageService.line(startDate, stopDate);
        return ResultData.result(true).setData(response);
    }
}

