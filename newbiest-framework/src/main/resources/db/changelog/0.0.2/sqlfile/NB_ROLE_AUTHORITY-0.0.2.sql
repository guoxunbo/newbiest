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

 Date: 29/12/2018 16:22:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_ROLE_AUTHORITY
-- ----------------------------
DROP TABLE IF EXISTS `NB_ROLE_AUTHORITY`;
CREATE TABLE `NB_ROLE_AUTHORITY` (
  `role_rrn` bigint(20) DEFAULT NULL COMMENT '角色主键',
  `authority_rrn` bigint(20) DEFAULT NULL COMMENT '功能主键',
  KEY `fk_role_authority_rolerrn` (`role_rrn`),
  KEY `fk_role_authority_authorityrrn` (`authority_rrn`),
  CONSTRAINT `fk_role_authority_authorityrrn` FOREIGN KEY (`authority_rrn`) REFERENCES `NB_AUTHORITY` (`object_rrn`),
  CONSTRAINT `fk_role_authority_rolerrn` FOREIGN KEY (`role_rrn`) REFERENCES `NB_ROLE` (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Role Authority Info';

-- ----------------------------
-- Records of NB_ROLE_AUTHORITY
-- ----------------------------
INSERT INTO `NB_ROLE_AUTHORITY` VALUES (5, 15);
INSERT INTO `NB_ROLE_AUTHORITY` VALUES (5, 17);
INSERT INTO `NB_ROLE_AUTHORITY` VALUES (5, 19);
INSERT INTO `NB_ROLE_AUTHORITY` VALUES (5, 3);
INSERT INTO `NB_ROLE_AUTHORITY` VALUES (5, 14);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
