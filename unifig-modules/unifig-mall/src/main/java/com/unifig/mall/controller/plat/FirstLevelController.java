package com.unifig.mall.controller.plat;

import com.unifig.context.Constants;
import com.unifig.mall.bean.vo.*;
import com.unifig.mall.service.CmsArticleCategoryService;
import com.unifig.mall.service.CmsArticleService;
import com.unifig.mall.service.PmsProductCategoryService;
import com.unifig.mall.service.SmsHomeAdvertiseService;
import com.unifig.result.ResultData;
import com.unifig.utils.Query;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品专题Controller
 *    on 2018/6/1.
 */
@RestController
@Api(tags = "微信小程序", description = "一级页面")
@RequestMapping("/first/level")
public class FirstLevelController {
    @Autowired
    private CmsArticleService cmsArticleService;

    @Autowired
    private SmsHomeAdvertiseService advertiseService;


    @Autowired
    private CmsArticleCategoryService cmsArticleCategoryService;

    @Autowired
    private PmsProductCategoryService pmsProductCategoryService;

    /**
     * 首页
     * /
     *
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("小程序-首页")
    @RequestMapping(value = "/home/wxsapp", method = RequestMethod.GET)
    public ResultData<HomeWXSappVo> home(@RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("page", page);
            params.put("limit", size);
            Query query = new Query(params);

            HomeWXSappVo homeWXSappVo = new HomeWXSappVo();
            //首页文章列表
            List<HomeCmsArticleVo> cmsArticleHomeVos = cmsArticleService.selectHomeList(query);
            //首页轮播图列表
            List<HomeSmsAdvertiseVo> homeSmsAdvertiseVos = advertiseService.selectHomeListByType(new Date(System.currentTimeMillis()), Constants.DEFAULT_VAULE_TOW);
            //文章分类 HomeCmsArticleCategoryVo
            List<HomeCmsArticleCategoryVo> homeCmsArticleCategoryVos = cmsArticleCategoryService.selectHomeList();
            homeWXSappVo.setCmsArticleHomeVos(cmsArticleHomeVos);
            homeWXSappVo.setHomeCmsArticleCategoryVos(homeCmsArticleCategoryVos);
            homeWXSappVo.setHomeSmsAdvertiseVos(homeSmsAdvertiseVos);
            return ResultData.result(true).setData(homeWXSappVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.result(false);
        }
    }


    /**
     * 首页
     * /
     *
     * @return
     */
    @ApiOperation("h5商城")
    @RequestMapping(value = "/home/app", method = RequestMethod.GET)
    public ResultData homeApp() {
        try {
            Map<String, Object> homeMap = new HashMap<String, Object>();

            Date now = new Date(System.currentTimeMillis());
            //轮播
            List<HomeSmsAdvertiseVo> homeSmsAdvertiseVosOne = advertiseService.selectHomeListByType(now, Constants.DEFAULT_VAULE_ONE);

            //导航
            List<HomePmsProductCategoryVo> pmsProductCategoryVos = pmsProductCategoryService.selectHomeList();

            homeMap.put("naviga", pmsProductCategoryVos);//导航
            homeMap.put("ad", homeSmsAdvertiseVosOne);//轮播
            return ResultData.result(true).setData(homeMap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 首页
     * /
     *
     * @return
     */
    @ApiOperation("首页-商品分类")
    @RequestMapping(value = "/home/category", method = RequestMethod.GET)
    public ResultData homeCategoryList() {
        try {
            //导航
            List<HomePmsProductCategoryVo> pmsProductCategoryVos = pmsProductCategoryService.selectHomeList();
            return ResultData.result(true).setData(pmsProductCategoryVos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
