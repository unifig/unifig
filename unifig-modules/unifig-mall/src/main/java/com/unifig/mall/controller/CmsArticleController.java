package com.unifig.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.CmsArticle;
import com.unifig.mall.bean.model.CmsArticleCategory;
import com.unifig.mall.bean.model.CmsArticleProduct;
import com.unifig.mall.bean.vo.CmsArticleInfoVo;
import com.unifig.mall.bean.vo.CmsArticleListVo;
import com.unifig.mall.service.CmsArticleCategoryService;
import com.unifig.mall.service.CmsArticleProductService;
import com.unifig.mall.service.CmsArticleService;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import com.unifig.utils.BeanMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 文章表 前端控制器
 * </p>
 *
 *
 * @since 2019-01-22
 */
@RestController
@Api(tags = "文章", description = "文章")
@RequestMapping("/cms/article")
@ApiIgnore
public class CmsArticleController {

    @Autowired
    private CmsArticleService cmsArticleService;

    @Autowired
    private CmsArticleCategoryService cmsArticleCategoryService;

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
                           @RequestParam(required = false, defaultValue = "2") Integer rows
    ) {
        try {
            EntityWrapper<CmsArticle> cmsArticleWrapper = new EntityWrapper<CmsArticle>();
            cmsArticleWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            Page<CmsArticle> cmsArticlePage = cmsArticleService.selectPage(new Page<CmsArticle>(page, rows), cmsArticleWrapper);
            int count = cmsArticleService.selectCount(cmsArticleWrapper);
            return ResultData.result(true).setData(cmsArticlePage.getRecords()).setCount(count);
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
            CmsArticle cmsArticle = cmsArticleService.selectById(String.valueOf(id));
            if (cmsArticle == null) {
                return ResultData.result(false).setMsg("数据不存在");
            }
            CmsArticleInfoVo cmsArticleInfoVo = new CmsArticleInfoVo();
            if (cmsArticle != null) {
                BeanMapUtils.copyBean2Bean(cmsArticleInfoVo, cmsArticle);
            }
            List<CmsArticleProduct> cmsArticleProducts = cmsArticleProductService.selectListByArticleId(cmsArticle.getId());
            cmsArticleInfoVo.setProductList(cmsArticleProducts);
            Integer click = cmsArticle.getClick();
            if (click == null) {
                click=0;
            }
            cmsArticle.setClick(cmsArticle.getClick()+click);
            cmsArticleService.insertOrUpdate(cmsArticle);
            return ResultData.result(true).setData(cmsArticleInfoVo);
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
    public ResultData iou(@RequestBody CmsArticle cmsArticle) {
        try {
            if (cmsArticle == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            //补充其他数据
            String id = cmsArticle.getId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (id == null) {
                cmsArticle.setCreateTime(now);
                cmsArticle.setEditTime(now);
                CmsArticleCategory cmsArticleCategory = cmsArticleCategoryService.selectById(cmsArticle.getCatId());
                if (cmsArticleCategory != null) {
                    cmsArticle.setCatName(cmsArticleCategory.getCatName());

                }
            }
            cmsArticle.setEditTime(now);
            cmsArticle.setEnable(Constants.DEFAULT_VAULE_ONE);
            cmsArticleService.insertOrUpdate(cmsArticle);
            List<CmsArticleProduct> productList = cmsArticle.getProductList();
            for (CmsArticleProduct cmsArticleProduct : productList) {
                String cmsArticleProductId = cmsArticleProduct.getId();
                if (cmsArticleProductId == null) {
                    cmsArticleProduct.setCreateTime(now);
                }
                cmsArticleProduct.setEditTime(now);
                cmsArticleProduct.setEnable(Constants.DEFAULT_VAULE_ONE);
                cmsArticleProduct.setArticleId(cmsArticle.getId());
                /*if(cmsArticleProduct.getId() == null){
                    cmsArticleProductService.insert(cmsArticleProduct);
                }*/
            }
            cmsArticleProductService.insertOrUpdateBatch(productList);
            return ResultData.result(true).setData(cmsArticle);
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
            CmsArticle cmsArticle = cmsArticleService.selectById(String.valueOf(id));
            if (cmsArticle == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            cmsArticle.setEnable(Constants.DEFAULT_VAULE_ZERO);
            //更新状态
            cmsArticleService.updateById(cmsArticle);
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }

    /**
     * 删除 关联商品
     *
     * @return
     */
    @ApiOperation("删除 关联商品")
    @RequestMapping(value = "del/ap", method = RequestMethod.GET)
    public ResultData delcap(@RequestParam String id
    ) {
        try {
            CmsArticleProduct cmsArticleProduct = cmsArticleProductService.selectById(id);
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


    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("分类查询列表数据")
    @RequestMapping(value = "/catlist", method = RequestMethod.GET)
    @ResponseBody
    public ResultData catlist(@RequestParam(required = false, defaultValue = "1") Integer page,
                              @RequestParam(required = false, defaultValue = "10") Integer rows,
                              @ApiParam("1首页推荐文章 2视频文章 3图片文章 4 首页活动(转发得积分) 5首页活动一元团购 6商城banner 7素材banner") @RequestParam(required = true) Integer catId
    ) {
        try {
            List<CmsArticleListVo> cmsArticleListVos = cmsArticleService.selectByCatId(page, rows, catId);
            return ResultData.result(true).setData(cmsArticleListVos);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }

    @ApiOperation("修改文章推荐状态")
    @RequestMapping(value = "/update/navStatus", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('cms:articleCategory:update')")
    public Object updateNavStatus(@RequestParam("ids") String[] ids, @RequestParam("navStatus") Integer navStatus) {
        int count = cmsArticleService.updateNavStatus(Arrays.asList(ids), navStatus);
        if (count > 0) {
            return ResultData.result(true).setCount(count);
        } else {
            return ResultData.result(false);
        }
    }

}

