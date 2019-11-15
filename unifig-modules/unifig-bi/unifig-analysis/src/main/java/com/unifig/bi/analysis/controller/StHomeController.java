package com.unifig.bi.analysis.controller;


import com.unifig.bi.analysis.service.StHomeService;
import com.unifig.bi.analysis.vo.StReportVo;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 首页
 * </p>
 *
 *
 * @since 2019-03-21
 */
@RestController
@RequestMapping("/home")
public class StHomeController {

    @Autowired
    private StHomeService stHomeService;


    /**
     * 数据总览
     *
     * @return
     */
    @RequestMapping(value = "/data/overview", method = RequestMethod.GET)
    @ResponseBody
    public ResultData overview(@RequestParam(required = true) String deptId) {

        Map<String, Object> response = stHomeService.overview(deptId);
        return ResultData.result(true).setData(response);
    }


    /**
     * 统计报表
     *
     * @return
     */
    @RequestMapping(value = "/data/report", method = RequestMethod.GET)
    @ResponseBody
    public ResultData report(@RequestParam(required = true) String deptId,@RequestParam(required = true) String type //day week month year
    ) {

        Map<String, StReportVo> map = new TreeMap<String, StReportVo>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 降序排序
                        return Long.valueOf(obj1)>Long.valueOf(obj2)?1:0;
                        //return obj2.compareTo(obj1);
                    }
                });
        List<StReportVo> result =new  ArrayList<StReportVo>();
        Map<String, StReportVo> report = stHomeService.report(deptId, type);

        map.putAll(report);

        Set<Map.Entry<String, StReportVo>> entries = map.entrySet();
        for (Map.Entry<String, StReportVo> entry : entries) {
            result.add(entry.getValue());
        }
        return ResultData.result(true).setData(result);
    }

}

