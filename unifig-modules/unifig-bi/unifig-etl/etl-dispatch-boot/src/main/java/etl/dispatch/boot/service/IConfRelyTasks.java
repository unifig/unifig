package etl.dispatch.boot.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;

import etl.dispatch.boot.entity.ConfRelyTasks;

/**
 * <p>
 * 存储各个任务之间依赖配置 服务类
 * </p>
 *
 *
 * @since 2017-08-14
 */
public interface IConfRelyTasks extends IService<ConfRelyTasks> {


	Object selectConfRelyTasks(Integer id);

	Object updateConfRelyTasks(List<ConfRelyTasks> entityList, String createUser);

	List<ConfRelyTasks> isNext(Integer id);

	Integer updatebyGroupId(Integer groupId);

	Integer updatebyTasksId(Integer tasksId);
	
}
