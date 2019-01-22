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

 Date: 22/01/2019 11:11:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for NB_MESSAGE
-- ----------------------------
DROP TABLE IF EXISTS `NB_MESSAGE`;
CREATE TABLE `NB_MESSAGE` (
  `object_rrn` bigint(20) NOT NULL AUTO_INCREMENT,
  `active_flag` varchar(1) DEFAULT 'Y' COMMENT '数据是否可用',
  `org_rrn` bigint(20) DEFAULT '0' COMMENT '区域号',
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建者',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '更新者',
  `lock_version` bigint(20) DEFAULT '1' COMMENT '乐观锁版本 程序自己更新 不可人为改变',
  `key_id` varchar(64) DEFAULT NULL COMMENT '消息名称',
  `message` varchar(256) DEFAULT NULL COMMENT '英文消息',
  `message_zh` varchar(256) DEFAULT NULL COMMENT '中文消息',
  `message_res` varchar(256) DEFAULT NULL COMMENT '其他消息',
  PRIMARY KEY (`object_rrn`),
  UNIQUE KEY `UK_MESSAGE_ORG_RRN_NAME` (`org_rrn`,`key_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='I18n Message Info';

-- ----------------------------
-- Records of NB_MESSAGE
-- ----------------------------
INSERT INTO `NB_MESSAGE` VALUES (1, 'Y', 0, '2018-06-06 22:17:21', 'admin', '2018-06-06 22:17:21', 'admin', 1, 'common.system_occurred_error', 'System error. Please call administrator', '系统错误, 请联系管理员', NULL);
INSERT INTO `NB_MESSAGE` VALUES (2, 'Y', 0, '2018-06-06 22:17:21', 'admin', '2018-06-06 22:17:21', 'admin', 1, 'security.user_is_not_found', 'The user is not found', '用户不存在', NULL);
INSERT INTO `NB_MESSAGE` VALUES (3, 'Y', 0, '2018-06-06 22:17:21', 'admin', '2018-06-06 22:17:21', 'admin', 1, 'security.user_is_not_in_validation', 'The user is not validation', '请先修改密码', NULL);
INSERT INTO `NB_MESSAGE` VALUES (4, 'Y', 0, '2018-06-06 22:17:21', 'admin', '2018-06-06 22:17:21', 'admin', 1, 'security.wrong_pwd_more_than_count', 'Too many password errors ', '密码错误次数太多，请重置密码', NULL);
INSERT INTO `NB_MESSAGE` VALUES (5, 'Y', 0, '2018-06-06 22:17:21', 'admin', '2018-06-06 22:17:21', 'admin', 1, 'security.user_pwd_is_incorrect', 'The pwd is incorrect', '密码错误', NULL);
INSERT INTO `NB_MESSAGE` VALUES (6, 'Y', 0, '2018-06-06 22:17:21', 'admin', '2018-06-06 22:17:21', 'admin', 1, 'security.pwd_expiry', 'The pwd is expiry', '密码过期，请修改密码', NULL);
INSERT INTO `NB_MESSAGE` VALUES (7, 'Y', 0, '2018-08-08 16:26:19', 'admin', '2018-08-08 16:26:19', 'admin', 1, 'com.generator_rule_is_not_exist', 'Generator Rule is not exist', '生成规则不存在', NULL);
INSERT INTO `NB_MESSAGE` VALUES (8, 'Y', 0, '2018-08-08 16:26:19', 'admin', '2018-08-08 16:26:19', 'admin', 1, 'com.generator_id_more_than_size', 'Generator ID is more than size', 'ID超过长度限制', NULL);
INSERT INTO `NB_MESSAGE` VALUES (9, 'Y', 0, '2018-08-08 16:26:19', 'admin', '2018-08-08 16:26:19', 'admin', 1, 'com.generator_id_seq_error', 'Generator ID sequence error', '生成ID序列号错误', NULL);
INSERT INTO `NB_MESSAGE` VALUES (10, 'Y', 0, '2019-01-11 13:50:54', NULL, '2019-01-11 13:50:54', NULL, 1, 'common.entity_is_not_newest', 'Entity is Update by another', '记录被其他人更新', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
