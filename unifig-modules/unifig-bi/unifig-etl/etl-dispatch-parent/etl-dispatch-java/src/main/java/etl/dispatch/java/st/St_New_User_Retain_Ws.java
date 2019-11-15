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
 * 新增用户首次使用周留存 统计
 * 
 *
 *
 */
@Service
public class St_New_User_Retain_Ws extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_New_User_Retain_Ws.class);
	private static final String configJsonPath = "classpath*:conf/json/st_new_user_retain_ws.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.new_user_retain_ws";
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
			String optime_hour_mon = DateUtil.getWeekMondayByDate(new Date(), 1);
			
			/**
			 * 周留存统计>>>>>> st_new_user_retain_ws_yyyymmdd
			 */
			// 创建单周(WS)目标表
			String target_ws_sql = super.getJsonConfigValue(configJsonPath, "create_ws_yyyymmdd",false);
			if (!StringUtil.isNullOrEmpty(target_ws_sql)) {
				target_ws_sql = target_ws_sql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + ScriptTimeUtil.optime_monday());
				SqlUtils.sqlExecute(dataSource, target_ws_sql, this.getName());
			}
			// 创建单周新用户临时表
			String create_ws_tmpuser_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_ws_tmpuser_yyyymmdd",false);
			if (!StringUtil.isNullOrEmpty(create_ws_tmpuser_yyyymmdd)) {
				SqlUtils.sqlExecute(dataSource, create_ws_tmpuser_yyyymmdd, this.getName());
			}
			// 支持重跑，删除当天数据与历史数据
			String delete_ws_date = super.getJsonConfigValue(configJsonPath, "delete_ws_date");
			if(!StringUtil.isNullOrEmpty(delete_ws_date)){
				delete_ws_date = delete_ws_date.replace("${statisDate}", optime_hour_mon);
				SqlUtils.sqlExecute(dataSource, delete_ws_date, this.getName());
			}
			
			// 删除临时表活跃用户清单
			String delete_yes_tmpDate = super.getJsonConfigValue(configJsonPath, "delete_yes_tmpDate");
			if (!StringUtil.isNullOrEmpty(delete_yes_tmpDate)) {
				delete_yes_tmpDate = delete_yes_tmpDate.replace("${statisDate}", optime_hour_mon);
				SqlUtils.sqlExecute(dataSource, delete_yes_tmpDate, this.getName());
			}
			
			// 插入单周新用户数据
			String insert_ws_tmpuser_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_tmpuser_yyyymmdd",false);
			if (!StringUtil.isNullOrEmpty(insert_ws_tmpuser_yyyymmdd)) {
				insert_ws_tmpuser_yyyymmdd = insert_ws_tmpuser_yyyymmdd.replace("${statis_date}", ScriptTimeUtil.optime_monday());
				insert_ws_tmpuser_yyyymmdd = insert_ws_tmpuser_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
				insert_ws_tmpuser_yyyymmdd = insert_ws_tmpuser_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
				insert_ws_tmpuser_yyyymmdd = insert_ws_tmpuser_yyyymmdd.replace("${is_new}", String.valueOf("0"));//0-新用户 1-老用户
				SqlUtils.sqlExecute(dataSource, insert_ws_tmpuser_yyyymmdd, this.getName());
			}
			
			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`", "`app_version_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 获取InsertSQL
			String insertWsSql = super.getJsonConfigValue(configJsonPath, "insertWsSql", false);
			if (!StringUtil.isNullOrEmpty(insertWsSql)) {
				insertWsSql = insertWsSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + ScriptTimeUtil.optime_monday());
			}
			// 获取周留存查询SQL
			String selectWsSql = super.getJsonConfigValue(configJsonPath, "selectWsSql", false);
			if (!StringUtil.isNullOrEmpty(selectWsSql)) {
				selectWsSql = selectWsSql.replace("${statis_date}", ScriptTimeUtil.optime_monday());
				selectWsSql = selectWsSql.replace("${min_id}", ScriptTimeUtil.optime_monday());
				selectWsSql = selectWsSql.replace("${max_id}", ScriptTimeUtil.optime_sunday());
				selectWsSql = selectWsSql.replace("${is_new}", String.valueOf("0"));//0-新用户 1-老用户
			}
			// 获取周留存查询聚合SQL
			StringBuffer strWeekWsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strWeekWsBuffer.append(" " + insertWsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strWeekWsBuffer.append(" UNION ");
					}
					strWeekWsBuffer.append(" " + selectWsSql + " ");
					strWeekWsBuffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入周留存查询Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strWeekWsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strWeekWsBuffer.toString(), this.getName());
			}

			// 历史新用户留存统计数据，2、3、4、5、6、7、8、9
			Integer[] retainTimes = new Integer[] { 2, 3, 4, 5, 6, 7, 8, 9 };

			// 查询新用户时间
			String statisDateSql = "select statis_date from bi_tmp.tmp_st_new_user_retain_ws_all_store group by statis_date";
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
				//获取历史周的 周一
				String retains_hour_week = DateUtil.getWeekMondayByDate(new Date(), 1 * retainTime);
				if (!statisDateUser.contains(retains_hour_week)) {
					continue;
				}
				// 删除留存临时表
				String drop_ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
				if (!StringUtil.isNullOrEmpty(drop_ws_tmp_yyyymmdd)) {
					drop_ws_tmp_yyyymmdd = drop_ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_week);
					SqlUtils.sqlExecute(dataSource, drop_ws_tmp_yyyymmdd, this.getName());
				}
				// 创建留存临时表
				String create_ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_ws_tmp_yyyymmdd");
				if (!StringUtil.isNullOrEmpty(create_ws_tmp_yyyymmdd)) {
					create_ws_tmp_yyyymmdd = create_ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_week);
					SqlUtils.sqlExecute(dataSource, create_ws_tmp_yyyymmdd, this.getName());
				}
				// 历史数据转移
				String insert_ws_old_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_old_yyyymmdd");
				if (!StringUtil.isNullOrEmpty(insert_ws_old_yyyymmdd)) {
					insert_ws_old_yyyymmdd = insert_ws_old_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_week);
					insert_ws_old_yyyymmdd = insert_ws_old_yyyymmdd.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_week);
					insert_ws_old_yyyymmdd = insert_ws_old_yyyymmdd.replace("${statis_date}", retains_hour_week);
					SqlUtils.sqlExecute(dataSource, insert_ws_old_yyyymmdd, this.getName());
				}
				retainUserDate.add(retainTime + ":" + retains_hour_week);
			}

			// 历史数据聚合运算
			if (!retainUserDate.isEmpty()) {
				for (String retainDate : retainUserDate) {
					String[] retains = retainDate.split(":");
					switch (retains[0]) {
					case "2":
						String retains_hour_2week = retains[1];
						// 获取InsertSQL
						String insert_2ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_2ws_new_yyyymmdd)) {
							insert_2ws_new_yyyymmdd = insert_2ws_new_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_2week);
						}
						// 获取周留存查询SQL
						String select_2ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_2ws_new_yyyymmdd)) {
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${statis_date}", retains_hour_2week);
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${second_weeks_retained}", "count(DISTINCT `user_id`) AS second_weeks_retained");
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${third_weeks_retained}", "0 AS third_weeks_retained");
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${fourth_weeks_retained}", "0 AS fourth_weeks_retained");
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${fifth_weeks_retained}", "0 AS fifth_weeks_retained");
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${sixth_weeks_retained}", "0 AS sixth_weeks_retained");
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${seventh_weeks_retained}", "0 AS seventh_weeks_retained");
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${eighth_weeks_retained}", "0 AS eighth_weeks_retained");
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${ninth_weeks_retained}", "0 AS ninth_weeks_retained");
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${retains_week}", retains_hour_2week);
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
							select_2ws_new_yyyymmdd = select_2ws_new_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
						}

						// 获取周留存查询聚合SQL
						StringBuffer str2Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str2Buffer.append(" " + insert_2ws_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str2Buffer.append(" UNION ");
								}
								str2Buffer.append(" " + select_2ws_new_yyyymmdd + " ");
								str2Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入周留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str2Buffer)) {
							SqlUtils.sqlExecute(dataSource, str2Buffer.toString(), this.getName());
						}
						break;
					case "3":
						String retains_hour_3week = retains[1];
						// 获取InsertSQL
						String insert_3ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_3ws_new_yyyymmdd)) {
							insert_3ws_new_yyyymmdd = insert_3ws_new_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_3week);
						}
						// 获取周留存查询SQL
						String select_3ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_3ws_new_yyyymmdd)) {
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${statis_date}", retains_hour_3week);
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${second_weeks_retained}", "0 AS second_weeks_retained");
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${third_weeks_retained}", "count(DISTINCT `user_id`) AS third_weeks_retained");
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${fourth_weeks_retained}", "0 AS fourth_weeks_retained");
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${fifth_weeks_retained}", "0 AS fifth_weeks_retained");
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${sixth_weeks_retained}", "0 AS sixth_weeks_retained");
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${seventh_weeks_retained}", "0 AS seventh_weeks_retained");
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${eighth_weeks_retained}", "0 AS eighth_weeks_retained");
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${ninth_weeks_retained}", "0 AS ninth_weeks_retained");
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${retains_week}", retains_hour_3week);
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
							select_3ws_new_yyyymmdd = select_3ws_new_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
						}

						// 获取周留存查询聚合SQL
						StringBuffer str3Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str3Buffer.append(" " + insert_3ws_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str3Buffer.append(" UNION ");
								}
								str3Buffer.append(" " + select_3ws_new_yyyymmdd + " ");
								str3Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入周留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str3Buffer)) {
							SqlUtils.sqlExecute(dataSource, str3Buffer.toString(), this.getName());
						}
						break;
					case "4":
						String retains_hour_4week = retains[1];
						// 获取InsertSQL
						String insert_4ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_4ws_new_yyyymmdd)) {
							insert_4ws_new_yyyymmdd = insert_4ws_new_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_4week);
						}
						// 获取周留存查询SQL
						String select_4ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_4ws_new_yyyymmdd)) {
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${statis_date}", retains_hour_4week);
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${second_weeks_retained}", "0 AS second_weeks_retained");
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${third_weeks_retained}", " 0 AS third_weeks_retained");
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${fourth_weeks_retained}", "count(DISTINCT `user_id`) AS fourth_weeks_retained");
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${fifth_weeks_retained}", "0 AS fifth_weeks_retained");
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${sixth_weeks_retained}", "0 AS sixth_weeks_retained");
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${seventh_weeks_retained}", "0 AS seventh_weeks_retained");
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${eighth_weeks_retained}", "0 AS eighth_weeks_retained");
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${ninth_weeks_retained}", "0 AS ninth_weeks_retained");
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${retains_week}", retains_hour_4week);
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
							select_4ws_new_yyyymmdd = select_4ws_new_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
						}

						// 获取周留存查询聚合SQL
						StringBuffer str4Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str4Buffer.append(" " + insert_4ws_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str4Buffer.append(" UNION ");
								}
								str4Buffer.append(" " + select_4ws_new_yyyymmdd + " ");
								str4Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入周留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str4Buffer)) {
							SqlUtils.sqlExecute(dataSource, str4Buffer.toString(), this.getName());
						}
						break;
					case "5":
						String retains_hour_5week = retains[1];
						// 获取InsertSQL
						String insert_5ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_5ws_new_yyyymmdd)) {
							insert_5ws_new_yyyymmdd = insert_5ws_new_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_5week);
						}
						// 获取周留存查询SQL
						String select_5ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_5ws_new_yyyymmdd)) {
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${statis_date}", retains_hour_5week);
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${second_weeks_retained}", "0 AS second_weeks_retained");
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${third_weeks_retained}", " 0 AS third_weeks_retained");
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${fourth_weeks_retained}", "0 AS fourth_weeks_retained");
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${fifth_weeks_retained}", "count(DISTINCT `user_id`) AS fifth_weeks_retained");
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${sixth_weeks_retained}", "0 AS sixth_weeks_retained");
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${seventh_weeks_retained}", "0 AS seventh_weeks_retained");
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${eighth_weeks_retained}", "0 AS eighth_weeks_retained");
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${ninth_weeks_retained}", "0 AS ninth_weeks_retained");
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${retains_week}", retains_hour_5week);
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
							select_5ws_new_yyyymmdd = select_5ws_new_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
						}

						// 获取周留存查询聚合SQL
						StringBuffer str5Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str5Buffer.append(" " + insert_5ws_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str5Buffer.append(" UNION ");
								}
								str5Buffer.append(" " + select_5ws_new_yyyymmdd + " ");
								str5Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入周留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str5Buffer)) {
							SqlUtils.sqlExecute(dataSource, str5Buffer.toString(), this.getName());
						}
						break;
					case "6":
						String retains_hour_6week = retains[1];
						// 获取InsertSQL
						String insert_6ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_6ws_new_yyyymmdd)) {
							insert_6ws_new_yyyymmdd = insert_6ws_new_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_6week);
						}
						// 获取周留存查询SQL
						String select_6ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_6ws_new_yyyymmdd)) {
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${statis_date}", retains_hour_6week);
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${second_weeks_retained}", "0 AS second_weeks_retained");
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${third_weeks_retained}", " 0 AS third_weeks_retained");
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${fourth_weeks_retained}", "0 AS fourth_weeks_retained");
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${fifth_weeks_retained}", "0 AS fifth_weeks_retained");
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${sixth_weeks_retained}", "count(DISTINCT `user_id`)  AS sixth_weeks_retained");
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${seventh_weeks_retained}", "0 AS seventh_weeks_retained");
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${eighth_weeks_retained}", "0 AS eighth_weeks_retained");
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${ninth_weeks_retained}", "0 AS ninth_weeks_retained");
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${retains_week}", retains_hour_6week);
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
							select_6ws_new_yyyymmdd = select_6ws_new_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
						}

						// 获取周留存查询聚合SQL
						StringBuffer str6Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str6Buffer.append(" " + insert_6ws_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str6Buffer.append(" UNION ");
								}
								str6Buffer.append(" " + select_6ws_new_yyyymmdd + " ");
								str6Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入周留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str6Buffer)) {
							SqlUtils.sqlExecute(dataSource, str6Buffer.toString(), this.getName());
						}
						break;
					case "7":
						String retains_hour_7week = retains[1];
						// 获取InsertSQL
						String insert_7ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_7ws_new_yyyymmdd)) {
							insert_7ws_new_yyyymmdd = insert_7ws_new_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_7week);
						}
						// 获取周留存查询SQL
						String select_7ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_7ws_new_yyyymmdd)) {
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${statis_date}", retains_hour_7week);
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${second_weeks_retained}", "0 AS second_weeks_retained");
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${third_weeks_retained}", " 0 AS third_weeks_retained");
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${fourth_weeks_retained}", "0 AS fourth_weeks_retained");
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${fifth_weeks_retained}", "0 AS fifth_weeks_retained");
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${sixth_weeks_retained}", "0 AS sixth_weeks_retained");
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${seventh_weeks_retained}", "count(DISTINCT `user_id`)  AS seventh_weeks_retained");
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${eighth_weeks_retained}", "0 AS eighth_weeks_retained");
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${ninth_weeks_retained}", "0 AS ninth_weeks_retained");
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${retains_week}", retains_hour_7week);
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
							select_7ws_new_yyyymmdd = select_7ws_new_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
						}
						// 获取周留存查询聚合SQL
						StringBuffer str7Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str7Buffer.append(" " + insert_7ws_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str7Buffer.append(" UNION ");
								}
								str7Buffer.append(" " + select_7ws_new_yyyymmdd + " ");
								str7Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入周留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str7Buffer)) {
							SqlUtils.sqlExecute(dataSource, str7Buffer.toString(), this.getName());
						}
						break;
					case "8":
						String retains_hour_8week = retains[1];
						// 获取InsertSQL
						String insert_8ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_8ws_new_yyyymmdd)) {
							insert_8ws_new_yyyymmdd = insert_8ws_new_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_8week);
						}
						// 获取周留存查询SQL
						String select_8ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_8ws_new_yyyymmdd)) {
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${statis_date}", retains_hour_8week);
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${second_weeks_retained}", "0 AS second_weeks_retained");
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${third_weeks_retained}", " 0 AS third_weeks_retained");
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${fourth_weeks_retained}", "0 AS fourth_weeks_retained");
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${fifth_weeks_retained}", "0 AS fifth_weeks_retained");
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${sixth_weeks_retained}", "0 AS sixth_weeks_retained");
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${seventh_weeks_retained}", "0 AS seventh_weeks_retained");
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${eighth_weeks_retained}", "count(DISTINCT `user_id`)   AS eighth_weeks_retained");
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${ninth_weeks_retained}", "0 AS ninth_weeks_retained");
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${retains_week}", retains_hour_8week);
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
							select_8ws_new_yyyymmdd = select_8ws_new_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
						}

						// 获取周留存查询聚合SQL
						StringBuffer str8Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str8Buffer.append(" " + insert_8ws_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str8Buffer.append(" UNION ");
								}
								str8Buffer.append(" " + select_8ws_new_yyyymmdd + " ");
								str8Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入周留存查询Cube聚合数据
						if (!StringUtil.isNullOrEmpty(str8Buffer)) {
							SqlUtils.sqlExecute(dataSource, str8Buffer.toString(), this.getName());
						}
						break;
					case "9":
						String retains_hour_9week = retains[1];
						// 获取InsertSQL
						String insert_9ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "insert_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(insert_9ws_new_yyyymmdd)) {
							insert_9ws_new_yyyymmdd = insert_9ws_new_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_9week);
						}
						// 获取周留存查询SQL
						String select_9ws_new_yyyymmdd = super.getJsonConfigValue(configJsonPath, "select_ws_new_yyyymmdd", false);
						if (!StringUtil.isNullOrEmpty(select_9ws_new_yyyymmdd)) {
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${statis_date}", retains_hour_9week);
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${second_weeks_retained}", "0 AS second_weeks_retained");
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${third_weeks_retained}", " 0 AS third_weeks_retained");
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${fourth_weeks_retained}", "0 AS fourth_weeks_retained");
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${fifth_weeks_retained}", "0 AS fifth_weeks_retained");
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${sixth_weeks_retained}", "0 AS sixth_weeks_retained");
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${seventh_weeks_retained}", "0 AS seventh_weeks_retained");
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${eighth_weeks_retained}", "0 AS eighth_weeks_retained");
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${ninth_weeks_retained}", "count(DISTINCT `user_id`) AS ninth_weeks_retained");
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${retains_week}", retains_hour_9week);
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${min_id}", ScriptTimeUtil.optime_monday());
							select_9ws_new_yyyymmdd = select_9ws_new_yyyymmdd.replace("${max_id}", ScriptTimeUtil.optime_sunday());
						}

						// 获取周留存查询聚合SQL
						StringBuffer str9Buffer = new StringBuffer();
						if (null != listWithRollup && !listWithRollup.isEmpty()) {
							str9Buffer.append(" " + insert_9ws_new_yyyymmdd + " ");
							int i = 0;
							for (String withRollup : listWithRollup) {
								if (i > 0) {
									str9Buffer.append(" UNION ");
								}
								str9Buffer.append(" " + select_9ws_new_yyyymmdd + " ");
								str9Buffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
								i++;
							}
						}
						// 插入周留存查询Cube聚合数据
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
						String retains_hour_2week = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_new_user_retain_ws_" + retains_hour_2week, this.getName());
						// 插入新聚合数据
						String insert2WsSelectSql = super.getJsonConfigValue(configJsonPath, "insertWsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert2WsSelectSql)) {
							insert2WsSelectSql = insert2WsSelectSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_2week);
							insert2WsSelectSql = insert2WsSelectSql.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_2week);
							SqlUtils.sqlExecute(dataSource, insert2WsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_2ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_2ws_tmp_yyyymmdd)) {
							drop_2ws_tmp_yyyymmdd = drop_2ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_2week);
							SqlUtils.sqlExecute(dataSource, drop_2ws_tmp_yyyymmdd, this.getName());
						}
						break;
					case "3":
						String retains_hour_3week = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_new_user_retain_ws_" + retains_hour_3week, this.getName());
						// 插入新聚合数据
						String insert3WsSelectSql = super.getJsonConfigValue(configJsonPath, "insertWsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert3WsSelectSql)) {
							insert3WsSelectSql = insert3WsSelectSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_3week);
							insert3WsSelectSql = insert3WsSelectSql.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_3week);
							SqlUtils.sqlExecute(dataSource, insert3WsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_3ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_3ws_tmp_yyyymmdd)) {
							drop_3ws_tmp_yyyymmdd = drop_3ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_3week);
							SqlUtils.sqlExecute(dataSource, drop_3ws_tmp_yyyymmdd, this.getName());
						}
						break;
					case "4":
						String retains_hour_4week = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_new_user_retain_ws_" + retains_hour_4week, this.getName());
						// 插入新聚合数据
						String insert4WsSelectSql = super.getJsonConfigValue(configJsonPath, "insertWsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert4WsSelectSql)) {
							insert4WsSelectSql = insert4WsSelectSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_4week);
							insert4WsSelectSql = insert4WsSelectSql.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_4week);
							SqlUtils.sqlExecute(dataSource, insert4WsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_4ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_4ws_tmp_yyyymmdd)) {
							drop_4ws_tmp_yyyymmdd = drop_4ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_4week);
							SqlUtils.sqlExecute(dataSource, drop_4ws_tmp_yyyymmdd, this.getName());
						}
						break;
					case "5":
						String retains_hour_5week = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_new_user_retain_ws_" + retains_hour_5week, this.getName());
						// 插入新聚合数据
						String insert5WsSelectSql = super.getJsonConfigValue(configJsonPath, "insertWsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert5WsSelectSql)) {
							insert5WsSelectSql = insert5WsSelectSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_5week);
							insert5WsSelectSql = insert5WsSelectSql.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_5week);
							SqlUtils.sqlExecute(dataSource, insert5WsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_5ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_5ws_tmp_yyyymmdd)) {
							drop_5ws_tmp_yyyymmdd = drop_5ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_5week);
							SqlUtils.sqlExecute(dataSource, drop_5ws_tmp_yyyymmdd, this.getName());
						}
						break;
					case "6":
						String retains_hour_6week = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_new_user_retain_ws_" + retains_hour_6week, this.getName());
						// 插入新聚合数据
						String insert6WsSelectSql = super.getJsonConfigValue(configJsonPath, "insertWsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert6WsSelectSql)) {
							insert6WsSelectSql = insert6WsSelectSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_6week);
							insert6WsSelectSql = insert6WsSelectSql.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_6week);
							SqlUtils.sqlExecute(dataSource, insert6WsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_6ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_6ws_tmp_yyyymmdd)) {
							drop_6ws_tmp_yyyymmdd = drop_6ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_6week);
							SqlUtils.sqlExecute(dataSource, drop_6ws_tmp_yyyymmdd, this.getName());
						}
						break;
					case "7":
						String retains_hour_7week = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_new_user_retain_ws_" + retains_hour_7week, this.getName());
						// 插入新聚合数据
						String insert7WsSelectSql = super.getJsonConfigValue(configJsonPath, "insertWsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert7WsSelectSql)) {
							insert7WsSelectSql = insert7WsSelectSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_7week);
							insert7WsSelectSql = insert7WsSelectSql.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_7week);
							SqlUtils.sqlExecute(dataSource, insert7WsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_7ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_7ws_tmp_yyyymmdd)) {
							drop_7ws_tmp_yyyymmdd = drop_7ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_7week);
							SqlUtils.sqlExecute(dataSource, drop_7ws_tmp_yyyymmdd, this.getName());
						}
						break;
					case "8":
						String retains_hour_8week = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_new_user_retain_ws_" + retains_hour_8week, this.getName());
						// 插入新聚合数据
						String insert8WsSelectSql = super.getJsonConfigValue(configJsonPath, "insertWsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert8WsSelectSql)) {
							insert8WsSelectSql = insert8WsSelectSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_8week);
							insert8WsSelectSql = insert8WsSelectSql.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_8week);
							SqlUtils.sqlExecute(dataSource, insert8WsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_8ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_8ws_tmp_yyyymmdd)) {
							drop_8ws_tmp_yyyymmdd = drop_8ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_8week);
							SqlUtils.sqlExecute(dataSource, drop_8ws_tmp_yyyymmdd, this.getName());
						}
						break;
					case "9":
						String retains_hour_9week = retains[1];
						// truncate旧 数据
						SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_new_user_retain_ws_" + retains_hour_9week, this.getName());
						// 插入新聚合数据
						String insert9WsSelectSql = super.getJsonConfigValue(configJsonPath, "insertWsSelectSql");
						if (!StringUtil.isNullOrEmpty(insert9WsSelectSql)) {
							insert9WsSelectSql = insert9WsSelectSql.replace("st_new_user_retain_ws_yyyymmdd", "st_new_user_retain_ws_" + retains_hour_9week);
							insert9WsSelectSql = insert9WsSelectSql.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_9week);
							SqlUtils.sqlExecute(dataSource, insert9WsSelectSql, this.getName());
						}
						// 删除留存临时表
						String drop_9ws_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ws_tmp_yyyymmdd");
						if (!StringUtil.isNullOrEmpty(drop_9ws_tmp_yyyymmdd)) {
							drop_9ws_tmp_yyyymmdd = drop_9ws_tmp_yyyymmdd.replace("tmp_st_new_user_retain_ws_yyyymmdd", "tmp_st_new_user_retain_ws_" + retains_hour_9week);
							SqlUtils.sqlExecute(dataSource, drop_9ws_tmp_yyyymmdd, this.getName());
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
