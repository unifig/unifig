package com.unifig.organ.log;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LogManager {

    private static Logger logger = LoggerFactory.getLogger(LogManager.class);

    public static void writeRegisterLog(LogParam param) {
        Marker registerMarker = MarkerFactory.getDetachedMarker(MarkerEnum.SPAP_REGISTER.getName());
        JSONObject registerLog = new JSONObject();
        registerLog.put("channel", param.getChannel());
        registerLog.put("userId", param.getUserId());
        registerLog.put("timestamp", System.currentTimeMillis());
        registerLog.put("type", MarkerEnum.SPAP_REGISTER.getName());
        logger.info(registerMarker, registerLog.toJSONString());
    }

    public static void writeLogoutLog(LogParam param) {
        Marker registerMarker = MarkerFactory.getDetachedMarker(MarkerEnum.SPAP_LOGOUT.getName());
        JSONObject registerLog = new JSONObject();
        registerLog.put("channel", param.getChannel());
        registerLog.put("userId", param.getUserId());
        registerLog.put("timestamp", System.currentTimeMillis());
        registerLog.put("type", MarkerEnum.SPAP_LOGOUT.getName());
        logger.info(registerMarker, registerLog.toJSONString());
    }

    public static void writeLoginLog(LogParam param) {
        Marker registerMarker = MarkerFactory.getDetachedMarker(MarkerEnum.SPAP_LOGIN.getName());
        JSONObject registerLog = new JSONObject();
        registerLog.put("channel", param.getChannel());
        registerLog.put("userId", param.getUserId());
        registerLog.put("timestamp", System.currentTimeMillis());
        registerLog.put("type", MarkerEnum.SPAP_LOGIN.getName());
        logger.info(registerMarker, registerLog.toJSONString());
    }
}
