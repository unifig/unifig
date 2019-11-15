package com.unifig.organ.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.model.UmsMember;
import com.unifig.organ.constant.Constants;
import com.unifig.organ.mapper.UmsMemberMapper;
import com.unifig.organ.model.UmsIntegrationChangeHistory;
import com.unifig.organ.model.UmsMemberIntegrationRuleSetting;
import com.unifig.organ.service.UmsIntegrationChangeHistoryService;
import com.unifig.organ.service.UmsMemberIntegrationRuleSettingService;
import com.unifig.organ.service.UmsMemberScService;
import com.unifig.organ.service.UmsMemberService;
import com.unifig.organ.utils.BeanUtils;
import com.unifig.organ.utils.CharUtil;
import com.unifig.organ.utils.DateUtils;
import com.unifig.organ.vo.UserVo;
import com.unifig.result.MsgCode;
import com.unifig.result.ResultData;
import com.unifig.utils.CacheRedisUtils;
import com.unifig.utils.CodeUtil;
import com.unifig.utils.MD5Util;
import com.unifig.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 *
 * @since 2019-06-27
 */
@Service
public class UmsMemberServiceImpl extends ServiceImpl<UmsMemberMapper, UmsMember> implements UmsMemberService {
    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private CacheRedisUtils cacheRedisUtils;


    @Autowired
    private UmsMemberScService umsMemberScService;

    @Autowired
    private UmsMemberIntegrationRuleSettingService uitrs;

    @Autowired
    private UmsIntegrationChangeHistoryService uichs;


    @Value("${redis.key.prefix.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${authCode.expire.seconds}")
    private Long AUTH_CODE_EXPIRE_SECONDS;

    @Autowired
    private UmsMemberIntegrationRuleSettingService umsMemberIntegrationRuleSettingService;


    @Override
    public ResultData updatePassword(String mobile, String password, String authCode) {

        UmsMember umsMember = selectByMobile(mobile);
        if (umsMember != null) {
            return ResultData.result(false).setCode(MsgCode.USER_NOT_FOUND.getCode());
        }
        //验证验证码
        if (!verifyAuthCode(authCode, mobile)) {
            ResultData.result(false).setCode(MsgCode.VERIFICATION_CODE_ERROR.getCode());
        }
        umsMember.setPassword(MD5Util.getMD5(password));
        insertOrUpdate(umsMember);
        return ResultData.result(true);
    }

    @Override
    public UserVo getUserVoById(Long id) {
        UmsMember umsMember = selectById(id);
        if (umsMember != null) {
            UserVo userVo = new UserVo(umsMember);
            return userVo;
        }
        return null;
    }

    @Override
    public List<UserVo> selectPage(Integer page, Integer size, String nickname, String mobile) {

        EntityWrapper<UmsMember> umsMemberEntityWrapper = new EntityWrapper<UmsMember>();
        if (!StringUtil.isBlankOrNull(mobile)) {
            umsMemberEntityWrapper.eq("mobile", mobile);
        }

        if (!StringUtil.isBlankOrNull(nickname)) {
            umsMemberEntityWrapper.eq("nickname", nickname);
        }
        umsMemberEntityWrapper.orderBy("register_time", false);
        Page<UmsMember> umsMemberPage = selectPage(new Page<UmsMember>(page, size), umsMemberEntityWrapper);
        List<UserVo> userVos = BeanUtils.covertMgCarCatVo(umsMemberPage.getRecords());
        return userVos;
    }


    //对输入的验证码进行校验
    private boolean verifyAuthCode(String authCode, String telephone) {
        if (StringUtils.isEmpty(authCode)) {
            return false;
        }
        String realAuthCode = (String) cacheRedisUtils.get(REDIS_KEY_PREFIX_AUTH_CODE + telephone);
        return authCode.equals(realAuthCode);
    }

    @Override
    public UmsMember selectByMobile(String mobile) {

        EntityWrapper<UmsMember> umsMemberEntityWrapper = new EntityWrapper<UmsMember>();
        umsMemberEntityWrapper.eq("mobile", mobile);
        UmsMember umsMember = selectOne(umsMemberEntityWrapper);

        return umsMember;
    }

    @Override
    public int selectPageCount(Integer page, Integer size, String nickname, String mobile) {
        EntityWrapper<UmsMember> umsMemberEntityWrapper = new EntityWrapper<UmsMember>();
        if (!StringUtil.isBlankOrNull(mobile)) {
            umsMemberEntityWrapper.eq("mobile", mobile);
        }

        if (!StringUtil.isBlankOrNull(nickname)) {
            umsMemberEntityWrapper.eq("nickname", nickname);
        }

        return selectCount(umsMemberEntityWrapper);
    }

    @Override
    public UmsMember getByUsername(String username) {

        EntityWrapper<UmsMember> umsMemberEntityWrapper = new EntityWrapper<UmsMember>();
        if (!StringUtil.isBlankOrNull(username)) {
            umsMemberEntityWrapper.eq("username", username);
        }
        UmsMember umsMember = selectOne(umsMemberEntityWrapper);
        return umsMember;
    }

    @Override
    public UmsMember getByOpenid(String openid) {
        if (StringUtil.isBlankOrNull(openid)) {
            return null;
        }

        EntityWrapper<UmsMember> umsMemberEntityWrapper = new EntityWrapper<UmsMember>();
        if (!StringUtil.isBlankOrNull(openid)) {
            umsMemberEntityWrapper.eq("openid", openid);
        }
        UmsMember umsMember = selectOne(umsMemberEntityWrapper);
        return umsMember;
    }

    @Override
    public UserVo getUserVoByOpenid(String openid) {
        UmsMember umsMember = getByOpenid(openid);
        if (Objects.isNull(umsMember)) {
            return new UserVo();
        }
        return new UserVo(umsMember);
    }

    @Override
    public Integer updateIntegration(Long id, Integer integration) {
        UmsMember umsMember = umsMemberMapper.selectById(id);
        if (umsMember == null) {
            return 0;
        }
        Integer old = umsMember.getIntegration();
        umsMember.setIntegration(old + integration);
        umsMemberMapper.updateById(umsMember);
        return umsMember.getIntegration();
    }

    @Override
    public int umemberIntegration(String userId) {
        UmsMember umsMember = selectById(userId);
        Integer integration = umsMember.getIntegration();
        if (integration == null) integration = 0;
        return integration;
    }

    @Override
    public UmsMember isBinding(String openid, int weChat) {

        UmsMember umsMember = getByOpenid(openid);

        return umsMember;
    }


    @Override
    public UmsMember login(String mobile, String password) {
        UmsMember umsMember = selectByMobile(mobile);

        return umsMember;
    }


    @Override
    public ResultData successCallback(String action, String userId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(action)) {
            return ResultData.result(true);
        }
        UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting = umsMemberIntegrationRuleSettingService.selectByAction(action);
        updateIntegration(Long.valueOf(userId), umsMemberIntegrationRuleSetting.getIntegration().intValue());
        return ResultData.result(true);
    }

    @Override
    public ResultData clickCallback(String openId, String userId) {
        //openId 分享者的id
        UserVo userVo = getUserVoByOpenid(openId);

        if (userVo != null && !String.valueOf(userVo.getId()).equals(userId) && userVo.getId() == null) {
            //userId 点击进入的用户id
            // userVo.setRecommenderId(userId);
            int count = umsMemberScService.checkUserHaveFrom(userId, String.valueOf(userVo.getId()));
            if (count == 0) {
                updateIntegrationByAction(String.valueOf(userVo.getId()), Constants.SHARE_SUCCESS);
            }
        }
        return ResultData.result(true);
    }


    /**
     * 微信用户注册
     *
     * @param
     * @param openid
     * @param phoneNumber
     * @return
     */
    @Override
    public UmsMember register(JSONObject jsonObject, String openid, String phoneNumber, Integer openType) {
        UmsMember umsMember = getByOpenid(openid);
        if (null == umsMember) {
            umsMember = new UmsMember();
        }
        //获取wei_chat信息
        Date now = new Date(System.currentTimeMillis());
        umsMember.setCity(jsonObject.getString("city"));
        umsMember.setProvince(jsonObject.getString("province"));
        umsMember.setAvatar(jsonObject.getString("avatarUrl"));
        umsMember.setMobile(phoneNumber);
        umsMember.setCountry(jsonObject.getString("country"));
        umsMember.setNickname(jsonObject.getString("nickName"));
        umsMember.setGender(jsonObject.getInteger("gender"));
        umsMember.setCreateTime(now);
        umsMember.setEditTime(now);
        umsMember.setOpenid(openid);
        umsMember.setSourceType(openType);
        umsMember.setUsername("微信用户" + CharUtil.getRandomString(12));
        umsMember.setInvitCode(CodeUtil.genRandomNum());
        umsMember.setRegisterTime(now);
        insertOrUpdate(umsMember);
        return umsMember;
    }

    @Override
    public List<UserVo> selectPage(Integer page, Integer size, String proxy) {
        EntityWrapper<UmsMember> umsMemberEntityWrapper = new EntityWrapper<UmsMember>();
        if (!StringUtil.isBlankOrNull(proxy)) {
            umsMemberEntityWrapper.eq("proxy", proxy);
        }
        Page<UmsMember> umsMemberPage = selectPage(new Page<UmsMember>(page, size), umsMemberEntityWrapper);
        List<UserVo> userVos = BeanUtils.covertMgCarCatVo(umsMemberPage.getRecords());
        return userVos;
    }

    @Override
    public int selectPageCount(Integer page, Integer size, String proxy) {
        EntityWrapper<UmsMember> umsMemberEntityWrapper = new EntityWrapper<UmsMember>();
        if (!StringUtil.isBlankOrNull(proxy)) {
            umsMemberEntityWrapper.eq("proxy", proxy);
        }
        return selectCount(umsMemberEntityWrapper);
    }

    @Override
    public void updateIntegrationByAction(String userId, String action) {
        UmsMemberIntegrationRuleSetting shareSuccessAction = uitrs.selectByAction(action);
        updateIntegration(Long.valueOf(userId), shareSuccessAction.getIntegration().intValue());
        addIntegrationHistory(userId, shareSuccessAction, 0);
    }

    @Override
    public void addIntegrationHistory(String userId, UmsMemberIntegrationRuleSetting shareSuccessAction, Integer integration) {
        //updateIntegration(Long.valueOf(userId), shareSuccessAction.getIntegration().intValue());
        UmsIntegrationChangeHistory uicht = new UmsIntegrationChangeHistory();
        uicht.setEnable(Constants.DEFAULT_VAULE_INT_ONE);
        uicht.setStatus(Constants.DEFAULT_VAULE_INT_ONE);
        uicht.setAction(shareSuccessAction.getAction());
        uicht.setActionInfo(shareSuccessAction.getActionInfo());
        if (integration > 0) {
            uicht.setChangeCount(integration);
        } else {
            uicht.setChangeCount(shareSuccessAction.getIntegration().intValue());
        }
        if (shareSuccessAction.getIntegration().intValue() <= 0) {
            uicht.setChangeType(Constants.DEFAULT_VAULE_INT_ZERO);
        } else {
            uicht.setChangeType(Constants.DEFAULT_VAULE_INT_ONE);
        }
        uicht.setMemberId(userId);
        uicht.setSourceType(shareSuccessAction.getType());
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
        uicht.setCreateTime(now);
        uicht.setEditTime(now);
        uichs.insert(uicht);
    }

    @Override
    public int checkoutNewUser(String userId) {
        Date now = new Date(System.currentTimeMillis());

        long old = DateUtils.addDays(now, -1).getTime();
        UmsMember umsMember = selectById(userId);
        if(umsMember==null){
            return Constants.DEFAULT_VAULE_INT_ONE;
        }
        long time = umsMember.getRegisterTime().getTime();
        if (time > old) {
            return Constants.DEFAULT_VAULE_INT_ZERO;
        }
        return Constants.DEFAULT_VAULE_INT_ONE;
    }

    @Override
    public Integer lockIntegration(Long id, Integer integration) {
        UmsMember umsMember = selectById(id);
        Integer can = umsMember.getIntegration();
        if (can == null) {
            can = 0;
        }
        Integer lockIntegration = umsMember.getLockIntegration();
        if (lockIntegration == null) {
            lockIntegration = 0;
        }
        umsMember.setIntegration(can - integration);
        umsMember.setLockIntegration(lockIntegration + integration);
        return integration;
    }

    @Override
    public Integer unlockIntegration(Long id, Integer integration) {
        UmsMember umsMember = selectById(id);
        Integer can = umsMember.getIntegration();
        if (can == null) {
            can = 0;
        }
        Integer lockIntegration = umsMember.getLockIntegration();
        if (lockIntegration == null) {
            lockIntegration = 0;
        }
        umsMember.setIntegration(can + integration);
        umsMember.setLockIntegration(lockIntegration - integration);
        return integration;
    }


    public static void main(String[] args) {
        Date date = DateUtils.addDays(new Date(System.currentTimeMillis()), -1);
    }

}
