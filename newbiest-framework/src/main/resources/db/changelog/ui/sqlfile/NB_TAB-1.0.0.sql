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

 Date: 29/12/2018 16:22:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_TAB
-- ----------------------------
DROP TABLE IF EXISTS `NB_TAB`;
CREATE TABLE `NB_TAB` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(64) DEFAULT NULL COMMENT '名字',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `TABLE_RRN` bigint(20) DEFAULT NULL COMMENT '动态表主键',
  `SEQ_NO` bigint(20) DEFAULT NULL COMMENT '顺序',
  `TAB_TYPE` varchar(256) DEFAULT 'Tab' COMMENT '样式',
  `LABEL` varchar(128) DEFAULT NULL COMMENT '英文标签',
  `LABEL_ZH` varchar(128) DEFAULT NULL COMMENT '中文标签',
  `LABEL_RES` varchar(128) DEFAULT NULL COMMENT '其他语言标签',
  `REF_TABLE_NAME` varchar(64) DEFAULT NULL COMMENT '关联的动态表名称',
  `EDIT_FLAG` varchar(1) DEFAULT 'N' COMMENT '可以编辑',
  `WHERE_CLAUSE` varchar(256) DEFAULT NULL COMMENT '当类型是table的时候和主对象的关联关系',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `TABLE_RRN` (`TABLE_RRN`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='tab Info';

-- ----------------------------
-- Records of NB_TAB
-- ----------------------------
INSERT INTO `NB_TAB` VALUES (1, 'Y', 0, 'BasicInfo', '基本信息', 2, 10, 'Field', 'BasicInfo', '基本信息', '', NULL, 'N', NULL);
INSERT INTO `NB_TAB` VALUES (2, 'Y', 0, 'TabInfo', 'Tab信息', 2, 20, 'Table', 'TabInfo', 'Tab信息', NULL, 'NBTab', 'N', 'tableRrn = :objectRrn');
INSERT INTO `NB_TAB` VALUES (3, 'Y', 0, 'FieldInfo', '字段信息', 2, 30, 'Table', 'FieldInfo', '字段信息', NULL, 'NBField', 'N', 'tableRrn = :objectRrn');
INSERT INTO `NB_TAB` VALUES (4, 'Y', 0, 'BasicInfo', '基本信息', 66, 10, 'Field', 'BasicInfo', '基本信息', NULL, NULL, 'N', NULL);
INSERT INTO `NB_TAB` VALUES (5, 'Y', 0, 'QueryInfo', '查询信息', 66, 30, 'Field', 'QueryInfo', '查询信息', NULL, NULL, 'N', NULL);
INSERT INTO `NB_TAB` VALUES (6, 'Y', 0, 'DisplayInfo', '查询信息', 66, 20, 'Field', 'DisplayInfo', '显示信息', NULL, NULL, 'N', NULL);
INSERT INTO `NB_TAB` VALUES (7, 'Y', 0, 'FieldInfo', '字段信息', 65, 10, 'Table', 'FieldInfo', '字段信息', NULL, 'NBField', NULL, 'tabRrn = :objectRrn');
INSERT INTO `NB_TAB` VALUES (8, 'Y', 1, 'BasicInfo', '基本信息', 65, 5, 'Field', 'BasicInfo', '基本信息', NULL, NULL, NULL, NULL);
INSERT INTO `NB_TAB` VALUES (9, 'Y', 0, 'SystemRefValueInfo', '系统栏位参考值', 67, 10, 'Table', 'SystemRefValueInfo', '系统栏位参考值', NULL, 'NBSystemRefList', 'Y', 'referenceName = :name');
INSERT INTO `NB_TAB` VALUES (10, 'Y', 0, 'OwnerRefValueInfo', '区域参考值', 70, 10, 'Table', 'OwnerRefValueInfo', '区域栏位参考值', NULL, 'NBOwnerRefList', 'Y', 'referenceName = :name');
INSERT INTO `NB_TAB` VALUES (11, 'Y', 1, 'ContactInfo', '联系信息', 72, 10, 'Field', 'ContactInfo', '联系信息', NULL, NULL, 'N', NULL);
INSERT INTO `NB_TAB` VALUES (12, 'Y', 1, 'PasswordRelated', '密码相关', 72, 5, 'Field', 'PwdRelated', '密码相关', NULL, NULL, 'N', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
