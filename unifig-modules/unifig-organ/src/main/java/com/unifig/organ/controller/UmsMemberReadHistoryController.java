package com.unifig.organ.controller;

import com.unifig.organ.domain.CommonResult;
import com.unifig.organ.domain.UmsMemberReadHistory;
import com.unifig.organ.service.UmsMemberReadHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 会员商品浏览记录管理Controller
 *    on 2018/8/3.
 */
@Controller
@Api(tags = "会员商品浏览记录", description = "会员商品浏览记录管理")
@RequestMapping("/cms/member/readHistory")
@ApiIgnore
public class UmsMemberReadHistoryController {
    @Autowired
    private UmsMemberReadHistoryService memberReadHistoryService;

    @ApiOperation("创建浏览记录")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Object create(@RequestBody UmsMemberReadHistory memberReadHistory) {
        int count  = memberReadHistoryService.create(memberReadHistory);
        if(count>0){
            return new CommonResult().success(count);
        }else{
            return new CommonResult().failed();
        }
    }

    @ApiOperation("删除浏览记录")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Object delete(@RequestParam("ids") List<String> ids) {
        int count  = memberReadHistoryService.delete(ids);
        if(count>0){
            return new CommonResult().success(count);
        }else{
            return new CommonResult().failed();
        }
    }

    @ApiOperation("展示浏览记录")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Object list(Long memberId) {
        List<UmsMemberReadHistory> memberReadHistoryList = memberReadHistoryService.list(memberId);
        return new CommonResult().success(memberReadHistoryList);
    }
}
