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

 Date: 29/12/2018 16:23:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_USER_HIS
-- ----------------------------
DROP TABLE IF EXISTS `NB_USER_HIS`;
CREATE TABLE `NB_USER_HIS` (
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
  `role_list` varchar(256) DEFAULT NULL COMMENT '角色列表',
  `org_list` varchar(64) DEFAULT NULL COMMENT '区域列表',
  `last_logon` datetime DEFAULT CURRENT_TIMESTAMP,
  `trans_type` varchar(32) DEFAULT NULL COMMENT '操作类型',
  `action_code` varchar(32) DEFAULT NULL COMMENT '原因码',
  `action_comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `action_reason` varchar(256) DEFAULT NULL COMMENT '原因',
  `history_seq` varchar(64) DEFAULT NULL COMMENT '序列号 来找到同个事务的不同操作记录',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=290 DEFAULT CHARSET=utf8 COMMENT='User History Info';

SET FOREIGN_KEY_CHECKS = 1;
