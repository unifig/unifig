package com.unifig.mall.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 参团用户列表
 */
@Data
@ToString
@ApiModel("参团用户")
public class JoinUserVo {

    @ApiModelProperty("用户id")
    private String id;

    @ApiModelProperty("用户昵称")
    private String name;

    @ApiModelProperty("用户头像")
    private String pic;

}
