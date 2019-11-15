package etl.dispatch.boot.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import etl.dispatch.boot.dao.ConfInfoTasksMapper;
import etl.dispatch.boot.entity.ConfInfoTasks;
import etl.dispatch.boot.service.IConfInfoTasks;

/**
 * <p>
 * 存储各个任务配置 服务实现类
 * </p>
 *
 *
 * @since 2017-08-14
 */
@Service
public class ConfInfoTasksService extends ServiceImpl<ConfInfoTasksMapper, ConfInfoTasks> implements IConfInfoTasks {
	@Autowired
	private ConfInfoTasksMapper confInfoTasksMapper;

	@Override
	public List<ConfInfoTasks> selectUnselected() {
		return confInfoTasksMapper.selectUnselected();
	}

	@Override
	public void page(Page page,ConfInfoTasks entity) {
		page.setRecords(confInfoTasksMapper.page(page,entity));
	}
}
