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
import etl.dispatch.java.spap.ods.domain.SpapDimIsp;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class SpapDimIspDao {
	private static Logger logger = LoggerFactory.getLogger(SpapDimIspDao.class);
	private final String findIspByNameSql = "select `id` , `name`  from bi_dim_spap.dim_isp where 1=1  and  name = '${ispName}' ";
	private final String findIspByIspIdSql = "select `id` , `name`  from bi_dim_spap.dim_isp where 1=1 and  id = ${ispId}";
	private final String saveSql = "insert into bi_dim_spap.dim_isp(`name` )VALUES( '${ispName}')";

	public List<SpapDimIsp> findIspByName(SpapDimDataSource dimDataSource, Map<String, Object> paramMap) {
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		String selectSql = this.findIspByNameSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${ispName}", String.valueOf(paramMap.get("ispName")));
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimIspDao.findIspByName");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<SpapDimIsp> dimIspList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				SpapDimIsp dimIsp = new SpapDimIsp();
				ObjectClassHelper.setFieldValue(dimIsp, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimIsp, "name", rsMap.get("name"));
				dimIspList.add(dimIsp);
			}
			return dimIspList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public List<SpapDimIsp> findIspByIspId(SpapDimDataSource dimDataSource, int ispId) {
		String selectSql = this.findIspByIspIdSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${ispId}", String.valueOf(ispId));
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimIspDao.findIspByIspId");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<SpapDimIsp> dimIspList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				SpapDimIsp dimIsp = new SpapDimIsp();
				ObjectClassHelper.setFieldValue(dimIsp, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimIsp, "name", rsMap.get("name"));
				dimIspList.add(dimIsp);
			}
			return dimIspList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public void saveDimIsp(SpapDimDataSource dimDataSource, SpapDimIsp dimIsp) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${ispName}", dimIsp.getName());
		}
		try {
			DataSource dataSource = dimDataSource.cluster1DataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimIspDao.saveDimIsp");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}
}
