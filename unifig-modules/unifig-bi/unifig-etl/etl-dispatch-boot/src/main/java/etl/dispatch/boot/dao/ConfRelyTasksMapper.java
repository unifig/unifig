package etl.dispatch.boot.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.boot.entity.ConfRelyTasks;

/**
 * <p>
 * 存储各个任务之间依赖配置 Mapper 接口
 * </p>
 *
 *
 * @since 2017-08-14
 */
@BaseRepository
public interface ConfRelyTasksMapper extends BaseMapper<ConfRelyTasks> {
	public List<Map<String, Object>> selectTasks(Map<String, Object> map);

	public List<ConfRelyTasks> isNext(@Param("id") Integer id);

	public Integer updatebyGroupId(@Param("groupId") Integer groupId);

	public Integer updatebyTasksId(@Param("tasksId") Integer tasksId);
}