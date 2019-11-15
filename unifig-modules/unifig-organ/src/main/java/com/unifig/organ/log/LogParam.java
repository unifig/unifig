package com.unifig.organ.log;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LogParam {

    private Integer channel;
    private long userId;
    private String type;//日志类型

}
