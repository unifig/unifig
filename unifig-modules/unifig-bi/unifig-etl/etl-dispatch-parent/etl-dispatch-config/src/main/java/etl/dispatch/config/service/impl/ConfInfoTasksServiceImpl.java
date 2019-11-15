package etl.dispatch.config.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etl.dispatch.config.dao.IConfInfoTasksDao;
import etl.dispatch.config.entity.ConfInfoTasksEntity;
import etl.dispatch.config.service.IConfInfoTasksService;

@Service
public class ConfInfoTasksServiceImpl implements IConfInfoTasksService {
	private static Logger logger = LoggerFactory.getLogger(ConfInfoTasksServiceImpl.class);

	@Autowired
	private IConfInfoTasksDao confInfoTasksDao;

	@Override
	public List<ConfInfoTasksEntity> findConfInfoTasks(ConfInfoTasksEntity confInfoTasks) {
		if (null == confInfoTasks) {
			logger.error("ConfInfoTasksEntity is null, Query operation failed! ");
			return null;
		}
		return confInfoTasksDao.findConfInfoTasks(confInfoTasks);
	}
}
