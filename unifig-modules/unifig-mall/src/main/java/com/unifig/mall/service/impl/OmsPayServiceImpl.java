/**
 * FileName: OmsPayServiceImpl
 * Author:
 * Date:     2019/7/10 11:02
 * Description: 支付实现类
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.service.impl;

import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.bean.model.OmsOrderExample;
import com.unifig.mall.bean.model.OmsOrderItem;
import com.unifig.mall.bean.model.OmsOrderItemExample;
import com.unifig.mall.mapper.OmsOrderItemMapper;
import com.unifig.mall.mapper.OmsOrderMapper;
import com.unifig.mall.service.OmsPayService;
import com.unifig.mall.service.OmsPortalOrderService;
import com.unifig.result.ResultData;
import com.unifig.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * <h3>概要:</h3><p>OmsPayServiceImpl</p>
 * <h3>功能:</h3><p>支付实现类</p>
 *
 * @create 2019/7/10
 * @since 1.0.0
 */
@Service
@Slf4j
public class OmsPayServiceImpl implements OmsPayService {

    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Override
    public ResultData payPrepay(UserCache user, Long orderId, HttpServletRequest request) {
        OmsOrder order = portalOrderService.getOrderById(orderId);
        String nonceStr = CharUtil.getRandomString(32);
        //https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=3
        Map<Object, Object> resultObj = new TreeMap();

        try {
            Map<Object, Object> parame = new TreeMap<Object, Object>();
            parame.put("appid", ResourceUtil.getConfigByName("wx.appId"));
            // 商家账号。
            parame.put("mch_id", ResourceUtil.getConfigByName("wx.mchId"));
            String randomStr = CharUtil.getRandomNum(18).toUpperCase();
            // 随机字符串
            parame.put("nonce_str", randomStr);
            // 商户订单编号
            parame.put("out_trade_no", order.getOrderSn());
            Map orderGoodsParam = new HashMap();
            orderGoodsParam.put("order_id", orderId);
            // 商品描述
            parame.put("body", "商城-支付");
            //订单的商品
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            if (null != orderItemList) {
//                String body = "unifig商城-";
                String body = "";

                for (OmsOrderItem goodsVo : orderItemList) {
                    body = body + goodsVo.getProductName() + "、";
                }
                if (body.length() > 0) {
                    body = body.substring(0, body.length() - 1);
                }
                // 商品描述
                parame.put("body", body);
            }
            //支付金额
//            parame.put("total_fee", order.getTotalAmount().multiply(new BigDecimal(100)).intValue());
            //2019-09-07修改
            parame.put("total_fee", order.getPayAmount().multiply(new BigDecimal(100)).intValue());
            // 回调地址
            parame.put("notify_url", ResourceUtil.getConfigByName("wx.notifyUrl"));
            // 交易类型APP
            parame.put("trade_type", ResourceUtil.getConfigByName("wx.tradeType"));
            parame.put("spbill_create_ip",getRemortIP(request));
            parame.put("openid", user.getOpenid());
            String sign = WechatUtil.arraySign(parame, ResourceUtil.getConfigByName("wx.paySignKey"));
            // 数字签证
            parame.put("sign", sign);

            String xml = MapUtils.convertMap2Xml(parame);
            log.info("xml:" + xml);
            Map<String, Object> resultUn = XmlUtil.xmlStrToMap(WechatUtil.requestOnce(ResourceUtil.getConfigByName("wx.uniformorder"), xml));
            // 响应报文
            String return_code = MapUtils.getString("return_code", resultUn);
            String return_msg = MapUtils.getString("return_msg", resultUn);
            if (return_code.equalsIgnoreCase("FAIL")) {
                return ResultData.result(false).setMsg("支付失败," + return_msg);
            } else if (return_code.equalsIgnoreCase("SUCCESS")) {
                // 返回数据
                String result_code = MapUtils.getString("result_code", resultUn);
                String err_code_des = MapUtils.getString("err_code_des", resultUn);
                if (result_code.equalsIgnoreCase("FAIL")) {
                    return ResultData.result(false).setMsg("支付失败," + return_msg);
                } else if (result_code.equalsIgnoreCase("SUCCESS")) {
                    String prepay_id = MapUtils.getString("prepay_id", resultUn);
                    // 先生成paySign 参考https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=5
                    resultObj.put("appId", ResourceUtil.getConfigByName("wx.appId"));
                    resultObj.put("timeStamp", DateUtils.timeToStr(System.currentTimeMillis() / 1000, DateUtils.DATE_TIME_PATTERN));
                    resultObj.put("nonceStr", nonceStr);
                    resultObj.put("package", "prepay_id=" + prepay_id);
                    resultObj.put("signType", "MD5");
                    String paySign = WechatUtil.arraySign(resultObj, ResourceUtil.getConfigByName("wx.paySignKey"));
                    resultObj.put("paySign", paySign);
                    // 业务处理
//                    order.setPay_id(prepay_id);
                    // 付款中
                    order.setStatus(0);
                    orderMapper.updateByPrimaryKey(order);
                    //扣减积分
                    return ResultData.result(true).setMsg("微信统一订单下单成功," + return_msg).setData(resultObj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            return toResponsFail("下单失败,error=" + e.getMessage());
            return ResultData.result(false).setMsg("下单失败,error=" + e.getMessage());
        }
        return null;
    }

    @Override
    public ResultData orderQuery(UserCache user, Long orderId) {
        if (orderId == null) {
            return ResultData.result(false).setMsg("订单不存在");
        }
        OmsOrder order = portalOrderService.getOrderById(orderId);
        TreeMap<Object, Object> parame = new TreeMap<Object, Object>();
        parame.put("appid", ResourceUtil.getConfigByName("wx.appId"));
        // 商家账号。
        parame.put("mch_id", ResourceUtil.getConfigByName("wx.mchId"));
        String randomStr = CharUtil.getRandomNum(18).toUpperCase();
        // 随机字符串
        parame.put("nonce_str", randomStr);
        // 商户订单编号
        parame.put("out_trade_no", order.getOrderSn());

        String sign = WechatUtil.arraySign(parame, ResourceUtil.getConfigByName("wx.paySignKey"));
        // 数字签证
        parame.put("sign", sign);

        String xml = MapUtils.convertMap2Xml(parame);
        log.info("xml:" + xml);
        Map<String, Object> resultUn = null;
        try {
            resultUn = XmlUtil.xmlStrToMap(WechatUtil.requestOnce(ResourceUtil.getConfigByName("wx.orderquery"), xml));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.result(false).setMsg("查询失败,error=" + e.getMessage());
        }
        // 响应报文
        String return_code = MapUtils.getString("return_code", resultUn);
        String return_msg = MapUtils.getString("return_msg", resultUn);

        if (!"SUCCESS".equals(return_code)) {
            return ResultData.result(false).setMsg("查询失败,error=" + return_msg);
        }

        String trade_state = MapUtils.getString("trade_state", resultUn);
        if ("SUCCESS".equals(trade_state)) {
            // 更改订单状态
            // 业务处理
            OmsOrder order1 = new OmsOrder();
            order.setPaymentTime(new Date());
            order.setStatus(1);
            orderMapper.updateByPrimaryKey(order);
            return  ResultData.result(true).setMsg("支付成功");
        } else {
            // 失败
            return ResultData.result(false).setMsg("查询失败,error=" + trade_state);
        }
    }

    @Override
    public ResultData refund(Long orderId, Double refundMoney) {
        log.info("发起订单退款,退款订单id{}",orderId);
        return getRefundResultData(orderId, refundMoney, orderMapper);
    }

    @Override
    public void notify(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            InputStream in = request.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();
            //xml数据
            String reponseXml = new String(out.toByteArray(), "utf-8");
            WechatRefundApiResult result = (WechatRefundApiResult) XmlUtil.xmlStrToBean(reponseXml, WechatRefundApiResult.class);
            String result_code = result.getResult_code();
            if (result_code.equalsIgnoreCase("FAIL")) {
                //订单编号
                String out_trade_no = result.getOut_trade_no();
                log.error("订单" + out_trade_no + "支付失败");
                response.getWriter().write(setXml("SUCCESS", "OK"));
            } else if (result_code.equalsIgnoreCase("SUCCESS")) {
                //订单编号
                String out_trade_no = result.getOut_trade_no();
                log.error("订单" + out_trade_no + "支付成功");
                // 业务处理
                OmsOrderExample example = new OmsOrderExample();
                example.createCriteria().andOrderSnEqualTo(out_trade_no);
                List<OmsOrder> omsOrders = orderMapper.selectByExample(example);
                if(omsOrders != null){
                    OmsOrder order = omsOrders.get(0);
                    order.setPaymentTime(new Date());
                    //判断是否是团购订单
                    if(order.getOrderType().toString().equals("2")){
                        //团购订单设置状态为参团中状态
                        order.setStatus(6);
                    }else{
                        //普通订单设置状态待发货
                        order.setStatus(1);
                    }
                    orderMapper.updateByPrimaryKey(order);
                }else{
                    log.error("订单{}支付失败",out_trade_no);
                }
                response.getWriter().write(setXml("SUCCESS", "OK"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static ResultData getRefundResultData(Long orderId, Double refundMoney, OmsOrderMapper orderMapper) {
        log.info("开始进行退款");
        OmsOrder omsOrder = orderMapper.selectByPrimaryKey(orderId);
        if (null == omsOrder) {
            log.info("订单不存在");
            return  ResultData.result(false).setMsg("Order does not exist");
        }

        if (omsOrder.getStatus() == 2||omsOrder.getStatus() == 3||omsOrder.getStatus() == 4||omsOrder.getStatus() == 5 ) {
            log.info("该订单无法退款");
            return  ResultData.result(false).setMsg("This order cannot be refunded");
        }

        if (omsOrder.getStatus() == 0) {
            log.info("订单未付款，不能退款");
            return  ResultData.result(false).setMsg("订单未付款，不能退款");
        }
        WechatRefundApiResult result = WechatUtil.wxRefund(omsOrder.getOrderSn(),
                omsOrder.getPayAmount().doubleValue(), refundMoney != null ?refundMoney:omsOrder.getPayAmount().doubleValue());
        if (result.getResult_code().equals("SUCCESS")) {
            //退款完成关闭订单
            omsOrder.setStatus(4);
            orderMapper.updateByPrimaryKey(omsOrder);
            log.info("退款成功并关闭订单完成");
            return ResultData.result(true).setMsg("成功退款");
        } else {
            return ResultData.result(true).setMsg("退款失败");
        }
    }


    /**
     * 获取ip
     * @param request
     * @return
     */
    public String getRemortIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 返回微信服务
     * @param return_code
     * @param return_msg
     * @return
     */
    public static String setXml(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }
}
