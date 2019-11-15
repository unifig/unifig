package com.unifig.organ.controller;

import com.unifig.organ.dto.PageUtils;
import com.unifig.organ.dto.R;
import com.unifig.organ.service.UmsSysConfigService;
import com.unifig.organ.model.SysConfigEntity;
import com.unifig.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 系统配置信息Controller
 *
 *
 *
 * @date 2018-10-24
 */
@RestController
@RequestMapping("/sys/config")
@ApiIgnore
public class UmsSysConfigController extends AbstractController {
    @Autowired
    private UmsSysConfigService sysConfigService;

    /**
     * 所有配置列表
     *
     * @param params 请求参数
     * @return R
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<SysConfigEntity> configList = sysConfigService.queryList(query);
        int total = sysConfigService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(configList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }


    /**
     * 根据主键获取配置信息
     *
     * @param id 主键
     * @return R
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SysConfigEntity config = sysConfigService.queryObject(id);

        return R.ok().put("config", config);
    }

    /**
     * 新增配置
     *
     * @param config 配置
     * @return R
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysConfigEntity config) {
        sysConfigService.save(config);

        return R.ok();
    }

    /**
     * 修改配置
     *
     * @param config 配置
     * @return R
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysConfigEntity config) {

        sysConfigService.update(config);

        return R.ok();
    }

    /**
     * 删除配置
     *
     * @param ids 主键集
     * @return R
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        sysConfigService.deleteBatch(ids);

        return R.ok();
    }

}
