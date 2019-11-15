package etl.dispatch.config.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etl.dispatch.config.dao.IConfRelyGroupDao;
import etl.dispatch.config.entity.ConfRelyGroupEntity;
import etl.dispatch.config.service.IConfRelyGroupService;

@Service
public class ConfRelyGroupServiceImpl implements IConfRelyGroupService {
	private static Logger logger = LoggerFactory.getLogger(ConfRelyGroupServiceImpl.class);

	@Autowired
	private IConfRelyGroupDao confRelyGroupDao;

	@Override
	public List<ConfRelyGroupEntity> findConfRelyGroup(ConfRelyGroupEntity confRelyGroup) {
		if (null == confRelyGroup) {
			logger.error("ConfRelyGroupEntity is null, Query operation failed! ");
			return null;
		}
		return confRelyGroupDao.findConfRelyGroup(confRelyGroup);
	}
}
