package etl.dispatch.java.dwd;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

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
 * 
 * @Description: 日程管理数据增强
 * @author: ylc
 */
@Service
public class Dwd_ScheduleInfoService extends AbstractScript{
	private static Logger logger = LoggerFactory.getLogger(Dwd_ScheduleInfoService.class);
	private static final String configJsonPath = "classpath*:conf/json/dwd_schedule_info_service.json";
	private DataSourcePool dataSourcePool;
	private Optional<Integer> saveDays = Optional.empty();
	
	public String getName() {
		return "dwd.schedule_info_service";
	}
	
	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		logger.info(IpUtils.getIPAddress()+"---> [" + Dwd_ScheduleInfoService.class.getCanonicalName() + "]");
		Map<String, Object> paramMap = scriptBean.getParamMap();
		DataSource dataSource = null;
		if (null != paramMap && !paramMap.isEmpty()) {
			Map<String, Object> dataSourceMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_DATASOURCE);
			if (null != dataSourceMap && !dataSourceMap.isEmpty()) {
				dataSourcePool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				dataSource = dataSourcePool.getDataSource(dataSourceMap);
				if (null == dataSource) {
					super.callback(false, "数据源获取失败; dataSource config:" + JSON.toJSONString(dataSourceMap), scriptBean, callback);
				}
			}
			saveDays = Optional.ofNullable((Integer)paramMap.get(CommonConstants.PROP_PARAMS_SAVEDAYS));
		}
		
		try {
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			String optime_month = optime_yesday.substring(0 , optime_yesday.length() - 2);
			
			// 第一步创建目标表
			String target_sql = super.getJsonConfigValue(configJsonPath, "create_dm_schedule_yyyymm");
			if (!StringUtil.isNullOrEmpty(target_sql)) {
				target_sql = target_sql.replace("dwd_schedule_dm_yyyymm", "dwd_schedule_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_sql, this.getName());
			} else {
				logger.error("path " + configJsonPath + "; create target table, get sql with key'templateTableSql' config value is null ");
			}
			
			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("dwd_schedule_dm_yyyymm", "dwd_schedule_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			
			// 第二步插入查询数据
			String inselect_sql = super.getJsonConfigValue(configJsonPath, "insertSelectSql");
			if (!StringUtil.isNullOrEmpty(inselect_sql)) {
				inselect_sql = inselect_sql.replace("dwd_schedule_dm_yyyymm", "dwd_schedule_dm_" + optime_month);
				inselect_sql = inselect_sql.replace("ods_schedule_dm_yyyymm", "ods_schedule_dm_" + optime_month);
				inselect_sql = inselect_sql.replace("${statis_date}", ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, inselect_sql, this.getName());
			} else {
				logger.error("path " + configJsonPath + "; inset target table, get sql with key 'insertSelectSql' config value is null ");
			}
			
			// 删除历史数据
			String delete_table = super.getJsonConfigValue(configJsonPath, "delete_table");
			String delete_date = super.getJsonConfigValue(configJsonPath, "delete_date");
			if (!StringUtil.isNullOrEmpty(delete_table) && !StringUtil.isNullOrEmpty(delete_date) && saveDays.isPresent()) {
				String delSql = super.getDelHistorySql(delete_table, delete_date, "dwd_schedule_dm_", saveDays.get());
				if(!StringUtil.isNullOrEmpty(delSql)){
					SqlUtils.sqlExecute(dataSource, delSql, this.getName());
				}
			}else{
				super.callback(false, "历史数据保留配置异常，saveDays is null;", scriptBean, callback);
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
