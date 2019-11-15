package etl.dispatch.script;

import java.io.Serializable;
import java.util.Map;

public class ScriptBean implements Serializable {
	private static final long serialVersionUID = -7950343154683392385L;
	private String groupId;
	private String taskId;
	private String taskName;
	private String scriptId;
	private String scriptName;
	private String scriptPath;
	private Map<String, Object> paramMap;
	private String personal;
	private int takeEval;// 执行最大耗时设置
	private String alarmNotice;
	private long startTimes;// 执行开始时间
	private long endTimes;// 执行结束时间
	private String serviceIp;// 服务器Ip地址

	public ScriptBean() {

	}

	public ScriptBean(String groupId, String taskId, String taskName, String scriptId, String scriptName, String scriptPath, Map<String, Object> paramMap, String personal, long startTimes, String serviceIp) {
		this.groupId = groupId;
		this.taskId = taskId;
		this.taskName = taskName;
		this.scriptId = scriptId;
		this.scriptName = scriptName;
		this.scriptPath = scriptPath;
		this.paramMap = paramMap;
		this.personal = personal;
		this.startTimes = startTimes;
	}

	public ScriptBean(String groupId, String taskId, String taskName, String scriptId, String scriptName, String scriptPath, Map<String, Object> paramMap, String personal, long startTimes, String serviceIp, int takeEval, String alarmNotice) {
		this.groupId = groupId;
		this.taskId = taskId;
		this.taskName = taskName;
		this.scriptId = scriptId;
		this.scriptName = scriptName;
		this.scriptPath = scriptPath;
		this.paramMap = paramMap;
		this.personal = personal;
		this.startTimes = startTimes;
		this.takeEval = takeEval;
		this.alarmNotice = alarmNotice;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getScriptId() {
		return scriptId;
	}

	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public int getTakeEval() {
		return takeEval;
	}

	public void setTakeEval(int takeEval) {
		this.takeEval = takeEval;
	}

	public String getAlarmNotice() {
		return alarmNotice;
	}

	public void setAlarmNotice(String alarmNotice) {
		this.alarmNotice = alarmNotice;
	}

	public long getStartTimes() {
		return startTimes;
	}

	public void setStartTimes(long startTimes) {
		this.startTimes = startTimes;
	}

	public long getEndTimes() {
		return endTimes;
	}

	public void setEndTimes(long endTimes) {
		this.endTimes = endTimes;
	}

	public String getServiceIp() {
		return serviceIp;
	}

	public void setServiceIp(String serviceIp) {
		this.serviceIp = serviceIp;
	}

	@Override
	public String toString() {
		return "ScriptBean [groupId=" + groupId + ", taskId=" + taskId + ", taskName=" + taskName + ", scriptId=" + scriptId + ", scriptName=" + scriptName + ", scriptPath=" + scriptPath + ", paramMap=" + paramMap + ", personal=" + personal + ", takeEval=" + takeEval + ", alarmNotice=" + alarmNotice + ", startTimes=" + startTimes + ", endTimes=" + endTimes + ", serviceIp=" + serviceIp + "]";
	}

}
