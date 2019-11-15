package etl.dispatch.config.dao;

import java.util.List;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.config.entity.ConfRelyGroupEntity;

@BaseRepository
public interface IConfRelyGroupDao {

	public List<ConfRelyGroupEntity> findConfRelyGroup(ConfRelyGroupEntity confRelyGroup);
}
