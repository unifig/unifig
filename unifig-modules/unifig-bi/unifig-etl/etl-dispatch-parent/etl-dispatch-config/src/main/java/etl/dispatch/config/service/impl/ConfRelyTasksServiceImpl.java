package etl.dispatch.config.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etl.dispatch.config.dao.IConfRelyTasksDao;
import etl.dispatch.config.entity.ConfRelyTasksEntity;
import etl.dispatch.config.service.IConfRelyTasksService;

@Service
public class ConfRelyTasksServiceImpl implements IConfRelyTasksService {
	private static Logger logger = LoggerFactory.getLogger(ConfRelyTasksServiceImpl.class);

	@Autowired
	private IConfRelyTasksDao confRelyTasksDao;

	@Override
	public List<ConfRelyTasksEntity> findConfRelyTasks(ConfRelyTasksEntity confRelyTasks) {
		if (null == confRelyTasks) {
			logger.error("ConfRelyTasksEntity is null, Query operation failed! ");
			return null;
		}
		return confRelyTasksDao.findConfRelyTasks(confRelyTasks);
	}
}
