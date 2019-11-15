package etl.dispatch.config.dao;

import java.util.List;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.config.entity.ConfInfoTasksEntity;

@BaseRepository
public interface IConfInfoTasksDao {

	public List<ConfInfoTasksEntity> findConfInfoTasks(ConfInfoTasksEntity confInfoTasks);
}
