package etl.dispatch.config.dao;

import java.util.List;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.config.entity.ConfInfoPythonScriptEntity;

@BaseRepository
public interface IConfInfoPythonScriptDao {

	public List<ConfInfoPythonScriptEntity> findConfInfoPythonScript(ConfInfoPythonScriptEntity confInfoPythonScript);
}
