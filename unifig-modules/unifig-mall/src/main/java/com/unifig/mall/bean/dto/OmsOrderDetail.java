package com.unifig.mall.bean.dto;

import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.bean.model.OmsOrderOperateHistory;
import com.unifig.mall.bean.model.OmsOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 订单详情信息
 *    on 2018/10/11.
 */
@Data
public class OmsOrderDetail extends OmsOrder {

    @ApiModelProperty("订单中所包含的商品")
    private List<OmsOrderItem> orderItemList;

    @ApiModelProperty("订单操作历史记录")
    private List<OmsOrderOperateHistory> historyList;

}
