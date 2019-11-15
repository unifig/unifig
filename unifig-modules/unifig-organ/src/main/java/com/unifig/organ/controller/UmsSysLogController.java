package com.unifig.organ.controller;

import com.unifig.organ.dto.PageUtils;
import com.unifig.organ.dto.R;
import com.unifig.organ.model.SysLogEntity;
import com.unifig.organ.service.UmsSysLogService;
import com.unifig.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 系统日志Controller
 *
 *
 *
 * @date 2018-10-24
 */
@Controller
@RequestMapping("/sys/log")
@ApiIgnore
public class UmsSysLogController {
    @Autowired
    private UmsSysLogService sysLogService;

    /**
     * 系统日志列表
     *
     * @param params 请求参数
     * @return R
     */
    @ResponseBody
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<SysLogEntity> sysLogList = sysLogService.queryList(query);
        int total = sysLogService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(sysLogList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

}
