package com.unifig.bi.analysis.controller;


import com.unifig.bi.analysis.service.StCmsArticleService;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 文章分类表 前端控制器
 * </p>
 *
 *
 * @since 2019-03-21
 */
@RestController
@RequestMapping("/cms/article")
public class StCmsArticleController {


    @Autowired
    private StCmsArticleService stCmsArticleService;

    /**
     * 文章阅读量
     *
     * @param startDate
     * @param stopDate
     * @return
     */
    @RequestMapping(value = "/line", method = RequestMethod.GET)
    @ResponseBody
    public ResultData line(@RequestParam(value = "startDate", required = true) java.sql.Date startDate,
                           @RequestParam(value = "stopDate", required = true) java.sql.Date stopDate,
                           @RequestParam(value = "articleId", required = false) String articleId) {

        Map<String, Object> response = stCmsArticleService.line(startDate, stopDate, articleId);
        return ResultData.result(true).setData(response);
    }
}

