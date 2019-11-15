package com.unifig.organ.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.context.Constants;
import com.unifig.organ.model.UmsMemberIntegrationRuleSetting;
import com.unifig.organ.service.UmsMemberIntegrationRuleSettingService;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.Timestamp;

/**
 * <p>
 * 会员积分成长规则表 前端控制器
 * </p>
 *
 *
 * @since 2019-01-27
 */
@RestController
@RequestMapping("/ums/member/irs")
@Api(tags = "用户积分规则", description = "用户积分规则")
public class UmsMemberIntegrationRuleSettingController {
    @Autowired
    private UmsMemberIntegrationRuleSettingService umsMemberIntegrationRuleSettingService;

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("列表数据查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsMemberIntegrationRuleSetting> list(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "2") Integer rows) {
        try {
            EntityWrapper<UmsMemberIntegrationRuleSetting> umsMemberIntegrationRuleSettingWrapper = new EntityWrapper<UmsMemberIntegrationRuleSetting>();
            umsMemberIntegrationRuleSettingWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            Page<UmsMemberIntegrationRuleSetting> umsMemberIntegrationRuleSettingPage = umsMemberIntegrationRuleSettingService.selectPage(new Page<UmsMemberIntegrationRuleSetting>(page, rows), umsMemberIntegrationRuleSettingWrapper);
            int count = umsMemberIntegrationRuleSettingService.selectCount(umsMemberIntegrationRuleSettingWrapper);
            return ResultData.result(true).setData(umsMemberIntegrationRuleSettingPage.getRecords()).setCount(count);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }

    /**
     * 详情
     *
     * @return
     */
    @ApiOperation("详情数据查询")
    @RequestMapping(value = "info", method = RequestMethod.GET)
    @ResponseBody
    public ResultData info(@RequestParam(required = true, defaultValue = "24") String id

    ) {
        try {
            UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting = umsMemberIntegrationRuleSettingService.selectById(String.valueOf(id));
            return ResultData.result(true).setData(umsMemberIntegrationRuleSetting);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }


    /**
     * 新建 更新
     *
     * @return
     */
    @ApiOperation("新增或者更新")
    @PostMapping(value = "/iou")
    public ResultData<UmsMemberIntegrationRuleSetting> iou(@RequestBody UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting) {
        try {
            if (umsMemberIntegrationRuleSetting == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            //补充其他数据
            String id = umsMemberIntegrationRuleSetting.getId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (id == null) {
                umsMemberIntegrationRuleSetting.setCreateTime(now);
            }
            umsMemberIntegrationRuleSetting.setEditTime(now);
            umsMemberIntegrationRuleSetting.setEnable(Constants.DEFAULT_VAULE_ONE);
            umsMemberIntegrationRuleSettingService.insertOrUpdate(umsMemberIntegrationRuleSetting);
           // umsMemberIntegrationRuleSettingService.cache(umsMemberIntegrationRuleSetting.getAction());
            return ResultData.result(true).setData(umsMemberIntegrationRuleSetting);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }

    /**
     * 删除
     *
     * @return
     */
    @ApiOperation("删除")
    @RequestMapping(value = "del", method = RequestMethod.GET)
    public ResultData del(@RequestParam String id
    ) {
        try {
            UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting = umsMemberIntegrationRuleSettingService.selectById(String.valueOf(id));
            if (umsMemberIntegrationRuleSetting == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            umsMemberIntegrationRuleSetting.setEnable(Constants.DEFAULT_VAULE_ZERO);
            //更新状态
            umsMemberIntegrationRuleSettingService.updateById(umsMemberIntegrationRuleSetting);
            umsMemberIntegrationRuleSettingService.deleteCache(umsMemberIntegrationRuleSetting.getAction());
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }

    /**
     * 缓存  支持 单 多
     *
     * @return
     */
    @ApiOperation("缓存")
    @RequestMapping(value = "/cache", method = RequestMethod.GET)
    @ResponseBody
    public ResultData cache(@RequestParam(required = false, defaultValue = "24") String action

    ) {
        try {
            umsMemberIntegrationRuleSettingService.cache(action);
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }

}

