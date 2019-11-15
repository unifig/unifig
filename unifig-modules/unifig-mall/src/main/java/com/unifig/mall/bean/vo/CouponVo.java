/**
 * FileName: couponVo
 * Author:
 * Date:     2019/5/9 16:48
 * Description: 优惠券发放vo
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <h3>概要:</h3><p>couponVo</p>
 * <h3>功能:</h3><p>优惠券发放vo</p>
 *
 * @create 2019/5/9
 * @since 1.0.0
 */
@Data
public class CouponVo {

    @ApiModelProperty("优惠券id")
    private Long couponId;

    @ApiModelProperty("用户信息集合")
    private List<CouponUserVo> users;

}
