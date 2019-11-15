package com.unifig.organ.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 车型
 * </p>
 *
 *
 * @since 2019-06-21
 */
public class UserInfoEncryptedVo {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("openid")
    private String openid;
    @ApiModelProperty("加密数据")
    private String encryptedData;
    @ApiModelProperty("iv")
    private String iv;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
