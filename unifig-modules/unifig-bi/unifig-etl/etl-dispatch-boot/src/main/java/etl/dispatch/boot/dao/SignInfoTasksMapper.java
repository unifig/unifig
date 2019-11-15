package etl.dispatch.boot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.boot.entity.SignInfoTasks;

/**
 * <p>
 * 存储各个任务任务组执行完成标记 Mapper 接口
 * </p>
 *
 *
 * @since 2017-08-14
 */
@BaseRepository
public interface SignInfoTasksMapper extends BaseMapper<SignInfoTasks> {

}