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

 Date: 29/12/2018 16:22:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_ROLE_HIS
-- ----------------------------
DROP TABLE IF EXISTS `NB_ROLE_HIS`;
CREATE TABLE `NB_ROLE_HIS` (
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
  `users` varchar(256) DEFAULT NULL COMMENT '所有用户',
  `authorities` varchar(256) DEFAULT NULL COMMENT '所有权限',
  `trans_type` varchar(32) DEFAULT NULL COMMENT '操作类型',
  `action_code` varchar(32) DEFAULT NULL COMMENT '原因码',
  `action_comment` varchar(256) DEFAULT NULL COMMENT '备注',
  `action_reason` varchar(256) DEFAULT NULL COMMENT '原因',
  `history_seq` varchar(64) DEFAULT NULL COMMENT '序列号 来找到同个事务的不同操作记录',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='Role History Info';

SET FOREIGN_KEY_CHECKS = 1;
