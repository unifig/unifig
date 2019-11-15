package etl.dispatch.java.st;

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
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.StringUtil;

/**
 * 概览 统计
 * 
 *
 *
 */
@Service
public class St_Overview_Analysis extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Overview_Analysis.class);
	private static final String configJsonPath = "classpath*:conf/json/st_overview_analysis.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.overview_analysis";
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
			
			// 创建(MDT)表
			String target_mdt_sql = super.getJsonConfigValue(configJsonPath, "create_mdt_yyyymm");
			if(!StringUtil.isNullOrEmpty(target_mdt_sql)){
				target_mdt_sql = target_mdt_sql.replace("st_overview_analysis_dt_yyyymm", "st_overview_analysis_dt_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_mdt_sql, this.getName());
			}
			
			// 创建(DM)表
			String target_dm_sql = super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if(!StringUtil.isNullOrEmpty(target_dm_sql)){
				target_dm_sql = target_dm_sql.replace("st_overview_analysis_dm_yyyymm", "st_overview_analysis_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_dm_sql, this.getName());
   			}
			
			// 支持重跑，删除dm当天数据
			String delete_yes_dmDate = super.getJsonConfigValue(configJsonPath, "delete_yes_dmDate");
			if(!StringUtil.isNullOrEmpty(delete_yes_dmDate)){
				delete_yes_dmDate = delete_yes_dmDate.replace("st_overview_analysis_dm_yyyymm", "st_overview_analysis_dm_" + optime_month);
				delete_yes_dmDate = delete_yes_dmDate.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_dmDate, this.getName());
			}
			
			// 支持重跑，删除mdt当天数据
			String delete_yes_mdtDate = super.getJsonConfigValue(configJsonPath, "delete_yes_mdtDate");
			if(!StringUtil.isNullOrEmpty(delete_yes_mdtDate)){
				delete_yes_mdtDate = delete_yes_mdtDate.replace("st_overview_analysis_dt_yyyymm", "st_overview_analysis_dt_" + optime_month);
				delete_yes_mdtDate = delete_yes_mdtDate.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_mdtDate, this.getName());
			}
			
			/**
			 * 日累计统计>>>>>> st_overview_analysis_dt_yyyymmdd
			 */
			// 查询所有用户数
			List<Map> allUserCountMap = null;
			String select_dt_count = super.getJsonConfigValue(configJsonPath, "select_dt_usercount");
			if (!StringUtil.isNullOrEmpty(select_dt_count)) {
				select_dt_count = select_dt_count.replace("ods_user_info_dm_yyyymm", "ods_user_info_dm_" + optime_month);
 				allUserCountMap = SqlUtils.querySqlList(dataSource, select_dt_count, this.getName());
			}
			// 查询最近一周用户数据
			List<Map> weekUserCountMap = null;
			String select_dt_Wusercount = super.getJsonConfigValue(configJsonPath, "select_dt_WMusercount");
			if (!StringUtil.isNullOrEmpty(select_dt_Wusercount)) {
				select_dt_Wusercount = select_dt_Wusercount.replace("${min_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -7));
				select_dt_Wusercount = select_dt_Wusercount.replace("${max_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1));
				weekUserCountMap = SqlUtils.querySqlList(dataSource, select_dt_Wusercount, this.getName());
			}
			// 查询最近一月用户数据
			List<Map> monthUserCountMap = null;
			String select_dt_Musercount = super.getJsonConfigValue(configJsonPath, "select_dt_WMusercount");
			if (!StringUtil.isNullOrEmpty(select_dt_Musercount)) {
				select_dt_Musercount = select_dt_Musercount.replace("${min_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -30));
				select_dt_Musercount = select_dt_Musercount.replace("${max_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1));
				monthUserCountMap = SqlUtils.querySqlList(dataSource, select_dt_Musercount, this.getName());
			}
			// 插入汇总数据
			String insertDtValuesSql = super.getJsonConfigValue(configJsonPath, "insertMDtValuesSql");
			if (!StringUtil.isNullOrEmpty(insertDtValuesSql)) {
				insertDtValuesSql = insertDtValuesSql.replace("st_overview_analysis_dt_yyyymm", "st_overview_analysis_dt_" + optime_month);
				insertDtValuesSql = insertDtValuesSql.replace("${statis_date}", ScriptTimeUtil.optime_yesday());
				insertDtValuesSql = insertDtValuesSql.replace("${user_count}", null == allUserCountMap ? "0" : String.valueOf(allUserCountMap.get(0).get("user_count")));
				insertDtValuesSql = insertDtValuesSql.replace("${week_user_count}", null == weekUserCountMap ? "0" : String.valueOf(weekUserCountMap.get(0).get("user_count")));
				insertDtValuesSql = insertDtValuesSql.replace("${month_user_count}", null == monthUserCountMap ? "0" : String.valueOf(monthUserCountMap.get(0).get("user_count")));
				SqlUtils.sqlExecute(dataSource, insertDtValuesSql, this.getName());
			}
			
			/**
			 * 单日统计>>>>>> st_overview_analysis_dt_yyyymmdd
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
			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 获取(DS)InsertTmpSQL
			String insertTmpDsSql = super.getJsonConfigValue(configJsonPath, "insertTmpDsSql");

			// 获取(DS)新用户SQL
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
					strNewDsBuffer.append(" group by `statis_date`, " + withRollup.substring(1) + "  with rollup ");
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
			
			//获取(DM)Remains User Temp Sql
			String selectTmpRemainsUserDsSql = super.getJsonConfigValue(configJsonPath, "selectTmpRemainsUserDsSql");
			if(!StringUtil.isNullOrEmpty(selectTmpRemainsUserDsSql)){
				selectTmpRemainsUserDsSql = selectTmpRemainsUserDsSql.replace("${retains_day}",DateUtil.getSysStrCurrentDate("yyyyMMdd", -1 * 2));
				selectTmpRemainsUserDsSql = selectTmpRemainsUserDsSql.replace("${yesterday}", ScriptTimeUtil.optime_yesday());
			}
			
			//获取(DS)Remains user Cube聚合sql
			StringBuffer strRemainsDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strRemainsDsBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strRemainsDsBuffer.append(" UNION ");
					}
					strRemainsDsBuffer.append(" " + selectTmpRemainsUserDsSql + " ");
					strRemainsDsBuffer.append(" group by `statis_date`, " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			
			// 插入(DS)Remains user Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strRemainsDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strRemainsDsBuffer.toString(), this.getName());
			}
			
			// 插入(DM) Cube聚合数据
			String insertDsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
			if (!StringUtil.isNullOrEmpty(insertDsSelectSql)) {
				insertDsSelectSql = insertDsSelectSql.replace("st_overview_analysis_dm_yyyymm", "st_overview_analysis_dm_" + optime_month);
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
