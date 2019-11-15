package etl.dispatch.config.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etl.dispatch.config.dao.IConfInfoPythonScriptDao;
import etl.dispatch.config.entity.ConfInfoPythonScriptEntity;
import etl.dispatch.config.service.IConfInfoPythonScriptService;

@Service
public class ConfInfoPythonScriptServiceImpl implements IConfInfoPythonScriptService {
	private static Logger logger = LoggerFactory.getLogger(ConfInfoPythonScriptServiceImpl.class);
	@Autowired
	private IConfInfoPythonScriptDao confInfoPythonScriptDao;
	@Override
	public List<ConfInfoPythonScriptEntity> findConfInfoPythonScript(ConfInfoPythonScriptEntity confInfoPythonScript) {
		if(null == confInfoPythonScript){
			logger.error("ConfInfoPythonScriptEntity is null, Query operation failed! ");
			return null;
		}
		return confInfoPythonScriptDao.findConfInfoPythonScript(confInfoPythonScript);
	}
}
