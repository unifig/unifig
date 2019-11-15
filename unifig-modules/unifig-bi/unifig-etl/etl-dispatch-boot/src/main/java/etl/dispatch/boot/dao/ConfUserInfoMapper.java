package etl.dispatch.boot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.boot.entity.ConfUserInfo;

/**
 * <p>
  * 组织机构(部门+岗位) Mapper 接口
 * </p>
 *
 *
 * @since 2017-08-14
 */
@BaseRepository
public interface ConfUserInfoMapper extends BaseMapper<ConfUserInfo> {

}