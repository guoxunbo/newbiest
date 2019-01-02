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

 Date: 02/01/2019 11:59:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_REFERENCE_LIST
-- ----------------------------
DROP TABLE IF EXISTS `NB_REFERENCE_LIST`;
CREATE TABLE `NB_REFERENCE_LIST` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `REFERENCE_NAME` varchar(64) DEFAULT NULL COMMENT '参考名称',
  `KEY` varchar(256) DEFAULT NULL COMMENT '保存到数据库中的值',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `VALUE` varchar(256) DEFAULT NULL COMMENT '显示值',
  `SEQ_NO` bigint(20) DEFAULT NULL COMMENT '序号',
  `category` varchar(32) DEFAULT NULL COMMENT '类别',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COMMENT='Reference list Info';

-- ----------------------------
-- Records of NB_REFERENCE_LIST
-- ----------------------------
INSERT INTO `NB_REFERENCE_LIST` VALUES (2, 'Y', 0, 'Language', 'Chinese', '中文', 'Chinese', 10, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (3, 'Y', 0, 'Language', 'English', '英文', 'English', 20, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (4, 'Y', 0, 'FieldDataType', 'string', '字符串', 'string', 10, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (5, 'Y', 0, 'FieldDataType', 'int', '整数', 'int', 20, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (6, 'Y', 0, 'FieldDataType', 'double', '小数', 'double', 30, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (7, 'Y', 0, 'FieldDisplayType', 'text', '文本', 'text', 10, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (8, 'Y', 0, 'FieldDisplayType', 'password', '密码', 'password', 20, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (9, 'Y', 0, 'FieldDisplayType', 'calendar', '日期', 'calendar', 30, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (10, 'Y', 0, 'FieldDisplayType', 'calendarFromTo', 'calendarFromTo', 'calendarFromTo', 40, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (11, 'Y', 0, 'FieldDisplayType', 'datetime', '时间', 'datetime', 50, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (12, 'Y', 0, 'FieldDisplayType', 'datetimeFromTo', 'datetimeFromTo', 'datetimeFromTo', 60, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (13, 'Y', 0, 'FieldDisplayType', 'sysRefList', '系统栏位参考值', 'sysRefList', 70, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (14, 'Y', 0, 'FieldDisplayType', 'userRefList', '用户栏位参考值', 'userRefList', 80, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (15, 'Y', 0, 'FieldDisplayType', 'referenceTable', '系统栏位参考表', 'referenceTable', 90, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (16, 'Y', 0, 'FieldDisplayType', 'radio', '选择框', 'radio', 100, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (17, 'Y', 0, 'TabType', 'Field', '栏位显示', 'Field', 10, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (18, 'Y', 0, 'TabType', 'Table', '表格显示', 'Table', 20, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (29, 'Y', 1, 'FieldDisplayType', 'int', '整数', 'int', 12, 'System');
INSERT INTO `NB_REFERENCE_LIST` VALUES (30, 'Y', 1, 'FieldDisplayType', 'double', '小数', 'double', 13, 'System');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
