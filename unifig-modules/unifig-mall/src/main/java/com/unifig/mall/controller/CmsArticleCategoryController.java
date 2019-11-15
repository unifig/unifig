package com.unifig.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.CmsArticleCategory;
import com.unifig.mall.service.CmsArticleCategoryService;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.Timestamp;
import java.util.Arrays;

/**
 * <p>
 * 文章分类表 前端控制器
 * </p>
 *
 *
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/cms/articleCategory")
@ApiIgnore
public class CmsArticleCategoryController {
    @Autowired
    private CmsArticleCategoryService cmsArticleService;

    /**
     * 列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData list(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "10") Integer rows
    ) {
        try {
            EntityWrapper<CmsArticleCategory> cmsArticleCategoryWrapper = new EntityWrapper<CmsArticleCategory>();
            cmsArticleCategoryWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            Page<CmsArticleCategory> cmsArticleCategoryPage = cmsArticleService.selectPage(new Page<CmsArticleCategory>(page, rows), cmsArticleCategoryWrapper);
            int count = cmsArticleService.selectCount(cmsArticleCategoryWrapper);
            return ResultData.result(true).setData(cmsArticleCategoryPage.getRecords()).setCount(count);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }

    /**
     * 详情
     *
     * @return
     */
    @RequestMapping(value = "info", method = RequestMethod.GET)
    @ResponseBody
    public ResultData info(@RequestParam(required = true, defaultValue = "1") String id

    ) {
        try {
            CmsArticleCategory cmsArticleCategory = cmsArticleService.selectById(String.valueOf(id));
            return ResultData.result(true).setData(cmsArticleCategory);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }


    /**
     * 加班 新建 更新
     *
     * @return
     */
    @RequestMapping(value = "iou", method = RequestMethod.POST)
    public ResultData iou(@RequestBody CmsArticleCategory cmsArticleCategory

    ) {
        try {
            if (cmsArticleCategory == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            //补充其他数据
            String id = cmsArticleCategory.getId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (id == null) {
                cmsArticleCategory.setCreateTime(now);
            }
            cmsArticleCategory.setEditTime(now);
            cmsArticleCategory.setEnable(Constants.DEFAULT_VAULE_ONE);
            cmsArticleService.insertOrUpdate(cmsArticleCategory);
            return ResultData.result(true).setData(cmsArticleCategory);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.GET)
    public ResultData del(@RequestParam String id
    ) {
        try {
            CmsArticleCategory cmsArticleCategory = cmsArticleService.selectById(String.valueOf(id));
            if (cmsArticleCategory == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            cmsArticleCategory.setEnable(Constants.DEFAULT_VAULE_ZERO);
            //更新状态
            cmsArticleService.updateById(cmsArticleCategory);
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }


    @ApiOperation("修改导航栏显示状态")
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

    @ApiOperation("修改显示状态")
    @RequestMapping(value = "/update/showStatus", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('cms:articleCategory:update')")
    public Object updateShowStatus(@RequestParam("ids") String[] ids, @RequestParam("showStatus") Integer showStatus) {
        int count = cmsArticleService.updateShowStatus(Arrays.asList(ids), showStatus);
        if (count > 0) {
            return ResultData.result(true).setCount(count);
        } else {
            return ResultData.result(false);
        }
    }
}

