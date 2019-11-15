package com.unifig.organ.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.User;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.organ.domain.UserVo;
import com.unifig.result.ResultData;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 *
 * @since 2019-03-06
 */
public interface UserService extends IService<User> {

	/**
	 * 创建账号
	 *
	 * @param userCache
	 * @param user
	 * @return
	 */
	ResultData createUser(UserCache userCache, User user);

	/**
	 * 更新账号
	 *
	 * @param userCache
	 * @param user
	 * @return
	 */
	ResultData updateUser(UserCache userCache, User user);

	/**
	 * 删除账号信息
	 *
	 * @param userCache
	 * @param id
	 * @return
	 */
	ResultData deleteUser(UserCache userCache, Long id);

	/**
	 * 分页获取账号信息列表
	 *
	 * @param userCache
	 * @param userVo
	 * @return
	 */
	ResultData findeUserdListByUsertVo(UserCache userCache, UserVo userVo);

	/**
	 * 更新用户店铺信息
	 * @param uuid
	 * @param shopId
	 * @return
	 */
	int updateUserShopId(String uuid,String shopId);
	/**
	 * 更新用户店铺信息
	 * @param uuid
	 * @param shopId
	 * @return
	 */
	int updateUserShopId(String uuid,String shopId,String openId);
}
