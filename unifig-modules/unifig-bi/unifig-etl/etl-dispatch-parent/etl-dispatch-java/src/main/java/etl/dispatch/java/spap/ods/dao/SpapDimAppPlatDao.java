package etl.dispatch.java.spap.ods.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.base.datasource.SpapDimDataSource;
import etl.dispatch.java.spap.ods.domain.SpapDimAppPlat;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class SpapDimAppPlatDao{
	private static Logger logger = LoggerFactory.getLogger(SpapDimAppPlatDao.class);
	private final String findSql = "select `id`, `name`  from bi_dim_spap.dim_app_plat where `name`='${name}'";
	private final String saveSql = "insert into bi_dim_spap.dim_app_plat  (`name`)values ('${name}')";

	/**
	 * 數據庫查詢平台应用类型
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public SpapDimAppPlat findAppPlatByName(SpapDimDataSource dimDataSource, String appPlatName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${name}", appPlatName);
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimAppPlatDao.findAppPlatByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				SpapDimAppPlat dimAppPlat = new SpapDimAppPlat();
				ObjectClassHelper.setFieldValue(dimAppPlat, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimAppPlat, "name", rsMap.get("name"));
				return dimAppPlat;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 保存平台应用类型
	 * @param newDimOs
	 */
	public void saveDimAppPlat(SpapDimDataSource dimDataSource, SpapDimAppPlat newAppPlat) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${name}", newAppPlat.getName());
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimAppPlatDao.saveDimAppPlat");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}

}
