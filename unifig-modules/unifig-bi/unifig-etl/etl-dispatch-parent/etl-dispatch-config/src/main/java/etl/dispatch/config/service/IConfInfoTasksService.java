package etl.dispatch.config.service;

import java.util.List;

import etl.dispatch.config.entity.ConfInfoTasksEntity;

public interface IConfInfoTasksService {
	public List<ConfInfoTasksEntity> findConfInfoTasks(ConfInfoTasksEntity confInfoTasks);
}
