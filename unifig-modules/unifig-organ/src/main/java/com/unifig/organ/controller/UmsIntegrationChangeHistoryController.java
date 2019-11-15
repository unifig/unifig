package com.unifig.organ.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.annotation.CurrentUser;
import com.unifig.context.Constants;
import com.unifig.entity.cache.UserCache;
import com.unifig.model.UmsMember;
import com.unifig.organ.model.UmsIntegrationChangeHistory;
import com.unifig.organ.service.UmsIntegrationChangeHistoryService;
import com.unifig.organ.service.UmsMemberService;
import com.unifig.organ.vo.UmsIrchVo;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

/**
 * <p>
 * 积分变化历史记录表 前端控制器
 * </p>
 *
 *
 * @since 2019-01-28
 */
@Api(tags = "用户积分记录", description = "用户积分")
@RestController
@RequestMapping("/ums/irth")
public class UmsIntegrationChangeHistoryController {
    @Autowired
    private UmsIntegrationChangeHistoryService umsIntegrationChangeHistoryService;

    @Autowired
    private UmsMemberService umsMemberService;

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("列表数据查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsIntegrationChangeHistory> list(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                        @RequestParam(required = false, defaultValue = "2") Integer rows, @CurrentUser UserCache userCache) {
        try {
            EntityWrapper<UmsIntegrationChangeHistory> umsIntegrationChangeHistoryWrapper = new EntityWrapper<UmsIntegrationChangeHistory>();
            umsIntegrationChangeHistoryWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            umsIntegrationChangeHistoryWrapper.eq("member_id", userCache.getUserId());
            umsIntegrationChangeHistoryWrapper.orderBy("create_time", false);
            Page<UmsIntegrationChangeHistory> umsIntegrationChangeHistoryPage = umsIntegrationChangeHistoryService.selectPage(new Page<UmsIntegrationChangeHistory>(page, rows), umsIntegrationChangeHistoryWrapper);
            int count = umsIntegrationChangeHistoryService.selectCount(umsIntegrationChangeHistoryWrapper);

            return ResultData.result(true).setData(umsIntegrationChangeHistoryPage.getRecords()).setCount(count);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }


    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("微信小程序-用户积分明细查询")
    @RequestMapping(value = "/wx/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsIntegrationChangeHistory> wxList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                          @RequestParam(required = false, defaultValue = "2") Integer rows, @CurrentUser UserCache userCache) {
        try {
            EntityWrapper<UmsIntegrationChangeHistory> umsIntegrationChangeHistoryWrapper = new EntityWrapper<UmsIntegrationChangeHistory>();
            umsIntegrationChangeHistoryWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            umsIntegrationChangeHistoryWrapper.eq("member_id", userCache.getUserId());
            umsIntegrationChangeHistoryWrapper.orderBy("create_time", false);
            Page<UmsIntegrationChangeHistory> umsIntegrationChangeHistoryPage = umsIntegrationChangeHistoryService.selectPage(new Page<UmsIntegrationChangeHistory>(page, rows), umsIntegrationChangeHistoryWrapper);
            int count = umsIntegrationChangeHistoryService.selectCount(umsIntegrationChangeHistoryWrapper);
            int useIntegration = umsIntegrationChangeHistoryService.selectUse(userCache.getUserId());
            UmsMember umsMember = umsMemberService.selectById(userCache.getUserId());
            UmsIrchVo umsIrchVo = new UmsIrchVo();
            umsIrchVo.setHistory(umsIntegrationChangeHistoryPage.getRecords());
            umsIrchVo.setIntegration(umsMember.getIntegration());
            umsIrchVo.setLockIntegration(umsMember.getLockIntegration());
            umsIrchVo.setIntegrationCount(umsMember.getLockIntegration() + umsMember.getIntegration());
            double d = useIntegration;
            umsIrchVo.setUse(d / 100);
            return ResultData.result(true).setData(umsIrchVo).setCount(count);
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
    public ResultData<UmsIntegrationChangeHistory> info(@RequestParam(required = true, defaultValue = "24") String id

    ) {
        try {
            UmsIntegrationChangeHistory umsIntegrationChangeHistory = umsIntegrationChangeHistoryService.selectById(String.valueOf(id));
            return ResultData.result(true).setData(umsIntegrationChangeHistory);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }

    /**
     * 根新积分记录状态 为用户加积分
     *
     * @return
     */
    @ApiOperation("根新积分状态 为用户加积分")
    @RequestMapping(value = "ustatus", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsIntegrationChangeHistory> ustatus(@RequestParam(required = true, defaultValue = "24") String id

    ) {
        try {
            UmsIntegrationChangeHistory umsIntegrationChangeHistory = umsIntegrationChangeHistoryService.selectById(String.valueOf(id));
            umsIntegrationChangeHistory.setStatus(Constants.DEFAULT_VAULE_ONE);
            umsIntegrationChangeHistoryService.updateById(umsIntegrationChangeHistory);
            umsMemberService.updateIntegration(Long.valueOf(umsIntegrationChangeHistory.getMemberId()), umsIntegrationChangeHistory.getChangeCount());

            return ResultData.result(true).setData(umsIntegrationChangeHistory);
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
    public ResultData<UmsIntegrationChangeHistory> iou(@RequestBody UmsIntegrationChangeHistory umsIntegrationChangeHistory) {
        try {
            if (umsIntegrationChangeHistory == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            //补充其他数据
            String id = umsIntegrationChangeHistory.getId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (id == null) {
                umsIntegrationChangeHistory.setCreateTime(now);
            }
            umsIntegrationChangeHistory.setEditTime(now);
            umsIntegrationChangeHistory.setEnable(Constants.DEFAULT_VAULE_ONE);
            umsIntegrationChangeHistoryService.insertOrUpdate(umsIntegrationChangeHistory);
            return ResultData.result(true).setData(umsIntegrationChangeHistory);
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
            UmsIntegrationChangeHistory umsIntegrationChangeHistory = umsIntegrationChangeHistoryService.selectById(String.valueOf(id));
            if (umsIntegrationChangeHistory == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            umsIntegrationChangeHistory.setEnable(Constants.DEFAULT_VAULE_ZERO);
            //更新状态
            umsIntegrationChangeHistoryService.updateById(umsIntegrationChangeHistory);
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }

}

