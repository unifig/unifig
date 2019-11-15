package com.unifig.bi.analysis.controller;


import com.unifig.bi.analysis.service.StSmsNavigationService;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 导航栏 前端控制器
 * </p>
 *
 *
 * @since 2019-03-22
 */
@RestController
@RequestMapping("/sms/navigation")
public class StSmsNavigationController {

    @Autowired
    private StSmsNavigationService stSmsNavigationService;

    /**
     * 导航阅读量
     *
     * @param startDate
     * @param stopDate
     * @return
     */
    @RequestMapping(value = "/line", method = RequestMethod.GET)
    @ResponseBody
    public ResultData line(@RequestParam(value = "startDate", required = true) java.sql.Date startDate,
                           @RequestParam(value = "stopDate", required = true) java.sql.Date stopDate,
                           @RequestParam(value = "bannerId", required = false) String navigationId) {

        Map<String, Object> response = stSmsNavigationService.line(startDate, stopDate, navigationId);
        return ResultData.result(true).setData(response);
    }
}

