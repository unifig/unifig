package com.unifig.mall.controller.client;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.domain.CartProduct;
import com.unifig.mall.bean.vo.OmsCartProductVo;
import com.unifig.mall.feign.UmsMemberFeign;
import com.unifig.mall.service.OmsCartItemService;
import com.unifig.model.CartPromotionItem;
import com.unifig.model.OmsCartItem;
import com.unifig.result.Rest;
import com.unifig.result.RestList;
import com.unifig.result.ResultData;
import com.unifig.utils.UserTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 购物车管理Controller
 *    on 2018/8/2.
 */
@RestController
@Api(tags = "购物车管理", description = "OmsCartItemController")
@RequestMapping("/client/cart")
public class OmsClientCartItemController {
    @Autowired
    private OmsCartItemService cartItemService;

    @Autowired
    private UmsMemberFeign umsMemberFeign;

    @Autowired
    private UserTokenUtil userTokenUtil;

    @ApiOperation("添加商品到购物车")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultData add(@RequestBody OmsCartItem cartItem, @CurrentUser UserCache user) {
        int count = cartItemService.add(cartItem,user);
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(false);
    }

    @ApiOperation("获取会员的购物车列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public RestList<OmsCartItem> list(@CurrentUser UserCache user) {
        List<OmsCartItem> cartItemList = cartItemService.list(Long.parseLong(user.getUserId()));
        return RestList.resultData(new OmsCartItem()).setData(cartItemList);
    }

    @ApiOperation("获取会员的购物车列表(含店铺信息)")
    @RequestMapping(value = "/shopAndProduct/list", method = RequestMethod.GET)
    public RestList<OmsCartProductVo> shopAndProductlist(@CurrentUser UserCache user) {
        List<OmsCartItem> cartItemList = cartItemService.list(Long.parseLong(user.getUserId()));
        Map<String,List<OmsCartItem>> cartItemMap = cartItemList.stream().collect(Collectors.groupingBy(OmsCartItem::getShopId));
        List<OmsCartProductVo> listVO = new ArrayList<>();
        for(List<OmsCartItem> value : cartItemMap.values()){
            if(value.size()>0){
                OmsCartProductVo vo = new OmsCartProductVo();
                vo.setGoodsList(value);
                vo.setStoreName(value.get(0).getShopName());
                listVO.add(vo);
            }
        }
        return RestList.resultData(new OmsCartProductVo()).setData(listVO);
    }

    @ApiOperation("获取某个会员的购物车列表,包括促销信息")
    @RequestMapping(value = "/list/promotion", method = RequestMethod.GET)
    public RestList<CartPromotionItem> listPromotion(@CurrentUser UserCache user) {
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(Long.parseLong(user.getUserId()));
        return RestList.resultData(new CartPromotionItem()).setData(cartPromotionItemList);
    }

    @ApiOperation("修改购物车中某个商品的数量")
    @RequestMapping(value = "/update/quantity", method = RequestMethod.GET)
    public ResultData updateQuantity(@ApiParam("购物车商品id") @RequestParam Long id,
                                 @ApiParam("数量") @RequestParam Integer quantity,
                                 @CurrentUser UserCache user) {
        int count = cartItemService.updateQuantity(id,Long.parseLong(user.getUserId()),quantity);
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(false);
    }

    @ApiOperation("获取购物车中某个商品的规格,用于重选规格")
    @RequestMapping(value = "/getProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public Rest<CartProduct> getCartProduct(@ApiParam("商品id") @PathVariable Long productId) {
        CartProduct cartProduct = cartItemService.getCartProduct(productId);
        return Rest.resultData(new CartProduct()).setData(cartProduct);
    }

    @ApiOperation("修改购物车中商品的规格")
    @RequestMapping(value = "/update/attr", method = RequestMethod.POST)
    public ResultData updateAttr(@RequestBody OmsCartItem cartItem,@CurrentUser UserCache user) {
        int count = cartItemService.updateAttr(cartItem,user);
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(false);
    }

    @ApiOperation("删除购物车中的某个商品")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResultData delete(@ApiParam("购物车商品id(多个用英文逗号隔开)") Long[] ids,
                         @CurrentUser UserCache user) {
        int count = cartItemService.delete(Long.parseLong(user.getUserId()), Arrays.asList(ids));
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(false);
    }

    @ApiOperation("清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    public ResultData clear(@CurrentUser UserCache user) {
        int count = cartItemService.clear(Long.parseLong(user.getUserId()));
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(false);
    }

    /**
     * 生成确认单(temporary)
     * @param user
     * @return
     */
    @ApiOperation("获取会员的购物车列表(含店铺信息)")
    @RequestMapping(value = "/generateConfirmOrder/list", method = RequestMethod.GET)
    public ResultData shopAndProductlist(@CurrentUser UserCache user,@RequestParam  List<Long> id) {
        Map<String,Object> map = new HashMap<>();
        BigDecimal totalAmount = new BigDecimal("0");
        Integer quantity = 0;
        List<OmsCartItem> cartItemList =  cartItemService.selectListById(id);
        for (OmsCartItem omsCartItem : cartItemList) {
            quantity = quantity + omsCartItem.getQuantity();
            totalAmount = totalAmount.add(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
        }
        Map<String,List<OmsCartItem>> cartItemMap = cartItemList.stream().collect(Collectors.groupingBy(OmsCartItem::getShopId));
        List<OmsCartProductVo> listVO = new ArrayList<>();
        for(List<OmsCartItem> value : cartItemMap.values()){
            if(value.size()>0){
                OmsCartProductVo vo = new OmsCartProductVo();
                vo.setGoodsList(value);
                vo.setStoreName(value.get(0).getShopName());
                listVO.add(vo);
            }
        }
        map.put("list",listVO);
        map.put("totalAmount",totalAmount);
        map.put("quantity",quantity);
        return ResultData.result(true).setData(map);
    }
}
