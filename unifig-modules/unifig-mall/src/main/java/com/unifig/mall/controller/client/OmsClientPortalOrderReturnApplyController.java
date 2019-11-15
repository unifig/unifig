///**
// * FileName: OmsClientPortalOrderReturnApplyController
// * Author:
// * Date:     2019-10-09 10:29
// * Description: 客户端退货申请
// * History:
// * <author>          <time>          <version>          <desc>
// */
//package com.unifig.mall.controller.client;
//
//import com.unifig.mall.bean.domain.OmsOrderReturnApplyParam;
//import com.unifig.mall.service.OmsPortalOrderReturnApplyService;
//import com.unifig.result.ResultData;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
///**
// * <h3>概要:</h3><p>OmsClientPortalOrderReturnApplyController</p>
// * <h3>功能:</h3><p>客户端退货申请</p>
// *
// * @create 2019-10-09
// * @since 1.0.0
// */
//@RestController
//@Api(tags = "申请退货管理", description = "OmsClientPortalOrderReturnApplyController")
//@RequestMapping("/client/returnApply")
//public class OmsClientPortalOrderReturnApplyController {
//
//    @Autowired
//    private OmsPortalOrderReturnApplyService returnApplyService;
//
//    @ApiOperation("申请退货")
//    @RequestMapping(value = "/create", method = RequestMethod.POST)
//    public ResultData create(@RequestBody OmsOrderReturnApplyParam returnApply) {
//        int count = returnApplyService.create(returnApply);
//        if (count > 0) {
//            return ResultData.result(true).setData(count);
//        }
//        return ResultData.result(false).setMsg("申请失败");
//    }
//
//}
