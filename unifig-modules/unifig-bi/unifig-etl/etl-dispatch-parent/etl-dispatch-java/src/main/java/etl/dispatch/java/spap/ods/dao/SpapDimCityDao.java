package etl.dispatch.java.spap.ods.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.base.datasource.SpapDimDataSource;
import etl.dispatch.java.spap.ods.domain.SpapDimCity;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class SpapDimCityDao {
	private static Logger logger = LoggerFactory.getLogger(SpapDimCityDao.class);
	private final String findCityByNameSql = "select `id` , `country_id`, `region_id`, `name`  from bi_dim_spap.dim_city where 1=1 and  country_id =${countryId} and  region_id =${regionId} and  name ='${cityName}' ";
	private final String findCityByCityIdSql = "select `id` , `country_id`, `region_id`, `name`  from bi_dim_spap.dim_city where 1=1 and  id =${cityId} and  country_id =${countryId} and  region_id =${regionId} ";
	private final String saveSql = "insert into bi_dim_spap.dim_city(`country_id`, `region_id`, `name` )VALUES( ${countryId}, ${regionId}, '${cityName}')";

	public List<SpapDimCity> findCityByName(SpapDimDataSource dimDataSource, Map<String, Object> paramMap) {
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
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimCityDao.findCityByName");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<SpapDimCity> dimCityList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				SpapDimCity dimCity = new SpapDimCity();
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

	public List<SpapDimCity> findCityByCityId(SpapDimDataSource dimDataSource, Map<String, Object> paramMap) {
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
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimCityDao.findCityByCityId");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<SpapDimCity> dimCityList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				SpapDimCity dimCity = new SpapDimCity();
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

	public void saveDimCity(SpapDimDataSource dimDataSource, SpapDimCity dimCity) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${countryId}", String.valueOf(dimCity.getCountryId()));
			insertSql = insertSql.replace("${regionId}", String.valueOf(dimCity.getRegionId()));
			insertSql = insertSql.replace("${cityName}", dimCity.getName());
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimCityDao.saveDimCity");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}
}
