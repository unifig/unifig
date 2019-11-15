package etl.dispatch.java.st;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.StringUtil;

/**
 * 应用自定义事件分析（按日）
 * 
 *
 *
 */
@Service
public class St_ApplicationEventReport extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_ApplicationEventReport.class);
	private static final String configJsonPath = "classpath*:conf/json/st_applicationEventReport.json";
	private static final String select_source_storeId = "${store_id}";
	private static final String source_dwd_yyyymmdd = "dw_event_report_dm_yyyymm";
	private static final String target_dwd_yyyymmdd = "st_event_report_dm_yyyymm";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.applicationEventReport";
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
		}
		try {
			// 获取昨天所在天
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			// 获取昨天所在月
			String optime_month = optime_yesday.substring(0, optime_yesday.length() - 2);
			
			// 获取前天所在天
			String optime_befyesday =  DateUtil.getSysStrCurrentDate("yyyyMMdd", -2);
			// 获取前天所在月
			String optime_befmonth = optime_befyesday.substring(0, optime_befyesday.length() - 2);

			// 第1步 Cube聚合组合条件
			String[] criteriaArr = new String[] { "`hour`", "`app_version_id`", "`channel_id`", "`app_plat_id`", "`event_key_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 第1.1 步 创建单月数据统计
			String target_sql_m = super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if (!StringUtil.isNullOrEmpty(target_sql_m)) {
				target_sql_m = target_sql_m.replace(target_dwd_yyyymmdd, "st_event_report_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_sql_m, this.getName());
			}

			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace(target_dwd_yyyymmdd, "st_event_report_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}

			/**
			 * 第2步 <<<<单日数据统计>>>>
			 */
			// 删除临时表
			String drop_sql_tmp = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_sql_tmp)) {
				drop_sql_tmp = drop_sql_tmp.replace("tmp_st_event_reportds_yyyymmdd", "tmp_st_event_reportds_" + optime_yesday);
				SqlUtils.sqlExecute(dataSource, drop_sql_tmp, this.getName());
			}
			// 创建临时表
			String create_sql_tmp = super.getJsonConfigValue(configJsonPath, "create_ds_tm_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(create_sql_tmp)) {
				create_sql_tmp = create_sql_tmp.replace("tmp_st_event_reportds_yyyymmdd", "tmp_st_event_reportds_" + optime_yesday);
				SqlUtils.sqlExecute(dataSource, create_sql_tmp, this.getName());
			}
			// 获取(DS)InsertSQL
			String insert_tmp_Sql = super.getJsonConfigValue(configJsonPath, "insertTmpSql");
			if (!StringUtil.isNullOrEmpty(insert_tmp_Sql)) {
				insert_tmp_Sql = insert_tmp_Sql.replace("tmp_st_event_reportds_yyyymmdd", "tmp_st_event_reportds_" + optime_yesday);
			}
			// 获取(昨天)SelectSQL
			String select_theyes_ds_Sql = super.getJsonConfigValue(configJsonPath, "selectDsTheYesSql");
			if (!StringUtil.isNullOrEmpty(select_theyes_ds_Sql)) {
				select_theyes_ds_Sql = select_theyes_ds_Sql.replace(source_dwd_yyyymmdd, "dw_event_report_dm_" + optime_month);
				select_theyes_ds_Sql = select_theyes_ds_Sql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取(昨天)Cube聚合SQL
			StringBuffer strTheYesDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strTheYesDsBuffer.append(" " + insert_tmp_Sql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strTheYesDsBuffer.append(" UNION ");
					}
					strTheYesDsBuffer.append(" " + select_theyes_ds_Sql + " ");
					strTheYesDsBuffer.append(" group by dw.`statis_date` , " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(昨天)Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strTheYesDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strTheYesDsBuffer.toString(), this.getName());
			}

			// 获取(前天)SelectSQL
			String select_befyes_ds_Sql = super.getJsonConfigValue(configJsonPath, "selectDsBefYesSql");
			if (!StringUtil.isNullOrEmpty(select_befyes_ds_Sql)) {
				select_befyes_ds_Sql = select_befyes_ds_Sql.replace(source_dwd_yyyymmdd, "dw_event_report_dm_" + optime_befmonth);
				select_befyes_ds_Sql = select_befyes_ds_Sql.replace(select_source_storeId, optime_befyesday);
			}
			// 获取(前天)Cube聚合SQL
			StringBuffer strBefYesDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strBefYesDsBuffer.append(" " + insert_tmp_Sql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strBefYesDsBuffer.append(" UNION ");
					}
					strBefYesDsBuffer.append(" " + select_befyes_ds_Sql + " ");
					strBefYesDsBuffer.append(" group by dw.`statis_date` , " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(前天)Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strBefYesDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strBefYesDsBuffer.toString(), this.getName());
			}

			/**
			 * 第3步<<<<数据入DM表>>>>
			 */
			// 3.3获取(DM)InsertSQL
			String insert_select_DmSql = super.getJsonConfigValue(configJsonPath, "insertSelectDmSql");
			if (!StringUtil.isNullOrEmpty(insert_select_DmSql)) {
				insert_select_DmSql = insert_select_DmSql.replace(target_dwd_yyyymmdd, "st_event_report_dm_" + optime_month);
				insert_select_DmSql = insert_select_DmSql.replace("tmp_st_event_reportds_yyyymmdd", "tmp_st_event_reportds_" + optime_yesday);
				insert_select_DmSql = insert_select_DmSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insert_select_DmSql, this.getName());
			}
			// 删除临时表
			drop_sql_tmp = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_sql_tmp)) {
				drop_sql_tmp = drop_sql_tmp.replace("tmp_st_event_reportds_yyyymmdd", "tmp_st_event_reportds_" + optime_yesday);
				SqlUtils.sqlExecute(dataSource, drop_sql_tmp, this.getName());
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
