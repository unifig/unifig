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
 * 应用交互追踪ST业务层数据汇总（按日）
 * 
 *
 *
 */
@Service
public class St_ApplicationInteractTrace extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_ApplicationInteractTrace.class);
	private static final String configJsonPath = "classpath*:conf/json/st_applicationInteractTrace.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.applicationInteractTrace";
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
			String optime_month = optime_yesday.substring(0, optime_yesday.length() - 2);
			
			// 第1步 Cube聚合组合条件
			String[] criteriaArr = new String[] { "dw.`hour`", "dw.`app_version_id`", "dw.`channel_id`", "dw.`app_plat_id`", "dw.`interaction_view_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);
			
			// 第1.1 步 创建单月数据统计
			String target_sql_m =super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if (!StringUtil.isNullOrEmpty(target_sql_m)) {
				target_sql_m = target_sql_m.replace("st_event_analysis_dm_yyyymm", "st_event_analysis_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_sql_m, this.getName());
			}
			
			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("st_event_analysis_dm_yyyymm", "st_event_analysis_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			
			/**
			 * 第2步 <<<<单日数据统计>>>>
			 */
			// 第2.2获取(DS)InsertSQL
			String insert_ds_Sql = super.getJsonConfigValue(configJsonPath, "insertDmSql");
			if (!StringUtil.isNullOrEmpty(insert_ds_Sql)) {
				insert_ds_Sql = insert_ds_Sql.replace("st_event_analysis_dm_yyyymm", "st_event_analysis_dm_" + optime_month);
			}
			// 第2.3获取(DS)SelectSQL
			String select_ds_Sql = super.getJsonConfigValue(configJsonPath, "selectDsSql");
			if (!StringUtil.isNullOrEmpty(select_ds_Sql)) {
				select_ds_Sql = select_ds_Sql.replace("dw_interact_trace_dm_yyyymm", "dw_interact_trace_dm_" + optime_month);
				select_ds_Sql = select_ds_Sql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 第2.4 获取(DS)Cube聚合SQL
			StringBuffer strDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strDsBuffer.append(" " + insert_ds_Sql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strDsBuffer.append(" UNION ");
					}
					strDsBuffer.append(" " + select_ds_Sql + " ");
					strDsBuffer.append(" group by dw.`statis_date` , " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 第2.5插入(DM)Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strDsBuffer.toString(), this.getName());
			}
			
			/**
			 * 第3步<<<<日累计数据统计>>>>
			 */
			// 第3.1 判断分区表是否存在
			String dt_times_slice = super.getJsonConfigValue(configJsonPath, "insertDtTimeSlice");
			Set<Integer> sliceSet = null;
			if (!StringUtil.isNullOrEmpty(dt_times_slice)) {
				sliceSet = new HashSet<Integer>();
				for (String timeSlice : dt_times_slice.split(",")) {
					sliceSet.add(NumberUtils.intValue(timeSlice));
				}
			}

			// 3.3获取(DT)InsertSQL
			String insert_dt_Sql = super.getJsonConfigValue(configJsonPath, "insertDtSql");
			if (!StringUtil.isNullOrEmpty(insert_dt_Sql)) {
				insert_dt_Sql = insert_dt_Sql.replace("st_event_analysis_dm_yyyymm", "st_event_analysis_dm_" + optime_month);
			}

			// 3.4 获取(DT)Cube聚合SQL
			StringBuffer strDtBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strDtBuffer.append(" " + insert_dt_Sql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strDtBuffer.append(" UNION ");
					}
					String select_dt_Sql = super.getJsonConfigValue(configJsonPath, "selectDtSql");
					strDtBuffer.append(" " + select_dt_Sql + " ");
					strDtBuffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			
			// selectFrom
			String selectFrom = super.getJsonConfigValue(configJsonPath, "selectFrom", false);
			
			// 3.5 插入(DT)Cube聚合数据
			if (null != sliceSet && !sliceSet.isEmpty() && !StringUtil.isNullOrEmpty(selectFrom)) {
				for (Integer dtTimeSlice : sliceSet) {
					if (!StringUtil.isNullOrEmpty(strDtBuffer)) {
						String dt_sql = strDtBuffer.toString();
						if (!StringUtil.isNullOrEmpty(dt_sql)) {
							String end = DateUtil.getSysStrCurrentDate("yyyyMMdd", -1);
							String start = DateUtil.getSysStrCurrentDate("yyyyMMdd", -1 * dtTimeSlice);
							String selectFromSql = super.getMonthSql(start, end, selectFrom);
							dt_sql = dt_sql.replace("${selectFrom}", selectFromSql);
							dt_sql = dt_sql.replace("${time_slice}", String.valueOf(dtTimeSlice));
							dt_sql = dt_sql.replace("${statis_date}", ScriptTimeUtil.optime_yesday());
							dt_sql = dt_sql.replace("${min_id}", start);
							dt_sql = dt_sql.replace("${max_id}", end);
						}
						SqlUtils.sqlExecute(dataSource, dt_sql, this.getName());
					}
				}
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
