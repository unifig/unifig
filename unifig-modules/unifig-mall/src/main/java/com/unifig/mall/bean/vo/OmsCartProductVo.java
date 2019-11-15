/**
 * FileName: OmsCartProductVo
 * Author:
 * Date:     2019/3/19 16:40
 * Description: 购物车列表VO
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.bean.vo;

import com.unifig.model.OmsCartItem;
import lombok.Data;

import java.util.List;

/**
 * <h3>概要:</h3><p>OmsCartProductVo</p>
 * <h3>功能:</h3><p>购物车列表VO</p>
 *
 * @create 2019/3/19
 * @since 1.0.0
 */
@Data
public class OmsCartProductVo {
    private String storeName;
    private Integer storeChecked = 0;
    private List<OmsCartItem> goodsList;
}
