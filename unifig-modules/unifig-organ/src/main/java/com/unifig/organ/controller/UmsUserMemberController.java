package com.unifig.organ.controller;

import cn.hutool.core.bean.BeanUtil;
import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.model.UmsMember;
import com.unifig.model.UserGroupVo;
import com.unifig.organ.constant.Constants;
import com.unifig.organ.model.UmsMemberIntegrationRuleSetting;
import com.unifig.organ.service.UmsCollectionProductService;
import com.unifig.organ.service.UmsMemberIntegrationRuleSettingService;
import com.unifig.organ.service.UmsMemberService;
import com.unifig.organ.vo.UserVo;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员登录注册管理Controller
 *    on 2018/8/3.
 */
@RestController
@Api(tags = "用户 会员", description = "会员登录注册管理")
@RequestMapping("/sso")
public class UmsUserMemberController {
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private UmsCollectionProductService memberCollectionService;

    @Autowired
    private UmsMemberIntegrationRuleSettingService umsMemberIntegrationRuleSettingService;


    @ApiOperation("修改密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultData updatePassword(@RequestParam String telephone,
                                     @RequestParam String password,
                                     @RequestParam String authCode) {
        return memberService.updatePassword(telephone, password, authCode);
    }


    @ApiOperation("用户信息完善")
    @RequestMapping(value = "/umember", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<UserVo> umember(UmsMember umsMember, @CurrentUser UserCache userCache) {
        umsMember.setId(Long.valueOf(userCache.getUserId()));
        memberService.updateById(umsMember);
        return ResultData.result(true);
    }


    @ApiOperation("用户积分变更-action-只可以根据action扣")
    @RequestMapping(value = "/umember/change", method = RequestMethod.POST)
    @ResponseBody
    public ResultData umemberChange(@CurrentUser UserCache userCache, @RequestParam(required = false) String userId, String action) {
        if (userId == null) {
            userId = userCache.getUserId();
        }
        UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting = umsMemberIntegrationRuleSettingService.selectByAction(action);
        if (umsMemberIntegrationRuleSetting == null) {
            return ResultData.result(false).setCode(MsgConstants.INTEGRATION_RULE_SETTING_ERRO);
        }
        int integration = memberService.updateIntegration(Long.valueOf(userId), umsMemberIntegrationRuleSetting.getIntegration().intValue());
        return ResultData.result(true).setData(integration);
    }


    /**
     * 所有用户列表
     */
    @ApiOperation("用户列表")
    @GetMapping("/user/list")
    public ResultData list(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "10") Integer size,
                           @RequestParam(required = false) String nickname,
                           @RequestParam(required = false) String mobile) {
        //查询列表数据
        List<UserVo> userVos = memberService.selectPage(page, size, nickname, mobile);
        int count = memberService.selectPageCount(page, size, nickname, mobile);

        return ResultData.result(true).setMsg("true").setData(userVos).setCount(count);
    }


    /**
     * 用户列表 根据proxy搜索  0普通用户 1店铺管理者 2代销 3配送人员
     */
    @ApiOperation("用户列表根据proxy搜索")
    @GetMapping("/user/list/proxy")
    public ResultData listProxy(@RequestParam(required = false, defaultValue = "1") Integer page,
                                @RequestParam(required = false, defaultValue = "10") Integer size,
                                @RequestParam(required = false) String proxy) {
        List<UserVo> userVos = memberService.selectPage(page, size, proxy);
        int count = memberService.selectPageCount(page, size, proxy);

        return ResultData.result(true).setMsg("true").setData(userVos).setCount(count);
    }


    /**
     * 给用户设置角色  proxy  0普通用户 1店铺管理者 2代销 3配送人员
     */
    @ApiOperation("给用户设置角色 proxy  0普通用户 1店铺管理者 2代销 3配送人员")
    @GetMapping("/user/set/proxy")
    public ResultData setProxy(@RequestParam(required = true) String userId,
                               @RequestParam(required = true) Integer proxy) {
        UmsMember umsMember = memberService.selectById(userId);
        umsMember.setProxy(proxy);
        memberService.insertOrUpdate(umsMember);
        return ResultData.result(true).setMsg("true").setData(umsMember);
    }


    /**
     * 团购 参团用户列表
     */
    @ApiOperation("参团用户列表")
    @GetMapping("/user/list/group")
    public ResultData listGroup(List<String> ids) {
        List<UserGroupVo> userGroupVos = new ArrayList<UserGroupVo>();
        for (String id : ids) {
            UserVo userVo = memberService.getUserVoById(Long.valueOf(id));
            UserGroupVo userGroupVo = new UserGroupVo();
            BeanUtil.copyProperties(userVo, userGroupVo);
            userGroupVos.add(userGroupVo);
        }
        return ResultData.result(true).setMsg("true").setData(userGroupVos).setCount(ids.size());
    }


    @ApiOperation("获取当前登录会员")
    @RequestMapping(value = "/feign/umemberInfo", method = RequestMethod.GET)
    @ResponseBody
    public UmsMember feignUmemberInfo(@CurrentUser UserCache userCache) {
        UserVo userVo = memberService.getUserVoById(Long.valueOf(userCache.getUserId()));
        UmsMember user = new UmsMember();
        BeanUtil.copyProperties(userVo, user);
        return user;
    }


    @ApiOperation("根据会员编号获取会员")
    @RequestMapping(value = "/feign/getById", method = RequestMethod.GET)
    @ResponseBody
    public UmsMember feignGetById(Long id) {
        UserVo userVo = memberService.getUserVoById(id);
        UmsMember user = new UmsMember();
        BeanUtil.copyProperties(userVo, user);
        return user;
    }

    @ApiOperation("根据用户名获取会员")
    @RequestMapping(value = "/feign/getByUsername", method = RequestMethod.GET)
    @ResponseBody
    public UmsMember feignGetByUsername(String username) {
        UmsMember byUsername = memberService.getByUsername(username);
        UmsMember user = new UmsMember();
        BeanUtil.copyProperties(byUsername, user);
        return user;
    }


    @ApiOperation("用户积分-可用积分查询")
    @RequestMapping(value = "/feign/getIntegration", method = RequestMethod.GET)
    public int feignGetIntegration(@CurrentUser UserCache userCache, @RequestParam(required = false) String userId) {
        if (userId == null) {
            userId = userCache.getUserId();
        }
        int integration = memberService.umemberIntegration(userId);
        return integration;
    }


    @ApiOperation("用户积分变更-积分消费")
    @RequestMapping(value = "/feign/updateIntegration", method = RequestMethod.GET)
    public void feignUpdateIntegration(@RequestParam("id") Long id, @RequestParam("integration") Integer integration) {
        Integer integer = memberService.updateIntegration(id, integration);
        UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting = umsMemberIntegrationRuleSettingService.selectByAction(Constants.USER_BUY);
        memberService.addIntegrationHistory(String.valueOf(id), umsMemberIntegrationRuleSetting, integration);
    }

    @ApiOperation("用户积分变更-积分锁定")
    @RequestMapping(value = "/feign/lockIntegration", method = RequestMethod.GET)
    public void feignlockIntegration(@RequestParam("id") Long id, @RequestParam("integration") Integer integration) {
        Integer integer = memberService.lockIntegration(id, integration);
    }

    @ApiOperation("用户积分变更-积分解锁")
    @RequestMapping(value = "/feign/unlockIntegration", method = RequestMethod.GET)
    public void feignUnlockIntegration(@RequestParam("id") Long id, @RequestParam("integration") Integer integration) {
        Integer integer = memberService.unlockIntegration(id, integration);
    }


    @ApiOperation("用户详情查询")
    @RequestMapping(value = "/umemberInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<UserVo> umemberInfo(@CurrentUser UserCache userCache) {
        UserVo userVo = memberService.getUserVoById(Long.valueOf(userCache.getUserId()));
        return ResultData.result(true).setData(userVo);
    }


    @ApiOperation("用户剩余积分查询")
    @RequestMapping(value = "/umember/integration", method = RequestMethod.POST)
    @ResponseBody
    public ResultData umemberIntegration(@CurrentUser UserCache userCache, @RequestParam(required = false) String userId) {
        if (userId == null) {
            userId = userCache.getUserId();
        }
        int integration = memberService.umemberIntegration(userId);
        return ResultData.result(true).setData(integration);
    }


}
