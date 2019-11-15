package com.unifig.mall.controller;


import com.unifig.mall.service.CmsArticleCategoryService;
import com.unifig.mall.util.print.Methods;
import com.unifig.result.ResultData;
import com.unifig.utils.CodeUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 订单打印 前端控制器
 * </p>
 *
 *
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/print")
@Api(tags = "订单打印 ", description = "PrintController")
public class PrintController {
    @Autowired
    private CmsArticleCategoryService cmsArticleService;

    /**
     * 打印
     *
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResultData add(@RequestBody String content
    ) {
        try {
//            String content1 = "<FH2><FW2> **\b\b一日三线**</FW2></FH2>\n" +
//                    "................................\\r\n" +
//                    "<FH2><FW2> --在线支付--</FW2></FH2>\n" +
//                    "下单时间：2019-05-08T10：39：56\\r\n" +
//                    "订单编号：1206479504417779722\n" +
//                    "***************商品*************\\r\n" +
//                    "<FH><FW>\b苹果 200g 10元</FW></FH>\\r\n" +
//                    "<FH><FW>里脊肉 500g 48元</FW></FH>\\r\n" +
//                    "<FH><FW> --其他费用--</FW></FH>\\r\n" +
//                    "................................\\r\n" +
//                    "<FH><FW>配送费：￥1</FW></FH>\\r\n" +
//                    "<FH><FW>小计：￥59</FW></FH>\\r\n" +
//                    "<FH><FW>折扣：￥0</FW></FH>\\r\n" +
//                    "*******************************\\r\n" +
//                    "总价：￥59\\r\n" +
//                    "<FH><FW>红梅小区昌吉路231弄 22号</FW></FH>\\r\n" +
//                    "<FH><FW>102室</FW></FH>\\r\n" +
//                    "<FH><FW>张伟 先生：139-1708-0384</FW></FH>\\r\n" +
//                    "<FH2><FW2> **#3 完 **</FW2></FH2>";
            Methods.getInstance().init("1045424885", "97bae6a2ca7ed6f62b46598eb258b614");
            Methods.getInstance().getFreedomToken();
            Methods.getInstance().refreshToken();
            Methods.getInstance().addPrinter("4004637710", "249162955983");
            Methods.getInstance().print("4004637710", content, CodeUtil.genRandomNum());
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }


//    public static void main(String[] args) {
//        String content = "<FH2><FW2> **\b\b一日三线**</FW2></FH2>\n" +
//                "................................\\r\n" +
//                "<FH2><FW2> --在线支付--</FW2></FH2>\n" +
//                "下单时间：2019-05-08T10：39：56\\r\n" +
//                "订单编号：1206479504417779722\n" +
//                "***************商品*************\\r\n" +
//                "<FH><FW>\b苹果 200g 10元</FW></FH>\\r\n" +
//                "<FH><FW>里脊肉 500g 48元</FW></FH>\\r\n" +
//                "<FH><FW> --其他费用--</FW></FH>\\r\n" +
//                "................................\\r\n" +
//                "<FH><FW>配送费：￥1</FW></FH>\\r\n" +
//                "<FH><FW>小计：￥59</FW></FH>\\r\n" +
//                "<FH><FW>折扣：￥0</FW></FH>\\r\n" +
//                "*******************************\\r\n" +
//                "总价：￥59\\r\n" +
//                "<FH><FW>红梅小区昌吉路231弄 22号</FW></FH>\\r\n" +
//                "<FH><FW>102室</FW></FH>\\r\n" +
//                "<FH><FW>张伟 先生：139-1708-0384</FW></FH>\\r\n" +
//                "<FH2><FW2> **#3 完 **</FW2></FH2>";
//        Methods.getInstance().init("1045424885", "97bae6a2ca7ed6f62b46598eb258b614");
//        Methods.getInstance().getFreedomToken();
//        Methods.getInstance().refreshToken();
//        Methods.getInstance().addPrinter("4004637710", "249162955983");
//        Methods.getInstance().print("4004637710", content, CodeUtil.genRandomNum());
//    }

}

