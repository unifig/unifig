package etl.dispatch.boot.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import etl.dispatch.boot.entity.ConfInfoTasks;

/**
 * <p>
 * 存储各个任务配置 服务类
 * </p>
 *
 *
 * @since 2017-08-14
 */
public interface IConfInfoTasks extends IService<ConfInfoTasks> {

	List<ConfInfoTasks> selectUnselected();
	
	void page(Page page, ConfInfoTasks entity);
	
}
