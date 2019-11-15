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
 * 存储各个任务配置
 * </p>
 *
 *
 * @since 2017-08-14
 */
@TableName("conf_info_tasks")
public class ConfInfoTasks extends Model<ConfInfoTasks> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键 (tasks_id),调度任务id
     */
	@TableId(value="pk_id", type= IdType.AUTO)
	private Integer pkId;
    /**
     * 调度任务命名，调度任务名称
     */
	@TableField("tasks_name")
	private String tasksName;
    /**
     * 调度任务描述
     */
	private String remark;
    /**
     * shell、python、java等
     */
	@TableField("script_type")
	private Integer scriptType;
    /**
     * 执行脚本id
     */
	@TableField("script_id")
	private Integer scriptId;
    /**
     * 任务耗时
     */
	@TableField("take_eval")
	private Integer takeEval;
    /**
     * 延迟告警，若任务脚本执行延迟，则告警
     */
	@TableField("alarm_notice")
	private String alarmNotice;
    /**
     * 1 : 正常 -1：删除
     */
	private Integer status;
	@TableField("create_user")
	private String createUser;
	@TableField("create_time")
	private Date createTime;
	@TableField("update_user")
	private String updateUser;
	@TableField("update_time")
	private Date updateTime;
	
	@TableField(exist = false)
	private String scriptPath;
	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public String getTasksName() {
		return tasksName;
	}

	public void setTasksName(String tasksName) {
		this.tasksName = tasksName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getScriptType() {
		return scriptType;
	}

	public void setScriptType(Integer scriptType) {
		this.scriptType = scriptType;
	}

	public Integer getScriptId() {
		return scriptId;
	}

	public void setScriptId(Integer scriptId) {
		this.scriptId = scriptId;
	}

	public Integer getTakeEval() {
		return takeEval;
	}

	public void setTakeEval(Integer takeEval) {
		this.takeEval = takeEval;
	}

	public String getAlarmNotice() {
		return alarmNotice;
	}

	public void setAlarmNotice(String alarmNotice) {
		this.alarmNotice = alarmNotice;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	protected Serializable pkVal() {
		return this.pkId;
	}

}
