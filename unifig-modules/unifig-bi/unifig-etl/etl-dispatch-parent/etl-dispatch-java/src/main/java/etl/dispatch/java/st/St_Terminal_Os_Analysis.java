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
import etl.dispatch.script.util.ListUtils;
import etl.dispatch.script.util.ScriptTimeUtil;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.StringUtil;

/**
 * 访问用户终端分析
 * 
 *
 */
@Service
public class St_Terminal_Os_Analysis extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Terminal_Os_Analysis.class);
	private static final String configJsonPath = "classpath*:conf/json/st_terminal_os_analysis.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.terminal_analysis";
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
			/**
			 * <<<<单日数据统计>>>>
			 */
			// 创建单天Os(DS)目标表
			String ds__os_target_sql = super.getJsonConfigValue(configJsonPath, "create_os_ds_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(ds__os_target_sql)) {
				ds__os_target_sql = ds__os_target_sql.replace("st_terminal_os_analysis_ds_yyyymmdd", "st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, ds__os_target_sql, this.getName());
			}
			
			// 支持重跑，删除dm当天数据
			String delete_os_yes_date = super.getJsonConfigValue(configJsonPath, "delete_os_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_os_yes_date)){
				delete_os_yes_date = delete_os_yes_date.replace("st_terminal_os_analysis_ds_yyyymmdd", "st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				delete_os_yes_date = delete_os_yes_date.replace("${statisDate}", ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, delete_os_yes_date, this.getName());
			}
			
			
			// 删除临时表
			String drop_os_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_os_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_os_ds_tmp_yyyymmdd)) {
				drop_os_ds_tmp_yyyymmdd = drop_os_ds_tmp_yyyymmdd.replace("tmp_st_terminal_os_analysis_ds_yyyymmdd", "tmp_st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_os_ds_tmp_yyyymmdd, this.getName());
			}
			// 创建临时表
			String create_os_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_os_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(create_os_ds_tmp_yyyymmdd)) {
				create_os_ds_tmp_yyyymmdd = create_os_ds_tmp_yyyymmdd.replace("tmp_st_terminal_os_analysis_ds_yyyymmdd", "tmp_st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, create_os_ds_tmp_yyyymmdd, this.getName());
			}
			
			// Cube聚合组合条件
			String[] criteriaOsArr = new String[] { "`hour`", "`app_plat_id`", "`app_version_id`", "`os_id`" };
			List<String> OslistWithRollup = super.getCriteriaArr(criteriaOsArr);

			// 获取(DS)insertDsSql
			String insertOsDsTmpSql = super.getJsonConfigValue(configJsonPath, "insertOsDsTmpSql");
			if (!StringUtil.isNullOrEmpty(insertOsDsTmpSql)) {
				insertOsDsTmpSql = insertOsDsTmpSql.replace("tmp_st_terminal_os_analysis_ds_yyyymmdd", "tmp_st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
			}
			// 获取(DS)新用户selectDsSql
			String selectOsDsTmpNewUserSql = super.getJsonConfigValue(configJsonPath, "selectOsDsTmpNewUserSql");
			if (!StringUtil.isNullOrEmpty(selectOsDsTmpNewUserSql)) {
				selectOsDsTmpNewUserSql = selectOsDsTmpNewUserSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				selectOsDsTmpNewUserSql = selectOsDsTmpNewUserSql.replace("${is_new}", "0");//0-新用户 1-老用户
			}
			// 获取(DS)所有用户selectDsSql
			String selectDsTmpAllUserSql = super.getJsonConfigValue(configJsonPath, "selectOsDsTmpAllUserSql");
			if (!StringUtil.isNullOrEmpty(selectDsTmpAllUserSql)) {
				selectDsTmpAllUserSql = selectDsTmpAllUserSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取(DS)新用户 Cube聚合SQL
			List<List<String>> splitDsNewUserList = ListUtils.splitList(OslistWithRollup);
			for (List<String> listRollup : splitDsNewUserList) {
				StringBuffer strDsBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDsBuffer.append(" " + insertOsDsTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDsBuffer.append(" UNION ");
						}
						strDsBuffer.append(" " + selectOsDsTmpNewUserSql + " ");
						strDsBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
						i++;
					}
				}
				// 插入(DS) Cube聚合数据
				if (!StringUtil.isNullOrEmpty(strDsBuffer)) {
					SqlUtils.sqlExecute(dataSource, strDsBuffer.toString(), this.getName());
				}
			}
			// 获取(DS)所有用户 Cube聚合SQL
			List<List<String>> splitDsAllUserList = ListUtils.splitList(OslistWithRollup);
			for (List<String> listRollup : splitDsAllUserList) {
				StringBuffer strDsBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDsBuffer.append(" " + insertOsDsTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDsBuffer.append(" UNION ");
						}
						strDsBuffer.append(" " + selectDsTmpAllUserSql + " ");
						strDsBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
						i++;
					}
				}
				// 插入(DS) Cube聚合数据
				if (!StringUtil.isNullOrEmpty(strDsBuffer)) {
					SqlUtils.sqlExecute(dataSource, strDsBuffer.toString(), this.getName());
				}
			}
			// 获取(DS)去重Sql
			String insertOsDsDistinctSql = super.getJsonConfigValue(configJsonPath, "insertOsDsDistinctSql");
			if (!StringUtil.isNullOrEmpty(insertOsDsDistinctSql)) {
				insertOsDsDistinctSql = insertOsDsDistinctSql.replace("tmp_st_terminal_os_analysis_ds_yyyymmdd", "tmp_st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertOsDsDistinctSql = insertOsDsDistinctSql.replace("st_terminal_os_analysis_ds_yyyymmdd", "st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertOsDsDistinctSql = insertOsDsDistinctSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertOsDsDistinctSql, this.getName());
			}
			// 清空临时表
			SqlUtils.sqlExecute(dataSource, "truncate table bi_tmp.tmp_st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday(), this.getName());
			// 数据迁移到临时表
			String insertOsDsWaiteSql = super.getJsonConfigValue(configJsonPath, "insertOsDsWaiteSql");
			if (!StringUtil.isNullOrEmpty(insertOsDsWaiteSql)) {
				insertOsDsWaiteSql = insertOsDsWaiteSql.replace("tmp_st_terminal_os_analysis_ds_yyyymmdd", "tmp_st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertOsDsWaiteSql = insertOsDsWaiteSql.replace("st_terminal_os_analysis_ds_yyyymmdd", "st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertOsDsWaiteSql = insertOsDsWaiteSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertOsDsWaiteSql, this.getName());
			}
			// 清空DS表
			SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday(), this.getName());
			// 聚合数据
			String insertOsDsSelectSql = super.getJsonConfigValue(configJsonPath, "insertOsDsSelectSql");
			if (!StringUtil.isNullOrEmpty(insertOsDsSelectSql)) {
				insertOsDsSelectSql = insertOsDsSelectSql.replace("tmp_st_terminal_os_analysis_ds_yyyymmdd", "tmp_st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertOsDsSelectSql = insertOsDsSelectSql.replace("st_terminal_os_analysis_ds_yyyymmdd", "st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertOsDsSelectSql = insertOsDsSelectSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertOsDsSelectSql, this.getName());
			}
			// 删除临时表
			if (!StringUtil.isNullOrEmpty(drop_os_ds_tmp_yyyymmdd)) {
				drop_os_ds_tmp_yyyymmdd = drop_os_ds_tmp_yyyymmdd.replace("tmp_st_terminal_os_analysis_ds_yyyymmdd", "tmp_st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_os_ds_tmp_yyyymmdd, this.getName());
			}
			
			
			/**
			 * <<<<日累计数据统计>>>>
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

			// 删除临时表
			String drop_os_dt_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_os_dt_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_os_dt_tmp_yyyymmdd)) {
				drop_os_dt_tmp_yyyymmdd = drop_os_dt_tmp_yyyymmdd.replace("tmp_st_terminal_os_analysis_dt_yyyymmdd", "tmp_st_terminal_os_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_os_dt_tmp_yyyymmdd, this.getName());
			}
			// 创建临时表
			String create_os_dt_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_os_dt_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(create_os_dt_tmp_yyyymmdd)) {
				create_os_dt_tmp_yyyymmdd = create_os_dt_tmp_yyyymmdd.replace("tmp_st_terminal_os_analysis_dt_yyyymmdd", "tmp_st_terminal_os_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, create_os_dt_tmp_yyyymmdd, this.getName());
			}

			// 3.7获取(DT)InsertSQL
			String insertOsDtTmpSql = super.getJsonConfigValue(configJsonPath, "insertOsDtTmpSql");
			if (!StringUtil.isNullOrEmpty(insertOsDtTmpSql)) {
				insertOsDtTmpSql = insertOsDtTmpSql.replace("tmp_st_terminal_os_analysis_dt_yyyymmdd", "tmp_st_terminal_os_analysis_dt_" + ScriptTimeUtil.optime_yesday());
			}

			// 3.8 获取(DT)新用户Cube聚合SQL
			List<List<String>> splitDtNewUserList = ListUtils.splitList(OslistWithRollup);
			for (List<String> listRollup : splitDtNewUserList) {
				StringBuffer strDtBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDtBuffer.append(" " + insertOsDtTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDtBuffer.append(" UNION ");
						}
						String selectDtTmpSql = super.getJsonConfigValue(configJsonPath, "selectOsDtTmpNewUserSql");
						strDtBuffer.append(" " + selectDtTmpSql + " ");
						strDtBuffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
						i++;
					}
				}
				// 3.9 插入(DT)Cube聚合数据
				if (null != sliceSet && !sliceSet.isEmpty()) {
					for (Integer dtTimeSlice : sliceSet) {
						if (!StringUtil.isNullOrEmpty(strDtBuffer)) {
							String dt_sql = strDtBuffer.toString();
							if (!StringUtil.isNullOrEmpty(dt_sql)) {
								dt_sql = dt_sql.replace("${time_slice}", String.valueOf(dtTimeSlice));
								dt_sql = dt_sql.replace("${statis_date}", ScriptTimeUtil.optime_yesday());
								dt_sql = dt_sql.replace("${min_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1 * dtTimeSlice));
								dt_sql = dt_sql.replace("${max_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1));
								dt_sql = dt_sql.replace("${is_new}", "0");//0-新用户 1-老用户
							}
							SqlUtils.sqlExecute(dataSource, dt_sql, this.getName());
						}
					}
				}
			}

			// 3.8 获取(DT)所有用户Cube聚合SQL
			List<List<String>> splitDtAllUserList = ListUtils.splitList(OslistWithRollup);
			for (List<String> listRollup : splitDtAllUserList) {
				StringBuffer strDtBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDtBuffer.append(" " + insertOsDtTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDtBuffer.append(" UNION ");
						}
						String selectDtTmpSql = super.getJsonConfigValue(configJsonPath, "selectOsDtTmpAllUserSql");
						strDtBuffer.append(" " + selectDtTmpSql + " ");
						strDtBuffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
						i++;
					}
				}
				// 3.9 插入(DT)Cube聚合数据
				if (null != sliceSet && !sliceSet.isEmpty()) {
					for (Integer dtTimeSlice : sliceSet) {
						if (!StringUtil.isNullOrEmpty(strDtBuffer)) {
							String dt_sql = strDtBuffer.toString();
							if (!StringUtil.isNullOrEmpty(dt_sql)) {
								dt_sql = dt_sql.replace("${time_slice}", String.valueOf(dtTimeSlice));
								dt_sql = dt_sql.replace("${statis_date}", ScriptTimeUtil.optime_yesday());
								dt_sql = dt_sql.replace("${min_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1 * dtTimeSlice));
								dt_sql = dt_sql.replace("${max_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1));
							}
							SqlUtils.sqlExecute(dataSource, dt_sql, this.getName());
						}
					}
				}
			}

			// 获取(DT)去重Sql
			String insertDtDistinctSql = super.getJsonConfigValue(configJsonPath, "insertOsDtDistinctSql");
			if (!StringUtil.isNullOrEmpty(insertDtDistinctSql)) {
				insertDtDistinctSql = insertDtDistinctSql.replace("tmp_st_terminal_os_analysis_dt_yyyymmdd", "tmp_st_terminal_os_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertDtDistinctSql = insertDtDistinctSql.replace("st_terminal_os_analysis_ds_yyyymmdd", "st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertDtDistinctSql = insertDtDistinctSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDtDistinctSql, this.getName());
			}

			// 清空临时表
			SqlUtils.sqlExecute(dataSource, "truncate table bi_tmp.tmp_st_terminal_os_analysis_dt_" + ScriptTimeUtil.optime_yesday(), this.getName());
			// 数据迁移到临时表
			String insertOsDtWaiteSql = super.getJsonConfigValue(configJsonPath, "insertOsDtWaiteSql");
			if (!StringUtil.isNullOrEmpty(insertOsDtWaiteSql)) {
				insertOsDtWaiteSql = insertOsDtWaiteSql.replace("tmp_st_terminal_os_analysis_dt_yyyymmdd", "tmp_st_terminal_os_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertOsDtWaiteSql = insertOsDtWaiteSql.replace("st_terminal_os_analysis_ds_yyyymmdd", "st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertOsDtWaiteSql = insertOsDtWaiteSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertOsDtWaiteSql, this.getName());
			}
			// 清空DT表
			SqlUtils.sqlExecute(dataSource, "delete from bi_st.st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday() + " where time_slice != 1", this.getName());
			// 聚合数据
			String insertOsDtSelectSql = super.getJsonConfigValue(configJsonPath, "insertOsDtSelectSql");
			if (!StringUtil.isNullOrEmpty(insertOsDtSelectSql)) {
				insertOsDtSelectSql = insertOsDtSelectSql.replace("tmp_st_terminal_os_analysis_dt_yyyymmdd", "tmp_st_terminal_os_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertOsDtSelectSql = insertOsDtSelectSql.replace("st_terminal_os_analysis_ds_yyyymmdd", "st_terminal_os_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertOsDtSelectSql = insertOsDtSelectSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertOsDtSelectSql, this.getName());
			}
			
			// 删除临时表
			if (!StringUtil.isNullOrEmpty(drop_os_dt_tmp_yyyymmdd)) {
				drop_os_dt_tmp_yyyymmdd = drop_os_dt_tmp_yyyymmdd.replace("tmp_st_terminal_os_analysis_dt_yyyymmdd", "tmp_st_terminal_os_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_os_dt_tmp_yyyymmdd, this.getName());
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
