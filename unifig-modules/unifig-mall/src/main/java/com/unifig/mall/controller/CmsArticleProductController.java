package com.unifig.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.annotation.CurrentUser;
import com.unifig.context.Constants;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.model.CmsArticleProduct;
import com.unifig.mall.service.CmsArticleProductService;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.Timestamp;

/**
 * <p>
 * 文章商品关联表 前端控制器
 * </p>
 *
 *
 * @since 2019-02-18
 */
@Controller
@RequestMapping("/cms/article/product")
@ApiIgnore
public class CmsArticleProductController {

    @Autowired
    private CmsArticleProductService cmsArticleProductService;
    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("列表数据查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData list(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "2") Integer rows, @CurrentUser UserCache userCache) {
        try {
            EntityWrapper<CmsArticleProduct> cmsArticleProductWrapper = new EntityWrapper<CmsArticleProduct>();
            cmsArticleProductWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            Page<CmsArticleProduct> cmsArticleProductPage = cmsArticleProductService.selectPage(new Page<CmsArticleProduct>(page, rows), cmsArticleProductWrapper);
            int count = cmsArticleProductService.selectCount(cmsArticleProductWrapper);
            return ResultData.result(true).setData(cmsArticleProductPage.getRecords()).setCount(count);
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
            CmsArticleProduct cmsArticleProduct = cmsArticleProductService.selectById(String.valueOf(id));
            return ResultData.result(true).setData(cmsArticleProduct);
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
    public ResultData iou(@RequestBody CmsArticleProduct cmsArticleProduct) {
        try {
            if (cmsArticleProduct == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            //补充其他数据
            String id = cmsArticleProduct.getId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (id == null) {
                cmsArticleProduct.setCreateTime(now);
            }
            cmsArticleProduct.setEditTime(now);
            cmsArticleProduct.setEnable(Constants.DEFAULT_VAULE_ONE);
            cmsArticleProductService.insertOrUpdate(cmsArticleProduct);
            return ResultData.result(true).setData(cmsArticleProduct);
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
            CmsArticleProduct cmsArticleProduct = cmsArticleProductService.selectById(String.valueOf(id));
            if (cmsArticleProduct == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            cmsArticleProduct.setEnable(Constants.DEFAULT_VAULE_ZERO);
            //更新状态
            cmsArticleProductService.updateById(cmsArticleProduct);
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }
}

