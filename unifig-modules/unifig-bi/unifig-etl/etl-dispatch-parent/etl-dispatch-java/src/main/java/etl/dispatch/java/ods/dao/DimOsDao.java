package etl.dispatch.java.ods.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.domain.DimOs;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class DimOsDao{
	private static Logger logger = LoggerFactory.getLogger(DimOsDao.class);
	private final String findSql = "select `id`, `name`  from bi_dim.dim_os where `name`='${name}'";
	private final String saveSql = "insert into bi_dim.dim_os  (`name`)values ('${name}')";

	/**
	 * 數據庫查詢终端操作系统
	 * @param name
	 * @return
	 */
	public DimOs findOsByName(DimDataSource dimDataSource, String osName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${name}", osName);
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimOsDao.findOsByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				DimOs dimOs = new DimOs();
				ObjectClassHelper.setFieldValue(dimOs, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimOs, "name", rsMap.get("name"));
				return dimOs;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 保存终端操作系统
	 * @param newDimOs
	 */
	public void saveDimOs(DimDataSource dimDataSource, DimOs newOs) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${name}", newOs.getName());
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimOsDao.saveDimOs");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}

	}

}
