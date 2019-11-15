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
 * 活跃用户 统计
 * 
 *
 *
 */
@Service
public class St_Active_User extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Active_User.class);
	private static final String configJsonPath = "classpath*:conf/json/st_active_user.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.active_user";
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
			String url = dataSource.getConnection().getMetaData().getURL();
			String substring = url.substring(13, 22);
			
			
			// 创建(DM)表
			String target_dm_sql = super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if(!StringUtil.isNullOrEmpty(target_dm_sql)){
				target_dm_sql = target_dm_sql.replace("st_active_user_dm_yyyymm", "st_active_user_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_dm_sql, this.getName());
			}
			
			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("st_active_user_dm_yyyymm", "st_active_user_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}",optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			
			/**
			 * 单日统计>>>>>> st_active_user_ds_yyyymmdd
			 */
			// 创建单日(DS)目标表
			String target_ds_sql = super.getJsonConfigValue(configJsonPath, "create_ds_yyyymmdd");
			if (!StringUtil.isNullOrEmpty(target_ds_sql)) {
				target_ds_sql = target_ds_sql.replace("st_active_user_ds_yyyymmdd", "st_active_user_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, target_ds_sql, this.getName());
			}
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
			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`","`app_version_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 获取(DS)InsertTmpSQL
			String insertTmpDsSql = super.getJsonConfigValue(configJsonPath, "insertTmpDsSql", false);

			// 获取日活跃查询SQL
			String selectTmpSignUserDsSql = super.getJsonConfigValue(configJsonPath, "selectTmpSignUserDsSql", false);
			if (!StringUtil.isNullOrEmpty(selectTmpSignUserDsSql)) {
				selectTmpSignUserDsSql = selectTmpSignUserDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}

			// 获取(DS)日活跃查询聚合SQL
			StringBuffer strDayDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strDayDsBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strDayDsBuffer.append(" UNION ");
					}
					strDayDsBuffer.append(" " + selectTmpSignUserDsSql + " ");
					strDayDsBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)日活跃查询Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strDayDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strDayDsBuffer.toString(), this.getName());
			}

			// 获取周活跃查询SQL
			String selectTmpWeekUserDsSql = super.getJsonConfigValue(configJsonPath, "selectTmpWeekUserDsSql", false);
			if (!StringUtil.isNullOrEmpty(selectTmpWeekUserDsSql)) {
				selectTmpWeekUserDsSql = selectTmpWeekUserDsSql.replace("${statis_date}", ScriptTimeUtil.optime_yesday());
				selectTmpWeekUserDsSql = selectTmpWeekUserDsSql.replace("${min_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -7));
				selectTmpWeekUserDsSql = selectTmpWeekUserDsSql.replace("${max_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1));
			}

			// 获取(DS)周活跃查询 Cube聚合SQL
			StringBuffer strWeekDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strWeekDsBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strWeekDsBuffer.append(" UNION ");
					}
					strWeekDsBuffer.append(" " + selectTmpWeekUserDsSql + " ");
					strWeekDsBuffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)周活跃查询 Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strWeekDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strWeekDsBuffer.toString(), this.getName());
			}
			
			// 获取月活跃查询SQL
			String selectTmpMonthUserDsSql = super.getJsonConfigValue(configJsonPath, "selectTmpMonthUserDsSql", false);
			if (!StringUtil.isNullOrEmpty(selectTmpMonthUserDsSql)) {
				selectTmpMonthUserDsSql = selectTmpMonthUserDsSql.replace("${statis_date}", ScriptTimeUtil.optime_yesday());
				selectTmpMonthUserDsSql = selectTmpMonthUserDsSql.replace("${min_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -30));
				selectTmpMonthUserDsSql = selectTmpMonthUserDsSql.replace("${max_id}", DateUtil.getSysStrCurrentDate("yyyyMMdd", -1));
			}

			// 获取(DS)月活跃查询 Cube聚合SQL
			StringBuffer strMonthDsBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strMonthDsBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strMonthDsBuffer.append(" UNION ");
					}
					strMonthDsBuffer.append(" " + selectTmpMonthUserDsSql + " ");
					strMonthDsBuffer.append(" group by " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)月活跃查询 Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strMonthDsBuffer)) {
				SqlUtils.sqlExecute(dataSource, strMonthDsBuffer.toString(), this.getName());
			}

			// 插入(DS) Cube聚合数据
			String insertDsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDsSelectSql", false);
			if (!StringUtil.isNullOrEmpty(insertDsSelectSql)) {
				insertDsSelectSql = insertDsSelectSql.replace("st_active_user_ds_yyyymmdd", "st_active_user_ds_" + ScriptTimeUtil.optime_yesday());
				insertDsSelectSql = insertDsSelectSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insertDsSelectSql, this.getName());
			}
			// 删除单日临时表
			if (!StringUtil.isNullOrEmpty(drop_tmp_ds_yyyymmdd)) {
				SqlUtils.sqlExecute(dataSource, drop_tmp_ds_yyyymmdd, this.getName());
			}
			
			// 插入(DM)表
			String insert_dm_sql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
			if(!StringUtil.isNullOrEmpty(insert_dm_sql)){
				insert_dm_sql = insert_dm_sql.replace("st_active_user_dm_yyyymm", "st_active_user_dm_" + optime_month);
				insert_dm_sql = insert_dm_sql.replace("st_active_user_ds_yyyymmdd", "st_active_user_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, insert_dm_sql, this.getName());
			}
			
			// 删除(DS)表
			String drop_ds_sql = super.getJsonConfigValue(configJsonPath, "drop_active_user_ds");
			if(!StringUtil.isNullOrEmpty(drop_ds_sql)){
				drop_ds_sql = drop_ds_sql.replace("st_active_user_ds_yyyymmdd", "st_active_user_ds_" + ScriptTimeUtil.optime_yesday());
				SqlUtils.sqlExecute(dataSource, drop_ds_sql, this.getName());
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
