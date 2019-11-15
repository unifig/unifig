/*
 Navicat Premium Data Transfer

 Source Server         : 125.208.1.67-root
 Source Server Type    : MySQL
 Source Server Version : 50711
 Source Host           : 125.208.1.67:33665
 Source Schema         : bi_online

 Target Server Type    : MySQL
 Target Server Version : 50711
 File Encoding         : 65001

 Date: 10/01/2018 16:55:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for all_sche_log_all_store
-- ----------------------------
DROP TABLE IF EXISTS `all_sche_log_all_store`;
CREATE TABLE `all_sche_log_all_store`  (
  `master_id` int(10) DEFAULT NULL COMMENT 'master_id',
  `sche_num` int(10) DEFAULT NULL COMMENT '日程总数',
  INDEX `idx_schedule`(`master_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for all_time_log_201712
-- ----------------------------
DROP TABLE IF EXISTS `all_time_log_201712`;
CREATE TABLE `all_time_log_201712`  (
  `timestamp` bigint(20) DEFAULT NULL,
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `cube_id` int(4) DEFAULT NULL,
  `plat_id` int(4) DEFAULT NULL,
  `ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 省/地区维表dim_region字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  INDEX `idx_all_time_log_201712`(`cube_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for all_time_log_201801
-- ----------------------------
DROP TABLE IF EXISTS `all_time_log_201801`;
CREATE TABLE `all_time_log_201801`  (
  `timestamp` bigint(20) DEFAULT NULL,
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `cube_id` int(4) DEFAULT NULL,
  `plat_id` int(4) DEFAULT NULL,
  `ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `isp_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 固网运营商字典维表dim_isp字典匹配自增获取',
  `country_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 国家字典表维表dim_country字典匹配自增获取',
  `region_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 省/地区维表dim_region字典匹配自增获取',
  `city_id` int(10) DEFAULT NULL COMMENT 'oper_ip IP地址庫 地市维表dim_city字典匹配自增获取',
  INDEX `idx_all_time_log_201801`(`cube_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for all_time_log_all_store
-- ----------------------------
DROP TABLE IF EXISTS `all_time_log_all_store`;
CREATE TABLE `all_time_log_all_store`  (
  `cube_id` int(10) NOT NULL COMMENT 'cube_id',
  `plat_id` int(4) NOT NULL,
  `online_time` bigint(20) DEFAULT NULL COMMENT '在线时长',
  `status` int(4) DEFAULT 0 COMMENT '在线状态:0：下线；1：在线',
  PRIMARY KEY (`cube_id`, `plat_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for all_time_tmp_all_store
-- ----------------------------
DROP TABLE IF EXISTS `all_time_tmp_all_store`;
CREATE TABLE `all_time_tmp_all_store`  (
  `cube_id` int(10) NOT NULL COMMENT 'cube_id',
  `plat_id` int(4) NOT NULL,
  `hashid` bigint(20) DEFAULT NULL,
  `online_time` bigint(20) DEFAULT NULL COMMENT '在线时长'
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '单次在线时长统计' ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for login_log_all_store
-- ----------------------------
DROP TABLE IF EXISTS `login_log_all_store`;
CREATE TABLE `login_log_all_store`  (
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `cube_id` int(4) DEFAULT NULL,
  `plat_id` int(4) DEFAULT NULL,
  `timestamp` bigint(20) DEFAULT NULL,
  `hashid` bigint(20) NOT NULL,
  INDEX `idx_login_log_all_store`(`hashid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for logout_log_all_store
-- ----------------------------
DROP TABLE IF EXISTS `logout_log_all_store`;
CREATE TABLE `logout_log_all_store`  (
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `cube_id` int(4) DEFAULT NULL,
  `plat_id` int(4) DEFAULT NULL,
  `timestamp` bigint(20) DEFAULT NULL,
  `hashid` bigint(20) NOT NULL,
  INDEX `idx_logout_log_all_store`(`hashid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
