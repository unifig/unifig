package com.unifig.organ.controller.admin;


import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.organ.model.OmsShopStaff;
import com.unifig.organ.service.OmsShopStaffService;
import com.unifig.result.Rest;
import com.unifig.result.RestList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>
 * 店铺员工表 前端控制器
 * </p>
 *
 *
 * @since 2019-03-11
 */
@RestController
@RequestMapping("/oms/shop/staff")
@Api(tags = "店铺员工", description = "OmsShopStaffController")
@ApiIgnore
public class OmsShopStaffController {

    @Autowired
    private OmsShopStaffService omsShopStaffService;

    @ApiOperation(value = "保存员工")
    @PostMapping(value = "/save")
    public Rest<OmsShopStaff> save(@RequestBody OmsShopStaff OmsShopStaff) {
        OmsShopStaff shop = omsShopStaffService.save(OmsShopStaff);
        return Rest.resultData(new OmsShopStaff()).setData(shop);
    }

    @ApiOperation(value = "保存员工集合")
    @PostMapping(value = "/saveList")
    public RestList<OmsShopStaff> saveList(@RequestBody List<OmsShopStaff> OmsShopStaff) {
        List<OmsShopStaff> shop = omsShopStaffService.saveList(OmsShopStaff);
        return RestList.resultData(new OmsShopStaff()).setData(shop);
    }

    @ApiOperation(value = "修改员工")
    @PostMapping(value = "/update")
    public Rest<OmsShopStaff> update(@RequestBody OmsShopStaff OmsShopStaff) {
        OmsShopStaff shop = omsShopStaffService.updateShop(OmsShopStaff);
        return Rest.resultData(new OmsShopStaff()).setData(shop);
    }

    @ApiOperation(value = "查看详情")
    @GetMapping(value = "/select/{id}")
    public Rest<OmsShopStaff> selectByShopId(@PathVariable(value="id") String id) {
        OmsShopStaff shop = omsShopStaffService.selectByShopId(id);
        return Rest.resultData(new OmsShopStaff()).setData(shop);
    }

    @ApiOperation(value = "店铺员工列表")
    @GetMapping(value = "/select")
    public RestList<OmsShopStaff> selectShopList(@RequestParam(required = false, defaultValue = "0") Integer page,
                                            @RequestParam(required = false, defaultValue = "10") Integer rows,
                                            @ApiParam("店铺id") @RequestParam(required = true, defaultValue = "1") String shopId) {
        Page<OmsShopStaff> pageList =  omsShopStaffService.selectShopList(page,rows,shopId);
        return RestList.resultData(new OmsShopStaff()).setData(pageList.getRecords()).setCount((int)pageList.getTotal());
    }




}

