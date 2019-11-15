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
import etl.dispatch.java.spap.ods.domain.SpapDimIndustry;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class SpapDimIndustryDao{
	private static Logger logger = LoggerFactory.getLogger(SpapDimIndustryDao.class);
	private final String findSql = "select `id`, `name`  from bi_dim_spap.dim_industry where `name`='${name}'";
	private final String saveSql = "insert into bi_dim_spap.dim_industry  (`name`)values ('${name}')";

	/**
	 * 數據庫查詢工作行业
	 * @param name
	 * @return
	 */
	public SpapDimIndustry findIndustryByName(SpapDimDataSource dimDataSource, String industryName) {
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${name}", industryName);
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimIndustryDao.findIndustryByName");
			if (null != rsMaplist && !rsMaplist.isEmpty()) {
				Map<Object, Object> rsMap = rsMaplist.get(0);
				SpapDimIndustry dimIndustry = new SpapDimIndustry();
				ObjectClassHelper.setFieldValue(dimIndustry, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimIndustry, "name", rsMap.get("name"));
				return dimIndustry;
			}
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 保存工作行业
	 * @param newDimOs
	 */
	public void saveDimIndustry(SpapDimDataSource dimDataSource, SpapDimIndustry newIndustry) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${name}", newIndustry.getName());
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimIndustryDao.saveDimIndustry");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}
}
