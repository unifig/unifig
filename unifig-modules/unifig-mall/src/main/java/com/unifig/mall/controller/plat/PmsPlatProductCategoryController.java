package com.unifig.mall.controller.plat;

import com.unifig.mall.service.PmsProductCategoryService;
import com.unifig.mall.bean.vo.PmsProductCategoryVo;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 商品分类模块Controller
 *    on 2018/4/26.
 */
@RestController
@Api(tags = "小程序-商品分类", description = "商品分类")
@RequestMapping("/client/productCategory")
@ApiIgnore
public class PmsPlatProductCategoryController {
    @Autowired
    private PmsProductCategoryService productCategoryService;



    @ApiOperation("分页查询商品分类")
    @RequestMapping(value = "/list/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getClientList(@PathVariable Long parentId) {
        List<PmsProductCategoryVo> pmsProductCategoryVos = productCategoryService.getClientList(parentId);
        return ResultData.result(true).setData(pmsProductCategoryVos);
    }
}
