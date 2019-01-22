/*
 Navicat Premium Data Transfer

 Source Server         : butterfly
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : localhost:3306
 Source Schema         : butterfly

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 22/01/2019 11:12:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_QUERY
-- ----------------------------
DROP TABLE IF EXISTS `NB_QUERY`;
CREATE TABLE `NB_QUERY` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `query_text` varchar(1024) DEFAULT NULL COMMENT '查询的SQL',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_QUERY_ORG_RRN_NAME` (`org_rrn`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='QueryText Info';

SET FOREIGN_KEY_CHECKS = 1;
