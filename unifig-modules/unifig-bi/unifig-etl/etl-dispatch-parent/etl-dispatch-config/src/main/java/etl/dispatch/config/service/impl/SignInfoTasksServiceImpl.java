package etl.dispatch.config.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etl.dispatch.config.dao.ISignInfoTasksDao;
import etl.dispatch.config.entity.SignInfoTasksEntity;
import etl.dispatch.config.service.ISignInfoTasksService;

@Service
public class SignInfoTasksServiceImpl implements ISignInfoTasksService {
	private static Logger logger = LoggerFactory.getLogger(SignInfoTasksServiceImpl.class);

	@Autowired
	private ISignInfoTasksDao signInfoTasksDao;

	@Override
	public void saveSignInfoTasks(SignInfoTasksEntity signInfoTasks) {
		if (null == signInfoTasks) {
			logger.error("SignInfoTasksEntity is null, save operation failed! ");
			return;
		}
		this.signInfoTasksDao.saveSignInfoTasks(signInfoTasks);

	}
	@Override
	public void deleteSignInfoTasks(SignInfoTasksEntity signInfoTasks){
		if (null == signInfoTasks) {
			logger.error("SignInfoTasksEntity is null, delete operation failed! ");
			return;
		}
		this.signInfoTasksDao.deleteSignInfoTasks(signInfoTasks);
	}
	
	@Override
	public List<SignInfoTasksEntity> findSignInfoTasks(SignInfoTasksEntity signInfoTasks) {
		if (null == signInfoTasks) {
			logger.error("SignInfoTasksEntity is null, Query operation failed! ");
			return null;
		}
		return this.signInfoTasksDao.findSignInfoTasks(signInfoTasks);
	}

}
