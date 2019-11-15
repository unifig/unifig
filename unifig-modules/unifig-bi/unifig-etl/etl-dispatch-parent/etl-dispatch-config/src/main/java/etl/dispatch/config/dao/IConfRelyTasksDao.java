package etl.dispatch.config.dao;

import java.util.List;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.config.entity.ConfRelyTasksEntity;

@BaseRepository
public interface IConfRelyTasksDao {

	public List<ConfRelyTasksEntity> findConfRelyTasks(ConfRelyTasksEntity confRelyTasks);
}
