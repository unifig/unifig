package etl.dispatch.boot.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;

import etl.dispatch.boot.entity.ConfInfoGroup;

/**
 * <p>
 * 存储任务分组配置 服务类
 * </p>
 *
 *
 * @since 2017-08-14
 */
public interface IConfInfoGroup extends IService<ConfInfoGroup> {

	boolean createQuartz(ConfInfoGroup entity);

	List<Map<String, Object>> selectRely(Integer classifyId);

	boolean updateConfInfoGroup(ConfInfoGroup entity, String createUser) throws Exception;

	boolean insertConfInfoGroup(ConfInfoGroup entity, String name, String fatherId) throws Exception;

	void restart(ConfInfoGroup confInfoGroup);
	
	void handle(ConfInfoGroup confInfoGroup , Integer handle);

	Boolean Quartz(Integer id);
	
}
