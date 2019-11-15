package com.unifig.mall.bean.dto;

import com.unifig.mall.bean.model.OmsCompanyAddress;
import com.unifig.mall.bean.model.OmsOrderReturnApply;
import lombok.Getter;
import lombok.Setter;

/**
 * 申请信息封装
 *    on 2018/10/18.
 */
public class OmsOrderReturnApplyResult extends OmsOrderReturnApply {
    @Getter
    @Setter
    private OmsCompanyAddress companyAddress;
}
