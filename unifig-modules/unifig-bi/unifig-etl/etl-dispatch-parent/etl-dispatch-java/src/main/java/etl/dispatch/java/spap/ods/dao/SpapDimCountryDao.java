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
import etl.dispatch.java.spap.ods.domain.SpapDimCountry;
import etl.dispatch.java.spap.ods.domain.SpapDimCountryCode;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class SpapDimCountryDao {
	private static Logger logger = LoggerFactory.getLogger(SpapDimCountryDao.class);
	private final String findCountryByNameSql = "select `id` , `country_code`, `name`  from bi_dim_spap.dim_country where 1=1 and  country_code = '${countryCode}' and  name ='${countryName}' ";
	private final String findCountryByIdSql = "select `id` , `country_code`, `name`  from bi_dim_spap.dim_country where 1=1  and  id = ${countryId} ";
	private final String findCountryCodeSql = "select `id` , `code2`, `code3`, `en_name`, `cn_name` from bi_dim_spap.dim_country_code where 1=1 ";
	private final String saveSql = "insert into bi_dim_spap.dim_country(`country_code`, `name` )VALUES( '${countryCode}', '${countryName}')";

	public List<SpapDimCountry> findCountryByName(SpapDimDataSource dimDataSource, Map<String, Object> paramMap) {
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		String selectSql = this.findCountryByNameSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${countryCode}", String.valueOf(paramMap.get("countryCode")));
			selectSql = selectSql.replace("${countryName}", String.valueOf(paramMap.get("countryName")));
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimCountryDao.findCountryByName");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<SpapDimCountry> dimCountryList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				SpapDimCountry dimCountry = new SpapDimCountry();
				ObjectClassHelper.setFieldValue(dimCountry, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimCountry, "countryCode", rsMap.get("country_code"));
				ObjectClassHelper.setFieldValue(dimCountry, "name", rsMap.get("name"));
				dimCountryList.add(dimCountry);
			}
			return dimCountryList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public List<SpapDimCountry> findCountryByCountryId(SpapDimDataSource dimDataSource, int countryId) {
		String selectSql = this.findCountryByIdSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${countryId}", String.valueOf(countryId));
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimCountryDao.findCountryByCountryId");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<SpapDimCountry> dimCountryList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				SpapDimCountry dimCountry = new SpapDimCountry();
				ObjectClassHelper.setFieldValue(dimCountry, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimCountry, "countryCode", rsMap.get("country_code"));
				ObjectClassHelper.setFieldValue(dimCountry, "name", rsMap.get("name"));
				dimCountryList.add(dimCountry);
			}
			return dimCountryList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public void saveDimCountry(SpapDimDataSource dimDataSource, SpapDimCountry dimCountry) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${countryCode}", String.valueOf(dimCountry.getCountryCode()));
			insertSql = insertSql.replace("${countryName}", dimCountry.getName());
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimCountryDao.saveDimCountry");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}

	public List<SpapDimCountryCode> findCountryCode(SpapDimDataSource dimDataSource) {
		String selectSql = this.findCountryCodeSql;
		if (StringUtil.isNullOrEmpty(selectSql)) {
			return null;
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimCountryDao.findCountryCode");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<SpapDimCountryCode> dimCountryCodeList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				SpapDimCountryCode dimCountryCode = new SpapDimCountryCode();
				ObjectClassHelper.setFieldValue(dimCountryCode, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimCountryCode, "code2", rsMap.get("code2"));
				ObjectClassHelper.setFieldValue(dimCountryCode, "code3", rsMap.get("code3"));
				ObjectClassHelper.setFieldValue(dimCountryCode, "enName", rsMap.get("en_name"));
				ObjectClassHelper.setFieldValue(dimCountryCode, "cnName", rsMap.get("cn_name"));
				dimCountryCodeList.add(dimCountryCode);
			}
			return dimCountryCodeList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}
}
