package etl.dispatch.boot.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;

import etl.dispatch.boot.entity.ConfRelyGroup;

/**
 * <p>
 * 存储各个任务组之间依赖配置 服务类
 * </p>
 *
 *
 * @since 2017-08-14
 */
public interface IConfRelyGroup extends IService<ConfRelyGroup> {

	Object selectGroup(Integer classifyId);

	List<ConfRelyGroup> isNext(Integer id);

	Object createConfRelyGroup(List<ConfRelyGroup> entityList, String createUser);


	boolean deleteConfRelyGroup(Integer id) throws Exception;



}
