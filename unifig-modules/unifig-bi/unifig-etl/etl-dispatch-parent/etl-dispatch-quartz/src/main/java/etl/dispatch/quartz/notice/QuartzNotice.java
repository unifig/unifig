package etl.dispatch.quartz.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.config.api.IEtlConfigApiService;
import etl.dispatch.config.entity.SignInfoTasksEntity;
import etl.dispatch.config.enums.IsSuccessEnum;
import etl.dispatch.config.enums.ClassifyEnum;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.HttpRequestor;
import etl.dispatch.util.MailCheckUtil;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;
@Component
public class QuartzNotice {
	private static final Logger logger = LoggerFactory.getLogger(QuartzNotice.class);
	private static String postUrl ;
	@Value("${notice.mail.postUrl}")  
    public void setPostUrl(String url) {  
		 postUrl = url;  
    }  
	public static void taskAlarm(boolean isSuccess, String errorMsg, ScriptBean scriptBean) {
		int takeEval = scriptBean.getTakeEval();
		long startTimes = scriptBean.getStartTimes();
		long endTimes = scriptBean.getEndTimes();
		String alarmJson = scriptBean.getAlarmNotice();
		String personal = scriptBean.getPersonal();

		scriptBean.setParamMap(null);
		String message = null;
		if (!isSuccess) {
			message = "脚本执行异常; " + scriptBean.toString() + "; error :" + errorMsg;
		} else {
			if ((takeEval >0) && ((endTimes - startTimes) / 1000 >= takeEval)) {
				message = "脚本执行超时; " + JSON.toJSONString(scriptBean) + "; 共计耗时:" + (endTimes - startTimes) / 1000 + "秒 , 超时:" + (((endTimes - startTimes) / 1000) - takeEval) + "秒";
			} else {
				message = "脚本执行成功; " + JSON.toJSONString(scriptBean) + ";";
			}
		}
		if (StringUtil.isNullOrEmpty(message)) {
			return;
		}
		doSignTask(isSuccess, message, scriptBean.getGroupId(), scriptBean.getTaskId(), scriptBean.getTaskName(), scriptBean.getScriptPath(), scriptBean.getStartTimes(), scriptBean.getEndTimes() );
		if(!isSuccess){
			doAlarm(isSuccess, alarmJson, personal, message, scriptBean.getTaskName());
		}
	}

	public static void doSignTask(boolean isSuccess, String message, String groupId, String taskId, String taskName, String scriptPath,  long startTimes, long endTimes) {
		IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
		if (null != etlConfigApiService) {
			SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
			ObjectClassHelper.setFieldValue(signInfoTasks, "classify", ClassifyEnum.TASK.getCode());
			ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", groupId+"_"+taskId);
			ObjectClassHelper.setFieldValue(signInfoTasks, "taskName", taskName);
			ObjectClassHelper.setFieldValue(signInfoTasks, "scriptPath", scriptPath);
			ObjectClassHelper.setFieldValue(signInfoTasks, "startTime", DateUtil.format2Date(startTimes));
			ObjectClassHelper.setFieldValue(signInfoTasks, "endTime", DateUtil.format2Date(endTimes));
			ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
			
			ObjectClassHelper.setFieldValue(signInfoTasks, "isSuccess", isSuccess ? IsSuccessEnum.SUCCESS.getCode() : IsSuccessEnum.FAIL.getCode());
			ObjectClassHelper.setFieldValue(signInfoTasks, "message", message);
			etlConfigApiService.saveSignInfoTasks(signInfoTasks);
		}
	}

	public static void doAlarm(boolean isSuccess, String alarmJson, String personal, String message, String mailTitel) {
		Map<String, Object> alarmJsonMap = null;
		if (!StringUtil.isNullOrEmpty(alarmJson)) {
			try {
				alarmJsonMap = (Map) JSON.parse(alarmJson);
			} catch (Exception ex) {
				logger.error("alarmNotice failed to convert to JSON,  error:" + ex.getMessage());
			}
		}
		List<String> alarmList = new ArrayList<String>();
		// {"sms":"18939195459,13462401088","email":"xucong@shixinyun.com,516216877@qq.com"}
		if (null != alarmJsonMap & !alarmJsonMap.isEmpty()) {
			for (Object object : alarmJsonMap.values()) {
				String alarmVal = String.valueOf(object);
				alarmList.add(alarmVal);
			}
		}
		// kfuuser@126.com
		if (!isSuccess && !StringUtil.isNullOrEmpty(personal)) {
			alarmList.add(personal);
		}
		doAlarm(alarmList, message, mailTitel);
	}

	public static void doAlarm(List<String> alarmList, String message, String mailTitel) {
		if (null == alarmList || alarmList.isEmpty()) {
			return;
		}
		for (String alarmVal : alarmList) {
			String[] alarmValArr = alarmVal.split(",");
			if (null != alarmValArr && alarmValArr.length > 0) {
				for (String person : alarmValArr) {
					if (MailCheckUtil.checkEmail(person)) {
						try {
							// 发送邮件告警: message
							Map<String, Object> parameterMap = new HashMap<String, Object>();
							parameterMap.put("receiver", person);
							parameterMap.put("subject", DateUtil.getSysStrCurrentDate("yyyy-MM-dd") + " ETL调度任务运行报告：" + mailTitel);
							parameterMap.put("message", message);
							new HttpRequestor().doPost(postUrl, parameterMap, null);
						} catch (Exception e) {
							logger.error("send email notice filed,  error:" + e.getMessage(), e);
						}
					} else {
						if (MailCheckUtil.checkMobileNumber(person)) {
							// 发送短信告警 :message
						}
					}
				}
			}
		}
	}
}
