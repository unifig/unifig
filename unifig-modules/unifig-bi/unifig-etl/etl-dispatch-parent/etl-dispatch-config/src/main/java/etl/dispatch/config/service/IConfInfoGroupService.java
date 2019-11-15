package etl.dispatch.config.service;

import java.util.List;

import etl.dispatch.config.entity.ConfInfoGroupEntity;

public interface IConfInfoGroupService {

	public List<ConfInfoGroupEntity> findConfInfoGroup(ConfInfoGroupEntity confInfoGroup);
}
