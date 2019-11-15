package etl.dispatch.base.enums;

/**
 * @Title:LoginTypeEnum
 *
 */

public enum LoginTypeEnum {
    LOGIN(1, "登录"), LOGOUT( -1, "注销"), TIMEOUT( -2, "超时退出");

    private int code;

    private String desc;

    LoginTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
