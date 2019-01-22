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

 Date: 22/01/2019 11:12:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_ROLE
-- ----------------------------
DROP TABLE IF EXISTS `NB_ROLE`;
CREATE TABLE `NB_ROLE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `role_id` varchar(64) DEFAULT NULL COMMENT '角色名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_ROLE_ORG_RRN_ROLE_ID` (`org_rrn`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='Role Info';

-- ----------------------------
-- Records of NB_ROLE
-- ----------------------------
INSERT INTO `NB_ROLE` VALUES (5, 'Y', 1, '2018-12-26 13:18:12', NULL, '2019-01-21 19:03:54', NULL, 285, 'TestRole', '测试角色1');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
