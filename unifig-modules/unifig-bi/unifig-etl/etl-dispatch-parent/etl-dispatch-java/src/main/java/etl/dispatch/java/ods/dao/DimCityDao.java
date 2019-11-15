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
import etl.dispatch.java.ods.domain.DimCity;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class DimCityDao {
	private static Logger logger = LoggerFactory.getLogger(DimCityDao.class);
	private final String findCityByNameSql = "select `id` , `country_id`, `region_id`, `name`  from bi_dim.dim_city where 1=1 and  country_id =${countryId} and  region_id =${regionId} and  name ='${cityName}' ";
	private final String findCityByCityIdSql = "select `id` , `country_id`, `region_id`, `name`  from bi_dim.dim_city where 1=1 and  id =${cityId} and  country_id =${countryId} and  region_id =${regionId} ";
	private final String saveSql = "insert into bi_dim.dim_city(`country_id`, `region_id`, `name` )VALUES( ${countryId}, ${regionId}, '${cityName}')";

	public List<DimCity> findCityByName(DimDataSource dimDataSource, Map<String, Object> paramMap) {
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		String selectSql = this.findCityByNameSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${countryId}", String.valueOf(paramMap.get("countryId")));
			selectSql = selectSql.replace("${regionId}", String.valueOf(paramMap.get("regionId")));
			selectSql = selectSql.replace("${cityName}", String.valueOf(paramMap.get("cityName")));
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimCityDao.findCityByName");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<DimCity> dimCityList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				DimCity dimCity = new DimCity();
				ObjectClassHelper.setFieldValue(dimCity, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimCity, "countryId", rsMap.get("country_id"));
				ObjectClassHelper.setFieldValue(dimCity, "regionId", rsMap.get("region_id"));
				ObjectClassHelper.setFieldValue(dimCity, "name", rsMap.get("name"));
				dimCityList.add(dimCity);
			}
			return dimCityList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public List<DimCity> findCityByCityId(DimDataSource dimDataSource, Map<String, Object> paramMap) {
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		String selectSql = this.findCityByCityIdSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${cityId}", String.valueOf(paramMap.get("cityId")));
			selectSql = selectSql.replace("${countryId}", String.valueOf(paramMap.get("countryId")));
			selectSql = selectSql.replace("${regionId}", String.valueOf(paramMap.get("regionId")));
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimCityDao.findCityByCityId");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<DimCity> dimCityList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				DimCity dimCity = new DimCity();
				ObjectClassHelper.setFieldValue(dimCity, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimCity, "countryId", rsMap.get("country_id"));
				ObjectClassHelper.setFieldValue(dimCity, "regionId", rsMap.get("region_id"));
				ObjectClassHelper.setFieldValue(dimCity, "name", rsMap.get("name"));
				dimCityList.add(dimCity);
			}
			return dimCityList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public void saveDimCity(DimDataSource dimDataSource, DimCity dimCity) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${countryId}", String.valueOf(dimCity.getCountryId()));
			insertSql = insertSql.replace("${regionId}", String.valueOf(dimCity.getRegionId()));
			insertSql = insertSql.replace("${cityName}", dimCity.getName());
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimCityDao.saveDimCity");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}
}
