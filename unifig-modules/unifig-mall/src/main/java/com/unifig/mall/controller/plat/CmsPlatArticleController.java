package com.unifig.mall.controller.plat;


import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.CmsArticle;
import com.unifig.mall.bean.model.CmsArticleProduct;
import com.unifig.mall.bean.vo.CmsArticleCategoryVo;
import com.unifig.mall.bean.vo.CmsArticleInfoVo;
import com.unifig.mall.bean.vo.HomeCmsArticleVo;
import com.unifig.mall.service.CmsArticleCategoryService;
import com.unifig.mall.service.CmsArticleProductService;
import com.unifig.mall.service.CmsArticleService;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import com.unifig.utils.BeanMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@Api(tags = "文章")
@RequestMapping("/client/article")
public class CmsPlatArticleController {

    @Autowired
    private CmsArticleService cmsArticleService;

    @Autowired
    private CmsArticleCategoryService cmsArticleCategoryService;

    @Autowired
    private CmsArticleProductService cmsArticleProductService;

    /**
     * 列表-分类列表
     *
     * @return
     */
    @ApiOperation("列表-分类列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<HomeCmsArticleVo> list(@RequestParam(required = false, defaultValue = "1") Integer page,
                                             @RequestParam(required = false, defaultValue = "2") Integer rows,
                                             @RequestParam(required = false) String catId,
                                             @RequestParam(required = false) String title
    ) {
        try {
            List<HomeCmsArticleVo> homeCmsArticleVo = cmsArticleService.selectPageByCatId(new Page<CmsArticle>(page, rows), catId, title);
            return ResultData.result(true).setData(homeCmsArticleVo);

        } catch (Exception e) {
            return ResultData.result(false);

        }

    }


    /**
     * 所有分类列表
     *
     * @return
     */
    @ApiOperation("所有分类列表")
    @RequestMapping(value = "/cat/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<CmsArticleCategoryVo> catList(
            @RequestParam(required = false) String catName
    ) {
        try {
            List<CmsArticleCategoryVo> cmsArticleCategoryVos = cmsArticleCategoryService.selectList(catName);
            return ResultData.result(true).setData(cmsArticleCategoryVos);

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
    public ResultData<CmsArticleInfoVo> info(@RequestParam(required = true) String id

    ) {
        try {
            CmsArticle cmsArticle = cmsArticleService.selectById(String.valueOf(id));
            if (cmsArticle == null) {
                return ResultData.result(false);
            }
            CmsArticleInfoVo cmsArticleInfoVo = new CmsArticleInfoVo();
            BeanMapUtils.copyBean2Bean(cmsArticleInfoVo, cmsArticle);
            List<CmsArticleProduct> cmsArticleProducts = cmsArticleProductService.selectListByArticleId(cmsArticle.getId());
            cmsArticleInfoVo.setProductList(cmsArticleProducts);
            //查询对应推荐商品信息
            return ResultData.result(true).setData(cmsArticleInfoVo);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }


    /**
     * 点赞
     *
     * @return
     */
    @ApiOperation("点赞")
    @RequestMapping(value = "like", method = RequestMethod.GET)
    public ResultData like(@RequestParam String id
    ) {
        try {
            CmsArticle cmsArticle = cmsArticleService.selectById(String.valueOf(id));
            if (cmsArticle == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            Integer like = cmsArticle.getLike();
            if (like == null) {
                like = Constants.DEFAULT_VAULE_ZERO;
            }
            like=like+1;
            cmsArticle.setLike(like);
            //更新状态
            cmsArticleService.updateById(cmsArticle);
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }

}

