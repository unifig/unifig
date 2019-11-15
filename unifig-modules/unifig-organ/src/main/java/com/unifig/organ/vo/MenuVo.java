package com.unifig.organ.vo;


import com.baomidou.mybatisplus.annotations.TableField;
import com.unifig.organ.dto.Tree;
import lombok.Data;

/**

 * @date 2018-10-24
 */
@Data
public class MenuVo extends Tree {

	/**
	 * 菜单ID
	 */
	private Long menuId;

	/**
	 * 父菜单ID，一级菜单为0
	 */
	private Long parentId;

	/**
	 * 父菜单名称
	 */
	@TableField(exist = false)
	private String parentName;

	/**
	 * 菜单名称
	 */
	private String name;

	/**
	 * 授权(多个用逗号分隔，如：user:list,user:create)
	 */
	private String perms;

}
