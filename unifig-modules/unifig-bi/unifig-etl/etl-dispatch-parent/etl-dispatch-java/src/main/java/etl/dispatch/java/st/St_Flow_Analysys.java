package etl.dispatch.java.st;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import etl.dispatch.util.StringUtil;

public class St_Flow_Analysys extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Flow_Analysys.class);
	private static final String configJsonPath = "classpath*:conf/json/st_flow_analysis.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.flow_analysis";
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
				create_dm_yyyymm = create_dm_yyyymm.replace("st_flow_analysis_dm_yyyymm", "st_flow_analysis_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, create_dm_yyyymm, this.getName());
			}
			
			// 删除DS临时表
			String drop_ds_tmp_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_ds_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_ds_tmp_yyyymmdd)) {
				drop_ds_tmp_yyyymmdd = drop_ds_tmp_yyyymmdd.replace("tmp_flow_analysis_dm_yyyymmdd", "tmp_flow_analysis_dm_" + optime_yesday);
				SqlUtils.sqlExecute(dataSource, drop_ds_tmp_yyyymmdd, this.getName());
			}
			
			// 创建DS临时表
			String create_tmp_dm_yyyymmdd = super.getJsonConfigValue(configJsonPath, "create_tmp_dm_yyyymmdd");
			if(!StringUtil.isNullOrEmpty(create_tmp_dm_yyyymmdd)){
				create_tmp_dm_yyyymmdd = create_tmp_dm_yyyymmdd.replace("tmp_flow_analysis_dm_yyyymmdd", "tmp_flow_analysis_dm_" + optime_yesday);
				SqlUtils.sqlExecute(dataSource, create_tmp_dm_yyyymmdd, this.getName());
			}
			
			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("st_area_analysis_dm_yyyymm", "st_area_analysis_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			
			// Cube聚合组合条件
			String[] criteriaArr = new String[] { "`hour`","`master`", "`app_plat_id`", "`app_version_id`", "`message_type`", "`network_type`"};
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr); 
			
			// 获取(DS)insertDsTmpSql
			String insertDsTmpSql = super.getJsonConfigValue(configJsonPath, "insertDsTmpSql");
			if (!StringUtil.isNullOrEmpty(insertDsTmpSql)) {
				insertDsTmpSql = insertDsTmpSql.replace("tmp_flow_analysis_dm_yyyymmdd", "tmp_flow_analysis_dm_" + optime_yesday);
			}
			
			// 插入stSQl
			String insertDsSql = super.getJsonConfigValue(configJsonPath, "insertDsSql");
			if (!StringUtil.isNullOrEmpty(insertDsSql)) {
				insertDsSql = insertDsSql.replace("st_flow_analysis_dm_yyyymm", "st_flow_analysis_dm_" + optime_month);
			}
			
			// 获取message(DS)sql 
			String selectMessageSql = super.getJsonConfigValue(configJsonPath, "selectMessageSql");
			if (!StringUtil.isNullOrEmpty(selectMessageSql)) {
				selectMessageSql = selectMessageSql.replace("bi_dw.dw_engine_message_log_dm_yyyymm", "bi_dw.dw_engine_message_log_dm_" + optime_month);
				selectMessageSql = selectMessageSql.replace(select_source_storeId, optime_yesday);
			}
			
			// 获取singleCall(DS)sql
			String selectSingleCallSql = super.getJsonConfigValue(configJsonPath, "selectSingleCallSql");
			if (!StringUtil.isNullOrEmpty(selectSingleCallSql)) {
				selectSingleCallSql = selectSingleCallSql.replace("bi_dw.dw_engine_singlecall_log_dm_yyyymm", "bi_dw.dw_engine_singlecall_log_dm_" + optime_month);
				selectSingleCallSql = selectSingleCallSql.replace(select_source_storeId, optime_yesday);
			}
			
			// 获取mutliCall(DS)sql
			String selectMutliCallSql = super.getJsonConfigValue(configJsonPath, "selectMutliCallSql");
			if (!StringUtil.isNullOrEmpty(selectMutliCallSql)) {
				selectMutliCallSql = selectMutliCallSql.replace("bi_dw.dw_engine_mutlicall_log_dm_yyyymm", "bi_dw.dw_engine_mutlicall_log_dm_" + optime_month);
				selectMutliCallSql = selectMutliCallSql.replace(select_source_storeId, optime_yesday);
			}
			
			// 插入st表
			StringBuffer insert_st_buffer = new StringBuffer(insertDsSql);
			
			StringBuffer insert_st_message_buffer = insert_st_buffer.append(selectMessageSql);
			if(!StringUtil.isNullOrEmpty(insert_st_message_buffer)){
				SqlUtils.sqlExecute(dataSource, insert_st_message_buffer.toString(), this.getName());
			}
			
			StringBuffer insert_st_single_buffer = insert_st_buffer.append(selectSingleCallSql);
			if(!StringUtil.isNullOrEmpty(insert_st_single_buffer)){
				SqlUtils.sqlExecute(dataSource, insert_st_single_buffer.toString(), this.getName());
			}
			
			StringBuffer insert_st_mutli_buffer = insert_st_buffer.append(selectMutliCallSql);
			if(!StringUtil.isNullOrEmpty(insert_st_mutli_buffer)){
				SqlUtils.sqlExecute(dataSource, insert_st_mutli_buffer.toString(), this.getName());
			}
			
			//获取st(DS)表
			String selectStSql = super.getJsonConfigValue(configJsonPath, "selectStSql");
			if (!StringUtil.isNullOrEmpty(selectStSql)) {
				selectStSql = selectStSql.replace("bi_st.st_flow_analysis_dm_yyyymm", "bi_st.st_flow_analysis_dm_" + optime_month);
				selectStSql = selectStSql.replace(select_source_storeId, optime_yesday);
			}
			// 获取流量cubeSQL
			List<List<String>> splitDsList = ListUtils.splitList(listWithRollup);
			for (List<String> listRollup : splitDsList) {
				StringBuffer strDsBuffer = new StringBuffer();
				if (null != listRollup && !listRollup.isEmpty()) {
					strDsBuffer.append(" " + insertDsTmpSql + " ");
					int i = 0;
					for (String withRollup : listRollup) {
						if (i > 0) {
							strDsBuffer.append(" UNION ");
						}
						strDsBuffer.append(" " + selectStSql + " ");
						strDsBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
						i++;
					}
				}
				// 插入(DS) Cube聚合数据
				if (!StringUtil.isNullOrEmpty(strDsBuffer)) {
					SqlUtils.sqlExecute(dataSource, strDsBuffer.toString(), this.getName());
				}
			}
			
			// 清空DS表
			SqlUtils.sqlExecute(dataSource, "delete from bi_st.st_flow_analysis_dm_" + optime_month + " where statis_date = " + optime_yesday, this.getName());
			
			// 获取(DS)去重Sql
			String insertDsDistinctSql = super.getJsonConfigValue(configJsonPath, "insertDmDistinctSql");
			if (!StringUtil.isNullOrEmpty(insertDsDistinctSql)) {
				insertDsDistinctSql = insertDsDistinctSql.replace("tmp_flow_analysis_dm_yyyymmdd", "tmp_flow_analysis_dm_" + optime_yesday);
				insertDsDistinctSql = insertDsDistinctSql.replace("st_flow_analysis_dm_yyyymm", "st_flow_analysis_dm_" + optime_month);
				insertDsDistinctSql = insertDsDistinctSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDsDistinctSql, this.getName());
			}
			
			// 删除临时表
			if (!StringUtil.isNullOrEmpty(drop_ds_tmp_yyyymmdd)) {
				drop_ds_tmp_yyyymmdd = drop_ds_tmp_yyyymmdd.replace("tmp_flow_analysis_dm_yyyymmdd", "tmp_flow_analysis_dm_" + optime_yesday);
				SqlUtils.sqlExecute(dataSource, drop_ds_tmp_yyyymmdd, this.getName());
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
