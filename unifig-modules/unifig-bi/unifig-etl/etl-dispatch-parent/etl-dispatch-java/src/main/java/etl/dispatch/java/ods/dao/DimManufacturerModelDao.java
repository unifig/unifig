package etl.dispatch.java.ods.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.domain.DimManufacturerModel;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class DimManufacturerModelDao{
	private static Logger logger = LoggerFactory.getLogger(DimManufacturerModelDao.class);
	private final String findSql = "select `id`, `manufacturer_id`, `name` , `name_lowercase` from bi_dim.dim_manufacturer_model where `manufacturer_id`=${manufacturerId} and `name`='${name}'";
	private final String saveSql = "insert into bi_dim.dim_manufacturer_model  (`manufacturer_id`, `name` , `name_lowercase`)values ('${manufacturerId}','${name}','${nameLowercase}')";

	/**
	 * 數據庫查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @return
	 */
	public DimManufacturerModel findManufacturerModelByName(DimDataSource dimDataSource, int manufacturerId, String manufacturerModelName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${manufacturerId}", String.valueOf(manufacturerId));
			selectSql = selectSql.replace("${name}", manufacturerModelName);
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimManufacturerModelDao.findManufacturerModelByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				DimManufacturerModel dimManufacturerModel = new DimManufacturerModel();
				ObjectClassHelper.setFieldValue(dimManufacturerModel, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimManufacturerModel, "manufacturerId", rsMap.get("manufacturer_id"));
				ObjectClassHelper.setFieldValue(dimManufacturerModel, "name", rsMap.get("name"));
				ObjectClassHelper.setFieldValue(dimManufacturerModel, "nameLowercase", rsMap.get("name_lowercase"));
				return dimManufacturerModel;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 保存終端设备型号
	 * @param newManufacturerModel
	 */
	public void saveDimManufacturerModel(DimDataSource dimDataSource, DimManufacturerModel newManufacturerModel) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${manufacturerId}", String.valueOf(newManufacturerModel.getManufacturerId()));
			insertSql = insertSql.replace("${name}", newManufacturerModel.getName());
			insertSql = insertSql.replace("${nameLowercase}", newManufacturerModel.getName().toLowerCase());
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimManufacturerModelDao.saveDimManufacturerModel");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}

}
