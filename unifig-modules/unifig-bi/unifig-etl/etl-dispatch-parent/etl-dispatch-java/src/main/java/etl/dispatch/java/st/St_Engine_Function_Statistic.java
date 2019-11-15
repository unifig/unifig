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

@Service
public class St_Engine_Function_Statistic extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(St_Engine_Function_Statistic.class);
	private static final String configJsonPath = "classpath*:conf/json/st_engine_function_statistic.json";
	private static final String select_source_storeId = "${store_id}";

	private DataSourcePool dataSourcePool;

	public String getName() {
		return "st.engine_function_statistic";
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

			// 创建(DM)表
			String target_dm_sql = super.getJsonConfigValue(configJsonPath, "create_dm_yyyymm");
			if (!StringUtil.isNullOrEmpty(target_dm_sql)) {
				target_dm_sql = target_dm_sql.replace("st_function_statistic_dm_yyyymm", "st_function_statistic_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_dm_sql, this.getName());
			}

			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("st_function_statistic_dm_yyyymm", "st_function_statistic_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}

			/**
			 * 单日统计>>>>>> st_function_statistic_ds_yyyymmdd
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
			String[] criteriaArr = new String[] { "`hour`", "`app_plat_id`", "`app_version_id`" };
			List<String> listWithRollup = super.getCriteriaArr(criteriaArr);

			// 获取(DS)InsertTmpSQL
			String insertTmpDsSql = super.getJsonConfigValue(configJsonPath, "insertTmpDsSql");

			// 获取单聊文本统计SQL
			String selectSingle_textDsSql = super.getJsonConfigValue(configJsonPath, "selectSingle_textDsSql");
			if (!StringUtil.isNullOrEmpty(selectSingle_textDsSql)) {
				selectSingle_textDsSql = selectSingle_textDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectSingle_textDsSql = selectSingle_textDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}

			// 获取单聊图片统计SQL
			String selectSingle_pictureDsSql = super.getJsonConfigValue(configJsonPath, "selectSingle_pictureDsSql");
			if (!StringUtil.isNullOrEmpty(selectSingle_pictureDsSql)) {
				selectSingle_pictureDsSql = selectSingle_pictureDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectSingle_pictureDsSql = selectSingle_pictureDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}

			// 获取单聊文件统计SQL
			String selectSingle_fileDsSql = super.getJsonConfigValue(configJsonPath, "selectSingle_fileDsSql");
			if (!StringUtil.isNullOrEmpty(selectSingle_fileDsSql)) {
				selectSingle_fileDsSql = selectSingle_fileDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectSingle_fileDsSql = selectSingle_fileDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}

			// 获取单聊短语音统计SQL
			String selectSingle_short_voiceDsSql = super.getJsonConfigValue(configJsonPath, "selectSingle_short_voiceDsSql");
			if (!StringUtil.isNullOrEmpty(selectSingle_short_voiceDsSql)) {
				selectSingle_short_voiceDsSql = selectSingle_short_voiceDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectSingle_short_voiceDsSql = selectSingle_short_voiceDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取单聊短视频统计SQL
			String selectSingle_short_videoDsSql = super.getJsonConfigValue(configJsonPath, "selectSingle_short_videoDsSql");
			if (!StringUtil.isNullOrEmpty(selectSingle_short_videoDsSql)) {
				selectSingle_short_videoDsSql = selectSingle_short_videoDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectSingle_short_videoDsSql = selectSingle_short_videoDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取单聊语音通话统计SQL
			String selectSingle_voiceDsSql = super.getJsonConfigValue(configJsonPath, "selectSingle_voiceDsSql");
			if (!StringUtil.isNullOrEmpty(selectSingle_voiceDsSql)) {
				selectSingle_voiceDsSql = selectSingle_voiceDsSql.replace("dw_engine_singlecall_log_dm_yyyymm", "dw_engine_singlecall_log_dm_" + optime_month);
				selectSingle_voiceDsSql = selectSingle_voiceDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取单聊视频通话统计SQL
			String selectSingle_videoDsSql = super.getJsonConfigValue(configJsonPath, "selectSingle_videoDsSql");
			if (!StringUtil.isNullOrEmpty(selectSingle_videoDsSql)) {
				selectSingle_videoDsSql = selectSingle_videoDsSql.replace("dw_engine_singlecall_log_dm_yyyymm", "dw_engine_singlecall_log_dm_" + optime_month);
				selectSingle_videoDsSql = selectSingle_videoDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}

			// 获取群聊文本统计SQL
			String selectGroup_textDsSql = super.getJsonConfigValue(configJsonPath, "selectGroup_textDsSql");
			if (!StringUtil.isNullOrEmpty(selectGroup_textDsSql)) {
				selectGroup_textDsSql = selectGroup_textDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectGroup_textDsSql = selectGroup_textDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取群聊图片统计SQL
			String selectGroup_pictureDsSql = super.getJsonConfigValue(configJsonPath, "selectGroup_pictureDsSql");
			if (!StringUtil.isNullOrEmpty(selectGroup_pictureDsSql)) {
				selectGroup_pictureDsSql = selectGroup_pictureDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectGroup_pictureDsSql = selectGroup_pictureDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取群聊文件统计SQL
			String selectGroup_fileDsSql = super.getJsonConfigValue(configJsonPath, "selectGroup_fileDsSql");
			if (!StringUtil.isNullOrEmpty(selectGroup_fileDsSql)) {
				selectGroup_fileDsSql = selectGroup_fileDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectGroup_fileDsSql = selectGroup_fileDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取群聊短语音统计SQL
			String selectGroup_short_voiceDsSql = super.getJsonConfigValue(configJsonPath, "selectGroup_short_voiceDsSql");
			if (!StringUtil.isNullOrEmpty(selectGroup_short_voiceDsSql)) {
				selectGroup_short_voiceDsSql = selectGroup_short_voiceDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectGroup_short_voiceDsSql = selectGroup_short_voiceDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}
			// 获取群聊短视频统计SQL
			String selectGroup_short_videoDsSql = super.getJsonConfigValue(configJsonPath, "selectGroup_short_videoDsSql");
			if (!StringUtil.isNullOrEmpty(selectGroup_short_videoDsSql)) {
				selectGroup_short_videoDsSql = selectGroup_short_videoDsSql.replace("dw_engine_message_log_dm_yyyymm", "dw_engine_message_log_dm_" + optime_month);
				selectGroup_short_videoDsSql = selectGroup_short_videoDsSql.replace(select_source_storeId, ScriptTimeUtil.optime_yesday());
			}

			// 获取(DS)单聊文本统计聚合SQL
			StringBuffer strSingle_textBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strSingle_textBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strSingle_textBuffer.append(" UNION ");
					}
					strSingle_textBuffer.append(" " + selectSingle_textDsSql + " ");
					strSingle_textBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)单聊文本统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strSingle_textBuffer)) {
				SqlUtils.sqlExecute(dataSource, strSingle_textBuffer.toString(), this.getName());
			}

			// 获取(DS)单聊图片统计聚合SQL
			StringBuffer strSingle_pictureBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strSingle_pictureBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strSingle_pictureBuffer.append(" UNION ");
					}
					strSingle_pictureBuffer.append(" " + selectSingle_pictureDsSql + " ");
					strSingle_pictureBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)单聊图片统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strSingle_pictureBuffer)) {
				SqlUtils.sqlExecute(dataSource, strSingle_pictureBuffer.toString(), this.getName());
			}

			// 获取(DS)单聊文件统计聚合SQL
			StringBuffer strSingle_fileBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strSingle_fileBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strSingle_fileBuffer.append(" UNION ");
					}
					strSingle_fileBuffer.append(" " + selectSingle_fileDsSql + " ");
					strSingle_fileBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)单聊文件统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strSingle_fileBuffer)) {
				SqlUtils.sqlExecute(dataSource, strSingle_fileBuffer.toString(), this.getName());
			}

			// 获取(DS)单聊短语音统计聚合SQL
			StringBuffer strSingle_short_voiceBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strSingle_short_voiceBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strSingle_short_voiceBuffer.append(" UNION ");
					}
					strSingle_short_voiceBuffer.append(" " + selectSingle_short_voiceDsSql + " ");
					strSingle_short_voiceBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)单聊短语音统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strSingle_short_voiceBuffer)) {
				SqlUtils.sqlExecute(dataSource, strSingle_short_voiceBuffer.toString(), this.getName());
			}

			// 获取(DS)单聊短视频统计聚合SQL
			StringBuffer strSingle_short_videoBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strSingle_short_videoBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strSingle_short_videoBuffer.append(" UNION ");
					}
					strSingle_short_videoBuffer.append(" " + selectSingle_short_videoDsSql + " ");
					strSingle_short_videoBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)单聊短视频统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strSingle_short_videoBuffer)) {
				SqlUtils.sqlExecute(dataSource, strSingle_short_videoBuffer.toString(), this.getName());
			}

			// 获取(DS)单聊语音通话统计聚合SQL
			StringBuffer strSingle_voiceBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strSingle_voiceBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strSingle_voiceBuffer.append(" UNION ");
					}
					strSingle_voiceBuffer.append(" " + selectSingle_voiceDsSql + " ");
					strSingle_voiceBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)单聊语音通话统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strSingle_voiceBuffer)) {
				SqlUtils.sqlExecute(dataSource, strSingle_voiceBuffer.toString(), this.getName());
			}

			// 获取(DS)单聊视频通话统计聚合SQL
			StringBuffer strSingle_videoBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strSingle_videoBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strSingle_videoBuffer.append(" UNION ");
					}
					strSingle_videoBuffer.append(" " + selectSingle_videoDsSql + " ");
					strSingle_videoBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)单聊视频通话统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strSingle_videoBuffer)) {
				SqlUtils.sqlExecute(dataSource, strSingle_videoBuffer.toString(), this.getName());
			}

			// 获取(DS)群聊文本统计聚合SQL
			StringBuffer strGroup_textBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strGroup_textBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strGroup_textBuffer.append(" UNION ");
					}
					strGroup_textBuffer.append(" " + selectGroup_textDsSql + " ");
					strGroup_textBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)群聊文本统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strGroup_textBuffer)) {
				SqlUtils.sqlExecute(dataSource, strGroup_textBuffer.toString(), this.getName());
			}

			// 获取(DS)群聊图片统计聚合SQL
			StringBuffer strGroup_pictureBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strGroup_pictureBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strGroup_pictureBuffer.append(" UNION ");
					}
					strGroup_pictureBuffer.append(" " + selectGroup_pictureDsSql + " ");
					strGroup_pictureBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)群聊图片统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strGroup_pictureBuffer)) {
				SqlUtils.sqlExecute(dataSource, strGroup_pictureBuffer.toString(), this.getName());
			}

			// 获取(DS)群聊文件统计聚合SQL
			StringBuffer strGroup_fileBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strGroup_fileBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strGroup_fileBuffer.append(" UNION ");
					}
					strGroup_fileBuffer.append(" " + selectGroup_fileDsSql + " ");
					strGroup_fileBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)群聊文件统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strGroup_fileBuffer)) {
				SqlUtils.sqlExecute(dataSource, strGroup_fileBuffer.toString(), this.getName());
			}

			// 获取(DS)群聊短语音统计聚合SQL
			StringBuffer strGroup_short_voiceBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strGroup_short_voiceBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strGroup_short_voiceBuffer.append(" UNION ");
					}
					strGroup_short_voiceBuffer.append(" " + selectGroup_short_voiceDsSql + " ");
					strGroup_short_voiceBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)群聊短语音统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strGroup_short_voiceBuffer)) {
				SqlUtils.sqlExecute(dataSource, strGroup_short_voiceBuffer.toString(), this.getName());
			}

			// 获取(DS)群聊短视频统计聚合SQL
			StringBuffer strGroup_short_videoBuffer = new StringBuffer();
			if (null != listWithRollup && !listWithRollup.isEmpty()) {
				strGroup_short_videoBuffer.append(" " + insertTmpDsSql + " ");
				int i = 0;
				for (String withRollup : listWithRollup) {
					if (i > 0) {
						strGroup_short_videoBuffer.append(" UNION ");
					}
					strGroup_short_videoBuffer.append(" " + selectGroup_short_videoDsSql + " ");
					strGroup_short_videoBuffer.append(" group by `statis_date`,  " + withRollup.substring(1) + "  with rollup ");
					i++;
				}
			}
			// 插入(DS)群聊短视频统计Cube聚合数据
			if (!StringUtil.isNullOrEmpty(strGroup_short_videoBuffer)) {
				SqlUtils.sqlExecute(dataSource, strGroup_short_videoBuffer.toString(), this.getName());
			}

			// 插入(DS) Cube聚合数据
			String insertDsSelectSql = super.getJsonConfigValue(configJsonPath, "insertDmSelectSql");
			if (!StringUtil.isNullOrEmpty(insertDsSelectSql)) {
				insertDsSelectSql = insertDsSelectSql.replace("st_function_statistic_dm_yyyymm", "st_function_statistic_dm_" + optime_month);
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

	}

}
