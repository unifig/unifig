package com.unifig.organ.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.model.UmsMember;
import com.unifig.organ.model.UmsMemberIntegrationRuleSetting;
import com.unifig.organ.vo.UserVo;
import com.unifig.result.ResultData;

import java.util.List;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 *
 * @since 2019-06-27
 */
public interface UmsMemberService extends IService<UmsMember> {

    ResultData updatePassword(String telephone, String password, String authCode);

    UserVo getUserVoById(Long valueOf);

    List<UserVo> selectPage(Integer page, Integer size, String nickname, String mobile);

    UmsMember selectByMobile(String mobile);

    int selectPageCount(Integer page, Integer size, String nickname, String mobile);

    UmsMember getByUsername(String username);

    UmsMember getByOpenid(String openid);


    UserVo getUserVoByOpenid(String openid);

    /**
     * 积分变更 返回变更后的积分
     *
     * @param id
     * @param integration
     * @return
     */
    Integer updateIntegration(Long id, Integer integration);

    int umemberIntegration(String userId);

    UmsMember isBinding(String openid, int weChat);

    UmsMember login(String mobile, String password);

    ResultData successCallback(String action, String userId);

    ResultData clickCallback(String openId, String userId);

    UmsMember register(JSONObject jsonObject, String openid, String phoneNumber, Integer openType);

    List<UserVo> selectPage(Integer page, Integer size, String proxy);

    int selectPageCount(Integer page, Integer size, String proxy);

    void updateIntegrationByAction(String userId,String shareSuccess);

    void addIntegrationHistory(String userId, UmsMemberIntegrationRuleSetting shareSuccessAction,Integer integration);

    int checkoutNewUser(String userId);

    Integer lockIntegration(Long id, Integer integration);

    Integer unlockIntegration(Long id, Integer integration);
}
