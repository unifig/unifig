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
import etl.dispatch.util.StringUtil;

/**
 * 活跃用户日留存 统计；st_active_user_retain_ds_yyyymmdd
 * 
 *
 */
@Service
public class St_Active_User_Retain_Ds extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Active_User_Retain_Ds.class);
	private static final String configJsonPath = "classpath*:conf/json/st_active_user_retain_ds.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.active_user_retain_ds";
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

			// 创建(DM)表
			String target_dm_sql = super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if (!StringUtil.isNullOrEmpty(target_dm_sql)) {
				target_dm_sql = target_dm_sql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_dm_sql, this.getName());
			}

			// 创建单日活跃用户临时表
			String create_ds_tmpuser_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_ds_tmpuser_yyyymmdd", false);
			if (!StringUtil.isNullOrEmpty(create_ds_tmpuser_yyyymmdd)) {
				SqlUtils.sqlExecute(dataSource, create_ds_tmpuser_yyyymmdd, this.getName());
			}

			// 删除 ST层汇总数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}

			// 删除临时表活跃用户清单
			String delete_yes_tmpDate = super.getJsonConfigValue(configJsonPath, "delete_yes_tmpDate");
			if (!StringUtil.isNullOrEmpty(delete_yes_tmpDate)) {
				delete_yes_tmpDate = delete_yes_tmpDate.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_tmpDate, this.getName());
			}

			// 插入单日活跃用户清单
			String insert_ds_tmpuser_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_tmpuser_yyyymmdd", false);
			if (!StringUtil.isNullOrEmpty(insert_ds_tmpuser_yyyymmdd)) {
				insert_ds_tmpuser_yyyymmdd = insert_ds_tmpuser_yyyymmdd.replace("${store_id}", ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insert_ds_tmpuser_yyyymmdd, this.getName());
			}

			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`", "`app_version_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 获取InsertSQL
			String insertDsSql = super.getJsonConfigValue(configJsonPath, "insertDmSql", false);
			if (!StringUtil.isNullOrEmpty(insertDsSql)) {
				insertDsSql = insertDsSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + optime_month);
			}
			// 获取日留存查询SQL
			String selectDsSql = super.getJsonConfigValue(configJsonPath, "selectDsSql", false);
			if (!StringUtil.isNullOrEmpty(selectDsSql)) {
				selectDsSql = selectDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取日留存查询聚合SQL
			StringBuffer strDayDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strDayDsBuffer.append(" " + insertDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strDayDsBuffer.append(" UNION ");
					}
					strDayDsBuffer.append(" " + selectDsSql + " ");
					strDayDsBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入日留存查询Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strDayDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strDayDsBuffer.toString(), this.getName());
			}

			// 历史活跃用户留存统计数据，2、3、4、5、6、7、15、30
			Integer[] retainTimes = new Integer[] { 2, 3, 4, 5, 6, 7, 15, 30 };

			// 查询活跃用户时间
			String statisDateSql = "select statis_date from bi_tmp.tmp_st_active_user_retain_ds_all_store group by statis_date";
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
				String retains_hour_day = DateUtil.getSysStrCurrentDate("yyyyMMdd", -1 * retainTime);
				String retains_hour_month = retains_hour_day.substring(0, optime_yesday.length() - 2);
				if (!statisDateUser.contains(retains_hour_day)) {
					continue;
				}
				// 删除留存临时表
				String drop_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
				if (!StringUtil.isNullOrEmpty(drop_ds_tmp_yyyymmdd)) {
					drop_ds_tmp_yyyymmdd = drop_ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_day);
					SqlUtils.sqlExecute(dataSource, drop_ds_tmp_yyyymmdd, this.getName());
				}
				// 创建留存临时表
				String create_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_ds_tmp_yyyymmdd");
				if (!StringUtil.isNullOrEmpty(create_ds_tmp_yyyymmdd)) {
					create_ds_tmp_yyyymmdd = create_ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_day);
					SqlUtils.sqlExecute(dataSource, create_ds_tmp_yyyymmdd, this.getName());
				}
				// 历史数据转移
				String insert_ds_old_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_old_yyyymmdd");
				if (!StringUtil.isNullOrEmpty(insert_ds_old_yyyymmdd)) {
					insert_ds_old_yyyymmdd = insert_ds_old_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_day);
					insert_ds_old_yyyymmdd = insert_ds_old_yyyymmdd.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_month);
					insert_ds_old_yyyymmdd = insert_ds_old_yyyymmdd.replace("${statis_date}", retains_hour_day);
					SqlUtils.sqlExecute(dataSource, insert_ds_old_yyyymmdd, this.getName());
				}
				retainUserDate.add(retainTime + ":" + retains_hour_day);
			}

			// 历史数据聚合运算
			if (!retainUserDate.isEmpty()) {
				for (String retainDate : retainUserDate) {
					String[] retains = retainDate.split(":");
					switch (retains[0]) {
					case "2":
						String retains_hour_2day = retains[1];
						// 获取InsertSQL`
						String insert_2ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_2ds_new_yyyymmdd)) {
							insert_2ds_new_yyyymmdd = insert_2ds_new_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_2day);
						}
						// 获取日留存查询SQL
						String select_2ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_2ds_new_yyyymmdd)) {
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${statis_date}", retains_hour_2day);
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${second_days_retained}", "count(DISTINCT `user_id`) AS second_days_retained");
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${third_days_retained}", "0 AS third_days_retained");
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${fourth_days_retained}", "0 AS fourth_days_retained");
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${fifth_days_retained}", "0 AS fifth_days_retained");
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${sixth_days_retained}", "0 AS sixth_days_retained");
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${seventh_days_retained}", "0 AS seventh_days_retained");
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${fifteenth_days_retained}", "0 AS fifteenth_days_retained");
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${thirtieth_days_retained}", "0 AS thirtieth_days_retained");
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${retains_day}", retains_hour_2day);
							select_2ds_new_yyyymmdd = select_2ds_new_yyyymmdd.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
						}

						// 获取日留存查询聚合SQL
						StringBuffer str2Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str2Buffer.append(" " + insert_2ds_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str2Buffer.append(" UNION ");
								}
								str2Buffer.append(" " + select_2ds_new_yyyymmdd + " ");
								str2Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入日留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str2Buffer)) {
							SqlUtils.sqlExecute(dataSource, str2Buffer.toString(), this.getName());
						}
						break;
					case "3":
						String retains_hour_3day = retains[1];
						// 获取InsertSQL
						String insert_3ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_3ds_new_yyyymmdd)) {
							insert_3ds_new_yyyymmdd = insert_3ds_new_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_3day);
						}
						// 获取日留存查询SQL
						String select_3ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_3ds_new_yyyymmdd)) {
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${statis_date}", retains_hour_3day);
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${second_days_retained}", "0 AS second_days_retained");
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${third_days_retained}", "count(DISTINCT `user_id`) AS third_days_retained");
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${fourth_days_retained}", "0 AS fourth_days_retained");
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${fifth_days_retained}", "0 AS fifth_days_retained");
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${sixth_days_retained}", "0 AS sixth_days_retained");
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${seventh_days_retained}", "0 AS seventh_days_retained");
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${fifteenth_days_retained}", "0 AS fifteenth_days_retained");
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${thirtieth_days_retained}", "0 AS thirtieth_days_retained");
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${retains_day}", retains_hour_3day);
							select_3ds_new_yyyymmdd = select_3ds_new_yyyymmdd.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
						}

						// 获取日留存查询聚合SQL
						StringBuffer str3Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str3Buffer.append(" " + insert_3ds_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str3Buffer.append(" UNION ");
								}
								str3Buffer.append(" " + select_3ds_new_yyyymmdd + " ");
								str3Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入日留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str3Buffer)) {
							SqlUtils.sqlExecute(dataSource, str3Buffer.toString(), this.getName());
						}
						break;
					case "4":
						String retains_hour_4day = retains[1];
						// 获取InsertSQL
						String insert_4ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_4ds_new_yyyymmdd)) {
							insert_4ds_new_yyyymmdd = insert_4ds_new_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_4day);
						}
						// 获取日留存查询SQL
						String select_4ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_4ds_new_yyyymmdd)) {
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${statis_date}", retains_hour_4day);
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${second_days_retained}", "0 AS second_days_retained");
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${third_days_retained}", " 0 AS third_days_retained");
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${fourth_days_retained}", "count(DISTINCT `user_id`) AS fourth_days_retained");
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${fifth_days_retained}", "0 AS fifth_days_retained");
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${sixth_days_retained}", "0 AS sixth_days_retained");
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${seventh_days_retained}", "0 AS seventh_days_retained");
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${fifteenth_days_retained}", "0 AS fifteenth_days_retained");
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${thirtieth_days_retained}", "0 AS thirtieth_days_retained");
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${retains_day}", retains_hour_4day);
							select_4ds_new_yyyymmdd = select_4ds_new_yyyymmdd.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
						}

						// 获取日留存查询聚合SQL
						StringBuffer str4Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str4Buffer.append(" " + insert_4ds_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str4Buffer.append(" UNION ");
								}
								str4Buffer.append(" " + select_4ds_new_yyyymmdd + " ");
								str4Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入日留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str4Buffer)) {
							SqlUtils.sqlExecute(dataSource, str4Buffer.toString(), this.getName());
						}
						break;
					case "5":
						String retains_hour_5day = retains[1];
						// 获取InsertSQL
						String insert_5ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_5ds_new_yyyymmdd)) {
							insert_5ds_new_yyyymmdd = insert_5ds_new_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_5day);
						}
						// 获取日留存查询SQL
						String select_5ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_5ds_new_yyyymmdd)) {
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${statis_date}", retains_hour_5day);
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${second_days_retained}", "0 AS second_days_retained");
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${third_days_retained}", " 0 AS third_days_retained");
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${fourth_days_retained}", "0 AS fourth_days_retained");
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${fifth_days_retained}", "count(DISTINCT `user_id`) AS fifth_days_retained");
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${sixth_days_retained}", "0 AS sixth_days_retained");
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${seventh_days_retained}", "0 AS seventh_days_retained");
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${fifteenth_days_retained}", "0 AS fifteenth_days_retained");
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${thirtieth_days_retained}", "0 AS thirtieth_days_retained");
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${retains_day}", retains_hour_5day);
							select_5ds_new_yyyymmdd = select_5ds_new_yyyymmdd.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
						}

						// 获取日留存查询聚合SQL
						StringBuffer str5Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str5Buffer.append(" " + insert_5ds_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str5Buffer.append(" UNION ");
								}
								str5Buffer.append(" " + select_5ds_new_yyyymmdd + " ");
								str5Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入日留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str5Buffer)) {
							SqlUtils.sqlExecute(dataSource, str5Buffer.toString(), this.getName());
						}
						break;
					case "6":
						String retains_hour_6day = retains[1];
						// 获取InsertSQL
						String insert_6ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_6ds_new_yyyymmdd)) {
							insert_6ds_new_yyyymmdd = insert_6ds_new_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_6day);
						}
						// 获取日留存查询SQL
						String select_6ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_6ds_new_yyyymmdd)) {
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${statis_date}", retains_hour_6day);
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${second_days_retained}", "0 AS second_days_retained");
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${third_days_retained}", " 0 AS third_days_retained");
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${fourth_days_retained}", "0 AS fourth_days_retained");
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${fifth_days_retained}", "0 AS fifth_days_retained");
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${sixth_days_retained}", "count(DISTINCT `user_id`)  AS sixth_days_retained");
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${seventh_days_retained}", "0 AS seventh_days_retained");
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${fifteenth_days_retained}", "0 AS fifteenth_days_retained");
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${thirtieth_days_retained}", "0 AS thirtieth_days_retained");
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${retains_day}", retains_hour_6day);
							select_6ds_new_yyyymmdd = select_6ds_new_yyyymmdd.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
						}

						// 获取日留存查询聚合SQL
						StringBuffer str6Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str6Buffer.append(" " + insert_6ds_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str6Buffer.append(" UNION ");
								}
								str6Buffer.append(" " + select_6ds_new_yyyymmdd + " ");
								str6Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入日留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str6Buffer)) {
							SqlUtils.sqlExecute(dataSource, str6Buffer.toString(), this.getName());
						}
						break;
					case "7":
						String retains_hour_7day = retains[1];
						// 获取InsertSQL
						String insert_7ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_7ds_new_yyyymmdd)) {
							insert_7ds_new_yyyymmdd = insert_7ds_new_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_7day);
						}
						// 获取日留存查询SQL
						String select_7ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_7ds_new_yyyymmdd)) {
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${statis_date}", retains_hour_7day);
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${second_days_retained}", "0 AS second_days_retained");
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${third_days_retained}", " 0 AS third_days_retained");
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${fourth_days_retained}", "0 AS fourth_days_retained");
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${fifth_days_retained}", "0 AS fifth_days_retained");
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${sixth_days_retained}", "0 AS sixth_days_retained");
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${seventh_days_retained}", "count(DISTINCT `user_id`)  AS seventh_days_retained");
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${fifteenth_days_retained}", "0 AS fifteenth_days_retained");
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${thirtieth_days_retained}", "0 AS thirtieth_days_retained");
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${retains_day}", retains_hour_7day);
							select_7ds_new_yyyymmdd = select_7ds_new_yyyymmdd.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
						}
						// 获取日留存查询聚合SQL
						StringBuffer str7Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str7Buffer.append(" " + insert_7ds_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str7Buffer.append(" UNION ");
								}
								str7Buffer.append(" " + select_7ds_new_yyyymmdd + " ");
								str7Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入日留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str7Buffer)) {
							SqlUtils.sqlExecute(dataSource, str7Buffer.toString(), this.getName());
						}
						break;
					case "15":
						String retains_hour_15day = retains[1];
						// 获取InsertSQL
						String insert_15ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_15ds_new_yyyymmdd)) {
							insert_15ds_new_yyyymmdd = insert_15ds_new_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_15day);
						}
						// 获取日留存查询SQL
						String select_15ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_15ds_new_yyyymmdd)) {
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${statis_date}", retains_hour_15day);
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${second_days_retained}", "0 AS second_days_retained");
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${third_days_retained}", " 0 AS third_days_retained");
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${fourth_days_retained}", "0 AS fourth_days_retained");
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${fifth_days_retained}", "0 AS fifth_days_retained");
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${sixth_days_retained}", "0 AS sixth_days_retained");
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${seventh_days_retained}", "0 AS seventh_days_retained");
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${fifteenth_days_retained}", "count(DISTINCT `user_id`)   AS fifteenth_days_retained");
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${thirtieth_days_retained}", "0 AS thirtieth_days_retained");
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${retains_day}", retains_hour_15day);
							select_15ds_new_yyyymmdd = select_15ds_new_yyyymmdd.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
						}

						// 获取日留存查询聚合SQL
						StringBuffer str15Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str15Buffer.append(" " + insert_15ds_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str15Buffer.append(" UNION ");
								}
								str15Buffer.append(" " + select_15ds_new_yyyymmdd + " ");
								str15Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入日留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str15Buffer)) {
							SqlUtils.sqlExecute(dataSource, str15Buffer.toString(), this.getName());
						}
						break;
					case "30":
						String retains_hour_30day = retains[1];
						// 获取InsertSQL
						String insert_30ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_30ds_new_yyyymmdd)) {
							insert_30ds_new_yyyymmdd = insert_30ds_new_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_30day);
						}
						// 获取日留存查询SQL
						String select_30ds_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ds_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_30ds_new_yyyymmdd)) {
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${statis_date}", retains_hour_30day);
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${second_days_retained}", "0 AS second_days_retained");
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${third_days_retained}", " 0 AS third_days_retained");
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${fourth_days_retained}", "0 AS fourth_days_retained");
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${fifth_days_retained}", "0 AS fifth_days_retained");
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${sixth_days_retained}", "0 AS sixth_days_retained");
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${seventh_days_retained}", "0 AS seventh_days_retained");
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${fifteenth_days_retained}", "0 AS fifteenth_days_retained");
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${thirtieth_days_retained}", "count(DISTINCT `user_id`) AS thirtieth_days_retained");
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${retains_day}", retains_hour_30day);
							select_30ds_new_yyyymmdd = select_30ds_new_yyyymmdd.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
						}

						// 获取日留存查询聚合SQL
						StringBuffer str30Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str30Buffer.append(" " + insert_30ds_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str30Buffer.append(" UNION ");
								}
								str30Buffer.append(" " + select_30ds_new_yyyymmdd + " ");
								str30Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入日留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str30Buffer)) {
							SqlUtils.sqlExecute(dataSource, str30Buffer.toString(), this.getName());
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
						String retains_hour_2day = retains[1];
						String retains_hour_2month = retains_hour_2day.substring(0, retains_hour_2day.length() - 2);
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "DELETE FROM bi_st.st_active_user_retain_dm_" + retains_hour_2month + " where statis_date = " + retains_hour_2day, this.getName());
						// 插入新聚合数据
						String insert2DsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
						if (!StringUtil.isNullOrEmpty(insert2DsSelectSql)) {
							insert2DsSelectSql = insert2DsSelectSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_2month);
							insert2DsSelectSql = insert2DsSelectSql.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_2day);
							SqlUtils.sqlExecute(dataSource, insert2DsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_2ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_2ds_tmp_yyyymmdd)) {
							drop_2ds_tmp_yyyymmdd = drop_2ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_2day);
							SqlUtils.sqlExecute(dataSource, drop_2ds_tmp_yyyymmdd, this.getName());
						}
						break;
					case "3":
						String retains_hour_3day = retains[1];
						String retains_hour_3month = retains_hour_3day.substring(0, retains_hour_3day.length() - 2);
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "DELETE FROM bi_st.st_active_user_retain_dm_" + retains_hour_3month + " where statis_date = " + retains_hour_3day, this.getName());
						// 插入新聚合数据
						String insert3DsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
						if (!StringUtil.isNullOrEmpty(insert3DsSelectSql)) {
							insert3DsSelectSql = insert3DsSelectSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_3month);
							insert3DsSelectSql = insert3DsSelectSql.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_3day);
							SqlUtils.sqlExecute(dataSource, insert3DsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_3ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_3ds_tmp_yyyymmdd)) {
							drop_3ds_tmp_yyyymmdd = drop_3ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_3day);
							SqlUtils.sqlExecute(dataSource, drop_3ds_tmp_yyyymmdd, this.getName());
						}
						break;
					case "4":
						String retains_hour_4day = retains[1];
						String retains_hour_4month = retains_hour_4day.substring(0, retains_hour_4day.length() - 2);
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "DELETE FROM bi_st.st_active_user_retain_dm_" + retains_hour_4month + " where statis_date = " + retains_hour_4day, this.getName());
						// 插入新聚合数据
						String insert4DsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
						if (!StringUtil.isNullOrEmpty(insert4DsSelectSql)) {
							insert4DsSelectSql = insert4DsSelectSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_4month);
							insert4DsSelectSql = insert4DsSelectSql.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_4day);
							SqlUtils.sqlExecute(dataSource, insert4DsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_4ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_4ds_tmp_yyyymmdd)) {
							drop_4ds_tmp_yyyymmdd = drop_4ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_4day);
							SqlUtils.sqlExecute(dataSource, drop_4ds_tmp_yyyymmdd, this.getName());
						}
						break;
					case "5":
						String retains_hour_5day = retains[1];
						String retains_hour_5month = retains_hour_5day.substring(0, retains_hour_5day.length() - 2);
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "DELETE FROM bi_st.st_active_user_retain_dm_" + retains_hour_5month + " where statis_date = " + retains_hour_5day, this.getName());
						// 插入新聚合数据
						String insert5DsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
						if (!StringUtil.isNullOrEmpty(insert5DsSelectSql)) {
							insert5DsSelectSql = insert5DsSelectSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_5month);
							insert5DsSelectSql = insert5DsSelectSql.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_5day);
							SqlUtils.sqlExecute(dataSource, insert5DsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_5ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_5ds_tmp_yyyymmdd)) {
							drop_5ds_tmp_yyyymmdd = drop_5ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_5day);
							SqlUtils.sqlExecute(dataSource, drop_5ds_tmp_yyyymmdd, this.getName());
						}
						break;
					case "6":
						String retains_hour_6day = retains[1];
						String retains_hour_6month = retains_hour_6day.substring(0, retains_hour_6day.length() - 2);
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "DELETE FROM bi_st.st_active_user_retain_dm_" + retains_hour_6month + " where statis_date = " + retains_hour_6day, this.getName());
						// 插入新聚合数据
						String insert6DsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
						if (!StringUtil.isNullOrEmpty(insert6DsSelectSql)) {
							insert6DsSelectSql = insert6DsSelectSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_6month);
							insert6DsSelectSql = insert6DsSelectSql.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_6day);
							SqlUtils.sqlExecute(dataSource, insert6DsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_6ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_6ds_tmp_yyyymmdd)) {
							drop_6ds_tmp_yyyymmdd = drop_6ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_6day);
							SqlUtils.sqlExecute(dataSource, drop_6ds_tmp_yyyymmdd, this.getName());
						}
						break;
					case "7":
						String retains_hour_7day = retains[1];
						String retains_hour_7month = retains_hour_7day.substring(0, retains_hour_7day.length() - 2);
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "DELETE FROM bi_st.st_active_user_retain_dm_" + retains_hour_7month + " where statis_date = " + retains_hour_7day, this.getName());
						// 插入新聚合数据
						String insert7DsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
						if (!StringUtil.isNullOrEmpty(insert7DsSelectSql)) {
							insert7DsSelectSql = insert7DsSelectSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_7month);
							insert7DsSelectSql = insert7DsSelectSql.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_7day);
							SqlUtils.sqlExecute(dataSource, insert7DsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_7ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_7ds_tmp_yyyymmdd)) {
							drop_7ds_tmp_yyyymmdd = drop_7ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_7day);
							SqlUtils.sqlExecute(dataSource, drop_7ds_tmp_yyyymmdd, this.getName());
						}
						break;
					case "15":
						String retains_hour_15day = retains[1];
						String retains_hour_15month = retains_hour_15day.substring(0, retains_hour_15day.length() - 2);
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "DELETE FROM bi_st.st_active_user_retain_dm_" + retains_hour_15month + " where statis_date = " + retains_hour_15day, this.getName());
						// 插入新聚合数据
						String insert15DsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
						if (!StringUtil.isNullOrEmpty(insert15DsSelectSql)) {
							insert15DsSelectSql = insert15DsSelectSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_15month);
							insert15DsSelectSql = insert15DsSelectSql.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_15day);
							SqlUtils.sqlExecute(dataSource, insert15DsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_15ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_15ds_tmp_yyyymmdd)) {
							drop_15ds_tmp_yyyymmdd = drop_15ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_15day);
							SqlUtils.sqlExecute(dataSource, drop_15ds_tmp_yyyymmdd, this.getName());
						}
						break;
					case "30":
						String retains_hour_30day = retains[1];
						String retains_hour_30month = retains_hour_30day.substring(0, retains_hour_30day.length() - 2);
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "DELETE FROM bi_st.st_active_user_retain_dm_" + retains_hour_30month + " where statis_date = " + retains_hour_30day, this.getName());
						// 插入新聚合数据
						String insert30DsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
						if (!StringUtil.isNullOrEmpty(insert30DsSelectSql)) {
							insert30DsSelectSql = insert30DsSelectSql.replace("st_active_user_retain_dm_yyyymm", "st_active_user_retain_dm_" + retains_hour_30month);
							insert30DsSelectSql = insert30DsSelectSql.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_30day);
							SqlUtils.sqlExecute(dataSource, insert30DsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_30ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_30ds_tmp_yyyymmdd)) {
							drop_30ds_tmp_yyyymmdd = drop_30ds_tmp_yyyymmdd.replace("tmp_st_active_user_retain_ds_yyyymmdd", "tmp_st_active_user_retain_ds_" + retains_hour_30day);
							SqlUtils.sqlExecute(dataSource, drop_30ds_tmp_yyyymmdd, this.getName());
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
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(dataSource) + ",message: " + ex.getMessage(), scriptBean, callback);
			}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}
}
