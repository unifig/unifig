package com.unifig.organ.dto;

import com.baomidou.mybatisplus.annotations.TableField;

import java.io.Serializable;
import java.util.List;

/**
 * 名称：Tree <br>
 * 描述：<br>
 *

 * @date 2018-10-24
 */
public class Tree<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**********iview tree属性**************/
    /**
     * 标题
     */
    @TableField(exist = false)
    private String title;

    /**
     * 是否展开直子节点
     */
    @TableField(exist = false)
    private boolean expand = false;

    /**
     * 禁掉响应
     */

    @TableField(exist = false)
    private boolean disabled = false;
    /**
     * 禁掉 checkbox
     */
    @TableField(exist = false)
    private boolean disableCheckbox = false;
    /**
     * 是否选中子节点
     */
    @TableField(exist = false)
    private boolean selected = false;
    /**
     * 是否勾选(如果勾选，子节点也会全部勾选)
     */
    @TableField(exist = false)
    private boolean checked = false;
    @TableField(exist = false)
    private boolean leaf = false;
    /**
     * ztree属性
     */
    @TableField(exist = false)
    private Boolean open;
    @TableField(exist = false)
    private List<?> list;

    /**
     * 子节点属性数组
     */
    @TableField(exist = false)
    private List<?> children;
    @TableField(exist = false)
    private String value;
    @TableField(exist = false)

    private String label;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisableCheckbox() {
        return disableCheckbox;
    }

    public void setDisableCheckbox(boolean disableCheckbox) {
        this.disableCheckbox = disableCheckbox;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<?> getChildren() {
        return children;
    }

    public void setChildren(List<?> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
