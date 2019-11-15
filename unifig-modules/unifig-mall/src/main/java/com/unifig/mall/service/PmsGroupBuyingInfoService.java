package com.unifig.mall.service;

import com.unifig.mall.bean.model.PmsGroupBuying;
import com.unifig.mall.bean.model.PmsGroupBuyingInfo;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.mall.bean.vo.PmsGroupBuyingInfoList;
import com.unifig.mall.bean.vo.PmsGroupBuyingInfoVo;
import com.unifig.result.ResultData;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 商品团购子表 服务类
 * </p>
 *
 *
 * @since 2019-01-23
 */
public interface PmsGroupBuyingInfoService extends IService<PmsGroupBuyingInfo> {

    /**
     * 团购不存在
     */
    Integer CONSTANT_GROUP_NONENTITY = 8001;

    /**
     * 团购超时
     */
    Integer CONSTANT_GROUP_TIMEOUT = 8002;

    /**
     * 团购已满
     */
    Integer CONSTANT_GROUP_FULL_HOUSE = 8003;

    /**
     * 团购已关闭
     */
    Integer CONSTANT_GROUP_CLOSE = 8004;

    /**
     * 团购超过最大限制
     */
    Integer CONSTANT_GROUP_REPETITION = 8005;



    //团购状态 0拼团中  1拼团成功 2 拼团失败
    Integer CONSTANT_GROUP_STATUS_IN_SPELLING = 0;

    Integer CONSTANT_GROUP_STATUS_SUCCESS = 1;

    Integer CONSTANT_GROUP_STATUS_BE_DEFEATED = 2;


    PmsGroupBuyingInfo createInfo(PmsGroupBuyingInfo pmsGroupBuyingInfo,PmsGroupBuying groupBuyingBasics);

    @Transactional
    ResultData join(String id,String userId,String oderId);

    void close();

    ResultData<PmsGroupBuyingInfo> selectList(Integer page, Integer rows, String productId, Integer status);

    ResultData info(Integer page, Integer rows, String groupBuyingId, Integer status);

    ResultData<PmsGroupBuyingInfoVo> infoByPid(String pid);

    ResultData<PmsGroupBuyingInfo> selectByGroupBuyingIdlist(String groupBuyingId);

    Object selectByProductId(String productId, String userId);

    boolean isQualified(PmsGroupBuying pmsGroupBuying);
}
