/**
 * FileName: OmsShopClientFeign
 * Author:
 * Date:     2019/4/1 11:27
 * Description: 店铺feign类
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.feign;

import com.unifig.result.ResultData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <h3>概要:</h3><p>OmsShopClientFeign</p>
 * <h3>功能:</h3><p>店铺feign类</p>
 *
 * @create 2019/4/1
 * @since 1.0.0
 */
@Component
@FeignClient(name = "unifig-organ"/*,url = "https://api.ratelll.com/ro/"*/)
public interface UmsProductCollectionFeign {

    @GetMapping(value = "ums/product/collection/select/product/{id}")
    boolean selectByProductId(@PathVariable(value = "id") Long id);

}
