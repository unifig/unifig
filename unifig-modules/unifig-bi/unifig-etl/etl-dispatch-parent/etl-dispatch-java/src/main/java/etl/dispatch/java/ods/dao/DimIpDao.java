package etl.dispatch.java.ods.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.domain.DimIp;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

@Service
public class DimIpDao{
	private static Logger logger = LoggerFactory.getLogger(DimIpDao.class);
	private final String findSql = "select `id` , `ip`, `ip_num`, `country_id`, `region_id`, `city_id`, `isp_id` from bi_dim.dim_ip where 1=1 and ip = '${ip}' and  ip_num = ${ipNum} ";
	private final String saveSql = "insert into bi_dim.dim_ip(`ip`, `ip_num`, `country_id`, `region_id`, `city_id`, `isp_id` )VALUES('${ip}', ${ipNum}, ${countryId}, ${regionId}, ${cityId}, ${ispId} )";

	public List<DimIp> findDimIpByIp(DimDataSource dimDataSource, Map<String, Object> paramMap) {
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		String selectSql = this.findSql;
		if (!StringUtil.isNullOrEmpty(selectSql)) {
			selectSql = selectSql.replace("${ip}", String.valueOf(paramMap.get("ip")));
			selectSql = selectSql.replace("${ipNum}", String.valueOf(paramMap.get("ipNum")));
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			List<Map> rsMaplist = SqlUtils.querySqlList(dataSource, selectSql, "dimIpDao.findDimIpByIp");
			if (null == rsMaplist || rsMaplist.isEmpty()) {
				return null;
			}
			List<DimIp> dimIpList = new ArrayList<>();
			for (Map rsMap : rsMaplist) {
				DimIp dimIp = new DimIp();
				ObjectClassHelper.setFieldValue(dimIp, "id", rsMap.get("id"));
				ObjectClassHelper.setFieldValue(dimIp, "ip", rsMap.get("ip"));
				ObjectClassHelper.setFieldValue(dimIp, "ipNum", rsMap.get("ip_num"));
				ObjectClassHelper.setFieldValue(dimIp, "countryId", rsMap.get("country_id"));
				ObjectClassHelper.setFieldValue(dimIp, "regionId", rsMap.get("region_id"));
				ObjectClassHelper.setFieldValue(dimIp, "cityId", rsMap.get("city_id"));
				ObjectClassHelper.setFieldValue(dimIp, "ispId", rsMap.get("isp_id"));
				dimIpList.add(dimIp);
			}
			return dimIpList;
		} catch (SQLException ex) {
			logger.error("fatal error while select dim table selectSql: " + selectSql + ", message: " + ex.getMessage(), ex);
		}
		return null;
	}

	public void saveDimIp(DimDataSource dimDataSource, DimIp dimIp) {
		String insertSql = this.saveSql;
		if (!StringUtil.isNullOrEmpty(insertSql)) {
			insertSql = insertSql.replace("${ip}", dimIp.getIp());
			insertSql = insertSql.replace("${ipNum}", String.valueOf(dimIp.getIpNum()));
			insertSql = insertSql.replace("${countryId}", String.valueOf(dimIp.getCountryId()));
			insertSql = insertSql.replace("${regionId}", String.valueOf(dimIp.getRegionId()));
			insertSql = insertSql.replace("${cityId}", String.valueOf(dimIp.getCityId()));
			insertSql = insertSql.replace("${ispId}", String.valueOf(dimIp.getIspId()));
		}
		try {
			DataSource dataSource = dimDataSource.clusterDataSource();
			SqlUtils.sqlExecute(dataSource, insertSql, "dimIpDao.saveDimIp");
		} catch (SQLException ex) {
			logger.error("fatal error while save dim table insertSql: " + insertSql + ", message: " + ex.getMessage(), ex);
		}
	}
}
