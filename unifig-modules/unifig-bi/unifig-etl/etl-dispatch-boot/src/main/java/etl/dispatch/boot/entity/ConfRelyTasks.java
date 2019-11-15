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
 * 存储各个任务之间依赖配置
 * </p>
 *
 *
 * @since 2017-08-14
 */
@TableName("conf_rely_tasks")
public class ConfRelyTasks extends Model<ConfRelyTasks> {

    private static final long serialVersionUID = 1L;

    /**
     * 记录自增主键
     */
	@TableId(value="pk_id", type= IdType.AUTO)
	private Integer pkId;
    /**
     * 任务分组id
     */
	@TableField("group_id")
	private Integer groupId;
    /**
     * 调度任务id；来源 conf_info_ltasks ；tasks_id
     */
	@TableField("tasks_id")
	private Integer tasksId;
    /**
     * 依赖任务id；来源 conf_info_tasks ；tasks_id，依赖任务id 为-1，则为顶级任务
     */
	@TableField("relytasks_id")
	private Integer relytasksId;
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


	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getTasksId() {
		return tasksId;
	}

	public void setTasksId(Integer tasksId) {
		this.tasksId = tasksId;
	}

	public Integer getRelytasksId() {
		return relytasksId;
	}

	public void setRelytasksId(Integer relytasksId) {
		this.relytasksId = relytasksId;
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
