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

 Date: 02/01/2019 11:59:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_REFERENCE_TABLE
-- ----------------------------
DROP TABLE IF EXISTS `NB_REFERENCE_TABLE`;
CREATE TABLE `NB_REFERENCE_TABLE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `name` varchar(64) DEFAULT NULL COMMENT '名字',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `TABLE_RRN` bigint(20) DEFAULT NULL COMMENT '对应nbTable的主键',
  `KEY_FIELD` varchar(64) DEFAULT NULL COMMENT '参考表所对应Key栏位, 保存到数据库中的值',
  `TEXT_FIELD` varchar(128) DEFAULT NULL COMMENT '参考表所对应Text栏位,显示在页面上的值',
  `WHERE_CLAUSE` varchar(128) DEFAULT NULL COMMENT '查询条件',
  `ORDER_BY` varchar(128) DEFAULT NULL COMMENT '排序条件',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `org_rrn` (`org_rrn`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='ReferenceTable Info';

-- ----------------------------
-- Records of NB_REFERENCE_TABLE
-- ----------------------------
INSERT INTO `NB_REFERENCE_TABLE` VALUES (1, 'Y', 0, 'NBOrg', '区域', 1, 'objectRrn', 'name', NULL, NULL);
INSERT INTO `NB_REFERENCE_TABLE` VALUES (2, 'Y', 0, 'NBTable', '动态表', 2, 'objectRrn', 'name', NULL, NULL);
INSERT INTO `NB_REFERENCE_TABLE` VALUES (3, 'Y', 0, 'NBTabByTable', '动态Tab', 65, 'objectRrn', 'name', 'tableRrn = :tableRrn', NULL);
INSERT INTO `NB_REFERENCE_TABLE` VALUES (4, 'Y', 0, 'NBRefTable', '栏位参考表', 68, 'name', 'name', NULL, NULL);
INSERT INTO `NB_REFERENCE_TABLE` VALUES (5, 'Y', 0, 'NBTableByName', '动态表名称', 2, 'name', 'name', NULL, NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
