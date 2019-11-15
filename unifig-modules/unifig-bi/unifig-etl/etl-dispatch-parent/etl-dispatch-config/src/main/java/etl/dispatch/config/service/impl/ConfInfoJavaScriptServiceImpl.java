package etl.dispatch.config.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etl.dispatch.config.dao.IConfInfoJavaScriptDao;
import etl.dispatch.config.entity.ConfInfoJavaScriptEntity;
import etl.dispatch.config.service.IConfInfoJavaScriptService;

@Service
public class ConfInfoJavaScriptServiceImpl implements IConfInfoJavaScriptService {
	private static Logger logger = LoggerFactory.getLogger(ConfInfoJavaScriptServiceImpl.class);
	@Autowired
	private IConfInfoJavaScriptDao confInfoJavaScriptDao;

	@Override
	public List<ConfInfoJavaScriptEntity> findConfInfoJavaScript(ConfInfoJavaScriptEntity confInfoJavaScript) {
		if (null == confInfoJavaScript) {
			logger.error("ConfInfoJavaScriptEntity is null, Query operation failed! ");
			return null;
		}
		return confInfoJavaScriptDao.findConfInfoJavaScript(confInfoJavaScript);
	}
}
