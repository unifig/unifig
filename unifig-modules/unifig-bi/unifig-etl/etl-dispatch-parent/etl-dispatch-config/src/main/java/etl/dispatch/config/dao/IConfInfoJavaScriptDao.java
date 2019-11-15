package etl.dispatch.config.dao;

import java.util.List;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.config.entity.ConfInfoJavaScriptEntity;

@BaseRepository
public interface IConfInfoJavaScriptDao {

	public List<ConfInfoJavaScriptEntity> findConfInfoJavaScript(ConfInfoJavaScriptEntity confInfoJavaScript);
}
