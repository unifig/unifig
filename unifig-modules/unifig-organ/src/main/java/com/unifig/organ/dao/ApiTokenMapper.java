package com.unifig.organ.dao;

import com.unifig.organ.model.TokenEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Token
 *
 *
 * @email kaixin254370777@163.com
 * @date 2017-03-23 15:22:07
 */
public interface ApiTokenMapper extends BaseDao<TokenEntity> {

    TokenEntity queryByUserId(@Param("userId") Long userId);

    TokenEntity queryByToken(@Param("token") String token);

}
