package com.unifig.organ.log;

public enum MarkerEnum {

    SPAP_REGISTER("spap-register"),

    SPAP_LOGIN("spap-login"),

    SPAP_LOGOUT("spap-logout"),

    SPAP_SCHEDULE("spap-schedule");

    private String name;

    MarkerEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}