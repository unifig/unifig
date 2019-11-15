package etl.dispatch.config.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etl.dispatch.config.dao.IConfInfoGroupDao;
import etl.dispatch.config.entity.ConfInfoGroupEntity;
import etl.dispatch.config.service.IConfInfoGroupService;

@Service
public class ConfInfoGroupServiceImpl implements IConfInfoGroupService {
	private static Logger logger = LoggerFactory.getLogger(ConfInfoGroupServiceImpl.class);

	@Autowired
	private IConfInfoGroupDao confInfoGroupDao;

	@Override
	public List<ConfInfoGroupEntity> findConfInfoGroup(ConfInfoGroupEntity confInfoGroup) {
		if (null == confInfoGroup) {
			logger.error("ConfInfoGroupEntity is null, Query operation failed! ");
			return null;
		}
		return confInfoGroupDao.findConfInfoGroup(confInfoGroup);
	}

}
