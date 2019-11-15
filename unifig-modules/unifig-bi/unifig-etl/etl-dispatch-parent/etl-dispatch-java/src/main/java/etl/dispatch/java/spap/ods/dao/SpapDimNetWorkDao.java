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
import etl.dispatch.java.spap.ods.domain.SpapDimNetWork;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class SpapDimNetWorkDao{
	private static Logger logger = LoggerFactory.getLogger(SpapDimNetWorkDao.class);
	private final String findSql = "select `id`, `name`  from bi_dim_spap.dim_network where `name`='${name}'";
	private final String saveSql = "insert into bi_dim_spap.dim_network  (`name`)values ('${name}')";

	/**
	 * 數據庫查詢网络类型
	 * @param name
	 * @return
	 */
	public SpapDimNetWork findNetWorkByName(SpapDimDataSource dimDataSource, String netWorkName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${name}", netWorkName);
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimNetWorkDao.findNetWorkByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				SpapDimNetWork dimNetWork = new SpapDimNetWork();
				ObjectClassHelper.setFieldValue(dimNetWork, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimNetWork, "name", rsMap.get("name"));
				return dimNetWork;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 保存网络类型
	 * @param newDimOs
	 */
	public void saveDimNetWork(SpapDimDataSource dimDataSource, SpapDimNetWork newNetWork) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${name}", newNetWork.getName());
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimNetWorkDao.saveDimNetWork");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}

	}

}
