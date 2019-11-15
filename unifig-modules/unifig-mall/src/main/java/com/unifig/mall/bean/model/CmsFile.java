package com.unifig.mall.bean.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 文章表
 * </p>
 *
 *
 * @since 2019-02-14
 */
@TableName("cms_file")
public class CmsFile extends Model<CmsFile> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 访问路径
     */
    private String url;
    /**
     * 服务器路径
     */
    private String absolutePath;
    /**
     * 上传时间
     */
    @TableField("upload_time")
    private Date uploadTime;
    /**
     * 新增记录时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 删记录时间
     */
    @TableField("edit_time")
    private Date editTime;
    /**
     * space domain
     */
    @TableField("ratel_no")
    private String ratelNo;
    /**
     * 1可以 0不可以
     */
    private Integer enable;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getRatelNo() {
        return ratelNo;
    }

    public void setRatelNo(String ratelNo) {
        this.ratelNo = ratelNo;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "CmsFile{" +
                ", id=" + id +
                ", name=" + name +
                ", suffix=" + suffix +
                ", url=" + url +
                ", absolutePath=" + absolutePath +
                ", uploadTime=" + uploadTime +
                ", createTime=" + createTime +
                ", editTime=" + editTime +
                ", ratelNo=" + ratelNo +
                ", enable=" + enable +
                "}";
    }
}
