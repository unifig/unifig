package etl.dispatch.java.dwd;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

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

@Service
public class Dwd_EngineMessageLog extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(Dwd_EngineMessageLog.class);
	private static final String configJsonPath = "classpath*:conf/json/dwd_engineMessage.json";
	private static final String select_source_storeId = "${store_id}";
	private static final String source_ods_yyyymmdd = "ods_engine_message_log_dm_yyyymm";
	private static final String target_dwd_yyyymmdd = "dwd_engine_message_log_dm_yyyymm";
	private DataSourcePool dataSourcePool;
	private Optional<Integer> saveDays = Optional.empty();

	public String getName() {
		return "dwd.engineMessage";
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
				} else {
					logger.info(" Server Ip:" + IpUtils.getIPAddress() + "---> [" + this.getClass().getCanonicalName() + "]; dataSource url:" + dataSourceMap.get("url"));
				}
			}
			saveDays = Optional.ofNullable((Integer) paramMap.get(CommonConstants.PROP_PARAMS_SAVEDAYS));
		}

		try {
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			String optime_month = optime_yesday.substring(0, optime_yesday.length() - 2);

			// 创建目标表
			String target_sql = super.getJsonConfigValue(configJsonPath, "templateDmTableSql");
			if (!StringUtil.isNullOrEmpty(target_sql)) {
				target_sql = target_sql.replace(target_dwd_yyyymmdd, "dwd_engine_message_log_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_sql, this.getName());
			} else {
				logger.error("path " + configJsonPath + "; create target table, get sql with key'templateTableSql' config value is null ");
			}

			// 创建临时表
			String createTmpSql = super.getJsonConfigValue(configJsonPath, "createTmpSql");
			if (!StringUtil.isNullOrEmpty(createTmpSql)) {
				SqlUtils.sqlExecute(dataSource, createTmpSql, this.getName());
			}

			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("dwd_engine_message_log_dm_yyyymm", "dwd_engine_message_log_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			// 插入临时表
			String insertTmpSql = super.getJsonConfigValue(configJsonPath, "insertTmpSql");
			if (!StringUtil.isNullOrEmpty(insertTmpSql)) {
				insertTmpSql = insertTmpSql.replace("ods_engine_message_log_dm_yyyymm", "ods_engine_message_log_dm_" + optime_month);
				insertTmpSql = insertTmpSql.replace("${store_id}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, insertTmpSql, this.getName());
			}

			// 插入sql
			String insertSql = super.getJsonConfigValue(configJsonPath, "insertSql");
			if (!StringUtil.isNullOrEmpty(insertSql)) {
				insertSql = insertSql.replace("bi_dwd.dwd_engine_message_log_dm_yyyymm", "bi_dwd.dwd_engine_message_log_dm_" + optime_month);
			}

			// selectGroupSql
			String userSelectSql = super.getJsonConfigValue(configJsonPath, "userSelectSql");

			// selectUserSql
			String groupSelectSql = super.getJsonConfigValue(configJsonPath, "groupSelectSql");

			// 插入group
			if (!StringUtil.isNullOrEmpty(insertSql) && !StringUtil.isNullOrEmpty(groupSelectSql)) {
				groupSelectSql = groupSelectSql.replace("user_info_yyyymmdd", "user_info_" + optime_yesday);
				groupSelectSql = groupSelectSql.replace("engine_group_info_yyyymmdd", "engine_group_info_" + optime_yesday);
				groupSelectSql = groupSelectSql.replace("${store_id}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, insertSql + groupSelectSql, this.getName());
			}

			// 删除tmp表中group数据
			String delete_group_date = super.getJsonConfigValue(configJsonPath, "delete_group_date");
			if (!StringUtil.isNullOrEmpty(delete_group_date)) {
				delete_group_date = delete_group_date.replace("${store_id}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_group_date, this.getName());
			}

			// 插入user
			if (!StringUtil.isNullOrEmpty(insertSql) && !StringUtil.isNullOrEmpty(userSelectSql)) {
				userSelectSql = userSelectSql.replace("user_info_yyyymmdd", "user_info_" + optime_yesday);
				userSelectSql = userSelectSql.replace("${store_id}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, insertSql + userSelectSql, this.getName());
			}

			// 删除历史数据
			String delete_table = super.getJsonConfigValue(configJsonPath, "delete_table");
			String delete_date = super.getJsonConfigValue(configJsonPath, "delete_date");
			if (!StringUtil.isNullOrEmpty(delete_table) && !StringUtil.isNullOrEmpty(delete_date) && saveDays.isPresent()) {
				String delSQl = super.getDelHistorySql(delete_table, delete_date, "dwd_engine_message_log_dm_", saveDays.get());
				if (!StringUtil.isNullOrEmpty(delSQl)) {
					SqlUtils.sqlExecute(dataSource, delSQl, this.getName());
				}
			} else {
				super.callback(false, "历史数据保留配置异常，saveDays is null;", scriptBean, callback);
			}

			// 删除临时表
			String delete_tmp_sql = super.getJsonConfigValue(configJsonPath, "delete_tmp_sql");
			if (!StringUtil.isNullOrEmpty(delete_tmp_sql)) {
				SqlUtils.sqlExecute(dataSource, delete_tmp_sql, this.getName());
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
		
	}

}
