package com.unifig.organ.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.CommonResult;
import com.unifig.organ.model.UmsMemberReceiveAddress;
import com.unifig.organ.service.UmsMemberReceiveAddressService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员收货地址管理Controller
 *    on 2018/8/28.
 */
@Controller
@Api(tags = "会员收货地址", description = "会员收货地址管理")
@RequestMapping("/member/address")
public class UmsMemberReceiveAddressController {

    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;


    @ApiOperation("添加收货地址")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<UmsMemberReceiveAddress> add(@RequestBody UmsMemberReceiveAddress address, @CurrentUser UserCache userCache) {
        int count = memberReceiveAddressService.add(address, userCache.getUserId());
        if (count > 0) {
            return ResultData.result(true).setData(address);
        }
        return ResultData.result(false).setData(address);
    }

    @ApiOperation("删除收货地址")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<UmsMemberReceiveAddress> delete(@PathVariable Long id, @CurrentUser UserCache userCache) {
        int count = memberReceiveAddressService.delete(id, userCache.getUserId());
        if (count > 0) {
            return ResultData.result(true).setData(id);
        }
        return ResultData.result(false).setData(id);
    }

    @ApiOperation("修改收货地址")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResultData update(@PathVariable Long id, @RequestBody UmsMemberReceiveAddress address, @CurrentUser UserCache userCache) {
        int count = memberReceiveAddressService.update(id, address, userCache.getUserId());
        if (count > 0) {
            return ResultData.result(true).setData(id);
        }
        return ResultData.result(false).setData(id);
    }

    @ApiOperation("显示所有收货地址")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsMemberReceiveAddress> list(@CurrentUser UserCache userCache) {
        List<UmsMemberReceiveAddress> addressList = memberReceiveAddressService.list(userCache.getUserId());
        return ResultData.result(true).setData(addressList);
    }

    @ApiOperation("收货地址详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public  ResultData<UmsMemberReceiveAddress> getItem(@PathVariable Long id, @CurrentUser UserCache userCache) {
        UmsMemberReceiveAddress address = memberReceiveAddressService.getItem(id, userCache.getUserId());
        return ResultData.result(true).setData(address);
    }

    @ApiOperation("设为默认收获地址")
    @RequestMapping(value = "/defaultStatus/{id}", method = RequestMethod.GET)
    @ResponseBody
    public  ResultData<UmsMemberReceiveAddress> defaultStatus(@PathVariable Long id, @CurrentUser UserCache userCache) {
        UmsMemberReceiveAddress address = memberReceiveAddressService.defaultStatus(id, userCache.getUserId());
        return ResultData.result(true).setData(address);
    }
}
