package etl.dispatch.config.service;

import java.util.List;

import etl.dispatch.config.entity.ConfRelyTasksEntity;

public interface IConfRelyTasksService {
	public List<ConfRelyTasksEntity> findConfRelyTasks(ConfRelyTasksEntity confRelyTasks);
}
