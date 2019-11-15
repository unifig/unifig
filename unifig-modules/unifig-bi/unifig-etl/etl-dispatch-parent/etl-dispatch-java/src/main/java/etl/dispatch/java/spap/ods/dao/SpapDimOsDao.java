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
import etl.dispatch.java.spap.ods.domain.SpapDimOs;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class SpapDimOsDao{
	private static Logger logger = LoggerFactory.getLogger(SpapDimOsDao.class);
	private final String findSql = "select `id`, `name`  from bi_dim_spap.dim_os where `name`='${name}'";
	private final String saveSql = "insert into bi_dim_spap.dim_os  (`name`)values ('${name}')";

	/**
	 * 數據庫查詢终端操作系统
	 * @param name
	 * @return
	 */
	public SpapDimOs findOsByName(SpapDimDataSource dimDataSource, String osName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${name}", osName);
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimOsDao.findOsByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				SpapDimOs dimOs = new SpapDimOs();
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
	public void saveDimOs(SpapDimDataSource dimDataSource, SpapDimOs newOs) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${name}", newOs.getName());
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimOsDao.saveDimOs");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}

	}

}
