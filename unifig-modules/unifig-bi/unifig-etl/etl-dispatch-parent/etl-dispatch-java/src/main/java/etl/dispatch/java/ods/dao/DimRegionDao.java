package etl.dispatch.java.ods.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.domain.DimRegion;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class DimRegionDao {
	private static Logger logger = LoggerFactory.getLogger(DimRegionDao.class);
	private final String findRegionByNameSql = "select `id` , `country_id`,  `name`  from bi_dim.dim_region where 1=1 and  country_id =${countryId}  and  name ='${regionName}' ";
	private final String findRegionByRegionIdSql = "select `id` , `country_id`,  `name`  from bi_dim.dim_region where 1=1 and  id =${regionId} and  country_id =${countryId} ";
	private final String saveSql = "insert into bi_dim.dim_region(`country_id`,  `name` )VALUES( ${countryId}, '${regionName}')";

	public List<DimRegion> findDimRegionByName(DimDataSource dimDataSource, Map<String, Object> paramMap) {
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		String selectSql = this.findRegionByNameSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${countryId}", String.valueOf(paramMap.get("countryId")));
			selectSql = selectSql.replace("${regionName}", String.valueOf(paramMap.get("regionName")));
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimRegionDao.findDimRegionByName");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<DimRegion> dimRegionList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				DimRegion dimRegion = new DimRegion();
				ObjectClassHelper.setFieldValue(dimRegion, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimRegion, "countryId", rsMap.get("country_id"));
				ObjectClassHelper.setFieldValue(dimRegion, "name", rsMap.get("name"));
				dimRegionList.add(dimRegion);
			}
			return dimRegionList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public List<DimRegion> findDimRegionByRegionId(DimDataSource dimDataSource, Map<String, Object> paramMap) {
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		String selectSql = this.findRegionByRegionIdSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${regionId}", String.valueOf(paramMap.get("regionId")));
			selectSql = selectSql.replace("${countryId}", String.valueOf(paramMap.get("countryId")));
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimRegionDao.findDimRegionByRegionId");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<DimRegion> dimRegionList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				DimRegion dimRegion = new DimRegion();
				ObjectClassHelper.setFieldValue(dimRegion, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimRegion, "countryId", rsMap.get("country_id"));
				ObjectClassHelper.setFieldValue(dimRegion, "name", rsMap.get("name"));
				dimRegionList.add(dimRegion);
			}
			return dimRegionList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public void saveDimRegion(DimDataSource dimDataSource, DimRegion dimRegion) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${countryId}", String.valueOf(dimRegion.getCountryId()));
			insertSql = insertSql.replace("${regionName}", dimRegion.getName());
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimRegionDao.saveDimRegion");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}

}
