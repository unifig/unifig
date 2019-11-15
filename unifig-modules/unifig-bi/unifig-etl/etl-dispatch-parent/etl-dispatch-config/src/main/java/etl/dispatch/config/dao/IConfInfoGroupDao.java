package etl.dispatch.config.dao;

import java.util.List;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.config.entity.ConfInfoGroupEntity;

@BaseRepository
public interface IConfInfoGroupDao {

	public List<ConfInfoGroupEntity> findConfInfoGroup(ConfInfoGroupEntity confInfoGroup);
}
