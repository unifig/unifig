package etl.dispatch.java.ods.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.domain.DimOsVersion;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class DimOsVersionDao{
	private static Logger logger = LoggerFactory.getLogger(DimOsVersionDao.class);
	private final String findSql = "select `id`, `os_id`, `name`  from bi_dim.dim_os_version where `os_id`=${osId} and `name`='${name}'";
	private final String saveSql = "insert into bi_dim.dim_os_version  (`os_id`, `name`)values ('${osId}','${name}')";

	/**
	 * 數據庫查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public DimOsVersion findOsVersionByName(DimDataSource dimDataSource, short osId, String osVersionName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${osId}", String.valueOf(osId));
			selectSql = selectSql.replace("${name}", osVersionName);
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimOsVersionDao.findOsVersionByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				DimOsVersion dimOsVersion = new DimOsVersion();
				ObjectClassHelper.setFieldValue(dimOsVersion, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimOsVersion, "osId", rsMap.get("os_id"));
				ObjectClassHelper.setFieldValue(dimOsVersion, "name", rsMap.get("name"));
				return dimOsVersion;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 保存操作系统版本
	 * @param osVersion
	 */
	public void saveDimOsVersion(DimDataSource dimDataSource, DimOsVersion newOsVersion) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${osId}", String.valueOf(newOsVersion.getOsId()));
			insertSql = insertSql.replace("${name}", newOsVersion.getName());
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimOsVersionDao.saveDimOsVersion");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}
}
