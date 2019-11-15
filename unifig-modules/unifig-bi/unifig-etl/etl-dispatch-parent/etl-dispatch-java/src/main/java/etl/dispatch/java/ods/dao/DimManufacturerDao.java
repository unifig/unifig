package etl.dispatch.java.ods.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.domain.DimManufacturer;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class DimManufacturerDao{
	private static Logger logger = LoggerFactory.getLogger(DimManufacturerDao.class);
	private final String findSql = "select `id`, `name`, `name_lowercase`  from bi_dim.dim_manufacturer where `name`='${name}'";
	private final String saveSql = "insert into bi_dim.dim_manufacturer  (`name`, `name_lowercase`)values ('${name}','${nameLowercase}')";

	/**
	 * 數據庫查詢終端平台厂商
	 * @param name
	 * @return
	 */
	public DimManufacturer findManufacturerByName(DimDataSource dimDataSource, String manufacturerName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${name}", manufacturerName);
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimManufacturerDao.findManufacturerByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				DimManufacturer dimManufacturer = new DimManufacturer();
				ObjectClassHelper.setFieldValue(dimManufacturer, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimManufacturer, "name", rsMap.get("name"));
				ObjectClassHelper.setFieldValue(dimManufacturer, "nameLowercase", rsMap.get("name_lowercase"));
				return dimManufacturer;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 保存終端平台廠商
	 * @param manufacturer
	 */
	public void saveDimManufacturer(DimDataSource dimDataSource, DimManufacturer newManufacturer) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${name}", newManufacturer.getName());
			insertSql = insertSql.replace("${nameLowercase}", newManufacturer.getName().toLowerCase());
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimManufacturerDao.saveDimManufacturer");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}
}
