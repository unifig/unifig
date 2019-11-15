/**
 * FileName: 订单配送
 * Author:
 * Date:     2019-07-24
 * Description: 订单配送
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.service;

import com.unifig.mall.bean.model.OmsOrderDeliveryman;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.result.ResultData;

import java.util.List;

/**
 * <p>
 * 订单配送 服务类
 * </p>
 *
 *
 * @since 2019-07-24
 */
public interface OmsOrderDeliverymanService extends IService<OmsOrderDeliveryman> {

	/**
	 * 配送默认状态 配送中
	 */
	Integer DELIVERYMAN_STATUS_DEFAULT = 0;

	/**
	 * 配送完成状态
	 */
	Integer DELIVERYMAN_STATUS_ACCOMPLISH = 1;

	/**
	 * 查询分页数据
 	*/
	ResultData<OmsOrderDeliveryman> findListByPage(int pageNum, int pageSize);


	/**
 	* 根据id查询
 	*/
	ResultData<OmsOrderDeliveryman> getById(String id);

	/**
	 * 新增
	 */
	ResultData add(List<String> ids,String userId,String userName);

	/**
	 * 删除
	 */
	ResultData delete(List<String> ids);

	/**
 	* 修改
 	*/
	ResultData update(String uuid);

    ResultData<OmsOrderDeliveryman> clientList(int pageNum, int pageSize, int status, String userId);
}
