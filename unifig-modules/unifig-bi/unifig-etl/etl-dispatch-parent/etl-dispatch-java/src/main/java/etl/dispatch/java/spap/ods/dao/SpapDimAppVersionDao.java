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
import etl.dispatch.java.spap.ods.domain.SpapDimAppVersion;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class SpapDimAppVersionDao{
	private static Logger logger = LoggerFactory.getLogger(SpapDimAppVersionDao.class);
	private final String findSql = "select `id`, `app_plat_id`, `app_id` , `app_version`  from bi_gather.conf_app_version where `app_plat_id`=${appPlatId} and `app_id`=${appId} and `app_version`='${appVersion}'";
	private final String findByIdSql = "select `id`, `app_plat_id`, `app_id` , `app_version`  from bi_gather.conf_app_version where `app_plat_id`=${appPlatId} and `app_id`=${appId}";
	private final String saveSql = "insert into bi_gather.conf_app_version  (`app_plat_id`, `app_id` , `app_version`)values ('${appPlatId}','${appId}','${appVersion}')";

	/**
	 * 數據庫查詢平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public SpapDimAppVersion findAppVersionByName(SpapDimDataSource dimDataSource, int appPlatId, int appId, String appVersionName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${appPlatId}", String.valueOf(appPlatId));
			selectSql = selectSql.replace("${appId}", String.valueOf(appId));
			selectSql = selectSql.replace("${appVersion}", appVersionName);
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimAppVersionDao.findAppVersionByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				SpapDimAppVersion dimAppVersion = new SpapDimAppVersion();
				ObjectClassHelper.setFieldValue(dimAppVersion, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimAppVersion, "appPlatId", rsMap.get("app_plat_id"));
				ObjectClassHelper.setFieldValue(dimAppVersion, "appId", rsMap.get("app_id"));
				ObjectClassHelper.setFieldValue(dimAppVersion, "appVersion", rsMap.get("app_version"));
				return dimAppVersion;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 保存平台应用版本
	 * @param osVersion
	 */
	public void saveDimAppVersion(SpapDimDataSource dimDataSource, SpapDimAppVersion newAppVersion) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${appPlatId}", String.valueOf(newAppVersion.getAppPlatId()));
			insertSql = insertSql.replace("${appId}", String.valueOf(newAppVersion.getAppId()));
			insertSql = insertSql.replace("${appVersion}", newAppVersion.getAppVersion());
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimAppVersionDao.saveAppVersion");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}

	public List<SpapDimAppVersion> findAppVersionById(SpapDimDataSource dimDataSource,int appPlatId, int appId) {
		String selectSql = this.findByIdSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${appPlatId}", String.valueOf(appPlatId));
			selectSql = selectSql.replace("${appId}", String.valueOf(appId));
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimAppVersionDao.findAppVersionById");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				List<SpapDimAppVersion> dimAppVersions = new ArrayList<SpapDimAppVersion>();
				for (Map rsMap : rsMaplist) {
					SpapDimAppVersion dimAppVersion = new SpapDimAppVersion();
					ObjectClassHelper.setFieldValue(dimAppVersion, "id", rsMap.get("id"));
					ObjectClassHelper.setFieldValue(dimAppVersion, "appPlatId", rsMap.get("app_plat_id"));
					ObjectClassHelper.setFieldValue(dimAppVersion, "appId", rsMap.get("app_id"));
					ObjectClassHelper.setFieldValue(dimAppVersion, "appVersion", rsMap.get("app_version"));
					dimAppVersions.add(dimAppVersion);
				}
				return dimAppVersions;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}
}
