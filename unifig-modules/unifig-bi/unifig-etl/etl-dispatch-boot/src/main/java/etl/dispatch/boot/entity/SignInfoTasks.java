package etl.dispatch.boot.entity;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 存储各个任务任务组执行完成标记
 * </p>
 *
 *
 * @since 2017-08-14
 */
@TableName("sign_info_tasks")
public class SignInfoTasks extends Model<SignInfoTasks> {
	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键 (log_id),任务运行id
	 */
	@TableId(value = "pk_id", type = IdType.AUTO)
	private Integer pkId;
	/**
	 * 1: task log ,2: group log
	 */
	private Integer classify;
	/**
	 * 任务组Id或者任务Id
	 */
	@TableField("task_id")
	private String taskId;
	/**
	 * 任务组名或者任务名
	 */
	@TableField("task_name")
	private String taskName;
	@TableField("script_path")
	private String scriptPath;
	/**
	 * 调度执行标记
	 */
	@TableField("time_sign")
	private String timeSign;
	/**
	 * 执行开始时间
	 */
	@TableField("start_time")
	private Date startTime;
	/**
	 * 执行结束时间
	 */
	@TableField("end_time")
	private Date endTime;
	/**
	 * 日志插入时间
	 */
	@TableField("log_time")
	private Date logTime;
	/**
	 * 0:失败； 1:成功
	 */
	@TableField("is_success")
	private Integer isSuccess;
	/**
	 * 消息日志
	 */
	private String message;

	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public Integer getClassify() {
		return classify;
	}

	public void setClassify(Integer classify) {
		this.classify = classify;
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

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public String getTimeSign() {
		return timeSign;
	}

	public void setTimeSign(String timeSign) {
		this.timeSign = timeSign;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public Integer getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Integer isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	protected Serializable pkVal() {
		return this.pkId;
	}
}
