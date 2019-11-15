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
 * 存储 Java汇总实现脚本
 * </p>
 *
 *
 * @since 2017-08-14
 */
@TableName("conf_info_java_script")
public class ConfInfoJavaScript extends Model<ConfInfoJavaScript> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键 (script_id),任务脚本id
     */
	@TableId(value="pk_id", type= IdType.AUTO)
	private Integer pkId;
    /**
     * 执行脚本名称
     */
	@TableField("script_name")
	private String scriptName;
    /**
     * 唯一区分脚本的串，如 user_count_ds（用户单天统计实现）等
     */
	@TableField("script_key")
	private String scriptKey;
    /**
     * json串，初始化参数数据
     */
	@TableField("preset_param")
	private String presetParam;
    /**
     * java 实现类的具体路径，通过唯一标识获取实体类，通过校验路径判断是否正确
     */
	@TableField("script_path")
	private String scriptPath;
    /**
     * 脚本开发者，若出现执行脚本异常，会告警抄送开发者
     */
	private String personal;
    /**
     * 1 : 正常 -1：删除 0: 暂停
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

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getScriptKey() {
		return scriptKey;
	}

	public void setScriptKey(String scriptKey) {
		this.scriptKey = scriptKey;
	}

	public String getPresetParam() {
		return presetParam;
	}

	public void setPresetParam(String presetParam) {
		this.presetParam = presetParam;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
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
