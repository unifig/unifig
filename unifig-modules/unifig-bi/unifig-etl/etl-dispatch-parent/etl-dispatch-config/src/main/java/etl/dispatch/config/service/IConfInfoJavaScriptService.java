package etl.dispatch.config.service;

import java.util.List;

import etl.dispatch.config.entity.ConfInfoJavaScriptEntity;

public interface IConfInfoJavaScriptService {
	public List<ConfInfoJavaScriptEntity> findConfInfoJavaScript(ConfInfoJavaScriptEntity confInfoJavaScript);
}
