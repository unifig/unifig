package etl.dispatch.java.st;

import java.io.IOException;
import java.sql.SQLException;
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
import etl.dispatch.util.StringUtil;

/**
 * 应用错误ST业务层数据汇总（错误日志量少,直接查询）
 * 
 *
 *
 */
@Service
public class St_ApplicationErrorReport extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_ApplicationErrorReport.class);
	private static final String configJsonPath = "classpath*:conf/json/st_applicationErrorReport.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.applicationErrorReport";
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
			
			
			// 第一步创建单天(Dm)目标表
			String target_sql = super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if (!StringUtil.isNullOrEmpty(target_sql)) {
				target_sql = target_sql.replace("st_error_analysis_dm_yyyymm", "st_error_analysis_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_sql, this.getName());
			}
			
			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("st_error_analysis_dm_yyyymm", "st_error_analysis_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			
			//判断是否创建分区
			/*boolean isHaveCreate = false;
			String selectPartitionSql = super.getJsonConfigValue(configJsonPath, "selectPartitionSql");
			if (!StringUtil.isNullOrEmpty(selectPartitionSql)) {
				selectPartitionSql = selectPartitionSql.replace("st_error_analysis_dm_yyyymm", "st_error_analysis_dm_" + optime_month);
				List<Map> partitionList = SqlUtils.querySqlList(dataSource, selectPartitionSql, this.getName());
				if(null!=partitionList && !partitionList.isEmpty()){
					for(Map partitionMap: partitionList){
						String partition = String.valueOf(partitionMap.get("part"));
						if(partition.equalsIgnoreCase("p"+DateUtil.getSysStrCurrentDate("yyyyMMdd", -1))){
							isHaveCreate = true;
							break;
						}
					}
				}
			}
			
			// 第二步 添加创建表分区
			String alert_sql = super.getJsonConfigValue(configJsonPath, "addPartitionSql");
			if (!StringUtil.isNullOrEmpty(alert_sql) && !isHaveCreate) {
				alert_sql = alert_sql.replaceAll("st_error_analysis_dm_yyyymm", "st_error_analysis_dm_" + optime_month);
				alert_sql = alert_sql.replace("${yyyymmdd}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1));
				SqlUtils.sqlExecute(dataSource, alert_sql, this.getName());
			}*/
			// 第三步插入单天(DS)查询数据
			String inselect_ds_sql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
			if (!StringUtil.isNullOrEmpty(inselect_ds_sql)) {
				inselect_ds_sql = inselect_ds_sql.replace("st_error_analysis_dm_yyyymm","st_error_analysis_dm_" + optime_month);
				inselect_ds_sql = inselect_ds_sql.replace("dw_error_report_dm_yyyymm", "dw_error_report_dm_" + optime_month);
				inselect_ds_sql = inselect_ds_sql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, inselect_ds_sql, this.getName());
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
