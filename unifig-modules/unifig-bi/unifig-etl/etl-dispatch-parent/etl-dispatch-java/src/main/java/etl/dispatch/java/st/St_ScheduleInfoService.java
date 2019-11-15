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
import etl.dispatch.util.StringUtil;

/**
 * 
 * @Description: 日程管理数据汇总
 * @author: ylc
 */
@Service
public class St_ScheduleInfoService extends AbstractScript{
	private static Logger logger = LoggerFactory.getLogger(St_ScheduleInfoService.class);
	private static final String configJsonPath = "classpath*:conf/json/st_schedule_info_service.json";
	
	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.St_ScheduleInfoService";
	}

	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		logger.info(IpUtils.getIPAddress()+"---> [" + St_ScheduleInfoService.class.getCanonicalName() + "]");
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
		}

		try {
			String optime_hour_yesday = ScriptTimeUtil.optime_yesday();
			String optime_month = optime_hour_yesday.substring(0, optime_hour_yesday.length() - 2);

			// 创建(DM)表
			String target_dm_sql = super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if (!StringUtil.isNullOrEmpty(target_dm_sql)) {
				target_dm_sql = target_dm_sql.replace("st_schedule_info_dm_yyyymm", "st_schedule_info_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_dm_sql, this.getName());
			}
			
			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("st_schedule_info_dm_yyyymm", "st_schedule_info_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_hour_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			// 删除临时表
			String drop_tmp_ds_yyyymmdd = super.getJsonConfigValue(configJsonPath, "drop_tmp_ds_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(drop_tmp_ds_yyyymmdd)) {
				SqlUtils.sqlExecute(dataSource, drop_tmp_ds_yyyymmdd, this.getName());
			}
			
			// 创建临时表
			String create_tmp = super.getJsonConfigValue(configJsonPath, "create_tmp_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(target_dm_sql)) {
				SqlUtils.sqlExecute(dataSource, create_tmp, this.getName());
			}
			
			// Cube聚合组合条件
			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`","`app_id`", "`app_version_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);
			
			// 获取(DS)InsertTmpSQL
			String insertTmpDsSql = super.getJsonConfigValue(configJsonPath, "insertTmpDsSql");
			
			// 获取(add)添加日程sql
			String selectAddDsSql = super.getJsonConfigValue(configJsonPath, "selectAddDsSql");
			if (!StringUtil.isNullOrEmpty(selectAddDsSql)) {
				selectAddDsSql = selectAddDsSql.replace("dw_schedule_dm_yyyymm", "dw_schedule_dm_" + optime_month);
				selectAddDsSql = selectAddDsSql.replace("${statis_date}", optime_hour_yesday);
			}
			
			// 获取(complete)完成日程sql
			String selectCompleteDsSql = super.getJsonConfigValue(configJsonPath, "selectCompleteDsSql");
			if (!StringUtil.isNullOrEmpty(selectCompleteDsSql)) {
				selectCompleteDsSql = selectCompleteDsSql.replace("dw_schedule_dm_yyyymm", "dw_schedule_dm_" + optime_month);
				selectCompleteDsSql = selectCompleteDsSql.replace("${statis_date}", optime_hour_yesday);
			}
			
			// 获取(share)分享日程sql
			String selectShareDsSql = super.getJsonConfigValue(configJsonPath, "selectShareDsSql");
			if (!StringUtil.isNullOrEmpty(selectShareDsSql)) {
				selectShareDsSql = selectShareDsSql.replace("dw_schedule_dm_yyyymm", "dw_schedule_dm_" + optime_month);
				selectShareDsSql = selectShareDsSql.replace("${statis_date}", optime_hour_yesday);
			}
			
			// 获取(user)创建人数sql
			String selectUserDsSql = super.getJsonConfigValue(configJsonPath, "selectUserDsSql");
			if (!StringUtil.isNullOrEmpty(selectUserDsSql)) {
				selectUserDsSql = selectUserDsSql.replace("dw_schedule_dm_yyyymm", "dw_schedule_dm_" + optime_month);
				selectUserDsSql = selectUserDsSql.replace("${statis_date}", optime_hour_yesday);
			}
			
			// 获取(add)添加日程聚合SQL
			StringBuffer addBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				addBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						addBuffer.append(" UNION ");
					}
					addBuffer.append(" " + selectAddDsSql + " ");
					addBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)添加日程聚合数据
			if (!StringUtil.isNullOrEmpty(addBuffer)) {
				SqlUtils.sqlExecute(dataSource, addBuffer.toString(), this.getName());
			}
			
			// 获取(complete)完成日程聚合SQL
			StringBuffer completeBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				completeBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						completeBuffer.append(" UNION ");
					}
					completeBuffer.append(" " + selectCompleteDsSql + " ");
					completeBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)完成日程聚合数据
			if (!StringUtil.isNullOrEmpty(completeBuffer)) {
				SqlUtils.sqlExecute(dataSource, completeBuffer.toString(), this.getName());
			}
			
			// 获取(share)分享日程聚合SQL
			StringBuffer shareBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				shareBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						shareBuffer.append(" UNION ");
					}
					shareBuffer.append(" " + selectShareDsSql + " ");
					shareBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)分享日程聚合数据
			if (!StringUtil.isNullOrEmpty(shareBuffer)) {
				SqlUtils.sqlExecute(dataSource, shareBuffer.toString(), this.getName());
			}
			
			// 获取(user)创建人数聚合SQL
			StringBuffer historyBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				historyBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						historyBuffer.append(" UNION ");
					}
					historyBuffer.append(" " + selectUserDsSql + " ");
					historyBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)创建人数聚合数据
			if (!StringUtil.isNullOrEmpty(historyBuffer)) {
				SqlUtils.sqlExecute(dataSource, historyBuffer.toString(), this.getName());
			}
			
			// 插入(DS) Cube聚合数据
			String insertDsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
			if (!StringUtil.isNullOrEmpty(insertDsSelectSql)) {
				insertDsSelectSql = insertDsSelectSql.replace("st_schedule_info_dm_yyyymm", "st_schedule_info_dm_" + optime_month);
				insertDsSelectSql = insertDsSelectSql.replace("${statis_date}", optime_hour_yesday);
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
		
	}
}
