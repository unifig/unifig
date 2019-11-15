package com.unifig.organ.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.UmsMemberCollectionArticle;
import com.unifig.organ.service.UmsCollectionArticleService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员文章收藏Controller
 *    on 2018/8/2
 */
@Controller
@Api(tags = "会员文章收藏")
@RequestMapping("/ums/article/collection")
public class UmsCollectionArticleController {
    @Autowired
    private UmsCollectionArticleService memberCollectionService;

    @ApiOperation("添加文章收藏")
    @RequestMapping(value = "/addArticle", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<UmsMemberCollectionArticle> addArticle(@RequestBody UmsMemberCollectionArticle ArticleCollection, @CurrentUser UserCache userCache) {
        ArticleCollection.setMemberId(userCache.getUserId());
        ArticleCollection.setCreateTime(new Date());
        int count = memberCollectionService.addArticle(ArticleCollection);
        if (count > 0) {
            return ResultData.result(true).setData(ArticleCollection);
        } else {
            return ResultData.result(false).setData(ArticleCollection);
        }
    }

    @ApiOperation("删除收藏文章")
    @RequestMapping(value = "/delete/{articleId}", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<UmsMemberCollectionArticle> deleteArticle(@CurrentUser UserCache userCache,@PathVariable String articleId) {
        int count = memberCollectionService.deleteArticle(userCache.getUserId(), articleId);
        if (count > 0) {
            return ResultData.result(true).setData(articleId);
        } else {
            return ResultData.result(false).setData(articleId);
        }
    }

    @ApiOperation("收藏列表")
    @RequestMapping(value = "/listArticle", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsMemberCollectionArticle> listArticle(@CurrentUser UserCache userCache) {
        List<UmsMemberCollectionArticle> memberArticleCollectionList = memberCollectionService.listArticle(userCache.getUserId());
        return ResultData.result(true).setData(memberArticleCollectionList);
    }


    @ApiOperation("显示首页列表")
    @RequestMapping(value = "/listArticle/index", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsMemberCollectionArticle> listArticleIndex(@CurrentUser UserCache userCache) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<UmsMemberCollectionArticle> memberArticleCollectionList = memberCollectionService.listArticleIndex(userCache.getUserId());
        long count = memberCollectionService.countArticle(userCache.getUserId());
        result.put("list", memberArticleCollectionList);
        result.put("count", count);
        return ResultData.result(true).setData(result);

    }


}
