package etl.dispatch.config.api;

import java.util.List;
import java.util.Map;

import etl.dispatch.config.entity.ConfInfoGroupEntity;
import etl.dispatch.config.entity.SignInfoTasksEntity;

public interface IEtlConfigApiService {

	public void saveSignInfoTasks(SignInfoTasksEntity signInfoTasks);
	
	public void deleteSignInfoTasks(SignInfoTasksEntity signInfoTasks);

	public List<SignInfoTasksEntity> findSignInfoTasks(SignInfoTasksEntity signInfoTasks);
	
	public List<Map<String, Object>> getEtlConfigQuartzData() ;

	Map<String, Object> getConfigQuartzData(ConfInfoGroupEntity confInfoGroupEntity);
}
