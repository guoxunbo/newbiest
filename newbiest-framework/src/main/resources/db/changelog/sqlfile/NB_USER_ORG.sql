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

 Date: 22/01/2019 11:13:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_USER_ORG
-- ----------------------------
DROP TABLE IF EXISTS `NB_USER_ORG`;
CREATE TABLE `NB_USER_ORG` (
  `org_rrn` bigint(20) DEFAULT NULL COMMENT '区域主键',
  `user_rrn` bigint(20) DEFAULT NULL COMMENT '用户主键',
  KEY `fk_user_org_userrrn` (`user_rrn`),
  KEY `fk_user_org_orgrrn` (`org_rrn`),
  CONSTRAINT `fk_user_org_orgrrn` FOREIGN KEY (`org_rrn`) REFERENCES `NB_ORG` (`object_rrn`),
  CONSTRAINT `fk_user_org_userrrn` FOREIGN KEY (`user_rrn`) REFERENCES `NB_USER` (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='User Org Info';

-- ----------------------------
-- Records of NB_USER_ORG
-- ----------------------------
INSERT INTO `NB_USER_ORG` VALUES (1, 2);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
