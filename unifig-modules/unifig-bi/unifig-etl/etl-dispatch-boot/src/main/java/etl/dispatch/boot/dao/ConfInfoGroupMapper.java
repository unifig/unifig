package etl.dispatch.boot.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.boot.entity.ConfInfoGroup;

/**
 * <p>
 * 存储任务分组配置 Mapper 接口
 * </p>
 *
 *
 * @since 2017-08-14
 */
@BaseRepository
public interface ConfInfoGroupMapper extends BaseMapper<ConfInfoGroup> {
	public List<Map<String, Object>> selectRely(Integer classifyId);
}