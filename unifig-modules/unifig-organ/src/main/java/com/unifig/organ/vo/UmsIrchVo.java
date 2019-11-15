package com.unifig.organ.vo;

import com.unifig.organ.model.UmsIntegrationChangeHistory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 积分变化历史记录表
 * </p>
 *
 *
 * @since 2019-01-28
 */
@Data
public class UmsIrchVo {

    private static final long serialVersionUID = 1L;
    /**
     * 积分
     */
    @ApiModelProperty(value = "可用积分")
    private Integer integration;
    /**
     * 锁定的积分
     */
    @ApiModelProperty(value = "锁定的积分")
    private Integer lockIntegration;

    /**
     * 累计节约
     */
    @ApiModelProperty(value = "累计节省")
    private Double use;

    @ApiModelProperty(value = "积分总数")
    private Integer  integrationCount;


    private List<UmsIntegrationChangeHistory> history = new ArrayList<UmsIntegrationChangeHistory>();
}
