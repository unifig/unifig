/**
 * FileName: 用户关系表
 * Author:   maxl
 * Date:     2019-08-30
 * Description: 用户关系表
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.organ.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.organ.model.UmsMemberSc;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.organ.vo.UserShareVo;
import com.unifig.result.ResultData;
import java.util.List;

/**
 * <p>
 * 用户关系表 服务类
 * </p>
 *
 *
 * @since 2019-08-30
 */
public interface UmsMemberScService extends IService<UmsMemberSc> {
	/**
	 * 查询分页数据
 	*/
	ResultData<UmsMemberSc> findListByPage(int pageNum, int pageSize);


	/**
 	* 根据id查询
 	*/
	ResultData<UmsMemberSc> getById(String id);

	/**
	 * 新增
	 */
	ResultData add(UmsMemberSc umsMemberSc);

	/**
	 * 删除
	 */
	ResultData delete(List<String> ids);

	/**
 	* 修改
 	*/
	ResultData update(UmsMemberSc umsMemberSc);

	UmsMemberSc getByToId(String userId);

	int checkUserHaveFrom(String toUserId,String fromUserId);

    void bindSc(UserShareVo userShareVo, UserCache userCache,String code);
}
