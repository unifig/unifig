package etl.dispatch.java.st;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
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
import etl.dispatch.util.StringUtil;

/**
 * 活跃用户月留存 统计; st_active_user_retain_ms_yyyymm
 *
 */
@Service
public class St_Active_User_Retain_Ms extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Active_User_Retain_Ms.class);
	private static final String configJsonPath = "classpath*:conf/json/st_active_user_retain_ms.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.active_user_retain_ms";
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
			// 创建单月(MS)目标表
			String target_ms_sql = super.getJsonConfigValue(configJsonPath, "create_ms_yyyymm",false);
			if (!StringUtil.isNullOrEmpty(target_ms_sql)) {
				target_ms_sql = target_ms_sql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + ScriptTimeUtil.optime_lastmonth());
				SqlUtils.sqlExecute(dataSource, target_ms_sql, this.getName());
			}
			// 创建单月活跃用户临时表
			String create_ms_tmpuser_yyyymm = super.getJsonConfigValue(configJsonPath, "create_ms_tmpuser_yyyymm",false);
			if (!StringUtil.isNullOrEmpty(create_ms_tmpuser_yyyymm)) {
				SqlUtils.sqlExecute(dataSource, create_ms_tmpuser_yyyymm, this.getName());
			}
			
			// 支持重跑，删除当天用户清单数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("${statisDate}", ScriptTimeUtil.optime_lastmonth());
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			
			// 删除临时表用户清单
			String delete_yes_tmpDate = super.getJsonConfigValue(configJsonPath, "delete_yes_tmpDate");
			if (!StringUtil.isNullOrEmpty(delete_yes_tmpDate)) {
				delete_yes_tmpDate = delete_yes_tmpDate.replace("${statisDate}", ScriptTimeUtil.optime_lastmonth());
				SqlUtils.sqlExecute(dataSource, delete_yes_tmpDate, this.getName());
			}
			
			// 插入单月活跃用户数据
			String insert_ms_tmpuser_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_tmpuser_yyyymm",false);
			if (!StringUtil.isNullOrEmpty(insert_ms_tmpuser_yyyymm)) {
				insert_ms_tmpuser_yyyymm = insert_ms_tmpuser_yyyymm.replace("${statis_date}", ScriptTimeUtil.optime_lastmonth());
				insert_ms_tmpuser_yyyymm = insert_ms_tmpuser_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
				insert_ms_tmpuser_yyyymm = insert_ms_tmpuser_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
				SqlUtils.sqlExecute(dataSource, insert_ms_tmpuser_yyyymm, this.getName());
			}

			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`", "`app_version_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 获取InsertSQL
			String insertMsSql = super.getJsonConfigValue(configJsonPath, "insertMsSql", false);
			if (!StringUtil.isNullOrEmpty(insertMsSql)) {
				insertMsSql = insertMsSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + ScriptTimeUtil.optime_lastmonth());
			}
			// 获取月留存查询SQL
			String selectMsSql = super.getJsonConfigValue(configJsonPath, "selectMsSql", false);
			if (!StringUtil.isNullOrEmpty(selectMsSql)) {
				selectMsSql = selectMsSql.replace("${statis_date}", ScriptTimeUtil.optime_lastmonth());
				selectMsSql = selectMsSql.replace("${min_id}", ScriptTimeUtil.optime_month_first());
				selectMsSql = selectMsSql.replace("${max_id}", ScriptTimeUtil.optime_month_last());
			}
			// 获取月留存查询聚合SQL
			StringBuffer strWeekMsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strWeekMsBuffer.append(" " + insertMsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strWeekMsBuffer.append(" UNION ");
					}
					strWeekMsBuffer.append(" " + selectMsSql + " ");
					strWeekMsBuffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入月留存查询Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strWeekMsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strWeekMsBuffer.toString(), this.getName());
			}

			// 历史活跃用户留存统计数据，2、3、4、5、6、7、8、9
			Integer[] retainTimes = new Integer[] { 2, 3, 4, 5, 6, 7, 8, 9 };

			// 查询活跃用户时间
			String statisDateSql = "select statis_date from bi_tmp.tmp_st_active_user_retain_ms_all_store group by statis_date";
			List<Map> statisDate = SqlUtils.querySqlList(dataSource, statisDateSql, this.getName());
			Set<String> statisDateUser = new HashSet<String>();
			if (null != statisDate && !statisDate.isEmpty()) {
				for (Map dateMap : statisDate) {
					statisDateUser.add(String.valueOf(dateMap.get("statis_date")));
				}
			}

			// 对比判断是否存在
			Set<String> retainUserDate = new HashSet<String>();
			for (int retainTime : retainTimes) {
				//获取历史月的月份
				String retains_hour_month = DateUtil.getYearMonth(new Date(), -1*retainTime);
				if (!statisDateUser.contains(retains_hour_month)) {
					continue;
				}
				// 删除留存临时表
				String drop_ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
				if (!StringUtil.isNullOrEmpty(drop_ms_tmp_yyyymm)) {
					drop_ms_tmp_yyyymm = drop_ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_month);
					SqlUtils.sqlExecute(dataSource, drop_ms_tmp_yyyymm, this.getName());
				}
				// 创建留存临时表
				String create_ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "create_ms_tmp_yyyymm");
				if (!StringUtil.isNullOrEmpty(create_ms_tmp_yyyymm)) {
					create_ms_tmp_yyyymm = create_ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_month);
					SqlUtils.sqlExecute(dataSource, create_ms_tmp_yyyymm, this.getName());
				}
				// 历史数据转移
				String insert_ms_old_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_old_yyyymm");
				if (!StringUtil.isNullOrEmpty(insert_ms_old_yyyymm)) {
					insert_ms_old_yyyymm = insert_ms_old_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_month);
					insert_ms_old_yyyymm = insert_ms_old_yyyymm.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_month);
					insert_ms_old_yyyymm = insert_ms_old_yyyymm.replace("${statis_date}", retains_hour_month);
					SqlUtils.sqlExecute(dataSource, insert_ms_old_yyyymm, this.getName());
				}
				retainUserDate.add(retainTime + ":" + retains_hour_month);
			}

			// 历史数据聚合运算
			if (!retainUserDate.isEmpty()) {
				for (String retainDate : retainUserDate) {
					String[] retains = retainDate.split(":");
					switch (retains[0]) {
					case "2":
						String retains_hour_2month = retains[1];
						// 获取InsertSQL
						String insert_2ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(insert_2ms_new_yyyymm)) {
							insert_2ms_new_yyyymm = insert_2ms_new_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_2month);
						}
						// 获取月留存查询SQL
						String select_2ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "select_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(select_2ms_new_yyyymm)) {
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${statis_date}", retains_hour_2month);
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${second_months_retained}", "count(DISTINCT `user_id`) AS second_months_retained");
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${third_months_retained}", "0 AS third_months_retained");
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${fourth_months_retained}", "0 AS fourth_months_retained");
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${fifth_months_retained}", "0 AS fifth_months_retained");
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${sixth_months_retained}", "0 AS sixth_months_retained");
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${seventh_months_retained}", "0 AS seventh_months_retained");
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${eighth_months_retained}", "0 AS eighth_months_retained");
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${ninth_months_retained}", "0 AS ninth_months_retained");
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${retains_day}", retains_hour_2month);
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
							select_2ms_new_yyyymm = select_2ms_new_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
						}

						// 获取月留存查询聚合SQL
						StringBuffer str2Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str2Buffer.append(" " + insert_2ms_new_yyyymm + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str2Buffer.append(" UNION ");
								}
								str2Buffer.append(" " + select_2ms_new_yyyymm + " ");
								str2Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入月留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str2Buffer)) {
							SqlUtils.sqlExecute(dataSource, str2Buffer.toString(), this.getName());
						}
						break;
					case "3":
						String retains_hour_3month = retains[1];
						// 获取InsertSQL
						String insert_3ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(insert_3ms_new_yyyymm)) {
							insert_3ms_new_yyyymm = insert_3ms_new_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_3month);
						}
						// 获取月留存查询SQL
						String select_3ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "select_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(select_3ms_new_yyyymm)) {
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${statis_date}", retains_hour_3month);
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${second_months_retained}", "0 AS second_months_retained");
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${third_months_retained}", "count(DISTINCT `user_id`) AS third_months_retained");
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${fourth_months_retained}", "0 AS fourth_months_retained");
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${fifth_months_retained}", "0 AS fifth_months_retained");
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${sixth_months_retained}", "0 AS sixth_months_retained");
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${seventh_months_retained}", "0 AS seventh_months_retained");
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${eighth_months_retained}", "0 AS eighth_months_retained");
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${ninth_months_retained}", "0 AS ninth_months_retained");
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${retains_day}", retains_hour_3month);
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
							select_3ms_new_yyyymm = select_3ms_new_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
						}

						// 获取月留存查询聚合SQL
						StringBuffer str3Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str3Buffer.append(" " + insert_3ms_new_yyyymm + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str3Buffer.append(" UNION ");
								}
								str3Buffer.append(" " + select_3ms_new_yyyymm + " ");
								str3Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入月留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str3Buffer)) {
							SqlUtils.sqlExecute(dataSource, str3Buffer.toString(), this.getName());
						}
						break;
					case "4":
						String retains_hour_4month = retains[1];
						// 获取InsertSQL
						String insert_4ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(insert_4ms_new_yyyymm)) {
							insert_4ms_new_yyyymm = insert_4ms_new_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_4month);
						}
						// 获取月留存查询SQL
						String select_4ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "select_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(select_4ms_new_yyyymm)) {
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${statis_date}", retains_hour_4month);
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${second_months_retained}", "0 AS second_months_retained");
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${third_months_retained}", " 0 AS third_months_retained");
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${fourth_months_retained}", "count(DISTINCT `user_id`) AS fourth_months_retained");
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${fifth_months_retained}", "0 AS fifth_months_retained");
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${sixth_months_retained}", "0 AS sixth_months_retained");
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${seventh_months_retained}", "0 AS seventh_months_retained");
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${eighth_months_retained}", "0 AS eighth_months_retained");
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${ninth_months_retained}", "0 AS ninth_months_retained");
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${retains_day}", retains_hour_4month);
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
							select_4ms_new_yyyymm = select_4ms_new_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
						}

						// 获取月留存查询聚合SQL
						StringBuffer str4Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str4Buffer.append(" " + insert_4ms_new_yyyymm + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str4Buffer.append(" UNION ");
								}
								str4Buffer.append(" " + select_4ms_new_yyyymm + " ");
								str4Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入月留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str4Buffer)) {
							SqlUtils.sqlExecute(dataSource, str4Buffer.toString(), this.getName());
						}
						break;
					case "5":
						String retains_hour_5month = retains[1];
						// 获取InsertSQL
						String insert_5ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(insert_5ms_new_yyyymm)) {
							insert_5ms_new_yyyymm = insert_5ms_new_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_5month);
						}
						// 获取月留存查询SQL
						String select_5ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "select_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(select_5ms_new_yyyymm)) {
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${statis_date}", retains_hour_5month);
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${second_months_retained}", "0 AS second_months_retained");
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${third_months_retained}", " 0 AS third_months_retained");
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${fourth_months_retained}", "0 AS fourth_months_retained");
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${fifth_months_retained}", "count(DISTINCT `user_id`) AS fifth_months_retained");
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${sixth_months_retained}", "0 AS sixth_months_retained");
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${seventh_months_retained}", "0 AS seventh_months_retained");
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${eighth_months_retained}", "0 AS eighth_months_retained");
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${ninth_months_retained}", "0 AS ninth_months_retained");
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${retains_day}", retains_hour_5month);
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
							select_5ms_new_yyyymm = select_5ms_new_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
						}

						// 获取月留存查询聚合SQL
						StringBuffer str5Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str5Buffer.append(" " + insert_5ms_new_yyyymm + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str5Buffer.append(" UNION ");
								}
								str5Buffer.append(" " + select_5ms_new_yyyymm + " ");
								str5Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入月留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str5Buffer)) {
							SqlUtils.sqlExecute(dataSource, str5Buffer.toString(), this.getName());
						}
						break;
					case "6":
						String retains_hour_6month = retains[1];
						// 获取InsertSQL
						String insert_6ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(insert_6ms_new_yyyymm)) {
							insert_6ms_new_yyyymm = insert_6ms_new_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_6month);
						}
						// 获取月留存查询SQL
						String select_6ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "select_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(select_6ms_new_yyyymm)) {
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${statis_date}", retains_hour_6month);
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${second_months_retained}", "0 AS second_months_retained");
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${third_months_retained}", " 0 AS third_months_retained");
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${fourth_months_retained}", "0 AS fourth_months_retained");
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${fifth_months_retained}", "0 AS fifth_months_retained");
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${sixth_months_retained}", "count(DISTINCT `user_id`)  AS sixth_months_retained");
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${seventh_months_retained}", "0 AS seventh_months_retained");
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${eighth_months_retained}", "0 AS eighth_months_retained");
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${ninth_months_retained}", "0 AS ninth_months_retained");
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${retains_day}", retains_hour_6month);
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
							select_6ms_new_yyyymm = select_6ms_new_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
						}

						// 获取月留存查询聚合SQL
						StringBuffer str6Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str6Buffer.append(" " + insert_6ms_new_yyyymm + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str6Buffer.append(" UNION ");
								}
								str6Buffer.append(" " + select_6ms_new_yyyymm + " ");
								str6Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入月留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str6Buffer)) {
							SqlUtils.sqlExecute(dataSource, str6Buffer.toString(), this.getName());
						}
						break;
					case "7":
						String retains_hour_7month = retains[1];
						// 获取InsertSQL
						String insert_7ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(insert_7ms_new_yyyymm)) {
							insert_7ms_new_yyyymm = insert_7ms_new_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_7month);
						}
						// 获取月留存查询SQL
						String select_7ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "select_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(select_7ms_new_yyyymm)) {
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${statis_date}", retains_hour_7month);
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${second_months_retained}", "0 AS second_months_retained");
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${third_months_retained}", " 0 AS third_months_retained");
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${fourth_months_retained}", "0 AS fourth_months_retained");
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${fifth_months_retained}", "0 AS fifth_months_retained");
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${sixth_months_retained}", "0 AS sixth_months_retained");
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${seventh_months_retained}", "count(DISTINCT `user_id`)  AS seventh_months_retained");
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${eighth_months_retained}", "0 AS eighth_months_retained");
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${ninth_months_retained}", "0 AS ninth_months_retained");
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${retains_day}", retains_hour_7month);
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
							select_7ms_new_yyyymm = select_7ms_new_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
						}
						// 获取月留存查询聚合SQL
						StringBuffer str7Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str7Buffer.append(" " + insert_7ms_new_yyyymm + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str7Buffer.append(" UNION ");
								}
								str7Buffer.append(" " + select_7ms_new_yyyymm + " ");
								str7Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入月留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str7Buffer)) {
							SqlUtils.sqlExecute(dataSource, str7Buffer.toString(), this.getName());
						}
						break;
					case "8":
						String retains_hour_8month = retains[1];
						// 获取InsertSQL
						String insert_8ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(insert_8ms_new_yyyymm)) {
							insert_8ms_new_yyyymm = insert_8ms_new_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_8month);
						}
						// 获取月留存查询SQL
						String select_8ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "select_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(select_8ms_new_yyyymm)) {
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${statis_date}", retains_hour_8month);
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${second_months_retained}", "0 AS second_months_retained");
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${third_months_retained}", " 0 AS third_months_retained");
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${fourth_months_retained}", "0 AS fourth_months_retained");
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${fifth_months_retained}", "0 AS fifth_months_retained");
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${sixth_months_retained}", "0 AS sixth_months_retained");
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${seventh_months_retained}", "0 AS seventh_months_retained");
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${eighth_months_retained}", "count(DISTINCT `user_id`)   AS eighth_months_retained");
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${ninth_months_retained}", "0 AS ninth_months_retained");
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${retains_day}", retains_hour_8month);
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
							select_8ms_new_yyyymm = select_8ms_new_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
						}

						// 获取月留存查询聚合SQL
						StringBuffer str8Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str8Buffer.append(" " + insert_8ms_new_yyyymm + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str8Buffer.append(" UNION ");
								}
								str8Buffer.append(" " + select_8ms_new_yyyymm + " ");
								str8Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入月留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str8Buffer)) {
							SqlUtils.sqlExecute(dataSource, str8Buffer.toString(), this.getName());
						}
						break;
					case "9":
						String retains_hour_9month = retains[1];
						// 获取InsertSQL
						String insert_9ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "insert_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(insert_9ms_new_yyyymm)) {
							insert_9ms_new_yyyymm = insert_9ms_new_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_9month);
						}
						// 获取月留存查询SQL
						String select_9ms_new_yyyymm = super.getJsonConfigValue(configJsonPath, "select_ms_new_yyyymm", false);
						if (!StringUtil.isNullOrEmpty(select_9ms_new_yyyymm)) {
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${statis_date}", retains_hour_9month);
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${second_months_retained}", "0 AS second_months_retained");
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${third_months_retained}", " 0 AS third_months_retained");
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${fourth_months_retained}", "0 AS fourth_months_retained");
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${fifth_months_retained}", "0 AS fifth_months_retained");
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${sixth_months_retained}", "0 AS sixth_months_retained");
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${seventh_months_retained}", "0 AS seventh_months_retained");
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${eighth_months_retained}", "0 AS eighth_months_retained");
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${ninth_months_retained}", "count(DISTINCT `user_id`) AS ninth_months_retained");
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${retains_day}", retains_hour_9month);
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${min_id}", ScriptTimeUtil.optime_month_first());
							select_9ms_new_yyyymm = select_9ms_new_yyyymm.replace("${max_id}", ScriptTimeUtil.optime_month_last());
						}

						// 获取月留存查询聚合SQL
						StringBuffer str9Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str9Buffer.append(" " + insert_9ms_new_yyyymm + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str9Buffer.append(" UNION ");
								}
								str9Buffer.append(" " + select_9ms_new_yyyymm + " ");
								str9Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入月留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str9Buffer)) {
							SqlUtils.sqlExecute(dataSource, str9Buffer.toString(), this.getName());
						}
						break;
					default:
						break;
					}
				}
			}
			// 重新group聚合数据
			if (!retainUserDate.isEmpty()) {
				for (String retainDate : retainUserDate) {
					String[] retains = retainDate.split(":");
					switch (retains[0]) {
					case "2":
						String retains_hour_2month = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_active_user_retain_ms_" + retains_hour_2month, this.getName());
						// 插入新聚合数据
						String insert2MsSelectSql = super.getJsonConfigValue(configJsonPath, "insertMsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert2MsSelectSql)) {
							insert2MsSelectSql = insert2MsSelectSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_2month);
							insert2MsSelectSql = insert2MsSelectSql.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_2month);
							SqlUtils.sqlExecute(dataSource, insert2MsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_2ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
						if (!StringUtil.isNullOrEmpty(drop_2ms_tmp_yyyymm)) {
							drop_2ms_tmp_yyyymm = drop_2ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_2month);
							SqlUtils.sqlExecute(dataSource, drop_2ms_tmp_yyyymm, this.getName());
						}
						break;
					case "3":
						String retains_hour_3month = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_active_user_retain_ms_" + retains_hour_3month, this.getName());
						// 插入新聚合数据
						String insert3MsSelectSql = super.getJsonConfigValue(configJsonPath, "insertMsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert3MsSelectSql)) {
							insert3MsSelectSql = insert3MsSelectSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_3month);
							insert3MsSelectSql = insert3MsSelectSql.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_3month);
							SqlUtils.sqlExecute(dataSource, insert3MsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_3ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
						if (!StringUtil.isNullOrEmpty(drop_3ms_tmp_yyyymm)) {
							drop_3ms_tmp_yyyymm = drop_3ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_3month);
							SqlUtils.sqlExecute(dataSource, drop_3ms_tmp_yyyymm, this.getName());
						}
						break;
					case "4":
						String retains_hour_4month = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_active_user_retain_ms_" + retains_hour_4month, this.getName());
						// 插入新聚合数据
						String insert4MsSelectSql = super.getJsonConfigValue(configJsonPath, "insertMsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert4MsSelectSql)) {
							insert4MsSelectSql = insert4MsSelectSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_4month);
							insert4MsSelectSql = insert4MsSelectSql.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_4month);
							SqlUtils.sqlExecute(dataSource, insert4MsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_4ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
						if (!StringUtil.isNullOrEmpty(drop_4ms_tmp_yyyymm)) {
							drop_4ms_tmp_yyyymm = drop_4ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_4month);
							SqlUtils.sqlExecute(dataSource, drop_4ms_tmp_yyyymm, this.getName());
						}
						break;
					case "5":
						String retains_hour_5month = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_active_user_retain_ms_" + retains_hour_5month, this.getName());
						// 插入新聚合数据
						String insert5MsSelectSql = super.getJsonConfigValue(configJsonPath, "insertMsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert5MsSelectSql)) {
							insert5MsSelectSql = insert5MsSelectSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_5month);
							insert5MsSelectSql = insert5MsSelectSql.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_5month);
							SqlUtils.sqlExecute(dataSource, insert5MsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_5ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
						if (!StringUtil.isNullOrEmpty(drop_5ms_tmp_yyyymm)) {
							drop_5ms_tmp_yyyymm = drop_5ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_5month);
							SqlUtils.sqlExecute(dataSource, drop_5ms_tmp_yyyymm, this.getName());
						}
						break;
					case "6":
						String retains_hour_6month = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_active_user_retain_ms_" + retains_hour_6month, this.getName());
						// 插入新聚合数据
						String insert6MsSelectSql = super.getJsonConfigValue(configJsonPath, "insertMsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert6MsSelectSql)) {
							insert6MsSelectSql = insert6MsSelectSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_6month);
							insert6MsSelectSql = insert6MsSelectSql.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_6month);
							SqlUtils.sqlExecute(dataSource, insert6MsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_6ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
						if (!StringUtil.isNullOrEmpty(drop_6ms_tmp_yyyymm)) {
							drop_6ms_tmp_yyyymm = drop_6ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_6month);
							SqlUtils.sqlExecute(dataSource, drop_6ms_tmp_yyyymm, this.getName());
						}
						break;
					case "7":
						String retains_hour_7month = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_active_user_retain_ms_" + retains_hour_7month, this.getName());
						// 插入新聚合数据
						String insert7MsSelectSql = super.getJsonConfigValue(configJsonPath, "insertMsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert7MsSelectSql)) {
							insert7MsSelectSql = insert7MsSelectSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_7month);
							insert7MsSelectSql = insert7MsSelectSql.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_7month);
							SqlUtils.sqlExecute(dataSource, insert7MsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_7ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
						if (!StringUtil.isNullOrEmpty(drop_7ms_tmp_yyyymm)) {
							drop_7ms_tmp_yyyymm = drop_7ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_7month);
							SqlUtils.sqlExecute(dataSource, drop_7ms_tmp_yyyymm, this.getName());
						}
						break;
					case "8":
						String retains_hour_8month = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_active_user_retain_ms_" + retains_hour_8month, this.getName());
						// 插入新聚合数据
						String insert8MsSelectSql = super.getJsonConfigValue(configJsonPath, "insertMsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert8MsSelectSql)) {
							insert8MsSelectSql = insert8MsSelectSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_8month);
							insert8MsSelectSql = insert8MsSelectSql.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_8month);
							SqlUtils.sqlExecute(dataSource, insert8MsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_8ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
						if (!StringUtil.isNullOrEmpty(drop_8ms_tmp_yyyymm)) {
							drop_8ms_tmp_yyyymm = drop_8ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_8month);
							SqlUtils.sqlExecute(dataSource, drop_8ms_tmp_yyyymm, this.getName());
						}
						break;
					case "9":
						String retains_hour_9month = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_active_user_retain_ms_" + retains_hour_9month, this.getName());
						// 插入新聚合数据
						String insert9MsSelectSql = super.getJsonConfigValue(configJsonPath, "insertMsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert9MsSelectSql)) {
							insert9MsSelectSql = insert9MsSelectSql.replace("st_active_user_retain_ms_yyyymm", "st_active_user_retain_ms_" + retains_hour_9month);
							insert9MsSelectSql = insert9MsSelectSql.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_9month);
							SqlUtils.sqlExecute(dataSource, insert9MsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_9ms_tmp_yyyymm = super.getJsonConfigValue(configJsonPath, "drop_ms_tmp_yyyymm");
						if (!StringUtil.isNullOrEmpty(drop_9ms_tmp_yyyymm)) {
							drop_9ms_tmp_yyyymm = drop_9ms_tmp_yyyymm.replace("tmp_st_active_user_retain_ms_yyyymm", "tmp_st_active_user_retain_ms_" + retains_hour_9month);
							SqlUtils.sqlExecute(dataSource, drop_9ms_tmp_yyyymm, this.getName());
						}
						break;
					default:
						break;
					}
				}
			}
			
			// 脚本结束回调状态
			super.callback(true, null, scriptBean, callback);
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ",message: " + ex.getMessage(), scriptBean, callback);
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}
}
