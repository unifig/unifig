package etl.dispatch.boot.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.boot.entity.ConfInfoTasks;

/**
 * <p>
 * 存储各个任务配置 Mapper 接口
 * </p>
 *
 *
 * @since 2017-08-14
 */
@BaseRepository
public interface ConfInfoTasksMapper extends BaseMapper<ConfInfoTasks> {
	public List<ConfInfoTasks> selectUnselected();
	

	public List page(Page page, ConfInfoTasks entity);
}