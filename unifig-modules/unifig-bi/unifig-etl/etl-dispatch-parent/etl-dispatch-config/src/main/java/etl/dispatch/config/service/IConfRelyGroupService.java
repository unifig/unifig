package etl.dispatch.config.service;

import java.util.List;

import etl.dispatch.config.entity.ConfRelyGroupEntity;

public interface IConfRelyGroupService {
	public List<ConfRelyGroupEntity> findConfRelyGroup(ConfRelyGroupEntity confRelyGroup);
}
