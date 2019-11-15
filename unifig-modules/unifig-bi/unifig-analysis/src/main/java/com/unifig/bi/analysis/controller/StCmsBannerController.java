package com.unifig.bi.analysis.controller;


import com.unifig.bi.analysis.service.StCmsBannerService;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * banner 前端控制器
 * </p>
 *
 *
 * @since 2019-03-22
 */
@RestController
@RequestMapping("/cms/banner")
public class StCmsBannerController {

    @Autowired
    private StCmsBannerService stCmsBannerService;

    /**
     * bannner阅读量
     *
     * @param startDate
     * @param stopDate
     * @return
     */
    @RequestMapping(value = "/line", method = RequestMethod.GET)
    @ResponseBody
    public ResultData line(@RequestParam(value = "startDate", required = true) java.sql.Date startDate,
                           @RequestParam(value = "stopDate", required = true) java.sql.Date stopDate,
                           @RequestParam(value = "bannerId", required = false) String bannerId) {

        Map<String, Object> response = stCmsBannerService.line(startDate, stopDate, bannerId);
        return ResultData.result(true).setData(response);
    }
}

