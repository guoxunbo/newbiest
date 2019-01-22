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

 Date: 29/12/2018 16:22:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_RELATION
-- ----------------------------
DROP TABLE IF EXISTS `NB_RELATION`;
CREATE TABLE `NB_RELATION` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `relation_type` varchar(32) DEFAULT NULL COMMENT '关系类型 Sql/普通的SQL关联， Class/通过HQL关联',
  `source` varchar(32) DEFAULT NULL COMMENT '源对象',
  `source_relation_field` varchar(32) DEFAULT NULL COMMENT '源对象关联的栏位',
  `target` varchar(256) DEFAULT NULL COMMENT '目标对象',
  `target_relation_field` varchar(1024) DEFAULT NULL COMMENT '目标对象关联的栏位',
  PRIMARY KEY (`object_rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Relation Info. 在一些特定的环境下，模拟一些需要关系卡控的。比如在特定的需求下，这个用户组突然要关联一些东西，但是在起初的设计没有设计这块。通过这个实现';

SET FOREIGN_KEY_CHECKS = 1;
