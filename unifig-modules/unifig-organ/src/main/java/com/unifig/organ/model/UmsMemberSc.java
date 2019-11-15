/**
 * FileName: 用户关系表
 * Author:   maxl
 * Date:     2019-08-30
 * Description: 用户关系表
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.organ.model;

import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户关系表
 * </p>
 *
 *
 * @since 2019-08-30
 */
@Data
@Accessors(chain = true)
@TableName("ums_member_sc")
public class UmsMemberSc extends Model<UmsMemberSc> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.UUID)
	@ApiModelProperty(value = "", name = "id")
	private String id;
    /**
     * 邀请人
     */
	@TableField("from_id")
	@ApiModelProperty(value = "邀请人", name = "fromId")
	private String fromId;
    /**
     * 被邀请人
     */
	@TableField("to_id")
	@ApiModelProperty(value = "被邀请人", name = "toId")
	private String toId;
    /**
     * 被邀请人昵称
     */
	@TableField("to_nickname")
	@ApiModelProperty(value = "被邀请人昵称", name = "toNickname")
	private String toNickname;
    /**
     * 邀请二维码的编码
     */
	@TableField("or_code")
	@ApiModelProperty(value = "邀请二维码的编码", name = "orCode")
	private String orCode;
    /**
     * 注册时间
     */
	@TableField("create_time")
	@ApiModelProperty(value = "注册时间", name = "createTime")
	private Date createTime;
    /**
     * 注册时间
     */
	@TableField("edit_time")
	@ApiModelProperty(value = "注册时间", name = "editTime")
	private Date editTime;
    /**
     * 是否可用

     */
	@ApiModelProperty(value = "是否可用", name = "enable")
	private Integer enable;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
