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

 Date: 29/12/2018 16:23:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_USER_ROLE
-- ----------------------------
DROP TABLE IF EXISTS `NB_USER_ROLE`;
CREATE TABLE `NB_USER_ROLE` (
  `role_rrn` bigint(20) DEFAULT NULL COMMENT '角色主键',
  `user_rrn` bigint(20) DEFAULT NULL COMMENT '用户主键',
  KEY `fk_user_role_rolerrn` (`role_rrn`),
  KEY `fk_user_role_userrrn` (`user_rrn`),
  CONSTRAINT `fk_user_role_rolerrn` FOREIGN KEY (`role_rrn`) REFERENCES `NB_ROLE` (`object_rrn`),
  CONSTRAINT `fk_user_role_userrrn` FOREIGN KEY (`user_rrn`) REFERENCES `NB_USER` (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='User Role Info';

-- ----------------------------
-- Records of NB_USER_ROLE
-- ----------------------------
INSERT INTO `NB_USER_ROLE` VALUES (5, 2);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
