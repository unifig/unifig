package com.unifig.organ.controller.client;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.model.UmsMember;
import com.unifig.organ.feign.SandCouponFeignClient;
import com.unifig.organ.feign.SandOrderFeignClient;
import com.unifig.organ.service.UmsCollectionProductService;
import com.unifig.organ.service.UmsMemberService;
import com.unifig.organ.vo.UserIndexVo;
import com.unifig.result.MsgCode;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 商品专题Controller
 *    on 2018/6/1.
 */
@RestController
@Api(tags = "小程序-一级页面", description = "一级页面")
@RequestMapping("/first/level")
public class FirstLevelController {

    private static Logger logger = LoggerFactory.getLogger(FirstLevelController.class);

    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private UmsCollectionProductService memberCollectionService;

    @Autowired
    private SandCouponFeignClient sandCouponFeignClient;

    @Autowired
    private SandOrderFeignClient sandOrderFeignClient;


    @ApiOperation("小程序-我的")
    @RequestMapping(value = "/my/wxsapp", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UserIndexVo> myWxapp(@CurrentUser UserCache userCache) {
        if (userCache == null) {
            return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
        }
        logger.info("FirstLevelController myWxapp userId:{}", userCache.getUserId());

        try {
            UmsMember umsMember = memberService.selectById(Long.valueOf(userCache.getUserId()));
            if (umsMember == null) {
                return ResultData.result(false).setCode(MsgCode.USER_NOT_FOUND);
            }
            // int fans = memberService.countFans(userCache.getUserId());
            //List<UmsMemberProductCollection> memberProductCollectionList = memberCollectionService.listProductIndex(Long.valueOf(userCache.getUserId()));
            long count = memberCollectionService.countProduct(Long.valueOf(userCache.getUserId()));
            UserIndexVo userIndexVo = new UserIndexVo();
            userIndexVo.setProductCollectionCount((int) count);
            userIndexVo.setFans(0);
            userIndexVo.setFollow(0);
            userIndexVo.setGender(umsMember.getGender());
            userIndexVo.setMobile(umsMember.getMobile());
            userIndexVo.setNickname(umsMember.getNickname());
            userIndexVo.setInvitCode(umsMember.getInvitCode());
            userIndexVo.setMobile(umsMember.getMobile());
            userIndexVo.setBalance(0.0);
            userIndexVo.setCoupon(0);
            Integer integration = umsMember.getIntegration();
            Integer lockIntegration = umsMember.getLockIntegration();
            userIndexVo.setIntegration(integration);
            userIndexVo.setIntegrationCount(integration + lockIntegration);
            Integer couponCount = sandCouponFeignClient.couponCount();
            userIndexVo.setCoupon(couponCount);
            Map<String, Integer> orderMap = sandOrderFeignClient.clientListStatistics();
            userIndexVo.setPayWait(orderMap.get("payWait"));
            userIndexVo.setPayAfter(orderMap.get("payAfter"));
            userIndexVo.setReceivingWait(orderMap.get("receivingWait"));
            userIndexVo.setEvaluateWait(orderMap.get("receivingWait"));
            userIndexVo.setRetreatWait(orderMap.get("retreatWait"));
            userIndexVo.setAvatar(umsMember.getAvatar());
            logger.info("FirstLevelController myWxapp userIndexVo nickname:{} userId:{}",userIndexVo.getNickname(),0);
            return ResultData.result(true).setData(userIndexVo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("FirstLevelController myWxapp exception");
            return ResultData.result(false);

        }
    }
}
