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

 Date: 22/01/2019 11:13:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_USER
-- ----------------------------
DROP TABLE IF EXISTS `NB_USER`;
CREATE TABLE `NB_USER` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `description` varchar(256) DEFAULT NULL COMMENT '描述 比如名字',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `pwd_changed` datetime DEFAULT NULL COMMENT '密码修改时间',
  `pwd_expiry` datetime DEFAULT NULL COMMENT '密码过期时间',
  `pwd_life` bigint(20) DEFAULT NULL COMMENT '密码有效期',
  `pwd_wrong_count` bigint(20) DEFAULT NULL COMMENT '密码错误次数',
  `phone` varchar(32) DEFAULT NULL COMMENT '电话',
  `department` varchar(32) DEFAULT NULL COMMENT '部门',
  `sex` varchar(1) DEFAULT NULL COMMENT '性别 M/Man F/Feman',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱地址',
  `in_valid_flag` varchar(1) DEFAULT NULL COMMENT '是否在密码有效期之内',
  `last_logon` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_ORG_RRN_USERNAME` (`org_rrn`,`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='User Info';

-- ----------------------------
-- Records of NB_USER
-- ----------------------------
INSERT INTO `NB_USER` VALUES (1, 'Y', 0, '2018-06-06 22:17:22', NULL, '2019-01-21 19:33:48', NULL, 281, 'admin', '管理员', 'c4ca4238a0b923820dcc509a6f75849b', '2018-12-25 12:02:04', NULL, NULL, 0, NULL, NULL, NULL, '11603652@qq.com', 'Y', '2019-01-21 19:33:48');
INSERT INTO `NB_USER` VALUES (2, 'Y', 0, '2018-12-25 11:11:40', NULL, '2019-01-11 13:41:22', NULL, 15, 'test', '测试用户1', 'c4ca4238a0b923820dcc509a6f75849b', '2018-12-25 12:01:29', '2019-10-21 12:01:29', 300, 0, '', NULL, NULL, '116036512@qq.com', 'Y', '2018-12-29 10:52:00');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
