package com.unifig.organ.controller.admin;


import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.organ.model.OmsShop;
import com.unifig.organ.service.OmsShopService;
import com.unifig.result.Rest;
import com.unifig.result.RestList;
import com.unifig.result.ResultData;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 店铺表 前端控制器
 * </p>
 *
 *
 * @since 2019-03-11
 */
@RestController
@RequestMapping("/oms/shop")
@Api(tags = "店铺", description = "OmsShopController")

public class OmsShopController {

    @Autowired
    private OmsShopService omsShopService;

    @ApiOperation(value = "保存")
    @PostMapping(value = "/save")
    public Rest<OmsShop> save(@RequestBody OmsShop omsShop) {
        OmsShop shop = omsShopService.save(omsShop);
        return Rest.resultData(new OmsShop()).setData(shop);
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "/update")
    public Rest<OmsShop> update(@RequestBody OmsShop omsShop) {
        OmsShop shop = omsShopService.updateShop(omsShop);
        return Rest.resultData(new OmsShop()).setData(shop);
    }

    @ApiOperation(value = "关闭店铺")
    @GetMapping(value = "/delete")
    public ResultData delete(@ApiParam("店铺id集合") @RequestParam String ids) {
        return omsShopService.deleteShop(ids);
    }

    @ApiOperation(value = "启用店铺")
    @GetMapping(value = "/open")
    public ResultData open(@ApiParam("店铺id集合") @RequestParam String ids) {
        return omsShopService.openShop(ids);
    }

    @ApiOperation(value = "查看详情")
    @GetMapping(value = "/select/{id}")
    public Rest<OmsShop> selectByShopId(@PathVariable(value="id") String id) {
        OmsShop shop = omsShopService.selectByShopId(id);
        return Rest.resultData(new OmsShop()).setData(shop);
    }

    @ApiOperation(value = "店铺列表")
    @GetMapping(value = "/select")
    public RestList<OmsShop> selectShopList(@RequestParam(required = false, defaultValue = "0") Integer page,
                                            @RequestParam(required = false, defaultValue = "10") Integer rows,
                                            @ApiParam("平台id") @RequestParam String terraceId,
                                            @ApiParam("店铺名称") @RequestParam String name) {
        Page<OmsShop> pageList =  omsShopService.selectShopList(page,rows,terraceId,name);
        return RestList.resultData(new OmsShop()).setData(pageList.getRecords()).setCount((int)pageList.getTotal());
    }
}

