package com.unifig.organ.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.constant.Constants;
import com.unifig.organ.service.UmsMemberScService;
import com.unifig.organ.service.UmsMemberService;
import com.unifig.organ.service.UserShareService;
import com.unifig.organ.utils.WxUtils;
import com.unifig.organ.vo.UserShareVo;
import com.unifig.result.MsgCode;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 分享
 *    on 2018/8/3.
 */
@RestController
@Api(tags = "用户分享", description = "用户分享")
@RequestMapping("/ums/share")
public class UmsShareController {

    private static Logger logger = LoggerFactory.getLogger(UmsShareController.class);


    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private WxUtils wxUtils;

    @Autowired
    private UserShareService userShareService;

    @Autowired
    private UmsMemberScService umsMemberScService;

    @ApiOperation("分享成功回调")
    @RequestMapping(value = "/success/callback", method = RequestMethod.GET)
    @ResponseBody
    public Object successCallback(@RequestParam String action,
                                  @CurrentUser UserCache userCache) {
        //分享成功，为用户加积分
        return memberService.successCallback(action, userCache.getUserId());
    }

    @ApiOperation("用户点击分享内容-回调")
    @RequestMapping(value = "/click/callback", method = RequestMethod.GET)
    @ResponseBody
    public Object clickCallback(@RequestParam String openId,
                                @CurrentUser UserCache userCache) {
        //为用户补充上下级

        return memberService.clickCallback(openId, userCache.getUserId());
    }


    @ApiOperation("用户分享前获取二维码")
    @RequestMapping(value = "/orcode", method = RequestMethod.POST)
    public ResultData orcode(HttpServletResponse res, @CurrentUser UserCache userCache, @RequestBody UserShareVo userShareVo) {
        //为用户补充上下级 type 分享类型
        try {
            //ServletOutputStream outputStream = res.getOutputStream();
            byte[] bytes = userShareService.miniqrQrCode(userCache, userShareVo);
            if (null == bytes) {
                return ResultData.result(false).setCode(MsgCode.ORCODE_PAME_ERROR);
            }
            res.setHeader("Content-Type", "image/png");
            BASE64Encoder encoder = new BASE64Encoder();
            String encode = encoder.encode(bytes);
            return ResultData.result(true).setData(encode);
        } catch (IOException e) {
            logger.error("UmsShareController.orcode.IOException", e.getMessage());
        }
        return ResultData.result(false);
    }

    @ApiOperation("用户扫描二维码进入获取信息")
    @RequestMapping(value = "/san/orcode", method = RequestMethod.GET)
    public ResultData<UserShareVo> sanOrcode(@CurrentUser UserCache userCache, @RequestParam String code) {
        //通过code
        try {
            UserShareVo userShareVo = userShareService.sanOrcode(code);
            if (userShareVo == null) {
                return ResultData.result(false).setCode(MsgCode.ORCODE_NOTFOUND_ERROR);
            }
            logger.info("UmsShareController sanOrcode userCache userId:{} code:{}",userCache.getUserId(),code);
            //检测关系
            int count = umsMemberScService.checkUserHaveFrom(userCache.getUserId(), userShareVo.getUserId());
            //检测注册时间
            int status = memberService.checkoutNewUser(userCache.getUserId());

            //为分享二维码的人 加积分 并且产生邀请关系
            if (count == 0 && status == 0) {
                //保存用户好友关系
                umsMemberScService.bindSc(userShareVo, userCache, code);
                memberService.updateIntegrationByAction(userShareVo.getUserId(), Constants.SHARE_SUCCESS);
            }
            return ResultData.result(true).setData(userShareVo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UmsShareController.sanOrcode.Exception", e.getMessage());
            return ResultData.result(false);

        }
    }
}
