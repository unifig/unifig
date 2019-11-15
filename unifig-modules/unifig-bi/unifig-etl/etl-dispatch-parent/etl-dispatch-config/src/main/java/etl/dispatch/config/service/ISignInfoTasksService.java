package etl.dispatch.config.service;

import java.util.List;

import etl.dispatch.config.entity.SignInfoTasksEntity;

public interface ISignInfoTasksService {

	public void saveSignInfoTasks(SignInfoTasksEntity signInfoTasks);

	public void deleteSignInfoTasks(SignInfoTasksEntity signInfoTasks);
	
	public List<SignInfoTasksEntity> findSignInfoTasks(SignInfoTasksEntity signInfoTasks);
}
