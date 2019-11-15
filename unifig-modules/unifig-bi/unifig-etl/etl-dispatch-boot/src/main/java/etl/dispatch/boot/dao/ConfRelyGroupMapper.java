package etl.dispatch.boot.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.boot.bean.GroupBean;
import etl.dispatch.boot.entity.ConfRelyGroup;

/**
 * <p>
  * 存储各个任务组之间依赖配置 Mapper 接口
 * </p>
 *
 *
 * @since 2017-08-14
 */
@BaseRepository
public interface ConfRelyGroupMapper extends BaseMapper<ConfRelyGroup> {
	public List<GroupBean> selectGroup(Integer classifyId);
	public List<ConfRelyGroup> isNext(@Param("id") Integer id);
	public Integer updatebyGroupId(@Param("groupId") Integer groupId);
}