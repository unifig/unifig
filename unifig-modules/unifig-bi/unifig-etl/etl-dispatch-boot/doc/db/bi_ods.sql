/*
 Navicat Premium Data Transfer

 Source Server         : 125.208.1.67-root
 Source Server Type    : MySQL
 Source Server Version : 50711
 Source Host           : 125.208.1.67:33665
 Source Schema         : bi_ods

 Target Server Type    : MySQL
 Target Server Version : 50711
 File Encoding         : 65001

 Date: 10/01/2018 12:47:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ods_app_crash_message
-- ----------------------------
DROP TABLE IF EXISTS `ods_app_crash_message`;
CREATE TABLE `ods_app_crash_message`  (
  `crash_message_id` bigint(255) NOT NULL AUTO_INCREMENT COMMENT '信息id',
  `ip_port` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'ftp服务器的ip+端口; 特殊符号 ： 分隔',
  `ftp_user` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'ftp 服务器的用户名',
  `ftp_dir` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ftp文件夹路径',
  `ftp_file` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ftp文件名称',
  `crash_message_hashid` bigint(255) NOT NULL COMMENT '文件hash值',
  PRIMARY KEY (`crash_message_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_app_crash_report
-- ----------------------------
DROP TABLE IF EXISTS `ods_app_crash_report`;
CREATE TABLE `ods_app_crash_report`  (
  `timestamp` bigint(20) NOT NULL,
  `store_id` int(9) NOT NULL DEFAULT 0 COMMENT '分区id',
  `year` int(9) NOT NULL DEFAULT 1900 COMMENT '年',
  `month` int(9) NOT NULL DEFAULT 0 COMMENT '月',
  `day` int(9) NOT NULL DEFAULT 0 COMMENT '日',
  `hour` int(9) NOT NULL DEFAULT 0 COMMENT '时',
  `app_id` mediumint(9) NOT NULL,
  `app_version_id` mediumint(9) NOT NULL,
  `app_plat_id` mediumint(9) NOT NULL DEFAULT -9,
  `country_id` mediumint(9) NOT NULL,
  `region_id` mediumint(9) NOT NULL,
  `carrier_id` mediumint(9) NOT NULL,
  `connect_type_id` mediumint(9) NOT NULL,
  `network_type_id` mediumint(9) NOT NULL,
  `os_id` mediumint(9) NOT NULL,
  `os_version_id` mediumint(9) NOT NULL,
  `device_type` mediumint(9) NOT NULL,
  `device_id` bigint(20) NOT NULL,
  `manufacturer_id` mediumint(9) NOT NULL,
  `manufacturer_model_id` mediumint(9) NOT NULL,
  `crash_message_id` bigint(255) NOT NULL COMMENT '崩溃信息ID，来源ods_app_crash_message',
  `platform` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '崩溃平台 ( e.g. \"win32\" or \"darwin\");重复数据',
  `process_type` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '崩溃进程类型( e.g. \"renderer\" or \"main\")',
  `guid` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '崩溃uuid',
  `_version` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '崩溃应用版本号;重复数据',
  `prod` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '崩溃应用名称;重复数据',
  INDEX `idx_ods_crash_report_times`(`timestamp`) USING BTREE,
  INDEX `idx_ods_crash_report_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_crash_report_store`(`store_id`) USING BTREE,
  INDEX `idx_ods_crash_report_appid`(`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_app_error_message
-- ----------------------------
DROP TABLE IF EXISTS `ods_app_error_message`;
CREATE TABLE `ods_app_error_message`  (
  `error_message_id` bigint(255) NOT NULL AUTO_INCREMENT COMMENT '信息id',
  `error_message` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '信息详情',
  `error_message_hashcode` int(255) NOT NULL COMMENT '信息hash值',
  PRIMARY KEY (`error_message_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_app_error_report
-- ----------------------------
DROP TABLE IF EXISTS `ods_app_error_report`;
CREATE TABLE `ods_app_error_report`  (
  `timestamp` bigint(20) NOT NULL,
  `store_id` int(9) NOT NULL DEFAULT 0 COMMENT '分区id',
  `year` int(9) NOT NULL DEFAULT 1900 COMMENT '年',
  `month` int(9) NOT NULL DEFAULT 0 COMMENT '月',
  `day` int(9) NOT NULL DEFAULT 0 COMMENT '日',
  `hour` int(9) NOT NULL DEFAULT 0 COMMENT '时',
  `app_id` mediumint(9) NOT NULL,
  `app_version_id` mediumint(9) NOT NULL,
  `app_plat_id` mediumint(9) NOT NULL DEFAULT -9,
  `country_id` mediumint(9) NOT NULL,
  `region_id` mediumint(9) NOT NULL,
  `carrier_id` mediumint(9) NOT NULL,
  `connect_type_id` mediumint(9) NOT NULL,
  `network_type_id` mediumint(9) NOT NULL,
  `os_id` mediumint(9) NOT NULL,
  `os_version_id` mediumint(9) NOT NULL,
  `device_type` mediumint(9) NOT NULL,
  `device_id` bigint(20) NOT NULL,
  `manufacturer_id` mediumint(9) NOT NULL,
  `manufacturer_model_id` mediumint(9) NOT NULL,
  `error_message` text CHARACTER SET utf8 COLLATE utf8_general_ci,
  `error_message_id` bigint(255) NOT NULL COMMENT '崩溃信息ID',
  `stack_trace` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci,
  `stack_trace_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `decode_stack_trace` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci,
  `decode_stack_trace_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `additional_info` text CHARACTER SET utf8 COLLATE utf8_general_ci,
  `additional_info_path` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `error_uuid` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `image_uuids` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci,
  `error_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '错误类型',
  `error_level` int(9) NOT NULL DEFAULT 0 COMMENT '类型等级:0：高，1：中，2：低',
  INDEX `idx_ods_error_report_times`(`timestamp`) USING BTREE,
  INDEX `idx_ods_error_report_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_error_report_store`(`store_id`) USING BTREE,
  INDEX `idx_ods_error_report_appid`(`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_app_event_report
-- ----------------------------
DROP TABLE IF EXISTS `ods_app_event_report`;
CREATE TABLE `ods_app_event_report`  (
  `timestamp` bigint(20) NOT NULL,
  `store_id` int(9) NOT NULL DEFAULT 0 COMMENT '分区id',
  `year` int(9) NOT NULL DEFAULT 1900 COMMENT '年',
  `month` int(9) NOT NULL DEFAULT 0 COMMENT '月',
  `day` int(9) NOT NULL DEFAULT 0 COMMENT '日',
  `hour` int(9) NOT NULL DEFAULT 0 COMMENT '时',
  `app_id` mediumint(9) NOT NULL,
  `app_version_id` mediumint(9) NOT NULL,
  `app_plat_id` mediumint(9) NOT NULL DEFAULT -9,
  `country_id` mediumint(9) NOT NULL,
  `region_id` mediumint(9) NOT NULL,
  `carrier_id` mediumint(9) NOT NULL,
  `connect_type_id` mediumint(9) NOT NULL,
  `network_type_id` mediumint(9) NOT NULL,
  `os_id` mediumint(9) NOT NULL,
  `os_version_id` mediumint(9) NOT NULL,
  `device_type` mediumint(9) NOT NULL,
  `device_id` bigint(20) NOT NULL,
  `manufacturer_id` mediumint(9) NOT NULL,
  `manufacturer_model_id` mediumint(9) NOT NULL,
  `event_key_id` int(9) NOT NULL,
  `event_id` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事件Id',
  `event_hash_id` bigint(20) NOT NULL COMMENT '事件MurmurHash',
  `event_value` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '事件值',
  INDEX `idx_ods_event_report_times`(`timestamp`) USING BTREE,
  INDEX `idx_ods_event_report_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_event_report_store`(`store_id`) USING BTREE,
  INDEX `idx_ods_event_report_appid`(`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_app_interaction_trace
-- ----------------------------
DROP TABLE IF EXISTS `ods_app_interaction_trace`;
CREATE TABLE `ods_app_interaction_trace`  (
  `timestamp` bigint(20) NOT NULL,
  `store_id` int(9) NOT NULL DEFAULT 0 COMMENT '分区id',
  `year` int(9) NOT NULL DEFAULT 1900 COMMENT '年',
  `month` int(9) NOT NULL DEFAULT 0 COMMENT '月',
  `day` int(9) NOT NULL DEFAULT 0 COMMENT '日',
  `hour` int(9) NOT NULL DEFAULT 0 COMMENT '小时',
  `app_id` mediumint(9) NOT NULL,
  `app_version_id` mediumint(9) NOT NULL,
  `app_plat_id` mediumint(9) NOT NULL DEFAULT -9,
  `country_id` mediumint(9) NOT NULL,
  `region_id` mediumint(9) NOT NULL,
  `carrier_id` mediumint(9) NOT NULL,
  `connect_type_id` mediumint(9) NOT NULL,
  `network_type_id` mediumint(9) NOT NULL,
  `os_id` mediumint(9) NOT NULL,
  `os_version_id` mediumint(9) NOT NULL,
  `device_type` mediumint(9) NOT NULL,
  `device_id` bigint(20) NOT NULL,
  `manufacturer_id` mediumint(9) NOT NULL,
  `manufacturer_model_id` mediumint(9) NOT NULL,
  `interaction_view_id` mediumint(9) NOT NULL,
  `duration` int(11) NOT NULL,
  `trace_data` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  INDEX `idx_ods_interact_trace_times`(`timestamp`) USING BTREE,
  INDEX `idx_ods_interact_trace_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_interact_trace_store`(`store_id`) USING BTREE,
  INDEX `idx_ods_interact_trace_appid`(`app_id`, `store_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_engine_message_log_dm_201710
-- ----------------------------
DROP TABLE IF EXISTS `ods_engine_message_log_dm_201710`;
CREATE TABLE `ods_engine_message_log_dm_201710`  (
  `master` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发送者',
  `direct` int(2) DEFAULT 0 COMMENT '流量上下行, 1-上行（服务器->终端），2-下行（服务器<-终端）',
  `timestamp` bigint(13) DEFAULT 0 COMMENT '发送时间,',
  `content_bytes` bigint(13) DEFAULT 0 COMMENT '内容大小,',
  `entity_bytes` bigint(13) DEFAULT 0 COMMENT '消息实体大小, ',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流量产生动作 ',
  `network_type` int(10) DEFAULT -9 COMMENT '流量类型(3G,4G,Wifi),   ',
  `app_device_type` int(10) DEFAULT -9 COMMENT '终端设备分类（pc、web、android、ios、macos、windows)',
  `app_version` int(10) DEFAULT -9 COMMENT '坐标版本号',
  `is_group` int(4) DEFAULT 2 COMMENT '是否是群消息:1、群消息，2、不是群消息',
  `from` bigint(13) DEFAULT NULL COMMENT '发送者',
  `to` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '接收者cube号',
  `type` int(3) DEFAULT -9 COMMENT '内容类型:1:Text:文本，2:File：文件，3:Image：图片，4:VoiceClip：短语音，5:VideoClip：短视频，6:Card：卡片消息，7:History：历史消息，8:Rich：富文本消息',
  `year` int(10) DEFAULT NULL COMMENT '年',
  `month` int(10) DEFAULT NULL COMMENT '月',
  `day` int(10) DEFAULT NULL COMMENT '日',
  `hour` int(10) DEFAULT NULL COMMENT '时',
  `store_id` int(10) DEFAULT NULL COMMENT '分区',
  INDEX `idx_ods_message_log_yyyymmdd_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_message_log_yyyymmdd_range`(`store_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_engine_message_log_dm_201801
-- ----------------------------
DROP TABLE IF EXISTS `ods_engine_message_log_dm_201801`;
CREATE TABLE `ods_engine_message_log_dm_201801`  (
  `master` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发送者',
  `direct` int(2) DEFAULT 0 COMMENT '流量上下行, 1-上行（服务器->终端），2-下行（服务器<-终端）',
  `timestamp` bigint(13) DEFAULT 0 COMMENT '发送时间,',
  `sn` bigint(20) DEFAULT NULL,
  `content_bytes` bigint(13) DEFAULT 0 COMMENT '内容大小,',
  `entity_bytes` bigint(13) DEFAULT 0 COMMENT '消息实体大小, ',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流量产生动作 ',
  `network_type` int(10) DEFAULT -9 COMMENT '流量类型(3G,4G,Wifi),   ',
  `app_device_type` int(10) DEFAULT -9 COMMENT '终端设备分类（pc、web、android、ios、macos、windows)',
  `app_version` int(10) DEFAULT -9 COMMENT '坐标版本号',
  `is_group` int(4) DEFAULT 2 COMMENT '是否是群消息:1、群消息，2、不是群消息',
  `from` bigint(13) DEFAULT NULL COMMENT '发送者',
  `to` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '接收者cube号',
  `type` int(3) DEFAULT -9 COMMENT '内容类型:1:Text:文本，2:File：文件，3:Image：图片，4:VoiceClip：短语音，5:VideoClip：短视频，6:Card：卡片消息，7:History：历史消息，8:Rich：富文本消息',
  `year` int(10) DEFAULT NULL COMMENT '年',
  `month` int(10) DEFAULT NULL COMMENT '月',
  `day` int(10) DEFAULT NULL COMMENT '日',
  `hour` int(10) DEFAULT NULL COMMENT '时',
  `store_id` int(10) DEFAULT NULL COMMENT '分区',
  INDEX `idx_ods_message_log_yyyymmdd_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_message_log_yyyymmdd_range`(`store_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_engine_multicall_log_dm_201710
-- ----------------------------
DROP TABLE IF EXISTS `ods_engine_multicall_log_dm_201710`;
CREATE TABLE `ods_engine_multicall_log_dm_201710`  (
  `master` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发送者',
  `timestamp` bigint(13) DEFAULT NULL COMMENT '日志产生时间',
  `create_time` bigint(13) DEFAULT NULL COMMENT '语音创建时间',
  `answered_time` bigint(13) DEFAULT NULL COMMENT '应答时间',
  `profile_create_time` bigint(13) DEFAULT NULL COMMENT '通话开始时间',
  `hangup_time` bigint(13) DEFAULT NULL COMMENT '挂断时间',
  `out_audio_bytes` bigint(13) DEFAULT NULL COMMENT '语音上行流量',
  `in_audio_bytes` bigint(13) DEFAULT NULL COMMENT '语音下行流量',
  `out_video_bytes` bigint(13) DEFAULT NULL COMMENT '视频上行流量',
  `in_video_bytes` bigint(13) DEFAULT NULL COMMENT '视屏下行流量',
  `call_type` int(2) DEFAULT NULL COMMENT '1-语音，2-视频',
  `duration` int(10) DEFAULT NULL COMMENT '时长,单位s',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流量产生动作',
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '成员',
  `network_type_id` int(2) DEFAULT NULL COMMENT '流量类型(3G,4G,Wifi)',
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 省/地区维表dim_region字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  `app_plat_id` int(2) DEFAULT NULL COMMENT '坐标登录处自取',
  `app_version_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '坐标版本号（自取）',
  `year` int(10) DEFAULT NULL COMMENT '年',
  `month` int(10) DEFAULT NULL COMMENT '月',
  `day` int(10) DEFAULT NULL COMMENT '日',
  `hour` int(10) DEFAULT NULL COMMENT '小时',
  `store_id` int(10) DEFAULT NULL COMMENT '分区',
  INDEX `idx_ods_multicall_log_ds_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_multicall_log_ds_range`(`store_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_engine_multicall_log_dm_201801
-- ----------------------------
DROP TABLE IF EXISTS `ods_engine_multicall_log_dm_201801`;
CREATE TABLE `ods_engine_multicall_log_dm_201801`  (
  `master` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发送者',
  `timestamp` bigint(13) DEFAULT NULL COMMENT '日志产生时间',
  `create_time` bigint(13) DEFAULT NULL COMMENT '语音创建时间',
  `answered_time` bigint(13) DEFAULT NULL COMMENT '应答时间',
  `profile_create_time` bigint(13) DEFAULT NULL COMMENT '通话开始时间',
  `hangup_time` bigint(13) DEFAULT NULL COMMENT '挂断时间',
  `out_audio_bytes` bigint(13) DEFAULT NULL COMMENT '语音上行流量',
  `in_audio_bytes` bigint(13) DEFAULT NULL COMMENT '语音下行流量',
  `out_video_bytes` bigint(13) DEFAULT NULL COMMENT '视频上行流量',
  `in_video_bytes` bigint(13) DEFAULT NULL COMMENT '视屏下行流量',
  `call_type` int(2) DEFAULT NULL COMMENT '1-语音，2-视频',
  `duration` int(10) DEFAULT NULL COMMENT '时长,单位s',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流量产生动作',
  `member` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '成员',
  `network_type_id` int(2) DEFAULT NULL COMMENT '流量类型(3G,4G,Wifi)',
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 省/地区维表dim_region字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  `app_plat_id` int(2) DEFAULT NULL COMMENT '坐标登录处自取',
  `app_version_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '坐标版本号（自取）',
  `year` int(10) DEFAULT NULL COMMENT '年',
  `month` int(10) DEFAULT NULL COMMENT '月',
  `day` int(10) DEFAULT NULL COMMENT '日',
  `hour` int(10) DEFAULT NULL COMMENT '小时',
  `store_id` int(10) DEFAULT NULL COMMENT '分区',
  INDEX `idx_ods_multicall_log_ds_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_multicall_log_ds_range`(`store_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_engine_singlecall_log_dm_201710
-- ----------------------------
DROP TABLE IF EXISTS `ods_engine_singlecall_log_dm_201710`;
CREATE TABLE `ods_engine_singlecall_log_dm_201710`  (
  `master` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发送者',
  `timestamp` bigint(13) DEFAULT NULL COMMENT '日志产生时间',
  `create_time` bigint(13) DEFAULT NULL COMMENT '语音创建时间',
  `answered_time` bigint(13) DEFAULT NULL COMMENT '应答时间',
  `profile_create_time` bigint(13) DEFAULT NULL COMMENT '通话开始时间',
  `hangup_time` bigint(13) DEFAULT NULL COMMENT '挂断时间',
  `out_audio_bytes` bigint(13) DEFAULT NULL COMMENT '语音上行流量',
  `in_audio_bytes` bigint(13) DEFAULT NULL COMMENT '语音下行流量',
  `out_video_bytes` bigint(13) DEFAULT NULL COMMENT '视频上行流量',
  `in_video_bytes` bigint(13) DEFAULT NULL COMMENT '视屏下行流量',
  `call_type` int(2) DEFAULT NULL COMMENT '1-语音，2-视频',
  `duration` int(10) DEFAULT NULL COMMENT '时长,单位s',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流量产生动作',
  `caller` int(10) DEFAULT NULL COMMENT '呼叫方',
  `callee` int(10) DEFAULT NULL COMMENT '被叫方',
  `network_type_id` int(2) DEFAULT NULL COMMENT '流量类型(3G,4G,Wifi)',
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 省/地区维表dim_region字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  `app_plat_id` int(2) DEFAULT NULL COMMENT '坐标登录处自取',
  `app_version_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '坐标版本号（自取）',
  `year` int(10) DEFAULT NULL COMMENT '年',
  `month` int(10) DEFAULT NULL COMMENT '月',
  `day` int(10) DEFAULT NULL COMMENT '日',
  `hour` int(10) DEFAULT NULL COMMENT '小时',
  `store_id` int(10) DEFAULT NULL COMMENT '分区',
  INDEX `idx_ods_message_log_ds_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_message_log_ds_range`(`store_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_engine_singlecall_log_dm_201801
-- ----------------------------
DROP TABLE IF EXISTS `ods_engine_singlecall_log_dm_201801`;
CREATE TABLE `ods_engine_singlecall_log_dm_201801`  (
  `master` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发送者',
  `timestamp` bigint(13) DEFAULT NULL COMMENT '日志产生时间',
  `create_time` bigint(13) DEFAULT NULL COMMENT '语音创建时间',
  `answered_time` bigint(13) DEFAULT NULL COMMENT '应答时间',
  `profile_create_time` bigint(13) DEFAULT NULL COMMENT '通话开始时间',
  `hangup_time` bigint(13) DEFAULT NULL COMMENT '挂断时间',
  `out_audio_bytes` bigint(13) DEFAULT NULL COMMENT '语音上行流量',
  `in_audio_bytes` bigint(13) DEFAULT NULL COMMENT '语音下行流量',
  `out_video_bytes` bigint(13) DEFAULT NULL COMMENT '视频上行流量',
  `in_video_bytes` bigint(13) DEFAULT NULL COMMENT '视屏下行流量',
  `call_type` int(2) DEFAULT NULL COMMENT '1-语音，2-视频',
  `duration` int(10) DEFAULT NULL COMMENT '时长,单位s',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流量产生动作',
  `caller` int(10) DEFAULT NULL COMMENT '呼叫方',
  `callee` int(10) DEFAULT NULL COMMENT '被叫方',
  `network_type_id` int(2) DEFAULT NULL COMMENT '流量类型(3G,4G,Wifi)',
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 省/地区维表dim_region字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  `app_plat_id` int(2) DEFAULT NULL COMMENT '坐标登录处自取',
  `app_version_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '坐标版本号（自取）',
  `year` int(10) DEFAULT NULL COMMENT '年',
  `month` int(10) DEFAULT NULL COMMENT '月',
  `day` int(10) DEFAULT NULL COMMENT '日',
  `hour` int(10) DEFAULT NULL COMMENT '小时',
  `store_id` int(10) DEFAULT NULL COMMENT '分区',
  INDEX `idx_ods_message_log_ds_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_message_log_ds_range`(`store_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_login_log_dm_201712
-- ----------------------------
DROP TABLE IF EXISTS `ods_login_log_dm_201712`;
CREATE TABLE `ods_login_log_dm_201712`  (
  `user_id` bigint(11) NOT NULL COMMENT '用户ID',
  `master` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '发送人',
  `oper_type` int(2) DEFAULT NULL COMMENT '操作类型（1-登陆 2-注销）',
  `timestamp` bigint(13) DEFAULT NULL COMMENT '登录登出时间',
  `oper_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '登陆登出IP地址',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作动作',
  `network_id` int(10) DEFAULT NULL COMMENT 'network 网络类型维表dim_network字典匹配自增获取',
  `channel` int(4) DEFAULT 1000 COMMENT '坐标渠道（1000-官方）',
  `app_plat_id` int(10) DEFAULT NULL COMMENT 'app_device_type  平台应用类型维表dim_app_plat字典匹配自增获取',
  `app_version_id` int(10) DEFAULT 2 COMMENT 'app_version 平台应用 版本维表dim_app_version字典匹配自增获取',
  `os_id` int(10) DEFAULT NULL COMMENT 'os_name 操作系统维表dim_os字典匹配自增获取',
  `os_version_id` int(10) DEFAULT NULL COMMENT 'os_version 操作系统版本维表dim_os_version字典匹配自增获取',
  `manufacturer_id` int(10) DEFAULT NULL COMMENT 'device_vendor 设备生产厂家维表dim_manufacturer字典匹配自增获取',
  `manufacturer_model_id` int(10) DEFAULT NULL COMMENT 'device_model 设备型号维表dim_manufacturer_model字典匹配自增获取',
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 省/地区维表dim_region字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  `year` int(9) NOT NULL DEFAULT 1900 COMMENT '年',
  `month` int(9) NOT NULL DEFAULT 0 COMMENT '月',
  `day` int(9) NOT NULL DEFAULT 0 COMMENT '日',
  `hour` int(9) NOT NULL DEFAULT 0 COMMENT '小时',
  `store_id` int(9) NOT NULL DEFAULT 19000101 COMMENT '时间id',
  INDEX `idx_ods_login_log_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_login_log_range`(`store_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_login_log_dm_201801
-- ----------------------------
DROP TABLE IF EXISTS `ods_login_log_dm_201801`;
CREATE TABLE `ods_login_log_dm_201801`  (
  `user_id` bigint(11) NOT NULL COMMENT '用户ID',
  `master` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '发送人',
  `oper_type` int(2) DEFAULT NULL COMMENT '操作类型（1-登陆 2-注销）',
  `timestamp` bigint(13) DEFAULT NULL COMMENT '登录登出时间',
  `oper_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '登陆登出IP地址',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作动作',
  `network_id` int(10) DEFAULT NULL COMMENT 'network 网络类型维表dim_network字典匹配自增获取',
  `channel` int(4) DEFAULT 1000 COMMENT '坐标渠道（1000-官方）',
  `app_plat_id` int(10) DEFAULT NULL COMMENT 'app_device_type  平台应用类型维表dim_app_plat字典匹配自增获取',
  `app_version_id` int(10) DEFAULT 2 COMMENT 'app_version 平台应用 版本维表conf_app_version字典匹配自增获取',
  `os_id` int(10) DEFAULT NULL COMMENT 'os_name 操作系统维表dim_os字典匹配自增获取',
  `os_version_id` int(10) DEFAULT NULL COMMENT 'os_version 操作系统版本维表dim_os_version字典匹配自增获取',
  `manufacturer_id` int(10) DEFAULT NULL COMMENT 'device_vendor 设备生产厂家维表dim_manufacturer字典匹配自增获取',
  `manufacturer_model_id` int(10) DEFAULT NULL COMMENT 'device_model 设备型号维表dim_manufacturer_model字典匹配自增获取',
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 省/地区维表dim_region字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  `year` int(9) NOT NULL DEFAULT 1900 COMMENT '年',
  `month` int(9) NOT NULL DEFAULT 0 COMMENT '月',
  `day` int(9) NOT NULL DEFAULT 0 COMMENT '日',
  `hour` int(9) NOT NULL DEFAULT 0 COMMENT '小时',
  `store_id` int(9) NOT NULL DEFAULT 19000101 COMMENT '时间id',
  INDEX `idx_ods_login_log_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_login_log_range`(`store_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_schedule_dm_201801
-- ----------------------------
DROP TABLE IF EXISTS `ods_schedule_dm_201801`;
CREATE TABLE `ods_schedule_dm_201801`  (
  `master_id` int(10) NOT NULL COMMENT '用户id',
  `sche_id` int(10) DEFAULT NULL COMMENT '日程id',
  `status` int(2) DEFAULT NULL COMMENT '状态（0未完成，1已完成）',
  `action` int(2) DEFAULT NULL COMMENT '1:表示日程添加,2:delete日程删除,3:日程标注完成,4:日程分享',
  `app_plat_id` int(4) DEFAULT NULL COMMENT '设备',
  `app_version_id` int(4) DEFAULT NULL COMMENT '版本',
  `app_id` int(4) DEFAULT NULL COMMENT '应用id',
  `create_timestamp` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `start_timestamp` bigint(20) DEFAULT NULL COMMENT '开始时间',
  `update_timestamp` bigint(20) DEFAULT NULL COMMENT '修改时间',
  `end_timestamp` bigint(20) DEFAULT NULL COMMENT '结束时间',
  `year` int(4) DEFAULT NULL,
  `month` int(4) DEFAULT NULL,
  `day` int(4) DEFAULT NULL,
  `hour` int(4) DEFAULT NULL,
  `statis_date` int(10) DEFAULT NULL,
  INDEX `idx_schedule_yyyymm`(`statis_date`, `master_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_user_info_dm_201712
-- ----------------------------
DROP TABLE IF EXISTS `ods_user_info_dm_201712`;
CREATE TABLE `ods_user_info_dm_201712`  (
  `user_id` bigint(13) UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户id，主键（自动增长）',
  `new_user` tinyint(4) NOT NULL COMMENT '0-新用户 1-老用户',
  `display_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
  `face_src` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '租户图像地址',
  `large_face_src` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `small_face_src` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `qr_code` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '二维码路径',
  `update_time` bigint(21) DEFAULT 0 COMMENT '最后更新时间戳',
  `login_verfication_type` int(1) DEFAULT 0 COMMENT '是否设置密码标记 -1为手机登录生成账号未设置密码',
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮箱',
  `register_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '127.0.0.1' COMMENT '租户注册IP地址',
  `cube` int(10) DEFAULT 0 COMMENT '租户注册IP地址',
  `zb_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '0',
  `login_ip` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '127.0.0.1' COMMENT '当前登录IP地址',
  `login_time` bigint(13) DEFAULT 0 COMMENT '登录时间',
  `cube_cloud` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所在服务器',
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系电话',
  `sex` int(1) DEFAULT 1 COMMENT '1-男 2-女',
  `create_time` bigint(21) DEFAULT 0 COMMENT '创建时间',
  `profile_email` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系邮箱',
  `profile_mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系电话',
  `profile_birth_time` bigint(13) DEFAULT NULL,
  `profile_industry` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `email_activation_time` bigint(13) DEFAULT 0 COMMENT '邮箱激活时间',
  `company` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `job` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `province` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `city` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `county` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `guest` tinyint(4) DEFAULT 0 COMMENT '0-注册用户 1-游客',
  `age` int(10) DEFAULT NULL COMMENT 'profile_birth_time 解析生日获取年龄',
  `industry_id` int(10) DEFAULT NULL COMMENT 'industry_name 行业维表dim_industry字典匹配自增获取',
  `app_plat_id` int(10) DEFAULT NULL COMMENT 'app_device_type  平台应用类型维表dim_app_plat字典匹配自增获取',
  `app_version_id` int(10) DEFAULT 2 COMMENT 'app_version 平台应用 版本维表dim_app_version字典匹配自增获取',
  `os_id` int(10) DEFAULT NULL COMMENT 'os_name 操作系统维表dim_os字典匹配自增获取',
  `os_version_id` int(10) DEFAULT NULL COMMENT 'os_version 操作系统版本维表dim_os_version字典匹配自增获取',
  `manufacturer_id` int(10) DEFAULT NULL COMMENT 'device_vendor 设备生产厂家维表dim_manufacturer字典匹配自增获取',
  `manufacturer_model_id` int(10) DEFAULT NULL COMMENT 'device_model 设备型号维表dim_manufacturer_model字典匹配自增获取',
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  `channel` int(4) DEFAULT 1000 COMMENT '坐标渠道（1000-官方）',
  `year` int(9) NOT NULL DEFAULT 1900 COMMENT '年',
  `month` int(9) NOT NULL DEFAULT 0 COMMENT '月',
  `network` int(4) DEFAULT NULL,
  `day` int(9) NOT NULL DEFAULT 0 COMMENT '日',
  `hour` int(9) NOT NULL DEFAULT 0 COMMENT '小时',
  `register_date` int(9) NOT NULL DEFAULT 0 COMMENT '注册时间',
  `insert_date` int(9) NOT NULL DEFAULT 0 COMMENT '插入ods时间',
  INDEX `idx_ods_user_info_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_user_info_range`(`insert_date`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ods_user_info_dm_201801
-- ----------------------------
DROP TABLE IF EXISTS `ods_user_info_dm_201801`;
CREATE TABLE `ods_user_info_dm_201801`  (
  `user_id` bigint(13) UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户id，主键（自动增长）',
  `new_user` tinyint(4) NOT NULL COMMENT '0-新用户 1-老用户',
  `display_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
  `face_src` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '租户图像地址',
  `large_face_src` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `small_face_src` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `qr_code` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '二维码路径',
  `update_time` bigint(21) DEFAULT 0 COMMENT '最后更新时间戳',
  `login_verfication_type` int(1) DEFAULT 0 COMMENT '是否设置密码标记 -1为手机登录生成账号未设置密码',
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮箱',
  `register_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '127.0.0.1' COMMENT '租户注册IP地址',
  `cube` int(10) DEFAULT 0 COMMENT '租户注册IP地址',
  `zb_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '0',
  `login_ip` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '127.0.0.1' COMMENT '当前登录IP地址',
  `login_time` bigint(13) DEFAULT 0 COMMENT '登录时间',
  `cube_cloud` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所在服务器',
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系电话',
  `sex` int(1) DEFAULT 1 COMMENT '1-男 2-女',
  `create_time` bigint(21) DEFAULT 0 COMMENT '创建时间',
  `profile_email` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系邮箱',
  `profile_mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '联系电话',
  `profile_birth_time` bigint(13) DEFAULT NULL,
  `profile_industry` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `email_activation_time` bigint(13) DEFAULT 0 COMMENT '邮箱激活时间',
  `company` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `job` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `province` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `city` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `county` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `guest` tinyint(4) DEFAULT 0 COMMENT '0-注册用户 1-游客',
  `age` int(10) DEFAULT NULL COMMENT 'profile_birth_time 解析生日获取年龄',
  `industry_id` int(10) DEFAULT NULL COMMENT 'industry_name 行业维表dim_industry字典匹配自增获取',
  `app_plat_id` int(10) DEFAULT NULL COMMENT 'app_device_type  平台应用类型维表dim_app_plat字典匹配自增获取',
  `app_version_id` int(10) DEFAULT 2 COMMENT 'app_version 平台应用 版本维表conf_app_version字典匹配自增获取',
  `os_id` int(10) DEFAULT NULL COMMENT 'os_name 操作系统维表dim_os字典匹配自增获取',
  `os_version_id` int(10) DEFAULT NULL COMMENT 'os_version 操作系统版本维表dim_os_version字典匹配自增获取',
  `manufacturer_id` int(10) DEFAULT NULL COMMENT 'device_vendor 设备生产厂家维表dim_manufacturer字典匹配自增获取',
  `manufacturer_model_id` int(10) DEFAULT NULL COMMENT 'device_model 设备型号维表dim_manufacturer_model字典匹配自增获取',
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  `channel` int(4) DEFAULT 1000 COMMENT '坐标渠道（1000-官方）',
  `year` int(9) NOT NULL DEFAULT 1900 COMMENT '年',
  `month` int(9) NOT NULL DEFAULT 0 COMMENT '月',
  `network` int(4) DEFAULT NULL,
  `day` int(9) NOT NULL DEFAULT 0 COMMENT '日',
  `hour` int(9) NOT NULL DEFAULT 0 COMMENT '小时',
  `register_date` int(9) NOT NULL DEFAULT 0 COMMENT '注册时间',
  `insert_date` int(9) NOT NULL DEFAULT 0 COMMENT '插入ods时间',
  INDEX `idx_ods_user_info_date`(`year`, `month`, `day`) USING BTREE,
  INDEX `idx_ods_user_info_range`(`insert_date`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
