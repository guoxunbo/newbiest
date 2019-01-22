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

 Date: 22/01/2019 11:12:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_REFERENCE_NAME
-- ----------------------------
DROP TABLE IF EXISTS `NB_REFERENCE_NAME`;
CREATE TABLE `NB_REFERENCE_NAME` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(64) DEFAULT NULL COMMENT '名字',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `category` varchar(32) DEFAULT NULL COMMENT '类别',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_REF_NAME_ORG_RRN_NAME_CATEGORY` (`org_rrn`,`name`,`category`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='Reference Name Info';

-- ----------------------------
-- Records of NB_REFERENCE_NAME
-- ----------------------------
INSERT INTO `NB_REFERENCE_NAME` VALUES (2, 'Y', 0, 'Language', '语言', 'System');
INSERT INTO `NB_REFERENCE_NAME` VALUES (3, 'Y', 0, 'FieldDataType', '栏位数据类型', 'System');
INSERT INTO `NB_REFERENCE_NAME` VALUES (4, 'Y', 0, 'FieldDisplayType', '栏位显示类型', 'System');
INSERT INTO `NB_REFERENCE_NAME` VALUES (5, 'Y', 0, 'TabType', 'Tab类型', 'System');
INSERT INTO `NB_REFERENCE_NAME` VALUES (13, 'Y', 1, 'RecipeCategroy', 'Recipe类型', 'System');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
