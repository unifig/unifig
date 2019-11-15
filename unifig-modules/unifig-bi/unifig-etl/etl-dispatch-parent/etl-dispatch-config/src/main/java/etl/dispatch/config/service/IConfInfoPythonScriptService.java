package etl.dispatch.config.service;

import java.util.List;

import etl.dispatch.config.entity.ConfInfoPythonScriptEntity;

public interface IConfInfoPythonScriptService {
	public List<ConfInfoPythonScriptEntity> findConfInfoPythonScript(ConfInfoPythonScriptEntity confInfoPythonScript);
}
