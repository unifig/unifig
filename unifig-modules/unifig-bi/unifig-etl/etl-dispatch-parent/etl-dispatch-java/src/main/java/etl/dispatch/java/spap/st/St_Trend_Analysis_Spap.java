package etl.dispatch.java.spap.st;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.java.datasource.DataSourcePool;
import etl.dispatch.script.AbstractScript;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.script.ScriptCallBack;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.script.util.ScriptTimeUtil;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.StringUtil;

/**
 * 趋势 统计
 */
@Service
public class St_Trend_Analysis_Spap extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Trend_Analysis_Spap.class);
	private static final String configJsonPath = "classpath*:conf/spapjson/st_trend_analysis.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.trend_analysis_spap";
	}

	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		Map<String, Object> paramMap = scriptBean.getParamMap();
		DataSource dataSource = null;
		if (null != paramMap && !paramMap.isEmpty()) {
			Map<String, Object> dataSourceMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_DATASOURCE);
			if (null != dataSourceMap && !dataSourceMap.isEmpty()) {
				dataSourcePool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				dataSource = dataSourcePool.getDataSource(dataSourceMap);
				if (null == dataSource) {
					super.callback(false, "数据源获取失败; dataSource config:" + JSON.toJSONString(dataSourceMap), scriptBean, callback);
				}else{
					logger.info(" Server Ip:"+IpUtils.getIPAddress()+"---> [" + this.getClass().getCanonicalName() + "]; dataSource url:"+ dataSourceMap.get("url"));
				}
			}
		}
		try {
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			String optime_month = optime_yesday.substring(0, optime_yesday.length() -2);
			
			// 创建当月(DM)统计表
			String  target_dm_sql= super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if(!StringUtil.isNullOrEmpty(target_dm_sql)){
				target_dm_sql = target_dm_sql.replace("st_trend_analysis_dm_yyyymm", "st_trend_analysis_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_dm_sql, this.getName());
			}
			
			// 支持重跑，删除dm当天数据
			String delete_yes_dmDate = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_dmDate)){
				delete_yes_dmDate = delete_yes_dmDate.replace("st_trend_analysis_dm_yyyymm", "st_trend_analysis_dm_" + optime_month);
				delete_yes_dmDate = delete_yes_dmDate.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_dmDate, this.getName());
			}
			
			/**
			 * 单日统计>>>>>> st_trend_analysis_ds_yyyymmdd
			 */
			
			// 删除单日临时表
			String drop_tmp_ds_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_tmp_ds_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_tmp_ds_yyyymmdd)) {
				SqlUtils.sqlExecute(dataSource, drop_tmp_ds_yyyymmdd, this.getName());
			}
			// 创建单日临时表
			String create_tmp_ds_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_tmp_ds_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(create_tmp_ds_yyyymmdd)) {
				SqlUtils.sqlExecute(dataSource, create_tmp_ds_yyyymmdd, this.getName());
			}

			// Cube聚合组合条件
			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`","`app_version_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 获取(DS)InsertTmpSQL
			String insertTmpDsSql = super.getJsonConfigValue(configJsonPath, "insertTmpDsSql");

			// 获取(DS)SelectNewTmpSQL
			String selectTmpNewUserDsSql = super.getJsonConfigValue(configJsonPath, "selectTmpNewUserDsSql");
			if (!StringUtil.isNullOrEmpty(selectTmpNewUserDsSql)) {
				selectTmpNewUserDsSql = selectTmpNewUserDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				selectTmpNewUserDsSql = selectTmpNewUserDsSql.replace("${is_new}", "0");//0-新用户 1-老用户
			}

			// 获取(DS)New user Cube聚合SQL
			StringBuffer strNewDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strNewDsBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strNewDsBuffer.append(" UNION ");
					}
					strNewDsBuffer.append(" " + selectTmpNewUserDsSql + " ");
					strNewDsBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)New user Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strNewDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strNewDsBuffer.toString(), this.getName());
			}

			// 获取(DS)SelectSignTmpSQL
			String selectTmpSignUserDsSql = super.getJsonConfigValue(configJsonPath, "selectTmpSignUserDsSql");
			if (!StringUtil.isNullOrEmpty(selectTmpSignUserDsSql)) {
				selectTmpSignUserDsSql = selectTmpSignUserDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}

			// 获取(DS)Sign user Cube聚合SQL
			StringBuffer strSignDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strSignDsBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strSignDsBuffer.append(" UNION ");
					}
					strSignDsBuffer.append(" " + selectTmpSignUserDsSql + " ");
					strSignDsBuffer.append(" group by `statis_date`, " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)Sign user Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strSignDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strSignDsBuffer.toString(), this.getName());
			}

			// 插入(DS) Cube聚合数据
			String insertDsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
			if (!StringUtil.isNullOrEmpty(insertDsSelectSql)) {
				insertDsSelectSql = insertDsSelectSql.replace("st_trend_analysis_dm_yyyymm", "st_trend_analysis_dm_" + optime_month);
				insertDsSelectSql = insertDsSelectSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDsSelectSql, this.getName());
			}
			// 删除单日临时表
			if (!StringUtil.isNullOrEmpty(drop_tmp_ds_yyyymmdd)) {
				SqlUtils.sqlExecute(dataSource, drop_tmp_ds_yyyymmdd, this.getName());
			}
			
			// 脚本结束回调状态
			super.callback(true, null, scriptBean, callback);
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(dataSource) + ",message: " + ex.getMessage(), scriptBean, callback);
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}
}
