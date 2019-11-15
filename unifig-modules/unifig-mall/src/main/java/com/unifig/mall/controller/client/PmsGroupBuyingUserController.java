package com.unifig.mall.controller.client;


import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.domain.OrderParam;
import com.unifig.mall.bean.vo.PmsGroupBuyingUserVo;
import com.unifig.mall.service.PmsGroupBuyingUserService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 用户参团记录 前端控制器
 * </p>
 *
 *
 * @since 2019-06-25
 */
@RestController
@RequestMapping("/pmsGroupBuyingUser")
@Api(tags = "用户参团管理", description = "PmsGroupBuyingUserController")
public class PmsGroupBuyingUserController {

    @Autowired
    private PmsGroupBuyingUserService pmsGroupBuyingUserService;

    @ApiOperation("参团列表")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型：1->发团；2->参团；",defaultValue = "1", allowableValues = "1,2", paramType = "query", dataType = "integer")
    })
    public ResultData<PmsGroupBuyingUserVo> list(@RequestParam(required = false, defaultValue = "0")Integer type,
                                                 @CurrentUser UserCache userCache,
                                                 @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){
        return pmsGroupBuyingUserService.list(pageNum,pageSize,type,userCache.getUserId());
    }

}

