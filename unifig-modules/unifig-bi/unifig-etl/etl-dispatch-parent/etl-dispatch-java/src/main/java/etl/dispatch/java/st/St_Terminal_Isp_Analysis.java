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
public class St_Terminal_Isp_Analysis extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Terminal_Isp_Analysis.class);
	private static final String configJsonPath = "classpath*:conf/json/st_terminal_isp_analysis.json";
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
			// 创建单天Isp(DS)目标表
			String ds__isp_target_sql = super.getJsonConfigValue(configJsonPath, "create_isp_ds_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(ds__isp_target_sql)) {
				ds__isp_target_sql = ds__isp_target_sql.replace("st_terminal_isp_analysis_ds_yyyymmdd", "st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, ds__isp_target_sql, this.getName());
			}
			
			// 支持重跑，删除dm当天数据
			String delete_isp_yes_date = super.getJsonConfigValue(configJsonPath, "delete_isp_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_isp_yes_date)){
				delete_isp_yes_date = delete_isp_yes_date.replace("st_terminal_isp_analysis_ds_yyyymmdd", "st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				delete_isp_yes_date = delete_isp_yes_date.replace("${statisDate}", ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, delete_isp_yes_date, this.getName());
			}
			
			
			// 删除临时表
			String drop_isp_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_isp_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_isp_ds_tmp_yyyymmdd)) {
				drop_isp_ds_tmp_yyyymmdd = drop_isp_ds_tmp_yyyymmdd.replace("tmp_st_terminal_isp_analysis_ds_yyyymmdd", "tmp_st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_isp_ds_tmp_yyyymmdd, this.getName());
			}
			// 创建临时表
			String create_isp_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_isp_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(create_isp_ds_tmp_yyyymmdd)) {
				create_isp_ds_tmp_yyyymmdd = create_isp_ds_tmp_yyyymmdd.replace("tmp_st_terminal_isp_analysis_ds_yyyymmdd", "tmp_st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, create_isp_ds_tmp_yyyymmdd, this.getName());
			}
			
			// Cube聚合组合条件
			String[] criteriaIspArr = new String[] { "`hour`", "`app_plat_id`", "`app_version_id`", "`isp_id`" };
			List<String> IsplistWithRollup = super.getCriteriaArr(criteriaIspArr);

			// 获取(DS)insertDsSql
			String insertIspDsTmpSql = super.getJsonConfigValue(configJsonPath, "insertIspDsTmpSql");
			if (!StringUtil.isNullOrEmpty(insertIspDsTmpSql)) {
				insertIspDsTmpSql = insertIspDsTmpSql.replace("tmp_st_terminal_isp_analysis_ds_yyyymmdd", "tmp_st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
			}
			// 获取(DS)新用户selectDsSql
			String selectIspDsTmpNewUserSql = super.getJsonConfigValue(configJsonPath, "selectIspDsTmpNewUserSql");
			if (!StringUtil.isNullOrEmpty(selectIspDsTmpNewUserSql)) {
				selectIspDsTmpNewUserSql = selectIspDsTmpNewUserSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				selectIspDsTmpNewUserSql = selectIspDsTmpNewUserSql.replace("${is_new}", "0");//0-新用户 1-老用户
			}
			// 获取(DS)所有用户selectDsSql
			String selectDsTmpAllUserSql = super.getJsonConfigValue(configJsonPath, "selectIspDsTmpAllUserSql");
			if (!StringUtil.isNullOrEmpty(selectDsTmpAllUserSql)) {
				selectDsTmpAllUserSql = selectDsTmpAllUserSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取(DS)新用户 Cube聚合SQL
			List<List<String>> splitDsNewUserList = ListUtils.splitList(IsplistWithRollup);
			for (List<String> listRollup : splitDsNewUserList) {
				StringBuffer strDsBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDsBuffer.append(" " + insertIspDsTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDsBuffer.append(" UNION ");
						}
						strDsBuffer.append(" " + selectIspDsTmpNewUserSql + " ");
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
			List<List<String>> splitDsAllUserList = ListUtils.splitList(IsplistWithRollup);
			for (List<String> listRollup : splitDsAllUserList) {
				StringBuffer strDsBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDsBuffer.append(" " + insertIspDsTmpSql + " ");
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
			String insertIspDsDistinctSql = super.getJsonConfigValue(configJsonPath, "insertIspDsDistinctSql");
			if (!StringUtil.isNullOrEmpty(insertIspDsDistinctSql)) {
				insertIspDsDistinctSql = insertIspDsDistinctSql.replace("tmp_st_terminal_isp_analysis_ds_yyyymmdd", "tmp_st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertIspDsDistinctSql = insertIspDsDistinctSql.replace("st_terminal_isp_analysis_ds_yyyymmdd", "st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertIspDsDistinctSql = insertIspDsDistinctSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertIspDsDistinctSql, this.getName());
			}
			// 清空临时表
			SqlUtils.sqlExecute(dataSource, "truncate table bi_tmp.tmp_st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday(), this.getName());
			// 数据迁移到临时表
			String insertIspDsWaiteSql = super.getJsonConfigValue(configJsonPath, "insertIspDsWaiteSql");
			if (!StringUtil.isNullOrEmpty(insertIspDsWaiteSql)) {
				insertIspDsWaiteSql = insertIspDsWaiteSql.replace("tmp_st_terminal_isp_analysis_ds_yyyymmdd", "tmp_st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertIspDsWaiteSql = insertIspDsWaiteSql.replace("st_terminal_isp_analysis_ds_yyyymmdd", "st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertIspDsWaiteSql = insertIspDsWaiteSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertIspDsWaiteSql, this.getName());
			}
			// 清空DS表
			SqlUtils.sqlExecute(dataSource, "truncate table bi_st.st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday(), this.getName());
			// 聚合数据
			String insertIspDsSelectSql = super.getJsonConfigValue(configJsonPath, "insertIspDsSelectSql");
			if (!StringUtil.isNullOrEmpty(insertIspDsSelectSql)) {
				insertIspDsSelectSql = insertIspDsSelectSql.replace("tmp_st_terminal_isp_analysis_ds_yyyymmdd", "tmp_st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertIspDsSelectSql = insertIspDsSelectSql.replace("st_terminal_isp_analysis_ds_yyyymmdd", "st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertIspDsSelectSql = insertIspDsSelectSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertIspDsSelectSql, this.getName());
			}
			// 删除临时表
			if (!StringUtil.isNullOrEmpty(drop_isp_ds_tmp_yyyymmdd)) {
				drop_isp_ds_tmp_yyyymmdd = drop_isp_ds_tmp_yyyymmdd.replace("tmp_st_terminal_isp_analysis_ds_yyyymmdd", "tmp_st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_isp_ds_tmp_yyyymmdd, this.getName());
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
			String drop_isp_dt_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_isp_dt_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_isp_dt_tmp_yyyymmdd)) {
				drop_isp_dt_tmp_yyyymmdd = drop_isp_dt_tmp_yyyymmdd.replace("tmp_st_terminal_isp_analysis_dt_yyyymmdd", "tmp_st_terminal_isp_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_isp_dt_tmp_yyyymmdd, this.getName());
			}
			// 创建临时表
			String create_isp_dt_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_isp_dt_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(create_isp_dt_tmp_yyyymmdd)) {
				create_isp_dt_tmp_yyyymmdd = create_isp_dt_tmp_yyyymmdd.replace("tmp_st_terminal_isp_analysis_dt_yyyymmdd", "tmp_st_terminal_isp_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, create_isp_dt_tmp_yyyymmdd, this.getName());
			}

			// 3.7获取(DT)InsertSQL
			String insertIspDtTmpSql = super.getJsonConfigValue(configJsonPath, "insertIspDtTmpSql");
			if (!StringUtil.isNullOrEmpty(insertIspDtTmpSql)) {
				insertIspDtTmpSql = insertIspDtTmpSql.replace("tmp_st_terminal_isp_analysis_dt_yyyymmdd", "tmp_st_terminal_isp_analysis_dt_" + ScriptTimeUtil.optime_yesday());
			}

			// 3.8 获取(DT)新用户Cube聚合SQL
			List<List<String>> splitDtNewUserList = ListUtils.splitList(IsplistWithRollup);
			for (List<String> listRollup : splitDtNewUserList) {
				StringBuffer strDtBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDtBuffer.append(" " + insertIspDtTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDtBuffer.append(" UNION ");
						}
						String selectDtTmpSql = super.getJsonConfigValue(configJsonPath, "selectIspDtTmpNewUserSql");
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
			List<List<String>> splitDtAllUserList = ListUtils.splitList(IsplistWithRollup);
			for (List<String> listRollup : splitDtAllUserList) {
				StringBuffer strDtBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDtBuffer.append(" " + insertIspDtTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDtBuffer.append(" UNION ");
						}
						String selectDtTmpSql = super.getJsonConfigValue(configJsonPath, "selectIspDtTmpAllUserSql");
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
			String insertDtDistinctSql = super.getJsonConfigValue(configJsonPath, "insertIspDtDistinctSql");
			if (!StringUtil.isNullOrEmpty(insertDtDistinctSql)) {
				insertDtDistinctSql = insertDtDistinctSql.replace("tmp_st_terminal_isp_analysis_dt_yyyymmdd", "tmp_st_terminal_isp_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertDtDistinctSql = insertDtDistinctSql.replace("st_terminal_isp_analysis_ds_yyyymmdd", "st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertDtDistinctSql = insertDtDistinctSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDtDistinctSql, this.getName());
			}

			// 清空临时表
			SqlUtils.sqlExecute(dataSource, "truncate table bi_tmp.tmp_st_terminal_isp_analysis_dt_" + ScriptTimeUtil.optime_yesday(), this.getName());
			// 数据迁移到临时表
			String insertIspDtWaiteSql = super.getJsonConfigValue(configJsonPath, "insertIspDtWaiteSql");
			if (!StringUtil.isNullOrEmpty(insertIspDtWaiteSql)) {
				insertIspDtWaiteSql = insertIspDtWaiteSql.replace("tmp_st_terminal_isp_analysis_dt_yyyymmdd", "tmp_st_terminal_isp_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertIspDtWaiteSql = insertIspDtWaiteSql.replace("st_terminal_isp_analysis_ds_yyyymmdd", "st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertIspDtWaiteSql = insertIspDtWaiteSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertIspDtWaiteSql, this.getName());
			}
			// 清空DT表
			SqlUtils.sqlExecute(dataSource, "delete from bi_st.st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday() + " where time_slice != 1", this.getName());
			// 聚合数据
			String insertIspDtSelectSql = super.getJsonConfigValue(configJsonPath, "insertIspDtSelectSql");
			if (!StringUtil.isNullOrEmpty(insertIspDtSelectSql)) {
				insertIspDtSelectSql = insertIspDtSelectSql.replace("tmp_st_terminal_isp_analysis_dt_yyyymmdd", "tmp_st_terminal_isp_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertIspDtSelectSql = insertIspDtSelectSql.replace("st_terminal_isp_analysis_ds_yyyymmdd", "st_terminal_isp_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertIspDtSelectSql = insertIspDtSelectSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertIspDtSelectSql, this.getName());
			}
			
			// 删除临时表
			if (!StringUtil.isNullOrEmpty(drop_isp_dt_tmp_yyyymmdd)) {
				drop_isp_dt_tmp_yyyymmdd = drop_isp_dt_tmp_yyyymmdd.replace("tmp_st_terminal_isp_analysis_dt_yyyymmdd", "tmp_st_terminal_isp_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_isp_dt_tmp_yyyymmdd, this.getName());
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
