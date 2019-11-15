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
 * 访问用户地域分析
 * 
 *
 */
@Service
public class St_Area_Analysis extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Area_Analysis.class);
	private static final String configJsonPath = "classpath*:conf/json/st_area_analysis.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.area_analysis";
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
			
			// 创建单月(DM)表
			String create_dm_yyyymm = super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if(!StringUtil.isNullOrEmpty(create_dm_yyyymm)){
				create_dm_yyyymm = create_dm_yyyymm.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, create_dm_yyyymm, this.getName());
			}
			
			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			
			/**
			 * <<<<单日数据统计>>>>
			 */
			// 删除临时表
			String drop_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_ds_tmp_yyyymmdd)) {
				drop_ds_tmp_yyyymmdd = drop_ds_tmp_yyyymmdd.replace("tmp_st_area_analysis_ds_yyyymmdd", "tmp_st_area_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_ds_tmp_yyyymmdd, this.getName());
			}
			// 创建临时表
			String create_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(create_ds_tmp_yyyymmdd)) {
				create_ds_tmp_yyyymmdd = create_ds_tmp_yyyymmdd.replace("tmp_st_area_analysis_ds_yyyymmdd", "tmp_st_area_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, create_ds_tmp_yyyymmdd, this.getName());
			}
			// Cube聚合组合条件
			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`", "`app_version_id`", "`country_id`", "`region_id`", "`city_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 获取(DS)insertDsSql
			String insertDsTmpSql = super.getJsonConfigValue(configJsonPath, "insertDsTmpSql");
			if (!StringUtil.isNullOrEmpty(insertDsTmpSql)) {
				insertDsTmpSql = insertDsTmpSql.replace("tmp_st_area_analysis_ds_yyyymmdd", "tmp_st_area_analysis_ds_" + ScriptTimeUtil.optime_yesday());
			}
			// 获取(DS)新用户selectDsSql
			String selectDsTmpNewUserSql = super.getJsonConfigValue(configJsonPath, "selectDsTmpNewUserSql");
			if (!StringUtil.isNullOrEmpty(selectDsTmpNewUserSql)) {
				selectDsTmpNewUserSql = selectDsTmpNewUserSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				selectDsTmpNewUserSql = selectDsTmpNewUserSql.replace("${is_new}", "0");//0-新用户 1-老用户
			}
			// 获取(DS)所有用户selectDsSql
			String selectDsTmpAllUserSql = super.getJsonConfigValue(configJsonPath, "selectDsTmpAllUserSql");
			if (!StringUtil.isNullOrEmpty(selectDsTmpAllUserSql)) {
				selectDsTmpAllUserSql = selectDsTmpAllUserSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取(DS)新用户 Cube聚合SQL
			List<List<String>> splitDsNewUserList = ListUtils.splitList(listWithRollup);
			for (List<String> listRollup : splitDsNewUserList) {
				StringBuffer strDsBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDsBuffer.append(" " + insertDsTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDsBuffer.append(" UNION ");
						}
						strDsBuffer.append(" " + selectDsTmpNewUserSql + " ");
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
			List<List<String>> splitDsAllUserList = ListUtils.splitList(listWithRollup);
			for (List<String> listRollup : splitDsAllUserList) {
				StringBuffer strDsBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDsBuffer.append(" " + insertDsTmpSql + " ");
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
			String insertDsDistinctSql = super.getJsonConfigValue(configJsonPath, "insertDmDistinctSql");
			if (!StringUtil.isNullOrEmpty(insertDsDistinctSql)) {
				insertDsDistinctSql = insertDsDistinctSql.replace("tmp_st_area_analysis_ds_yyyymmdd", "tmp_st_area_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertDsDistinctSql = insertDsDistinctSql.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				insertDsDistinctSql = insertDsDistinctSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDsDistinctSql, this.getName());
			}
			// 清空临时表
			SqlUtils.sqlExecute(dataSource, "truncate table bi_tmp.tmp_st_area_analysis_ds_" + ScriptTimeUtil.optime_yesday(), this.getName());
			// 数据迁移到临时表
			String insertDsWaiteSql = super.getJsonConfigValue(configJsonPath, "insertDsWaiteSql");
			if (!StringUtil.isNullOrEmpty(insertDsWaiteSql)) {
				insertDsWaiteSql = insertDsWaiteSql.replace("tmp_st_area_analysis_ds_yyyymmdd", "tmp_st_area_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertDsWaiteSql = insertDsWaiteSql.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				insertDsWaiteSql = insertDsWaiteSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDsWaiteSql, this.getName());
			}
			// 清空DS表
			SqlUtils.sqlExecute(dataSource, "delete from bi_st.st_area_analysis_dm_" + optime_month + " where statis_date = " + ScriptTimeUtil.optime_yesday(), this.getName());
			// 聚合数据
			String insertDsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDsSelectSql");
			if (!StringUtil.isNullOrEmpty(insertDsSelectSql)) {
				insertDsSelectSql = insertDsSelectSql.replace("tmp_st_area_analysis_ds_yyyymmdd", "tmp_st_area_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				insertDsSelectSql = insertDsSelectSql.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				insertDsSelectSql = insertDsSelectSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDsSelectSql, this.getName());
			}
			// 删除临时表
			if (!StringUtil.isNullOrEmpty(drop_ds_tmp_yyyymmdd)) {
				drop_ds_tmp_yyyymmdd = drop_ds_tmp_yyyymmdd.replace("tmp_st_area_analysis_ds_yyyymmdd", "tmp_st_area_analysis_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_ds_tmp_yyyymmdd, this.getName());
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
			String drop_dt_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_dt_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_dt_tmp_yyyymmdd)) {
				drop_dt_tmp_yyyymmdd = drop_dt_tmp_yyyymmdd.replace("tmp_st_area_analysis_dt_yyyymmdd", "tmp_st_area_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_dt_tmp_yyyymmdd, this.getName());
			}
			// 创建临时表
			String create_dt_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_dt_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(create_dt_tmp_yyyymmdd)) {
				create_dt_tmp_yyyymmdd = create_dt_tmp_yyyymmdd.replace("tmp_st_area_analysis_dt_yyyymmdd", "tmp_st_area_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, create_dt_tmp_yyyymmdd, this.getName());
			}

			// 3.7获取(DT)InsertSQL
			String insertDtTmpSql = super.getJsonConfigValue(configJsonPath, "insertDtTmpSql");
			if (!StringUtil.isNullOrEmpty(insertDtTmpSql)) {
				insertDtTmpSql = insertDtTmpSql.replace("tmp_st_area_analysis_dt_yyyymmdd", "tmp_st_area_analysis_dt_" + ScriptTimeUtil.optime_yesday());
			}

			// 3.8 获取(DT)新用户Cube聚合SQL
			List<List<String>> splitDtNewUserList = ListUtils.splitList(listWithRollup);
			for (List<String> listRollup : splitDtNewUserList) {
				StringBuffer strDtBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDtBuffer.append(" " + insertDtTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDtBuffer.append(" UNION ");
						}
						String selectDtTmpSql = super.getJsonConfigValue(configJsonPath, "selectDtTmpNewUserSql");
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
			List<List<String>> splitDtAllUserList = ListUtils.splitList(listWithRollup);
			for (List<String> listRollup : splitDtAllUserList) {
				StringBuffer strDtBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDtBuffer.append(" " + insertDtTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDtBuffer.append(" UNION ");
						}
						String selectDtTmpSql = super.getJsonConfigValue(configJsonPath, "selectDtTmpAllUserSql");
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
			String insertDtDistinctSql = super.getJsonConfigValue(configJsonPath, "insertDtDistinctSql");
			if (!StringUtil.isNullOrEmpty(insertDtDistinctSql)) {
				insertDtDistinctSql = insertDtDistinctSql.replace("tmp_st_area_analysis_dt_yyyymmdd", "tmp_st_area_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertDtDistinctSql = insertDtDistinctSql.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				insertDtDistinctSql = insertDtDistinctSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDtDistinctSql, this.getName());
			}

			// 清空临时表
			SqlUtils.sqlExecute(dataSource, "truncate table bi_tmp.tmp_st_area_analysis_dt_" + ScriptTimeUtil.optime_yesday(), this.getName());
			// 数据迁移到临时表
			String insertDtWaiteSql = super.getJsonConfigValue(configJsonPath, "insertDtWaiteSql");
			if (!StringUtil.isNullOrEmpty(insertDtWaiteSql)) {
				insertDtWaiteSql = insertDtWaiteSql.replace("tmp_st_area_analysis_dt_yyyymmdd", "tmp_st_area_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertDtWaiteSql = insertDtWaiteSql.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				insertDtWaiteSql = insertDtWaiteSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDtWaiteSql, this.getName());
			}
			// 清空DT表
			SqlUtils.sqlExecute(dataSource, "delete from bi_st.st_area_analysis_dm_" + optime_month + " where time_slice != 1 and statis_date = " + ScriptTimeUtil.optime_yesday(), this.getName());
			// 聚合数据
			String insertDtSelectSql = super.getJsonConfigValue(configJsonPath, "insertDtSelectSql");
			if (!StringUtil.isNullOrEmpty(insertDtSelectSql)) {
				insertDtSelectSql = insertDtSelectSql.replace("tmp_st_area_analysis_dt_yyyymmdd", "tmp_st_area_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				insertDtSelectSql = insertDtSelectSql.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				insertDtSelectSql = insertDtSelectSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDtSelectSql, this.getName());
			}
			// 删除临时表
			if (!StringUtil.isNullOrEmpty(drop_dt_tmp_yyyymmdd)) {
				drop_dt_tmp_yyyymmdd = drop_dt_tmp_yyyymmdd.replace("tmp_st_area_analysis_dt_yyyymmdd", "tmp_st_area_analysis_dt_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_dt_tmp_yyyymmdd, this.getName());
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
