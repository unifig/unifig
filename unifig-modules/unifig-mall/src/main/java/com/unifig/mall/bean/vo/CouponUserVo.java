/**
 * FileName: couponUserVo
 * Author:
 * Date:     2019/5/9 16:51
 * Description: 优惠券用户信息vo
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <h3>概要:</h3><p>couponUserVo</p>
 * <h3>功能:</h3><p>优惠券用户信息vo</p>
 *
 * @create 2019/5/9
 * @since 1.0.0
 */
@Data
public class CouponUserVo {

    @ApiModelProperty("用户id")
    private Long currentMemberId;

    @ApiModelProperty("用户昵称")
    private String currentMemberName;
}
