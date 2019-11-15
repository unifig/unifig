package etl.dispatch.config.dao;

import java.util.List;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.config.entity.SignInfoTasksEntity;

@BaseRepository
public interface ISignInfoTasksDao {
	
	public void saveSignInfoTasks(SignInfoTasksEntity signInfoTasks);
	
	public void deleteSignInfoTasks(SignInfoTasksEntity signInfoTasks);
	
	public List<SignInfoTasksEntity> findSignInfoTasks(SignInfoTasksEntity signInfoTasks);
}
